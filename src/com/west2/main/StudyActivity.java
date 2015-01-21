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
import com.west2.view.study.BlankroomActivity;
import com.west2.view.study.CourseSetActivity;
import com.west2.view.study.ExamActivity;
import com.west2.view.study.JWCNoticeActivity;
import com.west2.view.study.MarkActivity;

public class StudyActivity extends Activity{
	// 页面跳转
	private final int BLANK=1000;
	private final int MARK=1001;
	private final int COURSE=1002;
	private final int EXAM=1003;
	private final int NOTICE=1004;
	private final int PHYSICS=1005;
	// 默认数据
	private int[] mColor={R.color.blue,R.color.orange,R.color.red,R.color.green};
	private final String[] eName={"Blank","Mark","Course","Exam","Notice","Physics"};
	private final String[] cName={"空教室","成绩","课表","考试","教务处通知","物理实验"};

	private ListView menuList;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_study);
		
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
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Message msg = dataHandler.obtainMessage();
				msg.what = 1000+position;
				dataHandler.sendMessageDelayed(msg, 100);
			}
		});
	}
	/*
	 * 页面跳转
	 */
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			Intent intent = null;
			switch(msg.what){
			case MARK:intent = new Intent(StudyActivity.this,MarkActivity.class);
				break;
			case COURSE:intent = new Intent(StudyActivity.this,CourseSetActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("week", "");
				intent.putExtras(bundle);
				break;
			case EXAM:intent = new Intent(StudyActivity.this,ExamActivity.class);
				break;
			case BLANK:intent = new Intent(StudyActivity.this,BlankroomActivity.class);
				break;
			case NOTICE:intent = new Intent(StudyActivity.this,JWCNoticeActivity.class);
				break;
			case PHYSICS:
				String url=StudyActivity.this.getString(R.string.url_physics);
				Uri uri = Uri.parse(url); 
				intent = new Intent(Intent.ACTION_VIEW, uri);  startActivity(intent);
				break;
			}
			if(intent==null) return ;
			startActivity(intent);
		}
	};
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Study");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Study");
	    MobclickAgent.onPause(this);
	}
}
