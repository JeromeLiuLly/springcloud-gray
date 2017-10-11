package com.candao.gray.web.net;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.candao.irms.framework.net.http.HttpClient;
import com.candao.irms.framework.net.http.HttpResult;

public class HttpCommon extends HttpClient {

	// 默认编码
	private static final String DEFAULT_CHARSET = "UTF-8";

	public static HttpResult delete(String url, String[][] parameters, int timeoutSeconds, String[][] headers)
			throws Exception {
		HttpResult httpResult = new HttpResult();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		try {
			// 拼凑get请求参数
			String requestUrl = url;
			if (parameters != null) {
				int i = 0;
				for (String[] pair : parameters) {
					if (i == 0) {
						requestUrl += "?";
					} else {
						requestUrl += "&";
					}
					requestUrl += pair[0] + "=" + URLEncoder.encode(pair[1], DEFAULT_CHARSET);
					i++;
				}
			}

			System.out.println("requestUrl >> " + requestUrl);

			HttpDelete httpDelete = new HttpDelete(requestUrl);
			// 设置请求头及配置
			setHeaderAndConfs(httpDelete, headers, timeoutSeconds);
			// System.out.println("request:" + httpDelete.getRequestLine());
			// 请求执行及响应
			httpResponse = httpClient.execute(httpDelete);
			response(httpResponse, httpResult);
		} finally {
			httpClient.close();
		}
		return httpResult;
	}

	public static HttpResult put(String url, String[][] parameters, int timeoutSeconds, String[][] headers)
			throws Exception {
		HttpResult httpResult = new HttpResult();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		try {
			// 拼凑get请求参数
			String requestUrl = url;
			if (parameters != null) {
				int i = 0;
				for (String[] pair : parameters) {
					if (i == 0) {
						requestUrl += "?";
					} else {
						requestUrl += "&";
					}
					requestUrl += pair[0] + "=" + URLEncoder.encode(pair[1], DEFAULT_CHARSET);
					i++;
				}
			}

			System.out.println("requestUrl >> " + requestUrl);

			HttpPut httpPut = new HttpPut(requestUrl);
			// 设置请求头及配置
			setHeaderAndConfs(httpPut, headers, timeoutSeconds);
			// System.out.println("request:" + httpPut.getRequestLine());
			// 请求执行及响应
			httpResponse = httpClient.execute(httpPut);
			response(httpResponse, httpResult);
		} finally {
			httpClient.close();
		}
		return httpResult;
	}
	
	/**
	 * 设置请求头及配置
	 * 
	 * @param httpRequest-HttpPost或HttpGet
	 * @param headers-请求头参数
	 * @param timeoutSeconds-请求超时秒数
	 */
	private static void setHeaderAndConfs(HttpRequestBase httpRequest, String[][] headers, int timeoutSeconds) {
		// 设置headers
		if (headers != null && headers.length > 0) {
			for (int i = 0; i < headers.length; i++) {
				String[] keyValues = headers[i];
				httpRequest.addHeader(keyValues[0], keyValues[1]);
			}
		}
		// 设置confs
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeoutSeconds * 1000)
				.setConnectTimeout(timeoutSeconds * 1000).build();// 设置请求和传输超时时间
		httpRequest.setConfig(requestConfig);
	}

	/**
	 * 获取响应信息
	 * 
	 * @param httpResponse-响应CloseableHttpResponse
	 * @param httpResult-响应结果
	 * @throws Exception
	 */
	private static void response(CloseableHttpResponse httpResponse, HttpResult httpResult) throws Exception {
		try {
			// 解析返结果
			HttpEntity entity = httpResponse.getEntity();
			httpResult.statusCode = httpResponse.getStatusLine().getStatusCode();
			if (entity != null && httpResult.statusCode == HttpStatus.SC_OK) {
				httpResult.content = EntityUtils.toString(entity, DEFAULT_CHARSET);
				// System.out.println(httpResult.content);
			}
			// 关闭底层的HttpEntity流
			EntityUtils.consume(entity);
		} finally {
			httpResponse.close();
		}
	}

}
