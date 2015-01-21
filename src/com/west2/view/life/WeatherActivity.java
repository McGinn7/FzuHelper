package com.west2.view.life;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.west2.domain.Weather;
import com.west2.main.R;
import com.west2.service.DateService;
import com.west2.service.RefreshService;
import com.west2.service.WeatherService;

public class WeatherActivity extends Activity{
	private ImageView weatherIcon;
	private TextView day1_date,day1_weather;
	private TextView day2_date,day2_weather;
	private TextView day3_date,day3_weather;
	private TextView textWeather,textTemperature,textDate;

	private WeatherService weaService;
	private DateService dateService;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		weatherIcon = (ImageView)findViewById(R.id.weather_img_icon);
		textWeather = (TextView)findViewById(R.id.weather_text_weather);
		textTemperature = (TextView)findViewById(R.id.weather_text_temperature);
		textDate = (TextView)findViewById(R.id.weather_text_date);

		day1_date = (TextView)findViewById(R.id.weather_day1);
		day2_date = (TextView)findViewById(R.id.weather_day2);
		day3_date = (TextView)findViewById(R.id.weather_day3);
		day1_weather = (TextView)findViewById(R.id.weather_day1_weather);
		day2_weather = (TextView)findViewById(R.id.weather_day2_weather);
		day3_weather = (TextView)findViewById(R.id.weather_day3_weather);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		weaService = new WeatherService(this);
		dateService = new DateService(this);
		dataHandler.post(getWeatherFromDb);
		if(new RefreshService(this).getWeahter()){
			new Thread(getWeatherFromNet).start();
		}
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		
	}
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what==0) return ;
			dataHandler.post(getWeatherFromDb);
		}
	};
	/*
	 * 网络获取天气
	 */
	Runnable getWeatherFromNet = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg=dataHandler.obtainMessage();
			if(weaService.queryWeather())
				msg.what = 1;
			else
				msg.what = 0;
			dataHandler.sendMessage(msg);
		}
	};
	/*
	 * 数据库调用天气
	 */
	Runnable getWeatherFromDb = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<Weather> list = weaService.getWeather();
			if(list==null || list.size()==0) return ;
			weatherIcon.setImageResource(weaService.getIconWeather(list.get(0).getWeather()));
			textWeather.setText(list.get(0).getWeather());
			textTemperature.setText(list.get(0).getTemperature().replace("℃", "")+"°");
			String tmp = weekDayTrans(dateService.getWeekDay());
			textDate.setText(dateService.getMonth()+"/"+dateService.getDay()+
					"  "+tmp);
			for(int i=1;i<4;++i){
				if(list.size()<=i) break;
				switch(i){
				case 1:
					day1_date.setText(list.get(i).getDate());
					day1_weather.setText(list.get(i).getWeather() + " " + list.get(i).getTemperature());
					break;
				case 2:
					day2_date.setText(list.get(i).getDate());
					day2_weather.setText(list.get(i).getWeather() + " " + list.get(i).getTemperature());
					break;
				case 3:
					day3_date.setText(list.get(i).getDate());
					day3_weather.setText(list.get(i).getWeather() + " " + list.get(i).getTemperature());
					break;
				}
			}
		}
	};
	/*
	 * 周数格式化
	 */
	private String weekDayTrans(String weekDay){
		if(weekDay.contains("一") || weekDay.contains("1")) return "周一";
		if(weekDay.contains("二") || weekDay.contains("2")) return "周二";
		if(weekDay.contains("三") || weekDay.contains("3")) return "周三";
		if(weekDay.contains("四") || weekDay.contains("4")) return "周四";
		if(weekDay.contains("五") || weekDay.contains("5")) return "周五";
		if(weekDay.contains("六") || weekDay.contains("6")) return "周六";
		return "周日";
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
	    MobclickAgent.onPageStart("Weather");
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Weather");
	    MobclickAgent.onPause(this);
	}
}
