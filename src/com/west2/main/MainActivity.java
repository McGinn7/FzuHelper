package com.west2.main;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.west2.domain.Course;
import com.west2.domain.Exam;
import com.west2.domain.Mark;
import com.west2.domain.MyBook;
import com.west2.domain.Weather;
import com.west2.service.CourseService;
import com.west2.service.CreditService;
import com.west2.service.DateService;
import com.west2.service.ExamService;
import com.west2.service.LibraryService;
import com.west2.service.LoginService;
import com.west2.service.MarkService;
import com.west2.service.NotificationService;
import com.west2.service.RefreshService;
import com.west2.service.WeatherService;
import com.west2.view.life.WeatherActivity;
import com.west2.view.study.CourseSetActivity;

public class MainActivity extends Activity{
	private String[] jieArray={"1-2","3-4","5-6","7-8","9-10"};
	// 控件
	private TextView textGreet,textDate;
	private TextView textWeather,textTemperature;
	private TextView textCredit,textWeek,textAddCredit;
	private ImageView iconWeather;
	private ImageButton btnLife,btnStudy;
	private Button btnSetting,btnQuit,btnCourse;
	// 主页课程
	private int cntPos = 0;
	private boolean[] isCourse;
	private List<Course> listToday = null;
	private ImageButton btnPreCourse,btnNextCourse;
	private TextView textJie1,textCourse1,textPlace1;
	private TextView textJie2,textCourse2,textPlace2;
	// 服务
	private DateService dateService;
	private MarkService markService;
	private ExamService examService;
	private LoginService loginService;
	private LibraryService libService;
	private CourseService courseService;
	private CreditService creditService;
	private WeatherService weatherService;
	private RefreshService refreshService;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // 友盟
        UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
        initUMUpdate();
        
        findView();
        initValue();
        setListener();
    }
    private void initUMUpdate(){
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo){
			}
		});
    }
    private void findView(){
    	textAddCredit = (TextView)findViewById(R.id.text_credit_add);
    	textCredit = (TextView)findViewById(R.id.text_credit);
    	textWeek = (TextView)findViewById(R.id.text_week);
    	textGreet = (TextView)findViewById(R.id.text_greet);
    	textDate = (TextView)findViewById(R.id.text_date);
//    	textWeather = (TextView)findViewById(R.id.text_weather);
//    	textTemperature = (TextView)findViewById(R.id.text_temperature);
//    	iconWeather = (ImageView)findViewById(R.id.icon_weather);
    	btnLife = (ImageButton)findViewById(R.id.button_left);
    	btnStudy = (ImageButton)findViewById(R.id.button_right);
    	btnSetting = (Button)findViewById(R.id.button_setting);
    	btnQuit = (Button)findViewById(R.id.button_quit);
    	btnCourse = (Button)findViewById(R.id.main_button_course);
    	
    	textJie1 = (TextView)findViewById(R.id.text_jie_1);
    	textJie2 = (TextView)findViewById(R.id.text_jie_2);
    	textCourse1 = (TextView)findViewById(R.id.text_course_1);
    	textCourse2 = (TextView)findViewById(R.id.text_course_2);
    	textPlace1 = (TextView)findViewById(R.id.text_place_1);
    	textPlace2 = (TextView)findViewById(R.id.text_place_2);
    	btnPreCourse = (ImageButton)findViewById(R.id.button_pre_course);
    	btnNextCourse = (ImageButton)findViewById(R.id.button_next_course);
    }
    private void initValue(){
    	cntPos = 1;
    	isCourse = new boolean[10];

    	dateService = new DateService(this);
    	markService = new MarkService(this);
    	examService = new ExamService(this);
    	loginService = new LoginService(this);
    	libService = new LibraryService(this);
    	creditService = new CreditService(this);
    	courseService = new CourseService(this);
    	weatherService = new WeatherService(this);
    	refreshService = new RefreshService(this);
    	dataHandler.post(getCourseFromDb);
    	dataHandler.post(getCreditFromDb);
    	new Thread(getDataFromNet).start();
    	textWeek.setText(dateService.getWeek() + " Week");
    	textDate.setText(dateService.getLocalDate());
    	textGreet.setText(dateService.getDayOrNight()+","+loginService.getRaelname()+"同学");
    }
    private void setListener(){
    	textCredit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
				dialog.show();
				Window window = dialog.getWindow();
				window.setContentView(R.layout.credit_dialog);
				Button btnBack = (Button)window.findViewById(R.id.credit_dialog_back);
				btnBack.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
			}
    	});
    	btnLife.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SetActivity.setPosition(0);
			}
    	});
    	btnStudy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SetActivity.setPosition(2);
			}
    	});
    	btnSetting.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,SettingActivity.class);
				MainActivity.this.startActivity(intent);
				MainActivity.this.finish();
			}
    	});
    	btnQuit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MainActivity.this.finish();
			}
    	});
    	btnCourse.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(MainActivity.this,CourseSetActivity.class);
