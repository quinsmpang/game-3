package com.moonic.bac;

import org.json.JSONArray;

import com.ehc.common.ReturnValue;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.worldboss.WorldBoss;

/**
 * 世界BOSS BAC
 * @author wkc
 */
public class WorldBossBAC {
	public static WorldBoss worldboss;//世界BOSS
	
	/**
	 * 启动世界BOSS
	 */
	public ReturnValue start(long actiTimeLen, byte isConstraint){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			if(worldboss != null){
				if(isConstraint == 1){
					worldboss.endHandle(dbHelper);
				} else{
					BACException.throwInstance("世界BOSS已启动");
				}
			}
			WorldBoss wb = new WorldBoss(actiTimeLen);
			worldboss = wb;
			wb.start();
			return new ReturnValue(true, "启动成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 加入世界BOSS
	 */
	public ReturnValue join(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			if(worldboss == null){
				BACException.throwInstance("世界BOSS未开始或已结束");
			}
			JSONArray json = worldboss.join(playerid);
			return new ReturnValue(true, json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 与BOSS战斗
	 */
	public ReturnValue toBattle(int playerid, String posStr){
		DBHelper dbHelper = new DBHelper();
		try {
			if(worldboss == null){
				BACException.throwInstance("世界BOSS未开始或已结束");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_WORLD_BOSS_BATTLE);
			JSONArray jsonarr = worldboss.toBattle(dbHelper, playerid, posStr, gl);
			gl.save();
			return new ReturnValue(true, jsonarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取数据
	 */
	public ReturnValue getData(int playerid){
		try {
			if(worldboss == null){
				BACException.throwInstance("世界BOSS未开始或已结束");
			}
			JSONArray jsonarr = worldboss.getData(playerid);
			return new ReturnValue(true, jsonarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 获取登陆数据
	 */
	public JSONArray getLoginData() {
		JSONArray jsonarr = new JSONArray();
		if(worldboss != null) {
			jsonarr = worldboss.getLoginData();
		}
		return jsonarr;
	}
	
	//-------------------静态区---------------------
	
	private static WorldBossBAC instance = new WorldBossBAC();
	
	/**
	 * 获取实例
	 */
	public static WorldBossBAC getInstance(){
		return instance;
	}
}
