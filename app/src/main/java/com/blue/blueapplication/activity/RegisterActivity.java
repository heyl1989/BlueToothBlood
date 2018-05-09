package com.blue.blueapplication.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.blueapplication.FrameApp;
import com.blue.blueapplication.R;
import com.blue.blueapplication.cache.SharePCach;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.domain.ResponseData;
import com.blue.blueapplication.domain.UserInfo;
import com.blue.blueapplication.http.AsyncHttpClient;
import com.blue.blueapplication.http.DomainHttpResponseHandler;
import com.blue.blueapplication.http.RequestParams;
import com.blue.blueapplication.utils.ControlActivity;
import com.blue.blueapplication.utils.ToastUtil;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class RegisterActivity extends BaseActivity{


    private EditText phoneEdt;
    private EditText pwdEdt;
    private TextView registerTv;
    private ImageView iv_back;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        phoneEdt = (EditText) findViewById(R.id.edt_phone);
        pwdEdt = (EditText) findViewById(R.id.edt_pwd);
        registerTv = (TextView) findViewById(R.id.tv_register);
        registerTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == iv_back) {
            finish();
        }else if (v == registerTv) {
            register();
        }
    }


    private void register() {
        final String phone = phoneEdt.getText().toString().trim();
        final String pwd = pwdEdt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(pwd)){
            ToastUtil.showToast("请填写用户名或者密码");
            return;
        }

        RequestParams params = new RequestParams();
        params.put("email",phone);
        params.put("password",pwd);
        AsyncHttpClient client = new AsyncHttpClient(this);
        client.post(Constants.REGISTER_URL, params,
                new DomainHttpResponseHandler<ResponseData>(
                        ResponseData.class) {
                    @Override
                    public void onStart() {
                        showProgressDialog("");
                        super.onStart();
                    }

                    @Override
                    protected void onDomainSuccess(ResponseData t) {
                        dismissProgressDialog();
                        login(phone,pwd);
                        ToastUtil.showToast(t.msg);
                        if(!TextUtils.equals(phone, SharePCach.loadStringCach("name"))){
                            SharePCach.removeShareCach("historyNum");
                        }
                        SharePCach.saveStringCach("name",phone);
                        SharePCach.saveStringCach("pwd",pwd);
                        ControlActivity.closeAllActivity();
                        FrameApp.setIsRegister("1");
                        Intent intent = new Intent(RegisterActivity.this,CompleteInfoActivity.class);
                        startActivity(intent);
                        finish();
                    }

                });
    }

    private void login(String email,String pwd) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", pwd);
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
                    }

                    @Override
                    protected void onFailure(Throwable error, String content) {
                    }

                });
    }
}
