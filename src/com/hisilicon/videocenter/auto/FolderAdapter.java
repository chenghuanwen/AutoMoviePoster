package com.hisilicon.videocenter.auto;

import java.util.ArrayList;

import com.hisilicon.videocenter.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FolderAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<FolderBean> mDatas;
	

	public FolderAdapter(Context mContext, ArrayList<FolderBean> mDatas) {
		super();
		this.mContext = mContext;
		this.mDatas = mDatas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas==null?0:mDatas.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mDatas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		FolderBean folder = mDatas.get(position);
		ViewHolder holder;
		if(contentView == null){
			holder = new ViewHolder();
			contentView = LayoutInflater.from(mContext).inflate(R.layout.filelist_item, null);
			holder.name = (TextView) contentView.findViewById(R.id.folder_name);
			contentView.setTag(holder);
		}else{
			holder = (ViewHolder) contentView.getTag();
		}
		holder.name.setText(folder.getName());
		return contentView;
	}
	
	class ViewHolder{
		private TextView name;
	}
	
	
	public void addAll(ArrayList<FolderBean> datas,boolean flag){
		if(flag)
			mDatas.clear();
			mDatas.addAll(datas);
			//Log.i("folder","数据量=="+mDatas.size());
		notifyDataSetChanged();
	}

}
