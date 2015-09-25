package com.systek.guide.service;

import java.util.ArrayList;
import java.util.Vector;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.systek.guide.biz.BizFactory;
import com.systek.guide.biz.DownloadBiz;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.entity.BeaconBean;
import com.systek.guide.entity.ExhibitBean;
import com.systek.guide.entity.LabelBean;
import com.systek.guide.entity.MapBean;
import com.systek.guide.entity.MuseumBean;

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
	// public int count;
	/* 下载开始时间 */
	long startTime;
	/* 下载状态监听器 */
	DownloadStateReceiver downloadStateReceiver;
	
	/** 详细信息资源集合 */
	private ArrayList<BeaconBean> beaconList;
	private ArrayList<LabelBean> labelList;
	private ArrayList<ExhibitBean> exhibitList;
	private ArrayList<MapBean> mapList;
	private ArrayList<MuseumBean> museumList;


	DownloadBiz downloadBiz;

	public DownloadService() {
		super("download");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册广播
		downloadStateReceiver = new DownloadStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.ACTION_CONTINUE);
		filter.addAction(Const.ACTION_PAUSE);
		registerReceiver(downloadStateReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消广播
		unregisterReceiver(downloadStateReceiver);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Toast.makeText(this, "已启动下载服务", Toast.LENGTH_SHORT).show();
		try {
			String assetsJson = intent.getStringExtra(Const.DOWNLOAD_ASSETS_KEY);
			String museumId = intent.getStringExtra(Const.DOWNLOAD_MUSEUMID_KEY);
			/* 创建下载业务对象，并开始下载 */
			downloadBiz = (DownloadBiz) BizFactory.getDownloadBiz(getApplicationContext());
			assetsList = downloadBiz.parseAssetsJson(assetsJson);
			downloadBiz.count = assetsList.size();
			downloadBiz.downloadAssets(assetsList, 0, assetsList.size(),museumId);
			sendProgress();
			while (!isDownloadOver) {}
			/**获取所有展品详细信息的JSON*/
			getJsonforDetailMuseum(museumId);
			while(beaconList==null||beaconList.size()<=0
					||labelList==null||labelList.size()<=0
					||exhibitList==null||exhibitList.size()<=0
					||mapList==null||mapList.size()<=0
					||museumList==null||museumList.size()<=0){}
			/**将展品详细信息保存至数据库*/
			saveAllAssetsList(museumId);
			saveDownloadRecord();
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
			Toast.makeText(getApplicationContext(), "数据获取异常", Toast.LENGTH_SHORT).show();
		}
	}

	private void saveDownloadRecord() {
		
	}

	private void sendProgress() {
		new Thread() {
			public void run() {
				/* 当下载未完成时，每秒发送一条广播以更新进度条 */
				int progress;
				while (!isDownloadOver) {
					progress = (assetsList.size() + 1 - downloadBiz.count) * 100 / assetsList.size();
					Intent in = new Intent();
					in.setAction(Const.ACTION_PROGRESS);
					in.putExtra(Const.ACTION_PROGRESS, progress);// currentSize*100/totalSize
					sendBroadcast(in);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						ExceptionUtil.handleException(e);
					}
				}

			};
		}.start();
	}
	
	
	/** 获取博物馆所有展品的详细信息的json */
	private void getJsonforDetailMuseum(String museumId) {
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, Const.BEACON_URL + museumId, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					beaconList = (ArrayList<BeaconBean>) JSON.parseArray(responseInfo.result, BeaconBean.class);
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
					labelList = (ArrayList<LabelBean>) JSON.parseArray(responseInfo.result, LabelBean.class);
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
					exhibitList = (ArrayList<ExhibitBean>) JSON.parseArray(responseInfo.result, ExhibitBean.class);
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
					mapList = (ArrayList<MapBean>) JSON.parseArray(responseInfo.result, MapBean.class);
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
					museumList = (ArrayList<MuseumBean>) JSON.parseArray(responseInfo.result, MuseumBean.class);
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
	
	/** 保存所有详细信息至数据库 */
	private void saveAllAssetsList(String id) {

		DbUtils db = DbUtils.create(this);
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
		LogUtil.i("json已保存至数据库", "-------------------------------");
		/*Intent in = new Intent();
		in.setAction(Const.ACTION_PROGRESS);
		in.putExtra(Const.ACTION_PROGRESS, 100);// currentSize*100/totalSize
		sendBroadcast(in);*/

	}
	
	/* 广播接收器，用于接收用户操控下载状态 */
	private class DownloadStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			/* 继续 */
			if (action.equals(Const.ACTION_CONTINUE)) {
				String id=intent.getStringExtra(Const.ACTION_CONTINUE);
				downloadBiz.downloadAssets(assetsList, assetsList.size() - downloadBiz.count, assetsList.size(),id);
				/* 暂停 */
			} else if (action.equals(Const.ACTION_PAUSE)) {
				for (int i = 0; i < downloadBiz.httpHandlerList.size(); i++) {
					if (downloadBiz.httpHandlerList.get(i) != null
							&& !downloadBiz.httpHandlerList.get(i).isCancelled()) {
						downloadBiz.httpHandlerList.get(i).cancel();
					}
				}
			}
		}

	}
}
