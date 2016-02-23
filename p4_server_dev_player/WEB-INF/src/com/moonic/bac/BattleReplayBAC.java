package com.moonic.bac;

import java.sql.ResultSet;

import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;

import conf.LogTbName;

public class BattleReplayBAC {
	
	/**
	 * 获取战斗回放数据
	 */
	public ReturnValue getBattleReplay(long battleid){//TODO 待优化
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		try {
			ResultSet rs = dbHelper.query(LogTbName.tab_battle_replay(), "replaydata", "battleid="+battleid);
			if(!rs.next()){
				BACException.throwInstance("未找到相关日志");
			}
			return new ReturnValue(true, new String(rs.getBytes("replaydata"), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 存战斗回放
	 */
	public void saveReplay(long battleId, String replayData, int validityday, int battlefrom){
		try {
			SqlString logSqlStr = new SqlString();
			logSqlStr.add("battleid", battleId);
			logSqlStr.addBlob("replaydata", replayData.getBytes("UTF-8"));
			logSqlStr.addDateTime("expirationtime", MyTools.getTimeStr(System.currentTimeMillis()+MyTools.long_day*validityday));
			logSqlStr.add("battlefrom", battlefrom);
			DBHelper.logInsert(LogTbName.tab_battle_replay(), logSqlStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 清理过期战斗录像
	 */
	public ReturnValue clearExpirationReplay(){
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		try {
			dbHelper.delete(LogTbName.tab_battle_replay(), "expirationtime<="+MyTools.getTimeStr()+" or battlefrom=2");
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//-----------------静态区--------------------
	
	private static BattleReplayBAC instance = new BattleReplayBAC();

	public static BattleReplayBAC getInstance() {
		return instance;
	}
}
