package com.moonic.battle;

public class Buff 
{     
    public BuffFixData buffFixData;
    //public int num;    
    public short level; //级别  
    public int shellHP; //代替减免伤害总量
    public byte shellAbnormal; //异常防御次数
    public short fromRoleAtk;  //施放者的攻击力
    public boolean haveExeced; //被执行过，必须被执行过才开始减剩余回合数    
    public byte turns; //剩余回合数
    public boolean isChangeBuff; //是变身buff
}
