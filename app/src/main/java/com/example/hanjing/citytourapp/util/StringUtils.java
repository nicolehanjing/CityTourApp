package com.example.hanjing.citytourapp.util;

public class StringUtils {
	/***
	 * 判断字符串为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return (null == str || str.length() <= 0 || "null".equals(str));
	}

	/***
	 * 判断字符串不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !(null == str || str.length() <= 0 || "null".equals(str));
	}

	/***
	 * 是否是几位数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str, int len) {
		if (isBlank(str)) {
			return false;
		} else {
			String telRegex = "\\d{" + len + "}";
			return str.matches(telRegex);
		}
	}

	/***
	 * 数字转化
	 * 
	 * @param number
	 * @return
	 */
	public static String ChangeToCapital(int number) {
		String mNum = "两";
		switch (number) {
		case 2:
			mNum = "两";
			break;
		case 3:
			mNum = "三";
			break;
		case 4:
			mNum = "四";
			break;
		case 5:
			mNum = "五";
			break;
		case 6:
			mNum = "六";
			break;
		case 7:
			mNum = "七";
			break;
		case 8:
			mNum = "八";
			break;
		case 9:
			mNum = "九";
			break;
		case 10:
			mNum = "十";
			break;
		case 11:
			mNum = "十一";
			break;
		case 12:
			mNum = "十二";
			break;
		case 13:
			mNum = "十三";
			break;
		case 14:
			mNum = "十四";
			break;
		default:
			mNum = "两";
			break;
		}

		return mNum;
	}

}
