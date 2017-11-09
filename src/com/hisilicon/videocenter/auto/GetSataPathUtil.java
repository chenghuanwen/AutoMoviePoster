package com.hisilicon.videocenter.auto;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.hisilicon.videocenter.util.LogUtil;

public class GetSataPathUtil {
	private static final String TAG = "folder";
	private String localStorePath;
	private Context mContext;
	public GetSataPathUtil(Context context){
		mContext = context;
	}

	public String getSataPath(){
		List<StorageInfo> list = GetMountInfo.listAllStorage(mContext);
		for (StorageInfo info : list) {
			Log.e(TAG, info.toString());
			if (!info.toString().contains("emulated")) {	
				String name = info.getName();		
				
				if("sda".equals(name) || "sdb".equals(name) || "sdc".equals(name)){
					File file2 = new File(info.getPath());
					file2.list(new FilenameFilter() {

						@Override
						public boolean accept(File arg0, String arg1) {
							// TODO Auto-genera 7 ted method stub
							if(arg1.contains("jlink") || arg1.contains("RECYCLE.BIN")){
								try {
									LogUtil.i("folder","==="+arg1+".path=="+arg0.getCanonicalPath());
									localStorePath = arg0.getCanonicalPath();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return true;
							}
							return false;
						}
					});
					
				}
				
			}

		}
		
		if(TextUtils.isEmpty(localStorePath))
			localStorePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		return localStorePath;
	}
	
	
}
