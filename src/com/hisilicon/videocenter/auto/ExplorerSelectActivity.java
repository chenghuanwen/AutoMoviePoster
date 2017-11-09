package com.hisilicon.videocenter.auto;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.AEADBadTagException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.explorer.common.FileUtil;
import com.hisilicon.videocenter.HomeActivity;
import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.util.LogUtil;

public class ExplorerSelectActivity extends Activity {
	private static final String TAG = "folder";
	private ListView listview;
	private TextView tvCurrentPath, tvFolderCount;
	private ArrayList<FolderBean> folders;
	private FolderAdapter mAdapter;
	private String currentPath = "";
	private String firstPath = "";
	private ProgressBar pb;
	private Dialog dialog,coverDialog;
	private String selectPath;
	private static final int RESULT_CODE = 101;
	private long firstPress = 0;
	private SharedPreferenceUtil sp;
	private Set<String> savePaths ;
	private int pathNum = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explorer_select);
		initView();
		getMountName();
	}

	private void initView() {
		// TODO Auto-generated method stub
		listview = (ListView) findViewById(R.id.file_listview);
		tvCurrentPath = (TextView) findViewById(R.id.tv_current_path);
		tvFolderCount = (TextView) findViewById(R.id.tv_dir_count);
		pb = (ProgressBar) findViewById(R.id.pb);
		folders = new ArrayList<>();
		mAdapter = new FolderAdapter(this, folders);
		listview.setAdapter(mAdapter);
		listview.requestFocus();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				int length = firstPath.split("\\/").length;
				if (position == 0 && length > 3) {
					scanFolder(firstPath);
					firstPath = new File(firstPath).getParent();
					//Log.i("folder", "first path ===" + firstPath + "length=="+ length);
				} else if (position == 0 && length == 3) {
					getMountName();
				} else if (position == 0 && length < 3) {
					finish();
				} else {
					String path = folders.get(position).getPath();

					scanFolder(path);
				}

			}
		});
		listview.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				if (arg2 != 0){
					try {
						currentPath = folders.get(arg2).getPath();
						tvCurrentPath.setText(currentPath);
						tvFolderCount.setText((arg2+1) + "/" + folders.size());

						firstPath = new File(currentPath).getParent();
						if (firstPath.split("\\/").length > 3)
							firstPath = new File(firstPath).getParent();
					//	Log.i("folder", "firstpath===" + firstPath);
					} catch (Exception e) {
						// TODO: handle exception
					//	Log.i("folder", "firstpath===" + e.toString());
					}
				}else{
					tvCurrentPath.setText("");
					tvFolderCount.setText("1/" + folders.size());
				}
					

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		sp = SharedPreferenceUtil.getInstance(this);
		pathNum = sp.getNum();
		savePaths = sp.get("path");
		if(savePaths==null)
			savePaths = new HashSet<>();
	}

	private void scanFolder(String path) {
		ArrayList<FolderBean> beans = new ArrayList<>();
		FolderBean def = new FolderBean();
		def.setName("..");
		beans.add(def);
		pb.setVisibility(View.VISIBLE);
		// folders.clear();
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(path)) {
			pb.setVisibility(View.GONE);
			return;
		}

		File file = new File(path);
		if (file.exists() && file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File file2 : listFiles) {
				if (file2.isDirectory()) {
					FolderBean folder = new FolderBean();
					folder.setPath(file2.getAbsolutePath());
					folder.setName(file2.getName());
					beans.add(folder);
					
				}
			}
			mAdapter.addAll(beans, true);
			tvFolderCount.setText("1/" + folders.size());
			pb.setVisibility(View.GONE);
		}
	}

	private void getMountName() {
		// TODO Auto-generated method stub
		ArrayList<FolderBean> listfile = new ArrayList<>();
		// folders.clear();
		FolderBean def = new FolderBean();
		def.setName("..");
		listfile.add(def);
		List<StorageInfo> list = GetMountInfo.listAllStorage(this);
		for (StorageInfo info : list) {
			Log.e(TAG, info.toString());
			if (!info.toString().contains("emulated")) {
				FolderBean folder = new FolderBean();
				folder.setPath(info.getPath());
				String name = info.getName();
				folder.setName(name);
				listfile.add(folder);
				
			}

		}
		mAdapter.addAll(listfile, true);
		
		/*
		 * List<StorageInfo> infos = getMountInfo.getAvaliableStorage(list); for
		 * (StorageInfo info : infos) { Log.e(TAG, info.toString()); }
		 * 
		 * Log.e(TAG, "Environment.getExternalStorageDirectory(): " +
		 * Environment.getExternalStorageDirectory());
		 */

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if (keyCode == 22) {//右键
			Set set = sp.get("path");
			if (set != null) {
				Iterator<String> iterator = set.iterator();
				while (iterator.hasNext()) {
					final String path = (String) iterator.next();// mnt/sda/sda2/videos;3
					int indexOf = path.indexOf(";");
					String realPath = path.substring(0,indexOf);
					LogUtil.i("folder", "realpath=="+realPath+"===currentpath"+currentPath);
					if (currentPath.equals(realPath)) {
						Toast.makeText(ExplorerSelectActivity.this,
								getResources().getString(R.string.add_repeat),
								Toast.LENGTH_SHORT).show();
						return true;
					} else if (realPath.contains(currentPath)) {//覆盖子路径
						Toast.makeText(ExplorerSelectActivity.this,
								getResources().getString(R.string.add_chlid),
								Toast.LENGTH_SHORT).show();
						View view = getLayoutInflater().inflate(R.layout.cover_child_path_dialog, null);
						TextView tvcancel = (TextView) view.findViewById(R.id.tv_cancel);
						TextView tvsure = (TextView) view.findViewById(R.id.tv_sure);
						tvcancel.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
							coverDialog.dismiss();
							}
						});
						tvsure.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								pathNum += 1;
								savePaths.remove(path);
								selectPath = currentPath+";"+pathNum;
								savePaths.add(selectPath);
								sp.put("path", savePaths);
								sp.putNum(pathNum);
								
								Intent data = new Intent();
								data.putExtra("path",selectPath);
								setResult(RESULT_CODE, data);
								finish();
							}
						});
						
						if(coverDialog==null){
							coverDialog =new Dialog(ExplorerSelectActivity.this,R.style.MyDialog);
							coverDialog.setContentView(view);
						}
						
						coverDialog.show();
						

						return true;

					} else if (currentPath.contains(realPath)) {
						Toast.makeText(ExplorerSelectActivity.this,
								getResources().getString(R.string.add_parent),
								Toast.LENGTH_SHORT).show();
						return true;
					}

				}
			}
			if (dialog == null) {
				View view = getLayoutInflater().inflate(
						R.layout.add_movies_dialog, null);
				TextView sure = (TextView) view.findViewById(R.id.tv_sure);
				sure.requestFocus();
				TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
				sure.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						pathNum += 1;
						selectPath = currentPath+";"+pathNum;
						savePaths.add(selectPath);
						sp.put("path", savePaths);
						sp.putNum(pathNum);
						dialog.dismiss();
						
						
						String	showText = getString(R.string.movie_folder_add_success,currentPath);
						FileUtil.showToast(ExplorerSelectActivity.this, showText);
						
						
						Intent data = new Intent();
						data.putExtra("path", selectPath);
						setResult(RESULT_CODE, data);
						
						LogUtil.i("folder","add selectpath==="+selectPath+"pathnum==="+pathNum);
						finish();
					}
				});
				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				dialog = new Dialog(this,R.style.MyDialog);
				Window window = dialog.getWindow();
			    LayoutParams lp = window.getAttributes();
			    lp.height = 369;
			    lp.width = 480;
			    window.setAttributes(lp);
			    dialog.setContentView(view);
			}
			dialog.show();

		} else if(keyCode == KeyEvent.KEYCODE_MENU){
			startActivity(new Intent(ExplorerSelectActivity.this,PathManagerActivity.class));
			
		}else if (keyCode == KeyEvent.KEYCODE_BACK) {
			long newPress = System.currentTimeMillis();
			if (newPress - firstPress > 1000) {
				LogUtil.i("folder", "first path ===" + firstPath);
				scanFolder(firstPath);
				if (firstPath.split("\\/").length > 3)
					firstPath = new File(firstPath).getParent();
				else if (firstPath.split("\\/").length == 3)
					getMountName();
				Toast.makeText(this,
						getResources().getString(R.string.exit_tip),
						Toast.LENGTH_SHORT).show();
			} else {
				finish();
			}
			firstPress = newPress;
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}

}
