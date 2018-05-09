package com.blue.blueapplication.utils;

import com.blue.blueapplication.log.D;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;





/**
 * 专门解析json字符串的工具类，提供了四个方�?
 * @author user
 *
 */
public class JsonTools {

	/**
	 * 把一个json字符串解析成List<Map<String, String>>
	 * @param string
	 * @return
	 */
	public static List<Map<String, String>> toList(String string) {
		if (string != null) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			try {
				JSONArray jsonArray = new JSONArray(string);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Map<String, String> map = new HashMap<String, String>();
					Iterator iterator = jsonObject.keys();
					while (iterator.hasNext()) {
						String key = (String) iterator.next();
						map.put(key.trim(), jsonObject.getString(key).trim());
					}
					list.add(map);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return list;
		}
		return null;

	}
	public static List<String> toListString(String json) {
		if (json != null) {
			List<String> list = new ArrayList<String>();
			try {
				JSONArray jsonArray = new JSONArray(json);
				for (int i = 0; i < jsonArray.length(); i++) {
					String string = jsonArray.getString(i);
				
					list.add(string);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return list;
		}
		return null;
		
	}

	/**
	 * 把一个一层json字符串解析成map
	 * 
	 * @param string
	 * @return
	 */
	public static Map<String, String> toMap(String string) {
		if (string != null&&!string.equals("")) {
			Map<String, String> map = new TreeMap<String, String>();
			try {
				JSONObject jsonObject = new JSONObject(string);
				Iterator iterator = jsonObject.keys();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					map.put(key.trim(), jsonObject.getString(key).trim());
				}
			} catch (JSONException e) {
				D.w("----JSONException----"+e.toString());
				return null;
			}
			return map;
		}
		return null;

	}

	/**
	 * 如果json字符串有两层，则使用此方法直接可以解析json字符串为javabean，切记类的属性名于返回的json字符串的键名要完全一�?
	 * 
	 * @param jsonString
	 * @param cls
	 * @param listOrKey
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> List<T> toListBean(String jsonString, Class<T> cls,
			String listOrKey){
		JSONObject jsonObject;
		List<T> list ;
		try {
			jsonObject = new JSONObject(jsonString);
			list = new ArrayList<T>();
			JSONArray jsonArray  = (JSONArray) jsonObject.get(listOrKey);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonO = jsonArray.getJSONObject(i);
				Field[] fields = cls.getDeclaredFields();
				T bean = cls.newInstance();
				for (int j = 0; j < fields.length; j++) {
					fields[j].setAccessible(true);
					if (jsonO.has(fields[j].getName())) {
							fields[j].set(bean,jsonO.getString(fields[j].getName()));
					}
				}
				list.add(bean);
			}
		} catch (Exception e) {
			D.i("----Exception e--------------------------"+e.toString());
			return null;
		}
		
		return list;
	}
	public static <T> List<T> toListBeanNoKey(String jsonString, Class<T> cls){
		List<T> list ;
		try {
			list = new ArrayList<T>();
			JSONArray jsonArray  = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonO = jsonArray.getJSONObject(i);
				Field[] fields = cls.getDeclaredFields();
				T bean = cls.newInstance();
				for (int j = 0; j < fields.length; j++) {
					fields[j].setAccessible(true);
					if (jsonO.has(fields[j].getName())) {
						fields[j].set(bean,jsonO.getString(fields[j].getName()));
					}
				}
				list.add(bean);
			}
		} catch (Exception e) {
			D.i("----Exception e--------------------------"+e.toString());
			return null;
		}
		
		return list;
	}
	
	


	/**
	 * 如果json字符串有�?层，则使用此方法直接可以解析json字符串为javabean，切记类的属性名于返回的json字符串的键名要完全一�?
	 * @param jsonString
	 * @param cls
	 * @param listOrKey
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> T toSingleBean(String jsonString, Class<T> cls)  {
		if (jsonString==null||jsonString.equals("")) {
			return null;
		}
		JSONObject jsonObject;
		T t = null;
		try {
			jsonObject = new JSONObject(jsonString);
			t = cls.newInstance();
			Field[] fields = cls.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				if (jsonObject.has(fields[i].getName())) {
						fields[i].set(t,jsonObject.getString(fields[i].getName()));
				}
			}
		} catch (Exception e) {
			D.i("------"+e.toString());
		}
		return t;
	}
	
	public static <T> T toSingleBean(String jsonString, Class<T> cls,String key)  {
		Map<String, String> map = toMap(jsonString);
		if (map!=null&&map.containsKey(key)) {
			return toSingleBean(map.get(key), cls);
		}
		return null;
	}
	
	
		  
		  private static String getString(JSONObject json, String key) {
			    if (json.has(key)) {
			      if (json.isNull(key) == false) {
			        return json.optString(key, "");
			      }
			    }

			    return null;
			  }

}
