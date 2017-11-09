package com.hisilicon.videocenter;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.net.wifi.WifiManager;

import com.explorer.common.CommonActivity;

/**
 * Page container
 * CNcomment:页面容器
 */
@SuppressWarnings("deprecation")
public class TabBarExample extends TabActivity {
	// Tab view
	// CNcomment:标签页视图
	TabHost tabHost;

	// SAMBA Tab
	// CNcomment:SAMBA标签
	TabSpec threeTabSpec;

	// NFS Tab
	// CNcomment:NFS标签
	TabSpec fourTabSpec;

	// Tab bar
	// CNcomment:标签栏
	private static TabWidget widget;
	private IntentFilter mIntenFilter = null;
	private BroadcastReceiver mReceiver = null;

	/**
	 * Page display
	 * CNcomment:页面显示
	 */
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		
		threeTabSpec = tabHost.newTabSpec("tid3");
		threeTabSpec.setIndicator(getString(R.string.lan_tab_title));
		threeTabSpec.setContent(new Intent(this, SambaActivity.class));
		
		fourTabSpec = tabHost.newTabSpec("tid4");
		fourTabSpec.setIndicator(getString(R.string.nfs_tab_title));
		fourTabSpec.setContent(new Intent(this, NFSActivity.class));

		tabHost.addTab(threeTabSpec);
		tabHost.addTab(fourTabSpec);
		
		widget = (TabWidget) findViewById(android.R.id.tabs);

		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int count = widget.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = widget.getChildTabViewAt(i);
			view.setBackgroundResource(R.drawable.button_selector_round);
			view.getLayoutParams().height = 52;
			
			TextView text = (TextView) view.findViewById(android.R.id.title);
			text.setTextColor(getResources().getColor(R.drawable.list_item_color_selector));
			text.setBackground(null);
		//	text.setGravity(Gravity.CENTER);
			text.setTextSize(20);
			 RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) text.getLayoutParams();
	          params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0); //取消文字底边对齐
	           params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE); //设置文字居中对齐
		}

		mIntenFilter = new IntentFilter();
		mIntenFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		mIntenFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mIntenFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

		mIntenFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		//mIntenFilter.addAction(ConnectivityManager.INET_CONDITION_ACTION);

		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (0 != tabHost.getCurrentTab()) {
					boolean bIsConnect = true;
					final String action = intent.getAction();
					if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
						final NetworkInfo networkInfo = (NetworkInfo) intent
								.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
						bIsConnect = networkInfo != null
								&& networkInfo.isConnected();
					} else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
							/* || action.equals(ConnectivityManager.INET_CONDITION_ACTION)*/) {
						NetworkInfo info = (NetworkInfo) (intent
								.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO));
						bIsConnect = info.isConnected();
					}
					
					if (false == bIsConnect) {
						Toast.makeText(
								TabBarExample.this,
								getString(R.string.network_error_exitnetbrowse),
								Toast.LENGTH_LONG).show();
						tabHost.setCurrentTab(0);
					}
				}
			};
		};
		registerReceiver(mReceiver, mIntenFilter);

		// Label switching, focus on the label, refresh the interface content
		// CNcomment:标签切换时，焦点在标签上，刷新界面内容
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				switch (tabHost.getCurrentTab()) {
				case 0:
					widget.requestFocus();
					CommonActivity.cancleToast();
					SambaActivity smbActivity = (SambaActivity) getCurrentActivity();
					if (smbActivity.IsNetworkDisconnect()) {
						tabHost.setCurrentTab(0);
					} else {
						if (smbActivity.isFileCut) {
							if (!smbActivity.getPathTxt().equals("")
									&& !smbActivity.getPathTxt()
											.equals(smbActivity.getServerName())) {
								smbActivity.updateList(true);
								smbActivity.isFileCut = false;
							}
						}
					}
					break;
				case 1:
					widget.requestFocus();
					CommonActivity.cancleToast();
					NFSActivity nfsActivity = (NFSActivity) getCurrentActivity();
					if (nfsActivity.IsNetworkDisconnect()) {
						tabHost.setCurrentTab(0);
					} else {
						if (nfsActivity.isFileCut) {
							if (!nfsActivity.getPathTxt().equals("")) {
								nfsActivity.updateList(true);
								nfsActivity.isFileCut = false;
							}
						}
					}
					break;
				}
			}
		});
		
	}

	public static TabWidget getWidget() {
		return widget;
	}

	/**
	 * cancle Toast
	 * CNcomment:取消提示
	 */
	protected void onStop() {
		super.onStop();
		CommonActivity.cancleToast();
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}
