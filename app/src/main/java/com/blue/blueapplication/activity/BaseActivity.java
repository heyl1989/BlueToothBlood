package com.blue.blueapplication.activity;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blue.blueapplication.R;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.utils.PicassoFunctions;
import com.squareup.picasso.Picasso;

/**
 * Created by wangxiaojian on 16/4/14.
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener{
    private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.listActivity.add(this);
        // 禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        picasso = PicassoFunctions.getPicasso(this);
    }

    @Override
    public void onClick(View v) {

    }
    private static Dialog dialog;
    private static ProgressBar pb;

    /**
     * 隐藏loading对话框
     */
    public static void dismissProgressDialog() {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    protected void showTost(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public TextView txt;
    public ImageView delete_img;

    public void showProgressDialog(String msg) {
        if (TextUtils.isEmpty(msg)){
            msg = "请稍等...";
        }
        showProgressDialog(msg, true);
    }

    public void showProgressDialog(int resID) {

        showProgressDialog(getString(resID), true);
    }

    /**
     * loading对话框 显示一个半透明的的loading对话框
     *
     * @param message
     *            要显示的文字的资源
     */
    public void showProgressDialog(String message, boolean cancelable) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(this, R.style.processDialog);
        View view = LayoutInflater.from(this).inflate(
                R.layout.loading_dialog_layout, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.addContentView(view, params);
        pb = (ProgressBar) view.findViewById(R.id.loading_dialog_progressBar);
        delete_img = (ImageView) view.findViewById(R.id.delete_img);
        txt = (TextView) view.findViewById(R.id.loading_message);
        txt.setText(message);
        txt.setTextSize(15);
        dialog.setCancelable(cancelable);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.my_dialog);
        delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissProgressDialog();
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.listActivity.remove(this);
    }


    protected final void setCachedImage(ImageView iv, String url, int failedId) {
        if (failedId != 0) {
            picasso.load(url).placeholder(failedId)
                    .error(failedId).into(iv);
        } else {
            picasso.load(url).placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher).into(iv);

        }
    }
}
