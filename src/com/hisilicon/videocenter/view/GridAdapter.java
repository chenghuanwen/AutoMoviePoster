package com.hisilicon.videocenter.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.auto.GetSataPathUtil;
import com.hisilicon.videocenter.auto.ImageEncryptionUtil;
import com.hisilicon.videocenter.auto.OnUnLockImageFinishedListener;
import com.hisilicon.videocenter.util.LogUtil;
import com.hisilicon.videocenter.util.Movie;
import com.hisilicon.videocenter.videoinfo.ReadVideoMessages;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class GridAdapter extends BaseAdapter {

	private Context context;
	private ReadVideoMessages info;
	private List<Movie> movies;
	private MediaMetadataRetriever retr;
	private StringBuilder sb;
	private String info1 = "", info2 = "", info3 = "", info4 = "", info5 = "",
			info6 = "", info7 = "", info8 = "", info9 = "", info10 = "",
			info11 = "", info12 = "", info13 = "";
	private ImageEncryptionUtil encryptionUtil;
	private GetSataPathUtil sataPathUtil;
	private String unlockBase;

	public GridAdapter(Context context, List<Movie> movies) {
		this.context = context;
		info = new ReadVideoMessages();
		if (null == movies)
			this.movies = new ArrayList<Movie>();
		else
			this.movies = movies;

		unlockBase = new GetSataPathUtil(context).getSataPath()
				+ "/JSLMoive/unlock/";
		encryptionUtil = new ImageEncryptionUtil(context);

		initOptions();
	}

	DisplayImageOptions options;

	private void initOptions() {
		Options decodingOptions = new Options();
		decodingOptions.outHeight = 232;// 232
		decodingOptions.outWidth = 178;// 178
		options = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.pic_default).cacheOnDisk(false)
				.displayer(new RoundedBitmapDisplayer(6))
				.imageScaleType(ImageScaleType.EXACTLY)
				.decodingOptions(decodingOptions).build();
	}

	public void refreshView(List<Movie> movies) {
		this.movies.clear();
		this.movies.addAll(movies);
		this.notifyDataSetChanged();
		// getMovieInfo();
		// info.getAllInfo();
		LogUtil.i("folder", "刷新数据===" + this.movies.size());
	}

	@Override
	public int getCount() {
		return movies.size();
	}

	@Override
	public Object getItem(int position) {
		return movies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup adpterView) {
		// LogUtil.i("grid", "position:"+position);
		final ViewHolder holder;
		Movie movie = movies.get(position);
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.grid_item, null);
			convertView.setLayoutParams(new AbsListView.LayoutParams(184, 276));
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);
			holder.textView = (TextView) convertView
					.findViewById(R.id.textView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (movie.isAuto() == 1) {// 加密图片
			// Log.i("folder","加密图片=====");
			encryptionUtil.unlock(movie.getPicPath(),
					new OnUnLockImageFinishedListener() {

						@Override
						public void onUnlockFinish(Bitmap bitmap) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								holder.imageView.setImageBitmap(bitmap);
								// bitmap.recycle();
							} else {
								holder.imageView
										.setImageResource(R.drawable.pic_default);
							}

						}
					});

		} else {// 非加密图片
				// Log.i("folder","非加密图片=====");
			ImageLoader.getInstance().displayImage(
					"file:/" + movie.getPicPath(), holder.imageView, options);
		}

		// ImageLoader.getInstance().displayImage("file:/" + movie.getPicPath(),
		// holder.imageView, options);
		holder.textView.setText(movie.getTitle());

		return convertView;
	}

	public class ViewHolder {
		ImageView imageView;
		TextView textView;
	}

	public void getMovieInfo() {
		for (int i = 0; i < movies.size(); i++) {

			Movie movie = movies.get(i);
			Log.i("MV", "信息==" + i + "path===" + movie.getMoviePath());
			if (retr == null)
				retr = new MediaMetadataRetriever();
			if (sb == null)
				sb = new StringBuilder();
			try {
				retr.setDataSource(movie.getMoviePath());
				info1 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
				info2 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
				info3 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
				info4 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
				info5 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
				info6 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
				info7 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
				info8 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER);
				info9 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				info10 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
				info11 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
				info12 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
				info13 = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
				sb.append(info1).append(info2).append(info3).append(info4)
						.append(info5).append(info6).append(info7)
						.append(info8).append(info9).append(info10)
						.append(info11).append(info12).append(info13);
				String result = sb.toString();
				Log.i("MV", "信息==" + result);
				sb.setLength(0);

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

}