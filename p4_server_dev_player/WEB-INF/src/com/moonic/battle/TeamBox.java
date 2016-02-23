package com.moonic.battle;

import java.util.ArrayList;

import org.json.JSONArray;

import com.moonic.bac.*;

/**
 * 一个玩家小队的数据箱
 * @author John
 */
public class TeamBox 
{
	public byte teamType;//所属队伍
	
	public int playerid;
	public String pname;
	public int pnum;//头像编号
	
	//国战：[NPC类型，等级，头像编号，疲劳度，战力，累计减能力百分比，队伍ID，所在城市编号，所属方]
	public JSONArray parameterarr;//自定义参数
	
	public ArrayList<SpriteBox> sprites = new ArrayList<SpriteBox>();//参战伙伴集合
	
	/**
	 * 获取数据
	 */
	public JSONArray getJSONArray(){
		JSONArray spritesarr = new JSONArray();
		for(int i = 0; i < sprites.size(); i++){
			spritesarr.add(sprites.get(i).getJSONArray());
		}
		int power=0;
		try
		{
			power = PartnerBAC.getInstance().getTeamBoxBattlePower(this);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		JSONArray dataarr = new JSONArray();
		dataarr.add(teamType);
		dataarr.add(playerid);
		dataarr.add(pname);
		dataarr.add(power);
		dataarr.add(spritesarr);
		dataarr.add(pnum);
		return dataarr;
	}
	
	/**
	 * 获取队伍简要数据字符串
	 */
	public String getTeamDataStr(){
		StringBuffer sb = new StringBuffer();
		sb.append(pname+"("+playerid+")");
		sb.append("[");
		for(int i = 0; i < sprites.size(); i++){
			SpriteBox spritebox = sprites.get(i);
			sb.append(spritebox.name+"("+spritebox.partnerId+"),");
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 获取伙伴指定属性总值
	 */
	public int getTotalPropValue(byte type){
		int value = 0;
		for(int i = 0; i < sprites.size(); i++){
			SpriteBox spritebox = sprites.get(i);
			value += spritebox.battle_prop[type];
		}
		return value;
	}
	
	/**
	 * 获取已死亡伙伴数组
	 */
	public JSONArray getDiedPartner(){
		JSONArray arr = new JSONArray();
		for(int i = 0; i < sprites.size(); i++){
			SpriteBox spritebox = sprites.get(i);
			if(spritebox.battle_prop[Const.PROP_HP] <= 0){
				arr.add(spritebox.partnerId);		
			}
		}
		return arr;
	}
	
	/**
	 * 获取伙伴编号与站位
	 */
	public JSONArray getNumFormaction() throws Exception {
		JSONArray arr = new JSONArray();
		for(int i = 0; i < sprites.size(); i++){
			SpriteBox spritebox = sprites.get(i);
			arr.put(spritebox.posNum-1, spritebox.num);
			//System.out.println(spritebox.name+" "+spritebox.posNum+" "+spritebox.num);
		}
		//System.out.println("-------------------");
		return arr;
	}
}
