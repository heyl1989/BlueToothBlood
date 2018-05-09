package com.blue.blueapplication.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * Created by wangxiaojian on 16/4/27.
 */
public class Bluetooth {
    public final static int CONNECT_FAILED= 0;
    public final static int CONNECT_SUCCESS= 1;
    public final static int READ_FAILED= 2;
    public final static int WRITE_FAILED= 3;
    public final static int DATA= 4;

    private Handler handler;
    private BluetoothDevice device;
    BluetoothSocket socket;
    private boolean isConnect;

    public Bluetooth(Handler handler,BluetoothDevice device){
        this.handler = handler;
        this.device = device;
    }

    private byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }


    public void connect(final String message) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                BluetoothSocket tmp = null;
                Method method;
                try {
                    method = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    tmp = (BluetoothSocket) method.invoke(device, 1);
                } catch (Exception e) {
                    setState(CONNECT_FAILED);
                    Log.e("TAG", e.toString());
                }
                socket = tmp;
                try {
                    socket.connect();
                    isConnect = true;
                } catch (Exception e) {
                    setState(CONNECT_FAILED);
                    Log.e("TAG", e.toString());
                }
                if (isConnect) {
                    try {
                        OutputStream outStream = socket.getOutputStream();
                        outStream.write(getHexBytes(message));
                    } catch (IOException e) {
                        setState(WRITE_FAILED);
                        Log.e("TAG", e.toString());
                    }
                    try {
                        InputStream inputStream = socket.getInputStream();
                        int data;
                        while (true) {
                            try {
                                data = inputStream.read();
                                Message msg = handler.obtainMessage();
                                msg.what = DATA;
                                msg.arg1 = data;
                                handler.sendMessage(msg);
                            } catch (IOException e) {
                                setState(READ_FAILED);
                                Log.e("TAG", e.toString());
                                break;
                            }
                        }
                    } catch (IOException e) {
                        setState(WRITE_FAILED);
                        Log.e("TAG", e.toString());
                    }
                }

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        Log.e("TAG", e.toString());
                    }
                }
            }
        });
        thread.start();
    }

    private  void setState(int state){
        handler.sendEmptyMessage(state);
    }




}
