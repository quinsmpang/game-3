package com.moonic.cb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.SqlString;
import com.moonic.bac.CBBAC;
import com.moonic.bac.PartnerBAC;
import com.moonic.bac.PlaFacBAC;
import com.moonic.bac.PlayerBAC;
import com.moonic.bac.VipBAC;
import com.moonic.battle.Const;
import com.moonic.battle.TeamBox;
import com.moonic.gamelog.GameLog;
import com.moonic.txtdata.CBDATA;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MyLog;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 国战
 * @author John
 */
public class CBMgr {
	public HashMap<Integer, City> cbmap = new HashMap<Integer, City>();//国战集合
	
	public HashMap<Integer, Integer> playermap = new HashMap<Integer, Integer>();//参战玩家
	
	public HashMap<Integer, Partner> partnermap = new HashMap<Integer, Partner>();//参战伙伴
	
	public ArrayList<Integer> declarefactionidList = new ArrayList<Integer>();//进攻帮派ID集合
	
	public ScheduledExecutorService timertimer = MyTools.createTimer(3);
	
	private MyLog log = new MyLog(MyLog.NAME_DATE, "cb", "CB", true, true, true, null);
	
	private int teamid = 1;
	
	/**
	 * 创建队伍ID
	 */
	public synchronized int createTeamid(){
		return teamid++;
	}
	
	/**
	 * 启动一场国战
	 */
	public City startCB(int mapkey, int citynum, int citytype, int influence1, int factionid1, String factionname1, ArrayList<TeamBox> teamlist1, int influence2, int factionid2, String factionname2, ArrayList<TeamBox> teamlist2) throws Exception {
		if(cbmap.containsKey(mapkey)){
			BACException.throwInstance("已经有其他帮派宣战了");
		}
		DBPaRs cityRs = DBPool.getInst().pQueryA(CBBAC.tab_cb_city, "num="+citynum);
		final City city = new City();
		city.mapkey = mapkey;
		city.num = citynum;
		city.name = cityRs.getString("name");
		city.type = citytype;
		city.battletime = System.currentTimeMillis()+MyTools.long_minu*CBDATA.declarewarwaittimelen;
		String logName = null;
		if(influence1 != 0){
			logName = "BATTLE-"+MyTools.formatTime("yyyyMMddHHmmss")+"-"+citynum+"-NPC";
		} else {
			logName = "BATTLE-"+MyTools.formatTime("yyyyMMddHHmmss")+"-"+citynum+"-PVP";
		}
		final String logNameEx = logName;
		city.log = new MyLog(MyLog.NAME_CUSTOM, "cb", "CBCITY", true, false, false, logName);
		Faction faction1 = new Faction();
		faction1.city = city;
		faction1.influence = influence1;
		faction1.factionid = factionid1;
		faction1.factionname = factionname1;
		faction1.teamType = 0;
		city.batteamidarr1 = faction1.addTeams(teamlist1);
		Faction faction2 = new Faction();
		faction2.city = city;
		faction2.influence = influence2;
		faction2.factionid = factionid2;
		faction2.factionname = factionname2;
		faction2.teamType = 1;
		faction2.addTeams(teamlist2);
		city.faction1 = faction1;
		city.faction2 = faction2;
		cbmap.put(mapkey, city);
		log.d("帮派"+factionid1+"向城市"+citynum+"宣战，战斗将在"+CBDATA.declarewarwaittimelen+"分钟后开始");
		timertimer.schedule(new MyTimerTask() {
			public void run2() {
				log.d("宣战时间结束，启动城市"+city.num+"的战斗");
				(new Thread(city, "国战战斗线程："+logNameEx)).start();
			}
		}, MyTools.long_minu*CBDATA.declarewarwaittimelen, TimeUnit.MILLISECONDS);
		declarefactionidList.add(factionid1);
		return city;
	}
	
	/**
	 * 加入城战
	 */
	public int join(int mapkey, byte teamType, TeamBox teambox) throws Exception {
		City city = cbmap.get(mapkey);
		if(city == null){
			BACException.throwInstance("目标城市无战斗");
		}
		if(city.end){
			BACException.throwInstance("战斗已结束");
		}
		int teamid = 0;
		if(teamType == 0){
			teamid = city.faction1.addTeam(teambox);
		} else 
		if(teamType == 1){
			teamid = city.faction2.addTeam(teambox);
		}
		return teamid;
	}
	
	/**
	 * 复活参战伙伴
	 */
	public void relivePartner(DBHelper dbHelper, int playerid, String partneridarrStr, GameLog gl) throws Exception {
		JSONArray partneridarr = new JSONArray(partneridarrStr);
		if(partneridarr.length() == 0){
			BACException.throwInstance("要复活的伙伴数组为空");
		}
		int vip = PlayerBAC.getInstance().getIntValue(playerid, "vip");
		DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
		JSONObject relivedataarr = new JSONObject(plafacRs.getString("cbpartnerrelivedata"));
		int maxamount = VipBAC.getInstance().getVipFuncData(vip, 9);
		for(int i = 0; i < partneridarr.length(); i++){
			int partnerid = partneridarr.optInt(i);
			Partner partner = partnermap.get(partnerid);
			if(partner == null){
				BACException.throwInstance("伙伴未参战 partnerid="+partnerid);
			}
			if(!partner.died){
				BACException.throwInstance("伙伴未阵亡 partnerid="+partnerid);
			}
			int relivedamount = relivedataarr.optInt(String.valueOf(partnerid));
			if(relivedamount >= maxamount){
				BACException.throwInstance("复活次数已满 partnerid="+partnerid);
			}
			relivedataarr.put(String.valueOf(partnerid), relivedamount+1);
		}
		PlayerBAC.getInstance().useCoin(dbHelper, playerid, partneridarr.length()*CBDATA.reliveprice, gl);
		
		SqlString sqlStr = new SqlString();
		sqlStr.add("cbpartnerrelivedata", relivedataarr.toString());
		PlaFacBAC.getInstance().update(dbHelper, playerid, sqlStr);
		
		for(int p = 0; p < partneridarr.length(); p++){
			int partnerid = partneridarr.optInt(p);
			Partner partner = partnermap.get(partnerid);
			for(int i = 0; i < partner.teambox.sprites.size(); i++){//从队伍中移除
				if(partner.teambox.sprites.get(i).partnerId == partnerid){
					partner.teambox.sprites.remove(i);
					break;
				}
			}
			playermapRemove(partner.teambox.playerid);//玩家参战记录--
			partnermap.remove(partnerid);//从国战中移除
		}
	}
	
