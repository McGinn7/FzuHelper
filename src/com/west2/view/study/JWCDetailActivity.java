package com.west2.view.study;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.west2.main.R;
import com.west2.service.JWCNoticeService;
import com.west2.utils.LoadingDialog;

public class JWCDetailActivity extends Activity {

	private String url;
	private String html;
	private WebView mWebView;
	private LoadingDialog loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_jwcdetail);
		
		findView();
		initValue();
	}
	private void findView(){
		mWebView = (WebView)findViewById(R.id.jwc_webview);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		Intent intent = this.getIntent();
		url = intent.getStringExtra("url");
		loading = new LoadingDialog(this);
		loading.show();
        new GetHtmlTask().execute();
	}
	/*
	 * 网络获取详细信息
	 */
	private class GetHtmlTask extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			html = JWCNoticeService.getDetailFromUrl(url);
			return html;
		}
		@Override
		protected void onPostExecute(String result) {
			if(loading.isShowing()) loading.cancel();
			if(html==null||html.length()==0){
				Toast.makeText(JWCDetailActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
			}
			else{
				mWebView.loadDataWithBaseURL(url, html, "text/html", "utf-8", url);
			}
			super.onPostExecute(result);
		}
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("JWCDetail");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("JWCDetail");
	    MobclickAgent.onPause(this);
	}
}
