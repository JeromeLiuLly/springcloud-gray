package com.candao.gray.user.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

import com.candao.gray.user.api.UserApi;
import com.candao.gray.user.api.bean.OrderModel;

@RestController
public class UserController implements UserApi {

	@Override
	public String hello(String name, int age) {
		return "user-service2,flag:gray" + name + age;
	}

	@Override
	public OrderModel post(OrderModel orderModel) {
		orderModel.setOrderNo(2222222L);
		return orderModel;
	}

	@Override
	public String testGet(Map<String, Object> map) {
		return String.valueOf(map);
	}

}
