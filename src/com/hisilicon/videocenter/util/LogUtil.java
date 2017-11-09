package com.hisilicon.videocenter.util;

import android.util.Log;

public class LogUtil {
	public static final boolean IS_DEBUG = true;
	
	public static void i(String tag,String msg){
		if(IS_DEBUG){
			Log.i(tag,msg);
		}
	}

}
