package com.systek.guide.biz.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.systek.guide.biz.IGetBeanBiz;
import com.systek.guide.common.utils.Tools;

import android.content.Context;

public class GetBeansFromNet implements IGetBeanBiz {

	List<?> list;
	Object  obj;
	@Override
	public <T> List<T> getAllBeans(Context context, final Class<T> entityType,String id) {
		
		String url=Tools.checkTypeforUrl(entityType)+id;
		HttpUtils http = new HttpUtils();
		
		http.send(HttpRequest.HttpMethod.GET,url, new RequestCallBack<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				list = JSON.parseArray(responseInfo.result, entityType);
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(HttpException error, String msg) {
			}
		});
		while (list == null) {
		}
		return (List<T>) list;
	}

	@Override
	public <T> T getBeanById(Context context, final Class<T> entityType, String id) {
		String url=Tools.checkTypeforUrl(entityType)+id;
		
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET,url, new RequestCallBack<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				obj=JSON.parseObject(responseInfo.result, entityType);
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(HttpException error, String msg) {
			}
		});
		while (obj == null) {
		}
		return (T) obj;
	}
	
}
