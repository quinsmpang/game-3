package com.moonic.cb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Sortable;
import server.common.Tools;

import com.ehc.common.SqlString;
import com.moonic.bac.AwardBAC;
import com.moonic.bac.BattleReplayBAC;
import com.moonic.bac.CBBAC;
import com.moonic.bac.FactionBAC;
import com.moonic.bac.MailBAC;
import com.moonic.battle.BattleBox;
import com.moonic.battle.BattleManager;
import com.moonic.battle.Const;
import com.moonic.battle.SpriteBox;
import com.moonic.battle.TeamBox;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.GamePushData;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.txtdata.CBDATA;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MyLog;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

import conf.Conf;

/**
 * 城池
 * @author John
 */
public class City implements Runnable {
	public int mapkey;
	public int num;//城池编号
	public String name;//城市名称
	public int type;//城市类型
	
	public Faction faction1;//攻方
	public Faction faction2;//守方
	
	public ArrayList<Partner> partnerlist = new ArrayList<Partner>();//参加此城战的伙伴集合，城战结束时设置集合内伙伴的状态
	
	private JSONArray killarr = new JSONArray();
	
	public long battletime;//开始战斗时间
	
	public HashMap<Integer, JSONArray> killerInfoMap = new HashMap<Integer, JSONArray>();//杀手信息
	public HashMap<Integer, Integer> killerAmountMap = new HashMap<Integer, Integer>();//击杀数量
	
	public boolean end;
	
	public MyLog log;
	
	public JSONArray batteamidarr1;
	
	/**
	 * 获取数据
	 */
	public JSONArray getData() throws Exception {
		JSONArray dataarr = new JSONArray();
		dataarr.add(faction1.getData());
		dataarr.add(faction2.getData());
		return dataarr;
	}
	
	/**
	 * 获取数据2
	 */
	public JSONArray getData2() throws Exception {
		JSONArray dataarr = new JSONArray();
		dataarr.add(battletime);
		dataarr.add(faction1.getData2());
		dataarr.add(faction2.getData2());
		return dataarr;
	}
	
	/**
	 * 获取出场顺序
	 */
	public JSONArray getBatterList(int teamType) {
		if(teamType == Const.teamA){
			return faction1.getBattlerData();
		} else 
		{
			return faction2.getBattlerData();
		}
	}
	
	/**
	 * 获取击杀排行
	 */
	public JSONArray getKillRanking(int playerid){
		Integer[] keys = killerAmountMap.keySet().toArray(new Integer[killerAmountMap.size()]);
		KillRankingPla[] plas = new KillRankingPla[keys.length];
		for(int i = 0; i < keys.length; i++){
			plas[i] = new KillRankingPla(keys[i], killerAmountMap.get(keys[i]));
		}
		Tools.sort(plas, 1);
		JSONArray rankingarr = new JSONArray();
		for(int i = 0; rankingarr.length() < 9 && i < plas.length; i++){
			JSONArray info = killerInfoMap.get(plas[i].playerid);
			JSONArray arr = new JSONArray();
			arr.add(info.optString(0));//玩家名
			arr.add(info.optInt(1));//玩家等级
			arr.add(info.optInt(2));//玩家头像
			arr.add(plas[i].amount);//击杀数
			rankingarr.add(arr);
		}
		JSONArray returnarr = new JSONArray();
		returnarr.add(rankingarr);
		returnarr.add(killerAmountMap.containsKey(playerid)?killerAmountMap.get(playerid):0);
		return returnarr;
	}
	
	class KillRankingPla implements Sortable {
		int playerid;
		int amount;
		public KillRankingPla(int playerid, int amount){
			this.playerid = playerid;
			this.amount = amount;
		}
		public double getSortValue() {
			return amount;
		}
	}
	
