package com.west2.view.study;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.CourseListAdapter;
import com.west2.custom.adapter.ViewPagerAdapter;
import com.west2.domain.Course;
import com.west2.main.R;
import com.west2.service.CourseService;
import com.west2.service.DateService;

public class CourseDayActivity extends Activity{

	private Button btnChange;
	private static Context context;
	private static ViewPager viewpager;
	private static DateService dateService;
	private static CourseService courseService;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_course_day);

		findView();
		initValue();
		setListener();
	}
	private void findView(){
		viewpager = (ViewPager)findViewById(R.id.course_day_viewpager);
		btnChange = (Button) findViewById(R.id.day_button_change);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		courseService = new CourseService(this);
		dateService = new DateService(this);
		context = this.getApplicationContext();
		reCourse();
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		btnChange.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CourseSetActivity.changePage(0);
			}
		});
		viewpager.setOnPageChangeListener(new OnPageChangeListener(){
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				CourseSetActivity.setWeekDayBackground(position);
			}
		});
	}
	/*
	 * 页面切换
	 */
	public static void changePage(int position){
		viewpager.setCurrentItem(position);
	}
	/*
	 * 更新课程
	 */
	public static void reCourse(){
		List<View> views = new ArrayList<View>();
		for(int i=1;i<=7;++i){
			ListView listview = new ListView(context);
			List<Course> list = courseService.getCourseInDay(i+"");
			listview.setAdapter(new CourseListAdapter(context,list));
			views.add(listview);
		}
		ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(views);
		viewpager.setAdapter(mViewPagerAdapter);
		viewpager.setCurrentItem(Integer.parseInt(dateService.getNumberWeekDay())-1);
	}
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("CourseDay");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("CourseDay");
	    MobclickAgent.onPause(this);
	}
}
