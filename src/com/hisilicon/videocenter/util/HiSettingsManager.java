package com.hisilicon.videocenter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class HiSettingsManager {
	private static HiSettingsManager mInstance;
	private SharedPreferences mSP;

	public static HiSettingsManager getInstance() {
		if (mInstance == null) {
			synchronized (HiSettingsManager.class) {
				if (mInstance == null) {
					mInstance = new HiSettingsManager();
				}
			}
		}
		return mInstance;
	}

	public HiSettingsManager() {
	}

	public static final String KEY_SHARE_PREFERENCE = "hi_settings_sp";
	public static final String KEY_SP_LANGUAGE = "hi_settings_language";
	public static final String KEY_SP_LOCAL_BROWSER = "hi_settings_local_browser";

	public void initSharedPreferences(Context context) {
		mSP = context.getSharedPreferences(KEY_SHARE_PREFERENCE, Context.MODE_MULTI_PROCESS);
	}

	public void setLanguageIndex(int index) {
		mSP.edit().putInt(KEY_SP_LANGUAGE, index).commit();
	}

	public int getLanguageIndex() {
		return mSP.getInt(KEY_SP_LANGUAGE, 0);
	}

	public void setLocalBrowserIndex(int index) {
		mSP.edit().putInt(KEY_SP_LOCAL_BROWSER, index).commit();
	}

	public int getLocalBrowserIndex() {
		return mSP.getInt(KEY_SP_LOCAL_BROWSER, 0);
	}
}
