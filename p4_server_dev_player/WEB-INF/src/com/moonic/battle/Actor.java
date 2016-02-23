package com.moonic.battle;

/**
 * 战斗行动者 
 *
 */
public class Actor {
	public BattleRole battleRole;  //对应的角色
	public int cmdType;	
	public BattleSkill battleSkill;
	
	public JSONWrap doBattle()
	{
	    battleRole.actor = this;	    
	    return battleRole.doBattle();
	}
}
