package com.systek.guide.adapter;

import java.util.List;

import com.systek.guide.R;
import com.systek.guide.entity.ExhibitBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchNamesAdapter extends BaseAdapter{
	
	private List<ExhibitBean> list;
	private Context context;
	private LayoutInflater inflater;
	
	
	public SearchNamesAdapter(List<ExhibitBean> list, Context c) {
		super();
		this.list = list;
		this.context = c;
		inflater=LayoutInflater.from(context);
	}
	
	public  void updateData(List<ExhibitBean> list){
		this.list=list;
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
		
		ViewHolder viewHolder=null;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.item_search_activity_name, null);
			viewHolder=new ViewHolder();
			viewHolder.nameTv=(TextView)convertView.findViewById(R.id.item_search_name);
			viewHolder.addressTv=(TextView)convertView.findViewById(R.id.item_search_address);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		viewHolder.nameTv.setText(list.get(position).getName());
		viewHolder.addressTv.setText(list.get(position).getLabels());
		return convertView;
	}
	
	class ViewHolder {
		TextView nameTv,addressTv;
	}
	
}
