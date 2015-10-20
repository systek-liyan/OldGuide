package com.systek.guide.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.R;
import com.systek.guide.beacon.BeaconSearcher;
import com.systek.guide.beacon.BeaconSearcher.OnNearestBeaconListener;
import com.systek.guide.beacon.NearestBeacon;
import com.systek.guide.common.MyApplication;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.ImageLoaderUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.entity.BeaconBean;
import com.systek.guide.entity.ExhibitBean;
import com.systek.guide.widget.LyricView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class DescribeActivity extends BaseActivity implements OnNearestBeaconListener{

	/**歌词view*/
	private LyricView lyricView;
	/**媒体播放器*/
	private MediaPlayer mediaPlayer;
	/**媒体控制按钮*/
	private Button ctrlPlay;
	/**媒体播放进度条*/
	private SeekBar seekBar;
	/**歌词每行的间隔*/
	private int INTERVAL = 8;
	/**当前展品*/
	private ExhibitBean exhibit;
	/**歌词是否在运行*/
	private boolean isLyricRun;

	/* 用于判断文件类型 */
	private final int FILE_TYPE_AUDIO = 1;
	private final int FILE_TYPE_LYRIC = 2;
	private final int FILE_TYPE_ICON = 3;
	/**博物馆ID标记*/
	private final String MUSEUMID = "museumId";
	/**切换歌词背景图片的MSG类型*/
	private final int MSG_WHAT_CHANGE_IMG = 4;

	/**当前博物馆ID*/
	private String museumId;
	/**当前展品ID*/
	private String currentExhibitId;
	/**讲解播放路径*/
	private String mp3Path;
	/**歌词背景*/
	private ImageView displayIv;
	/**控制文字是否显示的button*/
	private Button ctrlText;
	/**多角度图片布局*/
	private LinearLayout multi_angleLayout;
	/**附近展品布局*/
	private LinearLayout nearly_exhibitLayout;
	private String currentIconPath;
	private Iterator<Entry<Integer, String>> multiImgsIterator;
	private HashMap<Integer, String> multiImgsMap;
	private ArrayList<Integer> imgsTimeList;
	/**beacon搜索器接口*/
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

	public static final String BeaconNotify_open = "BeaconNoitify_open";
	public static final String BeaconNotify_close = "BeaconNoitify_close";
	public static final int MSG_WHAT_EXHIBIT_ID = 5;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_WHAT_CHANGE_IMG) {
				String imgName = String.valueOf(msg.obj);
				ImageLoaderUtil.releaseImageViewResouce(displayIv);
				ImageLoaderUtil.displaySdcardImage(DescribeActivity.this,
						Const.LOCAL_ASSETS_PATH + museumId + "/" 
								+ Const.LOCAL_FILE_TYPE_IMAGE + "/" + imgName,displayIv);
			}else if(msg.what ==MSG_WHAT_EXHIBIT_ID){
				removeAllImageViews(nearly_exhibitLayout);
				removeAllImageViews(multi_angleLayout);
				String exhibitId=(String) msg.obj;
				currentExhibitId=exhibitId;
				initData(exhibitId);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(savedInstanceState == null){
			super.onCreate(savedInstanceState);
		}
		setContentView(R.layout.activity_describe);			
		if (savedInstanceState != null) {
			String exId = savedInstanceState.getString(MUSEUMID);
			if (exId != null && !exId.equals("")) {
				currentExhibitId = exId;
			}
		}else {
			currentExhibitId = getIntent().getStringExtra(Const.INTENT_EXHIBIT_ID);
		}
		initialize();
	}

	private void initialize() {
		initView();
		addListener();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				resetMusic(mp3Path);
				lyricView.SetTextSize();
				lyricView.setOffsetY(220);
				mediaPlayer.start();
			}
		});
		isLyricRun = true;
		// 初始化beacon搜索器
		initBeaconSearcher();
		registBrocastReceiver();
		new Thread(new runable()).start();
		new ImgObserver().start();
		if(currentExhibitId!=null&&!currentExhibitId.equals("")){
			initData(currentExhibitId);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	protected void onPause() {
		super.onPause();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
		System.gc();
	};

	@Override
	protected void onStop() {
		super.onStop();
		isLyricRun = false;
		mediaPlayer.stop();
		finish();
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

	private void registBrocastReceiver() {
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
	}

	public void initBeaconSearcher() {
		if(mBeaconSearcher==null){
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
	}

	@Override
	public void getNearestBeacon(int type, Beacon beacon) {


		if(MyApplication.guideModel==MyApplication.GUIDE_MODEL_AUTO){
			if (beacon == null) {
				return;
			} else {
				//Identifier major = beacon.getId2();
				Identifier minor = beacon.getId3();
				DbUtils db = DbUtils.create(this);
				List<BeaconBean> beans = null;
				String beaconId = null;
				try {
					beans = db.findAll(Selector.from(BeaconBean.class).where("minor", "=", minor));
				} catch (DbException e) {
					ExceptionUtil.handleException(e);
				}
				if (beans != null&&beans.size()>0) {
					BeaconBean bean=beans.get(0);
					ExhibitBean eb=null;
					beaconId = bean.getId();
					try {
						eb=db.findFirst(Selector.from(ExhibitBean.class).where("beaconId","=",beaconId));
					} catch (DbException e) {
						ExceptionUtil.handleException(e);
					}
					if(eb!=null){
						String ebId=eb.getId();
						if(ebId!=null&&!ebId.equals(currentExhibitId)){
							currentExhibitId=ebId;
							LogUtil.i("tag",currentExhibitId);
							if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
								mediaPlayer.stop();	
							}
							//TODO
							//multi_angleLayout.clearDisappearingChildren();
							//	nearly_exhibitLayout.clearDisappearingChildren();
							Message msg=Message.obtain();
							msg.what=MSG_WHAT_EXHIBIT_ID;
							msg.obj=ebId;
							mHandler.sendMessage(msg);
						}
					}
				}
				if(db!=null){
					db.close();
				}
			}
		}
	}

	class  MyThread extends Thread{
		@Override
		public void run() {

		}
	}


	private void initData(String exId) {
		exhibit = getExhibit(exId);
		museumId = exhibit.getMuseumId();
		if(mediaPlayer!=null){
			if(mediaPlayer.isPlaying()){
				mediaPlayer.stop();				
			}
		}
		if(imgsTimeList!=null){
			imgsTimeList.clear();
		}else{
			imgsTimeList = new ArrayList<Integer>();			
		}
		String currentAudioPath = getLocalUrl(FILE_TYPE_AUDIO, exId);
		String currentLyricPath = getLocalUrl(FILE_TYPE_LYRIC, exId);
		initMedia(currentAudioPath);
		initLyric(currentLyricPath);
		initMainIcon();
		multiIcon();
		nearExhibit();

	}

	private void multiIcon() {
		/* 多角度图片 */
		// currentIconPath=exhibit.getImgsurl();
		multiImgsMap = new HashMap<Integer, String>();
		String imgsPath = exhibit.getImgsurl();
		String[] imgsUrl = imgsPath.split(",");
		if (imgsUrl != null && !imgsUrl[0].equals("") && imgsUrl.length != 0) {
			for (int i = 0; i < imgsUrl.length; i++) {
				String imgsName = imgsUrl[i].substring(imgsUrl[i].lastIndexOf("/") + 1);
				String[] nameTime = imgsName.split("\\*");
				multiImgsMap.put(Integer.valueOf(nameTime[1]), nameTime[0]);
			}
			multiImgsIterator = multiImgsMap.entrySet().iterator();
			while (multiImgsIterator.hasNext()) {
				Entry<Integer, String> e = multiImgsIterator.next();
				final int time = e.getKey();
				String imgPath = e.getValue();
				imgsTimeList.add(time);
				String multi_ImgPath = Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/"+ imgPath;

				ImageView imageView = new ImageView(this);  
				imageView.setScaleType(ImageView.ScaleType. CENTER);  
				multi_angleLayout.addView(imageView,new LayoutParams(360, LinearLayout.LayoutParams.MATCH_PARENT)); 
				ImageLoaderUtil.displaySdcardImage(this, multi_ImgPath, imageView);
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String iconName = (String) v.getTag();
						ImageLoaderUtil.releaseImageViewResouce(displayIv);
						ImageLoaderUtil.displaySdcardImage(DescribeActivity.this,
								Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + iconName,
								displayIv);
						mediaPlayer.seekTo(time);
						lyricView.setOffsetY(220 - lyricView.SelectIndex(time) * (lyricView.getSIZEWORD() + INTERVAL - 1));
					}
				});

			}

			if (imgsTimeList.size() > 0) {

				Collections.sort(imgsTimeList, new Comparator<Integer>() {
					@Override
					public int compare(Integer lhs, Integer rhs) {
						return lhs - rhs;
					}
				});
			}

		}else{
			//TODO
			String path = (String) displayIv.getTag();
			ImageView imageView = new ImageView(this);  
			imageView.setScaleType(ImageView.ScaleType. CENTER);  
			multi_angleLayout.addView(imageView,new LayoutParams(360, LinearLayout.LayoutParams.MATCH_PARENT)); 
			ImageLoaderUtil.displaySdcardImage(this, path, imageView);
		}
	}

	private void removeAllImageViews(LinearLayout ll) {//动态删除组件
		if(ll!=null&&ll.getChildCount()>0){
			//获取linearlayout子view的个数
			int count = ll.getChildCount();
			//研究整个LAYOUT布局，第0位的是含add和remove两个button的layout
			//第count-1个是那个文字被置中的textview
			//因此，在remove的时候，只能操作的是0<location<count-1这个范围的
			//在执行每次remove时，我们从count-2的位置即textview上面的那个控件开始删除~
			for (int i=0;i<count;i++) {
				//count-2>0用来判断当前linearlayout子view数多于2个，即还有我们点add增加的button
				//ll.removeViewAt(i);
				ImageView v=(ImageView) ll.getChildAt(i);
				ImageLoaderUtil.releaseImageViewResouce(v);
				if(v!=null){
					v.setVisibility(View.GONE);					
				}
			}		 
		}
	}


	private void nearExhibit() {
		String lExhibitId = exhibit.getLexhibit();
		String rExhibitId = exhibit.getRexhibit();
		if (lExhibitId != null && !lExhibitId.equals("")) {
			LogUtil.i("lExhibitId:", "------------------------" + "1" + lExhibitId + "1");

			ImageView left_Img = new ImageView(this);
			left_Img.setTag(lExhibitId);
			String l_IconUrl = getLocalUrl(FILE_TYPE_ICON, lExhibitId);
			String l_IconName = l_IconUrl.substring(l_IconUrl.lastIndexOf("/") + 1);
			nearly_exhibitLayout.addView(left_Img, new LayoutParams(360, LayoutParams.MATCH_PARENT));
			ImageLoaderUtil.displaySdcardImage(this,
					Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + l_IconName,
					left_Img);
			left_Img.setOnClickListener(myOnclickListener);
		}
		ImageView mid_Img = new ImageView(this);
		String mid_IconName = currentIconPath.substring(currentIconPath.lastIndexOf("/") + 1);
		nearly_exhibitLayout.addView(mid_Img, new LayoutParams(360, LayoutParams.MATCH_PARENT));
		ImageLoaderUtil.displaySdcardImage(this,
				Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + mid_IconName, mid_Img);
		if (rExhibitId != null && !rExhibitId.equals("")) {
			ImageView right_Img = new ImageView(this);
			right_Img.setTag(rExhibitId);
			String r_IconUrl = getLocalUrl(FILE_TYPE_ICON, rExhibitId);
			String r_IconName = r_IconUrl.substring(r_IconUrl.lastIndexOf("/") + 1);
			nearly_exhibitLayout.addView(right_Img, new LayoutParams(360, LayoutParams.MATCH_PARENT));
			ImageLoaderUtil.displaySdcardImage(this,
					Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + r_IconName,
					right_Img);
			right_Img.setOnClickListener(myOnclickListener);
		}
	}

	private void initLyric(String currentLyricPath) {
		// 判断sdcard上有没有歌词
		File lyricFile = new File(currentLyricPath);
		if (lyricFile.exists()) {
			// sdcard显示
			LyricView.read(currentLyricPath);
			lyricView.SetTextSize();
			lyricView.setOffsetY(220);
		}
	}

	private void initMainIcon() {
		String totalUrl = exhibit.getIconurl();
		String fileName = totalUrl.substring(totalUrl.lastIndexOf("/") + 1);
		String iconUrl = Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + fileName;
		File iconFile = new File(iconUrl);
		currentIconPath = iconUrl;
		if (iconFile.exists()) {
			ImageLoaderUtil.releaseImageViewResouce(displayIv);
			displayIv.setTag(iconUrl);
			ImageLoaderUtil.displaySdcardImage(DescribeActivity.this, iconUrl, displayIv);
		}
	}

	private void initMedia(String currentAudioPath) {
		// 判断sdcard上有没有MP3
		File audioFile = new File(currentAudioPath);
		if (audioFile.exists()) {
			// sdcard显示
			mp3Path = currentAudioPath;
			try {
				resetMusic(mp3Path);
			} catch (IllegalArgumentException e) {
				ExceptionUtil.handleException(e);
			} catch (SecurityException e) {
				ExceptionUtil.handleException(e);
			} catch (IllegalStateException e) {
				ExceptionUtil.handleException(e);
			}
		}
	}

	OnClickListener myOnclickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO
			String exhibitId = (String) v.getTag();
			if(exhibitId!=null&&!exhibitId.equals("")){
				if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
					mediaPlayer.stop();				
				}
				Message msg=Message.obtain();
				msg.what=MSG_WHAT_EXHIBIT_ID;
				msg.obj=exhibitId;
				mHandler.sendMessage(msg);
			}				
		}
	};

	private void initView() {
		lyricView = (LyricView) findViewById(R.id.mylyricview);
		ctrlPlay = (Button) findViewById(R.id.ctrlPlay);
		ctrlText = (Button) findViewById(R.id.ctrlText);
		seekBar = (SeekBar) findViewById(R.id.MusicseekBar1);
		displayIv = (ImageView) findViewById(R.id.dispalyImageView);
		multi_angleLayout = (LinearLayout) findViewById(R.id.ll_currentExhibit);
		nearly_exhibitLayout = (LinearLayout) findViewById(R.id.ll_nearlyExhibit);
	}

	private void addListener() {
		ctrlPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
					// ctrlPlay.setText("播放");
					mediaPlayer.pause();
				} else {
					// ctrlPlay.setText("暂停");
					mediaPlayer.start();
					lyricView.setOffsetY(220 - lyricView.SelectIndex(mediaPlayer.getCurrentPosition())
							* (lyricView.getSIZEWORD() + INTERVAL - 1));
				}
			}
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					mediaPlayer.seekTo(progress);
					lyricView.setOffsetY(
							220 - lyricView.SelectIndex(progress) * (lyricView.getSIZEWORD() + INTERVAL - 1));
				}
			}
		});

		ctrlText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lyricView.getVisibility() == View.VISIBLE) {
					lyricView.setVisibility(View.GONE);
				} else {
					lyricView.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private String getLocalUrl(int type, String exhibitId) {
		ExhibitBean exh = getExhibit(exhibitId);
		String museumId = exhibit.getMuseumId();
		String loacalUrl = null;
		String fileName = null;
		String totalUrl = null;
		switch (type) {
		case FILE_TYPE_AUDIO:
			totalUrl = exh.getAudiourl();
			fileName = totalUrl.substring(totalUrl.lastIndexOf("/") + 1);
			loacalUrl = Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_AUDIO + "/" + fileName;
			break;
		case FILE_TYPE_ICON:
			totalUrl = exh.getIconurl();
			fileName = totalUrl.substring(totalUrl.lastIndexOf("/") + 1);
			loacalUrl = Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + fileName;
			break;
		case FILE_TYPE_LYRIC:
			totalUrl = exh.getTexturl();
			fileName = totalUrl.substring(totalUrl.lastIndexOf("/") + 1);
			loacalUrl = Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_LYRIC + "/" + fileName;
			break;
		}
		return loacalUrl;
	}

	private ExhibitBean getExhibit(String exhibitId) {
		ExhibitBean exh = null;
		DbUtils db = DbUtils.create(this);
		try {
			exh = db.findById(ExhibitBean.class, exhibitId);
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return exh;
	}

	protected void resetMusic(String mp3Path) {
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(mp3Path);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		seekBar.setMax(mediaPlayer.getDuration());
	}

	class runable implements Runnable {

		@Override
		public void run() {

			while (isLyricRun) {
				try {
					Thread.sleep(100);
					if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
						lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.SpeedLrc());
						lyricView.SelectIndex(mediaPlayer.getCurrentPosition());
						seekBar.setProgress(mediaPlayer.getCurrentPosition());
						mHandler.post(mUpdateResults);
					}
				} catch (InterruptedException e) {
					ExceptionUtil.handleException(e);
				}
			}
		}
	}

	Runnable mUpdateResults = new Runnable() {
		public void run() {
			lyricView.invalidate(); // 更新视图
		}
	};


	class ImgObserver extends Thread{

		@Override
		public void run() {
			while(isLyricRun){
				if (imgsTimeList!=null&&imgsTimeList.size() > 0) {
					for (int i = 0; i < imgsTimeList.size(); i++) {
						int imgTime = imgsTimeList.get(i);
						int playTime = mediaPlayer.getCurrentPosition();
						int overTime= 0;
						if(i+1>=imgsTimeList.size()){
							overTime=imgsTimeList.get(i);
						}else{
							overTime=imgsTimeList.get(i+1);
						}
						if (playTime > imgTime && playTime < overTime) {
							Message msg = Message.obtain();
							msg.what = MSG_WHAT_CHANGE_IMG;
							msg.obj = multiImgsMap.get(imgsTimeList.get(i));
							mHandler.sendMessage(msg);
						}
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						ExceptionUtil.handleException(e);
					}					
				}
			}			
		}
	}

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
		if (type == NearestBeacon.GET_EXHIBIT_BEACON || type == NearestBeacon.GET_LOCATION_BEACON)
			mBeaconSearcher.setNearestBeaconType(type);
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
					MyApplication.guideModel=MyApplication.GUIDE_MODEL_AUTO;
					break;
				case BluetoothAdapter.STATE_OFF:
					MyApplication.guideModel=MyApplication.GUIDE_MODEL_HAND;
					break;
				default:
					MyApplication.guideModel=MyApplication.GUIDE_MODEL_HAND;
				}
			}else if(action.equals(BeaconNotify_open)){
				if (mBeaconSearcher!=null&&mBeaconSearcher.checkBLEEnable()) {
					mBeaconSearcher.openSearcher();
				}
			}else if(action.equals(BeaconNotify_close)){
				if(mBeaconSearcher!=null){
					mBeaconSearcher.closeSearcher();					
				}
			}
		}
	};

}
