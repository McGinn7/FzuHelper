package com.west2.db;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.west2.db.DatabaseHelper;
import com.west2.domain.Weather;


public class DBHelperForWeather {
	/**
	 * Table Name
	 */
	private final String DATABASE_TABLE = "tb_weather";
	/**
	 * Table columns
	 */
	private final String KEY_ROWID = "_id";
	private final String KEY_DATE = "date";
	private final String KEY_WEATHER = "weather";
	private final String KEY_TEMPERATURE = "temperature";

	private DatabaseHelper dbHelper = null;
	private Context context;
	private SQLiteDatabase mDb;

	public DBHelperForWeather(Context context) {
		this.context = context;
	}

	public void open() {
		dbHelper = new DatabaseHelper(context);
		mDb = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		mDb.close();
	}

	/**
	 * this method is used to insert a lot of new record Weather record
	 * 
	 * @param list
	 */
	public void createWeathers(List<Weather> list) {
		String sql = "insert into " + DATABASE_TABLE + "(" + KEY_DATE + ","
				+ KEY_WEATHER + "," + KEY_TEMPERATURE + ") " + "values(?,?,?)";
		SQLiteStatement stat = mDb.compileStatement(sql);
		mDb.beginTransaction();
		for (Weather c : list) {
			stat.bindString(1, c.getDate());
			stat.bindString(2, c.getWeather());
			stat.bindString(3, c.getTemperature());
			stat.executeInsert();
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
		stat.close();
	}

	/**
	 * This method will return Cursor holding all the Weather records.
	 * 
	 * @return Cursor
	 */
	public Cursor fetchAllWeathers() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_DATE,
				KEY_WEATHER, KEY_TEMPERATURE }, null, null, null, null,
				KEY_ROWID + " asc");
	}

	/**
	 * This method will delete all Weather records.
	 * 
	 */
	public void deleteAllWeathers() {
		mDb.execSQL("delete from " + DATABASE_TABLE);
	}
}
