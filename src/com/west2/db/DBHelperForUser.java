package com.west2.db;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
public class DBHelperForUser {
	/**
	 * Table Name
	 */
	private final String DATABASE_TABLE = "tb_user";
	/**
	 * Table columns
	 */
	public final String KEY_NAME = "name";
	public final String KEY_PASSWORD = "password";
	public final String KEY_REALNAME = "realName";
	public final String KEY_ROWID = "_id";

	private DatabaseHelper dbHelper = null;
	private Context context;
	private SQLiteDatabase mDb;

	public DBHelperForUser(Context context) {
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
	 * This method is used to create/insert new record User record.
	 * 
	 * @param name
	 * @param grade
	 * @return long
	 */
	public long createUser(String name, String password, String realName) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_PASSWORD, password);
		initialValues.put(KEY_REALNAME, realName);
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * This method will delete User record.
	 * 
	 * @param rowId
	 * @return boolean
	 */
	public boolean deleteUser(long rowId) {
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * This method will return Cursor holding all the User records.
	 * 
	 * @return Cursor
	 */
	public Cursor fetchAllUsers() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_NAME,
				KEY_PASSWORD, KEY_REALNAME }, null, null, null, null, null);
	}

	/**
	 * This method will return Cursor holding the specific User record.
	 * 
	 * @param id
	 * @return Cursor
	 * @throws SQLException
	 */
	public Cursor fetchUser(long id) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_PASSWORD, KEY_REALNAME }, KEY_ROWID
				+ "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * This method will update User record.
	 * 
	 * @param id
	 * @param name
	 * @param password
	 * @return boolean
	 */
	public boolean updateUser(int id, String name, String password,
			String realName) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_PASSWORD, password);
		args.put(KEY_REALNAME, realName);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + id, null) > 0;
	}
}
