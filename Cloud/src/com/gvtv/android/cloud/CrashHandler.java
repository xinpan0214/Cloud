package com.gvtv.android.cloud;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Intent;
import android.text.TextUtils;

import com.android.common.dataprovider.util.Constant;
import com.gvtv.android.cloud.service.MessageService;
import com.gvtv.android.cloud.sockets.SocketClient;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.UiUtil;

public class CrashHandler implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private final static int TRASH_EXCEPTION = 1 << 1;
	private final static int NOLMAL_EXCEPTION = 1 << 2;
	
	// 记录异常崩溃文件名
	public static final String CRASH_EX = "trash_exception.txt";
	// 开关是否记录异常崩溃错误记录
	public static final boolean RECORD_TRASH_EX = Boolean.TRUE;
	// 开关是否记录异常错误记录
	public static final boolean RECORD_NORMAL_EX = Boolean.TRUE;
	private final static int LEVEL = (RECORD_TRASH_EX ? TRASH_EXCEPTION : 0) | (RECORD_NORMAL_EX ? NOLMAL_EXCEPTION : 0);
	private static CrashHandler mInstance;

	private CrashHandler() {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	public static CrashHandler getInstance() {
		if (mInstance == null) {
			mInstance = new CrashHandler();
		}
		return mInstance;
	}

	public void init() {
		if (mDefaultHandler == null)
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (ex != null && LEVEL >= TRASH_EXCEPTION) {
			handlerException(thread, ex);
		}
		Intent tcpIntent = new Intent(CloudApplication.getInstance(), MessageService.class);
		CloudApplication.getInstance().stopService(tcpIntent);
		SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
		CloudApplication.getInstance().exit();
		if (mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
		//CloudApplication.getInstance().exit();
		//android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	
	private void handlerException(Thread thread, final Throwable ex) {
		if (ex == null) {
			return;
		}
		final StackTraceElement[] stack = ex.getStackTrace();
		final String message = ex.getMessage();
		final String className = ex.getClass().getName();
		recordException(Constant.CRASH_EX, message, className, stack);
	}
	
	private static void recordException(String fileName, String message,
			String className, StackTraceElement[] stack) {
		//String path =  CloudApplication.getInstance().getCacheDir().getAbsolutePath();
		String path =  CloudApplication.getInstance().getExternalCacheDir().getAbsolutePath();
		LogUtils.getLog(CrashHandler.class).verbose("path: " + path);
		if (!TextUtils.isEmpty(path)) {
			try {
				OutputStream os = new FileOutputStream(path + File.separator
						+ fileName, true);
				Writer mWriter = new BufferedWriter(new OutputStreamWriter(os,
						"UTF-8"));
				final String time = UiUtil.transforTime(
						System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss");
				mWriter.write("++++++++++++++++  exception " + time
						+ "  ++++++++++++++++ \r\n");
				mWriter.write("exception          " + className);
				mWriter.write("\r\n");
				mWriter.write("description           " + message);
				mWriter.write("\r\n");
				if (stack != null && stack.length > 0) {
					for (int i = 0; i < stack.length; i++) {
						mWriter.write(time);
						mWriter.write("    ");
						mWriter.write(stack[i].toString());
						mWriter.write("\r\n");
					}
				}
				mWriter.write("----------------  exception end  ----------------");
				mWriter.write("\r\n \r\n");
				mWriter.flush();
				os.close();
				mWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}


}
