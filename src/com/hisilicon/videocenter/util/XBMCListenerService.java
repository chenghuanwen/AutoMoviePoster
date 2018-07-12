package com.hisilicon.videocenter.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.entity.InputStreamEntity;

import com.hisilicon.videocenter.HomeActivity;
import com.hisilicon.videocenter.util.ShellUtils.CommandResult;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;


public class XBMCListenerService extends Service{
	  private final String TAG = "MusicService";
	    /**
	     * 音频管理类
	     */
	    private AudioManager mAudioManager;
	    /**
	     * 是否播放音乐
	     */
	    private static boolean vIsActive=false;
	    /**
	     * 音乐监听器
	     */
	    private MyOnAudioFocusChangeListener mListener;

	    private Timer mTimer;
	    private TimerTask mTask;
	    private ActivityManager mActivityManager;
	    private Method method;
	    private final String PLAYER_PACKAGE_NAME = "org.xbmc.kodi";
	    private List<String> commandList;
	    private CommandResult result;
	    private String logPath = "/mnt/sdcard/logcat.txt";
	    private File logFile;
	    private BufferedReader reader;
	    private String line = "";
	    @Override
	    public void onCreate() {
	    	// TODO Auto-generated method stub
	    	super.onCreate();
	    	Log.i(TAG,"XBMC Service oncreate========");
	    }
	    
	    
	    @Override
	    @Deprecated
	    public void onStart(Intent intent, int startId) {
	    	// TODO Auto-generated method stub
	    	super.onStart(intent, startId);
	    }

	    public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.d(TAG, "onStartCommand");
	        
	        try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(logPath)));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	        commandList = new ArrayList<>();
	        
	        if(new File(logPath).exists())
	        commandList.add("rm -r /mnt/sdcard/logcat.txt");//如果不删除之前的logcat.txt文件，每次执行logcat命令也不会更新该文件
	        	try {
					new File(logPath).createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        commandList.add("logcat -d -v time -f /mnt/sdcard/logcat.txt");
	        	        	        	        
	        
	  /*      // 获取系统音乐服务
	        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
	        // 获取系统音乐服务状态
	        vIsActive=mAudioManager.isMusicActive();
	       
	        Log.i(TAG,"系统音乐服务状态======="+vIsActive);
	        mListener = new MyOnAudioFocusChangeListener();
	        if(vIsActive) {//播放状态
	            int result = mAudioManager.requestAudioFocus(mListener,
	                    AudioManager.STREAM_MUSIC,
	                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

	            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
	            {
	                Log.d(TAG, "requestAudioFocus successfully.");
	            }
	            else
	            {
	                Log.d(TAG, "requestAudioFocus failed.");
	            }
	        }*/
	        
	        
	        mActivityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
	        
	        mTimer = new Timer(true);
	        mTask = new TimerTask() {
				@Override
				public void run() {
					
					 result = ShellUtils.execCommand(commandList, false);
					 Log.i(TAG,"xbmc log==="+result+"=="+result.errorMsg);
					 try {
						while ((line=reader.readLine()) != null) {
						//	Log.i(TAG,"kodi log===="+line);
							if(line.contains("Kodi") && line.contains("application stopped")){
								Log.i(TAG,"kodi log===kill kodi progress=====");
								method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
								method.invoke(mActivityManager, PLAYER_PACKAGE_NAME);
								mTimer.cancel();
								stopService(new Intent(getApplicationContext(),XBMCListenerService.class));
							}
								
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
					// Log.i(TAG,"logcat result====="+result+"==="+result.successMsg+"==="+result.errorMsg);
					
					// TODO Auto-generated method stub
				/*	vIsActive=mAudioManager.isMusicActive();
					// int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC ); 
				//	Log.i(TAG, "timer task ========"+vIsActive);
					if(!vIsActive){
						
						try {
							method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
							//method.invoke(mActivityManager, "org.xbmc.kodi");
							method.invoke(mActivityManager, PLAYER_PACKAGE_NAME);
							mTimer.cancel();
							  mAudioManager.abandonAudioFocus(mListener);
							  stopService(new Intent(getApplicationContext(),XBMCListenerService.class));
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					//	mActivityManager.forceStopPackage("org.xbmc.kodi");
					}*/
				}
			};
	        mTimer.schedule(mTask, 1000, 2000);
	        return super.onStartCommand(intent, flags, startId);
	    }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(vIsActive)
        {
            mAudioManager.abandonAudioFocus(mListener);
        }
        Log.d(TAG, "onDestroy");
    }

    /**
     * 内部类：音乐监听器
     */
    public class MyOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            // TODO Auto-generated method stub
        	Log.d(TAG, "onAudioFocusChange.====="+focusChange);
        	if(focusChange==1){
        		try {
					method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
					//method.invoke(mActivityManager, "org.xbmc.kodi");
					method.invoke(mActivityManager, PLAYER_PACKAGE_NAME);
			            mAudioManager.abandonAudioFocus(mListener);
			            stopService(new Intent(getApplicationContext(),XBMCListenerService.class));
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
        	}
        }
    }
    
    
  
}
