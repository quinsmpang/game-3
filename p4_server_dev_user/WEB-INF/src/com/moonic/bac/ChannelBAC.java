package com.moonic.bac;

import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 渠道BAC
 * @author John
 */
public class ChannelBAC {
	public static String tab_channel = "tab_channel";
	
	/**
	 * 获取渠道码
	 */
	public DBPaRs getChannelListRs(String channel) throws Exception {
		return DBPool.getInst().pQueryA(tab_channel, "code="+channel);
	}
	
	//--------------静态区--------------
	
	private static ChannelBAC instance = new ChannelBAC();
		
	public static ChannelBAC getInstance() {			
		return instance;
	}
}
