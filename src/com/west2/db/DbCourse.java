package com.west2.db;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.west2.domain.Course;

public class DbCourse {
	private final String DB_TABLE = "db_course";
	private final String DB_TABLE2 = "db_course2";
	private final String KEY_DAY = "day";
	private final String KEY_JIE = "jie";
	private final String KEY_WEEK = "week";
	private final String KEY_PLACE = "place";
	private final String KEY_SCHEDULE = "schedule";
	private final String KEY_COURSE = "course_name";
	private final String KEY_TEACHER = "teacher_name";
	
	private Context context;
	private DataBase mHelper = null;
	private SQLiteDatabase database = null;

	private final String INSERT2 = "insert into " + DB_TABLE2 + "("
			+ KEY_SCHEDULE + "," + KEY_COURSE + ") " + "values(?,?)";
	private final String INSERT = "insert into " + DB_TABLE + "(" 
		+ KEY_COURSE +","+ KEY_PLACE + "," + KEY_TEACHER + ","
		+ KEY_WEEK + "," + KEY_DAY + "," + KEY_JIE + ") "
		+ "values(?,?,?,?,?,?)";
	private final String DELETE = "delete from " + DB_TABLE;
	private final String DELETE2 = "delete from " + DB_TABLE2;
	private final String[] QUERY2 = {KEY_COURSE};
	private final String[] QUERY = {KEY_COURSE,KEY_PLACE,KEY_TEACHER,KEY_WEEK,KEY_DAY,KEY_JIE};
	
	public DbCourse(Context context){
		this.context = context;
	}
	public void open(){
		mHelper = new DataBase(context);
		database = mHelper.getWritableDatabase();
	}
	public void close(){
		if(mHelper!=null) mHelper.close();
		if(database!=null) database.close();
	}
	public void insertCourse(List<Course> list){
		SQLiteStatement statement = database.compileStatement(INSERT);
		database.beginTransaction();
		for(Course c:list){
			statement.bindString(1, c.getCourseName());
			statement.bindString(2, c.getPlace());
			statement.bindString(3, c.getTeacherName());
			statement.bindString(4, c.getWeek());
			statement.bindString(5, c.getDay());
			statement.bindString(6, c.getJie());
			statement.executeInsert();
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		statement.close();
	}
	public void insertCourse2(List<String> list,List<String> minList,List<String> maxList){
		SQLiteStatement statement = database.compileStatement(INSERT2);
		database.beginTransaction();
		for(int i=0;i<list.size();++i){
			statement.bindString(1, minList.get(i)+"~"+maxList.get(i)+"ÖÜ");
			statement.bindString(2, list.get(i));
			statement.executeInsert();
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		statement.close();
	}
	public Cursor getCourseList(){
		return database.query(DB_TABLE2, QUERY2,
			null, null, null, null, KEY_COURSE + " asc");
	}
	public Cursor getWeekList(){
		return database.query(DB_TABLE, new String[]{KEY_WEEK},
			null, null, null, null, KEY_WEEK+" asc");
	}
	public Cursor getSchedule(String course){
		return database.query(DB_TABLE2, new String[]{KEY_SCHEDULE},
			KEY_COURSE+"=?", new String[]{course}, null, null, null);
	}
	public Cursor getCourseOfWeek(String week){
		return database.query(DB_TABLE, QUERY,
			KEY_WEEK+"=?", new String[]{week}, null, null, null);
	}
	public void dropCourse(){
		database.execSQL(DELETE);
		database.execSQL(DELETE2);
	}
}