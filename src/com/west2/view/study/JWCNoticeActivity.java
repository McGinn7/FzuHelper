package com.west2.view.study;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import com.west2.custom.adapter.JWCNoticeAdapter;
import com.west2.domain.JWCNotice;
import com.west2.main.R;
import com.west2.service.JWCNoticeService;
import com.west2.utils.LoadingDialog;

public class JWCNoticeActivity extends Activity {
	private int page;
	private ListView mListView;
	private List<JWCNotice> mData;
	private LoadingDialog loading;
	private JWCNoticeAdapter mAdapter;
	private PullToRefreshListView pullListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_jwc_notice);

		findView();
		initValue();
		setListener();
	}
	private void findView(){
		pullListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		mListView = pullListView.getRefreshableView();
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		loading = new LoadingDialog(this);
		loading.show();
		page=1;
		pullListView.setMode(Mode.PULL_FROM_END);
		mData = new ArrayList<JWCNotice>();
		mAdapter= new JWCNoticeAdapter(JWCNoticeActivity.this, mData);
		mListView.setAdapter(mAdapter);
		new GetDataTask().execute();
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
				new GetDataTask().execute();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				String url = "";
				if(position>0) url = mData.get(position-1).getUrl();
				Intent intent = new Intent(JWCNoticeActivity.this, JWCDetailActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
			}
		});
	}
	/*
	 * 网络获取通知
	 */
	private class GetDataTask extends AsyncTask<Void, Void, List<JWCNotice>> {  
		List<JWCNotice> list;
		@Override
        protected List<JWCNotice> doInBackground(Void... params) {  
            // Simulates a background job.
			list = JWCNoticeService.getJWCNotices(JWCNoticeActivity.this,page);
            return list;
        }  
		@Override
		protected void onPostExecute(List<JWCNotice> result) {
			if(loading.isShowing()) loading.cancel();
			if(pullListView.isRefreshing()) pullListView.onRefreshComplete();
			if(list==null||list.size()==0){
				Toast.makeText(getApplicationContext(), R.string.net_error, Toast.LENGTH_SHORT).show();
				super.onPostExecute(result);
				return;
			}
			++page;
			for(int i=0;i<list.size();++i) mData.add(list.get(i));
			mAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("JWCNotice");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("JWCNotice");
	    MobclickAgent.onPause(this);
	}
}
