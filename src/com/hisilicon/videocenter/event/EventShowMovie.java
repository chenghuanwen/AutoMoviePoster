package com.hisilicon.videocenter.event;

/**
 * 即将展示的影库目录
 * @author test
 *
 */
public class EventShowMovie {
	public boolean mResult = false;
	/**
	 * 配置文件所在的目录
	 */
	public String mFolderPath;
	
	public EventShowMovie(String path, boolean result) {
		mFolderPath = path;
		mResult = result;
    }

}
