package com.gvtv.android.cloud.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gvtv.android.cloud.R;
import com.nostra13.universalimageloader.utils.L;
/**
 * 构建界面常用工具类
 */
@SuppressLint("SimpleDateFormat")
public class UiUtil {
	/**
	 * 拨打电话号码
	 * @param activity当前上下文环境
	 */
	public static void callto114(Activity activity) {
		activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:010114")));
	}

	/* ++++++++++++++++++++++++++++++ Dialog ++++++++++++++++++++++++++++++++++ */
	
//	public static void showAlertDialog(Activity act, String title, String msg, DialogInterface.OnClickListener okClick){
//		showAlertDialog(act, 0, title, msg, okClick, getCloseDialogListener());
//	}
//	public static void showAlertDialog(Activity act, String msg, DialogInterface.OnClickListener okClick){
//		showAlertDialog(act, 0, act.getString(R.string.promp), msg, okClick, getCloseDialogListener());
//	}
	
//	public static void showAlertDialog(Activity act, int iconId, String msg,
//			DialogInterface.OnClickListener okClick,
//			DialogInterface.OnClickListener cancelClick) {
//		showAlertDialog(act, 0, act.getString(R.string.promp), msg, okClick, getCloseDialogListener());
//	}
	
//	public static void showAlertDialog(Activity act, String title, String msg,
//			String ok, String cancel, DialogInterface.OnClickListener okClick,
//			DialogInterface.OnClickListener cancelClick) {
//		showAlertDialog(act, 0, title, msg, ok, cancel, okClick, cancelClick);
//	}
	
//	public static void showAlertDialog(Activity act, int iconId, String msg,
//			String ok, String cancel, DialogInterface.OnClickListener okClick,
//			DialogInterface.OnClickListener cancelClick) {
//		showAlertDialog(act, iconId, act.getString(R.string.promp), msg, ok, cancel, okClick, cancelClick);
//	}
	/**
	 * 根据参数创建提示框
	 * 
	 * @param act 当前Activity
	 * @param iconId 提示框左上角图标(0则显示为默认图标--提示消息图标)
	 * @param msg 提示框主体消息
	 * @param okClick 确认按钮监听(如果为null则不创建确认按钮)
	 * @param cancelClick 取消按钮监听(如果为null则不创建取消按钮)
	 */
//	public static void showAlertDialog(Activity act, int iconId, String title, String msg,
//			DialogInterface.OnClickListener okClick,
//			DialogInterface.OnClickListener cancelClick) {
//		AlertDialog.Builder localBuilder = new AlertDialog.Builder(act);
//		localBuilder.setMessage(CheckUtil.getString(msg));
//		iconId = (0 == iconId ? android.R.drawable.ic_dialog_info : iconId);
//		localBuilder.setIcon(iconId);
//		localBuilder.setTitle(title);
//		if (null != okClick) localBuilder.setPositiveButton(act.getString(R.string.ok), okClick);
//		if (null != cancelClick) localBuilder.setNegativeButton(act.getString(R.string.cancel), cancelClick);
//		localBuilder.create().show();
//	}

	// 根据参数创建提示框(自定义提示按钮文字)
//	public static void showAlertDialog(Activity act, int iconId, String title, String msg,
//			String ok, String cancel, DialogInterface.OnClickListener okClick,
//			DialogInterface.OnClickListener cancelClick) {
//		AlertDialog.Builder localBuilder = new AlertDialog.Builder(act);
//		localBuilder.setMessage(CheckUtil.getString(msg));
//		iconId = (0 == iconId ? android.R.drawable.ic_dialog_info : iconId);
//		localBuilder.setIcon(iconId);
//		localBuilder.setTitle(title);
//		if (null != okClick) localBuilder.setPositiveButton(ok, okClick); 
//		if (null != cancelClick) localBuilder.setNegativeButton(cancel, cancelClick); 
//		localBuilder.create().show();
//	}



	/***
	 * 注销登录
	 */
	public static void logout(Activity activity) {
		SharedPreferences.Editor editor = activity.getSharedPreferences("userInfo", 0).edit();
		editor.putLong("userId", -1);
		editor.putString("usr", "");
		editor.putString("pass", "");
		editor.putBoolean("login", false);
		editor.commit();
	}
	
