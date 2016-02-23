package com.moonic.cb;

import java.util.ArrayList;

import org.json.JSONArray;

import com.moonic.bac.CBBAC;
import com.moonic.bac.PartnerBAC;
import com.moonic.battle.Const;
import com.moonic.battle.TeamBox;

/**
 * 帮派
 * @author John
 */
public class Faction {
	public int influence;//势力
	
	public int factionid;//帮派ID
	
	public String factionname;//帮派名
	
	public byte teamType;//所属队伍(攻/守)
	
	public int teamamount;//总队伍数
	
	public ArrayList<TeamBox> teamlist = new ArrayList<TeamBox>();//剩余队伍数
	
	public ArrayList<Integer> pidlist = new ArrayList<Integer>();//此阵营用有哪些角色的角色ID集合
	
	public City city;
	
	/**
	 * 加多个队伍
	 */
	public JSONArray addTeams(ArrayList<TeamBox> teamboxlist) throws Exception {
		JSONArray batteamidarr = new JSONArray();
		for(int i = 0; i < teamboxlist.size(); i++){
			int batteamid = addTeam(teamboxlist.get(i));
			batteamidarr.add(batteamid);
		}
		return batteamidarr;
	}
	
	/**
	 * 加队伍
	 */
	public synchronized int addTeam(TeamBox teambox) throws Exception {
		teambox.teamType = teamType;
		for(int i = 0; teambox.playerid != 0 && i < teambox.sprites.size(); i++){
			Partner partner = new Partner();
			partner.id = teambox.sprites.get(i).partnerId;
			partner.city = city;
			partner.teambox = teambox;
			if(teambox.playerid != 0 && !pidlist.contains(teambox.playerid)){
				pidlist.add(teambox.playerid);
			}
			city.partnerlist.add(partner);
			CBBAC.cbmgr.playermapAdd(partner.teambox.playerid);
			CBBAC.cbmgr.partnermap.put(partner.id, partner);
		}
		int teamid = CBBAC.cbmgr.createTeamid();
		int power = PartnerBAC.getInstance().getTeamBoxBattlePower(teambox);
		teambox.parameterarr.put(4, power);
		teambox.parameterarr.put(6, teamid);
		teambox.parameterarr.put(7, city.num);
		teambox.parameterarr.put(8, teamType);
		teamlist.add(teambox);
		city.log.d("加入战场 阵营："+(teambox.teamType==Const.teamA?"攻方":"守方")+" TEAMID："+teamid+" 队伍信息："+teambox.getTeamDataStr());
		teamamount++;
		return teamid;
	}
	
	/**
	 * 获取数据
	 */
	public JSONArray getData() throws Exception {
		JSONArray arr = new JSONArray();
		arr.add(factionname);//帮派名
		arr.add(teamlist.size());//队伍人数
		arr.add(getCurrBatterData());//当前战斗者数据
		return arr;
	}
	
	/**
	 * 获取数据2
	 */
	public JSONArray getData2() throws Exception {
		JSONArray arr = new JSONArray();
		arr.add(factionname);//帮派名
		arr.add(teamlist.size());//队伍人数
		return arr;
	}
	
	/**
	 * 获取将出战人物信息
	 */
	public JSONArray getBattlerData(){
		JSONArray rankingarr = new JSONArray();
		for(int i = 0; rankingarr.length() < 9 && i < teamlist.size(); i++){
			TeamBox teambox = teamlist.get(i);
			JSONArray arr = new JSONArray();
			arr.add(teambox.pname);//玩家名
			arr.add(teambox.parameterarr.optInt(1));//等级
			arr.add(teambox.parameterarr.optInt(4));//战力
			rankingarr.add(arr);
		}
		return rankingarr;
	}
	
	/**
	 * 获取当前战斗伙伴的数据
	 */
	public JSONArray getCurrBatterData() throws Exception {
		JSONArray batterdata = null;
		if(teamlist.size() > 0){
			TeamBox teambox = teamlist.get(0);
			batterdata = new JSONArray();
			batterdata.add(teambox.playerid);
			batterdata.add(teambox.pname);
			batterdata.add(teambox.parameterarr.optInt(2));//头像编号
			batterdata.add(teambox.parameterarr.optInt(1));//历练等级
			batterdata.add(teambox.parameterarr.optInt(3));//疲劳度
			batterdata.add(teambox.parameterarr.optInt(4));//战力
			batterdata.add(teambox.getTotalPropValue(Const.PROP_HP));//当前血量
			batterdata.add(teambox.getTotalPropValue(Const.PROP_MAXHP));//最大血量
			batterdata.add(teambox.getNumFormaction());//获取编号站位
		}
		return batterdata;
	}
}
