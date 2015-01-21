package com.west2.custom.adapter;

import java.util.List;

import com.west2.domain.BlankRoom;
import com.west2.main.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BlankRoomAdapter extends BaseAdapter{
	private Activity mContext;
	private List<BlankRoom> mRooms;
	private int colorArr[] = {R.color.side_color1,R.color.side_color2,R.color.side_color3,R.color.side_color4};
	private final String[] jieArray={" 1\r\n-\r\n2 "," 3\r\n-\r\n4 "," 5\r\n-\r\n6 "," 7\r\n-\r\n8 "," 9\r\n-\r\n10 "};

	public BlankRoomAdapter(Activity context,List<BlankRoom> rooms){
		this.mContext = context;
		this.mRooms = rooms;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mRooms.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(view==null){
			holder = new ViewHolder();
			view=LayoutInflater.from(mContext).inflate(R.layout.list_item_blank_room, null);
			
			holder.sideColor = (LinearLayout)view.findViewById(R.id.side_color);
			holder.more_rooms = (LinearLayout)view.findViewById(R.id.more_rooms);
			holder.textTime  = (TextView)view.findViewById(R.id.time);
			holder.textMore = (TextView)view.findViewById(R.id.text_more);
			view.setTag(holder);
			
		}else{
			holder = (ViewHolder)view.getTag();
		}
		TextView textRoom[]={(TextView)view.findViewById(R.id.room1),
				             (TextView)view.findViewById(R.id.room2),
				             (TextView)view.findViewById(R.id.room3),
				             (TextView)view.findViewById(R.id.room4),
				             (TextView)view.findViewById(R.id.room5)};
		
		TextView textFloor[]={(TextView)view.findViewById(R.id.floor1),
	                          (TextView)view.findViewById(R.id.floor2),
	                          (TextView)view.findViewById(R.id.floor3),
	                          (TextView)view.findViewById(R.id.floor4),
	                          (TextView)view.findViewById(R.id.floor5)};
		
		holder.sideColor.setBackgroundResource(colorArr[position%4]);
		holder.textTime.setText(jieArray[position%5]);
		String tmp = "";
		BlankRoom room = mRooms.get(position);
		StringBuffer[] sb = room.getRooms();
		for(int i=0;i<sb.length;++i) {
	
			if(sb[i].length()!=0)
				textRoom[i].setText(sb[i]);
			else{
				textFloor[i].setVisibility(View.GONE);
			}
		}
		
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				final ViewHolder holder = (ViewHolder)v.getTag();
				
				switch (event.getAction()) {
				
				case MotionEvent.ACTION_UP:
					if (holder.more_rooms.isShown()) {
						holder.more_rooms.setVisibility(View.GONE);
						holder.textMore.setVisibility(View.VISIBLE);
					} else {
						holder.textMore.setVisibility(View.GONE);
						TranslateAnimation mShowAction = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, -0.2f,
								Animation.RELATIVE_TO_SELF, 0.0f);
						mShowAction.setDuration(300);
						holder.more_rooms.startAnimation(mShowAction);
						holder.more_rooms.setVisibility(View.VISIBLE);
						
					}
					break;
				}
				return true;
			}
		});
		
		return view;
	}

    @Override    
    public boolean isEnabled(int position) {     
       return false;     
    }  
    
    final static class ViewHolder{
		LinearLayout sideColor;
		LinearLayout more_rooms;
		TextView textMore;
		TextView textTime;
	}
}
