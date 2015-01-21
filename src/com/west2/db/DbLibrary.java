package com.west2.db;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.west2.domain.MyBook;

public class DbLibrary {
	private final String DB_TABLE = "db_library";
	private final String KEY_BOOKNAME = "bookname";
	private final String KEY_BARCODE = "barcode";
	private final String KEY_COLLECTION = "collection";
	private final String KEY_RETURNDATE = "returndate";
	
	private Context context;
	private DataBase mHelper = null;
	private SQLiteDatabase database = null;

	private final String INSERT = "insert into " + DB_TABLE 
		+ "(" + KEY_BOOKNAME +","+ KEY_BARCODE + "," + KEY_COLLECTION + "," + KEY_RETURNDATE + ") "
		+ "values(?,?,?,?)";
	private final String DELETE = "delete from " + DB_TABLE;
	private final String[] QUERY = { KEY_BOOKNAME,KEY_BARCODE,KEY_COLLECTION,KEY_RETURNDATE };
	
	public DbLibrary(Context context){
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
	public void insertMyBook(List<MyBook> list){
		SQLiteStatement statement = database.compileStatement(INSERT);
		database.beginTransaction();
		for(MyBook m:list){
			statement.bindString(1, m.getBookName());
			statement.bindString(2, m.getBarCode());
			statement.bindString(3, m.getCollection());
			statement.bindString(4, m.getReturnDate());
			statement.executeInsert();
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		statement.close();
	}
	public Cursor getMyBook(){
		return database.query(DB_TABLE, QUERY, 
				null, null, null, null, null);
	}
	public void dropTable(){
		database.execSQL(DELETE);
	}
}