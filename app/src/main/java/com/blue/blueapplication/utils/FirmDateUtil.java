package com.blue.blueapplication.utils;

import android.util.Log;

import com.blue.blueapplication.cache.SharePCach;

import java.util.Arrays;

/**
 * Created by heyl on 2016/8/18.
 */
public class FirmDateUtil {

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[2];
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static byte[] getHexTime() {
        Log.e("CRC",FirmUpdateData.getCrc0(SharePCach.loadStringCach("filePath"))+"");
        Log.e("LEN  ",FirmUpdateData.getlength(SharePCach.loadStringCach("filePath"))+"");
        byte crc0L = intToBytes(FirmUpdateData.getCrc0(SharePCach.loadStringCach("filePath")))[0];
        byte crc0H = intToBytes(FirmUpdateData.getCrc0(SharePCach.loadStringCach("filePath")))[1];
        byte lenL = intToBytes(FirmUpdateData.getlength(SharePCach.loadStringCach("filePath")))[0];
        byte lenH = intToBytes(FirmUpdateData.getlength(SharePCach.loadStringCach("filePath")))[1];
        byte addressL = intToBytes(1024)[0];
        byte addressH = intToBytes(1024)[1];
        Log.e("version",SharePCach.loadStringCach("version"));
        String[] version = SharePCach.loadStringCach("version").split("\\.");
        Log.e("version",version[0]);
        byte[] versionByte = {(byte)Integer.parseInt(version[0]),(byte)Integer.parseInt(version[1]),(byte)Integer.parseInt(version[2])};
        byte versionL = BCDToInt.intToBytes(BCDToInt.bytesToInt(versionByte,0))[0];
        byte versionH = BCDToInt.intToBytes(BCDToInt.bytesToInt(versionByte,0))[1];
        Log.i("date", Arrays.toString(new byte[]{crc0L, crc0H, (byte) 0xFF, (byte) 0xFF, (byte)0x00, (byte)0x00, lenL, lenH,(byte) 0x45,(byte) 0x45,(byte) 0x45,(byte) 0x45,addressL,addressH,(byte) 0x01,(byte) 0xFF}));
        return new byte[]{crc0L, crc0H, (byte) 0xFF, (byte) 0xFF, (byte)0x00, (byte)0x00, lenL, lenH,(byte) 0x45,(byte) 0x45,(byte) 0x45,(byte) 0x45,addressL,addressH,(byte) 0x01,(byte) 0xFF};
    }
}
