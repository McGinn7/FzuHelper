package com.west2.custom.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.west2.main.R;

public class WeekListAdapter extends BaseAdapter{
	private Context mContext;
	private List<String> list;
	private String cntWeek;
	public WeekListAdapter(Context context,List<String> list,String week){
		this.mContext=context;
		this.list=list;
		this.cntWeek=week;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
			view=LayoutInflater.from(mContext).inflate(R.layout.list_item_week, null);
		TextView textWeek = (TextView)view.findViewById(R.id.week);
		if(list.get(position).contains("周"))
			textWeek.setText(list.get(position));
		else
			textWeek.setText("第"+list.get(position)+"周");
		if(list.get(position).equals(cntWeek)){
			textWeek.append("(本周)");
			textWeek.setBackgroundResource(R.color.course_color1);
		}
		return view;
	}
}
