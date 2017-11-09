package com.hisilicon.videocenter.util;

import java.util.ArrayList;

public class MovieUtils {
	private static ArrayList<String> mVideoType = new ArrayList<String>();
	
	static {
	    addFileType("MPEG");
	    addFileType("MPG");
	    addFileType("MP4");
	    addFileType("M4V");
	    addFileType("3GP");
	    addFileType("3GPP");
	    addFileType("3G2");
	    addFileType("3GPP2");
	    addFileType("MKV");
	    addFileType("WEBM");
	    addFileType("TS");
	    addFileType("AVI");
	    addFileType("WMV");
	    addFileType("ASF");
	    addFileType("M2TS");
	    addFileType("ISO");
	    addFileType("BDMV");
	}
	
	private static void addFileType(String type) {
    	mVideoType.add(type);
    }
	
    private static String getFileType(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot < 0) {
        	return null;
        }
            
        return path.substring(lastDot + 1).toUpperCase();
    }
    
    public static boolean isVideoFile(String path) {
    	if (mVideoType.contains(getFileType(path))) {
    		return true;
    	} else {
    		return false;
    	}
    	
    }

}
