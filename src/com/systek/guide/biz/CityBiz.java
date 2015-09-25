package com.systek.guide.biz;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.systek.guide.activity.CityActivity;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.MyHttpUtils;
import com.systek.guide.db.Dao;
import com.systek.guide.db.DbHelper;
import com.systek.guide.entity.CityBean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class CityBiz extends AsyncTask<String, Integer, List<CityBean>>implements InterfaceCityBiz {

	Context context;

	CityBiz(Context context) {
		this.context = context;
	}

	@Override
	protected List<CityBean> doInBackground(String... params) {
		String url = params[0];
		try {
			HttpEntity entity = MyHttpUtils.get(MyHttpUtils.GET, url, null);
			String json = EntityUtils.toString(entity);
			// 解析json 获取List<City>
			// json : {result:ok, data:[{},{},{}]}
			// JSONObject obj=new JSONObject(json);
			/* TODO 此处应判断状态码，后续 */
			// String result=obj.getString("result");
			/*
			 * if("ok".equals(result)){ JSONArray ary=obj.getJSONArray("data");
			 * List<CityModule> musics=parseJSON(ary); return musics; }
			 */
			JSONArray ary = new JSONArray(json);
			List<CityBean> cities = parseJSON(ary);
			return cities;
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<CityBean> result) {
		DbHelper mhelper = null;
		SQLiteDatabase db = null;
		Dao cityDao = null;
		((CityActivity) context).updateListView(result);
		try {
			mhelper = new DbHelper(context);
			db = mhelper.getWritableDatabase();
			cityDao = new Dao(mhelper);
			while (result == null) {
			}
			if (!mhelper.tabIsExist(DbHelper.DBTAblENAME)) {
				mhelper.onCreate(db);
			}
			for (CityBean city : result) {
				cityDao.insert(city);
			}
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		} finally {
			if (mhelper != null) {
				mhelper.close();
			}
			if (db != null) {
				db.close();
			}
		}
	}

	private List<CityBean> parseJSON(JSONArray ary) throws JSONException {
		List<CityBean> list = new ArrayList<CityBean>();
		for (int i = 0; i < ary.length(); i++) {
			JSONObject obj = ary.getJSONObject(i);
			CityBean c = new CityBean();
			c.setAlpha(obj.getString("alpha"));
			c.setName(obj.getString("name"));
			list.add(c);
		}
		return list;
	}
}
