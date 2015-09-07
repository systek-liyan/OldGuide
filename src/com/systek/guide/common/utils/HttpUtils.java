package com.systek.guide.common.utils;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtils {
	public static final int GET=0;
	public static final int POST=1;
	/**
	 * 发送http请求的工具方法
	 * @param method  请求方式
	 * @param uri  请求资源路径
	 * @param pair 请求中携带的参数  可以是null
	 * @return 响应实体
	 * @throws Exception
	 */
	public static HttpEntity get(int method, String uri, List<NameValuePair> pairs)throws Exception{
		HttpClient client=new DefaultHttpClient();
		HttpResponse resp=null;
		switch (method) {
		case GET:
			HttpGet get=new HttpGet(uri);
			resp=client.execute(get);
			break;
		case POST:
			HttpPost post=new HttpPost(uri);
			if(pairs!=null){
				HttpEntity entity=new UrlEncodedFormEntity(pairs, "utf-8");
				post.setEntity(entity);
			}
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			resp=client.execute(post);
			break;
		}
		HttpEntity respEntity=resp.getEntity();
		return respEntity;
	}
	
}





