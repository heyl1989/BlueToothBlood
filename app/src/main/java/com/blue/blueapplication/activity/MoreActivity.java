package com.blue.blueapplication.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blue.blueapplication.FrameApp;
import com.blue.blueapplication.R;
import com.blue.blueapplication.cache.SharePCach;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.utils.ControlActivity;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class MoreActivity extends BaseActivity {


    private ImageView iv_back;
    private RelativeLayout rl_about;
    private RelativeLayout rl_shop;
    private RelativeLayout rl_version;
    private RelativeLayout rl_reset;
    private RelativeLayout rl_update_pwd;
    private RelativeLayout rl_cancel_bind;
    private RelativeLayout rl_connect;
    private RelativeLayout rl_logout;
    private TextView connetstate;
    private RelativeLayout rl_update_firmware;
    private ImageView ringrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        rl_shop = (RelativeLayout) findViewById(R.id.rl_shop);
        rl_version = (RelativeLayout) findViewById(R.id.rl_version);
        rl_reset = (RelativeLayout) findViewById(R.id.rl_reset);
        rl_update_pwd = (RelativeLayout) findViewById(R.id.rl_update_pwd);
        rl_update_firmware = (RelativeLayout) findViewById(R.id.rl_update_firmware);
        rl_cancel_bind = (RelativeLayout) findViewById(R.id.rl_cancel_bind);
        rl_logout = (RelativeLayout) findViewById(R.id.rl_logout);
        rl_connect = (RelativeLayout) findViewById(R.id.rl_connect);
        connetstate = (TextView) findViewById(R.id.tv_connetstate);
        ringrow = (ImageView) findViewById(R.id.img_ringrow);
        Log.e("ConnetState",FrameApp.getConnetState()+"");

        iv_back.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_shop.setOnClickListener(this);
        rl_version.setOnClickListener(this);
        rl_reset.setOnClickListener(this);
        rl_update_pwd.setOnClickListener(this);
        rl_update_firmware.setOnClickListener(this);
        rl_cancel_bind.setOnClickListener(this);
        rl_logout.setOnClickListener(this);
        rl_connect.setOnClickListener(this);


    }
    @Override
    protected void onResume() {
        super.onResume();
        if (MainV2Activity.mainV2Activity.mConnected) {
            connetstate.setText("已连接");
            ringrow.setVisibility(View.INVISIBLE);
            rl_connect.setEnabled(false);
        } else {
            connetstate.setText("未连接");
            ringrow.setVisibility(View.VISIBLE);
            rl_connect.setEnabled(true);
        }
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == iv_back) {
            finish();
        } else if (v == rl_about) {

            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (v == rl_shop) {
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
        } else if (v == rl_version) {
            Intent intent = new Intent(this, VersionActivity.class);
            startActivity(intent);
        } else if (v == rl_reset) {
            Intent intent = new Intent(this, ResetActivity.class);
            startActivity(intent);
        } else if (v == rl_update_pwd) {
            Intent intent = new Intent(this, UpdatePwdActivity.class);
            startActivity(intent);
        } else if(v == rl_update_firmware){
            Intent intent = new Intent(this, UpdateFirmwareActivity.class);
            startActivity(intent);
        }else if (v == rl_cancel_bind) {
            cancelBind();
        } else if (v == rl_logout) {
            logout();
        } else if (v == rl_connect) {
            Intent intent = new Intent(this, CheckDeviceActivity.class);
            startActivity(intent);
        }

    }

    AlertDialog dialog = null;

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定退出登录?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ControlActivity.closeAllActivity();
                SharePCach.saveStringCach("name", "");
                if (Constants.mUserInfo != null) {
                    Constants.mUserInfo.nickName = "";
                    Constants.mUserInfo.height = "";
                    Constants.mUserInfo.weight = "";
                    Constants.mUserInfo.birthday = "";
                    Constants.mUserInfo.gender = "";
                }
                Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();

    }

    private void cancelBind() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定退出绑定?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharePCach.removeShareCach("deviceAddress");
                SharePCach.removeShareCach("deviceName");
                ControlActivity.closeAllActivity();
                Intent intent = new Intent(MoreActivity.this, ConnectDevideActivity.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();

    }


}
