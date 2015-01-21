package com.west2.custom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.west2.main.R;

public class MenuAdapter extends BaseAdapter{
	private int total;
	private Context context;
	private String[] cName;
	private String[] eName;
	private int[] mColor;
	public MenuAdapter(int tot,Context context,int[] color,String[] en,String[] cn){
		total=tot;
		cName=cn;
		eName=en;
		mColor=color;
		this.context=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return total;
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
	public View getView(int position, View view, final ViewGroup parent) {
		// TODO Auto-generated method stub
		if(view==null)
			view = LayoutInflater.from(context).inflate(R.layout.list_item_menu, null);
		LinearLayout bgLayout = (LinearLayout)view.findViewById(R.id.bglayout);
		TextView mEName=(TextView)view.findViewById(R.id.eName);
		TextView mCName=(TextView)view.findViewById(R.id.cName);
		mEName.setText(eName[position]);
		mCName.setText(cName[position]);
        bgLayout.setBackgroundResource(mColor[position%mColor.length]);
        
		view.setOnTouchListener(new OnTouchListener(){
			float x,y,dx,dy;
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				Animation shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shark);//加载动画资源文件
				view.startAnimation(shakeAnimation);
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					x = event.getX();
					y = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					dx = event.getX();
					dy = event.getY();
					if(Math.abs(dx-x)>Math.abs(dy-y)) parent.setFocusable(false);
					else parent.setFocusable(true);
					break;
				case MotionEvent.ACTION_UP:
					break;					
				}
				return false;
			}
		});
		return view;
	}

}
