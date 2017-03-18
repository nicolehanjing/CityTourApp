package com.example.hanjing.citytourapp.model;


import java.io.Serializable;

public class UserVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int id;
	public String nickName;
	public String phone;
	public int type;//0 rider    1 driver
	public int help;//0 默认    1 需要帮助
	public String pwd;
	public String home_location;
	public String cur_location;
	
	
	
}
