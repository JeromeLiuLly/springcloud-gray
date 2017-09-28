package com.candao.gray.gateway;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import com.candao.gray.core.CoreHeaderInterceptor;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class PreFilter extends ZuulFilter {
	private static final Map<String, String> TOKEN_LABEL_MAP = new HashMap<String, String>();

	static {
		TOKEN_LABEL_MAP.put("emt", "EN,Male,Test");
		TOKEN_LABEL_MAP.put("eft", "EN,Female,Test");
		TOKEN_LABEL_MAP.put("cmt", "CN,Male,Test");
		TOKEN_LABEL_MAP.put("cft", "CN,Female,Test");
		TOKEN_LABEL_MAP.put("em", "EN,Male");
		TOKEN_LABEL_MAP.put("ef", "EN,Female");
		TOKEN_LABEL_MAP.put("cm", "CN,Male");
		TOKEN_LABEL_MAP.put("cf", "CN,Female");

		TOKEN_LABEL_MAP.put("gray", "gray");
	}

	private static final Logger logger = LoggerFactory.getLogger(PreFilter.class);

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		String token = ctx.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

		String labels = TOKEN_LABEL_MAP.get(token);

		logger.info("label: " + labels);

		//断言,如果不存在灰度,不进行 flag位 透传
		CoreHeaderInterceptor.initHystrixRequestContext(labels); // zuul本身调用微服务

		// 透传上下文
		ctx.addZuulRequestHeader(CoreHeaderInterceptor.HEADER_LABEL, labels); // 传递给后续微服务

		return null;
	}
}
