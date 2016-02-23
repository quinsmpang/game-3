package com.moonic.mode;

import org.json.JSONArray;

import com.moonic.mgr.ActMgr;


/**
 * 用户
 * @author John
 */
public class User extends ActMgr {
	public String sessionid;
	
	public int uid;
	
	public String channel;
	public String username;
	
	/**
	 * 获取KEY
	 */
	public String getKey() {
		return uid+"["+username+"("+channel+")]";
	}
	
	public String converToStr(){
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(sessionid);
		jsonarr.add(uid);
		jsonarr.add(channel);
		jsonarr.add(username);
		return jsonarr.toString();
	}
	
	public static User converToUser(String str) {
		User user = null;
		try {
			JSONArray jsonarr = new JSONArray(str);
			user = new User();
			user.sessionid = jsonarr.optString(0);
			user.uid = jsonarr.optInt(1);
			user.channel = jsonarr.optString(2);
			user.username = jsonarr.optString(3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
}
