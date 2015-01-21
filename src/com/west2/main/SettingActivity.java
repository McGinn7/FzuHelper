package com.west2.main;

import junit.framework.Assert;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.west2.service.CreditService;
import com.west2.service.LoginService;
import com.west2.start.LoginActivity;

public class SettingActivity extends Activity {
	private LinearLayout FAQLayout, aboutLayout;
	private TextView tv1, tv2, tv3, tv4,tv5;
	private Button btnBack,btnLogout;
	private boolean isOpenFAQ = false;
	private boolean isOpenAbout = false;
	private String appId = "wx8fe92559f5eaf924";
	private String shareUrl;
	private String shareContent;
	private UMImage shareImage ;
	private Handler mHandler;
	private static Context context;
	private static CreditService creService;
	private static LoginService loginService;
	
	//友盟组件
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",RequestType.SOCIAL);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		findView();
		setListener();
		initUm();
	}

	private void findView() {
		// TODO Auto-generated method stub
		
		mHandler = new Handler();
		context = this.getApplicationContext();
		creService = new CreditService(context);
		loginService = new LoginService(context);

		FAQLayout = (LinearLayout) findViewById(R.id.FAQ_content);
		aboutLayout = (LinearLayout) findViewById(R.id.about_content);
		btnBack = (Button) findViewById(R.id.button_back);
		btnLogout = (Button) findViewById(R.id.button_logout);
		tv1 = (TextView) findViewById(R.id.about_1);
		tv2 = (TextView) findViewById(R.id.about_2);
		tv3 = (TextView) findViewById(R.id.about_3);
		tv4 = (TextView) findViewById(R.id.about_4);
		tv5 = (TextView) findViewById(R.id.about_5);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(SettingActivity.this,SetActivity.class);
			SettingActivity.this.startActivity(intent);
			SettingActivity.this.finish();
		} //
		return false;
	}
	private void setListener() {
		// TODO Auto-generated method stub
		tv1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isOpenFAQ) {
					tv1.setText(getResources().getString(R.string.FAQ));
					FAQLayout.setVisibility(View.GONE);
				} else {
					tv1.setText(getResources().getString(R.string.FAQ_hide));
					FAQLayout.setVisibility(View.VISIBLE);
				}
				isOpenFAQ = !isOpenFAQ;
			}
		});
		tv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isOpenAbout) {
					tv2.setText(getResources().getString(R.string.about));
					aboutLayout.setVisibility(View.GONE);
				} else {
					tv2.setText(getResources().getString(R.string.about_hide));
					aboutLayout.setVisibility(View.VISIBLE);
				}
				isOpenAbout = !isOpenAbout;
			}
		});
		tv3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FeedbackAgent agent = new FeedbackAgent(SettingActivity.this);
				agent.startFeedbackActivity();
			}
		});

		tv4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mController.openShare(SettingActivity.this, false);
				// 友盟回调 200成功
				mController.getConfig().registerListener(new SnsPostListener() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
					}
					@Override
					public void onComplete(SHARE_MEDIA arg0, int arg1,SocializeEntity arg2) {
						// TODO Auto-generated method stub
						if (arg1 == 200) {
							
							new ShareTask().execute();
//							mHandler.post(shareThread);
						}
					}
				});
			}
		});
		//手动更新
		tv5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingActivity.this, "检查更新中", Toast.LENGTH_SHORT).show();
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.forceUpdate(SettingActivity.this);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				    @Override
				    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
				        switch (updateStatus) {
				        case UpdateStatus.Yes:
				            UmengUpdateAgent.showUpdateDialog(SettingActivity.this, updateInfo);
				            break;
				        case UpdateStatus.No:
				            Toast.makeText(SettingActivity.this, R.string.updated, Toast.LENGTH_SHORT).show();
				            break;
				        case UpdateStatus.NoneWifi:
				            Toast.makeText(SettingActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
				            break;
				        case UpdateStatus.Timeout:
				            Toast.makeText(SettingActivity.this, "超时", Toast.LENGTH_SHORT).show();
				            break;
				        }
				    }
				});
			}
		});
		btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingActivity.this,SetActivity.class);
				SettingActivity.this.startActivity(intent);
				SettingActivity.this.finish();
			}
		});
		btnLogout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new CreditService(SettingActivity.this).setCredit("");
				new LoginService(SettingActivity.this).setAulogin(false);
				Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
				SettingActivity.this.startActivity(intent);
				SettingActivity.this.finish();
			}
		});
	}
	private void initUm(){
		//分享内容
//		shareUrl="http://3w.west2online.com/Robin/download.html";
//		shareContent=" 福大助手太好用了，作者太帅了，小伙伴们快来一起玩耍吧！   ";
		shareUrl=this.getString(R.string.share_url);
		shareContent=this.getString(R.string.share_content);
		shareImage = new UMImage(this, R.drawable.erweima);
		
		mController.getConfig().removePlatform(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.TENCENT);
		// 以下为添加的社会化组件
		mController.setShareContent("欢迎下载福大助手    " + shareUrl);
//		mController.setShareMedia(new UMediaObject)
		// 设置分享图片，参数2为本地图片的资源引用
		mController.setShareMedia(shareImage);
		mController.setAppWebSite(shareUrl);
		mController.setShareContent(shareContent);

		// 微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this,appId);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(shareContent+shareUrl);
		circleMedia.setTitle("福大助手");
		circleMedia.setShareImage(new UMImage(this, R.drawable.ic_launcher));
		circleMedia.setTargetUrl(shareUrl);
		mController.setShareMedia(circleMedia);
				
		// QQ
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(SettingActivity.this,
				"100424468", "c7394704798a158208a74ab60104f0ba");
		qqSsoHandler.addToSocialSDK();
		QQShareContent qqMedia = new QQShareContent();
		qqMedia.setShareContent(shareContent +shareUrl);
		qqMedia.setTargetUrl(shareUrl);
		qqMedia.setShareImage(shareImage);
		qqMedia.setTitle("福大助手");
		mController.setShareMedia(qqMedia);
		// Qzone
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				SettingActivity.this, "100424468",
				"c7394704798a158208a74ab60104f0ba");
		qZoneSsoHandler.addToSocialSDK();
		
		QZoneShareContent qZoneShareContent = new QZoneShareContent();
		qZoneShareContent.setShareContent(shareContent +shareUrl);
		qZoneShareContent.setTargetUrl(shareUrl);
		qZoneShareContent.setTitle("福大助手");
		qZoneShareContent.setShareImage(shareImage);
		mController.setShareMedia(qZoneShareContent);
		
		SinaShareContent sinaShareContent = new SinaShareContent();
		sinaShareContent.setShareContent(shareContent +shareUrl);
		sinaShareContent.setTargetUrl(shareUrl);
		sinaShareContent.setTitle("福大助手");
		sinaShareContent.setShareImage(shareImage);
		mController.setShareMedia(sinaShareContent);
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	/*
	 * 分享线程
	 */
	Runnable shareThread = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Toast.makeText(SettingActivity.this, "分享成功",Toast.LENGTH_SHORT).show();
			boolean status = creService.signOrShare(loginService.getUsername(), "share");
			if(status){
				Log.e("", "111");
				Toast.makeText(SettingActivity.this, "积分已增加",Toast.LENGTH_SHORT).show();
			}else
				Log.e("", "2222");
			
		}
	};
	
	class ShareTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected void onPostExecute(Boolean result){
			if(result)
				Toast.makeText(SettingActivity.this, "积分已增加",Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(SettingActivity.this, "今天已经分享过了",Toast.LENGTH_SHORT).show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			boolean status = creService.signOrShare(loginService.getUsername(), "share");
			return status;
		}
		
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Setting");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Setting");
	    MobclickAgent.onPause(this);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.gc();
		super.onDestroy();
	}
}
