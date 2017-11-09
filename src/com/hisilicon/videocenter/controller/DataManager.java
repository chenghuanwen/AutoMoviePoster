package com.hisilicon.videocenter.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.auto.OnQueryLocalDataFinishedListener;
import com.hisilicon.videocenter.auto.SharedPreferenceUtil;
import com.hisilicon.videocenter.auto.db.MovieDaoImpl;
import com.hisilicon.videocenter.event.EventDBChange;
import com.hisilicon.videocenter.util.Common;
import com.hisilicon.videocenter.util.CommonConstant;
import com.hisilicon.videocenter.util.CommonConstant.Local;
import com.hisilicon.videocenter.util.CommonConstant.Nfs;
import com.hisilicon.videocenter.util.CommonConstant.Samba;
import com.hisilicon.videocenter.util.LogUtil;
import com.hisilicon.videocenter.util.MountUtils;
import com.hisilicon.videocenter.util.Movie;
import com.hisilicon.videocenter.util.MsgConstant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;

public class DataManager {
	protected static final String TAG = "DataManager";

	private static DataManager mInstance;
	
	private Context mContext;
	private Handler mCallbackHandler;
	private MovieDaoImpl movieDaoImpl;
	private SharedPreferenceUtil sp;
	private static final String MEDIA_CONFIG_FILE_NAME = "justlink_media_index.txt";
	private ConcurrentHashMap<String, List<Movie>> mAllMovies = new ConcurrentHashMap<>();

	private ConnectivityManager connectivityManager;

	private DataManager() {
		EventBus.getDefault().register(this);
		
	}

	public static DataManager getInstance() {
		if (mInstance == null) {
			synchronized (DataManager.class) {
				if (mInstance == null) {
					mInstance = new DataManager();
				}
			}
		}
		
		
		
		return mInstance;

	}

	public void setContext(Context context) {
		mContext = context;
		connectivityManager = (ConnectivityManager)mContext.getSystemService("connectivity");
	}
	
	public void setCallBack(Handler handler) {
		mCallbackHandler = handler;
	}
	
	/**
	 * 必选先设置context
	 */
	public void registUsbBroadcast() {
		IntentFilter iFilter=new IntentFilter();
		//iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		iFilter.addDataScheme("file");
		mContext.registerReceiver(mBroadcastReceiver,iFilter);
	}
	
