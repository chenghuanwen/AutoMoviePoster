package com.hisilicon.videocenter.event;

import java.util.Map;

/**
 * 数据库发生变化的消息
 * @author test
 *
 */
public class EventDBChange {
	public static final int EVENT_DB_ADD = 0;
	public static final int EVENT_DB_DELETE = 1;
	public static final int EVENT_DB_UPDATE = 2;
	
	public Map<String, Object> mDeviceItemMap;
	public String mSelfName;
	public int mEventType;
	
	public EventDBChange(Map<String, Object> map, String name, int type) {
		mDeviceItemMap = map;
		mSelfName = name;
		mEventType = type;
    }

}
