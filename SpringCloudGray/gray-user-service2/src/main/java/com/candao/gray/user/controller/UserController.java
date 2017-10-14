package com.candao.gray.user.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.candao.gray.user.api.UserApi;
import com.candao.gray.user.api.bean.UserModel;

@RestController
public class UserController implements UserApi {

	@Autowired
	private DiscoveryClient discoveryClient;

	@Override
	public String hello(String userName) {
		Map<String, String> map = null;
		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("user-service");
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (serviceInstance.getPort() == 6013) {
				map = serviceInstance.getMetadata();
				break;
			}
		}
		String result = "";
		if (map != null && !map.isEmpty()) {
			result += "user-service2 metadata不为空,我是灰度服务!!! 服务metaData：" + JSONObject.toJSONString(map);
		} else {
			result += "user-service2 服务metaData是空,可以识别为正常服务";
		}
		return result;
	}

	@Override
	public UserModel post(String userName) {
		Map<String, String> map = null;
		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("user-service");
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (serviceInstance.getPort() == 6013) {
				map = serviceInstance.getMetadata();
				break;
			}
		}

		UserModel userModel = new UserModel();
		userModel.setUserName(userName);
		userModel.setId(UUID.randomUUID().toString());
		if (map != null && !map.isEmpty()) {
			userModel.setFlag("user-service2 metadata不为空,我是灰度服务!!! 服务metaData：" + JSONObject.toJSONString(map));
		} else {
			userModel.setFlag("user-service2 服务metaData是空,可以识别为正常服务");
		}
		return userModel;
	}

}
