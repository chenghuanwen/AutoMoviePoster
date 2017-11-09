package com.hisilicon.videocenter.videoinfo;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ReadVideoMessages {
	private  String path = "/mnt/sdb/sdb2/justlinkMedia/Videos";
	private String musicpath = "/mnt/sdcard/Music/testaudio.mp3";

	/**
	 * 得到视频的大小
	 * 
	 * @param f
	 *            文件
	 * @return 视频的大小
	 */
	public static String getFileSize(File f) {
		// 保留两位小数
		DecimalFormat df = new DecimalFormat(".##");
		// 得到视频的长度
		Long long1 = f.length();
		String size = "";
		long G = 1024 * 1024 * 1024;
		long M = 1024 * 1024;
		long K = 1024;
		// 视频大小超过G、超过M不超过G、超过K不超过M
		if (long1 / G >= 1) {
			size = df.format((double) long1 / G) + "G";
		} else if (long1 / M >= 1) {
			size = df.format((double) long1 / M) + "M";
		} else if (long1 / K >= 1) {
			size = df.format((double) long1 / K) + "K";
		} else {
			size = long1 + "B";
		}
		// System.out.println(time);
		return size;

	}

	/**
	 * 得到视频的长度
	 * 
	 * @param f
	 *            文件
	 * @return 视频的长度
	 */
	public static String getVideoTime(File f) {
		String time = "";
		//新建编码器对象
		
		try {
			Encoder encoder = new Encoder();
			//得到多媒体视频的信息
			MultimediaInfo m = encoder.getInfo(f);
			//得到毫秒级别的多媒体是视频长度
			long ls = m.getDuration();
			//转换为分秒
			time = ls / 60000 + "分" + (ls - (ls / 60000 * 60000)) / 1000 + "秒";
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("MV","error=="+e.toString());
		}

		return time;

	}

	// 显示目录的方法
	/**
	 * 得到视频所有的信息
	 * 
	 * @param file
	 *            文件夹 or 文件
	 * @return 视频的信息
	 */
	private List<VideoName> videoNames = new ArrayList<VideoName>();
	// System.out.println( file.getAbsolutePath());
	private  String time = "";
	private String size = "";
	public  List<VideoName> getAllMessage(File file) {
		
		// 判断传入对象是否为一个文件夹对象
		if (!file.isDirectory()) {
			String name = file.getName();
			Log.i("MV","文件名=="+name);
			if(!file.getName().contains("txt") && !file.getName().contains("jpg"))
				getMovieInfo(file);
			//System.out.println("你输入的不是一个文件夹，请检查路径是否有误！！");
			Log.i("MV","你输入的不是一个文件夹，请检查路径是否有误！！");
			
		} else {
			File[] f = file.listFiles();
			if(f!=null && f.length>0){
				
			for (int i = 0; i < f.length; i++) {
				// 判断文件列表中的对象是否为文件夹对象，如果是则执行tree递归，直到把此文件夹中所有文件输出为止
				
				getAllMessage(f[i]);
			}
			}
		}
		return videoNames;

	}

	public  void getAllInfo() {
		File f = new File(path);
		//List<VideoName> videoMessages = getAllMessage(f);
		try {
			readMp3ID3V1(musicpath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.i("MV",videoMessages.size()+"");
	}
	
	
	public void getMovieInfo(File file){
		time = getVideoTime(file);
		if (time.equals("")) {
			time = "未知";
		}
		size = getFileSize(file);
		VideoName videoName = new VideoName();
		int j = file.getName().indexOf(".", 1);
		videoName.setName(file.getName().substring(0, j));
		videoName.setSize(size);
		videoName.setTime(time);
		videoName.setBrief(file.getName().substring(j + 1,
				file.getName().length()));
		videoName.setUrl(path
				+ file.getName());
		videoNames.add(videoName);
		Log.i("MV",time
				+ "---"
				+ size
				+ "---"
				+ file.getName().substring(0, j)
				+ "---"
				+ file.getName().substring(j + 1,
						file.getName().length()) + "---"
				+ path
				+ file.getName());
	}

	
	public void   readMp3ID3V1(String path)   throws   Exception{ 
        byte[] buf = new byte[1024]; 
        File file = new File(path); 
 
      FileInputStream fis = new FileInputStream(file); 
      /*---读取MP3文件尾部信息，并显示----*/ 
      long size = file.length(); 
      Log.i("MV","文件总字节数："+size);
 
      fis.skip(size-128); 
 
      //标志位TAG:3  byte 
      fis.read(buf,0,3); 
      String tag = new String(buf,0,3); 
      Log.i("MV", "ID3V1:  "+tag); 
 
      //歌曲名称 30 byte 
      fis.read(buf,0,30); 
      String songname = new String(buf,0,30);
      Log.i("MV","song   name:   "+songname); 
 
      //歌手名称   30   byte 
      int len = fis.read(buf,0,30); 
      String songername = new String(buf,0,len); 
      Log.i("MV", "songer   name:   "+songername); 
 
      //专辑名称   30   byte 
      len = fis.read(buf,0,30); 
      String albumname = new String(buf,0,len); 
      Log.i("MV","album   name:   "+albumname); 
 
      //年代 4 byte 
      fis.read(buf,0,4); 
      String year = new String(buf,0,4); 
      Log.i("MV", "year   : "+year); 
 
      //comment 30 byte 
      fis.read(buf,0,28); 
      len = fis.read(buf,0,28); 
      String con = new String(buf,0,len); 
      Log.i("MV", "comment:   "+con); 
      //genre   1   byte 
      fis.read(buf,0,1); 
      Log.i("MV", "Genre:   "+buf[0]); 
      fis.close(); 
 
    }
	
}
