package com.blue.blueapplication.utils;

import com.google.gson.Gson;

public class GsonUtil {

	public static Gson gson;

	private GsonUtil() {

	}

	public static Gson getInstance() {
		if (gson == null) {
			synchronized (GsonUtil.class) {
				if (gson == null) {
					gson = new Gson();
				}
				return gson;
			}
		} else {
			return gson;
		}
	}

}
