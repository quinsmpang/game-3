package com.moonic.team;

import org.json.JSONArray;
import org.json.JSONException;

import com.moonic.bac.PlaTeamBAC;
import com.moonic.bac.PlayerBAC;
import com.moonic.util.DBPaRs;

/**
 * 队伍成员
 * @author wkc
 */
public class Member {
	public int id;//ID
	public int num;//编号
	public String name;//名字
	public int lv;//等级
	
	public int times;//已获得奖励次数
	
	public boolean isLeader;//是否是队长
	public boolean isReady;//是否已准备
	
	public JSONArray partnerArr;//上阵伙伴
	
	public int battlePower;//战力
	
	/**
	 * 构造
	 * @throws Exception 
	 */
	public Member(int playerid) throws Exception{
		DBPaRs playerRs = PlayerBAC.getInstance().getDataRs(playerid);
		this.id = playerid;
		this.num = playerRs.getInt("num");
		this.name = playerRs.getString("name");
		this.lv = playerRs.getInt("lv");
		this.times = PlaTeamBAC.getInstance().getIntValue(playerid, "times");
		this.isLeader = false;
		this.isReady = false;
		this.partnerArr = new JSONArray();
	}
	
	/**
	 * 获取成员信息
	 */
	public JSONArray getMemberInfo(){
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(this.id);//ID
		jsonarr.add(this.num);//编号
		jsonarr.add(this.name);//名字
		jsonarr.add(this.lv);//等级
		jsonarr.add(this.isLeader ? 1 : 0);//是否为队长
		jsonarr.add(this.isReady ? 1 : 0);//是否准备
		jsonarr.add(this.battlePower);//战力
		jsonarr.add(this.partnerArr);//上证伙伴数据
		return jsonarr;
	}
	
	/**
	 * 获取阵型数据
	 * @throws JSONException 
	 */
	public JSONArray getPosArr() throws JSONException{
		JSONArray posArr = new JSONArray();
		for(int i = 0; i < partnerArr.length(); i++){
			JSONArray partner = partnerArr.getJSONArray(i);
			int partnerId = 0;
			if(partner.length() > 0){
				partnerId = partner.getInt(0);
			}
			posArr.add(partnerId);
		}
		return posArr;
	}
	
	/**
	 * 获取上阵伙伴数量
	 */
	public int getPartnerAm() throws JSONException{
		int partnerAm = 0;
		for(int i = 0; i < partnerArr.length(); i++){
			JSONArray partner = partnerArr.getJSONArray(i);
			if(partner.length() > 0){
				partnerAm++;
			}
		}
		return partnerAm;
	}
}
