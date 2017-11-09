package com.hisilicon.videocenter.auto;

import java.util.ArrayList;

import com.hisilicon.videocenter.util.MovieBaseInfo;

public interface OnScanFinishedListener {
	void onScanFinished(ArrayList<MovieBaseInfo> bases);
}
