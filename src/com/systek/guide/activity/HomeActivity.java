package com.systek.guide.activity;

import java.util.ArrayList;

import org.altbeacon.beacon.Beacon;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.systek.guide.R;
import com.systek.guide.adapter.HomeFragmentPagerAdapter;
import com.systek.guide.beacon.BeaconSearcher;
import com.systek.guide.beacon.BeaconSearcher.OnNearestBeaconListener;
import com.systek.guide.beacon.NearestBeacon;
import com.systek.guide.common.MyApplication;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.fragment.MuseumIntroduceFragment;
import com.systek.guide.fragment.MuseumIntroduceFragment.OnFragmentInteractionListener;
import com.systek.guide.widget.DrawerView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HomeActivity extends FragmentActivity implements OnFragmentInteractionListener ,OnNearestBeaconListener{

	String museumId;
	MuseumIntroduceFragment museumIntroduceFragment;
	ViewPager homeViewPager;
	ArrayList<Fragment> fragments;
	private HomeFragmentPagerAdapter homeFragmentPageAdapter;
	private ImageView iv_Search;
	private ImageView iv_Drawer;
	/*侧滑菜单*/
	SlidingMenu side_drawer;
	private static onBeaconSearcherListener mBeaconListener;
	/**
	 * store BeaconSearcher instance, we use it to range beacon,and get the
	 * minBeacon from it.
	 */
	private static BeaconSearcher mBeaconSearcher;

	/**
	 * 自定义的打开 Bluetooth 的请求码，与 onActivityResult 中返回的 requestCode 匹配。
	 */
	private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;
	/**
	 * Bluetooth 设备可见时间，单位：秒。
	 */
	private static final int BLUETOOTH_DISCOVERABLE_DURATION = 250;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		MyApplication.listActivity.add(this);
		initView();
		addListener();
		initFragment();
		// 初始化beacon搜索器
		initBeaconSearcher();

		// 注册广播监听器
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
		registerReceiver(mBluetoothReceiver, intentFilter);
		// 添加导游模式切换监听
		//((AppContext)getApplication()).addGuideModeChangedListener(this);
		if(mBeaconSearcher.checkBLEEnable()){
			mBeaconSearcher.openSearcher();			
		}else{
			// 请求打开 Bluetooth
			Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
			requestBluetoothOn.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			// 设置 Bluetooth 设备可见时间
			requestBluetoothOn.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
					BLUETOOTH_DISCOVERABLE_DURATION);
			// 请求开启 Bluetooth
			this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);

		}
	}

	private void initBeaconSearcher() {

		// 设定用于展品定位的最小停留时间(ms)
		mBeaconSearcher = BeaconSearcher.getInstance(this);
		// NearestBeacon.GET_EXHIBIT_BEACON：展品定位beacon
		// NearestBeacon.GET_EXHIBIT_BEACON：游客定位beacon。可以不用设置上述的最小停留时间和最小距离
		mBeaconSearcher.setMin_stay_milliseconds(2000);
		// 设定用于展品定位的最小距离(m)
		mBeaconSearcher.setExhibit_distance(3.0);
		// 设置获取距离最近的beacon类型
		mBeaconSearcher.setNearestBeaconType(NearestBeacon.GET_EXHIBIT_BEACON);
		// 当蓝牙打开时，打开beacon搜索器，开始搜索距离最近的Beacon
		// 设置beacon监听器
		mBeaconSearcher.setNearestBeaconListener(this);

	}

	@Override
	public void getNearestBeacon(int type, Beacon beacon) {

		if (mBeaconListener != null) {
			mBeaconListener.onNearestBeaconDiscovered(type, beacon);
			LogUtil.i("测试信息", type+beacon.getBluetoothAddress());
			
		}
		
	}

	private void addListener() {
		iv_Search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent (HomeActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});

		iv_Drawer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(side_drawer.isMenuShowing()){
					side_drawer.showContent();
				}else{
					side_drawer.showMenu();
				}
			}
		});

	}

	private void initView() {
		museumId = getIntent().getStringExtra(Const.INTENT_MUSEUM_ID);
		homeViewPager=(ViewPager) findViewById(R.id.home_viewpager);
		iv_Search=(ImageView) findViewById(R.id.iv_serach);
		iv_Drawer=(ImageView) findViewById(R.id.iv_drawer);
		side_drawer = new DrawerView(this).initSlidingMenu();
	}

	/* 初始化fragment */
	private void initFragment() {
		fragments=new ArrayList<Fragment>();
		fragments.clear();// 清空
		museumIntroduceFragment = MuseumIntroduceFragment.newInstance(museumId);
		fragments.add(museumIntroduceFragment);
		homeFragmentPageAdapter = new HomeFragmentPagerAdapter(this.getSupportFragmentManager(),fragments);
		homeViewPager.setAdapter(homeFragmentPageAdapter);

		//homeViewPager.setOnPageChangeListener(pageListener);
	}

	@Override
	public void onFragmentInteraction(String arg) {

	}

	/**
	 * 设置蓝牙监听
	 */
	private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				switch (mBluetoothAdapter.getState()) {
				case BluetoothAdapter.STATE_ON:
					//	((AppContext)getApplication()).setBleEnable(true);
					break;
				case BluetoothAdapter.STATE_OFF:
					//	((AppContext)getApplication()).setBleEnable(false);
					break;
				}
			}
		}
	};


	/**
	 * 设置beacon搜索器监听接口
	 * 
	 * @param listener
	 */
	public static void setBeaconSearcherListener(onBeaconSearcherListener listener) {
		mBeaconListener = listener;
	}

	/**
	 * beacon搜索回调接口，用于给监听者传递nearest beacon数据
	 * 
	 * @author yetwish
	 */
	public interface onBeaconSearcherListener {

		void onNearestBeaconDiscovered(int type, Beacon beacon);
	}


	public static void setBeaconLocateType(int type) {
		if (type == NearestBeacon.GET_EXHIBIT_BEACON|| type == NearestBeacon.GET_LOCATION_BEACON)
			mBeaconSearcher.setNearestBeaconType(type);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBeaconSearcher != null) {
			mBeaconSearcher.closeSearcher();
		}
		unregisterReceiver(mBluetoothReceiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
		if (requestCode == REQUEST_CODE_BLUETOOTH_ON){
			switch (resultCode){
			// 点击确认按钮
			case Activity.RESULT_OK:	{
				LogUtil.i("TAG", "用户选择开启 Bluetooth，Bluetooth 会被开启");
			}
			break;
			// 点击取消按钮或点击返回键
			case Activity.RESULT_CANCELED:{
				LogUtil.i("TAG", "用户拒绝打开 Bluetooth, Bluetooth 不会被开启");
			}
			break;
			}
		}
	}

}
