package com.moonic.mgr;

import java.sql.ResultSet;

import server.config.ServerConfig;

import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 数据表库
 * @author John
 */
public class TabStor {
	//---------------数据表---------------
	/**
	 * 充值方式
	 */
	public static final String tab_charge_type = "tab_charge_type";
	/**
	 * 游戏系统
	 */
	public static final String tab_game_sys = "tab_game_sys";
	/**
	 * 游戏功能
	 */
	public static final String tab_game_func = "tab_game_func";
	/**
	 * 帐号渠道
	 */
	public static final String tab_platform = "tab_platform";
	/**
	 * 联运渠道
	 */
	public static final String tab_channel = "tab_channel";
	/**
	 * 假概率
	 */
	public static final String tab_fakeodds_item = "tab_fakeodds_item";
	/**
	 * 平台礼包
	 */
	public static final String tab_platform_gift = "tab_platform_gift";
	/**
	 * 角色
	 */
	public static final String tab_role = "tab_role";
	/**
	 * 基本加点
	 */
	public static final String tab_role_base_prop = "tab_role_base_prop";
	/**
	 * 加点属性类型
	 */
	public static final String tab_base_type = "tab_base_type";
	/**
	 * 装备部位
	 */
	public static final String tab_eqpos_type = "tab_eqpos_type";
	/**
	 * 封测兑换物品
	 */
	public static final String tab_cbt_exchange = "tab_cbt_exchange";
	/**
	 * 宠物
	 */
	public static final String tab_pet = "tab_pet";
	/**
	 * 特权
	 */
	public static final String tab_prerogative = "tab_prerogative";
	/**
	 * 玩家处理类型
	 */
	public static final String tab_player_change_type = "tab_player_change_type";
	
	//---------------应用表---------------
	/**
	 * 家族库
	 */
	public static final String tab_faction_stor = "tab_faction_stor";
	
	/**
	 * 根据ID获取应用表数据名称属性
	 */
	public static String getDataName(String table, int id) {
		if(id != 0){
			return getDataVal(table, "id="+id, "name");		
		} else {
			return "";
		}
	}
	
	/**
	 * 获取数据表属性值
	 */
	public static String getListVal(String table, String where, String column){
		String value = null;
		try {
			DBPaRs rs = DBPool.getInst().pQueryA(table, where);
			if(rs.exist()){
				value = rs.getString(column);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 获取应用表属性值
	 */
	public static String getDataVal(String table, String where, String column) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		try {
			dbHelper.openConnection();
			ResultSet facRs = dbHelper.query(table, column, where);
			if(!facRs.next()){
				BACException.throwInstance("未找到记录");
			}
			return facRs.getString(column);
		} catch(Exception ex) {
			ex.printStackTrace();
			return "error:"+ex.toString();
		} finally {
			dbHelper.closeConnection();
		}
	}
}
