package com.moonic.battle;

/**
 * 战斗指令对象
 * @author huangyan
 *
 */
public class Command 
{	
	public byte commandType; //指令类型
	
	public static final byte CMD_NONE=0; //无行动
	public static final byte CMD_WAIT=1;  //待机
	public static final byte CMD_ATTACK=2; //普通攻击
	public static final byte CMD_AUTOSKILL=3; //随机技能
	public static final byte CMD_ANGRYSKILL=4; //怒技能    	

    public int[] targetIds; //目标群体id集合
    //技能攻击时
    public int skillNum; //使用的技能编号
    public byte skillLevel=1; //服务端设置的技能等级
    
    public int ranStart; //使用的随机数起始值
    public int ranEnd; //使用的随机数结束值    
    
    
    public Command(int type)
    {
    	commandType = (byte)type;
    }
    public Command()
    {
    	
    }   
}
