package com.blue.blueapplication.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.utils.PicassoFunctions;
import com.squareup.picasso.Picasso;

/**
 * Created by wangxiaojian on 16/4/15.
 */
public class BaseFragment extends Fragment{
    private Picasso picasso;
    protected Activity mActivity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = getActivity();
        picasso = PicassoFunctions.getPicasso(activity);

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
