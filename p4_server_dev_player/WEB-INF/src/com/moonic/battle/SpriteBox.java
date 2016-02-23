package com.moonic.battle;

import org.json.JSONArray;

import server.common.Tools;

import com.moonic.bac.PartnerBAC;
import com.moonic.util.BACException;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

import conf.Conf;

/**
 * 战斗者数据箱
 * @author John
 */
public class SpriteBox 
{		
	public int id; //战斗中的唯一标识id
	public int playerId;//所属主角ID
	public int partnerId;//伙伴ID
	public byte type;//战斗者类型(1.TAB_PARTNER 2.TAB_ENEMY)
	public int num;//编号
	public short level; //等级
	public String name;//名称
	public byte phase = 1;//伙伴阶数
	public byte star = 1;//伙伴星级
	
	//---辅助数据---
	
	public byte battletype;//战斗者攻击类型(职业) 职业编号：1 近战型  2 法术型 3 肉盾型 4 刺杀型 5 辅助型
	public byte sex;//性别 1男 2女
	
	//---以下参数不需要在创建SPRITEBOX对象时赋值---
	
	public byte teamType; //0:A队 1:B队
	public byte posNum;//位置编号 从1开始，从左到后，从前到后顺序编号
	
	//---以下参数不需要主动赋值---
	
	public static final byte BATTLE_PROP_LEN = 16;
	
	public int[] battle_prop = new int[BATTLE_PROP_LEN];//战斗属性
	
	public JSONArray autoSkills = new JSONArray();//技能[[技能编号,技能等级],...]
	public JSONArray angrySkills = new JSONArray();//技能[[技能编号,技能等级],...] --主动技能最多一个
	public JSONArray beSkills = new JSONArray();//技能[[技能编号,技能等级],...]
	
	public int skillAddBattlerPower;//技能额外增加的战力
	
	//---以下参数仅在转换时用于数值计算---
	
	public int[] battle_prop_perc = new int[BATTLE_PROP_LEN];//战斗属性加成百分比
	public int[] battle_prop_save = new int[BATTLE_PROP_LEN];//用于计算战斗属性加成百分比的属性
	public int[] battle_prop_src = new int[BATTLE_PROP_LEN];//生成出来的原始属性
	
	/**
	 * 保存用于计算战斗属性加成百分比的属性
	 */
	public void saveBattlePropSave(){
		for(int i = 0; i < battle_prop_perc.length; i++){
			battle_prop_save[i] = battle_prop[i];
		}
	}
	
	/**
	 * 保存原始属性
	 */
	public void saveBattlePropSrc(){
		for(int i = 0; i < battle_prop_perc.length; i++){
			battle_prop_src[i] = battle_prop[i];
		}
	}
	
	/**
	 * 转换
	 */
	public void conver() throws Exception {
		//被动技能
		for(int i = 0; i < beSkills.length(); i++){
			JSONArray skiarr = beSkills.optJSONArray(i);
			int skinum = skiarr.optInt(0);
			int skilv = skiarr.optInt(1);
			DBPaRs skiRs = DBPool.getInst().pQueryA(PartnerBAC.tab_bskill, "num="+skinum);
			double[][] attr = Tools.splitStrToDoubleArr2(skiRs.getString("attr"), "|", ",");
			for(int k = 0; k < attr.length; k++){
				if(attr[k][0] == 1){
					addProp(attr[k][0], attr[k][1], attr[k][2], attr[k][3]+attr[k][4]*(skilv-1));
				}
			}
		}
		updateIngredientData("被动技能");
		//技能战力
		JSONArray[] skigrouparr = {beSkills, autoSkills, angrySkills};
		for(int s = 0; s < skigrouparr.length; s++){
			for(int i = 0; i < skigrouparr[s].length(); i++){
				JSONArray skiarr = skigrouparr[s].optJSONArray(i);
				int skinum = skiarr.optInt(0);
				int skilv = skiarr.optInt(1);
				DBPaRs sbpRs = DBPool.getInst().pQueryA(PartnerBAC.tab_skill_battlepower, "num="+skinum);
				int[] power = Tools.splitStrToIntArr(sbpRs.getString("power"), ",");
				skillAddBattlerPower += power[0] + power[1] * (skilv - 1);
			}	
		}
		//属性百分比
		if(battle_prop_save == null){//为空则补充
			saveBattlePropSave();
		}
		for(int i = 0; i < battle_prop_perc.length; i++){
			if(battle_prop_perc[i] > 0){
				addProp(1, i, 1, battle_prop_save[i] * battle_prop_perc[i] / 100);
			}
		}
		updateIngredientData("属性百分比");
		//血量初始化
		battle_prop[Const.PROP_HP] = battle_prop[Const.PROP_MAXHP];
		updateIngredientData("血量初始化");
		//存原始战斗属性
		saveBattlePropSrc();
	}
	
