package com.blue.blueapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.utils.ToastUtil;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class VersionActivity extends BaseActivity {


    private ImageView iv_back;
    private TextView tv_update;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        tv_update = (TextView) findViewById(R.id.tv_update);
        iv_back.setOnClickListener(this);
        tv_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == iv_back) {
            finish();
        }else if (v == tv_update){
            ToastUtil.showToast("已经是最新版本了");
        }
    }


}
