package com.blue.blueapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.domain.HistoryPressure;

import java.util.List;

public class HistoryAdapter extends SuperAdapter<HistoryPressure>{

	public int state;
	public void setState(int state){
		this.state = state;
	}
	public HistoryAdapter(Context context, List<HistoryPressure> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView==null) {
			convertView = mInflater.inflate(R.layout.history_item, null);
			holder = new ViewHolder();
			holder.timeTv = (TextView)convertView.findViewById(R.id.tv_time);
			holder.pressureTv = (TextView)convertView.findViewById(R.id.tv_pressure);
			holder.heartTv = (TextView)convertView.findViewById(R.id.tv_heart);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		HistoryPressure pressure = mList.get(position);
		if (pressure!=null) {
			if ("001".equals(pressure.createTime)){
				holder.timeTv.setText("时间");
				holder.pressureTv.setText("高低压");
				holder.heartTv.setText("心率");
			}else {
				String[] strings = pressure.createTime.split("-");
				if (state==0){
					if (strings.length>2){
						String[] strings1 = strings[2].split(" ");
						if (strings1.length>1){
							String[] strings2 = strings1[1].split(":");
							if (strings2!=null&&strings2.length>0){
								if (strings2[0].startsWith("0")){
									holder.timeTv.setText(strings2[0].replace("0","")+"时"+strings2[1].replace("0","")+"分");
								}else {
									holder.timeTv.setText(strings2[0]+"时"+strings2[1]+"分");
								}

							}
						}
					}

				}else if (state==1){
					if (strings.length>2){
						if (strings[2].startsWith("0")){
							holder.timeTv.setText(strings[2].replace("0","")+"号");
						}else {
							holder.timeTv.setText(strings[2]+"号");

						}
					}
				}else if (state==2){
					if (strings.length>1)
						if (strings[1].startsWith("0")){
							holder.timeTv.setText(strings[1].replace("0","")+"月");
						}else {
							holder.timeTv.setText(strings[1]+"月");
						}
				}

				holder.pressureTv.setText(pressure.highPressure+"/"+pressure.lowPressure+"mmHg");
				holder.heartTv.setText(pressure.heartRate+"bpm");
			}

		}
		return convertView;
	}

	static class ViewHolder {
		TextView timeTv;
		TextView pressureTv;
		TextView heartTv;
	}

}
