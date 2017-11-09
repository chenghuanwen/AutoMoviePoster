package com.hisilicon.videocenter.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hisilicon.videocenter.R;

public class MyTextView extends LinearLayout {

    private TextView textView1;

    private TextView textView2;

    public MyTextView(Context context) {
        super(context);
        initView(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.my_text_view, null);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
    }

    public void setTitle(int id) {
        if (null != textView1)
            textView1.setText(id);
    }

    public void setTitle(String msg) {
        if (null != textView1)
            textView1.setText(msg);
    }

    public void setInfo(int id) {
        if (null != textView2)
            textView2.setText(id);
    }

    public void setInfo(String msg) {
        if (null != textView2)
            textView2.setText(msg);
    }

}