	/**
	 * 获取数据
	 */
	public JSONArray getJSONArray(){
		JSONArray dataarr = new JSONArray();
		dataarr.add(id);
		dataarr.add(playerId);
		dataarr.add(partnerId);
		dataarr.add(type);
		dataarr.add(num);
		dataarr.add(level);
		dataarr.add(name);
		dataarr.add(phase);
		dataarr.add(star);
		
		dataarr.add(teamType);
		dataarr.add(posNum);
		
		dataarr.add(battle_prop);
		
		dataarr.add(autoSkills);
		dataarr.add(angrySkills);
		dataarr.add(beSkills);
		
		dataarr.add(battletype);
		dataarr.add(sex);
		return dataarr;
	}
	
	/**
	 * 加入战斗属性数组到属性箱
	 */
	public void addBattleProp(int[] data) throws Exception {
		if(data.length != BATTLE_PROP_LEN){
			return;
		}
		for(int i = 0; i < BATTLE_PROP_LEN; i++){
			addProp(1, i, 1, data[i]);
		}
	}
	
	/**
	 * 加入属性到属性箱
	 */
	public void addProp(String data) throws Exception {
		if(data.equals("-1")){
			return;
		}
		addProp(Tools.splitStrToDoubleArr2(data, "|", ","));
	}
	
	/**
	 * 加入属性到属性箱
	 */
	public void addProp(double[][] data) throws Exception {
		for(int i = 0; data != null && i < data.length; i++){
			addProp(data[i]);
		}
	}
	
	/**
	 * 加入属性
	 */
	public void addProp(double... data) throws Exception {
		int type = (int)data[0];
		if(type != 1){//战斗属性){
			BACException.throwAndOutInstance("非战斗属性类型 type="+type);
		}
		if(data[2] == 0){
			battle_prop_perc[(int)data[1]] += data[3];
		} else {
			battle_prop[(int)data[1]] += data[3];
		}
	}
	
	/**
	 * 加入技能
	 */
	public void addSkill(int num, int level) throws Exception {
		if(num <= 1000){
			angrySkills.add(new JSONArray(new int[]{num, level}));
		} else 
		if(num <= 2000) {
			beSkills.add(new JSONArray(new int[]{num, level}));
		} else 
		{
			autoSkills.add(new JSONArray(new int[]{num, level}));
		}
	}
	
	public String[] ingredientTag;
	public long[][] ingredientData = new long[BATTLE_PROP_LEN*2][];
	
	/**
	 * 更新成分数据
	 */
	public void updateIngredientData(String tag){
		if(!Conf.gdout){
			return;
		}
		ingredientTag = Tools.addToStrArr(ingredientTag, tag);
		int index = 0;
		for(int i = 0; i < battle_prop.length; i++,index++){
			ingredientData[index] = Tools.addToLongArr(ingredientData[index], battle_prop[i]);
		}
		for(int i = 0; i < battle_prop_perc.length; i++,index++){
			ingredientData[index] = Tools.addToLongArr(ingredientData[index], battle_prop_perc[i]);
		}
	}
	
	/**
	 * 获取成分数据字符串
	 */
	public String getIngredientStr(){
		if(ingredientTag == null){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("-------------------------------------"+name+"("+partnerId+")-------------------------------------\r\n");
		int index = 0;
		for(int i = 0; i < battle_prop.length; i++,index++){
			sb.append(Const.BATTLE_PROP_NAME[i] + getOneAttrIngredientStr(ingredientTag, ingredientData[index]));
		}
		for(int i = 0; i < battle_prop_perc.length; i++,index++){
			sb.append(Const.BATTLE_PROP_NAME[i] + "百分比" + getOneAttrIngredientStr(ingredientTag, ingredientData[index]));
		}
		return sb.toString();
	}
	
	/**
	 * 根据 tag 和数组返回字符串
	 */
	private String getOneAttrIngredientStr(String[] tag, long[] data){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < tag.length; i++){
			long addvalue = i==0 ? data[i] : data[i]-data[i-1];
			//if(addvalue > 0)
			{
				sb.append(" ");
				sb.append(tag[i] + ":" + addvalue);		
			}
		}
		sb.append(" ");
		sb.append("总:"+data[data.length-1]);
		sb.append("\r\n");
		return sb.toString();
	}
}
