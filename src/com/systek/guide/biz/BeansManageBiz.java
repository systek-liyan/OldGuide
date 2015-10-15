package com.systek.guide.biz;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.biz.impl.GetBeansFromLocal;
import com.systek.guide.biz.impl.GetBeansFromNet;
import com.systek.guide.common.MyApplication;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.DbUtil;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.entity.BeaconBean;
import com.systek.guide.entity.CityBean;
import com.systek.guide.entity.DownloadAreaBeans;
import com.systek.guide.entity.DownloadInfoBean;
import com.systek.guide.entity.ExhibitBean;
import com.systek.guide.entity.LabelBean;
import com.systek.guide.entity.LyricBean;
import com.systek.guide.entity.MapBean;
import com.systek.guide.entity.MuseumBean;

import android.content.Context;

public class BeansManageBiz implements IBiz {

	Context context;
	IGetBeanBiz iGetBeanBiz;

	BeansManageBiz(Context context) {
		this.context = context;
	}

	public void setiGetBeanBiz(IGetBeanBiz iGetBeanBiz) {
		this.iGetBeanBiz = iGetBeanBiz;
	}

	public <T> List<T> getAllBeans(Class<T> clazz,String condition) {

		List<T> list = null;
		String url=null;
		if (DbUtil.isTableExist(context, clazz)) {
			setiGetBeanBiz(new GetBeansFromLocal());
		} else {
			if (MyApplication.currentNetworkType != Const.INTERNET_TYPE_NONE) {
				url=checkTypeforUrl(clazz);
				setiGetBeanBiz(new GetBeansFromNet());
			}else{
				list=new ArrayList<T>();
				return list;
			}
		}
		list = iGetBeanBiz.getBeans(context, clazz,url+condition);
		if(iGetBeanBiz instanceof GetBeansFromNet){
			boolean isSaveSucess=saveAllBeans(list);
			LogUtil.i("测试信息", "数据保存"+isSaveSucess);
		}
		return (List<T>) list;
	}


	public <T> List<T> getBeans(Class<T> entry, String id,String condition) {
		List<T> list = null;
		if (DbUtil.isTableExist(context, entry)) {
			setiGetBeanBiz(new GetBeansFromLocal());
		} else {
			setiGetBeanBiz(new GetBeansFromNet());
		}
		list = iGetBeanBiz.getBeans(context, entry,id);
		if (iGetBeanBiz instanceof GetBeansFromNet) {
			boolean isSaveSucess = saveAllBeans(list);
			LogUtil.i("测试信息", "数据保存" + isSaveSucess);
		}
		return (List<T>) list;
	}


	public <T> boolean saveAllBeans(List<?> list) {
		if (list == null || list.size() == 0) {
			return false;
		}
		DbUtils db = DbUtils.create(context);
		try {
			db.saveAll(list);
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return true;
	}

	private <T>String checkTypeforUrl(Class<T> clazz) {
		String name = clazz.getCanonicalName();
		String url=null;
		Class<?> beacon = BeaconBean.class;
		Class<?> city = CityBean.class;
		Class<?> downloadArea = DownloadAreaBeans.class;
		Class<?> downloadInfo = DownloadInfoBean.class;
		Class<?> exhibit = ExhibitBean.class;
		Class<?> label = LabelBean.class;
		Class<?> lyric = LyricBean.class;
		Class<?> map = MapBean.class;
		Class<?> museum = MuseumBean.class;
		String beaconName = beacon.getName();
		String cityName = city.getName();
		String downloadAreaName = downloadArea.getName();
		String downloadInfoName = downloadInfo.getName();
		String exhibitName = exhibit.getName();
		String labelName = label.getName();
		String lyricName = lyric.getName();
		String mapName = map.getName();
		String museumName = museum.getName();
		LogUtil.i("测试信息", name);
		if (name.equals(exhibitName)) {
			url= Const.EXHIBIT_URL;
		} else if (name.equals(beaconName)) {

		} else if (name.equals(cityName)) {
			url= Const.CITYLISTURL;
		} else if (name.equals(downloadAreaName)) {

		} else if (name.equals(downloadInfoName)) {

		} else if (name.equals(labelName)) {

		} else if (name.equals(lyricName)) {

		} else if (name.equals(mapName)) {

		} else if (name.equals(museumName)) {
			url=Const.MUSEUMS_URL;
		}
		return url;
	}
}
