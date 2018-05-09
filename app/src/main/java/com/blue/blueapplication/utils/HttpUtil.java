package com.blue.blueapplication.utils;

import com.blue.blueapplication.log.D;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class HttpUtil {




	/**
	 * 
	 * form格式上传
	 * 
	 * @param actionUrl
	 *            上传地址
	 * @param params
	 *            键值对应的字符串型式的参数
	 * @param data
	 *            图片字节数组，如果有的话
	 * @return
	 */
	public static String formpost(String actionUrl, Map<String, String> params,
			byte[] data) {
		D.i("-------actionUrl--------"+actionUrl);
		D.i("--------params-------"+params);
		InputStream is = null;
		DataOutputStream ds = null;
		try {
			String enterNewline = "\r\n";
			String fix = "--";
			String boundary = "######";
			String MULTIPART_FORM_DATA = "multipart/form-data";

			URL url = new URL(actionUrl);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty(
					"Accept",
					"image/gif, image/png,image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, */*");
			con.setRequestProperty("Accept-Encoding", "gzip, deflate");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ ";boundary=" + boundary);

			ds = new DataOutputStream(con.getOutputStream());
			if (params != null) {
				Set<String> keySet = params.keySet();
				Iterator<String> it = keySet.iterator();

				while (it.hasNext()) {
					String key = it.next();
					String value = params.get(key);
					ds.writeBytes(fix + boundary + enterNewline);
					ds.writeBytes("Content-Disposition: form-data; "
							+ "name=\"" + key + "\"" + enterNewline);
					ds.writeBytes(enterNewline);
					ds.write(value.getBytes("UTF-8"));
					// ds.writeBytes(value);//如果有中文乱码，保存改用上面的ds.writeBytes(enterNewline);那句代码
					ds.writeBytes(enterNewline);
				}
			}

			if (data != null && data.length > 0) {
				ds.writeBytes(fix + boundary + enterNewline);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\""
						+ "imagePath" + "\"" + "; filename=\"" + "photo.png"
						+ "\"" + enterNewline);
				ds.writeBytes(enterNewline);
				ds.write(data);
				ds.writeBytes(enterNewline);
			}

			ds.writeBytes(fix + boundary + fix + enterNewline);
			ds.flush();
			is = con.getInputStream();
			return changeInputStremToString(is, "");
		} catch (Exception e) {

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ds != null) {
				try {
					ds.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	private static String changeInputStremToString(InputStream inputStream,
			String encode) throws IOException {
		int len = 0;
		byte[] data = new byte[1024];
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		while ((len = inputStream.read(data)) != -1) {
			arrayOutputStream.write(data, 0, len);
		}
		String value = new String(arrayOutputStream.toByteArray(), "utf-8");
		return value;
	}

}
