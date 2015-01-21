package com.west2.db;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.west2.domain.Exam;

public class DbExam {
	private final String DB_TABLE = "db_exam";
	private final String KEY_DATE = "date";
	private final String KEY_TIME = "time";
	private final String KEY_ROOM = "room";
	private final String KEY_COURSE = "course_name";
	private final String KEY_TEACHER = "teacher_name";

	
	private DataBase mHelper = null;
	private SQLiteDatabase database;
	private Context context;
	
	private final String INSERT = "insert into " + DB_TABLE + "("
		+ KEY_COURSE +","+ KEY_TEACHER + "," + KEY_DATE + ","
		+ KEY_TIME + "," + KEY_ROOM + ") " + "values(?,?,?,?,?)";
	private final String DELETE = "delete from " + DB_TABLE;
	private final String[] QUERY = {
		KEY_COURSE,KEY_TEACHER,KEY_DATE,KEY_TIME,KEY_ROOM
	};
	
	public DbExam(Context context){
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
	public void insertExam(List<Exam> list){
		SQLiteStatement statement = database.compileStatement(INSERT);
		database.beginTransaction();
		for(Exam e:list){
			statement.bindString(1, e.getCourseName());
			statement.bindString(2, e.getTeacherName());
			statement.bindString(3, e.getDate());
			statement.bindString(4, e.getTime());
			statement.bindString(5, e.getRoom());
			statement.executeInsert();
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		statement.close();
	}
	public Cursor getExam(){
		return database.query(DB_TABLE, QUERY,
			null, null, null, null, null);
	}
	public void dropExam(){
		database.execSQL(DELETE);
	}
}