	/**
	 * ++玩家参战记录
	 */
	public void playermapAdd(int playerid){
		if(playermap.containsKey(playerid)){
			playermap.put(playerid, playermap.get(playerid) + 1);
		} else {
			playermap.put(playerid, 1);
		}
	}
	
	/**
	 * --玩家参战记录
	 */
	public void playermapRemove(int playerid){
		if(playermap.containsKey(playerid)){
			int count = playermap.get(playerid);
			if(count > 1){
				playermap.put(playerid, count - 1);
			} else {
				playermap.remove(playerid);
			}
		}
	}
	
	/**
	 * 检查队伍是否可用于国战
	 */
	public void checkJoin(int playerid, JSONArray posarr) throws Exception {
		JSONArray posarr2 = PartnerBAC.getInstance().checkPosarr(playerid, posarr, Conf.worldLevel-10, 5);
		for(int i = 0; i < posarr2.length(); i++){
			if(partnermap.get(posarr2.get(i)) != null){
				BACException.throwInstance("伙伴("+posarr2.get(i)+")正在参与国战");
			}
		}
	}
	
	/**
	 * 判断指定帮派在城战中的所属队伍
	 */
	public byte getFactionTeamType(int mapkey, int factionid) throws Exception {
		City city = cbmap.get(mapkey);
		if(city == null){
			BACException.throwInstance("目标城市无战斗");
		}
		if(city.faction1.factionid != 0 && factionid == city.faction1.factionid){
			return Const.teamA;
		} else 
		if(city.faction2.factionid != 0 && factionid == city.faction2.factionid){
			return Const.teamB;
		} else 
		{
			return -1;
		}
	}
	
	/**
	 * 检查城市是否在战争中
	 */
	public boolean checkCityInWar(int mapkey){
		City city = cbmap.get(mapkey);
		return city!=null && !city.end;
	}
	
	/**
	 * 检查伙伴是否在战争中
	 */
	public boolean checkPartnerInWar(int partnerid){
		return partnermap.get(partnerid)!=null;
	}
	
	/**
	 * 获取参与国战的伙伴的状态
	 */
	public JSONArray getPartnerWarState(JSONArray partnerarr){
		ArrayList<TeamBox> teamboxList = new ArrayList<TeamBox>();
		for(int i = 0; i < partnerarr.size(); i++){
			Partner partner = partnermap.get(partnerarr.optInt(i));
			if(partner != null && !teamboxList.contains(partner.teambox)){
				teamboxList.add(partner.teambox);
			}
		}
		JSONArray battlearr = new JSONArray();
		for(int i = 0; i < teamboxList.size(); i++){
			TeamBox teambox = teamboxList.get(i);
			JSONArray arr = new JSONArray();
			for(int j = 0; j < teambox.sprites.size(); j++){
				arr.add(teambox.sprites.get(j).partnerId);
				arr.add(teambox.sprites.get(j).battle_prop[Const.PROP_HP]>0?1:0);
			}
			JSONArray teamarr = new JSONArray();
			teamarr.add(teambox.parameterarr.optInt(6));//队伍ID
			teamarr.add(teambox.parameterarr.optInt(7));//所在城市编号
			teamarr.add(teambox.parameterarr.optInt(8));//所属战斗方
			teamarr.add(arr);
			battlearr.add(teamarr);
		}
		return battlearr;
	}
	
	/**
	 * 获取出场顺序
	 */
	public JSONArray getBattlerList(int mapkey, int teamType) throws Exception {
		City city = cbmap.get(mapkey);
		if(city == null){
			BACException.throwInstance("城池无战事");
		}
		return city.getBatterList(teamType);
	}
	
	/**
	 * 获取击杀排行
	 */
	public JSONArray getKillRanking(int playerid, int mapkey) throws Exception {
		City city = cbmap.get(mapkey);
		if(city == null){
			BACException.throwInstance("城池无战事");
		}
		return city.getKillRanking(playerid);
	}
	
	/**
	 * 获取城池
	 */
	public City getCity(int mapkey) throws Exception {
		City city = cbmap.get(mapkey);
		if(city == null){
			BACException.throwInstance("城池无战事");
		}
		return city;
	}
	
	/**
	 * 获取城池战斗数据
	 */
	public JSONArray getCityData(int mapkey) throws Exception {
		City city = cbmap.get(mapkey);
		if(city == null){
			BACException.throwInstance("城池无战事");
		}
		return city.getData();
	}
	
	/**
	 * 获取城池战斗数据2
	 */
	public JSONArray getCityData2(int mapkey) throws Exception {
		City city = cbmap.get(mapkey);
		if(city == null){
			return null;
		}
		return city.getData2();
	}
}
