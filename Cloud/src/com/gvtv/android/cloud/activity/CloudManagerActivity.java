package com.gvtv.android.cloud.activity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.dataprovider.HttpCallBack;
import com.android.common.dataprovider.packet.outpacket.OutPacket;
import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.bean.TaskReturn;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.db.SqliteUtils;
import com.gvtv.android.cloud.http.HttpRequestUtils;
import com.gvtv.android.cloud.http.XmlPullParserUtils;
import com.gvtv.android.cloud.msg.JsonUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.service.MessageService;
import com.gvtv.android.cloud.sockets.SocketClient;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.util.SortUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.SlippingViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

@SuppressLint("HandlerLeak")
public class CloudManagerActivity extends BaseActivity implements OnClickListener , HttpCallBack, OnItemClickListener, OnItemLongClickListener{

	private Button mUserButton;
	private Button mSearchButton;
	private Button mSetButton;
	private Button mFileManagerButton;
	private TextView mPageTextView;
	private View mAddAppTipView;

	private static final int GRIDVIEW_COUNT = 8;
	private int mPageCount;
	private List<AppInfo> mAppList = new ArrayList<AppInfo>();
	private List<AppInfo> mAppListUpdate = new ArrayList<AppInfo>();
	private List<AppGridAdapter> mGridViewAdapters = new ArrayList<AppGridAdapter>();
	private ViewPager mViewPager;
	private List<View> mAllViews = new ArrayList<View>();
	private AppViewPagerAdapter appViewPagerAdapter;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private CloudManagerActivity instance;
	private long time;
	private AppInfo app_uninstall;
	private int poi_uninstall;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_main);
		instance = this;
		findViews();
		setListeners();
		CloudApplication.getInstance().addActivity(instance);
		init();
		AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(instance, getResources().getString(R.string.loading_please_wait));
	}
	
	
	private void findViews(){
		mSearchButton = (Button) findViewById(R.id.searchBtn);
		mSearchButton.setOnClickListener(this);
		mUserButton = (Button) findViewById(R.id.userBtn);
		mUserButton.setOnClickListener(this);
		mSetButton = (Button) findViewById(R.id.setBtn);
		mSetButton.setOnClickListener(this);
		mFileManagerButton = (Button) findViewById(R.id.fileManagerBtn);
		mFileManagerButton.setOnClickListener(this);
		mPageTextView = (TextView) findViewById(R.id.pageText);
		mAddAppTipView = findViewById(R.id.addAppTipView);
		mViewPager = (ViewPager) findViewById(R.id.slipping_viewpager);
		((SlippingViewPager) mViewPager).setBackGroud(BitmapFactory.decodeResource(getResources(), R.drawable.index_bg));
	}
	
	/*将登陆获取到的应用和本地数据库进行比较，将本地没有的插入数据库*/
	private void init(){
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.public_defaulticon01)
		.showImageForEmptyUri(R.drawable.public_defaulticon01)
		.showImageOnFail(R.drawable.public_defaulticon01)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		SqliteUtils.getInstance(instance).refreshAppByLoginInfo(CloudApplication.bindedDeviceInfo.getAppids(), CloudApplication.user_name);
	}
	
	private void setListeners(){
		mSearchButton.setOnClickListener(this);
		mUserButton.setOnClickListener(this);
		mSetButton.setOnClickListener(this);
		mFileManagerButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userBtn:
			Intent userIntent = new Intent(this, UserInfoActivity.class);
			startActivity(userIntent);
			break;
		case R.id.searchBtn:
			Intent searchIntent = new Intent(this, SearchFromDeviceActivity.class);
			startActivity(searchIntent);
			break;
		case R.id.setBtn:
			Intent setIntent = new Intent(this, DeviceControlActivity.class);
			startActivity(setIntent);
			break;
		case R.id.fileManagerBtn:
			Intent fileManagerIntent = new Intent(this,FileManagerActivity.class);
			startActivity(fileManagerIntent);
			break;

		default:
			break;
		}

	}
	
	
	@Override
	protected void onResume() {
		mAppListUpdate.clear();
		refreshViews();
		HttpRequestUtils.requestUpdateApp(instance, instance, SqliteUtils.getInstance(instance).queryAllApp(CloudApplication.user_name));
		MessageReceiver.msghList.add(instance);
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.QUERY_DEVINFO);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		} catch (Exception e) {
			LogUtils.getLog(getClass()).error(e.toString());
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof CloudManagerActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		long temp = time;
		time = System.currentTimeMillis();
		if(time - temp > 3000){
			ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.press_backkey_once_more));
		}else{
			Intent tcpIntent = new Intent(instance, MessageService.class);
			stopService(tcpIntent);
			SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
			CloudApplication.getInstance().exit();
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		CloudApplication.getInstance().removeActivity(instance);
		super.onDestroy();
	}
	

