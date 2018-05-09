package com.blue.blueapplication.utils;

import java.text.SimpleDateFormat;

public class Utility {

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().equals("")) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return yyyy.MM.dd HH:mm
	 */
	public static String formatTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		return sdf.format(time);
	}

}
