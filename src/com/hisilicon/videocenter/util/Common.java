package com.hisilicon.videocenter.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;


import com.hisilicon.videocenter.R;
import com.hisilicon.android.hinetshare.HiNfsClientManager;
import com.hisilicon.android.hinetshare.Jni;

public class Common {

	public static final String PACKAGE_VIDEO_PLAYER = "com.hisilicon.android.videoplayer";

	public static final String ACTIVITY_VIDEO_PLAYER = "com.hisilicon.android.videoplayer.activity.MediaFileListService";

	public static final String PACKAGE_FILE_MANAGER = "com.explorer";

	public static final String FILE_MOVIE_LIST = "movie_list.txt";

	public static final String FILE_MOVIE_INFO = "movie_info.xml";

	public static final String FILE_TYPE_INFO = "type_info.xml";

	public static final String FILE_MOVIE_IMAGE = "folder.jpg";

	public static final String EXTRA_MOVIE = "movie";

	public static final String PROP_SHOW_LOCAL_PLAY = "show_local_play";

	public static final String PROP_ROOT_PATH = "root_path";

	public static final String PROP_TYPE = "type";

	public static final int SUCCESS = 0;

	private static final String TAG = "Common";

	public static List<String> getLocalMountPoint(Context context) {
		List<String> localMountList = new ArrayList<String>();
		if (null != context) {
			String[] mountType = context.getResources().getStringArray(
					R.array.mountType);
			MountInfo info = new MountInfo(context);
			for (int j = 0; j < mountType.length; j++) {
				for (int i = 0; i < info.index; i++) {
					if (info.type[i] == j && info.path[i] != null) {
						if (info.path[i].contains("/mnt")
								|| info.path[i].contains("/storage")) {
							localMountList.add(info.path[i]);
						}
					}
				}
			}
		}
		return localMountList;
	}

	public static List<String> getNetMountPoint(File file) {
		List<String> netMountList = new ArrayList<String>();

		List<Mount> netMounts = getNetMountList(file);
		if (null != netMounts && netMounts.size() > 0) {
			for (int i = 0; i < netMounts.size(); i++) {
				String mountPath = null;
				Mount mount = netMounts.get(i);
				if (Mount.TYPE_NFS == mount.getType()) {
					mountPath = mountNFS(mount);
				} else if (Mount.TYPE_SAMBA == mount.getType()) {
					mountPath = mountSamba(mount);
				}
				if (!TextUtils.isEmpty(mountPath)) {
					netMountList.add(mountPath);
				}
			}
		}
		return netMountList;

	}

