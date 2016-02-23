package com.moonic.worldboss;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import server.common.Tools;

import com.moonic.bac.AwardBAC;
import com.moonic.bac.CustomActivityBAC;
import com.moonic.bac.ItemBAC;
import com.moonic.bac.MailBAC;
import com.moonic.bac.PartnerBAC;
import com.moonic.bac.WorldBossBAC;
import com.moonic.battle.BattleBox;
import com.moonic.battle.BattleManager;
import com.moonic.battle.Const;
import com.moonic.battle.SpriteBox;
import com.moonic.battle.TeamBox;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.txtdata.WorldBossData;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyLog;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 世界BOSS
 * @author wkc 
 */
public class WorldBoss {
	public long startTime;//开始时间
	public long actiTimeLen;//活动时长
	
	public boolean inActi;//活动是否进行中
	
	public Boss boss;//敌人
	
	private Hashtable<Integer, WBPlayer> pla_tab;//加入的玩家
	
	private ScheduledExecutorService timer = MyTools.createTimer(3);//计时器
	
	public MyLog log = new MyLog(MyLog.NAME_CUSTOM, "worldboss", "WORLDBOSS", true, false, true, MyTools.formatTime("yyyy-MM-dd-hh-mm-ss"));
	
	public static final String tab_world_boss_award_damage = "tab_world_boss_award_damage";
	public static final String tab_world_boss_award_rank = "tab_world_boss_award_rank";
	
	/**
	 * 构造
	 */
	public WorldBoss(long actiTimeLen){
		this.actiTimeLen = actiTimeLen;
		this.pla_tab = new Hashtable<Integer, WBPlayer>();
	}
	
