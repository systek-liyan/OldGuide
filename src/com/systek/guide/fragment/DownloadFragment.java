package com.systek.guide.fragment;

import java.util.ArrayList;

import com.systek.guide.R;
import com.systek.guide.adapter.DownloadExpandableAdapter;
import com.systek.guide.adapter.DownloadExpandableAdapter.CallbackforViewHolder;
import com.systek.guide.adapter.DownloadExpandableAdapter.ChildViewHolder;
import com.systek.guide.biz.BizFactory;
import com.systek.guide.biz.DownloadBiz;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.entity.DownloadTargetModels;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class DownloadFragment extends Fragment {

	private Activity activity;
	private ExpandableListView mListView;
	private ArrayList<DownloadTargetModels> list;
	private DownloadExpandableAdapter mAdapter;
	private ChildViewHolder childViewHolder;
	private DownloadBiz downloadBiz;
	private DownloadBrocastReceiver receiver;
	/* 用于联网返回可下载数据的json */
	private String menuJson;

	/** 回调获得当前点击下载的viewholder **/
	private CallbackforViewHolder progressListener = new CallbackforViewHolder() {

		@Override
		public void getViewHolder(ChildViewHolder holder) {
			childViewHolder = holder;
			holder.progressBar.setVisibility(View.VISIBLE);
			holder.progressBar.setMax(100);
			holder.tvStateRecord.setVisibility(View.VISIBLE);
		}

		@Override
		public void onUpdate() {

		}
	};

	private DownloadFragment() {
	}

	public static DownloadFragment newInstance() {
		DownloadFragment fragment = new DownloadFragment();
		return fragment;
	}

	@Override
	public void onAttach(final Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
		downloadBiz = (DownloadBiz) BizFactory.getDownloadBiz(activity);
		downloadBiz.getJsonForDownloadMenu();
		LogUtil.i("当前状态", "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*注册广播*/
		receiver = new DownloadBrocastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.ACTION_PROGRESS);
		filter.addAction(Const.ACTION_DOWNLOAD_JSON);
		filter.addAction(Const.ACTION_ASSETS_JSON);
		activity.registerReceiver(receiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.i("当前状态", "onCreateView");
		/*获取各种view控件及设置adapter*/
		View view = inflater.inflate(R.layout.fragment_download, container, false);
		mListView = (ExpandableListView) view.findViewById(R.id.lv_download_list);
		mAdapter = new DownloadExpandableAdapter(activity, list);
		mListView.setAdapter(mAdapter);
		mAdapter.setForViewHodler(progressListener);
		return view;
	}

	@Override
	public void onResume() {
		LogUtil.i("当前状态", "onResume");
		super.onResume();
		mAdapter.updateData(list);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activity.unregisterReceiver(receiver);
		LogUtil.i("当前状态", "onDetach");
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		public void onFragmentInteraction(Uri uri);
	}
	
	
	/**用于接收下载中需要的数据的广播接收器*/
	class DownloadBrocastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			/*如果广播是可下载数据的json，则解析，并更新界面数据*/
			if (intent.getAction().equals(Const.ACTION_DOWNLOAD_JSON)) {
				menuJson = intent.getStringExtra(Const.ACTION_DOWNLOAD_JSON);
				try {
					list = (ArrayList<DownloadTargetModels>) downloadBiz.parseJsonForDownloadMenu(menuJson);
					mAdapter.updateData(list);
				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}
				/*如果广播是下载进度，则更新进度条，下载完毕则隐藏相关控件*/
			} else if (intent.getAction().equals(Const.ACTION_PROGRESS)) {
				int progress = intent.getIntExtra(Const.ACTION_PROGRESS, -1);

				if (childViewHolder != null) {
					if (progress == 100) {
						childViewHolder.progressBar.setVisibility(View.INVISIBLE);
						childViewHolder.tvStateRecord.setText(R.string.downloaded_text);
						childViewHolder.ivStart.setVisibility(View.INVISIBLE);
						childViewHolder.tvState.setVisibility(View.INVISIBLE);
					}
					childViewHolder.progressBar.setProgress(progress);
				}
			}
		}

	}

}
