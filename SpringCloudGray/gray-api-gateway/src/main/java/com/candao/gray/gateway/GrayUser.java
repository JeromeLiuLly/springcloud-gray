package com.candao.gray.gateway;
public class GrayUser {
	
	private Integer id;
	
	private String userName;
	
	private String serviceTag;
	
	private String serviceValue;
	
	private String strategy;
	
	private Integer weight;
	
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getServiceTag() {
		return serviceTag;
	}

	public void setServiceTag(String serviceTag) {
		this.serviceTag = serviceTag;
	}

	public String getServiceValue() {
		return serviceValue;
	}

	public void setServiceValue(String serviceValue) {
		this.serviceValue = serviceValue;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}