//	private void loadViews() {
//		/*init处有将数据库和服务器同步，故而可以直接查询获得所有用户应用*/
//		mAppList.clear();
//		mAppList.addAll(SqliteUtils.getInstance(instance).queryAllAppWithAppName(CloudApplication.user_name));
//		Collections.sort(mAppList, new SortUtils.SortAppByAppName());
//		// 获得list之后计算页数
//		mPageCount = (mAppList.size() + 2 + GRIDVIEW_COUNT - 1) / GRIDVIEW_COUNT;
//		mPageTextView.setText(1 + "/" + mPageCount);
//		formatTextView(mPageTextView);
//		LogUtils.getLog(getClass()).verbose("pageCount: " + mPageCount);
//		
//		mGridViewAdapters.clear();
//		mAllViews.clear();
//		LayoutInflater inflater = LayoutInflater.from(instance);
//		for (int i = 0; i < mPageCount; i++) {
//			View mView = inflater.inflate(R.layout.slipping_gridview, null);
//			GridView mGridView = (GridView) mView
//					.findViewById(R.id.slipping_gridview);
//			AppGridAdapter adapter = new AppGridAdapter(mContext, i);
//			mGridView.setAdapter(adapter);
//			mGridViewAdapters.add(adapter);
//			mAllViews.add(mView);
//			mGridView.setOnItemClickListener(instance);
//			mGridView.setOnItemLongClickListener(instance);
//		}
//		appViewPagerAdapter = new AppViewPagerAdapter();
//		mViewPager.setAdapter(appViewPagerAdapter);
//		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int arg0) {
//				mPageTextView.setText(arg0 + 1 + "/" + mPageCount);
//				formatTextView(mPageTextView);
//				mGridViewAdapters.get(arg0).notifyDataSetChanged();
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//			}
//		});
//	}
	
	
	private void refreshViews() {
		/*init处有将数据库和服务器同步，故而可以直接查询获得所有用户应用*/
		mAppList.clear();
		mAppList.addAll(SqliteUtils.getInstance(instance).queryAllAppWithAppName(CloudApplication.user_name));
		Collections.sort(mAppList, new SortUtils.SortAppByAppName());
		// 获得list之后计算页数
		int temp = mPageCount;
		mPageCount = (mAppList.size() + 2 + GRIDVIEW_COUNT - 1) / GRIDVIEW_COUNT;
		LogUtils.getLog(getClass()).verbose("pageCount: " + mPageCount);
		if(temp > mPageCount){
			int currentItem = mViewPager.getCurrentItem();
			mAllViews.remove(mPageCount);
			mGridViewAdapters.remove(mPageCount);
			appViewPagerAdapter = new AppViewPagerAdapter();
			mViewPager.setAdapter(appViewPagerAdapter);
			if(currentItem >= mPageCount){
				currentItem = mPageCount - 1;
				mViewPager.setCurrentItem(currentItem, true);
			}else{
				mViewPager.setCurrentItem(currentItem, true);
				mGridViewAdapters.get(currentItem).notifyDataSetChanged();
			}
		}else{
			LayoutInflater inflater = LayoutInflater.from(instance);
			for (int i = temp; i < mPageCount; i++) {
				View mView = inflater.inflate(R.layout.slipping_gridview, null);
				GridView mGridView = (GridView) mView
						.findViewById(R.id.slipping_gridview);
				AppGridAdapter adapter = new AppGridAdapter(instance, i);
				mGridView.setAdapter(adapter);
				mGridViewAdapters.add(adapter);
				mAllViews.add(mView);
				mGridView.setOnItemClickListener(instance);
				mGridView.setOnItemLongClickListener(instance);
			}
			if(appViewPagerAdapter != null){
				for (int i = 0; i < mPageCount; i++) {
					mGridViewAdapters.get(i).notifyDataSetChanged();
				}
				appViewPagerAdapter.notifyDataSetChanged();
			}else{
				appViewPagerAdapter = new AppViewPagerAdapter();
				mViewPager.setAdapter(appViewPagerAdapter);
				for (int i = 0; i < mPageCount; i++) {
					mGridViewAdapters.get(i).notifyDataSetChanged();
				}
				mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						mPageTextView.setText(arg0 + 1 + "/" + mPageCount);
						formatTextView(mPageTextView);
						mGridViewAdapters.get(arg0).notifyDataSetChanged();
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
					}
				});
			}
		}
		mPageTextView.setText((mViewPager.getCurrentItem() + 1) + "/" + mPageCount);
		formatTextView(mPageTextView);
	}
	

	private class AppViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mAllViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			mViewPager.removeView((View) object);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(mAllViews.get(position),
					new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT));
			return mAllViews.get(position);
		}
	}

	private class AppGridAdapter extends BaseAdapter {

		private Context mContext;
		private int mPagePosition;
		private LayoutInflater mInflater;

		private AppGridAdapter(Context context, int pagePosition) {
			this.mContext = context;
			this.mPagePosition = pagePosition;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		}

		@Override
		public int getCount() {
			if(mPagePosition < mPageCount - 1){
				return GRIDVIEW_COUNT;
			}else{
				return mAppList.size() + 2 - GRIDVIEW_COUNT * (mPageCount - 1);
			}
		}

		@Override
		public Object getItem(int position) {
			if(mPagePosition == 0){
				if(position >= 2){
					return mAppList.get(GRIDVIEW_COUNT * mPagePosition + position -2);
				}else{
					return null;
				}
			}else{
				return mAppList.get(GRIDVIEW_COUNT * mPagePosition + position -2);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int nowPosition = GRIDVIEW_COUNT * mPagePosition + position;
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.gridview_item, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.appName);
				holder.icon = (ImageView) convertView.findViewById(R.id.appIcon);
				holder.layout = (RelativeLayout) convertView.findViewById(R.id.gridview_item_layout);
				holder.appupgrade = (TextView) convertView.findViewById(R.id.appupgrade);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (mPagePosition == 0) {
				if (position == 0) {
					holder.layout
							.setBackgroundResource(R.drawable.appstore_selector);
					holder.icon.setBackgroundResource(R.drawable.icon_04index_mall);
					holder.name.setText(R.string.app_store);
					holder.name.setTextColor(mContext.getResources().getColor(R.color.white));
	
					holder.appupgrade.setVisibility(View.INVISIBLE);
				} else if (position == 1) {
					holder.layout
							.setBackgroundResource(R.drawable.down_manager_selector);
					holder.icon.setBackgroundResource(R.drawable.icon_04index_downloadmanager);
					holder.name.setText(R.string.download_manager);
					holder.name.setTextColor(mContext.getResources().getColor(R.color.white));
					holder.appupgrade.setVisibility(View.INVISIBLE);
				}else{
					AppInfo app = mAppList.get(nowPosition - 2);
					for (AppInfo appUpdate : mAppListUpdate) {
						if(app.getVersion() == null){
							holder.appupgrade.setVisibility(View.INVISIBLE);
						}else if(app.getAppcode().equals(appUpdate.getAppcode())){
							if(app.getVersion().equals(appUpdate.getVersion())){
								holder.appupgrade.setVisibility(View.INVISIBLE);
							}else{
								holder.appupgrade.setVisibility(View.VISIBLE);
							}
							break;
						}else{
							holder.appupgrade.setVisibility(View.INVISIBLE);
						}
					}
					
					holder.layout.setBackgroundResource(R.drawable.file_manager_selector);
					holder.name.setTextColor(mContext.getResources().getColor(R.color.avail_space_percent_color));	
					imageLoader.displayImage(app.getIcon(), holder.icon, options, null);
					holder.name.setText(app.getAppname());
				}
			}else{
				AppInfo app = mAppList.get(nowPosition - 2);
				for (AppInfo appUpdate : mAppListUpdate) {
					if(app.getVersion() == null){
						holder.appupgrade.setVisibility(View.INVISIBLE);
					}else if(app.getAppcode().equals(appUpdate.getAppcode())){
						if(app.getVersion().equals(appUpdate.getVersion())){
							holder.appupgrade.setVisibility(View.INVISIBLE);
						}else{
							holder.appupgrade.setVisibility(View.VISIBLE);
						}
						break;
					}else{
						holder.appupgrade.setVisibility(View.INVISIBLE);
					}
				}
				holder.layout.setBackgroundResource(R.drawable.file_manager_selector);
				imageLoader.displayImage(app.getIcon(), holder.icon, options, null);
				holder.name.setText(app.getAppname());
				holder.name.setTextColor(mContext.getResources().getColor(R.color.avail_space_percent_color));
			}
			// 当mPageCount改变时通知adapter刷新,当应用数为0时，显示应用下载提示
			if (mAppList.size() == 0) {
				mAddAppTipView.setVisibility(View.VISIBLE);
			} else {
				mAddAppTipView.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	static class ViewHolder {
		RelativeLayout layout;
		ImageView icon;
		TextView name;
		TextView appupgrade;
	}
	
	private void formatTextView(TextView tv){
		String str = tv.getText().toString().trim();
		int index = str.indexOf('/');
		int len = str.length();
		SpannableStringBuilder builder = new SpannableStringBuilder(str);	  
		//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色  
		ForegroundColorSpan rightSpan = new ForegroundColorSpan(0xFF000000);  
		ForegroundColorSpan leftSpan = new ForegroundColorSpan(0xFF2089BC);   
		builder.setSpan(leftSpan, 0, index, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);  
		builder.setSpan(rightSpan, index, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		tv.setText(builder);
	}
	
	
	
	
	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		switch (type) {		
		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			LogUtils.getLog(getClass()).verbose("recevice a msg");
			String jsonStr;
			TaskReturn taskRet;
			if(resp == MsgResponseCode.OK){
				if(msg.getRet()  == MsgResponseCode.OK){
					switch (msg.getAction()) {
					case MsgActionCode.QUERY_DEVINFO:
						jsonStr = msg.getJson_content();
						taskRet = JsonUtils.parseQUERY_DEVINFO(jsonStr);
						if(taskRet.getRet() == MsgResponseCode.OK){
							CloudApplication.bindedDeviceInfo.setDev_name(taskRet.getDev_name());
							CloudApplication.bindedDeviceInfo.setAccess_code(taskRet.getDevcode());
							CloudApplication.bindedDeviceInfo.setFree_size(taskRet.getFree());
							CloudApplication.bindedDeviceInfo.setTotal_size(taskRet.getTotal());
							CloudApplication.bindedDeviceInfo.setUsed_size(taskRet.getUsed());
							CloudApplication.bindedDeviceInfo.setSpeedlimit(taskRet.getSpeedlimit());
							CloudApplication.bindedDeviceInfo.setUpload_flag(taskRet.getUpload_flag());
							
							//PreferenceUtils.setDeviceName(mContext, taskRet.getDev_name());
							//PreferenceUtils.setDeviceCode(mContext, taskRet.getDevcode());
							//PreferenceUtils.setDeviceFreeSize(mContext, taskRet.getFree());
							//PreferenceUtils.setDeviceTotalSize(mContext, taskRet.getTotal());
							//PreferenceUtils.setDeviceUsedSize(mContext, taskRet.getUsed());
							PreferenceUtils.setSpeedlimit(instance, taskRet.getSpeedlimit());
							//PreferenceUtils.setUpload_flag(mContext, taskRet.getUpload_flag());
						}else{
							ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
						}
						break;
						
					default:
						break;
					}
					
				}else{
					ToastUtil.getToastUtils().showToastByCode(instance, msg.getRet());
				}
			}else{
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
			
		case MsgTypeCode.INSTALL_UNINSTALL_REQ:// 安装卸载应用
			if (resp == MsgResponseCode.OK) {
				SqliteUtils.getInstance(getApplicationContext()).deleteAppByAppcode(app_uninstall.getAppcode(), CloudApplication.user_name);
				refreshViews();
			} else {
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
			
		case MsgTypeCode.USER_APPID_REQ:// 获取appid信息
			if (resp == MsgResponseCode.OK) {
				
			} else {
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onCancel(OutPacket out, int token) {
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
	}

	@Override
	public void onSuccessful(ByteBuffer buffer, int bufLen, int token) {
		//LogUtils.getLog(getClass()).verbose(new String(buffer.array()));
		mAppListUpdate.clear();
		mAppListUpdate.addAll(XmlPullParserUtils.parseToAppInfo(buffer));
		Collections.sort(mAppListUpdate, new SortUtils.SortAppByAppName());
		SqliteUtils.getInstance(instance).refreshAppByUpdate(mAppListUpdate, CloudApplication.user_name);
		refreshViews();
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
	}

	@Override
	public void onNetError(int responseCode, String errorDesc, OutPacket out, int token) {
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		for (int i = 0; i < mAllViews.size(); i++) {
			if(mAllViews.get(i) == view.getParent()){
				int nowPosition = GRIDVIEW_COUNT * i + position;
				if(nowPosition == 0){
					Intent appIntent = new Intent(
							CloudManagerActivity.this,
							AppStoreActivity.class);
					startActivity(appIntent);
				}else if(nowPosition == 1){
					Intent appIntent = new Intent(
							CloudManagerActivity.this,
							DownloadManagerActivity.class);
					startActivity(appIntent);
				}else{
					AppInfo app = mAppList.get(nowPosition -2);
					for (AppInfo appUpdate : mAppListUpdate) {
						if(app.getAppcode().equals(appUpdate.getAppcode())){
							if(app.getVersion() != null && !app.getVersion().equals(appUpdate.getVersion())){
								SqliteUtils.getInstance(instance).upgradeApp(appUpdate, CloudApplication.user_name);
								mAppList.remove(nowPosition -2);
								mAppList.add(nowPosition -2, appUpdate);
								mGridViewAdapters.get(i).notifyDataSetChanged();
							}else if(app.getVersion() == null){
								SqliteUtils.getInstance(instance).upgradeApp(appUpdate, CloudApplication.user_name);
								mAppList.remove(nowPosition -2);
								mAppList.add(nowPosition -2, appUpdate);
								mGridViewAdapters.get(i).notifyDataSetChanged();
							}
							break;
						}
					}
					Intent appIntent = new Intent(
							CloudManagerActivity.this,
							APPActivity.class);
					appIntent.putExtra(AppConst.CLOUDMANAGER_TO_APP, mAppList.get(nowPosition -2));
					startActivity(appIntent);
				}
				break;
			}
		}
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		for (int i = 0; i < mAllViews.size(); i++) {
			if(mAllViews.get(i) == view.getParent()){
				final int nowPosition = GRIDVIEW_COUNT * i + position;
				if(nowPosition == 0){
				}else if(nowPosition == 1){
				}else{
					Builder dialog = new AlertDialog.Builder(instance);
					dialog.setTitle(getResources().getString(R.string.uninstall_app));
					dialog.setCancelable(false);
					dialog.setPositiveButton(getResources().getString(R.string.app_uninstall), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							poi_uninstall = nowPosition -2;
							app_uninstall = mAppList.get(poi_uninstall);
							CloudApplication.requestID += 2;
							try {
								SocketUtils.install_uninstall_app_req(instance, app_uninstall, CloudApplication.requestID, 1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					dialog.show();
				}
				break;
			}
		}
		return true;
	}
}
