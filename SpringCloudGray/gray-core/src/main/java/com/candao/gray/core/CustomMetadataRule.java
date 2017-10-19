package com.candao.gray.core;

import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.candao.gray.core.net.HttpClient;
import com.candao.gray.core.net.HttpResult;
import com.candao.gray.core.rule.RoundRobinRuleBySelf;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

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
