package com.systek.guide.db;

import com.systek.guide.entity.BeanInterface;

import android.database.Cursor;

public interface InterfaceDao {
	boolean insert(BeanInterface model);
	void remove();
	void update();
	void query();
	Cursor select(String tableName);
}