	public static List<Mount> getNetMountList(File file) {
		if (null == file || !file.exists() || !file.canRead())
			return null;
		List<Mount> mounts = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(fis, "UTF-8");
			Mount mount = null;

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					mounts = new ArrayList<Mount>();
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("Mount")) {
						mount = new Mount();
						break;
					} else if (parser.getName().equals("type")) {
						eventType = parser.next();
						mount.setType(Integer.valueOf(parser.getText().trim()));
						break;
					} else if (parser.getName().equals("address")) {
						eventType = parser.next();
						mount.setAddress(parser.getText().trim());
						break;
					} else if (parser.getName().equals("workpath")) {
						eventType = parser.next();
						mount.setWorkpath(parser.getText().trim());
						break;
					} else if (parser.getName().equals("user")) {
						eventType = parser.next();
						mount.setUser(parser.getText().trim());
						break;
					} else if (parser.getName().equals("password")) {
						eventType = parser.next();
						mount.setPassword(parser.getText().trim());
						break;
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("Mount")) {
						mounts.add(mount);
						mount = null;
					}
					break;
				}
				eventType = parser.next();
			}

			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mounts;
	}

	private static String mountNFS(Mount mount) {
		if (null != mount) {
			String address = mount.getAddress();
			String workpath = mount.getWorkpath();

			HiNfsClientManager client = new HiNfsClientManager();
			int result = client.mount(address , workpath);
			if (SUCCESS == result) {
				return client.getMountPoint(address + ":" + workpath);
			}
		}
		return null;
	}

	private static String mountSamba(Mount mount) {
		if (null != mount) {
			String address = mount.getAddress();
			String workpath = mount.getWorkpath();
			String mountpoint = " ";
			String user = mount.getUser();
			String password = mount.getPassword();

			Jni jni = new Jni();
			int result = jni.UImount(address, workpath, mountpoint, user,
					password);
			if (SUCCESS == result) {
				return jni.getMountPoint(address + "/" + workpath);
			}
		}
		return null;
	}

	public static MovieInfo getMovieInfo(File file) {
		if (null == file || !file.exists() || !file.canRead())
			return null;

		MovieInfo info = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(fis, "UTF-8");

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					info = new MovieInfo();
					break;

				case XmlPullParser.START_TAG:
					if (parser.getName().equals("title")) {
						eventType = parser.next();
						String title = parser.getText().trim();
						if (TextUtils.isEmpty(title)) {
							title = file.getParent();
						}
						info.setTitle(title);
					} else if (parser.getName().equals("director")) {
						eventType = parser.next();
						info.setDirector(parser.getText().trim());
					} else if (parser.getName().equals("performer")) {
						eventType = parser.next();
						info.setPerformer(parser.getText().trim());
					} else if (parser.getName().equals("type")) {
						eventType = parser.next();
						info.setType(parser.getText().trim());
					} else if (parser.getName().equals("area")) {
						eventType = parser.next();
						info.setArea(parser.getText().trim());
					} else if (parser.getName().equals("duration")) {
						eventType = parser.next();
						info.setDuration(parser.getText().trim());
					} else if (parser.getName().equals("synopsis")) {
						eventType = parser.next();
						info.setSynopsis(parser.getText().trim());
					}
					break;
				}
				eventType = parser.next();
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	private static final String MEDIA_CONFIG_FILE_NAME = "justlink_media_index.txt";
	
	/**
	 * 扫描一个盘下的至多二级目录下的配置文件
	 * @return
	 */
	private static ArrayList<String> getConfigFile(String mountDisk) {
		ArrayList<String> list = new ArrayList();
		File diskFile = new File(mountDisk);
		if (!diskFile.isDirectory()) {
		//	Log.i(TAG, "mountDisk is file");
			return null;
		}
		
		String tempPath = null;
		File tempFile = null;
		File[] files = diskFile.listFiles();
		for (File file:files) {
			if (file.isDirectory()) {
				tempPath = file.getPath() + File.separator + MEDIA_CONFIG_FILE_NAME;
				tempFile = new File(tempPath);
				if (tempFile.exists() && !list.contains(tempPath)) {
					list.add(tempPath);
				}
			} else if (MEDIA_CONFIG_FILE_NAME.equals(file.getName())) {
				if (!list.contains(tempPath)) {
					list.add(tempPath);
				}
			}

		}
		
		return list;
		
	}
	
	public static List<String> getAllConfigFolders (List<String> mountList) {
		List<String> allFolders = new ArrayList<String>();
		for (String mountDisk : mountList) {
			allFolders.addAll(getConfigFolders(mountDisk));
		}
		
		return allFolders;
		
	}
	
	/**
	 * 获取至多二级目录下的配置文件路径
	 * @param mountDisk
	 * @return
	 */
	public static ArrayList<String> getConfigFolders(String mountDisk) {
		ArrayList<String> list = new ArrayList();
		File diskFile = new File(mountDisk);
		if (!diskFile.isDirectory()) {
			//Log.i(TAG, "mountDisk is file");
			return null;
		}
		
		String tempPath = null;
		File tempFile = null;
		File[] files = diskFile.listFiles();
		for (File file:files) {
			if (file.isDirectory()) {
				tempPath = file.getPath() + File.separator + MEDIA_CONFIG_FILE_NAME;
				tempFile = new File(tempPath);
				if (tempFile.exists() && !list.contains(tempPath)) {
					list.add(file.getPath());
				}
			} else if (MEDIA_CONFIG_FILE_NAME.equals(file.getName())) {
				if (!list.contains(tempPath)) {
					list.add(file.getParent());
				}
			}

		}
		
		return list;
		
	}
	
	public static ArrayList<Movie> parseFolder(String configFolder) {
		if (TextUtils.isEmpty(configFolder)) {
			return null;
		}
		
		String configFile = configFolder + File.separator + MEDIA_CONFIG_FILE_NAME;
		try {
	        return parseMovies(configFile);
        } catch (IOException e) {
        	e.printStackTrace();
	        return null;
        }
	}
	
	private static ArrayList<Movie> parseMovies(String configFile) throws IOException {
		ArrayList<Movie> movies = new ArrayList<Movie>();
		File cfgfile = new File(configFile);
		
		if (cfgfile.exists() && cfgfile.canRead()) {
			Log.e("listFile:", cfgfile.getAbsolutePath());
			BufferedReader reader = new BufferedReader(new FileReader(cfgfile));
			//reader.readLine(); //忽略掉第一行
			String line = null;
			while ((line = reader.readLine()) != null) {
				
				String[] infos = line.split("\\|");
				//LogUtil.i("line:", line+"====length"+infos.length);
				if (infos.length < 10) {
					continue;
				}
				
				Movie movie = new Movie();
				movie.mNum = infos[1].trim();
				movie.mName = infos[2].trim();
				//movie.mPath = infos[2].trim();
				movie.mArea = infos[4].trim();
				movie.mType = infos[5].trim();
				movie.mDirector = infos[6].trim();
				movie.mActors = infos[7].trim();
				movie.mDuration = infos[8].trim();
				movie.mResolution = infos[9].trim();
				movie.onLineTime = infos[10].trim();
				movie.mLanguage = infos[11].trim();
				movie.mSubtitles = infos[12].trim();
				movie.mConfigDir = cfgfile.getParent();
				movies.add(movie);
			}
			reader.close();
			reader = null;
		}
		//LogUtil.i("line","本地电影数量==="+movies.size());
		return movies;
	}
	
	public static List<Movie> getAllMovies (List<String> mountList) {
		List<Movie> allMovies = new ArrayList<Movie>();
		for (String mountDisk : mountList) {
			ArrayList<String> configList = getConfigFile(mountDisk);
			for (String configFile : configList) {
				try {
	                allMovies.addAll(parseMovies(configFile));
                } catch (IOException e) {
	                e.printStackTrace();
                }
			}
			
		}
		
		return allMovies;
		
	}

	public static List<String> getAllTypes(List<String> mountList) {
		List<String> allTypes = new ArrayList<String>();
		for (String mountDisk : mountList) {
			ArrayList<String> configList = getConfigFile(mountDisk);
			for (String configFile : configList) {
				try {
					allTypes.addAll(parseType(configFile));
                } catch (Exception e) {
	                e.printStackTrace();
                }
			}
			
		}
		
		return allTypes;
	}

	private static List<String> parseType(String configFile) {
		List<String> types = new ArrayList<String>();
	    File typeFile = new File(configFile);
	    String rootPath = typeFile.getParent();
	    typeFile = new File(rootPath, FILE_TYPE_INFO);

	    if (!typeFile.exists() || !typeFile.canRead()) {
	    	return types;
	    }

	    try {
	    	FileInputStream fis = new FileInputStream(typeFile);
	    	XmlPullParser parser = Xml.newPullParser();
	    	parser.setInput(fis, "UTF-8");

	    	int eventType = parser.getEventType();
	    	while (eventType != XmlPullParser.END_DOCUMENT) {
	    		switch (eventType) {
	    		case XmlPullParser.START_DOCUMENT:
	    			break;

	    		case XmlPullParser.START_TAG:
	    			if (parser.getName().equals("type")) {
	    				eventType = parser.next();
	    				String type = parser.getText().trim();
	    				if (!TextUtils.isEmpty(type) && !types.contains(type)) {
	    					types.add(type);
	    				}
	    				break;
	    			}
	    		}
	    		eventType = parser.next();
	    	}
	    	fis.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return types;
    }

}