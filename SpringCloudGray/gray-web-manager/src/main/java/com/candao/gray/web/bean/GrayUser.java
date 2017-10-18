package com.candao.gray.web.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "grayuser")
public class GrayUser {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "service_tag")
	private String serviceTag;

	@Column(name = "service_value")
	private String serviceValue;
	
	@Column(name = "weight")
	private Integer weight;
	
	@Column(name = "strategy")
	private String strategy;
	
	@Column(name = "status")
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
