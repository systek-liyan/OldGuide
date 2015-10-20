package com.systek.guide.biz;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.biz.impl.GetBeansFromLocal;
import com.systek.guide.biz.impl.GetBeansFromNet;
import com.systek.guide.common.MyApplication;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.DbUtil;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;

import android.content.Context;

public class BeansManageBiz implements IBiz {

	Context context;
	IGetBeanBiz iGetBeanBiz;

	BeansManageBiz(Context context) {
		this.context = context;
	}

	public void setiGetBeanBiz(IGetBeanBiz iGetBeanBiz) {
		this.iGetBeanBiz = iGetBeanBiz;
	}

	public <T> List<T> getAllBeans(Class<T> clazz,String id) {

		List<T> list = null;
		if (DbUtil.isTableExist(context, clazz)) {
			setiGetBeanBiz(new GetBeansFromLocal());
		} else {
			if (MyApplication.currentNetworkType != Const.INTERNET_TYPE_NONE) {
				setiGetBeanBiz(new GetBeansFromNet());
			}else{
				list=new ArrayList<T>();
				return list;
			}
		}
		list = iGetBeanBiz.getAllBeans(context, clazz,id);
		if(iGetBeanBiz instanceof GetBeansFromNet){
			boolean isSaveSucess=saveAllBeans(list);
			LogUtil.i("测试信息", "数据保存"+isSaveSucess);
		}
		return (List<T>) list;
	}


	public <T> T getBeanById(Class<T> entityType, String id) {
		
		T t =DbUtil.findById(context, entityType, id);
		if (t!=null) {
			setiGetBeanBiz(new GetBeansFromLocal());
		} else {
			setiGetBeanBiz(new GetBeansFromNet());
		}
		t = iGetBeanBiz.getBeanById(context, entityType, id);
		if (iGetBeanBiz instanceof GetBeansFromNet) {
			boolean isSaveSucess = saveBean(t);
			LogUtil.i("测试信息", "数据保存" + isSaveSucess);
		}
		return t;
	}


	private <T>boolean saveBean(T t) {
		if (t == null ) {
			return false;
		}
		DbUtils db = DbUtils.create(context);
		try {
			db.save(t);
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return true;
	}

	public <T> boolean saveAllBeans(List<?> list) {
		if (list == null || list.size() == 0) {
			return false;
		}
		DbUtils db = DbUtils.create(context);
		try {
			db.saveAll(list);
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return true;
	}

}
