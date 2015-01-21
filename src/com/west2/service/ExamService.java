package com.west2.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.util.Base64;

import com.west2.db.DbExam;
import com.west2.domain.Exam;
import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class ExamService {
	private Activity activity;

	public ExamService(Activity activity){
		this.activity = activity;
	}
	public void saveExam(List<Exam> list){
		DbExam helper = new DbExam(activity);
		helper.open();
		helper.dropExam();
		helper.insertExam(list);
		helper.close();
	}
	public List<Exam> getExam(){
		DbExam helper = new DbExam(activity);
		helper.open();
		Cursor cursor = helper.getExam();
		Exam exam;
		List<Exam> list = new ArrayList<Exam>();
		while(cursor.moveToNext()){
			exam = new Exam();
			exam.setCourseName(cursor.getString(0));
			exam.setTeacherName(cursor.getString(1));
			exam.setDate(cursor.getString(2));
			exam.setTime(cursor.getString(3));
			exam.setRoom(cursor.getString(4));
			list.add(exam);
		}
		cursor.close();
		helper.close();
		return list;
	}
	public boolean queryExam(){
		String url = activity.getResources().getString(R.string.url_exam)
				+"num="+new LoginService(activity).getUsername()+"&pass=";
		String pass = new String (Base64.encode(new LoginService(activity).getPassword().getBytes(), Base64.DEFAULT));
		url+=pass;
		String res = HttpUtils.getData(url);
		if(res == null) return false;
		try {
			JSONObject json = new JSONObject(res);
			JSONArray examArray = json.getJSONArray("examArr");
			if(examArray==null) return false;
			List<Exam> resList = new ArrayList<Exam>(examArray.length());
			Exam exam;
			JSONObject item;
			// 获取未来天气
			for(int i=0;i<examArray.length();++i){
				item = examArray.getJSONObject(i);
				exam = new Exam();
				exam.setCourseName(item.getString("courseName"));
				exam.setTeacherName(item.getString("teacherName"));
				exam.setDate(item.getString("examDate"));
				exam.setTime(item.getString("examTime"));
				exam.setRoom(item.getString("examRoom"));
				resList.add(exam);
			}
			if(resList.size()<=0) return false;
			saveExam(resList);
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}