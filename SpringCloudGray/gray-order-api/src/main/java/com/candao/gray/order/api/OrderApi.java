package com.candao.gray.order.api;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.candao.gray.order.api.bean.OrderInfo;

@RequestMapping("/inner/order")
public interface OrderApi {

    String SERVICE_NAME = "order-service";

    
    
    /**
     * 获取骑手订单列表集合
     * @param horsemanId	骑手id
     * @return
     */
    @RequestMapping(value = "/getOrderInfoListByHorsemanId", method = RequestMethod.POST)
    @ResponseBody
    List<OrderInfo> getListByHorsemanId(@RequestParam("horsemanId") Integer horsemanId);
    
    
    /**
     * 获取订单数据
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/getOrderInfo", method = RequestMethod.POST)
    @ResponseBody
    OrderInfo getOrderInfo(@RequestParam("orderId") String orderId);

}