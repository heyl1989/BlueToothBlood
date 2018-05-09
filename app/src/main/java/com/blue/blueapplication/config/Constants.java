package com.blue.blueapplication.config;

import android.support.v4.app.FragmentActivity;

import com.blue.blueapplication.domain.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class Constants {

    public static List<FragmentActivity> listActivity = new ArrayList<FragmentActivity>();
    public static UserInfo mUserInfo;
    public static boolean isConnectBlue;

    public static String LOGIN_URL = "http://101.201.36.190/main/user/api/login";
    public static String REGISTER_URL = "http://101.201.36.190/main/user/api/register";
    public static String UPDATE_PWD_URL = "http://101.201.36.190/main/user/api/updatePassword";
    public static String FORGETPWD_URL = "http://101.201.36.190/main/user/api/forgetPassword";
    public static String COMPLETE_USER_URL = "http://101.201.36.190/main/user/api/updateUser";
    public static String DAY_REPORT_URL = "http://101.201.36.190/main/user/api/dayReport";
    public static String MONTH_REPORT_URL = "http://101.201.36.190/main/user/api/monthReport";
    public static String YEAR_REPORT_URL = "http://101.201.36.190/main/user/api/yearReport";
    public static String I_UPLOADFILE = "http://101.201.36.190/main/user/api/uploadHeadImg";
    public static String I_SAVEPRESS = "http://101.201.36.190/main/user/api/savePressure";
    public static String SAVE_HISTORY = "http://101.201.36.190/main/user/api/savePressureHistory";
    public static String UPDATE = "http://120.25.211.226:9000/api/ManualTest/GetUpdateFile";


}
