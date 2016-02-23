package com.moonic.bac;

import java.util.ArrayList;

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
import com.moonic.socket.Player;
import com.moonic.socket.SocketServer;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MyTools;

/**
 * 角色试炼抽象类
 * @author wkc
 */
public abstract class PlaTrialBAC {
	public final String tab_trial;
	public static final String tab_trial_times = "tab_trial_times";
	
	public static final byte TYPE_EXP = 1;//经验试炼
	public static final byte TYPE_PARTNER = 2;//伙伴试炼
	public static final byte TYPE_MONEY = 3;//铜钱试炼
	
	/**
	 * 构造
	 * @param tab_trial，试炼数据表
	 */
	public PlaTrialBAC(String tab_trial) {
		this.tab_trial = tab_trial;
	}
	
	/**
	 * 检查试炼条件
	 * @return 消耗的体力
	 */
	public int checkConditon(int playerid, byte type, int num) throws Exception{
		DBPaRs plaTrialRs = PlaRoleBAC.getInstance().getDataRs(playerid);
		if(!plaTrialRs.exist()){
			BACException.throwInstance("试炼尚未开放");
		}
		DBPaRs trialListRs = getTrialListRs(num);
		//每日次数限制
		int times = 0;//已挑战次数
		if(type == TYPE_PARTNER){
			int belong = trialListRs.getInt("type");
			JSONObject timesObj = new JSONObject(plaTrialRs.getString("partnertimes"));
			times = timesObj.optInt(String.valueOf(belong));
		} else if(type == TYPE_EXP){
			times = plaTrialRs.getInt("exptimes");
		} else if (type == TYPE_MONEY){
			times = plaTrialRs.getInt("moneytimes");
		}
		int[] conarr = getDailyCondition(type, num);
		if(times >= conarr[0]){
			BACException.throwInstance("试炼次数已用完");
		} 
		//体力需求
		int energy = PlaRoleBAC.getInstance().getIntValue(playerid, "energy");
		if(energy < conarr[1]){
			BACException.throwInstance("体力不足");
		}
		//历练等级限制
		int needLv = trialListRs.getInt("needlv");
		int plv = PlayerBAC.getInstance().getIntValue(playerid, "lv");
		if(plv < needLv){
			BACException.throwInstance("历练等级未达到条件");
		}
		return conarr[1];
	}
	
	/**
	 * 获取角色BatteBox
	 */
	public BattleBox getBattleBox(int playerid, int num, JSONArray posArr) throws Exception{
		DBPaRs trialListRs = getTrialListRs(num);
		int initAnger = trialListRs.getInt("anger");
		TeamBox myTeamBox = PartnerBAC.getInstance().getTeamBox(playerid, 0, posArr);
		for(int i = 0; i < myTeamBox.sprites.size(); i++){
			SpriteBox spritebox = myTeamBox.sprites.get(i);
			spritebox.addProp(1, Const.PROP_ANGER, 1, initAnger);
		}
		//TODO 回合数限制
		//int turnLimit = trialListRs.getInt("limit");
		BattleBox battleBox = new BattleBox();
		battleBox.teamArr[0].add(myTeamBox);
		String enemy = trialListRs.getString("enemy");
		TeamBox enemyTeamBox = Enemy.getInstance().createTeamBox(enemy);
		battleBox.teamArr[1].add(enemyTeamBox);
		return battleBox;
	}
	
	/**
	 * 验证战斗，并返回星级
	 */
	public int verifyBattle(int playerid, String battleRecord) throws Exception{
		Player pla = SocketServer.getInstance().plamap.get(playerid);
		if(pla.verifybattle_battlebox == null){
			BACException.throwInstance("请先进入挑战");
		}
		BattleBox battlebox = pla.verifybattle_battlebox;
		BattleManager.verifyPVEBattle(battlebox, battleRecord);
		if(battlebox.winTeam != Const.teamA){
			BACException.throwInstance("战斗失败");
		}
		
		//计算星级
		int deadam = 0;//计算战斗后挂了的数量
		ArrayList<SpriteBox> sprites = battlebox.teamArr[0].get(0).sprites;
		for(int i = 0; i< sprites.size(); i++){
			if(sprites.get(i).battle_prop[Const.PROP_HP] <= 0){
				deadam++;
			}
		}
		int star = 3 - (deadam > 2 ? 2 : deadam);
		return star;
	}
	
	/**
	 * 试炼开始
	 */
	public BattleBox start(int playerid, byte type, int num, String posStr, GameLog gl) throws Exception{
		JSONArray posArr = new JSONArray(posStr);
		PartnerBAC.getInstance().checkPosarr(playerid, posArr, 1, 1);
		int needPower = checkConditon(playerid, type, num);
		BattleBox battleBox = getBattleBox(playerid, num, posArr);
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(System.currentTimeMillis());
		jsonarr.add(needPower);
		battleBox.parameterarr = jsonarr;
		Player pla = SocketServer.getInstance().plamap.get(playerid);
		pla.verifybattle_battlebox = battleBox;
		DBPaRs trialListRs = getTrialListRs(num);
		StringBuffer remarkSb = new StringBuffer();
		remarkSb.append("开始试炼");
		remarkSb.append(GameLog.formatNameID(trialListRs.getString("name"), num));
		gl.addRemark(remarkSb);
		return battleBox;
	}
	
