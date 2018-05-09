package com.blue.blueapplication.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by heyl on 2016/8/18.
 */
public class FirmFileUtil {

    private static int length = 0;
    /**
     * 文件转化为字节数组
     *
     * @param filePath
     * @return
     */
    public static byte[] getBytesFromFile(String filePath) {
        File file = new File(filePath);
        short crcValue = 0;
        byte[] buf = null;
        byte[] BUFFER = new byte[0x40000];
        try {
            InputStream fileStream = new FileInputStream(filePath);
            length = fileStream.available();
            fileStream.read(BUFFER, 0, BUFFER.length);

            //从这里开始，计算缓冲区大小
            if (length % 4096 == 0) {
                length = BUFFER.length;
            }else{
                length = (length/4096 + 1)*4096;
            }
            //重新生成一个新的字节数组，长度是65536，重新赋值，不足的直接补0
            buf = new byte[length];
            for (int i = 0; i<length; i++) {
                if (i <BUFFER.length) {
                    buf[i] = BUFFER[i];
                }else{
                    buf[i] = (byte)0x00;
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return buf;

    }

    /**
     * 文件转化为字节数组（厂家）
     * @param filePath
     * @return
     */
    public static  byte[] getFileByte(String filePath) {
        File mFile = new File(filePath);
        byte[] buffer = null;
        InputStream mFileStream = null;
        try {
            mFileStream = new FileInputStream(mFile);
            int length = mFileStream.available();
            if (length % 4096 != 0) {
                int m = length / 4096;
                length = (m + 1) * 4096;
            }
            buffer = new byte[length];
            mFileStream.read(buffer, 0, buffer.length);
            mFileStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buffer;
    }
}
