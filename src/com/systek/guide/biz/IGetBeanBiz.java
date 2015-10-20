package com.systek.guide.biz;

import java.util.List;

import android.content.Context;

public interface IGetBeanBiz {
	
	<T>List<T>  getAllBeans(Context context,Class<T> entityType,String Id);
	<T> T  getBeanById(Context context,Class<T> entityType,String Id);
}
