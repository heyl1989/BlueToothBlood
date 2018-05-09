package com.blue.blueapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static String get24Time(long time) {
        Date today = new Date(time);
        SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        return f.format(today);
    }

    public static String getYMDTime(long time) {
        Date today = new Date(time);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(today);
    }

    public static String getYMTime(long time) {
        Date today = new Date(time);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM");
        return f.format(today);
    }

    /**
     * 在当前日期上加N天后的日期
     *
     * @param n
     * @return
     */
    public static String getNextNDay(int n) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + n);
        return format.format(c.getTime());
    }

    /**
     * 在当前日期上加N天后的日期
     *
     * @param n
     * @return
     */
    public static String getNextNMonth(int n) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) + n);
        return format.format(c.getTime());
    }
}
