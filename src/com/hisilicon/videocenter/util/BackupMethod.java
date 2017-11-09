package com.hisilicon.videocenter.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BackupMethod {
	private final String MOUNT_LIST_FLE = "mount_list.xml";
	private final String PROPERTY_FILE = "properties.txt";
	
//	private void initLocalProperty() {
//	try {
//		copyAssetsFile(PROPERTY_FILE, mFile.getAbsolutePath());
//		
//		String str = null;
//		BufferedReader reader = new BufferedReader(new FileReader(new File(
//				mFile, PROPERTY_FILE)));
//		while ((str = reader.readLine()) != null) {
//			String[] s = str.split("=");
//			if (s.length != 2)
//				continue;
//
//			String key = s[0].trim();
//			String value = s[1].trim();
//			if (key.equals(Common.PROP_SHOW_LOCAL_PLAY)) {
//				showLocalPlay = value.equals("true");
//			} else if (key.equals(Common.PROP_ROOT_PATH)) {
//				rootPath = value;
//			}
//		}
//		reader.close();
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
//	
//	Log.i(TAG, "showLocalPlay:"+showLocalPlay + " rootPath"+rootPath);
//	
//}
	
//	private List<String> getAllMountList() {
//		ArrayList<String> list = new ArrayList<String>();
//		// 本地挂载点
//		List<String> localList = Common.getLocalMountPoint(this);
//		if (null != localList && localList.size() > 0) {
//			list.addAll(localList);
//		}
//
//		// 拷贝网络挂载点文件
//		copyAssetsFile(MOUNT_LIST_FLE, mFile.getAbsolutePath());
//
//		try {
//			List<String> netList = Common.getNetMountPoint(new File(mFile
//					.getAbsolutePath(), MOUNT_LIST_FLE));
//			if (null != netList && netList.size() > 0) {
//				list.addAll(netList);
//			}
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		
//
//		return list;
//	}

	
//	private void copyAssetsFile(String fileName, String savePath) {
//		String filename = savePath + "/" + fileName;
//		InputStream is = null;
//		FileOutputStream fos = null;
//		try {
//			//if (!(new File(filename)).exists()) {
//				is = getResources().getAssets().open(fileName);
//				fos = new FileOutputStream(filename);
//				byte[] buffer = new byte[7168];
//				int count = 0;
//				while ((count = is.read(buffer)) > 0) {
//					fos.write(buffer, 0, count);
//				}
//
//			//}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (null != fos)
//					fos.close();
//				if (null != is)
//					is.close();
//			} catch (Exception e2) {
//				e2.printStackTrace();
//			}
//		}
//	}


}
