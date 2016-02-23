package com.moonic.battle;

import java.util.ArrayList;

import org.json.JSONArray;

import conf.Conf;

/**
 * 战斗数据箱
 * @author John
 */
public class BattleBox 
{	
	public int bgnum = 1;//战斗背景
	
	public boolean mustSaveLog;//是否必定存战斗日志
	
	public ArrayList<TeamBox>[] teamArr;//红蓝双方队伍,红队TeamBox[0],蓝队TeamBox[1] 允许某队有多个队伍，需要全部击败且不允许回血
	
	public JSONArray parameterarr;//自定义参数
	
	//---以下为战斗结束后需要赋值的内容---
	
	public long battleId = createBattleId();//战斗ID
	
	public byte winTeam;//胜利的队伍
	
	public JSONArray replayData;//录像数据 结构：JSONARRAY[第一场录像，第二场录像...第N场录像]
	
	private static long startBattleId = Conf.sid * 10000000000000L + System.currentTimeMillis(); //起始战斗id
	
	/**
	 * 创造BATTLEID
	 */
	private static synchronized long createBattleId(){
		return startBattleId++;
	}
	
	/**
	 * 构造
	 */
	public BattleBox(){
		teamArr = new ArrayList[2];
		teamArr[0] = new ArrayList<TeamBox>();
		teamArr[1] = new ArrayList<TeamBox>();
	}
	
	/**
	 * 获取数据
	 */
	public JSONArray getJSONArray(){		
		createSpriteIds(); //创建每个sprite角色的唯一id
		
		JSONArray teamsarr = new JSONArray();
		for(int i = 0; i < teamArr.length; i++){
			JSONArray tarr = new JSONArray();
			for(int k = 0; k < teamArr[i].size(); k++){
				tarr.add(teamArr[i].get(k).getJSONArray());
			}
			teamsarr.add(tarr);
		}
		JSONArray dataarr = new JSONArray();
		dataarr.add(bgnum);
		dataarr.add(teamsarr);
		return dataarr;
	}
	
	/**
	 * 创建双方角色的唯一id序列
	 */
	public void createSpriteIds()
	{
		int id=1;
		
		for (int i = 0; teamArr != null && i < teamArr.length; i++)   //遍历AB两队
		{
			ArrayList<TeamBox> teamBoxArr = teamArr[i];
			for(int j=0;j<teamBoxArr.size();j++)  //遍历A或B的某小队，P4都是只有一个小队
			{
				TeamBox team = teamBoxArr.get(j);
				
				for (int k = 0; team.sprites != null && k < team.sprites.size(); k++)  //遍历角色 
				{
					SpriteBox sprite = team.sprites.get(k);
					sprite.id = id;
					id++;
				}				
			}			
		}				
	}
}
