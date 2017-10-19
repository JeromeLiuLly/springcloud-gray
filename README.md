# springcloud-gray
基于springcloud实现的灰度发布

## 架构设计和界面

### 架构模型
![灰度发布架构设计图](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E7%81%B0%E5%BA%A6%E6%9E%B6%E6%9E%84%E5%9B%BE.png)
![灰度发布数据流图](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E7%81%B0%E5%BA%A6%E6%95%B0%E6%8D%AE%E6%B5%81.png)

### 平台化操作
![平台化界面图](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E7%81%B0%E5%BA%A6%E5%8F%91%E5%B8%83.png)

## 项目结构
### gray-config-server 配置中心
端口：6007，方便起见直接读取配置文件，生产环境可以读取git。先启动配置中心，所有服务的配置（包括注册中心的地址）均从配置中心读取。

### gray-xxx-service 服务消费者
调用服务提供者和服务提供者，验证是否进入灰度服务。

### gray-core 框架核心包
核心jar包，所有微服务均引用该包，用于负载自定义策略规则，是实现灰度发布的核心架包。

### gray-service-registry-center 注册中心
端口：6006，用于统筹各个注册服务。

### gray-api-gateway 网关
端口：4002，拉取灰度策略，进行请求的把标签操作。

## 应用场景

### 灰度发布
通过灰度版本的控制，实现符合灰度策略的对象，优先进入灰度服务进行体验。

### 异构服务的共存
例如，根据不同的策略，有根据不同的渠道、地域、门店、品牌等，优先使用不同的服务。例如，广州地域的用户，仅能使用基于广州部署的微服务。

### 同等级服务的调用
例如，业务场景，根据不同的渠道和来源进行下单。微信的下单，仅能调用微信的order-service服务;官网下单，仅能调用官网的order--service下单;
通过这样的方式，上层业务无须调用何种具体服务统一底层进行负载调用，实现业务的解耦和服务的可插拔配置；

## 实现思路
根据标签的控制，我们当然放到之前写的Ribbon的**```CustomMetadataRule```**中，每个实例配置的不同规则也是跟之前一样放到注册中心的metadata中，关键是标签数据如何传过来。自定义规则[**```CustomMetadataRule```**]的实现思路里面有答案，请求都通过**```gray-api-gateway```**进来，因此我们可以在zuul里面给请求打标签，基于用户，IP或其他看你的需求，然后将标签信息放入Thystrix。hystrix的原理，为了做到故障隔离，hystrix启用了自己的线程。另外使用sleuth方案，他的链路跟踪就能够将spam传递下去，翻翻sleuth源码，找找其他资料，发现可以使用**```HystrixRequestVariableDefault```**，这里不建议直接使用**```HystrixConcurrencyStrategy```**，会和sleuth的strategy冲突。代码参见**```CoreHeaderInterceptor```**。现在可以测试zuul里面的rule，看能否拿到标签内容了。

这里还不是终点，解决了zuul的路由，服务A调服务B这里的路由怎么处理呢？zuul算出来的标签如何往后面依次传递下去呢，我们还是抄sleuth：把标签放入header，服务A调服务B时，将服务A header里面的标签放到服务B的header里，依次传递下去。这里的关键点就是：内部的微服务在接收到发来的请求时（gateway-->A，A-->B都是这种情况）。

总结一下：zuul依据用户或IP等计算标签，并将标签放入header里向后传递，后续的微服务通过拦截器，将header里的标签放入RestTemplate请求的header里继续向后接力传递。将灰度标识放入（**```HystrixRequestVariableDefault```**），使Ribbon Rule可以使用。

## 代码分析实现流程
自定义的规则，该处可以实现针对不同的策略，使用不同的负载机制[ **轮询、随机、权重随机** ]
```java
public class CustomMetadataRule extends ZoneAvoidanceRule {

	// 检测灰度开关是否启动
	private HttpResult checkGraySwitch() {
		String url = "http://10.200.102.136:6015/eureka/apps/switch";
		HttpResult result = new HttpResult();
		result.statusCode = 500;
		try {
			result = HttpClient.get(url, null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return result;
	}

	@Override
	public Server choose(Object key) {

		// 获取是否存在存活的服务可调用
		List<Server> serverList = this.getPredicate().getEligibleServers(this.getLoadBalancer().getAllServers());
		// 获取不到服务
		if (CollectionUtils.isEmpty(serverList)) {
			return null;
		}

		// 获取灰度开关是否启动
		HttpResult result = checkGraySwitch();

		// 灰度开关被设置成关闭状态,默认走空metadata或者是特定标识是正常的服务,轮询访问
		Boolean isOpen = Boolean.parseBoolean(JSONObject.parseObject(result.content).getString("errorMsg"));
		if (result.statusCode == 200 && !isOpen) {
			isOpen = true;
			return RoundRobinRuleBySelf.getInstance().choose(this.getLoadBalancer(), key,isOpen);
		}

		// 灰度发布启动状态,未被设置成灰度对象,默认走空metadata或者是特定标识是正常的服务,轮询访问
		if (StringUtils.isEmpty(CoreHeaderInterceptor.label.get())) {
			isOpen = false;
			return RoundRobinRuleBySelf.getInstance().choose(this.getLoadBalancer(), key,isOpen);
		}
		
		// 灰度发布启动状态,被设置成灰度对象,走空特定标识的服务,轮询访问
		return RoundRobinRuleBySelf.getInstance().choose(this.getLoadBalancer(), key,!isOpen);
	}
}
```

