package com.candao.gray.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.candao.gray.web.bean.GrayUser;
import com.candao.gray.web.service.GrayUserService;
import com.candao.irms.framework.net.http.HttpResult;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private GrayUserService grayUserService;
	
	@RequestMapping(value = "/allUser", method = RequestMethod.GET)
	@ResponseBody
	public List<GrayUser> getAllUser(){
		return grayUserService.getAllUser();
	}
	
	
	@RequestMapping(value = "/{userName}/getUser", method = RequestMethod.GET)
	@ResponseBody
	public GrayUser getUserByUserName(@PathVariable("userName") String userName){
		return grayUserService.getUserByUseName(userName);
	}
	
	@RequestMapping(value = "/{id}/updateUser", method = RequestMethod.GET)
	@ResponseBody
	public HttpResult updateStrategy(@PathVariable("id") Integer id) throws Exception{
		
		HttpResult httpResult = new HttpResult();
		GrayUser grayUser = grayUserService.getUserById(id);
		if (grayUser == null) {
			httpResult.statusCode = 500;
			httpResult.errorMsg = "找不到用户对象";
			return httpResult;
		}
		
		Integer status = grayUser.getStatus() > 0 ? 0 : 1;
		grayUser.setStatus(status);
		grayUserService.updateUser(grayUser);
		
		httpResult.statusCode = 200;
		httpResult.errorMsg = "状态更新成功";
		return httpResult;
	}
	
	@RequestMapping(value = "/editUser", method = RequestMethod.POST)
	@ResponseBody
	public HttpResult editUser(GrayUser grayUser) throws Exception{
		
		HttpResult httpResult = new HttpResult();
		if (grayUser == null) {
			httpResult.statusCode = 500;
			httpResult.errorMsg = "页面用户对象传递失败";
			return httpResult;
		}
		GrayUser grayUserDB = grayUserService.getUserById(grayUser.getId());
		
		grayUserDB.setStatus(grayUser.getStatus());
		grayUserDB.setServiceTag(grayUser.getServiceTag());
		grayUserDB.setWeight(grayUser.getWeight());
		
		grayUserService.updateUser(grayUserDB);
		
		
		httpResult.statusCode = 200;
		httpResult.errorMsg = "用户灰度策略更新成功";
		return httpResult;
	}
	
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	@ResponseBody
	public HttpResult addUser(GrayUser grayUser) throws Exception{
		
		GrayUser grayUserDB = new GrayUser();
		
		grayUserDB.setUserName(grayUser.getUserName());
		grayUserDB.setStatus(grayUser.getStatus());
		grayUserDB.setServiceTag(grayUser.getServiceTag());
		grayUserDB.setWeight(grayUser.getWeight());
		
		
		HttpResult httpResult = new HttpResult();
		try {
			grayUserService.addUser(grayUserDB);
			httpResult.statusCode = 200;
			httpResult.errorMsg = "用户灰度策略添加成功";
		} catch (Exception e) {
			e.printStackTrace();
			httpResult.statusCode = 500;
			httpResult.errorMsg = "用户灰度策略添加失败";
		}
		
		return httpResult;
	}
	
	@RequestMapping(value = "/{id}/deleteUser", method = RequestMethod.GET)
	@ResponseBody
	public HttpResult deleteStrategy(@PathVariable("id") Integer id) throws Exception{
		
		HttpResult httpResult = new HttpResult();
		grayUserService.deleteUserById(id);
		
		httpResult.statusCode = 200;
		httpResult.errorMsg = "用户灰度策略删除成功";
		return httpResult;
	}
	
	
	

}
