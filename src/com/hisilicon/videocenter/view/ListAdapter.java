package com.hisilicon.videocenter.view;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hisilicon.videocenter.R;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    public boolean searchEdit;

    public ListAdapter(Context context, List<String> list, Handler handler) {
        this.context = context;
        if (null == list) {
        	this.list = new ArrayList<String>();
        } else {
        	this.list = list;
        }
            
    }

    public void refreshView() {
        this.notifyDataSetChanged();
    }

    public void refreshView(List<String> list) {
        this.list = list;
        refreshView();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String msg = list.get(position);
        TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.list_item, null);
        textView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, 47));   
        textView.setText(msg);
        return textView;
    }

}