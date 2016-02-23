package com.moonic.battle;

public class BattleConfig {
	public static int maxTurns =40;  //限制回合数，到了该回合数未定出胜负，攻方算战败
	
	public static double atkFactor=1; //攻击系数
	public static double defFactor=1; //防御系数
	public static double harmFactor=1.2; //伤害系数
	
	public static int angryMaxValue=100; //最大怒气值	
	
	
	public static int hitAddAngry=20;  //普攻打人加怒
	public static int autoSkillAddAngry=30; //随机技打人加怒

	public static int beHitAddAngry=10;  //被普攻击中加怒
	public static int beAutoSkillHitAddAngry=15;  //被随机技能击中加怒
	public static int beAngrySkillHitAddAngry=15; //被怒技技能击中加怒
}
