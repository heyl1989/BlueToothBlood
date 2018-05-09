package com.blue.blueapplication.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blue.blueapplication.FrameApp;
import com.blue.blueapplication.R;
import com.blue.blueapplication.activity.CheckDeviceActivity;
import com.blue.blueapplication.activity.ConnectDevideActivity;
import com.blue.blueapplication.activity.HistoryActivity;
import com.blue.blueapplication.activity.LoginActivity;
import com.blue.blueapplication.activity.MainV2Activity;
import com.blue.blueapplication.adapter.BloodPressureAdapter;
import com.blue.blueapplication.cache.SharePCach;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.domain.BloadPressure;
import com.blue.blueapplication.domain.ResponseData;
import com.blue.blueapplication.http.AsyncHttpClient;
import com.blue.blueapplication.http.DomainHttpResponseHandler;
import com.blue.blueapplication.http.RequestParams;
import com.blue.blueapplication.utils.ToastUtil;
import com.blue.blueapplication.widgets.RoundProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class ContentFragment extends BaseFragment {

    private TextView checkState;
    private PopupWindow popWindow;

    public static ContentFragment newInstance(boolean containDevice) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putBoolean("hasdevice", containDevice);
        fragment.setArguments(args);
        return fragment;
    }

    private ImageView menuIv;
    private ImageView historyIv;
    private ListView contentLv;
    private BloodPressureAdapter adapter;
    private ImageView checkTv;
    private MainV2Activity mainActivity;
    private RoundProgressBar roundProgressBar;
    private double spance = 1;
    private boolean hasDevice;
    private boolean isclickCheck;
    public boolean isTest;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainV2Activity) {
            mainActivity = (MainV2Activity) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, null);
        menuIv = (ImageView) view.findViewById(R.id.iv_menu);
        historyIv = (ImageView) view.findViewById(R.id.iv_time);
        contentLv = (ListView) view.findViewById(R.id.lv_main);
        checkTv = (ImageView) view.findViewById(R.id.tv_check);
        checkState = (TextView) view.findViewById(R.id.tv_check_state);
        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        hasDevice = getArguments().getBoolean("hasdevice");
        viewAddLister();
        initData();
        return view;
    }

    /**
     * 点击事件
     */
    android.os.Handler mhandler = new android.os.Handler();

    private void viewAddLister() {

        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.toggle(0);
                }
            }
        });
        //历史数据
        historyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });
        //检测
        checkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasDevice && !isTest) {
                    if(mainActivity.mConnected){
                        isclickCheck = true;
                        isTest = true;
                        //mainActivity.startBlueService();
                        mainActivity.modiFyTime();
                        startProgressContent();
                        checkState.setText("正在检测");
                    }else{
                        FrameApp.setConnetState("0");
                        showPop();
                    }
                }
            }
        });
    }

    /**
     * 开启Progress
     */
    private void startProgressContent() {
        if (currentProgress == 0 || currentProgress > 100) {
            currentProgress = 1;
            spance = 0.5;
            new Thread(runnable).start();
        }
    }

    public double currentProgress = 0;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            roundProgressBar.setProgress((int) currentProgress);
            currentProgress += spance;
            if (currentProgress >= 100) {
                roundProgressBar.setProgress(0);
                checkState.setText("开始检测");
            }
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (currentProgress <= 100) {
                handler.sendEmptyMessage((int) currentProgress);
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private List<BloadPressure> bloadPressures;

    private void initData() {
        if (getActivity() == null) return;
        bloadPressures = BloadPressure.getInitData();
        adapter = new BloodPressureAdapter(getActivity(), bloadPressures);
        contentLv.setAdapter(adapter);
    }


    public void updateData(String string) {
        if (!isclickCheck) return;
        Log.i("检测数据", "" + string);
        String[] datas = string.split("d");
        Log.i("高压", datas[1]);
        Log.i("低压", datas[2]);
        Log.i("心率", datas[3]);
        Log.i("检测次数", datas[4]);
        Log.i("检测数据长度", datas.length + "");
        if (datas != null && datas.length == 5 && "10".equals(datas[4].trim())) {
            spance = 10;
            for (BloadPressure bloadPressure : bloadPressures) {
                if (bloadPressure.name.equals("高压")) {
                    bloadPressure.result = datas[1].trim();
                } else if (bloadPressure.name.equals("低压")) {
                    bloadPressure.result = datas[2].trim();
                } else if (bloadPressure.name.equals("心率")) {
                    bloadPressure.result = datas[3].trim();
                }
            }
            uploadInfo(datas[1], datas[2], datas[3]);
            adapter.notifyDataSetChanged();
            isTest = false;
            if (mainActivity != null)
                mainActivity.closeBlueDevice();
        } else if (datas != null && datas.length == 5) {
            String mPro = datas[4].trim().replace("0", "");
            int progress = 0;
            if (!TextUtils.isEmpty(mPro)) {
                progress = Integer.parseInt(mPro) * 10;
            }
            if (progress > currentProgress) {
                currentProgress = progress;
                roundProgressBar.setProgress((int) currentProgress);
            }

        }
//        else {
//            spance = 1;
//            startProgressContent();
//        }
    }

    @Override
    public void onDestroy() {
        currentProgress = 101;
        super.onDestroy();
    }


    private void uploadInfo(String height, String low, String heart) {

        RequestParams params = new RequestParams();
        params.put("tokenId", Constants.mUserInfo.tokenId);
        params.put("highPressure", height);
        params.put("lowPressure", low);
        params.put("heartRate", heart);
        AsyncHttpClient client = new AsyncHttpClient(getActivity());
        client.post(Constants.I_SAVEPRESS, params,
                new DomainHttpResponseHandler<ResponseData>(
                        ResponseData.class) {
                    @Override
                    public void onStart() {

                        super.onStart();
                    }

                    @Override
                    protected void onDomainSuccess(ResponseData t) {

                    }

                });
    }


    protected void showPop() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.pop_mainv2, null);
        TextView connetState = (TextView) view.findViewById(R.id.tv_pop_reconnet);
        connetState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
                Intent intent = new Intent(mActivity, CheckDeviceActivity.class);
                startActivity(intent);
            }
        });
        if (popWindow == null) {
            popWindow = new PopupWindow(view,
                    WindowManager.LayoutParams.FILL_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, false);
        }
        popWindow.setFocusable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.showAtLocation(roundProgressBar, Gravity.BOTTOM, 0, 0);

    }


}
