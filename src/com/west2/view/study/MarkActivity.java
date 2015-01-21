package com.west2.view.study;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.CreditListAdapter;
import com.west2.custom.adapter.MarkListAdapter;
import com.west2.domain.Credit;
import com.west2.domain.Mark;
import com.west2.main.R;
import com.west2.service.LoginService;
import com.west2.service.MarkService;
import com.west2.service.RefreshService;

public class MarkActivity extends Activity{
	private MarkService markService;
	private LoginService loginService;
	private RefreshService refreshService;

	private String[] term;
	private int cntPosition=0;
	private ListView markList,creditList;
	private LinearLayout layoutMark,layoutCredit;
	private Button buttonMenuCredit,buttonMenuTerm;
	private PullToRefreshListView pullListViewMark;
	private PullToRefreshListView pullListViewCredit;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mark);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		pullListViewMark=(PullToRefreshListView)findViewById(R.id.pull_refresh_list_mark);
		pullListViewCredit=(PullToRefreshListView)findViewById(R.id.pull_refresh_list_credit);
		buttonMenuCredit=(Button)findViewById(R.id.button_menu_credit);
		buttonMenuTerm=(Button)findViewById(R.id.button_menu_term);
		layoutMark=(LinearLayout)findViewById(R.id.layout_mark);
		layoutCredit=(LinearLayout)findViewById(R.id.layout_credit);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		markList=pullListViewMark.getRefreshableView();
		creditList=pullListViewCredit.getRefreshableView();
		registerForContextMenu(markList);
		registerForContextMenu(creditList);

		markService = new MarkService(MarkActivity.this);
		loginService = new LoginService(MarkActivity.this);
		refreshService = new RefreshService(MarkActivity.this);

		getCreditFromDb();
		getTermFromDb();
		getMarkFromDb();
		if(refreshService.getMark()){
			new Thread(getMarkFromNet).start();
		}
		if(refreshService.getCredit()){
			new Thread(getCreditFromNet).start();
		}
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		pullListViewMark.setOnRefreshListener(new OnRefreshListener<ListView>(){
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String labelTime = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(labelTime);
				new Thread(getMarkFromNet).start();
			}
		});
		pullListViewCredit.setOnRefreshListener(new OnRefreshListener<ListView>(){
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String labelTime = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(labelTime);
				new Thread(getCreditFromNet).start();
			}
		});
		buttonMenuCredit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(cntPosition==0){
					if(pullListViewCredit.isShown()) return ;
					layoutMark.setAnimation(setAnim(R.anim.left_out));
					layoutCredit.setAnimation(setAnim(R.anim.left_in));
					layoutMark.setVisibility(View.GONE);
					layoutCredit.setVisibility(View.VISIBLE);
					pullListViewMark.setVisibility(View.GONE);
					pullListViewCredit.setVisibility(View.VISIBLE);
					buttonMenuCredit.setText("我的成绩");
					cntPosition=1;
				}
				else{
					if(pullListViewMark.isShown()) return ;
					layoutMark.setAnimation(setAnim(R.anim.right_in));
					layoutCredit.setAnimation(setAnim(R.anim.right_out));
					layoutMark.setVisibility(View.VISIBLE);
					layoutCredit.setVisibility(View.GONE);
					pullListViewMark.setVisibility(View.VISIBLE);
					pullListViewCredit.setVisibility(View.GONE);					
					buttonMenuCredit.setText("我的绩点");
					cntPosition=0;
				}
			}
		});
		buttonMenuTerm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final AlertDialog.Builder builder = new AlertDialog.Builder(MarkActivity.this);
				AlertDialog dialog;
				builder.setTitle("请选择学期");
				final List<String> list = markService.getTermList();
				builder.setSingleChoiceItems(term,-1,
					new DialogInterface.OnClickListener() {  
				    public void onClick(DialogInterface dialog, int item) {
				    	markService.setCurrentTerm(list.get(item));
				    	buttonMenuTerm.setText(term[item]);
				    	getMarkFromDb();
				    	dialog.dismiss();
				    }
				});
				dialog = builder.show();
			}
		});
	}
	/*
	 * 网络返回结果处理
	 */
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what==1){
				if(pullListViewMark.isRefreshing())
					pullListViewMark.onRefreshComplete();
				getTermFromDb();
				getMarkFromDb();
				return ;
			}
			if(msg.what==2){
				if(pullListViewCredit.isRefreshing())
					pullListViewCredit.onRefreshComplete();
				getCreditFromDb();
			}
			if(msg.what==0){
				if(pullListViewCredit.isRefreshing())
					pullListViewCredit.onRefreshComplete();
				if(pullListViewMark.isRefreshing())
					pullListViewMark.onRefreshComplete();
			}
		}
	};
	/*
	 * 网络获取成绩
	 */
	Runnable getMarkFromNet = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!pullListViewMark.isRefreshing())
				pullListViewMark.setRefreshing();
			boolean status = markService.queryMark(loginService.getUsername());
			refreshService.setMark(!status);
			Message msg=dataHandler.obtainMessage();
			if(status) msg.what=1; else msg.what=0;
			dataHandler.sendMessage(msg);
		}
	};
	/*
	 * 网络后去绩点
	 */
	Runnable getCreditFromNet = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!pullListViewCredit.isRefreshing())
				pullListViewCredit.setRefreshing();
			boolean status = markService.queryCredit(loginService.getUsername(), loginService.getPassword());
			refreshService.setCredit(!status);
			Message msg=dataHandler.obtainMessage();
			if(status) msg.what=2; else msg.what=0;
			dataHandler.sendMessage(msg);
		}
	};
	/*
	 * 数据库调用学期列表
	 */
	private void getTermFromDb(){
		String[] temp = this.getResources().getStringArray(R.array.term_list);
		List<String> list = markService.getTermList();
		if(list!=null && list.size()>0){
			term = new String[list.size()];
			for(int i=0;i<list.size();++i) term[i]=temp[i];
			String cntTerm = markService.getCurrentTerm();
			buttonMenuTerm.setText(term[list.indexOf(cntTerm)]);
		}
	}
	/*
	 * 数据库调用成绩
	 */
	private void getMarkFromDb(){
		String cntTerm = markService.getCurrentTerm();
		if(cntTerm==null || cntTerm.equals("")){
			Toast.makeText(MarkActivity.this, getResources().getString(R.string.net_error),
					Toast.LENGTH_SHORT).show();
			return ;
		}
		List<Mark> list = markService.getMark(cntTerm);
		if(list==null){
			Toast.makeText(MarkActivity.this, getResources().getString(R.string.net_error),
					Toast.LENGTH_SHORT).show();
			return ;
		}
		MarkListAdapter listAdapter = new MarkListAdapter(MarkActivity.this,list);
		markList.setAdapter(listAdapter);
	}
	/*
	 * 数据库调用绩点
	 */
	private void getCreditFromDb(){
		Credit credit = markService.getCredit();
		if(credit==null) return ;
		CreditListAdapter listAdapter = new CreditListAdapter(MarkActivity.this,credit);
		creditList.setAdapter(listAdapter);
	}
	private Animation setAnim(int id){
		Animation mAnim = AnimationUtils.loadAnimation(this, id);
		mAnim.setFillAfter(true);
		return mAnim;
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
	    MobclickAgent.onPageStart("Mark");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Mark");
	    MobclickAgent.onPause(this);
	}
}