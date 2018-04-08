package com.hisilicon.videocenter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.hisilicon.videocenter.auto.ImageEncryptionUtil;
import com.hisilicon.videocenter.auto.OnUnLockImageFinishedListener;
import com.hisilicon.videocenter.auto.db.BDInfo;
import com.hisilicon.videocenter.auto.db.DVDInfo;
import com.hisilicon.videocenter.util.Common;
import com.hisilicon.videocenter.util.HiSettingsManager;
import com.hisilicon.videocenter.util.Movie;
import com.hisilicon.videocenter.util.XBMCListenerService;
import com.hisilicon.videocenter.view.EpisodeAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MovieActivity extends Activity implements OnItemClickListener{
	private ImageView imageView;
	private TextView textTitle;
	private TextView textDirector;
	private TextView textPerformer;
	private TextView textType;
	private TextView textArea;
	private TextView textDuration;
	private TextView textSynopsis;
	private Button btnPlay,btnXBMC;

	private GridView mEpisodeGridView;
	private EpisodeAdapter mEpisodeAdapter;

	private Movie movie;
	private BDInfo mBdInfo;
	private DVDInfo mDvdInfo;
	private Handler mHandler = new Handler();
	
	private ImageEncryptionUtil encryptionUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie);
		movie = (Movie) getIntent().getSerializableExtra(Common.EXTRA_MOVIE);
		encryptionUtil = new ImageEncryptionUtil(this);
		initView();
		mBdInfo = new BDInfo();
		mDvdInfo = new DVDInfo();
	
		
	}



	private void initView() {
		imageView = (ImageView) findViewById(R.id.imageView);
		mEpisodeGridView = (GridView) findViewById(R.id.gridview_episode);
		mEpisodeGridView.setOnItemClickListener(this);

		DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnFail(R.drawable.pic_default)
					.cacheOnDisk(false)
					.imageScaleType(ImageScaleType.EXACTLY)
					.build();
		if(movie.isAuto()==1){//加密
			  encryptionUtil.unlock(movie.getPicPath(),new OnUnLockImageFinishedListener() {
	   				
	   				@Override
	   				public void onUnlockFinish(Bitmap bitmap) {
	   					// TODO Auto-generated method stub
	   							if(bitmap != null){
	   								imageView.setImageBitmap(bitmap);
	   							}else{
	   								imageView.setImageResource(R.drawable.pic_default);
	   							}
	   						
	   				}
	   			});    
	    	   
		}else{
			ImageLoader.getInstance().displayImage("file:/" + movie.getPicPath(), imageView, options);	
		}
		

		textTitle = (TextView) findViewById(R.id.textTitle);
		textDirector = (TextView) findViewById(R.id.textDirector);
		textPerformer = (TextView) findViewById(R.id.textPerformer);
		textType = (TextView) findViewById(R.id.textType);
		textArea = (TextView) findViewById(R.id.textArea);
		textDuration = (TextView) findViewById(R.id.textDuration);
		textSynopsis = (TextView) findViewById(R.id.textSynopsis);

		setText(textTitle, checkStr(movie.mName), "", R.style.textStyle0, R.style.textStyle2);
		setText(textDirector, getString(R.string.text_director), checkStr(movie.mDirector));
		setText(textPerformer, getString(R.string.text_performer), checkStr(movie.mActors));
		setText(textType, getString(R.string.text_type), checkStr(movie.mType));
		setText(textArea, getString(R.string.text_area), checkStr(movie.mArea));
		setText(textDuration, getString(R.string.text_duration), checkStr(movie.mDuration));

		btnPlay = (Button)findViewById(R.id.btnPlay);
		btnXBMC = (Button)findViewById(R.id.btnPlay_xbmc);
		//btnXBMC.setVisibility(View.GONE);
		btnPlay.requestFocus();
		btnPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playMovie(-1,0);
			}
		});

		btnXBMC.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				playMovie(-1,1);
			}
		});
		
		ImageView btnBack = (ImageView) findViewById(R.id.home_back_iv);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		intAsync();
	}
	
	private Thread mGetSynopsosThread = null;
	private String order[] = {"input","tap","1000","200"}; 
