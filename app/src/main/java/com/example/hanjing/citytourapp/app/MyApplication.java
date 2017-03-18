package com.example.hanjing.citytourapp.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.hanjing.citytourapp.model.UserVo;


public class MyApplication extends Application {
	
	private static MyApplication mApplication;
	
	public final String PREF_USERNAME = "zhu_id";
	private static SharedPreferences.Editor editor;
	//private static Context context;
	private int zhu_id = -1;
	
	private UserVo userVo;
	private String city;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mApplication = this;
		//context = getApplicationContext();
	}

	public static MyApplication getInstance() {
		if(null == mApplication)
		{
			mApplication = new MyApplication();
		}
		return mApplication;
	}

	//public static Context getContext() {
	//	return context;
	//}

	public UserVo getUser(){
		return userVo;
	}
	
	
	public void setUser(UserVo userVo){
		this.userVo = userVo;
	}
	public String getCity(){
		return city;
	}
	public void setCity(String city){
		this.city = city;
	}
	
	
	public int getZhID() {
		if (zhu_id == -1) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mApplication);
			zhu_id = preferences.getInt(PREF_USERNAME, -1);
		}
		return zhu_id;
	}
	public void setZhID(int zhu_id) {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mApplication);
		editor = preferences.edit();
		
		editor.putInt(PREF_USERNAME, zhu_id);
		editor.commit();
		
	}

}
