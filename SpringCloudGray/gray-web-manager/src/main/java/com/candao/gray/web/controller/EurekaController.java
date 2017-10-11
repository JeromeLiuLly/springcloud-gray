package com.candao.gray.web.controller;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.candao.gray.web.bean.GrayStrategy;
import com.candao.gray.web.bean.GraySwitch;
import com.candao.gray.web.net.HttpCommon;
import com.candao.gray.web.service.GrayStrategyService;
import com.candao.gray.web.service.GraySwitchService;
import com.candao.irms.framework.net.http.HttpClient;
import com.candao.irms.framework.net.http.HttpResult;

@RestController
@RequestMapping("/eureka")
public class EurekaController {
	
	
	@Autowired
	private GrayStrategyService grayStrategyService;
	
	@Autowired
	private GraySwitchService graySwitchService;

	@RequestMapping(value = "/apps", method = RequestMethod.GET)
	@ResponseBody
	public String getEurekaApps() throws Exception {

		//注册中心的目标地址
		String url = "http://10.200.102.136:6006/eureka/apps";

		String[][] header = new String[][] { { "Accept", "application/json, text/plain, */*" } };
		HttpResult httpResult = HttpClient.get(url, null, 3000, header);

		return httpResult.content;
	}
	
	@RequestMapping(value = "/apps/{applicationName}/{instanceId}/metadata", method = RequestMethod.GET)
	@ResponseBody
	public HttpResult deleteMetaData(
			@PathVariable("applicationName") String applicationName,
			@PathVariable("instanceId") String instanceId,
			HttpServletRequest request) throws Exception {

		//注册中心的目标地址
		String url = "http://10.200.102.136:6006/eureka/apps/"+applicationName+"/"+instanceId+"/metadata?"+request.getQueryString();

		String[][] header = new String[][] { { "Accept", "application/json, text/plain, */*" } };
		HttpResult httpResult = HttpCommon.delete(url, null, 3000, header);

		return httpResult;
	}
	
	@RequestMapping(value = "/apps/allStrategy", method = RequestMethod.GET)
	@ResponseBody
	public List<GrayStrategy> getAllStrategy(){
		return grayStrategyService.getAllStrategy();
	}
	
	@RequestMapping(value = "/apps/{id}/{applicationName}/{instanceId}/synMetaDataStrategy", method = RequestMethod.GET)
	@ResponseBody
	public HttpResult synMetaDataStrategy(
			@PathVariable("id") Integer id,
			@PathVariable("applicationName") String applicationName,
			@PathVariable("instanceId") String instanceId,
			HttpServletRequest request) throws Exception{
		
		HttpResult httpResult = new HttpResult();
		GrayStrategy grayStrategy = grayStrategyService.getStrategyById(id);
		if (grayStrategy == null) {
			httpResult.statusCode = 500;
			httpResult.errorMsg = "找不到灰度服务对象";
			return httpResult;
		}
		
		if (grayStrategy.getStatus() == 0) {
			httpResult.statusCode = 500;
			httpResult.errorMsg = "灰度服务的状态是关闭状态,请开启生效后操作";
			return httpResult;
		}
		//注册中心的目标地址
		String url = "http://10.200.102.136:6006/eureka/apps/"+applicationName+"/"+instanceId+"/metadata?"+request.getQueryString();

		String[][] header = new String[][] { { "Accept", "application/json, text/plain, */*" } };
		
		httpResult = HttpCommon.put(url, null, 3000, header);
		return httpResult;
	}
	
	@RequestMapping(value = "/apps/{id}/updateStrategy", method = RequestMethod.GET)
	@ResponseBody
	public HttpResult updateStrategy(@PathVariable("id") Integer id) throws Exception{
		
		HttpResult httpResult = new HttpResult();
		GrayStrategy grayStrategy = grayStrategyService.getStrategyById(id);
		if (grayStrategy == null) {
			httpResult.statusCode = 500;
			httpResult.errorMsg = "找不到灰度服务对象";
			return httpResult;
		}
		
		grayStrategy.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		Integer status = grayStrategy.getStatus() > 0 ? 0 : 1;
		grayStrategy.setStatus(status);
		grayStrategyService.updateStrategyById(grayStrategy);
		
		httpResult.statusCode = 200;
		httpResult.errorMsg = "状态更新成功";
		return httpResult;
	}
	
