package com.hisilicon.videocenter.auto.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.webkit.WebChromeClient.CustomViewCallback;

import com.hisilicon.videocenter.auto.OnQueryLocalDataFinishedListener;
import com.hisilicon.videocenter.controller.DataManager;
import com.hisilicon.videocenter.util.LogUtil;
import com.hisilicon.videocenter.util.Movie;

public class MovieDaoImpl implements MovieDao {
	private DatabaseManager dataManager;
	private SQLiteDatabase db;
	
	public MovieDaoImpl(Context context){
		dataManager = DatabaseManager.getInstance();
		dataManager.setContext(context);
	}

	@Override
	public  void queryLocalMovies(OnQueryLocalDataFinishedListener listener) {
		db = dataManager.getDatabase();
		List<Movie> list = new ArrayList<>();
		// TODO Auto-generated method stub
		try {
			
		Cursor cursor = db.query("movie", null, null, null, null, null, null);
		if(cursor!=null && cursor.moveToFirst()){
			do{
				Movie movie = new Movie();
				movie.setTitle(cursor.getString(cursor.getColumnIndex("mname")));
				movie.setRate(cursor.getString(cursor.getColumnIndex("rate")));
				movie.setOnLineTime(cursor.getString(cursor.getColumnIndex("onlinetime")));
				movie.setInfo(cursor.getString(cursor.getColumnIndex("info")));
				movie.setmDuration(cursor.getString(cursor.getColumnIndex("mduration")));
				movie.setmType(cursor.getString(cursor.getColumnIndex("mtype")));
				movie.setmArea(cursor.getString(cursor.getColumnIndex("marea")));
				movie.setmDirector(cursor.getString(cursor.getColumnIndex("mdirector")));
				movie.setmActors(cursor.getString(cursor.getColumnIndex("mactors")));
				movie.setPicPath(cursor.getString(cursor.getColumnIndex("picpath")));
				movie.setMoviePath(cursor.getString(cursor.getColumnIndex("moviepath")));
				movie.setAuto(cursor.getInt(cursor.getColumnIndex("isauto")));
				list.add(movie);
			}while(cursor.moveToNext());
		}
		cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("folder","数据库查询异常===="+e.toString());
		}
		Log.i("folder","查询数据库数量===="+list.size());
		listener.onQueryLocalDataFinish(list);
	}

	@Override
	public void addMovies(List<Movie> movies) {
		db = dataManager.getDatabase();
		// TODO Auto-generated method stub
		for (Movie movie : movies) {
			ContentValues values = new ContentValues();
			values.put("mname", movie.getTitle());
			values.put("rate", movie.getRate());
			values.put("onlinetime", movie.getOnLineTime());
			values.put("info", movie.getInfo());
			values.put("mduration", movie.getmDuration());
			values.put("mtype", movie.getmType());
			values.put("marea", movie.getmArea());
			values.put("mdirector", movie.getmDirector());
			values.put("mactors", movie.getmActors());
			values.put("picpath", movie.getPicPath());
			values.put("moviepath", movie.getMoviePath());
			values.put("isauto", movie.isAuto());
			values.put("pathnum",movie.getPathNum());
			long insert = db.insert("movie", null, values);
			if(insert>0){
				LogUtil.i("folder","成功添加到数据库.....");
			}else{
				LogUtil.i("folder","数据库保存失败.....");
			}
		}
		
		
	}

	@Override
	public void deleteMovies(int num) {
		db = dataManager.getDatabase();
		// TODO Auto-generated method stub
		try {
			db.delete("movie", "pathnum=?", new String[] {num+"".trim()});		
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("folder","数据删除存失败.....");
		}
		
		
		
	}

}
