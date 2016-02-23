package com.moonic.battle;

public class BattleSkillFixData 
{   
	public int skillCategory;  //技能大类：1随机，2怒
	
	//随机字段 ：1编号	2技能名	3图标编码	4最大等级	5对自身BUFF编号	6触发几率	7施放时机	8技能类型	9对敌对友	10范围	11范围参数	12最大攻击次数	13战力	14计算参数	15暴击增幅	16BUFF编号	17BUFF几率	18其他参数	19其他触发概率	20命中率	21技能特效CFG编号	22延迟	23是否物理攻击	24飘字、出命中特效时间差（秒）	25目标身上特效编号	26人物坐标偏移	27命中音效	28动作形式	29动作名
	//怒字段：  1编号	2技能名	3图标编码	4最大等级	5怒气保留	6对自身BUFF编号	7技能类型	8对敌对友	9范围	10范围参数	11最大攻击次数	12战力	13计算参数	14暴击增幅	15BUFF编号	16BUFF几率	17其他参数	18其他触发概率	19驱散数量	20命中率	21技能特效CFG编号	22延迟	23是否物理攻击	24飘字、出命中特效时间差（秒）	25目标身上特效编号	26人物坐标偏移	27命中音效	28动作形式
	
	public int num;
    public String name;
    public byte maxLv; //最大等级
    public short selfBuff; //对自身BUFF编号
    public short powerLeft; //怒气保留
    public byte rate; //触发几率
    public short[] useTurn; //施放时机
    public byte type; //技能类型 1:伤害,2:恢复,3:buff
    public byte targetType; //对敌对友1:对友2：对敌
    public byte range; //攻击范围
    public short[] rangeArgs; //范围参数
    public byte maxUseTimes; //最大攻击次数
    public short[] harmArgs; //计算参数
    public short criticalAdd; //暴击增幅
    public short[] buffs; //BUFF编号
    public short[][] buffsRate; //BUFF几率数组
    public short[] otherArgs; //其他参数
    public byte otherRate; //其他触发概率
    public byte hitRate; //命中率
    public byte clearAmount; //驱散数量    
    
    //被动技能专用参数
    public short[][] args;
    public byte owner;
    
   
    public int calcExtraRecover(BattleRole battleRole,int maxHP)
    {    	       
        return 0;
    }
    /// <summary>
    /// 计算攻击魔法实际伤害
    /// </summary>
    /// <param name="baseValue"></param>
    /// <returns></returns>
    public int calcExtraHarm(BattleRole battleRole,int baseValue)
    {    	    
        return 0;
    }
    
}
