package com.systek.guide.db;


import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.entity.CityModel;
import com.systek.guide.entity.ModelInterface;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Dao implements InterfaceDao{

	private DbHelper helper;

	public Dao(DbHelper helper) {
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
				id = database.insert(DbHelper.DBTAblENAME, null, values);
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

	@Override
	public Cursor select(String tableName) { 
		SQLiteDatabase db = helper.getReadableDatabase(); 
		Cursor cursor = db .query(tableName, null, null, null, null, null, null); 
		return cursor;
	}

}
