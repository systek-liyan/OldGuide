package com.systek.guide.biz.impl;

import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.biz.IGetBeanBiz;
import com.systek.guide.common.utils.ExceptionUtil;

import android.content.Context;

public class GetBeansFromLocal implements IGetBeanBiz{
	
	@Override
	public <T> List<T> getAllBeans(Context context, Class<T> entityType,String condition) {
		
		DbUtils db=DbUtils.create(context);
		List<T> list=null;
		try {
				list=db.findAll(entityType);				
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
		}finally{
			if(db!=null){
				db.close();
			}
		}
		return list;
	}

	@Override
	public <T> T getBeanById(Context context, Class<T> entityType, String id) {
		DbUtils db=DbUtils.create(context);
		T t=null;
		try {
				t=db.findById(entityType, id);			
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