//	private String order = "adb shell input tap 1000 200";
	private void intAsync() {
		mGetSynopsosThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				final String synopsis = movie.getSynopsis();
				final boolean isMulti = movie.isMultiEpisode();
				
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mHandler.removeCallbacksAndMessages(null);
						setText(textSynopsis, getString(R.string.text_synopsis), checkStr(synopsis));
						if (isMulti) {
							btnPlay.setVisibility(View.GONE);
							mEpisodeGridView.setVisibility(View.VISIBLE);
							mEpisodeAdapter = new EpisodeAdapter(getApplicationContext());
							mEpisodeGridView.setAdapter(mEpisodeAdapter);
							int size = movie.getEpisodeCount();
							mEpisodeAdapter.refreshView(size);
							if (size > 15) {
								LayoutParams lp = mEpisodeGridView.getLayoutParams();
								lp.width = LayoutParams.MATCH_PARENT;
								lp.height = 102;
								mEpisodeGridView.setLayoutParams(lp);
							}
						} else {
							btnPlay.setVisibility(View.VISIBLE);
							mEpisodeGridView.setVisibility(View.GONE);
						}
						
					}
				});
			}
		});
		
		mGetSynopsosThread.start();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (mGetSynopsosThread != null && mGetSynopsosThread.isAlive()) {
					try {
						mGetSynopsosThread.interrupt();
                    } catch (Throwable e) {
	                    e.printStackTrace();
                    }
					
				}
				
			}
		}, 10000);
		
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    btnPlay.requestFocus();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    
	    mHandler.removeCallbacksAndMessages(null);
	    if (mGetSynopsosThread != null && mGetSynopsosThread.isAlive()) {
	    	try {
				mGetSynopsosThread.interrupt();
            } catch (Throwable e) {
                e.printStackTrace();
            }
	    }
	    
	   // stopService(new Intent(MovieActivity.this,XBMCListenerService.class));
	}
	
	private String getMediaType(String filePath) {
		String type = null;
		try {
			if (mBdInfo.isBDFile(filePath)) {
				type = "video/bd";
				return type;
			} else if (mDvdInfo.isDVDFile(filePath)) {
				type = "video/dvd";
				return type;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(TextUtils.isEmpty(filePath)){
			type = "novideo";
		return type;
		}
		
		File movieFile = new File(filePath);
		if (!movieFile.exists() || movieFile.isDirectory()) {
			type = "novideo";
			return type;
		}

		String end = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length()).toUpperCase();
		if (end.equals("ISO") || end.equals("BDMV"))
			type = "video/iso";
		else
			type = "video/*";
		return type;

	}

	private String checkStr(String s) {
		if (TextUtils.isEmpty(s))
			return getString(R.string.unknown);
		return s;
	}

	private void setText(TextView textView, String title, String info) {
		setText(textView, title, info, R.style.textStyle1, R.style.textStyle2);
	}

	private void setText(TextView textView, String title, String info, int style1, int style2) {
		if (null == title)
			title = "";
		if (null == info)
			info = "";

		info = " " + info;

		int len1 = title.length();
		int len2 = info.length();
		SpannableString styledText = new SpannableString(title + info);
		styledText.setSpan(new TextAppearanceSpan(this, style1), 0, len1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(this, style2), len1, (len1 + len2), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(styledText, TextView.BufferType.SPANNABLE);
	}

	private void playMovie(int index,int mode) {
		String moviePath = null;
		if (index >= 0) {
			moviePath = movie.getEpisodePath(index);
		} else {
			moviePath = movie.getMoviePath();
		}

		String Mediatype = getMediaType(moviePath);
		if (Mediatype == "novideo") {
			AlertDialog.Builder builder = new AlertDialog.Builder(MovieActivity.this, R.style.DarkDialog);
			builder.setTitle("ERROR");
			builder.setMessage("NO FILE");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			return;
		}

		if(mode==0){
		
			Intent intent = new Intent();
			intent.setClassName(Common.PACKAGE_VIDEO_PLAYER, Common.ACTIVITY_VIDEO_PLAYER);
			intent.setDataAndType(Uri.parse(moviePath), Mediatype);
			intent.putExtra("subtitles", HiSettingsManager.getInstance().getLanguageIndex());
			intent.putExtra("movie_name", movie.mName);
			startService(intent);
		}else{
			
			openAPPByPackage("org.xbmc.kodi",moviePath,Mediatype);
			
			 SystemClock.sleep(7000);
		        try {
		            new ProcessBuilder(order).start();//模拟输入去掉xbmc弹窗
		            return;
		        } catch(IOException e) {
		            e.printStackTrace();
		            Log.i("CHW","input error ==="+e.toString());
		        }
			
			//openAPPByPackage("org.morefun.morefuntv",moviePath,Mediatype);

		/*	SystemClock.sleep(3000);
			startService(new Intent(MovieActivity.this,XBMCListenerService.class));*/
			
		}
		
		
	}

	

	/**
	 * 通过包名打开app
	 * 只能打开data分区的app，打开system分区的没有权限
	 */
	private void openAPPByPackage(String pg,String path,String mediaType) {
		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等  
		
		Log.i("TAG", "==包名=="+pg);
		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(pg, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(packageInfo == null){return;}
		
		Intent intent = new Intent(Intent.ACTION_MAIN,null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setPackage(pg);
		List<ResolveInfo> infoList = getPackageManager().queryIntentActivities(intent, 0);
		ResolveInfo resolveInfo = infoList.iterator().next();
		
		if(resolveInfo != null){
			
		String packageName = resolveInfo.activityInfo.packageName;
		String activityName = resolveInfo.activityInfo.name;
		Log.i("TAG", "==类名=="+activityName);
		Intent appIntent = new Intent(Intent.ACTION_MAIN,null);
		appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		ComponentName cp = new ComponentName(packageName,activityName);
		appIntent.setComponent(cp);
		appIntent.setDataAndType(Uri.parse(path), mediaType);
		Log.i("TAG","movie path ===="+path+"==mediaType=="+mediaType);
		startActivity(appIntent);
		}
		
		
	}
	
	
	@Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		playMovie(position + 1,0);   
    }

}