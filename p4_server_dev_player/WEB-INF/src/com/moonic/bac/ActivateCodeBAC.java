package com.moonic.bac;

import server.common.Tools;

import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

public class ActivateCodeBAC {
	public static String tab_activate_code = "tab_activate_code";
	
	private static String[] exemptActivateChannel = {"003"};
	
	/**
	 * 检测用户是否已激活
	 * @param channel 渠道
	 * @param username 用户名
	 */
	public boolean checkActivate(DBHelper dbHelper, String channel, String username) throws Exception {
		if (!ConfigBAC.getBoolean("needactivate")) {
			return true;
		}
		if(Tools.contain(exemptActivateChannel, channel)){
			return true;
		}
		DBPaRs channelRs = DBPool.getInst().pQueryA(ChannelBAC.tab_channel, "code="+channel);
		//tab_activate_code中的channel实际意义为platform
		return dbHelper.queryExist(tab_activate_code, "channel='"+channelRs.getString("platform")+"' and activate_user='"+username+"' and activated=1");
	}
	
	//------------------静态区--------------------
	
	private static ActivateCodeBAC instance = new ActivateCodeBAC();

	public static ActivateCodeBAC getInstance() {
		return instance;
	}
}
