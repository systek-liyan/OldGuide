package com.systek.guide.activity;

import java.io.File;
import java.io.IOException;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.R;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.ImageLoaderUtil;
import com.systek.guide.common.view.LyricView;
import com.systek.guide.entity.ExhibitBean;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class DescribeActivity extends BaseActivity {

	private LyricView lyricView;  
	private MediaPlayer mediaPlayer;  
	private Button ctrlPlay;  
	private SeekBar seekBar;  
	private String mp3Path;
	//歌词每行的间隔
	private int INTERVAL=8;
	private ExhibitBean exhibit;
	private boolean isLyricRun;
	

	/*用于判断文件类型*/
	private final  int FILE_TYPE_AUDIO=1;
	private final  int FILE_TYPE_ICON=2;
	private final  int FILE_TYPE_LYRIC=3;
	private final  int FILE_TYPE_IMGS=4;

	Handler mHandler = new Handler();
	private String exhibitId;
	private ImageView displayIv;
	private Button ctrlText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_describe);
		initView();
		initData();
		addListener();  
		seekBar.setMax(mediaPlayer.getDuration());  
		new Thread(new runable()).start();
	}
	
	protected void onPause() {
		super.onPause();
		if(mediaPlayer.isPlaying()){
			mediaPlayer.pause();
		}
		System.gc();
	};

	@Override
	protected void onStop() {
		super.onStop();
		isLyricRun=false;
		mediaPlayer.stop();
	}
	
	private void initData() {
		exhibitId=getIntent().getStringExtra(Const.INTENT_EXHIBIT_ID);
		exhibit= getExhibit(exhibitId);
		isLyricRun=true;
		mediaPlayer=new MediaPlayer();
		String audioPath=getLocalUrl(FILE_TYPE_AUDIO, exhibitId);
		String iconPath=getLocalUrl(FILE_TYPE_ICON, exhibitId);
		String imgsPath=getLocalUrl(FILE_TYPE_IMGS, exhibitId);
		String lyricPath=getLocalUrl(FILE_TYPE_LYRIC, exhibitId);
		
		// 判断sdcard上有没有MP3
		File file = new File(audioPath);
		if (file.exists()) {
			// sdcard显示
			mp3Path =audioPath;
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
		
		// 判断sdcard上有没有歌词
		File lyricFile = new File(lyricPath);
		if (lyricFile.exists()) {
			// sdcard显示
			LyricView.read(lyricPath);  
			lyricView.SetTextSize();  
			lyricView.setOffsetY(120); 
		}
		File iconFile = new File(iconPath);
		if (iconFile.exists()) {
			//从sdcard显示
			ImageLoaderUtil.displaySdcardImage(this, iconPath, displayIv);
		}
		
		File imgs = new File(imgsPath);
		if (imgs.exists()) {
			
		}
		
	}

	private void initView() {
		lyricView=(LyricView)findViewById(R.id.mylyricview);
		ctrlPlay=(Button)findViewById(R.id.ctrlPlay);
		ctrlText=(Button)findViewById(R.id.ctrlText);
		seekBar=(SeekBar)findViewById(R.id.MusicseekBar1);
		displayIv=(ImageView)findViewById(R.id.dispalyImageView);
	}

	private void addListener() {
		ctrlPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {  
					//ctrlPlay.setText("播放");  
					mediaPlayer.pause();  
				} else {
					//ctrlPlay.setText("暂停");
					mediaPlayer.start();  
					lyricView.setOffsetY(120 - lyricView.SelectIndex(mediaPlayer.getCurrentPosition())  
							* (lyricView.getSIZEWORD() + INTERVAL-1));  

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
			public void onProgressChanged(SeekBar seekBar, int progress,  
					boolean fromUser) {  
				if (fromUser) {  
					mediaPlayer.seekTo(progress);  
					lyricView.setOffsetY(120 - lyricView.SelectIndex(progress)* (lyricView.getSIZEWORD() + INTERVAL-1));  
				}  
			}  
		});  

		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
			@Override  
			public void onCompletion(MediaPlayer mp) {  
				resetMusic(mp3Path);  
				lyricView.SetTextSize();  
				lyricView.setOffsetY(120);  
				mediaPlayer.start();  
			}  
		});
		
		ctrlText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(lyricView.getVisibility()==View.VISIBLE){
					lyricView.setVisibility(View.GONE);
				}else{
					lyricView.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	private String getLocalUrl(int type,String exhibitId){
		ExhibitBean exh = getExhibit(exhibitId);
		String museumId=exhibit.getMuseumId();
		String loacalUrl=null;
		String fileName=null;
		String totalUrl=null;
		switch (type) {
		case FILE_TYPE_AUDIO:
			totalUrl=exh.getAudiourl();
			fileName=totalUrl.substring(totalUrl.lastIndexOf("/")+1);
			loacalUrl = Const.LOCAL_ASSETS_PATH+museumId+"/"+Const.LOCAL_FILE_TYPE_AUDIO+"/"+fileName;
			break;
		case FILE_TYPE_ICON:
			totalUrl=exh.getIconurl();
			fileName=totalUrl.substring(totalUrl.lastIndexOf("/")+1);
			loacalUrl = Const.LOCAL_ASSETS_PATH+museumId+"/"+Const.LOCAL_FILE_TYPE_IMAGE+"/"+fileName;
			break;
		case FILE_TYPE_LYRIC:
			totalUrl=exh.getTexturl();
			fileName=totalUrl.substring(totalUrl.lastIndexOf("/")+1);
			loacalUrl = Const.LOCAL_ASSETS_PATH+museumId+"/"+Const.LOCAL_FILE_TYPE_LYRIC+"/"+fileName;
			break;
		case FILE_TYPE_IMGS:
			totalUrl=exh.getImgsurl();
			fileName=totalUrl.substring(totalUrl.lastIndexOf("/")+1);
			loacalUrl = Const.LOCAL_ASSETS_PATH+museumId+"/"+Const.LOCAL_FILE_TYPE_IMAGE+"/"+fileName;
			break;
		}
		return loacalUrl;
	}

	private ExhibitBean getExhibit(String exhibitId) {
		ExhibitBean	 exh=null;
		DbUtils db= DbUtils.create(this);
		try {
			exh= db.findById(ExhibitBean.class, exhibitId);
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
		}finally{
			if(db!=null){
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

}
