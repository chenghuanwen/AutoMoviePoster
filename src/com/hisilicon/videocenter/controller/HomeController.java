package com.hisilicon.videocenter.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;

import com.hisilicon.videocenter.TabBarExample;
import com.hisilicon.videocenter.event.EventDBChange;
import com.hisilicon.videocenter.event.EventShowMovie;
import com.hisilicon.videocenter.view.FolderSaveDialog;
import com.hisilicon.videocenter.view.MenuDialog;
import com.hisilicon.videocenter.view.FolderDeleteDialog;
import com.hisilicon.videocenter.view.FolderManageDialog;

public class HomeController {
	private static HomeController mInstance;
	
	private Activity mActivity;
	
	private HomeController() {
		EventBus.getDefault().register(this);
	}
	
	public static HomeController getInstance() {
		if (mInstance == null) {
			synchronized (HomeController.class) {
	            if (mInstance == null) {
	            	mInstance = new HomeController();
	            }
            }
		}
		
		return mInstance;
		
	}
	
	public void setActivity (Activity activity) {
		mActivity = activity;
	}
	
	public void destroy() {
		mActivity = null;
		mMenuDialog = null;
		mMovieManageDialog = null;
    }

	private MenuDialog mMenuDialog;
	public void showMenuSetting() {
		if (mMenuDialog == null) {
			mMenuDialog = new MenuDialog.Builder().create(mActivity);
		}
		
		if (!mMenuDialog.isShowing()) {
			mMenuDialog.show();
		}
		
	    
    }
	
	public void hideMenuSetting() {
		if (mMenuDialog != null) {
			mMenuDialog.hide();
		}
		
		mMenuDialog = null;
		
	}
	
	private FolderManageDialog mMovieManageDialog;
	public void showMovieManageDialog() {
		//if (mMovieManageDialog == null) {
			mMovieManageDialog = new FolderManageDialog(mActivity);
			List list = new ArrayList<Map<String, Object>>();
			list.addAll(SambaController.getInstance().getServer());
			list.addAll(NfsController.getInstance().getServer());
			mMovieManageDialog.setData(list);
		//}
		
		mMovieManageDialog.show();
	    
    }
	
	public void hideMovieManageDialog() {
		if (mMovieManageDialog != null) {
			mMovieManageDialog.hide();
		}
		
		mMovieManageDialog = null;
	}
	
	public void showMovieFolderAddDialog() {
		Intent intent = new Intent("hi.vidocenter.intent.manage");
		intent.addCategory("android.intent.category.DEFAULT");
	    mActivity.startActivity(intent);
    }

	public void showFolderEditeDialog(Map item) {
		DialogController.showFolderEditeDialog(mActivity, item); 
    }

	public void showFolderDeleteDialog(Map item) {
		DialogController.showFolderDeleteDialog(mActivity, item);
    }
	
	@Subscribe
	public void onEventMainThread(EventDBChange event) {
		if (mMovieManageDialog != null && mMovieManageDialog.isShowing()) {
			List list = new ArrayList<Map<String, Object>>();
			list.addAll(SambaController.getInstance().getServer());
			list.addAll(NfsController.getInstance().getServer());
			mMovieManageDialog.setData(list);
		}
		
	}

}
