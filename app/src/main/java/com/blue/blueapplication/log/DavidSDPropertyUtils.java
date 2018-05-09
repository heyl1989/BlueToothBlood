package com.blue.blueapplication.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import android.os.Environment;

public final class DavidSDPropertyUtils {

	public static void saveMessage(Properties logProps, String filename) {
		String path = getSDPath();
		logProps.put("save time =", new Date(System.currentTimeMillis()) + "");
		FileOutputStream localFileOutputStream = null;
		try {
			File tmpFile = new File(path + "/Android");
			if (!tmpFile.exists()) {
				tmpFile.mkdirs();
			}
			File file = new File(path + "/Android", filename);
			localFileOutputStream = new FileOutputStream(file);
			logProps.save(localFileOutputStream, null);
		} catch (Exception e) {
		} finally {
			if (localFileOutputStream != null) {
				try {
					localFileOutputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	// ��ȡSDCard·��
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // check
														// SD
														// if
														// exist
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
			// get SD's file path
			return sdDir.toString();
		} else {
			return null;
		}
	}

	/**
	 * �ӱ�����ļ��У���ȡ��Ϣ��?
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getMessage(Properties logProps, String key,
			String defaultValue) {
		try {
			String value = logProps.getProperty(key);
			if (value == null || value == "") {
				return defaultValue;
			}
			return value;
		} catch (Exception e) {
		} finally {
		}
		return defaultValue;
	}

	public static Properties getProperties(String filename) {
		Properties logProps = new Properties();
		String path = getSDPath();
		FileInputStream fileInputStream = null;
		try {
			File tmpFile = new File(path + "/Android");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			File file = new File(path + "/Android", filename);
			fileInputStream = new FileInputStream(file);
			logProps.load(fileInputStream);
			return logProps;
		} catch (Exception e) {
			return new Properties();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
