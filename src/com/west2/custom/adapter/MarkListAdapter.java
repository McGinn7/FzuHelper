package com.west2.custom.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.west2.domain.Mark;
import com.west2.main.R;

public class MarkListAdapter extends BaseAdapter{
	private int colorArr[] = {R.color.side_color1,R.color.side_color2,R.color.side_color3,R.color.side_color4};
	private Context mContext;
	private List<Mark> list;
	public MarkListAdapter(Context context,List<Mark> list){
		this.mContext=context;
		this.list=list;
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
		if(view==null)
			view=LayoutInflater.from(mContext).inflate(R.layout.list_item_mark, null);
		LinearLayout side_color = (LinearLayout)view.findViewById(R.id.side_color);
		TextView courseName=(TextView)view.findViewById(R.id.exam_name);
		TextView courseScore=(TextView)view.findViewById(R.id.exam_score);
		side_color.setBackgroundResource(colorArr[position%4]);
		courseName.setText(list.get(position).getCourseName());
		courseScore.setText(list.get(position).getScore());
		return view;
	}
}
