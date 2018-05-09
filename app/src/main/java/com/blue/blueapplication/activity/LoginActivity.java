package com.blue.blueapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.cache.SharePCach;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.domain.UserInfo;
import com.blue.blueapplication.http.AsyncHttpClient;
import com.blue.blueapplication.http.DomainHttpResponseHandler;
import com.blue.blueapplication.http.RequestParams;
import com.blue.blueapplication.utils.ToastUtil;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class LoginActivity extends BaseActivity {


    private EditText phoneEdt;
    private EditText pwdEdt;
    private TextView loginBtn;
    private CheckBox savePwdCbx;
    private TextView forgetPwdTv;
    private TextView registerTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneEdt = (EditText) findViewById(R.id.edt_phone);
        pwdEdt = (EditText) findViewById(R.id.edt_pwd);
        loginBtn = (TextView) findViewById(R.id.tv_login);
        savePwdCbx = (CheckBox) findViewById(R.id.cb_save_pwd);
        forgetPwdTv = (TextView) findViewById(R.id.tv_forget_pwd);
        registerTv = (TextView) findViewById(R.id.tv_register);
        loginBtn.setOnClickListener(this);
        forgetPwdTv.setOnClickListener(this);
        registerTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == loginBtn) {
            login();
        } else if (v == forgetPwdTv) {
            forgetPwd();
        } else if (v == registerTv) {
            register();
        }
    }

    private void login() {
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
        client.post(Constants.LOGIN_URL, params,
                new DomainHttpResponseHandler<UserInfo>(
                        UserInfo.class) {
                    @Override
                    public void onStart() {
                        showProgressDialog("");
                        super.onStart();
                    }

                    @Override
                    protected void onDomainSuccess(UserInfo t) {
                        Constants.mUserInfo = t;
                        if ("1".equals(t.userStatus)){
                            if(!TextUtils.equals(phone,SharePCach.loadStringCach("name"))){
                                SharePCach.removeShareCach("historyNum");
                            }
                            SharePCach.saveStringCach("name",phone);
                            SharePCach.saveStringCach("pwd",pwd);
                            Intent intent = new Intent(LoginActivity.this,ConnectDevideActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Intent intent = new Intent(LoginActivity.this,CompleteInfoActivity.class);
                            startActivity(intent);
                        }
                    }


                });
    }

    private void forgetPwd() {
        Intent intent = new Intent(this, ForgetPwdActivity.class);
        startActivity(intent);
    }

    private void register() {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
