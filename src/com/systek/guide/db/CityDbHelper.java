package com.systek.guide.db;

import com.systek.guide.common.utils.ExceptionUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CityDbHelper extends SQLiteOpenHelper{

	Context context;
	private SQLiteDatabase db;
	private final static String DATABASE_NAME = "CityName.db"; 
	private final static int DATABASE_VERSION = 1; 
	private final static String TABLE_NAME = "CityName"; 
	public final static int CITY_ID = 0; 
	public final static String CITYNAME = "name"; 
	public final static String CITYALPHA = "alpha";

	public CityDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//创建table 
	@Override
	public void onCreate(SQLiteDatabase db) { 
		
	} 
	
	public void createCityDB(SQLiteDatabase db){
		String sql1 = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql1);
		String sql = "CREATE TABLE " + TABLE_NAME + " (" + CITY_ID 
				+ " INTEGER primary key autoincrement, " + CITYNAME + " varchar(100), "+ CITYALPHA +" varchar(2));"; 
		db.execSQL(sql); 
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME; 
		db.execSQL(sql); 
		onCreate(db); 
	} 

	public Cursor select() { 
		SQLiteDatabase db = this.getReadableDatabase(); 
		Cursor cursor = db .query(TABLE_NAME, null, null, null, null, null, null); 
		return cursor;
	} 
	//增加操作 
	public long insert(String cityName,String alpha) 
	{ 
		SQLiteDatabase db = this.getWritableDatabase(); 
		/* ContentValues */
		ContentValues cv = new ContentValues(); 
		cv.put(CITYNAME, cityName); 
		cv.put(CITYNAME, alpha); 
		long row = db.insert(TABLE_NAME, null, cv); 
		return row;
	} 
	//删除操作 
	public void delete(int id) 
	{ 
		SQLiteDatabase db = this.getWritableDatabase(); 
		String where = CITY_ID + " = ?"; 
		String[] whereValue ={ Integer.toString(id) }; 
		db.delete(TABLE_NAME, where, whereValue); 
	} 
	//修改操作 
	public void update(int id, String cityName,String alpha) 
	{ 
		SQLiteDatabase db = this.getWritableDatabase(); 
		String where = CITY_ID + " = ?"; 
		String[] whereValue = { Integer.toString(id) }; 

		ContentValues cv = new ContentValues(); 
		cv.put(CITYNAME, cityName); 
		cv.put(CITYALPHA, alpha); 
		db.update(TABLE_NAME, cv, where, whereValue); 
	} 

	public boolean tabIsExist(String tabName){
		boolean result = false;
		if(tabName == null){
			return false;
		}
		Cursor cursor = null;
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