feignClient 调用flag位透传的问题
```java
public class CoreFeignRequestInterceptor implements RequestInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(CoreHttpRequestInterceptor.class);

	@Override
	public void apply(RequestTemplate template) {
		String header = StringUtils.collectionToDelimitedString(CoreHeaderInterceptor.label.get(),CoreHeaderInterceptor.HEADER_LABEL_SPLIT);
		String tag = CoreHeaderInterceptor.tag.get();
		template.header(CoreHeaderInterceptor.HEADER_LABEL, header).header(CoreHeaderInterceptor.HEADER_TAG, tag);
		logger.info("label: " + header + " tag : " + tag);
	}

}
```

HttpRequest 调用flag位透传的问题
```java
public class CoreHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CoreHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);

        String header = StringUtils.collectionToDelimitedString(CoreHeaderInterceptor.label.get(), CoreHeaderInterceptor.HEADER_LABEL_SPLIT);
        
        String tag = CoreHeaderInterceptor.tag.get();
        
        logger.info("label: "+header + " tag : " + tag);
        
        HttpHeaders headers = requestWrapper.getHeaders();
        headers.add(CoreHeaderInterceptor.HEADER_LABEL, header);
        headers.add(CoreHeaderInterceptor.HEADER_TAG, tag);
        
        return execution.execute(requestWrapper, body);
    }
}
```

配置生效
```java
@Configuration
@EnableWebMvc
public class CoreAutoConfiguration extends WebMvcConfigurerAdapter {

	@Bean
	public DefaultPropertiesFactory defaultPropertiesFactory() {
		return new DefaultPropertiesFactory();
	}

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new CoreHttpRequestInterceptor());
		return restTemplate;
	}

  //用于配置feignClient透传生效
	@Bean
	public Feign.Builder feignBuilder() {
		return Feign.builder().requestInterceptor(new CoreFeignRequestInterceptor());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CoreHeaderInterceptor());
	}
}
```

## 测试
测试/验证流程说明：

### 第一步,测试灰度发布总开关是否生效

#### 简易形式：

1.先启动 order服务;
 
 1.1 未标识灰度服务时，前端每一次访问都是随机的情况
 访问url：http://127.0.0.1:4002/order/inner/order/getOrderInfoListByUserName?userName=liulianyuan
 ![图一](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/1.png)
 1.2 分别启动灰度服务和正常服务。一开始启动的时候是无差别的。我们可以在 metadata.html 进行动态配置，指定某个服务是灰度的。
 以下，设置了order-service2 是灰度服务。
 访问url：http://127.0.0.1:4002/order/inner/order/getOrderInfoListByUserName?userName=liulianyuan
 ![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/3.png)
 ![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/4.png)
 ![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/5.png)
 ![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/6.png)
### 第二步,测试灰度发布的正常行为

#### 方式1------(灰度用户): 前端 ==> 网关 ==> 正常服务 ==> 灰度服务

1.启动 order服务 和 user服务;

  1.1 user-service是正常服务，标识user-service2为灰度服务；order-service 均是正常服务。
  1.2 启动所有服务。一开始启动的时候是无差别的。我们可以在 metadata.html 进行动态配置，指定某个服务是灰度的。

请求url: http://127.0.0.1:4002/order/test?userName=liulianyuan
![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/7.png)
![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/8.png)
该处是随机访问正常的order-service服务的。多试几次，就可以看见order-service和order-service2的出现。【该处如果需要做成轮需，需要改代码】

#### 方式2------(灰度用户): 前端 ==> 网关 ==> 灰度服务 ==> 正常服务

1.启动 order服务 和 user服务;

  1.1 user-service是正常服务，标识user-service2为灰度服务；order-service 均是正常服务。
  1.2 启动所有服务。一开始启动的时候是无差别的。我们可以在 metadata.html 进行动态配置，指定某个服务是灰度的。
  ![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/9.png)
  请求url: http://127.0.0.1:4002/user/getOrderInfo?userName=liulianyuan
  ![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/10.png)
  ![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/11.png)
 该处是随机访问正常的order-service服务的。多试几次，就可以看见order-service和order-service2的出现。【该处如果需要做成轮需，需要改代码】
 
#### 方式3------(灰度用户): 前端 ==> 网关 ==> 灰度服务 ==> 灰度服务

1.启动 order服务 和 user服务;

  1.1 user-service是正常服务，标识user-service2为灰度服务；order-service 均是正常服务。
  1.2 启动所有服务。一开始启动的时候是无差别的。我们可以在 metadata.html 进行动态配置，指定某个服务是灰度的。
  ![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/12.png)
  请求url: http://127.0.0.1:4002/order/test?userName=liulianyuan
  ![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/13.png)
#### 方式4------(正常用户): 前端 ==> 网关 ==> 正常服务 ==> 正常服务

1.启动 order服务 和 user服务;

  1.1 user-service是正常服务，标识user-service2为灰度服务；order-service 均是正常服务。
  1.2 启动所有服务。一开始启动的时候是无差别的。我们可以在 metadata.html 进行动态配置，指定某个服务是灰度的。
![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/14.png)
   请求url: http://127.0.0.1:4002/order/test?userName=lly
![图二](https://raw.githubusercontent.com/JeromeLiuLly/springcloud-gray/master/SpringCloudGray/%E6%B5%8B%E8%AF%95%E5%9B%BE/15.png)
