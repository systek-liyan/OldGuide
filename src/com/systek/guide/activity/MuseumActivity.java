package com.systek.guide.activity;

import java.util.ArrayList;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.systek.guide.R;
import com.systek.guide.adapter.MuseumAdapter;
import com.systek.guide.biz.BeansManageBiz;
import com.systek.guide.biz.BizFactory;
import com.systek.guide.common.MyApplication;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Const;
import com.systek.guide.entity.MuseumBean;
import com.systek.guide.widget.DrawerView;
import com.systek.guide.widget.TopBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MuseumActivity extends BaseActivity{

	private ListView lvMuseum;
	/*当前所在城市*/
	private String city;
	/*侧滑菜单*/
	SlidingMenu side_drawer;
	List<MuseumBean> museumList;
	private MuseumAdapter adapter;
	private TopBar headerLayout;
	private final int MSG_WHAT_MUSEUMS=1;

	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==MSG_WHAT_MUSEUMS){
				adapter.updateData(museumList);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_museum);
		initialize();
	}

	private void initialize() {
		// 初始化视图
		initViews();
		// 初始化数据
		initData();
		//初始化侧滑菜单
		initSlidingMenu();
	}

	private void initSlidingMenu() {
		side_drawer = new DrawerView(this).initSlidingMenu();
	}

	private void initData() {
		city=getIntent().getStringExtra("city");
		new Thread(){
			public void run() {
				BeansManageBiz biz=(BeansManageBiz) BizFactory.getBeansManageBiz(MuseumActivity.this);
				museumList=biz.getAllBeans(MuseumBean.class,"");
				while(museumList==null){}
				handler.sendEmptyMessage(MSG_WHAT_MUSEUMS);
			};
		}.start();
		/*DbUtils db = DbUtils.create(this);
		try {
			List<MuseumBean> list = db.findAll(Selector.from(MuseumBean.class) .where("city" ,"like", "%"+city+"%"));
			if(list!=null&&list.size()>0){
				LogUtil.i("TAG", list.get(0).toString());
				museumList=list;
			}else{
				museumList=new ArrayList<MuseumBean>();
			}

		} catch (DbException e) {
			ExceptionUtil.handleException(e);
		}finally{
		if(db!=null){
				db.close();
			}
		}*/
	}

	private void initViews() {
		// 初始化头部
		headerLayout = (TopBar) findViewById(R.id.activity_museum_header);
		headerLayout.setSearchingVisible(false);
		headerLayout.setTitle(getResources().getString(R.string.title_activity_museum));
		headerLayout.setTitleLeftGravity();
		headerLayout.findViewById(R.id.frag_header_iv_menu).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(side_drawer.isMenuShowing()){
							side_drawer.showContent();
						}else{
							side_drawer.showMenu();
						}
					}
				});
		lvMuseum = (ListView) findViewById(R.id.activity_museum_list);
		if(museumList==null){
			museumList=new ArrayList<MuseumBean>();
		}
		adapter =new MuseumAdapter(museumList, this);
		lvMuseum.setAdapter(adapter);

		lvMuseum.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				MuseumBean bean =(MuseumBean) adapter.getItem(position);
				Intent intent = new Intent(MuseumActivity.this,HomeActivity.class);
				intent.putExtra(Const.INTENT_MUSEUM_ID, bean.getId());
				startActivity(intent);
			}
		});

	}


	@Override
	protected void onResume() {
		super.onResume();
		adapter.updateData(museumList);
	}


	/*用于计算点击返回键时间*/
	private long mExitTime=0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(side_drawer.isMenuShowing() ||side_drawer.isSecondaryMenuShowing()){
				side_drawer.showContent();
			}else {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(this, "在按一次退出",
							Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				} else {
					MyApplication.exit();
				}
			}
			return true;
		}
		//拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
