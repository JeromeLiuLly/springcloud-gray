package com.candao.gray.core;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

public class CoreHeaderInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CoreHeaderInterceptor.class);

    public static final String HEADER_LABEL = "x-label";
    public static final String HEADER_TAG = "x-tag";
    public static final String HEADER_LABEL_SPLIT = ",";

    public static final HystrixRequestVariableDefault<List<String>> label = new HystrixRequestVariableDefault<List<String>>();
    public static final HystrixRequestVariableDefault<String> tag = new HystrixRequestVariableDefault<String>();
    

    public static void initHystrixRequestContext(String labels,String tag) {
        logger.info("label: " + labels +" tag:" + tag);
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }

        if (!StringUtils.isEmpty(labels)) {
            CoreHeaderInterceptor.label.set(Arrays.asList(labels.split(CoreHeaderInterceptor.HEADER_LABEL_SPLIT)));
            CoreHeaderInterceptor.tag.set(tag);
        } else {
            CoreHeaderInterceptor.label.set(null);
            CoreHeaderInterceptor.tag.set(null);
        }
    }

    public static void shutdownHystrixRequestContext() {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().shutdown();
        }
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        CoreHeaderInterceptor.initHystrixRequestContext(request.getHeader(CoreHeaderInterceptor.HEADER_LABEL),request.getHeader(CoreHeaderInterceptor.HEADER_TAG));
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        CoreHeaderInterceptor.shutdownHystrixRequestContext();
    }
}
