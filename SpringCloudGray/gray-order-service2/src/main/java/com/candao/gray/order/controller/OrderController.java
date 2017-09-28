package com.candao.gray.order.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.candao.gray.order.api.OrderApi;
import com.candao.gray.order.api.bean.OrderInfo;

@RestController
@RequestMapping("/inner/order")
public class OrderController implements OrderApi {

	@Override
	public List<OrderInfo> getListByHorsemanId(@RequestParam("horsemanId")Integer horsemanId) {
		List<OrderInfo> list = new  ArrayList<OrderInfo>();
		
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setId(horsemanId.toString());
		orderInfo.setBrandId(110);
		orderInfo.setBrandName("测试-gray");
		
		list.add(orderInfo);
		return list;
	}

	@Override
	public OrderInfo getOrderInfo(@RequestParam("orderId")String orderId) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setId(orderId);
		orderInfo.setGoodsName("鸡蛋");
		return orderInfo;
	}

}