	public static void logoutRegister(Activity activity){
		SharedPreferences.Editor editor = activity.getSharedPreferences("regUserInfo", 0).edit();
		editor.putBoolean("login", false);
		editor.putString("mobile", "");
		editor.putString("username", "");
		editor.putString("idcard", "");
		editor.commit();
	}

	/**
	 * 退出应用
	 */
//	public static void doExit(final Activity act) {
//		DialogInterface.OnClickListener okClick = new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				logout(act);
//				logoutRegister(act);
//				TabGroupActivity parent = (TabGroupActivity) act.getParent();
//				if(null != parent) {
//					LocalActivityManager manager = parent.getLocalActivityManager();
//					ArrayList<String> ids = ((MyApplication) act.getApplication()).getmIdList();
//					int i = ids.size() - 1;
//					while (i >= 0) {
//						if (null != ids.get(i))
//							manager.destroyActivity(ids.get(i), true);
//						i--;
//					}
//				} else {
//					act.finish();
//				}
//				android.os.Process.killProcess(android.os.Process.myPid());
//			}
//		};
//		UiUtil.showAlertDialog(act, 0, "确定要退出吗？", okClick, getCloseDialogListener());
//	}

	/**
	 * 显示连接网络选择框
	 */
//	public static void openWifiDialog(final Activity act) {
//		UiUtil.showAlertDialog(act, 0, act.getString(R.string.networkError),
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//						paramDialogInterface.dismiss();
//						act.startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
//					}
//				}, UiUtil.getCloseDialogListener());
//	}

	/**
	 * 消息提示框监听，点击关闭提示框
	 * @return DialogInterface.OnClickListener 点击事件监听类(用作关闭提示框)
	 */
//	public static DialogInterface.OnClickListener getCloseDialogListener() {
//		return new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		};
//	}

	/*++++++++++++++++++++++++++++++ TextView&RadioButton  ++++++++++++++++++++++++++++++++++*/
	/**
	 * 设置TextView中文粗体  用在setText之后
	 * @param v视图TextView
	 */
	public static void setTextBold(TextView v) {
		v.getPaint().setFakeBoldText(true);
	}
	
	/**
	 * 设置TextView删除线 用在setText时
	 * @param v 视图TextView
	 * @param start 开始设置删除线的位置
	 * @param end 结束设置删除线的位置
	 * @param str 要设置的字符串
	 * @return
	 */
	public static SpannableString getTextStrikethrough(TextView v, int start, int end, String str) {
		SpannableString nameSp = new SpannableString(str);
		nameSp.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// 删除线
		return nameSp;
	}

