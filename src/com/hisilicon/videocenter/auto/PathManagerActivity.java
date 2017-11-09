package com.hisilicon.videocenter.auto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.R.id;
import com.hisilicon.videocenter.R.layout;
import com.hisilicon.videocenter.auto.db.MovieDaoImpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class PathManagerActivity extends Activity {
	private ListView mList;
	private Button btnFinish;
	private ProgressBar pb;
	private PathManagerAdapter adapter;
	private ArrayList<String> paths;
	private SharedPreferenceUtil sp;
	private String selectPath;
	private int selectPosition;
	private Dialog dialog;
	private MovieDaoImpl movieDaoImpl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path_manager);
		initView();
		getPathData();
	}


	private void getPathData() {
		// TODO Auto-generated method stub
		sp = SharedPreferenceUtil.getInstance(this);
		Set set = sp.get("path");
	//	Log.i("folder","init set  num==="+set.size());
		if(set==null || set.size()==0){
			Toast.makeText(this, getResources().getString(R.string.empty_toast), Toast.LENGTH_LONG).show();
			return;
			}
		
		ArrayList<String> list = new ArrayList<>();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String path = (String) it.next();	
			if(!(path.contains("nfs")||path.contains("samba")) && !TextUtils.isEmpty(path))
			list.add(path);
			
		}
		paths.clear();
		paths.addAll(list);
		adapter.notifyDataSetChanged();
		mList.setSelection(0);
		if(paths.size() > 0)
		selectPath = paths.get(0);
	}


	private void initView() {
		// TODO Auto-generated method stub
		mList = (ListView) findViewById(R.id.file_listview);
		btnFinish = (Button) findViewById(R.id.btn_finish);
		pb = (ProgressBar) findViewById(R.id.pb);
		paths = new ArrayList<>();
		adapter = new PathManagerAdapter(this, paths);
		mList.setAdapter(adapter);
		mList.setOnItemSelectedListener(new MSelectListener());
		btnFinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			finish();	
			}
		});
		
		movieDaoImpl = new MovieDaoImpl(this);
	}
	
	private class MSelectListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			selectPath = paths.get(arg2);
			selectPosition = arg2;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_MENU){
			if (dialog == null) {
				View view = getLayoutInflater().inflate(
						R.layout.delete_path_dialog, null);
				TextView sure = (TextView) view.findViewById(R.id.tv_sure);
				sure.requestFocus();
				TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
				sure.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
					
						int indexOf = selectPath.indexOf(";");
						String pathNum = selectPath.substring(indexOf+1, selectPath.length());
						int num = Integer.parseInt(pathNum);
						//Log.i("folder","del selectpath==="+selectPath+"pathnum==="+num);
						Set set = sp.get("path");
						set.remove(selectPath);
						sp.put("path", set);
					//	Log.i("folder","del rest num==="+sp.get("path").size());
						
						movieDaoImpl.deleteMovies(num);
						dialog.dismiss();
						
						paths.remove(selectPosition);
						adapter.notifyDataSetChanged();
						mList.setSelection(0);
						if(paths.size() > 0)
						selectPath = paths.get(0);
						
					}
				});
				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				//dialog = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT).setView(view).create();
				dialog = new Dialog(this, R.style.MyDialog);
				dialog.setContentView(view);

			}
			dialog.show();
			
		}
		return super.onKeyDown(keyCode, event);
	}
}
