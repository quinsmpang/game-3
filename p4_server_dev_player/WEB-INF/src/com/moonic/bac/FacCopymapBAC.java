package com.moonic.bac;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.battle.BattleBox;
import com.moonic.battle.BattleManager;
import com.moonic.battle.Const;
import com.moonic.battle.SpriteBox;
import com.moonic.battle.TeamBox;
import com.moonic.gamelog.GameLog;
import com.moonic.mgr.LockStor;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.Player;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MyTools;

/**
 * 帮派副本
 * @author John
 */
public class FacCopymapBAC {
	public static final String tab_fac_cm_map = "tab_fac_cm_map";
	public static final String tab_fac_cm_point = "tab_fac_cm_point";
	public static final String tab_fac_cm_damage = "tab_fac_cm_damage";
	
	/**
	 * 进入副本
	 */
	public ReturnValue into(int playerid, int pointnum, String posStr){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs pointRs = DBPool.getInst().pQueryA(tab_fac_cm_point, "num="+pointnum);
			if(!pointRs.exist()){
				BACException.throwInstance("点位不存在 num="+pointnum);
			}
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			JSONObject placmdataobj = new JSONObject(plafacRs.getString("cmdata"));//个人副本数据
			if(placmdataobj.optInt(pointRs.getString("map")) >= 2){//个人进入副本次数
				BACException.throwInstance("进入此副本次数已满");
			}
			synchronized (LockStor.getLock(LockStor.FAC_COPYMAP, factionid)) {
				DBPaRs facRs = FactionBAC.getInstance().getDataRs(factionid);
				JSONObject cmdataobj = new JSONObject(facRs.getString("cmdata"));//帮派副本数据
				JSONArray maparr = cmdataobj.optJSONArray(pointRs.getString("map"));//地图数据
				if(maparr == null){//没打过
					if(pointRs.getInt("posnum") != 1){
						BACException.throwInstance("点位未开启，请先通关前面的点位");
					}
					DBPaRs mapRs = DBPool.getInst().pQueryA(tab_fac_cm_map, "num="+pointRs.getString("map"));
					int[][] opencond = Tools.splitStrToIntArr2(mapRs.getString("opencond"), "|", ",");
					for(int i = 0; i < opencond.length; i++){//检查是否满足进入条件
						if(opencond[i][0] == 1 && facRs.getInt("lv") < opencond[i][1]){
							BACException.throwInstance("地图未开启，帮派等级不足");
						} else 
						if(opencond[i][1] == 2 && !(new JSONArray(facRs.getString("cmpassdata")).contains(opencond[i][1]))){
							BACException.throwInstance("地图未开启，未通关指定地图");
						}
					}
					maparr = new JSONArray();
					maparr.add(1);//当前打到的点位序列
					maparr.add(0);//最后重置时间
					//maparr.add(null);//副本BOSS剩余血量
				} else {
					if(maparr.optInt(0) == -1){//当前达到的点位为-1表示已通关
						BACException.throwInstance("已通关");
					}
					if(maparr.optInt(0) != pointRs.getInt("posnum")){
						BACException.throwInstance("点位不符 当前点位："+maparr.optInt(0)+" 客户端点位："+pointRs.getInt("posnum"));
					}
					if(maparr.optInt(3) != 0){//当前正在战斗的角色ID
						BACException.throwInstance("有帮众正在战斗，请稍后进入");
					}
				}
				Player pla = SocketServer.getInstance().plamap.get(playerid);
				if(pla.verifybattle_battlebox != null){//正在战斗中
					BACException.throwInstance("请先退出副本");
				}
				JSONArray posarr = new JSONArray(posStr);
				PartnerBAC.getInstance().checkPosarr(playerid, posarr, 0, 1);
				TeamBox teambox1 = PartnerBAC.getInstance().getTeamBox(playerid, 0, new JSONArray(posStr));
				TeamBox teambox2 = Enemy.getInstance().createTeamBox(pointRs.getString("enemy"), maparr.optJSONArray(2));
				BattleBox battlebox = new BattleBox();
				battlebox.teamArr[0].add(teambox1);
				battlebox.teamArr[1].add(teambox2);
				battlebox.parameterarr = new JSONArray(new int[]{factionid, pointRs.getInt("map"), pointRs.getInt("posnum")});//POSNUM是点位序号
				pla.verifybattle_battlebox = battlebox;
				
				maparr.put(3, playerid);//设置当前战斗者为自己
				cmdataobj.put(pointRs.getString("map"), maparr);//更新帮派副本数据
				SqlString sqlStr = new SqlString();
				sqlStr.add("cmdata", cmdataobj.toString());
				FactionBAC.getInstance().update(dbHelper, factionid, sqlStr);//更新帮派副本数据
				/*
				placmdataobj.put(pointRs.getString("map"), placmdataobj.optInt(pointRs.getString("map"))+1);//记录进入此副本次数
				SqlString plaSqlStr = new SqlString();
				plaSqlStr.add("cmdata", placmdataobj.toString());
				PlaFacBAC.getInstance().update(dbHelper, playerid, plaSqlStr);
				*/
				JSONArray pusharr = new JSONArray();
				pusharr.add(playerid);
				pusharr.add(pointnum);//点位编号
				PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACCOPYMAP_INTO, pusharr.toString(), factionid, playerid);
				
				return new ReturnValue(true, battlebox.getJSONArray().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 副本战斗结束后通知
	 */
	public ReturnValue end(int playerid, String battleRecord){
		DBHelper dbHelper = new DBHelper();
		try {
			Player pla = SocketServer.getInstance().plamap.get(playerid);
			if(pla.verifybattle_battlebox == null){
				BACException.throwInstance("请先进入副本");
			}
			BattleManager.verifyPVEBattle(pla.verifybattle_battlebox, battleRecord);
			JSONArray parameterarr = pla.verifybattle_battlebox.parameterarr;
			DBPaRs facRs = FactionBAC.getInstance().getDataRs(parameterarr.optInt(0));
			JSONObject cmdataobj = new JSONObject(facRs.getString("cmdata"));
			JSONArray maparr = cmdataobj.optJSONArray(parameterarr.optString(1));//副本地图数据
			int totaldamage = 0;
			JSONArray new_hparr = new JSONArray();
			JSONArray hparr = maparr.optJSONArray(2);
			ArrayList<SpriteBox> enemySprites = pla.verifybattle_battlebox.teamArr[1].get(0).sprites;
			for(int i = 0; i < enemySprites.size(); i++){
				SpriteBox spritebox = enemySprites.get(i);
				int index = spritebox.posNum-1;
				int oldhp = spritebox.battle_prop[Const.PROP_MAXHP];
				if(hparr != null){
					oldhp = hparr.optInt(index);
				}
				int currhp = spritebox.battle_prop[Const.PROP_HP];
				new_hparr.put(index, currhp);
				totaldamage += oldhp - currhp;
			}
			DBPaRs pointRs = DBPool.getInst().pQueryA(tab_fac_cm_point, "map="+parameterarr.optInt(1)+" and posnum="+parameterarr.optInt(2));
			JSONArray cmpassdataarr = new JSONArray(facRs.getString("cmpassdata"));
			boolean passmap = false;
			if(pla.verifybattle_battlebox.winTeam == Const.teamA){
				DBPaRs pointRs2 = DBPool.getInst().pQueryA(tab_fac_cm_point, "map="+parameterarr.optInt(1)+" and posnum="+(parameterarr.optInt(2)+1));
				if(pointRs2.exist()){
					maparr.put(0, parameterarr.optInt(2)+1);
				} else {
					maparr.put(0, -1);//-1表示地图全部通关
					if(!cmpassdataarr.contains(parameterarr.optInt(1))){//记录通关地图
						cmpassdataarr.add(parameterarr.optInt(1));
					}
					passmap = true;
				}
				if(maparr.length() >= 3){
					maparr.remove(2);//清除剩余血量
				}
				int[] memarr = FacMemBAC.getInstance().getFacMemIDs(parameterarr.optInt(0));//邮件发送通关奖励
				MailBAC.getInstance().sendModelMail(dbHelper, memarr, 1, null, new Object[]{pointRs.getString("name")}, pointRs.getString("award"));
			} else {
				maparr.put(2, new_hparr);//更新剩余血量
			}
			pla.verifybattle_battlebox = null;
			
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACCOPYMAP_END);
			maparr.remove(maparr.length()-1);//清除正在战斗的人的记录
			SqlString sqlStr = new SqlString();
			sqlStr.add("cmdata", cmdataobj.toString());
			sqlStr.add("cmpassdata", cmpassdataarr.toString());
			FactionBAC.getInstance().update(dbHelper, parameterarr.optInt(0), sqlStr);
			/**/
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			JSONObject placmdataobj = new JSONObject(plafacRs.getString("cmdata"));
			placmdataobj.put(parameterarr.optString(1), placmdataobj.optInt(parameterarr.optString(1))+1);//记录进入此副本次数
			SqlString plaSqlStr = new SqlString();
			plaSqlStr.add("cmdata", placmdataobj.toString());
			PlaFacBAC.getInstance().update(dbHelper, playerid, plaSqlStr);
			/**/
			PlaRoleBAC.getInstance().subValue(dbHelper, playerid, "energy", pointRs.getInt("needenergy"), gl, "体力");
			
			JSONArray returnarr = new JSONArray();//发放造成伤害的奖励
			DBPaRs damageRs = DBPool.getInst().pQueryA(tab_fac_cm_damage, "mindamage<="+totaldamage+" and maxdamage>="+totaldamage);
			if(damageRs.exist()){
				JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, damageRs.getString("award"), ItemBAC.SHORTCUT_MAIL, 1, gl);
				returnarr.add(damageRs.getString("award"));
				returnarr.add(awardarr);
				FactionBAC.getInstance().changeMoney(dbHelper, parameterarr.optInt(0), damageRs.getInt("facmoney"), gl);//加帮派资金
			}
			
			PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_TEAMCM, gl);
			
			if(passmap){
				int[] pids = FacMemBAC.getInstance().getFacMemIDs(parameterarr.optInt(0));
				for(int i = 0; pids != null && i < pids.length; i++){
					PlaWelfareBAC.getInstance().updateAchieveProgress(dbHelper, pids[i], PlaWelfareBAC.ACHIEVE_FACCM_NUM, parameterarr.optInt(1), gl); 
				}
			}
			
			PlaWelfareBAC.getInstance().updateAchieveProgress(dbHelper, playerid, PlaWelfareBAC.ACHIEVE_FACCM_TIMES, gl);
			
			JSONArray pusharr = new JSONArray();
			pusharr.add(playerid);//角色ID
			pusharr.add(pointRs.getInt("num"));//点位编号
			pusharr.add(maparr.optJSONArray(2));//剩余血量
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACCOPYMAP_END, pusharr.toString(), parameterarr.optInt(0), playerid);
			
