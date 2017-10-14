package com.candao.gray.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.candao.gray.order.api.bean.OrderInfo;
import com.candao.gray.user.feignclient.OrderInfoFeignClient;

@RestController
public class OrderController {
	
	@Autowired
	private DiscoveryClient discoveryClient;

	// 注入服务提供者,远程的Http服务
	@Autowired
	private OrderInfoFeignClient orderInfoFeignClient;

	@RequestMapping(value = "/getOrderInfoListByUserName", method = RequestMethod.POST)
	@ResponseBody
	public List<OrderInfo> getListByHorsemanId(@RequestParam("userName") String userName) {
		return orderInfoFeignClient.getListByUserName(userName);
	}

	@RequestMapping(value = "/getOrderInfo", method = RequestMethod.POST)
    @ResponseBody
	public OrderInfo getOrderInfo(@RequestParam("userName") String userName) {
		Map<String, String> map = null;
		String metadata = ", user-service 描述信息：";
		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("user-service");
		for(ServiceInstance serviceInstance : serviceInstances){
			if (serviceInstance.getPort() == 6010) {
				map =serviceInstance.getMetadata();
				break;
			}
		}
		if (map != null && !map.isEmpty() ) {
			metadata += "user-service metadata不为空,我是灰度服务!!! 服务metaData："+JSONObject.toJSONString(map) + "; ";
		}else{
			metadata += "user-service 服务metaData是空,可以识别为正常服务; ";
		}
		
		OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(userName);
		orderInfo.setBrandName(metadata + orderInfo.getBrandName());
		return orderInfo;
	}

}
