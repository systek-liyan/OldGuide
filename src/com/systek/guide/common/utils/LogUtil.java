package com.systek.guide.common.utils;

import com.systek.guide.common.MyApplication;

import android.util.Log;


/**
 * ��־Ҫͳһ����
 * @author ZQ
 *
 */
public class LogUtil {
	
	public static void i(String tag,Object msg)
	{
		if (MyApplication.isRelease)
		{
			return;
		}
		Log.i(tag, String.valueOf(msg));
	}

}
