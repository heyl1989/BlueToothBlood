package com.blue.blueapplication.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.domain.ResponseData;
import com.blue.blueapplication.http.AsyncHttpClient;
import com.blue.blueapplication.http.DomainHttpResponseHandler;
import com.blue.blueapplication.http.RequestParams;
import com.blue.blueapplication.utils.ToastUtil;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class ForgetPwdActivity extends BaseActivity{


    private EditText phoneEdt;
    private TextView registerTv;
    private ImageView iv_back;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        phoneEdt = (EditText) findViewById(R.id.edt_phone);
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
        if (TextUtils.isEmpty(phone)){
            ToastUtil.showToast("请填写邮箱");
            return;
        }

        RequestParams params = new RequestParams();
        params.put("email",phone);
        AsyncHttpClient client = new AsyncHttpClient(this);
        client.post(Constants.FORGETPWD_URL, params,
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
                        ToastUtil.showToast(t.msg);
                        finish();
                    }

                });
    }
}
