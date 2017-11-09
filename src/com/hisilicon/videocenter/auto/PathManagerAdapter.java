package com.hisilicon.videocenter.auto;

import java.util.ArrayList;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.R.id;
import com.hisilicon.videocenter.R.layout;
import com.hisilicon.videocenter.util.LogUtil;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PathManagerAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> mPaths;
	
	

	public PathManagerAdapter(Context context, ArrayList<String> mPaths) {
		super();
		this.context = context;
		this.mPaths = mPaths;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPaths==null?0:mPaths.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(contentView == null){
			holder = new ViewHolder();
			contentView = LayoutInflater.from(context).inflate(R.layout.path_manager_item, null);
			holder.path1 = (TextView) contentView.findViewById(R.id.tv_path1);
			holder.path2 = (TextView) contentView.findViewById(R.id.tv_path2);
			contentView.setTag(holder);
		}else{
			holder = (ViewHolder) contentView.getTag();
		}
		
		
		String string = mPaths.get(position);
		int indexOf = string.indexOf(";");
		String path = string.substring(0, indexOf);
		LogUtil.i("folder","save path ==="+path);
		if(!TextUtils.isEmpty(path)){
			String[] split = path.split("\\/");
			String path1 = split[2];
			holder.path1.setText(path1);
			holder.path2.setText(path);
		}
		
		return contentView;
	}
	
	
	class ViewHolder{
		TextView path1,path2;
	}

}
