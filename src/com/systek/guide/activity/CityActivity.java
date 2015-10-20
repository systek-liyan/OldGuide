package com.systek.guide.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.systek.guide.R;
import com.systek.guide.adapter.CityAdapter;
import com.systek.guide.biz.BeansManageBiz;
import com.systek.guide.biz.BizFactory;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.NetworkUtil;
import com.systek.guide.entity.CityBean;
import com.systek.guide.widget.SideBar;
import com.systek.guide.widget.SideBar.OnTouchingLetterChangedListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CityActivity extends BaseActivity{

	private LocationClient mLocationClient;
	private SideBar sideBar;
	private TextView dialog;
	private CityAdapter adapter;
	/** 汉字转换成拼音的类*/
	//private CharacterParser characterParser;
	private List<CityBean> cities;
	private  final int MSG_WHAT_CITIES=1;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private ListView cityListView;
	private Button loacteButton;

	private String currentCity;

	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==MSG_WHAT_CITIES){
				updateListView(cities);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		initialize();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mLocationClient!=null&&mLocationClient.isStarted()){
			mLocationClient.stop();			
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initialize() {
		initViews();
		addListener();
		initData();
		int netState=NetworkUtil.checkNet(this);
		if(netState==Const.INTERNET_TYPE_NONE){
			Toast.makeText(this, "当前无网络连接", Toast.LENGTH_SHORT).show();
		}else{			
			initLocation();
		}
		initAdapter();
	}

	private void initAdapter() {
		if(cities!=null&&cities.size()>0){
			Collections.sort(cities, pinyinComparator);		
		}else{
			cities=new ArrayList<CityBean>();
		}
		// 自定义Adapter
		adapter = new CityAdapter(this, cities);
		cityListView.setAdapter(adapter);
	}

	private void initViews() {
		// 实例化汉字转拼音类
		//characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		cityListView = (ListView) findViewById(R.id.city_list);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.city_dialog);
		sideBar.setTextView(dialog);
		loacteButton = (Button) findViewById(R.id.city_btn_loacte);
		/*
		 * mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		 * 
		 * //根据输入框输入值的改变来过滤搜索 mClearEditText.addTextChangedListener(new
		 * TextWatcher() {
		 * 
		 * @Override public void onTextChanged(CharSequence s, int start, int
		 * before, int count) { //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		 * filterData(s.toString()); }
		 * 
		 * @Override public void beforeTextChanged(CharSequence s, int start,
		 * int count, int after) { }
		 * 
		 * @Override public void afterTextChanged(Editable s) { } });
		 */
	}

	private void addListener() {
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					cityListView.setSelection(position);
				}
			}
		});

		cityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				CityBean city = (CityBean) cityListView.getAdapter().getItem(position);
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				currentCity=city.getName();
				Toast.makeText(getApplication(), currentCity, Toast.LENGTH_SHORT).show();
				gotoMuseumActivity();
				finish();
			}
		});
	}

	/* 获取数据 */
	private void initData() {
		new Thread(){
			public void run() {
				BeansManageBiz biz=(BeansManageBiz) BizFactory.getBeansManageBiz(CityActivity.this);
				cities=biz.getAllBeans(CityBean.class,"");
				while(cities==null){}
				handler.sendEmptyMessage(MSG_WHAT_CITIES);
			};
		}.start();
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param data
	 * @return
	 * 
	 * 		private List<CityModule> filledData(String [] data){ List
	 *         <CityModule> mSortList = new ArrayList<CityModule>();
	 * 
	 *         for(int i=0; i<data.length; i++){ CityModule cityModule = new
	 *         CityModule(); cityModule.setName(data[i]); //汉字转换成拼音 String
	 *         pinyin = characterParser.getSelling(data[i]); String sortString =
	 *         pinyin.substring(0, 1).toUpperCase();
	 * 
	 *         // 正则表达式，判断首字母是否是英文字母 if(sortString.matches("[A-Z]")){
	 *         cityModule.setAlpha(sortString.toUpperCase()); }else{
	 *         cityModule.setAlpha("#"); } mSortList.add(cityModule); } return
	 *         mSortList;
	 * 
	 *         }
	 */

	/**
	 * 根据输入框中的值来过滤数据并更新ListView 待用
	 * 
	 * @param filterStr
	 * 
	 *            private void filterData(String filterStr) { List
	 *            <CityModule> filterDateList = new ArrayList<CityModule>();
	 * 
	 *            if (TextUtils.isEmpty(filterStr)) { filterDateList = cities; }
	 *            else { filterDateList.clear(); for (CityModule sortModel :
	 *            cities) { String name = sortModel.getName(); if
	 *            (name.toUpperCase().indexOf(
	 *            filterStr.toString().toUpperCase()) != -1 ||
	 *            characterParser.getSelling(name).toUpperCase()
	 *            .startsWith(filterStr.toString().toUpperCase())) {
	 *            filterDateList.add(sortModel); } } }
	 * 
	 *            // 根据a-z进行排序 Collections.sort(filterDateList,
	 *            pinyinComparator); adapter.updateListView(filterDateList); }
	 */

	public class PinyinComparator implements Comparator<CityBean> {

		public int compare(CityBean o1, CityBean o2) {
			// 这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
			if (o2.getAlpha().equals("#")) {
				return -1;
			} else if (o1.getAlpha().equals("#")) {
				return 1;
			} else {
				return o1.getAlpha().compareTo(o2.getAlpha());
			}
		}
	}

	private void initLocation() {
		try {
			LocationClientOption option = new LocationClientOption();
			option.setScanSpan(1);
			option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
			option.setOpenGps(true);//可选，默认false,设置是否使用gps
			option.setIsNeedAddress(true);
			option.setLocationMode(LocationMode.Hight_Accuracy);
			option.setCoorType("bd0911");
			mLocationClient = new LocationClient(this);
			mLocationClient.setLocOption(option);
			mLocationClient.registerLocationListener(bdLocationListener);
			mLocationClient.start();
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}

	/**
	 * 更新ListView中的数据
	 * @param cityList
	 */
	public void updateListView(List<CityBean> cityList) {
		this.cities = cityList;
		Collections.sort(cities, pinyinComparator);
		adapter.updateListView(cities);
	}
	
	BDLocationListener bdLocationListener=new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation bdLocation) {

			currentCity = bdLocation.getCity();
			/*
			 * // 纬度 double latitude = bdLocation.getLatitude(); // 经度
			 * double longitude = bdLocation.getLongitude();
			 * LogUtil.i("定位", "纬度=" + latitude + ",经度=" + longitude);
			 */
			if (currentCity == null) {
				loacteButton.setText("定位失败");
				loacteButton.setEnabled(false);
				Toast.makeText(CityActivity.this, "定位失败，请手动选择城市", Toast.LENGTH_SHORT).show();
				mLocationClient.stop();
			} else {
				loacteButton.setText(currentCity);
				builDialog();
			}
		}
	};
	
	
	private void builDialog() {
		AlertDialog.Builder builder = new Builder(CityActivity.this);   
		builder.setMessage("当期城市为"+currentCity+",是否选择？");   
		builder.setTitle("提示");   
		builder.setPositiveButton("确定", new OnClickListener() {   

			@Override  
			public void onClick(DialogInterface dialog, int which) {   
				gotoMuseumActivity();
				mLocationClient.stop();
				mLocationClient.unRegisterLocationListener(bdLocationListener);
				dialog.dismiss();
				finish();
			}

		});   

		builder.setNegativeButton("取消", new OnClickListener() {   
			@Override  
			public void onClick(DialogInterface dialog, int which) {   
				dialog.dismiss();   
			}   
		});
		AlertDialog dialog=builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	private void gotoMuseumActivity() {
		Intent intent = new Intent(CityActivity.this, MuseumActivity.class);
		intent.putExtra("city", currentCity);
		startActivity(intent);
	}   
}
