package com.west2.service;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class CreditService {
	private Context context;
	private final String SHARED_CREDIT = "credit";
	private SharedPreferences creditShare;
	public CreditService(Context context){
		this.context = context;
		creditShare = context.getSharedPreferences(SHARED_CREDIT,context.MODE_PRIVATE);
	}
	public void setCredit(String credit){
		if(credit==null) return ;
		Editor editor = creditShare.edit();
		editor.putString("mycredit", credit);
		editor.commit();
	}
	public void setLastCount(String lastcount){
		if(lastcount==null) return ;
		Editor editor = creditShare.edit();
		editor.putString("lastcount",lastcount);
		editor.commit();
	}
	public String getCredit(){
		return creditShare.getString("mycredit", "");
	}
	public String getLastCount(){
		return creditShare.getString("lastcount", "");
	}
	public void queryCredit(String username){
		String url = context.getResources().getString(R.string.url_get_credit)
				+"num/"+username;
		String res = HttpUtils.getData(url);
		if(res==null) return ;
		try {
			JSONObject json = new JSONObject(res);
			JSONObject item = json.getJSONObject("date");
			String credit = item.getString("credit");
			if(credit!=null) setCredit(credit);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean signOrShare(String username,String action){
		if(username==null || username.equals("")) return false;
		String url = context.getResources().getString(R.string.url_sign_or_share)
				+"num/"+username+"/action/"+action;
		
		String res = HttpUtils.getData(url);
		
		if(res==null) return false;
		try {
			JSONObject json = new JSONObject(res);
			boolean status = false;
			status = json.getBoolean("status");
			if(status){
				JSONObject date = json.getJSONObject("date");
				String lastcount = date.getString("lastCount");
				String credit = date.getString("credit");
				setLastCount(lastcount);
				setCredit(credit);
			}
			else{
				setLastCount("");
				queryCredit(username);
			}
			return status;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
