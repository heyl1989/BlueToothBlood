package com.blue.blueapplication.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue.blueapplication.R;
import com.blue.blueapplication.domain.BloadPressure;

import java.util.List;

public class BloodPressureAdapter extends SuperAdapter<BloadPressure>{

	public BloodPressureAdapter(Context context, List<BloadPressure> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView==null) {
			convertView = mInflater.inflate(R.layout.main_item, null);
			holder = new ViewHolder();
			holder.iconTv = (TextView)convertView.findViewById(R.id.tv_icon);
			holder.resultTv = (TextView)convertView.findViewById(R.id.tv_result);
			//将字体文件保存在assets/fonts/目录下，创建Typeface对象
			Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(),"fonts/DINCond_Medium.otf");
			//使用字体
			holder.resultTv.setTypeface(typeFace);
			holder.pressureRemindTv = (TextView)convertView.findViewById(R.id.tv_ya_remind);
			holder.pressureRute = (TextView)convertView.findViewById(R.id.tv_rute);
			holder.tv_unit = (TextView)convertView.findViewById(R.id.tv_unit);
			holder.pressureLowTv = (TextView)convertView.findViewById(R.id.tv_ya_low);
			holder.pressureHeightTv = (TextView)convertView.findViewById(R.id.tv_ya_heigh);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		BloadPressure pressure = mList.get(position);
		if (pressure!=null) {
			holder.iconTv.setCompoundDrawablesWithIntrinsicBounds(null,mContext.getResources().getDrawable(pressure.iconId),null,null);
			holder.iconTv.setText(pressure.name);
			holder.resultTv.setText(pressure.result);
			holder.pressureRemindTv.setText(pressure.pressureRemind);
			holder.pressureLowTv.setText(pressure.pressureLow);
			holder.pressureHeightTv.setText(pressure.pressreHeight);
			if ("心率".equals(pressure.name)){
				holder.pressureRute.setText(" 次/分钟");
				holder.tv_unit.setText("/bpm  ");
			}else {
				holder.pressureRute.setText("/mmHg");
				holder.tv_unit.setText("/mmHg");
			}
		}
		return convertView;
	}

	static class ViewHolder {
		TextView iconTv;
		TextView resultTv;
		TextView pressureRemindTv;
		TextView pressureRute;
		TextView tv_unit;
		TextView pressureLowTv;
		TextView pressureHeightTv;
	}

}
