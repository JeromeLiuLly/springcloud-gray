package com.candao.gray.user.api.bean;

public class Horseman {
	
    private Integer id;

    private String loginName;       //骑士登陆账号

    private String userName;        //骑士昵称

    private String horsemanPhone;   //骑士手机号码

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHorsemanPhone() {
		return horsemanPhone;
	}

	public void setHorsemanPhone(String horsemanPhone) {
		this.horsemanPhone = horsemanPhone;
	}
}