	/**
	 * 更新底部导航栏 的 drawableTop
	 * @param view要更新drawableTop的RadioButton
	 * @param resource图片资源ID
	 * @param context上下文环境
	 */
//	public static void updateTop(RadioButton view, int resource, Context context) {
//		int i1 = view.getHeight();
//		int i2 = view.getWidth();
//		int size = dip2px(context, 26);
//		Bitmap bmp = ImageUtils.zoomBitmap(getBitmap(resource, context), size, size);
//		view.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(context.getResources(), bmp), null, null);
//	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * @param context上下文环境
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * @param context上下文环境
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		return (int) (dipValue * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * @param context上下文环境
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * @param context上下文环境
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		return (int) (spValue * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	/*++++++++++++++++++++++++++++++ Activity ++++++++++++++++++++++++++++++++++*/
	/**
	 * 根据资源ID获取 Drawable
	 * @param resource资源ID
	 * @param context上下文环境
	 * @return Drawable
	 */
	public static Drawable getDrawable(int resource, Context context) {
		return context.getResources().getDrawable(resource);
	}

	/**
	 * 根据资源ID获取Bitmap
	 * @param resource资源ID
	 * @param context上下文环境
	 * @return Bitmap
	 */
	public static Bitmap getBitmap(int resource, Context context) {
		return UiUtil.readBitMap(context, resource);
	}

	/**
	 * 获取xml资源文件的View
	 * @param act当前Activity
	 * @param source布局文件的资源id
	 * @return View当前XML资源的View
	 */
	public static View getInflater(Activity act, int source) {
		return act.getLayoutInflater().inflate(source, null);
	}

	/***
	 * 根据商品classId创建分类图标链接
	 * @param id
	 * @return
	 */
//	public static String getProductClassUrl(Long id) {
//		return AppConstants.PRODUCTCLASS_IMAGE + id + ".png";
//	}

	/** 获取屏幕分辨率 dm.heightPixels ,dm.widthPixels */
	public static DisplayMetrics getScreenHW(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/***
	 * 得到控件在屏幕上的位置
	 * 
	 * @param v
	 *            int x = location[0]; int y = location[1];
	 * @return
	 */
	public static int[] getLocationXY(View v) {
		 int[] location = new int[2];  
		v.getLocationOnScreen(location);
		return location;
	}

	public static int[] getLocationWXY(View v) {
		int[] location = new int[2];
		v.getLocationInWindow(location);
		return location;
	}
	/**
	 * 显示日历控件（未完善）
	 * @param activity
	 * @return
	 */
//	public static PopupWindow showDatePopupWindow(Activity activity){
//		// 绑定控件资源
//		// 创建PopupWindow
//		dateWidget dateWidget = new DateWidget(activity);
//		PopupWindow pWindow = new PopupWindow(dateWidget.generateContentView(), LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
//		// 显示PopupWindow
//		pWindow.setAnimationStyle(R.style.AnimationFade);// 设置PopupWindow显示和隐藏时的动画
//		pWindow.setBackgroundDrawable(new ColorDrawable(R.color.reg_orange));
//		pWindow.setFocusable(false);
//		pWindow.setTouchable(true);
//		pWindow.setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss() {
//				
//			}
//		});
//		pWindow.setOutsideTouchable(true);
//		pWindow.showAtLocation(activity.getParent().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//		return pWindow;
//	}
	
	/**
	 * 以最省内存的方式读取本地资源的图片
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

//	/** 购物车内商品数量 */
//	public static void getCartCount(Activity activity, TextView mall_cartNum) {
//		int count = CartDBManager.getInstance(activity).getCount();
//		if (count == 0) {
//			mall_cartNum.setVisibility(View.GONE);
//		} else {
//			mall_cartNum.setVisibility(View.VISIBLE);
//			if (count > 9)
//				mall_cartNum.setGravity(Gravity.LEFT);
//			else
//				mall_cartNum.setGravity(Gravity.CENTER);
//			mall_cartNum.setText("" + count);
//		}
//	}
	
	public static TextView setstyle(TextView tv,int start, int end,int col) {
		SpannableStringBuilder spannable = new SpannableStringBuilder(tv.getText());
//		CharacterStyle span1 = new UnderlineSpan();
//		spannable.setSpan(span1, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ForegroundColorSpan span2 = new ForegroundColorSpan(col);
		spannable.setSpan(span2, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		CharacterStyle span3=new StyleSpan(android.graphics.Typeface.ITALIC); 
//		spannable.setSpan(span3, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(spannable);
		return tv;
	}
	
	/** 
     * 设置Listview的高度 
     */  
	public static void setListViewHeight(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		params.height += 10;
		listView.setLayoutParams(params);
	}

	// 软键盘是否显示{edit有一个显示即显示}
	public static boolean isActive(Activity activity, EditText... edit) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(
				Context.INPUT_METHOD_SERVICE);
		boolean b = false;
		for (int i = 0; i < edit.length; i++) {
			if (imm.isActive(edit[i])) {
				b = true;
			}
		}
		return b;
	}
	/**
	 * 为程序创建桌面快捷方式
	 */
	public static void addShortcut(Activity cx) {
		Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");// action
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, cx.getString(R.string.app_name));// 快捷方式名字
		intent.putExtra("duplicate", false); // 是否重复创建快捷方式
		Parcelable icon = Intent.ShortcutIconResource.fromContext(cx.getApplicationContext(), R.drawable.icon);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// icon
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, cx.getIntent()); // 启动界面
		cx.sendBroadcast(intent);// 发送广播
	}

	/**
	 * 删除当前应用的桌面快捷方式
	 * 
	 * @param cx
	 */
	public static void delShortcut(Context cx) {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
		// 获取当前应用名称
		String title = null;
		try {
			final PackageManager pm = cx.getPackageManager();
			title = pm.getApplicationLabel(pm.getApplicationInfo(cx.getPackageName(), PackageManager.GET_META_DATA))
					.toString();
		} catch (Exception e) {
		}
		// 快捷方式名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		Intent shortcutIntent = cx.getPackageManager().getLaunchIntentForPackage(cx.getPackageName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		cx.sendBroadcast(shortcut);
	}
	
	public static void getUsesPermission(String packageName, Context mContext){
	    try {
	      PackageManager packageManager = mContext.getPackageManager();
	      PackageInfo packageInfo=packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
	      String [] usesPermissionsArray=packageInfo.requestedPermissions;
	      //ActivityInfo[] actInfo =  packageInfo.activities;
	      for (int i = 0; i < usesPermissionsArray.length; i++) {
	        
	         //得到每个权限的名字,如:android.permission.INTERNET
	         String usesPermissionName=usesPermissionsArray[i];
	         System.out.println("usesPermissionName="+usesPermissionName);
	         
	         //通过usesPermissionName获取该权限的详细信息
	         PermissionInfo permissionInfo=packageManager.getPermissionInfo(usesPermissionName, 0);
	   
	         //获得该权限属于哪个权限组,如:网络通信
	         PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0);
	         System.out.println("permissionGroup=" + permissionGroupInfo.loadLabel(packageManager).toString());  
	        
	         //获取该权限的标签信息,比如:完全的网络访问权限
	         String permissionLabel=permissionInfo.loadLabel(packageManager).toString();
	         System.out.println("permissionLabel="+permissionLabel);
	         
	         //获取该权限的详细描述信息,比如:允许该应用创建网络套接字和使用自定义网络协议
	         //浏览器和其他某些应用提供了向互联网发送数据的途径,因此应用无需该权限即可向互联网发送数据.
	         String permissionDescription=permissionInfo.loadDescription(packageManager).toString();
	         System.out.println("permissionDescription="+permissionDescription);
	      }
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	}
	
	
	public static String getCacheDirectory(Context context) {
		String appCacheDir = null;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			appCacheDir = getExternalCacheDir(context).getAbsolutePath();
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir().getAbsolutePath();
		}
		if (appCacheDir == null) {
			L.w("Can't define system cache directory!");
			appCacheDir = context.getCacheDir().getAbsolutePath(); // retry
		}
		return appCacheDir;
	}
	
