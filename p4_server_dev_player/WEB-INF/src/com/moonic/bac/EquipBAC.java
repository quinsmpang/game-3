package com.moonic.bac;

import org.json.JSONArray;
import org.json.JSONObject;

import com.moonic.gamelog.GameLog;
import com.moonic.util.DBHelper;

/**
 * 装备抽象类
 * @author John
 */
public abstract class EquipBAC extends PlaStorBAC {
	
	/**
	 * 构造方法
	 * @param table	表名
	 * @param mkey	关键字：playerid
	 */
	public EquipBAC(String table, String mkey, String id_col) {
		super(table, mkey, id_col);
	}
	
	//-------------------抽象区---------------------
	
	/**
	 * 获取登陆物品信息
	 */
	public abstract void getLoginItemInfo(int playerid, JSONObject infoobj) throws Exception;
	
	/**
	 * 创建装备
	 */
	public abstract JSONArray create(DBHelper dbHelper, int playerid, int itemid, int num, JSONArray extendarr, int from, GameLog gl) throws Exception;
	
	/**
	 * 获取装备数据
	 */
	public abstract JSONArray getData(int playerid, int itemid) throws Exception;
	
	/**
	 * 销毁装备
	 */
	public abstract void destory(DBHelper dbHelper, int playerid, int itemid, GameLog gl) throws Exception;
}
