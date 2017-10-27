package com.gvtv.android.cloud.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularGrammarUtils {

	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	public static boolean isDeviceName(String deviceName) {
		String str = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(deviceName);
		return m.matches();
	}

	//不包含汉字[^\u4e00-\u9fa5]+
	//只能包含汉字 [\u4e00-\u9fa5]+
	public static boolean isPasswd(String pass) {
		String patternStr = "[^\u4e00-\u9fa5]{6,16}";
		Pattern p = Pattern.compile(patternStr);
		Matcher m = p.matcher(pass);
		return m.matches();
	}
	
	public static boolean checkPaswdIsSame(String pwd_1, String pwd_2) {
		if(pwd_1.equals(pwd_2)){
			return true;
		}
		return false;
	}

	public static String buildMailUrl(String email) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("http://");
		buffer.append("mail.");
		buffer.append(email.subSequence(email.indexOf('@') + 1, email.length()));
		LogUtils.getLog(RegularGrammarUtils.class).verbose("email:" + buffer.toString());
		return buffer.toString();
	}
}
