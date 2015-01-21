package com.west2.service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class RefreshService {
	public static final String SHARED_REFRESH = "refresh";

		private static Context context;
		private static SharedPreferences preference;
		public RefreshService(Activity activity){
			this.context = activity.getApplicationContext();
			preference = context.getSharedPreferences(SHARED_REFRESH,context.MODE_PRIVATE);
		}
		public boolean getMyBook(){
			return preference.getBoolean("mybook_refresh", true);
		}
		public boolean getCredit(){
			return preference.getBoolean("credit_refresh", true);
		}
		public boolean getDate(){
			return preference.getBoolean("date_refresh", true);
		}
		public boolean getWeahter(){
			return preference.getBoolean("weather_refresh", true);
		}
		public boolean getCourse(){
			return preference.getBoolean("course_refresh", true);
		}
		public boolean getMark(){
			return preference.getBoolean("mark_refresh", true);
		}
		public boolean getExam(){
			return preference.getBoolean("exam_refresh", true);
		}
		public void setMyBook(boolean need){
			Editor editor = preference.edit();
			editor.putBoolean("mybook_refresh", need);
			editor.commit();			
		}
		public void setCredit(boolean need){
			Editor editor = preference.edit();
			editor.putBoolean("credit_refresh", need);
			editor.commit();
		}
		public void setDate(boolean need){
			Editor editor = preference.edit();
			editor.putBoolean("date_refresh", need);
			editor.commit();			
		}
		public void setWeather(boolean need){
			Editor editor = preference.edit();
			editor.putBoolean("weather_refresh", need);
			editor.commit();			
		}
		public void setCourse(boolean need){
			Editor editor = preference.edit();
			editor.putBoolean("course_refresh", need);
			editor.commit();
		}
		public void setMark(boolean need){
			Editor editor = preference.edit();
			editor.putBoolean("mark_refresh", need);
			editor.commit();
		}
		public void setExam(boolean need){
			Editor editor = preference.edit();
			editor.putBoolean("exam_refresh", need);
			editor.commit();
		}
}
