package com.systek.guide.activity;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.systek.guide.R;
import com.systek.guide.adapter.HomeFragmentPagerAdapter;
import com.systek.guide.common.MyApplication;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.view.DrawerView;
import com.systek.guide.fragment.MuseumIntroduceFragment;
import com.systek.guide.fragment.MuseumIntroduceFragment.OnFragmentInteractionListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HomeActivity extends FragmentActivity implements OnFragmentInteractionListener{
	
	String museumId;
	MuseumIntroduceFragment museumIntroduceFragment;
	ViewPager homeViewPager;
	ArrayList<Fragment> fragments;
	private HomeFragmentPagerAdapter homeFragmentPageAdapter;
	private ImageView iv_Search;
	private ImageView iv_Drawer;
	/*侧滑菜单*/
	SlidingMenu side_drawer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		MyApplication.listActivity.add(this);
		initView();
		addListener();
		initFragment();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFragmentInteraction(String arg) {
		
	}
}
