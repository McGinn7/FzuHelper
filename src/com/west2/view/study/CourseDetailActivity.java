package com.west2.view.study;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.west2.domain.Course;
import com.west2.main.R;

public class CourseDetailActivity extends Activity{
	private String jieArray[]={"1-2节","3-4节","5-6节","7-8节","9-10节"};
	private String weekDayArray[]={"周一","周二","周三","周四","周五","周六","周日"};
	private int bgColor[] = {R.color.blue,
			R.color.course_color1,R.color.course_color2,R.color.course_color3,R.color.course_color4,
			R.color.course_color5,R.color.course_color6,R.color.course_color7,R.color.course_color8,
			R.color.course_color9,R.color.course_color10,R.color.course_color11,R.color.course_color12,
			R.color.course_color13,R.color.course_color14,R.color.course_color15,R.color.course_color16};

	private Course course;
	private ImageButton buttonBack;
	private LinearLayout mainLayout;
	private TextView textCoursename,textRoom,textTeacher,textTime,textSchedule,textWeek;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_course_detail);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		textCoursename = (TextView)findViewById(R.id.text_coursename);		
		textRoom = (TextView)findViewById(R.id.text_room);
		textTeacher = (TextView)findViewById(R.id.text_teacher);
		textTime = (TextView)findViewById(R.id.text_time);
		textSchedule = (TextView)findViewById(R.id.text_schedule);
		textWeek = (TextView)findViewById(R.id.text_week);
		buttonBack = (ImageButton)findViewById(R.id.button_back);
		mainLayout = (LinearLayout)findViewById(R.id.course_detail_layout);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		course = (Course)bundle.get("course");
		int pos = (Integer)bundle.get("bgcolor");
		if(pos<=16) mainLayout.setBackgroundResource(bgColor[pos]);
		textCoursename.setText(course.getCourseName());
		textTeacher.setText(course.getTeacherName());
		textSchedule.setText(course.getSchedule());
		textWeek.setText("第"+course.getWeek()+"周");
		int day = Integer.parseInt(course.getDay());
		int jie = Integer.parseInt(course.getJie());
		String res = weekDayArray[day-1]+jieArray[jie-1];
		textTime.setText(res);
		String room = course.getPlace();
		room = room.replace("[", "");
		room = room.replace("]", "");
		textRoom.setText(room);
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		buttonBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CourseDetailActivity.this.finish();
			}
		});
	}
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		if(KeyCode==KeyEvent.KEYCODE_BACK){
			this.finish();
		}
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("CourseDetail");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("CourseDetail");
	    MobclickAgent.onPause(this);
	}
}
