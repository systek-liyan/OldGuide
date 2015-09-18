package com.systek.guide.biz;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.entity.BeaconModel;
import com.systek.guide.entity.DownloadInfoModel;
import com.systek.guide.entity.DownloadTargetModels;
import com.systek.guide.entity.ExhibitModel;
import com.systek.guide.entity.LabelModel;
import com.systek.guide.entity.MapModel;
import com.systek.guide.entity.MuseumModel;
import com.systek.guide.service.DownloadService;

import android.content.Context;
import android.content.Intent;

public class DownloadBiz implements BizInterface {

	Context context;
	/**详细信息资源集合 */
	private ArrayList<BeaconModel> beaconList;
	private ArrayList<LabelModel> labelList;
	private ArrayList<ExhibitModel> exhibitList;
	private ArrayList<MapModel> mapList;
	private ArrayList<MuseumModel> museumList;

	public DownloadBiz(Context context) {
		this.context = context;
	}

	private String assetsJson;
	
	/**获得assets资源json*/
	public String getAssetsJSON(String id) {

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
		while (assetsJson == null) {}
		return assetsJson;
	}
	
	/**下载assets的方法，启动下载服务*/
	public void downloadAssets(String museumId,String assetsJson2) {
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(Const.DOWNLOAD_ASSETS_KEY, assetsJson2);
		context.startService(intent);
		
		getJsonforDetailMuseum(museumId);
		/* 将数据保存至数据库 */
		saveAllAssetsList(museumId);
	}
	
	/**保存所有详细信息至数据库*/
	public void saveAllAssetsList(String id) {
		while(!DownloadService.isDownloadOver){}
		
		DbUtils db = DbUtils.create(context);
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
		Intent in = new Intent();
		in.setAction(Const.ACTION_PROGRESS);
		in.putExtra(Const.ACTION_PROGRESS, 100);// currentSize*100/totalSize
		context.sendBroadcast(in);

	}

	/**解析assets资源json */
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
	
	/**获得可下载城市与博物馆的json数据*/
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
	
	
	/**解析可下载城市与博物馆的json数据*/
	public ArrayList<DownloadTargetModels> parseJsonForDownloadMenu(String menuJson) throws JSONException {

		JSONArray ary = new JSONArray(menuJson);
		List<DownloadTargetModels> list = new ArrayList<DownloadTargetModels>();

		for (int i = 0; i < ary.length(); i++) {
			JSONObject obj = ary.getJSONObject(i);
			DownloadTargetModels models = new DownloadTargetModels();
			models.setCity(obj.getString("city"));
			JSONArray array = new JSONArray(obj.getString("museumList"));
			ArrayList<DownloadInfoModel> modelList = new ArrayList<DownloadInfoModel>();
			
			for (int j = 0; j < array.length(); j++) {
				JSONObject obj1 = array.getJSONObject(j);
				DownloadInfoModel model = new DownloadInfoModel();
				model.setMuseumId(obj1.getString("museumId"));
				model.setName(obj1.getString("name"));
				model.setTotal(obj1.getInt("size"));
				modelList.add(model);
			}

			models.setList(modelList);
			list.add(models);
		}
		return (ArrayList<DownloadTargetModels>) list;
	}
	
	/**获取博物馆所有展品的详细信息的json*/
	public void getJsonforDetailMuseum(String museumId) {
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

}
