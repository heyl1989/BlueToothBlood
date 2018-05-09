package com.blue.blueapplication.utils;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.blue.blueapplication.FrameApp;


/**
 * 获取屏幕大小，dip变像素，像素变dip，同时封装了android 标准toast方法方便工程应用
 * @author user
 *
 */
public class UIUtil {

	private Application app = null;

	private UIUtil(Application app) {
		this.app = app;
		this.mDisplayMetrics = new DisplayMetrics();
	}

	private static UIUtil __instance = null;

	public static void initUIUtil(Application app) {
		if (__instance == null)
			__instance = new UIUtil(app);
		__instance.init();
	}

	public synchronized static UIUtil getInstance() {
		return __instance;
	}

	private final DisplayMetrics mDisplayMetrics;
	private boolean inited = false;

	private void init() {
		if (inited) {
			return;
		}
		WindowManager mWindowManager = ((WindowManager) app
				.getSystemService(Context.WINDOW_SERVICE));
		mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
		inited = true;
	}

	public DisplayMetrics getDisplayMetrics() {
		return mDisplayMetrics;
	}

	public String makeTimeString(long milliSecs) {

		int second = (int) milliSecs / 1000;

		return makeTimeString(second);
	}

	/**
	 * 鐢熸垚瑕佹樉�??��殑鏃堕棿瀛楃锟�??
	 * 
	 * @param milliSecs
	 * @return
	 */

	public String makeTimeString(int second) {
		StringBuffer sb = null;
		sb = new StringBuffer();
		int m = second / 60;
		sb.append(m < 10 ? "0" : "").append(m);
		sb.append(":");
		int s = second % 60;
		sb.append(s < 10 ? "0" : "").append(s);
		return sb.toString();
	}

	/**
	 * 鏃堕棿鐨勮浆锟�
	 * 
	 * @param time
	 * @return
	 */
	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	/**
	 * 灏嗛渶瑕佺殑瀛椾綋澶у皬杞�??��涓哄疄闄呮樉�??��ぇ锟�??
	 * 
	 * @param size
	 * @return
	 */
	public float getTextSizeAjustDensity(float size) {
		if (size <= 0) {
			size = 15;
		}
		float realSize = (float) (size * (getDensity() - 0.1));
		return realSize;
	}

	/**
	 * 获取手机密度:low, mid, high, x
	 */
	public float getDensity() {
		return mDisplayMetrics.density;
	}

	/**
	 * 
	 * 获取屏幕宽度
	 */
	public int getmScreenWidth() {
		return mDisplayMetrics.widthPixels;
	}

	/**
	 * 
	 * 获取屏幕高度
	 */
	public int getmScreenHeight() {
		return mDisplayMetrics.heightPixels;
	}

	/**
	 * dip变像px	 * 
	 * @param dpValue
	 * @return
	 */
	public int DipToPixels(float dpValue) {
		final float scale = getDensity();
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 像素变dip
	 * 
	 * @param pxValue
	 * @return
	 */
	public int PixelsToDip(float pxValue) {
		final float scale = getDensity();
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * android 标准提示
	 * @param id 文字id
	 */
	public static void showToast(int id) {
		String mess = FrameApp.mApp.getResources().getString(id);
		Toast.makeText(FrameApp.mApp, mess, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示字符�?
	 * @param mess
	 */
	public static void showToast(String mess) {
		if (mess == null) {
			return;
		}
		Toast.makeText(FrameApp.mApp, mess, Toast.LENGTH_SHORT).show();
	}
}
