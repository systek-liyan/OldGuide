package com.systek.guide.fragment;

import com.systek.guide.R;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class DownloadFragment extends Fragment {

	/**
	 * 正在更新的listview
	 */
	private ListView lvUpdating;

	/**
	 * 已经下载完成的listview
	 */
	private ListView lvDownloadComplete;

	/**
	 * 正在下载标题栏 tv
	 */
	private TextView tvUpdating;

	/**
	 * 下载完成标题栏tv
	 */
	private TextView tvDownloaded;

	/**
	 * 当没有正在更新列表 也没有下载完成列表时显示
	 */
	private TextView tvNoItems;
	
	Activity activity;

	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;


	private OnFragmentInteractionListener mListener;

	public static DownloadFragment newInstance() {
		DownloadFragment fragment = new DownloadFragment();
		return fragment;
	}

	private DownloadFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_download, container, false);
		lvUpdating = (ListView) view.findViewById(R.id.lv_download_ing);
		lvDownloadComplete = (ListView) view.findViewById(R.id.lv_download_complete);
		tvUpdating = (TextView) view.findViewById(R.id.tv_download_ing);
		tvDownloaded = (TextView) view.findViewById(R.id.tv_download_complete);
		tvNoItems = (TextView) view.findViewById(R.id.tv_download_no_items);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onAttach(Activity activity) {
		this.activity=activity;
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
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
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

}
