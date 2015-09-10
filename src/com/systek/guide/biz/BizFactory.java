package com.systek.guide.biz;

import android.content.Context;

public class BizFactory {
	
	public static InterfaceBiz getCityBiz( Context context){
		
		return new CityBiz(context);
	}

}
