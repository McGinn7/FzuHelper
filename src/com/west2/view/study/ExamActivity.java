package com.west2.view.study;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.ExamListAdapter;
import com.west2.domain.Exam;
import com.west2.main.R;
import com.west2.service.ExamService;
import com.west2.service.RefreshService;

public class ExamActivity extends Activity{
	private ListView listExam;
	private ExamService examService;
	private PullToRefreshListView pullListView;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_exam);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		this.pullListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		listExam = pullListView.getRefreshableView();
		registerForContextMenu(listExam);
		
		examService = new ExamService(this);
		getExamFromDb();
		if(new RefreshService(this).getExam()){
			new Thread(getExamFromNet).start();
		}
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		pullListView.setOnRefreshListener(new OnRefreshListener<ListView>(){
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String labelTime = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(labelTime);
				new Thread(getExamFromNet).start();
			}
		});
	}
	/*
	 * 处理网络返回结果
	 */
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			if(pullListView.isRefreshing())
				pullListView.onRefreshComplete();
			// 更新考试列表
			getExamFromDb();
		}
	};
	/*
	 * 网络获取考场
	 */
	Runnable getExamFromNet = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!pullListView.isRefreshing())
				pullListView.setRefreshing();
			examService.queryExam();
			Message msg = dataHandler.obtainMessage();
			dataHandler.sendMessage(msg);
		}
	};
	/*
	 * 数据库调用考场信息
	 */
	private void getExamFromDb(){
		List<Exam> list = examService.getExam();
		if(list==null){
			Toast.makeText(ExamActivity.this, getResources().getString(R.string.net_error),
					Toast.LENGTH_SHORT).show();
			return ;
		}
		if(list.size()==0){
			Exam exam = new Exam();
			exam.setCourseName("暂无考试");
			exam.setTeacherName("");
			exam.setDate("");
			exam.setTime("");
			exam.setRoom("");
			list.add(exam);
		}
		ExamListAdapter mAdapter = new ExamListAdapter(this,list);
		listExam.setAdapter(mAdapter);
	}
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		if(KeyCode==KeyEvent.KEYCODE_BACK){
			this.finish();
		}
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Exam");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Exam");
	    MobclickAgent.onPause(this);
	}
}
