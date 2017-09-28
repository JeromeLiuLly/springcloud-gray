package com.candao.gray.order.feignclient;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.candao.gray.user.api.UserApi;

@FeignClient(name = UserApi.SERVICE_NAME)
public interface UserFeignClient extends UserApi{


}