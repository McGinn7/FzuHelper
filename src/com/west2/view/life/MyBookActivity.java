package com.west2.view.life;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.MyBookAdapter;
import com.west2.domain.MyBook;
import com.west2.main.R;
import com.west2.service.LibraryService;

public class MyBookActivity extends Activity{
	private ListView listBook;
	private Handler uiHandler;
	private List<MyBook> cntList;
	private MyBookAdapter mAdapter;
	private LibraryService libService;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_mybook);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		listBook = (ListView)findViewById(R.id.mybook_pull_refresh_list);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		libService = new LibraryService(this);
		cntList = new ArrayList<MyBook>();
		mAdapter = new MyBookAdapter(this,cntList);
		listBook.setAdapter(mAdapter);
		uiHandler = new Handler();
		uiHandler.post(getBookFromDb);
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		listBook.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
	}
	/*
	 * 数据库调用图书信息
	 */
	Runnable getBookFromDb = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<MyBook> list = libService.getMyBook();
			if(list!=null){
				for(int i=0;i<list.size();++i){
					cntList.add(list.get(i));
				}
				mAdapter.notifyDataSetChanged();
			}
		}
	};
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		if(KeyCode==KeyEvent.KEYCODE_BACK){
			this.finish();
		}
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("MyBook");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("MyBook");
	    MobclickAgent.onPause(this);
	}
}
