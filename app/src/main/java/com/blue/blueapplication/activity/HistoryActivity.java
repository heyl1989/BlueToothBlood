package com.blue.blueapplication.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.blueapplication.R;
import com.blue.blueapplication.adapter.HistoryAdapter;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.domain.HistoryPressure;
import com.blue.blueapplication.domain.HistoryPressureResult;
import com.blue.blueapplication.http.AsyncHttpClient;
import com.blue.blueapplication.http.DomainHttpResponseHandler;
import com.blue.blueapplication.http.RequestParams;
import com.blue.blueapplication.utils.CommonUtils;
import com.blue.blueapplication.utils.DateUtil;
import com.blue.blueapplication.widgets.ChartView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.editorpage.ShareActivity;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class HistoryActivity extends BaseActivity {


    private HistoryAdapter adapter;
    private ImageView backIv;
    private ImageView shareIv;
    private TextView dayReportTv;
    private TextView monthReportTv;
    private TextView yearReportTv;
    private ImageView leftIv;
    private ImageView rightTv;
    private TextView timeTv;
    private ListView mainLv;
    private ChartView cv_main;
    private int state = 0;
    private int dayCount;
    private int monthCount;
    private int yearCount;
    private LinearLayout ll_chart;
    private HorizontalScrollView hsv_chart;

    private List<HistoryPressure> pressures = new ArrayList<>();
    private View view_history;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        view_history = findViewById(R.id.view_history);
        backIv = (ImageView) findViewById(R.id.iv_back);
        ll_chart = (LinearLayout) findViewById(R.id.ll_chart);
        hsv_chart = (HorizontalScrollView) findViewById(R.id.hsv_chart);
        cv_main = (ChartView) findViewById(R.id.cv_main);
        shareIv = (ImageView) findViewById(R.id.iv_share);
        dayReportTv = (TextView) findViewById(R.id.tv_day_report);
        monthReportTv = (TextView) findViewById(R.id.tv_month_report);
        yearReportTv = (TextView) findViewById(R.id.tv_year_report);
        leftIv = (ImageView) findViewById(R.id.iv_left);
        rightTv = (ImageView) findViewById(R.id.iv_right);
        timeTv = (TextView) findViewById(R.id.tv_time);
        mainLv = (ListView) findViewById(R.id.lv_main);
//        mainLv.addFooterView(getLayoutInflater().inflate(R.layout.remind_footer,null));
        backIv.setOnClickListener(this);
        shareIv.setOnClickListener(this);
        leftIv.setOnClickListener(this);
        rightTv.setOnClickListener(this);
        dayReportTv.setOnClickListener(this);
        monthReportTv.setOnClickListener(this);
        yearReportTv.setOnClickListener(this);
        getDayReport();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == backIv) {
            finish();
        } else if (v == shareIv) {
            share();
        } else if (v == leftIv) {
            if (state == 0) {
                dayCount--;
                getDayReport();

            } else if (state == 1) {
                monthCount--;
                getMonthReport();

            } else if (state == 2) {
                yearCount--;
                getYearReport();

            }

        } else if (v == rightTv) {
            if (state == 0) {
                dayCount++;
                getDayReport();

            } else if (state == 1) {
                monthCount++;
                getMonthReport();

            } else if (state == 2) {
                yearCount++;
                getYearReport();

            }
        } else if (v == dayReportTv) {
            state = 0;
            dayReportTv.setEnabled(false);
            monthReportTv.setEnabled(true);
            yearReportTv.setEnabled(true);
            getDayReport();
        } else if (v == monthReportTv) {
            state = 1;
            dayReportTv.setEnabled(true);
            monthReportTv.setEnabled(false);
            yearReportTv.setEnabled(true);
            getMonthReport();
        } else if (v == yearReportTv) {
            state = 2;
            dayReportTv.setEnabled(true);
            monthReportTv.setEnabled(true);
            yearReportTv.setEnabled(false);
            getYearReport();
        }
    }

    /**
     * 分享
     */
    private void share() {
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE
                };
        Bitmap bitmap = CommonUtils.zoomImg(CommonUtils.getViewBitmap(view_history));

        UMImage image = new UMImage(HistoryActivity.this,bitmap);
        new ShareAction(this).setDisplayList(displaylist)