//				MainActivity.this.startActivity(intent);
			}
    	});
    	btnPreCourse.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(listToday == null || listToday.size()==0) return ;
				if(cntPos>1){
					--cntPos;
					if(cntPos<=1)
						btnPreCourse.setImageDrawable(
							MainActivity.this.getResources().getDrawable(R.drawable.left_dark));
					if(listToday.size()-cntPos>=2)
						btnNextCourse.setImageDrawable(
								MainActivity.this.getResources().getDrawable(R.drawable.right_bright));
					dataHandler.post(getCourseFromDb);
				}
				Log.e("MainActivity", "cntPos = " + cntPos);
				Log.e("MainActivity", "listToday.size() = " + listToday.size());
			}
    	});
    	btnNextCourse.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(listToday == null || listToday.size()==0) return ;
				if(cntPos+1<listToday.size()){
					++cntPos;
					Log.e("MainActivity", "cntPos = " + cntPos);
					Log.e("MainActivity", "listToday.size() = " + listToday.size());
					if(cntPos+1>=listToday.size())
						btnNextCourse.setImageDrawable(
							MainActivity.this.getResources().getDrawable(R.drawable.right_dark));
					if(cntPos>=2)
						btnPreCourse.setImageDrawable(
								MainActivity.this.getResources().getDrawable(R.drawable.left_bright));
					dataHandler.post(getCourseFromDb);
				}
			}
    	});
    }
    /*
     * 处理更新结果
     */
    @SuppressLint("HandlerLeak")
	Handler dataHandler = new Handler(){
        /*
         * msg.what
         * 1 : 成绩提醒
         * 2 : 本日考试提醒
         * 3 : 明日考试提醒
         * 4 : 更新天气界面
         * 5 : 图书即将到期
         * 6 : 图书过期
         * 7 : 更新积分
         * 8 : 更新课表UI
         */
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case 0:break;
    		case 1:NotificationService.showMessage(MainActivity.this, 1, "成绩已更新");
    			break;
    		case 2:NotificationService.showMessage(MainActivity.this, 2, "今日有考试");
    			break;
    		case 3:NotificationService.showMessage(MainActivity.this, 3, "明日有考试");
    			break;
    		case 4:dataHandler.post(getCourseFromDb);
    			break;
    		case 5:NotificationService.showMessage(MainActivity.this, 4, "图书即将到期");
    			break;
    		case 6:NotificationService.showMessage(MainActivity.this, 5, "图书已过期，请及时归还");
    			break;
    		case 7:dataHandler.post(getCreditFromDb);
    			dataHandler.post(refreshAddCredit);
    			break;
    		case 8:
    			listToday.clear();
    			listToday = null;
    			dataHandler.post(getCourseFromDb);
    			Log.e("MainActivity", "更新课程UI");
    			break;
    		}
    	}
    };
    /*
     * 签到提示
     */
    Runnable refreshAddCredit = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String lastcount = creditService.getLastCount();
			if(!lastcount.equals("")){
				SpannableString sp;
				sp = new SpannableString("今日签到+"+lastcount);
				sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				sp.setSpan(new ForegroundColorSpan(Color.WHITE), 4, 5+lastcount.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textAddCredit.setText(sp);
				textAddCredit.setVisibility(View.VISIBLE);
				Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha);
				anim.setFillAfter(true);
				textAddCredit.startAnimation(anim);
				creditService.setLastCount("");
			}
		}
    };
    /*
     * 判断字符串是否为数字
     */
    boolean isNumber(String str){
    	if(str==null || str.equals("")) return false;
    	for(int i=0;i<str.length();++i)
    	if(!Character.isDigit(str.charAt(i))) return false;
    	return true;
    }
    /*
     * 课程UI
     */
    Runnable getCourseFromDb = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e("MainActivity","课程UI线程");
			if(listToday == null){
				textJie1.setText("");
				textJie2.setText("");
				textCourse1.setText("");
				textCourse2.setText("");
				textPlace1.setText("");
				textPlace2.setText("");
				btnPreCourse.setImageDrawable(
					MainActivity.this.getResources().getDrawable(R.drawable.left_dark));
				btnNextCourse.setImageDrawable(
					MainActivity.this.getResources().getDrawable(R.drawable.right_dark));
		    	for(int i=0;i<10;++i) isCourse[i] = false;
				String weekDay = dateService.getNumberWeekDay();
				List<Course> list = courseService.getCourse(dateService.getWeek());
				listToday = new ArrayList<Course>();
				if(list!=null){
					for(Course c:list)
					if(c.getDay().equals(weekDay) && isNumber(c.getJie())
					&& !c.getCourseName().equals("")){
						int jie = Integer.parseInt(c.getJie());
						if(jie>=0 && jie<10 && !isCourse[jie]){
							listToday.add(c);
							isCourse[jie]=true;
						}
					}
				}
				else
					listToday = null;
				if(listToday == null || listToday.size() == 0)
					btnCourse.setText("今日无课");
				else
					btnCourse.setText("");
				cntPos = 1;
				if(listToday.size()>2){
					btnNextCourse.setImageDrawable(MainActivity.this.getResources().
						getDrawable(R.drawable.right_bright));
				}
			}
			if(listToday == null) return ;
			if(listToday.size()>=cntPos){
				int pos = cntPos - 1;
				if(isNumber(listToday.get(pos).getJie())){
					int jie = Integer.parseInt(listToday.get(pos).getJie());
					textJie1.setText(jieArray[jie-1]);
					textCourse1.setText(listToday.get(pos).getCourseName());
					textPlace1.setText(listToday.get(pos).getPlace());
				}
			}
			if(listToday.size()> cntPos){
				int pos = cntPos;
				if(isNumber(listToday.get(pos).getJie())){
					int jie = Integer.parseInt(listToday.get(pos).getJie());
					textJie2.setText(jieArray[jie-1]);
					textCourse2.setText(listToday.get(pos).getCourseName());
					textPlace2.setText(listToday.get(pos).getPlace());
				}
			}
		}
    };
    /*
     * 积分UI
     */
    Runnable getCreditFromDb = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String credit = creditService.getCredit();
			if(credit==null || credit.equals("")) return ;
			textCredit.setText(credit);
		}
    };
    /*
     * 网络获取
     */
    Runnable getDataFromNet = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = null;
			creditService.signOrShare(loginService.getUsername(), "sign");
			msg = dataHandler.obtainMessage();
			msg.what = 7;
			dataHandler.sendMessage(msg);
			
			//更新天气
			if(refreshService.getWeahter()){
				boolean isSucceed = weatherService.queryWeather();
				if(isSucceed){
					refreshService.setWeather(false);
					msg = dataHandler.obtainMessage();
					msg.what = 4;
					dataHandler.post(getCourseFromDb);
				}
				else{
					refreshService.setWeather(true);
				}
			}
			//更新日期
			if(refreshService.getDate()){
				refreshService.setDate(!dateService.queryDate());
			}
			//更新成绩
			if(refreshService.getMark()){
				List<Mark> list1 = null;
				List<Mark> list2 = null;
				if(markService.getTermList()!=null){
					int pos = markService.getTermList().size()-1;
					if(pos>=0) list1 = markService.getMark(markService.getTermList().get(pos));
				}
				markService.queryCredit(loginService.getUsername(), loginService.getPassword());
				refreshService.setMark(!markService.queryMark(loginService.getUsername()));
				if(markService.getTermList()!=null){
					int pos = markService.getTermList().size()-1;
					if(pos>=0) list2 = markService.getMark(markService.getTermList().get(pos));
				}
				if(list2!=null){
					if(list1==null || list1.size()<list2.size()){
						msg = dataHandler.obtainMessage();
						msg.what = 1;
						dataHandler.sendMessage(msg);
					}
				}
			}
			//我的图书
			if(refreshService.getMyBook()){
				refreshService.setMyBook(false);
				libService.login();
				List<MyBook> listBook = libService.getMyBook();
				if(listBook!=null){
					try{
						int cntYear = Integer.parseInt(dateService.getYear());
						int cntMonth = Integer.parseInt(dateService.getMonth());
						int cntDay = Integer.parseInt(dateService.getDay());
						for(int i=0;i<listBook.size();++i){
							MyBook book = listBook.get(i);
							int year = Integer.parseInt(book.getYear());
							int month = Integer.parseInt(book.getMonth());
							int day = Integer.parseInt(book.getDay());
							if(year<cntYear || (year==cntYear && month<cntMonth) || (year==cntYear && month==cntMonth && day<cntDay)){
								msg.what = 6;
								dataHandler.sendMessage(msg);
								continue;
							}
							for(int j=0;j<2;++j){
								day++;
								if(day>dateService.getDayOfMonth(year, month)){
									day-=dateService.getDayOfMonth(year, month);
									if(++month>12){month-=12;year++;}
								}
								if(year==cntYear && month==cntMonth && day==cntDay){
									msg.what = 5;
									dataHandler.sendMessage(msg);
									break;
								}
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			//更新考场
			if(refreshService.getExam()){
				refreshService.setExam(!examService.queryExam());
				try{
					int cntYear = Integer.parseInt(dateService.getYear());
					int cntMonth = Integer.parseInt(dateService.getMonth());
					int cntDay = Integer.parseInt(dateService.getDay());
					int cntHour = Integer.parseInt(dateService.getHour());
					int cntMinute = Integer.parseInt(dateService.getMinute());
					List<Exam> list = examService.getExam();
					if(list!=null){
						for(int i=0;i<list.size();++i){
							Exam exam = list.get(i);
							int year = Integer.parseInt(exam.getYear());
							int month = Integer.parseInt(exam.getMonth());
							int day = Integer.parseInt(exam.getDay());
							int hour = Integer.parseInt(exam.getHour());
							int minute = Integer.parseInt(exam.getMinute());
							if(year==cntYear && month==cntMonth && day==cntDay
							&&(hour>cntHour || (hour==cntHour && minute<cntMinute))){
								msg = dataHandler.obtainMessage();
								msg.what = 2;
								dataHandler.sendMessage(msg);
								continue;
							}
							day++;
							if(day>dateService.getDayOfMonth(year, month)){
								day-=dateService.getDayOfMonth(year, month);
								if(++month>12){month-=12;++year;}
							}
							if(year==cntYear && month==cntMonth && day==cntDay
							&&(hour>cntHour || (hour==cntHour && minute<cntMinute))){
								msg = dataHandler.obtainMessage();
								msg.what = 3;
								dataHandler.sendMessage(msg);
								continue;
							}
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			//更新课表
			if(refreshService.getCourse()){
				Log.e("MainActivity", "更新课表");
				boolean status = courseService.queryCourse(loginService.getUsername());
				refreshService.setCourse(!status);
				if(status){
					msg = dataHandler.obtainMessage();
					msg.what = 8;
					dataHandler.sendMessage(msg);
				}
			}
		}
    };
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Main");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Main");
	    MobclickAgent.onPause(this);
	}
}
