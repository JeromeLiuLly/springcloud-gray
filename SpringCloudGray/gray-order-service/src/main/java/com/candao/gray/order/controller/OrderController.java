package com.candao.gray.order.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.candao.gray.order.api.OrderApi;
import com.candao.gray.order.api.bean.OrderInfo;

@RestController
@RequestMapping("/inner/order")
public class OrderController implements OrderApi {
	
	@Autowired
	private DiscoveryClient discoveryClient;

	@Override
	public List<OrderInfo> getListByUserName(@RequestParam("userName")String userName) {
		List<OrderInfo> list = new  ArrayList<OrderInfo>();
		
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setId(UUID.randomUUID().toString());
		Map<String, String> map = null;
		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("order-service");
		for(ServiceInstance serviceInstance : serviceInstances){
			if (serviceInstance.getPort() == 6011) {
				map =serviceInstance.getMetadata();
				break;
			}
		}
		if (map != null && !map.isEmpty() ) {
			orderInfo.setBrandName("order-service metadata不为空,我是灰度服务!!! 服务metaData："+JSONObject.toJSONString(map));
		}else{
			orderInfo.setBrandName("order-service 服务metaData是空,可以识别为正常服务");
		}
		orderInfo.setUserName(userName);
		
		list.add(orderInfo);
		return list;
	}

	@Override
	public OrderInfo getOrderInfo(@RequestParam("userName")String userName) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setId(UUID.randomUUID().toString());
		Map<String, String> map = null;
		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("order-service");
		for(ServiceInstance serviceInstance : serviceInstances){
			if (serviceInstance.getPort() == 6011) {
				map =serviceInstance.getMetadata();
				break;
			}
		}
		if (map != null && !map.isEmpty() ) {
			orderInfo.setBrandName("order-service metadata不为空,我是灰度服务!!! 服务metaData："+JSONObject.toJSONString(map));
		}else{
			orderInfo.setBrandName("order-service 服务metaData是空,可以识别为正常服务");
		}
		orderInfo.setUserName(userName);
		return orderInfo;
	}

}
