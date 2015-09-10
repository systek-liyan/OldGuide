package com.systek.guide.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CityDbHelper extends SQLiteOpenHelper{

	Context context;
	private final static int DATABASE_VERSION = 1; 
	public static final String DBCITYNAME="CityName.db";
	public static final String DBTAblENAME="CityName";
	public static final String CITY_ID = "id";
	public static final String CITYNAME = "name";
	public static final String CITYALPHA = "alpha";

	public CityDbHelper(Context context) {
		super(context, DBCITYNAME, null, DATABASE_VERSION);
	}

	//创建table 
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql1 = "drop table if exists " + DBTAblENAME;
		db.execSQL(sql1);
		String sql = "create table if not exists "+DBTAblENAME +
				"(_id integer primary key autoincrement, " +CITYNAME + " varchar(100) , " +
				CITYALPHA + " varchar(2))";
		db.execSQL(sql);
	} 
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		onCreate(db); 
	} 

}