	public void unRegistUsbBroadcast() {
		mContext.unregisterReceiver(mBroadcastReceiver);
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				StorageVolume storage = (StorageVolume) intent.getExtras().get(StorageVolume.EXTRA_STORAGE_VOLUME);
				String path = storage.getPath();
				onUsbMountChange(action, path);
				Log.i(TAG, "action:" + action + "path:" + path);
            } catch (Exception e) {
	            e.printStackTrace();
            }
			
			
		}
	};
	
	public void initData() {
		Thread mRefreshThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// 本地数据读取
				try {
					initLocalData();
					mCallbackHandler.removeMessages(MsgConstant.MSG_LOAD_FAIL);
					mCallbackHandler.sendEmptyMessageDelayed(MsgConstant.MSG_LOAD_SUCCESS, 0);
				} catch (Throwable e) {
					e.printStackTrace();
				}

				NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
				if(networkInfo != null){
				// 网络Nfs数据读取
				List<Map<String, Object>> nfsList = NfsController.getInstance().getServer();
				for (Map item : nfsList) {
					String tempPath = NfsController.getInstance().mountPathSync(item);
					Log.i("folder","nfs路径===="+tempPath);
				
						List<Movie> tempMovie = Common.parseFolder(tempPath);
						if (tempMovie != null) {
							mAllMovies.put(tempPath, tempMovie);
							mCallbackHandler.sendEmptyMessageDelayed(MsgConstant.MSG_UPDATE_MOVIE, 0);
						}				
				}

				// 网络samba数据读取
				List<Map<String, Object>> sambaList = SambaController.getInstance().getServer();
				for (Map item : sambaList) {
					String tempPath = SambaController.getInstance().mountPathSync(item);
					
						List<Movie> tempMovie = Common.parseFolder(tempPath);
						if (tempMovie != null) {
							mAllMovies.put(tempPath, tempMovie);
							mCallbackHandler.sendEmptyMessageDelayed(MsgConstant.MSG_UPDATE_MOVIE, 0);
						}		
				}
				}
			}
		});

		mCallbackHandler.sendEmptyMessageDelayed(MsgConstant.MSG_LOAD_FAIL, 60000);
		mRefreshThread.start();
		
		movieDaoImpl = new MovieDaoImpl(mContext);
		sp = SharedPreferenceUtil.getInstance(mContext);
	}
	
	public List<Movie> getAllMovies() {
		List<Movie> result = new ArrayList<Movie>();
		Collection<List<Movie>> collection = mAllMovies.values();
		for (List<Movie> list : collection) {
			result.addAll(list);
		}
		
		//result.addAll(getAutoDatabaseMovies());
		
		return result;
	}

	public int getMovieCountByItem(Map item) {
		String mountPath = null;
		if (Samba.TYPE_NAME.equals(item.get(CommonConstant.Common.TYPE))) {
			mountPath = SambaController.getInstance().getPath(item);
		} else if (Nfs.TYPE_NAME.equals(item.get(CommonConstant.Common.TYPE))) {
			mountPath = NfsController.getInstance().getPath(item);
		} else {
			mountPath = (String) item.get(Local.WORK_PATH);
		}
		
		if (mAllMovies == null) {
			return 0;
		}
		
		if (mountPath == null ) {
			return 0;
		}
		
		List list = mAllMovies.get(mountPath);
	    return list == null ? 0 :list.size();
    }
	
	private void initLocalData() {
		mAllMovies.clear();
		ArrayList<String> localMountList = new ArrayList<String>(MountUtils.getSDCardPaths());
		initMoviesTypes(localMountList);

		List<String> mConfigFolders = Common.getAllConfigFolders(localMountList);
		for(String folder : mConfigFolders) {
			ArrayList<Movie> tempMovie = Common.parseFolder(folder);
			if (tempMovie != null && !tempMovie.isEmpty()) {
				mAllMovies.put(folder, tempMovie);
			}
		}

	}
	
	public List<Map<String, Object>> getDefaultSataFolder() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sataPath = MountUtils.getSataPath();
		if (sataPath != null) {
			String configPath = sataPath + File.separator + "justlinkMedia";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(CommonConstant.Common.TYPE, Local.TYPE_NAME);
			map.put(Local.WORK_PATH, configPath);
			map.put(Local.DISPLAY_PATH, configPath);
			map.put(Local.SELFDEFINE_NAME, "默认影库");
			list.add(map);
		}
		
		return list;
	}
	
	private ArrayList<String> mTypesList = new ArrayList<>();
	private void initMoviesTypes(ArrayList<String> mountlist) {
		mTypesList.clear();
		//mTypesList.add(mContext.getString(R.string.text_movie_folder));
		//mTypesList.add(mContext.getString(R.string.text_search));
		mTypesList.add(mContext.getString(R.string.text_all));
		
		String sataPaht = MountUtils.getSataPath();
		if (!TextUtils.isEmpty(sataPaht)) {
			ArrayList<String> sataList = new ArrayList<>();
			sataList.add(sataPaht);
			List<String> sataTypes = Common.getAllTypes(sataList);
			if (sataTypes != null && !sataTypes.isEmpty()) {
				mTypesList.addAll(sataTypes);
				return;
			}
			
		}
		
		String[] typesArray = mContext.getResources().getStringArray(R.array.type_info_list);
		mTypesList.addAll(Arrays.asList(typesArray));
		//mTypesList.addAll(Common.getAllTypes(mountlist));
	}
	
	public ArrayList<String> reGetTypesList() {
		ArrayList<String> mountlist = new ArrayList<String>(MountUtils.getSDCardPaths());
		initMoviesTypes(mountlist);
		return mTypesList;
		
	}
	public ArrayList<String> getTypesList() {
		return mTypesList;
	}
	
	/**
	 * 数据库发生变化
	 * 
	 * @param event
	 */
	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onEventMainThread(EventDBChange event) {
		if (event.mEventType == EventDBChange.EVENT_DB_UPDATE) {
			return;
		}
		
		reloadAllData();
		
		mCallbackHandler.sendEmptyMessageDelayed(MsgConstant.MSG_UPDATE_MOVIE, 0);
		
		if (event.mEventType == EventDBChange.EVENT_DB_ADD) {
			Map map = event.mDeviceItemMap;
			String mountPath = null;
			if (Samba.TYPE_NAME.equals(map.get(CommonConstant.Common.TYPE))) {
				mountPath = SambaController.getInstance().getPath(map);
			} else if (Nfs.TYPE_NAME.equals(map.get(CommonConstant.Common.TYPE))) {
				mountPath = NfsController.getInstance().getPath(map);
			} else {
				mountPath = (String)map.get(Local.WORK_PATH);
			}
			LogUtil.i("folder","网络挂载路径==="+mountPath);
			List list = mAllMovies.get(mountPath);
			if (list != null) {
				Message msg = mCallbackHandler.obtainMessage(MsgConstant.MSG_SHOW_ADD_TEXT);
				msg.obj = event.mSelfName;
				msg.arg1 = list.size();
				mCallbackHandler.sendMessage(msg);
			}
		}
		
	}
	
	private void onUsbMountChange(String action, String path) {
		if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
			final String mountPoint = path;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						reloadAllData();
						Set<String> set = mAllMovies.keySet();
						int count = 0;
						for(String key : set) {
							if (key.startsWith(mountPoint)) {
								count += mAllMovies.get(key).size();
							}
						}
						
						mCallbackHandler.sendEmptyMessageDelayed(MsgConstant.MSG_UPDATE_MOVIE, 0);
						
						Message msg = mCallbackHandler.obtainMessage(MsgConstant.MSG_SHOW_ADD_TEXT);
						msg.obj = mountPoint;
						msg.arg1 = count;
						mCallbackHandler.sendMessage(msg);
						
                    } catch (Exception e) {
	                    e.printStackTrace();
                    }
					
				}
			}).start();
		} else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
			Set<String> set = mAllMovies.keySet();
			for(String key : set) {
				if (key.startsWith(path)) {
					mAllMovies.remove(key);
				}
			}
			
			mCallbackHandler.sendEmptyMessageDelayed(MsgConstant.MSG_UPDATE_MOVIE, 0);
		}
	}

	private void reloadAllData() {
	    // 本地数据读取
		try {
			initLocalData();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		// 网络Nfs数据读取
		List<Map<String, Object>> nfsList = NfsController.getInstance().getServer();
		for (Map item : nfsList) {
			String tempPath = NfsController.getInstance().mountPathSync(item);
			String configFile = tempPath + File.separator + MEDIA_CONFIG_FILE_NAME;
			File cfgfile = new File(configFile);
			LogUtil.i("folder","预加载网络路径==="+tempPath);
			if (cfgfile.exists() && cfgfile.canRead()) {//老版本，有电影索引文件
				List<Movie> tempMovie = Common.parseFolder(tempPath);
				if (tempMovie != null) {
					mAllMovies.put(tempPath, tempMovie);
				}
			}else{
				Message autoMsg = Message.obtain();
				autoMsg.obj = tempPath;
				autoMsg.what = MsgConstant.MSG_AUTO_SEARCH_MOVIE;
				mCallbackHandler.sendMessage(autoMsg);
			}
						
		}

		// 网络samba数据读取
		List<Map<String, Object>> sambaList = SambaController.getInstance().getServer();
		for (Map item : sambaList) {
			String tempPath = SambaController.getInstance().mountPathSync(item);
			String configFile = tempPath + File.separator + MEDIA_CONFIG_FILE_NAME;
			File cfgfile = new File(configFile);
			
			if (cfgfile.exists() && cfgfile.canRead()) {//老版本，有电影索引文件
				List<Movie> tempMovie = Common.parseFolder(tempPath);
				if (tempMovie != null) {
					mAllMovies.put(tempPath, tempMovie);
				}	
			}else{
				Message autoMsg = Message.obtain();
				autoMsg.obj = tempPath;
				autoMsg.what = MsgConstant.MSG_AUTO_SEARCH_MOVIE;
				mCallbackHandler.sendMessage(autoMsg);
			}
			
		}
    }
	
	
	
	public void getAutoDatabaseMovies(final OnQueryLocalDataFinishedListener listener){
		//搜索以往自动添加的数据
				Set set = sp.get("path");
				if(set != null){
					Iterator<String> iterator = set.iterator();	
					while(iterator.hasNext()){
						String path = iterator.next();
						int indexOf = path.indexOf(";");
						String realPath = path.substring(0,indexOf);
						File file = new File(realPath);
						if(!file.exists()){
							movieDaoImpl.deleteMovies(Integer.parseInt(path.substring(indexOf+1,path.length())));
						}
						
					}
				}
				
			movieDaoImpl.queryLocalMovies(new OnQueryLocalDataFinishedListener() {
				
				@Override
				public void onQueryLocalDataFinish(List<Movie> movies) {
					// TODO Auto-generated method stub
					listener.onQueryLocalDataFinish(movies);
				}
			});
	}
	
	
	

	public void destroy() {
		EventBus.getDefault().unregister(this);
	    mCallbackHandler = null;
	    mContext = null;
	    mAllMovies.clear();
	    mTypesList.clear();
	    
    }

}
