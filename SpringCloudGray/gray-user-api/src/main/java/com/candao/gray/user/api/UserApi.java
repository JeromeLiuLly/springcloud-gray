package com.candao.gray.user.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.candao.gray.user.api.bean.UserModel;

@RequestMapping("/inner/user")
public interface UserApi {

	String SERVICE_NAME = "user-service";

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello(@RequestParam("userName") String userName);

	@RequestMapping(value = "/test/post", method = RequestMethod.POST)
	@ResponseBody
	public UserModel post(@RequestParam("userName") String userName);
}
