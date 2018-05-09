package com.blue.blueapplication.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.blueapplication.FrameApp;
import com.blue.blueapplication.R;
import com.blue.blueapplication.utils.Bluetooth;
import com.blue.blueapplication.utils.ControlActivity;
import com.blue.blueapplication.widgets.RoundProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class CheckDeviceActivity extends BaseActivity {


    private TextView tv_check;

    private BluetoothAdapter bluetoothAdapter;
    private List<String> devices;
    private List<BluetoothDevice> deviceList;
    private Bluetooth client;
    private final String lockName = "imu-xueyayi";
    private String message = "000001";
    private ListView listView;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // 10���ֹͣ��������.
    private static final long SCAN_PERIOD = 10000;
    private RoundProgressBar roundProgressBar;
    private PopupWindow popWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_device);

        listView = (ListView) findViewById(R.id.lv_main);
        roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        roundProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentProgress >= 100){
                    currentProgress = 0;
                    new Thread(runnable).start();
                    scanLeDevice(true);
                }
            }
        });

        mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;
                showPop(1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popWindow.dismiss();
                        ControlActivity.closeAllActivity();
                        FrameApp.setConnetState("1");
                        final Intent intent = new Intent(CheckDeviceActivity.this, MainV2Activity.class);
                        intent.putExtra("name", device.getName());
                        intent.putExtra("address", device.getAddress());
                        if (mScanning) {
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            mScanning = false;
                        }
                        startActivity(intent);
                        finish();
                    }
                },500);
            }
        });

        if (currentProgress==1||currentProgress>100){
            currentProgress = 1;
            new Thread(runnable).start();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listView.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);


    }

    private int currentProgress = 1;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            roundProgressBar.setProgress(currentProgress);
            currentProgress++;
            if(currentProgress >= 100){
                roundProgressBar.setProgress(0);
                if(mLeDeviceListAdapter.getCount() == 0){
                    //showPop(0);
                }
            }
        }
    };


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (currentProgress<=100){
                handler.sendEmptyMessage(currentProgress);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        currentProgress=100;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 搜索设备
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }


    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = CheckDeviceActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }


    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }


    protected void showPop(int state) {
        View view = getLayoutInflater().inflate(R.layout.pop_main_activity,
                null);
        LinearLayout noDevice = (LinearLayout)view.findViewById(R.id.ll_no_device);
        TextView connetState = (TextView) view.findViewById(R.id.tv_pop_connetstate);
        if(state == 1){
            noDevice.setVisibility(View.GONE);
        }
        if(state == 0){
            connetState.setText("重新搜索设备");
            connetState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
                }
            });
        }
        popWindow = new PopupWindow(view,
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, false);
        popWindow.setFocusable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.showAtLocation(findViewById(R.id.roundProgressBar), Gravity.CENTER, 0, 0);
        WindowManager.LayoutParams layoutParams = CheckDeviceActivity.this.getWindow().getAttributes();
        layoutParams.alpha = 0.5f; //0.0-1.0
        CheckDeviceActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CheckDeviceActivity.this.getWindow().setAttributes(layoutParams);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = CheckDeviceActivity.this.getWindow().getAttributes();
                layoutParams.alpha = 1f; //0.0-1.0
                CheckDeviceActivity.this.getWindow().setAttributes(layoutParams);
            }
        });
    }

}
