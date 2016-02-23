package com.moonic.battle;


/**
 * 定义常量值专用 
 *
 */
public class Const 
{
	public static final byte teamA=0; //A队常量
	public static final byte teamB=1; //B队常量
	public static final byte teamCheat=-1; //玩家作弊
	
	public static final byte PROP_MAXHP=0; //最大生命值
	public static final byte PROP_ATTACK=1; //攻击
	public static final byte PROP_DEFENCE=2; //普防
	public static final byte PROP_MAGICDEF=3; //魔防
	public static final byte PROP_DODGE=4; //回避
	public static final byte PROP_HIT=5; //命中
	public static final byte PROP_BLOCK=6; //格挡
	public static final byte PROP_BREAK=7; //破击
	public static final byte PROP_TOUGHNESS=8; //韧性
	public static final byte PROP_CRITICAL=9; //暴击
	public static final byte PROP_CRITICAL_MUL=10; //暴击伤害值
	public static final byte PROP_SPEED=11; //速度
	public static final byte PROP_HP=12; //当前生命值
	public static final byte PROP_ANGER=13; //当前怒气
	public static final byte PROP_CRIT_ADD_DAMAGE=14; //增加暴击伤害
	public static final byte PROP_BECRIT_SUB_DAMAGE=15; //减少被暴击伤害
	
	public static final String[] BATTLE_PROP_NAME = {"最大生命值", "攻击", "普防", "魔防", "回避", "命中", "格挡", "破击", "韧性", "暴击", "暴击倍率", "速度", "当前生命值", "当前怒气值", "增加暴击伤害", "减少被暴击伤害"};
	
	public static final byte RANGE_FRONT_SINGLE=1;
	public static final byte RANGE_FRONT_LINE=2;
	public static final byte RANGE_COL=3;
	public static final byte RANGE_BACK_SINGLE=4;
	public static final byte RANGE_SELF=5;
	public static final byte RANGE_N_HIGH=6;
	public static final byte RANGE_N_LOW=7;
	public static final byte RANGE_ALL=8;
	public static final byte RANGE_SINGLE=9;
	public static final byte RANGE_LINE=10;
	public static final byte RANGE_BACK_LINE=11;
	public static final byte RANGE_SPLIT=12;
	
	public static final byte DISTANCE_NEAR=1;
	public static final byte DISTANCE_FAR=2;

	
	public static final byte ACTTYPE_NEAR=1;
	public static final byte ACTTYPE_FAR=2;
	public static final byte ACTTYPE_BACK=3;
	
	public static final byte TARGET_FRIEND=1;
	public static final byte TARGET_ENEMY=2;
	
	public static final byte SKILLTYPE_HARM=1;
	public static final byte SKILLTYPE_RECOVER=2;
	public static final byte SKILLTYPE_BUFF=3;
	
	public static final byte SKILLCATEGORY_AUTO=1;
	public static final byte SKILLCATEGORY_ANGRY=2;
	public static final byte SKILLCATEGORY_BE=3;
	
	//战斗者职业
	public static final byte SERIES_NEAR=1;
	public static final byte SERIES_MAGIC=2;
	public static final byte SERIES_DEF=3;
	public static final byte SERIES_KILL=4;
	public static final byte SERIES_HELP=5;
	
	//buff类型
	public static byte BUFF_TYPE_PROP=1; //改变属性
    public static byte BUFF_TYPE_SHELL=2;  //增加抵挡指定伤害值的护盾
    public static byte BUFF_TYPE_ABNORMAL_SHELL=3; //增加免疫指定次数异常状态的护盾
    public static byte BUFF_TYPE_CHANGEBODY=4;  //变身
    public static byte BUFF_TYPE_CHANGEHARM=5;  //改变受到伤害值
    public static byte BUFF_TYPE_DIZZY=6;  //晕眩
    public static byte BUFF_TYPE_UNCUREABLE=7;  //不能被加血
    public static byte BUFF_TYPE_LOSTHP=8;  //每回合开始前扣血
    public static byte BUFF_TYPE_CONFUSE=9;  //混乱，只攻击自己人
    public static byte BUFF_TYPE_SILENCE=10;  //沉默，无法施放主动技能和概率技能
    public static byte BUFF_TYPE_NOTADDANGRY=11;  //固定回合数内无法通过任何途径获得怒气
    public static byte BUFF_TYPE_WEAK=12;  //虚弱，对敌方造成的伤害减少
    public static byte BUFF_TYPE_CORPSE_EXPLODE=13;  //尸体爆炸：敌人被攻击后先加上该BUFF再计算伤害，该BUFF仅本次攻击有效，如果在存在该BUFF时单位被击杀，则对敌方所有单位造成伤害（特殊做，常用）
    public static byte BUFF_TYPE_DODGE_ALL=14;  //100%闪避所有伤害
    public static byte BUFF_TYPE_SPRIT_EAT=15;  //元神吞噬：敌人被攻击后先加上该BUFF再计算伤害，该BUFF仅本次攻击有效，如果在存在该BUFF时单位被击杀，则基于伤害值恢复自身生命（特殊做，常用）
    public static byte BUFF_TYPE_COMBO=16;  //连斩：敌人被攻击后先加上该BUFF再计算伤害，该BUFF仅本次攻击有效，如果在存在该BUFF时单位被击杀，则攻击方本次攻击不计算入攻击次数内，可立即再次使用怒气技能攻击下一个单位（特殊做，常用）
    public static byte BUFF_TYPE_DETER=17; //元神威慑：降低敌方指定属性最高的单位的指定属性    格式：17,条件属性编号#降低的属性编号，多个属性用,分割#
    
    public static byte HARM_TYPE_POINT=1;
    public static byte HARM_TYPE_DEBUFF=2;
    public static byte HARM_TYPE_PHY=3;
    public static byte HARM_TYPE_MAGIC=4; 
    
    public static byte BESKILL_TYPE3=3;
    public static byte BESKILL_TYPE4=4;
    public static byte BESKILL_TYPE5=5;
    public static byte BESKILL_TYPE6=6;
    public static byte BESKILL_TYPE7=7;
    public static byte BESKILL_TYPE8=8;
    public static byte BESKILL_TYPE9=9;
    public static byte BESKILL_TYPE11=11;
    public static byte BESKILL_TYPE12=12;
    public static byte BESKILL_TYPE13=13;
    public static byte BESKILL_TYPE14=14;
    public static byte BESKILL_TYPE17=17;
    
    static String[] propNames=new String[]{"生命","攻击","普防","法防","回避","命中","格挡","破击","韧性","暴击","暴击伤害值","速度","当前生命值","当前怒气","增加暴击伤害","减少被暴击伤害"};
    
    public static byte EXEC_RESULT_TYPE_HP=1;  //回合前执行影响血
    public static byte EXEC_RESULT_TYPE_ANGRY=2;  //回合前执行影响怒气
    public static byte EXEC_RESULT_TYPE_ADDBUFF=3;  //回合前执行加buff
    
}
