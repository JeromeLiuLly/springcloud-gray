package com.candao.gray.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class CoreHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CoreHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);

        String header = StringUtils.collectionToDelimitedString(CoreHeaderInterceptor.label.get(), CoreHeaderInterceptor.HEADER_LABEL_SPLIT);
        
        String tag = CoreHeaderInterceptor.tag.get();
        
        logger.info("label: "+header + " tag : " + tag);
        
        HttpHeaders headers = requestWrapper.getHeaders();
        headers.add(CoreHeaderInterceptor.HEADER_LABEL, header);
        headers.add(CoreHeaderInterceptor.HEADER_TAG, tag);
        
        return execution.execute(requestWrapper, body);
    }
}
