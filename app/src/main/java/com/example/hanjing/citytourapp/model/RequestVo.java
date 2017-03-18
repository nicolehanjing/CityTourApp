package com.example.hanjing.citytourapp.model;

import java.io.Serializable;

public class RequestVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int id;
	public int user_id;
	public String start_loc;
	public String start_add;
	public String end_loc;
	public String end_add;
	public int type;//0 乘客 1 司机
	public int status;//0 未处理 1 进行中 2 已结束
	public String time;
	public String uuid;
	public int number;
	public int money;
	public int priority;//优先级
	public int receiving_id;//优先级

}
