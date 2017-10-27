package com.gvtv.android.cloud.msg;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonTools {

	public static <T> T getPerson(String jsonString, Class<T> cls) {
		T t = null;
		try {
			Gson gson = new Gson();
			t = gson.fromJson(jsonString, cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public static <T> ArrayList<T> getPersons(String jsonString, Class<T> cls) {
		ArrayList<T> list = new ArrayList<T>();
		try {
			Gson gson = new Gson();
			list = gson.fromJson(jsonString, new TypeToken<ArrayList<T>>() {
			}.getType());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static ArrayList<String> getList(String jsonString) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Gson gson = new Gson();
			list = gson.fromJson(jsonString, new TypeToken<ArrayList<String>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public static ArrayList<Map<String, Object>> listKeyMap(String jsonString) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Gson gson = new Gson();
			list = gson.fromJson(jsonString, new TypeToken<ArrayList<Map<String, Object>>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}