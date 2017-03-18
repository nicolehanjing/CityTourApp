package com.example.hanjing.citytourapp.util;

import android.util.Log;

/**
 * 定制自己的日志工具,自由地控制日志的打印，当程序处于开发阶段就让日志打印出来，当程序 上线了之后就把日志屏蔽掉。
 * 
 */
public class LogUtils {
	
	private static final String TAG = "LogUtils";
	
	private static final boolean LOGGER = true;

	public static void v(String tag, String msg) {
		if (LOGGER) {
			Log.v(TAG, tag + "-->" + msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LOGGER) {
			Log.d(TAG, tag + "-->" + msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LOGGER) {
			Log.i(TAG, tag + "-->" + msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LOGGER) {
			Log.v(TAG, tag + "-->" + msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LOGGER) {
			Log.e(TAG, tag + "-->" + msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (LOGGER) {
			Log.e(TAG, tag + "-->" + msg);
		}
	}
}
