package com.moonic.bac;

import server.common.Tools;

import com.moonic.util.BACException;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 系统配置
 * @author John
 */
public class ConfigBAC {
	public static String tb_config = "tb_config";
	
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
	public static String getString(String name) {
		try {
			DBPaRs confRs = DBPool.getInst().pQueryA(tb_config, "name='"+name+"'");
			if(!confRs.exist()){
				BACException.throwAndOutInstance("缺少CONFIG参数："+name);
			}
			return confRs.getString("value");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static ConfigBAC instance = new ConfigBAC();

	public static ConfigBAC getInstance() {
		return instance;
	}
}
