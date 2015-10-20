package com.systek.guide.activity;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.R;
import com.systek.guide.R.drawable;
import com.systek.guide.R.id;
import com.systek.guide.R.layout;
import com.systek.guide.adapter.PublicExhibitAdapter;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.entity.ExhibitBean;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class SubjectSelectActivity extends BaseActivity {

	private ListView lableSelectedListView;
	private Button selectButton_hot;
	private Button selectButton_use;
	private Button selectButton_time;
	private Button selectButton_material;
	private ImageView drawerMenuButton;
	private List<ExhibitBean> allExhibitList;
	private List<ExhibitBean> checkedExhibitList;
	private String museumId;
	private PublicExhibitAdapter subjectSelectExhibitAdapter;
	private Button check_stone;
	private Button check_ceram;
	private Button check_copper;
	private Button check_dynasty;
	private Button check_framing;
	private Button check_jade;
	private LinearLayout checkedLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subject_select);
		museumId=getIntent().getStringExtra(Const.INTENT_MUSEUM_ID);
		initView();
		initData();
		addlistener();
	}

	private void addlistener() {
		selectButton_hot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		});
	}

	private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_select_label, null);
       check_stone=(Button) contentView.findViewById(R.id.check_stone);
        check_ceram=(Button) contentView.findViewById(R.id.check_ceram);
        check_copper=(Button) contentView.findViewById(R.id.check_copper);
        check_dynasty=(Button) contentView.findViewById(R.id.check_dynasty);
        check_framing=(Button) contentView.findViewById(R.id.check_framing);
        check_jade=(Button) contentView.findViewById(R.id.check_jade);
        
        check_stone.setOnClickListener(checkBtListener);
        check_ceram.setOnClickListener(checkBtListener);
        check_copper.setOnClickListener(checkBtListener);
        check_dynasty.setOnClickListener(checkBtListener);
        check_framing.setOnClickListener(checkBtListener);
        check_jade.setOnClickListener(checkBtListener);
        
        
        // 设置按钮的点击事件
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	LogUtil.i("popupWindow", "--------onTouch");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.switch_btn_on));
        int[] location =new int[2];
        view.getLocationOnScreen(location);
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0]+view.getWidth()+5, location[1]-popupWindow.getHeight());

    }

	OnClickListener checkBtListener= new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Button b=(Button)v;
			String str=(String) b.getText();
			LogUtil.i("已选择-----------------", str);
			v.setVisibility(View.GONE);
			List<ExhibitBean> exList=null;
			DbUtils db=DbUtils.create(SubjectSelectActivity.this);
			try {
				exList=db.findAll(Selector.from(ExhibitBean.class).where("labels", "like", "%"+str+"%"));
			} catch (DbException e) {
				ExceptionUtil.handleException(e);
			}finally{
				if(db!=null){
					db.close();
				}
			}
			for(ExhibitBean bean:exList){
				checkedExhibitList.add(bean);
			}
			Button bt=new Button(SubjectSelectActivity.this);
			bt.setText(str);
			checkedLinearLayout.addView(bt,new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,35));
			subjectSelectExhibitAdapter.updateData(checkedExhibitList);
		}
	};
	
	private void initData() {
		DbUtils db=DbUtils.create(this);
		try {
			checkedExhibitList=new ArrayList<ExhibitBean>();
			allExhibitList=db.findAll(Selector.from(ExhibitBean.class).where("museumId", "like", "%"+museumId+"%"));
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
		}
		subjectSelectExhibitAdapter=new PublicExhibitAdapter(this, allExhibitList);
		lableSelectedListView.setAdapter(subjectSelectExhibitAdapter);
	}

	private void initView() {
		lableSelectedListView=(ListView)findViewById(R.id.lableSelectedListView);
		selectButton_hot=(Button)findViewById(R.id.selectButton_hot); 
		selectButton_material=(Button)findViewById(R.id.selectButton_material); 
		selectButton_time=(Button)findViewById(R.id.selectButton_time); 
		selectButton_use=(Button)findViewById(R.id.selectButton_use); 
		drawerMenuButton=(ImageView)findViewById(R.id.drawerMenuButton); 
		checkedLinearLayout=(LinearLayout)findViewById(R.id.linearLayout1); 
	}

}
