package com.moonic.util;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.moonic.mgr.LockStor;

/**
 * 数据库缓存
 * @author John
 */
public class DBPool {
	
	/**
	 * 列表缓存数据
	 * 结构：
	 * 层次1：缓存名[TABLE_NAME+MAIN_KEY]	>>	表数据
	 * 层次2：MAIN_VALUE						>>	一条数据
	 * 层次3：数据项							>>	数据值
	 */
	private JSONObject tabpool = new JSONObject();
	/**
	 * 文本缓存数据
	 * 结构：
	 * 缓存名[TXTNAME]						>>	TXT内容
	 */
	private JSONObject txtpool = new JSONObject();
	
	public MyLog log = new MyLog(MyLog.NAME_DATE, "log_dbp", "DB_P", true, false, true, null);
	
	/**
	 * 列表缓存清单
	 */
	public ReturnValue TestA(){
		try {
			JSONArray jsonarr = tabpool.names();
			if(jsonarr == null){
				jsonarr = new JSONArray();
			}
			return new ReturnValue(true, jsonarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 文本缓存清单
	 */
	public ReturnValue TestB(){
		try {
			JSONArray jsonarr = txtpool.names();
			if(jsonarr == null){
				jsonarr = new JSONArray();
			}
			return new ReturnValue(true, jsonarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 获取列表缓存数据
	 */
	public ReturnValue Test1(String tab){
		try {
			JSONArray jsonarr = readTableFromPool(tab);
			String str = DBUtil.getFormatStr(tab, jsonarr);
			return new ReturnValue(true, str);
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 清除列表缓存数据
	 */
	public ReturnValue Test2(String tab){
		try {
			clearTableFromPool(tab);
			return new ReturnValue(true);
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 获取文本缓存数据
	 */
	public ReturnValue Test3(String key) {
		try {
			String str = readTxtFromPool(key);
			return new ReturnValue(true, str);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 清除文本缓存数据
	 */
	public ReturnValue Test4(String key) {
		clearTxtFromPool(key);
		return new ReturnValue(true, "处理成功");
	}
	
	/**
	 * 查询
	 */
	public DBPaRs pQueryA(String table, String where) throws Exception {
		return new DBPaRs(pQueryS(table, where));
	}
	
	/**
	 * 查询
	 */
	public DBPsRs pQueryS(String table) throws Exception {
		return pQueryS(table, null);
	}
	
	/**
	 * 查询
	 */
	public DBPsRs pQueryS(String table, String where) throws Exception {
		return pQueryS(table, where, null);
	}
	
	/**
	 * 查询
	 */
	public DBPsRs pQueryS(String table, String where, String order) throws Exception {
		return pQueryS(table, where, order, 0, 0);
	}
	
	/**
	 * 查询
	 */
	public DBPsRs pQueryS(String table, String where, String order, int rows) throws Exception {
		return pQueryS(table, where, order, 1, rows);
	}
	
	/**
	 * 查询
	 * @param table 表
	 * @param where 条件 忽略大小写 限制：允许AND,OR连接和小括号嵌套，仅允许Q_COMP_SPLIT中的比较符(例：COLUMN1=1 AND COLUMN2>1 AND COLUMN3 IS NULL)
	 * @param order 排序 忽略大小写 限制：仅允许单条件排序(例1：ID 例2：ID DESC 例3：ASC)
	 * @param minRow 起始行号 行号从1开始
	 * @param maxRow 终止行号
	 */
	public DBPsRs pQueryS(String table, String where, String order, int minRow, int maxRow) throws Exception {
		JSONArray json = DBUtil.jsonQuery(table, readTableFromPool(table), where, order, minRow, maxRow);
		return new DBPsRs(table, where, json);
	}
	
	/**
	 * 从列表数据缓存中获取指定列表数据
	 * --------------------------------
	 * 1.key必须为小写
	 * 2.缓存表必须为列表
	 */
	public JSONArray readTableFromPool(String table) throws Exception {
		synchronized(LockStor.getLock(LockStor.DB_POOL_TAB)){
			JSONArray jsonarr = tabpool.optJSONArray(table);
			if(jsonarr == null){
				DBHelper dbHelper = new DBHelper();
				try {
					dbHelper.openConnection();
					jsonarr = DBUtil.convertRsToFormat(table, dbHelper.query(table, null, null, "id"));
				} catch (Exception e) {
					throw e;
				} finally {
					dbHelper.closeConnection();
				}
				if(jsonarr.length() > 0){
					tabpool.put(table, jsonarr);
					log.d("加入列表缓存："+table);
				} else {
					log.e("从数据库读取 "+table+" 失败，表不存在");
				}
			}
			return jsonarr;
		}
	}
	
	private ArrayList<DBPoolClearListener> tabclearListeners = new ArrayList<DBPoolClearListener>();
	
	/**
	 * 加入表清理监听
	 */
	public void addTabClearListener(DBPoolClearListener listener){
		tabclearListeners.add(listener);
	}
	
	/**
	 * 从缓存中清除指定列表缓存
	 */
	public void clearTableFromPool(String table) {
		synchronized(LockStor.getLock(LockStor.DB_POOL_TAB)){
			DBUtil.clearColData(table);
			JSONArray jsonarr = tabpool.optJSONArray(table);
			if(jsonarr != null){
				tabpool.remove(table);
				log.d("清除列表缓存："+table, true);
				for(int i = 0; i < tabclearListeners.size(); i++){
					tabclearListeners.get(i).callback(table);
				}
			}
		}
	}
	
	/**
	 * 从缓存读取指定键的文件数据
	 */
	public String readTxtFromPool(String key) throws Exception {
		synchronized(LockStor.getLock(LockStor.DB_POOL_TXT)){
			String fileText = txtpool.optString(key, null);
			if(fileText == null){
				DBHelper dbHelper = new DBHelper();
				try {
					dbHelper.openConnection();
					ResultSet rs = dbHelper.query("tab_txt", "txtvalue", "txtkey='"+key+"'");
					if(!rs.next()){
						BACException.throwAndPrintInstance("缺少数据文件：" + key + ".txt");
					}
					fileText = new String(rs.getBytes("txtvalue"), "UTF-8");
					txtpool.put(key, fileText);
					log.d("加入文本缓存："+key);	
				} catch (Exception e) {
					throw e;
				} finally {
					dbHelper.closeConnection();
				}
			}
			return fileText;
		}
	}
	
	private ArrayList<DBPoolClearListener> txtclearListeners = new ArrayList<DBPoolClearListener>();
	
	/**
	 * 加入文本清理监听
	 */
	public void addTxtClearListener(DBPoolClearListener listener){
		txtclearListeners.add(listener);
	}
	
	/**
	 * 清除文本缓存中的指定键的文件数据
	 */
	public void clearTxtFromPool(String key){
		synchronized(LockStor.getLock(LockStor.DB_POOL_TXT)){
			String fileText = txtpool.optString(key, null);
			if(fileText != null){
				txtpool.remove(key);
				log.d("清除文本缓存："+key, true);
				for(int i = 0; i < txtclearListeners.size(); i++){
					txtclearListeners.get(i).callback(key);
				}
			}		
		}
	}
	
	//--------------静态区--------------
	
	private static DBPool instance = new DBPool();
	
	/**
	 * 获取实例
	 */
	public static DBPool getInst(){
		return instance;
	}
}