			CustomActivityBAC.getInstance().updateProcess(dbHelper, playerid, 25);
			
			gl.addRemark("造成伤害："+totaldamage);
			gl.save();
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 退出副本
	 */
	public ReturnValue exit(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			exit(dbHelper, playerid);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 退出副本
	 */
	public void exit(DBHelper dbHelper, int playerid) throws Exception {
		DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
		int factionid = plafacRs.getInt("factionid");
		if(factionid == 0){
			return;
		}
		DBPaRs facRs = FactionBAC.getInstance().getDataRs(factionid);
		JSONObject cmdataobj = new JSONObject(facRs.getString("cmdata"));
		int mapnum = 0;
		int posnum = 0;
		@SuppressWarnings("rawtypes")
		Iterator iterator = cmdataobj.keys();
		while(iterator.hasNext()){
			String num = (String)iterator.next();
			JSONArray maparr = cmdataobj.optJSONArray(num);
			if(maparr.length() >= 4 && maparr.optInt(3) == playerid){
				maparr.remove(maparr.length()-1);//清除正在战斗的人的记录
				mapnum = Integer.valueOf(num);
				posnum = maparr.optInt(0);
				break;
			}
		}
		if(mapnum != 0){
			SqlString sqlStr = new SqlString();
			sqlStr.add("cmdata", cmdataobj.toString());
			FactionBAC.getInstance().update(dbHelper, factionid, sqlStr);
			DBPaRs pointRs = DBPool.getInst().pQueryA(tab_fac_cm_point, "map="+mapnum+" and posnum="+posnum);
			JSONArray pusharr = new JSONArray();
			pusharr.add(playerid);//角色ID
			pusharr.add(pointRs.getInt("num"));//点位编号
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACCOPYMAP_EXIT, pusharr.toString(), factionid, playerid);
			Player pla = SocketServer.getInstance().plamap.get(playerid);
			pla.verifybattle_battlebox = null;
		}
	}
	
