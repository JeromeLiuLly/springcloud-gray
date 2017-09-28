package com.candao.gray.order.controller;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.candao.gray.order.feignclient.UserFeignClient;
import com.candao.gray.user.api.bean.OrderModel;
import com.google.common.collect.Maps;

@RestController
public class UserController {

	// 注入服务提供者,远程的Http服务
	@Autowired
	private UserFeignClient userFeignClient;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String hello(@RequestParam("name") String name, @RequestParam("age") int age) {
		return userFeignClient.hello(name, age);
	}

	@PostMapping("/test/post")
	public OrderModel testPost(@RequestBody OrderModel orderModel) {
		return userFeignClient.post(orderModel);
	}

	@GetMapping("/test/get")
	public String testGet() {
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("orderNo", "1");
		map.put("createTime", new Date());
		map.put("payTime", new Date());
		return userFeignClient.testGet(map);
	}
}