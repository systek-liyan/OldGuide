package com.systek.guide.db;

import com.systek.guide.common.utils.ExceptionUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{

	Context context;
	private final static int DATABASE_VERSION = 1; 
	public static final String DATABASE_NAME="xUtils.db";
	public static final String DBTAblENAME="CityName";
	public static final String CITY_ID = "id";
	public static final String CITYNAME = "name";
	public static final String CITYALPHA = "alpha";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//创建table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists "+DBTAblENAME +
				"(_id integer primary key autoincrement, " +CITYNAME + " varchar(100) , " +
				CITYALPHA + " varchar(2))";
		db.execSQL(sql);
	} 
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		onCreate(db); 
	} 
	
	public boolean tabIsExist(String tabName){
		boolean result = false;
		if(tabName == null){
			return false;
		}
		Cursor cursor = null;
		SQLiteDatabase db=null;
		try {
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
			db=getWritableDatabase();
			cursor = db.rawQuery(sql, null);
			if(cursor.moveToNext()){
				int count = cursor.getInt(0);
				if(count>0){
					result = true;
				}
			}
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
		finally{
			if(cursor!=null){
				cursor.close();
			}else if(db!=null){
				db.close();
			}
		}
		return result;
	}
	
}
