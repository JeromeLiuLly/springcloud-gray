package com.candao.gray;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableCircuitBreaker		//断路器
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class UserServiceApplication{

	public static void main(String[] args) {
		new SpringApplicationBuilder(UserServiceApplication.class).web(true).run(args);
	}

}