package com.systek.guide.biz;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.common.utils.Tools;
import com.systek.guide.service.DownloadService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class DownloadBiz implements  BizInterface {

	/*int count;
	long startTime;
	private ArrayList<HttpHandler<File>> list;
	Context context;


	protected DownloadBiz(Context context) {
		super();
		this.context=context;
		DownloadStateReceiver receiver=new DownloadStateReceiver();
		IntentFilter filter = new IntentFilter();  
		filter.addAction(Const.ACTION_DOWNLOAD);  
		filter.addAction(Const.ACTION_CONTINUE);  
		filter.addAction(Const.ACTION_PAUSE);  
		//注册广播        
		context.registerReceiver(receiver, filter);
	}

	private class DownloadStateReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();  

			if(action.equals(Const.ACTION_DOWNLOAD)){  
				for(int i=0;i<list.size();i++){
					list.get(i).pause();
				}
			}else if(action.equals(Const.ACTION_CONTINUE)){
				for(int i=0;i<list.size();i++){
					list.get(i).resume();
				}
			}else if(action.equals(Const.ACTION_CONTINUE)){}
		}

	}

	public  void downloadAssets(Context context, Vector<String> assetsList) {

		int start1 = 0;
		int end1=assetsList.size()/2;
		int start2=assetsList.size()/2;
		int end2=assetsList.size();

		count=assetsList.size();
		startTime=System.currentTimeMillis();
		LogUtil.i("downloadAssets开始执行", "------当前时间为" + startTime+"文件个数"+count );

		Tools.createOrCheckFolder(Const.LOCAL_AUDIO_PATH);
		Tools.createOrCheckFolder(Const.LOCAL_IMAGE_PATH);
		Tools.createOrCheckFolder(Const.LOCAL_LYRIC_PATH);
		HttpUtils http=new HttpUtils();
		String str = "";
		String savePath = "";

		for (int i = start1; i < end1; i++) {
			str = assetsList.get(i);
			if (str.endsWith(".jpg")) {
				savePath = Const.LOCAL_IMAGE_PATH +str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else if (str.endsWith(".lrc")) {
				savePath = Const.LOCAL_LYRIC_PATH +str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else if (str.endsWith(".mp3")||str.endsWith(".wav")) {
				savePath = Const.LOCAL_AUDIO_PATH + str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else {
				LogUtil.i("文件后缀异常", "------------------------------------------");
			}
		}
		for (int i = start2; i < end2; i++) {
			str = assetsList.get(i);
			if (str.endsWith(".jpg")) {
				savePath = Const.LOCAL_IMAGE_PATH +str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else if (str.endsWith(".lrc")) {
				savePath = Const.LOCAL_LYRIC_PATH +str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else if (str.endsWith(".mp3")||str.endsWith(".wav")) {
				savePath = Const.LOCAL_AUDIO_PATH + str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else {
				count--;
				LogUtil.i("文件后缀异常", "------------------------------------------");
			}
		}

	}


	private void download(HttpUtils http, String savePath, final String url) {

		HttpHandler<File> httpHandler=http.download(url, savePath, true,true,new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				count--;
				if (url.endsWith(".jpg")) {
					LogUtil.i("jpg文件下载成功", url.substring(url.lastIndexOf("/")+1)+"剩余个数"+count);
				} else if (url.endsWith(".lrc")) {
					LogUtil.i("lrc文件下载成功", url.substring(url.lastIndexOf("/")+1)+"剩余个数"+count);
				} else if (url.endsWith(".mp3")) {
					LogUtil.i("mp3文件下载成功", url.substring(url.lastIndexOf("/")+1)+"剩余个数"+count);
				}else if (url.endsWith(".wav")) {
					LogUtil.i("wav文件下载成功", url.substring(url.lastIndexOf("/")+1)+"剩余个数"+count);
				}
				if (count<=1) {
					long cost = System.currentTimeMillis() - startTime;
					LogUtil.i("下载执行完毕", "用时----------------" + cost / 1000 + "秒");
					DownloadService.isDownloadOver=true;
				}
			}
			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i("文件下载失败" + error.toString(), msg);
			}
		});

		list.add(httpHandler);
	}
*/
}
