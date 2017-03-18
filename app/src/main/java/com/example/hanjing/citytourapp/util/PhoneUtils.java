package com.example.hanjing.citytourapp.util;


import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.example.hanjing.citytourapp.app.MyApplication;


public class PhoneUtils {

	/****
	 * 拨打电话
	 * 
	 * @param phone
	 */
	public static void call(String phone) {
		if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(phone.trim())) {
			return;
		}
	  try {
		    Intent intent = new Intent(Intent.ACTION_CALL);
			Uri data = Uri.parse("tel:" + phone);
			intent.setData(data);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MyApplication.getInstance().startActivity(intent);	
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	/**
	 * 判断手机号码
	 * 
	 * @param phoneNo
	 * @return
	 */
	public static boolean isMobilePhone(String phoneNo) {
		String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(phoneNo))
			return false;
		else
			return phoneNo.matches(telRegex);

	}
}
