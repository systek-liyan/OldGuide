package com.systek.guide.activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.R;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.ImageLoaderUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.common.view.LyricView;
import com.systek.guide.entity.ExhibitBean;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
	//歌词每行的间隔
	private int INTERVAL=8;
	private ExhibitBean exhibit;
	private boolean isLyricRun;


	/*用于判断文件类型*/
	private final  int FILE_TYPE_AUDIO=1;
	private final  int FILE_TYPE_ICON=2;
	private final  int FILE_TYPE_LYRIC=3;
	private final String MUSEUMID="museumId";

	Handler mHandler = new Handler();
	private String exhibitId;
	private ImageView displayIv;
	private Button ctrlText;
	private LinearLayout multi_angleLayout;
	private LinearLayout nearly_exhibitLayout;
	private String museumId;
	private String currentIconPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_describe);
		String exId=null;
		if(savedInstanceState!=null){
			exId=savedInstanceState.getString(MUSEUMID);			
		}
		if(exId!=null&&!exId.equals("")){
			exhibitId=exId;
		}else{
			exhibitId=getIntent().getStringExtra(Const.INTENT_EXHIBIT_ID);			
		}
		initView();
		initData(exhibitId);
		addListener();  
		seekBar.setMax(mediaPlayer.getDuration());  
		new Thread(new runable()).start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
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

	private void initData(String exId) {
		exhibit= getExhibit(exId);
		museumId=exhibit.getMuseumId();
		isLyricRun=true;
		mediaPlayer=new MediaPlayer();
		String currentAudioPath=getLocalUrl(FILE_TYPE_AUDIO, exId);
		currentIconPath=getLocalUrl(FILE_TYPE_ICON, exId);
		String currentLyricPath=getLocalUrl(FILE_TYPE_LYRIC, exId);
		// 判断sdcard上有没有MP3
		File file = new File(currentAudioPath);
		if (file.exists()) {
			// sdcard显示
			mp3Path =currentAudioPath;
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
		File lyricFile = new File(currentLyricPath);
		if (lyricFile.exists()) {
			// sdcard显示
			LyricView.read(currentLyricPath);  
			lyricView.SetTextSize();  
			lyricView.setOffsetY(120); 
		}
		File iconFile = new File(currentIconPath);
		if (iconFile.exists()) {
			//从sdcard显示
			ImageLoaderUtil.displaySdcardImage(this, currentIconPath, displayIv);
		}
		
		String imgsPath=exhibit.getImgsurl();
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		String[] imgsUrl=imgsPath.split(",");
		if(imgsUrl!=null&&!imgsUrl[0].equals("")&&imgsUrl.length!=0){
			for(int i=0;i<imgsUrl.length;i++){
				String imgsName=imgsUrl[i].substring(imgsUrl[i].lastIndexOf("/")+1);
				String [] nameTime=imgsName.split("\\*");
				map.put(nameTime[0], Integer.valueOf(nameTime[1]));
			}
		}else{
			ImageView multi_Img=new ImageView(this);
			multi_angleLayout.addView(multi_Img,new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
			ImageLoaderUtil.displaySdcardImage(this,currentIconPath, multi_Img);
		}
		Iterator<Entry<String, Integer>>  iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Integer> e = iter.next();
			String key = (String) e.getKey();
			final int value = (Integer) e.getValue();
			ImageView iv=new ImageView(this);
			iv.setTag(key);
			multi_angleLayout.addView(iv,new LayoutParams(160, LinearLayout.LayoutParams.MATCH_PARENT));
			ImageLoaderUtil.displaySdcardImage(this,Const.LOCAL_ASSETS_PATH+museumId+
					"/"+Const.LOCAL_FILE_TYPE_IMAGE+"/"+key, iv);
			iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String iconName=(String) v.getTag();
					ImageLoaderUtil.releaseImageViewResouce(displayIv);
					ImageLoaderUtil.displaySdcardImage(DescribeActivity.this, Const.LOCAL_ASSETS_PATH+museumId+
							"/"+Const.LOCAL_FILE_TYPE_IMAGE+"/"+iconName, displayIv);
					mediaPlayer.seekTo(value);
					lyricView.setOffsetY(120 - lyricView.SelectIndex(value)* (lyricView.getSIZEWORD() + INTERVAL-1));
				}
			});
		}
		
		String lExhibitId=exhibit.getLexhibit();
		String rExhibitId=exhibit.getRexhibit();
		if(lExhibitId!=null&&!lExhibitId.equals("")){
			LogUtil.i("lExhibitId:", "------------------------"+"1"+lExhibitId+"1");
			ImageView left_Img=new ImageView(this);
			left_Img.setTag(lExhibitId);
			String l_IconUrl=getLocalUrl(FILE_TYPE_ICON, lExhibitId);
			String l_IconName=l_IconUrl.substring(l_IconUrl.lastIndexOf("/")+1);
			nearly_exhibitLayout.addView(left_Img, new LayoutParams(160, LayoutParams.MATCH_PARENT));
			ImageLoaderUtil.displaySdcardImage(this,Const.LOCAL_ASSETS_PATH+museumId+
					"/"+Const.LOCAL_FILE_TYPE_IMAGE+"/"+l_IconName, left_Img);
			left_Img.setOnClickListener(myOnclickListener);
		}
		ImageView mid_Img=new ImageView(this);
		String mid_IconName=currentIconPath.substring(currentIconPath.lastIndexOf("/")+1);
		nearly_exhibitLayout.addView(mid_Img, new LayoutParams(160, LayoutParams.MATCH_PARENT));
		ImageLoaderUtil.displaySdcardImage(this,Const.LOCAL_ASSETS_PATH+museumId+
				"/"+Const.LOCAL_FILE_TYPE_IMAGE+"/"+mid_IconName, mid_Img);		
		if(rExhibitId!=null&&!rExhibitId.equals("")){
			ImageView right_Img=new ImageView(this);
			right_Img.setTag(rExhibitId);
			String r_IconUrl=getLocalUrl(FILE_TYPE_ICON, rExhibitId);
			String r_IconName=r_IconUrl.substring(r_IconUrl.lastIndexOf("/")+1);
			nearly_exhibitLayout.addView(right_Img, new LayoutParams(160, LayoutParams.MATCH_PARENT));
			ImageLoaderUtil.displaySdcardImage(this,Const.LOCAL_ASSETS_PATH+museumId+
					"/"+Const.LOCAL_FILE_TYPE_IMAGE+"/"+r_IconName, right_Img);
			right_Img.setOnClickListener(myOnclickListener);
		}
		
	}
	
	OnClickListener myOnclickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//TODO 
			String exhibitId=(String) v.getTag();
			Bundle bundle = new Bundle();
			bundle.putString(MUSEUMID, exhibitId);
			mediaPlayer.stop();
			onCreate(bundle);
		}
	};
	
	private void initView() {
		lyricView=(LyricView)findViewById(R.id.mylyricview);
		ctrlPlay=(Button)findViewById(R.id.ctrlPlay);
		ctrlText=(Button)findViewById(R.id.ctrlText);
		seekBar=(SeekBar)findViewById(R.id.MusicseekBar1);
		displayIv=(ImageView)findViewById(R.id.dispalyImageView);
		multi_angleLayout=(LinearLayout)findViewById(R.id.ll_currentExhibit);
		nearly_exhibitLayout=(LinearLayout)findViewById(R.id.ll_nearlyExhibit);
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
