package com.west2.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;
import com.west2.main.R;
import com.west2.main.SetActivity;
import com.west2.service.DateService;
import com.west2.service.LoginService;
import com.west2.service.RefreshService;
import com.west2.service.WeatherService;

public class StartActivity extends Activity{
	// ҳ����ת
	private final int GUIDE	= 1000;
	private final int LOGIN	= 1001;
	private final int MAIN	= 1002;
	private long DISPLAY_TIME = 1000;
	// ����
	private LoginService loginService;
	private RefreshService refreshService;
	private WeatherService weatherService;
	private DateService dateService;

	private Message msg;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// ����ͳ��
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.setDebugMode(true);
		// ��ʼ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		initValue();
	}

	private void initValue(){
		loginService = new LoginService(this);
		refreshService = new RefreshService(this);
		// ���ø�������
		refreshService.setWeather(true);
		refreshService.setDate(true);
		refreshService.setMark(true);
		refreshService.setExam(true);
		refreshService.setMyBook(true);
		
		msg = new Message();
		msg.what = LOGIN;
//		new Thread(getWeather).start();
		new Thread(getDate).start();
		new Thread(count).start();
		new Thread(login).start();
	}
	
	/*
	 * ����ҳ����ת
	 */
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			Intent intent=null;
			switch(msg.what){
			case GUIDE:intent = new Intent(StartActivity.this,GuideActivity.class);
				loginService.setFirstIn(false);
				break;
			case LOGIN:intent = new Intent(StartActivity.this,LoginActivity.class);
				break;
			case MAIN:intent = new Intent(StartActivity.this,SetActivity.class);
				break;
			}
			if(intent==null)
				intent = new Intent(StartActivity.this,LoginActivity.class);
			StartActivity.this.startActivity(intent);
			StartActivity.this.finish();
		}
	};

	/**
	 * ��ʱ�߳�
	 */
	Runnable count = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			dataHandler.sendMessageDelayed(msg, DISPLAY_TIME);
		}
	};
	/*
	 * ��¼�߳�
	 */
	Runnable login = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(loginService.getFirstIn()==true)
				msg.what=GUIDE;
			else
			if(loginService.getAutologin()==false)
				msg.what=LOGIN;
			else
				msg.what=MAIN;
		}
	};
	/*
	 * ��ȡ����
	 */
//	Runnable getWeather = new Runnable(){
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			weatherService = new WeatherService(StartActivity.this);
//			boolean test = weatherService.queryWeather();
//			refreshService.setWeather(!test);
//		}
//	};
	/*
	 * ��ȡ����
	 */
	Runnable getDate = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			dateService = new DateService(StartActivity.this);
			boolean test = dateService.queryDate();
			refreshService.setDate(!test);
		}
	};
}