//                .withText("体积小巧，方便携带；测试方式简单，适用人群广泛；数字显示方式易于查看；依据脉搏波医学原理，基于光电传感器，运用专属的算法来实现血压和心率测量。")
//                .withTitle("")
//                .withTargetUrl("http://www.i-mu.com.cn/")
                .withMedia(image)
                .setListenerList(umShareListener)
                .open();
    }

    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            showTost(" 分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showTost(" 分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showTost(" 分享取消了");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void showReportData(List<HistoryPressure> paramPressures, int state) {

        if (paramPressures != null) {
            pressures.clear();
//            HistoryPressure pressure = new HistoryPressure();
//            pressure.createTime = "001";
//            pressures.add(pressure);
            pressures.addAll(paramPressures);
            if (adapter == null) {
                adapter = new HistoryAdapter(this, pressures);
                adapter.setState(state);
                mainLv.setAdapter(adapter);
            } else {
                adapter.setState(state);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showReportDataSize(List<HistoryPressure> paramPressures, int state) {
        String[] stringsX = null;
        String[] stringsData = null;
        String[] height = null;      //数据
        String[] low = null;      //数据
        String[] heart = null;      //数据

        if (paramPressures != null) {
            if (state == 0) {
                stringsX = new String[25];
                height = new String[25];
                low = new String[25];
                heart = new String[25];
                for (int i = 0; i < 25; i++) {
                    stringsX[i] = String.valueOf(i) + "时";
                    HistoryPressure pressure = checkTimeIsExit(paramPressures, i);
                    if (pressure != null) {
                        heart[i] = pressure.heartRate;
                        height[i] = pressure.highPressure;
                        low[i] = pressure.lowPressure;
                    } else {
                        heart[i] = "0";
                        height[i] = "0";
                        low[i] = "0";
                    }
                }
            } else if (state == 1) {
                stringsX = new String[32];
                height = new String[32];
                low = new String[32];
                heart = new String[32];
                for (int i = 0; i < 32; i++) {
                    stringsX[i] = String.valueOf(i) + "号";
                    ;
                    HistoryPressure pressure = checkIsExit(paramPressures, i);
                    if (pressure != null) {
                        heart[i] = pressure.heartRate;
                        height[i] = pressure.highPressure;
                        low[i] = pressure.lowPressure;
                    } else {
                        heart[i] = "0";
                        height[i] = "0";
                        low[i] = "0";
                    }
                }
            } else if (state == 2) {
                stringsX = new String[13];
                height = new String[13];
                low = new String[13];
                heart = new String[13];
                for (int i = 0; i < 13; i++) {
                    stringsX[i] = String.valueOf(i) + "月";
                    ;
                    HistoryPressure pressure = checkMonthIsExit(paramPressures, i);
                    if (pressure != null) {
                        heart[i] = pressure.heartRate;
                        height[i] = pressure.highPressure;
                        low[i] = pressure.lowPressure;
                    } else {
                        heart[i] = "0";
                        height[i] = "0";
                        low[i] = "0";
                    }
                }
            }
            cv_main.SetInfo(
                    stringsX,   //X轴刻度
                    new String[]{"", "30", "60", "90", "120", "150", "180"},   //Y轴刻度
                    height,
                    low,
                    heart,  //数据
                    ""
            );
            hsv_chart.scrollTo(0, 0);
        }
    }

    private HistoryPressure checkIsExit(List<HistoryPressure> paramPressures, int index) {

        HistoryPressure result = null;
        for (Iterator<HistoryPressure> iterator = paramPressures.iterator(); iterator.hasNext(); ) {
            HistoryPressure pressure = iterator.next();
            String[] strings = pressure.createTime.split("-");
            if (strings.length > 2) {
                if (String.valueOf(index).equals(strings[2]) || ("0" + index).equals(strings[2])) {
                    result = pressure;
                    break;
                }
            }
        }
        return result;

    }

    private HistoryPressure checkMonthIsExit(List<HistoryPressure> paramPressures, int index) {

        HistoryPressure result = null;
        for (Iterator<HistoryPressure> iterator = paramPressures.iterator(); iterator.hasNext(); ) {
            HistoryPressure pressure = iterator.next();
            String[] strings = pressure.createTime.split("-");
            if (strings.length > 1) {
                if (String.valueOf(index).equals(strings[1]) || ("0" + index).equals(strings[1])) {
                    result = pressure;
                    break;
                }
            }
        }
        return result;
    }

    private HistoryPressure checkTimeIsExit(List<HistoryPressure> paramPressures, int index) {

        HistoryPressure result = null;
        for (Iterator<HistoryPressure> iterator = paramPressures.iterator(); iterator.hasNext(); ) {
            HistoryPressure pressure = iterator.next();
            String[] strings = pressure.createTime.split("-");
            if (strings.length > 2) {
                String[] strings1 = strings[2].split(" ");
                if (strings1.length > 1) {
                    String[] strings2 = strings1[1].split(":");
                    if (strings2 != null && strings2.length > 0) {
                        if (String.valueOf(index).equals(strings2[0]) || ("0" + index).equals(strings2[0])) {
                            result = pressure;
                            break;
                        }
                    }

                }
            }
        }
        return result;
    }


    private void getDayReport() {
        RequestParams params = new RequestParams();
        params.put("tokenId", Constants.mUserInfo.tokenId);
        params.put("date", DateUtil.getNextNDay(dayCount));
        timeTv.setText(DateUtil.getNextNDay(dayCount));
        AsyncHttpClient client = new AsyncHttpClient(this);
        client.post(Constants.DAY_REPORT_URL, params,
                new DomainHttpResponseHandler<HistoryPressureResult>(
                        HistoryPressureResult.class) {
                    @Override
                    public void onStart() {
                        showProgressDialog("");
                        super.onStart();
                    }

                    @Override
                    protected void onDomainSuccess(HistoryPressureResult t) {
                        dismissProgressDialog();
                        showReportData(t.result, 0);
                        showReportDataSize(t.result, 0);


                    }

                });
    }


    private void getMonthReport() {
        RequestParams params = new RequestParams();
        params.put("tokenId", Constants.mUserInfo.tokenId);
        params.put("month", DateUtil.getNextNMonth(monthCount));
        timeTv.setText(DateUtil.getNextNMonth(monthCount));
        AsyncHttpClient client = new AsyncHttpClient(this);
        client.post(Constants.MONTH_REPORT_URL, params,
                new DomainHttpResponseHandler<HistoryPressureResult>(
                        HistoryPressureResult.class) {
                    @Override
                    public void onStart() {
                        showProgressDialog("");
                        super.onStart();
                    }

                    @Override
                    protected void onDomainSuccess(HistoryPressureResult t) {
                        dismissProgressDialog();
                        showReportData(t.result, 1);
                        showReportDataSize(t.result, 1);
                    }

                });
    }

    private void getYearReport() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int a = calendar.get(Calendar.YEAR) + yearCount;
        RequestParams params = new RequestParams();
        params.put("tokenId", Constants.mUserInfo.tokenId);
        params.put("year", String.valueOf(a));
        timeTv.setText(String.valueOf(a));
        AsyncHttpClient client = new AsyncHttpClient(this);
        client.post(Constants.YEAR_REPORT_URL, params,
                new DomainHttpResponseHandler<HistoryPressureResult>(
                        HistoryPressureResult.class) {
                    @Override
                    public void onStart() {
                        showProgressDialog("");
                        super.onStart();
                    }

                    @Override
                    protected void onDomainSuccess(HistoryPressureResult t) {
                        dismissProgressDialog();
                        showReportData(t.result, 2);
                        showReportDataSize(t.result, 2);
                    }
                });
    }


}
