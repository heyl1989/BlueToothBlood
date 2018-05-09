package com.blue.blueapplication.utils;

import android.app.Activity;

import com.blue.blueapplication.config.Constants;

import java.util.ArrayList;
import java.util.List;


public class ControlActivity {

	/**
	 * 获取某个activity
	 * 
	 * @param cls
	 * @return
	 */
	public static Activity getActivity(Class<?> cls) {
		for (int i = 0; i < Constants.listActivity.size(); i++) {
			Activity a = Constants.listActivity.get(i);

			if (cls.getName().equals(a.getClass().getName())) {
				return a;
			}
		}

		return null;
	}

	/**
	 * 关闭�??��的activity
	 */
	public static void closeAllActivity() {
		List<Activity> list = new ArrayList<Activity>();

		for (int i = 0; i < Constants.listActivity.size(); i++) {
			list.add(Constants.listActivity.get(i));
		}

		for (int i = 0; i < list.size(); i++) {
			list.get(i).finish();
		}
	}

	/**
	 * 关闭除了某个activity的其他所有activity
	 * 
	 * @param cls
	 */
	public static void closeAllActivityExcept(Class<?> cls) {
		List<Activity> list = new ArrayList<Activity>();

		for (int i = 0; i < Constants.listActivity.size(); i++) {
			if (!cls.getName().equals(
					Constants.listActivity.get(i).getClass().getName())) {
				list.add(Constants.listActivity.get(i));
			}
		}

		for (int i = 0; i < list.size(); i++) {
			list.get(i).finish();
		}
	}

//	/**
//	 * 关闭除了当前main里面的activity的其他activity
//	 * 
//	 * @param cls
//	 */
//	public static void closeMainActivity(Class<?> cls) {
//		List<Activity> list = new ArrayList<Activity>();
//
//		for (int i = 0; i < Constants.listActivity.size(); i++) {
//			if (Constants.currentActivity.getName().equals(
//					Constants.listActivity.get(i).getClass().getName())) {
//				System.out.println("--------"+Constants.currentActivity.getName());
//				System.out.println("--------"+Constants.listActivity.get(i).getClass().getName());
//				break;
//			}
//			if (cls.getName().equals(
//					Constants.listActivity.get(i).getClass().getSuperclass()
//							.getName())) {
//				list.add(Constants.listActivity.get(i));
//			}
//		}
//
//		for (int i = 0; i < list.size(); i++) {
//			System.out.println("--------"+list.get(i).getClass().getName());
//			list.get(i).finish();
//		}
//	}

	/**
	 * 关闭某个activity
	 * 
	 * @param cls
	 */
	public static void closeActivity(Class<?> cls) {
		List<Activity> list = new ArrayList<Activity>();

		for (int i = 0; i < Constants.listActivity.size(); i++) {
			if (cls.getName().equals(
					Constants.listActivity.get(i).getClass().getName())) {
				list.add(Constants.listActivity.get(i));
			}
		}

		for (int i = 0; i < list.size(); i++) {
			list.get(i).finish();
		}
	}
}
