package com.gvtv.android.cloud.msg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.BackupFile;
import com.gvtv.android.cloud.bean.DeviceInfo;
import com.gvtv.android.cloud.bean.TaskReturn;
import com.gvtv.android.cloud.bean.UserInfo;
import com.gvtv.android.cloud.bean.VideoInfo;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.SortUtils;

public class JsonUtils {
	
	/*添加任务接口(ACTION:ADD_TASK)
	req:{user:user,appid:value,appname:name,task_info:[
	   {filename:filename,filesize:filesize,url:url},..]}
	res:{returns:[{return:200,filename:value,taskid:value},
	　　   {return:602,filename:value},
	　　   {return:609,filename:value},
	　　   {return:603,filename:value,appid:value,appname:value},
	　　   {return:613,filename:value,appid:value,appname:value},
	　　…]}
	return取值：
	200：成功，返回的appid为当前任务所在appid
	602：添加下载任务失败，原因不明
	603：添加下载任务失败,已经添加了该任务，重复添加，控制端可以理解为添加成功，忽略
	613: 添加下载任务失败，已经下载完该任务
	609：磁盘已满
	taskid：当添加任务成功，则返回的是产生的任务ID，否则为0
	Appid：当添加任务成功，则返回当前任务所属应用ID，当return返回613错误时，返回已添加过的任务所在应用ID。*/
	
	public static String buildADD_TASK(String user, String appid, String appname, List<VideoInfo> vFiles) throws JSONException{
		
		JSONObject add_task = new JSONObject();
		JSONArray task_info = new JSONArray();
		JSONObject task_object = null;
		add_task.put("user", user);
		add_task.put("appid", appid);
		add_task.put("appname", appname);
		for (VideoInfo videoFile : vFiles) {
			task_object = new JSONObject();
			task_object.put("filename", videoFile.getFilename());
			task_object.put("filesize", videoFile.getFilesize());
			task_object.put("url", videoFile.getPlay_url());
			task_info.put(task_object);
		}
		add_task.put("task_info", task_info);
		//LogUtils.getLog(JsonUtils.class).verbose("add_task: " + add_task.toString());
		return add_task.toString();
	}
	
//	//重新下载
	//req:	{task_info:[{taskid:taskid},.....]}
	//res:	{returns:[{taskid:taskid,return:0},.......]}eturn有两个值：610找不到该记录，200 OK
	public static String buildReDownload_TASK(List<VideoInfo> videoInfos) throws JSONException{
		JSONObject add_task = new JSONObject();
		JSONArray task_info = new JSONArray();
		JSONObject task_object = null;
		for (int i = 0; i < videoInfos.size(); i++) {
			task_object = new JSONObject();
			task_object.put("taskid", videoInfos.get(i).getTaskid());
			task_info.put(task_object);
		}
		add_task.put("task_info", task_info);
		return add_task.toString();
	}
	
