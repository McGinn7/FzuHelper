package com.west2.custom.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.west2.domain.JWCNotice;
import com.west2.main.R;

public class JWCNoticeAdapter extends BaseAdapter {

	private List<JWCNotice> mData;
	private Activity mContext;
	private int colorArr[] = {R.color.side_color1,R.color.side_color2,R.color.side_color3,R.color.side_color4};
	public JWCNoticeAdapter(Activity context,List<JWCNotice> data ){
		this.mContext=context;
		this.mData=data;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(view==null)
			view=LayoutInflater.from(mContext).inflate(R.layout.list_item_jwc, null);
		TextView title=(TextView)view.findViewById(R.id.jwc_title);
		TextView date=(TextView)view.findViewById(R.id.jwc_date);
		LinearLayout side=(LinearLayout)view.findViewById(R.id.side);
		JWCNotice notice=mData.get(position);
		title.setText(notice.getTitle());
		date.setText(notice.getDate());
		side.setBackgroundResource(colorArr[position%4]);
		if(notice.isRed())
			title.setTextColor(mContext.getResources().getColor(R.color.red));
		else
			title.setTextColor(mContext.getResources().getColor(R.color.black));
		return view;
	}
}
