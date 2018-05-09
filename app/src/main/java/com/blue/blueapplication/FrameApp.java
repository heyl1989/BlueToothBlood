package com.blue.blueapplication;

import android.app.Application;

import com.blue.blueapplication.utils.UIUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.socialize.PlatformConfig;


public class FrameApp extends Application {

	public static FrameApp mApp;
	public UIUtil ui = null;
	private static String mconnetState;
	private static String isRegister;

	@Override
	public void onCreate() {
		super.onCreate();
		//微信appid appsecret
		PlatformConfig.setWeixin("wx611dd5e8a6390d67", "eda8a29b69a5762bb1fff2604618f377");
		mApp = this;
		initData();

	}


	/**
	 * 初始化工程
	 */
	private void initData() {
		UIUtil.initUIUtil(this);
		ui = UIUtil.getInstance();

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.mipmap.icon_head_portrait)
				.showImageOnFail(R.mipmap.icon_head_portrait).cacheInMemory(false)
				.cacheOnDisc(false).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.discCacheSize(1 * 1024 * 1024)//
				.discCacheFileCount(1)// 缓存一百张图片
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);

	}

	public static String getConnetState() {
		return mconnetState;
	}
	public static String getIsRegister() {
		return isRegister;
	}


	public static void setConnetState(String connetState) {
		mconnetState = connetState;
	}
	public static void setIsRegister(String connetState) {
		isRegister = connetState;
	}
}