	/**
	 * 战斗
	 */
	public void run(){
		try {
			DBPaRs cityRs = DBPool.getInst().pQueryA(CBBAC.tab_cb_city, "num="+num);
			log.d("城市："+cityRs.getString("name"));
			log.d("交战双方："+faction1.factionid+"("+faction1.influence+") vs "+faction2.factionid+"("+faction2.influence+")");
			log.d("城战开始");
			while(true){
				if(faction1.teamlist.size() <= 0 || faction2.teamlist.size() <= 0){
					log.d("有队伍无人参赛，结束城战");
					end = true;
					break;
				}
				TeamBox teambox1 = faction1.teamlist.get(0);
				TeamBox teambox2 = faction2.teamlist.get(0);
				BattleBox battlebox = new BattleBox();
				battlebox.teamArr[0].add(teambox1);
				battlebox.teamArr[1].add(teambox2);
				BattleManager.createPVPBattle(battlebox);
				if(battlebox.winTeam != Const.teamA){
					log.d("淘汰攻方队伍："+faction1.teamlist.get(0).getTeamDataStr());
				} else 
				if(battlebox.winTeam != Const.teamB){
					log.d("淘汰守方队伍："+faction2.teamlist.get(0).getTeamDataStr());
				}
				for(int i = 0; i < teambox1.sprites.size(); ){
					SpriteBox spritebox = teambox1.sprites.get(i);
					if(spritebox.battle_prop[Const.PROP_HP] <= 0 || battlebox.winTeam != Const.teamA){//将已死亡的伙伴移出队伍，并标记为已死亡
						//teambox1.sprites.remove(i);
						if(teambox1.playerid != 0){
							spritebox.battle_prop[Const.PROP_HP] = 0;//TODO DEBUG
							CBBAC.cbmgr.partnermap.get(spritebox.partnerId).died = true;
						}
						i++;//因为不会将已死亡的伙伴从队伍移除，所以都要++，如果恢复移除策略，则这里不能++
					} else {
						i++;
					}
				}
				for(int i = 0; i < teambox2.sprites.size(); ){
					SpriteBox spritebox = teambox2.sprites.get(i);
					if(spritebox.battle_prop[Const.PROP_HP] <= 0 || battlebox.winTeam != Const.teamB){//后一条件因为尚无战斗，暂加
						//teambox2.sprites.remove(i);
						if(teambox2.playerid != 0){
							spritebox.battle_prop[Const.PROP_HP] = 0;//TODO DEBUG
							CBBAC.cbmgr.partnermap.get(spritebox.partnerId).died = true;
						}
						i++;
					} else {
						i++;
					}
				}
				TeamBox winTb = null;
				TeamBox loseTb = null;
				Faction loseFac = null;
				if(battlebox.winTeam != Const.teamA){
					if(teambox2.playerid != 0){
						if(!killerInfoMap.containsKey(teambox2.playerid)){
							JSONArray arr = new JSONArray();
							arr.add(teambox2.pname);
							arr.add(teambox2.parameterarr.optInt(1));
							arr.add(teambox2.parameterarr.optInt(2));
							killerInfoMap.put(teambox2.playerid, arr);
							killerAmountMap.put(teambox2.playerid, 1);
						} else {
							killerAmountMap.put(teambox2.playerid, killerAmountMap.get(teambox2.playerid)+1);
						}
						JSONArray bekillarr = new JSONArray();
						bekillarr.add(teambox2.playerid);
						bekillarr.add(teambox1.parameterarr.optInt(0));//敌人类型 0.玩家 1.普通NPC 2.精英NPC
						bekillarr.add(teambox1.parameterarr.optInt(1));//等级
						killarr.add(bekillarr);
					}
					if(teambox1.parameterarr.optInt(4) >= teambox2.parameterarr.optInt(4)/2){//达到站立值的一半
						int pl = teambox2.parameterarr.optInt(3)+1;
						DBPaRs fatigueRs = DBPool.getInst().pQueryA(CBBAC.tab_cb_fatigue, "minam<="+pl+" and maxam>="+pl);
						int perc = Math.min(99, teambox2.parameterarr.optInt(5)+fatigueRs.getByte("minusperc"));
						teambox2.parameterarr.put(3, pl);
						teambox2.parameterarr.put(5, perc);
						for(int i = 0; i < teambox2.sprites.size(); i++){
							SpriteBox spritebox = teambox2.sprites.get(i);
							spritebox.battle_prop[Const.PROP_ATTACK] = (int)(spritebox.battle_prop_src[Const.PROP_ATTACK]*(100d-perc)/100);
							spritebox.battle_prop[Const.PROP_DEFENCE] = (int)(spritebox.battle_prop_src[Const.PROP_DEFENCE]*(100d-perc)/100);
							spritebox.battle_prop[Const.PROP_MAGICDEF] = (int)(spritebox.battle_prop_src[Const.PROP_MAGICDEF]*(100d-perc)/100);
						}
					}
					faction1.teamlist.remove(0);
					winTb = teambox2;
					loseTb = teambox1;
					loseFac = faction1;
				} else 
				if(battlebox.winTeam != Const.teamB){
					if(teambox1.playerid != 0){
						if(!killerInfoMap.containsKey(teambox1.playerid)){
							JSONArray arr = new JSONArray();
							arr.add(teambox1.pname);
							arr.add(teambox1.parameterarr.optInt(1));
							arr.add(teambox1.parameterarr.optInt(2));
							killerInfoMap.put(teambox1.playerid, arr);
							killerAmountMap.put(teambox1.playerid, 1);
						} else {
							killerAmountMap.put(teambox1.playerid, killerAmountMap.get(teambox1.playerid)+1);
						}
						JSONArray bekillarr = new JSONArray();
						bekillarr.add(teambox1.playerid);
						bekillarr.add(teambox2.parameterarr.optInt(0));//敌人类型 0.玩家 1.普通NPC 2.精英NPC
						bekillarr.add(teambox2.parameterarr.optInt(1));//等级
						killarr.add(bekillarr);
					}
					if(teambox2.parameterarr.optInt(4) >= teambox1.parameterarr.optInt(4)/2){
						int pl = teambox1.parameterarr.optInt(3)+1;
						DBPaRs fatigueRs = DBPool.getInst().pQueryA(CBBAC.tab_cb_fatigue, "minam<="+pl+" and maxam>="+pl);
						int perc = Math.min(99, teambox1.parameterarr.optInt(5)+fatigueRs.getByte("minusperc"));
						teambox1.parameterarr.put(3, pl);
						teambox1.parameterarr.put(5, perc);
						for(int i = 0; i < teambox1.sprites.size(); i++){
							SpriteBox spritebox = teambox1.sprites.get(i);
							spritebox.battle_prop[Const.PROP_ATTACK] = (int)(spritebox.battle_prop_src[Const.PROP_ATTACK]*(100d-perc)/100);
							spritebox.battle_prop[Const.PROP_DEFENCE] = (int)(spritebox.battle_prop_src[Const.PROP_DEFENCE]*(100d-perc)/100);
							spritebox.battle_prop[Const.PROP_MAGICDEF] = (int)(spritebox.battle_prop_src[Const.PROP_MAGICDEF]*(100d-perc)/100);
						}
					}
					faction2.teamlist.remove(0);
					winTb = teambox1;
					loseTb = teambox2;
					loseFac = faction2;
				}
				BattleReplayBAC.getInstance().saveReplay(battlebox.battleId, battlebox.replayData.toString(), 1, 2);
				//System.out.println("killarr:"+killarr);
				//战斗结果
				JSONArray pusharr = new JSONArray();
				pusharr.add(num);
				pusharr.add(teambox1.getTotalPropValue(Const.PROP_HP));
				pusharr.add(teambox2.getTotalPropValue(Const.PROP_HP));
				pusharr.add(winTb.parameterarr.optInt(3));//疲劳度
				//pusharr.add(loseFac.getCurrBatterData());//下一个出场的选手
				pusharr.add(winTb.parameterarr.optInt(6));//胜利者队伍ID
				pusharr.add(loseTb.parameterarr.optInt(6));//失败者队伍ID
				pusharr.add(winTb.getDiedPartner());//胜利者死亡伙伴
				//pusharr.add(loseFac.teamlist.size());//失败方剩余队伍数
				pusharr.add(battlebox.battleId);//战斗ID
				PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_CB_BATTLE_RESULT, pusharr.toString());
				log.d("战斗结束推送内容："+pusharr.toString());
				log.d("休息"+CBDATA.battlespacetimelen+"秒后进入下一场战斗");
				//休眠
				Thread.sleep(CBDATA.battlespacetimelen*1000);
				//下一场战斗数据
				JSONArray pusharr2 = new JSONArray();
				pusharr2.add(num);
				pusharr2.add(loseFac.getCurrBatterData());//下一个出场的选手
				pusharr2.add(loseFac.teamlist.size());//失败方剩余队伍数
				PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_CB_NEXT_BATTLE, pusharr2.toString());
				//判断
				if(faction1.teamlist.size() <= 0 || faction2.teamlist.size() <= 0){
					log.d("有队伍全灭，结束城战");
					end = true;
					break;
				}
			}
			log.d("城战结束");
			DBHelper dbHelper = new DBHelper();
			try {
				if(type == 1){//自有城市
					if(faction1.teamlist.size() > 0){//进攻方胜利
						SqlString sqlStr = new SqlString();
						sqlStr.add("occupyselfcity", 1);
						FactionBAC.getInstance().update(dbHelper, faction1.factionid, sqlStr);
					}
				} else 
				if(type == 3){//公共城市
					SqlString sqlStr = new SqlString();
					if(faction1.teamlist.size() > 0){//进攻方胜利
						sqlStr.add("influencenum", faction1.influence);
						sqlStr.add("factionid", faction1.factionid);
						sqlStr.add("factionname", faction1.factionname);
						sqlStr.add("defnpclv", Conf.worldLevel);
						sqlStr.add("defnpcam", 0);
						log.d("攻方胜利 更新城市归属 NPC等级设置为"+Conf.worldLevel+" NPC数量清零");
					} else {//防守方胜利
						int npcamount = 0;
						while(npcamount < faction2.teamlist.size() && faction2.teamlist.get(npcamount).playerid == 0){
							npcamount++;
						}
						sqlStr.add("defnpcam", npcamount);
						log.d("守方胜利  NPC数量剩余"+npcamount);
					}
					String nextwartime = MyTools.getTimeStr(System.currentTimeMillis()+CBDATA.nowartimelen*MyTools.long_minu);
					sqlStr.addDateTime("nowarendtime", nextwartime);
					CBBAC.getInstance().update(dbHelper, Conf.sid, sqlStr, "serverid="+Conf.sid+" and citynum="+num);
					log.d("本城免战至"+nextwartime);
				}
				
				//发送击杀奖励
				JSONObject killawardobj = new JSONObject();
				for(int i = 0; i < killarr.length(); i++){
					JSONArray arr = killarr.optJSONArray(i);
					DBPaRs killawardRs = DBPool.getInst().pQueryA(CBBAC.tab_cb_killaward, "minlv<="+arr.optInt(2)+" and maxlv>="+arr.optInt(2));
					String award = null;
					if(arr.optInt(1) == 0){
						award = killawardRs.getString("plaaward");
					} else 
					if(arr.optInt(1) == 1){
						award = killawardRs.getString("npcaward1");
					} else 
					if(arr.optInt(1) == 2){
						award = killawardRs.getString("npcaward2");
					}
					if(!killawardobj.has(arr.optString(0))){
						killawardobj.put(arr.optString(0), award);
						killawardobj.put(arr.optString(0)+"kill", 1);
					} else {
						killawardobj.put(arr.optString(0), killawardobj.optString(arr.optString(0))+"|"+award);
						killawardobj.put(arr.optString(0)+"kill", killawardobj.optInt(arr.optString(0)+"kill")+1);
					}
					//System.out.println(killawardobj.optString(arr.optString(0)));
				}
				log.d("击杀奖励清单："+killawardobj.toString());
				@SuppressWarnings("unchecked")
				Iterator<String> keys = killawardobj.keys();
				while(keys.hasNext()){
					String pid = keys.next();
					if(!pid.contains("kill")){
						Object[] contentReplace = new Object[]{cityRs.getString("name"), 
								faction1.teamamount, 
								faction2.teamamount, 
								faction1.teamlist.size()>0?faction1.factionname:faction2.factionname,
								killawardobj.optInt(pid+"kill"),
								faction1.factionname,
								faction2.factionname};
						MailBAC.getInstance().sendModelMail(dbHelper, new int[]{Integer.valueOf(pid)}, 5, new Object[]{cityRs.getString("name")}, contentReplace, AwardBAC.getInstance().tiyContent(killawardobj.optString(pid)));		
					}
				}
				JSONArray pusharr = new JSONArray();
				pusharr.add(num);
				pusharr.add(faction1.teamamount);
				pusharr.add(faction2.teamamount);
				pusharr.add(faction1.teamlist.size()>0?Const.teamA:Const.teamB);
				PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_CB_BATTLE_END, pusharr.toString());
				
