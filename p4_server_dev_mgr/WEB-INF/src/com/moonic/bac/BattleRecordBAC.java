package com.moonic.bac;

import java.sql.ResultSet;

import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;

import conf.LogTbName;

/**
 * 战斗记录
 * @author John
 */
public class BattleRecordBAC {
	
	/**
	 * 获取战斗记录
	 */
	public ReturnValue getBattleRecord(long battleid){
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		try {
			dbHelper.openConnection();
			//System.out.println("battleid="+battleid);
			ResultSet logRs = dbHelper.query(LogTbName.TAB_BATTLE_RECORD(), null, "battleid="+battleid);
			if(!logRs.next()){
				BACException.throwInstance("战斗记录不存在");
			}
			String replaydata = null;
			String particulardata = null;
			String propdata = null;
			try {
				replaydata = new String(logRs.getBytes("replaydata"), "UTF-8");	
			} catch (Exception e) {}
			try {
				particulardata = new String(logRs.getBytes("particulardata"), "UTF-8");	
			} catch (Exception e) {}
			try {
				propdata = new String(logRs.getBytes("propdata"), "UTF-8");	
			} catch (Exception e) {}
			StringBuffer sb = new StringBuffer();
			sb.append("<font color='#ff0000'>回放数据</font>");
			sb.append("\r\n");
			sb.append(replaydata);
			sb.append("\r\n");
			sb.append("<font color='#ff0000'>过程数据</font>");
			sb.append("\r\n");
			sb.append(particulardata);
			sb.append("\r\n");
			sb.append("<font color='#ff0000'>属性数据</font>");
			sb.append("\r\n");
			sb.append(propdata);
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//-------------------静态区---------------------
	
	private static BattleRecordBAC instance = new BattleRecordBAC();
	
	/**
	 * 获取实例
	 */
	public static BattleRecordBAC getInstance(){
		return instance;
	}
}
