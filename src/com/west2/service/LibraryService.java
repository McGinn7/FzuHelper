package com.west2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Base64;

import com.west2.db.DbLibrary;
import com.west2.domain.Book;
import com.west2.domain.MyBook;
import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class LibraryService {
	public static final String SHARED_USER_INFO="libray_user";
	private static Context context;
	private static SharedPreferences share_user;

	public LibraryService(Context context){
		this.context = context;
	}
	public LibraryService(Activity activity){
		this.context = activity.getApplicationContext();
		share_user = context.getSharedPreferences(SHARED_USER_INFO,context.MODE_PRIVATE);
	}
	/* login
	 *  0:网络错误
	 *  1:成功
	 * -1:用户名为空
	 * -2:密码为空
	 * -3:用户名或密码包含非法字符
	 * -4:用户名或密码错误
	 */
	public int login(){
		String username = getUsername();
		if(username==null || username.equals("")) return -1;
		String password = getPassword();
		if(password==null || password.equals("")) return -2;
		if(username.contains(" ") || password.contains(" ")) return -3;
		password = new String(Base64.encode(password.getBytes(), Base64.DEFAULT));
		String url = context.getResources().getString(R.string.url_library)
				+"num="+username+"&pass="+password;
		String res = HttpUtils.getData(url);
		if(res==null) return 0;
		try {
			JSONObject json = new JSONObject(res);
			if(json.has("error")) return -4;
			List<MyBook> list = new ArrayList<MyBook>();
			JSONArray array = json.getJSONArray("bookArr");
			for(int i=0;i<array.length();++i){
				JSONObject item = array.getJSONObject(i);
				if(item.getString("bookName")==null || item.getString("bookName").equals("")) continue;
				MyBook book = new MyBook();
				book.setBookName(item.getString("bookName"));
				book.setBarCode(item.getString("barCode"));
				book.setCollection(item.getString("Collection"));
				book.setReturnDate(item.getString("returnDate"));
				list.add(book);
			}
			saveMyBook(list);
			return 1;
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}
	public void saveMyBook(List<MyBook> list){
		DbLibrary mHelper = new DbLibrary(context);
		mHelper.open();
		mHelper.dropTable();
		mHelper.insertMyBook(list);
		mHelper.close();
	}
	public List<MyBook> getMyBook(){
		DbLibrary mHelper = new DbLibrary(context);
		mHelper.open();
		Cursor cursor = mHelper.getMyBook();
		MyBook book;
		List<MyBook> list = new ArrayList<MyBook>();
		while(cursor.moveToNext()){
			book = new MyBook();
			book.setBookName(cursor.getString(0));
			book.setBarCode(cursor.getString(1));
			book.setCollection(cursor.getString(2));
			book.setReturnDate(cursor.getString(3));
			list.add(book);
		}
		cursor.close();
		mHelper.close();
		return list;
	}
	/**
	 * @param username
	 * @param password
	 * @param book
	 * @return
	 * 	0 : 预约失败
	 *  1 : 预约成功
	 *  2 : 账号或密码错误
	 */
	public int reserBook(String username,String password,Book book){
		String place = book.getPlace().replace("/", "%2F");
		if(place == null || place.equals("")) return 0;
		password = new String(Base64.encode(password.getBytes(), Base64.DEFAULT));
		String url = context.getResources().getString(R.string.url_library_reser)
				+"user="+username+"&password="+password
				+"&place="+place+"&appointment_id="+book.getId();
		String res = HttpUtils.getData(url);
		if(res==null) return 0;
		try {
			JSONObject json = new JSONObject(res);
			String status = json.getString("res");
			if(status.contains("成功")) return 1;
			if(status.contains("失败")) return 0;
			return 2;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * @param
	 *  book : 续借的书
	 * @return
	 * 0 : 失败
	 * 1 : 续借成功
	 * 2 : 已过期
	 * 3 : 续借上限
	 */
	public int renewBook(MyBook book){
		if(book.getBookName().equals("")) return 0;
		String url = context.getResources().getString(R.string.url_library_renew)
				+"book_barcode="+book.getBarCode();
		String res = HttpUtils.getData(url);
		if(res==null) return 0;
		try {
			JSONObject json = new JSONObject(res);
			String status = json.getString("status");
			if(status.equals("1")) return 1;
			if(status.equals("2")) return 2;
			if(status.equals("3")) return 3;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public List<Book> queryBook(String key,int page){
		String url = context.getResources().getString(R.string.url_library_getbook)
			+"key="+key+"&page="+page;
		List<Book> list = new ArrayList<Book>();
		String res = HttpUtils.getData(url);

		if(res==null) return list;
		System.out.println(res);
		try{
			Matcher matcher;
			Pattern patStore = Pattern.compile("馆藏数：[0-9]+");
			Pattern patAmount = Pattern.compile("可借数：[0-9]+");
			JSONArray json = new JSONArray(res);
			for(int i=0;i<json.length();++i){
				JSONObject item = json.getJSONObject(i);
				if(item.getString("name").equals("")) break;
				Book book = new Book();
				book.setNumber(item.getString("num"));
				book.setName(item.getString("name"));
				book.setUrl(item.getString("url"));
				book.setAuthor(item.getString("author"));
				book.setPublisher(item.getString("publisher"));
				book.setId(item.getString("appointment_id"));
				book.setYear(item.getString("year"));
				book.setPlace(item.getString("place"));
				String temp = item.getString("amount");
				matcher = patStore.matcher(temp);
				if(matcher.find()){
					String count = matcher.group().toString();
					count = count.replace("馆藏数：", "");
					book.setStore(Integer.parseInt(count));
				}
				matcher = patAmount.matcher(temp);
				if(matcher.find()){
					String count = matcher.group().toString();
					count = count.replace("可借数：", "");
					book.setCntAmount(Integer.parseInt(count));
				}
				list.add(book);
			}
			return list;
		}
		catch(Exception e){
			e.printStackTrace();
			return list;
		}
	}
	public String getUsername(){
		return share_user.getString("username", "");
	}
	public String getPassword(){
		return share_user.getString("password", "");
	}
	public void setUsername(String name){
		Editor editor = share_user.edit();
		editor.putString("username", name);
		editor.commit();
	}
	public void setPassword(String password){
		Editor editor = share_user.edit();
		editor.putString("password", password);
		editor.commit();
	}
}