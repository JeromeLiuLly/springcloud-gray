package com.candao.gray.core;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.github.charlesvhe.springcloud.practice.core.CoreFeignRequestInterceptor;

import feign.Feign;

@Configuration
@EnableWebMvc
@RibbonClients(defaultConfiguration = DefaultRibbonConfiguration.class)
public class CoreAutoConfiguration extends WebMvcConfigurerAdapter {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new CoreHttpRequestInterceptor());
        return restTemplate;
    }
    
    @Bean
	public Feign.Builder feignBuilder() {
		return Feign.builder().requestInterceptor(new CoreFeignRequestInterceptor());
	}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CoreHeaderInterceptor());
    }
}
