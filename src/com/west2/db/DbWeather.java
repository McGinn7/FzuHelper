package com.west2.db;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.west2.domain.Weather;

public class DbWeather {
	private final String DB_TABLE = "db_weather";
	private final String KEY_ID = "_id";
	private final String KEY_DATE = "date";
	private final String KEY_WIND = "wind";
	private final String KEY_WEATHER = "weather";
	private final String KEY_TEMPERATURE = "temperature";
	
	private DataBase mHelper = null;
	private SQLiteDatabase database;
	private Context context;
	
	private final String INSERT = "insert into " + DB_TABLE + "("
		+ KEY_DATE +","+ KEY_WEATHER + "," + KEY_WIND + "," + KEY_TEMPERATURE + ") "
		+ "values(?,?,?,?)";
	private final String DELETE = "delete from "+DB_TABLE;
	private final String[] QUERY = 
		{KEY_ID,KEY_DATE,KEY_WEATHER,KEY_WIND,KEY_TEMPERATURE};
	
	public DbWeather(Context context){
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
	public void insertWeather(List<Weather> list){
		SQLiteStatement statement = database.compileStatement(INSERT);
		database.beginTransaction();
		for(Weather w:list){
			statement.bindString(1, w.getDate());
			statement.bindString(2, w.getWeather());
			statement.bindString(3, w.getWind());
			statement.bindString(4, w.getTemperature());
			statement.executeInsert();
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		statement.close();
	}
	public Cursor getWeather(){
		return database.query(DB_TABLE, QUERY,
			null, null, null, null, KEY_ID+" asc");
	}
	public void dropWeather(){
		database.execSQL(DELETE);
	}
}