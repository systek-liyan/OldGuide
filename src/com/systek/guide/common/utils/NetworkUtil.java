package com.systek.guide.common.utils;

import com.systek.guide.common.MyApplication;
import com.systek.guide.common.config.Const;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

	public static int checkNet(Context context) {
		// 判断用户是打开还是关闭
		ConnectivityManager manager = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo == null) {
			MyApplication.currentNetworkType = Const.INTERNET_TYPE_NONE;
			LogUtil.i("NetworkStateChanged", "关闭");
		} else {
			/* 用户开了WIFI和移动网络，操作系统使用的是WIFI */
			NetworkInfo wifiNetworkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
				LogUtil.i("NetworkStateChanged", "打开的是wifi");
				MyApplication.currentNetworkType = Const.INTERNET_TYPE_WIFI;
			}
			NetworkInfo mobileNetworkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobileNetworkInfo != null && mobileNetworkInfo.isConnected()) {
				LogUtil.i("NetworkStateChanged", "打开的是mobile");
				MyApplication.currentNetworkType = Const.INTERNET_TYPE_MOBILE;
			}
		}
		return MyApplication.currentNetworkType;
	}
}
