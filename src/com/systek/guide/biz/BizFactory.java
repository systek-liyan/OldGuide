package com.systek.guide.biz;

import android.content.Context;

public class BizFactory {
	
	public static IBiz getCityBiz( Context context){
		
		return new CityManageBiz(context);
	}
	public static IBiz getDownloadBiz(Context context){
		
		return new DownloadBiz(context);
	}
	public static IBiz getBeansManageBiz(Context context){
		
		return new BeansManageBiz(context);
	}
	
}
