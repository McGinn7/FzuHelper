package com.west2.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Course implements Serializable {
	private String jie,day,week,place,courseName,teacherName,schedule;
	public void setJie(String jie) {
		this.jie = jie;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public void setSchedule(String schedule){
		this.schedule = schedule;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getJie() {
		return jie;
	}
	public String getDay() {
		return day;
	}
	public String getWeek() {
		return week;
	}
	public String getPlace() {
		return place;
	}
	public String getSchedule(){
		return schedule;
	}
	public String getCourseName() {
		return courseName;
	}
	public String getTeacherName() {
		return teacherName;
	}
}
