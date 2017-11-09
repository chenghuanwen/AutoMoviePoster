package com.hisilicon.videocenter.util;

import java.io.File;

import android.util.Log;
import com.hisilicon.android.hidvdinfo.HiDVDInfo;

public class DVDInfo {
	private static final String TAG = "DVDInfo";
	private static boolean DEBUG = false;

	private static synchronized int checkDiscInfo(String pPath) {
		HiDVDInfo hiDVDInfo = new HiDVDInfo();
		hiDVDInfo.openDVD(pPath);
		int _Result = hiDVDInfo.checkDiscInfo();
		hiDVDInfo.closeDVD();
		return _Result;
	}

	public static boolean isDVDFile(String pPath) {
		if (DEBUG) {
			Log.v(TAG, "path is " + pPath);
		}

		File file = new File("/system/lib/libdvdinfo_jni.so");
		if (!file.exists()) {
			return false;
		}

		if (checkDiscInfo(pPath) < 0) {
			return false;
		}

		return true;
	}

}
