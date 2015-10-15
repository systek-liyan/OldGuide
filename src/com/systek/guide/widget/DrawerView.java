package com.systek.guide.widget;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.systek.guide.R;
import com.systek.guide.activity.CityActivity;
import com.systek.guide.activity.MuseumDownloadActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DrawerView implements OnClickListener{
	
	private final Activity activity;
	SlidingMenu localSlidingMenu;
	private SwitchButton switch_mode_btn;
	private TextView auto_mode_text;
	private RelativeLayout setting_btn;
	private RelativeLayout city_choose_btn;
	private RelativeLayout offline_btn;
	private RelativeLayout search_btn;
	private RelativeLayout app_more_btn;
	public DrawerView(Activity activity) {
		this.activity = activity;
	}

	public SlidingMenu initSlidingMenu() {
		localSlidingMenu = new SlidingMenu(activity);
		localSlidingMenu.setMode(SlidingMenu.LEFT);//设置左右滑菜单
		localSlidingMenu.setTouchModeAbove(SlidingMenu.SLIDING_WINDOW);//设置要使菜单滑动，触碰屏幕的范围
//		localSlidingMenu.setTouchModeBehind(SlidingMenu.SLIDING_CONTENT);//设置了这个会获取不到菜单里面的焦点，所以先注释掉
		localSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);//设置阴影图片的宽度
		localSlidingMenu.setShadowDrawable(R.drawable.shadow);//设置阴影图片
		localSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);//SlidingMenu划出时主页面显示的剩余宽度
		localSlidingMenu.setFadeDegree(0.35F);//SlidingMenu滑动时的渐变程度
		localSlidingMenu.attachToActivity(activity, SlidingMenu.RIGHT);//使SlidingMenu附加在Activity右边
//		localSlidingMenu.setBehindWidthRes(R.dimen.left_drawer_avatar_size);//设置SlidingMenu菜单的宽度
		localSlidingMenu.setMenu(R.layout.left_drawer_fragment);//设置menu的布局文件
//		localSlidingMenu.toggle();//动态判断自动关闭或开启SlidingMenu
		localSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
					public void onOpened() {
						
					}
				});
		localSlidingMenu.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
				
			}
		});
		initView();
		return localSlidingMenu;
	}

	private void initView() {
		switch_mode_btn = (SwitchButton)localSlidingMenu.findViewById(R.id.night_mode_btn);
		auto_mode_text = (TextView)localSlidingMenu.findViewById(R.id.night_mode_text);
		switch_mode_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					auto_mode_text.setText(activity.getResources().getString(R.string.action_close_auto_mode));
				}else{
					auto_mode_text.setText(activity.getResources().getString(R.string.action_open_auto_mode));
				}
			}
		});
		switch_mode_btn.setChecked(false);
		if(switch_mode_btn.isChecked()){
			auto_mode_text.setText(activity.getResources().getString(R.string.action_close_auto_mode));
		}else{
			auto_mode_text.setText(activity.getResources().getString(R.string.action_open_auto_mode));
		}
		ArrayList< RelativeLayout> list = new ArrayList<RelativeLayout>();
		
		city_choose_btn=(RelativeLayout)activity.findViewById(R.id.city_choose_btn);
		offline_btn=(RelativeLayout)activity.findViewById(R.id.offline_btn);
		search_btn=(RelativeLayout)activity.findViewById(R.id.search_btn);
		app_more_btn=(RelativeLayout)activity.findViewById(R.id.app_more_btn);
		setting_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.setting_btn);
		
		list.add(city_choose_btn);
		list.add(offline_btn);
		list.add(search_btn);
		list.add(app_more_btn);
		list.add(setting_btn);
		
		for(RelativeLayout rl: list){
			rl.setOnClickListener(this);
		}
		
	}

	Class<?> targetClass = null;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.city_choose_btn:
			targetClass = CityActivity.class;
			break;
		case R.id.offline_btn:
			targetClass = MuseumDownloadActivity.class;
			break;
		case R.id.search_btn:
			targetClass = CityActivity.class;
			break;
		case R.id.app_more_btn:
			targetClass = CityActivity.class;
			break;
		case R.id.setting_btn:
			targetClass = CityActivity.class;
		default:
			break;
		}
		if(targetClass != null){
			Intent intent = new Intent(activity, targetClass);
			activity.startActivity(intent);
		}
		
	}

}
