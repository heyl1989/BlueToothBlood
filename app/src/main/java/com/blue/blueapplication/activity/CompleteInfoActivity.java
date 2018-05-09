package com.blue.blueapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.blueapplication.FrameApp;
import com.blue.blueapplication.R;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.datetimepicker.DateTimePickDialogUtil;
import com.blue.blueapplication.domain.FileInfo;
import com.blue.blueapplication.domain.SelectItem;
import com.blue.blueapplication.domain.UserInfo;
import com.blue.blueapplication.http.AsyncHttpClient;
import com.blue.blueapplication.http.DomainHttpResponseHandler;
import com.blue.blueapplication.http.RequestParams;
import com.blue.blueapplication.log.D;
import com.blue.blueapplication.utils.DateUtil;
import com.blue.blueapplication.utils.HttpUtil;
import com.blue.blueapplication.utils.JsonTools;
import com.blue.blueapplication.utils.ToastUtil;
import com.blue.blueapplication.utils.UIUtil;
import com.blue.blueapplication.widgets.MMAlert;
import com.blue.blueapplication.widgets.QuizAlert;
import com.blue.blueapplication.widgets.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class CompleteInfoActivity extends BaseActivity {

    protected static final int DOWNSUCCESS = 0;
    protected static final int DOWNFAIL = 1;
    private ImageView iv_back;
    private TextView tv_edit;
    private RoundedImageView iv_account_image;
    private EditText edt_nickname;
    private TextView edt_height;
    private TextView edt_weight;
    private TextView edt_birthday;
    private TextView edt_gender;
    private TextView edt_bmi;
    private boolean isEdit = false;
    private Bitmap bitmap;
    private byte[] bs;

    private ImageView iv_height;
    private ImageView iv_weight;
    private ImageView iv_birthday;
    private ImageView iv_gender;
    private float height;
    private float weight;
    private float bmi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_height = (ImageView) findViewById(R.id.iv_height);
        iv_weight = (ImageView) findViewById(R.id.iv_weight);
        iv_birthday = (ImageView) findViewById(R.id.iv_birthday);
        iv_gender = (ImageView) findViewById(R.id.iv_gender);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        iv_account_image = (RoundedImageView) findViewById(R.id.iv_account_image);
        edt_nickname = (EditText) findViewById(R.id.edt_nickname);
        edt_height = (TextView) findViewById(R.id.edt_height);
        edt_weight = (TextView) findViewById(R.id.edt_weight);
        edt_birthday = (TextView) findViewById(R.id.edt_birthday);
        edt_bmi = (TextView) findViewById(R.id.edt_bmi);
        edt_gender = (TextView) findViewById(R.id.edt_gender);

        if (Constants.mUserInfo != null) {
            ImageLoader.getInstance().displayImage(Constants.mUserInfo.headUrl, iv_account_image);
            edt_nickname.setText(Constants.mUserInfo.nickName);
            edt_height.setText(Constants.mUserInfo.height);
            edt_weight.setText(Constants.mUserInfo.weight);
            edt_birthday.setText(Constants.mUserInfo.birthday);
            edt_bmi.setText("0.0");
            if ("1".equals(Constants.mUserInfo.gender)) {
                edt_gender.setText("女");
            } else {
                edt_gender.setText("男");
            }

            edt_nickname.setEnabled(false);
            edt_height.setEnabled(false);
            edt_weight.setEnabled(false);
            edt_birthday.setEnabled(false);
            edt_gender.setEnabled(false);
            edt_bmi.setEnabled(false);
            weight = Float.parseFloat(Constants.mUserInfo.weight);
            height = Float.parseFloat(Constants.mUserInfo.height)/100;
            bmi = weight/(height*height);
            edt_bmi.setText(String.valueOf(bmi).length()>5?String.valueOf(bmi).substring(0,5):String.valueOf(bmi));


        }

        iv_back.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        iv_account_image.setOnClickListener(this);
        iv_height.setOnClickListener(this);
        iv_weight.setOnClickListener(this);
        iv_birthday.setOnClickListener(this);
        iv_gender.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == iv_back) {
            finish();
        } else if (v == tv_edit) {
            if (isEdit) {
                uploadInfo();
            } else {
                iv_height.setVisibility(View.VISIBLE);
                iv_weight.setVisibility(View.VISIBLE);
                iv_birthday.setVisibility(View.VISIBLE);
                iv_gender.setVisibility(View.VISIBLE);
                edt_nickname.setEnabled(true);
                edt_height.setEnabled(true);
                edt_weight.setEnabled(true);
                edt_birthday.setEnabled(true);
                edt_gender.setEnabled(true);
                tv_edit.setText("完成");
            }
            isEdit = !isEdit;
        } else if (v == iv_account_image) {
            makeCamera();
        }else if (v == iv_birthday) {
            DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                    this, DateUtil.get24Time(System.currentTimeMillis()));
            dateTimePicKDialog.dateTimePicKDialog(edt_birthday);
        }else if (v == iv_height) {
            List<SelectItem> selectItems = new ArrayList<>();
            for (int i = 50;i<261;i++){
                SelectItem selectItem = new SelectItem();
                selectItem.des = i+"cm";
                selectItem.value = String.valueOf(i);
                selectItems.add(selectItem);
            }
            QuizAlert.showAlert(this, selectItems, new QuizAlert.SelectQuiz() {

                @Override
                public void confirmSelect(SelectItem applySelectDomain) {
                    edt_height.setText(applySelectDomain.value);
                    height = Float.parseFloat(applySelectDomain.value)/100;
                    bmi = weight/(height*height);
                    edt_bmi.setText(String.valueOf(bmi).length()>5?String.valueOf(bmi).substring(0,5):String.valueOf(bmi));



                }
            },"选择身高",110);
        }else if (v == iv_weight) {
            List<SelectItem> selectItems = new ArrayList<>();
            for (int i = 20;i<181;i++){
                SelectItem selectItem = new SelectItem();
                selectItem.des = i+"kg";
                selectItem.value = String.valueOf(i);
                selectItems.add(selectItem);
            }
            QuizAlert.showAlert(this, selectItems, new QuizAlert.SelectQuiz() {

                @Override
                public void confirmSelect(SelectItem applySelectDomain) {
                    edt_weight.setText(applySelectDomain.value);
                    weight = Float.parseFloat(applySelectDomain.value);
                    bmi = weight/(height*height);
                    edt_bmi.setText(String.valueOf(bmi).length()>5?String.valueOf(bmi).substring(0,5):String.valueOf(bmi));

                }
            },"选择体重",40);
        }else if (v == iv_gender) {
            List<SelectItem> selectItems = new ArrayList<>();
            for (int i = 0;i<2;i++){
                SelectItem selectItem = new SelectItem();
                if (i==0){
                    selectItem.des = "男";
                    selectItem.value = String.valueOf(i);
                }else {
                    selectItem.des = "女";
                    selectItem.value = String.valueOf(i);
                }

                selectItems.add(selectItem);
            }
            QuizAlert.showAlert(this, selectItems, new QuizAlert.SelectQuiz() {

                @Override
                public void confirmSelect(SelectItem applySelectDomain) {
                    edt_gender.setText(applySelectDomain.value.equals("1")?"女":"男");
                }
            },"选择性别",0);
        }

    }




    private void makeCamera() {
        MMAlert.showAlert(this, "请选择",
                getResources().getStringArray(R.array.select_image), null,
                new MMAlert.OnAlertSelectId() {
                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case 0:
                                takeCamera();
                                break;
                            case 1:
                                selectCamera();
                                break;
                        }
                    }
                });
    }

    private void takeCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 下面这句指定调用相机拍照后的照片存储的路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                    Environment.getExternalStorageDirectory(), "temp.jpg")));
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            UIUtil.showToast("抱歉，不能照相");
        }
    }

    private void selectCamera() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            ToastUtil.showToast("获取图库失败");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        bs = null;

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {// 相册
            if (data != null) {
                startPhotoZoom(data.getData());
            }
        }
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {// 拍照
            File temp = new File(Environment.getExternalStorageDirectory()
                    + "/temp.jpg");
            startPhotoZoom(Uri.fromFile(temp));

        }

        if (requestCode == 3) {
            if (data != null) {
                setPicToView(data);
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100,
                    byteArrayOutputStream);
            bs = byteArrayOutputStream.toByteArray();
            if (bs != null) {
                showProgressDialog("");
                new UploadImageThread().start();
            }
        }
    }


    class UploadImageThread extends Thread {
        @Override
        public void run() {
            Map<String, String> mapA = new HashMap<String, String>();
            mapA.put("tokenId", Constants.mUserInfo.tokenId);
            String result = HttpUtil.formpost(Constants.I_UPLOADFILE, mapA, bs);
            D.i("====上传图片=======" + result);
            if (result != null && !"".equals(result)) {
                Map<String, String> map = JsonTools.toMap(result);
                if (map != null && map.containsKey("status")
                        && "0".equals(map.get("status"))) {
                    FileInfo fileInfo = JsonTools.toSingleBean(result,
                            FileInfo.class);
                    Message message = handler.obtainMessage();
                    message.obj = fileInfo;
                    message.what = DOWNSUCCESS;
                    handler.sendMessage(message);
                } else {
                    handler.sendEmptyMessage(DOWNFAIL);
                }
            } else {
                handler.sendEmptyMessage(DOWNFAIL);
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            dismissProgressDialog();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            switch (msg.what) {
                case DOWNSUCCESS:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    UIUtil.showToast("上传成功");
                    if (fileInfo != null) {
                        ImageLoader.getInstance().displayImage(fileInfo.headImg, iv_account_image);
                        Constants.mUserInfo.headUrl = fileInfo.headImg;
                    }
                    break;
            }
        }

        ;
    };

    private void uploadInfo() {
        final String name = edt_nickname.getText().toString().trim();
        final String height = edt_height.getText().toString().trim();
        final String weight = edt_weight.getText().toString().trim();
        final String birthday = edt_birthday.getText().toString().trim();
        final String gender = edt_gender.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(height)||
                TextUtils.isEmpty(weight) || TextUtils.isEmpty(birthday)||
                TextUtils.isEmpty(gender)  ) {
            ToastUtil.showToast("请完成信息");
            return;
        }

        RequestParams params = new RequestParams();
        params.put("tokenId", Constants.mUserInfo.tokenId);
        params.put("nickName", name);
        params.put("height", height);
        params.put("weight", weight);
        params.put("birthday", birthday);
        params.put("gender", "女".equals(gender) ? "1" : "0");
        AsyncHttpClient client = new AsyncHttpClient(this);
        client.post(Constants.COMPLETE_USER_URL, params,
                new DomainHttpResponseHandler<UserInfo>(
                        UserInfo.class) {
                    @Override
                    public void onStart() {
                        showProgressDialog("");
                        super.onStart();
                    }

                    @Override
                    protected void onDomainSuccess(UserInfo t) {
                        if (Constants.mUserInfo != null) {
                            Constants.mUserInfo.nickName = name;
                            Constants.mUserInfo.height = height;
                            Constants.mUserInfo.weight = weight;
                            Constants.mUserInfo.birthday = birthday;
                            Constants.mUserInfo.gender = "女".equals(gender) ? "1" : "0";
                        }
                        if(TextUtils.equals("1",FrameApp.getIsRegister())){
                            FrameApp.setIsRegister("0");
                            Intent intent = new Intent(CompleteInfoActivity.this,ConnectDevideActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }

                });
    }


}
