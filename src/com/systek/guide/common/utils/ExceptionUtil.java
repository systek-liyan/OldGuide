package com.systek.guide.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.systek.guide.common.MyApplication;

/**
 * ������Ŀ�����е��쳣
 * 
 * @author ZQ
 * 
 */
public class ExceptionUtil {

	public static void handleException(Exception e) {
		// ���쳣��Ϣ����ַ���������������Ա
		String str = "";
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		str = stringWriter.toString();
		
		if (MyApplication.isRelease) {
			// ��������
		} else {
			// ������
			LogUtil.i("�쳣��Ϣ", str);
		}

	}

	
}
