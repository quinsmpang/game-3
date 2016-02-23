package com.moonic.bac;


/**
 * 客服统计
 * @author John
 */
public class ServiceStatBAC {
	
	//--------------静态区--------------
	
	private static ServiceStatBAC instance = new ServiceStatBAC();
	
	public static ServiceStatBAC getInstance() {		
		return instance;
	}
}
