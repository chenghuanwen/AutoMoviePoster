package com.hisilicon.videocenter.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greenrobot.eventbus.EventBus;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.explorer.ftp.DBHelper;
import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.auto.SharedPreferenceUtil;
import com.hisilicon.videocenter.auto.db.MovieDaoImpl;
import com.hisilicon.videocenter.event.EventDBChange;
import com.hisilicon.videocenter.event.EventShowMovie;
import com.hisilicon.android.hinetshare.HiNfsClientManager;
import com.hisilicon.videocenter.util.CommonConstant;
import com.hisilicon.videocenter.util.CommonConstant.Nfs;
import com.hisilicon.videocenter.util.LogUtil;

public class NfsController {
	private static NfsController mInstance;
	private Activity mActivity;
	private SharedPreferenceUtil sp;
	private MovieDaoImpl movieDaoImpl;
	private NfsController() {
	}

	public static NfsController getInstance() {
		if (mInstance == null) {
			synchronized (NfsController.class) {
				if (mInstance == null) {
					mInstance = new NfsController();
					
				}
			}
		}

		return mInstance;

	}

	public void setActivity(Activity activity) {
		mActivity = activity;
		sp = SharedPreferenceUtil.getInstance(mActivity);
		movieDaoImpl = new MovieDaoImpl(mActivity);
	}

