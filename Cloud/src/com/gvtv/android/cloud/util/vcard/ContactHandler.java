package com.gvtv.android.cloud.util.vcard;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.ContactStruct.ContactMethod;
import a_vcard.android.syncml.pim.vcard.ContactStruct.OrganizationData;
import a_vcard.android.syncml.pim.vcard.ContactStruct.PhoneData;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.vcard.ContactInfo.OrganizationInfo;

/**
 * 联系人 备份/还原操作
 * 
 * @author
 * 
 */
public class ContactHandler {
	private static ContactHandler instance_ = new ContactHandler();

	/** 获取实例 */
	public static ContactHandler getInstance() {
		return instance_;
	}

	/**
	 * 查询所有contacts中的数据，获取联系人contact_id和名字
	 * 
	 * @param context
	 * @return
	 */
	public List<ContactInfo> getAllDisplayName(Activity context, ContentResolver cr) {
		List<ContactInfo> infoList = new ArrayList<ContactInfo>();
		Cursor cursorContact = cr.query(ContactsContract.Contacts.CONTENT_URI, null,null, null, null);
		/* 把全部info信息读出来，放入mapInfos,Contacts._ID为key */
		while (cursorContact.moveToNext()) {
			int id = Integer.valueOf(cursorContact.getString(cursorContact
					.getColumnIndex(android.provider.ContactsContract.Contacts._ID)));
			int flagHasPhone = Integer.valueOf(cursorContact.getString(cursorContact
					.getColumnIndex(android.provider.ContactsContract.Contacts.HAS_PHONE_NUMBER)));
			String displayName = cursorContact.getString(cursorContact
					.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME));
			ContactInfo cantact = new ContactInfo(displayName);
			cantact.setId(id);
			if (flagHasPhone == 1) {
				cantact.setHasPhoneNumber(true);
			} else {
				cantact.setHasPhoneNumber(false);
			}
			infoList.add(cantact);
		}
		return infoList;
	}

	/**
	 * 获取联系人信息
	 * 
	 * @param context
	 * @return
	 */
	public ContactInfo getContactInfo(Activity context, ContactInfo contactInfo, ContentResolver cr) {
		String contactId = String.valueOf(contactInfo.getId());
		LogUtils.getLog(getClass()).verbose("contactId: " + contactId);
		// 设置联系人电话信息
		if (contactInfo.isHasPhoneNumber()) {
			List<ContactInfo.PhoneInfo> phoneNumberList = getPhoneInfos(contactId, context);
			if (!phoneNumberList.isEmpty()) {
				contactInfo.setPhones(phoneNumberList);
			}
		}

		// 设置email信息
		List<ContactInfo.EmailInfo> emailInfoList = getEmailInfo(contactId, context);
		if (!emailInfoList.isEmpty()) {
			contactInfo.setEmail(emailInfoList);
		}
		// 设置地址信息
		List<ContactInfo.PostalInfo> postalInfoList = getPostalInfo(contactId, context);
		if (!postalInfoList.isEmpty()) {
			contactInfo.setPostal(postalInfoList);
		}
		// // 设置公司信息
		List<ContactInfo.OrganizationInfo> organizationInfoList = getOrganizationInfo(contactId,
				context);
		if (!organizationInfoList.isEmpty()) {
			contactInfo.setOrganization(organizationInfoList);
		}
		return contactInfo;
	}

	private List<ContactInfo.PhoneInfo> getPhoneInfos(String id, Context context) {
		List<ContactInfo.PhoneInfo> phoneNumberList = new ArrayList<ContactInfo.PhoneInfo>();
		Cursor phonesCursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
		if (phonesCursor != null) {
			while (phonesCursor.moveToNext()) {
				// 遍历所有电话号码
				String phoneNumber = phonesCursor.getString(phonesCursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				// 对应的联系人类型
				int type = phonesCursor.getInt(phonesCursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				String label = phonesCursor.getString(phonesCursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
				// 初始化联系人电话信息
				ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
				phoneInfo.type = type;
				phoneInfo.number = phoneNumber;
				phoneInfo.label = label;
				//LogUtils.getLog(getClass()).info("label=" + phoneInfo.label);
				//LogUtils.getLog(getClass()).info("type=" + type);
				//LogUtils.getLog(getClass()).info("number=" + phoneNumber);
				phoneNumberList.add(phoneInfo);
			}
			phonesCursor.close();
		}
		return phoneNumberList;
	}

	private List<ContactInfo.EmailInfo> getEmailInfo(String id, Context context) {
		List<ContactInfo.EmailInfo> emailList = new ArrayList<ContactInfo.EmailInfo>();
		// 获得联系人的EMAIL
		Cursor emailCur = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + id, null, null);
		if (emailCur != null) {
			while (emailCur.moveToNext()) {
				// 遍历所有的email
				String email = emailCur.getString(emailCur
						.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
				int type = emailCur.getInt(emailCur
						.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

				// 初始化联系人邮箱信息
				ContactInfo.EmailInfo emailInfo = new ContactInfo.EmailInfo();
				emailInfo.type = type; // 设置邮箱类型
				emailInfo.email = email; // 设置邮箱地址
				emailList.add(emailInfo);
			}
			emailCur.close();
		}
		return emailList;
	}

	/* 地址 */
	private List<ContactInfo.PostalInfo> getPostalInfo(String id, Context context) {
		List<ContactInfo.PostalInfo> postalList = new ArrayList<ContactInfo.PostalInfo>();

		Cursor postalCur = context.getContentResolver()
				.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null,
						ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + "=" + id,
						null, null);
		if (postalCur != null) {
			while (postalCur.moveToNext()) {
				String address = postalCur
						.getString(postalCur
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
				int type = postalCur.getInt(postalCur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

				ContactInfo.PostalInfo postalInfo = new ContactInfo.PostalInfo();
				postalInfo.type = type;
				postalInfo.address = address;
				postalList.add(postalInfo);
			}
			postalCur.close();
		}
		
		return postalList;
	}

	/* 公司 */
	private List<ContactInfo.OrganizationInfo> getOrganizationInfo(String id, Context context) {
		List<ContactInfo.OrganizationInfo> organizationList = new ArrayList<ContactInfo.OrganizationInfo>();

		// 获取该联系人组织
		Cursor organizationsCursor = context.getContentResolver().query(
				Data.CONTENT_URI,
				new String[] { Data._ID, Organization.TYPE, Organization.COMPANY,
						Organization.TITLE },
				Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
						+ Organization.CONTENT_ITEM_TYPE + "'", new String[] { id }, null);
		if (organizationsCursor != null) {
			while (organizationsCursor.moveToNext()) {
				int type = organizationsCursor.getInt(organizationsCursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
				String company = organizationsCursor.getString(organizationsCursor
						.getColumnIndex(Organization.COMPANY));
				String jobDescription = organizationsCursor.getString(organizationsCursor
						.getColumnIndex(Organization.TITLE));
				ContactInfo.OrganizationInfo organizationInfo = new ContactInfo.OrganizationInfo();
				organizationInfo.type = type;
				organizationInfo.companyName = company;
				organizationInfo.jobDescription = jobDescription;
				organizationList.add(organizationInfo);
			}
			organizationsCursor.close();
		}
		
		return organizationList;
	}

	/**
	 * 备份联系人
	 */

	@SuppressLint("SimpleDateFormat")
	public String getDateFormate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}

//	public String backupContacts(Context context, List<ContactInfo> infos ,String rootpath) {
//		String path = null;
//		if (infos == null) {
//			return path;
//		}
//		try {
//			LogUtils.getLog(getClass()).info("infos.size=" + infos.size());
//			
//			path = rootpath + "/" + AppConst.BACKUPCONTACT_NAME;
//			LogUtils.getLog(getClass()).info("filepath: " + path);
//			
//			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
//
//			VCardComposer composer = new VCardComposer();
//			for (ContactInfo info : infos) {
//				LogUtils.getLog(getClass()).info("info===name" + info.getName());
//				ContactStruct contact = new ContactStruct();
//				contact.name = info.getName();
//				List<ContactInfo.PhoneInfo> numberList = info.getPhones();
//				for (ContactInfo.PhoneInfo phoneInfo : numberList) {
//					contact.addPhone(phoneInfo.type, phoneInfo.number, phoneInfo.label, true);
//				}
//				List<ContactInfo.EmailInfo> emailList = info.getEmail();
//				for (ContactInfo.EmailInfo emailInfo : emailList) {
//					contact.addContactmethod(Contacts.KIND_EMAIL, emailInfo.type, emailInfo.email,
//							null, true);
//				}
//				List<ContactInfo.PostalInfo> postalList = info.getPostal();
//				for (ContactInfo.PostalInfo postal : postalList) {
//					contact.addContactmethod(Contacts.KIND_POSTAL, postal.type, postal.address,
//							null, true);
//				}
//				List<ContactInfo.OrganizationInfo> organizationList = info.getOrganization();
//				for (ContactInfo.OrganizationInfo organization : organizationList) {
//					contact.company = organization.companyName;
//					// contact.addOrganization(organization.type,
//					// organization.companyName,
//					// organization.jobDescription, false);
//				}
//				String vcardString = composer.createVCard(contact,
//						VCardComposer.VERSION_VCARD30_INT);
//				writer.write(vcardString);
//				writer.write("\n");
//				writer.flush();
//			}
//			writer.close();
//
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			LogUtils.getLog(getClass()).error("UnsupportedEncodingException=" + e.toString());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			LogUtils.getLog(getClass()).error("FileNotFoundException=" + e.toString());
//		} catch (VCardException e) {
//			e.printStackTrace();
//			LogUtils.getLog(getClass()).error("VCardException=" + e.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//			LogUtils.getLog(getClass()).error("IOException=" + e.toString());
//		}
//		return path;
//	}
	
	public String backupContacts(Context context, List<ContactInfo> infos) {
		StringBuffer sbvcard = new StringBuffer();
		try {
			LogUtils.getLog(getClass()).info("infos.size=" + infos.size());
			VCardComposer composer = new VCardComposer();
			for (ContactInfo info : infos) {
				LogUtils.getLog(getClass()).info("info===name" + info.getName());
				ContactStruct contact = new ContactStruct();
				contact.name = info.getName();
				List<ContactInfo.PhoneInfo> numberList = info.getPhones();
				for (ContactInfo.PhoneInfo phoneInfo : numberList) {
					contact.addPhone(phoneInfo.type, phoneInfo.number, phoneInfo.label, true);
				}
				List<ContactInfo.EmailInfo> emailList = info.getEmail();
				for (ContactInfo.EmailInfo emailInfo : emailList) {
					contact.addContactmethod(Contacts.KIND_EMAIL, emailInfo.type, emailInfo.email,
							null, true);
				}
				List<ContactInfo.PostalInfo> postalList = info.getPostal();
				for (ContactInfo.PostalInfo postal : postalList) {
					contact.addContactmethod(Contacts.KIND_POSTAL, postal.type, postal.address,
							null, true);
				}
				List<ContactInfo.OrganizationInfo> organizationList = info.getOrganization();
				for (ContactInfo.OrganizationInfo organization : organizationList) {
					contact.company = organization.companyName;
					// contact.addOrganization(organization.type,
					// organization.companyName,
					// organization.jobDescription, false);
				}
				String vcardString = composer.createVCard(contact,
						VCardComposer.VERSION_VCARD30_INT);
				sbvcard.append(vcardString);
			}

		} catch (VCardException e) {
			e.printStackTrace();
			LogUtils.getLog(getClass()).error("VCardException=" + e.toString());
		}
		return sbvcard.toString();
	}
	

	/**
	 * 获取vCard文件中的联系人信息
	 * 
	 * @return
	 */
	public List<ContactInfo> restoreContacts(String path) throws Exception {
		List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();

		VCardParser parse = new VCardParser();
		VDataBuilder builder = new VDataBuilder();

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));

		String vcardString = "";
		String line;
		while ((line = reader.readLine()) != null) {
			vcardString += line + "\n";
		}
		reader.close();
		LogUtils.getLog(getClass()).verbose(vcardString);
		boolean parsed = parse.parse(vcardString, "UTF-8", builder);

		if (!parsed) {
			throw new VCardException("Could not parse vCard file: " + path);
		}

		List<VNode> pimContacts = builder.vNodeList;

		for (VNode contact : pimContacts) {

			ContactStruct contactStruct = ContactStruct.constructContactFromVNode(contact, 1);
			// 获取备份文件中的联系人电话信息
			List<PhoneData> vcfPhoneDataList = contactStruct.phoneList;
			List<ContactInfo.PhoneInfo> phoneInfoList = new ArrayList<ContactInfo.PhoneInfo>();
			for (PhoneData phoneData : vcfPhoneDataList) {
				ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
				phoneInfo.number = phoneData.data;
				phoneInfo.type = phoneData.type;
				phoneInfoList.add(phoneInfo);
			}

			// 获取备份文件中的联系人邮箱信息和地址
			List<ContactMethod> vcfContactmethodList = contactStruct.contactmethodList;
			List<ContactInfo.EmailInfo> emailInfoList = new ArrayList<ContactInfo.EmailInfo>();
			List<ContactInfo.PostalInfo> postalInfoList = new ArrayList<ContactInfo.PostalInfo>();

			if (null != vcfContactmethodList) {
				// 存在 Email 信息
				for (ContactMethod contactMethod : vcfContactmethodList) {
					if (Contacts.KIND_EMAIL == contactMethod.kind) {
						ContactInfo.EmailInfo emailInfo = new ContactInfo.EmailInfo();
						emailInfo.email = contactMethod.data;
						emailInfo.type = contactMethod.type;
						emailInfoList.add(emailInfo);
					}
					if (Contacts.KIND_POSTAL == contactMethod.kind) {
						ContactInfo.PostalInfo postalInfo = new ContactInfo.PostalInfo();
						postalInfo.address = contactMethod.data;
						postalInfo.type = contactMethod.type;
						postalInfoList.add(postalInfo);
					}
				}
			}

			List<ContactInfo.OrganizationInfo> organizationInfoList = new ArrayList<ContactInfo.OrganizationInfo>();
			List<OrganizationData> vcfOrganizationList = contactStruct.organizationList;
			// 获取备份文件中的联系人电话信息
			if (null != vcfOrganizationList) {
				for (OrganizationData organizationData : vcfOrganizationList) {
					OrganizationInfo organizationInfo = new ContactInfo.OrganizationInfo();
					organizationInfo.companyName = organizationData.companyName;
					organizationInfo.type = organizationData.type;
					organizationInfoList.add(organizationInfo);
				}
			}
			ContactInfo info = new ContactInfo(contactStruct.name);
			if (emailInfoList.size() != 0) {
				info.setEmail(emailInfoList);
			}
			if (phoneInfoList.size() != 0) {
				info.setPhones(phoneInfoList);
			}
			if (postalInfoList.size() != 0) {
				info.setPostal(postalInfoList);
			}
			if (organizationInfoList.size() != 0) {
				info.setOrganization(organizationInfoList);
			}

			contactInfoList.add(info);
		}

		return contactInfoList;
	}
	
	/**
	 * 获取vCard文件中的联系人信息,并插入通讯录
	 * 
	 * @return
	 */
	public void restoreContacts(String path, Activity context) throws Exception {
		VCardParser parse = new VCardParser();
		VDataBuilder builder = new VDataBuilder();

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));

		String vcardString;
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		reader.close();
		vcardString = sb.toString();
		boolean parsed = parse.parse(vcardString, "UTF-8", builder);

		if (!parsed) {
			throw new VCardException("Could not parse vCard file: " + path);
		}

		List<VNode> pimContacts = builder.vNodeList;

		for (VNode contact : pimContacts) {

			ContactStruct contactStruct = ContactStruct.constructContactFromVNode(contact, 1);
			// 获取备份文件中的联系人电话信息
			List<PhoneData> vcfPhoneDataList = contactStruct.phoneList;
			List<ContactInfo.PhoneInfo> phoneInfoList = new ArrayList<ContactInfo.PhoneInfo>();
			for (PhoneData phoneData : vcfPhoneDataList) {
				ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
				phoneInfo.number = phoneData.data;
				phoneInfo.type = phoneData.type;
				phoneInfoList.add(phoneInfo);
			}

			// 获取备份文件中的联系人邮箱信息和地址
			List<ContactMethod> vcfContactmethodList = contactStruct.contactmethodList;
			List<ContactInfo.EmailInfo> emailInfoList = new ArrayList<ContactInfo.EmailInfo>();
			List<ContactInfo.PostalInfo> postalInfoList = new ArrayList<ContactInfo.PostalInfo>();

			if (null != vcfContactmethodList) {
				// 存在 Email 信息
				for (ContactMethod contactMethod : vcfContactmethodList) {
					if (Contacts.KIND_EMAIL == contactMethod.kind) {
						ContactInfo.EmailInfo emailInfo = new ContactInfo.EmailInfo();
						emailInfo.email = contactMethod.data;
						emailInfo.type = contactMethod.type;
						emailInfoList.add(emailInfo);
					}
					if (Contacts.KIND_POSTAL == contactMethod.kind) {
						ContactInfo.PostalInfo postalInfo = new ContactInfo.PostalInfo();
						postalInfo.address = contactMethod.data;
						postalInfo.type = contactMethod.type;
						postalInfoList.add(postalInfo);
					}
				}
			}

			List<ContactInfo.OrganizationInfo> organizationInfoList = new ArrayList<ContactInfo.OrganizationInfo>();
			List<OrganizationData> vcfOrganizationList = contactStruct.organizationList;
			// 获取备份文件中的联系人电话信息
			if (null != vcfOrganizationList) {
				for (OrganizationData organizationData : vcfOrganizationList) {
					OrganizationInfo organizationInfo = new ContactInfo.OrganizationInfo();
					organizationInfo.companyName = organizationData.companyName;
					organizationInfo.type = organizationData.type;
					organizationInfoList.add(organizationInfo);
				}
			}
			ContactInfo info = new ContactInfo(contactStruct.name);
			if (emailInfoList.size() != 0) {
				info.setEmail(emailInfoList);
			}
			if (phoneInfoList.size() != 0) {
				info.setPhones(phoneInfoList);
			}
			if (postalInfoList.size() != 0) {
				info.setPostal(postalInfoList);
			}
			if (organizationInfoList.size() != 0) {
				info.setOrganization(organizationInfoList);
			}
			addContacts(context, info);
			LogUtils.getLog(getClass()).info("addContacts====" + info.getName());
		}

	}
	
	
	/**
	 * 获取vCard文件中的联系人信息,并插入通讯录
	 * 
	 * @return
	 * @throws VCardException 
	 * @throws IOException 
	 * @throws ContactsExcetion 
	 */
	public void restoreContacts(byte[] vcardbyte, Activity context) throws VCardException, IOException, ContactsExcetion {
		VCardParser parse = new VCardParser();
		VDataBuilder builder = new VDataBuilder();

		String vcardString;
		vcardString = new String(vcardbyte, "UTF-8");
		boolean parsed = parse.parse(vcardString, "UTF-8", builder);

		if (!parsed) {
			throw new VCardException("Could not parse vCard file: file is empty");
		}

		List<VNode> pimContacts = builder.vNodeList;
		
		ContentResolver cr = context.getContentResolver();
		
		ArrayList<ContentValues> nameValues = new ArrayList<ContentValues>(); 
		ArrayList<ContentValues> phoneValues = new ArrayList<ContentValues>();  
		ArrayList<ContentValues> emailValues = new ArrayList<ContentValues>(); 
		ArrayList<ContentValues> postalValues = new ArrayList<ContentValues>(); 
		ArrayList<ContentValues> organizationValues = new ArrayList<ContentValues>(); 
		for (VNode contact : pimContacts) {

			ContactStruct contactStruct = ContactStruct.constructContactFromVNode(contact, 1);
			// 获取备份文件中的联系人电话信息
			List<PhoneData> vcfPhoneDataList = contactStruct.phoneList;
			List<ContactInfo.PhoneInfo> phoneInfoList = new ArrayList<ContactInfo.PhoneInfo>();
			
			if(null != vcfPhoneDataList){
				for (PhoneData phoneData : vcfPhoneDataList) {
					ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
					phoneInfo.number = phoneData.data;
					phoneInfo.type = phoneData.type;
					phoneInfoList.add(phoneInfo);
				}
			}
			

			// 获取备份文件中的联系人邮箱信息和地址
			List<ContactMethod> vcfContactmethodList = contactStruct.contactmethodList;
			List<ContactInfo.EmailInfo> emailInfoList = new ArrayList<ContactInfo.EmailInfo>();
			List<ContactInfo.PostalInfo> postalInfoList = new ArrayList<ContactInfo.PostalInfo>();

			if (null != vcfContactmethodList) {
				// 存在 Email 信息
				for (ContactMethod contactMethod : vcfContactmethodList) {
					if (Contacts.KIND_EMAIL == contactMethod.kind) {
						ContactInfo.EmailInfo emailInfo = new ContactInfo.EmailInfo();
						emailInfo.email = contactMethod.data;
						emailInfo.type = contactMethod.type;
						emailInfoList.add(emailInfo);
					}
					if (Contacts.KIND_POSTAL == contactMethod.kind) {
						ContactInfo.PostalInfo postalInfo = new ContactInfo.PostalInfo();
						postalInfo.address = contactMethod.data;
						postalInfo.type = contactMethod.type;
						postalInfoList.add(postalInfo);
					}
				}
			}

			List<ContactInfo.OrganizationInfo> organizationInfoList = new ArrayList<ContactInfo.OrganizationInfo>();
			List<OrganizationData> vcfOrganizationList = contactStruct.organizationList;
			// 获取备份文件中的联系人电话信息
			if (null != vcfOrganizationList) {
				for (OrganizationData organizationData : vcfOrganizationList) {
					OrganizationInfo organizationInfo = new ContactInfo.OrganizationInfo();
					organizationInfo.companyName = organizationData.companyName;
					organizationInfo.type = organizationData.type;
					organizationInfoList.add(organizationInfo);
				}
			}
			ContactInfo info = new ContactInfo(contactStruct.name);
			if (emailInfoList.size() != 0) {
				info.setEmail(emailInfoList);
			}
			if (phoneInfoList.size() != 0) {
				info.setPhones(phoneInfoList);
			}
			if (postalInfoList.size() != 0) {
				info.setPostal(postalInfoList);
			}
			if (organizationInfoList.size() != 0) {
				info.setOrganization(organizationInfoList);
			}
			buildContactsValues(cr, info, nameValues, phoneValues, emailValues, postalValues, organizationValues);
			LogUtils.getLog(getClass()).info("addContacts====" + info.getName());
		}
		addAllContacts(cr, nameValues, phoneValues, emailValues, postalValues, organizationValues);
		
	}
	
	public void buildContactsValues(ContentResolver cr, ContactInfo info, 
			ArrayList<ContentValues> nameValues, 
			ArrayList<ContentValues> phoneValues, 
			ArrayList<ContentValues> emailValues, 
			ArrayList<ContentValues> postalValues, 
			ArrayList<ContentValues> organizationValues) throws ContactsExcetion {
			
			ContentValues values = new ContentValues();
			// 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
			Uri rawContactUri = cr.insert(RawContacts.CONTENT_URI, values);
			if(rawContactUri == null){
				throw new ContactsExcetion();
			}
			long rawContactId = ContentUris.parseId(rawContactUri);
			
			
			// 往data表入姓名数据
			values = new ContentValues();
			values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
			values.put(StructuredName.GIVEN_NAME, info.getName());
			nameValues.add(values);
	
			// 获取联系人电话信息
			List<ContactInfo.PhoneInfo> phoneList = info.getPhones();
			/** 录入联系电话 */
			for (ContactInfo.PhoneInfo phoneInfo : phoneList) {
				values = new ContentValues();
				values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
				// 设置录入联系人电话信息
				values.put(Phone.NUMBER, phoneInfo.number);
				values.put(Phone.TYPE, phoneInfo.type);
				phoneValues.add(values);
			}
	
			// 获取联系人邮箱信息
			List<ContactInfo.EmailInfo> emailList = info.getEmail();
	
			/** 录入联系人邮箱信息 */
			for (ContactInfo.EmailInfo email : emailList) {
				values = new ContentValues();
				values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
				// 设置录入的邮箱信息
				values.put(Email.DATA, email.email);
				values.put(Email.TYPE, email.type);
				emailValues.add(values);
			}
	
			for (ContactInfo.PostalInfo postal : info.getPostal()) {
				values = new ContentValues();
				values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
				values.put(StructuredPostal.DATA1, postal.address);
				values.put(StructuredPostal.TYPE, postal.type);
				postalValues.add(values);
			}
			for (ContactInfo.OrganizationInfo organization : info.getOrganization()) {
				values = new ContentValues();
				values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
				values.put(Organization.COMPANY, organization.companyName);
				values.put(Organization.TYPE, organization.type);
				organizationValues.add(values);
			}
		}
	
	public void addAllContacts(ContentResolver cr, 
			ArrayList<ContentValues> nameValues, 
			ArrayList<ContentValues> phoneValues, 
			ArrayList<ContentValues> emailValues, 
			ArrayList<ContentValues> postalValues, 
			ArrayList<ContentValues> organizationValues) {
		// 插入姓名数据
		cr.bulkInsert(android.provider.ContactsContract.Data.CONTENT_URI,
				nameValues.toArray(new ContentValues[0]));

		// 插入电话信息
		cr.bulkInsert(android.provider.ContactsContract.Data.CONTENT_URI,
				phoneValues.toArray(new ContentValues[0]));

		// 插入邮箱信息
		cr.bulkInsert(android.provider.ContactsContract.Data.CONTENT_URI,
				emailValues.toArray(new ContentValues[0]));

		cr.bulkInsert(Data.CONTENT_URI, postalValues.toArray(new ContentValues[0]));
		cr.bulkInsert(Data.CONTENT_URI, organizationValues.toArray(new ContentValues[0]));
	}
	
	
	public void addContacts(ContentResolver cr, ContactInfo info) {
		
		ContentValues values = new ContentValues();
		// 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
		Uri rawContactUri = cr.insert(RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);
		// 往data表入姓名数据
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.GIVEN_NAME, info.getName());
		cr.insert(android.provider.ContactsContract.Data.CONTENT_URI,
				values);

		// 获取联系人电话信息
		List<ContactInfo.PhoneInfo> phoneList = info.getPhones();
		/** 录入联系电话 */
		for (ContactInfo.PhoneInfo phoneInfo : phoneList) {
			values.clear();
			values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			// 设置录入联系人电话信息
			values.put(Phone.NUMBER, phoneInfo.number);
			values.put(Phone.TYPE, phoneInfo.type);
			// 往data表入电话数据
			cr.insert(android.provider.ContactsContract.Data.CONTENT_URI,
					values);
		}

		// 获取联系人邮箱信息
		List<ContactInfo.EmailInfo> emailList = info.getEmail();

		/** 录入联系人邮箱信息 */
		for (ContactInfo.EmailInfo email : emailList) {
			values.clear();
			values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
			// 设置录入的邮箱信息
			values.put(Email.DATA, email.email);
			values.put(Email.TYPE, email.type);
			// 往data表入Email数据
			cr.insert(android.provider.ContactsContract.Data.CONTENT_URI,
					values);
		}

		for (ContactInfo.PostalInfo postal : info.getPostal()) {
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
			values.put(StructuredPostal.DATA1, postal.address);
			values.put(StructuredPostal.TYPE, postal.type);
			cr.insert(Data.CONTENT_URI, values);
		}
		for (ContactInfo.OrganizationInfo organization : info.getOrganization()) {
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
			values.put(Organization.COMPANY, organization.companyName);
			values.put(Organization.TYPE, organization.type);
			cr.insert(Data.CONTENT_URI, values);
		}
	}

	/**
	 * 向手机中录入联系人信息
	 * 
	 * @param info
	 *            要录入的联系人信息
	 */
	public void addContacts(Activity context, ContactInfo info) {
		ContentResolver cr = context.getContentResolver();
		ContentValues values = new ContentValues();
		// 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
		Uri rawContactUri = cr.insert(RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);

		// 往data表入姓名数据
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.GIVEN_NAME, info.getName());
		cr.insert(android.provider.ContactsContract.Data.CONTENT_URI,
				values);

		// 获取联系人电话信息
		List<ContactInfo.PhoneInfo> phoneList = info.getPhones();
		/** 录入联系电话 */
		for (ContactInfo.PhoneInfo phoneInfo : phoneList) {
			values.clear();
			values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			// 设置录入联系人电话信息
			values.put(Phone.NUMBER, phoneInfo.number);
			values.put(Phone.TYPE, phoneInfo.type);
			// 往data表入电话数据
			cr.insert(android.provider.ContactsContract.Data.CONTENT_URI,
					values);
		}

		// 获取联系人邮箱信息
		List<ContactInfo.EmailInfo> emailList = info.getEmail();

		/** 录入联系人邮箱信息 */
		for (ContactInfo.EmailInfo email : emailList) {
			values.clear();
			values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
			// 设置录入的邮箱信息
			values.put(Email.DATA, email.email);
			values.put(Email.TYPE, email.type);
			// 往data表入Email数据
			cr.insert(android.provider.ContactsContract.Data.CONTENT_URI,
					values);
		}

		for (ContactInfo.PostalInfo postal : info.getPostal()) {
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
			values.put(StructuredPostal.DATA1, postal.address);
			values.put(StructuredPostal.TYPE, postal.type);
			cr.insert(Data.CONTENT_URI, values);
		}
		for (ContactInfo.OrganizationInfo organization : info.getOrganization()) {
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
			values.put(Organization.COMPANY, organization.companyName);
			values.put(Organization.TYPE, organization.type);
			cr.insert(Data.CONTENT_URI, values);
		}
	}
	
	
	

}
