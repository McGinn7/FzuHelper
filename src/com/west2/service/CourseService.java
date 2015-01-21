package com.west2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Log;

import com.west2.db.DbCourse;
import com.west2.domain.Course;
import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class CourseService {
	private final String SHARE_WEEK = "week";
	private Activity activity;
	private SharedPreferences share_week;
	private Comparator cmp = new Comparator(){
		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			String s0 = (String) arg0;
			String s1 = (String) arg1;
			int w1 = Integer.parseInt(s0);
			int w2 = Integer.parseInt(s1);
			if(w1<w2) return -1;
			return 1;
		}
	};
	public CourseService(Activity activity){
		this.activity = activity;
		share_week = activity.getSharedPreferences(SHARE_WEEK, activity.MODE_PRIVATE);
	}
	public String getSaveWeek(){
		String week = share_week.getString("week", "");
		if(week==null || week.equals(""))
		return new DateService(activity).getWeek();
		return week;
	}
	public void saveWeek(String week){
		if(week==null || week.equals("")) return ;
		Editor editor = share_week.edit();
		editor.putString("week", week);
		editor.commit();
	}
	public List<String> getCourseList(){
		List<String> list = new ArrayList<String>();
		DbCourse courseHelper = new DbCourse(activity);
		courseHelper.open();
		Cursor cursor = courseHelper.getCourseList();
		while(cursor.moveToNext()){
			list.add(cursor.getString(0));
		}
		cursor.close();
		courseHelper.close();
		return list;
	}
	public List<String> getWeekList(){
		List<String> list = new ArrayList<String>();
		DbCourse courseHelper = new DbCourse(activity);
		courseHelper.open();
		Cursor cursor = courseHelper.getWeekList();
		while(cursor.moveToNext()){
			String week = cursor.getString(0);
			if(!list.contains(week)) list.add(week);
		}
		cursor.close();
		courseHelper.close();
		Collections.sort(list,cmp);
		return list;
	}
	public String getSchedule(String coursename){
		DbCourse courseHelper = new DbCourse(activity);
		courseHelper.open();
		Cursor cursor = courseHelper.getSchedule(coursename);
		List<String> list = new ArrayList<String>();
		while(cursor.moveToNext()){
			String schedule = cursor.getString(0);
			list.add(schedule);
		}
		cursor.close();
		courseHelper.close();
		if(list.size()==0) return " ";
		return list.get(0);
	}
	public void saveCourse(List<Course> list,List<String> clist,List<String> minList,List<String> maxList){
		if(list==null || list.size()==0) return ;
		DbCourse courseHelper = new DbCourse(activity);
		courseHelper.open();
		courseHelper.dropCourse();
		courseHelper.insertCourse(list);
		courseHelper.insertCourse2(clist,minList,maxList);
		courseHelper.close();
	}
	public List<Course> getCourse(String week){
		if(week==null || week.equals("")) return null;
		DbCourse courseHelper = new DbCourse(activity);
		courseHelper.open();
		List<Course> list = new ArrayList<Course>();
		Cursor cursor = courseHelper.getCourseOfWeek(week);
		while(cursor.moveToNext()){
			Course course = new Course();
			course.setCourseName(cursor.getString(0));
			course.setPlace(cursor.getString(1));
			course.setTeacherName(cursor.getString(2));
			course.setWeek(cursor.getString(3));
			course.setDay(cursor.getString(4));
			course.setJie(cursor.getString(5));
			list.add(course);
		}
		cursor.close();
		courseHelper.close();
		return list;
	}
	public List<Course> getCourseInDay(String cntWeekDay){
		String week = getSaveWeek();
		if(week==null || week.equals("")) return null;
		DbCourse courseHelper = new DbCourse(activity);
		courseHelper.open();
		List<Course> list = new ArrayList<Course>();
		Cursor cursor = courseHelper.getCourseOfWeek(week);
		while(cursor.moveToNext()){
			Course course = new Course();
			course.setCourseName(cursor.getString(0));
			course.setPlace(cursor.getString(1));
			course.setTeacherName(cursor.getString(2));
			course.setWeek(cursor.getString(3));
			course.setDay(cursor.getString(4));
			course.setJie(cursor.getString(5));
			if(course.getDay().equals(cntWeekDay)){
				list.add(course);
			}
		}
		cursor.close();
		courseHelper.close();
		return list;
	}
	public boolean queryCourse(String userNumber){
		String url = activity.getResources().getString(R.string.url_course) 
			+ "num="+userNumber;
		String res = HttpUtils.getData(url);
		if(res==null) return false;
		List<Course> list = new ArrayList<Course>();
		List<String> clist = new ArrayList<String>();
		List<String> minList = new ArrayList<String>();
		List<String> maxList = new ArrayList<String>();
		Pattern pattern = Pattern.compile("[0-9]{1,2}");
		String week = "";
		try{
			JSONObject json = new JSONObject(res);
			if(json==null) return false;
			JSONArray weekArray = json.getJSONArray("WeekArr");
			for(int i=0;i<weekArray.length();++i){
				JSONObject item = weekArray.getJSONObject(i);
				Matcher matcher = pattern.matcher(item.getString("week"));
				if(matcher.find()) week = matcher.group().toString();
				if(week==null || week.equals("") || week.equals("0")){
					continue;
				}
				JSONObject courseItem = item.getJSONObject("courseArr");
				for(int j=1;j<=7;++j){
					JSONObject dayItem = courseItem.getJSONObject(""+j);
					for(int k=1;k<=5;++k){
						JSONObject jieItem = dayItem.getJSONObject(""+k);
						Course course = new Course();
						course.setWeek(week);
						course.setDay(""+j);
						course.setJie(jieItem.getString("jie"));
						course.setCourseName(jieItem.getString("courseName"));
						course.setPlace(jieItem.getString("place"));
						course.setTeacherName(jieItem.getString("teacherName"));
						list.add(course);
						if(!clist.contains(jieItem.getString("courseName"))){
							clist.add(jieItem.getString("courseName"));
							minList.add(""+week);
							maxList.add(""+week);
						}
						else{
							maxList.set(clist.indexOf(jieItem.getString("courseName")), ""+week);
						}
					}
				}
			}
			if(list.size()<=0) return false;
			saveCourse(list,clist,minList,maxList);
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}