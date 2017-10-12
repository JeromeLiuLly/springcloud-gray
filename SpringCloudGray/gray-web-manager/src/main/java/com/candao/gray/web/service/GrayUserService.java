package com.candao.gray.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.candao.gray.web.bean.GrayUser;
import com.candao.gray.web.dao.GrayUserRepository;
import com.candao.irms.framework.jpa.specification.Filter;
import com.candao.irms.framework.jpa.specification.Order;
import com.candao.irms.framework.jpa.specification.Order.Direction;
import com.candao.irms.framework.jpa.specification.QueryParams;


@Service
public class GrayUserService {
	
	@Autowired
	private GrayUserRepository grayUserRepository;
	
	public List<GrayUser> getAllUser(){
		return grayUserRepository.findAll();
	}
	
	public GrayUser getUserByUseName(String userName){
		StringBuffer sql = new StringBuffer("select * from grayuser");
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order("weight", Direction.asc);
		orders.add(order);
		
		QueryParams<GrayUser> queryParams = new QueryParams<GrayUser>();
		queryParams.clearAll().and(Filter.eq("user_name", userName)).and(Filter.eq("status", 1)).setOrders(orders);;
		List<GrayUser> grayUsers =  grayUserRepository.findByQueryParam(sql, queryParams);
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
