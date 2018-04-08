package com.hisilicon.videocenter.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import com.hisilicon.videocenter.HomeActivity;

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
	    @Override
	    public void onCreate() {
	    	// TODO Auto-generated method stub
	    	super.onCreate();
	    }
	    
	    @Override
	    @Deprecated
	    public void onStart(Intent intent, int startId) {
	    	// TODO Auto-generated method stub
	    	super.onStart(intent, startId);
	    }

	    public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.d(TAG, "onStartCommand");
	        // 获取系统音乐服务
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
	        }
	        
	        
	        mActivityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
	        
	        mTimer = new Timer(true);
	        mTask = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					vIsActive=mAudioManager.isMusicActive();
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
					}
				}
			};
	        mTimer.schedule(mTask, 1000, 1000);
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
