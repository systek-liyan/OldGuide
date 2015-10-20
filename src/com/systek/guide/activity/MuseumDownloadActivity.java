package com.systek.guide.activity;

import java.util.ArrayList;

import com.systek.guide.R;
import com.systek.guide.adapter.DownloadFragmentPagerAdapter;
import com.systek.guide.common.MyApplication;
import com.systek.guide.fragment.DownloadFragment;
import com.systek.guide.fragment.DownloadManageFragment;
import com.systek.guide.widget.MyToggleButton;
import com.systek.guide.widget.MyToggleButton.OnStateChangedListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MuseumDownloadActivity extends FragmentActivity {

	private ImageView iv_back;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private ViewPager mViewPager;
	/** 头部标题ToggleButton按钮 */
	private MyToggleButton tbTitle;
	// 在onCreate()和onSaveInstanceState()之间传递参数值，以记录Activity上一次关闭前的状态
	private static final String Bundle_key = "state";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_museum_download);
		MyApplication.listActivity.add(this);
		// 恢复到上一次关闭状态
		if (savedInstanceState != null) {
			tbTitle.setCurrentState(savedInstanceState.getInt(Bundle_key));
		}
		initView();
		addListener();
		initFragment();
	}

	private void addListener() {

		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tbTitle.setStateChangedListener(new OnStateChangedListener() {

			@Override
			public void onSwitchOn() {
				changeView(0);
			}

			@Override
			public void onSwitchOff() {
				changeView(1);
			}
		});
	}

	private void initView() {

		tbTitle = (MyToggleButton) findViewById(R.id.toggle_btn);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);

	}

	/* 初始化fragment */
	private void initFragment() {
		fragments.clear();// 清空
		DownloadFragment dlFragment = DownloadFragment.newInstance();
		DownloadManageFragment mnFragment = DownloadManageFragment.newInstance();
		fragments.add(dlFragment);
		fragments.add(mnFragment);

		DownloadFragmentPagerAdapter mAdapter = new DownloadFragmentPagerAdapter
				(this.getSupportFragmentManager(),fragments);
		mViewPager.setAdapter(mAdapter);
		
		mViewPager.setOnPageChangeListener(pageListener);
	}

	/**
	 * ViewPager切换监听方法,当页面改动时，头部标题栏随之改动
	 */
	public OnPageChangeListener pageListener = new OnPageChangeListener() {
		 
		@Override
		public void onPageSelected(int position) {
			
			if (position == 0) {
				tbTitle.setCurrentState(MyToggleButton.STATE_ON);
			} else if(position==1){
				tbTitle.setCurrentState(MyToggleButton.STATE_OFF);
			}else{}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int position) {
		}
	};

	// 手动设置ViewPager要显示的视图
	private void changeView(int desTab) {
		mViewPager.setCurrentItem(desTab, true);
	}

}
