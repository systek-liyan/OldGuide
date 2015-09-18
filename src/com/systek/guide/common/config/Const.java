package com.systek.guide.common.config;


import android.os.Environment;

public class Const {
	
	
	/*服务器上的域名加端口号*/		//此处先随便写个
	public static final String BASEURL="http://182.92.82.70";
	/*可下载城市列表*/
	public static final String DOWNLOAD_CITY_LIST=BASEURL+"/api/assetsService/assetsSizeList";
	/*城市列表URL*/
	public static final String CITYLISTURL =BASEURL+ "/api/cityService/cityList";
	/*资源路径*/
	public static final String EXHIBIT_URL=BASEURL+ "/api/exhibitService/exhibitList?museumId=";
	public static final String MUSEUM_MAP_URL=BASEURL+ "/api/museumMapService/museumMapList?museumId=";
	public static final String  MUSEUMS_URL=BASEURL+ "/api/museumService/museumList?museumId=";
	public static final String BEACON_URL=BASEURL+ "/api/beaconService/beaconList?museumId=";
	public static final String LABELS_URL=BASEURL+ "/api/labelsService/labelsList?museumId=";
	public static final String ASSETS_URL =BASEURL + "/api/assetsService/assetsList?museumId=";
	
	/*网络状态*/
	public static final int TYPE_WIFI=1;
	public static final int TYPE_MOBILE=2;
	public static final int TYPE_NONE=3;
	/*用于MuseumActivity界面*/
	public static final String CITY_MUSEUM="city";
	/*存储至本地sdcard位置*/
	public static final String SDCARD_ROOT=Environment.getExternalStorageDirectory().getAbsolutePath();
	/*sdcard存储图片的位置*/
	public static final String LOCAL_IMAGE_PATH=SDCARD_ROOT+"/Guide/image";
	public static final String LOCAL_AUDIO_PATH=SDCARD_ROOT+"/Guide/audio";
	public static final String LOCAL_LYRIC_PATH=SDCARD_ROOT+"/Guide/lyric";
	
	/*用于下载后传递数据*/
	public static final String DOWNLOAD_ASSETS_KEY="download_assets_key";
	//public static final String DOWNLOAD_MUSEUMID_KEY="download_museumId_key";
	public static final int MSG_WHAT=1;
	
	/*用于下载过程中Message*/
	public static final int DOWNLOAD_MSG_WHAT=11;
	public static final int MSG_DOWANLOAD_OVER=200;
	public static final int MSG_PROGRESSBAR_WHAT=22;
	public static final String DOWNLOAD_KEY_PROGRESSBAR="progressbar";

	/*xUtils自动创建数据库的名称*/
	public static final String DB_BEACON_NAME="com_systek_entity_BeaconModel";
	public static final String DB_EXHIBIT_NAME="com_systek_entity_ExhibitMoel";
	public static final String DB_LABEL_NAME="com_systek_entity_LabelModel";
	public static final String DB_MAP_NAME="com_systek_entity_MapModel";
	public static final String DB_MUSEUM_NAME="com_systek_entity_MuseumModel";
	
	/*用于下载过程中传递消息的广播过滤信息*/
	public static final String ACTION_DOWNLOAD="download";
	public static final String ACTION_PAUSE="pause";
	public static final String ACTION_CONTINUE="continue";
	public static final String ACTION_PROGRESS="progress";
	public static final String ACTION_DOWNLOAD_JSON="download_json";
	public static final String ACTION_ASSETS_JSON="assets_json";
	
	/*用于记录ExpandableListview中下载item的位置的Message类型*/
	public static final int EXPANDABLE_MSG_CHILD=33;
	
}
