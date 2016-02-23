package com.moonic.battle;

import org.json.JSONObject;

/**
 * json键值转换封装对象
 * @author huangyan
 *
 */
public class JSONWrap 
{
	private JSONObject json;	
	
	static public class KEY
	{
		public static final String ID="a"; //role id
		public static final String CMD="b"; //战斗指令编号
		public static final String TARGET_IDS="c"; //目标roles id
		public static final String SOURCE_HP_CHANGE="d"; //来源角色HP变化值		
		public static final String TARGET_HP_CHANGE="e"; //目标角色HP变化值
		public static final String SKILL_NUM="f"; //技能编号
		public static final String CRITICAL="g"; //是否暴击
		public static final String BLOCK="h"; //格挡 
		public static final String DODGE="i"; //闪避						
		public static final String ANGRY="j";  //怒气变化后的值
		public static final String WIN_TEAM="win";  //胜利的队伍号
		public static final String ADD_BUFF="add"; //被命中的buff
		public static final String QUEUE="que";  //每回合战斗流程
		public static final String EXECBUFF="buf";  //每回合前执行BUFF结果
		public static final String REMOVE_BUFF="del"; //被清除的buff		
		public static final String PRE="pre"; //战前行为	
		public static final String BLUE_BLOOD="blue"; //盾牌蓝色扣血	
		public static final String EXECBESKILL="beskl";  //每回合前执行被动技能结果
		public static final String LEFTHP="left";  //每回合剩余血量
	}
	
	public JSONWrap()
	{
		json = new JSONObject();
	}
	public void put(String key,int value)
	{
		json.put(key, value);
	}
	public void put(String key,Object value)
	{
		json.put(key, value);
	}
	public JSONObject getJsonObj()
	{
		return json;
	}
}
