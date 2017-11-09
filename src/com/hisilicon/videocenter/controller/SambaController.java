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
import com.hisilicon.android.hinetshare.Jni;
import com.hisilicon.videocenter.util.CommonConstant;
import com.hisilicon.videocenter.util.CommonConstant.Nfs;
import com.hisilicon.videocenter.util.CommonConstant.Samba;

public class SambaController {
	private static SambaController mInstance;
	private Activity mActivity;
	private SharedPreferenceUtil sp;
	private MovieDaoImpl movieDaoImpl;
	private SambaController() {
	}

	public static SambaController getInstance() {
		if (mInstance == null) {
			synchronized (SambaController.class) {
				if (mInstance == null) {
					mInstance = new SambaController();
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
		Cursor cursor = sqlite.query(Samba.TABLE_NAME, new String[] { Samba.ID, Samba.NICK_NAME, Samba.WORK_PATH,
		        Samba.SERVER_IP, Samba.ACCOUNT, Samba.PASSWORD, Nfs.SELFDEFINE_NAME }, null, null, null, null, null);

		String nickName = null;
		String workPath = null;
		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(CommonConstant.Common.TYPE, Samba.TYPE_NAME);
			map.put(Samba.IMAGE, R.drawable.folder_file);

			nickName = cursor.getString(cursor.getColumnIndex(Samba.NICK_NAME));
			workPath = cursor.getString(cursor.getColumnIndex(Samba.WORK_PATH));
			map.put(Samba.NICK_NAME, nickName);
			map.put(Samba.WORK_PATH, workPath);
			map.put(Samba.DISPLAY_PATH, (nickName + File.separator + workPath).replace("\\", "/"));
			map.put(Samba.SERVER_IP, cursor.getString(cursor.getColumnIndex(Samba.SERVER_IP)));
			map.put(Samba.ACCOUNT, cursor.getString(cursor.getColumnIndex(Samba.ACCOUNT)));
			map.put(Samba.PASSWORD, cursor.getString(cursor.getColumnIndex(Samba.PASSWORD)));
			map.put(Samba.SELFDEFINE_NAME, cursor.getString(cursor.getColumnIndex(Samba.SELFDEFINE_NAME)));
			map.put(Samba.SHORT, 1);
			map.put(Samba.MOUNT_POINT, " ");

			list.add(map);
		}
		cursor.close();
		return list;
	}

	public void mountPath(Map item) {
		final Map netItem = item;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String mountPath = mountPathSync(netItem);
				if (mountPath != null) {
					EventBus.getDefault().post(new EventShowMovie(mountPath, true));
				} else {
					EventBus.getDefault().post(new EventShowMovie(null, true));
				}
				
			}
		}).start();
		
	}
	public String getPath(Map item) {
		final String userserver = item.get(Samba.SERVER_IP).toString();
		final String folder_position = item.get(Samba.WORK_PATH).toString().replace("\\", "/");
		final StringBuilder builder = new StringBuilder(userserver);
		builder.append("/").append(folder_position);
		final Jni jni = new Jni();
		String returnStr = jni.getMountPoint(builder.toString());
		if (returnStr == null || returnStr.equals("ERROR")) {
			returnStr = null;
		}
		
		return returnStr;
	}
	
	public String mountPathSync(Map item) {
		final String userserver = item.get(Samba.SERVER_IP).toString();
		final String folder_position = item.get(Samba.WORK_PATH).toString().replace("\\", "/");
		final String username = item.get(Samba.ACCOUNT).toString();
		final String userpass = item.get(Samba.PASSWORD).toString();

		final StringBuilder builder = new StringBuilder(userserver);
		builder.append("/").append(folder_position);
		final Jni jni = new Jni();
		String returnStr = jni.getMountPoint(builder.toString());
		if (returnStr == null || returnStr.equals("ERROR")) {
			int result = jni.UImount(userserver, folder_position, " ", username, userpass);
			Log.e("ADD1165", "mountPath result: " + result);
			returnStr = jni.getMountPoint(builder.toString());
			if (returnStr == null || returnStr.equals("ERROR")) {
				returnStr = null;
			}
		}
		
		Log.e("ADD1165", "mountPath returnStr: " + returnStr);
		return returnStr;
    }

	public void deleteShortcut(Map map) {
		String ip = (String) map.get(Samba.SERVER_IP);
		String workPath = (String) map.get(Samba.WORK_PATH);
		String nickName = (String) map.get(Samba.NICK_NAME);

		DBHelper dbHelper = DBHelper.getInstance(mActivity);
		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		sqlite.delete(Samba.TABLE_NAME, Samba.SERVER_IP + "=? and " + Samba.WORK_PATH + "=? and " + Samba.NICK_NAME
		        + "=?", new String[] { ip, workPath, nickName });

		
		String tempPath = SambaController.getInstance().mountPathSync(map);
		Log.i("folder","影库编辑路径==="+tempPath);
		//兼容新版(自动版)
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
			}
		}
			
		EventBus.getDefault().post(new EventDBChange(map, (String) map.get(Samba.SELFDEFINE_NAME), EventDBChange.EVENT_DB_DELETE));

	}

	public void addShortcut(Map map) {
		String ip = (String) map.get(Samba.SERVER_IP);
		String workPath = (String) map.get(Samba.WORK_PATH);
		String nickName = (String) map.get(Samba.NICK_NAME);
		String selfDefineName = (String) map.get(Samba.SELFDEFINE_NAME);
		String userName = (String) map.get(Samba.ACCOUNT);
		String userPassword = (String) map.get(Samba.PASSWORD);

		DBHelper dbHelper = DBHelper.getInstance(mActivity);
		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();

		Cursor cursor = sqlite.query(Samba.TABLE_NAME, new String[] { Samba.ID }, Samba.NICK_NAME + "=? and "
		        + Samba.WORK_PATH + "=?", new String[] { nickName, workPath }, null, null, null);
		// Already exists, but the user name, password change
		// CNcomment:已存在，但是用户名、密码被改变
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(Samba.ID));
			ContentValues values = new ContentValues();
			values.put(Samba.ACCOUNT, userName);
			values.put(Samba.PASSWORD, userPassword);
			values.put(Samba.SELFDEFINE_NAME, selfDefineName);
			sqlite.update(Samba.TABLE_NAME, values, Samba.ID + "=?", new String[] { String.valueOf(id) });
			EventBus.getDefault().post(new EventDBChange(map, selfDefineName, EventDBChange.EVENT_DB_UPDATE));
		} else {
			ContentValues values = new ContentValues();
			values.put(Samba.NICK_NAME, nickName);
			values.put(Samba.SERVER_IP, ip);
			values.put(Samba.WORK_PATH, workPath);
			values.put(Samba.ACCOUNT, userName);
			values.put(Samba.PASSWORD, userPassword);
			values.put(Samba.SELFDEFINE_NAME, selfDefineName);
			sqlite.insert(Samba.TABLE_NAME, Samba.ID, values);
			EventBus.getDefault().post(new EventDBChange(map, selfDefineName, EventDBChange.EVENT_DB_ADD));
		}

		cursor.close();
		
	}
	
	public boolean hasAdded(Map map) {
		String ip = (String) map.get(Samba.SERVER_IP);
		String workPath = (String) map.get(Samba.WORK_PATH);
		String nickName = (String) map.get(Samba.NICK_NAME);;

		DBHelper dbHelper = DBHelper.getInstance(mActivity);
		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		Cursor cursor = null;
		try {
			cursor = sqlite.query(Samba.TABLE_NAME, new String[] { Samba.ID }, Samba.NICK_NAME + "=? and "
			        + Samba.WORK_PATH + "=?", new String[] { nickName, workPath }, null, null, null);
			
			if (cursor.moveToFirst()) {
				return true;
			} else {
				return false;
			}
        } catch (Exception e) {
	        e.printStackTrace();
        } finally {
        	cursor.close();
        }
		
		return false;

	}

}
