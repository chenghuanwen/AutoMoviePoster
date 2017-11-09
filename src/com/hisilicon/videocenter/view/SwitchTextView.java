package com.hisilicon.videocenter.view;

import com.hisilicon.videocenter.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SwitchTextView extends LinearLayout implements View.OnClickListener{

	private TextView mCurrentText;
	private String[] mTextStringArray;
	private int arrayIndex = 0;
	
	private TextChangeListener mListener;
	
	public static interface TextChangeListener{
		public void onIndexChange(int index);
	}
	
	public SwitchTextView(Context context) {
		super(context);
		initView();
    }
	
	public SwitchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SwitchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    	super(context, attrs, defStyleAttr);
    	initView();
    }
	
	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.switch_text_layout, this);
		findViewById(R.id.iv_arrow_left).setOnClickListener(this);
		findViewById(R.id.iv_arrow_right).setOnClickListener(this);
		mCurrentText = (TextView) findViewById(R.id.tv_switch_current);
	}
	
	public void setTextArray(String[] array) {
		mTextStringArray = array;
		freshText();
	}
	
	public void setTextIndex(int index) {
		arrayIndex = index;
		freshText();
	}
	
	private void freshText() {
		if (mTextStringArray == null) {
			return;
		}
		
		mCurrentText.setText(mTextStringArray[arrayIndex]);
	}
	
	public void onLeftClick() {
		if (mTextStringArray == null) {
			return;
		}
		
		arrayIndex -= 1;
		if (arrayIndex < 0) {
			arrayIndex = mTextStringArray.length -1;
		}
		
		freshText();
		
		if (mListener != null) {
			mListener.onIndexChange(arrayIndex);
		}
	}
	
	public void onRightClick() {
		if (mTextStringArray == null) {
			return;
		}
		
		arrayIndex += 1;
		if (arrayIndex > mTextStringArray.length -1) {
			arrayIndex = 0;
		}
		
		freshText();
		
		if (mListener != null) {
			mListener.onIndexChange(arrayIndex);
		}
	}
	
	@Override
    public void onClick(View arg0) {
	    switch (arg0.getId()) {
		case R.id.iv_arrow_left:
			onLeftClick();
			break;
		case R.id.iv_arrow_right:
			onRightClick();
			break;
		default:
			break;
		}
	    
    }
	
	public OnKeyListener mOnkeyListener = new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View arg0, int keycode, KeyEvent keyevent) {
			if (keyevent.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keycode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					onLeftClick();
					return true;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					onRightClick();
					return true;
				default:
					break;
				}
			}
			return false;
		}
	};
	
	public void setOnTextChangeListener(TextChangeListener listener) {
		mListener = listener;
	}
	

}
