package com.hisilicon.videocenter.view;

import com.hisilicon.videocenter.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class CommonDialog extends Dialog {
	private View.OnClickListener mOkClickListener;
	private View.OnClickListener mCancelClickListener;
	
	private TextView mTitle;
	private TextView mContent;

	public CommonDialog(Context context) {
	    super(context, R.style.MenuDialog);
	    initView();
    }
	
	public CommonDialog(Context context, int theme) {
	    super(context, theme);
	    initView();
    }
	
	private void initView() {
		View layout = LayoutInflater.from(getContext()).inflate(R.layout.movie_common_dialog_layout, null);
		addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setContentView(layout);
		initPosition();
		mTitle = (TextView) findViewById(R.id.title);
		mContent = (TextView) findViewById(R.id.content);
		
		findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				hide();
				if (mOkClickListener != null) {
					mOkClickListener.onClick(view);
				}
			}
		});
		
		findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hide();
				if (mCancelClickListener != null) {
					mCancelClickListener.onClick(arg0);
				}
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

	public void setTitle(String title) {
		if (mTitle != null) {
			mTitle.setText(title);
		}
    }
	
	public void setText(String text) {
		if (mContent != null) {
			mContent.setText(text);
		}
	}

	public void setOnclickOkListener(View.OnClickListener listener) {
		mOkClickListener = listener;
	}
	
	public void setOnClickCancelListener(View.OnClickListener listener) {
		mCancelClickListener = listener;
	}
	
}
