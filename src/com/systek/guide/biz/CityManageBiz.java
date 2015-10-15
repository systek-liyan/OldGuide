package com.systek.guide.biz;

import java.util.List;

import com.systek.guide.biz.impl.GetBeansFromLocal;
import com.systek.guide.biz.impl.GetBeansFromNet;
import com.systek.guide.common.MyApplication;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.DbUtil;
import com.systek.guide.entity.CityBean;

import android.content.Context;
import android.widget.Toast;

public class CityManageBiz implements IBiz{

	Context context;
	IGetBeanBiz iGetBeanBiz;
	
	CityManageBiz(Context context) {
		this.context = context;
	}
	public void setiGetBeanBiz(IGetBeanBiz iGetBeanBiz) {
		this.iGetBeanBiz = iGetBeanBiz;
	}
	
	public List<CityBean> getCities(){
		
		List<CityBean> list =null;
		if (MyApplication.currentNetworkType==Const.INTERNET_TYPE_NONE){
			Toast.makeText(context, "当前无网络", Toast.LENGTH_SHORT).show();
		}else{
			if(DbUtil.isTableExist(context, CityBean.class)){
				setiGetBeanBiz(new GetBeansFromLocal());
			}else{
				setiGetBeanBiz(new GetBeansFromNet());
			}
		//	list=iGetBeanBiz.getBeans(context, CityBean.class);
		}
		return list;
	}

	/*@Override
	protected List<CityBean> doInBackground(String... params) {
		String url = params[0];
		try {
			HttpEntity entity = MyHttpUtils.get(MyHttpUtils.GET, url, null);
			String json = EntityUtils.toString(entity);
			// 解析json 获取List<City>
			// json : {result:ok, data:[{},{},{}]}
			// JSONObject obj=new JSONObject(json);
			 TODO 此处应判断状态码，后续 
			// String result=obj.getString("result");
			
			 * if("ok".equals(result)){ JSONArray ary=obj.getJSONArray("data");
			 * List<CityModule> musics=parseJSON(ary); return musics; }
			 
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
	}*/
}
