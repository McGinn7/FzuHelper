package com.west2.view.life;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.MarketAdapter;
import com.west2.domain.MarketMessage;
import com.west2.main.R;
import com.west2.service.MarketService;
import com.west2.service.ViewService;
import com.west2.utils.LoadingDialog;

public class MarketActivity extends Activity {
	private int page;
	private ListView mListView;
	private LoadingDialog loading;
	private MarketAdapter mAdapter;
	private List<MarketMessage> mData;
	private PullToRefreshListView mPullToRefreshListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_market);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		mPullToRefreshListView =(PullToRefreshListView) findViewById(R.id.market_pullToRefreshListView);
		mListView = mPullToRefreshListView.getRefreshableView();
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		loading = new LoadingDialog(this);
		loading.show();
		page=1;
		mData = new ArrayList<MarketMessage>();
		mAdapter = new MarketAdapter(MarketActivity.this, mData);
		mListView.setAdapter(mAdapter);
		new GetDataTask().execute(page);
		mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		mPullToRefreshListView.setOnRefreshListener(new  OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String label = df.format(new Date());
	            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新时间"+label);
				new GetDataTask().execute(page);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int positon,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MarketActivity.this, MarketDetailActivity.class);
				intent.putExtra("url", mData.get(positon-1).getUrl());
				startActivity(intent);
			}
		});
	}
	private class GetDataTask extends AsyncTask<Integer, Void, List<MarketMessage>>{
		private List<MarketMessage> list;
		@Override
		protected List<MarketMessage> doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			list = MarketService.getMarketMessage(MarketActivity.this, arg0[0]);
			return list;
		}
		@Override
		protected void onPostExecute(List<MarketMessage> result) {
			if (mPullToRefreshListView.isRefreshing()) {
				mPullToRefreshListView.onRefreshComplete();
			}
			if(loading.isShowing()) loading.cancel();
			if(list==null||list.size()==0){
				Toast.makeText(MarketActivity.this, R.string.net_error_market, Toast.LENGTH_SHORT).show();
			}else{
				page++;
				for(int i=0;i<list.size();++i){
					mData.add(list.get(i));
				}
				mAdapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			new ViewService(this).changeActivityAnim(this, 1);
		}
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Market");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Market"); 
	    MobclickAgent.onPause(this);
	}
}
