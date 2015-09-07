package com.systek.guide.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.systek.guide.common.MyApplication;

/**
 * 处理项目中所有的异常
 * 
 * @author ZQ
 * 
 */
public class ExceptionUtil {

	public static void handleException(Exception e) {
		// 把异常信息变成字符串，发给开发人员
		String str = "";
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		str = stringWriter.toString();
		
		if (MyApplication.isRelease) {
			// 联网发送
		} else {
			// 开发中
			LogUtil.i("异常信息", str);
		}

	}

	
}
