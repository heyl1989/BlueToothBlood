package com.blue.blueapplication.utils;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by qipc on 2016/5/31.
 */
public class DataUtil {

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    public static byte[] intToByte1(int value){
        byte[] src = new byte[1];
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static String toHex(int num){
        return Integer.toHexString(num);
    }

    public static byte[] getHexTime() {
        String[] currentTime = DateUtil.getCurrentTime().split("-");
        byte yearH = intToBytes2(Integer.parseInt(currentTime[0]))[0];
        byte yearL = intToBytes2(Integer.parseInt(currentTime[0]))[1];
        byte month = intToByte1(Integer.parseInt(currentTime[1]))[0];
        byte day = intToByte1(Integer.parseInt(currentTime[2]))[0];
        byte hour = intToByte1(Integer.parseInt(currentTime[3]))[0];
        byte minute = intToByte1(Integer.parseInt(currentTime[4]))[0];
        byte second = intToByte1(Integer.parseInt(currentTime[5]))[0];
        Log.i("date", Arrays.toString(new byte[]{(byte) 0xB0, yearH, yearL, month, day, hour, minute, second}));
        return new byte[]{(byte) 0xB0, yearH, yearL, month, day, hour, minute, second};
    }

}