	/**
	 * 重置地图
	 */
	public ReturnValue resetMap(int playerid, int mapnum){
		DBHelper dbHelper = new DBHelper();
		try {
			//TODO 是否在营业时间内
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			if(plafacRs.getInt("position") == 0){
				BACException.throwInstance("没有权限");
			}
			DBPaRs mapRs = DBPool.getInst().pQueryA(tab_fac_cm_map, "num="+mapnum);
			if(!mapRs.exist()){
				BACException.throwInstance("地图不存在 mapnum="+mapnum);
			}
			synchronized (LockStor.getLock(LockStor.FAC_COPYMAP, factionid)) {
				DBPaRs facRs = FactionBAC.getInstance().getDataRs(factionid);
				JSONObject cmdataobj = new JSONObject(facRs.getString("cmdata"));
				JSONArray maparr = cmdataobj.optJSONArray(String.valueOf(mapnum));
				if(maparr == null || maparr.optInt(0) == 1){
					BACException.throwInstance("不需要重置");
				} else 
				if(maparr.optInt(0) != -1){
					BACException.throwInstance("地图通关后才可重置");
				}
				long currdatelong = MyTools.getCurrentDateLong();
				if(MyTools.getCurrentDateLong(maparr.optLong(1)) == currdatelong){//最后重置时间在今天，则表示今日已重置过
					BACException.throwInstance("今日已重置过");
				}
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACCOPYMAP_RESETMAP);
				FactionBAC.getInstance().changeMoney(dbHelper, factionid, -mapRs.getInt("resetmoney"), gl);
				maparr.put(0, 1);
				maparr.put(1, currdatelong);
				if(maparr.length() >= 3){
					maparr.remove(2);//清除剩余血量
				}
				SqlString sqlStr = new SqlString();
				sqlStr.add("cmdata", cmdataobj.toString());
				FactionBAC.getInstance().update(dbHelper, factionid, sqlStr);
				
				JSONArray pusharr = new JSONArray();
				pusharr.add(playerid);
				pusharr.add(mapnum);//地图编号
				PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACCOPYMAP_RESETMAP, pusharr.toString(), factionid, playerid);
				
				gl.addRemark("重置地图："+GameLog.formatNameID(mapRs.getString("name"), mapnum))
				.save();
				return new ReturnValue(true);		
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	private static FacCopymapBAC instance = new FacCopymapBAC();
	
	/**
	 * 获取实例
	 */
	public static FacCopymapBAC getInstance(){
		return instance;
	}
}