	public static ArrayList<String> parseReDownload_TASK(String jsonStr){
		ArrayList<String> taskIds = new ArrayList<String>();
		try {
			JSONObject add_task = new JSONObject(jsonStr);
			JSONArray task_info = add_task.getJSONArray("returns");
			JSONObject task_object = null;
			for (int i = 0; i < task_info.length(); i++) {
				task_object = task_info.getJSONObject(i);
				if(task_object.getInt("return") == 200){
					taskIds.add(task_object.getString("taskid"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return taskIds;
	}
	
//	res:{returns:[{return:200,filename:value,taskid:value},
//	         　　   {return:602,filename:value},
//	         　　   {return:609,filename:value},
//	         　　   {return:603,filename:value,appid:value,appname:value},
//	         　　   {return:613,filename:value,appid:value,appname:value},
//	         　　…]}
//	         return取值：
//	         200：成功，返回的appid为当前任务所在appid
//	         602：添加下载任务失败，原因不明
//	         603：添加下载任务失败,已经添加了该任务，重复添加，控制端可以理解为添加成功，忽略
//	         613: 添加下载任务失败，已经下载完该任务
//	         609：磁盘已满
//	         taskid：当添加任务成功，则返回的是产生的任务ID，否则为0
//	         Appid：当添加任务成功，则返回当前任务所属应用ID，当return返回613错误时，返回已添加过的任务所在应用ID。
	public static ArrayList<TaskReturn> parseADD_TASK(String jsonStr){
		ArrayList<TaskReturn> returns = new ArrayList<TaskReturn>();
		try {
			JSONObject add_task = new JSONObject(jsonStr);
			JSONArray task_info = add_task.getJSONArray("returns");
			for (int i = 0; i < task_info.length(); i++) {
				JSONObject obj = task_info.getJSONObject(i);
				TaskReturn taskRet = new TaskReturn();
				taskRet.setRet(obj.getInt("return"));
				taskRet.setFilename(obj.getString("filename"));
				try {
					taskRet.setAppid(obj.getString("appid"));
					taskRet.setAppname(obj.getString("appname"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				returns.add(taskRet);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returns;
	}
	
	/*删除下载中任务接口(ACTION:DEL_TASK)（删除源文件）
	req:{task_info:[{taskid::value},.....]}
	res:{returns:[{taskid:value,return:value},.......]}
	return取值：
	200：成功
	611：删除下载任务失败，channel_remove()函数返回不为0
	610：删除下载任务失败,没有查找到相应任务
	601：删除下载任务失败，channel_delete()函数返回不为0*/
	
	public static String buildDEL_TASK(List<VideoInfo> vFiles) throws JSONException{
		JSONObject del_task = new JSONObject();
		JSONArray task_info = new JSONArray();
		JSONObject task_object = null;
		for (VideoInfo videoFile : vFiles) {
			task_object = new JSONObject();
			task_object.put("taskid", videoFile.getTaskid());
			task_info.put(task_object);
		}
		del_task.put("task_info", task_info);
		return del_task.toString();
	}
	
//	req:{task_info:[{taskid::value},.....]}
//	res:{returns:[{taskid:value,return:value},.......]}
//	return取值：
//	200：成功
//	611：删除下载任务失败，channel_remove()函数返回不为0
//	610：删除下载任务失败,没有查找到相应任务
//	601：删除下载任务失败，channel_delete()函数返回不为0
	public static ArrayList<TaskReturn> parseDEL_TASK(String jsonStr){
		ArrayList<TaskReturn> returns = new ArrayList<TaskReturn>();
		try {
			JSONObject del_task = new JSONObject(jsonStr);
			JSONArray task_info = del_task.getJSONArray("returns");
			for (int i = 0; i < task_info.length(); i++) {
				JSONObject obj = task_info.getJSONObject(i);
				TaskReturn taskRet = new TaskReturn();
				taskRet.setRet(obj.getInt("return"));
				taskRet.setTaskid(obj.getString("taskid"));
				returns.add(taskRet);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returns;
	}
	
	/*删除下载完成任务接口(ACTION:DEL_FIN_TASK)
	req:{del_rsc:value,task_info:[{taskid:value},.....]}
	res:{returns:[{taskid:value,return:value},.......]}
	del_rsc取值：0为不删除源文件；1为删除源文件
	return取值：
	200：成功
	610：删除下载任务失败,没有查找到相应任务
	201：删除任务成功，但为找到源文件*/
	
	public static String buildDEL_FIN_TASK(int del_rsc, List<VideoInfo> vFiles) throws JSONException{
		JSONObject del_fin_task = new JSONObject();
		JSONArray task_info = new JSONArray();
		JSONObject task_object = null;
		del_fin_task.put("del_rsc", del_rsc);
		for (VideoInfo videoFile : vFiles) {
			task_object = new JSONObject();
			task_object.put("taskid", videoFile.getTaskid());
			task_info.put(task_object);
		}
		del_fin_task.put("task_info", task_info);
		return del_fin_task.toString();
	}
	
	public static List<TaskReturn> parseDEL_FIN_TASK(String jsonStr){
		List<TaskReturn> returns = new ArrayList<TaskReturn>();
		try {
			JSONObject del_fin_task = new JSONObject(jsonStr);
			JSONArray task_info = del_fin_task.getJSONArray("returns");
			for (int i = 0; i < task_info.length(); i++) {
				JSONObject obj = task_info.getJSONObject(i);
				TaskReturn taskRet = new TaskReturn();
				taskRet.setRet(obj.getInt("return"));
				taskRet.setTaskid(obj.getString("taskid"));
				returns.add(taskRet);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returns;
	}
	
	
	/*继续下载任务接口(ACTION:RESUME_TASK)
	req:{task_info:[{taskid:value},.....]}
	　　res:{returns:[{taskid:value,return:value},.]}
	return取值：
	　　200：成功
	　　607：继续下载任务失败,没有查找到相应任务*/
	
	public static String buildRESUME_TASK(ArrayList<VideoInfo> vFiles) throws JSONException{
		JSONObject resume_task = new JSONObject();
		JSONArray task_info = new JSONArray();
		JSONObject task_object = null;
		for (VideoInfo videoFile : vFiles) {
			task_object = new JSONObject();
			task_object.put("taskid", videoFile.getTaskid());
			task_info.put(task_object);
		}
		resume_task.put("task_info", task_info);
		return resume_task.toString();
	}
	
	public static List<TaskReturn> parseRESUME_TASK(String jsonStr){
		List<TaskReturn> returns = new ArrayList<TaskReturn>();
		try {
			JSONObject resume_task = new JSONObject(jsonStr);
			JSONArray task_info = resume_task.getJSONArray("returns");
			for (int i = 0; i < task_info.length(); i++) {
				JSONObject obj = task_info.getJSONObject(i);
				TaskReturn taskRet = new TaskReturn();
				taskRet.setRet(obj.getInt("return"));
				taskRet.setTaskid(obj.getString("taskid"));
				returns.add(taskRet);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returns;
	}
	
	/*暂停下载任务接口(ACTION:PASUE_TASK)
	req:{task_info:[{taskid:value},.....]}
	res:{returns:[{taskid:value,return:value},…]}
	return取值：
	200：成功
	604：暂停下载任务失败,没有查找到相应任务
	606：暂停下载任务失败，原因不明，channel_pause()函数返回不为0*/
	
	public static String buildPASUE_TASK(ArrayList<VideoInfo> vFiles) throws JSONException{
		JSONObject pasue_task = new JSONObject();
		JSONArray task_info = new JSONArray();
		JSONObject task_object = null;
		for (VideoInfo videoFile : vFiles) {
			task_object = new JSONObject();
			task_object.put("taskid", videoFile.getTaskid());
			task_info.put(task_object);
		}
		pasue_task.put("task_info", task_info);
		return pasue_task.toString();
	}
	
	public static List<TaskReturn> parsePASUE_TASK(String jsonStr){
		List<TaskReturn> returns = new ArrayList<TaskReturn>();
		try {
			JSONObject pasue_task = new JSONObject(jsonStr);
			JSONArray task_info = pasue_task.getJSONArray("returns");
			for (int i = 0; i < task_info.length(); i++) {
				JSONObject obj = task_info.getJSONObject(i);
				TaskReturn taskRet = new TaskReturn();
				taskRet.setRet(obj.getInt("return"));
				taskRet.setTaskid(obj.getString("taskid"));
				returns.add(taskRet);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returns;
	}
	
	/*查询下载任务的统计结果（ACTION：QUERY_RSC_NUM）
	　　req:{user:user}
	　　res:{return:value,load:value,finish:value}
	  return取值：
	200：成功
	616：请求失败
	  Load:下载中列表任务数量，包括下载中、暂停、等待、失败
	  Finish:已下载列表任务数量。*/
	
	public static String buildQUERY_RSC_NUM(UserInfo user) throws JSONException{
		JSONObject query_rsc_num = new JSONObject();
		query_rsc_num.put("user", user.getUserName());	
		return query_rsc_num.toString();
	}
	
	public static TaskReturn parseQUERY_RSC_NUM(String jsonStr){
		TaskReturn ret = null;
		ret = GsonTools.getPerson(jsonStr, TaskReturn.class);
		return ret;
	}
	
	/*查询下载完成任务列表接口(ACTION: QUERY_FIN_TASK)
	　　req:{user:user,pagenum:pagenum}
	res:{return:value,pagenum:pagenum,appid:value,tasknum:values,task_info:
	　　[{taskid:value,filename:value,filesize:value,share_status:value},{taskid:value,filename:value,filesize:value,share_status:value},.....]}
	return取值：
	200：成功
	614：未找到用户
	616：请求失败*/
	
	public static String buildQUERY_FIN_TASK(String username, int pagenum) throws JSONException{
		JSONObject query_fin_task = new JSONObject();
		query_fin_task.put("user", username);
		query_fin_task.put("pagenum", pagenum);
		return query_fin_task.toString();
	}
	
	public static TaskReturn parseQUERY_FIN_TASK(String jsonStr){
		ArrayList<VideoInfo> vFiles = new ArrayList<VideoInfo>();
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_fin_task = new JSONObject(jsonStr);
			ret.setRet(query_fin_task.getInt("return"));
			ret.setPagenum(query_fin_task.getInt("pagenum"));
			ret.setTasknum(query_fin_task.getInt("tasknum"));
			try {
				JSONArray task_info = query_fin_task.getJSONArray("task_info");
				for (int i = 0; i < task_info.length(); i++) {
					JSONObject obj = task_info.getJSONObject(i);
					VideoInfo video = new VideoInfo();
					//fileid:value,filename:value,filesize:value,share_status:value
					video.setAppid(obj.getString("appid"));
					video.setAppname(obj.getString("appname"));
					video.setFilename(obj.getString("filename"));
					video.setFilesize(obj.getString("filesize").replaceAll("[^0-9]", ""));
					video.setFinish_time(obj.getLong("finish_time"));
					video.setTaskid(obj.getString("taskid"));
					video.setShare_status(obj.getInt("share_status"));
					//video.setPlay_url(obj.getString("url"));
					vFiles.add(video);
				}
				Collections.sort(vFiles, new SortUtils.SortVideoInfoByName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ret.setvFiles(vFiles);
		return ret;
	}
	
	
	/*查询下载中任务列表接口(ACTION:QUERY_TASK)
	　　req:{user:user,pagenum:pagenum,appid:value}
	res:{return:value,pagenum:pagenum,tasknum:values,task_info:
	　　[{taskid:value,filename:filename,status:status,ratio:ratio,filesize:filesize},
	　　 {taskid:value,filename:filename,status:status,ratio:ratio,filesize:filesize},
	　　  .....]}
	return取值：
	200：成功
	614：未找到用户*/
	
	public static String buildQUERY_TASK(String username, int pagenum) throws JSONException{
		JSONObject query_task = new JSONObject();
		query_task.put("user", username);
		query_task.put("pagenum", pagenum);
		return query_task.toString();
	}
	
	public static TaskReturn parseQUERY_TASK(String jsonStr){
		ArrayList<VideoInfo> vFiles = new ArrayList<VideoInfo>();
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_task = new JSONObject(jsonStr);
			ret.setRet(query_task.getInt("return"));
			ret.setPagenum(query_task.getInt("pagenum"));
			ret.setTasknum(query_task.getInt("tasknum"));
			try {
				JSONArray task_info = query_task.getJSONArray("task_info");
				for (int i = 0; i < task_info.length(); i++) {
					JSONObject obj = task_info.getJSONObject(i);
					VideoInfo video = new VideoInfo();
					//fileid:value,filename:value,filesize:value,share_status:value
					video.setAppid(obj.getString("appid"));
					video.setAppname(obj.getString("appname"));
					video.setCreate_time(obj.getLong("create_time"));
					video.setFilename(obj.getString("filename"));
					video.setFilesize(obj.getString("filesize").replaceAll("[^0-9]", ""));
					video.setRatio(obj.getInt("ratio"));
					video.setTaskid(obj.getString("taskid"));
					//video.setPlay_url(obj.getString("url"));
					video.setStatus(obj.getInt("status"));
					vFiles.add(video);
				}
				Collections.sort(vFiles, new SortUtils.SortVideoInfoByName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ret.setvFiles(vFiles);
		return ret;
	}
	
	/*查询文件所属应用列表接口（ACTION:QUERY_APPS）
	　　req:{user:user}
	res:{return:value,apps_info:
	　　[{appid:value,appname:value,tasknum:value},
	　　 {appid:value,appname:value,tasknum:value},
	　　  ......]}
	return取值：
	200：成功
	616：请求失败*/
	
	public static String buildQUERY_APPS(String username) throws JSONException{
		JSONObject query_apps = new JSONObject();
		query_apps.put("user", username);
		return query_apps.toString();
	}
	
	public static TaskReturn parseQUERY_APPS(String jsonStr){
		ArrayList<AppInfo> vGroups = new ArrayList<AppInfo>();
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_task = new JSONObject(jsonStr);
			ret.setRet(query_task.getInt("return"));
			try {
				JSONArray apps_info = query_task.getJSONArray("apps_info");
				for (int i = 0; i < apps_info.length(); i++) {
					AppInfo group = new AppInfo();
					JSONObject obj = apps_info.getJSONObject(i);
					group.setAppcode(obj.getString("appid"));
					group.setAppname(obj.getString("appname"));
					group.setTasknum(obj.getInt("tasknum"));
					vGroups.add(group);
				}
				ret.setvGroup(vGroups);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	/*查询某应用下文件列表接口(ACTION:QUERY_FILE)
	　　req:{user:user,pagenum:pagenum,appid:value}
	res:{return:value,pagenum:pagenum,file_info:[{fileid:value,filename:value,filesize:value,share_status:value},.....]}
	return取值：
	200：请求成功
	616：请求失败*/
	
	public static String buildQUERY_FILE(String username, int pagenum, String appid) throws JSONException{
		JSONObject query_file = new JSONObject();
		query_file.put("user", username);
		query_file.put("pagenum", pagenum);
		query_file.put("appid", appid);
		return query_file.toString();
	}
	
	public static TaskReturn parseQUERY_FILE(String jsonStr){
		ArrayList<VideoInfo> vFiles = new ArrayList<VideoInfo>();
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_file = new JSONObject(jsonStr);
			ret.setRet(query_file.getInt("return"));
			ret.setPagenum(query_file.getInt("pagenum"));
			ret.setAppid(query_file.getString("appid"));
			ret.setTasknum(query_file.getInt("tasknum"));
			ret.setvFiles(vFiles);
			try {
				JSONArray file_info = query_file.getJSONArray("file_info");
				for (int i = 0; i < file_info.length(); i++) {
					JSONObject obj = file_info.getJSONObject(i);
					VideoInfo video = new VideoInfo();
					//fileid:value,filename:value,filesize:value,share_status:value
					video.setFileid(obj.getString("fileid"));
					video.setFilename(obj.getString("filename"));
					video.setFilesize(obj.getString("filesize").replaceAll("[^0-9]", ""));
					video.setShare_status(obj.getInt("share_status"));
					video.setFinish_time(obj.getLong("finish_time"));
					//video.setPlay_url(obj.getString("url"));
					vFiles.add(video);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*模糊查询文件接口(ACTION:QUERY_FILE_LIKE)
	　　req:{user:user,keyword:value}
	res:{return:value,file_info:[{fileid:value,filename:value,filesize:value,share_status:value},.....]}
	return取值：
	200：请求成功
	616：请求失败*/
	
	public static String buildQUERY_FILE_LIKE(String userName, String keyword) throws JSONException{
		JSONObject query_file_like = new JSONObject();
		query_file_like.put("user", userName);
		query_file_like.put("keyword", keyword);
		return query_file_like.toString();
	}
	
	public static TaskReturn parseQUERY_FILE_LIKE(String jsonStr){
		ArrayList<VideoInfo> vFiles = new ArrayList<VideoInfo>();
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_file_like = new JSONObject(jsonStr);
			ret.setRet(query_file_like.getInt("return"));
			ret.setvFiles(vFiles);
			try {
				JSONArray file_info = query_file_like.getJSONArray("file_info");
				for (int i = 0; i < file_info.length(); i++) {
					JSONObject obj = file_info.getJSONObject(i);
					VideoInfo video = new VideoInfo();
					//video.setFileid(obj.getString("appid"));
					//video.setFileid(obj.getString("appname"));
					video.setFilename(obj.getString("filename"));
					video.setFilesize(obj.getString("filesize").replaceAll("[^0-9]", ""));
					video.setFileid(obj.getString("fileid"));
					video.setShare_status(obj.getInt("share_status"));
					//video.setTaskid(obj.getString("taskid"));
					//video.setPlay_url(obj.getString("url"));
					vFiles.add(video);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*文件管理的删除操作接口(ACTION:REMOVE_FILE)
	　　req:{file_info:[{fileid:value}, ...]}
	res:{rtn_info:[{fileid:value,result, value}]}
	return取值：
	200：删除成功
	616：请求失败*/
	
	public static String buildREMOVE_FILE(List<VideoInfo> vFiles) throws JSONException{
		JSONObject remove_file = new JSONObject();
		JSONArray task_info = new JSONArray();
		JSONObject task_object = null;
		for (VideoInfo videoFile : vFiles) {
			task_object = new JSONObject();
			task_object.put("fileid", videoFile.getFileid());
			task_info.put(task_object);
		}
		remove_file.put("file_info", task_info);
		return remove_file.toString();
	}
	
	public static Map<String, Integer>  parseREMOVE_FILE(String jsonStr){
		Map<String, Integer> rets = new HashMap<String,Integer>();
		try {
			JSONObject remove_file = new JSONObject(jsonStr);
			JSONArray rtn_info = remove_file.getJSONArray("rtn_info");
			for (int i = 0; i < rtn_info.length(); i++) {
				JSONObject rtnObj = rtn_info.getJSONObject(i);
				rets.put(rtnObj.getString("fileid"), rtnObj.getInt("result"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rets;
	}
	
	
	/*硬盘空间信息查询接口(ACTION:QUERY_DISK)
	req:
	res:{return:_value,total:_total,free:_free,used:_used}
	return取值：
	200：成功
	616：请求失败*/
	
	public static String buildQUERY_DISK(){
		return null;
	}
	
	public static TaskReturn parseQUERY_DISK(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_disk = new JSONObject(jsonStr);
			ret.setRet(query_disk.getInt("return"));
			ret.setTotal(query_disk.getInt("total"));
			ret.setFree(query_disk.getInt("free"));
			ret.setUsed(query_disk.getInt("used"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*设备信息查询接口(ACTION:QUERY_DEVINFO)
	req:
	res:{return:value,dev_name:name,devcode:value,total:_total,free:_free,used:_used,speedlimit:value,upload_flag:value}
	　　upload_flag取值：0无升级;1有升级;2下载中；3可安装
	return取值：
	200：成功
	　　616：请求失败*/
	
	public static String buildQUERY_DEVINFO(){
		return null;
	}
	
	public static TaskReturn parseQUERY_DEVINFO(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_devinfo = new JSONObject(jsonStr);
			ret.setRet(query_devinfo.getInt("return"));
			ret.setDev_name(query_devinfo.getString("dev_name"));
			ret.setDevcode(query_devinfo.getString("devcode"));
			ret.setTotal(query_devinfo.getInt("total"));
			ret.setFree(query_devinfo.getInt("free"));
			ret.setUsed(query_devinfo.getInt("used"));
			int speedlimit = query_devinfo.getInt("speedlimit");
			if(speedlimit < 0){
				speedlimit = 0;
			}
			ret.setSpeedlimit(speedlimit);
			ret.setUpload_flag(query_devinfo.getInt("update_flag"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*设备下载速度上限（ACTION:SET_SPEED_LIMIT）
	req:{speedlimit:_limits}
	　　res:
	return取值：(以结构体中的值为准)
	200：成功
	　　616：请求失败*/
	
	public static String buildSET_SPEED_LIMIT(int speedlimit) throws JSONException{
		JSONObject set_speed_limit = new JSONObject();
		set_speed_limit.put("speedlimit", speedlimit);
		return set_speed_limit.toString();
	}
	
	public static TaskReturn parseSET_SPEED_LIMIT(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject set_speed_limit = new JSONObject(jsonStr);
			ret.setRet(set_speed_limit.getInt("return"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*修改设备名称（ACTION:CHANGE_DEVNAME）
	req:{devname:devname}
	　　res:
	return取值：(以结构体中的值为准)
	200：成功
	　　616：请求失败*/
	
	public static String buildCHANGE_DEVNAME(String devname) throws JSONException{
		JSONObject change_devname = new JSONObject();
		change_devname.put("devname", devname);
		return change_devname.toString();
	}
	
	public static String parseCHANGE_DEVNAME(String jsonStr){
		String ret = null;
		try {
			JSONObject change_devname = new JSONObject(jsonStr);
			ret = change_devname.getString("devname");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	/*设备重启控制接口(ACTION:REMOTE_REBOOT)
	req:
	res:
	return取值：(以结构体中的值为准)
	200：成功
	616：请求失败*/
	
	public static String buildREMOTE_REBOOT(){
		return null;
	}
	
	public static TaskReturn parseREMOTE_REBOOT(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject remote_reboot = new JSONObject(jsonStr);
			ret.setRet(remote_reboot.getInt("return"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*设备恢复出厂接口(ACTION:CTL_REBACK)
	req:
	res:
	return取值：(以结构体中的值为准)
	200：成功
	616：请求失败*/
	
	public static String buildCTL_REBACK(){
		return null;
	}
	
	public static TaskReturn parseCTL_REBACK(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject ctl_reback = new JSONObject(jsonStr);
			ret.setRet(ctl_reback.getInt("root"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*设备产品信息接口(ACTION:QUERY_PROD_INFO)
	req:
	　　res:{return:value,hardversion:value,prodversion:value,prodname:value}
	return取值：
	200：成功
	　　616：请求失败*/
	
	public static String buildQUERY_PROD_INFO(){
		return null;
	}
	
	public static TaskReturn parseQUERY_PROD_INFO(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_prod_info = new JSONObject(jsonStr);
			ret.setRet(query_prod_info.getInt("return"));
			ret.setHardversion(query_prod_info.getString("hardversion"));
			ret.setProdversion(query_prod_info.getString("prodversion"));
			ret.setProdname(query_prod_info.getString("prodname"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*设备固件升级接口(ACTION:UPLOAD_DEV)
	req:
	res:
	return取值：(以结构体中的值为准)
	200：成功
	616：请求失败*/
	
	public static String buildUPLOAD_DEV(){
		return null;
	}
	
	public static TaskReturn parseUPLOAD_DEV(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject upload_dev = new JSONObject(jsonStr);
			ret.setRet(upload_dev.getInt("return"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*升级包下载进度查询接口：(ACTION:QUERY _UPLOAD_STATE)
	req:
	res:{return:_value,state:_state}
	return取值：
	200：成功
	616：请求失败
	_state的值为0~100；*/
	
	public static String buildQUERY_UPLOAD_STATE(){
		return null;
	}
	
	public static TaskReturn parseQUERY_UPLOAD_STATE(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_upload_state = new JSONObject(jsonStr);
			ret.setRet(query_upload_state.getInt("return"));
			ret.setState(query_upload_state.getInt("state"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*取消固件升级接口(ACTION:CANCEL_UPLOAD)
	req:
	res:
	return取值：(以结构体中的值为准)
	200：成功
	616：请求失败*/
	
	public static String buildCANCEL_UPLOAD(){
		return null;
	}
	
	public static TaskReturn parseCANCEL_UPLOAD(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_upload_state = new JSONObject(jsonStr);
			ret.setRet(query_upload_state.getInt("return"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*固件安装接口（ACTION:INSTALL_UPLOAD）
	req:
	res:
	return取值：(以结构体中的值为准)
	200：成功
	616：请求失败*/
	
	public static String buildINSTALL_UPLOAD(){
		return null;
	}
	
	public static TaskReturn parseINSTALL_UPLOAD(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject install_upload = new JSONObject(jsonStr);
			ret.setRet(install_upload.getInt("return"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*备份文件查询接口(ACTION:QUERY_BACKUP_INFO)
	req:{user:user}
	　　res:{return:value,fileinfo:[{filename:value,filesize:value,date:value,user_term:value}
	　　 {filename:value,filesize:value,date:value,user_term:value}]}
	return取值：
	200：成功
	　　616：请求失败*/
	
	public static String buildQUERY_BACKUP_INFO(String user) throws JSONException{
		JSONObject query_backup_info = new JSONObject();
		query_backup_info.put("user", user);
		return query_backup_info.toString();
	}
	
	/*res:{return:value,fileinfo:[{filename:value,filesize:value,date:value,user_term:value}
	　　 {filename:value,filesize:value,date:value,user_term:value}]}*/
	public static TaskReturn parseQUERY_BACKUP_INFO(String jsonStr){
		LogUtils.getLog(JsonUtils.class).verbose("QUERY_BACKUP_INFO: " + jsonStr);
		ArrayList<BackupFile> vFiles = new ArrayList<BackupFile>();
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject query_backup_info = new JSONObject(jsonStr);
			ret.setRet(query_backup_info.getInt("return"));
			try {
				JSONArray fileinfo = query_backup_info.getJSONArray("fileinfo");
				for (int i = 0; i < fileinfo.length(); i++) {
					JSONObject obj = fileinfo.getJSONObject(i);
					BackupFile mBackupFile = new BackupFile();
					mBackupFile.setFilename(obj.getString("filename"));
					mBackupFile.setFilesize(obj.getString("filesize"));
					mBackupFile.setDate(obj.getLong("date"));
					mBackupFile.setUser_term(obj.getString("user_term"));
					LogUtils.getLog(JsonUtils.class).error("Filename:" + mBackupFile.getFilename() + ",Filesize:" + mBackupFile.getFilesize());
					vFiles.add(mBackupFile);
				}
				Collections.sort(vFiles, new SortUtils.BackupFileSort());
				ret.setmBackupFileS(vFiles);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	/*备份请求接口（ACTION:QUEST_BACKUP)
	req:{user_term:value,user:user}
	　　res:
	return取值：(以结构体中的值为准)
	200：成功
	　　616：请求失败*/
	
	public static String buildQUEST_BACKUP() throws JSONException{
		JSONObject quest_backup = new JSONObject();
		quest_backup.put("user", CloudApplication.user_name);
		quest_backup.put("user_term", "2");
		return quest_backup.toString();
	}
	
	public static TaskReturn parseQUEST_BACKUP(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject quest_backup = new JSONObject(jsonStr);
			ret.setRet(quest_backup.getInt("return"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	/*
	 * 恢复请求接口（ACTION:QUEST_RECOVER）
	 * req:{user_term:value,user:user}
	res:
	return取值：(以结构体中的值为准)
	200：成功
	616：请求失败
	控制端与设备端直连的消息类型：
	version	Json-len	sequence	action	Return（选）	Offset(选)	Filesize(选)	Filename（选）	Json-cont
	消息标识	Json长度	包序号	动作	返回标识	数据偏移	大小	名称	Json内容
	Json-cont的内容如下：*/
	
	public static String buildQUEST_RECOVER() throws JSONException{
		JSONObject quest_recover = new JSONObject();
		quest_recover.put("user", CloudApplication.user_name);
		quest_recover.put("user_term", "2");
		return quest_recover.toString();
	}
	
	public static TaskReturn parseQUEST_RECOVER(String jsonStr){
		TaskReturn ret = new TaskReturn();
		try {
			JSONObject quest_recover = new JSONObject(jsonStr);
			ret.setRet(quest_recover.getInt("return"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
/*	绑定搜索包请求格式(ACTION:DEVICE_BIND)
	　　req:
	　　res:{dev_name:dev_name,mac:mac,access_code:access_code}
	　　包头中的rtn为200时，标识鉴权成功，rtn为618时标识鉴权失败。*/
	
	public static String buildDEVICE_BIND(){
		return null;
	}
	
	public static DeviceInfo parseDEVICE_BIND(String jsonStr){
		DeviceInfo mDeviceInfo = new DeviceInfo();
		try {
			JSONObject json = new JSONObject(jsonStr);
			mDeviceInfo.setDev_name(json.getString("dev_name"));
			mDeviceInfo.setMac(json.getString("mac"));
			mDeviceInfo.setAccess_code(json.getString("access_code"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} 
		return mDeviceInfo;
	}
	
	
	/*数据传输格式(ACTION:BACKUP _BLOCK/RECOVER _BLOCK)
	　　CONTENT中的内容不是json数据格式，而是需要传输的数据。
	传输结束（ACTION:BACKUP _OVER/RECOVER _OVER）
	　　无CONTENT的内容。
	注意：当以上两个命令（数据传输和传输结束）在直连不通的情况下，需要按照业务服务器的接口封装成转发包。*/
	
	
	//{"videoName":"上错花轿嫁错郎","colls":[{"count":1,"size":30,"url":"gvppp://PR/Ffxb1ZtpPQtA4SzPgqQ=="}]}
	public static ArrayList<VideoInfo> parseAppJson(String jsonStr ,Context mContext){
		ArrayList<VideoInfo> mVideos =  new ArrayList<VideoInfo>();
		try {
			JSONObject json = new JSONObject(jsonStr);
			String videoname = json.getString("videoName");
			//String episode_count;
			try {
				JSONArray colls = json.getJSONArray("colls");
				StringBuffer sb;
				for (int i = 0; i < colls.length(); i++) {
					JSONObject obj = colls.getJSONObject(i);
					VideoInfo video = new VideoInfo();
					sb = new StringBuffer();
					sb.append(videoname);
					sb.append("--");
					sb.append(obj.getInt("count"));
					//episode_count = mContext.getResources().getString(R.string.episode_count); 
					//episode_count = String.format(episode_count, obj.getInt("count")); 
					//sb.append(episode_count);
					
					video.setFilename(sb.toString());
					video.setFilesize(obj.getInt("size") + "");
					video.setPlay_url(obj.getString("url"));
					mVideos.add(video);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} 
		return mVideos;
	}
}
