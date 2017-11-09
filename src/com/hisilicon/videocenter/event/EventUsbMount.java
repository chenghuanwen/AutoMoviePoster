package com.hisilicon.videocenter.event;

/**
 * 数据库发生变化的消息
 * @author test
 *
 */
public class EventUsbMount {
	public String mAction;
	public String mPath;
	
	public EventUsbMount(String action, String path) {
		mAction = action;
		mPath = path;
    }

}
