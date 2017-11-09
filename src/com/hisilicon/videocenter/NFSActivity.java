package com.hisilicon.videocenter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.explorer.common.CommonActivity;
import com.explorer.common.FileAdapter;
import com.explorer.common.FileUtil;
import com.explorer.common.MyDialog;
import com.explorer.ftp.DBHelper;
import com.hisilicon.videocenter.controller.DialogController;
import com.hisilicon.android.hinetshare.HiNfsClientManager;
import com.hisilicon.videocenter.nfs.CommonFlag;
import com.hisilicon.videocenter.util.CommonConstant;
import com.hisilicon.videocenter.util.CommonConstant.Nfs;
import com.hisilicon.videocenter.util.CommonConstant.Samba;

public class NFSActivity extends CommonActivity implements Runnable {

	static final String TAG = "NFSActivity";

	private String parentPath = "";

	private List<File> fileArray = null;

	List<Map<String, Object>> list = null;

	private String directorys = "/sdcard";

	private int result;

	private String folder_position = "";

	private String serverName = "";

	private String Userserver = "";

	private EditText editServer;

	private EditText position;

	SubMenu suboper;

	private int myPosition = 0;

	MyDialog dialog;

	Button myOkBut;

	Button myCancelBut;

	private DBHelper dbHelper;

	private SQLiteDatabase sqlite;

	Cursor cursor;

	List<String> selected;

	int controlFlag = 0;

	int id = 0;

	int flagItem = 0;

	AlertDialog alertDialog;

	ListView listViews;

	String prevName = "";

	String prevFolder = "";

	List<Integer> intList;

	String localPath = "";

	List<Map<String, Object>> workFolderList;

	AlertDialog nServerLogonDlg;

	ProgressDialog pgsDlg;

	long waitLong;

	String strWorkgrpups;

	long totalLong;

	Timer timer;

	HiNfsClientManager nfsClient;

	static final String DIR_ICON = "dirIcon";

	Map<String, Object> clickedNetServer;

	Map<String, List<Map<String, Object>>> server2groupDirs = new HashMap<String, List<Map<String, Object>>>(
			1);


	private final static int NET_ERROR = -5;

	private String prevServer;

	private StringBuilder builder = null;

	private static final int MOUNT_RESULT_1 = 11;

	private static final int MOUNT_RESULT_2 = 12;

	private static final int MOUNT_RESULT_3 = 13;
	
	private AlertDialog sureDialog;

