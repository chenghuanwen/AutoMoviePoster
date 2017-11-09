package com.hisilicon.videocenter.view;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.view.MyEditText.BackListener;

import android.app.Dialog;
import android.content.Context;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class EditeTextDialog extends Dialog {
	private MyEditText mEditText;
	
	public EditeTextDialog(Context context) {
	    super(context, R.style.MenuDialog);
	    initView();
    }
	
	public EditeTextDialog(Context context, int theme) {
	    super(context, theme);
	    initView();
    }
	
	private void initView() {
		mEditText = new MyEditText(getContext());
		mEditText.setFocusable(true);
		mEditText.setFocusableInTouchMode(true);
		addContentView(mEditText, new LayoutParams(1, 1));
		setContentView(mEditText);
		mEditText.setBackListener(new BackListener() {
			
			@Override
			public void back(TextView textView) {
				hide();
			}
		});
		
		initPosition();
	}
	
	public void addTextChangedListener(TextWatcher textWatcher) {
		if (mEditText != null) {
			mEditText.addTextChangedListener(textWatcher);
		}
	}
	
	private void initPosition() {
	    Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.gravity = Gravity.LEFT|Gravity.BOTTOM;
        lp.width = 1;
        lp.height = 1;
        lp.x = -1;
        lp.y = -1;
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

}
