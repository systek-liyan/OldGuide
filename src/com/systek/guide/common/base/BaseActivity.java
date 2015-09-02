package com.systek.guide.common.base;

import com.systek.guide.common.MyApplication;
import com.systek.guide.common.utils.ExceptionUtil;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			MyApplication.listActivity.add(this);			
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}
}
