package com.candao.gray.web.bean;


import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;


@Entity
@Table(name = "graystrategy")
public class GrayStrategy {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "strategy_name")
	@NotBlank(message = "灰度策略名称不能为空")
	private String strategyName;
	
	@Column(name = "service_name")
	@NotBlank(message = "服务名称不能为空")
	private String serviceName;
	
	@Column(name = "instance_id")
	@NotBlank(message = "服务实例名称不能为空")
	private String instanceId;
	
	//权重
	@Column(name = "weight")
	private Integer weight;
	
	//服务灰度标签
	@Column(name = "service_tag")
	private String serviceTag;
	
	//灰度值域
	@Column(name = "strategy_value")
	private String strategyValue;
	
	//服务版本号
	@Column(name = "version")
	private String version;
	
	//创建时间
	@Column(name = "create_time")
	private Timestamp createTime;
	
	//修改时间
	@Column(name = "update_time")
	private Timestamp updateTime;
	
	//是否生效(0:关闭;1:生效)
	@Column(name = "status")
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getServiceTag() {
		return serviceTag;
	}

	public void setServiceTag(String serviceTag) {
		this.serviceTag = serviceTag;
	}

	public String getStrategyValue() {
		return strategyValue;
	}

	public void setStrategyValue(String strategyValue) {
		this.strategyValue = strategyValue;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
