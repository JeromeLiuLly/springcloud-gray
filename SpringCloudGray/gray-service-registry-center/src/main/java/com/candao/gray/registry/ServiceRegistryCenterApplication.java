package com.candao.gray.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ServiceRegistryCenterApplication {

	public static void main(String[] args) {
		 SpringApplication.run(ServiceRegistryCenterApplication.class, args);
	}
}
