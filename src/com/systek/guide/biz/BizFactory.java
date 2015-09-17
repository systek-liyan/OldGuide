package com.systek.guide.biz;

import android.content.Context;

public class BizFactory {
	
	public static BizInterface getCityBiz( Context context){
		
		return new CityBiz(context);
	}
	public static BizInterface getDownloadBiz(){
		
		return new DownloadBiz();
	}
	
}
