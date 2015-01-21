package com.west2.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Exam {
	private String courseName,teacherName,date,time,room;
	private String year,month,day,maxHour,maxMinute;
	public void setCourseName(String coursename){
		this.courseName=coursename;
	}
	public void setTeacherName(String teachername){
		this.teacherName=teachername;
	}
	public void setDate(String date){
		this.date=date;
		Matcher m;
		// 匹配年
		Pattern patYear = Pattern.compile("[0-9]+年");
		m = patYear.matcher(date);
		if(m.find()){
			year = m.group().toString();
			year = year.replace("年", "");
		}
		else
			year = "0";
		//匹配月
		Pattern patMonth = Pattern.compile("[0-9]+月");
		m = patMonth.matcher(date);
		if(m.find()){
			month = m.group().toString();
			month = month.replace("月", "");
		}
		else
			month = "0";
		//匹配日
		Pattern patDay = Pattern.compile("[0-9]+日");
		m = patDay.matcher(date);
		if(m.find()){
			day = m.group().toString();
			day = day.replace("日", "");
		}
		else
			day = "0";
	}
	public void setTime(String time){
		this.time=time;
		// 匹配最大时间
		String maxTime;
		Pattern patMaxTime = Pattern.compile("-[0-9]+:[0-9]+");
		Matcher matcher = patMaxTime.matcher(time);
		if(matcher.find()){
			maxTime = matcher.group().toString();
			Matcher m;
			// 匹配时
			Pattern patHour = Pattern.compile("-[0-9]+");
			m = patHour.matcher(maxTime);
			if(m.find()){
				maxHour = m.group().toString();
				maxHour = maxHour.replace("-", "");
			}
			else
				maxHour = "0";
			// 匹配分
			Pattern patMinute = Pattern.compile(":[0-9]+");
			m = patMinute.matcher(maxTime);
			if(m.find()){
				maxMinute = m.group().toString();
				maxMinute = maxMinute.replace(":", "");
			}
			else
				maxMinute = "0";
		}
	}
	public void setRoom(String room){
		this.room=room;
	}
	public String getCourseName(){
		return this.courseName;
	}
	public String getTeacherName(){
		return this.teacherName;
	}
	public String getDate(){
		return this.date;
	}
	public String getTime(){
		return this.time;
	}
	public String getRoom(){
		return this.room;
	}
	public String getYear(){
		return this.year;
	}
	public String getMonth(){
		return this.month;
	}
	public String getDay(){
		return this.day;
	}
	public String getHour(){
		return this.maxHour;
	}
	public String getMinute(){
		return this.maxMinute;
	}
}
