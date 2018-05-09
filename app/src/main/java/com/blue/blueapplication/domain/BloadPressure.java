package com.blue.blueapplication.domain;

import com.blue.blueapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxiaojian on 16/4/17.
 */
public class BloadPressure {

    public int iconId;
    public String name;
    public String result;
    public String pressureRemind;
    public String pressureLow;
    public String pressreHeight;

    public BloadPressure(int iconId, String name, String pressreHeight, String pressureLow, String pressureRemind,  String result) {
        this.iconId = iconId;
        this.name = name;
        this.pressreHeight = pressreHeight;
        this.pressureLow = pressureLow;
        this.pressureRemind = pressureRemind;
        this.result = result;
    }


    public static List<BloadPressure> getInitData(){
        List<BloadPressure> list = new ArrayList<>();
        BloadPressure pressure1 = new BloadPressure(R.mipmap.home_icon_high_pressure
        ,"高压","139","90","收缩压","0");
        BloadPressure pressure2 = new BloadPressure(R.mipmap.home_icon_low_pressure
        ,"低压","89","60","舒张压","0");
        BloadPressure pressure3 = new BloadPressure(R.mipmap.home_icon_heart_rate
        ,"心率","100","60","心率值","0");

        list.add(pressure1);
        list.add(pressure2);
        list.add(pressure3);

        return  list;
    }
}
