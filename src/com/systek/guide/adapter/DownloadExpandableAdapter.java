package com.systek.guide.adapter;

import java.util.ArrayList;

import com.systek.guide.R;
import com.systek.guide.biz.BizFactory;
import com.systek.guide.biz.DownloadBiz;
import com.systek.guide.common.config.Const;
import com.systek.guide.entity.DownloadInfoModel;
import com.systek.guide.entity.DownloadTargetModels;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadExpandableAdapter extends BaseExpandableListAdapter {

	Context context;
	ArrayList<DownloadTargetModels> listGroup;
	
	/** 记录下载状态，用于ChildViewHolder的tvStateRecord 表示正在下载状态 */
	private String DOWNLOADING = "DOWNLOADING";
	/** 记录下载状态，用于ChildViewHolder的tvStateRecord 表示空、失败状态*/
	private String NONE = "NONE";  
	/** 记录下载状态，用于ChildViewHolder的tvStateRecord 表示暂停状态*/
	private String PAUSE = "PAUSE";
	
	/**用于回调选中item的childviewhodler的接口*/
	public interface CallbackforViewHolder {
		/**
		 *  接收DownloadClient回调信息
		 * @param holder 对应可扩展ListView子层显示的该博物馆界面元素
		 */
		void getViewHolder(ChildViewHolder holder);

		// TODO 更新
		void onUpdate();
	}
	/** 执行下载回调  */
	private CallbackforViewHolder downloadListener;
	
	
	/** 设置执行下载回调  */
	public void setForViewHodler(CallbackforViewHolder downloadListener) {
		this.downloadListener = downloadListener;
	}
	
	
	public DownloadExpandableAdapter(Context context, ArrayList<DownloadTargetModels> list) {
		super();
		this.context = context;
		if (listGroup != null) {
			this.listGroup = list;
		} else {
			this.listGroup = new ArrayList<DownloadTargetModels>();
		}
	}
	
	/**更新数据*/
	public void updateData(ArrayList<DownloadTargetModels> list) {
		if (list != null) {
			this.listGroup = list;
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getGroupCount() {

		return listGroup.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		DownloadTargetModels models = listGroup.get(groupPosition);
		return models.getInfoCount();
	}

	@Override
	public Object getGroup(int groupPosition) {

		return listGroup.get(groupPosition);
	}

	@Override
	public DownloadInfoModel getChild(int groupPosition, int childPosition) {

		DownloadTargetModels models = listGroup.get(groupPosition);
		
		ArrayList<DownloadInfoModel> listModel = new ArrayList<DownloadInfoModel>(models.getList());
		DownloadInfoModel model = listModel.get(childPosition);
		return model;
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		/* 是否有固定ID */
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		GroupViewHolder groupViewHodler = null;
		/** 外层--城市 */
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_download_group, null);
			groupViewHodler = new GroupViewHolder();
			groupViewHodler.tvName = (TextView) convertView.findViewById(R.id.tv_download_group_name);
			groupViewHodler.ivIndicator = (ImageView) convertView.findViewById(R.id.iv_download_group_icon);

			convertView.setTag(groupViewHodler);
		} else {
			groupViewHodler = (GroupViewHolder) convertView.getTag();
		}
		/** 外层显示城市名 */
		DownloadTargetModels model = (DownloadTargetModels) this.getGroup(groupPosition);
		String name =model.getCity();
		groupViewHodler.tvName.setText(name);
		if (isExpanded) {
			groupViewHodler.ivIndicator.setImageResource(R.drawable.arrow_press);
		} else {
			groupViewHodler.ivIndicator.setImageResource(R.drawable.arrow);
		}
		return convertView;
	}

	@Override
	public View getChildView( final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		final ChildViewHolder childViewHodler;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_download_child, null);
			childViewHodler = new ChildViewHolder();
			/** 博物馆名称 */
			childViewHodler.tvName = (TextView) convertView.findViewById(R.id.tv_download_child_name);
			/** 下载资源文件大小 */
			childViewHodler.tvSize = (TextView) convertView.findViewById(R.id.tv_download_child_size);
			/** 开始(下载) **/
			childViewHodler.ivStart = (ImageView) convertView.findViewById(R.id.iv_download_child_icon);
			/** 状态用于显示进度等 **/
			childViewHodler.tvState = (TextView) convertView.findViewById(R.id.tv_download_child_state);
			/** 记录状态 **/
			childViewHodler.tvStateRecord = (TextView) convertView.findViewById(R.id.tv_download_child_state_record);
			//childViewHodler.tvStateRecord.setText(NONE);
			/** 进度条 **/
			childViewHodler.progressBar = (ProgressBar) convertView.findViewById(R.id.pb_downloading);
			convertView.setTag(childViewHodler);
		} else {
			childViewHodler = (ChildViewHolder) convertView.getTag();
		}

		// 得好友数据
		DownloadInfoModel info = (DownloadInfoModel) this.getChild(groupPosition,childPosition);
		String name =info.getName();
		// 博物馆名称
		childViewHodler.tvName.setText(name);
		// 资源文件大小
		double size =info.getTotal()/ 1024F / 1024F;
		childViewHodler.tvSize.setText(String.format("%.2fM", size));
		childViewHodler.tvStateRecord.setText(NONE);

		childViewHodler.ivStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/** 获取当前选中的子层博物馆对应的DownloadInfoModel对象*/
				DownloadInfoModel info=getChild(groupPosition, childPosition);
				final String id = info.getMuseumId();

				// 如果当前是空,显示pause图案，置状态为正在下载，表示按下执行下载操作
				if (childViewHodler.tvStateRecord.getText().equals(NONE)) {
					((ImageView) v).setImageResource(R.drawable.play_btn_pause);
					childViewHodler.tvState.setText("");
					childViewHodler.tvStateRecord.setText(DOWNLOADING);
					
					new Thread(){
						public void run() {
							/*创建下载业务对象并开始下载*/
							DownloadBiz downloadBiz= (DownloadBiz) BizFactory.getDownloadBiz(context);
							String assetsJson= downloadBiz.getAssetsJSON(id);
							downloadBiz.downloadAssets(id,assetsJson);
						};
					}.start();
					
				}
				// 如果当前是暂停状态，显示play图案，置状态为正在下载，表示按下执行下载操作
				else if (childViewHodler.tvStateRecord.getText().equals(PAUSE)) {
					((ImageView) v).setImageResource(R.drawable.play_btn_play);
					childViewHodler.tvState.setText("");
					childViewHodler.tvStateRecord.setText("");
					childViewHodler.tvStateRecord.setText(DOWNLOADING);
					/*发送广播请求继续下载*/
					Intent intent= new Intent();
					intent.setAction(Const.ACTION_CONTINUE);
					intent.putExtra(Const.ACTION_CONTINUE,Const.ACTION_CONTINUE);
					context.sendBroadcast(intent);
				}
				// 如果当前是下载状态，显示pause图案，置状态为暂停，表示按下执行暂停操作
				else if (childViewHodler.tvStateRecord.getText().equals(DOWNLOADING)) {
					((ImageView) v).setImageResource(R.drawable.play_btn_pause);					
					childViewHodler.tvStateRecord.setText("");
					childViewHodler.tvStateRecord.setText(PAUSE);
					//String str = childViewHodler.tvState.getText().toString();
					//childViewHodler.tvState.setText("暂停状态");
					/*发送广播暂停下载*/
					Intent intent= new Intent();
					intent.setAction(Const.ACTION_PAUSE);
					context.sendBroadcast(intent);
				}
				downloadListener.getViewHolder(childViewHodler);	
			}
		});
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return true;
	}

	/** 外层 */
	class GroupViewHolder {

		/** 城市 */
		TextView tvName;
		/** 展开 */
		ImageView ivIndicator;
	}

	/** 子层 */
	public class ChildViewHolder {
		/** 博物馆 */
		public TextView tvName;
		/** 资源文件大小 */
		public TextView tvSize;
		/** 状态 用于显示进度等 */
		public TextView tvState;
		/** 记录目前的下载状态,定义的String 常量：PAUSE，DOWNLOADING,NONE等 */
		public 	TextView tvStateRecord;
		/** 开始 */
		public 	ImageView ivStart;
		/** 进度条 */
		public ProgressBar progressBar;
	}
}

