package com.west2.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.west2.main.R;
import com.west2.main.SetActivity;
import com.west2.service.CreditService;
import com.west2.service.LoginService;
import com.west2.service.RefreshService;
import com.west2.utils.LoadingDialog;

public class LoginActivity extends Activity{
	private Thread loginThread;
	private LoadingDialog loading;
	private LoginService loginService;
	
	private Button btnVisit,btnLogin;
	private EditText userNameText,passwordText;
	private ImageButton deleteUsernameButton,deletePasswordButton;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		findView();
		initValue();
		setListener();
	}
	private void findView(){
		btnLogin=(Button)findViewById(R.id.button_login);
		btnVisit=(Button)findViewById(R.id.button_visit);
		userNameText=(EditText)findViewById(R.id.login_username_editext);
		passwordText=(EditText)findViewById(R.id.login_password_editext);
		deleteUsernameButton=(ImageButton)findViewById(R.id.delete_username_button);
		deletePasswordButton=(ImageButton)findViewById(R.id.delete_password_button);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		loading = new LoadingDialog(this);
		loginService = new LoginService(this);
		String username = loginService.getUsername();
		String password = loginService.getPassword();
		if(username!=null && !username.equals("")) userNameText.setText(username);
		if(password!=null && !password.equals("")) passwordText.setText(password);
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		//游客浏览
		btnVisit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loginService.setUsername("");
				loginService.setPassword("");
				loginService.setRealname("");
				new CreditService(LoginActivity.this).setCredit("");
				Toast.makeText(LoginActivity.this, R.string.visit_toast, 2000).show();
				Intent intent = new Intent(LoginActivity.this,SetActivity.class);
				LoginActivity.this.startActivity(intent);
				LoginActivity.this.finish();
			}
		});
		//登录按钮
		btnLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String username = userNameText.getText().toString();
				String password = passwordText.getText().toString();
				loginService.setUsername(username);
				loginService.setPassword(password);
				loading.show();
				if(loginThread==null || !loginThread.isAlive()){
					loginThread = new Thread(getData);
					loginThread.start();
				}
			}
		});
		//账号输入框
		userNameText.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					deleteUsernameButton.setVisibility(View.VISIBLE);
				}else{
					deleteUsernameButton.setVisibility(View.INVISIBLE);					
				}
			}
		});
		//密码输入框
		passwordText.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					deletePasswordButton.setVisibility(View.VISIBLE);
				}
				else{
					deletePasswordButton.setVisibility(View.INVISIBLE);
				}
			}			
		});
		//删除账号按钮
		deleteUsernameButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				userNameText.setText("");
			}
		});
		//删除密码按钮
		deletePasswordButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				passwordText.setText("");
			}
		});
	}
	/*
	 * 处理登录判定
	 */
	private Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			if(loading.isShowing()) loading.cancel(); else return ;
			if(msg.what != 1) loginService.setAulogin(false);
			switch(msg.what){
			case 0:	Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.net_error),
					Toast.LENGTH_SHORT).show();
				break;
			case 1:
				// 保存用户信息
				loginService.setUsername(userNameText.getText().toString());
				loginService.setPassword(passwordText.getText().toString());
				loginService.setAulogin(true);

				// 设置更新提醒
				RefreshService refreshService = new RefreshService(LoginActivity.this);
				refreshService.setCourse(true);
				refreshService.setExam(true);
				refreshService.setMark(true);
				refreshService.setCredit(true);

				Intent intent = new Intent(LoginActivity.this,SetActivity.class);
				LoginActivity.this.startActivity(intent);
				LoginActivity.this.finish();
				break;
			case -1:Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.login_username_null),
					Toast.LENGTH_SHORT).show();
				break;
			case -2:Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.login_password_null),
					Toast.LENGTH_SHORT).show();
				break;
			case -3:
			case -4:Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.login_contain_error),
					Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	/*
	 * 登录线程
	 */
	Runnable getData = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = dataHandler.obtainMessage();
			msg.what = loginService.login();
			dataHandler.sendMessage(msg);
			loginService.setUsername("");
			loginService.setPassword("");
		}
	};
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Login");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Login");
	    MobclickAgent.onPause(this);
	}
}