	private static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.w("Unable to create external cache directory");
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				L.i("Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}
	
	/**
	 * 得到当前手机sdcard路径
	 * 
	 * @return null/路径
	 */
	public static String getExternalStoragePath(){
		String state = android.os.Environment.getExternalStorageState();
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			return android.os.Environment.getExternalStorageDirectory().getPath();
		}
		return null;
	}
	
	private static SoftReference<Date> date_sof;
	private static SoftReference<SimpleDateFormat> dateFotmate_sof;
	/**
	 * 格式化时间
	 * @param time
	 * @param formate 需要格式化的样式 eg: yyyy-MM-dd HH:mm:ss
	 * @return 转换后的字符串
	 */
	public static String transforTime(long time, String formate) {

		Date date = null;
		if (date_sof != null) {
			date = date_sof.get();
		}
		if (date == null) {
			date = new Date();
			date_sof = null;
			date_sof = new SoftReference<Date>(date);
		}
		date.setTime(time);
		SimpleDateFormat formatter = getFormat();
		formatter.applyPattern(formate);
		String dateString = formatter.format(date);
		return dateString;
	}
	
	/**
	 * 得到一个时间格式化实例
	 * 
	 * @return
	 */
	public static SimpleDateFormat getFormat() {
		SimpleDateFormat formatter = null;
		if (dateFotmate_sof != null) {
			formatter = dateFotmate_sof.get();
		}
		if (formatter == null) {
			formatter = new SimpleDateFormat();
			dateFotmate_sof = null;
			dateFotmate_sof = new SoftReference<SimpleDateFormat>(formatter);
		}
		return formatter;
	}
}