	/**
	 * 启动
	 */
	public void start(){
		Out.println("世界BOSS开始");
		log.d("世界BOSS倒计时"+actiTimeLen/1000+"s");
		MyTimerTask endTT = new MyTimerTask() {
			public void run2() {
				DBHelper dbHelper = new DBHelper();
				try {
					dbHelper.openConnection();
					endHandle(dbHelper);
				} catch (Exception e){
					e.printStackTrace();
				} finally {
					dbHelper.closeConnection();
				}
			}
		};
		timer.schedule(endTT, actiTimeLen, TimeUnit.MILLISECONDS);
		startTime = System.currentTimeMillis();
		inActi = true;
		JSONArray pusharr = new JSONArray();
		pusharr.add(startTime);
		pusharr.add(actiTimeLen);
		PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_WORLD_BOSS_START, pusharr.toString());
	}
	
	/**
	 * 加入
	 */
	public JSONArray join(int playerid) throws Exception{
		WBPlayer player = pla_tab.get(playerid);
		if(player == null){
			player = new WBPlayer(playerid);
			pla_tab.put(playerid, player);
			log.d("玩家"+player.name+"加入了世界BOSS");
			GameLog.getInst(playerid, GameServlet.ACT_WORLD_BOSS_JOIN)
			.addRemark("玩家："+GameLog.formatNameID(player.name, playerid)+"加入世界BOSS")
			.save();
		} 
		return getData(playerid);
	}
	
	/**
	 * 挑战
	 */
	public JSONArray toBattle(DBHelper dbHelper, int playerid, String posStr, GameLog gl) throws Exception{
		if(!inActi){
			BACException.throwInstance("世界BOSS活动已结束");
		}
		WBPlayer player = pla_tab.get(playerid);
		if(player == null){
			BACException.throwInstance("玩家不在世界BOSS活动中");
		}
		if(player.chaTimes >= WorldBossData.chaTimes){
			BACException.throwInstance("本场世界BOSS的挑战次数已用完");
		}
		this.boss = new Boss();
		JSONArray posArr = new JSONArray(posStr);
		PartnerBAC.getInstance().checkPosarr(playerid, posArr, 1, 1);
		for(int i = 0; i < posArr.length(); i++){
			int partnerId = posArr.getInt(i);
			if(partnerId != 0 && player.partnerArr.contains(partnerId)){
				BACException.throwInstance("伙伴已参战（ID："+partnerId+")");
			}
		}
		BattleBox battlebox = getBattleBox(playerid, posArr);
		BattleManager.createPVPBattle(battlebox);
		//计算伤害
		int damage = 0;
		for(int i = 0; i < boss.teamBox.sprites.size(); i++){
			SpriteBox spriteBox = boss.teamBox.sprites.get(i);
			damage += (spriteBox.battle_prop[Const.PROP_MAXHP] - spriteBox.battle_prop[Const.PROP_HP]);
		}
		//根据伤害获得奖励
		JSONArray returnarr = new JSONArray();
		String award = null;
		if(damage > 0){
			player.totalDamage += damage;
			DBPsRs damageListRs = DBPool.getInst().pQueryS(tab_world_boss_award_damage);
			while(damageListRs.next()){
				if(damage <= damageListRs.getLong("end")){
					award = damageListRs.getString("award");
					break;
				}
			}
		}
		JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, award, ItemBAC.SHORTCUT_MAIL, 1, gl);
		returnarr.add(damage);//伤害
		returnarr.add(award);//奖励内容string
		returnarr.add(awardarr);//背包奖励arr
		returnarr.add(battlebox.replayData);//战斗录像
		//增加挑战次数
		player.chaTimes++;
		//增加已出战伙伴
		ArrayList<SpriteBox> mySprites = battlebox.teamArr[0].get(0).sprites;
		for(int i = 0; i < mySprites.size(); i++){
			player.partnerArr.add(mySprites.get(i).partnerId);
		}
		log.d("玩家" + player.name + "第"+ player.chaTimes + "次挑战，对BOSS造成" + damage + "点伤害");
		gl.addRemark("第"+ player.chaTimes + "次挑战对BOSS造成" + damage + " 点伤害");
		return returnarr;
	}
	
	/**
	 * 获取数据
	 */
	public JSONArray getData(int playerid) throws Exception{
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(getRankData(playerid));
		WBPlayer player = pla_tab.get(playerid);
		if(player == null){
			player = new WBPlayer(playerid);
		}
		jsonarr.add(player.getData());
		return jsonarr;
	}
	
	/**
	 * 获取战斗BatteBox
	 */
	public BattleBox getBattleBox(int playerid, JSONArray posArr) throws Exception{
		TeamBox myTeamBox = PartnerBAC.getInstance().getTeamBox(playerid, 0, posArr);
		BattleBox battleBox = new BattleBox();
		battleBox.bgnum = WorldBossData.bgNum;
		battleBox.teamArr[0].add(myTeamBox);
		battleBox.teamArr[1].add(boss.teamBox);
		return battleBox;
	}
	
	/**
	 * 活动结束处理
	 */
	public void endHandle(DBHelper dbHelper) throws Exception {
		inActi = false;
		log.d("------------世界BOSS结束处理------------");
		//排名奖励
		DBPsRs rankListRs = DBPool.getInst().pQueryS(tab_world_boss_award_rank);
		WBPlayer[] playerarr = pla_tab.values().toArray(new WBPlayer[pla_tab.size()]);
		Tools.sort(playerarr, 1);
		int begin = 0;//开始的下标
		while(rankListRs.next()){
			int end = rankListRs.getInt("end");
			StringBuffer awardSb = new StringBuffer();
			awardSb.append(rankListRs.getString("award"));
			String paramStrAppend = CustomActivityBAC.getInstance().getFuncActiPara(CustomActivityBAC.TYPE_WORLDBOSS_OTHER_ITEM);
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
			while(begin < playerarr.length && begin < end && playerarr[begin].totalDamage > 0){
				MailBAC.getInstance().sendModelMail(dbHelper, new int[]{playerarr[begin].id}, 6, null, new Object[]{(begin+1)}, awardSb.toString());
				GameLog.getInst(playerarr[begin].id, GameServlet.ACT_WORLD_BOSS_AWARD_RANK)
				.addRemark("获得伤害排名第"+(begin+1)+"名，奖励"+awardSb.toString())
				.save();
				begin++;
			}
			if(begin >= playerarr.length || playerarr[begin].totalDamage == 0){
				break;
			}
		}
		WorldBossBAC.worldboss = null;
		log.save();
		Out.println("世界BOSS结束");
	}
	
	/**
	 * 获取排行数据
	 */
	public JSONArray getRankData(int playerid){
		JSONArray rankarr = new JSONArray();
		WBPlayer[] playerarr = pla_tab.values().toArray(new WBPlayer[pla_tab.size()]);
		Tools.sort(playerarr, 1);
		for(int i = 0; i < playerarr.length; i++){
			if(i < 5){
				rankarr.add(playerarr[i].getData1());
			}
			if(playerarr[i].id == playerid){
				playerarr[i].rank = i+1; 
			}
		}
		return rankarr;
	}
	
	/**
	 * 获取登陆数据
	 */
	public JSONArray getLoginData(){
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(startTime);
		jsonarr.add(actiTimeLen);
		return jsonarr;
	}
}
