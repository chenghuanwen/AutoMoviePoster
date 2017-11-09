package com.hisilicon.videocenter.controller;

import java.util.Map;

import android.app.Activity;
import android.view.View;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.util.CommonConstant;
import com.hisilicon.videocenter.util.CommonConstant.Samba;
import com.hisilicon.videocenter.view.CommonDialog;
import com.hisilicon.videocenter.view.FolderDeleteDialog;
import com.hisilicon.videocenter.view.FolderSaveDialog;

public class DialogController {
	
	/**
	 * 影库编辑
	 * @param activity
	 * @param item
	 */
	public static void showFolderEditeDialog(Activity activity, Map item) {
		FolderSaveDialog mFolderEditeDialog = new FolderSaveDialog(activity);
		mFolderEditeDialog.setData(item);
		mFolderEditeDialog.setTitleName(activity.getResources().getString(R.string.settings_movie_foler_edite));
		mFolderEditeDialog.show();    
    }
	
	public static void showConfirmAddDialog(Activity activity, Map item) {
		final Activity tmpActivity = activity;
		final Map tmpItem = item;
		boolean hasAdded = false;
		if (Samba.TYPE_NAME.equals(tmpItem.get(CommonConstant.Common.TYPE))) {
			hasAdded = SambaController.getInstance().hasAdded(tmpItem);
		} else {
			hasAdded = NfsController.getInstance().hasAdded(tmpItem);
		}
		
		if (hasAdded) {
			CommonDialog reConfirmDialog = new CommonDialog(activity);
			reConfirmDialog.setTitle(activity.getString(R.string.settings_movie_foler_add));
			reConfirmDialog.setText(activity.getString(R.string.movie_folder_has_add_msg));
			reConfirmDialog.setOnclickOkListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showFolderAddDialog(tmpActivity, tmpItem);
				}
			});
			
			reConfirmDialog.show();
		} else {
			showFolderAddDialog(tmpActivity, tmpItem);
		}
		
	}
	
	/**
	 * 影库添加
	 * @param activity
	 * @param item
	 */
	public static void showFolderAddDialog(Activity activity, Map item) {
		FolderSaveDialog mFolderEditeDialog = new FolderSaveDialog(activity);
		mFolderEditeDialog.setData(item);
		mFolderEditeDialog.show();    
    }

	/**
	 * 影库删除
	 * @param activity
	 * @param item
	 */
	public static void showFolderDeleteDialog(Activity activity, Map item) {
		FolderDeleteDialog dialog = new FolderDeleteDialog(activity);
		dialog.setData(item);
	    dialog.show();
	    
    }

}
