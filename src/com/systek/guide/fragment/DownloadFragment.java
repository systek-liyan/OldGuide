package com.systek.guide.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.systek.guide.R;
import com.systek.guide.adapter.DownloadExpandableAdapter;
import com.systek.guide.adapter.DownloadExpandableAdapter.CallbackforViewHolder;
import com.systek.guide.adapter.DownloadExpandableAdapter.ChildViewHolder;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.common.utils.LogUtil;
import com.systek.guide.entity.DownloadInfoModel;
import com.systek.guide.entity.DownloadTargetModels;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class DownloadFragment extends Fragment{

	Activity activity;

	private ExpandableListView mListView;

	ArrayList<DownloadTargetModels> list;

	private DownloadExpandableAdapter mAdapter;

	ChildViewHolder childViewHolder;

	DownloadBrocastReceiver receiver;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if(msg.what==Const.MSG_WHAT){
				Bundle bundle=msg.getData();
				String json=bundle.getString(Const.DOWNLOAD_KEY);
				JSONArray ary=null;
				try {
					ary = new JSONArray(json);
					list = (ArrayList<DownloadTargetModels>) parseJSON(ary);
				} catch (JSONException e) {
					ExceptionUtil.handleException(e);
				}
			}
			mAdapter.updateData(list);
		}
	};

	/** 开始(下载)回调并接收来自DownloadClient下载博物馆离线资源文件列表的状态信息 **/
	private CallbackforViewHolder progressListener = new CallbackforViewHolder() {

		@Override
		public void getViewHolder(ChildViewHolder holder) {
			childViewHolder=holder;
			holder.progressBar.setVisibility(View.VISIBLE);
			holder.progressBar.setMax(100);
			holder.tvStateRecord.setVisibility(View.VISIBLE);
		}

		@Override
		public void onUpdate() {

		}
	};


	public static DownloadFragment newInstance() {
		DownloadFragment fragment = new DownloadFragment();
		return fragment;
	}

	private DownloadFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiver= new DownloadBrocastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.ACTION_PROGRESS);
		getActivity().registerReceiver(receiver, filter);
	}

	private void initData() {
		LogUtil.i("当前状态", "initData");
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET,Const.DOWNLOAD_CITY_LIST,
				new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// testTextView.setText(current + "/" + total);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtil.i("数据获取成功------", responseInfo.result);
				try {
					Message msg= Message.obtain();
					Bundle bundle= new Bundle();
					bundle.putString(Const.DOWNLOAD_KEY, responseInfo.result);
					msg.what=Const.MSG_WHAT;
					msg.setData(bundle);
					handler.sendMessage(msg);

				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.i(error.toString(), msg);
			}
		});
	}

	protected List<DownloadTargetModels> parseJSON(JSONArray ary) throws JSONException {
		List<DownloadTargetModels> list = new ArrayList<DownloadTargetModels>();
		for (int i = 0; i < ary.length(); i++) {
			JSONObject obj = ary.getJSONObject(i);
			DownloadTargetModels models = new DownloadTargetModels();
			models.setCity(obj.getString("city"));
			JSONArray array = new JSONArray(obj.getString("museumList"));
			ArrayList<DownloadInfoModel> modelList = new ArrayList<DownloadInfoModel>();
			for (int j = 0; j < array.length(); j++) {
				JSONObject obj1 = array.getJSONObject(j);
				DownloadInfoModel model = new DownloadInfoModel();
				model.setMuseumId(obj1.getString("museumId"));
				model.setName(obj1.getString("name"));
				model.setTotal(obj1.getInt("size"));
				modelList.add(model);
			}

			models.setList(modelList);
			list.add(models);
		}
		return list;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.i("当前状态", "onCreateView");
		View view = initView(inflater, container);
		//addListener();

		return view;
	}

	private View initView(LayoutInflater inflater, ViewGroup container) {
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
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
		initData();
		LogUtil.i("当前状态", "onAttach");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().unregisterReceiver(receiver);
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

	class DownloadBrocastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			int progress=intent.getIntExtra(Const.ACTION_PROGRESS, -1);
			if(childViewHolder!=null){
				if(progress==100){
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
