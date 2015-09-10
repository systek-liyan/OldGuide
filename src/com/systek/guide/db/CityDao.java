package com.systek.guide.db;


import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.entity.CityModel;
import com.systek.guide.entity.ModelInterface;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CityDao implements InterfaceDao{

	private CityDbHelper helper;

	public CityDao(CityDbHelper helper) {
		super();
		this.helper = helper;
	}

	@Override
	public boolean insert(ModelInterface model) {
		boolean flag = false;
		if(model instanceof CityModel){
			CityModel city=(CityModel)model;
			SQLiteDatabase database = null;
			long id = -1;
			try {
				database = helper.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put("name", city.getName());
				values.put("alpha", city.getAlpha());
				id = database.insert(CityDbHelper.DBTAblENAME, null, values);
				flag = (id != -1 ? true : false);
			} catch (Exception e) {
				ExceptionUtil.handleException(e);
			} finally {
				if (database != null) {
					database.close();
				}
			}
		}
		return flag;

	}

	@Override
	public void remove() {
	}

	@Override
	public void update() {

	}

	@Override
	public void query() {

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
			db=helper.getWritableDatabase();
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
	@Override
	public Cursor select(String tableName) { 
		SQLiteDatabase db = helper.getReadableDatabase(); 
		Cursor cursor = db .query(tableName, null, null, null, null, null, null); 
		return cursor;
	}

}
