package com.moonic.bac;

import server.common.Tools;

import com.moonic.util.BACException;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 假概率
 * @author John
 */
public class FakeBAC {
	public static String tab_fakeodds_item = "tab_fakeodds_item";
	
	//----------------静态区----------------
	
	/**
	 * 获取值
	 */
	public static boolean getBoolean(String name) {
		return Tools.str2boolean(getString(name));
	}
	
	/**
	 * 获取值
	 */
	public static int getInt(String name) {
		return Tools.str2int(getString(name));
	}
	
	/**
	 * 获取值
	 */
	public static String getString(String confkey) {
		try {
			DBPaRs confRs = DBPool.getInst().pQueryA(tab_fakeodds_item, "confkey='"+confkey+"'");
			if(!confRs.exist()){
				BACException.throwAndOutInstance("缺少假概率参数："+confkey);
			}
			return confRs.getString("confvalue");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static FakeBAC instance = new FakeBAC();

	public static FakeBAC getInstance() {
		return instance;
	}
}
