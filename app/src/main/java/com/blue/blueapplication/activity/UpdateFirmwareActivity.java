package com.blue.blueapplication.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.cache.SharePCach;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.domain.UpdateInfo;
import com.blue.blueapplication.http.AsyncHttpClient;
import com.blue.blueapplication.http.DomainHttpResponseHandler;
import com.blue.blueapplication.utils.FileUtils;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by heyl on 2016/8/8.
 */
public class UpdateFirmwareActivity extends BaseActivity {

    private ImageView iv_back;
    public ProgressBar progress;
    public TextView firm_version, update;
    public static UpdateFirmwareActivity updateFirmwareActivity;
    private UpdateInfo updateData;
    private String URL;
    private String newVersion;
    public static final String OAD_PROGRESS = "oad_progress";
    private TextView mOadProgreess;
    private int count;
    private TextView tv_isnew;
    //是否有最新版本
    private boolean isCanUpgrade = false;
    //是否正在升级
    private boolean isUpgrading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_firmware);
        updateFirmwareActivity = this;
        MainV2Activity.mainV2Activity.getVersion();
        getUpdateData();
        init();
    }

    private IntentFilter getFilter() {
        //
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(OAD_PROGRESS);
        return mIntentFilter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isUpgrading) {
            mOadProgreess.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        //返回按钮
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        //
        progress = (ProgressBar) findViewById(R.id.pb_progress);
        //进度条上的百分比
        mOadProgreess = (TextView) findViewById(R.id.tv_oad_progress);
        mOadProgreess.setVisibility(View.GONE);

        firm_version = (TextView) findViewById(R.id.tv_firm_version);
        tv_isnew = (TextView) findViewById(R.id.tv_isnew);
        tv_isnew.setVisibility(View.GONE);
        if (!MainV2Activity.mainV2Activity.mConnected) {
            firm_version.setText("设备未连接");
        } else {
            firm_version.setText(SharePCach.loadStringCach("version") + "");
        }
        update = (TextView) findViewById(R.id.tv_update);
        update.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (isUpgrading) {
                    showConfirm();
                } else {
                    finish();
                }
                break;
            case R.id.tv_update:
                if (!MainV2Activity.mainV2Activity.mConnected) {
                    showTost("设备未连接");
                    return;
                }
                if (isCanUpgrade) {
                    LocalBroadcastManager.getInstance(this).registerReceiver(OADProgress, getFilter());
                    downApk(URL, UpdateFirmwareActivity.this);
                    //正在升级
                    isUpgrading = true;
                    //升级过程中不让点击
                    update.setEnabled(false);
                    //显示进度条上的百分比
                    mOadProgreess.setVisibility(View.VISIBLE);
                } else {
                    showTost("目前已是最新版本，无需升级！");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isUpgrading) {
            showConfirm();
        } else {
            finish();
        }
        //super.onBackPressed();
    }

    /**
     * 返回确认弹框
     */
    AlertDialog dialog = null;

    private void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确认退出升级？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
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


    /**
     * 获取升级信息
     */
    private void getUpdateData() {
        if (TextUtils.isEmpty(SharePCach.loadStringCach("version"))) {
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient(this);
        String url = Constants.UPDATE + "?DeviceType=" + SharePCach.loadStringCach("type") + "&DeviceVersion=" + SharePCach.loadStringCach("version");
        client.get(url,
                new DomainHttpResponseHandler<UpdateInfo>(UpdateInfo.class) {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    protected void onSuccess(UpdateInfo updateInfo) {
                    }

                    @Override
                    protected void sendSuccessMessage(int statusCode, Header[] headers, String responseBody) {
                        Log.e("返回数据", responseBody);
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            URL = jsonObject.getString("Url");
                            newVersion = jsonObject.getString("Version");
                            if (TextUtils.equals(newVersion, SharePCach.loadStringCach("version"))) {
                                //已经是最新版本
                                Message msg = m_handler.obtainMessage();
                                msg.arg1 = 1;
                                m_handler.sendMessage(msg);
                            } else {
                                //需要升级
                                Message msg = m_handler.obtainMessage();
                                msg.arg1 = 0;
                                m_handler.sendMessage(msg);
                            }
                            SharePCach.saveStringCach("version", newVersion);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (null == URL) {
                            //已经是最新版本
                            Message msg = m_handler.obtainMessage();
                            msg.arg1 = 1;
                            m_handler.sendMessage(msg);
                        }

                    }

                    @Override
                    protected void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        showTost("网络错误");
                    }
                });
    }

    final Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                //需要升级
                //执行AsyncHttpClient的get或post函数
                tv_isnew.setVisibility(View.VISIBLE);
                isCanUpgrade = true;
            }
            if (msg.arg1 == 1) {
                tv_isnew.setVisibility(View.GONE);
                isCanUpgrade = false;
            }
        }
    };

    // 下载固件
    private void downApk(final String downloadUrl, final Context context) {
        Log.e("URL:", "" + downloadUrl);
        // 指定文件类型
        String[] allowedContentTypes = new String[]{".*"};
        // 获取二进制数据如图片和其他文件
        com.loopj.android.http.AsyncHttpClient client = new com.loopj.android.http.AsyncHttpClient();
        client.setTimeout(30000);
        client.get(downloadUrl, new BinaryHttpResponseHandler(allowedContentTypes) {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] binaryData) {

                // 文件夹地址
                String tempPath = "Download";
                // 文件地址
                String filePath = tempPath + "/" + "bluetooth" + ".bin";
                // 下载成功后需要做的工作
                Log.e("binaryData:", "共下载了：" + binaryData.length);

                FileUtils fileutils = new FileUtils(context);

                // 判断sd卡上的文件夹是否存在
                if (!fileutils.isFileExist(tempPath)) {
                    fileutils.createSDDir(tempPath);
                }

                // 删除已下载的apk
                if (fileutils.isFileExist(filePath)) {
                    fileutils.deleteFile(filePath);
                }

                InputStream inputstream = new ByteArrayInputStream(binaryData);
                if (inputstream != null) {
                    fileutils.write2SDFromInput(binaryData.length, filePath, inputstream);
                    try {
                        inputstream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // MainV2Activity.mainV2Activity.firmUpdate();
                LocalBroadcastManager.getInstance(UpdateFirmwareActivity.this).sendBroadcast(new Intent(BluetoothLeService.OAD_SERVER));
                SharePCach.saveStringCach("filePath", Environment.getExternalStorageDirectory() + "/" + filePath + "");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] binaryData, Throwable error) {
                //Log.i("http", error.getMessage());
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                // TODO Auto-generated method stub
                super.onProgress(bytesWritten, totalSize);
                count = (int) ((bytesWritten * 1.0 / totalSize) * 50) + 6;

                // 下载进度显示
                progress.setProgress(count);
                mOadProgreess.setText(count + "%");

                //Log.e("下载 Progress>>>>>", bytesWritten + " / " + totalSize);
            }

            @Override
            public void onRetry(int retryNo) {
                // TODO Auto-generated method stub
                super.onRetry(retryNo);
                // 返回重试次数
            }

        });
    }

    /**
     * 收广播
     */
    private final BroadcastReceiver OADProgress = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action == OAD_PROGRESS) {
                String str = intent.getStringExtra("progress");
                int oldPackage = intent.getIntExtra("oldPackage", 0);
                int allPackage = intent.getIntExtra("allPackage", 0);
                //mOadProgreess.setText(str);
                count = (int) (1.0 * oldPackage / allPackage * 44) + 57;
                progress.setProgress(count);
                mOadProgreess.setText(count + "%");
                //Log.e("☆", oldPackage+"/"+allPackage);
                if (oldPackage >= allPackage) {
                    return;
                }
                if (count == 100) {
                    firm_version.setText("幻响 V" + SharePCach.loadStringCach("version"));
                    //升级成功改变是否有新版本状态
                    isCanUpgrade = false;
                    isUpgrading = false;
                    //按钮可点击
                    update.setEnabled(true);
                    //隐藏new
                    tv_isnew.setVisibility(View.GONE);
                    //进度恢复初始化
                    count = 0;
                    progress.setProgress(count);
                    mOadProgreess.setVisibility(View.GONE);
                    showTost("升级成功,请重新连接蓝牙");
                    LocalBroadcastManager.getInstance(UpdateFirmwareActivity.this).unregisterReceiver(OADProgress);
                } else {
                    return;
                }
            }

        }

    };


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(OADProgress);
    }

}
