package com.moonic.bac;

import server.config.ServerConfig;

import com.ehc.dbc.BaseActCtrl;

/**
 * 访问服务器异常日志
 * @author John
 */
public class AccServerExcBAC extends BaseActCtrl {
	public static String tbName = "tab_access_server_exc_log";
	
	/**
	 * 构造
	 */
	public AccServerExcBAC() {
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase_Log());
	}
	
	//--------------静态区---------------
	
	private static AccServerExcBAC instance = new AccServerExcBAC();
	
	/**
	 * 获取实例
	 */
	public static AccServerExcBAC getInstance() {
		return instance;
	}
}
