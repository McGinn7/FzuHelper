package com.west2.custom.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.west2.domain.MarketMessage;
import com.west2.main.R;

public class MarketAdapter extends BaseAdapter{
	private Activity mContext;
	private List<MarketMessage> mData;
	private int colorArr[] = {R.color.side_color1,R.color.side_color2,R.color.side_color3,R.color.side_color4};

	public MarketAdapter(Activity context,List<MarketMessage> data){
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
		return mData.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_market, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.market_title);
			holder.author = (TextView) convertView.findViewById(R.id.market_author);
			holder.time = (TextView) convertView.findViewById(R.id.market_time);
			holder.layout = (LinearLayout) convertView.findViewById(R.id.market_kind_layout);
			convertView.setTag(holder); 
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		MarketMessage message = mData.get(position);		
		holder.layout.setBackgroundResource(colorArr[position%4]);
		holder.title.setText(message.getTitle().replace("&amp;",""));
		holder.author.setText(message.getAuthor()+"¡¡·¢±í");
		holder.time.setText(message.getTime());
		return convertView;
	}
	static class ViewHolder {
		TextView title;
		TextView author;
		TextView time;
		LinearLayout layout;
	}
}
