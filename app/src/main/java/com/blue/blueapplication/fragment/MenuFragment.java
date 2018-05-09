package com.blue.blueapplication.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.activity.CompleteInfoActivity;
import com.blue.blueapplication.activity.MainV2Activity;
import com.blue.blueapplication.activity.MoreActivity;
import com.blue.blueapplication.config.Constants;
import com.blue.blueapplication.widgets.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class MenuFragment extends BaseFragment{


    private RoundedImageView accountIconIv;
    private TextView accountNameTv;
    private LinearLayout mainPageLl;
    private LinearLayout aboutPageLl;

    private MainV2Activity mainActivity;




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainV2Activity){
            mainActivity = (MainV2Activity) activity  ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        accountIconIv = (RoundedImageView) view.findViewById(R.id.iv_account_image);
        accountNameTv = (TextView) view.findViewById(R.id.tv_account_name);
        mainPageLl = (LinearLayout) view.findViewById(R.id.ll_main_page);
        aboutPageLl = (LinearLayout)view.findViewById(R.id.ll_about_page);
        viewAddLister();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.mUserInfo!=null){
            accountNameTv.setText(Constants.mUserInfo.nickName);
            ImageLoader.getInstance().displayImage(Constants.mUserInfo.headUrl, accountIconIv);
        }
    }

    private void viewAddLister(){
        accountIconIv.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CompleteInfoActivity.class);
                startActivity(intent);

            }
        });
        mainPageLl.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (mainActivity!=null){
                    mainActivity.toggle(1);
                }
            }
        });
        aboutPageLl.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MoreActivity.class);
                startActivity(intent);
            }
        });
    }


}
