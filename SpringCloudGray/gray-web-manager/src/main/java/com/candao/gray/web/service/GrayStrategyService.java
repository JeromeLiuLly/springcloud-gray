package com.candao.gray.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.candao.gray.web.bean.GrayStrategy;
import com.candao.gray.web.dao.GrayStrategyRepository;

@Service
public class GrayStrategyService {
	
	@Autowired
	private GrayStrategyRepository grayStrategyRepository;
	
	public List<GrayStrategy> getAllStrategy(){
		return grayStrategyRepository.findAll();
	}
	
	public GrayStrategy getStrategyById(Integer id){
		return grayStrategyRepository.getOne(id);
	}
	
	public GrayStrategy updateStrategyById(GrayStrategy grayStrategy){
		return grayStrategyRepository.update(grayStrategy);
	}
	
	public GrayStrategy saveStrategy(GrayStrategy grayStrategy){
		return grayStrategyRepository.save(grayStrategy);
	}
	
	public void deleteStrategyById(Integer id){
		grayStrategyRepository.delete(id);
	}

}
