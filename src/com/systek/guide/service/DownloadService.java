package com.systek.guide.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.systek.guide.biz.BizFactory;
import com.systek.guide.biz.DownloadBiz;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.common.utils.Tools;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

public class DownloadService extends IntentService {
	/* 资源集合 */
	private Vector<String> assetsList;
	/* 判断assets是否下载完毕 */
	public static boolean isDownloadOver;
	/* 用于计算assets个数 */
	private int count;
	/* 下载开始时间 */
	long startTime;
	/* 控制下载的handler集合 */
	private ArrayList<HttpHandler<File>> httpHandlerList;
	/*下载状态监听器*/
	DownloadStateReceiver downloadStateReceiver;

	public DownloadService() {
		super("download");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//注册广播
		downloadStateReceiver = new DownloadStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.ACTION_CONTINUE);
		filter.addAction(Const.ACTION_PAUSE);
		registerReceiver(downloadStateReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消广播
		unregisterReceiver(downloadStateReceiver);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Toast.makeText(this, "已启动下载服务", Toast.LENGTH_SHORT).show();
		try {
			String assetsJson=intent.getStringExtra(Const.DOWNLOAD_ASSETS_KEY);
			/* 创建下载业务对象，并开始下载 */
			DownloadBiz downloadBiz=(DownloadBiz) BizFactory.getDownloadBiz(getApplicationContext());
			assetsList=downloadBiz.parseAssetsJson(assetsJson);
			downloadAssets(assetsList);
			
			/*当下载未完成时，每秒发送一条广播以更新进度条*/
			int progress;
			while (!isDownloadOver) {
				progress = (assetsList.size() + 1 - count) * 100 / assetsList.size();
				Intent in = new Intent();
				in.setAction(Const.ACTION_PROGRESS);
				in.putExtra(Const.ACTION_PROGRESS, progress);// currentSize*100/totalSize
				sendBroadcast(in);
				Thread.sleep(1000);
			}
			
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
			Toast.makeText(getApplicationContext(), "数据获取异常", Toast.LENGTH_SHORT).show();
		}
	}
	
	/*下载assets中数据*/
	public void downloadAssets(Vector<String> assetsList) {
		// mProgressListener.onStart();
		int start1 = 0;
		int end1 = assetsList.size() / 2;
		int start2 = assetsList.size() / 2;
		int end2 = assetsList.size();
		count = assetsList.size();
		startTime = System.currentTimeMillis();
		httpHandlerList = new ArrayList<HttpHandler<File>>();
		LogUtil.i("downloadAssets开始执行", "------当前时间为" + startTime + "文件个数" + count);
		
		/*检查文件夹是否创建，否则创建*/
		Tools.createOrCheckFolder(Const.LOCAL_AUDIO_PATH);
		Tools.createOrCheckFolder(Const.LOCAL_IMAGE_PATH);
		Tools.createOrCheckFolder(Const.LOCAL_LYRIC_PATH);
		
		HttpUtils http = new HttpUtils();
		String str = "";
		String savePath = "";
		
		/*分两段去下载*/
		for (int i = start1; i < end1; i++) {
			str = assetsList.get(i);
			if (str.endsWith(".jpg")) {
				savePath = Const.LOCAL_IMAGE_PATH + str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else if (str.endsWith(".lrc")) {
				savePath = Const.LOCAL_LYRIC_PATH + str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else if (str.endsWith(".mp3") || str.endsWith(".wav")) {
				savePath = Const.LOCAL_AUDIO_PATH + str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else {
				LogUtil.i("文件后缀异常", "------------------------------------------");
				count--;
			}
		}
		for (int i = start2; i < end2; i++) {
			str = assetsList.get(i);
			if (str.endsWith(".jpg")) {
				savePath = Const.LOCAL_IMAGE_PATH + str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else if (str.endsWith(".lrc")) {
				savePath = Const.LOCAL_LYRIC_PATH + str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				download(http, savePath, url);
			} else if (str.endsWith(".mp3") || str.endsWith(".wav")) {
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

		HttpHandler<File> httpHandler = http.download(url, savePath, true, true, new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				count--;
				if (url.endsWith(".jpg")) {
					LogUtil.i("jpg文件下载成功", url.substring(url.lastIndexOf("/") + 1) + "剩余个数" + count);
				} else if (url.endsWith(".lrc")) {
					LogUtil.i("lrc文件下载成功", url.substring(url.lastIndexOf("/") + 1) + "剩余个数" + count);
				} else if (url.endsWith(".mp3")) {
					LogUtil.i("mp3文件下载成功", url.substring(url.lastIndexOf("/") + 1) + "剩余个数" + count);
				} else if (url.endsWith(".wav")) {
					LogUtil.i("wav文件下载成功", url.substring(url.lastIndexOf("/") + 1) + "剩余个数" + count);
				}
				if (count <= 0) {
					long cost = System.currentTimeMillis() - startTime;
					LogUtil.i("下载执行完毕", "用时----------------" + cost / 1000 + "秒");
					isDownloadOver = true;
					Intent in = new Intent();
					in.setAction(Const.ACTION_PROGRESS);
					in.putExtra(Const.ACTION_PROGRESS, 100);// currentSize*100/totalSize
					sendBroadcast(in);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i("文件下载失败" + error.toString(), msg);
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				return;
			}

		});
		httpHandlerList.add(httpHandler);
	}

	
	/*广播接收器，用于接收用户操控下载状态*/
	private class DownloadStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			/*继续*/
			if (action.equals(Const.ACTION_CONTINUE)) {
				for (int i = 0; i < httpHandlerList.size(); i++) {
					httpHandlerList.get(i).resume();
				}
				/*暂停*/
			} else if (action.equals(Const.ACTION_PAUSE)) {
				for (int i = 0; i < httpHandlerList.size(); i++) {
					httpHandlerList.get(i).cancel();
				}
			}
		}

	}
}
