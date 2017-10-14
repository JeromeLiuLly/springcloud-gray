package com.candao.gray.order.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.candao.gray.order.feignclient.UserFeignClient;
import com.candao.gray.user.api.bean.UserModel;

@RestController
public class UserController {

	// 注入服务提供者,远程的Http服务
	@Autowired
	private UserFeignClient userFeignClient;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String hello(@RequestParam("userName") String userName) {
		return userFeignClient.hello(userName);
	}

	@PostMapping("/test/post")
	public UserModel testPost(@RequestParam("userName") String userName) {
		return userFeignClient.post(userName);
	}
}