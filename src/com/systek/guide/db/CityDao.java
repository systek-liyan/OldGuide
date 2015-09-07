package com.systek.guide.db;

import java.util.ArrayList;
import java.util.List;

import com.systek.guide.common.config.Constants;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.entity.CityModule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CityDao {
	Context context;
	private SQLiteDatabase db;

	private CityDao() {
	}  
	private static final CityDao single = new CityDao();  
	//静态工厂方法   
	public static CityDao getInstance() {  
		return single;  
	}  

	public void createDB(String name){
		if(tabIsExist(name)){
			db.execSQL("DROP TABLE IF EXISTS "+ name);
		}
		db.execSQL("CREATE TABLE "+name+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, alpha VARCHAR)");
	}
	/* 添加记录 */
	public void add(CityModule city,CityDbHelper helper, ContentValues values,String name) {
		try {

			db = helper.getWritableDatabase();// 以写的方式初始化SQLiteDatabase对象
			db.insert(name, null, values);
			/*db.execSQL("insert into CityName(_id,alpha,name)" + "values (?,?,?,?,?)",
				new Object[] { city.getAlpha(), city.getName() });*/
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}finally{
			if(db!=null){
				db.close();
			}else if(helper!=null){
				helper.close();
			}
		}


	}

	/* 更新数据 */
	public void update(CityModule city,CityDbHelper helper) {
		try {
			db = helper.getWritableDatabase();// 以写的方式初始化SQLiteDatabase对象
			db.execSQL("update CityName set alpha = ?,name = ?", 
					new Object[] { city.getAlpha(), city.getName() });
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}finally{
			if(db!=null){
				db.close();
			}else if(helper!=null){
				helper.close();
			}
		}
	}

	/* 查找信息 */
	public CityModule find(CityDbHelper helper,String name) {
		CityModule city = null;
		Cursor cursor=null;
		try {
			db = helper.getWritableDatabase();
			cursor = db.rawQuery("select * from CityName", // 根据名字查询
					new String[] { name });
			if (cursor.moveToNext()) {// 遍历查找到的信息
				city=new CityModule(cursor.getString(
						cursor.getColumnIndex("name")),
						cursor.getString(cursor.getColumnIndex("alpha")));
			}
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}finally{
			if(db!=null){
				db.close();
			}else if(cursor!=null){
				cursor.close();
			}else if(helper!=null){
				helper.close();
			}
		}
		return city;
	}

	/* 查找所有信息 */
	public List<CityModule> findAll(CityDbHelper helper) {
		List<CityModule>list = new ArrayList<CityModule>();
		Cursor cursor=null;
		try {
			db= helper.getWritableDatabase();
			cursor = db.rawQuery("select * from CityName", null);
			while(cursor.moveToNext()) {
				list.add(new CityModule(
						cursor.getString(cursor.getColumnIndex("name")),
						cursor.getString(cursor.getColumnIndex("alpha"))));
			}
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}finally{
			if(db!=null){
				db.close();
			}else if(cursor!=null){
				cursor.close();
			}else if(helper!=null){
				helper.close();
			}
		}
		return list;
	}

	public boolean tabIsExist(String tabName){
		boolean result = false;
		if(tabName == null){
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
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
