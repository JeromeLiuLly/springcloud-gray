package com.candao.gray.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.candao.gray.web.bean.GraySwitch;
import com.candao.gray.web.dao.GraySwitchRepository;

@Service
public class GraySwitchService {
	
	@Autowired
	private GraySwitchRepository graySwitchRepository;
	
	public GraySwitch getSwitch(){
		return graySwitchRepository.getOne(1);
	}
	
	public GraySwitch updateSwitch(GraySwitch graySwitch){
		return graySwitchRepository.update(graySwitch);
	}

}
