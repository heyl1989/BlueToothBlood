package com.blue.blueapplication.log;
import android.util.Log;

public final class D {

	private static boolean show_normal_log = DavidConstant.SHOW_NORMAL_LOG;//在DavidConstant里修改此�?
	private static boolean show_secret_log = DavidConstant.SHOW_SECRET_LOG;//在DavidConstant里修改此�?	
	private static boolean showTime = true;
	private static long lastTime = System.currentTimeMillis();
	private static long startTime;

	public static void setShowTime(boolean showTime) {
		D.showTime = showTime;
	}

	public static boolean isShowTime() {
		return showTime;
	}

	private synchronized static String setLog0(StackTraceElement[] stacks) {
		String log = stacks[1].getMethodName() + "(" + "" + stacks[1].getLineNumber() + ")";
		long currentTime = System.currentTimeMillis();
		if (isShowTime()) {

			if (startTime == 0) {
				startTime = currentTime;
			}
			if (lastTime == 0) {
				lastTime = currentTime;
			}
			log = log + "_" + (currentTime - startTime) + "ms_" + (currentTime - lastTime) + "ms";
		} else {
		}
		lastTime = currentTime;
		return log;
	}

	private synchronized static String setLog(StackTraceElement[] stacks) {
		//		Log.e("", "at cn.casee.atmospherepm.MainActivity.onCreate(MainActivity.java:148)_5432_5432fs");

		//		String log = stacks[1].getMethodName() + "(" + "" + stacks[1].getLineNumber() + ")";
		String fulClassName = stacks[1].getClassName();
		if (fulClassName.indexOf("$") != -1) {
			fulClassName = fulClassName.substring(0, fulClassName.indexOf("$"));
		}
		String className = fulClassName.substring(fulClassName.lastIndexOf(".") + 1, fulClassName.length());
		String log = "at " + fulClassName + "." + stacks[1].getMethodName() + "(" + className + ".java:" + stacks[1].getLineNumber() + ")";

		long currentTime = System.currentTimeMillis();
		if (isShowTime()) {

			if (startTime == 0) {
				startTime = currentTime;
			}
			if (lastTime == 0) {
				lastTime = currentTime;
			}
			log = log + "_" + (currentTime - startTime) + "ms_" + (currentTime - lastTime) + "ms";
		} else {
		}
		lastTime = currentTime;
		return log;
	}

	/**
	 * @author: Jiangtao.Cai
	* @date: 2012-8-31
	* @Description:日志输出了代码所在的类，方法，和行数，方便分析�?
	* 例如下面的一条日志输出：
	* 我在cn.casee.atmospherepm.MainActivity的onResume()方法里写�?	* D.w();
	* 而且eclipse上显示的这行代码�?��的行数是534。然后会输出�?	* cn.casee.atmospherepm.MainActivity(13306): onResume()(534)(1667ms)(1669ms)
	* 其中
	* 	cn.casee.atmospherepm.MainActivity是日志所在的类，
	* 	onResume()是当前执行的方法�?	* (534)是日志所在的行数�?	* (1667ms)是这个日志距离上�?��日志的执行时间�?
	* (1669ms)是这个日志距程序�?��后的首个日志的执行时间�?
	* 
	* 
	* 调用D.w("you log");输出除了上面的和你自定义的内容�?
	* 欢迎提出新的建议
	* �?��修改，可以点日志锁定�?��行的代码�?	@Override
	protected void onResume() {
		super.onResume();
		D.w();
		D.w();
		D.w();
		D.w();
	}
	 */
	public static void v() {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.v("D", log);
		}
	}

	public static void d() {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.d("D", log);
		}
	}

	public static void i() {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.i("D", log);
		}
	}

	public static void w() {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.w("D", log);
		}
	}

	public static void e() {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.e("D", log);
		}
	}

	public static void v(String s) {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.v("D", log + " " + s);
		}
	}

	public static void d(String s) {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.d("D", log + " " + s);
		}
	}

	public static void i(String s) {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.i("D", log + " " + s);
		}
	}

	public static void w(String s) {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.w("D", log + " " + s);
		}
	}

	public static void e(String s) {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.e("D", log + " " + s);
		}
	}

	public static void v_secret(String s) {
		if (show_secret_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.v("D", log + " " + s);
		}
	}

	public static void d_secret(String s) {
		if (show_secret_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.d("D", log + " " + s);
		}
	}

	public static void i_secret(String s) {
		if (show_secret_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.i("D", log + " " + s);
		}
	}

	public static void w_secret(String s) {
		if (show_secret_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.w("D", log + " " + s);
		}
	}

	public static void e_secret(String s) {
		if (show_secret_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			Log.e("D", log + " " + s);
		}
	}

	public static void E_secret(String s) {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			throw new IllegalArgumentException(stacks[1].getClassName() + "   " + log + "   " + s);
		}
	}

	public static void E_secret() {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			throw new IllegalArgumentException(stacks[1].getClassName() + "   " + log);
		}
	}

	public static void E(String s) {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			throw new IllegalArgumentException(stacks[1].getClassName() + "   " + log + "  " + s);
		}
	}

	public static void E() {
		if (show_normal_log) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String log = setLog(stacks);
			throw new IllegalArgumentException(stacks[1].getClassName() + "   " + log);
		}
	}
}
