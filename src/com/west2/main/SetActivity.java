package com.west2.main;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.ViewPagerAdapter;
import com.west2.service.ViewService;

public class SetActivity extends Activity{
	private Context context=null;
	private LocalActivityManager mManager=null;
	private static ViewPager viewPager=null;
	public static boolean canScoll = false;
	private boolean doubleBack;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set);

		mManager=new LocalActivityManager(this,true);
    	mManager.dispatchCreate(savedInstanceState);
    	findView();
    	initValue();
    	setListener();
	}
	public static void setPosition(int position){
		viewPager.setCurrentItem(position,true);
	}
	private void findView(){
		viewPager=(ViewPager)findViewById(R.id.viewpager);		
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		doubleBack=false;
		context=SetActivity.this;
		final ArrayList<View> mList=new ArrayList<View>();
		View view;
		Intent intent=null;
		intent=new Intent(context,LifeActivity.class);
		view=getView("LifeActivity",intent);
		mList.add(getView("LifeActivity",intent));
		intent=new Intent(context,MainActivity.class);
		mList.add(getView("MainActivity",intent));
		intent=new Intent(context,StudyActivity.class);
		view = getView("StudyActivity",intent);
		mList.add(getView("StudyActivity",intent));
		viewPager.setAdapter(new ViewPagerAdapter(mList));
		viewPager.setCurrentItem(ViewService.cntViewPosition);
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
	}
	private View getView(String id,Intent intent){
		if(intent==null) return null;
		return mManager.startActivity(id, intent).getDecorView();
	}
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		if(viewPager.getCurrentItem()!=1){
			if(KeyCode==KeyEvent.KEYCODE_BACK){
				SetActivity.setPosition(1);
			}
		}else{
			if(doubleBack==false){
				doubleBack=true;
				Toast.makeText(SetActivity.this,getResources().getString(R.string.double_back),
						Toast.LENGTH_SHORT).show();
				Timer exitTime = new Timer();
				exitTime.schedule(new TimerTask(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						doubleBack=false;
					}
				}, 2000);
			}
			else{
				this.finish();
				System.exit(0);
			}
		}
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Set");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Set");
	    MobclickAgent.onPause(this);
	}
}
