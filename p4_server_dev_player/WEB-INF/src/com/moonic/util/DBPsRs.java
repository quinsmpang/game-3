package com.moonic.util;

import org.json.JSONArray;

/**
 * 标准缓存结果集
 * @author John
 */
public class DBPsRs extends DBPRs {
	private JSONArray jsonarr;//结果集
	private int index;//指针
	
	/**
	 * 构造
	 */
	public DBPsRs(String tab, String where, JSONArray jsonarr){
		super(tab, where);
		this.jsonarr = jsonarr;
		index = -1;
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
	public boolean next() {
		boolean result = false;
		if(index < jsonarr.length()-1){
			index++;
			json = jsonarr.optJSONArray(index);
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
	 * 求和
	 */
	public double sum(String column) {
		int row = getRow();
		double sum = 0;
		beforeFirst();
		while(next()){
			sum += getDouble(column);
		}
		setRow(row);//还原指针位置
		return sum;
	}
	
	/**
	 * 获取联合查询对象集合
	 */
	public DBPsRs getJoinSRs(int index) throws Exception {
		JSONArray join = joinarr.optJSONArray(index);
		String subColumn = join.optString(2);
		String mainColumn = join.optString(0);
		JSONArray queryarr = new JSONArray();
		int row = getRow();
		beforeFirst();
		while(next()){
			JSONArray subarr = DBUtil.jsonQuery(tab, jsonarr, subColumn+"="+getString(mainColumn), null, 0, 0);
			MyTools.combJsonarr(queryarr, subarr);
		}
		setRow(row);//还原指针位置
		return new DBPsRs(tab, null, queryarr);
	}
	
	/**
	 * 设置指针位置
	 */
	public void setRow(int ind){
		if(ind < 0 || ind > jsonarr.length()){
			throw new RuntimeException("指针超出界限("+ind+")");
		}
		if(jsonarr!=null && ind>=0){
			index = ind-1;
			if(index >= 0){
				json = jsonarr.optJSONArray(index);		
			} else {
				json = null;
			}
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
		json = null;
		index = -1;
	}
	
	/**
	 * 跳到最后
	 */
	public void last(){
		if(jsonarr != null && jsonarr.length() > 0){
			index = jsonarr.length()-1;
			json = jsonarr.optJSONArray(index);
		}
	}
	
	/**
	 * 查询
	 */
	public DBPaRs query(String where) throws Exception {
		return new DBPaRs(tab, where, DBUtil.jsonQuery(tab, jsonarr, where, null, 0, 0));
	}
	
	/**
	 * 获取数据
	 */
	public JSONArray getJsonarr() throws Exception {
		JSONArray newarr = new JSONArray();
		int row = getRow();
		beforeFirst();
		while(next()){
			newarr.add(getJsonobj());
		}
		setRow(row);//还原指针位置
		return newarr;
	}
	
	/**
	 * 重写
	 */
	public String toString() {
		return DBUtil.getFormatStr(tab, jsonarr);
	}
}
