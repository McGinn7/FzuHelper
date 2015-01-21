package com.west2.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.MenuAdapter;
import com.west2.service.ViewService;
import com.west2.view.life.JudgeActivity;
import com.west2.view.life.LibraryActivity;
import com.west2.view.life.MapActivity;
import com.west2.view.life.MarketActivity;
import com.west2.view.life.WeatherActivity;

public class LifeActivity extends Activity {
	// 页面跳转
	private final int MAP=1000;
	private final int LIBRARY=1001;
	private final int MARKET=1002;
	private final int JUDGE=1003;
	private final int WEATHER=1004;
	private final int ACTIVITY=1005;
	// 默认数据
	private final int[] mColor={R.color.blue,R.color.orange,R.color.red,R.color.green};
	private final String[] eName={"Map","Library","Market","Judge","Weather","Activity"};
	private final String[] cName={"地图","图书馆","二手市场","一键评议","天气","活动"};
	
	private ListView menuList;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_life);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		menuList=(ListView)findViewById(R.id.menu_list);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		MenuAdapter mAdapter = new MenuAdapter(eName.length,this,mColor,eName,cName);
		menuList.setAdapter(mAdapter);
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		menuList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
//					changeActivity(1000+position);
				Message msg = dataHandler.obtainMessage();
				msg.what = 1000 + position;
				dataHandler.sendMessageDelayed(msg, 100);
			}
		});
	}
	/*
	 * 页面跳转
	 */
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			Intent intent=null;
			switch(msg.what){
			case MAP:intent=new Intent(LifeActivity.this,MapActivity.class);
				break;
			case LIBRARY:intent=new Intent(LifeActivity.this,LibraryActivity.class);
				break;
			case MARKET:intent=new Intent(LifeActivity.this,MarketActivity.class);
				break;
			case JUDGE:intent = new Intent(LifeActivity.this,JudgeActivity.class);
				break;
			case WEATHER:intent = new Intent(LifeActivity.this,WeatherActivity.class);
				break;
			case ACTIVITY:
				String url=LifeActivity.this.getString(R.string.url_activity);
				Uri uri = Uri.parse(url); 
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;
			}
			if(intent==null) return ;
			startActivity(intent);
			new ViewService(LifeActivity.this).changeActivityAnim(LifeActivity.this.getParent(), 0);
		}
	};
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Life2");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Life2");
	    MobclickAgent.onPause(this);
	}
}
