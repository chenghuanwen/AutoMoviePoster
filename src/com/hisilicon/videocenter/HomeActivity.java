package com.hisilicon.videocenter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
//import android.app.DownloadManager.Request;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.explorer.common.FileUtil;
import com.explorer.common.SocketClient;
import com.hisilicon.videocenter.R.style;
import com.hisilicon.videocenter.auto.ExplorerSelectActivity;
import com.hisilicon.videocenter.auto.FolderBean;
import com.hisilicon.videocenter.auto.GetMountInfo;
import com.hisilicon.videocenter.auto.GetSataPathUtil;
import com.hisilicon.videocenter.auto.ImageEncryptionUtil;
import com.hisilicon.videocenter.auto.OnQueryLocalDataFinishedListener;
import com.hisilicon.videocenter.auto.OnScanFinishedListener;
import com.hisilicon.videocenter.auto.OnUnLockImageFinishedListener;
import com.hisilicon.videocenter.auto.SharedPreferenceUtil;
import com.hisilicon.videocenter.auto.StorageInfo;
import com.hisilicon.videocenter.auto.onDownloadFinishedListener;
import com.hisilicon.videocenter.auto.db.BDInfo;
import com.hisilicon.videocenter.auto.db.DatabaseManager;
import com.hisilicon.videocenter.auto.db.MovieDaoImpl;
import com.hisilicon.videocenter.controller.DataManager;
import com.hisilicon.videocenter.controller.HomeController;
import com.hisilicon.videocenter.controller.NfsController;
import com.hisilicon.videocenter.controller.SambaController;
import com.hisilicon.videocenter.event.EventDBChange;
import com.hisilicon.videocenter.event.EventShowMovie;
import com.hisilicon.videocenter.util.Common;
import com.hisilicon.videocenter.util.CommonConstant;
import com.hisilicon.videocenter.util.CommonConstant.Local;
import com.hisilicon.videocenter.util.CommonConstant.Nfs;
import com.hisilicon.videocenter.util.CommonConstant.Samba;
import com.hisilicon.videocenter.util.HiSettingsManager;
import com.hisilicon.videocenter.util.LogUtil;
import com.hisilicon.videocenter.util.Movie;
import com.hisilicon.videocenter.util.MovieBaseInfo;
import com.hisilicon.videocenter.util.MsgConstant;
import com.hisilicon.videocenter.view.CommonDialog;
import com.hisilicon.videocenter.view.EditeTextDialog;
import com.hisilicon.videocenter.view.GridAdapter;
import com.hisilicon.videocenter.view.ListAdapter;
import com.hisilicon.videocenter.view.MovieFolderAdapter;
import com.hisilicon.videocenter.view.MyGridView;
import com.hisilicon.videocenter.view.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

