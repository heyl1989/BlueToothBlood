package com.blue.blueapplication.utils;

public class ClickUtils {

	private static long lastClickTime;

	/**
	 * @return 判断是不是快速点�?
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

}
