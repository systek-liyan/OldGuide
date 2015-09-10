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
import com.systek.guide.common.utils.HttpUtils;
import com.systek.guide.db.CityDao;
import com.systek.guide.db.CityDbHelper;
import com.systek.guide.entity.CityModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class CityBiz extends  AsyncTask<String, Integer, List<CityModel>> implements InterfaceBiz{

	Context context;

	CityBiz(Context context) {
		this.context=context;
	}

	@Override
	protected List<CityModel> doInBackground(String... params) {
		String url=params[0];
		try {
			HttpEntity entity=HttpUtils.get(HttpUtils.GET, url, null);
			String json=EntityUtils.toString(entity);
			//解析json  获取List<City>
			//json :  {result:ok, data:[{},{},{}]}
			//JSONObject obj=new JSONObject(json);
			/*TODO 此处应判断状态码，后续*/
			//String result=obj.getString("result");
			/*if("ok".equals(result)){
				JSONArray ary=obj.getJSONArray("data");
				List<CityModule> musics=parseJSON(ary);
				return musics;
			}*/
			JSONArray ary=new JSONArray(json);
			List<CityModel> cities=parseJSON(ary);
			return cities;
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<CityModel> result) {
		CityDbHelper mhelper=null;
		SQLiteDatabase db=null;
		CityDao cityDao=null;
		((CityActivity) context).updateListView(result);
		try {	
			mhelper = new CityDbHelper(context);
			db=mhelper.getWritableDatabase();
			cityDao=new CityDao(mhelper);
			while (result == null) {}
			if (!cityDao.tabIsExist(CityDbHelper.DBTAblENAME)) {
				mhelper.onCreate(db);
			}
			for (CityModel city : result) {
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

	private List<CityModel> parseJSON(JSONArray ary) throws JSONException {
		List<CityModel> list=new ArrayList<CityModel>();
		for(int i=0; i<ary.length(); i++){
			JSONObject obj=ary.getJSONObject(i);
			CityModel c=new CityModel();
			c.setAlpha(obj.getString("alpha"));
			c.setName(obj.getString("name"));
			list.add(c);
		}
		return list;
	}
}

