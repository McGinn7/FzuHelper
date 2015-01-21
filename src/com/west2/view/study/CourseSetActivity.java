package com.west2.view.study;

import java.util.ArrayList;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.ViewPagerAdapter;
import com.west2.domain.CubeOutTransformer;
import com.west2.main.R;
import com.west2.service.CourseService;
import com.west2.service.DateService;
import com.west2.service.ViewService;

public class CourseSetActivity extends Activity{
	private static String weekDayArray[]={"周一","周二","周三","周四","周五","周六","周日"};
	private LocalActivityManager mManager=null;
	private static ViewPager viewPager=null;
	private static TextView[] textWeekDay = new TextView[10];
	private static TextView textMonth;
	private static DateService dateService;
	private static ViewService viewService;
	private static CourseService courseService;
	private static Handler uiHandler;
	private static int cntWeekDay=0;
	private static LinearLayout layoutBlue;
	private static int pre = 0;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_course);

		mManager=new LocalActivityManager(this,true);
    	mManager.dispatchCreate(savedInstanceState);
    	findView();
    	initValue();
	}
	public static int getCntWeekDay(){
		return cntWeekDay;
	}
	public static int getCntIndex(){
		return viewPager.getCurrentItem();
	}
	public static void changePage(int position){
		if(position<0 || position>1) return ;
		viewPager.setCurrentItem(position,true);
	}
	public static void refreshDate(){
		uiHandler.post(refreshDateThread);
	}
	public static void setWeekDayBackground(int position){
		if(position<0) return ;
		int width = (viewService.screenWidth-pre)/7;
		TranslateAnimation anim = new TranslateAnimation(
				pre + width * cntWeekDay,
				pre + width * position, 0, 0);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		anim.setDuration(200);
		anim.setFillAfter(true);
		layoutBlue.startAnimation(anim);
		cntWeekDay = position;
	}
	
	@SuppressWarnings("deprecation")
	private View getView(String id,Intent intent){
		if(intent==null) return null;
		return mManager.startActivity(id, intent).getDecorView();
	}
	private void findView(){
		layoutBlue = (LinearLayout)findViewById(R.id.layout_blue);
		viewPager = (ViewPager)findViewById(R.id.course_viewpager);
		textMonth = (TextView)findViewById(R.id.course_week_month);
		textWeekDay[0]=(TextView)findViewById(R.id.text_monday);
		textWeekDay[1]=(TextView)findViewById(R.id.text_tuesday);
		textWeekDay[2]=(TextView)findViewById(R.id.text_wednesday);
		textWeekDay[3]=(TextView)findViewById(R.id.text_thursday);
		textWeekDay[4]=(TextView)findViewById(R.id.text_friday);
		textWeekDay[5]=(TextView)findViewById(R.id.text_saturday);
		textWeekDay[6]=(TextView)findViewById(R.id.text_sunday);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		uiHandler = new Handler();
		dateService = new DateService(this);
		viewService = new ViewService(this);
		courseService = new CourseService(this);
		pre = getResources().getDimensionPixelSize(R.dimen.course_width);
		try {
			viewPager.setPageTransformer(true,CubeOutTransformer.class.newInstance());
		} catch (Exception e) {}
		final ArrayList<View> mList=new ArrayList<View>();
		Intent intent=null;
		intent=new Intent(this,CourseWeekActivity.class);
		mList.add(getView("CourseWeekActivity",intent));
		intent=new Intent(this,CourseDayActivity.class);
		mList.add(getView("CourseDayActivity",intent));
		viewPager.setAdapter(new ViewPagerAdapter(mList));
		viewPager.setCurrentItem(0);
		viewPager.bringChildToFront(mList.get(0));
	}
	/*
	 * 更新日期线程
	 */
	static Runnable refreshDateThread = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int day = 0,month = 0,year = 0,cntWeek = 0,saveWeek = 0,weekDay=0;
			String mDay = dateService.getDay();
			if(mDay!=null && !mDay.equals("")) day = Integer.parseInt(dateService.getDay());
			String mMonth = dateService.getMonth();
			if(mMonth!=null && !mMonth.equals("")) month = Integer.parseInt(dateService.getMonth());
			String mYear = dateService.getYear();
			if(mYear!=null && !mYear.equals("")) year = Integer.parseInt(dateService.getYear());
			String mCntWeek = dateService.getWeek();
			if(mCntWeek!=null && !mCntWeek.equals("")) cntWeek = Integer.parseInt(dateService.getWeek());
			String mSaveWeek = courseService.getSaveWeek();
			if(mSaveWeek!=null && !mSaveWeek.equals("")) saveWeek = Integer.parseInt(courseService.getSaveWeek());
			weekDay = Integer.parseInt(dateService.getNumberWeekDay());
			cntWeekDay = weekDay - 1;
			setWeekDayBackground(cntWeekDay);
			if(day==0 || month==0 || year==0){
				for(int i=0;i<7;++i)
					textWeekDay[i].setText(weekDayArray[i]);
				return ;
			}
			while(weekDay>1){
				--weekDay;
				--day;
			}
			if(day<=0){
				--month;
				if(month<=0){
					month+=12;
					--year;
				}
				day+=dateService.getDayOfMonth(year,month);
			}
			while(saveWeek>cntWeek){
				++cntWeek;
				day+=7;
				int temp = dateService.getDayOfMonth(year,month);
				if(day>temp){
					day-=temp;
					++month;
					if(month>12){
						month-=12;
						++year;
					}
				}
			}
			while(saveWeek<cntWeek){
				--cntWeek;
				day-=7;
				if(day<=0){
					--month;
					if(month<=0){
						month+=12;
						--year;
					}
					day+=dateService.getDayOfMonth(year,month);
				}
			}
			textMonth.setText(month+"月");
			while(weekDay<=7){
				textWeekDay[weekDay-1].setText(""+day+"\r\n"+weekDayArray[weekDay-1]);
				++weekDay;
				++day;
				int temp=dateService.getDayOfMonth(year,month);
				if(day>temp){
					day-=temp;
					++month;
					if(month>12){
						month-=12;
						++year;
					}
				}
			}
		}
	};
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		if(KeyCode==KeyEvent.KEYCODE_BACK){
			if(viewPager.getCurrentItem()==0){
				this.finish();
			}
			else{
				this.changePage(0);
			}
		}
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Start");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Start");
	    MobclickAgent.onPause(this);
	}
}
