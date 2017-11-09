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
import android.widget.EditText;
import android.widget.TextView;

public class FolderSaveDialog extends Dialog {
	private EditText mSelfDefineText;
	private TextView mPath;
	private TextView mTitleName;
	
	private Map<String, Object> mData;
	
	public FolderSaveDialog(Context context) {
	    super(context, R.style.MenuDialog);
	    initView();
    }
	
	public FolderSaveDialog(Context context, int theme) {
	    super(context, theme);
	    initView();
    }
	
	private void initView() {
		View layout = LayoutInflater.from(getContext()).inflate(R.layout.movie_folder_save_layout, null);
		addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setContentView(layout);
		initPosition();
		
		mSelfDefineText = (EditText) layout.findViewById(R.id.self_define_name);
		mTitleName = (TextView) layout.findViewById(R.id.title_name);
		mPath = (TextView) layout.findViewById(R.id.folder_path);
		findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mData.put(CommonConstant.Nfs.SELFDEFINE_NAME, mSelfDefineText.getText().toString());
				if (Samba.TYPE_NAME.equals(mData.get(CommonConstant.Common.TYPE))) {
					SambaController.getInstance().addShortcut(mData);
				} else {
					NfsController.getInstance().addShortcut(mData);
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

	public void setData(Map<String, Object> item) {
		mData = item;
		String name = (String) mData.get(Samba.SELFDEFINE_NAME);
		if (name != null) {
			mSelfDefineText.setText(name);
			mSelfDefineText.setSelection(name.length());
		}
		
		mPath.setText((String) mData.get(Samba.DISPLAY_PATH));
    }

	public void setTitleName(String name) {
		mTitleName.setText(name);
    }
	
}
