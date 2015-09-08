package com.systek.guide.biz;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.systek.guide.activity.CityActivity;
import com.systek.guide.adapter.CityAdapter;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.HttpUtils;
import com.systek.guide.entity.CityModule;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

public class CityBiz extends  AsyncTask<String, Integer, List<CityModule>>{

	Context context;

	public CityBiz(CityActivity context) {
		this.context=context;
	}

	@Override
	protected List<CityModule> doInBackground(String... params) {
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
			List<CityModule> cities=parseJSON(ary);
			return cities;
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(List<CityModule> result) {
		((CityActivity) context).updateListView(result);
	}
	
	private List<CityModule> parseJSON(JSONArray ary) throws JSONException {
		List<CityModule> list=new ArrayList<CityModule>();
		for(int i=0; i<ary.length(); i++){
			JSONObject obj=ary.getJSONObject(i);
			CityModule c=new CityModule();
			c.setAlpha(obj.getString("alpha"));
			c.setName(obj.getString("name"));
			list.add(c);
		}
		return list;
	}
}

