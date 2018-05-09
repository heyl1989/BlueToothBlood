package com.blue.blueapplication.utils;

import com.blue.blueapplication.cache.SharePCach;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by heyl on 2016/8/18.
 */
public class FirmUpdateData {


    private static int length = 0;

    public static short getCrc0(String filePath) {
        File file = new File(filePath);
        short crcValue = 0;
        byte[] BUFFER = new byte[0x40000];
        try {
            InputStream fileStream = new FileInputStream(filePath);
            length = fileStream.available();
            fileStream.read(BUFFER, 0, BUFFER.length);

            if (length % 4096 == 0) {
                length = BUFFER.length;
                int pageCount = length / 1024 / 4;
                crcValue = calcImageCRC(0, BUFFER, ((pageCount * 0x1000) / (16 / 4)));
            } else {
                length = (length / 4096 + 1) * 4096;
                //重新生成一个新的字节数组，长度是65536，重新赋值，不足的直接补0
                byte[] buf = new byte[length];
                for (int i = 0; i < length; i++) {
                    if (i < BUFFER.length) {
                        buf[i] = BUFFER[i];
                    } else {
                        buf[i] = (byte) 0x00;
                    }
                }
                int pageCount = length / 1024 / 4;
                //将重新生成的buf传给方法，生成CRC，给设备传包的时候用buf，别用BUFFER
                crcValue = calcImageCRC(0, buf, ((pageCount * 0x1000) / (16 / 4)));
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return crcValue;
    }

    public static int getlength(String filePath) {
        File file = new File(filePath);
        short crcValue = 0;
        byte[] BUFFER = new byte[0x40000];
        try {
            InputStream fileStream = new FileInputStream(filePath);
            length = fileStream.available();
            fileStream.read(BUFFER, 0, BUFFER.length);

            if (length % 4096 == 0) {
                length = BUFFER.length;
            } else {
                length = (length / 4096 + 1) * 4096;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return length/4;
    }

    // 0x1000 4K
    private static short calcImageCRC(int page, byte[] buf, int len) {
        short crc = 0;
        long addr = page * 0x1000;

        byte pageBeg = (byte) page;
        byte pageEnd = (byte) (len / (0x1000 / 4));
        int osetEnd = ((len - (pageEnd * (0x1000 / 4))) * 4);

        pageEnd += pageBeg;

        while (true) {
            int oset;

            for (oset = 0; oset < 0x1000; oset++) {
                if ((page == pageBeg) && (oset == 0x00)) {
                    // Skip the CRC and shadow.
                    // Note: this increments by 3 because oset is
                    // incremented by 1 in each pass
                    // through the loop
                    oset += 3;
                } else if ((page == pageEnd) && (oset == osetEnd)) {
                    crc = crc16(crc, (byte) 0x00);
                    crc = crc16(crc, (byte) 0x00);

                    return crc;
                } else {
                    crc = crc16(crc, buf[(int) (addr + oset)]);
                }
            }
            page += 1;
            addr = page * 0x1000;
        }

    }

    private static short crc16(short crc, byte val) {
        final int poly = 0x1021;
        byte cnt;
        for (cnt = 0; cnt < 8; cnt++, val <<= 1) {
            byte msb;
            if ((crc & 0x8000) == 0x8000) {
                msb = 1;
            } else
                msb = 0;

            crc <<= 1;
            if ((val & 0x80) == 0x80) {
                crc |= 0x0001;
            }
            if (msb == 1) {
                crc ^= poly;
            }
        }

        return crc;
    }

    /**
     * 从一个byte[]数组中截取一部分
     *
     * @param src
     * @param begin
     * @param count
     * @return
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count + 2];
        byte packageL = FirmDateUtil.intToBytes(begin / 16)[0];
        byte packageH = FirmDateUtil.intToBytes(begin / 16)[1];
        bs[0] = packageL;
        bs[1] = packageH;
        for (int i = begin; i < begin + count; i++)
            bs[i - begin + 2] = src[i];
        return bs;
    }

}
