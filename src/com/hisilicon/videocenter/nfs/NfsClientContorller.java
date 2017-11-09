package com.hisilicon.videocenter.nfs;

import android.os.Handler;
import android.util.Log;

import com.hisilicon.android.hinetshare.HiNfsClientManager;

public class NfsClientContorller {
	private static final String TAG = "NfsClientContorller";
	
	private HiNfsClientManager nfsClient;
	
	private Handler mCallBackHandler;
	
	private String strWorkgrpups;

	/**
	 * 查找nfs服务器
	 */
	public void searchNfsServer() {
		Log.d(TAG, "call_getWorkgroups()");
		if (strWorkgrpups == null || "".equals(strWorkgrpups)
				|| strWorkgrpups.toLowerCase().equals("error")) {
			Log.d(TAG, "2511::call_getWorkgroups()");
			strWorkgrpups = nfsClient.getWorkgroups();
			if (strWorkgrpups.equals("")){
				mCallBackHandler.sendEmptyMessage(NFSParams.NET_ERROR);
			}
				
		}
	}
	
	/**
	 * 挂载服务器
	 */
	public void mountNfsServer() {
		
	}

}
