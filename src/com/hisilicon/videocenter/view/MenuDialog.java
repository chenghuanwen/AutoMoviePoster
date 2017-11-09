package com.hisilicon.videocenter.view;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.controller.HomeController;
import com.hisilicon.videocenter.util.HiSettingsManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class MenuDialog extends Dialog {
	public MenuDialog(Context context) {
	    super(context);
    }
	
	public MenuDialog(Context context, int theme) {
	    super(context, theme);
    }
	
	
	public static class Builder {
		private ViewGroup mLanguageItem;
		private ViewGroup mLocalBrowserItem;
		private ViewGroup mMoiveFolderManageItem;
		private ViewGroup mMovieFolderAddItem;
		
		public Builder() {
			
		}
		
		public MenuDialog create(Activity activity) {
			final MenuDialog menuDialog = new MenuDialog(activity, R.style.MenuDialog);
			View layout = LayoutInflater.from(activity).inflate(R.layout.movie_settings_list, null);
			menuDialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			menuDialog.setContentView(layout);
			initPosition(menuDialog);
			initView(layout);
			return menuDialog;
		}
		
		private void initView(View view) {
			mLanguageItem = (ViewGroup) view.findViewById(R.id.setting_menu_item_language);
			mLocalBrowserItem = (ViewGroup) view.findViewById(R.id.setting_menu_item_local_browser);
			mMoiveFolderManageItem = (ViewGroup) view.findViewById(R.id.setting_menu_item_folder_manage);
			mMovieFolderAddItem = (ViewGroup) view.findViewById(R.id.setting_menu_item_add_folder);
			
			final SwitchTextView mLanguageSwitchText = (SwitchTextView) view.findViewById(R.id.language_siwtch_text);
			mLanguageSwitchText.setTextArray(new String[]{"中文", "英文"});
			mLanguageSwitchText.setTextIndex(HiSettingsManager.getInstance().getLanguageIndex());
			mLanguageItem.setOnKeyListener(mLanguageSwitchText.mOnkeyListener);
			mLanguageSwitchText.setOnTextChangeListener(new SwitchTextView.TextChangeListener() {
				
				@Override
				public void onIndexChange(int index) {
					HiSettingsManager.getInstance().setLanguageIndex(index);
				}
			});
			
			final SwitchTextView mLocalSwitchText = (SwitchTextView) view.findViewById(R.id.local_browser_siwtch_text);
			mLocalSwitchText.setTextArray(new String[]{"是", "否"});
			mLocalSwitchText.setTextIndex(HiSettingsManager.getInstance().getLocalBrowserIndex());
			mLocalBrowserItem.setOnKeyListener(mLocalSwitchText.mOnkeyListener);
			mLocalSwitchText.setOnTextChangeListener(new SwitchTextView.TextChangeListener() {
				@Override
				public void onIndexChange(int index) {
					HiSettingsManager.getInstance().setLocalBrowserIndex(index);
				}
			});
			
			mMoiveFolderManageItem.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					HomeController.getInstance().showMovieManageDialog();	
				}
			});
			
			mMovieFolderAddItem.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					HomeController.getInstance().showMovieFolderAddDialog();
				}
			});
			
		}
		
		private void initPosition(Dialog dialog) {
		    Window dialogWindow = dialog.getWindow();
	        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
	        lp.x = 112;
	        lp.y = 86;
	        lp.width = 508;
	        lp.height = LayoutParams.WRAP_CONTENT;
	        dialogWindow.setAttributes(lp);
		}
		
	}
	
}
