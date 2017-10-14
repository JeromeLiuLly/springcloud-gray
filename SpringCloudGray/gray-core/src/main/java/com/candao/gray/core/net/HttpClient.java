package com.candao.gray.core.net;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * http请求帮助类
 * 
 * @author jameslei
 * @version 1.0.0 2017年5月5日 下午5:34:32
 */
public class HttpClient {
	// 默认编码
	private static final String DEFAULT_CHARSET = "UTF-8";
	// 默认超时时间
	private static final int DEFAULT_TIMEOUT_SECONDS = 10;

	/**
	 * 发起http-post请求，数据在请求body中（默认10秒超时）<br/>
	 * 默认Content-type: application/json
	 * @param url-请求地址
	 * @param msg-请求body数据
	 * @return
	 * @throws Exception
	 */
	public static HttpResult postWithBody(String url, String msg) throws Exception {
		return postWithBody(url, msg, DEFAULT_TIMEOUT_SECONDS);
	}

	/**
	 * 发起http-post请求，数据在请求body中<br/>
	 * 默认Content-type: application/json
	 * @param url-请求地址
	 * @param msg-请求body数据
	 * @param timeoutSeconds-超时秒数
	 * @return
	 * @throws Exception
	 */
	public static HttpResult postWithBody(String url, String msg, int timeoutSeconds) throws Exception {
		return post(url, true, msg, timeoutSeconds, new String[][] { { "Content-type", "application/json" } });
	}

	/**
	 * 发起http-post请求，数据在请求body中
	 * @param url-请求地址
	 * @param msg-请求body数据
	 * @param timeoutSeconds-超时秒数
	 * @return
	 * @throws Exception
	 */
	public static HttpResult postWithBody(String url, String msg, int timeoutSeconds, String[][] headers) throws Exception {
		return post(url, true, msg, timeoutSeconds, headers);
	}

	/**
	 * 发起http-post请求，参数形式（默认10秒超时）
	 * @param url-请求地址
	 * @param parameters-请求参数key-value，如：new String[][] { { "name", "小张" }, { "pwd", "123456" } }
	 * @return
	 */
	public static HttpResult postWithParams(String url, String[][] parameters) throws Exception {
		return postWithParams(url, parameters, DEFAULT_TIMEOUT_SECONDS);
	}

	/**
	 * 发起http-post请求，参数形式
	 * @param url-请求地址
	 * @param parameters-请求参数key-value，如：new String[][] { { "name", "小张" }, { "pwd", "123456" } }
	 * @param timeoutSeconds--超时秒数
	 * @return
	 * @throws Exception
	 */
	public static HttpResult postWithParams(String url, String[][] parameters, int timeoutSeconds) throws Exception {
		return post(url, false, parameters, timeoutSeconds, null);
	}

	/**
	 * 发起http-get请求（默认10秒超时）
	 * @param url-请求地址
	 * @param parameters-请求参数key-value，如：new String[][] { { "name", "小张" }, { "pwd", "123456" } }
	 * @return
	 */
	public static HttpResult get(String url, String[][] parameters) throws Exception {
		return get(url, parameters, DEFAULT_TIMEOUT_SECONDS);
	}

	/**
	 * 发起http-get请求
	 * @param url-请求地址
	 * @param parameters-请求参数key-value，如：new String[][] { { "name", "小张" }, { "pwd", "123456" } }
	 * @param timeoutSeconds--超时秒数
	 * @return
	 * @throws Exception
	 */
	public static HttpResult get(String url, String[][] parameters, int timeoutSeconds) throws Exception {
		return get(url, parameters, timeoutSeconds, null);
	}

	/**
	 * 发起http-post请求
	 * @param url-请求地址
	 * @param isBodyInfo-是否为body形式请求
	 * @param info-请求数据
	 * @param timeoutSeconds-超时秒数
	 * @param headers-请求header，如：new String[][] { { "Accept-Language", "zh-cn,zh;q=0.5" }, { "Host", "www.can-dao.com" } }
	 * @return
	 * @throws Exception
	 */
	public static HttpResult post(String url, boolean isBodyInfo, Object info, int timeoutSeconds, String[][] headers) throws Exception {
		HttpResult httpResult = new HttpResult();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			if (isBodyInfo) {// body形式请求
				if (info != null) {
					String msg = (String) info;
					httpPost.setEntity(new StringEntity(msg, DEFAULT_CHARSET));
				}
			} else {// 普通参数形式请求
				if (info != null) {
					String[][] parameters = (String[][]) info;
					// 填入各个表单域的值
					if (parameters != null && parameters.length > 0) {
						List<NameValuePair> nvps = new ArrayList<NameValuePair>();
						for (int i = 0; i < parameters.length; i++) {
							String[] keyValues = parameters[i];
							nvps.add(new BasicNameValuePair(keyValues[0], keyValues[1]));
						}
						httpPost.setEntity(new UrlEncodedFormEntity(nvps, DEFAULT_CHARSET));
					}
				}
			}
			// 设置请求头及配置
			setHeaderAndConfs(httpPost, headers, timeoutSeconds);
			// 请求执行及响应
//				System.out.println("request:" + httpPost.getRequestLine());
			httpResponse = httpClient.execute(httpPost);
			response(httpResponse, httpResult);
		} finally {
			httpClient.close();
		}
		return httpResult;
	}

	/**
	 * 发起http-get请求
	 * @param url-请求地址
	 * @param parameters-请求参数key-value，如：new String[][] { { "name", "小张" }, { "pwd", "123456" } }
	 * @param timeoutSeconds--超时秒数
	 * @param headers-请求header，如：new String[][] { { "Accept-Language", "zh-cn,zh;q=0.5" }, { "Host", "www.can-dao.com" } }
	 * @return
	 * @throws Exception
	 */
	public static HttpResult get(String url, String[][] parameters, int timeoutSeconds, String[][] headers) throws Exception {
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
			
			HttpGet httpGet = new HttpGet(requestUrl);
			// 设置请求头及配置
			setHeaderAndConfs(httpGet, headers, timeoutSeconds);
//				System.out.println("request:" + httpGet.getRequestLine());
			// 请求执行及响应
			httpResponse = httpClient.execute(httpGet);
			response(httpResponse, httpResult);
		} finally {
			httpClient.close();
		}
		return httpResult;
	}

	/**
	 * 设置请求头及配置
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
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeoutSeconds * 1000).setConnectTimeout(timeoutSeconds * 1000).build();// 设置请求和传输超时时间
		httpRequest.setConfig(requestConfig);
	}

	/**
	 * 获取响应信息
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
//					System.out.println(httpResult.content);
			}
			// 关闭底层的HttpEntity流
			EntityUtils.consume(entity);
		} finally {
			httpResponse.close();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(!Boolean.parseBoolean("true"));
	}
}
