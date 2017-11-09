package com.hisilicon.videocenter.auto.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.MGF1ParameterSpec;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.auto.GetSataPathUtil;
import com.hisilicon.videocenter.util.LogUtil;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;

public class DatabaseManager {
	private static DatabaseManager mDatabaseManager;
	private Context context;
	private final int BUFFER_SIZE = 1024*1024;
	private final static String PACKEGE_NAME = "com.hisilicon.videocenter";
	private final static String DATA_NAME = "moviebar.db";
	private  String LOCAL_PATH;
	//private final static String LOCAL_PATH = "/mnt/sda/sda2/moviebar.db";
	private GetSataPathUtil getSataPathUtil;
	public static StringBuilder sb = new StringBuilder();
	private DatabaseManager(){
		
	}
	
	
	
	public static DatabaseManager getInstance(){
		if(mDatabaseManager == null){
			synchronized (DatabaseManager.class) {
				if(mDatabaseManager == null)
					mDatabaseManager = new DatabaseManager();
					
			}
		}
		return mDatabaseManager;
	}
	
	
	public void setContext(Context context){
		this.context = context;
	getSataPathUtil = new GetSataPathUtil(context);
	LOCAL_PATH = getSataPathUtil.getSataPath()+"/JSLMovie/moviebar.db";
		
	}
	
	/**
	 * @return
	 */
	public SQLiteDatabase getDatabase(){
		sb.append("/data").append(Environment.getDataDirectory().getAbsolutePath())
		.append("/").append(PACKEGE_NAME).append("/").append("databases");
		String string = sb.toString();
		sb.setLength(0);
		return onpenDatabase(string);
	}

	/**
	 * 数据库创建兼容老版本：老版本基础数据库放在硬盘，新版本放在raw
	 * @param dbfile
	 * @return
	 */
	private SQLiteDatabase onpenDatabase(String dbfile) {
		LogUtil.i("folder","数据库路径==="+dbfile);
		// TODO Auto-generated method stub
		File database = new File(dbfile);
		if(!database.exists())
			database.mkdirs();
		
		File dbFile = new File(dbfile+File.separator+DATA_NAME);
		File localFile = new File(LOCAL_PATH);
		InputStream in  = null;
		
			try {
				if(localFile.exists()){
					 in = new FileInputStream(localFile);			
				}else{
					
					  in = this.context.getResources().openRawResource(R.raw.moviebar);
				}
				
				
				if(!(dbFile.exists())){
					dbFile.createNewFile();
				FileOutputStream out = new FileOutputStream(dbFile);
				int b = 0;
				byte[] buff = new byte[BUFFER_SIZE];
				while((b=in.read(buff)) > 0){
					out.write(buff, 0, b);
				}
				out.flush();
				out.close();
				in.close();
				LogUtil.i("folder","数据库复制完成===");
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogUtil.i("folder","数据库不存在===");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogUtil.i("folder","数据库复制异常==="+e.toString());
				if(dbFile.exists())//重新複製
					dbFile.delete();
				onpenDatabase(dbfile);
			}
		
		
		return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
	}
}
