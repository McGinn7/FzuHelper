package com.west2.view.life;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.west2.main.R;
import com.west2.service.MarketService;
import com.west2.utils.LoadingDialog;

public class MarketDetailActivity extends Activity {
	private String url;
	private String html;
	private WebView mWebView;
	private LoadingDialog loding;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_market_detail);
		findView();
		initValue();
	}

	private void findView(){
		mWebView = (WebView)findViewById(R.id.market_webview);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		Intent intent = this.getIntent();
		url = intent.getStringExtra("url")+"&mobile=2";
		loding = new LoadingDialog(this);
		WebSettings settings= mWebView.getSettings(); 
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		loding.show();
        new GetHtmlTask().execute();
	}
	/*
	 * 网络获取数据
	 */
	private class GetHtmlTask extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			html = MarketService.getMarketHtml(MarketDetailActivity.this,url);
			return html;
		}
		@Override
		protected void onPostExecute(String result) {
			if(loding.isShowing())loding.cancel();
			if(html==null||html.length()==0){
				Toast.makeText(MarketDetailActivity.this, R.string.net_delay, Toast.LENGTH_SHORT);
			}else{
				mWebView.loadDataWithBaseURL("", html, "text/html", "utf-8", "");
			}
			super.onPostExecute(result);
		}
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("MarketDetail");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("MarketDetail");
	    MobclickAgent.onPause(this);
	}
}
