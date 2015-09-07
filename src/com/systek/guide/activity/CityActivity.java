package com.systek.guide.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.systek.guide.R;
import com.systek.guide.adapter.CityAdapter;
import com.systek.guide.biz.CityBiz;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Constants;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.view.SideBar;
import com.systek.guide.common.view.SideBar.OnTouchingLetterChangedListener;
import com.systek.guide.db.CityDbHelper;
import com.systek.guide.entity.CityModule;
import com.systek.guide.parser.CharacterParser;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CityActivity extends BaseActivity {

	private SideBar sideBar;
	private TextView dialog;
	private CityAdapter adapter; 
	/** 
	 * 汉字转换成拼音的类 
	 */  
	private CharacterParser characterParser;  
	private List<CityModule> cities; 

	/** 
	 * 根据拼音来排列ListView里面的数据类 
	 */  
	private PinyinComparator pinyinComparator;
	private ListView cityListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		initViews();
	}

	private void initViews() {
		//实例化汉字转拼音类  
		characterParser = CharacterParser.getInstance();  
		pinyinComparator = new PinyinComparator();
		cityListView=(ListView)findViewById(R.id.city_list);  
		sideBar = (SideBar) findViewById(R.id.sidrbar);  
		dialog = (TextView) findViewById(R.id.city_dialog);  
		sideBar.setTextView(dialog);  

		//设置右侧触摸监听  
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {  

			@Override  
			public void onTouchingLetterChanged(String s) {  
				//该字母首次出现的位置  
				int position = adapter.getPositionForSection(s.charAt(0));  
				if(position != -1){  
					cityListView.setSelection(position);
				}  
			}  
		});  

		cityListView.setOnItemClickListener(new OnItemClickListener() {  

			@Override  
			public void onItemClick(AdapterView<?> parent, View view,  
					int position, long id) {  
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象  
				Toast.makeText(getApplication(), ((CityModule)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();  
				//TODO 选择城市后的操作
			}  
		});  

		initData();

		/* mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);  

	        //根据输入框输入值的改变来过滤搜索  
	        mClearEditText.addTextChangedListener(new TextWatcher() {  
	            @Override  
	            public void onTextChanged(CharSequence s, int start, int before, int count) {  
	                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表  
	                filterData(s.toString());  
	            }  
	            @Override  
	            public void beforeTextChanged(CharSequence s, int start, int count,  
	                    int after) {  
	            }  
	            @Override  
	            public void afterTextChanged(Editable s) {  
	            }  
	        });  
		 */
	}
	/* 获取数据*/
	private void initData() {
		CityDbHelper helper=null;
		cities=new ArrayList<CityModule>();
		try {
			helper=new CityDbHelper(this);
			/* 判断城市数据库是否存在，存在则查找，不存在联网查询*/
			if(helper.tabIsExist(Constants.DBTableNAME)){
				Cursor cursor=helper.select();
				while(cursor.moveToNext()){
					CityModule  city=new CityModule();
					city.setName(cursor.getString(cursor  
							.getColumnIndex("name")));  
					city.setAlpha(cursor.getString(cursor  
							.getColumnIndex("alpha")));  
					cities.add(city);  
				}
				// 根据a-z进行排序源数据  
				if(cities!=null){
					Collections.sort(cities, pinyinComparator);  
					adapter = new CityAdapter(this, cities);  
					cityListView.setAdapter(adapter);  
				}
				if(helper!=null){
					helper.close();
				}
			}else{
				CityBiz cityBiz=new CityBiz(this);
				cityBiz.execute(Constants.CITYLISTURL);
				new Thread(){public void run() {
					SQLiteDatabase db=null;
					CityDbHelper helper=null;
					try {
						helper=new CityDbHelper(getApplicationContext());
						db=helper.getWritableDatabase();
						while(cities==null){}
						if(!helper.tabIsExist(Constants.DBTableNAME)){
							db.execSQL("create table "+Constants.DBTableNAME +"(_id integer primary key autoincrement," 
									+" name varchar(100)," +" alpha varchar(2))");
						}
						for(CityModule city:cities){
							ContentValues values=new ContentValues();
							values.put("name", city.getName());
							values.put("alpha",city.getAlpha());
							db.insert(Constants.DBTableNAME, null, values);
						}
						if(helper!=null){
							helper.close();
						}
						if(db!=null){
							db.close();
						}
					} catch (Exception e) {
						ExceptionUtil.handleException(e);
					}

				};}.start();
			}
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}

	/** 
	 * 为ListView填充数据 
	 * @param data 
	 * @return 

	private List<CityModule> filledData(String [] data){  
		List<CityModule> mSortList = new ArrayList<CityModule>();  

		for(int i=0; i<data.length; i++){  
			CityModule cityModule = new CityModule();  
			cityModule.setName(data[i]);  
			//汉字转换成拼音  
			String pinyin = characterParser.getSelling(data[i]);  
			String sortString = pinyin.substring(0, 1).toUpperCase();  

			// 正则表达式，判断首字母是否是英文字母  
			if(sortString.matches("[A-Z]")){  
				cityModule.setAlpha(sortString.toUpperCase());  
			}else{  
				cityModule.setAlpha("#");  
			}  
			mSortList.add(cityModule);  
		}  
		return mSortList;  

	}  */

	/** 
	 * 根据输入框中的值来过滤数据并更新ListView 待用
	 * @param filterStr 

	private void filterData(String filterStr) {  
		List<CityModule> filterDateList = new ArrayList<CityModule>();  

		if (TextUtils.isEmpty(filterStr)) {  
			filterDateList = cities;  
		} else {  
			filterDateList.clear();  
			for (CityModule sortModel : cities) {  
				String name = sortModel.getName();  
				if (name.toUpperCase().indexOf(  
						filterStr.toString().toUpperCase()) != -1  
						|| characterParser.getSelling(name).toUpperCase()  
						.startsWith(filterStr.toString().toUpperCase())) {  
					filterDateList.add(sortModel);  
				}  
			}  
		}  

		// 根据a-z进行排序  
		Collections.sort(filterDateList, pinyinComparator);  
		adapter.updateListView(filterDateList);  
	}  */

	public class PinyinComparator implements Comparator<CityModule> {

		public int compare(CityModule o1, CityModule o2) {  
			//这里主要是用来对ListView里面的数据根据ABCDEFG...来排序  
			if (o2.getAlpha().equals("#")) {  
				return -1;  
			} else if (o1.getAlpha().equals("#")) {  
				return 1;  
			} else {  
				return o1.getAlpha().compareTo(o2.getAlpha());  
			}  
		}

	}

	/**
	 * 更新ListView中的数据
	 * @param cities
	 */
	public void updateListView(List<CityModule> cities) {
		this.cities=cities;
		//自定义Adapter 
		adapter=new CityAdapter(this, cities);
		cityListView.setAdapter(adapter);
	}


}
