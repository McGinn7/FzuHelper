package com.west2.db;

import java.util.List;
import java.util.Map;

import com.west2.domain.Mark;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class DbMark {
	private final String DB_TABLE = "db_mark";
	private final String KEY_COURSE = "course_name";
	private final String KEY_TERM = "term";
	private final String KEY_SCORE = "course_score";
	
	private Context context;
	private DataBase mHelper = null;
	private SQLiteDatabase database = null;

	private final String INSERT = "insert into " + DB_TABLE 
		+ "(" + KEY_COURSE +","+ KEY_TERM + "," + KEY_SCORE + ") "
		+ "values(?,?,?)";
	private final String DELETE = "delete from " + DB_TABLE;
	private final String[] QUERY = { KEY_COURSE,KEY_SCORE };
	
	public DbMark(Context context){
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
	public void insertMark(List<String> list,Map<String,List<Mark>> map){
		SQLiteStatement statement = database.compileStatement(INSERT);
		database.beginTransaction();
		for(int i=0;i<list.size();++i){
			List<Mark> mList = map.get(list.get(i));
			for(Mark m:mList){
				statement.bindString(1, m.getCourseName());
				statement.bindString(2, list.get(i));
				statement.bindString(3, m.getScore());
				statement.executeInsert();
			}
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		statement.close();
	}
	public Cursor getList(){
		return database.query(DB_TABLE, new String[]{KEY_TERM},
			null, null, null, null, KEY_TERM+" asc");
	}
	public Cursor getMark(String time){
		return database.query(DB_TABLE, QUERY, 
				KEY_TERM+"=?", new String[]{time}, null, null, KEY_TERM+" asc");
	}
	public void dropMark(){
		database.execSQL(DELETE);
	}
}