package com.moonic.bac;

import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 发言禁MAC
 * @author John
 */
public class BannedMacBAC {
	public static String tab_banned_mac = "tab_banned_mac";
	
	/**
	 * 是否有被禁MAC
	 */
	public boolean isBannedMac(String mac, String imei) throws Exception {
		if (mac != null && !mac.equals("")) {
			DBPaRs bannedRs = DBPool.getInst().pQueryA(tab_banned_mac, "mac='"+mac+"'");
			if (bannedRs.exist()){
				return true;
			}
		}
		if (imei != null && !imei.equals("")) {
			DBPaRs bannedRs = DBPool.getInst().pQueryA(tab_banned_mac, "imei='"+imei+"'");
			if (bannedRs.exist()){
				return true;
			}
		}
		return false;
	}
	
	//----------------静态区-----------------
	
	private static BannedMacBAC instance = new BannedMacBAC();
	
	/**
	 * 获取实例
	 */
	public static BannedMacBAC getInstance() {
		return instance;
	}
}
