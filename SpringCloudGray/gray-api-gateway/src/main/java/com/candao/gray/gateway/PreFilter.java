package com.candao.gray.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.candao.gray.core.CoreHeaderInterceptor;
import com.candao.gray.core.net.HttpClient;
import com.candao.gray.core.net.HttpResult;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class PreFilter extends ZuulFilter {
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
		String userName = ctx.getRequest().getParameter("userName");
		String url = "http://10.200.102.136:6015/user/"+userName+"/getUser";
		String labels = null;
		try {
			HttpResult result = HttpClient.get(url, null);
			if (result.content != null) {
				GrayUser grayUser = JSONObject.parseObject(result.content, GrayUser.class);
				labels = grayUser.getServiceTag();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("label: " + labels);

		//断言,如果不存在灰度,不进行 flag位 透传
		CoreHeaderInterceptor.initHystrixRequestContext(labels); // zuul本身调用微服务

		// 透传上下文
		ctx.addZuulRequestHeader(CoreHeaderInterceptor.HEADER_LABEL, labels); // 传递给后续微服务

		return null;
	}
}
