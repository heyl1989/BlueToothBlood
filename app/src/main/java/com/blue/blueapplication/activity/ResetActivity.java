package com.blue.blueapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.cache.SharePCach;
import com.blue.blueapplication.utils.ControlActivity;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class ResetActivity extends BaseActivity {


    private ImageView iv_back;
    private TextView tv_reset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        tv_reset = (TextView) findViewById(R.id.tv_reset);
        iv_back.setOnClickListener(this);
        tv_reset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == iv_back) {
            finish();
        }else if (v==tv_reset){
            ControlActivity.closeAllActivity();
            Intent intent = new Intent(this,LoginActivity.class);
            SharePCach.removeShareCach("name");
            SharePCach.removeShareCach("pwd");
            SharePCach.removeShareCach("deviceAddress");
            SharePCach.removeShareCach("deviceName");
            startActivity(intent);
            finish();

        }
    }


}
