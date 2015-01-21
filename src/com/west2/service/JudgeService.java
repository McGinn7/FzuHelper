package com.west2.service;

import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;

import com.west2.domain.Judge;
import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class JudgeService {
	private Context context;
	public JudgeService(Context mContext){
		context = mContext.getApplicationContext();
	}
	public Judge queryJudge(String username,String password){
		password = new String(Base64.encode(password.getBytes(), Base64.DEFAULT));
		String url = context.getResources().getString(R.string.url_judge_teacher)
				+"num="+username+"&pass="+password;
		String res = HttpUtils.getData(url);
		if(res==null) return null;
		try{
			JSONObject json = new JSONObject(res);
			if(!json.getBoolean("status")) return null;
			JSONObject item = json.getJSONObject("date");
			Judge judge = new Judge();
			judge.setRuntime(item.getString("runtime"));
			judge.setContent(item.getString("content"));
			return judge;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
