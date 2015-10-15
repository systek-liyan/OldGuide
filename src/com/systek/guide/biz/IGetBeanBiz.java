package com.systek.guide.biz;

import java.util.List;

import android.content.Context;

public interface IGetBeanBiz {
	
	<T>List<T>  getBeans(Context context,Class<T> entityType,String Id);
}
