package com.west2.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.west2.main.R;
import com.west2.utils.HttpUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.Time;
import android.util.Log;

public class DateService {
	private final String SHARED_DATE = "date_and_time";
	private Context context;
	private SharedPreferences dateShare;
	public DateService(Context mContext){
		context = mContext.getApplicationContext();
		dateShare = mContext.getSharedPreferences(SHARED_DATE, Context.MODE_PRIVATE);
	}
	public boolean queryDate(){
		String url = context.getResources().getString(R.string.url_date);
		String res = HttpUtils.getData(url);
		if(res==null) return false;
		try{
			JSONObject json = new JSONObject(res);
			if(json==null) return false;
			String week = json.getString("week");
			String date = json.getString("date");
			setWeek(week);
			setDate(date);
			Matcher matcher;
			Pattern patWeek = Pattern.compile("星期[\u4E00-\u9FA5]+");
			Pattern patDay = Pattern.compile("[0-9]+日");
			Pattern patMonth = Pattern.compile("[0-9]+月");
			Pattern patYear = Pattern.compile("[0-9]+年");
			matcher = patWeek.matcher(date);
			if(matcher.find()) setWeekDay(matcher.group().toString());
			matcher = patDay.matcher(date);
			if(matcher.find()) setDay(matcher.group().toString());
			matcher = patMonth.matcher(date);
			if(matcher.find()) setMonth(matcher.group().toString());
			matcher = patYear.matcher(date);
			if(matcher.find()) setYear(matcher.group().toString());
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public String getHour(){
		Time t=new Time("GMT+8");
		return "" + t.hour;
	}
	public String getMinute(){
		Time t=new Time("GMT+8");
		return "" + t.minute;
	}
	public String getWeek(){
		return dateShare.getString("week", "");
	}
	public String getDate(){
		return dateShare.getString("date", getLocalDate());
	}
	public String getWeekDay(){
		return dateShare.getString("weekday", "");
	}
	public String getNumberWeekDay(){
		String week = getWeekDay();
		if(week.contains("一")) return "1";
		if(week.contains("二")) return "2";
		if(week.contains("三")) return "3";
		if(week.contains("四")) return "4";
		if(week.contains("五")) return "5";
		if(week.contains("六")) return "6";
		return "7";
	}
	public String getDay(){
		return dateShare.getString("day", "");		
	}
	public String getMonth(){
		return dateShare.getString("month", "");
	}
	public String getYear(){
		return dateShare.getString("year", "");
	}
	public int getDayOfMonth(int year,int month){
		if(month<0 || month>12) return 0;
		if(month==2){
			if(year%400==0 || year%4==0 && year%100!=0) return 29;
			return 28;
		}
		if(month==4 || month==6 || month==9 || month==11) return 30;
		return 31;
	}
	public void setWeek(String week){
		if(week==null || week.equals("")) return ;
		Editor editor = dateShare.edit();
		editor.putString("week", week);
		editor.commit();
	}
	public void setDate(String date){
		if(date==null || date.equals("")) return ;
		Editor editor = dateShare.edit();
		editor.putString("date", date);
		editor.commit();		
	}
	public void setWeekDay(String weekday){
		if(weekday==null || weekday.equals("")) return ;
		Editor editor = dateShare.edit();
		editor.putString("weekday", weekday);
		editor.commit();
	}
	public void setDay(String day){
		if(day==null || day.equals("")) return ;
		if(day.contains("日")) day = day.replace("日", "");
		Editor editor = dateShare.edit();
		editor.putString("day", day);
		editor.commit();
	}
	public void setMonth(String month){
		if(month==null || month.equals("")) return ;
		if(month.contains("月")) month = month.replace("月", "");
		Editor editor = dateShare.edit();
		editor.putString("month", month);
		editor.commit();		
	}
	public void setYear(String year){
		if(year==null || year.equals("")) return ;
		if(year.contains("年")) year = year.replace("年", "");
		Editor editor = dateShare.edit();
		editor.putString("year", year);
		editor.commit();
	}
	public String getDayOrNight(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int hour=calendar.get(Calendar.HOUR_OF_DAY);
		if(hour<6) return "夜深了";
		if(hour<12) return "早上好";
		if(hour<18) return "下午好";
		return "晚上好";
	}
	public String getLocalDate(){
		StringBuilder res = new StringBuilder();
		SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日");
		String date = format.format(new java.util.Date());
		res.append(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		switch(calendar.get(Calendar.DAY_OF_WEEK)){
			case 1:res.append("星期日");break;
			case 2:res.append("星期一");break;
			case 3:res.append("星期二");break;
			case 4:res.append("星期三");break;
			case 5:res.append("星期四");break;
			case 6:res.append("星期五");break;
			case 7:res.append("星期六");break;
		}
		return res.toString();
	}
}
