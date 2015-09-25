package com.systek.guide.biz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.entity.DownloadInfoBean;
import com.systek.guide.entity.DownloadAreaBeans;
import com.systek.guide.service.DownloadService;

import android.content.Context;
import android.content.Intent;

public class DownloadBiz implements InterfaceDownloadManageBiz {

	Context context;
	/* 控制下载的handler集合 */
	public ArrayList<HttpHandler<File>> httpHandlerList;

	public  int count;
	/* 下载开始时间 */
	long startTime;
	private String assetsJson;

	/* 启动下载的线程数 */
	private int maxDownloadThread = 3;
	
	public DownloadBiz(Context context) {
		this.context = context;
	}

	@Override
	public void download(final String museumId) {
		
		new Thread(){
			public void run() {
				/* 获取所资源地址 */
				assetsJson = getAssetsJSON(museumId);
				/* 下载资源 */
				downloadAssets(assetsJson,museumId);
			};
		}.start();
	}

	@Override
	public void delete(String museumId) {
		// TODO 删除操作，待实现
		
	}
	
	public int getMaxDownloadThread() {
		return maxDownloadThread;
	}

	public void setMaxDownloadThread(int maxDownloadThread) {
		this.maxDownloadThread = maxDownloadThread;
	}


	/** 获得assets资源json */
	private String getAssetsJSON(String id) {

		LogUtil.i("getAssetsJSO开始执行", "------------------------");
		HttpUtils http = new HttpUtils();

		http.send(HttpRequest.HttpMethod.GET, Const.ASSETS_URL + id, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				assetsJson = responseInfo.result;
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i("getAssetsJSON失败" + error.toString(), msg);

			}
		});
		while (assetsJson == null) {
		}
		return assetsJson;
	}

	/** 下载assets的方法，启动下载服务 */
	private void downloadAssets(String assetsJson2,String id) {
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(Const.DOWNLOAD_ASSETS_KEY, assetsJson2);
		intent.putExtra(Const.DOWNLOAD_MUSEUMID_KEY, id);
		context.startService(intent);
	}


	/** 解析assets资源json */
	public Vector<String> parseAssetsJson(String assetsJson2) {
		JSONObject obj;
		Vector<String> list = null;
		try {
			obj = new JSONObject(assetsJson2);
			list = new Vector<String>();
			JSONArray ary = obj.getJSONArray("url");
			for (int i = 0; i < ary.length(); i++) {
				String str = ary.getString(i);
				list.add(str);
			}
		} catch (JSONException e) {
			ExceptionUtil.handleException(e);
		}
		return list;
	}

	/** 获得可下载城市与博物馆的json数据 */
	public void getJsonForDownloadMenu() {
		LogUtil.i("当前状态", "getJsonForDownloadMenu");

		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, Const.DOWNLOAD_CITY_LIST, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtil.i("数据获取成功------", responseInfo.result);
				Intent in = new Intent();
				in.setAction(Const.ACTION_DOWNLOAD_JSON);
				in.putExtra(Const.ACTION_DOWNLOAD_JSON, responseInfo.result);// currentSize*100/totalSize
				context.sendBroadcast(in);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i(error.toString(), msg);
			}
		});
	}

	/** 解析可下载城市与博物馆的json数据 */
	public ArrayList<DownloadAreaBeans> parseJsonForDownloadMenu(String menuJson) throws JSONException {

		JSONArray ary = new JSONArray(menuJson);
		List<DownloadAreaBeans> list = new ArrayList<DownloadAreaBeans>();

		for (int i = 0; i < ary.length(); i++) {
			JSONObject obj = ary.getJSONObject(i);
			DownloadAreaBeans models = new DownloadAreaBeans();
			models.setCity(obj.getString("city"));
			JSONArray array = new JSONArray(obj.getString("museumList"));
			ArrayList<DownloadInfoBean> modelList = new ArrayList<DownloadInfoBean>();

			for (int j = 0; j < array.length(); j++) {
				JSONObject obj1 = array.getJSONObject(j);
				DownloadInfoBean model = new DownloadInfoBean();
				model.setMuseumId(obj1.getString("museumId"));
				model.setName(obj1.getString("name"));
				model.setTotal(obj1.getInt("size"));
				modelList.add(model);
			}

			models.setList(modelList);
			list.add(models);
		}
		return (ArrayList<DownloadAreaBeans>) list;
	}


	/* 下载assets中数据 */
	public void downloadAssets(Vector<String> assetsList, int start, int end,String id) {

		startTime = System.currentTimeMillis();
		httpHandlerList = new ArrayList<HttpHandler<File>>();
		LogUtil.i("downloadAssets开始执行", "------当前时间为" + startTime + "文件个数" + count);
		
		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(getMaxDownloadThread());
		String str = "";
		String savePath = "";
		/* 遍历集合并下载 */
		for (int i = start; i < end; i++) {
			str = assetsList.get(i);
			if (str.endsWith(".jpg")||str.endsWith(".png")) {
				savePath = Const.LOCAL_ASSETS_PATH +id+"/"+Const.LOCAL_FILE_TYPE_IMAGE+ str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				downloadFile(http, savePath, url);
			} else if (str.endsWith(".lrc")) {
				savePath = Const.LOCAL_ASSETS_PATH+id+"/" +Const.LOCAL_FILE_TYPE_LYRIC+ str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				downloadFile(http, savePath, url);
			} else if (str.endsWith(".mp3") || str.endsWith(".wav")) {
				savePath = Const.LOCAL_ASSETS_PATH+id+"/" +Const.LOCAL_FILE_TYPE_AUDIO+ str.substring(str.lastIndexOf("/"));
				final String url = Const.BASEURL + assetsList.get(i);
				downloadFile(http, savePath, url);
			} else {
				LogUtil.i("文件后缀异常", "------------------------------------------");
				count--;
			}
		}
	}

	private void downloadFile(HttpUtils http, String savePath, final String url) {

		HttpHandler<File> httpHandler = http.download(url, savePath, true, true, new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				count--;
				if (url.endsWith(".jpg")) {
					LogUtil.i("jpg文件下载成功", url.substring(url.lastIndexOf("/") + 1) + "剩余个数" + count);
				}else if (url.endsWith(".png")) {
					LogUtil.i("png文件下载成功", url.substring(url.lastIndexOf("/") + 1) + "剩余个数" + count);
				}else if (url.endsWith(".lrc")) {
					LogUtil.i("lrc文件下载成功", url.substring(url.lastIndexOf("/") + 1) + "剩余个数" + count);
				} else if (url.endsWith(".mp3")) {
					LogUtil.i("mp3文件下载成功", url.substring(url.lastIndexOf("/") + 1) + "剩余个数" + count);
				} else if (url.endsWith(".wav")) {
					LogUtil.i("wav文件下载成功", url.substring(url.lastIndexOf("/") + 1) + "剩余个数" + count);
				}
				if (count <= 0) {
					long cost = System.currentTimeMillis() - startTime;
					LogUtil.i("下载执行完毕", "用时----------------" + cost / 1000 + "秒");
					DownloadService.isDownloadOver = true;
					Intent in = new Intent();
					in.setAction(Const.ACTION_PROGRESS);
					in.putExtra(Const.ACTION_PROGRESS, 100);// currentSize*100/totalSize
					context.sendBroadcast(in);
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


}
