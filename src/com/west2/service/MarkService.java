package com.west2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.west2.db.DbMark;
import com.west2.domain.Credit;
import com.west2.domain.Mark;
import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class MarkService {
	private final String SHARE_MARK = "mark";
	private final String SHARE_CREDIT = "credit";
	private Activity activity;
	private SharedPreferences share_mark;
	private SharedPreferences share_credit;
	public MarkService(Activity activity){
		this.activity = activity;
		share_mark = activity.getSharedPreferences(SHARE_MARK, activity.MODE_PRIVATE);
		share_credit = activity.getSharedPreferences(SHARE_CREDIT, activity.MODE_PRIVATE);
	}
	public String getCurrentTerm(){
		List<String> list = getTermList();
		if (list!=null && list.size()>0)
			return share_mark.getString("term", list.get(list.size()-1));
		else
			return share_mark.getString("term", "");
	}
	public List<String> getTermList(){
		DbMark markHelper = new DbMark(activity);
		markHelper.open();
		List<String> list = new ArrayList<String>();
		Cursor cursor = markHelper.getList();
		while(cursor.moveToNext()){
			String term = cursor.getString(0);
			if(!list.contains(term)) list.add(term);
		}
		cursor.close();
		markHelper.close();
		return list;
	}
	public Credit getCredit(){
		Credit credit = new Credit();
		credit.setCreditLeast(share_credit.getString("creditLeast", ""));
		credit.setCreditTotal(share_credit.getString("creditTotal", ""));
		credit.setCreditTotalMark(share_credit.getString("creditTotalMark", ""));
		credit.setGradePoint(share_credit.getString("gradePoint", ""));
		return credit;
	}
	public void setCurrentTerm(String term){
		Editor editor = share_mark.edit();
		editor.putString("term", term);
		editor.commit();
	}
	public void saveMark(List<String> timeList,Map<String,List<Mark>> map){
		DbMark markHelper = new DbMark(activity);
		markHelper.open();
		markHelper.dropMark();
		markHelper.insertMark(timeList, map);
		markHelper.close();
	}
	public void saveCredit(Credit credit){
		if(credit==null) return ;
		Editor editor = share_credit.edit();
		if(credit.getCreditLeast().equals("null")) credit.setCreditLeast("");
		if(credit.getCreditTotal().equals("null")) credit.setCreditTotal("");
		if(credit.getCreditTotalMark().equals("null")) credit.setCreditTotalMark("");
		if(credit.getGradePoint().equals("null")) credit.setGradePoint("");
		editor.putString("creditLeast", credit.getCreditLeast());
		editor.putString("creditTotal", credit.getCreditTotal());
		editor.putString("creditTotalMark", credit.getCreditTotalMark());
		editor.putString("gradePoint", credit.getGradePoint());
		editor.commit();
	}
	public List<Mark> getMark(String term){
		DbMark markHelper = new DbMark(activity);
		markHelper.open();
		List<Mark> list = new ArrayList<Mark>();
		Cursor cursor = markHelper.getMark(term);
		while(cursor.moveToNext()){
			Mark mark = new Mark();
			mark.setCourseName(cursor.getString(0));
			mark.setScore(cursor.getString(1));
			list.add(mark);
		}
		cursor.close();
		markHelper.close();
		return list;
	}
	public boolean queryCredit(String usernumber,String password){
		Log.e("MarkService", "password = " + password);
		password = new String(Base64.encode(password.getBytes(), Base64.DEFAULT));
//		password = new String(Base64.encode("123456".getBytes(), Base64.DEFAULT));
		Log.e("MarkService", "password = " + password);
		String url = activity.getResources().getString(R.string.url_credit)
			+"num="+usernumber+"&"+"pass="+password;
		String res = HttpUtils.getData(url);
		if(res==null) return false;
		try{
			JSONObject json = new JSONObject(res);
			if(json==null) return false;
			Credit credit = new Credit();
			credit.setCreditLeast(json.getString("creditLeast"));
			credit.setCreditTotal(json.getString("creditTotal"));
			credit.setCreditTotalMark(json.getString("creditTotalMark"));
			credit.setGradePoint(json.getString("gradePoint"));
			saveCredit(credit);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean queryMark(String usernumber){
		String url = activity.getResources().getString(R.string.url_mark) 
			+ "num="+usernumber;
		String res = HttpUtils.getData(url);
		if(res==null) return false;
		List<String> timeList = new ArrayList<String>();
		Map<String,List<Mark>> map = new TreeMap<String,List<Mark>>();
		try{
			JSONObject json = new JSONObject(res);
			if(json==null) return false;
			JSONArray markArray = json.getJSONArray("markArr");
			for(int i=0;i<markArray.length();++i){
				JSONObject item = markArray.getJSONObject(i);
				Mark mark = new Mark();
				mark.setCourseName(item.getString("courseName"));
				mark.setScore(item.getString("sore"));
				String time = item.getString("courseTime");
				if(map.containsKey(time)){
					List<Mark> list = map.get(time);
					list.add(mark);
				}
				else{
					timeList.add(time);
					List<Mark> list = new ArrayList<Mark>();
					list.add(mark);
					map.put(time, list);
				}
			}
			if(map.size()<=0) return false;
			saveMark(timeList,map);
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}