public class HomeActivity extends Activity implements OnClickListener,
		Handler.Callback {
	private static final String TAG = "HomeActivity";

	private static final int REQUEST_CODE = 100;
	private static final int RESULT_CODE = 101;

	private View mGridLayout;

	private MyListView listView;
	private ListAdapter listAdapter;

	private ListView mFolderList;
	private MovieFolderAdapter mFolderAdapter;

	private MyGridView gridView;
	private GridAdapter gridAdapter;

	private int curListPosition = -1;

	private TextView textPage;
	private TextView mCurrentType;
	private TextView mSearchText;

	private List<String> mTypeNames = null;
	private List<Movie> mAllMovies = null;
	private Map<String, List<Movie>> typeMovie;
	private List<Movie> curMovies;

	private ProgressDialog dialog;

	private Toast toast;
	private ImageView btnBack;
	private ImageView enterFileManager;
	private ImageView mBtnSearchDevice;
	private TextView tvLocalSelect;

	private SharedPreferences mSettingShareP;

	private DataManager mDataManager;

	private String selectPath = "";
	private String newNFO;
	private String newPoster;
	private String unlockImage;
	private String localStorePath = "";	
	private String downloadBase = "http://www2.jiashilian.com/JSLMovie/";
	private static final String MEDIA_CONFIG_FILE_NAME = "justlink_media_index.txt";
	private ArrayList<MovieBaseInfo> baseInfos = new ArrayList<>();
	private ArrayList<Movie> autoMovies = new ArrayList<>();
	private MovieDaoImpl movieDaoImpl;
	private int pathNum;
	private SharedPreferenceUtil autoSP;
	private Set<String> savePaths;//已添加过的电影文件夹路径集合
	private int downloadCount ;
	private int oldDownloadCount;
	private DownloadReciver mDownloadReciver;
	private OkHttpClient okHttpClient;
	private FileOutputStream posterOut;
	private FileOutputStream nfoOut;
	private BDInfo mBdInfo;
	private GetSataPathUtil sataUtil = new GetSataPathUtil(this);
	private ImageEncryptionUtil encryptionUtil;
	private UnlockThread unlockThread;
	private int unlockCount;
	
	private Handler mHandler = new Handler(this);
	
	private ConnectivityManager connectivityManager;
	private Dialog netDialog;
	

	@Override
	public boolean handleMessage(android.os.Message msg) {
		switch (msg.what) {
		case MsgConstant.MSG_LOAD_SUCCESS:
			mAllMovies = mDataManager.getAllMovies();
			mTypeNames = mDataManager.getTypesList();

			typeMovie = new HashMap<String, List<Movie>>();
			typeMovie.put(getString(R.string.text_all), mAllMovies);

			curMovies = new ArrayList<Movie>();
			curMovies.addAll(mAllMovies);

			hideLoadingDialog();

			listAdapter.refreshView(mTypeNames);
			listView.requestFocus();

			updateView();
			LogUtil.i("folder","update111111111");
			break;
		case MsgConstant.MSG_UPDATE_MOVIE:
			mAllMovies = mDataManager.getAllMovies();
			mTypeNames = mDataManager.getTypesList();

			if (typeMovie == null) {
				typeMovie = new HashMap<String, List<Movie>>();
			} else {
				typeMovie.clear();
			}
			typeMovie.put(getString(R.string.text_all), mAllMovies);
			listAdapter.refreshView(mTypeNames);

			updateView();
			LogUtil.i("folder","update22222222");
			break;
		case MsgConstant.MSG_SEARCH:
			String searchInfo = (String) msg.obj;
			searchMovie(searchInfo);
			break;

		case MsgConstant.MSG_FOCUS:
			listView.requestFocus();
			getAutoDatabaseMovies();
			break;

		case MsgConstant.MSG_LOAD_FAIL:
			hideLoadingDialog();
			break;
		case MsgConstant.MSG_HIDE_INPUT_METHOD:
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), 0);
			break;
		case MsgConstant.MSG_SHOW_INPUT_METHOD:

			break;
		case MsgConstant.MSG_SHOW_ADD_TEXT:
			String name = (String) msg.obj;
			int num = msg.arg1;
			String showText = null;
			if (num == 0) {
				showText = getString(R.string.movie_folder_add_success, name);
			} else {
				showText = getString(R.string.movie_folder_add_success_num,name, "" + num);
			}

			FileUtil.showToast(HomeActivity.this, showText);
			break;
		case  MsgConstant.MSG_AUTO_SEARCH_MOVIE://自动搜索SAMBA、NFS
			String netPath = (String) msg.obj;
			LogUtil.i("folder","自动搜索网络路径==="+netPath);
			autoSearchMovies(netPath);
			
			int pathNum = autoSP.getNum();
			pathNum++;
			savePaths = autoSP.get("path");
			if(savePaths==null)
				savePaths = new HashSet<>();
				savePaths.add(netPath+";"+pathNum);
				autoSP.put("path",savePaths);//保存已选的网络文件夹路径
			break;
			
		case MsgConstant.AUTO_ADD_MOVIE_FINISH://自动添加海报完成
			LogUtil.i("folder", "刷新111111111111" );
			gridAdapter.refreshView(mAllMovies);
			
			//gridView.smoothScrollToPositionFromTop(mAllMovies.size()-1, 0);
			
			hideLoadingDialog();
			
		String	show = getString(R.string.movie_folder_add_success_num,"", "" + autoMovies.size());
		FileUtil.showToast(HomeActivity.this, show);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		HomeController.getInstance().setActivity(this);
		SambaController.getInstance().setActivity(this);
		NfsController.getInstance().setActivity(this);
		HiSettingsManager.getInstance().initSharedPreferences(this);
		
		mDataManager = DataManager.getInstance();
		EventBus.getDefault().register(this);

		ImageLoaderConfiguration configuration = ImageLoaderConfiguration
				.createDefault(this);
		ImageLoader.getInstance().init(configuration);
		L.disableLogging();// 关闭工具类日志

		mSettingShareP = getSharedPreferences(
				HiSettingsManager.KEY_SHARE_PREFERENCE,
				Context.MODE_MULTI_PROCESS);
		mSettingShareP
				.registerOnSharedPreferenceChangeListener(mSharePreferenceLlistener);

		initView();

		showLoadingDialog(0,0);

		killConflictApp();
		// refreshData(true);
		
		movieDaoImpl = new MovieDaoImpl(getApplicationContext());
		
		mDataManager.setContext(getApplicationContext());
		mDataManager.setCallBack(mHandler);
		mDataManager.registUsbBroadcast();
		mDataManager.initData();
		
		registDownloadReceiver();
		mBdInfo = new BDInfo();
		okHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
		autoSP = SharedPreferenceUtil.getInstance(getApplicationContext());
		connectivityManager = (ConnectivityManager) getSystemService("connectivity");
		
		localStorePath = sataUtil.getSataPath();
		
	//	new UnlockThread().start();
		
		checkMovieDirPath();
		
		//getAutoDatabaseMovies();
	}

	
	private void getAutoDatabaseMovies() {//加载数据库数据
		final ArrayList<Movie> temp = new ArrayList<>();
		// TODO Auto-generated method stub
		mDataManager.getAutoDatabaseMovies(new OnQueryLocalDataFinishedListener() {
			
			@Override
			public void onQueryLocalDataFinish(List<Movie> movies) {
				// TODO Auto-generated method stub
				temp.addAll(movies);
				mAllMovies = mDataManager.getAllMovies();
				temp.addAll(mAllMovies);
				mAllMovies.clear();
				mAllMovies.addAll(temp);
				curMovies.clear();
				curMovies.addAll(temp);
				gridAdapter.refreshView(mAllMovies);

			}
		});
	}

	private void killConflictApp() {
		Intent intent = new Intent();
		intent.setClassName("com.flyaudio",
				"com.flyaudio.AssistiveTouchService");
		startService(intent);

		Intent myintent = new Intent();
		myintent.setClassName("com.justlink.remoteservicedemo",
				"com.justlink.remoteservicedemo.JustLinkCmdService");
		startService(myintent);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					SocketClient socketClient = null;
					socketClient = new SocketClient();
					socketClient.writeMess("system busybox killall com.player.boxplayer");
					// &&system busybox killall mediaserver && sleep 1
					socketClient.readNetResponseSync();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();

	}

	private OnSharedPreferenceChangeListener mSharePreferenceLlistener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences arg0,
				String arg1) {
			if (HiSettingsManager.KEY_SP_LOCAL_BROWSER.equals(arg1)) {
				try {
					if (enterFileManager != null) {
						if (HiSettingsManager.getInstance()
								.getLocalBrowserIndex() == 0) {
							enterFileManager.setVisibility(View.VISIBLE);
							tvLocalSelect.setVisibility(View.VISIBLE);
						} else {
							enterFileManager.setVisibility(View.GONE);
							tvLocalSelect.setVisibility(View.INVISIBLE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		HomeController.getInstance().destroy();
		SambaController.getInstance().setActivity(null);
		mDataManager.unRegistUsbBroadcast();
		mDataManager.destroy();
		mDataManager = null;
		EventBus.getDefault().unregister(this);
	    unregisterReceiver(mDownloadReciver);
	    //删除解密文件
	  /*  File unlock = new File(unlockImage);
	    deleteUnlockDir(unlock);*/
	}
	
	
	/**
	 * 删除文件夹
	 * @param unlock
	 */

	private void deleteUnlockDir(File unlock) {
		LogUtil.i("folder", "刪除解密文件夾=="+unlock.getAbsolutePath());
		// TODO Auto-generated method stub
		if(!unlock.exists())
			return;
		if(unlock.isFile()){
			unlock.delete();
			return;
		}
		 
		File[] listFiles = unlock.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			deleteUnlockDir(listFiles[i]);
		}
		unlock.delete();
		LogUtil.i("folder", "刪除解密文件夾文件夾==");
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_back_iv:
			onBackPressed();
			break;
		case R.id.id_settings_iv:
			HomeController.getInstance().showMenuSetting();
			break;
		case R.id.id_enter_file_manager:
			/*
			 * try { PackageManager packageManager = getPackageManager(); Intent
			 * intent =
			 * packageManager.getLaunchIntentForPackage(Common.PACKAGE_FILE_MANAGER
			 * ); startActivity(intent); } catch (Exception e) {
			 * Toast.makeText(HomeActivity.this,
			 * getString(R.string.no_activity_info), Toast.LENGTH_SHORT).show();
			 * e.printStackTrace(); }
			 */
			
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo == null){
				
				if(netDialog==null)
					netDialog = new Dialog(HomeActivity.this, style.MyDialog);
				View view = getLayoutInflater().inflate(R.layout.no_network_dialog, null);
				TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
				TextView tvSure = (TextView) view.findViewById(R.id.tv_sure);
				tvCancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
					netDialog.dismiss();	
					}
				});
				
				tvSure.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(HomeActivity.this, ExplorerSelectActivity.class);
						startActivityForResult(intent, REQUEST_CODE);	
					}
				});
				netDialog.setContentView(view);
				netDialog.show();
				
			/*	AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
				builder.setNegativeButton(getResources().getString(R.string.net_cancel), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						return;
					}
				}).setPositiveButton(getResources().getString(R.string.net_confirm), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(HomeActivity.this, ExplorerSelectActivity.class);
						startActivityForResult(intent, REQUEST_CODE);	
					}
				}).setTitle(getResources().getString(R.string.no_network_toast)).create().show();*/
			}else{
				Intent intent = new Intent(this, ExplorerSelectActivity.class);
				startActivityForResult(intent, REQUEST_CODE);	
			}
			
			break;
		case R.id.listView:
		case R.id.gridView:
		case R.id.home_root_view:
			mHandler.sendEmptyMessage(MsgConstant.MSG_HIDE_INPUT_METHOD);
			break;
		case R.id.movie_search:
			clearSearchText("");
			showEditeTextDialog();
			break;
		default:
			break;
		}
	}

	private void clearSearchText(String text) {
		mSearchText.setText(text);
	}

	private void showEditeTextDialog() {
		EditeTextDialog dialog = new EditeTextDialog(this);
		dialog.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				mHandler.removeMessages(MsgConstant.MSG_SEARCH);
				String info = arg0.toString().trim();
				Message msg = new Message();
				msg.what = MsgConstant.MSG_SEARCH;
				msg.obj = info;
				mHandler.sendMessageDelayed(msg, 500);

				mSearchText.setText(info);
			}
		});
		dialog.show();
	}

	@Override
	public void onBackPressed() {
		if (mTypeNames.get(curListPosition).equals(
				getString(R.string.text_movie_folder))
				&& gridView.isShown()) {
			mGridLayout.setVisibility(View.GONE);
			mFolderList.setVisibility(View.VISIBLE);
			mFolderList.requestFocus();
			return;
		}

		showQuitDialog();
	}

	private CommonDialog mQuitDialog = null;

	private void showQuitDialog() {
		if (mQuitDialog == null) {
			mQuitDialog = new CommonDialog(this);
			mQuitDialog.setTitle(getString(R.string.quit_app));
			mQuitDialog.setText(getString(R.string.quit_app_confirm));
			mQuitDialog.setOnclickOkListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					 //删除解密文件
					finish();
					/*  File unlock = new File(unlockImage);
					    deleteUnlockDir(unlock);*/
				}
			});
		}

		mQuitDialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			HomeController.getInstance().showMenuSetting();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void listPageFlip(int selectPosition) {
		if (selectPosition > (mTypeNames.size() - 1)) {
			selectPosition = mTypeNames.size() - 1;
		}

		if (mCurrentType != null) {
			mCurrentType.setText(mTypeNames.get(selectPosition));
		}
		listView.setSelectionFromTop(selectPosition, 150);
		// listView.setSelection(selectPosition);
		refreshView(selectPosition);
		LogUtil.i("folder","refresh22222222");
	}

	private void searchMovie(String searchInfo) {
		if (null != mAllMovies && mAllMovies.size() > 0) {
			curMovies.clear();
			for (Movie movie : mAllMovies) {
				if (movie.compareTitle(searchInfo)) {
					curMovies.add(movie);
				}
			}

			mGridLayout.setVisibility(View.VISIBLE);
			mFolderList.setVisibility(View.GONE);
			if (null != curMovies) {
				gridAdapter.refreshView(curMovies);
				LogUtil.i("folder", "刷新444444444444" );
			}
		}
	}

	private List<Movie> searchMovieFromType(String type) {
		showLoadingDialog(0,0);
		List<Movie> movies = typeMovie.get(type);
		if (null == movies) {
			movies = new ArrayList<Movie>();
			for (Movie movie : mAllMovies) {
				if (movie.isTypeOf(type)) {
					movies.add(movie);
				}
			}
			typeMovie.put(type, movies);
		}
		hideLoadingDialog();
		return movies;
	}

	private void gridPageFlip(boolean focus) {
		List<Movie> tempMovie = searchMovieFromType(mTypeNames.get(curListPosition));
		curMovies.clear();
		curMovies.addAll(tempMovie);
		gridAdapter.refreshView(curMovies);
		LogUtil.i("folder", "刷新333333333333" );
	}

	private void refreshView(int position) {
		curListPosition = position;
		gridPageFlip(false);
		mHandler.sendEmptyMessageDelayed(MsgConstant.MSG_FOCUS, 5);
	}

	private void initView() {
		mCurrentType = (TextView) findViewById(R.id.id_current_type_tv);
		mSearchText = (TextView) findViewById(R.id.movie_search);
		mSearchText.setOnClickListener(this);

		textPage = (TextView) findViewById(R.id.textPage);
		btnBack = (ImageView) findViewById(R.id.home_back_iv);
		btnBack.setOnClickListener(this);
		enterFileManager = (ImageView) findViewById(R.id.id_enter_file_manager);
		enterFileManager.setOnClickListener(this);
		if (HiSettingsManager.getInstance().getLocalBrowserIndex() == 0) {
			enterFileManager.setVisibility(View.VISIBLE);
		}

		tvLocalSelect = (TextView) findViewById(R.id.tv_local_select);
		
		mGridLayout = findViewById(R.id.grid_layout);
		gridView = (MyGridView) findViewById(R.id.gridView);
		gridView.setOnItemClickListener(new GridClickListener());
		gridAdapter = new GridAdapter(this, null);
		// gridAdapter.getMovieInfo();
		gridView.setAdapter(gridAdapter);
		gridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				if (textPage != null) {
					if (gridAdapter.getCount() == 0) {
						textPage.setText("0/0");
					} else if (gridAdapter.getCount() > 0
							&& gridAdapter.getCount() <= 10) {
						textPage.setText("1/1");
					} else {
						int index = gridView.getLastVisiblePosition() + 1;
						int sumPage = gridAdapter.getCount() % 10 == 0 ? gridAdapter
								.getCount() / 10
								: gridAdapter.getCount() / 10 + 1;
						int currentPage = index % 10 == 0 ? index / 10
								: index / 10 + 1;
						textPage.setText(currentPage + "/" + sumPage);
					}

				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				if (textPage != null) {
					if (gridAdapter.getCount() == 0) {
						textPage.setText("0/0");
					} else {
						int index = gridView.getLastVisiblePosition() + 1;
						int sumPage = gridAdapter.getCount() % 10 == 0 ? gridAdapter
								.getCount() / 10
								: gridAdapter.getCount() / 10 + 1;
						int currentPage = index % 10 == 0 ? index / 10
								: index / 10 + 1;
						textPage.setText(currentPage + "/" + sumPage);
					}

				}
			}
		});

		listView = (MyListView) findViewById(R.id.listView);
		listView.setOnItemSelectedListener(new ListSelectedListener());
		listView.setOnItemClickListener(new ListClickListener());
		listAdapter = new ListAdapter(getApplicationContext(), null, mHandler);
		listView.setAdapter(listAdapter);

		mFolderList = (ListView) findViewById(R.id.movie_list_folder);
		mFolderList.setOnItemClickListener(mFolderListItemClickListener);
		mFolderAdapter = new MovieFolderAdapter(this);
		mFolderList.setAdapter(mFolderAdapter);

		mBtnSearchDevice = (ImageView) findViewById(R.id.id_settings_iv);
		mBtnSearchDevice.setOnClickListener(this);
		encryptionUtil= new ImageEncryptionUtil(this);

		findViewById(R.id.home_root_view).setOnClickListener(this);

	}

	private void updateView() {
	//	LogUtil.i("folder","updateview========");
		if (curListPosition < 0) {
			listPageFlip(0);
			mSearchText.setFocusable(true);
		} else {
			listPageFlip(curListPosition);
		}
	}

	private OnItemClickListener mFolderListItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			Map<String, Object> item = mFolderAdapter.getItem(position);
			if (Samba.TYPE_NAME.equals(item.get(CommonConstant.Common.TYPE))) {
				SambaController.getInstance().mountPath(item);
			} else if (Nfs.TYPE_NAME.equals(item
					.get(CommonConstant.Common.TYPE))) {
				NfsController.getInstance().mountPath(item);
			} else {
				onEventMainThread(new EventShowMovie(
						(String) item.get(Local.WORK_PATH), true));
			}

		}
	};


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(EventShowMovie event) {
		LogUtil.i("folder", "nfs路径挂载结果===="+event.mResult );
		if (event.mResult) {
			String mNetFolderPath = event.mFolderPath;
			LogUtil.i("folder", "nfs路径===="+mNetFolderPath );
			String configFile = mNetFolderPath + File.separator + MEDIA_CONFIG_FILE_NAME;
			File cfgfile = new File(configFile);
			
			if (cfgfile.exists() && cfgfile.canRead()) {//老版本，有电影索引文件
				List<Movie> tempMovie = Common.parseFolder(mNetFolderPath);
				
				if (tempMovie == null || tempMovie.isEmpty()) {
					Toast.makeText(getApplicationContext(), "没有找到视频列表",
							Toast.LENGTH_SHORT).show();
					return;
				}

				curMovies.clear();
				curMovies.addAll(tempMovie);
				gridAdapter.refreshView(curMovies);
				LogUtil.i("folder", "刷新2222222222" );
			//	gridView.setSelection(0);
				mGridLayout.setVisibility(View.VISIBLE);
				mFolderList.setVisibility(View.INVISIBLE);
				gridView.requestFocus();
			}else{//新版自动搜索海报
				autoSearchMovies(mNetFolderPath);
				
				int pathNum = autoSP.getNum();
				pathNum++;
				savePaths = autoSP.get("path");
				if(savePaths==null)
					savePaths = new HashSet<>();
					savePaths.add(mNetFolderPath+";"+pathNum);
					autoSP.put("path",savePaths);//保存已选的网络文件夹路径
			}
			
		} else {
			Toast.makeText(getApplicationContext(), "挂载失败", Toast.LENGTH_SHORT)
					.show();
		}

	}

	/**
	 * 数据库发生变化
	 * 
	 * @param event
	 */
	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onEventMainThread(EventDBChange event) {
		if (mFolderList.isShown() && mFolderAdapter != null) {
			final List<Map<String, Object>> list = getFolderList();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mFolderAdapter.refreshView(list);
				}
			});

		}

		final EventDBChange localEvent = event;
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				switch (localEvent.mEventType) {
				case EventDBChange.EVENT_DB_ADD:
					// FileUtil.showToast(HomeActivity.this,
					// getString(R.string.movie_folder_add_success,
					// localEvent.mSelfName));
					break;
				case EventDBChange.EVENT_DB_DELETE:
					FileUtil.showToast(
							HomeActivity.this,
							getString(R.string.movie_folder_add_delete,
									localEvent.mSelfName));
					break;
				case EventDBChange.EVENT_DB_UPDATE:
					FileUtil.showToast(
							HomeActivity.this,
							getString(R.string.movie_folder_add_update,
									localEvent.mSelfName));
					break;
				default:
					break;
				}
			}
		});

	}

	private List<Map<String, Object>> getFolderList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.addAll(DataManager.getInstance().getDefaultSataFolder());
		list.addAll(SambaController.getInstance().getServer());
		list.addAll(NfsController.getInstance().getServer());
		return list;
	}

	private void showToast(int resId) {
		String msg = getString(resId);
		if (null == toast) {
			toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

	private class GridClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int position, long arg3) {
			if (null != curMovies && curMovies.size() > position) {
				Intent intent = new Intent(HomeActivity.this,
						MovieActivity.class);
				intent.putExtra(Common.EXTRA_MOVIE, curMovies.get(position));
				startActivity(intent);
			} else {
				showToast(R.string.no_movie_info);
			}
		}
	}

	private class ListClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int pos,
				long arg3) {
			clearSearchText(getString(R.string.text_search));
			curListPosition = pos;
			if (mCurrentType != null) {
				mCurrentType.setText(mTypeNames.get(pos));
			}

			if (mTypeNames.get(pos).equals(
					getString(R.string.text_movie_folder))) {
				mGridLayout.setVisibility(View.GONE);
				mFolderList.setVisibility(View.VISIBLE);
				mFolderAdapter.refreshView(getFolderList());
				return;
			}

			mGridLayout.setVisibility(View.VISIBLE);
			mFolderList.setVisibility(View.GONE);
			refreshView(pos);
			LogUtil.i("folder","refresh3333333333");
		}

	}

	private class ListSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			clearSearchText(getString(R.string.text_search));
			if (mCurrentType != null) {
				mCurrentType.setText(mTypeNames.get(pos));
			}

			curListPosition = pos;

			if (mTypeNames.get(pos).equals(
					getString(R.string.text_movie_folder))) {
				mGridLayout.setVisibility(View.GONE);
				mFolderList.setVisibility(View.VISIBLE);
				mFolderAdapter.refreshView(getFolderList());
				return;
			} else if (mTypeNames.get(pos).equals(
					getString(R.string.text_local_play))) {
				mGridLayout.setVisibility(View.VISIBLE);
				mFolderList.setVisibility(View.GONE);
				return;
			}

			mGridLayout.setVisibility(View.VISIBLE);
			mFolderList.setVisibility(View.GONE);
			refreshView(pos);
			LogUtil.i("folder","refresh11111111111");
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}

	private void showLoadingDialog(int total,int currentCount) {
		StringBuffer sb = new StringBuffer();
		if(total != 0){
			sb.append(getString(R.string.text_loading)).append("   ").append(currentCount).append("/").append(total);	
		}else{
			sb.append(getString(R.string.text_loading));
		}
		
		if (null == dialog) {
			dialog = new ProgressDialog(this, R.style.DarkDialog);
			dialog.setTitle(getString(R.string.text_tips));
			dialog.setMessage(sb.toString());
			dialog.setIndeterminate(false);
			dialog.setCancelable(true);
			dialog.show();
		} else {
			dialog.setMessage(sb.toString());
			dialog.show();
		}

		dialog.setCancelable(true);
	}

	private void hideLoadingDialog() {
		if (null != dialog) {
			dialog.dismiss();
		}

		dialog = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
			selectPath = data.getStringExtra("path");
			int indexOf = selectPath.indexOf(";");
			 pathNum = Integer.parseInt(selectPath.substring(indexOf+1),selectPath.length());
			 autoSearchMovies(selectPath.substring(0,indexOf));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void autoSearchMovies(String selectPath2) {
		// TODO Auto-generated method stub
		LogUtil.i("folder", "selectpath==" + selectPath2);
		
		baseInfos.clear();
		if (TextUtils.isEmpty(selectPath2))
			return;
		File dir = new File(selectPath2);

		String newDir = localStorePath+ "/JSLMovie" + File.separator;
		// unlockImage = newDir+ "unlock";
		 newNFO = newDir + "infos";
		 newPoster = newDir + "posters";
		
		try {
			File nfoFile = new File(newNFO);
			if (!nfoFile.exists()){
				nfoFile.mkdirs();
				LogUtil.i("folder","="+newNFO+"不存在");
			}
				
			File posterFile = new File(newPoster);
			if (!posterFile.exists()){
				posterFile.mkdirs();
				LogUtil.i("folder","="+newPoster+"不存在");
			}
			
		/*File unlockFile = new File(unlockImage);
			if (!unlockFile.exists()){
				unlockFile.mkdirs();
				LogUtil.i("folder","="+unlockFile+"不存在");
			}*/
				

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//LogUtil.i("folder","=error=="+e.toString());
		}

		//downloadInfo(filterFile(dir));//递归
		
		if(!dir.exists())
			LogUtil.i("folder","目标文件夹不存在===");
		
		GetDirectoryMovies(dir, new OnScanFinishedListener() {
			
			@Override
			public void onScanFinished(ArrayList<MovieBaseInfo> bases) {
				// TODO Auto-generated method stub
				LogUtil.i("folder", "待下载电影数==="+bases.size());
				downloadInfo(bases);
			}
		});

	}

	
	//递归扫描
	public ArrayList<MovieBaseInfo> filterFile(File dir) {
	
	//	final ArrayList<MovieBaseInfo> list = new ArrayList<>();
		dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				
				if(arg0.isDirectory()){
					String absolutePath = arg0.getAbsolutePath();
					String[] split = absolutePath.split("/");
					if(split.length<9)
				filterFile(arg0);
				
				}else{
					if(filterMovie(arg0.getName())){
						MovieBaseInfo base = new MovieBaseInfo();
						int indexOf = arg0.getName().indexOf(".");
						
						String suffix = arg0.getName().substring(0,indexOf);	
						base.setName(suffix);
						base.setPath(arg0.getAbsolutePath());
						LogUtil.i("folder","筛选文件名==="+arg0.getName());
						baseInfos.add(base);
					}
				}
				
				return false;
			}
		});
		//baseInfos.addAll(list);
		LogUtil.i("folder","电影文件数==="+baseInfos.size());
		return baseInfos;
	
	}
	
	
	// 非递归扫描
	 private  File tmp;
	 private void GetDirectoryMovies(File file,final OnScanFinishedListener listener) {
		 final LinkedList list = new LinkedList(); //保存待遍历文件夹的列表
	      GetOneDir(file, list); //调用遍历文件夹根目录文件的方法
	     
		 new Thread(){
			 public void run() {

			      while (!list.isEmpty()) {
			          tmp = (File) list.removeFirst();
			          //这个地方的判断有点多余，但是为了保险还是给个判断了，正常情况列表中是只有文件夹的

			          //但是不排除特殊情况，例如：本身是文件夹的目标在压入堆栈之后变成了文件
			          if (tmp.isDirectory()) {
			              GetOneDir(tmp, list);
			          } else {
			        	  if(filterMovie(tmp.getName())){
			        		  LogUtil.i("folder","筛选文件名==="+tmp.getName());
			        		  
			        		  MovieBaseInfo base = new MovieBaseInfo();
			        		  
			        			Pattern patter = Pattern.compile("[0-9]{5,10}");
			        			Matcher matcher = patter.matcher(tmp.getName());
			        			if(matcher.find()){//纯数字文件名，往上两层找中文名
			        				String p = tmp.getParentFile().getName();
			        				Pattern compile = Pattern.compile("[\u4e00-\u9fa5]");
			        				if(compile.matcher(p).find()){
			        					 base.setName(p);
			        				}else{
			        					String pp = tmp.getParentFile().getParentFile().getName();
			        					if(compile.matcher(pp).find()){
			        						base.setName(pp);
			        					}else{
			        						 int indexOf = tmp.getName().indexOf(".");
			       	  	        		  base.setName(tmp.getName().substring(0,indexOf));
			        					}
			        						
			        				}
			        			}else{
			  	        		  int indexOf = tmp.getName().indexOf(".");
			  	        		  base.setName(tmp.getName().substring(0,indexOf));
			        			}
			        			
			        			 base.setPath(tmp.getAbsolutePath());
			  	        		  baseInfos.add(base);		
			        	  }
			          }
			     }
			      
			      listener.onScanFinished(baseInfos);
			 };
		 }.start();
	     

	}


	// 遍历指定文件夹根目录下的文件

	 private void GetOneDir(File file , LinkedList list){
		 if(mBdInfo.isBDFile(file.getAbsolutePath())){//蓝光文件夹
			 MovieBaseInfo base = new MovieBaseInfo();
			 base.setName(file.getName());
   		  base.setPath(mBdInfo.getBDPlayPath());
   		  baseInfos.add(base);
   		  LogUtil.i("folder", "蓝光文件夹=="+base.getName());
		 }else{
	      //每个文件夹遍历都会调用该方法
	      File[] files = file.listFiles();
	      if (files == null || files.length == 0) {
	    	  LogUtil.i("folder","空文件夹=====");
	           return ;
	      }

	      for (File f : files) {
	          if (f.isDirectory()) {
	              list.add(f);
	          } else {
	               //这里列出当前文件夹根目录下的所有文件
	        	  if(filterMovie(f.getName())){
	        		  LogUtil.i("folder","筛选文件名==="+f.getName());
	        		  
	        		  MovieBaseInfo base = new MovieBaseInfo();
	        		  //纯数字文件名往上找两层
	        			Pattern patter = Pattern.compile("[0-9]{5,10}");
	        			Matcher matcher = patter.matcher(f.getName());
	        			if(matcher.find()){//纯数字文件名，往上两层找中文名
	        				String p = f.getParentFile().getName();
	        				Pattern compile = Pattern.compile("[\u4e00-\u9fa5]");
	        				if(compile.matcher(p).find()){
	        					 base.setName(p);
	        				}else{
	        					String pp = f.getParentFile().getParentFile().getName();
	        					if(compile.matcher(pp).find()){
	        						base.setName(pp);
	        					}else{
	        						 int indexOf = f.getName().indexOf(".");
	       	  	        		  base.setName(f.getName().substring(0,indexOf));
	        					}
	        						
	        				}
	        			}else{
	  	        		  int indexOf = f.getName().indexOf(".");
	  	        		  base.setName(f.getName().substring(0,indexOf));
	        			}
	        			
	        			 base.setPath(f.getAbsolutePath());
	  	        		  baseInfos.add(base);		
	        		
	        	  }
	          }
	     }
	 }
	 }

	
	
	
	
	
	
	
	public boolean filterMovie(String fileName){
		
	/*	Pattern patter = Pattern.compile("[0-9]{5,10}");
		Matcher matcher = patter.matcher(fileName);
		if(matcher.find())
		return false;*/
		
		int indexOf = fileName.indexOf(".");
		if(indexOf == -1)
			return false;
		String suffix = fileName.substring(indexOf);
		if (suffix.equalsIgnoreCase(".mp4")
				|| suffix.equalsIgnoreCase(".mkv") 
				|| suffix.equalsIgnoreCase(".iso")
				|| suffix.equalsIgnoreCase(".3gp")
				|| suffix.equalsIgnoreCase(".mpg")
				|| suffix.equalsIgnoreCase(".mpeg")
				|| suffix.equalsIgnoreCase(".webm")
				|| suffix.equalsIgnoreCase(".ts")
				|| suffix.equalsIgnoreCase(".m2ts")
				|| suffix.equalsIgnoreCase(".avi")
				|| suffix.equalsIgnoreCase(".rmvb")
				|| suffix.equalsIgnoreCase(".wmv")
				|| suffix.equalsIgnoreCase(".afs")
				|| suffix.equalsIgnoreCase(".divx")
				|| suffix.equalsIgnoreCase(".bdmv")) 
			
			return true;
		
		return false;
	}
	
	

	public void getAutoMovie(int pathNum) {
		autoMovies.clear();
		StringBuilder sbActors = new StringBuilder();
		StringBuilder sbTypes = new StringBuilder();
		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			String xmlPath;
			InputStream in = null;
			int eventType;
			if(baseInfos.size()==0)
				return;
			
			StringBuilder sb = new StringBuilder();
			LogUtil.i("folder", "oldcount=="+oldDownloadCount+"downCount==="+downloadCount);
		
			for (MovieBaseInfo base : baseInfos) {			
				Movie movie = new Movie();
				movie.setMoviePath(base.getPath());
				movie.setTitle(base.getName());
				sbActors.setLength(0);
				sbTypes.setLength(0);     
				movie.setAuto(1);
				movie.setPathNum(pathNum);
				movie.setPicPath(sb.append(newPoster).append("/").append(base.getName()).append("-poster.jpg").toString());
				sb.setLength(0);
				LogUtil.i("folder", "posterpath===" + movie.getPicPath());
				
				xmlPath = sb.append(newNFO).append("/").append(base.getName()).append(".nfo").toString();
				sb.setLength(0);
				LogUtil.i("folder", "xmlpath===" + xmlPath);
				
				if(!new File(xmlPath).exists()){
				continue;
				}
				
				in = new FileInputStream(xmlPath);
				parser.setInput(in, "UTF-8");
				eventType = parser.getEventType();
				
				while (eventType != XmlPullParser.END_DOCUMENT) {
				
					if (eventType == XmlPullParser.START_DOCUMENT) {
						LogUtil.i("folder", "xmlparse start===");
						
					} else if (eventType == XmlPullParser.START_TAG) {
						
						String name = parser.getName();
						if ("title".equals(name)) {
							movie.setTitle(parser.nextText());
							if("系统发生错误".equals(movie.getTitle())){
								LogUtil.i("folder", "错误的nfo文件===");
								movie.setTitle(base.getName());
								//autoMovies.add(movie);
								File error = new File(xmlPath);
								if(error.exists())
									error.delete();
								File inval = new File(movie.getPicPath());
								if(inval.exists())
									inval.delete();
								break;
							}
							LogUtil.i("folder", "titel===" + movie.getTitle());
						} else if ("rating".equals(name)) {
							movie.setRate(parser.nextText());
						} else if ("year".equals(name)) {
							movie.setOnLineTime(parser.nextText());
						} else if ("plot".equals(name)) {
							movie.setInfo(parser.nextText());
						} else if ("runtime".equals(name)) {
							movie.setmDuration(parser.nextText());
						} else if ("genre".equals(name)) {
							sbTypes.append(parser.nextText()).append("/");
							movie.setmType(sbTypes.toString().substring(0, sbTypes.length()-1));
						} else if ("country".equals(name)) {
							movie.setmArea(parser.nextText());
						} else if ("director".equals(name)) {
							movie.setmDirector(parser.nextText());
						} else if ("name".equals(name)) {
							sbActors.append(parser.nextText()).append("/");
							movie.setmActors(sbActors.toString().substring(0, sbActors.toString().length()-1));
						}
						
					}
					
					eventType = parser.next();
				}
				
				autoMovies.add(movie);

			}
			if(in != null)
			in.close();
			LogUtil.i("folder", "添加数量==" + autoMovies.size());
			//curMovies.clear();
			movieDaoImpl.addMovies(autoMovies);
			curMovies.addAll(autoMovies);
			//将新数据放在前面
			ArrayList<Movie> temp = new ArrayList<>();
			temp.addAll(autoMovies);
			temp.addAll(mAllMovies);
			mAllMovies.clear();
			mAllMovies.addAll(temp);
			curMovies.clear();
			curMovies.addAll(temp);
			
			//LogUtil.i("folder", "总数量==" + curMovies.size());
			mHandler.sendEmptyMessage(MsgConstant.AUTO_ADD_MOVIE_FINISH);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.i("folder","xml解析异常=="+e.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	class DownloadReciver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub			
			downloadCount++;
			LogUtil.i("folder","下载数量==="+downloadCount);
		//	hideLoadingDialog();
			showLoadingDialog(baseInfos.size(),downloadCount/2);
		//	if(downloadCount==baseInfos.size()*2 || downloadCount%40==0){
			if(downloadCount==baseInfos.size()*2){	
				LogUtil.i("folder", "更新電影=============");
				
			/*	unlockImage(new onDownloadFinishedListener() {
					
					@Override
					public void onDownloadFinish() {
						// TODO Auto-generated method stub
						getAutoMovie(pathNum);	
						oldDownloadCount = downloadCount;
						
						mHandler.sendEmptyMessage(MsgConstant.AUTO_ADD_MOVIE_FINISH);
					}
				});*/
				oldDownloadCount = downloadCount;
				getAutoMovie(pathNum);
				//SystemClock.sleep(2000);
				
				try {
					if(posterOut != null)
					posterOut.close();
					if(nfoOut != null)
					nfoOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	       //解密图片
			private int count;
			private void unlockImage(final onDownloadFinishedListener listener) {
				
				new Thread(){
					public void run() {
						count=0;
						File unlockDir = new File(unlockImage);
						File lockDir = new File(newPoster);
						if(!lockDir.exists())
							return;
						if(!unlockDir.exists())
							unlockDir.mkdirs();
						final File[] listFiles = lockDir.listFiles();
						if(listFiles==null || listFiles.length==0)
							return;
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i <baseInfos.size(); i++) {
							MovieBaseInfo base = baseInfos.get(i);
							sb.append(newPoster).append("/").append(base.getName()).append("-poster.jpg").toString();
							File tem = new File(sb.toString());
							if(tem.exists()){
								/*encryptionUtil.unlock(tem, new OnUnLockImageFinishedListener() {

									@Override
									public void onUnlockFinish(String path) {
										// TODO Auto-generated method stub
										++count;
										LogUtil.i("folder", "解密数量==="+count);
										if(count==baseInfos.size())
											listener.onDownloadFinish();					
									}
								});*/
							}else{
								++count;
								if(count==baseInfos.size())
									listener.onDownloadFinish();
							}
							sb.setLength(0);
													
						}
						
					};
				}.start();
				// TODO Auto-generated method stub
		
			}
	
	
	public void registDownloadReceiver(){
		mDownloadReciver = new DownloadReciver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("intent.action.jslmovie_download_finish");
		filter.addAction("intent.action.jslmovie_download_fail");
		registerReceiver(mDownloadReciver, filter);
	}


	public void downloadInfo(ArrayList<MovieBaseInfo> baseInfos){
		downloadCount=0;
		oldDownloadCount = 0;
		StringBuilder sb = new StringBuilder();
		StringBuilder sbPoster = new StringBuilder();
		StringBuilder sbNfo = new StringBuilder();
		Request posterReq = null;
		Request nfoReq = null;
		MovieBaseInfo base = null;
		File postFile = null;//本地保存路径
		File nfoFile = null;
		try {
		for (int i = 0; i < baseInfos.size(); i++) {
			base = baseInfos.get(i);
			
			String postPath = sb.append(downloadBase).append("posters/").append(base.getName()).append("-poster.jpg").toString();
			sb.setLength(0);
			posterReq = new Request.Builder().url(postPath).build();
			LogUtil.i("folder","download poster==="+postPath);
			
			 postFile = new File(sbPoster.append(newPoster).append("/").append(base.getName()).append("-poster.jpg").toString());
			 LogUtil.i("folder", "本地缓存路径=="+sbPoster.toString());
			 sbPoster.setLength(0);
			 if(!postFile.exists()){
					postFile.createNewFile();
			// posterOut = new FileOutputStream(postFile);	
			 save2Local(posterReq, postFile,false);
			 }else{
				 sendBroadcast(new Intent("intent.action.jslmovie_download_finish"));
			 }
			
			String nfoPath = sb.append(downloadBase).append("infos/").append(base.getName()).append(".nfo").toString();
			sb.setLength(0);
			nfoReq = new Request.Builder().url(nfoPath).build();
			LogUtil.i("folder","download nfo==="+nfoPath);
			
			nfoFile = new File(sbNfo.append(newNFO).append("/").append(base.getName()).append(".nfo").toString());
			sbNfo.setLength(0);
			if(!nfoFile.exists()){
				nfoFile.createNewFile();
			//nfoOut = new FileOutputStream(nfoFile);
			save2Local(nfoReq, nfoFile,true);
			}else{
				 sendBroadcast(new Intent("intent.action.jslmovie_download_finish"));
			}
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void save2Local(final Request request,final File file,final boolean isNfo){
		
		
		okHttpClient.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
			
				try {
					 FileOutputStream	out = new FileOutputStream(file);				
					 ResponseBody body = arg1.body();
					 byte[] bytes = body.bytes();
					 
				if(isNfo){
					out.write(bytes);
				}else{
						byte data = (byte) (bytes.length&0x7F);
						for (int i = 0; i <bytes.length ; i++) {
							bytes[i] ^= ((data+i)^i)&0x7F; 
						}
					out.write(bytes);
				}
				
				out.flush();
				sendBroadcast(new Intent("intent.action.jslmovie_download_finish"));
				
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				sendBroadcast(new Intent("intent.action.jslmovie_download_fail"));
				String absolutePath = file.getAbsolutePath();
				file.delete();
				//File tem = new File(absolutePath);
				//save2Local(request, tem, isNfo);//重新下载
				LogUtil.i("folder","下载失败=====");
			}
		});				
	}	
	
	
	  //检测以往添加的电影目录是否存在（是否换原盘路径）
		private void checkMovieDirPath() {                                                                                       
			// TODO Auto-generated method stub
			Set set = autoSP.get("path");
		//	Log.i("folder","init set  num==="+set.size());
			if(set==null || set.size()==0){
				//Toast.makeText(this, getResources().getString(R.string.empty_toast), Toast.LENGTH_LONG).show();
				return;
				}
			
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				String path = (String) it.next();	
				if(! (path.contains("nfs")||path.contains("samba"))){
					int indexOf = path.indexOf(";");
					int num = Integer.parseInt(path.substring(indexOf+1,path.length()));
					File tem = new File(path.substring(0,indexOf));
					if(!tem.exists()){
						Toast.makeText(this, getResources().getString(R.string.change_toast), Toast.LENGTH_LONG).show();
						showLoadingDialog(0,0);
						set.remove(path);
						movieDaoImpl.deleteMovies(num);
					}
				}
			}
			//刷新列表
		/*	mAllMovies = mDataManager.getAllMovies();
			gridAdapter.refreshView(mAllMovies);*/
			autoSP.put("path", set);
			hideLoadingDialog();
		}
	
	private class UnlockThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			unlockCount=0;
		//	showLoadingDialog();
			String newDir = localStorePath+ "/JSLMovie" + File.separator;
			// unlockImage = newDir+ "unlock";
			 newNFO = newDir + "infos";
			 newPoster = newDir + "posters";
			 
			File unlockDir = new File(unlockImage);
			File lockDir = new File(newPoster);
			if(!lockDir.exists()){
				hideLoadingDialog();
				return;
			}
			if(!unlockDir.exists())
				unlockDir.mkdirs();
			final File[] listFiles = lockDir.listFiles();
			if(listFiles==null || listFiles.length==0){
				hideLoadingDialog();
				return;
			}
				
		/*	for (int i=0;i<listFiles.length;i++) {
				encryptionUtil.unlock(listFiles[i], new OnUnLockImageFinishedListener() {

					@Override
					public void onUnlockFinish(String path) {
						// TODO Auto-generated method stub
						++unlockCount;
						if(count==listFiles.length)
							hideLoadingDialog();
					}
				});
			}*/
		}
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogUtil.i("folder", "onstop...........");
	}
}