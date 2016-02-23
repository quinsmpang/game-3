package com.moonic.bac;

import server.config.ServerConfig;

import com.ehc.dbc.BaseActCtrl;
import com.moonic.util.DBHelper;

import conf.LogTbName;

/**
 * 角色数据操作日志
 * @author 
 */
public class PlayerChangeLogBAC extends BaseActCtrl {
	
	/**
	 * 构造
	 */
	public PlayerChangeLogBAC() {
		super.setTbName(LogTbName.TAB_PLAYER_CHANGELOG());
		setDataBase(ServerConfig.getDataBase_Backup());
	}
	
	/**
	 * 指定条件的数据数量
	 */
	public int getCount(String whereClause) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		try {			
			return dbHelper.queryCount(LogTbName.TAB_PLAYER_CHANGELOG(), whereClause);
		} catch (Exception e) {			
			e.printStackTrace();
			return 0;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//------------------静态区----------------
	
	private static PlayerChangeLogBAC instance = new PlayerChangeLogBAC();
	
	public static PlayerChangeLogBAC getInstance() {
		return instance;
	}
}
