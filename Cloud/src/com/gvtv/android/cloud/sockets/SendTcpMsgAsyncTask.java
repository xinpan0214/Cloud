package com.gvtv.android.cloud.sockets;

import android.content.Context;
import android.os.AsyncTask;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.NetWorkUtils;
import com.gvtv.android.cloud.util.ToastUtil;

public class SendTcpMsgAsyncTask {
	private SocketClient mClient;
	private SendAsyncTask mTask;
	private OnSendendListener mListener;
	private byte[] msgBuffer;
	private Context mContext;
	private int time;
	public interface OnSendendListener {
		void isSendend();
	}

	public void setOnSendScuessListener(OnSendendListener listener) {
		this.mListener = listener;
	}

	public SendTcpMsgAsyncTask(byte[] msgBuffer, int client,OnSendendListener mListener) {
		mContext = CloudApplication.getInstance();
		mClient = SocketClient.getInstace(client);
		this.msgBuffer = msgBuffer;
		this.mListener = mListener;
	}

	// 发送
	public void send() {
		if (NetWorkUtils.isNetAvailable(mContext)) {//如果网络可用
			mTask = new SendAsyncTask();
			mTask.execute();
		} else {
			if (mListener != null){
				mListener.isSendend();
			}
			ToastUtil.getToastUtils().showToast(mContext, mContext.getResources().getString(R.string.network_not_available));
		}
	}

	// 停止
	public void stop() {
		if (mTask != null)
			mTask.cancel(true);
	}

	class SendAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... message) {
			
			boolean result = false;
			try {
				mClient.connect();
				result = mClient.send(msgBuffer);
			} catch (Exception e) {
				LogUtils.getLog(getClass()).error(e.toString());
				if(mClient.client_curr == SocketClient.CLIENT_BUSINESS){
					CloudApplication.bizAddes.clear();
				}else if(mClient.client_curr == SocketClient.CLIENT_LOAD){
					CloudApplication.loadAddes.clear();
				}
				result = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			LogUtils.getLog(getClass()).info("send msg result:"+result + ",time: " + time);
			if (!result && mListener != null) {
				mListener.isSendend();
			}
			super.onPostExecute(result);
		}
	}
	
	
//	public byte[] getSecret_key_req_buf() throws UnsupportedEncodingException, Exception{
//		ServerAddress bizAddr = new ServerAddress();
//		bizAddr.setserverIP(PreferenceUtils.getBizIP(CloudApplication.getInstance()));
//		bizAddr.setserverPort((short)(PreferenceUtils.getBizTCPPort(CloudApplication.getInstance())));
//		bizAddr.setServerUDPPort((short)(PreferenceUtils.getBizUDPPort(CloudApplication.getInstance())));
//		if(bizAddr.getserverPort() != 0){
//			CloudApplication.bizAddes.add(bizAddr);
//		}
//		
//		CloudApplication.aeskey = StringUtils.getRandomCharAndNum(32);
//		CloudApplication.requestID_relogin += 2;
//		LogUtils.getLog(SocketUtils.class).verbose("aeskey: " + CloudApplication.aeskey);
//		byte[] buf_key_agree_req = null;
//		byte[] buf_key_agree = RSACrypto.encrypt(
//				RSACrypto.loadPublicKey(RSACrypto.getPublicKey(mContext)),
//				CloudApplication.aeskey.getBytes("UTF-8"));
//		buf_key_agree_req = MessageUtils.buildSecretkeyMsg(buf_key_agree, CloudApplication.requestID_relogin);
//		return buf_key_agree_req;
//	}
}
