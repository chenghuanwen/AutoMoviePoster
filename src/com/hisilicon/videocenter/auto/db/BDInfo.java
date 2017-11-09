package com.hisilicon.videocenter.auto.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import com.explorer.ftp.DBHelper;
import com.hisilicon.videocenter.util.LogUtil;

import android.util.Log;

/**
 * 蓝光文件夹
 * @author Administrator
 *
 */

public class BDInfo {
	
	 private static final String TAG = "BDInfo";

	    private HiBDInfo mHiBDInfo;

	    private boolean DEBUG = false;
	    private String mBDMVPath;//蓝光目录地址

	    public BDInfo() {
	      //  mHiBDInfo = new HiBDInfo();
	    }

	    public synchronized int checkDiscInfo(String pPath) {
	        int _Result = 0;
	        mHiBDInfo.openBluray(pPath);
	        _Result = mHiBDInfo.checkDiscInfo();
	        mHiBDInfo.closeBluray();

	        return _Result;
	    }

	
	
	  public boolean isBDFile(String pPath) {
	      
	           // LogUtil.i("folder", "path is " + pPath);

	        if (!hasBDMVDir(pPath)) {
	            return false;
	        }

	      /*  if (checkDiscInfo(pPath) < 0) {
	            return false;
	        }*/

	        return true;
	    }

	    public boolean hasBDMVDir(String pPath) {
	        File _File = new File(pPath);

	        if (!_File.exists()) {
	            return false;
	        }

	        File[] _Files = _File.listFiles();

	        if (_Files == null) {
	            return false;
	        }

	        for (int i = 0; i < _Files.length; i++) {
	            if (_Files[i].getName().equalsIgnoreCase("BDMV")) {
	            	
	            	mBDMVPath = _Files[i].getAbsolutePath()+File.separator+"STREAM";
	                return true;
	            }
	        }

	        return false;
	    }
	    
	    
		/* * Blu-ray ISO file filter, for maximum video file
		 * CNcomment:过滤蓝光ISO文件，获取最大视频文件
		 */
		public File getMaxFile(List<File> listFile) {
			int temp = 0;
			for (int i = 0; i < listFile.size(); i++) {
				if (listFile.get(temp).length() <= listFile.get(i).length())
					temp = i;
			}
			return listFile.get(temp);
		}
	    
		public String getBDPlayPath(){
			File bdmv = new File(mBDMVPath);
			if(bdmv.exists()){
				
				File maxFile = getMaxFile(Arrays.asList(bdmv.listFiles()));
				return maxFile.getAbsolutePath();
			}
			
			return null;
		}
		
	    
}
