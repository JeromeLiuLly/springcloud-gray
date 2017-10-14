package com.candao.gray.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.candao.gray.order.api.bean.OrderInfo;
import com.candao.gray.user.feignclient.OrderInfoFeignClient;

@RestController
public class OrderController {

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
		return orderInfoFeignClient.getOrderInfo(userName);
	}

}
