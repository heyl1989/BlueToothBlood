package com.blue.blueapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blue.blueapplication.R;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class AboutActivity extends BaseActivity {


    private ImageView iv_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == iv_back) {
            finish();
        }
    }


}
