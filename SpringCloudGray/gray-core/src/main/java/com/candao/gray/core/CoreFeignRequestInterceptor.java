package com.candao.gray.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class CoreFeignRequestInterceptor implements RequestInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(CoreHttpRequestInterceptor.class);
	
	@Override
	public void apply(RequestTemplate template) {
		String header = StringUtils.collectionToDelimitedString(CoreHeaderInterceptor.label.get(),CoreHeaderInterceptor.HEADER_LABEL_SPLIT);
		template.header(CoreHeaderInterceptor.HEADER_LABEL, header);
		logger.info("label: "+header);
	}

}