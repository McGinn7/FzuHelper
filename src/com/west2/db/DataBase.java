package com.west2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper{
	/**
	 * Database Version
	 */
	private final static int VERSION = 1;
	/**
	 * Database Name
	 */
	private final static String DATABASE_NAME = "fzu_helper";
	/**
	 * Table List
	 */
	private final String DB_TABLE_MARK = "db_mark";
	private final String DB_TABLE_EXAM = "db_exam";
	private final String DB_TABLE_COURSE = "db_course";
	private final String DB_TABLE_COURSE2 = "db_course2";
	private final String DB_TABLE_WEATHER = "db_weather";
	private final String DB_TABLE_LIBRARY = "db_library";
	/**
	 * Table Columns
	 */
	private final String KEY_ID = "_id";
	/*
	 * Mark Columns	
	 */
	private final String KEY_COURSE = "course_name";
	private final String KEY_SCORE = "course_score";
	private final String KEY_TERM = "term";
	/**
	 * Exam Columns
	 */
	private final String KEY_TEACHER = "teacher_name";
	private final String KEY_DATE = "date";
	private final String KEY_TIME = "time";
	private final String KEY_ROOM = "room";
	/**
	 * Course Columns
	 */
	private final String KEY_WEEK = "week";
	private final String KEY_PLACE = "place";
	private final String KEY_DAY = "day";
	private final String KEY_JIE = "jie";
	private final String KEY_SCHEDULE = "schedule";
	/**
	 * Weather Columns
	 */
	private final String KEY_WEATHER = "weather";
	private final String KEY_WIND = "wind";
	private final String KEY_TEMPERATURE = "temperature";
	/**
	 * Library Columns
	 */
	private final String KEY_BOOKNAME = "bookname";
	private final String KEY_BARCODE = "barcode";
	private final String KEY_COLLECTION = "collection";
	private final String KEY_RETURNDATE = "returndate";
	/**
	 * DataBase Create Statement
	 */
	private final String CREATE_TABLE_MARK = "create table " + DB_TABLE_MARK + " (" 
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_COURSE + " text,"
			+ KEY_TERM + " text,"
			+ KEY_SCORE + " text);";
	private final String CREATE_TABLE_EXAM = "create table " + DB_TABLE_EXAM + " (" 
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_COURSE + " text,"
			+ KEY_TEACHER + " text,"
			+ KEY_DATE + " text,"
			+ KEY_TIME + " text,"
			+ KEY_ROOM + " text);";
	private final String CREATE_TABLE_WEATHER = "create table " + DB_TABLE_WEATHER + " (" 
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_DATE + " text,"
			+ KEY_WEATHER + " text,"
			+ KEY_WIND + " text,"
			+ KEY_TEMPERATURE + " text);";
	private final String CREATE_TABLE_COURSE = "create table " + DB_TABLE_COURSE + " (" 
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_COURSE + " text,"
			+ KEY_PLACE + " text,"
			+ KEY_TEACHER + " text,"
			+ KEY_WEEK + " text,"
			+ KEY_DAY + " text,"
			+ KEY_JIE + " text);";
	private final String CREATE_TABLE_COURSE2 = "create table " + DB_TABLE_COURSE2 + " ("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_SCHEDULE + " text,"
			+ KEY_COURSE + " text);";
	private final String CREATE_TABLE_LIBRARY = "create table " + DB_TABLE_LIBRARY + " ("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_BOOKNAME + " text,"
			+ KEY_BARCODE + " text,"
			+ KEY_COLLECTION + " text,"
			+ KEY_RETURNDATE + " text);";
	public DataBase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public DataBase(Context context, String name,int version) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}
	public DataBase(Context context, String name) {
		super(context, name, null, VERSION);
		// TODO Auto-generated constructor stub
	}
	public DataBase(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_MARK);
		db.execSQL(CREATE_TABLE_EXAM);
		db.execSQL(CREATE_TABLE_WEATHER);
		db.execSQL(CREATE_TABLE_COURSE);
		db.execSQL(CREATE_TABLE_COURSE2);
		db.execSQL(CREATE_TABLE_LIBRARY);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "DROP TABLE IF EXISTS "; 
		db.execSQL(sql + DB_TABLE_EXAM);
		db.execSQL(sql + DB_TABLE_MARK);
		db.execSQL(sql + DB_TABLE_WEATHER);
		db.execSQL(sql + DB_TABLE_COURSE);
		db.execSQL(sql + DB_TABLE_COURSE2);
		db.execSQL(sql + DB_TABLE_LIBRARY);
		onCreate(db);
	}
}