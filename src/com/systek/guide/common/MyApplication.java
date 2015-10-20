package com.systek.guide.common;

import java.util.ArrayList;

import com.baidu.mapapi.SDKInitializer;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application{
	/*所有的activity都放入此集合中*/
	public static ArrayList<Activity> listActivity = new ArrayList<Activity>();
	/*软件是否开发完毕*/
	public static final boolean isRelease = false;
	/*当前网络状态*/
	public static int currentNetworkType=Const.INTERNET_TYPE_NONE;
	
	public static MyApplication instance = null;
	
	public static int GUIDE_MODEL_AUTO=2;
	public static int GUIDE_MODEL_HAND=3;
	
	public static int guideModel;

	@Override
	public void onCreate() {
		super.onCreate();
		guideModel=GUIDE_MODEL_HAND;
		instance = this;
		initConfig();
		
		try {
			// 初始化百度地图
		SDKInitializer.initialize(this);
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
		
	}

	private void initConfig() {
		SharedPreferences settings = getSharedPreferences("setting", 0);
		String mGuideModel=settings.getString(Const.GUIDE_MODEL_KEY, Const.GUIDE_MODEL_HAND);
		if(mGuideModel.equals(Const.GUIDE_MODEL_HAND)){
			guideModel=GUIDE_MODEL_HAND;
		}else if(mGuideModel.equals(Const.GUIDE_MODEL_AUTO)){
			guideModel=GUIDE_MODEL_AUTO;
		}
		LogUtil.i("测试数据模式", "guideModel----"+guideModel);
	}

	/*退出程序 */
	public static void exit() {
		for (Activity activity : listActivity) {
			if(activity!=null){
				try {
					activity.finish();
					LogUtil.i("退出", activity.toString() + "退出了");
				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}				
			}
		}
		System.exit(0);

	}
	
	
}
