package com.moonic.bac;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.battle.BattleBox;
import com.moonic.battle.BattleManager;
import com.moonic.battle.Const;
import com.moonic.battle.SpriteBox;
import com.moonic.battle.TeamBox;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.Player;
import com.moonic.socket.SocketServer;
import com.moonic.txtdata.TowerData;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.Conf;
import server.common.Tools;

/**
 * 角色轮回塔
 * @author wkc
 */
public class PlaTowerBAC extends PlaBAC {
	public static final String tab_tower_npc = "tab_tower_npc";
	public static final String tab_tower_layer = "tab_tower_layer";
	public static final String tab_tower_score = "tab_tower_score";
	
	/**
	 * 构造
	 */
	public PlaTowerBAC() {
		super("tab_pla_tower", "playerid");
		needcheck = false;
	}

	/**
	 * 初始化数据
	 */
	public void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception {
		JSONArray ranArr = randomBossData(playerid, 1);
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("serverid", Conf.sid);
		sqlStr.add("layer", 0);
		sqlStr.add("diff", 0);
		sqlStr.add("bossdata", ranArr.toString());
		sqlStr.add("bosshp", new JSONArray().toString());
		sqlStr.add("dead", new JSONArray().toString());
		sqlStr.add("score", 0);
		insert(dbHelper, playerid, sqlStr);
		((JSONObject)parm[0]).put("tower", getBossData(ranArr, 0, null));//将随机敌人数据加入到开启功能的返回结果中
	}
	