				String result = faction1.teamlist.size()>0?"胜利":"失败";
				StringBuffer remark = new StringBuffer();
				remark.append("攻方")
				.append(GameLog.formatNameID(faction1.factionname, faction1.factionid))
				.append("，队伍数：")
				.append(faction1.teamamount)
				.append("，守方")
				.append(GameLog.formatNameID(faction2.factionname, faction2.factionid))
				.append("队伍数：")
				.append(faction2.teamamount)
				.append("\r\n结果：")
				.append(result)
				.append("，城市：")
				.append(cityRs.getString("name"));
				
				GameLog.getInst(0, GameServlet.ACT_CB_BATTLE_RESULT)
				.addRemark(remark)
				.save();
				
				GamePushData.getInstance(faction1.teamlist.size()>0?7:8)
				.add(faction1.factionname)
				.add(cityRs.getString("name"))
				.sendToAllOL();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbHelper.closeConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.d("城战过程中发生异常 异常信息："+e.toString());
		} finally {
			for(int i = 0; i < partnerlist.size(); i++){//解放在此城中战斗的伙伴
				Partner partner = partnerlist.get(i);
				CBBAC.cbmgr.playermapRemove(partner.teambox.playerid);
				CBBAC.cbmgr.partnermap.remove(partner.id);
			}
			CBBAC.cbmgr.cbmap.remove(mapkey);
			CBBAC.cbmgr.declarefactionidList.remove((Integer)faction1.factionid);
			log.save();
			Out.println("城战结束，存储城战日志");
		}
	}
}
