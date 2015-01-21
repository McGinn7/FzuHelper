package com.west2.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

//DatabaseHelper作为一个访问SQLite的助手类，提供两方面的功能
//第一，getReadableDatabase() getWritableDatabase()可以获得SQLiteDatabase对象,通过该对象对数据库进行操作
//第二，提供了onCreate()和onUpgrade()两个回调函数，允许我们在创建和升级数据库时候，进行自己的操作

public class DatabaseHelper extends SQLiteOpenHelper{
	/**
	 * Database Version
	 */
	private static final int VERSION = 1;

	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "fzu_database";

	/**
	 * Table Name
	 */
	private final String DATABASE_TABLE_USER = "tb_user";
	private final String DATABASE_TABLE_MARK = "tb_mark";
	private final String DATABASE_TABLE_EXAM = "tb_exam";
	private final String DATABASE_TABLE_SCHEDULE = "tb_schedule";
	private final String DATABASE_TABLE_WEEK = "tb_week";
	private final String DATABASE_TABLE_WEATHER = "tb_weather";
	/**
	 * Table columns
	 */
	private final String KEY_NAME = "name";
	private final String KEY_PASSWORD = "password";
	private final String KEY_REALNAME = "realName";
	private final String KEY_ROWID = "_id";
	private final String KEY_SCORE = "score";
	private final String KEY_TIME = "time";

	// exam
	private final String KEY_COURSE = "couse";
	private final String KEY_TEACHER = "teacher";
	private final String KEY_DATE = "date";
	private final String KEY_ROOM = "room";

	// schedule
	private final String KEY_WEEK = "week";
	private final String KEY_PLACE = "place";
	private final String KEY_WEEKDAY = "weekday";

	private final String KEY_WEEKDAYNOW = "weeknow";
	private final String KEY_WEEKNUM = "weeknum";

	// weather
	private final String KEY_WEATHER = "weather";
	private final String KEY_TEMPERATURE = "temperature";

	/**
	 * Database creation sql statement
	 */
	private final String CREATE_USER_TABLE = "create table "
			+ DATABASE_TABLE_USER + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_PASSWORD + " text not null, "
			+ KEY_REALNAME + " text not null);";
	private final String CREATE_MARK_TABLE = "create table "
			+ DATABASE_TABLE_MARK + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_SCORE + " text not null," + KEY_TIME
			+ " text not null);";
	private final String CREATE_EXAM_TABLE = "create table "
			+ DATABASE_TABLE_EXAM + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_COURSE
			+ " text not null, " + KEY_TEACHER + " text," + KEY_DATE + " text,"
			+ KEY_ROOM + " text," + KEY_TIME + " text);";
	private final String CREATE_SCHEDULE_TABLE = "create table "
			+ DATABASE_TABLE_SCHEDULE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_COURSE
			+ " text not null, " + KEY_WEEK + " text," + KEY_PLACE + " text,"
			+ KEY_TEACHER + " text," + KEY_TIME + " INTEGER," + KEY_WEEKDAY
			+ " INTEGER," + KEY_WEEKDAYNOW + " INTEGER," + KEY_WEEKNUM
			+ " INTEGER);";
	private final String CREATE_SCHEDULE_WEEK = "create table "
			+ DATABASE_TABLE_WEEK + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + " INTEGER,"
			+ KEY_WEEKDAYNOW + " INTEGER," + KEY_WEEKNUM + " INTEGER);";
	private final String CREATE_SCHEDULE_WEATHER = "create table "
			+ DATABASE_TABLE_WEATHER + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + " INTEGER," + KEY_DATE
			+ " text," + KEY_WEATHER + " text," + KEY_TEMPERATURE + " text);";

	// 在SQLiteOpenHelper的子类中，必须有构造函数

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, VERSION);
	}

	// 该函数是在第一次创建数据库的时候执行，实际上是在第一次得到SQLiteDatabase对象的时候
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_USER_TABLE);
		db.execSQL(CREATE_MARK_TABLE);
		db.execSQL(CREATE_EXAM_TABLE);
		db.execSQL(CREATE_SCHEDULE_TABLE);
		db.execSQL(CREATE_SCHEDULE_WEEK);
		db.execSQL(CREATE_SCHEDULE_WEATHER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
