package com.blue.blueapplication.utils;

import android.widget.Toast;

import com.blue.blueapplication.FrameApp;

public class ToastUtil {

	/**
	 * android 标准提示
	 * @param id 文字id
	 */
	public static void showToast(int id) {
		String mess = FrameApp.mApp.getResources().getString(id);
		Toast.makeText(FrameApp.mApp, mess, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示字符穿
	 * @param mess
	 */
	public static void showToast(String mess) {
		if (mess == null) {
			return;
		}
		Toast.makeText(FrameApp.mApp, mess, Toast.LENGTH_SHORT).show();
	}
}
