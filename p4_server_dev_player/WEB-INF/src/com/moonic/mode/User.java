package com.moonic.mode;

import com.moonic.mgr.ActMgr;


/**
 * 用户
 * @author John
 */
public class User extends ActMgr {
	public int uid;
	public String channel;
	
	/**
	 * 获取KEY
	 */
	public String getKey() {
		return String.valueOf(uid);
	}
}
