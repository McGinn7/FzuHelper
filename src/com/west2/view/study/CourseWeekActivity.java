package com.west2.view.study;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.west2.custom.adapter.WeekListAdapter;
import com.west2.domain.Course;
import com.west2.main.R;
import com.west2.service.CourseService;
import com.west2.service.DateService;
import com.west2.service.LoginService;
import com.west2.service.RefreshService;
import com.west2.service.ViewService;

public class CourseWeekActivity extends Activity{
	private int jieIdArr[] = {R.id.course_row_10,R.id.course_row_20,R.id.course_row_30,R.id.course_row_40,R.id.course_row_50,
			R.id.course_row_60,R.id.course_row_70,R.id.course_row_80,R.id.course_row_90,R.id.course_row_100};
	private int courseColor[]={
		R.drawable.course_bg1,R.drawable.course_bg2,R.drawable.course_bg3,R.drawable.course_bg4,
		R.drawable.course_bg5,R.drawable.course_bg6,R.drawable.course_bg7,R.drawable.course_bg8,
		R.drawable.course_bg9,R.drawable.course_bg10,R.drawable.course_bg11,R.drawable.course_bg12,
		R.drawable.course_bg13,R.drawable.course_bg14,R.drawable.course_bg15,R.drawable.course_bg16,
	};
	private int textViewId[][]={
		{R.id.course_row_11,R.id.course_row_12,R.id.course_row_13,R.id.course_row_14,R.id.course_row_15,R.id.course_row_16,R.id.course_row_17},
		{R.id.course_row_21,R.id.course_row_22,R.id.course_row_23,R.id.course_row_24,R.id.course_row_25,R.id.course_row_26,R.id.course_row_27},
		{R.id.course_row_31,R.id.course_row_32,R.id.course_row_33,R.id.course_row_34,R.id.course_row_35,R.id.course_row_36,R.id.course_row_37},
		{R.id.course_row_41,R.id.course_row_42,R.id.course_row_43,R.id.course_row_44,R.id.course_row_45,R.id.course_row_46,R.id.course_row_47},
		{R.id.course_row_51,R.id.course_row_52,R.id.course_row_53,R.id.course_row_54,R.id.course_row_55,R.id.course_row_56,R.id.course_row_57}
	};
	private Thread netThread = null;
	// 控件
	private int[][] textBgColor = new int[7][9];
	private TextView[] jieArr;
	private Handler uiHandler;
	private List<String> listWeek;
	private ListView listViewWeek;
	private List<Course> listCourse;
	private Course[][] courseArray;
	private TextView[][] textArray;
	private boolean first_time=true;
	private ImageButton buttonRefresh;
	private LinearLayout layoutWeekList;
	private Button buttonWeek,buttonChange;
	private int courseHeight,courseWidth,curPosition;
	// 服务
	private DateService dateService;
	private ViewService viewService;
	private LoginService loginService;
	private CourseService courseService;
	private RefreshService refreshService;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_course_week);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		listViewWeek=(ListView)findViewById(R.id.list_week);
		buttonWeek=(Button)findViewById(R.id.button_week);
		buttonChange=(Button)findViewById(R.id.button_change);
		buttonRefresh=(ImageButton)findViewById(R.id.button_refresh);
		layoutWeekList=(LinearLayout)findViewById(R.id.layout_list_week);
		textArray = new TextView[6][8];
		for(int i=1;i<=5;++i)
		for(int j=1;j<=7;++j){
			textArray[i][j] = (TextView)findViewById(textViewId[i-1][j-1]);
		}
		jieArr = new TextView[10];
		for(int i=0;i<10;++i)
			jieArr[i] = (TextView)findViewById(jieIdArr[i]);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		dateService = new DateService(this);
		viewService = new ViewService(this);
		loginService = new LoginService(this);
		courseService = new CourseService(this);
		refreshService = new RefreshService(this);
		uiHandler = new Handler();
		courseArray = new Course[7][9];
		int tmp = getResources().getDimensionPixelSize(R.dimen.course_width);
		courseWidth = (viewService.screenWidth-tmp)/7;
		courseHeight = viewService.screenHeight/6;
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle!=null){
			String week = bundle.getString("week");
			if(week==null || week.equals("")){
				week = dateService.getWeek();
				if(week!=null && week.equals("0")){
					courseService.saveWeek("1");
				}
				else{
					courseService.saveWeek(dateService.getWeek());
				}
			}
			else{
				courseService.saveWeek(week);
			}
		}
		CourseSetActivity.refreshDate();
		uiHandler.post(getWeekListFromDb);
		uiHandler.post(initJie);
		uiHandler.post(getCourseFromDb);
		if(refreshService.getCourse()){
			new Thread(getCourseFromNet).start();
		}
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		for(int i=1;i<=5;++i)
		for(int j=1;j<=7;++j)
		textArray[i][j].setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(listViewWeek.isShown()){
					hideListViewWeek();
					return ;
				}
				int r=5,c=7;
				for(r=0;r<5;++r){
					for(c=0;c<7;++c)
						if(textViewId[r][c]==view.getId()) break;
					if(r<5 && c<7 && textViewId[r][c]==view.getId()) break;
				}
				if(r==5 || c==7) return ;
				++r;++c;
				if(textArray[r][c].getText().toString().equals("") ||
				courseArray[r][c]==null){
					return ;
				}
				if(CourseSetActivity.getCntIndex()!=0) return ;
				Intent intent = new Intent(CourseWeekActivity.this,CourseDetailActivity.class);
				Bundle bundle = new Bundle();
				courseArray[r][c].setSchedule(courseService.getSchedule(courseArray[r][c].getCourseName()));
				bundle.putSerializable("course", courseArray[r][c]);
				bundle.putSerializable("bgcolor", textBgColor[r][c]);
				intent.putExtras(bundle);
				CourseWeekActivity.this.startActivity(intent);
			}
		});
		listViewWeek.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				hideListViewWeek();
				curPosition = position;
				uiHandler.postDelayed(weekChoose, 100);
			}
		});
		buttonWeek.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(listViewWeek.isShown()){
					listViewWeek.setVisibility(View.GONE);
					layoutWeekList.setVisibility(View.GONE);
				}
				else{
					listViewWeek.setVisibility(View.VISIBLE);
					layoutWeekList.setVisibility(View.VISIBLE);
				}
			}
		});
		buttonRefresh.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(listViewWeek.isShown()){
					hideListViewWeek();
					return ;
				}
				if(buttonRefresh.getAnimation()!=null){
					buttonRefresh.clearAnimation();
					return ;
				}
				if(CourseSetActivity.getCntIndex()!=0) return ;
				Animation animRotate = AnimationUtils.loadAnimation(CourseWeekActivity.this, R.anim.anim_rotate);
				buttonRefresh.startAnimation(animRotate);
				if(netThread==null || !netThread.isAlive()){
//					new Thread(getCourseFromNet).start();
					netThread = new Thread(getCourseFromNet);
					netThread.start();
				}
			}
		});
		buttonChange.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(listViewWeek.isShown()){
					hideListViewWeek();
					return ;
				}
				CourseSetActivity.changePage(1);
			}
		});
	}
	/*
	 * 初始化节数界面
	 */
	Runnable initJie = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			LayoutParams params;
			for(int i=0;i<10;++i){
				params = (LinearLayout.LayoutParams) jieArr[i].getLayoutParams();
				params.width = getResources().getDimensionPixelSize(R.dimen.course_width);
				params.height = courseHeight/2;
				params.setMargins(0, 1, 0, 1);
				jieArr[i].setLayoutParams(params);
			}
		}
	};
	/*
	 * 隐藏周数列表
	 */
	private void hideListViewWeek(){
		if(listViewWeek.isShown()){
			listViewWeek.setVisibility(View.GONE);		
			layoutWeekList.setVisibility(View.GONE);
		}
	}
	/*
	 * 课程返回结果处理
	 */
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			if(buttonRefresh.getAnimation()!=null) buttonRefresh.clearAnimation();
			if(msg.what==0){
				Log.e("CourseWeekActivity", "网络异常");
				Toast.makeText(CourseWeekActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
			}
			else{
				refreshService.setCourse(false);
				uiHandler.post(getCourseFromDb);
				uiHandler.post(getWeekListFromDb);
				CourseDayActivity.reCourse();
			}
		}
	};
	/*
	 * 周数改变线程
	 */
	Runnable weekChoose = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			listViewWeek.setVisibility(View.GONE);
			layoutWeekList.setVisibility(View.GONE);
			buttonWeek.setText(dealWeek(listWeek.get(curPosition)));
			courseService.saveWeek(listWeek.get(curPosition));
			uiHandler.post(getCourseFromDb);
			CourseSetActivity.refreshDate();
			CourseDayActivity.reCourse();
		}
	};
	/*
	 * 界面更新线程
	 */
	private void refreshUI(){
		// TODO Auto-generated method stub
		List<String> courseList = courseService.getCourseList();
		LayoutParams params;
		for(int i=1;i<=5;++i)
		for(int j=1;j<=7;++j){
			textBgColor[i][j]=0;
			courseArray[i][j]=null;
			if(!textArray[i][j].isShown())
				textArray[i][j].setVisibility(View.VISIBLE);
			String temp = textArray[i][j].getText().toString();
			textArray[i][j].setText(temp);
			if(temp.equals(""))
				textArray[i][j].setBackgroundResource(R.drawable.course_bg0);
			else
				textArray[i][j].setBackgroundResource(R.drawable.course_bg00);
			params = (LinearLayout.LayoutParams) textArray[i][j].getLayoutParams();
			params.width = courseWidth;
			params.height = courseHeight;
			params.setMargins(2, 2, 2, 2);
			textArray[i][j].setLayoutParams(params);
		}
		for(Course c:listCourse){
			if(c.getJie()==null || c.getJie().equals("")) continue;
			if(c.getDay()==null || c.getDay().equals("")) continue;
			int jie = Integer.parseInt(c.getJie());
			int day = Integer.parseInt(c.getDay());
			if(jie>=1 && jie<=5 && day>=1 && day<=7){
				courseArray[jie][day] = c;
			}
		}
		int i,j,k,pos;
		SpannableString sp;
		boolean isTheSame=false;
		String place,coursename;
		// 显示课程
		for(i=1;i<=5;++i)
		for(j=1;j<=7;++j)
		if(courseArray[i][j]!=null){
			place = courseArray[i][j].getPlace();
			coursename = courseArray[i][j].getCourseName();
			sp = new SpannableString(coursename + " " + place);
			sp.setSpan(new ForegroundColorSpan(Color.WHITE), 
					0, coursename.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			sp.setSpan(new ForegroundColorSpan(Color.WHITE),
					coursename.length(), coursename.length()+place.length()+1,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			textArray[i][j].setText(sp);
		}
		// 合并课程、设置背景色
		for(i=1;i<=5;++i)
		for(j=1;j<=7;++j){
			isTheSame=false;
			k=i-1;
			while(k>0){
				if(courseArray[k][j]==null && courseArray[i][j]!=null) break;
				if(courseArray[k][j]!=null && courseArray[i][j]==null) break;
				if(courseArray[i][j]!=null){
					if(!courseArray[k][j].getPlace().equals(courseArray[i][j].getPlace())) break;
					if(!courseArray[k][j].getCourseName().equals(courseArray[i][j].getCourseName())) break;
				}
				else{
					if(!textArray[k][j].getText().toString().equals(textArray[i][j].getText().toString())) break;
				}
				isTheSame=true;
				--k;
			}
			params = (LinearLayout.LayoutParams) textArray[i][j].getLayoutParams();
			params.width = courseWidth;
			params.setMargins(2, 2, 2, 2);
			if(isTheSame){
				params.height = 4*(i-k-1)+(i-k)*courseHeight;
				while(++k<i) textArray[k][j].setVisibility(View.GONE);
			}
			else
				params.height = courseHeight;
			textArray[i][j].setLayoutParams(params);
			// 设置颜色
			if(courseArray[i][j]!=null){
				pos = 1 + courseList.indexOf(courseArray[i][j].getCourseName());
				if(pos>=1 && pos<=16){
					textBgColor[i][j] = pos;
					textArray[i][j].setBackgroundResource(courseColor[pos-1]);
				}
			}
		}
	};
	/*
	 * 网络获取课程信息
	 */
	Runnable getCourseFromNet = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			boolean status = courseService.queryCourse(loginService.getUsername());
			Message msg = dataHandler.obtainMessage();
			if(!status){
				msg.what=0;
			}
			else{
				msg.what=1;
			}
			dataHandler.sendMessage(msg);
		}
	};
	/*
	 * 数据库调用课程
	 */
	Runnable getCourseFromDb = new Runnable(){
		public void run(){
			List<Course> list;
			if(first_time){
				list = courseService.getCourse(""+10);
				if(list==null) return ;
				listCourse = list;
				refreshUI();
				first_time=false;
			}
			list = courseService.getCourse(courseService.getSaveWeek());
			if(list==null) return ;
			listCourse = list;
			refreshUI();
		}
	};
	/*
	 * 数据库调用周数列表
	 */
	Runnable getWeekListFromDb = new Runnable(){
		public void run(){
			List<String> list = courseService.getWeekList();
			if(list==null || list.size()==0) return ;
			String cntWeek = dateService.getWeek();
			int lastListWeek = 1 + Integer.parseInt(list.get(list.size()-1));
			while(lastListWeek<=Integer.parseInt(cntWeek)){
				list.add(""+lastListWeek);
				lastListWeek++;
			}
			WeekListAdapter mAdapter = new WeekListAdapter(CourseWeekActivity.this,list,cntWeek);
			listViewWeek.setAdapter(mAdapter);
			buttonWeek.setText(dealWeek(courseService.getSaveWeek()));
			listWeek = list;
		}
	};
	/*
	 * 标记出本周
	 */
	private SpannableString dealWeek(String week){
		String tmp = "第 " + week + " 周";
		if(week.equals(dateService.getWeek())){
			tmp = tmp + "(本周)";
		}
		else{
			tmp = tmp + "(非本周)";
		}
		SpannableString res = new SpannableString("▲"+tmp);
		res.setSpan(new AbsoluteSizeSpan(15, true), 0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return res;
	}
	@Override
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("CourseWeek");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("CourseWeek");
	    MobclickAgent.onPause(this);
	}
}