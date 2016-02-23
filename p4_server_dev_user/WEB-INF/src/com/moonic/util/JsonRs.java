package com.moonic.util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * JsonRs
 * @author John
 */
public class JsonRs {
	private JSONArray jsonarr;
	private JSONObject obj;
	private int index = -1;
	
	/**
	 * 构造
	 */
	public JsonRs(JSONArray jsonarr){
		this.jsonarr = jsonarr;
	}
	
	/**
	 * 是否有数据
	 */
	public boolean have(){
		return count()>0;
	}
	
	/**
	 * 下一元素
	 */
	public boolean next(){
		boolean result = false;
		if(index < jsonarr.length()-1){
			index++;
			obj = jsonarr.optJSONObject(index);
			result = true;
		}
		return result;
	}
	
	/**
	 * 获取总条数
	 */
	public int count(){
		if(jsonarr != null){
			return jsonarr.length();
		} else {
			return 0;
		}
	}
	
	/**
	 * 设置指针位置
	 */
	public void setRow(int ind){
		if(ind < 0 || ind > jsonarr.length()){
			throw new RuntimeException("指针超出界限("+ind+")");
		}
		if(jsonarr!=null && ind>=1){
			index = ind-1;
			obj = jsonarr.optJSONObject(index);
		}
	}
	
	/**
	 * 获取指针位置
	 */
	public int getRow(){
		return index+1;
	}
	
	/**
	 * 跳到最前
	 */
	public void beforeFirst(){
		obj = null;
		index = -1;
	}
	
	/**
	 * 跳到最后
	 */
	public void last(){
		if(jsonarr != null && jsonarr.length() > 0){
			index = jsonarr.length()-1;
			obj = jsonarr.optJSONObject(index);
		}
	}
	
	/**
	 * 获取值
	 */
	public Object get(String key){
		if(obj == null){
			throw new RuntimeException("结果集已耗尽");
		}
		return obj.opt(key);
		/*
		if(obj.has(key)){
			return obj.opt(key);
		}
		throw new RuntimeException("无效标识符“"+key+"”");
		*/
	}
	
	/**
	 * 获取值
	 */
	public String getString(String key){
		Object obj = get(key);
		return obj!=null?obj.toString():null;
	}
	
	/**
	 * 获取值
	 */
	public byte[] getBytes(String key){
		Object obj = get(key);
		return obj!=null?(byte[])obj:null;
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
}
