package com.west2.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class LoginService {
	public static final String SHARED_FIRST_NAME="first_pref";
	public static final String SHARED_USER_INFO="user";

	//判断是否是首次启动
	private static Context context;
	private static SharedPreferences preference;
	private static SharedPreferences userPreference;
	public LoginService(Context context){
		this.context= context;
		preference = context.getSharedPreferences(SHARED_FIRST_NAME,context.MODE_PRIVATE);
		userPreference = context.getSharedPreferences(SHARED_USER_INFO,context.MODE_PRIVATE);
	}
	/* login
	 *  0:网络错误
	 *  1:成功
	 * -1:用户名为空
	 * -2:密码为空
	 * -3:用户名或密码包含非法字符
	 * -4:用户名或密码错误
	 */
	public int login(){
		String username = getUsername();
		if(username==null || username.equals("")) return -1;
		String password = getPassword();
		if(password==null || password.equals("")) return -2;
		if(username.contains(" ") || password.contains(" ")) return -3;
		//加密
		password = new String(Base64.encode(password.getBytes(), Base64.DEFAULT));
		String url = context.getResources().getString(R.string.url_login)
				+"num="+username+"&pass="+password;
		String res = HttpUtils.getData(url);
		if(res==null) return 0;
		try {
			JSONObject json = new JSONObject(res);
			if(json==null) return 0;
			String state = json.getString("state");
			if(state==null || state.equals("false")) return -4;
			setRealname(json.getString("userName"));
			return 1;
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}
	public boolean getAutologin(){
		return userPreference.getBoolean("isAutoLogin", false);
	}
	public String getUsername(){
		return userPreference.getString("username", "");
	}
	public String getPassword(){
		return userPreference.getString("password", "");
	}
	public String getRaelname(){
		return userPreference.getString("realname", "");
	}
	public boolean getFirstIn(){
		return preference.getBoolean("isFirstIn", true);
	}
	public void setAulogin(boolean auto){
		Editor editor = userPreference.edit();
		editor.putBoolean("isAutoLogin", auto);
		editor.commit();
	}
	public void setUsername(String name){
		Editor editor = userPreference.edit();
		editor.putString("username", name);
		editor.commit();
	}
	public void setPassword(String password){
		Editor editor = userPreference.edit();
		editor.putString("password", password);
		editor.commit();
	}
	public void setRealname(String realname){
		Editor editor = userPreference.edit();
		editor.putString("realname", realname);
		editor.commit();
	}
	public void setFirstIn(boolean isFirst){
		Editor editor= preference.edit();
		editor.putBoolean("isFirstIn",isFirst);
		editor.commit();
	}
}
