package com.systek.guide.common;

import java.util.ArrayList;

import com.baidu.mapapi.SDKInitializer;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application{
	/*所有的activity都放入此集合中*/
	public static ArrayList<Activity> listActivity = new ArrayList<Activity>();
	/*软件是否开发完毕*/
	public static final boolean isRelease = false;
	/*当前网络状态*/
	public static int currentNetworkType;
	
	public static MyApplication instance = null;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		try {
			// 初始化百度地图
		SDKInitializer.initialize(this);
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
		
	}

	/*退出程序 */
	public void exit() {
		for (Activity activity : listActivity) {
			try {
				activity.finish();
				LogUtil.i("退出", activity.toString() + "退出了");
			} catch (Exception e) {
				ExceptionUtil.handleException(e);
			}
		}
		System.exit(0);

	}
	
	
}
