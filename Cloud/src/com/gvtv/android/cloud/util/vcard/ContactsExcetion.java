package com.gvtv.android.cloud.util.vcard;

public class ContactsExcetion extends Exception {
	private static final long serialVersionUID = -8447050160293023994L;
	public ContactsExcetion() {
    }

    public ContactsExcetion(String detailMessage) {
        super(detailMessage);
    }


    public ContactsExcetion(String message, Throwable cause) {
        super(message, cause);
    }
    public ContactsExcetion(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause);
    }
}
