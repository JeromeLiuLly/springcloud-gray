package com.candao.gray.user.api;

import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.candao.gray.user.api.bean.OrderModel;

@RequestMapping("/inner/user")
public interface UserApi {

	String SERVICE_NAME = "user-service";

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello(@RequestParam("name") String name, @RequestParam("age") int age);

	@RequestMapping(value = "/test/post", method = RequestMethod.POST)
	@ResponseBody
	public OrderModel post(@RequestBody OrderModel orderModel);

	@RequestMapping(value = "/test/get", method = RequestMethod.GET)
	public String testGet(@RequestParam Map<String, Object> map);
}
