package com.west2.view.life;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.BookSearchAdapter;
import com.west2.domain.Book;
import com.west2.main.R;
import com.west2.service.LibraryService;
import com.west2.utils.LoadingDialog;

public class LibrarySearchActivity extends Activity{
	private LibraryService libService;
	private int page;
	private String key;
	private Handler uiHandler;
	private ListView listViewBook;
	private LoadingDialog loading;
	private BookSearchAdapter mAdapter;
	private List<Book> cntList,nextList;
	private PullToRefreshListView pullListView;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_library_search);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		pullListView = (PullToRefreshListView)findViewById(R.id.library_pull_refresh_list);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		loading = new LoadingDialog(this);
		uiHandler = new Handler();
		libService = new LibraryService(this);
		listViewBook = pullListView.getRefreshableView();
		pullListView.setMode(Mode.PULL_FROM_END);
		cntList = new ArrayList<Book>();
		nextList = new ArrayList<Book>();
		page=0;
		Bundle bundle = this.getIntent().getExtras();
		key = bundle.getString("key");
		mAdapter = new BookSearchAdapter(this,cntList);
		listViewBook.setAdapter(mAdapter);

		loading.show();
		new Thread(getBookFromNet).start();
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
	}
	/*
	 * 更新UI线程
	 */
	Runnable refreshUI = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(loading.isShowing()) loading.cancel();
			if(pullListView.isRefreshing()) pullListView.onRefreshComplete();
			mAdapter.notifyDataSetChanged();
		}
	};
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				Toast.makeText(LibrarySearchActivity.this, "检索无结果或图书馆暂不可用", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	/*
	 * 网络获取检索图书
	 */
	Runnable getBookFromNet = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(page>0 &&( nextList==null ||nextList.size()==0)){
				return ;
			}
			if(page==0){
				if(!pullListView.isRefreshing())
					pullListView.setRefreshing();
				nextList = libService.queryBook(key, ++page);
				if(nextList == null || nextList.size()==0){
					Message msg = dataHandler.obtainMessage();
					msg.what = 0;
					dataHandler.sendMessage(msg);
				}
			}
			for(int i=0;nextList!=null&&i<nextList.size();++i){
				cntList.add(nextList.get(i));
			}
			//更新listview
			uiHandler.post(refreshUI);
			nextList = libService.queryBook(key, ++page);
		}
	};
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		private String[] mStrings={""};
		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			new Thread(getBookFromNet).start();
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {
			pullListView.onRefreshComplete();
			super.onPostExecute(result);
		}
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
	    MobclickAgent.onPageStart("LibrarySearch");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("LibrarySearch");
	    MobclickAgent.onPause(this);
	}
}
