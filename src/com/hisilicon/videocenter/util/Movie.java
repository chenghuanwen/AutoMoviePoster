package com.hisilicon.videocenter.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import android.text.TextUtils;

public class Movie implements Serializable {
	private static final long serialVersionUID = 1L;

	public String mNum; // 电影编号
	public String mName; // 电影名称
	public String mArea; // 地区
	public String mType; // 类型
	public String mDirector; // 导演
	public String mActors; // 演员
	public String mDuration; // 时长
	public String mResolution; // 分辨率
	public String onLineTime; // 上映时间
	public String mLanguage; // 配音语言
	public String mSubtitles; // 字幕语言
	public String rate;//评分
	public String picPath;//海报路径
	public String moviePath;//电影路径
	public String info;
	public int pathNum ;//对应的路径编码，便于删除数据库数据

	public String mConfigDir; // 配置文件(justlink_media_index.txt)所在目录

	public String mMoviceDir; // 
	
	public int isAuto;//是否是自动添加(0:本地，1：自动)

	public boolean isTypeOf(String type) {
		if (mType != null && mType.contains(type)) {
			return true;
		}

		return false;
	}

	public String getTitle() {
		return mName;
	}

	public void setTitle(String title) {
		mName = title;
	}

	public String getMovieDir() {
		if (mMoviceDir == null) {
			mMoviceDir = mConfigDir + File.separator + "Videos" + File.separator + mName;
		}
		
		return mMoviceDir;
	}
	
	public boolean isMultiEpisode() {//判断是否是有多集的剧集（上下部，电视剧等）
		File file = new File(getMovieDir() + File.separator + mNum);
		if (file.exists() && file.isDirectory()) {
			return false;
		} else {
			file = new File(getMovieDir());
			String[] fileNames = file.list(new FilenameFilter() {
				@Override
				public boolean accept(File arg0, String arg1) {
					if (arg1.startsWith(mNum + "_")) {
						return true;
					} else {
						return false;
					}
				}
			});

			if (fileNames != null && fileNames.length > 0) {
				return true;
			} else {
				return false;
			}

		}
	}

	public String getEpisodePath(int index) {
		final String episodeName = mNum + "_" + index + "_";
		File file = new File(getMovieDir());
		if (file.exists() && file.isDirectory()) {
			String[] files = file.list(new FilenameFilter() {
				@Override
				public boolean accept(File arg0, String arg1) {
					if (arg1.startsWith(episodeName)) {
						return true;
					} else {
						return false;
					}

				}
			});

			if (files != null && files.length > 0) {
				return searchMoviceFileByDir(getMovieDir() + File.separator + files[0]);
			}
		}

		return getMoviePath();
	}

	public String getMoviePath() {
		if(1==isAuto()){
			return moviePath;
		}else{
			return searchMoviceFileByDir(getMovieDir() + File.separator + mNum);	
		}
		
	}

	private String searchMoviceFileByDir(String director) {
		File file = new File(director);
		if (file.exists() && file.isDirectory()) {
			String[] files = file.list(new FilenameFilter() {
				@Override
				public boolean accept(File arg0, String arg1) {
					if (arg1.contains(".")) {
						return true;
					} else {
						return false;
					}

				}
			});

			for (String fileName : files) {
				if (MovieUtils.isVideoFile(fileName)) {
					return file.getPath() + File.separator + fileName;
				}
			}
		}

		return file.getPath();
	}

	public void setTypes(String types) {
		this.mType = types;
	}

	public String getPicPath() {
		if(isAuto()==1){
			return picPath;	
		}else{
			return getMovieDir() + File.separator + mNum + ".jpg";
		}
		
	}

	public boolean compareTitle(String info) {
		if (TextUtils.isEmpty(mName))
			return false;

		if (mName.contains(info)) {
			return true;
		}

		return false;
	}

	public String getOnLineTime() {
		return onLineTime;
	}

	public void setOnLineTime(String onLineTime) {
		this.onLineTime = onLineTime;
	}

	public String getSynopsis() {
		if(isAuto()==1){
			return info;
		}else{
			String synPath = getMovieDir() + File.separator + mNum + ".txt";
			File file = new File(synPath);
			try {
				return FileUtils.readFileToString(file, "UTF-8");
			} catch (Exception e) {
				return "";
			}	
		}
		

	}

	public int getEpisodeCount() {
		File file = new File(getMovieDir());
		String[] files = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg1.contains(mNum + "_")) {
					return true;
				} else {
					return false;
				}

			}
		});
		return files.length;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public void setMoviePath(String moviePath) {
		this.moviePath = moviePath;
	}

	public String getmArea() {
		return mArea;
	}

	public void setmArea(String mArea) {
		this.mArea = mArea;
	}

	public String getmType() {
		return mType;
	}

	public void setmType(String mType) {
		this.mType = mType;
	}

	public String getmDirector() {
		return mDirector;
	}

	public void setmDirector(String mDirector) {
		this.mDirector = mDirector;
	}

	public String getmDuration() {
		return mDuration;
	}

	public void setmDuration(String mDuration) {
		this.mDuration = mDuration;
	}

	public int isAuto() {
		return isAuto;
	}

	public void setAuto(int isAuto) {
		this.isAuto = isAuto;
	}

	public String getmActors() {
		return mActors;
	}

	public void setmActors(String mActors) {
		this.mActors = mActors;
	}

	public String getInfo() {
		return info;
	}

	public int getPathNum() {
		return pathNum;
	}

	public void setPathNum(int pathNum) {
		this.pathNum = pathNum;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	
	 
}