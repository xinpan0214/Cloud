package com.gvtv.android.cloud.util.sms;

import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Xml;

public class ExportSmsXml {
	Context context;
	public static final String SMS_URI_ALL = "content://sms/";
	private StringWriter sw = null;
	private XmlSerializer serializer;

	public ExportSmsXml(Context context) {
		this.context = context;
	}

	public void xmlStart() {
		try {
			sw = new StringWriter();
			serializer = Xml.newSerializer();
			serializer.setOutput(sw);
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "sms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String createXml() throws Exception {

		this.xmlStart();
		Cursor cursor = null;
		try {
			ContentResolver conResolver = context.getContentResolver();
			String[] projection = new String[] { SmsField.ADDRESS, SmsField.PERSON, SmsField.DATE, SmsField.PROTOCOL, 
												SmsField.READ, SmsField.STATUS,	SmsField.TYPE, SmsField.REPLY_PATH_PRESENT,
												SmsField.BODY,SmsField.LOCKED,SmsField.ERROR_CODE, SmsField.SEEN }; // type=1是收件箱，==2是发件箱;read=0表示未读，read=1表示读过，seen=0表示未读，seen=1表示读过
			Uri uri = Uri.parse(SMS_URI_ALL);
			cursor = conResolver.query(uri, projection, null, null, "_id asc");
			if (cursor.moveToFirst()) {
				// 查看数据库sms表得知 subject和service_center始终是null所以这里就不获取它们的数据了。
				String address;
				String person;
				String date;
				String protocol;
				String read;
				String status;
				String type;
				String reply_path_present;
				String body;
				String locked;
				String error_code;
				String seen;
				do {
					// 如果address == null，xml文件中是不会生成该属性的,为了保证解析时，属性能够根据索引一一对应，必须要保证所有的item标记的属性数量和顺序是一致的
					address = cursor.getString(cursor.getColumnIndex(SmsField.ADDRESS));
					if (address == null) {
						address = "";
					}
					
					person = cursor.getString(cursor.getColumnIndex(SmsField.PERSON));
					if (person == null) {
						person = "";
					}
					date = cursor.getString(cursor.getColumnIndex(SmsField.DATE));
					if (date == null) {
						date = "";
					}
					protocol = cursor.getString(cursor.getColumnIndex(SmsField.PROTOCOL));
					if (protocol == null) {// 为了便于xml解析
						protocol = "";
					}
					read = cursor.getString(cursor.getColumnIndex(SmsField.READ));
					if (read == null) {
						read = "";
					}
					status = cursor.getString(cursor.getColumnIndex(SmsField.STATUS));
					if (status == null) {
						status = "";
					}
					type = cursor.getString(cursor.getColumnIndex(SmsField.TYPE));
					if (type == null) {
						type = "";
					}
					reply_path_present = cursor.getString(cursor.getColumnIndex(SmsField.REPLY_PATH_PRESENT));
					if (reply_path_present == null) {// 为了便于XML解析
						reply_path_present = "";
					}
					body = cursor.getString(cursor.getColumnIndex(SmsField.BODY));
					if (body == null) {
						body = "";
					}
					locked = cursor.getString(cursor.getColumnIndex(SmsField.LOCKED));
					if (locked == null) {
						locked = "";
					}
					error_code = cursor.getString(cursor.getColumnIndex(SmsField.ERROR_CODE));
					if (error_code == null) {
						error_code = "";
					}
					seen = cursor.getString(cursor.getColumnIndex(SmsField.SEEN));
					if (seen == null) {
						seen = "";
					}
					// 生成xml子标记
					// 开始标记
					serializer.startTag(null, "item");
					// 加入属性
					serializer.attribute(null, SmsField.ADDRESS, address.trim());
					serializer.attribute(null, SmsField.PERSON, person.trim());
					serializer.attribute(null, SmsField.DATE, date.trim());
					serializer.attribute(null, SmsField.PROTOCOL, protocol.trim());
					serializer.attribute(null, SmsField.READ, read.trim());
					serializer.attribute(null, SmsField.STATUS, status.trim());
					serializer.attribute(null, SmsField.TYPE, type.trim());
					serializer.attribute(null, SmsField.REPLY_PATH_PRESENT, reply_path_present.trim());
					serializer.attribute(null, SmsField.BODY, body.trim());
					serializer.attribute(null, SmsField.LOCKED, locked.trim());
					serializer.attribute(null, SmsField.ERROR_CODE, error_code.trim());
					serializer.attribute(null, SmsField.SEEN, seen.trim());
					// 结束标记
					serializer.endTag(null, "item");
				} while (cursor.moveToNext());
			} else {
				return "";
			}

		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}finally {
			if(cursor != null) {
				cursor.close();//手动关闭cursor，及时回收
			}
		}
		serializer.endTag(null, "sms");
		serializer.endDocument();
		return sw.toString();
	}
}