	/**
	 * Get encapsulated into a list of key-value pairs
	 * CNcomment:获取封装成key-value对的列表
	 */
	public List<Map<String, Object>> getServer() {
		DBHelper dbHelper = DBHelper.getInstance(mActivity);
		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Cursor cursor = sqlite.query(Nfs.TABLE_NAME, new String[] { Nfs.ID, Nfs.WORK_PATH, Nfs.SERVER_IP,
		        Nfs.ABSOLUTE_PATH, Nfs.SELFDEFINE_NAME }, null, null, null, null, null);
		String serverIp = null;
		String workPath = null;
		String absolutePaht = null;
		String nickName = null;
		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(CommonConstant.Common.TYPE, Nfs.TYPE_NAME);
			map.put(Nfs.IMAGE, R.drawable.folder_file);

			serverIp = cursor.getString(cursor.getColumnIndex(Nfs.SERVER_IP));
			workPath = cursor.getString(cursor.getColumnIndex(Nfs.WORK_PATH));
			absolutePaht = cursor.getString(cursor.getColumnIndex(Nfs.ABSOLUTE_PATH));
			nickName = serverIp + workPath;

			map.put(Nfs.NICK_NAME, nickName);
			map.put(Nfs.SERVER_IP, serverIp);
			map.put(Nfs.WORK_PATH, workPath);
			map.put(Nfs.ABSOLUTE_PATH, absolutePaht);
			map.put(Nfs.SELFDEFINE_NAME, cursor.getString(cursor.getColumnIndex(Nfs.SELFDEFINE_NAME)));
			map.put(Nfs.SHORT, 1);
			map.put(Nfs.MOUNT_POINT, " ");
			map.put(Nfs.DISPLAY_PATH, nickName + File.separator + absolutePaht);
			list.add(map);
		}
		cursor.close();
		return list;
	}

	public void mountPath(Map<String, Object> item) {
		final Map<String, Object> netItem = item;
		new Thread(new Runnable() {

			@Override
			public void run() {
				String result = mountPathSync(netItem);
				if (result != null) {
					EventBus.getDefault().post(new EventShowMovie(result, true));
				} else {
					EventBus.getDefault().post(new EventShowMovie(result, false));
				}
			}

		}).start();

	}
	
	/**
	 * 获取文件目录
	 * @param item
	 * @return
	 */
	public String getPath(Map<String, Object> item) {
		final Map<String, Object> netItem = item;
		final String userserver = netItem.get(Nfs.SERVER_IP).toString();
		final String folder_position = netItem.get(Nfs.WORK_PATH).toString().replace("\\", "/");
		final StringBuilder builder = new StringBuilder(userserver);
		builder.append(":").append(folder_position);

		final HiNfsClientManager nfsClient = new HiNfsClientManager();
		String returnStr = nfsClient.getMountPoint(builder.toString().replace("\\", "/"));
		if (returnStr == null || returnStr.equals("ERROR")) {
			return null;
		} else {
			return returnStr + File.separator + netItem.get(Nfs.ABSOLUTE_PATH);
		}

	}

	public String mountPathSync(Map<String, Object> item) {
		final Map<String, Object> netItem = item;
		final String userserver = netItem.get(Nfs.SERVER_IP).toString();
		final String folder_position = netItem.get(Nfs.WORK_PATH).toString().replace("\\", "/");
		final StringBuilder builder = new StringBuilder(userserver);
		builder.append(":").append(folder_position);

		final HiNfsClientManager nfsClient = new HiNfsClientManager();
		String returnStr = nfsClient.getMountPoint(builder.toString().replace("\\", "/"));
		if (returnStr == null || returnStr.equals("ERROR")) {
			int result = nfsClient.mount(builder.toString(),null);
			Log.e("ADD1165", "mountPath result: " + result);
			returnStr = nfsClient.getMountPoint(builder.toString().replace("\\", "/"));
			if (returnStr == null || returnStr.equals("ERROR")) {
				Log.e("ADD1165", "mountPath 2 returnStr: " + returnStr);
				return null;
			} else {
				return returnStr + File.separator + netItem.get(Nfs.ABSOLUTE_PATH);
			}
		} else {
			Log.e("ADD1165", "mountPath returnStr: " + returnStr);
			return returnStr + File.separator + netItem.get(Nfs.ABSOLUTE_PATH);
		}

	}

	public void deleteShortcut(Map map) {
		final String ip = (String) map.get(Nfs.SERVER_IP);
		final String workPath = (String) map.get(Nfs.WORK_PATH);
		final String ablolutePath = (String) map.get(Nfs.ABSOLUTE_PATH);

		DBHelper dbHelper = DBHelper.getInstance(mActivity);
		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		sqlite.delete(Nfs.TABLE_NAME, Nfs.SERVER_IP + "=? and " + Nfs.WORK_PATH + "=? and " + Nfs.ABSOLUTE_PATH + "=?",
		        new String[] { ip, workPath, ablolutePath });
		
		
		//兼容新版(自动版)
		String tempPath = NfsController.getInstance().mountPathSync(map);
		LogUtil.i("folder","影库编辑路径==="+tempPath);
		Set<String> path = sp.get("path");
		Iterator<String> iterator = path.iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			if(string.contains(tempPath)){
				int indexOf = string.indexOf(";");
				int pathNum = Integer.parseInt(string.substring(indexOf+1, string.length()));
				movieDaoImpl.deleteMovies(pathNum);
				path.remove(string);
				sp.put("path", path);
				return;
			}
		}
		
		

		EventBus.getDefault().post(
		        new EventDBChange(map, (String) map.get(Nfs.SELFDEFINE_NAME), EventDBChange.EVENT_DB_DELETE));
	}

	/**
	 * 添加影库及编辑影库
	 * 
	 * @param map
	 */
	public void addShortcut(Map map) {
		final String ip = (String) map.get(Nfs.SERVER_IP);
		final String workPath = (String) map.get(Nfs.WORK_PATH);
		final String absolutePath = (String) map.get(Nfs.ABSOLUTE_PATH);
		final String selfDefineName = (String) map.get(Nfs.SELFDEFINE_NAME);

		DBHelper dbHelper = DBHelper.getInstance(mActivity);
		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		Cursor cursor = sqlite.query(Nfs.TABLE_NAME, new String[] { Nfs.ID }, Nfs.SERVER_IP + " =? and "
		        + Nfs.WORK_PATH + " = ? and " + Nfs.ABSOLUTE_PATH + " = ?",
		        new String[] { ip, workPath, absolutePath }, null, null, null);
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(Nfs.ID));

			ContentValues values = new ContentValues();
			values.put(Nfs.SERVER_IP, ip);
			values.put(Nfs.WORK_PATH, workPath);
			values.put(Nfs.ABSOLUTE_PATH, absolutePath);
			values.put(Nfs.SELFDEFINE_NAME, selfDefineName);
			sqlite.update(Nfs.TABLE_NAME, values, Nfs.ID + "=?", new String[] { String.valueOf(id) });
			cursor.close();
			EventBus.getDefault().post(new EventDBChange(map, selfDefineName, EventDBChange.EVENT_DB_UPDATE));
			return;
		}

		ContentValues values = new ContentValues();
		values.put(Nfs.SERVER_IP, ip);
		values.put(Nfs.WORK_PATH, workPath);
		values.put(Nfs.ABSOLUTE_PATH, absolutePath);
		values.put(Nfs.SELFDEFINE_NAME, selfDefineName);
		sqlite.insert(Nfs.TABLE_NAME, Nfs.ID, values);
		EventBus.getDefault().post(new EventDBChange(map, selfDefineName, EventDBChange.EVENT_DB_ADD));
	}

	public boolean hasAdded(Map map) {
		final String ip = (String) map.get(Nfs.SERVER_IP);
		final String workPath = (String) map.get(Nfs.WORK_PATH);
		final String absolutePath = (String) map.get(Nfs.ABSOLUTE_PATH);

		DBHelper dbHelper = DBHelper.getInstance(mActivity);
		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		Cursor cursor = null;
		try {
			cursor = sqlite.query(Nfs.TABLE_NAME, new String[] { Nfs.ID }, Nfs.SERVER_IP + " =? and " + Nfs.WORK_PATH
			        + " = ? and " + Nfs.ABSOLUTE_PATH + " = ?", new String[] { ip, workPath, absolutePath }, null,
			        null, null);
			if (cursor.moveToFirst()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}

		return false;

	}

}
