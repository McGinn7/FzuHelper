package com.west2.service;

import java.lang.reflect.Field;

import com.west2.main.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.WindowManager;

public class ViewService {
	public static int screenWidth;
	public static int screenHeight;
	public static int minLeftMargin;
	public static int maxLeftMargin;
	public static int minTopMargin;
	public static int maxTopMargin;
	public static int statusBarHeight;
	public static int cntViewPosition=1;
	private Activity mActivity;
	public ViewService(Activity activity){
		this.mActivity=activity;
		getStatusBarHeight();
		WindowManager window=(WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
		screenWidth=window.getDefaultDisplay().getWidth();
		screenHeight=window.getDefaultDisplay().getHeight()-statusBarHeight;
		minLeftMargin=-screenWidth/4;
		maxLeftMargin=screenWidth/4;
		minTopMargin=-screenHeight/7;
		maxTopMargin=screenHeight/7;
		Log.e("", " "+statusBarHeight+" "+window.getDefaultDisplay().getHeight());
	}
	/**
	 * @param activity
	 * @param direction
	 *  0 : 到左边
	 *  1 : 到右边
	 *  2 : 从上到下
	 *  3 : 从下到上
	 */
	public void changeActivityAnim(Activity activity,int direction){
		switch(direction){
		case 0:activity.overridePendingTransition(R.anim.right_in,R.anim.right_out);
			break;
		case 1:activity.overridePendingTransition(R.anim.left_in,R.anim.left_out);
			break;
		case 2:activity.overridePendingTransition(R.anim.top_in,R.anim.top_out);
			break;
		case 3:activity.overridePendingTransition(R.anim.bottom_in,R.anim.bottom_out);
			break;
		}
	}
	private void getStatusBarHeight(){
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x=0;
		try{
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = mActivity.getResources().getDimensionPixelSize(x);
		}catch(Exception e1) {
		}
	}
}
