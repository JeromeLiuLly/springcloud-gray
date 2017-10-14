package com.candao.gray.order.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.candao.gray.order.feignclient.UserFeignClient;
import com.candao.gray.user.api.bean.UserModel;

@RestController
public class UserController {

	@Autowired
	private DiscoveryClient discoveryClient;
	
	// 注入服务提供者,远程的Http服务
	@Autowired
	private UserFeignClient userFeignClient;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String hello(@RequestParam("userName") String userName) {
		Map<String, String> map = null;
		String metadata = ", order-service2  描述信息：";
		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("order-service");
		for(ServiceInstance serviceInstance : serviceInstances){
			if (serviceInstance.getPort() == 6012) {
				map =serviceInstance.getMetadata();
				break;
			}
		}
		if (map != null && !map.isEmpty() ) {
			metadata += "order-service2 metadata不为空,我是灰度服务!!! 服务metaData："+JSONObject.toJSONString(map) + "; ";
		}else{
			metadata += "order-service2 服务metaData是空,可以识别为正常服务; ";
		}
		
		return metadata + userFeignClient.hello(userName);
	}

	@PostMapping("/test/post")
	public UserModel testPost(@RequestParam("userName") String userName) {
		Map<String, String> map = null;
		String metadata = ", order-service2  描述信息：";
		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("order-service");
		for(ServiceInstance serviceInstance : serviceInstances){
			if (serviceInstance.getPort() == 6012) {
				map =serviceInstance.getMetadata();
				break;
			}
		}
		if (map != null && !map.isEmpty() ) {
			metadata += "order-service2 metadata不为空,我是灰度服务!!! 服务metaData："+JSONObject.toJSONString(map) + "; ";
		}else{
			metadata += "order-service2 服务metaData是空,可以识别为正常服务; ";
		}
		UserModel userModel = userFeignClient.post(userName);
		userModel.setFlag(metadata + userModel.getFlag());
		return userModel;
	}
}