package com.moonic.bac;

import org.json.JSONArray;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;

import conf.LogTbName;


public class PlatformBAC {
	public static final String tab_platform = "tab_platform";
	
	/**
	 * 创建打开游戏日志
	 */
	public ReturnValue createtOpenGameLog(String data){
		try {
			JSONArray dataarr = new JSONArray(data);
			SqlString logSqlStr = new SqlString();
			logSqlStr.add("platform", dataarr.optString(0));//实为联运渠道
			logSqlStr.add("phonevendor", dataarr.optString(1));
			logSqlStr.add("phonemodel", dataarr.optString(2));
			logSqlStr.add("memory", dataarr.optInt(3));
			logSqlStr.add("mac", dataarr.optString(4));
			logSqlStr.add("imei", dataarr.optString(5));
			logSqlStr.addDateTime("savetime", MyTools.getTimeStr());
			DBHelper.logInsert(LogTbName.TAB_OPENGAME_LOG(), logSqlStr);
			return new ReturnValue(true);
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	//--------------静态区--------------
	
	private static PlatformBAC instance = new PlatformBAC();
	
	public static PlatformBAC getInstance() {
		return instance;
	}
}
