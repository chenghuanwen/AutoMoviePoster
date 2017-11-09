package com.hisilicon.videocenter.view;

import java.util.Map;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.controller.NfsController;
import com.hisilicon.videocenter.controller.SambaController;
import com.hisilicon.videocenter.util.CommonConstant;
import com.hisilicon.videocenter.util.CommonConstant.Samba;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class FolderDeleteDialog extends Dialog {
	private TextView mSelfDefineText;
	private TextView mPath;
	
	private Map mData;
	
	public FolderDeleteDialog(Context context) {
	    super(context, R.style.MenuDialog);
	    initView();
    }
	
	public FolderDeleteDialog(Context context, int theme) {
	    super(context, theme);
	    initView();
    }
	
	private void initView() {
		View layout = LayoutInflater.from(getContext()).inflate(R.layout.movie_folder_delete_layout, null);
		addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setContentView(layout);
		initPosition();
		
		mSelfDefineText = (TextView) layout.findViewById(R.id.self_define_name);
		mPath = (TextView) layout.findViewById(R.id.folder_path);
		findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (Samba.TYPE_NAME.equals(mData.get(CommonConstant.Common.TYPE))) {
					SambaController.getInstance().deleteShortcut(mData);
				} else {
					NfsController.getInstance().deleteShortcut(mData);
				}
				
				hide();
			}
		});
		
		findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hide();
			}
		});
	}
	
	private void initPosition() {
	    Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 703;
        lp.height = LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
	}
	
	@Override
	public void show() {
	    super.show();
	}
	
	@Override
	public void hide() {
		super.hide();
		
	}

	public void setData(Map item) {
		mData = item;
		mSelfDefineText.setText("影库");
		mPath.setText((String) mData.get(Samba.DISPLAY_PATH));
    }
	
}
