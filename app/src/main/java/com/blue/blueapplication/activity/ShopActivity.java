package com.blue.blueapplication.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blue.blueapplication.R;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class ShopActivity extends BaseActivity {


    private ImageView iv_back;
    private ImageView iv_jd;
    private ImageView iv_tmall;
    private ImageView iv_huanxiang;
    private ImageView iv_weidian;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        iv_back = (ImageView)findViewById(R.id.iv_back);

        iv_jd = (ImageView)findViewById(R.id.iv_jd);
        iv_tmall = (ImageView)findViewById(R.id.iv_tmall);
        iv_huanxiang = (ImageView)findViewById(R.id.iv_huanxiang);
        iv_weidian = (ImageView)findViewById(R.id.iv_weidian);
        iv_back.setOnClickListener(this);
        iv_jd.setOnClickListener(this);
        iv_tmall.setOnClickListener(this);
        iv_huanxiang.setOnClickListener(this);
        iv_weidian.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == iv_back) {
            finish();
        }else if (v == iv_jd){
            startUrl("http://imu.jd.com/");
        }else if (v == iv_tmall){
            startUrl("https://imusm.tmall.com/");
        }else if (v == iv_huanxiang){
            startUrl("http://www.i-mu.com.cn/");
        }else if (v == iv_weidian){
            startUrl("http://2003402.wxfenxiao.com/Shop/index/sid/2003402/pid/5164287.html");
        }
    }

    private void startUrl(String url){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }


}
