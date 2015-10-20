package com.systek.guide.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.systek.guide.R;
import com.systek.guide.adapter.ExhibitAdapter;
import com.systek.guide.biz.BeansManageBiz;
import com.systek.guide.biz.BizFactory;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Const;
import com.systek.guide.entity.ExhibitBean;
import com.systek.guide.entity.MuseumBean;
import com.systek.guide.widget.DrawerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class HomeActivity extends BaseActivity  {

	//private static final String MUSEUMID = "museumId";
	private String museumId;
	private ImageView iv_Search;
	private ImageView iv_Drawer;
	/* 侧滑菜单 */
	SlidingMenu side_drawer;
	private ListView boutiqueListView;
	private MuseumBean museum;
	private ExhibitAdapter exhibitAdapter;
	private List<ExhibitBean> boutiqueList;
	private Button specialButton;
	private Button media_ctrl;
	private final int MSG_WHAT_DATA = 1;

	/*private static onBeaconSearcherListener mBeaconListener;
	*//**
	 * store BeaconSearcher instance, we use it to range beacon,and get the
	 * minBeacon from it.
	 *//*
	private static BeaconSearcher mBeaconSearcher;

	*//**
	 * 自定义的打开 Bluetooth 的请求码，与 onActivityResult 中返回的 requestCode 匹配。
	 *//*
	private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;
	*//**
	 * Bluetooth 设备可见时间，单位：秒。
	 *//*
	private static final int BLUETOOTH_DISCOVERABLE_DURATION = 250;*/
	//public static final String BeaconNotify = "BeaconNoitify";

	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		public void handleMessage(Message msg){
		if(msg.what==MSG_WHAT_DATA){
			updateData();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_activity);
		initialize();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mp != null && mp.isPlaying()) {
			mp.stop();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mp != null && mp.isPlaying()) {
			mp.stop();
			mp = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*if (mBeaconSearcher != null) {
			mBeaconSearcher.closeSearcher();
		}
		unregisterReceiver(mBluetoothReceiver);*/
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
		if (requestCode == REQUEST_CODE_BLUETOOTH_ON) {
			switch (resultCode) {
			// 点击确认按钮
			case Activity.RESULT_OK: {
				LogUtil.i("TAG", "用户选择开启 Bluetooth，Bluetooth 会被开启");
			}
				break;
			// 点击取消按钮或点击返回键
			case Activity.RESULT_CANCELED: {
				LogUtil.i("TAG", "用户拒绝打开 Bluetooth, Bluetooth 不会被开启");
			}
				break;
			}
		}

	}
*/
	private void initialize() {

		initView();
		addListener();
		/*// 初始化beacon搜索器
		initBeaconSearcher();*/
		initData();
		// 注册广播监听器
		//registBrocastReceiver();
		updateData();
	}

	private void updateData() {
		if (museum != null && boutiqueList != null) {
			if (exhibitAdapter == null) {
				exhibitAdapter = new ExhibitAdapter(this, boutiqueList);
				boutiqueListView.setAdapter(exhibitAdapter);
			}
			exhibitAdapter.updateData(museum, boutiqueList);
			displayAudio();
		}
	}

	private void initData() {
		new Thread() {
			public void run() {
				museumId = getIntent().getStringExtra(Const.INTENT_MUSEUM_ID);
				BeansManageBiz biz = (BeansManageBiz) BizFactory.getBeansManageBiz(HomeActivity.this);
				museum = biz.getBeanById(MuseumBean.class, museumId);
				boutiqueList = biz.getAllBeans(ExhibitBean.class, "");
				handler.sendEmptyMessage(MSG_WHAT_DATA);
			};
		}.start();
	}

	/*private void registBrocastReceiver() {

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
		registerReceiver(mBluetoothReceiver, intentFilter);
		// 添加导游模式切换监听
		// ((AppContext)getApplication()).addGuideModeChangedListener(this);
		if (mBeaconSearcher!=null&&mBeaconSearcher.checkBLEEnable()) {
			mBeaconSearcher.openSearcher();
		} else {
			// 请求打开 Bluetooth
			Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
			requestBluetoothOn.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			// 设置 Bluetooth 设备可见时间
			requestBluetoothOn.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BLUETOOTH_DISCOVERABLE_DURATION);
			// 请求开启 Bluetooth
			this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);

		}
	}*/

	/*public void initBeaconSearcher() {

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
	}*/

	/*@Override
	public void getNearestBeacon(int type, Beacon beacon) {
	//	if (mBeaconListener != null) {
			// mBeaconListener.onNearestBeaconDiscovered(type, beacon);
			// LogUtil.i("测试信息", type + beacon.getBluetoothAddress());
			if (beacon == null) {
				return;
			} else {
				Identifier major = beacon.getId2();
				Identifier minor = beacon.getId3();
				DbUtils db = DbUtils.create(this);
				BeaconBean bean = null;
				String beaconId = null;
				List<BeaconBean> beans;
				//String sql="select * from com_systek_guide_entity_BeaconBean where minor = "+minor;
				try {
					bean = db.findFirst(Selector.from(BeaconBean.class).where("minor", "=", minor));
					//beans=db.findAll(Selector.from(BeaconBean.class).where("minor", "=", minor));
				} catch (DbException e) {
					ExceptionUtil.handleException(e);
				}
				if (bean != null) {
					beaconId = bean.getId();
					LogUtil.i("tag", beaconId);
				}

			}

		//}
	}*/

	private void addListener() {
		iv_Search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
				startActivity(intent);
			}
		});

		iv_Drawer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (side_drawer.isMenuShowing()) {
					side_drawer.showContent();
				} else {
					side_drawer.showMenu();
				}
			}
		});

		boutiqueListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
				} else {
					ExhibitBean exhibit = (ExhibitBean) exhibitAdapter.getItem(position);
					String exhibitId = exhibit.getId();
					Intent intent = new Intent(HomeActivity.this, DescribeActivity.class);
					intent.putExtra(Const.INTENT_EXHIBIT_ID, exhibitId);
					startActivity(intent);
				}
			}
		});

		specialButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				Intent intent = new Intent(HomeActivity.this, SubjectSelectActivity.class);
				intent.putExtra(Const.INTENT_MUSEUM_ID, museumId);
				startActivity(intent);
			}
		});

		media_ctrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mp != null) {
					if (mp.isPlaying()) {
						mp.pause();
					} else {
						mp.start();
					}
				}
			}
		});
	}

	private void initView() {
		iv_Search = (ImageView) findViewById(R.id.iv_serach);
		iv_Drawer = (ImageView) findViewById(R.id.iv_drawer);
		specialButton = (Button) findViewById(R.id.special_Button);
		side_drawer = new DrawerView(this).initSlidingMenu();
		boutiqueListView = (ListView) findViewById(R.id.frag_museum_introduce_listview);
		media_ctrl = (Button) findViewById(R.id.media_ctrl);
	}

	private MediaPlayer mp;

	private void displayAudio() {

		new Thread() {
			public void run() {
				if (mp != null && mp.isPlaying()) {
					mp.stop();
				}
				mp = new MediaPlayer();
				String totalIntroduceAudio = museum.getAudioUrl();
				String audioName = totalIntroduceAudio.substring(totalIntroduceAudio.lastIndexOf("/") + 1);
				String audioUrl = Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_AUDIO + "/"
						+ audioName;
				// 判断sdcard上有没有图片
				File file = new File(Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_AUDIO, audioName);
				if (file.exists()) {
					// sdcard显示
					String filePathName = Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_AUDIO + "/"
							+ audioName;
					try {
						mp.setDataSource(filePathName);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// 网络获取显示
					audioUrl = Const.BASEURL + totalIntroduceAudio;
					try {
						mp.setDataSource(audioUrl);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					mp.prepare();
					mp.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

	/**
	 * 设置beacon搜索器监听接口
	 * 
	 * @param listener
	 *//*
	public static void setBeaconSearcherListener(onBeaconSearcherListener listener) {
		mBeaconListener = listener;
	}

	*//**
	 * beacon搜索回调接口，用于给监听者传递nearest beacon数据
	 * 
	 * @author yetwish
	 *//*
	public interface onBeaconSearcherListener {

		void onNearestBeaconDiscovered(int type, Beacon beacon);
	}

	public static void setBeaconLocateType(int type) {
		if (type == NearestBeacon.GET_EXHIBIT_BEACON || type == NearestBeacon.GET_LOCATION_BEACON)
			mBeaconSearcher.setNearestBeaconType(type);
	}

	*//**
	 * 设置蓝牙监听
	 *//*
	private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				switch (mBluetoothAdapter.getState()) {
				case BluetoothAdapter.STATE_ON:
					MyApplication.guideModel=MyApplication.GUIDE_MODEL_AUTO;
					break;
				case BluetoothAdapter.STATE_OFF:
					MyApplication.guideModel=MyApplication.GUIDE_MODEL_HAND;
					break;
				default:
					MyApplication.guideModel=MyApplication.GUIDE_MODEL_HAND;
				}
			}else if(action.equals(BeaconNotify)){
				if (mBeaconSearcher!=null&&mBeaconSearcher.checkBLEEnable()) {
					mBeaconSearcher.openSearcher();
				}
				
			}
		}
	};*/
}
