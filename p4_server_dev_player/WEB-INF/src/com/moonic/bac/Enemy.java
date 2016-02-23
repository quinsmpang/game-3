package com.moonic.bac;

import org.json.JSONArray;

import server.common.Tools;

import com.moonic.battle.Const;
import com.moonic.battle.SpriteBox;
import com.moonic.battle.TeamBox;
import com.moonic.util.BACException;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 能力定型敌人
 * @author wkc
 */
public class Enemy {
	public static final String tab_enemy = "tab_enemy";
	
	/**
	 * 创建敌人队伍TeamBox
	 */
	public TeamBox createTeamBox(String enemy) throws Exception{
		return createTeamBox(enemy, null);
	}
	
	/**
	 * 创建敌人队伍TeamBox
	 * @param enemy,敌人编号String
	 * @param hparr,对应当前血量arr
	 */
	public TeamBox createTeamBox(String enemy, JSONArray hparr) throws Exception{
		TeamBox teamBox = new TeamBox();
		teamBox.teamType = 1;
		int[][] enemyarr = Tools.splitStrToIntArr2(enemy, "|", ",");
		byte pos = 1;
		boolean isNull = hparr == null ? true : false;
		for(int i = 0; i < enemyarr.length; i++){
			for(int j = 0; j < enemyarr[i].length; j++){
				if(enemyarr[i][j] != 0){
					if(isNull){
						SpriteBox spriteBox = createEnemyBox(enemyarr[i][j]);
						spriteBox.teamType = 1;
						spriteBox.posNum = pos;
						teamBox.sprites.add(spriteBox);
					} else{
						int currhp = hparr.optInt(pos-1);
						if(currhp > 0){
							SpriteBox spriteBox = createEnemyBox(enemyarr[i][j]);
							spriteBox.teamType = 1;
							spriteBox.posNum = pos;
							spriteBox.battle_prop[Const.PROP_HP] = currhp;
							teamBox.sprites.add(spriteBox);
						}
					}
				}
				pos++;
			}
		}
		return teamBox;
	}
	
	/**
	 * 创建敌人SpriteBox
	 * @param enemynum
	 */
	public SpriteBox createEnemyBox(int enemynum) throws Exception{
		DBPaRs enemyListRs = DBPool.getInst().pQueryA(tab_enemy, "num="+enemynum);
		if(!enemyListRs.exist()){
			BACException.throwInstance("不存在的敌人编号"+enemynum);
		}
		SpriteBox spriteBox = new SpriteBox();
		//基础数据
		spriteBox.type = 2;
		spriteBox.num = enemyListRs.getInt("num");
		spriteBox.name = enemyListRs.getString("name");
		spriteBox.level = enemyListRs.getShort("lv");
		spriteBox.battletype = enemyListRs.getByte("battletype");
		spriteBox.star = enemyListRs.getByte("star");
		spriteBox.phase = enemyListRs.getByte("quality");
		spriteBox.sex = enemyListRs.getByte("sex");
		//基础属性
		for(int i = 1; i <= 12; i++){
			spriteBox.addProp(1, i-1, 1, enemyListRs.getInt("prop"+i));
		}
		spriteBox.updateIngredientData("基础属性");
		//初始怒气
		int initAnger = enemyListRs.getInt("prop13");
		if(initAnger > 0){
			spriteBox.addProp(1, Const.PROP_ANGER, 1, initAnger);
		}
		//技能
		String skillStr = enemyListRs.getString("skill");
		if(!skillStr.equals("0")){
			int[][] skillarr = Tools.splitStrToIntArr2(skillStr, "|", ",");
			for(int i = 0; i < skillarr.length; i++){
				spriteBox.addSkill(skillarr[i][0], skillarr[i][1]);
			}
		}
		spriteBox.updateIngredientData("技能");
		//几率技能
		String skillStrOdds = enemyListRs.getString("odds");
		if(!skillStrOdds.equals("0")){
			int[][] skillarr = Tools.splitStrToIntArr2(skillStrOdds, "|", ",");
			for(int i = 0; i < skillarr.length; i++){
				spriteBox.addSkill(skillarr[i][0], skillarr[i][1]);
			}
		}
		//被动技能
		String skillStrb = enemyListRs.getString("bskill");
		if(!skillStrb.equals("0")){
			int[][] skillarr = Tools.splitStrToIntArr2(skillStrb, "|", ",");
			for(int i = 0; i < skillarr.length; i++){
				spriteBox.addSkill(skillarr[i][0], skillarr[i][1]);
			}
		}
		//转换
		spriteBox.conver();
		return spriteBox;
	}
	
	//--------------静态区--------------
	
	private static Enemy instance = new Enemy();
	
	/**
	 * 获取实例
	 */
	public static Enemy getInstance(){
		return instance;
	}
}
