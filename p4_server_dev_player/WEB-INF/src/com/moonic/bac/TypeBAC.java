package com.moonic.bac;

import com.moonic.util.BACException;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 类型
 */
public class TypeBAC {
	
	/**
	 * 获取类型数据集 
	 */
	public DBPaRs getTypeListRs(String tab, int num) throws Exception {
		DBPaRs typeListRs = DBPool.getInst().pQueryA(tab, "num=" + num);
		if(!typeListRs.exist()) {
			BACException.throwInstance("无"+tab+"类型数据" + num);
		}
		return typeListRs;
	}
	
	//--------------静态区--------------
	
	private static TypeBAC instance = new TypeBAC();
	
	/**
	 * 获取实例
	 */
	public static TypeBAC getInstance(){
		return instance;
	}
}
