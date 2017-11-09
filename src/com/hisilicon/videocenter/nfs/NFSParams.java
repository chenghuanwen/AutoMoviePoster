package com.hisilicon.videocenter.nfs;

public class NFSParams {
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


}
