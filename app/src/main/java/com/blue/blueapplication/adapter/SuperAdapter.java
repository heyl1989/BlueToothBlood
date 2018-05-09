package com.blue.blueapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class SuperAdapter<T> extends BaseAdapter {
	protected LayoutInflater mInflater;
	protected List<T> mList;
	protected Context mContext;

	public SuperAdapter(List<T> list) {
		mList = list;

	}

	public SuperAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public SuperAdapter(Context context, List<T> list) {
		mContext = context;
		mList = list;
		mInflater = LayoutInflater.from(context);
	}

	public List<T> getList() {
		return mList;
	}

	public void setList(List<T> list) {
		mList = list;
		notifyDataSetChanged();
	}

	public void setList(T[] array) {
		List<T> list = new ArrayList<T>(array.length);
		for (T t : array) {
			list.add(t);
		}
		setList(list);
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView,
			ViewGroup parent);


}
