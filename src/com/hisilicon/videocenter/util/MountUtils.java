package com.hisilicon.videocenter.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

public class MountUtils {
	/**
	 * 获取外置SD卡路径
	 * 
	 * @return
	 */
	public static List<String> getSDCardPaths() {
		List<String> sdcardPaths = new ArrayList<String>();
		String cmd = "cat /proc/mounts";
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				String[] temp = TextUtils.split(lineStr, " ");
				// 得到的输出的第二个空格后面是路径
				String result = temp[1];
				// Log.i("directory result:========", result);

				File file = new File(result);
				if ((result.startsWith("/mnt/") || result.startsWith("/storage/"))
						&& !result.startsWith("/mnt/smb/")
						&& !result.startsWith("/mnt/nfsShare/")
						&& file.isDirectory() 
						&& file.canRead()) {
					Log.e("directory can read can write:",
							file.getAbsolutePath());
					// 可读可写的文件夹未必是sdcard，我的手机的sdcard下的Android/obb文件夹也可以得到
					sdcardPaths.add(result);
				}

				// 检查命令是否执行失败。
				if (p.waitFor() != 0 && p.exitValue() == 1) {
					// p.exitValue()==0表示正常结束，1：非正常结束
					Log.e("CommonUtil:getSDCardPath", "命令执行失败!");
				}
			}
			inBr.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("CommonUtil:getSDCardPath", e.toString());

			sdcardPaths.add(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
		}

		optimize(sdcardPaths);
		return sdcardPaths;
	}

	private static void optimize(List<String> sdcaredPaths) {//优化，去掉重复路径、父路径（/mnt/sda    /mnt/sda/sda1）
		if (sdcaredPaths.size() == 0) {
			return;
		}
		int index = 0;
		while (true) {
			if (index >= sdcaredPaths.size() - 1) {
				String lastItem = sdcaredPaths.get(sdcaredPaths.size() - 1);
				for (int i = sdcaredPaths.size() - 2; i >= 0; i--) {
					if (sdcaredPaths.get(i).contains(lastItem)) {
						sdcaredPaths.remove(i);
					}
				}
				return;
			}

			String containsItem = sdcaredPaths.get(index);
			for (int i = index + 1; i < sdcaredPaths.size(); i++) {
				if (sdcaredPaths.get(i).contains(containsItem)) {
					sdcaredPaths.remove(i);
					i--;
				}
			}

			index++;
		}

	}

	/**
	 * 此方法暂时不用
	 * @author Mark_Music
	 *
	 */
	public static class StorageInfo {
		public String path;
		public String state;
		public boolean isRemoveable;

		public StorageInfo(String path) {
			this.path = path;
		}

		public boolean isMounted() {
			return "mounted".equals(state);
		}
	}

	public static List listAvaliableStorage(Context context) {
		List<String> storagges = new ArrayList<String>();
		StorageManager storageManager = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		try {
			Class<?>[] paramClasses = {};
			Method getVolumeList = StorageManager.class.getMethod(
					"getVolumeList", paramClasses);
			getVolumeList.setAccessible(true);
			Object[] params = {};
			Object[] invokes = (Object[]) getVolumeList.invoke(storageManager,
					params);
			if (invokes != null) {
				StorageInfo info = null;
				for (int i = 0; i < invokes.length; i++) {
					Object obj = invokes[i];
					Method getPath = obj.getClass().getMethod("getPath",
							new Class[0]);
					String path = (String) getPath.invoke(obj, new Object[0]);
					Log.e("TEST", "path:" + path);
					info = new StorageInfo(path);
					File file = new File(info.path);
					if ((file.exists()) && (file.isDirectory())
							&& (file.canWrite())) {
						Method isRemovable = obj.getClass().getMethod(
								"isRemovable", new Class[0]);
						String state = null;
						try {
							Method getVolumeState = StorageManager.class
									.getMethod("getVolumeState", String.class);
							state = (String) getVolumeState.invoke(
									storageManager, info.path);
							info.state = state;
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (info.isMounted()) {
							info.isRemoveable = ((Boolean) isRemovable.invoke(
									obj, new Object[0])).booleanValue();
							storagges.add(info.path);
						}
					}
				}
			}
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return storagges;
	}
	
	/**
	 * 获取SATA目录
	 * 
	 * @return
	 */
	public static String getSataPath() {
		try {
			IBinder service = ServiceManager.getService("mount");
			if (service == null) {
				return null;
			}
			
			IMountService mountService = IMountService.Stub.asInterface(service);
			List<android.os.storage.ExtraInfo> mountList = mountService.getAllExtraInfos();

			int size = mountList.size();
			for (int i = 0; i < size; i++) {
				String mountPoint = mountList.get(i).mMountPoint;
				String typeStr = mountList.get(i).mDevType;
				if (typeStr.equals("SATA")) {
					return mountPoint;
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}
	
}
