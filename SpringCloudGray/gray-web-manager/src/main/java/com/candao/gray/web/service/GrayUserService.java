package com.candao.gray.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.candao.gray.web.bean.GrayUser;
import com.candao.gray.web.dao.GrayUserRepository;


@Service
public class GrayUserService {
	
	@Autowired
	private GrayUserRepository grayUserRepository;
	
	public List<GrayUser> getAllUser(){
		return grayUserRepository.findAll();
	}
	
	public GrayUser getUserByUseName(String userName){
		GrayUser grayUser = new GrayUser();
		grayUser.setStatus(1);
		grayUser.setUserName(userName);
		Example<GrayUser> example = Example.of(grayUser);
		
		Sort sort = new Sort(Sort.Direction.DESC, "weight");
		List<GrayUser> grayUsers = grayUserRepository.findAll(example,sort);
		
		System.out.println(JSONObject.toJSONString(grayUsers));
		if (grayUsers != null && grayUsers.size() > 0) {
			return grayUsers.get(0);
		}
		return null;
	}
	
	public GrayUser getUserById(Integer id){
		return grayUserRepository.getOne(id);
	}
	
	public void deleteUserById(Integer id){
		grayUserRepository.delete(id);
	}
	
	public GrayUser updateUser(GrayUser grayUser){
		return grayUserRepository.update(grayUser);
	}
	
	public GrayUser addUser(GrayUser grayUser){
		return grayUserRepository.save(grayUser);
	}

}
