package com.systek.guide.activity;

import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.R;
import com.systek.guide.R.id;
import com.systek.guide.R.layout;
import com.systek.guide.adapter.SearchNamesAdapter;
import com.systek.guide.common.base.BaseActivity;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.entity.ExhibitBean;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends BaseActivity {

	private ListView mListView;
	private SearchView mSearchView;
	List<ExhibitBean> exhibitList;
	private SearchNamesAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		mListView = (ListView) findViewById(R.id.search_listview);
		mSearchView=(SearchView)findViewById(R.id.search);
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setSubmitButtonEnabled(true);
		mSearchView.setQueryHint("查询");		
		DbUtils db=DbUtils.create(SearchActivity.this);
		try {
			exhibitList=db.findAll(ExhibitBean.class);
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
		}finally{
			if(db!=null){
				db.close();
			}
		}
		adapter=new SearchNamesAdapter(exhibitList, SearchActivity.this);
		mListView.setAdapter(adapter);

		mListView.setTextFilterEnabled(true);
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (TextUtils.isEmpty(newText)) {
					// Clear the text filter.
					mListView.clearTextFilter();
				} else {
					// Sets the initial value for the text filter.
					DbUtils db= DbUtils.create(getApplicationContext());
					try {
						List<ExhibitBean> exList=db.findAll(Selector.from(ExhibitBean.class).where("name", "like","%"+ newText+"%"));
						exhibitList=exList;
						adapter.updateData(exhibitList);
					} catch (DbException e) {
						ExceptionUtil.handleException(e);
					}finally{
						if(db!=null){
							db.close();
						}
					}
				}
				return false;
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent =new Intent(SearchActivity.this,DescribeActivity.class);
				intent.putExtra(Const.INTENT_EXHIBIT_ID, adapter.getItem(position).getId());
				startActivity(intent);
				finish();
			}
		});
		

	}
}
