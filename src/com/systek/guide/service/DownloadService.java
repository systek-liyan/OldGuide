package com.systek.guide.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.common.utils.Tools;
import com.systek.guide.entity.BeaconModel;
import com.systek.guide.entity.ExhibitModel;
import com.systek.guide.entity.LabelModel;
import com.systek.guide.entity.MapModel;
import com.systek.guide.entity.MuseumModel;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class DownloadService extends IntentService {
	/* 资源集合 */
	private Vector<String> assetsList;
	private ArrayList<BeaconModel> beaconList;
	private ArrayList<LabelModel> labelList;
	private ArrayList<ExhibitModel> exhibitList;
	private ArrayList<MapModel> mapList;
	private ArrayList<MuseumModel> museumList;
	/* 判断assets是否下载完毕 */
	private static boolean isDownloadOver;
	/* 用于计算assets个数 */
	private int count;
	/* 用于计算当前下载文件大小 */
	// private int currentSize;
	/* 下载文件的总大小 */
	// private int totalSize;
	/* 下载开始时间 */
	long startTime;
	/* 控制下载的handler集合 */
	private ArrayList<HttpHandler<File>> httpHandlerList;
	// private OnProgressListener mProgressListener;

	/**
	 * 设置下载进度监听器
	 * 
	 * @return
	 * 
	 * 		public void setOnProgressListener(OnProgressListener
	 *         onProgressListener) { this.mProgressListener =
	 *         onProgressListener; }
	 */

	DownloadStateReceiver downloadStateReceiver;

	public DownloadService() {
		super("download");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		downloadStateReceiver = new DownloadStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.ACTION_CONTINUE);
		filter.addAction(Const.ACTION_PAUSE);
		// 注册广播
		registerReceiver(downloadStateReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(downloadStateReceiver);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			while (intent.getStringExtra(Const.DOWNLOAD_KEY) == null) {
			}
			Bundle bundle = intent.getExtras();
			String museumId = bundle.getString(Const.DOWNLOAD_KEY);
			// 获取Assets资源路径
			getAssetsJSON(museumId);
			while (assetsList == null || assetsList.size() == 0) {
			}
			/* 创建下载业务对象，并开始下载 */
			downloadAssets(assetsList);
			int progress;
			while (!isDownloadOver) {
				progress = (assetsList.size() + 1 - count) * 100 / assetsList.size();
				Intent in = new Intent();
				in.setAction(Const.ACTION_PROGRESS);
				in.putExtra(Const.ACTION_PROGRESS, progress);// currentSize*100/totalSize
				sendBroadcast(in);
				Thread.sleep(1000);
			}
			/* 获取beacon,map,exhibit,label 的JSON */
			getJSON(museumId);
			while (beaconList == null || labelList == null || exhibitList == null || mapList == null
					|| museumList == null || beaconList.size() <= 0 || labelList.size() <= 0 || exhibitList.size() <= 0
					|| mapList.size() <= 0l || museumList.size() <= 0) {
			}
			/* 将数据保存至数据库 */
			saveInToDatabase();
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
			Toast.makeText(getApplicationContext(), "数据获取异常", Toast.LENGTH_SHORT).show();
		}
	}

	private void getJSON(String museumId) {
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, Const.BEACON_URL + museumId, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					beaconList = (ArrayList<BeaconModel>) JSON.parseArray(responseInfo.result, BeaconModel.class);
				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i("下载JSON-BEACON_URL-获取失败" + error.toString(), msg);
			}
		});
		http.send(HttpRequest.HttpMethod.GET, Const.LABELS_URL + museumId, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					labelList = (ArrayList<LabelModel>) JSON.parseArray(responseInfo.result, LabelModel.class);
				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i("下载JSON-LABELS_URL-获取失败" + error.toString(), msg);
			}
		});
		http.send(HttpRequest.HttpMethod.GET, Const.EXHIBIT_URL + museumId, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					exhibitList = (ArrayList<ExhibitModel>) JSON.parseArray(responseInfo.result, ExhibitModel.class);
				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i("下载JSON-EXHIBIT_URL-获取失败" + error.toString(), msg);
			}
		});
		http.send(HttpRequest.HttpMethod.GET, Const.MUSEUM_MAP_URL + museumId, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					mapList = (ArrayList<MapModel>) JSON.parseArray(responseInfo.result, MapModel.class);
				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i("下载JSON-MUSEUM_MAP_URL-获取失败" + error.toString(), msg);
			}
		});
		http.send(HttpRequest.HttpMethod.GET, Const.MUSEUMS_URL + museumId, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					museumList = (ArrayList<MuseumModel>) JSON.parseArray(responseInfo.result, MuseumModel.class);
				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i("下载JSON-MUSEUMS_URL-获取失败" + error.toString(), msg);
			}
		});
	}

	private void saveInToDatabase() {
		DbUtils db = DbUtils.create(getApplicationContext());
		for (int i = 0; i < beaconList.size(); i++) {
			try {
				db.save(beaconList.get(i));
			} catch (DbException e) {
				ExceptionUtil.handleException(e);
			}
		}
		for (int i = 0; i < labelList.size(); i++) {
			try {
				db.save(labelList.get(i));
			} catch (DbException e) {
				ExceptionUtil.handleException(e);
			}
		}
		for (int i = 0; i < exhibitList.size(); i++) {
			try {
				db.save(exhibitList.get(i));
			} catch (DbException e) {
				ExceptionUtil.handleException(e);
			}
		}
		for (int i = 0; i < mapList.size(); i++) {
			try {
				db.save(mapList.get(i));
			} catch (DbException e) {
				ExceptionUtil.handleException(e);
			}
		}
		for (int i = 0; i < museumList.size(); i++) {
			try {
				db.save(museumList.get(i));
			} catch (DbException e) {
				ExceptionUtil.handleException(e);
			}
		}

		if (db != null) {
			db.close();
		}
		LogUtil.i("JSON已保存至数据库完毕", "-------------------------------");
		Intent in = new Intent();
		in.setAction(Const.ACTION_PROGRESS);
		in.putExtra(Const.ACTION_PROGRESS, 100);// currentSize*100/totalSize
		sendBroadcast(in);
	}

	private void getAssetsJSON(final String museumId) {

		LogUtil.i("getJSON开始执行", "------------------------");
		HttpUtils http = new HttpUtils();

		http.send(HttpRequest.HttpMethod.GET, Const.ASSETS_URL + museumId, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				JSONObject obj;
				try {
					obj = new JSONObject(responseInfo.result);
					JSONArray ary = obj.getJSONArray("url");
					// totalSize = Integer.valueOf((int) obj.getLong("size"));
					assetsList = parseAssetsJson(ary);
				} catch (JSONException e) {
					ExceptionUtil.handleException(e);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i("下载JSON-ASSETS_URL-获取失败" + error.toString(), msg);
				// mProgressListener.onFailed(Const.ASSETS_URL + museumId, msg);
			}
		});

	}

	protected Vector<String> parseAssetsJson(JSONArray ary) throws JSONException {
		Vector<String> list = new Vector<String>();
		for (int i = 0; i < ary.length(); i++) {
			String str = ary.getString(i);
			list.add(str);
		}
		return list;

	}

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

		Tools.createOrCheckFolder(Const.LOCAL_AUDIO_PATH);
		Tools.createOrCheckFolder(Const.LOCAL_IMAGE_PATH);
		Tools.createOrCheckFolder(Const.LOCAL_LYRIC_PATH);
		HttpUtils http = new HttpUtils();
		String str = "";
		String savePath = "";

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
				// mProgressListener.onFailed(url, msg);
				LogUtil.i("文件下载失败" + error.toString(), msg);
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				// mProgressListener.onProgress(total, current);
				// currentSize+=total;
				return;
			}

		});
		httpHandlerList.add(httpHandler);
	}

	/**
	 * 下载进度监听接口 ，用以监听下载进度的变化 下载博物馆离线资源文件列表的状态信息
	 */
	public interface OnProgressListener {

		/**
		 * 开始
		 */
		public void onStart();

		/**
		 * 下载进度更新时调用该回调，用以更新进度条显示
		 * 
		 * @param total
		 * @param current
		 */
		public void onProgress(long total, long current);

		/**
		 * 下载完成时 调用该回调
		 */
		public void onSuccess();

		/**
		 * 下载失败时 调用该回调
		 * 
		 * @param url
		 * @param msg
		 */
		public void onFailed(String url, String msg);
	}

	private class DownloadStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (action.equals(Const.ACTION_CONTINUE)) {
				for (int i = 0; i < httpHandlerList.size(); i++) {
					httpHandlerList.get(i).resume();
				}
			} else if (action.equals(Const.ACTION_PAUSE)) {
				for (int i = 0; i < httpHandlerList.size(); i++) {
					httpHandlerList.get(i).cancel();
				}
			}
		}

	}
}
