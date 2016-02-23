package com.moonic.battle;

import java.util.ArrayList;

import server.common.Tools;

public class BattleResult {    
    public int toRoleHpChange; //被施放者血量的变化，伤害值为负，恢复值为正       
    public int fromRoleHpChange; //施放者血量的变化，伤害值为负，恢复值为正   
    
    public boolean isCriticalAtk; //是否暴击    
    public boolean isDodge; //目标成功闪避
    public boolean isBlock; //目标成功格挡
    
    public boolean angryValueChanged; //怒气值变化了
    public byte angryValue; //变化后的怒气值
    
    //private ArrayList<Buff> addBuffs; //命中的buff
    //public int[] removeBuffNums; //移除的buff
    
    public int comboCount; //连击次数    
    
     
    /*public short[] getBuffIds()
    {
    	if(addBuffs!=null && addBuffs.size()>0)
    	{    		
    		short[] ids = null;
    		for(int i=0;i<addBuffs.size();i++)
    		{
    			Buff buff = addBuffs.get(i);
    			ids = Tools.addToShortArr(ids, buff.buffFixData.num);
    		}
    		return ids;
    	}
    	return null;
    }*/
}