	@RequestMapping(value = "/apps/editStrategy", method = RequestMethod.POST)
	@ResponseBody
	public HttpResult editStrategy(GrayStrategy grayStrategy) throws Exception{
		
		HttpResult httpResult = new HttpResult();
		if (grayStrategy == null) {
			httpResult.statusCode = 500;
			httpResult.errorMsg = "页面灰度服务对象传递失败";
			return httpResult;
		}
		GrayStrategy grayStrategyDB = grayStrategyService.getStrategyById(grayStrategy.getId());
		
		grayStrategyDB.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		
		grayStrategyDB.setStatus(grayStrategy.getStatus());
		grayStrategyDB.setServiceTag(grayStrategy.getServiceTag());
		grayStrategyDB.setStrategyName(grayStrategy.getStrategyName());
		grayStrategyDB.setVersion(grayStrategy.getVersion());
		grayStrategyDB.setStrategyValue(grayStrategy.getStrategyValue());
		grayStrategyDB.setWeight(grayStrategy.getWeight());
		
		grayStrategyService.updateStrategyById(grayStrategyDB);
		
		
		httpResult.statusCode = 200;
		httpResult.errorMsg = "状态更新成功";
		return httpResult;
	}
	
	@RequestMapping(value = "/apps/addStrategy", method = RequestMethod.POST)
	@ResponseBody
	public HttpResult addStrategy(GrayStrategy grayStrategy) throws Exception{
		
		GrayStrategy grayStrategyDB = new GrayStrategy();
		
		grayStrategyDB.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		grayStrategyDB.setCreateTime(new Timestamp(System.currentTimeMillis()));
		grayStrategyDB.setServiceName(grayStrategy.getServiceName());
		grayStrategyDB.setInstanceId(grayStrategy.getInstanceId());
		grayStrategyDB.setStatus(grayStrategy.getStatus());
		grayStrategyDB.setServiceTag(grayStrategy.getServiceTag());
		grayStrategyDB.setStrategyName(grayStrategy.getStrategyName());
		grayStrategyDB.setVersion(grayStrategy.getVersion());
		grayStrategyDB.setStrategyValue(grayStrategy.getStrategyValue());
		grayStrategyDB.setWeight(grayStrategy.getWeight());
		HttpResult httpResult = new HttpResult();
		try {
			grayStrategyService.saveStrategy(grayStrategyDB);
			httpResult.statusCode = 200;
			httpResult.errorMsg = "灰度服务添加成功";
		} catch (Exception e) {
			e.printStackTrace();
			httpResult.statusCode = 500;
			httpResult.errorMsg = "灰度服务添加失败";
		}
		
		return httpResult;
	}
	
	@RequestMapping(value = "/apps/{id}/deleteStrategy", method = RequestMethod.GET)
	@ResponseBody
	public HttpResult deleteStrategy(@PathVariable("id") Integer id) throws Exception{
		
		HttpResult httpResult = new HttpResult();
		grayStrategyService.deleteStrategyById(id);
		
		httpResult.statusCode = 200;
		httpResult.errorMsg = "状态更新成功";
		return httpResult;
	}
	
	
	@RequestMapping(value = "/apps/switch", method = RequestMethod.GET)
	@ResponseBody
	public HttpResult getSwitch() throws Exception{
		
		HttpResult httpResult = new HttpResult();
		GraySwitch graySwitch = graySwitchService.getSwitch();
		if (graySwitch == null) {
			httpResult.statusCode = 500;
			httpResult.errorMsg = "灰度服务开关数据不存在";
			return httpResult;
		}
		
		httpResult.statusCode = 200;
		httpResult.errorMsg = graySwitch.getGraySwitch() == 1  ? "true" : "false";
		return httpResult;
	}
	
	@RequestMapping(value = "/apps/updateSwitch", method = RequestMethod.GET)
	@ResponseBody
	public HttpResult updateSwitch() throws Exception{
		
		HttpResult httpResult = new HttpResult();
		GraySwitch graySwitch = graySwitchService.getSwitch();
		if (graySwitch == null) {
			httpResult.statusCode = 500;
			httpResult.errorMsg = "灰度服务开关数据不存在";
			return httpResult;
		}
		
		Integer status = graySwitch.getGraySwitch() == 1 ? 0 : 1;
		graySwitch.setGraySwitch(status);
		
		try {
			graySwitchService.updateSwitch(graySwitch);
			httpResult.statusCode = 200;
			httpResult.errorMsg = "灰度服务开关更新成功";
		} catch (Exception e) {
			e.printStackTrace();
			httpResult.statusCode = 500;
			httpResult.errorMsg = "灰度服务开关更新失败";
		}
		
		return httpResult;
	}
	
}
