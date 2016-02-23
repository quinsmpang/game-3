package com.moonic.bac;

import server.config.ServerConfig;

import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;

import conf.LogTbName;

/**
 * 玩家操作日志
 * @author John
 */
public class PlayerOperateLogBAC extends BaseActCtrl {
	public static final String[] actStr = {"封号", "解封", "禁言", "解禁", "踢下线"};
	
	/**
	 * 构造
	 */
	public PlayerOperateLogBAC() {
		super.setTbName(LogTbName.TAB_PLAYER_OPERATE_LOG());
		setDataBase(ServerConfig.getDataBase_Log());
	}
	
	/**
	 * 加日志
	 */
	public void addLog(int playerid, int serverid, int act , String actnote, String note, String username, String ip){
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("serverid", serverid);
		sqlStr.add("act", act);
		sqlStr.add("actnote", actnote);
		sqlStr.add("note", note);
		sqlStr.add("username", username);
		sqlStr.add("ip", ip);
		sqlStr.addDateTime("savetime", MyTools.getTimeStr());
		DBHelper.logInsert(LogTbName.TAB_PLAYER_OPERATE_LOG(), sqlStr);
	}
	
	//--------------静态区---------------
	
	private static PlayerOperateLogBAC instance = new PlayerOperateLogBAC();
	
	/**
	 * 获取实例
	 */
	public static PlayerOperateLogBAC getInstance() {
		return instance;
	}
}
