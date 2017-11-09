package com.hisilicon.videocenter.view;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.util.CommonConstant.Samba;

public class FolderManageAdapter extends BaseAdapter {
	private List<Map> mDataList = null;
	private OnItemViewClicklListener mItemClickListener;
	private Context context;
	
	
	
	
    public FolderManageAdapter(Context context) {
		this.context = context;
	}

	/**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public  class ViewHolder  {
        public  TextView mSelfDefineView;
        public  TextView mPathView;
        public  Button mEditeBtn;
        public  Button mDeleteBtn;
        
    }

/*	@Override
    public int getItemCount() {
	    return mDataList == null ? 0 : mDataList.size();
    }
	
	public void setOnItemClickListener(OnItemViewClicklListener listener) {
		mItemClickListener = listener;
	}

	@Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
	    Map map = (Map) mDataList.get(pos);
	    viewHolder.mSelfDefineView.setText((String) map.get(Samba.SELFDEFINE_NAME));
	    viewHolder.mPathView.setText((String) map.get(Samba.DISPLAY_PATH));

	    final int postion = pos;
	    viewHolder.mEditeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(view, postion);
				}
				
			}
		});
	    
	    viewHolder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(view, postion);
				}
			}
		});
	    
    }

	@Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
		 View v = LayoutInflater.from(viewGroup.getContext())
	                .inflate(R.layout.folder_manage_list_item, viewGroup, false);

	    return new ViewHolder(v, type);
    }*/
	
	public Map getItem(int pos) {
		return mDataList == null ? null : mDataList.get(pos);
	}

	public void setData(List list) {
	    this.mDataList = list;
	    notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList==null?0:mDataList.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.folder_manage_list_item, null);
			holder.mSelfDefineView = (TextView) convertView.findViewById(R.id.self_define_name);
	        holder.mPathView = (TextView) convertView.findViewById(R.id.folder_path);
	        holder.mEditeBtn = (Button) convertView.findViewById(R.id.folder_edite);
	        holder.mDeleteBtn = (Button) convertView.findViewById(R.id.folder_delete);
	        convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		 Map map = (Map) mDataList.get(position);
		 holder.mSelfDefineView.setText((String) map.get(Samba.SELFDEFINE_NAME));
		 holder.mPathView.setText((String) map.get(Samba.DISPLAY_PATH));

		    final int postion = position;
		    holder.mEditeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mItemClickListener != null) {
						mItemClickListener.onItemClick(view, postion);
					}
					
				}
			});
		    
		    holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if (mItemClickListener != null) {
						mItemClickListener.onItemClick(view, postion);
					}
				}
			});
		    
		
		
		return convertView;
	}
	
	
	public void setOnItemClickListener(OnItemViewClicklListener listener) {
		mItemClickListener = listener;
	}

}
