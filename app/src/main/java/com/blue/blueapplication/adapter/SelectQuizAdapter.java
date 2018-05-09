package com.blue.blueapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.domain.SelectItem;

import java.util.List;

public class SelectQuizAdapter extends SuperAdapter<SelectItem>{

	public SelectQuizAdapter(Context context, List<SelectItem> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.wheel_text_centered, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView
					.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SelectItem applySelectDomain = mList.get(position);
		if (applySelectDomain != null) {
			holder.text.setText(applySelectDomain.des);
		}

		return convertView;
	}


	static class ViewHolder {
		TextView text;
	}

}