	private int allOrShort = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lan);
		init();
	}

	private void init() {
		nfsClient = new HiNfsClientManager();
		listFile = new ArrayList<File>();
		intList = new ArrayList<Integer>();
		fileL = new ArrayList<File>();
		listlayout = R.layout.file_row;
		listView = (ListView) findViewById(R.id.listView);
		listView.setItemsCanFocus(true);
		pathTxt = (TextView) findViewById(R.id.textPath);
		list = new ArrayList<Map<String, Object>>(1);
		
		findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
			}
		});

		dbHelper = DBHelper.getInstance(this);
		sqlite = dbHelper.getWritableDatabase();
		selected = new ArrayList<String>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Nfs.IMAGE, R.drawable.mainfile);
		map.put(Nfs.NICK_NAME, getString(R.string.all_network));
		map.put(Nfs.SHORT, 0);
		list.add(map);
		//list.addAll(getServer());
		servers();

		isNetworkFile = true;
	}

	private void servers() {
		SimpleAdapter serveradapter = new SimpleAdapter(this, list,
				R.layout.file_row, new String[] { Nfs.IMAGE, Nfs.NICK_NAME },
				new int[] { R.id.image_Icon, R.id.text });
		listView.setAdapter(serveradapter);
		listView.setOnItemClickListener(ItemClickListener);
		listView.setOnItemSelectedListener(itemSelect);
		listView.setSelection(clickPos);

	}

	private void getDirectory(String path) {
		directorys = path;
		parentPath = path;
		currentFileString = path;

		getFiles(path);
	}

	OnClickListener butClick = new OnClickListener() {
		public void onClick(View v) {
			if (v.equals(myOkBut)) {
				myOkBut.setEnabled(false);
				Userserver = editServer.getText().toString();
				folder_position = position.getText().toString();
				/*
				 * if (folder_position.startsWith("/")) { folder_position =
				 * folder_position.substring(1); }
				 */

				if (folder_position.endsWith("/")) {
					folder_position = folder_position.substring(0,
							folder_position.length() - 1);
				}

				if (Userserver.trim().equals("")) {
					myOkBut.setEnabled(true);
					FileUtil.showToast(NFSActivity.this,
							getString(R.string.input_server));
				} else if (folder_position.trim().equals("")) {
					myOkBut.setEnabled(true);
					FileUtil.showToast(NFSActivity.this,
							getString(R.string.work_path_null));
				} else {
					doMount();
				}
			} else {
				dialog.cancel();
			}
		}
	};

	private void doMount() {
		builder = new StringBuilder(Userserver);
		builder.append(":").append(folder_position);
		if (controlFlag == 1) {
			boolean bServer = prevServer.equals(Userserver);
			boolean bDir = prevFolder.equals(folder_position);
			if (bDir && bServer) {
				dialog.cancel();
			} else {
				cursor = sqlite
						.query(Nfs.TABLE_NAME, new String[] { Nfs.ID }, Nfs.SERVER_IP
								+ "=? and " + Nfs.WORK_PATH + "=?", new String[] {
								Userserver, folder_position }, null, null, null);
				if (cursor.moveToFirst()) {
					myOkBut.setEnabled(true);
					FileUtil.showToast(this, getString(R.string.shortcut_exist));
				} else {
					progress = new ProgressDialog(this);
					progress.setOnKeyListener(new OnKeyListener() {
						public boolean onKey(DialogInterface arg0, int arg1,
								KeyEvent arg2) {
							return true;
						}
					});
					progress.show();
					MountThread thread = new MountThread(MOUNT_RESULT_2);
					thread.start();
				}
				cursor.close();
			}
		} else {
			cursor = sqlite.query(Nfs.TABLE_NAME, new String[] { Nfs.ID }, Nfs.SERVER_IP
					+ "=? and " + Nfs.WORK_PATH + "=?", new String[] { Userserver,
					folder_position }, null, null, null);
			if (cursor.moveToFirst()) {
				myOkBut.setEnabled(true);
				FileUtil.showToast(this, getString(R.string.shortcut_exist));
			} else {
				localPath = nfsClient.getMountPoint(builder.toString().replace(
						"\\", "/"));
				Log.w(TAG, builder.toString());
				Log.w(TAG, localPath);
				if (!localPath.equals("ERROR")) {
					showLading();
					dialog.cancel();
				} else {
					progress = new ProgressDialog(this);
					progress.setOnKeyListener(new OnKeyListener() {
						public boolean onKey(DialogInterface arg0, int arg1,
								KeyEvent arg2) {
							return true;
						}
					});
					progress.show();
					MountThread thread = new MountThread(MOUNT_RESULT_1);
					thread.start();
				}
			}
			cursor.close();
		}

	}

	private void showLading() {
		progress = new ProgressDialog(NFSActivity.this);
		progress.setTitle(getResources().getString(R.string.adding_server));
		progress.setMessage(getResources().getString(R.string.please_waitting));
		progress.show();

		Thread threas = new Thread(NFSActivity.this);
		threas.start();
	}

	public void run() {
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case SEARCH_RESULT:
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}

				synchronized (lock) {
					if (arrayFile.size() == 0 && arrayDir.size() == 0) {
						FileUtil.showToast(NFSActivity.this,
								getString(R.string.no_search_file));
						return;
					} else {
						updateList(true);
					}
				}

				break;
			case UPDATE_LIST:
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}
				
				adapter = new FileAdapter(NFSActivity.this, listFile);
				listView.setAdapter(adapter);
				listView.setOnItemSelectedListener(itemSelect);
				listView.setOnItemClickListener(ItemClickListener);
				adapter.setOnItemClickListener(ItemClickListener);
				fill(new File(currentFileString));
				break;
			case ISO_MOUNT_SUCCESS:
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}
				intList.add(myPosition);
				getFiles(mISOLoopPath);
				break;
			case ISO_MOUNT_FAILD:
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}
				FileUtil.showToast(NFSActivity.this,
						getString(R.string.new_mout_fail));
				break;

			case END_SEARCH:
			case NET_NORMAL:
				if (NFSActivity.this != null && !NFSActivity.this.isFinishing()) {
					pgsDlg.dismiss();
				}
				if (timer != null) {
					timer.cancel();
					timer.purge();
				}
				if (!strWorkgrpups.toLowerCase().equals("error")) {
					Log.d(TAG, "2424::strWorkgroups=" + strWorkgrpups);
					if (willClickNetDir) {
						Log.e("ADD2100", "POSITION");
						intList.add(myPosition);
					}
					updateServerListAfterParse(strWorkgrpups);
				} else {
					FileUtil.showToast(NFSActivity.this,
							getString(R.string.net_time_out));
				}
				break;
			case NET_ERROR:
				if (NFSActivity.this != null && !NFSActivity.this.isFinishing()) {
					pgsDlg.dismiss();
				}
				if (timer != null) {
					timer.cancel();
					timer.purge();
				}
				strWorkgrpups = "";
				FileUtil.showToast(NFSActivity.this,
						getString(R.string.no_server));
				break;
			case MOUNT_RESULT_1:
				myOkBut.setEnabled(true);
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}

				if (result == 0) {
					localPath = nfsClient.getMountPoint(builder.toString()
							.replace("\\", "/"));
					Log.d(TAG, "LOCAL " + localPath);
					showLading();
					dialog.cancel();
				} else if (result == NET_ERROR) {
					FileUtil.showToast(NFSActivity.this,
							getString(R.string.network_error));
				} else {
					FileUtil.showToast(NFSActivity.this,
							getString(R.string.new_server_error));
				}
				break;
			case MOUNT_RESULT_2:
				myOkBut.setEnabled(true);
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}

				if (result == 0) {
					builder = new StringBuilder(Userserver);
					builder.append(":").append(folder_position);
					localPath = nfsClient.getMountPoint(builder.toString()
							.replace("\\", "/"));
					Log.d(TAG, "LOCAL " + localPath);
					showLading();
					dialog.cancel();
				} else if (result == NET_ERROR) {
					FileUtil.showToast(NFSActivity.this,
							getString(R.string.network_error));
				} else {
					FileUtil.showToast(NFSActivity.this,
							getString(R.string.new_server_error));
				}
				break;
			case MOUNT_RESULT_3:
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}

				if (result == 0) {
					CommonFlag.needUpdate = true;
					showData(builder.toString(), sureDialog, myPosition);
				} else {
					if (result == NET_ERROR) {
						FileUtil.showToast(NFSActivity.this,
								getString(R.string.network_error));
					} else {
						FileUtil.showToast(NFSActivity.this,
								getString(R.string.new_server_error));
					}
				}
				break;
			default:
				progress.dismiss();
				ContentValues values = new ContentValues();
				values.put(Nfs.SERVER_IP, Userserver);
				values.put(Nfs.WORK_PATH, folder_position);
				Map<String, Object> map = null;
				if (controlFlag == 0) {
					sqlite.insert(Nfs.TABLE_NAME, Nfs.ID, values);
					boolean flag = false;
					for (Map<String, Object> maps : list) {
						if (!maps.containsValue(Userserver)) {
							flag = true;
							break;
						}
					}
					Log.w(TAG, " = " + flag);
					if (flag) {
						map = new HashMap<String, Object>();
						map.put(Nfs.IMAGE, R.drawable.folder_file);
						String nickName = "//" + Userserver + ":"
								+ folder_position;
						Log.d(TAG, "SHOWNAME = " + nickName);
						map.put(Nfs.NICK_NAME, nickName);
						map.put(Nfs.SHORT, 1);
						map.put(Nfs.MOUNT_POINT, localPath);
						map.put(Nfs.SERVER_IP, Userserver);
						map.put(Nfs.WORK_PATH, folder_position);
						list.add(map);
					}
				} else {
					sqlite.update(Nfs.TABLE_NAME, values, Nfs.ID + "=?",
							new String[] { String.valueOf(id) });
					map = new HashMap<String, Object>();
					map.put(Nfs.IMAGE, R.drawable.folder_file);
					String nickName = "//" + Userserver + ":" + folder_position;
					Log.d(TAG, "SHOWNAME = " + nickName);
					map.put(Nfs.NICK_NAME, nickName);
					map.put(Nfs.SHORT, 1);
					map.put(Nfs.MOUNT_POINT, localPath);
					map.put(Nfs.SERVER_IP, Userserver);
					map.put(Nfs.WORK_PATH, folder_position);

					if (pathTxt.getText().toString().equals("")) {
						list.set(listView.getSelectedItemPosition(), map);
					}

				}
				pathTxt.setText("");
				intList.clear();
				servers();
				listView.requestFocus();
				highlightLastItem(listView, list.size());
				break;
			}
		}
	};

	boolean willClickNetDir = false;

	private OnItemClickListener ItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			if (IsNetworkDisconnect()) {
				return;
			}
			
			if (listFile.size() > 0) {
				if (position >= listFile.size()) {
					position = listFile.size() - 1;
				}
			}
			
			myPosition = position;
			String[] splitedPath = pathTxt.getText().toString().split("/");
			if (splitedPath.length == 1) {
				fileL.clear();

				if (pathTxt.getText().toString().equals("")
						&& (intList.size() == 0)) {
					Log.w(TAG, list.get(position).get(Nfs.SHORT).toString());
					if (list.get(position).get(Nfs.SHORT).toString().equals("1")) {
						Userserver = list.get(position).get(Nfs.SERVER_IP)
								.toString();
						folder_position = list.get(position).get(Nfs.WORK_PATH)
								.toString().replace("\\", "/");
						String nick = list.get(position).get(Nfs.NICK_NAME)
								.toString();
						serverName = nick.substring(2, nick.length()
								- folder_position.length() - 1);
						mountPath(position, -1);
					} else {
						clickPos = 0;
						willClickNetDir = true;
						searchServers();
					}
				} else if (intList.size() == 1) {
					TextView text = (TextView) v.findViewById(R.id.text);
					serverName = text.getText().toString();
					willClickNetDir = true;
					showServerFolderList(text.getText().toString());
				} else if (intList.size() == 2) {
					if (workFolderList != null) {
						for (int i = 0; i < workFolderList.size(); i++) {
							String str = workFolderList.get(i).get(Nfs.WORK_PATH)
									.toString().replace("\\", "/");
							Log.w(TAG, " = " + str);
						}
						Userserver = workFolderList.get(position)
								.get(Nfs.SERVER_IP).toString();
						folder_position = workFolderList.get(position)
								.get(Nfs.WORK_PATH).toString().replace("\\", "/");
						mountPath(position, 0);
					}

				}
			} else {
				switch (v.getId()) {
				case R.id.add_btn:
					addShortCut();
					return;
				default:
					break;
				}
				chmodFile(listFile.get(position).getPath());
				if (listFile.get(position).canRead()) {
					if (listFile.get(position).isDirectory()) {
						intList.add(myPosition);
						clickPos = 0;
					} else {
						clickPos = position;
					}
					preCurrentPath = currentFileString;
					keyBack = false;
					getFiles(listFile.get(position).getPath());
				} else {
					FileUtil.showToast(NFSActivity.this, getString(R.string.file_cannot_read));
				}
			}
		}
	};

	private void mountPath(final int position, int flag) {
		allOrShort = flag;
		builder = new StringBuilder(Userserver);
		builder.append(":").append(folder_position);
		String returnStr = nfsClient.getMountPoint(builder.toString().replace(
				"\\", "/"));
		if (returnStr.equals("ERROR")) {
			progress = new ProgressDialog(this);
			progress.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface arg0, int arg1,
						KeyEvent arg2) {
					return true;
				}
			});
			progress.show();
			MountThread thread = new MountThread(MOUNT_RESULT_3);
			thread.start();
		} else {
			localPath = returnStr;
			mountSdPath = localPath;
			clickPos = 0;
			File file = new File(localPath);
			if (file.isDirectory() && file.canRead()) {
				Log.e("ADD1165", "POSITION");
				intList.add(position);
				CommonFlag.needUpdate = true;
			}
			myPosition = 0;
			getDirectory(returnStr);
		}
	}

	private void showData(String str, AlertDialog dialog, int position) {
		String returnStr = nfsClient.getMountPoint(str.toString().replace("\\",
				"/"));
		localPath = returnStr;
		mountSdPath = localPath;
		clickPos = 0;
		File file = new File(localPath);
		if (file.isDirectory() && file.canRead()) {
			intList.add(position);
		}
		getDirectory(returnStr);
	}

	private void showServerFolderList(String nickName) {
		List<Map<String, Object>> groupDetails;
		String detailGroup = nfsClient.getShareFolders(nickName);
		if (null != detailGroup && detailGroup.equals("ERROR")) {
			FileUtil.showToast(NFSActivity.this,
					getString(R.string.error_folder));
			return;
		} else {
			if (null != detailGroup && !"".equals(detailGroup)
					&& !detailGroup.toLowerCase().equals("error")) {
				groupDetails = parse2DetailDirectories(detailGroup);
				workFolderList = new ArrayList<Map<String, Object>>();
				Map<String, Object> map = null;
				for (int i = 0; i < groupDetails.size(); i++) {
					map = new HashMap<String, Object>();
					map.put(Nfs.IMAGE, R.drawable.folder_file);
					String serverIp = groupDetails.get(i).get(Nfs.SERVER_IP)
							.toString();
					map.put(Nfs.SERVER_IP, serverIp);
					String workPath = groupDetails.get(i).get(Nfs.WORK_PATH)
							.toString();
					map.put(Nfs.WORK_PATH, workPath);
					map.put(Nfs.NICK_NAME, nickName);
					workFolderList.add(map);
				}
				if (willClickNetDir) {
					intList.add(myPosition);
				}
				SimpleAdapter adapter = new SimpleAdapter(NFSActivity.this,
						workFolderList, R.layout.file_row, new String[] {
						Nfs.IMAGE, Nfs.WORK_PATH }, new int[] {
								R.id.image_Icon, R.id.text });
				pathTxt.setText(nickName);
				Log.d(TAG, "1372::[showServerFolderList]workFolderList.size="
						+ workFolderList.size());
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(ItemClickListener);
				listView.setOnItemSelectedListener(itemSelect);
			}
		}
	}

	public void getFiles(String path) {
		openFile = new File(path);
		if (!openFile.isDirectory()) {
			return;
		}
		
		currentFileString = path;
		updateList(true);

	};

	public void fill(File fileroot) {
		try {
			if (clickPos >= listFile.size()) {
				clickPos = listFile.size() - 1;
			}
			
			if (!fileroot.getPath().equals(directorys)) {
				parentPath = fileroot.getParent();
				currentFileString = fileroot.getPath();
			} else {
				currentFileString = directorys;
			}

			builder = new StringBuilder(Userserver);
			builder.append(":").append(folder_position);
			String tempStr = nfsClient.getMountPoint(builder.toString()
					.replace("\\", "/"));
			String display_path = fileroot.getPath().substring(
					tempStr.length(), fileroot.getPath().length());
			Log.i(TAG, "display_path" + display_path);
			pathTxt.setText(builder.toString() + display_path);

			if (clickPos >= 0) {
				listView.requestFocus();
				listView.setSelection(clickPos);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	AlertDialog attrServerDeletingDialog;
	List<File> fileL = null;

	OnItemSelectedListener itemSelect = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (!pathTxt.getText().toString().equals("")
					&& !pathTxt.getText().toString().equals(serverName)) {
				myPosition = position;
			}
		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	File[] sortFile;
	FileUtil util;

	public void updateList(boolean flag) {
		if (flag) {
			Log.i(TAG, "updateList");
			listFile.clear();
			if (progress != null && progress.isShowing()) {
				progress.dismiss();
			}
			progress = new ProgressDialog(NFSActivity.this);
			progress.show();
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}

			waitThreadToIdle(thread);

			thread = new MyThread();
			thread.setStopRun(false);
			progress.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					Log.v("\33[32m Main1", "onCancel" + "\33[0m");
					thread.setStopRun(true);
					if (keyBack) {
						intList.add(clickPos);
					} else {
						clickPos = myPosition;
						currentFileString = preCurrentPath;
						intList.remove(intList.size() - 1);
					}
					FileUtil.showToast(NFSActivity.this,
							getString(R.string.cause_anr));
				}
			});
			thread.start();
		} else {
			adapter.notifyDataSetChanged();
			fill(new File(currentFileString));
		}
	}

	int clickPos = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = pathTxt.getText().toString().equals(serverName);
		switch (keyCode) {

		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			super.onKeyDown(KeyEvent.KEYCODE_ENTER, event);
			return true;

		case KeyEvent.KEYCODE_BACK:// KEYCODE_BACK
			keyBack = true;
			willClickNetDir = false;
			if (intList.size() == 0) {
				onBackPressed();
			} else {
				if (!pathTxt.getText().toString().equals("")) {
					if (currentFileString.equals("") && flag) {
						pathTxt.setText("");
						willClickNetDir = false;
						searchServers();

					} else if (currentFileString.equals(localPath)) {
						listView.setVisibility(View.VISIBLE);
						currentFileString = "";
						listFile.clear();
						if (list.get(intList.get(0)).get(Nfs.SHORT).toString()
								.equals("1")) {
							pathTxt.setText("");
							clickPos = intList.get(intList.size() - 1);
							servers();
						} else {
							willClickNetDir = false;
							showServerFolderList(workFolderList
									.get(intList.get(intList.size() - 1))
									.get(Nfs.NICK_NAME).toString());
							pathTxt.setText(serverName);
						}
					} else {
						fileL.clear();
						if (currentFileString.equals(ISO_PATH)) {
							getFiles(prevPath);
						} else {
							getFiles(parentPath);
						}
					}
					if (intList.size() > 0) {
						int pos = intList.size() - 1;
						clickPos = intList.get(pos);
						listView.setSelection(clickPos);
						intList.remove(pos);
					}
				} else {
					servers();
					if (intList.size() > 0) {
						int pos = intList.size() - 1;
						if (listView.getVisibility() == View.VISIBLE) {
							clickPos = intList.get(pos);
							listView.setSelection(clickPos);
							intList.remove(pos);
						}

					}
				}
			}

			return true;

		case KeyEvent.KEYCODE_INFO: // info
			if (!pathTxt.getText().toString().equals("")
					&& !pathTxt.getText().toString().equals(serverName)) {
				FileUtil util = new FileUtil(this);
				util.showFileInfo(listFile.get(myPosition));
			}
			return true;
		}
		return false;
	}

	public String getCurrentFileString() {
		return currentFileString;
	}

	void searchServers() {
		timer = new Timer();
		waitLong = 0;
		totalLong = 120 * 1000;
		pgsDlg = new ProgressDialog(NFSActivity.this);
		pgsDlg.setTitle(R.string.wait_str);
		pgsDlg.setMessage(getString(R.string.search_str));
		pgsDlg.show();
		timer.schedule(new TimerTask() {
			public void run() {

				if (pgsDlg != null && pgsDlg.isShowing()) {
					waitLong += 3000;
					if (waitLong >= totalLong) {
						strWorkgrpups = "error";
						handler.sendEmptyMessage(END_SEARCH);
						return;
					}

					if (!"".equals(strWorkgrpups) && null != strWorkgrpups) {
						if (strWorkgrpups.toLowerCase().equals("error")) {
							handler.sendEmptyMessage(NET_ERROR);
						} else {
							handler.sendEmptyMessage(NET_NORMAL);
						}

						return;
					}
				} else {
					return;
				}
			}

		}, 1000, 3000);

		new Thread(new Runnable() {

			public void run() {
				Log.d(TAG, "2508::call_getWorkgroups()");
				if (strWorkgrpups == null || "".equals(strWorkgrpups)
						|| strWorkgrpups.toLowerCase().equals("error")) {
					Log.d(TAG, "2511::call_getWorkgroups()");
					strWorkgrpups = nfsClient.getWorkgroups();
					if (strWorkgrpups.equals(""))
						handler.sendEmptyMessage(NET_ERROR);
				}
			}
		}).start();

	}

	private void updateServerListAfterParse(String workgroup) {
		List<HashMap<String, Object>> groups = new ArrayList<HashMap<String, Object>>(
				1);
		if (!workgroup.equals("")) {
			String[] workgroups = workgroup.split("\\|");
			HashMap<String, Object> detailMap;
			for (int i = 0; i < workgroups.length; i++) {
				detailMap = new HashMap<String, Object>(1);
				String[] details = workgroups[i].split(":");
				String trimStr = details[0].trim();
				String pcName = trimStr.substring(
						trimStr.lastIndexOf("\\") + 1, trimStr.length());
				detailMap.put(Nfs.NICK_NAME, pcName);

				if (details.length == 2) {
					detailMap.put(Nfs.SERVER_INTRO, details[1].trim());

				} else if (details.length == 1) {
					detailMap.put(Nfs.SERVER_INTRO, "No Details");
				}
				detailMap.put(Nfs.IMAGE, R.drawable.mainfile);
				groups.add(detailMap);
			}
		}
		listSearchedServers(groups);

	}

	AlertDialog serverDialog;

	NFSServerAdapter nfsAdapter;

	private void listSearchedServers(List<HashMap<String, Object>> groups) {

		nfsAdapter = new NFSServerAdapter(this, groups, 0);
		listView.setAdapter(nfsAdapter);
		listView.setSelection(clickPos);
		listView.setOnItemClickListener(ItemClickListener);
		listView.setOnItemSelectedListener(itemSelect);
	}

	static class NFSServerAdapter extends BaseAdapter {

		Context context;

		List<HashMap<String, Object>> groups;

		int flag;

		public NFSServerAdapter(Context context,
				List<HashMap<String, Object>> groups, int flag) {
			this.context = context;
			this.groups = groups;
			this.flag = flag;
		}

		public int getCount() {
			return groups.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (flag == 0) {
				RelativeLayout layout = (RelativeLayout) inflater.inflate(
						R.layout.file_row, null);
				ImageView img = (ImageView) layout
						.findViewById(R.id.image_Icon);
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText(groups.get(position).get(Nfs.NICK_NAME).toString());
				img.setImageResource(R.drawable.mainfile);
				return layout;
			} else {
				CheckedTextView chktv = (CheckedTextView) inflater.inflate(
						R.layout.samba_server_checked_text_view, null);
				String textSamba = (String) groups.get(position).get(Nfs.NICK_NAME);
				if (chktv.getWidth() < chktv.getPaint().measureText(textSamba)) {
					chktv.setEllipsize(TruncateAt.MARQUEE);
					chktv.setMarqueeRepeatLimit(-1);
					chktv.setHorizontallyScrolling(true);
				}
				chktv.setText(textSamba);
				return chktv;
			}
		}
	}

	private List<Map<String, Object>> parse2DetailDirectories(String detailGroup) {
		Log.d(TAG, "2853::detailGroup=" + detailGroup);
		List<Map<String, Object>> detailInfos = new ArrayList<Map<String, Object>>(
				1);
		String[] details = detailGroup.split("\\|");
		HashMap<String, Object> map;
		String[] ipDetail = details[0].split(":");

		for (int i = 1; i < details.length; i++) {
			map = new HashMap<String, Object>(1);
			map.put(Nfs.SERVER_IP, ipDetail[1]);
			String[] dirDetails = details[i].split(":");
			String dir = dirDetails[0].trim();
			String dirName = dir.substring(dir.lastIndexOf("\\") + 1,
					dir.length());
			map.put(Nfs.WORK_PATH, dirName);
			map.put(Nfs.MOUNT_POINT, "");
			Log.d(TAG, "2893::map=" + map);
			detailInfos.add(map);
		}
		Log.d(TAG, "2896::detailInfos=" + detailInfos);
		return detailInfos;
	}

	public ListView getListView() {
		return listView;
	}

	protected void onDestroy() {
		if (cursor != null) {
			cursor.close();
			sqlite.close();
		}
		super.onDestroy();
	}

	private void highlightLastItem(ListView lvList, int dataSize) {
		lvList.smoothScrollToPosition(dataSize - 1);
		lvList.setSelection(dataSize - 1);
	}

	public int getFileFlag(String currentPath, String prePath) {
		if (currentPath.length() > prePath.length()) {
			prePath = currentPath;
			return 1;
		} else if (currentPath.length() < prePath.length()) {
			prePath = currentPath;
			return -1;
		} else {
			return 0;
		}
	}

	private void addShortCut() {
		String currentFolder = folder_position;
		String currentUserServer = Userserver;
		String absolutePath = "";
		
		if (TextUtils.isEmpty(currentFolder) || TextUtils.isEmpty(currentUserServer)) {
			//ip下面的第一级目录
			Map map = workFolderList.get(myPosition);
			String serverIp = (String) map.get(Samba.SERVER_IP);
			String serverName = (String) map.get(Samba.NICK_NAME);
			String serverFolder = (String) map.get(Samba.WORK_PATH);
			Log.e(TAG, "DB serverIp:"+serverIp + " serverName:" + serverName + " serverFolder:"+serverFolder);
			currentFolder = serverFolder;
			currentUserServer = serverIp;
		} else if (listFile != null && listFile.size() > 0) {
			//第二级目录
			File file = listFile.get(myPosition);
			String path;
		/*	if (file.isDirectory()) {
				path = file.getPath() + "/";
			} else {
				path = file.getParent() + "/";
			}*/
			
			if (file.isDirectory()) {
				path = file.getPath();
			} else {
				path = file.getParent();
			}
			
			if (path.matches("[/]{1,2}mnt/nfsShare/[a-zA-Z_0-9]*/.*")) {
				absolutePath = path.replaceFirst("[/]{1,2}mnt/nfsShare/[a-zA-Z_0-9]*/", "");
			}
			
			Log.e(TAG, "DB listFile path:"+path + " absolutePath:" + absolutePath);
		}
		
		final Map<String, Object> item = new HashMap<>();
		item.put(CommonConstant.Common.TYPE, Nfs.TYPE_NAME);
		item.put(Nfs.SERVER_IP, currentUserServer);
		item.put(Nfs.WORK_PATH, currentFolder);
		item.put(Nfs.ABSOLUTE_PATH, absolutePath);
		item.put(Nfs.SELFDEFINE_NAME, "影库");
		item.put(Nfs.DISPLAY_PATH, currentUserServer + currentFolder + File.separator + absolutePath);
		
		DialogController.showConfirmAddDialog(this, item);
		//setValues(currentUserServer, currentFolder, absolutePath, 1);
	}

	class MyThread extends MyThreadBase {

		public void run() {
			if (getFlag()) {
				setFlag(false);
				synchronized (lock) {
					util = new FileUtil(NFSActivity.this, 0,
							arrayDir, arrayFile, currentFileString);
				}
			} else {
				util = new FileUtil(NFSActivity.this, 0,
						currentFileString);
			}

			listFile = util.getFiles(0, "net");

			if (getStopRun()) {
				if (keyBack) {
					if (pathTxt.getText().toString()
							.substring(serverName.length()).equals(ISO_PATH)) {
						currentFileString = util.currentFilePath;
					}
				}
			} else {
				currentFileString = util.currentFilePath;
				handler.sendEmptyMessage(UPDATE_LIST);
			}
		}

		public File getMaxFile(List<File> listFile) {
			int temp = 0;
			for (int i = 0; i < listFile.size(); i++) {
				if (listFile.get(temp).length() <= listFile.get(i).length())
					temp = i;
			}
			return listFile.get(temp);
		}
	}

	public Handler getHandler() {
		return handler;
	}

	public void operateSearch(boolean b) {
		if (b) {
			for (int i = 0; i < fileArray.size(); i++) {
				listFile.remove(fileArray.get(i));
			}
		}
	}

	protected void onStop() {
		super.onStop();
		super.cancleToast();
	}

	public TextView getPathTxt() {
		return pathTxt;
	}

	public String getServerName() {
		return serverName;
	}

	class MountThread extends Thread {

		private int mountFlag = 0;

		public MountThread(int mountFlag) {
			this.mountFlag = mountFlag;
		}

		public void run() {
			switch (mountFlag) {
			case MOUNT_RESULT_1:
			case MOUNT_RESULT_3:
				builder = new StringBuilder(Userserver);
				builder.append(":").append(folder_position);
				result = nfsClient.mount(builder.toString(),null);
				handler.sendEmptyMessage(mountFlag);
				break;
			case MOUNT_RESULT_2:
				builder = new StringBuilder(prevServer);
				builder.append(":").append(prevFolder);
				String returnStr = nfsClient.getMountPoint(builder.toString()
						.replace("\\", "/"));
				if (returnStr.equals("ERROR")) {
					builder = new StringBuilder(Userserver);
					builder.append(":").append(folder_position);
					result = nfsClient.mount(builder.toString(),null);
				} else {
					nfsClient.unmount(returnStr);
					builder = new StringBuilder(Userserver);
					builder.append(":").append(folder_position);
					result = nfsClient.mount(builder.toString(),null);
				}
				handler.sendEmptyMessage(MOUNT_RESULT_2);
				break;
			}
		}
	}

	protected void onResume() {
		super.onResume();
		if (!currentFileString.equals("")
				&& preCurrentPath.equals(currentFileString)) {
			updateList(true);
		}
	}
}
