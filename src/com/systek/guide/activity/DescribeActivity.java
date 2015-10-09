package com.systek.guide.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.R;
import com.systek.guide.R.id;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.ImageLoaderUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.common.view.LyricView;
import com.systek.guide.entity.ExhibitBean;

import android.annotation.SuppressLint;
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

public class DescribeActivity extends BaseActivity {

	private LyricView lyricView;
	private MediaPlayer mediaPlayer;
	private Button ctrlPlay;
	private SeekBar seekBar;
	private String mp3Path;
	// 歌词每行的间隔
	private int INTERVAL = 8;
	private ExhibitBean exhibit;
	private boolean isLyricRun;

	/* 用于判断文件类型 */
	private final int FILE_TYPE_AUDIO = 1;
	private final int FILE_TYPE_LYRIC = 2;
	private final int FILE_TYPE_ICON = 3;
	private final String MUSEUMID = "museumId";
	private final int MSG_WHAT_CHANGE_IMG = 4;

	private String exhibitId;
	private ImageView displayIv;
	private Button ctrlText;
	private LinearLayout multi_angleLayout;
	private LinearLayout nearly_exhibitLayout;
	private String museumId;
	private String currentIconPath;
	Iterator<Entry<Integer, String>> multiImgsIterator;
	private HashMap<Integer, String> multiImgsMap;
	private ArrayList<Integer> imgsTimeList;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_WHAT_CHANGE_IMG) {
				String imgName = String.valueOf(msg.obj);
				ImageLoaderUtil.releaseImageViewResouce(displayIv);
				ImageLoaderUtil.displaySdcardImage(DescribeActivity.this,
						Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + imgName,
						displayIv);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_describe);
		String exId = null;
		if (savedInstanceState != null) {
			exId = savedInstanceState.getString(MUSEUMID);
		}
		if (exId != null && !exId.equals("")) {
			exhibitId = exId;
		} else {
			exhibitId = getIntent().getStringExtra(Const.INTENT_EXHIBIT_ID);
		}
		initView();
		initData(exhibitId);
		addListener();
		seekBar.setMax(mediaPlayer.getDuration());
		new Thread(new runable()).start();
		new ImgObserver().start();
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

	private void initData(String exId) {
		exhibit = getExhibit(exId);
		museumId = exhibit.getMuseumId();
		isLyricRun = true;
		mediaPlayer = new MediaPlayer();
		imgsTimeList = new ArrayList<Integer>();

		String currentAudioPath = getLocalUrl(FILE_TYPE_AUDIO, exId);
		String currentLyricPath = getLocalUrl(FILE_TYPE_LYRIC, exId);

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

		String totalUrl = exhibit.getIconurl();
		String fileName = totalUrl.substring(totalUrl.lastIndexOf("/") + 1);
		String iconUrl = Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + fileName;
		currentIconPath = iconUrl;
		File iconFile = new File(iconUrl);
		if (iconFile.exists()) {
			ImageLoaderUtil.releaseImageViewResouce(displayIv);
			ImageLoaderUtil.displaySdcardImage(DescribeActivity.this, iconUrl, displayIv);
		}

		// 判断sdcard上有没有歌词
		File lyricFile = new File(currentLyricPath);
		if (lyricFile.exists()) {
			// sdcard显示
			LyricView.read(currentLyricPath);
			lyricView.SetTextSize();
			lyricView.setOffsetY(220);
		}

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
				currentIconPath = Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/"
						+ imgPath;
				ImageView iv = new ImageView(this);
				iv.setTag(imgPath);
				multi_angleLayout.addView(iv, new LayoutParams(160, LinearLayout.LayoutParams.MATCH_PARENT));
				ImageLoaderUtil.displaySdcardImage(this, currentIconPath, iv);
				iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String iconName = (String) v.getTag();
						ImageLoaderUtil.releaseImageViewResouce(displayIv);
						ImageLoaderUtil.displaySdcardImage(DescribeActivity.this,
								Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + iconName,
								displayIv);
						mediaPlayer.seekTo(time);
						lyricView.setOffsetY(
								220 - lyricView.SelectIndex(time) * (lyricView.getSIZEWORD() + INTERVAL - 1));
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

		}

		String lExhibitId = exhibit.getLexhibit();
		String rExhibitId = exhibit.getRexhibit();
		if (lExhibitId != null && !lExhibitId.equals("")) {
			LogUtil.i("lExhibitId:", "------------------------" + "1" + lExhibitId + "1");
			ImageView left_Img = new ImageView(this);
			left_Img.setTag(lExhibitId);
			String l_IconUrl = getLocalUrl(FILE_TYPE_ICON, lExhibitId);
			String l_IconName = l_IconUrl.substring(l_IconUrl.lastIndexOf("/") + 1);
			nearly_exhibitLayout.addView(left_Img, new LayoutParams(160, LayoutParams.MATCH_PARENT));
			ImageLoaderUtil.displaySdcardImage(this,
					Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + l_IconName,
					left_Img);
			left_Img.setOnClickListener(myOnclickListener);
		}
		ImageView mid_Img = new ImageView(this);
		String mid_IconName = currentIconPath.substring(currentIconPath.lastIndexOf("/") + 1);
		nearly_exhibitLayout.addView(mid_Img, new LayoutParams(160, LayoutParams.MATCH_PARENT));
		ImageLoaderUtil.displaySdcardImage(this,
				Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + mid_IconName, mid_Img);
		if (rExhibitId != null && !rExhibitId.equals("")) {
			ImageView right_Img = new ImageView(this);
			right_Img.setTag(rExhibitId);
			String r_IconUrl = getLocalUrl(FILE_TYPE_ICON, rExhibitId);
			String r_IconName = r_IconUrl.substring(r_IconUrl.lastIndexOf("/") + 1);
			nearly_exhibitLayout.addView(right_Img, new LayoutParams(160, LayoutParams.MATCH_PARENT));
			ImageLoaderUtil.displaySdcardImage(this,
					Const.LOCAL_ASSETS_PATH + museumId + "/" + Const.LOCAL_FILE_TYPE_IMAGE + "/" + r_IconName,
					right_Img);
			right_Img.setOnClickListener(myOnclickListener);
		}

	}

	OnClickListener myOnclickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO
			String exhibitId = (String) v.getTag();
			Bundle bundle = new Bundle();
			bundle.putString(MUSEUMID, exhibitId);
			mediaPlayer.stop();
			onCreate(bundle);
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
				if (mediaPlayer.isPlaying()) {
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

		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				resetMusic(mp3Path);
				lyricView.SetTextSize();
				lyricView.setOffsetY(220);
				mediaPlayer.start();
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
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class runable implements Runnable {

		@Override
		public void run() {

			while (isLyricRun) {
				try {
					Thread.sleep(100);
					if (mediaPlayer.isPlaying()) {
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
			if (imgsTimeList.size() > 0) {
				while(isLyricRun){
					for (int i = 0; i < imgsTimeList.size(); i++) {
						int imgTime = imgsTimeList.get(i);
						int playTime = mediaPlayer.getCurrentPosition();
						if (playTime > imgTime && playTime < (imgTime + 200)) {
							Message msg = Message.obtain();
							msg.what = MSG_WHAT_CHANGE_IMG;
							msg.obj = multiImgsMap.get(imgsTimeList.get(i));
							mHandler.sendMessage(msg);
							LogUtil.i("TAG", "信息已发送------------------");
						}
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						ExceptionUtil.handleException(e);
					}					
				}
			}			
		}
	}
}
