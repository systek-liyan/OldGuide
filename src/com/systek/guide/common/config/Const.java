package com.systek.guide.common.config;

import android.os.Environment;

public class Const {
	public static final String CITYLISTURL = "http://182.92.82.70/api/cityService/cityList";
	/*网络状态*/
	public static final int TYPE_WIFI=1;
	public static final int TYPE_MOBILE=2;
	public static final int TYPE_NONE=3;
	/*用于MuseumActivity界面*/
	public static final String CITY_MUSEUM="city";
	
	/*存储图片的位置*/
	public static final String SDCARD_ROOT=Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String LOCAL_IMAGE_PATH=SDCARD_ROOT+"/Guide/image";
	
	/*服务器上博物馆图片的域名加端口号*/		//此处先随便写个
	public static final String INTERNET_IMAGE_PATH="http://124.207.192.18:8080";
	
	
}