	/**
	 * 进入挑战
	 * @param layer,要挑战的层数
	 * @param diff,难易程度
	 * @param posStr,阵型数据
	 */
	public ReturnValue enter(int playerid, int layer, byte diff, String posStr){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plaTowerRs = getDataRs(playerid);
			if(!plaTowerRs.exist()){
				BACException.throwInstance("轮回塔尚未开放");
			}
			DBPaRs towerListRs = DBPool.getInst().pQueryA(tab_tower_layer, "layer="+layer);
			if(!towerListRs.exist()){
				BACException.throwInstance("不存在的塔层"+layer);
			}
			long current = System.currentTimeMillis();
			if(current >= MyTools.getTimeLong(MyTools.getDateStr() + TowerData.forbiddenstarttime) 
					&& current <= MyTools.getTimeLong(MyTools.getDateStr() + TowerData.forbiddenendtime)){
				BACException.throwInstance("此时间段内不允许挑战");
			}
			if(diff < 1 || diff > 3){
				BACException.throwInstance("难度参数错误");
			}
			int passLayer = plaTowerRs.getInt("layer");
			if(layer != passLayer+1){
				BACException.throwInstance("挑战层数与当前进度进不符");
			}
			int currDiff = plaTowerRs.getInt("diff");
			if(currDiff != 0 && diff != currDiff){
				BACException.throwInstance("之前难度尚未通过");
			}
			JSONArray posArr = new JSONArray(posStr);
			PartnerBAC.getInstance().checkPosarr(playerid, posArr, 1, 1);
			JSONArray deadArr = new JSONArray(plaTowerRs.getString("dead"));
			for(int i = 0; i < deadArr.size(); i++){
				int deadId = deadArr.getInt(i);
				if(posArr.contains(deadId)){
					BACException.throwInstance("伙伴ID（"+deadId+"）已死亡");
				}
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TOWER_ENTER);
			Player pla = SocketServer.getInstance().plamap.get(playerid);
			BattleBox battleBox = getBattleBox(playerid, layer, diff, posArr, gl);
			pla.verifybattle_battlebox = battleBox;
			if(currDiff == 0){
				SqlString sqlStr = new SqlString();
				sqlStr.add("diff", diff);
				update(dbHelper, playerid, sqlStr);
			}
			
			gl.save();
			return new ReturnValue(true, battleBox.getJSONArray().toString());
		} catch(Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 结束挑战
	 */
	public ReturnValue end(int playerid, String battleRecord){
		DBHelper dbHelper = new DBHelper();
		try {
			Player pla = SocketServer.getInstance().plamap.get(playerid);
			BattleBox battleBox = pla.verifybattle_battlebox;
			if(battleBox == null){
				BACException.throwInstance("请先进入挑战");
			}
			int layer = battleBox.parameterarr.optInt(0);
			int diff = battleBox.parameterarr.optInt(1);
			DBPaRs plaTowerRs = getDataRs(playerid);
			JSONArray deadArr = new JSONArray(plaTowerRs.getString("dead"));
			JSONArray hpArr = new JSONArray(plaTowerRs.getString("bosshp"));
			ReturnValue rv = BattleManager.verifyPVEBattle(battleBox, battleRecord);
			if(!rv.success){
				BACException.throwInstance("验证战斗失败");
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_TOWER_END);
			StringBuffer remarkSb = new StringBuffer();
			remarkSb.append("挑战层数"+layer+"难度"+diff);
			SqlString sqlStr = new SqlString();
			//计算星级,并增加已死亡的伙伴
			int deadam = 0;//死亡伙伴数量
			ArrayList<SpriteBox> sprites = battleBox.teamArr[0].get(0).sprites;
			for(int i = 0; i < sprites.size(); i++){
				if(sprites.get(i).battle_prop[Const.PROP_HP] <= 0){
					deadam++;
					deadArr.add(sprites.get(i).partnerId);
				}
			}
			if(deadam > 0){
				sqlStr.add("dead", deadArr.toString());
			}
			JSONArray returnarr = new JSONArray();
			if(battleBox.winTeam == Const.teamA){//胜利
				int star = 3 - (deadam > 2 ? 2 : deadam);
				JSONArray bossArr = new JSONArray();
				DBPaRs towerListRs = DBPool.getInst().pQueryA(tab_tower_layer, "layer="+(layer+1));
				if(towerListRs.exist()){
					bossArr = randomBossData(playerid, layer+1);
				}
				sqlStr.add("layer", layer);
				sqlStr.add("diff", 0);
				sqlStr.add("bossdata", bossArr.toString());
				//获得奖励
				JSONArray awardArr = getAward(dbHelper, playerid, layer, (byte)diff, gl);
				//根据星级获得积分
				int oldScore = plaTowerRs.getInt("score");
				int score = TowerData.basicArr[diff-1] * TowerData.starArr[star-1];
				sqlStr.addChange("score", score);
				hpArr = new JSONArray();
				returnarr.add(star);//星级
				returnarr.add(score);//获得积分
				returnarr.add(awardArr);//奖励数据
				returnarr.add(getBossData(bossArr, 0, null));//刷新的boss数据
				remarkSb.append("成功，获得星级"+star+"，积分变化"+oldScore+"->"+(oldScore+score));
			} else{
				JSONArray hpPerArr = new JSONArray();//剩余血量百分比
				ArrayList<SpriteBox> enemySprites = battleBox.teamArr[1].get(0).sprites;
				for(int i = 0; i< enemySprites.size(); i++){
					SpriteBox spritebox = enemySprites.get(i);
					int index = spritebox.posNum-1;
					int currHp = spritebox.battle_prop[Const.PROP_HP];
					int maxHp = spritebox.battle_prop[Const.PROP_MAXHP];
					hpArr.put(index, currHp);
					hpPerArr.put(index, MyTools.formatNum(((double)currHp) / ((double)maxHp)));
				}
				returnarr.add(hpPerArr);//boss血量数据
				remarkSb.append("失败，剩余血量数据"+hpArr);
			}
			returnarr.add(deadArr);//已死亡伙伴
			sqlStr.add("bosshp", hpArr.toString());
			update(dbHelper, playerid, sqlStr);
			pla.verifybattle_battlebox = null;
			PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_TOWER, gl);
			
			gl.addRemark(remarkSb);
			gl.save();
			return new ReturnValue(true, returnarr.toString());
		} catch(Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 根据层数和难度获得奖励
	 */
	public JSONArray getAward(DBHelper dbHelper, int playerid, int layer, byte diff, GameLog gl) throws Exception{
		StringBuffer awardSb = new StringBuffer();
		DBPaRs towerListRs = getTowerListRs(layer);
		String basic = towerListRs.getString("basic"+diff);
		awardSb.append(basic);
		String[] itemStr = Tools.splitStr(towerListRs.getString("random"+diff), "|");
		int[] amarr = Tools.splitStrToIntArr(towerListRs.getString("amount"+diff), ","); 
		int amount = MyTools.getRandom(amarr[0], amarr[1]);
		String paramStr = CustomActivityBAC.getInstance().getFuncActiPara(CustomActivityBAC.TYPE_TOWER_MORE_ITEM);
		if(paramStr != null){
			int param = Tools.str2int(paramStr);
			amount += param;
		} 
		int[] oddsarr = Tools.splitStrToIntArr(towerListRs.getString("odds"+diff), ",");
		for(int i = 0; i < amount; i++){
			awardSb.append("|");
			int index = MyTools.getIndexOfRandom(oddsarr);
			int[] split = Tools.splitStrToIntArr(itemStr[index], ",");
			String item = Tools.combineInt((int[])ItemBAC.getInstance().enterItem(split, "towerodds")[0], ",");
			awardSb.append(item);
			oddsarr[index] = 0;
		}
		String paramStrAppend = CustomActivityBAC.getInstance().getFuncActiPara(CustomActivityBAC.TYPE_TOWER_OTHER_ITEM);
		if(paramStrAppend != null){
			String[] itemStrAppend = Tools.splitStr(paramStrAppend, "|");
			for(int i = 0; i < itemStrAppend.length; i++){
				String[] item = Tools.splitStr(itemStrAppend[i], "#");
				int odds = Tools.str2int(item[1]);
				int random = MyTools.getRandom(1, 1000);
				if(random < odds){
					awardSb.append("|"+item[0]);
				}
			}
		}
		JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
		JSONArray returnarr = new JSONArray();
		returnarr.add(awardSb.toString());
		returnarr.add(awardarr);
		return returnarr;
	}
	
	/**
	 * 随机敌人数据
	 */
	public JSONArray randomBossData(int playerid, int layer) throws Exception{
		JSONArray retrunArr = new JSONArray();
		int plv = PlayerBAC.getInstance().getIntValue(playerid, "lv");
		DBPaRs towerListRs = getTowerListRs(layer);
		for(int i = 1; i <= 3; i++){
			int[] lvarr = Tools.splitStrToIntArr(towerListRs.getString("lv"+i), ",");
			int npclv = plv + MyTools.getRandom(lvarr[0], lvarr[1]);
			int npctype = TowerData.typeArr[i-1];
			DBPaRs npcListRs = DBPool.getInst().pQueryA(tab_tower_npc, "npclv="+npclv + " and npctype="+npctype);
			if(!npcListRs.exist()){
				BACException.throwAndPrintInstance("npc等级"+npclv+"和类型"+npctype+"数据错误");
			}
			int[][] partnerNum = CBBAC.getInstance().createNPCTeamBox_1(npcListRs);
			JSONArray bossArr = new JSONArray();
			bossArr.add(npclv);
			JSONArray boss = new JSONArray();
			boss.add(new JSONArray(partnerNum[0]));
			boss.add(new JSONArray(partnerNum[1]));
			bossArr.add(boss);//随机的敌人
			retrunArr.add(bossArr);
		}
		return retrunArr;
	}
	
	/**
	 * 获取敌人TeamBox
	 */
	public TeamBox getBossTeamBox(JSONArray numArr, int npclv, int npctype) throws Exception{
		DBPaRs npcListRs = DBPool.getInst().pQueryA(tab_tower_npc, "npclv="+npclv + " and npctype="+npctype);
		if(!npcListRs.exist()){
			BACException.throwAndPrintInstance("npc等级"+npclv+"和类型"+npctype+"数据错误");
		}
		int[][] numarr = new int[2][3];
		for(int i = 0; i < numArr.length(); i++){
			JSONArray linearr = numArr.getJSONArray(i);
			for(int j = 0; j < linearr.length(); j++){
				numarr[i][j] = linearr.getInt(j);
			}
		}
		return CBBAC.getInstance().createNPCTeamBox_2("npc等级"+npclv+"类型"+npctype, npcListRs, numarr);
	}
	
	/**
	 * 获取角色BatteBox
	 */
	public BattleBox getBattleBox(int playerid, int layer, byte diff, JSONArray posArr, GameLog gl) throws Exception{
		DBPaRs plaTowerRs = getDataRs(playerid);
		JSONArray bossArr = new JSONArray(plaTowerRs.getString("bossdata"));
		BattleBox battleBox = new BattleBox();
		TeamBox myTeamBox = PartnerBAC.getInstance().getTeamBox(playerid, 0, posArr);
		JSONArray boss = bossArr.getJSONArray(diff-1);
		TeamBox enemyTeamBox = getBossTeamBox(boss.getJSONArray(1), boss.getInt(0), TowerData.typeArr[diff-1]);
		//保留血量
		JSONArray hpArr = new JSONArray(plaTowerRs.getString("bosshp"));
		if(hpArr.size() > 0){
			ArrayList<SpriteBox> enemySprites = enemyTeamBox.sprites;
			for(int i = 0; i < enemySprites.size(); ){
				SpriteBox spritebox = enemySprites.get(i);
				int index = spritebox.posNum-1;
				int currHp = hpArr.optInt(index);
				if(currHp <= 0){
					enemySprites.remove(i);
				} else{
					spritebox.battle_prop[Const.PROP_HP] = currHp;
					i++;
				}
			}
		}
		int bgnum = 103;
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if(hour < 6 || hour > 18){
			bgnum = 104;
		}
		battleBox.bgnum = bgnum;
		battleBox.teamArr[0].add(myTeamBox);
		battleBox.teamArr[1].add(enemyTeamBox);
		battleBox.parameterarr = new JSONArray(new int[]{layer, diff});
		gl.addRemark("开始挑战第"+layer+"层，难度"+diff+",npc等级"+boss.getInt(0));
		return battleBox;
	}
	
	/**
	 * 获取随机敌人数据
	 */
	public JSONArray getBossData(JSONArray bossArr, int diff, JSONArray hpArr) throws Exception{
		JSONArray bossarr = new JSONArray();//boss数据
		JSONArray hparr = new JSONArray();//剩余血量百分比
		for(int i = 0; i < bossArr.length(); i++){
			JSONArray boss = bossArr.getJSONArray(i);
			TeamBox teamBox = getBossTeamBox(boss.getJSONArray(1), boss.getInt(0), TowerData.typeArr[i]);
			int battlePower = PartnerBAC.getInstance().getTeamBoxBattlePower(teamBox);
			JSONArray one = new JSONArray();
			one.add(boss.getJSONArray(1));
			one.add(boss.getInt(0));
			one.add(battlePower);
			bossarr.add(one);
			if(i == diff-1){
				ArrayList<SpriteBox> enemySprites = teamBox.sprites;
				for(int j = 0; j < enemySprites.size(); j++){
					SpriteBox spritebox = enemySprites.get(j);
					int index = spritebox.posNum-1;
					int currHp = hpArr.optInt(index);
					int maxHp = spritebox.battle_prop[Const.PROP_MAXHP];
					hparr.put(index, MyTools.formatNum(((double)currHp) / ((double)maxHp)));
				}
			}
		}
		JSONArray returnarr = new JSONArray();
		returnarr.add(bossarr);
		returnarr.add(hparr);
		return returnarr;
	}
	
	/**
	 * 获取轮回塔层数据列表
	 */
	public DBPaRs getTowerListRs(int layer) throws Exception{
		DBPaRs towerListRs = DBPool.getInst().pQueryA(tab_tower_layer, "layer="+layer);
		if(!towerListRs.exist()){
			BACException.throwInstance("不存在的塔层"+layer);
		}
		return towerListRs;
	}
	
	/**
	 * 发送积分奖励
	 * @throws Exception 
	 */
	public void sendAward(DBHelper dbHelper) throws Exception{
		DBPsRs scoreListRs = DBPool.getInst().pQueryS(tab_tower_score);
		ResultSet scoreRs = dbHelper.query("tab_pla_tower", "score,playerid", "serverid="+Conf.sid+" and score >"+0);
		while(scoreRs.next()){
			try {
				int score = scoreRs.getInt("score");
				scoreListRs.beforeFirst();
				while(scoreListRs.next()){
					if(score >= scoreListRs.getInt("begin") && score <= scoreListRs.getInt("end")){
						String award = scoreListRs.getString("award");
						int playerid = scoreRs.getInt("playerid");
						MailBAC.getInstance().sendModelMail(dbHelper, new int[]{playerid}, 7, null, new Object[]{(score)}, award);
						GameLog.getInst(playerid, GameServlet.ACT_TOWER_AWARD)
						.addRemark("累计获得积分"+score+"，奖励"+award)
						.save();
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 重置每日轮回塔数据
	 */
	public void resetData(DBHelper dbHelper, int playerid, JSONObject jsonobj) throws Exception {
		DBPaRs plaTowerRs = getDataRs(playerid);
		if(!plaTowerRs.exist()){//功能未开启时直接返回
			return;
		}
		JSONArray ranArr = randomBossData(playerid, 1);
		SqlString sqlStr = new SqlString();
		sqlStr.add("layer", 0);
		sqlStr.add("diff", 0);
		sqlStr.add("bossdata", ranArr.toString());
		sqlStr.add("bosshp", new JSONArray().toString());
		sqlStr.add("dead", new JSONArray().toString());
		sqlStr.add("score", 0);
		update(dbHelper, playerid, sqlStr);
		jsonobj.put("tower", getBossData(ranArr, 0, null));
	}
	
	/**
	 * 获取数据
	 */
	public JSONArray getData(int playerid) throws Exception {
		DBPaRs plaTowerRs = getDataRs(playerid);
		if(!plaTowerRs.exist()){
			return null;
		}
		int layer = plaTowerRs.getInt("layer");
		int diff = plaTowerRs.getInt("diff");
		int score = plaTowerRs.getInt("score");
		JSONArray deadArr = new JSONArray(plaTowerRs.getString("dead"));
		JSONArray bossArr = new JSONArray(plaTowerRs.getString("bossdata"));
		JSONArray bossHp = new JSONArray(plaTowerRs.getString("bosshp"));
		JSONArray bossdata = getBossData(bossArr, diff, bossHp);
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(layer);//当日已通过的层数
		jsonarr.add(bossdata.getJSONArray(1));//当前boss剩余血量
		jsonarr.add(deadArr);//当前已阵亡的伙伴
		jsonarr.add(score);//当日已获得积分
		jsonarr.add(bossdata.getJSONArray(0));//随机boss数据
		jsonarr.add(diff);//当前挑战难度
		return jsonarr;
	}
	
	//------------------静态区------------------
	
	private static PlaTowerBAC instance = new PlaTowerBAC();
	
	public static PlaTowerBAC getInstance(){
		return instance;
	}
}
