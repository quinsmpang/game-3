package com.moonic.util;

import java.sql.Types;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 缓存结果集
 * @author John
 */
public abstract class DBPRs {
	protected String tab;//表
	protected String where;//条件
	protected JSONObject colobj;//字段
	protected JSONArray coltype;//字段类型
	protected JSONArray json;//当前指向的数据项
	protected JSONArray joinarr = new JSONArray();//联合查询
	
	/**
	 * 构造
	 */
	public DBPRs(String tab, String where){
		this.tab = tab;
		this.where = where;
		colobj = DBUtil.colmap.optJSONObject(tab);
		coltype = DBUtil.coltypemap.optJSONArray(tab);
	}
	
	/**
	 * 联合查询
	 */
	public void join(String mainColumn, DBPsRs subRs, String subColumn){
		JSONArray arr = new JSONArray();
		arr.add(mainColumn);
		arr.add(subRs);
		arr.add(subColumn);
		joinarr.add(arr);
	}
	
	/**
	 * 获取联合查询对象
	 * @param index 按Join顺序的下标，从0开始
	 */
	public DBPaRs getJoinARs(int index) throws Exception {
		JSONArray join = joinarr.optJSONArray(index);
		return ((DBPsRs)join.opt(1)).query(join.optString(2)+"="+getString(join.optString(0)));
	}
	
	/**
	 * 获取值
	 */
	public String getString(String key){
		if(json == null){
			throw new RuntimeException("结果集已耗尽[TAB:"+tab+",WHERE:"+where+"]");
		}
		if(!colobj.has(key)){
			throw new RuntimeException("“"+key+"”无效标识符[TAB:"+tab+",WHERE:"+where+"]");
		}
		int index = colobj.optInt(key);
		Object obj = json.opt(index);
		int type = coltype.optInt(index);
		if(type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP){
			return obj!=null?MyTools.getTimeStr(Long.valueOf(obj.toString())):null;
		} else {
			return obj!=null?obj.toString():null;
		}
	}
	
	/**
	 * 获取值
	 */
	public int getInt(String key){
		return (int)getDouble(key);
	}
	
	/**
	 * 获取值
	 */
	public long getLong(String key){
		return (long)getDouble(key);
	}
	
	/**
	 * 获取值
	 */
	public byte getByte(String key){
		return (byte)getDouble(key);
	}
	
	/**
	 * 获取值
	 */
	public short getShort(String key){
		return (short)getDouble(key);
	}
	
	/**
	 * 获取值
	 */
	public double getDouble(String key){
		String val = getString(key);
		return val!=null?Double.valueOf(val):0;
	}
	
	/**
	 * 获取值
	 */
	public long getTime(String key){
		return MyTools.getTimeLong(getString(key));
	}
	
	/**
	 * 获取数据
	 */
	public JSONObject getJsonobj() throws Exception {
		JSONObject jsonobj = new JSONObject();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = colobj.keys();
		while(iterator.hasNext()){
			String key = iterator.next();
			jsonobj.put(key, json.optString(colobj.optInt(key)));
		}
		return jsonobj;
	}
}
