package com.explorer.common;

import java.io.File;
import java.util.List;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.view.OnItemViewClicklListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * data adapter
 * 
 * @author liu_tianbao Provide data for the list or thumbnail
 */
public class FileAdapter extends BaseAdapter {
	private CommonActivity context;
	
	private Bitmap folder_File;
	private Bitmap other_File;

	// file list
	private List<File> mFileList;
	
	private OnItemClickListener mItemClickListener;

	/**
	 * @brief : Eliminating redundant code
	 */
	LayoutInflater inflater;

	String fileLength = null;

	/**
	 * @param context
	 *            page
	 * @param list
	 *            collection of files
	 * @param fileString
	 *            file path
	 * @param layout
	 *            data layout
	 */
	// public FileAdapter(Activity context, List<File> list, int layout) {
	public FileAdapter(CommonActivity context, List<File> list) {
		this.context = context;
		mFileList = list;
		inflater = LayoutInflater.from(context);

		other_File = BitmapFactory.decodeResource(context.getResources(), R.drawable.otherfile);
		folder_File = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder_file);
	}

	/* he number of data containers */
	/* CNcomment: 获得容器中数据的数目 */

	public int getCount() {
		return mFileList.size();
	}

	/* For each option object container */
	/* CNcomment: 获得容器中每个选项对象 */

	public Object getItem(int position) {
		return position;
	}

	/* Access to each option in the container object */
	/* CNcomment: 获得容器中每个选项对象的ID */

	public long getItemId(int position) {
		return position;
	}

	public List<File> getFiles() {
		return mFileList;
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mItemClickListener = listener;
	}

	/* Assignment for each option object */
	/* CNcomment: 为每个选项对象赋值 */

	public View getView(final int position, View convertView, ViewGroup parent) {
		return getMyView(convertView, position);
	}

	private View getMyView(View convertView, int position) {
		// Control container
		final ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.file_item, null);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.addBtn = (TextView) convertView.findViewById(R.id.add_btn);
			holder.icon = (ImageView) convertView.findViewById(R.id.image_Icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		convertView.setLayoutParams(new AbsListView.LayoutParams(648, 60));

		final File f = mFileList.get(position);
		String f_type = FileUtil.getMIMEType(f, context);
		// get the type of file
		if (f.isFile()) {
			if ("audio/*".equals(f_type)) {
				holder.icon.setImageBitmap(other_File);
			} else if ("video/*".equals(f_type) || "video/iso".equals(f_type)) {
				holder.icon.setImageBitmap(other_File);
			} else if ("apk/*".equals(f_type)) {
				holder.icon.setImageBitmap(other_File);
			} else if ("image/*".equals(f_type)) {
				holder.icon.setImageBitmap(other_File);
			} else if ("video/dvd".equals(f_type)) {
				holder.icon.setImageBitmap(other_File);
			} else {
				holder.icon.setImageBitmap(other_File);
			}
		} else {
			holder.icon.setImageBitmap(folder_File);
		}

		holder.text.setText(f.getName());
		
		final int pos = position;
		holder.text.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(null, view, pos, pos);
				}
			}
		});
		
		holder.addBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(null, view, pos, pos);
				}
			}
		});
		
		return convertView;
	}

	/**
	 * the type of control container
	 * 
	 * @author qian_wei save control
	 */
	private static class ViewHolder {
		private TextView text;
		private TextView addBtn;
		private ImageView icon;
	}

}