	/**
	 * 试炼结束
	 */
	public JSONArray end(DBHelper dbHelper, int playerid, int cmnum, String battleRecord, GameLog gl) throws Exception{
		int star = verifyBattle(playerid, battleRecord);
		return endHandle(dbHelper, playerid, cmnum, star, gl);
	}
	
	/**
	 * 获得经验奖励
	 */
	public JSONArray getExpAward(DBHelper dbHelper, int playerid, int num, GameLog gl) throws Exception{
		JSONArray returnarr = new JSONArray();
		DBPaRs trialListRs = getTrialListRs(num);
		int exp = trialListRs.getInt("exp");
		int exp1 = trialListRs.getInt("exp1");
		PlayerBAC.getInstance().addExp(dbHelper, playerid, exp, gl);
		Player pla = SocketServer.getInstance().plamap.get(playerid);
		BattleBox battleBox = pla.verifybattle_battlebox;
		for(int i = 0; i< battleBox.teamArr[0].get(0).sprites.size(); i++){
			int partnerId = battleBox.teamArr[0].get(0).sprites.get(i).partnerId;
			if(partnerId != 0){
				PartnerBAC.getInstance().addExp(dbHelper, playerid, partnerId, exp1, gl);
			}
		}
		returnarr.add(exp);
		returnarr.add(exp1);
		return returnarr;
	}
	
	/**
	 * 增加试炼次数
	 */
	public void addTrialTimes(DBHelper dbHelper, int playerid, GameLog gl, byte... type) throws Exception{
		Player pla = SocketServer.getInstance().plamap.get(playerid);
		PlaRoleBAC.getInstance().subValue(dbHelper, playerid, "energy", pla.verifybattle_battlebox.parameterarr.getInt(1), gl, "体力");
		if(pla.verifybattle_battlebox.parameterarr.getLong(0) >= MyTools.getCurrentDateLong()){//战斗结束时是否过了0点
			DBPaRs plaTrialRs = PlaRoleBAC.getInstance().getDataRs(playerid);
			SqlString sqlStr = new SqlString();
			StringBuffer remarkSb = new StringBuffer();
			remarkSb.append("今日");
			int oldTimes = 0;
			if(type[0] == TYPE_PARTNER){
				JSONObject timesObj = new JSONObject(plaTrialRs.getString("partnertimes"));
				oldTimes = timesObj.optInt(String.valueOf(type[1]));
				timesObj.put(String.valueOf(type[1]), oldTimes + 1);
				sqlStr.add("partnertimes", timesObj.toString());
				remarkSb.append("归属为"+type[1]);
			} else if(type[0] == TYPE_EXP){
				oldTimes = plaTrialRs.getInt("exptimes");
				sqlStr.addChange("exptimes", 1);
			} else if(type[0] == TYPE_MONEY){
				oldTimes = plaTrialRs.getInt("moneytimes");
				sqlStr.addChange("moneytimes", 1);
			}
			PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
			remarkSb.append("第"+(oldTimes+1)+"次通过");
			gl.addRemark(remarkSb);
		}
		pla.verifybattle_battlebox = null;
	}
	
	/**
	 * 试炼结束处理
	 */
	public abstract JSONArray endHandle(DBHelper dbHelper, int playerid, int num, int star, GameLog gl) throws Exception;
	
	/**
	 * 获取试炼数据列表
	 */
	public DBPaRs getTrialListRs(int num) throws Exception{
		DBPaRs trialListRs = DBPool.getInst().pQueryA(tab_trial, "num="+num);
		if(!trialListRs.exist()){
			BACException.throwInstance("不存在的试炼编号"+num);
		}
		return trialListRs;
	}
	
	/**
	 * 获取每日试炼条件
	 */
	public int[] getDailyCondition(byte type, int num) throws Exception{
		DBPaRs timesListRs = DBPool.getInst().pQueryA(tab_trial_times, "num="+type);
		if(!timesListRs.exist()){
			BACException.throwInstance("不存在的试炼类型"+type);
		}
		int[] conarr = new int[2];
		if(type == TYPE_PARTNER){
			DBPaRs trialListRs = getTrialListRs(num);
			int belong = trialListRs.getInt("type");
			int[][] timesarr = Tools.splitStrToIntArr2(timesListRs.getString("times"), "|", ",");
			for(int i = 0; i < timesarr.length; i++){
				if(timesarr[i][0] == belong){
					conarr[0] = timesarr[i][1];
					break;
				}
			}
			int[][] energyarr = Tools.splitStrToIntArr2(timesListRs.getString("energy"), "|", ",");
			for(int i = 0; i < energyarr.length; i++){
				if(energyarr[i][0] == belong){
					conarr[1] = energyarr[i][1];
					break;
				}
			}
		} else{
			conarr[0] = Tools.str2int(timesListRs.getString("times"));
			conarr[1] = Tools.str2int(timesListRs.getString("energy"));
		}
		return conarr;
	}
	
	//--------------调试功能-------------
	
	/**
	 * 重置挑战次数
	 */
	public ReturnValue debugResetTimes(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			if(this instanceof PlaTrialPartnerBAC){
				sqlStr.add("partnertimes", new JSONObject().toString());
			} else if(this instanceof PlaTrialExpBAC){
				sqlStr.add("exptimes", 0);
			} else if(this instanceof PlaTrialMoneyBAC){
				sqlStr.add("moneytimes", 0);
			}
			PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
}
