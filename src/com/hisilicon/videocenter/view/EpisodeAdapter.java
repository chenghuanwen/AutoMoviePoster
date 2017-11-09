package com.hisilicon.videocenter.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hisilicon.videocenter.R;

public class EpisodeAdapter extends BaseAdapter {
    private Context context;
    private int mEpisodeCount = 0;

    public EpisodeAdapter(Context context) {
    	this.context = context;
    }

    public void refreshView(int size) {
    	mEpisodeCount = size;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mEpisodeCount;
    }

    @Override
    public Object getItem(int position) {
        return Integer.toString(position + 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
        	holder = new ViewHolder();
            holder.textView = new TextView(context);
            holder.textView.setTextSize(27);
            holder.textView.setGravity(Gravity.CENTER);
            holder.textView.setBackgroundResource(R.drawable.episode_item_selector);
            convertView = holder.textView;
            convertView.setLayoutParams(new AbsListView.LayoutParams(45, 45));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.textView.setText((String) getItem(position));
        return convertView;
    }

    public class ViewHolder {
        TextView textView;
    }

}