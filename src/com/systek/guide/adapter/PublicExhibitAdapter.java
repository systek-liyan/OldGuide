package com.systek.guide.adapter;

import java.io.File;
import java.util.List;

import com.systek.guide.R;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ImageLoaderUtil;
import com.systek.guide.entity.ExhibitBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PublicExhibitAdapter extends BaseAdapter{

	private  Context context;
	private List<ExhibitBean> list;
	private LayoutInflater inflater;

	public PublicExhibitAdapter(Context context, List<ExhibitBean> list) {
		super();
		this.context = context;
		this.list = list;
		inflater=LayoutInflater.from(context);
	}
	
	public void setList(List<ExhibitBean> list) {
		this.list = list;
	}

	public void updateData(List<ExhibitBean> exlist){
		setList(exlist);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ExhibitBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (convertView == null||convertView.getTag() == null) {
			convertView = inflater.inflate( R.layout.item_exhibit, null);
			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.item_exhibit_tv_name);
			viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.item_exhibit_tv_address);
			viewHolder.tvIntroduce = (TextView) convertView.findViewById(R.id.item_exhibit_tv_introduction);
			viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.item_exhibit_iv_icon);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 取数据
		ExhibitBean exhibitBean = list.get(position);

		viewHolder.tvName.setText(exhibitBean.getName());
		viewHolder.tvAddress.setText(exhibitBean.getAddress());
		viewHolder.tvIntroduce.setText(exhibitBean.getIntroduce());

		// 显示图片
		String iconUrl = exhibitBean.getIconurl();
		//每个博物馆的资源以ID为目录
		String museumId = exhibitBean.getMuseumId();

		String iconName = iconUrl.substring(iconUrl.lastIndexOf("/") + 1);
		File file = new File(Const.LOCAL_ASSETS_PATH+museumId + "/" +Const.LOCAL_FILE_TYPE_IMAGE, iconName);
		// 判断sdcard上有没有图片
		if (file.exists()) {
			// 显示sdcard
			String filePathName =Const.LOCAL_ASSETS_PATH+museumId + "/" +Const.LOCAL_FILE_TYPE_IMAGE+"/"+ iconName;
			ImageLoaderUtil.displaySdcardImage(context, filePathName,viewHolder.ivIcon);
		} else {
			// 服务器上存的imageUrl有域名如http://www.systek.com.cn/1.png
			iconUrl = Const.BASEURL+ iconUrl;
			ImageLoaderUtil.displayNetworkImage(context, iconUrl, viewHolder.ivIcon);
		}
		return convertView;
	}

	class ViewHolder{
		TextView tvName,tvAddress,tvIntroduce;
		ImageView ivIcon;
	}

}
