package com.gvtv.android.cloud.util.sms;

public class SmsExcetion extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1430614591165544364L;
	public SmsExcetion() {
    }

    public SmsExcetion(String detailMessage) {
        super(detailMessage);
    }


    public SmsExcetion(String message, Throwable cause) {
        super(message, cause);
    }
    public SmsExcetion(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause);
    }
}
