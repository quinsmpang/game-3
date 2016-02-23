package com.moonic.util;

import org.json.JSONArray;

/**
 * 单数据缓存结果集
 * @author John
 */
public class DBPaRs extends DBPRs {
	
	/**
	 * 构造
	 */
	public DBPaRs(String tab, String where, JSONArray jsonarr) {
		super(tab, where);
		this.json = jsonarr.optJSONArray(0);
	}
	
	/**
	 * 构造
	 */
	public DBPaRs(DBPsRs rs) {
		super(rs.tab, rs.where);
		if(rs.next()){
			json = rs.json;
		}
	}
	
	/**
	 * 是否有数据
	 */
	public boolean exist(){
		return json!=null;
	}
	
	/**
	 * 重写
	 */
	public String toString() {
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(json);
		return DBUtil.getFormatStr(tab, jsonarr);
	}
}
