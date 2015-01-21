package com.west2.view.study;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.BlankRoomAdapter;
import com.west2.custom.adapter.ViewPagerAdapter;
import com.west2.domain.BlankRoom;
import com.west2.main.R;
import com.west2.service.BlankRoomService;
import com.west2.service.ViewService;
import com.west2.utils.LoadingDialog;

public class BlankroomActivity extends Activity {
	private int[] textId={
		R.id.blank_room_pos1,R.id.blank_room_pos2,R.id.blank_room_pos3,
		R.id.blank_room_pos4,R.id.blank_room_pos5,R.id.blank_room_pos6,
		R.id.blank_room_pos7,R.id.blank_room_pos8,R.id.blank_room_pos9
	};
	private ViewService viewService;
	private BlankRoomService roomService;

	private int mCurPos;
	private TextView[] textPos;
	private ViewPager viewpager;
	private LoadingDialog loading;
	private ImageButton btnRefresh;
	private LinearLayout layout_blue;
	private List<BlankRoom>[] listRoom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_blank_room);

		findView();
		initValue();
		setListener();
	}
	private void findView() {
		viewpager = (ViewPager)findViewById(R.id.blank_room_viewpager);
		layout_blue = (LinearLayout)findViewById(R.id.layout_blue);
		btnRefresh = (ImageButton)findViewById(R.id.room_refresh);
		textPos = new TextView[10];
		for(int i=0;i<9;++i){
			textPos[i] = (TextView)findViewById(textId[i]);
		}
	}
	/*
	 * 初始化数据
	 */
	private void initValue() {
		mCurPos=0;
		listRoom = new List[10];
		for(int i=0;i<9;++i) listRoom[i] = null;
		viewService = new ViewService(this);
		roomService = new BlankRoomService(this);
		loading = new LoadingDialog(this);
		loading.show();
		dataHandler.post(refreshUI);
	}
	/*
	 * 设置监听
	 */
	private void setListener() {
		for(int i=0;i<9;++i){
			textPos[i].setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					int pos = 0;
					for(int i=0;i<9;++i)
					if(view.getId() == textId[i]){
						pos = i;
						break;
					}
					setWeekDayBackground(pos);
					viewpager.setCurrentItem(pos,true);
					mCurPos = pos;
				}
			});
		}
		viewpager.setOnPageChangeListener(new OnPageChangeListener(){
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				setWeekDayBackground(position);
				mCurPos = position;
			}
		});
		btnRefresh.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loading.show();
				new Thread(getRoomFromNet).start();
			}
		});
	}
	/*
	 * 处理返回结果
	 */
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				break;
			case 1:
				dataHandler.post(refreshUI);
				break;
			case 2:
				viewpager.setCurrentItem(mCurPos,false);
				if(loading.isShowing()) loading.cancel();
				break;
			}
		}
	};
	/*
	 * 网络获取空教室
	 */
	Runnable getRoomFromNet = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			listRoom[mCurPos] = new ArrayList<BlankRoom>();
			for(int j=0;j<5;++j){
				BlankRoom room = roomService.getBlankRoom(j, mCurPos);
				if(!loading.isShowing()) return ;
				if(room!=null) listRoom[mCurPos].add(room);
			}
			Message msg = dataHandler.obtainMessage();
			msg.what = 1;
			dataHandler.sendMessage(msg);
		}
	};
	/*
	 * 更新UI线程
	 */
	Runnable refreshUI = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<View> views = new ArrayList<View>();
			for(int i=0;i<9;++i)
			if(listRoom[i]!=null){
				ListView listview = new ListView(BlankroomActivity.this);
				listview.setAdapter(
					new BlankRoomAdapter(BlankroomActivity.this,listRoom[i]));
				views.add(listview);
			}
			else{
				ImageView image = new ImageView(BlankroomActivity.this);
				image.setBackgroundResource(R.drawable.pull_refresh);

				views.add(image);
			}
			if(!loading.isShowing()) return ;
			viewpager.setAdapter(new ViewPagerAdapter(views));
			Message msg = dataHandler.obtainMessage();
			msg.what = 2;
			dataHandler.sendMessage(msg);
		}
	};
	/*
	 * 设置教室位置背景
	 */
	public void setWeekDayBackground(int position){
		if(position<0 || position == mCurPos) return ;
		int width = viewService.screenWidth/9;
		TranslateAnimation anim = new TranslateAnimation(
				width * mCurPos, width * position, 0, 0);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(200);
		anim.setFillAfter(true);
		layout_blue.startAnimation(anim);
		mCurPos = position;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Blankroom");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Blankroom");
	    MobclickAgent.onPause(this);
	}
}
