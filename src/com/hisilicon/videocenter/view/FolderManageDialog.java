package com.hisilicon.videocenter.view;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.hisilicon.videocenter.R;
import com.hisilicon.videocenter.controller.HomeController;

public class FolderManageDialog extends Dialog {
	protected ListView mFolderListView;
	protected FolderManageAdapter mAdapter;
  //  protected RecyclerView.LayoutManager mLayoutManager;
    private Context context;
	public FolderManageDialog(Context context) {
	    super(context, R.style.MenuDialog);
	    this.context = context;
	    initView();
    }
	
	public FolderManageDialog(Context context, int theme) {
	    super(context, theme);
	    initView();
    }
	
	private void initView() {
		View layout = LayoutInflater.from(getContext()).inflate(R.layout.movie_folder_manage_layout, null);
		addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setContentView(layout);
		
		mFolderListView = (ListView) layout.findViewById(R.id.folder_list_recyclerView);
	//	mLayoutManager = new LinearLayoutManager(layout.getContext());
		//mFolderListView.setLayoutManager(mLayoutManager);
		//mFolderListView.addItemDecoration(new DividerItemDecoration(layout.getContext(), LinearLayoutManager.HORIZONTAL));
		mAdapter = new FolderManageAdapter(context);
		mFolderListView.setAdapter(mAdapter);
		layout.findViewById(R.id.folder_back_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hide();
			}
		});
		
		mAdapter.setOnItemClickListener(new OnItemViewClicklListener() {
			
			@Override
			public void onItemClick(View view, int pos) {
				switch (view.getId()) {
				case R.id.folder_edite:
					HomeController.getInstance().showFolderEditeDialog(mAdapter.getItem(pos));
					break;
				case R.id.folder_delete:
					HomeController.getInstance().showFolderDeleteDialog(mAdapter.getItem(pos));
					break;
				default:
					break;
				}
				
			}
		});
		
		initPosition();
	}
	
	private void initPosition() {
	    Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 703;
        lp.height = LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
	}
	
	public void setData(List list) {
		mAdapter.setData(list);
	}
	
}
