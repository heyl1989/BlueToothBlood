package com.blue.blueapplication.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.blue.blueapplication.FrameApp;
import com.blue.blueapplication.R;
import com.blue.blueapplication.cache.SharePCach;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.domain.UserInfo;
import com.blue.blueapplication.http.AsyncHttpClient;
import com.blue.blueapplication.http.DomainHttpResponseHandler;
import com.blue.blueapplication.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxiaojian on 16/4/14.
 */
public class LoadingActivity extends BaseActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private List<BluetoothDevice> mdevice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter.startLeScan(leScanCallback);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(SharePCach.loadStringCach("name"))) {
                    Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    login();
                }
            }
        }, 3000);


    }


    private void login() {
        RequestParams params = new RequestParams();
        params.put("email", SharePCach.loadStringCach("name"));
        params.put("password", SharePCach.loadStringCach("pwd"));
        AsyncHttpClient client = new AsyncHttpClient(this);
        client.post(Constants.LOGIN_URL, params,
                new DomainHttpResponseHandler<UserInfo>(
                        UserInfo.class) {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    protected void onDomainSuccess(UserInfo t) {
                        Constants.mUserInfo = t;
                        if (mBluetoothAdapter == null) {
                            //跳转到搜索设备
                            Intent intent = new Intent(LoadingActivity.this, CheckDeviceActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }else{
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            } else {
                                if(mdevice.size() == 0){
                                    //选择连接设备和无设备体验
                                    Intent intent = new Intent(LoadingActivity.this, ConnectDevideActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                boolean ismDevice = false;
                                query : for(BluetoothDevice device : mdevice){
                                    Log.e("device",device.getName()+"");
                                    if(!TextUtils.isEmpty(device.getAddress()) && device.getAddress().equals(SharePCach.loadStringCach("deviceAddress"))){
                                        ismDevice = true;
                                        break query;
                                    }
                                }
                                gotoOthers(ismDevice);
                            }
                        }

                    }

                    @Override
                    protected void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        SharePCach.removeShareCach("name");
                        SharePCach.removeShareCach("pwd");
                        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                });
    }

    private void gotoOthers(boolean ismDevice) {
        if(ismDevice){
            FrameApp.setConnetState("1");
            final Intent intent = new Intent(LoadingActivity.this, MainV2Activity.class);
            intent.putExtra("name", SharePCach.loadStringCach("deviceName"));
            intent.putExtra("address", SharePCach.loadStringCach("deviceAddress"));
            startActivity(intent);
            finish();
        }else{
            //选择连接设备和无设备体验
            Intent intent = new Intent(LoadingActivity.this, ConnectDevideActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkDevice() {
        if (mBluetoothAdapter == null) return false;
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(SharePCach.loadStringCach("deviceAddress"));
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            return false;
        }else{
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(mdevice.size() == 0){
                //选择连接设备和无设备体验
                Intent intent = new Intent(LoadingActivity.this, ConnectDevideActivity.class);
                startActivity(intent);
                finish();
            }
            boolean isMDevice = false;
            query : for(BluetoothDevice device : mdevice){
                if(!TextUtils.isEmpty(device.getAddress())&&device.getAddress().equals(SharePCach.loadStringCach("deviceAddress"))){
                    isMDevice = true;
                    break query;
                }
            }
            gotoOthers(isMDevice);
        }
    }

    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback(){
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mdevice.add(device);
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        mBluetoothAdapter.stopLeScan(leScanCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothAdapter.stopLeScan(leScanCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(leScanCallback);
    }
}
