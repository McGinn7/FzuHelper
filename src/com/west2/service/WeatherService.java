package com.west2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

import com.west2.db.DbWeather;
import com.west2.domain.Weather;
import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class WeatherService {
	private final String SHARE_TIP = "tip";
	private SharedPreferences share;
	private Activity activity;
	public final static String url = "http://api.map.baidu.com/telematics/v3/weather?location=%e9%97%bd%e4%be%af&output=json&ak=j2qSuA501kLgX2jQPID8WOX7";

	public WeatherService(Activity activity){
		this.activity = activity;
		share = activity.getSharedPreferences(SHARE_TIP, activity.MODE_PRIVATE);
	}
	public int getIconWeather(String weather){
		if (weather.contains("雨")) 		return R.drawable.weather_1_rain;
		if (weather.contains("阴")) 		return R.drawable.weather_2_cloud;
		if (weather.contains("多云"))	return R.drawable.weather_3_cloud_sun;
		if (weather.contains("晴"))		return R.drawable.weather_5_sun;
		return R.drawable.weather_5_sun;
	}
	private void setTips(String name,String tip){
		if(tip==null) return ;
		Editor editor = share.edit();
		editor.putString(name, tip);
		editor.commit();
	}
	public void saveWeather(List<Weather> list){
		DbWeather helper = new DbWeather(activity);
		helper.open();
		helper.dropWeather();
		helper.insertWeather(list);
		helper.close();
	}
	public List<Weather> getWeather(){
		DbWeather helper = new DbWeather(activity);
		helper.open();
		Cursor cursor = helper.getWeather();
		Weather weather;
		List<Weather> list = new ArrayList<Weather>();
		while(cursor.moveToNext()){
			weather = new Weather();
			weather.setDate(cursor.getString(1));
			weather.setWeather(cursor.getString(2));
			weather.setWind(cursor.getString(3));
			weather.setTemperature(cursor.getString(4));
			list.add(weather);
		}
		cursor.close();
		helper.close();
		return list;
	}
	public boolean queryWeather(){
		String res = HttpUtils.getData2(url);
		if(res == null) return false;
		try {
			JSONObject json = new JSONObject(res);
			String status = json.getString("status");
			if(status==null || !status.equals("success")) return false;
			JSONArray resArray = json.getJSONArray("results");
			/*
			 * Get Tips
			 */
			JSONArray indexArray = resArray.getJSONObject(0).getJSONArray("index");
			String desCold = indexArray.getJSONObject(3).getString("des");
			String desSport = indexArray.getJSONObject(4).getString("des");
			setTips("desCold",desCold);
			setTips("desSport",desSport);
			/*
			 * Get Weather
			 */
			JSONArray weatherArray = resArray.getJSONObject(0).getJSONArray("weather_data");
			List<Weather> resList = new ArrayList<Weather>(weatherArray.length());
			Weather weather;
			JSONObject item;
			String temp;
			// 获取未来天气
			for(int i=0;i<weatherArray.length();++i){
				item = weatherArray.getJSONObject(i);
				weather = new Weather();
				temp = item.getString("date");
				weather.setDate(item.getString("date"));
				weather.setWeather(item.getString("weather"));
				weather.setWind(item.getString("wind"));
				weather.setTemperature(item.getString("temperature"));
				// 获取实时温度
				if(i==0){
					temp = item.getString("date"); 
					Pattern pattern = Pattern.compile("\\(实时：.+?\\)");
					Matcher matcher = pattern.matcher(temp);
					if(matcher.find()){
						String tmp = matcher.group().toString();
						tmp = tmp.replace("(实时：", "");
						tmp = tmp.replace(")", "");
						weather.setTemperature(tmp);
					}
					else{
						pattern = Pattern.compile("[0-9]+℃");
						matcher = pattern.matcher(item.getString("temperature"));
						if(matcher.find()){
							String tmp = matcher.group().toString();
							weather.setTemperature(tmp);
						}
					}
				}
				resList.add(weather);
			}
			if(resList.size()<=0) return false;
			saveWeather(resList);
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}