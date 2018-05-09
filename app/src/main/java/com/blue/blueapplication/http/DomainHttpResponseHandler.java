package com.blue.blueapplication.http;

import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;

import com.blue.blueapplication.domain.ResponseData;
import com.blue.blueapplication.log.D;
import com.blue.blueapplication.utils.GsonUtil;
import com.blue.blueapplication.utils.JsonTools;
import com.blue.blueapplication.utils.ToastUtil;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;


public class DomainHttpResponseHandler<T> extends AsyncHttpResponseHandler {
	private final static int SUCCESS_DOMAIN_MESSAGE = 101;
	private final static int SUCCESS_MSG_MESSAGE = 102;
	private final static int SUCCESS_DOMAIN_DATA_CONTENT = 103;
	private Class<T> cLass;
	protected String msg;

	public DomainHttpResponseHandler(Class<T> c) {
		super();
		cLass = c;
	}

    private void logToFile(String str) {
        try {
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "log.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
            out.write(str + "\n");
            out.close();
            out = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	@Override
	protected void sendSuccessMessage(int statusCode, Header[] headers,
			String responseBody) {
		try {
			D.i("-------网络返回数据-----, responseBody:" + responseBody);
			//logToFile(responseBody);
			if (!TextUtils.isEmpty(responseBody)) {
				Map<String, String> resultMap;
				resultMap = JsonTools.toMap(responseBody);
				if (resultMap != null) {
					// 获取到服务器返回的返回码
					String resultCode = resultMap.get("status");
					// 获取到服务器返回的msg信息（这个信息不能显示给用户，用来对接口进行判断）
					msg = resultMap.get("msg");
					if ("0".equals(resultCode)) {
						sendMessage(obtainMessage(SUCCESS_DOMAIN_DATA_CONTENT,
								responseBody));
						Object object = parseJson(responseBody);
						if (object != null) {
							sendMessage(obtainMessage(SUCCESS_DOMAIN_MESSAGE,
									object));
						} else {
							sendFailureMessage(null, "解析数据异常");
						}
					} else {

							sendFailureMessage(null, msg);
					}
				} else {
					sendFailureMessage(null, "数据格式有问题");
				}
			} else {
				sendFailureMessage(null, "网络数据为空");
			}
		} catch (Exception e) {
			sendFailureMessage(null, "解析数据错误");
		}

	}

	@Override
	protected void handleMessage(Message msg) {
	    D.i("handleMessage, type:" + msg.what);
		switch (msg.what) {
		case SUCCESS_DOMAIN_MESSAGE:
			parseObject(msg.obj);
			break;
		case SUCCESS_MSG_MESSAGE:
			onResponseMsgSuccess((String) msg.obj);
			break;
		case SUCCESS_DOMAIN_DATA_CONTENT:
			onSuccessReturnString((String) msg.obj);
			break;
		default:
			super.handleMessage(msg);
		}
	}

	/**
	 * 判断解析完的对象类型，回调相应的成功接口
	 * 
	 * @param object
	 */
	@SuppressWarnings("unchecked")
	private void parseObject(Object object) {
		if (object instanceof List<?>) {
			onSuccess((List<T>) object);
		} else if (object instanceof String) {
			onSuccessReturnString((String) object);
		} else {
			T t = (T) object;
			onSuccess(t);
		}
	}

	/**
	 * 解析成了对象
	 * 
	 * @param t
	 */
	protected void onSuccess(T t) {
		if (t == null) {
			onFailure(null, "解析出的对象为空");
		} else {
			onDomainSuccess(t);
		}
	}

	/**
	 * 解析成了列表
	 * 
	 * @param list
	 */
	private void onSuccess(List<T> list) {
		if (list == null) {
			onFailure(null, "解析出的列表为空");
		} else {
			onDomainListSuccess(list);
		}
	}

	/**
	 * 解析对象成功,如果对返回的data下的数据进行展示或者处理的时候需要实现此方法
	 * 
	 * @param t
	 */
	protected void onDomainSuccess(T t) {
		if (t instanceof ResponseData) {
			ResponseData data = (ResponseData) t;
			ToastUtil.showToast("提交成功");
		}
	}

	/**
	 * 解析列表成功，如果对返回的data下的数据进行展示或者处理的时候需要实现此方法
	 * 
	 * @param list
	 */
	protected void onDomainListSuccess(List<T> list) {

	}

	/**
	 * 解析失败直接称字符串
	 * 
	 * @param t
	 */
	protected void onSuccessReturnString(String t) {

	}

	/**
	 * 因为有的接口会根据error下的msg判断页面跳转，所以单独对msg进行处理
	 */
	private void onResponseMsgSuccess(String obj) {
		if (TextUtils.isEmpty(obj)) {
			onMsgSuccess("msg error");
		} else {
			onMsgSuccess(obj);
		}
	}

	/**
	 * 因为有的接口会根据error下的msg判断页面跳转，所以单独对msg进行处理
	 */
	protected void onMsgSuccess(String msg) {

	}

	/**
	 * 服务器返回的json数据的data部分解析成对象
	 * 
	 * @param responseBody
	 * @return
	 */
	private Object parseJson(String responseBody) {
		Object result = null;
		try {
			responseBody = responseBody.trim();
			if (responseBody.startsWith("{")) {
				result = GsonUtil.getInstance().fromJson(responseBody, cLass);
			} else if (responseBody.startsWith("[")) {
				result = GsonUtil.getInstance().fromJson(responseBody,
						new TypeToken<List<T>>() {
						}.getType());
			}
		} catch (JsonSyntaxException e) {
		    D.i("parseJson, JsonSyntaxException:" + e.toString());
			e.printStackTrace();
		} catch (Exception ex) {
		    D.i("parseJson, JsonSyntaxException:" + ex.toString());
		    result = null;
		}
		return result;
	}

}
