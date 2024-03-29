package com.systek.guide.common.utils;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import android.content.Context;

public class DbUtil {
	public static <T> boolean isTableExist(Context context,Class<T> entityType){
		DbUtils db=DbUtils.create(context);
		boolean isTableExist=false;
		try {
			isTableExist = db.tableIsExist(entityType);
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
		}finally{
			if(db!=null){
				db.close();
			}
		}
		return isTableExist;
	}
		public static <T> T findById(Context context,Class<T> entityType,String id){
			DbUtils db=DbUtils.create(context);
			T t=null;
			try {
				  t =db.findById(entityType, id);
			} catch (DbException e) {
				ExceptionUtil.handleException(e);
			}finally{
				if(db!=null){
					db.close();
				}
			}
			return t;
		}
}
