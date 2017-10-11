package com.candao.gray.web.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "grayswitch")
public class GraySwitch {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "gray_switch")
	private Integer graySwitch;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGraySwitch() {
		return graySwitch;
	}

	public void setGraySwitch(Integer graySwitch) {
		this.graySwitch = graySwitch;
	}
}
