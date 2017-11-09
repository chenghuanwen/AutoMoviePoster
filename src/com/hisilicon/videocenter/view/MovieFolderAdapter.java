package com.hisilicon.videocenter.view;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.controller.DataManager;
import com.hisilicon.videocenter.util.CommonConstant.Samba;

public class MovieFolderAdapter extends BaseAdapter {
	private Context mContext;
	private List<Map<String, Object>> list;

	public MovieFolderAdapter(Context contex) {
		mContext = contex;
	}

	public void refreshView() {
		this.notifyDataSetChanged();
	}

	public void refreshView(List<Map<String, Object>> list) {
		this.list = list;
		refreshView();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Map<String, Object> getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.folder_list_item, null);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, 47);
		convertView.setLayoutParams(lp);
		
		TextView selfDefineName = (TextView) convertView.findViewById(R.id.item_name);
		TextView countTv = (TextView) convertView.findViewById(R.id.item_count);
		TextView pathTv = (TextView) convertView.findViewById(R.id.item_path);

		Map map = list.get(position);
		int count = DataManager.getInstance().getMovieCountByItem(map);
		selfDefineName.setText((String) map.get(Samba.SELFDEFINE_NAME));
		countTv.setText(""+count);
		pathTv.setText((String) map.get(Samba.DISPLAY_PATH));

		return convertView;
	}

}