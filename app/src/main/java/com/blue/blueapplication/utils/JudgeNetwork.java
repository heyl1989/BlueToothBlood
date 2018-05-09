package com.blue.blueapplication.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.blue.blueapplication.FrameApp;


/**
 * 用来判断网络的类
 * @author user
 *
 */
public class JudgeNetwork {
	
	/**
	 * 判断当前网络是否是wifi
	 * 
	 * @return
	 */
	public static boolean isWiFiActive() {
		ConnectivityManager connectivity = (ConnectivityManager) FrameApp.mApp
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName() != null
							&& info[i].getTypeName().length() > 0) {
						String name = info[i].getTypeName().toUpperCase();
						if (name.equals("WIFI") && info[i].isConnected()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断是否网络处于连接状�?
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetWorkAvailable() {
		try {
			ConnectivityManager cm = (ConnectivityManager) FrameApp.mApp
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			return (info != null && info.isConnected() && info.isAvailable());
		} catch (Exception e) {
		}

		return false;

	}
	

}
