package com.candao.gray;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.candao.irms.framework.jpa.factory.BaseRepositoryFactoryBean;

@EnableCircuitBreaker		//断路器
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableJpaRepositories(basePackages = { "com.candao.gray" }, repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)//指定自定义的工厂类
public class WebManagerApplication{

	public static void main(String[] args) {
		new SpringApplicationBuilder(WebManagerApplication.class).web(true).run(args);
	}

}