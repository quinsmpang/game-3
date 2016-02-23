package com.moonic.worldboss;

import org.json.JSONArray;

import server.common.Sortable;

import com.moonic.bac.PlayerBAC;
import com.moonic.util.DBPaRs;

/**
 * 玩家
 * @author wkc
 */
public class WBPlayer implements Sortable{
	
	public int id;//ID
	public int num;//编号
	public String name;//名字
	public int lv;//等级
	
	public int chaTimes;//挑战次数
	
	public long totalDamage;//对BOSS总伤害
	
	public int rank;//排名
	
	public JSONArray partnerArr;//已参战的伙伴ID
	
	/**
	 * 构造
	 */
	public WBPlayer(int id) throws Exception{
		DBPaRs playerRs = PlayerBAC.getInstance().getDataRs(id);
		this.id = id;
		this.num = playerRs.getInt("num");
		this.name = playerRs.getString("name");
		this.lv = playerRs.getInt("lv");
		this.partnerArr = new JSONArray();
	}
	
	/**
	 * 获取角色数据
	 */
	public JSONArray getData(){
		JSONArray arr = new JSONArray();
		arr.add(chaTimes);
		arr.add(totalDamage);
		arr.add(rank);
		arr.add(partnerArr);
		return arr;
	}
	
	/**
	 * 获取排行角色数据
	 */
	public JSONArray getData1(){
		JSONArray arr = new JSONArray();
		arr.add(id);
		arr.add(num);
		arr.add(name);
		arr.add(lv);
		arr.add(totalDamage);
		return arr;
	}
	
	@Override
	public double getSortValue() {
		return totalDamage;
	}
}
