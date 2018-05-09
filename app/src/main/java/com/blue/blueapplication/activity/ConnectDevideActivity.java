package com.blue.blueapplication.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.blueapplication.R;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class ConnectDevideActivity extends BaseActivity {


    private TextView searchDevideTv;
    private TextView nothasDeviceTv;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);
        searchDevideTv = (TextView) findViewById(R.id.tv_search_device);
        nothasDeviceTv = (TextView) findViewById(R.id.tv_nothas_device);
        nothasDeviceTv.setOnClickListener(this);
        searchDevideTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == nothasDeviceTv) {
            nothDeviceConnect();
        } else if (v == searchDevideTv) {

            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
               // finish();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Intent intent = new Intent(ConnectDevideActivity.this, CheckDeviceActivity.class);
                startActivity(intent);
               // finish();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(ConnectDevideActivity.this, CheckDeviceActivity.class);
            startActivity(intent);
           // finish();
        }
    }

    private void nothDeviceConnect() {
        Intent intent = new Intent(ConnectDevideActivity.this, MainV2Activity.class);
        startActivity(intent);
       finish();
    }

    // 再按一次退出程序
    private long exitTime = 0;

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showTost("再按一次退出幻响血压仪");
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
