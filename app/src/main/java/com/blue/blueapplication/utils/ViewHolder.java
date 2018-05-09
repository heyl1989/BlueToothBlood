package com.blue.blueapplication.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by wangxiaojian on 16/4/25.
 */
public class ViewHolder {

    private ViewHolder(){

    }

    public static <T> T get(View view, int id) {
        SparseArray viewHolder =
                (SparseArray) view.getTag();
        if (viewHolder == null) {
            viewHolder = new
                    SparseArray();
            view.setTag(viewHolder);
        }
        View childView = (View) viewHolder.get(id);
        if (childView == null) {
            childView =

                    view.findViewById(id);
            viewHolder.put(id,

                    childView);
        }
        return (T) childView;
    }
}
