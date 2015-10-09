package com.systek.guide.fragment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.systek.guide.R;
import com.systek.guide.activity.DescribeActivity;
import com.systek.guide.activity.SubjectSelectActivity;
import com.systek.guide.adapter.ExhibitAdapter;
import com.systek.guide.common.config.Const;
import com.systek.guide.common.utils.ExceptionUtil;
import com.systek.guide.entity.ExhibitBean;
import com.systek.guide.entity.MuseumBean;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;


public class MuseumIntroduceFragment extends Fragment{

	Activity activity;

	private static final String MUSEUMID = "museumId";

	private String museumId;

	private OnFragmentInteractionListener mListener;

	private ListView boutiqueListView;

	private MuseumBean museum;

	private ExhibitAdapter exhibitAdapter;

	private List<ExhibitBean> boutiqueList;

	private Button specialButton;

	public MuseumIntroduceFragment() {
	}

	public static MuseumIntroduceFragment newInstance(String museumId) {
		MuseumIntroduceFragment fragment = new MuseumIntroduceFragment();
		Bundle bundle = new Bundle();
		bundle.putString(MUSEUMID, museumId);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity=activity;
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			museumId = getArguments().getString(MUSEUMID);
		}
		DbUtils db = DbUtils.create(activity);
		try {
			museum=db.findById(MuseumBean.class, museumId);
			boutiqueList=db.findAll(Selector.from(ExhibitBean.class).where("museumId" ,"like","%"+ museumId+"%"));
		} catch (DbException e) {
			ExceptionUtil.handleException(e);
		}finally{
			if(db!=null){
				db.close();
			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_museum_introduce, container, false);
		initView(view);
		return view;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mp.isPlaying()){
			mp.stop();			
		}
	}
	
	private void initView(View view) {
		if(museum!=null){
			try {
				boutiqueListView=(ListView)view.findViewById(R.id.frag_museum_introduce_listview);
				exhibitAdapter=new ExhibitAdapter(activity, boutiqueList);
				boutiqueListView.setAdapter(exhibitAdapter);
				specialButton=(Button)view.findViewById(R.id.special_Button);
				displayAudio();
				addListener();
			} catch (Exception e) {
				ExceptionUtil.handleException(e);
			}
		}
	}

	private void addListener() {
		boutiqueListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position==0){
				}else{
					ExhibitBean exhibit=(ExhibitBean)exhibitAdapter.getItem(position);
					String exhibitId=exhibit.getId();
					Intent intent = new Intent(getActivity(),DescribeActivity.class);
					intent.putExtra(Const.INTENT_EXHIBIT_ID, exhibitId);
					getActivity().startActivity(intent);
				}
			}
		});
		
		specialButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
				Intent intent =new Intent (getActivity(),SubjectSelectActivity.class);
				intent.putExtra(Const.INTENT_MUSEUM_ID, museumId);
				startActivity(intent);
			}
		});
		
	}

	private MediaPlayer mp;
	private void displayAudio(){

		new Thread(){

			public void run() {
				mp = new MediaPlayer();

				String totalIntroduceAudio=museum.getAudioUrl();
				String audioName=totalIntroduceAudio.substring(totalIntroduceAudio.lastIndexOf("/")+1);
				String audioUrl = Const.LOCAL_ASSETS_PATH+museumId+"/"+Const.LOCAL_FILE_TYPE_AUDIO+"/"+audioName;
				// 判断sdcard上有没有图片
				File file = new File(Const.LOCAL_ASSETS_PATH+museumId + "/" +Const.LOCAL_FILE_TYPE_AUDIO, audioName);
				if (file.exists()) {
					// sdcard显示
					String filePathName =Const.LOCAL_ASSETS_PATH+museumId + "/" +Const.LOCAL_FILE_TYPE_AUDIO+"/"+ audioName;
					try {
						mp.setDataSource(filePathName);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					//网络获取显示
					audioUrl = Const.BASEURL+ totalIntroduceAudio;
					try {
						mp.setDataSource (audioUrl);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					mp.prepare();
					mp.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();


	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		public void onFragmentInteraction(String arg);
	}


}
