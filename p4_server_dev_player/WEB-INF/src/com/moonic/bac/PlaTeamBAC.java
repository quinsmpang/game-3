package com.moonic.bac;

import org.json.JSONArray;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.mirror.MirrorMgr;
import com.moonic.servlet.GameServlet;
import com.moonic.team.TeamActivity;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;

import conf.Conf;

/**
 * 角色组队
 * @author wkc
 */
public class PlaTeamBAC extends PlaBAC {
	public static TeamActivity teamActivity;
	
	public static final String tab_team_boss = "tab_team_boss";

	public PlaTeamBAC() {
		super("tab_pla_team", "playerid");
		needcheck = false;
	}
	
	@Override
	public void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("serverid", Conf.sid);
		sqlStr.add("times", 0);
		insert(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 启动组队活动
	 */
	public ReturnValue start(long actiTimeLen, byte isConstraint){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			if(teamActivity != null){
				if(isConstraint == 1){
					teamActivity = null;
				} else{
					BACException.throwInstance("组队活动已启动");
				}
			}
			teamActivity = new TeamActivity(actiTimeLen);
			teamActivity.start();
			return new ReturnValue(true, "启动成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 创建队伍
	 * @param type,队伍类型 1~3
	 */
	public ReturnValue createTeam(int playerid, int type){
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			DBPaRs plaTeamRs = getDataRs(playerid);
			if(!plaTeamRs.exist()){
				BACException.throwInstance("功能尚未开启");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TEAM_ACTI_CREATE);
			int num = teamActivity.createTeam(playerid, type, gl);
			gl.save();
			return new ReturnValue(true, String.valueOf(num));
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 加入队伍
	 */
	public ReturnValue joinTeam(int playerid, int num){
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			DBPaRs plaTeamRs = getDataRs(playerid);
			if(!plaTeamRs.exist()){
				BACException.throwInstance("功能尚未开启");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TEAM_ACTI_JOIN);
			JSONArray jsonarr = teamActivity.joinTeam(playerid, num, gl);
			gl.save();
			return new ReturnValue(true, jsonarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 踢出队伍
	 */
	public ReturnValue kickOut(int playerid, int num, int memberid){
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TEAM_ACTI_KICK);
			teamActivity.kickOut(playerid, num, memberid, gl);
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 布阵
	 */
	public ReturnValue format(int playerid, int num, String posStr){
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			JSONArray posarr = new JSONArray(posStr);
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TEAM_ACTI_FORMAT);
			int battlePower = teamActivity.format(playerid, num, posarr, gl);
			gl.save();
			return new ReturnValue(true, String.valueOf(battlePower));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 准备
	 */
	public ReturnValue beReady(int playerid, int num){
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TEAM_ACTI_BEREADY);
			teamActivity.beReady(playerid, num, gl);
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 取消准备
	 */
	public ReturnValue cancelReady(int playerid, int num){
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TEAM_ACTI_CANCELREADY);
			teamActivity.cancelReady(playerid, num, gl);
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 战斗
	 */
	public ReturnValue battle(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TEAM_ACTI_BATTLE);
			JSONArray jsonarr = teamActivity.battle(dbHelper, playerid, num, gl);
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
	 * 获取队伍列表
	 * @param type,1~3,队伍类型
	 */
	public ReturnValue getTeamList(int playerid, int type){
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			if(type < 1 || type > 3){
				BACException.throwInstance("队伍类型参数错误");
			}
			JSONArray jsonarr = teamActivity.getTeamList(type);
			return new ReturnValue(true, jsonarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 获取数据
	 */
	public ReturnValue getDate(int playerid){
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			DBPaRs plaTeamRs = getDataRs(playerid);
			if(!plaTeamRs.exist()){
				BACException.throwInstance("功能尚未开启");
			}
			JSONArray jsonarr = new JSONArray();
			jsonarr.add(plaTeamRs.getInt("times"));//当日已挑战次数
			jsonarr.add(teamActivity.getTeamList(1));//队伍列表
			return new ReturnValue(true, jsonarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 退出队伍
	 */
	public ReturnValue exitTeam(int playerid, int num){
		try {
			if(teamActivity == null){
				BACException.throwInstance("组队活动未开始或已结束");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TEAM_ACTI_EXIT);
			teamActivity.exitTeam(playerid, num, gl);
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 登出
	 */
	public void logout(int playerid) throws Exception {
		if(teamActivity != null){
			teamActivity.logout(playerid);
		} 
	}
	
	/**
	 * 重置可获得奖励次数
	 * @throws Exception 
	 */
	public void resetTimes(DBHelper dbHelper) throws Exception{
		SqlString sqlStr = new SqlString();
		sqlStr.add("times", 0);
		dbHelper.update("tab_pla_team", sqlStr, "serverid="+Conf.sid);
		MirrorMgr.clearTabData("tab_pla_team", false);//手动清镜像
	}
	
	/**
	 * 获取登陆数据
	 */
	public JSONArray getLoginData() {
		JSONArray jsonarr = new JSONArray();
		if(teamActivity != null) {
			jsonarr = teamActivity.getLoginData();
		}
		return jsonarr;
	}
	
	//------------------静态区------------------
	
	private static PlaTeamBAC instance = new PlaTeamBAC();
	
	public static PlaTeamBAC getInstance(){
		return instance;
	}
	
	//------------------调试区------------------
	
	/**
	 * 调试重置获得奖励次数
	 */
	public ReturnValue debugResetTimes(int playerid) {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.add("times", 0);
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
}
