package com.blue.blueapplication.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchStringUtil {

	
	
	/**
	 * 验证字符串是否由6-15位字母数字组�?
	 * @param mobiles
	 * @return
	 */
	public static boolean judgeString(String string) {
		if (string.equals("")||string.length()<6||string.length()>15) {
			return false;
		}
		//：^(?!_)(?!.*?_$)[a-zA-Z0-9_]+$  字母数字下划�??
		Pattern p = Pattern
				.compile("^[a-zA-Z0-9_]+$");
		Matcher m = p.matcher(string);
		return m.matches();
	}
	
	/**
	 * 验证手机号码是否正确
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(14[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	/**
	 * �?*代替手机中间四位
	 * @param number
	 * @return
	 */
	public static String replaceNumber(String number) {
		if (number==null) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < number.length(); i++) {
			if (i >= 3 && i <= 6) {
				buffer.append("*");
				continue;
			}
			buffer.append(number.charAt(i));
		}
		return buffer.toString();
	}
}
