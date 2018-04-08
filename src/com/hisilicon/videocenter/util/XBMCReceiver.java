package com.hisilicon.videocenter.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class XBMCReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.i("MusicService","系统广播==="+arg1.getAction().toString()+"context==="+arg0);
	}

}
