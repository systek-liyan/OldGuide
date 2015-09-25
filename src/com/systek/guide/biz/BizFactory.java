package com.systek.guide.biz;

import android.content.Context;

public class BizFactory {
	
	public static InterfaceCityBiz getCityBiz( Context context){
		
		return new CityBiz(context);
	}
	public static InterfaceDownloadManageBiz getDownloadBiz(Context context){
		
		return new DownloadBiz(context);
	}
	
}
