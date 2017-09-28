package com.candao.gray.user.feignclient;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.candao.gray.order.api.OrderApi;

@FeignClient(name = OrderApi.SERVICE_NAME)
public interface OrderInfoFeignClient extends OrderApi {

}