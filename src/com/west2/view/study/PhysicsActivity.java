package com.west2.view.study;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.west2.main.R;
import com.west2.utils.LoadingDialog;

public class PhysicsActivity extends Activity{
	private String url;
	private String html;
	private WebView mWebview;
	private LoadingDialog loading;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_physics);
		
		findView();
		initValue();
	}
	private void findView(){
		mWebview = (WebView)findViewById(R.id.webview);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		url=this.getString(R.string.url_physics);
		loading = new LoadingDialog(this);
		loading.show();
		new GetHtmlTask().execute();
		mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                    view.loadUrl(url);
                    return false;
            }
		});
	}
	/*
	 * 网络获取html代码
	 */
	private class GetHtmlTask extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			html = com.west2.utils.HttpUtils.getData(url);
			return html;
		}
		@Override
		protected void onPostExecute(String result) {
			if(loading.isShowing()) loading.cancel();
			if(html==null||html.length()==0){
				Toast.makeText(PhysicsActivity.this, R.string.net_error, Toast.LENGTH_SHORT);
			}
			else{
				mWebview.loadDataWithBaseURL(url, html, "text/html", "utf-8", url);
			}
			super.onPostExecute(result);
		}
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Physics");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Physics");
	    MobclickAgent.onPause(this);
	}
}
