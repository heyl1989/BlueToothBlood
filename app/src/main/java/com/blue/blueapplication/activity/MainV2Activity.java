package com.blue.blueapplication.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.blue.blueapplication.R;
import com.blue.blueapplication.cache.SharePCach;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.domain.ResponseData;
import com.blue.blueapplication.fragment.ContentFragment;
import com.blue.blueapplication.fragment.MenuFragment;
import com.blue.blueapplication.http.AsyncHttpClient;
import com.blue.blueapplication.http.DomainHttpResponseHandler;
import com.blue.blueapplication.http.RequestParams;
import com.blue.blueapplication.slidingmenu.lib.SlidingMenu;
import com.blue.blueapplication.slidingmenu.lib.app.SlidingFragmentActivity;
import com.blue.blueapplication.utils.DataUtil;
import com.blue.blueapplication.utils.DateUtil;
import com.blue.blueapplication.utils.FirmDateUtil;
import com.blue.blueapplication.utils.FirmFileUtil;
import com.blue.blueapplication.utils.FirmUpdateData;
import com.blue.blueapplication.utils.ToastUtil;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainV2Activity extends SlidingFragmentActivity {

    private MenuFragment mMenuFragment;
    private ContentFragment mContentFragment;
    private SlidingMenu sm;
    private PowerManager.WakeLock wakeLock = null;

    private final static String TAG = "mainv2activity";

    public static final String EXTRAS_DEVICE_NAME = "name";
    public static final String EXTRAS_DEVICE_ADDRESS = "address";

    public static final String OPEN_XUEYAYI = "open";
    public static final String CLOSE_XUEYAYI = "close";
    public static final String HISTORY_DATA = "history";
    public static final String MODIFY_TIME = "modifyTime";
    public static final String VERSION = "version";
    public static final String UPDATE = "update";
    public static final String UPDATE_ONE = "update_one";
    public static final String UPDATE_TWO = "update_two";
    public static final String TYPE = "type";

    public static MainV2Activity mainV2Activity;

    private String mDeviceName;
    public String mDeviceAddress;
    public BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    public boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    //蓝牙服务的集合
    public List<BluetoothGattService> mGattServices;
    private byte[] fileBuffer;
    private Handler handler;
    public boolean isUpdate = false;
    public int i = 0;
    public boolean lastPackage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Constants.listActivity.add(this);
        mainV2Activity = this;
        handler=new Handler();
        //绑定服务
        Intent gattServiceIntent = new Intent(MainV2Activity.this, BluetoothLeService.class);
        boolean bll = bindService(gattServiceIntent, mServiceConnection,
                BIND_AUTO_CREATE);
        Log.i("service", "绑定服务" + bll);
        //注册广播接受者
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        if (!TextUtils.isEmpty(mDeviceName) || !TextUtils.isEmpty(mDeviceAddress)) {
            SharePCach.saveStringCach("deviceAddress", mDeviceAddress);
            SharePCach.saveStringCach("deviceName", mDeviceName);
        }

        setBehindContentView(R.layout.menu_container);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager()
                    .beginTransaction();
            mMenuFragment = new MenuFragment();
            t.replace(R.id.menu_frame, mMenuFragment);
            t.commit();
        } else {
            mMenuFragment = (MenuFragment) this.getSupportFragmentManager()
                    .findFragmentById(R.id.menu_frame);
        }
        sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setRightMenuOffsetRes(R.dimen.right_menu_offset);
        sm.setFadeEnabled(false);
        sm.setBehindWidthRes(R.dimen.left_menu_width);
        sm.setMode(SlidingMenu.LEFT);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mContentFragment = ContentFragment.newInstance(!TextUtils.isEmpty(mDeviceName));

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContentFragment).commit();
        if (!TextUtils.isEmpty(mDeviceName)) {
            Constants.isConnectBlue = true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        acquireWakeLock(this);
        if (mConnected) {
            displayGattServices(mGattServices, HISTORY_DATA);
        }
    }

    /**
     * 开启服务
     */
    public void startBlueService() {
        //如果mDeviceName为空提示，
        if (TextUtils.isEmpty(mDeviceName) || TextUtils.isEmpty(mDeviceAddress)) {
            ToastUtil.showToast("请选择搜索设备频道进行检测");
        } else {
            if (mGattServices != null && !mGattServices.isEmpty()) {
                displayGattServices(mGattServices, OPEN_XUEYAYI);
            }
        }
    }


    public void toggle(int state) {
        if (state == 0) {
            sm.showMenu();
        } else {
            sm.showContent();
        }
    }

    // Code to manage Service lifecycle.
    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            System.out.println("action = " + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                SharePCach.saveStringCach("deviceAddress", mDeviceAddress);
                SharePCach.saveStringCach("deviceName", mDeviceName);
                //链接成功
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                Constants.isConnectBlue = false;
                mConnected = false;
                //断开链接
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                mConnected = true;
                mGattServices = mBluetoothLeService.getSupportedGattServices();
                for (int i=0 ; i<mGattServices.size();i++){
                    Log.e("mGattServices",mGattServices.get(i).getUuid()+"");
                }
                displayGattServices(mGattServices, HISTORY_DATA);
                //TODO
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent
                        .getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void displayData(String string) {
        Log.e("string", string + "");
        if (!TextUtils.isEmpty(string)) {
            if (string.startsWith("-25")) {
                Log.e("historyData", string + "");
                String[] historyDatas = string.split(",");
                int num = Integer.parseInt(historyDatas[1]);
//            if(num > SharePCach.loadIntCach("historyNum")){
//                SharePCach.saveIntCach("historyNum",num);
//            }else{
//                SharePCach.saveIntCach("historyNum",SharePCach.loadIntCach("historyNum") + num-1);
//            }
                if (num != 0) {
                    uploadData(historyDatas[2], historyDatas[3], historyDatas[4], historyDatas[5]);
                }
            } else if (string.startsWith("-112")) {
                mContentFragment.updateData(string);
            } else if (string.startsWith("90")) {
                mContentFragment.isTest = false;
                mContentFragment.currentProgress = 100;
            } else if (string.startsWith("-80")) {
                startBlueService();
            } else if (string.startsWith("-94")) {
                String[] historyDatas = string.split(",");
                if (TextUtils.equals("0", historyDatas[4].trim())) {
                    Toast.makeText(MainV2Activity.this, "电池电量低，不可以固件升级", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.equals("1", historyDatas[4].trim())) {
                    firmUpdate1();
                }
                if (TextUtils.equals("2", historyDatas[4].trim())) {
                    try {
                        Log.e("读取版本号", historyDatas[1].trim() + "/" + historyDatas[2].trim() + "/" + historyDatas[3].trim());
                        String b = "";
                        String c = "";
                        String d = "";
                        if (Integer.toHexString(Integer.parseInt(historyDatas[1].trim())).length() == 1) {
                            b = "0" + Integer.toHexString(Integer.parseInt(historyDatas[1].trim()));
                        } else {
                            b = Integer.toHexString(Integer.parseInt(historyDatas[1].trim()));
                        }
                        if (Integer.toHexString(Integer.parseInt(historyDatas[2].trim())).length() == 1) {
                            c = "0" + Integer.toHexString(Integer.parseInt(historyDatas[2].trim()));
                        } else {
                            c = Integer.toHexString(Integer.parseInt(historyDatas[2].trim()));
                        }
                        if (Integer.toHexString(Integer.parseInt(historyDatas[3].trim())).length() == 1) {
                            d = "0" + Integer.toHexString(Integer.parseInt(historyDatas[3].trim()));
                        } else {
                            d = Integer.toHexString(Integer.parseInt(historyDatas[3].trim()));
                        }
                        if (null != UpdateFirmwareActivity.updateFirmwareActivity.firm_version) {
                            UpdateFirmwareActivity.updateFirmwareActivity.firm_version.setText("幻响 V" + b + "." + c + "." + d);
                            SharePCach.saveStringCach("version", b + "." + c + "." + d);
                            getType();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "读取版本号错误", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (string.startsWith("-92")) {
                String[] typeDatas = string.split(",");
                SharePCach.saveStringCach("type", typeDatas[1].trim() + "");
            } else if (string.startsWith("-104")) {
                String[] typeDatas = string.split(",");
                if (TextUtils.equals(typeDatas[1].trim(), "0") && TextUtils.equals(typeDatas[2].trim(), "0")) {
                    firmUpdate2();
                }
            }
        }
    }

    /**
     * 关闭指令
     */
    public void closeBlueDevice() {
        displayGattServices(mGattServices, CLOSE_XUEYAYI);
    }

    /**
     * 修改时间
     */
    public void modiFyTime() {
        displayGattServices(mGattServices, MODIFY_TIME);
    }

    /**
     * 固件升级第一步
     */
    public void firmUpdate1() {
        displayUpdateGattServices(mGattServices, UPDATE_ONE);
    }

    /**
     * 固件升级第二步
     */
    public void firmUpdate2() {
        displayUpdateGattServices(mGattServices, UPDATE_TWO);
    }

    /**
     * 固件升级
     */
    public void firmUpdate() {
        displayGattServices(mGattServices, UPDATE);
    }

    /**
     * 获取版本号
     */
    public void getVersion() {
        displayGattServices(mGattServices, VERSION);
    }

    /**
     * 获取识别码
     */
    public void getType() {
        displayGattServices(mGattServices, TYPE);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices, String order) {
        if (gattServices == null)
            return;
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                if (gattCharacteristic.getUuid().toString()
                        .equalsIgnoreCase("f0080003-0451-4000-b000-000000000000") ||
                        gattCharacteristic.getUuid().toString().equals("f0080002-0451-4000-b000-000000000000")) {
                    sendData(gattCharacteristic, order);
                }
            }
        }
    }

    /**
     * APp向设备发送数据
     */
    private void sendData(BluetoothGattCharacteristic characteristic, String order) {
        final int charaProp = characteristic.getProperties();
        if (characteristic.getUuid().toString()
                .equalsIgnoreCase("f0080003-0451-4000-b000-000000000000")) {
            if (TextUtils.equals("open", order)) {
                Log.i("service", "开始指令");
                characteristic.setValue(new byte[]{(byte) 0x90, (byte) 0x01});
            }
            if (TextUtils.equals("close", order)) {
                Log.i("service", "关闭指令");
                characteristic.setValue(new byte[]{(byte) 0x90, (byte) 0x00});
            }
            if (TextUtils.equals("history", order)) {
                Log.i("service", "历史数据");
                int num = SharePCach.loadIntCach("historyNum");
                Log.i("蓝牙服务GATT协议的返回数据", num + "" + (byte) 0xE7);
                characteristic.setValue(new byte[]{(byte) 0xE7, (byte) (num + 1)});
            }
            if (TextUtils.equals(MODIFY_TIME, order)) {
                Log.i("service", "设置时间");
                characteristic.setValue(DataUtil.getHexTime());
            }
            if (TextUtils.equals(VERSION, order)) {
                Log.i("service", "获取版本号");
                characteristic.setValue(new byte[]{(byte) 0xA2, (byte) 0x02});
            }
            if (TextUtils.equals(TYPE, order)) {
                Log.i("type", "获取识别码");
                characteristic.setValue(new byte[]{(byte) 0xA4, (byte) 0x01});
            }
            if (TextUtils.equals(UPDATE, order)) {
                Log.i("type", "固件升级");
                characteristic.setValue(new byte[]{(byte) 0xA2, (byte) 0x01});
            }
            mBluetoothLeService.wirteCharacteristic(characteristic);
        } else {
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            Log.i("service", "设置监听");
            if (characteristic.getUuid().toString().equals("f0080004-0451-4000-b000-000000000000") || characteristic.getUuid().toString().equals("f0080002-0451-4000-b000-000000000000")) {
                System.out.println("enable notification");
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }
        }
    }

    /**
     * 固件升级
     *
     * @return
     */
    private void displayUpdateGattServices(List<BluetoothGattService> gattServices, String order) {
        if (gattServices == null)
            return;
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                if (gattCharacteristic.getUuid().toString()
                        .equalsIgnoreCase("f000ffc1-0451-4000-b000-000000000000") ||
                        gattCharacteristic.getUuid().toString().equals("f000ffc2-0451-4000-b000-000000000000")) {
                    sendUpdateData(gattCharacteristic, order);
                }
            }
        }
    }

    private void sendUpdateData(BluetoothGattCharacteristic characteristic, String order) {
        final int charaProp = characteristic.getProperties();
        if (characteristic.getUuid().toString()
                .equalsIgnoreCase("f000ffc1-0451-4000-b000-000000000000")) {
            if (TextUtils.equals(UPDATE_ONE, order)) {
                Log.i("type", "固件升级第一步");
                characteristic.setValue(FirmDateUtil.getHexTime());
            }
            mBluetoothLeService.wirteCharacteristic(characteristic);
        }else if(characteristic.getUuid().toString()
                .equalsIgnoreCase("f000ffc2-0451-4000-b000-000000000000")){
            if (TextUtils.equals(UPDATE_TWO, order)) {
                isUpdate = true;
                Log.i("type", "固件升级第二步");
                fileBuffer = FirmFileUtil.getBytesFromFile(SharePCach.loadStringCach("filePath"));
                if(fileBuffer.length %16  == 0 ){
                    if(i < fileBuffer.length / 16){
                        byte packageL = FirmDateUtil.intToBytes(i)[0];
                        byte packageH = FirmDateUtil.intToBytes(i)[1];
                        byte[] bytes = {packageL, packageH, fileBuffer[i * 16], fileBuffer[i * 16 + 1], fileBuffer[i * 16 + 2], fileBuffer[i * 16 + 3], fileBuffer[i * 16 + 4],
                                fileBuffer[i * 16 + 5], fileBuffer[i * 16 + 6], fileBuffer[i * 16 + 7], fileBuffer[i * 16 + 8], fileBuffer[i * 16 + 9], fileBuffer[i * 16 + 10],
                                fileBuffer[i * 16 + 11], fileBuffer[i * 16 + 12], fileBuffer[i * 16 + 13], fileBuffer[i * 16 + 14], fileBuffer[i * 16 + 15]};
                        UpdateFirmwareActivity.updateFirmwareActivity.progress.setProgress((int) ((i*1.0)/(fileBuffer.length / 16) *44) +56);
                        Log.i("updateData", i + "/" + Arrays.toString(bytes));
                        characteristic.setValue(bytes);
                        Log.e("fileBuffer", fileBuffer.length + "");
                        i++;
                    }else{
                        isUpdate = false;
                        handler.post(runnableUi);
                        return;
                    }
                }else{
                    if(i < fileBuffer.length / 16){
                        byte packageL = FirmDateUtil.intToBytes(i)[0];
                        byte packageH = FirmDateUtil.intToBytes(i)[1];
                        byte[] bytes = {packageL, packageH, fileBuffer[i * 16], fileBuffer[i * 16 + 1], fileBuffer[i * 16 + 2], fileBuffer[i * 16 + 3], fileBuffer[i * 16 + 4],
                                fileBuffer[i * 16 + 5], fileBuffer[i * 16 + 6], fileBuffer[i * 16 + 7], fileBuffer[i * 16 + 8], fileBuffer[i * 16 + 9], fileBuffer[i * 16 + 10],
                                fileBuffer[i * 16 + 11], fileBuffer[i * 16 + 12], fileBuffer[i * 16 + 13], fileBuffer[i * 16 + 14], fileBuffer[i * 16 + 15]};
                        UpdateFirmwareActivity.updateFirmwareActivity.progress.setProgress((int) ((i*1.0)/(fileBuffer.length / 16) *44) +56);
                        Log.i("updateData", i + "/" + Arrays.toString(bytes));
                        characteristic.setValue(bytes);
                        Log.e("fileBuffer", fileBuffer.length + "");
                        i++;
                        lastPackage = false;
                    }
                    if(lastPackage){
                        if(i == fileBuffer.length / 16){
                            byte[] bytes = new byte[fileBuffer.length - (i*16)+2];
                            bytes = FirmUpdateData.subBytes(fileBuffer,i * 16,fileBuffer.length - (i*16));
                            Log.i("updateData", i + "/" + Arrays.toString(bytes));
                            characteristic.setValue(bytes);
                            i++;
                        }else if(i > fileBuffer.length / 16){
                            isUpdate = false;
                            handler.post(runnableUi);
                            return;
                        }
                    }
                }

//                for (int i = 0; i < fileBuffer.length / 16; i++) {
//                    byte packageL = FirmDateUtil.intToBytes(i)[0];
//                    byte packageH = FirmDateUtil.intToBytes(i)[1];
//                    byte[] bytes = {packageL, packageH, fileBuffer[i * 16], fileBuffer[i * 16 + 1], fileBuffer[i * 16 + 2], fileBuffer[i * 16 + 3], fileBuffer[i * 16 + 4],
//                            fileBuffer[i * 16 + 5], fileBuffer[i * 16 + 6], fileBuffer[i * 16 + 7], fileBuffer[i * 16 + 8], fileBuffer[i * 16 + 9], fileBuffer[i * 16 + 10],
//                            fileBuffer[i * 16 + 11], fileBuffer[i * 16 + 12], fileBuffer[i * 16 + 13], fileBuffer[i * 16 + 14], fileBuffer[i * 16 + 15]};
//                    UpdateFirmwareActivity.updateFirmwareActivity.progress.setProgress(i + 56);
//                    Log.i("updateData", i + "/" + Arrays.toString(bytes));
//                    characteristic.setValue(bytes);
//                    Log.e("fileBuffer", fileBuffer.length + "");
//                }
            }
            mBluetoothLeService.wirteCharacteristic(characteristic);
        } else {
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            Log.i("service", "设置监听");
            if (characteristic.getUuid().toString().equals("f000ffc2-0451-4000-b000-000000000000") || characteristic.getUuid().toString().equals("f000ffc1-0451-4000-b000-000000000000")) {
                System.out.println("enable notification");
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }
        }
    }
    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            UpdateFirmwareActivity.updateFirmwareActivity.progress.setProgress(100);
            UpdateFirmwareActivity.updateFirmwareActivity.firm_version.setText("幻响 V" + SharePCach.loadStringCach("version"));
            Toast.makeText(UpdateFirmwareActivity.updateFirmwareActivity, "升级完成", Toast.LENGTH_LONG).show();
        }

    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /**
     * 向服务器发送历史数据
     *
     * @param height
     * @param low
     * @param heart
     */
    private void uploadData(String date, String height, String low, String heart) {

        RequestParams params = new RequestParams();
        params.put("tokenId", Constants.mUserInfo.tokenId);
        params.put("highPressure", height + "");
        params.put("lowPressure", low + "");
        params.put("heartRate", heart + "");
        params.put("date", date + "");
        AsyncHttpClient client = new AsyncHttpClient(this);
        client.post(Constants.SAVE_HISTORY, params,
                new DomainHttpResponseHandler<ResponseData>(
                        ResponseData.class) {
                    @Override
                    public void onStart() {

                        super.onStart();
                    }

                    protected void sendSuccessMessage(int statusCode, Header[] headers,
                                                      String responseBody) {
                        Log.i("历史数据上传请求成功", responseBody);
                    }

                    @Override
                    protected void onDomainSuccess(ResponseData t) {

                    }

                    protected void onFailure(Throwable error, String content) {
                        Log.i("上传历史数据网络错误", "");
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("destory", "destory service");
        unregisterReceiver(mGattUpdateReceiver);
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
            if (mServiceConnection != null) {
                unbindService(mServiceConnection);
            }
        }
        mBluetoothLeService = null;
    }

    // 获取锁
    public void acquireWakeLock(Context context) {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) (context
                    .getSystemService(Context.POWER_SERVICE));
            wakeLock = powerManager.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
            wakeLock.acquire();
        }
    }

    // 释放锁
    public void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    // 再按一次退出程序
    private long exitTime = 0;

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainV2Activity.this, "再按一次退出幻响血压仪", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
