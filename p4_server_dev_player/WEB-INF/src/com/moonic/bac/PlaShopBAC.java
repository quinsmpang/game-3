package com.moonic.bac;

import com.ehc.common.SqlString;
import com.moonic.util.DBHelper;

/**
 * 角色商店
 * @author wkc
 */
public class PlaShopBAC extends PlaBAC {

	/**
	 * 构造
	 */
	private PlaShopBAC() {
		super("tab_pla_shop", "playerid");
	}

	/**
	 * 初始化
	 */
	public void init(DBHelper dbHelper, int playerid, Object... param) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("item1", "[]");
		sqlStr.add("buy1", "[]");
		sqlStr.add("times1", 0);
		sqlStr.add("item2", "[]");
		sqlStr.add("buy2", "[]");
		sqlStr.add("item3", "[]");
		sqlStr.add("buy3", "[]");
		sqlStr.add("times3", 0);
		sqlStr.add("item4", "[]");
		sqlStr.add("buy4", "[]");
		sqlStr.add("times4", 0);
		sqlStr.add("item5", "[]");
		sqlStr.add("buy5", "[]");
		sqlStr.add("times5", 0);
		sqlStr.add("item6", "[]");
		sqlStr.add("buy6", "[]");
		sqlStr.add("times6", 0);
		insert(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 重置数据
	 */
	public void resetData(DBHelper dbHelper, int playerid) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("times1", 0);
		sqlStr.add("times3", 0);
		sqlStr.add("times4", 0);
		sqlStr.add("times5", 0);
		sqlStr.add("times6", 0);
		update(dbHelper, playerid, sqlStr);
	}
	
	//--------静态区--------
	
	private static PlaShopBAC instance = new PlaShopBAC();
	
	public static PlaShopBAC getInstance(){
		return instance;
	}
}
