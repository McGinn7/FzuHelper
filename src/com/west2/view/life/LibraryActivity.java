package com.west2.view.life;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.west2.main.R;
import com.west2.service.LibraryService;
import com.west2.service.ViewService;
import com.west2.utils.LoadingDialog;

public class LibraryActivity extends Activity{
	// 服务
	private LibraryService libService;
	// 控件
	private EditText username,password,editSearch;
	private Button menuLogin,btnLogin,btnSearch;
	private ImageButton btnDelUsername,btnDelPassword;
	private LinearLayout viewSearch,viewLogin;
	private LoadingDialog loading;
	
	private Thread libLoginThread;
	private int cntPage = 0;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_library);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		menuLogin=(Button)findViewById(R.id.menu_login);
		btnLogin=(Button)findViewById(R.id.button_login);
		btnSearch=(Button)findViewById(R.id.button_search);
		viewLogin=(LinearLayout)findViewById(R.id.view_login);
		viewSearch=(LinearLayout)findViewById(R.id.view_search);
		username=(EditText)findViewById(R.id.edittext_username);
		password=(EditText)findViewById(R.id.edittext_password);
		editSearch=(EditText)findViewById(R.id.edittext_search);
		btnDelUsername=(ImageButton)findViewById(R.id.button_del_username);
		btnDelPassword=(ImageButton)findViewById(R.id.button_del_password);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		loading = new LoadingDialog(this);
		libService = new LibraryService(this);
		username.setText(libService.getUsername());
		password.setText(libService.getPassword());
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		btnDelUsername.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				username.setText("");
			}
		});
		btnDelPassword.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				password.setText("");
			}
		});
		btnLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				libService.setUsername(username.getText().toString());
				libService.setPassword(password.getText().toString());
				loading.show();
				if(libLoginThread==null || !libLoginThread.isAlive()){
					libLoginThread = new Thread(loginThread);
					libLoginThread.start();
				}
			}
		});
		btnSearch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String key = editSearch.getText().toString().replaceAll(" ", "").replaceAll("\n", "");
				if(key.equals("")){
					Toast.makeText(LibraryActivity.this, R.string.lib_hint, 1000).show();
					return ;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable("key", key);
				Intent intent = new Intent(LibraryActivity.this,LibrarySearchActivity.class);
				intent.putExtras(bundle);
				LibraryActivity.this.startActivity(intent);
			}
		});
		menuLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changePage();
			}
		});
		username.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1)
					btnDelUsername.setVisibility(View.VISIBLE);
				else
					btnDelUsername.setVisibility(View.GONE);
			}
		});
		password.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1)
					btnDelPassword.setVisibility(View.VISIBLE);
				else
					btnDelPassword.setVisibility(View.GONE);
			}
		});
	}
	/*
	 * 处理登录返回结果
	 */
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			if(loading.isShowing()) loading.cancel();
			else return ;
			libService.setUsername("");
			libService.setPassword("");
			switch(msg.what){
			case 0:
				Toast.makeText(LibraryActivity.this,R.string.net_error_library,Toast.LENGTH_SHORT).show();
				break;
			case 1:
				// 保存用户信息
				libService.setUsername(username.getText().toString());
				libService.setPassword(password.getText().toString());
				
				Intent intent = new Intent(LibraryActivity.this,MyBookActivity.class);
				LibraryActivity.this.startActivity(intent);
				break;
			case -1:Toast.makeText(LibraryActivity.this,
					getResources().getString(R.string.login_username_null),
					Toast.LENGTH_SHORT).show();
				break;
			case -2:Toast.makeText(LibraryActivity.this,
					getResources().getString(R.string.login_password_null),
					Toast.LENGTH_SHORT).show();
				break;
			case -3:
			case -4:Toast.makeText(LibraryActivity.this,
					getResources().getString(R.string.net_error_library),
					Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	/*
	 * 登录线程
	 */
	Runnable loginThread = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = handler.obtainMessage();
			msg.what = libService.login();
 			handler.sendMessage(msg);
		}
	};
	/*
	 * 登录、检索页面切换
	 */
	private void changePage(){
		if(cntPage==0){
			viewLogin.startAnimation(setAnim(R.anim.left_in));
			viewSearch.startAnimation(setAnim(R.anim.left_out));

			viewSearch.setVisibility(View.GONE);
			viewLogin.setVisibility(View.VISIBLE);
		
			editSearch.setVisibility(View.GONE);
			btnSearch.setVisibility(View.GONE);
			username.setVisibility(View.VISIBLE);
			password.setVisibility(View.VISIBLE);
			btnLogin.setVisibility(View.VISIBLE);
			
			menuLogin.setText("检索");
		}
		else{
			viewLogin.startAnimation(setAnim(R.anim.right_out));
			viewSearch.startAnimation(setAnim(R.anim.right_in));

			viewSearch.setVisibility(View.VISIBLE);
			viewLogin.setVisibility(View.GONE);
		
			editSearch.setVisibility(View.VISIBLE);
			btnSearch.setVisibility(View.VISIBLE);
			username.setVisibility(View.GONE);
			password.setVisibility(View.GONE);
			btnLogin.setVisibility(View.GONE);					
			
			menuLogin.setText("登录");
		}
		cntPage=1-cntPage;
	}
	private Animation setAnim(int id){
		Animation mAnim = AnimationUtils.loadAnimation(this, id);
		mAnim.setFillAfter(true);
		return mAnim;
	}
	
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		if(KeyCode==KeyEvent.KEYCODE_BACK){
			if(cntPage==1){
				changePage();
			}
			else{
				this.finish();
				new ViewService(this).changeActivityAnim(this, 1);
			}
		}
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Library");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Library");
	    MobclickAgent.onPause(this);
	}
}