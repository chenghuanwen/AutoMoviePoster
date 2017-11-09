package com.explorer.common;

import com.explorer.bd.BDInfo;
import com.explorer.bd.DVDInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.Set;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommonActivity extends Activity {
	/* The list of displayed data container */
	/* CNcomment:列表方式显示数据容器 */
	protected ListView listView;

	/* add new NFS mount */
	/* CNcomment: 新增NFS挂载 */
	protected final static int ADD_NFS_MOUNT = Menu.FIRST + 14;

	/* label */
	/* CNcomment: 标签 */
	protected final static int MENU_TAB = Menu.FIRST + 15;

	protected final static int MENU_CD_ROM = Menu.FIRST + 16;

	public static boolean OPERATER_ENABLE = true;
	public static boolean SORT_ENABLE = true;
	/* storage devices local mount path */
	/* CNcomment:存储设备本地挂载路径 */
	public String mountSdPath = "";

	/* access to NFS mount data */
	/* CNcomment:获取NFS挂载数据 */
	protected final static int MOUNT_DATA = 0;

	/* end of service search */
	/* CNcomment:结束服务搜索 */
	protected final static int END_SEARCH = 1;

	/* search result display */
	/* CNcomment:搜索结果显示 */
	protected final static int SEARCH_RESULT = 2;

	/* updata the list of files */
	/* CNcomment:更新文件列表 */
	protected final static int UPDATE_LIST = 3;

	/* network normal */
	/* CNcomment:网络正常 */
	protected final static int NET_NORMAL = 4;

	/* ISO mount successful */
	/* CNcomment:ISO挂载成功 */
	protected final static int ISO_MOUNT_SUCCESS = 5;

	/* network abnormal */
	/* CNcomment:网络异常 */
	protected final static int NET_ERROR = 6;

	/* add new mount */
	/* CNcomment:新增挂载 */
	protected final static int ADD_MOUNT = 7;

	/* ISO mount fail */
	/* CNcomment:ISO挂载失败 */
	protected final static int ISO_MOUNT_FAILD = 8;

	/* unmount */
	/* CNcomment:卸载挂载 */
	protected final static int DEL_MOUNT = 9;

	/* modify permissions */
	/* CNcomment:修改权限 */
	protected final static int CHMOD_FILE = 10;

	/* path display box */
	/* CNcomment: 路径显示框 */
	protected TextView pathTxt;

	/* wait for the dialog box */
	/* CNcomment: 等待对话框 */
	protected ProgressDialog progress;

	/* file list */
	/* CNcomment:文件列表 */
	protected List<File> arrayFile;

	/* directory list */
	/* CNcomment:目录列表 */
	protected List<File> arrayDir;

	/* file list a list of resources */
	/* CNcomment:文件列表列表资源 */
	protected int listlayout = 0;
	
	protected String mCurrentPath = "";

	/* share file read write mode */
	/* CNcomment:Share文件读写模式 */
	protected final static int SHARE_MODE = Context.MODE_MULTI_PROCESS;
	
	/* file current path */
	/* CNcomment:当前文件路径 */
	protected String currentFileString = "";
	final static String MUSIC_PATH = "music_path";

	/* search thread of execution */
	/* CNcomment:搜索执行线程 */
	protected Timer searchTimer;

	/* previout search for the keyword */
	/* CNcomment:前一次搜索关键字 */
	protected String prevKeyword;

	/* the file is cut operation */
	/* CNcomment:文件是否剪切操作 */
	public boolean isFileCut = false;

	/* object lock */
	/* CNcomment: 对象锁 */
	protected byte[] lock = new byte[0];

	/* file data adapter */
	/* CNcomment:文件数据适配器 */
	protected FileAdapter adapter;

	/* file list collection */
	/* CNcomment:文件列表集合 */
	protected List<File> listFile;

	// begin add by qian_wei/zhou_yong 2011/10/20
	// for chmod the file
	// ISO file mount path
	protected final static String ISO_PATH = "/mnt/iso";

	protected final static String CDROM_PATH = "/mnt/sr0/sr01";

	// begin add by luoqiong 2012/12/7
	// for /mnt/iso/loopX
	protected String mISOLoopPath = "";
	// end add by luoqiong 2012/12/7

	/* socket client */
	/* CNcomment:SOCKET客户端 */
	protected static SocketClient socketClient = null;

	/* want the open file */
	/* CNcomment:需要打开的文件 */
	protected File openFile;

	/* ISO file directory */
	/* CNcomment:ISO文件所在目录 */
	protected String prevPath = "";
	// end add by qian_wei/zhou_yong 2011/10/20

	// begin add by qian_wei/xiong_cuifan 2011/11/05
	// for click three buttons show toast
	// last click on the file path
	protected String preCurrentPath = "";

	/* whether to refresh the interface by returning */
	/* CNcomment: 是否通过返回刷新界面 */
	protected boolean keyBack = true;

	/* network file flag */
	/* CNcomment:网络文件标志 */
	protected boolean isNetworkFile = false;

	/* access to the file list thread base class */
	/* CNcomment:获取文件列表线程基类 */
	public MyThreadBase thread;

	// end add by qian_wei/xiong_cuifan 2011/11/08

	/* update the list of files */
	/* CNcomment:更新文件列表 */
	public abstract void updateList(boolean flag);

	/* populate the list of files to the data container */
	/* CNcomment: 填充文件列表到数据容器 */
	public abstract void fill(File file);

	/* get the message receiver object */
	/* CNcomment: 获得消息接收对象 */
	public abstract Handler getHandler();

	/* search results actions */
	/* CNcomment:搜索结果操作 */
	public abstract void operateSearch(boolean b);

	/* get list of files */
	/* CNcomment:获得文件列表 */
	public abstract void getFiles(String path);

	// 主要判断网络是否断开
	private ConnectivityManager mConnectivityManager = null;

	private BDInfo mBDInfo;

	private DVDInfo mDVDInfo;

	protected String mBDISOName = "";

	protected String mBDISOPath = "";

	// close Toast
	public static void cancleToast() {
		if (FileUtil.getToast() != null) {
			FileUtil.getToast().cancel();
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		arrayFile = new ArrayList<File>();
		arrayDir = new ArrayList<File>();
		listFile = new ArrayList<File>();
		
		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		mBDInfo = new BDInfo();

		File file=new File("/system/lib/libdvdinfo_jni.so");
		if(file.exists())
			mDVDInfo = new DVDInfo();
		else
			mDVDInfo = null;
	}

	protected BDInfo getBDInfo() {
		return mBDInfo;
	}

	protected DVDInfo getDVDInfo() {
		return mDVDInfo;
	}

	protected void launchHiBDPlayer(String path) {
		Intent intent = new Intent();
		intent.setData(Uri.parse("bluray://"));
		intent.putExtra("path", path);
		intent.putExtra("isNetworkFile", isNetworkFile);
		int index = mBDISOName.lastIndexOf(".");
		if (index != -1) {
			intent.putExtra("BDISOName", mBDISOName.substring(0, index));
		}
		intent.putExtra("BDISOPath", mBDISOPath);
		startActivity(intent);
	}

	public boolean IsNetworkDisconnect() {
		boolean bDisconnect = false;
		if (null != mConnectivityManager) {
			NetworkInfo tmpInfo = mConnectivityManager.getActiveNetworkInfo();
			if ((null == tmpInfo) || (false == tmpInfo.isConnected())) {
//				Toast.makeText(this,
//						getString(R.string.network_error_notbrowse),
//						Toast.LENGTH_LONG).show();
				bDisconnect = true;
			}
		}
		return bDisconnect;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// previous
		case KeyEvent.KEYCODE_PAGE_UP:
			if (!pathTxt.getText().toString().equals("")) {
				if (listView.getVisibility() == View.VISIBLE) {
					if (listView.getFirstVisiblePosition() >= 11) {
						listView.setSelection(listView.getFirstVisiblePosition() - 11);
					} else {
						listView.setSelection(0);
					}
					return true;
				}
			}

			// next page
		case KeyEvent.KEYCODE_PAGE_DOWN:
			if (!pathTxt.getText().toString().equals("")) {
				if (listView.getVisibility() == View.VISIBLE) {
					listView.setSelection(listView.getLastVisiblePosition());
					return true;
				}
			}
		}
		return false;
	}

	/* menu operation */
	/* CNcomment:菜单操作 */

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		return true;
	}
	private boolean fileFilter(String keyword,String filename){
		boolean ret = false;
		PinYin4j pinyin = new PinYin4j();
		Set<String> Set = pinyin.getPinyin(filename);
		String prefixString = keyword.toString().toLowerCase();
		Iterator iterator= Set.iterator();
		while (iterator.hasNext()) {
			final String pinyin1 = iterator.next().toString().toLowerCase();
			int len = prefixString.length();
			if(len>pinyin1.length())
				break;
			if (pinyin1.startsWith(prefixString)) {
				return true;
			}
		}
		return ret;
	}
	public static boolean isChineseChar(String str){
		boolean temp = false;
		Pattern p=Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m=p.matcher(str);
		if(m.find()){
			temp =  true;
		}
		return temp;
	}
	/* search file /folder actions */
	/* CNcomment: 搜索文件/文件夹操作 */

	/* @param keyword */
	/* CNcomment: 关键字 */
	/* @param fileRoot */
	/* CNcomment:目标目录地址 */
	/* return search files set */
	/* CNcomment: return 搜索后的文件集合 */
	public void searchFile(String keyword, String fileRoot) {
		Log.w("keyword", " = " + keyword);
		long start = System.currentTimeMillis();
		long max_stack_length = 0;
		long max_array_length = 0;
		long total_num = 0;
		int lenth = keyword.length();
		Stack<Result> stack = new Stack<Result>();

		Result result = new Result();

		result.setCurrentPath(fileRoot);
		result.setCurrentFileArray(new File(fileRoot).listFiles());
		if (max_array_length < result.getCurrentFileArray().length) {
			max_array_length = result.getCurrentFileArray().length;
		}
		result.setIndex(0);
		int i = 0;

		try {
			while (true) {
				if (result.getCurrentFileArray() == null|| result.getCurrentFileArray().length == 0|| i >= result.getCurrentFileArray().length) { 					if (stack.empty()) {
					Log.w("LOBNGGGGG", " = "
							+ (System.currentTimeMillis() - start));
					Log.w("max_stack_length", " = " + max_stack_length);
					Log.w("max_array_length", " = " + max_array_length);
					Log.w("total_num", " = " + total_num);
					return;
				} else {

					/* get the top level elernent in the stack */
					/* CNcomment:获取栈中的顶层元素 */
					result = stack.pop();
					result.show();
					Log.w("stack1", " = " + stack.size());
				}
				}

				for (i = result.getIndex(); i < result.getCurrentFileArray().length; i++) {
					// for search the file is a link file
					// waiting for the dialog box disappears,the search stops
					if (progress != null && progress.isShowing()) {
						// search results for more than 10000 records ,the
						// search stops
						if (numberLimmit()) {
							File file = result.getCurrentFileArray()[i];
							total_num++;
							String name = file.getName();
							String temp = name;
							if(temp.length()>lenth){
								temp = name.substring(0, lenth);
							}
							if (file.isFile()) {
								if(isChineseChar(temp)&&!isChineseChar(keyword)){
									if(fileFilter(keyword, temp)){
										arrayFile.add(file);
									}
								}else{
									if (name.toLowerCase().startsWith(
											keyword.toLowerCase())) {

										/* file has been added */
										/* CNcomment: 文件是否已添加 */
										arrayFile.add(file);
									}
								}
							} else {
								if (dirNotExist(stack, file)) {
									if(isChineseChar(temp)&&!isChineseChar(keyword)){
										if(fileFilter(keyword, temp)){
											arrayDir.add(file);
										}
									}else{
										if (name.toLowerCase().startsWith(
												keyword.toLowerCase())) {
											// directory has been added
											// CNcomment:目录是否已添加
											arrayDir.add(file);
										}
									}
									result.setIndex(i + 1);
									// the parent directory is pushed onto the
									// stack
									// CNcomment: 将父目录压入栈中
									result.show();
									stack.push(result);
									Log.w("stack2", " = " + stack.size());
									Log.w("LENGTH"," = "+ (arrayDir.size() + arrayFile.size()));
									if (max_stack_length < stack.size()) {
										max_stack_length = stack.size();
									}
									result = new Result();
									result.setCurrentPath(file
											.getCanonicalPath());
									result.setCurrentFileArray(file.listFiles());
									if (result.getCurrentFileArray() == null) {
										// do nothing
									} else if (max_array_length < result
											.getCurrentFileArray().length) {
										max_array_length = result
												.getCurrentFileArray().length;
									}
									result.setIndex(0);
									i = 0;
									break;
								} else {
									continue;
								}
							}
						} else {
							return;
						}
					} else {
						return;
					}
				} // end for
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * folder in the stack if there are
	 * @param stack
	 *            //CNcomment:栈
	 * @param file
	 *            //current file
	 * @return // folder in the stack if there are
	 */
	private boolean dirNotExist(Stack<Result> stack, File file) {
		try {
			for (int i = stack.size() - 1; i >= 0; i--) {
				File file1 = stack.get(i).getCurrentFileArray()[stack.get(i)
				                                                .getIndex() - 1];
				if (file1.getCanonicalPath().equals(file.getCanonicalPath())) {
					return false;
				} else {
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * restriction on the number of search results
	 * @return if the number reaches the limit
	 */
	private boolean numberLimmit() {
		int size = arrayFile.size() + arrayDir.size();
		if (size >= 10000) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isSearch = false;

	/**
	 * synchronous setting isSearch
	 * @param b
	 *            isSearch value
	 */
	protected synchronized void setFlag(boolean b) {
		this.isSearch = b;
	}

	/**
	 * synchronous setting isSearch
	 * @return isSearch value
	 */
	protected synchronized boolean getFlag() {
		return isSearch;
	}

	/**
	 * escape path
	 * @param path
	 * @return // after escape path
	 */
	public static String tranString(String path) {
		String tranPath = "";
		for (int i = 0; i < path.length(); i++) {
			tranPath += "\\" + path.substring(i, i + 1);
		}

		return tranPath;
	}

	/**
	 * clear files list
	 */
	public void clearList(List<File> fileDir, List<File> file) {
		if (fileDir != null) {
			fileDir.clear();
		}
		if (file != null) {
			file.clear();
		}
	}
	/* clear the value in the file adapter */
	/* CNcomment: 清除文件适配器中的值 */
	public void clearAdapter() {
		listFile.clear();
		adapter = new FileAdapter(this, listFile);
		listView.setAdapter(adapter);
	}
	
	// for chmod the file
	/**
	 * modify the file permissions
	 * @param path
	 */
	public static void chmodFile(String path) {
		/*socketClient = new SocketClient();
		if (null != socketClient) {
			try {
				socketClient.writeMess("system chmod 777 " + tranString(path));
				socketClient.readNetResponseSync();
			} catch (Exception e) {
			}
		}*/
	}
	// for broken into the directory contains many files,click again error

	public class MyThreadBase extends Thread {
		private boolean runFlag = false;

		public synchronized void setStopRun(boolean flag) {
			this.runFlag = flag;
		}

		public synchronized boolean getStopRun() {
			return runFlag;
		}

		public void run() {
		}
	}

	/**
	 * whether the thread is running
	 * @param thrd
	 *            //thread
	 * @return
	 */
	public boolean threadBusy(Thread thrd) {
		if (thrd == null)
			return false;

		if ((thrd.getState() != Thread.State.TERMINATED)
				&& (thrd.getState() != Thread.State.NEW)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * under the program is running state decided whether to open a new thread
	 * @param thrd
	 */
	public void waitThreadToIdle(Thread thrd) {
		while (threadBusy(thrd)) {
			try {
				Log.w("THREAD", "THREAD");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
