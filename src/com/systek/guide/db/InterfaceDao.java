package com.systek.guide.db;

import com.systek.guide.entity.ModelInterface;

import android.database.Cursor;

public interface InterfaceDao {
	boolean insert(ModelInterface model);
	void remove();
	void update();
	void query();
	Cursor select(String tableName);
}
