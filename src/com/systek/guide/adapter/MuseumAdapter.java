package com.systek.guide.adapter;

import java.io.File;
import java.util.List;

import com.systek.guide.R;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ImageLoaderUtil;
import com.systek.guide.entity.MuseumBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MuseumAdapter extends BaseAdapter{
	
	private List<MuseumBean> museumList;
	private Context context;
	private LayoutInflater inflater;
	
	public MuseumAdapter(List<MuseumBean> museumList, Context context) {
		super();
		this.museumList = museumList;
		this.context = context;
		inflater=LayoutInflater.from(context);
	}
	
	public void updateData( List<MuseumBean> museumList){
		this.museumList = museumList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return museumList.size();
	}

	@Override
	public Object getItem(int position) {
		return museumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate( R.layout.item_museum, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.item_museum_tv_name);
			viewHolder.address = (TextView) convertView.findViewById(R.id.item_museum_tv_address);
			viewHolder.opentime = (TextView) convertView.findViewById(R.id.item_museum_tv_time);
			viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.item_museum_iv_icon);
			viewHolder.isMuseumOpen=(Button)convertView.findViewById(R.id.item_museum_btn_isopen);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.isMuseumOpen.setVisibility(View.INVISIBLE);
		// 取数据
		MuseumBean museumModel = museumList.get(position);

		viewHolder.name.setText(museumModel.getName());
		viewHolder.address.setText(museumModel.getAddress());
		viewHolder.opentime.setText(museumModel.getOpentime());

		// 显示图片
		String imageUrl = museumModel.getIconUrl();
		//每个博物馆的资源以ID为目录
		String museumId = museumModel.getId();
		// 判断sdcard上有没有图片
		String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
		File file = new File(Const.LOCAL_ASSETS_PATH+museumId + "/" +Const.LOCAL_FILE_TYPE_IMAGE, imageName);
		if (file.exists()) {
			// 显示sdcard
			String filePathName =Const.LOCAL_ASSETS_PATH+museumId + "/" +Const.LOCAL_FILE_TYPE_IMAGE+"/"+ imageName;
			ImageLoaderUtil.displaySdcardImage(context, filePathName,viewHolder.ivImage);
		} else {
			// 服务器上存的imageUrl有域名如http://www.systek.com.cn/1.png
			imageUrl = Const.BASEURL+ imageUrl;
			ImageLoaderUtil.displayNetworkImage(context, imageUrl, viewHolder.ivImage);
		}
		return convertView;
	}

	class ViewHolder {
		TextView name, address, opentime;
		ImageView ivImage;
		Button isMuseumOpen;
	}
	
}
