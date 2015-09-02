package com.systek.guide.common;

import java.util.ArrayList;

import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application{
	
	public static ArrayList<Activity> listActivity = new ArrayList<Activity>();
	public static final boolean isRelease = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
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
		// 结束进程
		System.exit(0);

	}
}
