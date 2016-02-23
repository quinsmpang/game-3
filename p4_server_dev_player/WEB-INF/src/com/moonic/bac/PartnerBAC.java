package com.moonic.bac;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Sortable;
import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.battle.BattleBox;
import com.moonic.battle.BattleManager;
import com.moonic.battle.Const;
import com.moonic.battle.SpriteBox;
import com.moonic.battle.SpriteBoxExtendPropListener;
import com.moonic.battle.TeamBox;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.GamePushData;
import com.moonic.socket.Player;
import com.moonic.socket.SocketServer;
import com.moonic.txtdata.PartnerAwakenData;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPRs;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 伙伴
 * @author John
 */
public class PartnerBAC extends PlaStorBAC {
	public static final String tab_partner = "tab_partner";
	public static final String tab_partner_fetter = "tab_partner_fetter";
	public static final String tab_partner_phase_attr = "tab_partner_phase_attr";
	public static final String tab_partner_skill_uplv = "tab_partner_skill_uplv";
	public static final String tab_partner_uplv = "tab_partner_uplv";
	public static final String tab_partner_upphase = "tab_partner_upphase";
	public static final String tab_partner_upstar = "tab_partner_upstar";
	public static final String tab_bskill = "tab_bskill";
	public static final String tab_battlepower = "tab_battlepower";
	public static final String tab_skill_battlepower = "tab_skill_battlepower";
	
	/**
	 * 构造
	 */
	public PartnerBAC() {
		super("tab_partner_stor", "playerid", "id");
	}
	
	/**
	 * 穿装备
	 */
	public ReturnValue putonEquip(int playerid, int partnerid, int partnerid2, int itemid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			if(CBBAC.cbmgr.checkPartnerInWar(partnerid)){
				BACException.throwInstance("伙伴正在国战中，无法更换装备");
			}
			DBPaRs equipStorRs = EquipOrdinaryBAC.getInstance().getDataRsByKey(playerid, itemid);
			if(!equipStorRs.exist()){
				BACException.throwInstance("装备未找到");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_PUTON_EQUIP);
			DBPaRs equipRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_EQUIP_ORDINARY, equipStorRs.getInt("num"));
			int pos = equipRs.getInt("equiptype");
			String colName = "pos"+pos;
			//先保证指定空间有指定装备
			if(partnerid2 != 0){
				DBPaRs partnerStorRs2 = getDataRsByKey(playerid, partnerid2);
				if(!partnerStorRs2.exist()){
					BACException.throwInstance("穿戴伙伴未找到");
				}
				if(partnerStorRs2.getInt(colName) != itemid){
					BACException.throwInstance("穿戴伙伴未找到指定装备 partnerid="+partnerid2+" itemid="+itemid);
				}
				SqlString sqlStr = new SqlString();
				sqlStr.add(colName, 0);
				updateByKey(dbHelper, playerid, sqlStr, partnerid2);
				DBPaRs partnerRs2 = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs2.getInt("num"));
				gl.addRemark(GameLog.formatNameID(partnerRs2.getString("name"), partnerid2)+"卸下了"+GameLog.formatNameID(equipRs.getString("name"), itemid));
			} else {
				JSONArray itemarr = ItemBAC.getInstance().moveToZone(dbHelper, playerid, itemid, ItemBAC.ZONE_BAG, ItemBAC.ZONE_USE, false, gl);
				gl.addItemChaNoteArr(itemarr);
			}
			//如果身上有已经穿上的装备，则脱下
			int use_itemid = partnerStorRs.getInt(colName);
			if(use_itemid != 0){
				JSONArray use_itemarr = ItemBAC.getInstance().moveToZone(dbHelper, playerid, use_itemid, ItemBAC.ZONE_USE, ItemBAC.ZONE_BAG, true, gl);
				gl.addItemChaNoteArr(use_itemarr);
			}
			SqlString sqlStr = new SqlString();
			sqlStr.add(colName, itemid);
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			gl.addRemark(GameLog.formatNameID(partnerRs.getString("name"), partnerid)+"穿上了"+GameLog.formatNameID(equipRs.getString("name"), itemid));
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 一键换装
	 */
	public ReturnValue shortcutPutonEquip(int playerid, int partnerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(CBBAC.cbmgr.checkPartnerInWar(partnerid)){
				BACException.throwInstance("伙伴正在国战中，无法更换装备");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_SHORTCUT_PUTON_EQUIP);
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			int[] fetterarr = Tools.splitStrToIntArr(partnerRs.getString("fetternum"), ",");
			JSONArray fetterEquipArr = new JSONArray();//可产生羁绊的装备
			for(int i = 0; i < fetterarr.length; i++){
				DBPaRs fetterRs = DBPool.getInst().pQueryA(tab_partner_fetter, "num="+fetterarr[i]);
				int[][] funcarr = Tools.splitStrToIntArr2(fetterRs.getString("func"), "|", ",");
				for(int k = 0; k < funcarr.length; k++){
					if(funcarr[k][0] == 2){
						fetterEquipArr.add(funcarr[k][1]);
					}
				}
			}
			JSONArray useingarr = new JSONArray();//已装备到其他伙伴身上的装备
			DBPsRs itemStorRs = ItemBAC.getInstance().query(playerid, "playerid="+playerid+" and itemtype="+ItemBAC.TYPE_EQUIP_ORDINARY+" and zone="+ItemBAC.ZONE_USE);
			while(itemStorRs.next()){
				useingarr.add(itemStorRs.getInt("id"));
			}
			for(int i = 1; i <= 6; i++){
				int curr_itemid = partnerStorRs.getInt("pos"+i);
				if(curr_itemid != 0){
					useingarr.remove(useingarr.indexOf(curr_itemid));
				}
			}
			DBPsRs equipStorRs = EquipOrdinaryBAC.getInstance().query(playerid, "playerid="+playerid);
			SPEquip[] spequip = new SPEquip[equipStorRs.count()];
			while(equipStorRs.next()){
				if(!useingarr.contains(equipStorRs.getInt("itemid"))){
					DBPaRs equipRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_EQUIP_ORDINARY, equipStorRs.getInt("num"));
					spequip[equipStorRs.getRow()-1] = new SPEquip(equipStorRs.getInt("itemid"), equipRs.getInt("equiptype"), fetterEquipArr.contains(equipStorRs.getInt("num"))?1:0, equipRs.getInt("rare"), getBattlePower(partnerRs.getInt("battletype"), getEquipBattleData(equipStorRs)));		
				}
			}
			Tools.sort(spequip, 1);
			JSONArray itemarr = new JSONArray();
			SqlString sqlStr = new SqlString();
			for(int i = 1; i <= 6; i++){
				int curr_itemid = partnerStorRs.getInt("pos"+i);
				for(int k = 0; k < spequip.length; k++){
					if(spequip[k] != null && spequip[k].equiptype == i){//部位匹配
						if(spequip[k].itemid != curr_itemid){//比当前穿的好
							if(curr_itemid != 0){
								JSONArray arr1 = ItemBAC.getInstance().moveToZone(dbHelper, playerid, curr_itemid, ItemBAC.ZONE_USE, ItemBAC.ZONE_BAG, true, gl);
								MyTools.combJsonarr(itemarr, arr1);
							}
							JSONArray arr2 = ItemBAC.getInstance().moveToZone(dbHelper, playerid, spequip[k].itemid, ItemBAC.ZONE_BAG, ItemBAC.ZONE_USE, true, gl);
							MyTools.combJsonarr(itemarr, arr2);
							sqlStr.add("pos"+i, spequip[k].itemid);
						}
						break;//只找最好的
					}
				}
			}
			if(sqlStr.getColCount() <= 0){
				BACException.throwInstance("没有可更换的装备");
			}
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			gl.addItemChaNoteArr(itemarr);
			gl.save();
			return new ReturnValue(true, itemarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 一键换装排序装备
	 */
	class SPEquip implements Sortable {
		public int itemid;
		public int equiptype;
		public int fetter;
		public int rare;
		public int battlepower;
		
		public SPEquip(int itemid, int equiptype, int fetter, int rare, int battlepower) {
			this.itemid = itemid;
			this.equiptype = equiptype;
			this.fetter = fetter;
			this.rare = rare;
			this.battlepower = battlepower;
		}
		
		public double getSortValue() {
			return fetter*10000000000L+rare*1000000000L+battlepower;
		}
	}
	
	/**
	 * 脱装备
	 */
	public ReturnValue putoffEquip(int playerid, int partnerid, int pos){
		DBHelper dbHelper = new DBHelper();
		try {
			if(pos<1 || pos>6){
				BACException.throwInstance("装备部位错误 pos="+pos);
			}
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			if(CBBAC.cbmgr.checkPartnerInWar(partnerid)){
				BACException.throwInstance("伙伴正在国战中，无法更换装备");
			}
			String colName = "pos"+pos;
			int itemid = partnerStorRs.getInt(colName);
			if(itemid == 0){
				BACException.throwInstance("此部位上没有穿装备");
			}
			DBPaRs equipStorRs = EquipOrdinaryBAC.getInstance().getDataRsByKey(playerid, itemid);
			if(!equipStorRs.exist()){
				BACException.throwInstance("装备未找到");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_PUTOFF_EQUIP);
			JSONArray itemarr = ItemBAC.getInstance().moveToZone(dbHelper, playerid, itemid, ItemBAC.ZONE_USE, ItemBAC.ZONE_BAG, true, gl);
			gl.addItemChaNoteArr(itemarr);
			SqlString sqlStr = new SqlString();
			sqlStr.add(colName, 0);
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			DBPaRs equipRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_EQUIP_ORDINARY, equipStorRs.getInt("num"));
			gl.addRemark(GameLog.formatNameID(partnerRs.getString("name"), partnerid)+"脱下了"+GameLog.formatNameID(equipRs.getString("name"), itemid));
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 穿灵珠
	 */
	public ReturnValue putonOrb(int playerid, int partnerid, int pos){
		DBHelper dbHelper = new DBHelper();
		try {
			if(pos<1 || pos>6){
				BACException.throwInstance("装备部位错误 pos="+pos);
			}
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			String colName = "orb"+pos;
			if(partnerStorRs.getInt(colName) != 0){
				BACException.throwInstance("已经装备了灵珠");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_PUTON_ORB);
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			int[] upphasenumArr = Tools.splitStrToIntArr(partnerRs.getString("upphasenum"), ",");
			DBPaRs partnerUpphaseRs = DBPool.getInst().pQueryA(tab_partner_upphase, "num="+upphasenumArr[partnerStorRs.getInt("phase")-1]);//伙伴阶段初始值为1
			int orbnum = partnerUpphaseRs.getInt("pos"+pos);
			DBPaRs orbRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_ORB, orbnum);
			if(partnerStorRs.getInt("lv") < orbRs.getInt("putonlv")){
				BACException.throwInstance("伙伴等级未达到穿戴等级");
			}
			JSONArray itemarr = ItemBAC.getInstance().remove(dbHelper, playerid, ItemBAC.TYPE_ORB, orbnum, 1, ItemBAC.ZONE_BAG, gl);
			SqlString sqlStr = new SqlString();
			sqlStr.add(colName, orbnum);
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			gl.addItemChaNoteArr(itemarr);
			gl.addRemark("穿戴者："+GameLog.formatNameID(partnerRs.getString("name"), partnerid));
			gl.save();
			return new ReturnValue(true, itemarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 一键穿灵珠
	 */
	public ReturnValue shortcutPutonOrb(int playerid, int partnerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			int[] upphasenumArr = Tools.splitStrToIntArr(partnerRs.getString("upphasenum"), ",");
			DBPaRs partnerUpphaseRs = DBPool.getInst().pQueryA(tab_partner_upphase, "num="+upphasenumArr[partnerStorRs.getInt("phase")-1]);//伙伴阶段初始值为1
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_SHORTCUT_PUTON_ORB);
			DBPsRs itemStorRs = ItemBAC.getInstance().query(playerid, "playerid="+playerid);
			JSONObject orbobj = new JSONObject();//灵珠 编号-数量
			JSONObject orbdebris = new JSONObject();//灵珠碎片 编号-数量
			while(itemStorRs.next()){
				if(itemStorRs.getInt("itemtype") == ItemBAC.TYPE_ORB){
					orbobj.put(itemStorRs.getString("itemnum"), itemStorRs.getInt("itemamount"));
				} else 
				if(itemStorRs.getInt("itemtype") == ItemBAC.TYPE_ORB_DEBRIS){
					orbdebris.put(itemStorRs.getString("itemnum"), itemStorRs.getInt("itemamount"));
				}
			}
			JSONArray posarr = new JSONArray();//从未镶嵌到已镶嵌的部位
			JSONArray itemarr = new JSONArray();//物品变化
			SqlString sqlStr = new SqlString();
			for(int i = 1; i <= 6; i++){
				String column = "orb"+i;
				if(partnerStorRs.getInt(column) != 0){//已镶嵌灵珠
					continue;
				}
				int orbnum = partnerUpphaseRs.getInt("pos"+i);//需要镶嵌的灵珠编号
				DBPaRs orbRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_ORB, orbnum);
				if(partnerStorRs.getInt("lv") < orbRs.getInt("putonlv")){//检查是否满足等级条件
					continue;
				}
				if(orbobj.optInt(String.valueOf(orbnum)) > 0){//有成品
					JSONArray arr = ItemBAC.getInstance().remove(dbHelper, playerid, ItemBAC.TYPE_ORB, orbnum, 1, ItemBAC.ZONE_BAG, gl);
					MyTools.combJsonarr(itemarr, arr);
					orbobj.put(String.valueOf(orbnum), orbobj.optInt(String.valueOf(orbnum))-1);
				} else {//合成
					JSONObject new_orbobj = new JSONObject(orbobj.toString());
					JSONObject new_orbdebris = new JSONObject(orbdebris.toString());
					JSONObject remove_orbobj = new JSONObject();
					JSONObject remove_orbdebris = new JSONObject();
					if(!checkOrbShortcutComp(orbnum, 1, new_orbobj, new_orbdebris, remove_orbobj, remove_orbdebris)){//无成品灵珠，尝试快捷合成
						continue;
					}
					//System.out.println("remove_orbobj:"+remove_orbobj);
					//System.out.println("remove_orbdebris:"+remove_orbdebris);
					@SuppressWarnings("unchecked")
					Iterator<String> iterator_1 = remove_orbobj.keys();
					while(iterator_1.hasNext()){//根据移除集合逐个移除灵珠
						String num = iterator_1.next();
						//System.out.println(Tools.str2int(num)+"---------"+remove_orbobj.optInt(num));
						JSONArray arr = ItemBAC.getInstance().remove(dbHelper, playerid, ItemBAC.TYPE_ORB, Tools.str2int(num), remove_orbobj.optInt(num), ItemBAC.ZONE_BAG, gl);
						MyTools.combJsonarr(itemarr, arr);
					}
					@SuppressWarnings("unchecked")
					Iterator<String> iterator_2 = remove_orbdebris.keys();
					while(iterator_2.hasNext()){//根据移除集合逐个移除灵珠碎片
						String num = iterator_2.next();
						//System.out.println(Tools.str2int(num)+"+++++++++"+remove_orbdebris.optInt(num));
						JSONArray arr = ItemBAC.getInstance().remove(dbHelper, playerid, ItemBAC.TYPE_ORB_DEBRIS, Tools.str2int(num), remove_orbdebris.optInt(num), ItemBAC.ZONE_BAG, gl);
						MyTools.combJsonarr(itemarr, arr);
					}
					orbobj = new_orbobj;
					orbdebris = new_orbdebris;
				}
				posarr.add(i);//记录成功镶嵌灵珠的部位
				sqlStr.add(column, orbnum);//SQL记录要更新的字段
			}
			if(sqlStr.getColCount() <= 0){//无变化则返回提示
				BACException.throwInstance("没有可穿戴的灵珠");
			}
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			JSONArray returnarr = new JSONArray();
			returnarr.add(posarr);//穿上了灵珠的部位
			returnarr.add(itemarr);//物品变化
			
			gl.addItemChaNoteArr(itemarr);
			gl.addRemark("穿戴者："+GameLog.formatNameID(partnerRs.getString("name"), partnerid));
			gl.save();
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 灵珠合成检查
	 * @param orbnum 要合成的灵珠编号
	 * @param needamount 需要的数量
	 * @param orbobj 现有灵珠集合
	 * @param orbdebris 现有灵珠碎片集合
	 * @param remove_orbobj 将减少的灵珠集合
	 * @param remove_orbdebris 将减少的灵珠碎片集合
	 * @return 是否可合成
	 */
	public boolean checkOrbShortcutComp(int orbnum, int needamount, JSONObject orbobj, JSONObject orbdebris, JSONObject remove_orbobj, JSONObject remove_orbdebris) throws Exception {
		DBPaRs orbRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_ORB, orbnum);
		if(orbRs.getString("need").equals("0")){//无法合成
			return false;
		}
		int[][] needarr = Tools.splitStrToIntArr2(orbRs.getString("need"), "|", ",");//合成此灵珠所需要的材料
		for(int k = 0; k < needarr.length; k++){
			if(needarr[k][0] == ItemBAC.TYPE_ORB){//需要消耗灵珠
				int amount = orbobj.optInt(String.valueOf(needarr[k][1]));//拥有的数量
				if(amount < needarr[k][2]*needamount //拥有的数量小于所需数量 && 剩余所需数量无法通过合成获得
						&& !checkOrbShortcutComp(needarr[k][1], needarr[k][2]*needamount-amount, orbobj, orbdebris, remove_orbobj, remove_orbdebris) 
						){
					return false;
				}
				if(amount > 0){//如果当前拥有所需灵珠
					if(amount >= needarr[k][2]*needamount){//完全满足数量
						orbobj.put(String.valueOf(needarr[k][1]), amount-needarr[k][2]*needamount);//记录减少指定灵珠数量
						remove_orbobj.put(String.valueOf(needarr[k][1]), remove_orbobj.optInt(String.valueOf(needarr[k][1]))+needarr[k][2]*needamount);//记录将移除的灵珠数量
					} else {//不完全满足
						orbobj.put(String.valueOf(needarr[k][1]), 0);//记录清零指定灵珠数量
						remove_orbobj.put(String.valueOf(needarr[k][1]), remove_orbobj.optInt(String.valueOf(needarr[k][1]))+amount);//记录将移除的灵珠数量
					}
				}
			} else {//需要消耗灵珠碎片
				int amount = orbdebris.optInt(String.valueOf(needarr[k][1]));//所需数量
				if(amount < needarr[k][2]*needamount){//数量不足
					return false;
				}
				orbdebris.put(String.valueOf(needarr[k][1]), amount-needarr[k][2]*needamount);//记录减少灵珠碎片数量
				remove_orbdebris.put(String.valueOf(needarr[k][1]), remove_orbdebris.optInt(String.valueOf(needarr[k][1]))+needarr[k][2]*needamount);//记录将移除的灵珠碎片数量
			}
		}
		return true;
	}
	
	/**
	 * 进阶
	 */
	public ReturnValue upPhase(int playerid, int partnerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			for(int i = 1; i <= 6; i++){
				if(partnerStorRs.getInt("orb"+i) == 0){
					BACException.throwInstance("灵珠未穿齐");
				}
			}
			int oldPhase = partnerStorRs.getInt("phase");
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			int[] upphasenumArr = Tools.splitStrToIntArr(partnerRs.getString("upphasenum"), ",");
			if(oldPhase > upphasenumArr.length){
				BACException.throwInstance("已升到顶阶，无法再升阶");
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("phase", 1);
			for(int i = 1; i <= 6; i++){
				sqlStr.add("orb"+i, 0);
			}
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			GameLog.getInst(playerid, GameServlet.ACT_PARTNER_UPPHASE)
			.addChaNote(GameLog.formatNameID(partnerRs.getString("name"), partnerid)+"阶级", oldPhase, 1)
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 兑换伙伴
	 */
	public ReturnValue exchange(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPsRs partnerStorRs = query(playerid, "playerid="+playerid+" and num="+num);
			if(partnerStorRs.have()){
				BACException.throwInstance("不能兑换已拥有的伙伴");
			}
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+num);
			DBPsRs upStarRs = DBPool.getInst().pQueryS(tab_partner_upstar, "star<="+partnerRs.getInt("initstar"));
			int removeamount = (int)upStarRs.sum("debris");
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_EXCHANGE);
			JSONArray itemarr = ItemBAC.getInstance().remove(dbHelper, playerid, ItemBAC.TYPE_SOUL_STONE, partnerRs.getInt("needsoulstone"), removeamount, ItemBAC.ZONE_BAG, gl);
			int partnerid = create(dbHelper, playerid, num, partnerRs.getInt("awaken")==0?1:0, 5, 1, partnerRs.getInt("initstar"), null, null, null);
			
			JSONArray returnarr = new JSONArray();
			returnarr.add(itemarr);
			returnarr.add(partnerid);
			
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			GamePushData.getInstance(3)
			.add(plaRs.getString("name"))
			.add(partnerRs.getString("name"))
			.sendToAllOL();
			
			gl.addItemChaNoteArr(itemarr);
			gl.addRemark("兑换伙伴："+partnerRs.getString("name"));
			gl.save();
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 升星
	 */
	public ReturnValue upStar(int playerid, int partnerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			int nextStar = partnerStorRs.getInt("star")+1;
			DBPaRs upstarRs = DBPool.getInst().pQueryA(tab_partner_upstar, "star="+nextStar);
			if(!upstarRs.exist()){
				BACException.throwInstance("已升到满星");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_UPSTAR);
			PlayerBAC.getInstance().useMoney(dbHelper, playerid, upstarRs.getInt("needmoney"), gl);
			int oldStar = partnerStorRs.getInt("star");
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			JSONArray itemarr = ItemBAC.getInstance().remove(dbHelper, playerid, ItemBAC.TYPE_SOUL_STONE, partnerRs.getInt("needsoulstone"), upstarRs.getInt("debris"), ItemBAC.ZONE_BAG, gl);
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("star", 1);
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			if(nextStar >= 3){
				DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
				GamePushData.getInstance(2)
				.add(plaRs.getString("name"))
				.add(partnerRs.getString("name"))
				.add(nextStar)
				.sendToAllOL();	
			}
			
			gl.addItemChaNoteArr(itemarr);
			gl.addChaNote(GameLog.formatNameID(partnerRs.getString("name"), partnerid)+"星级", oldStar, 1);
			gl.save();
			return new ReturnValue(true, itemarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 觉醒
	 */
	public ReturnValue awaken(int playerid, int partnerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			int awakennum = partnerRs.getInt("awaken");
			if(awakennum == 0){
				BACException.throwInstance("无法觉醒");
			}
			if(partnerStorRs.getInt("phase") < PartnerAwakenData.awaken_needphase){
				BACException.throwInstance("不满足阶级条件");
			}
			if(partnerStorRs.getInt("star") < PartnerAwakenData.awaken_needstar){
				BACException.throwInstance("不满足星级条件");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_AWAKEN);
			PlayerBAC.getInstance().useMoney(dbHelper, playerid, PartnerAwakenData.awaken_needmoney, gl);
			SqlString sqlStr = new SqlString();
			sqlStr.add("num", awakennum);
			sqlStr.add("awaken", 1);
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			gl.addRemark(GameLog.formatNameID(partnerRs.getString("name"), partnerid)+"觉醒");
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 升级技能
	 */
	public ReturnValue upskilllv(int playerid, int partnerid, int pos, int upamount){
		DBHelper dbHelper = new DBHelper();
		try {
			if(pos<1 || pos>6){
				BACException.throwInstance("升级技能错误 pos="+pos);
			}
			if(upamount < 1){
				BACException.throwInstance("升级技能错误 upamount="+upamount);
			}
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			if(pos != 1){
				int[] bskilldata = Tools.splitStrToIntArr2(partnerRs.getString("bskill"), "|", ",")[pos-2];
				if(bskilldata[1] == 0){
					BACException.throwInstance("觉醒后开放");
				}
				if(partnerStorRs.getInt("phase") < bskilldata[1]){
					BACException.throwInstance("不满足品质条件");
				}
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_UPSKILLLV);
			String colName = "skilllv"+pos;
			int oldSkillLv = partnerStorRs.getInt(colName);
			DBPaRs partnerSkillUplvRs = DBPool.getInst().pQueryA(tab_partner_skill_uplv, "skilllv="+(oldSkillLv+upamount));
			if(!partnerSkillUplvRs.exist() || partnerSkillUplvRs.getString("skill"+pos).equals("0")){
				BACException.throwInstance("无法升到指定等级 num="+partnerStorRs.getInt("num")+" tgrlv="+(oldSkillLv+upamount));
			}
			int[] uplvdata = Tools.splitStrToIntArr(partnerSkillUplvRs.getString("skill"+pos), ",");
			if(partnerStorRs.getInt("lv") < uplvdata[0]){
				BACException.throwInstance("伙伴等级不足");
			}
			DBPsRs partnerSkillupLvRs2 = DBPool.getInst().pQueryS(tab_partner_skill_uplv, "skilllv>="+(oldSkillLv+1)+" and skilllv<="+(oldSkillLv+upamount));
			int needMoney = 0;
			while(partnerSkillupLvRs2.next()){
				needMoney += Tools.splitStrToIntArr(partnerSkillupLvRs2.getString("skill"+pos), ",")[1];
			}
			PlayerBAC.getInstance().useMoney(dbHelper, playerid, needMoney, gl);
			
			SqlString sqlStr = new SqlString();
			sqlStr.addChange(colName, upamount);
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_PARTNER_SKILL_UP, upamount, gl);
			
			gl.addChaNote(GameLog.formatNameID(partnerRs.getString("name"), partnerid)+"技能"+pos, oldSkillLv, upamount);
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 一键强化
	 */
	public ReturnValue shortcutStreEquip(int playerid, int partnerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			int plaLv = plaRs.getInt("lv");
			int money = plaRs.getInt("money");
			int usemoney = 0;
			int sumStreLv = 0;//累计强化等级
			JSONArray strearr = new JSONArray();
			for(int i = 1; i <= 6; i++){
				int itemid = partnerStorRs.getInt("pos"+i);
				if(itemid != 0){
					DBPaRs equipStorRs = EquipOrdinaryBAC.getInstance().getDataRsByKey(playerid, itemid);
					int strelv = equipStorRs.getInt("strelv");
					int addStreLv = 0;
					DBPsRs streRs = DBPool.getInst().pQueryS(EquipOrdinaryBAC.tab_equip_stre, "strelv>"+strelv+" and strelv<="+plaLv, "strelv");
					DBPaRs equipRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_EQUIP_ORDINARY, equipStorRs.getInt("num"));
					boolean haveStre = false;
					while(streRs.next()){
						int needmoney = streRs.getInt("q"+equipRs.getInt("rare"));
						if(money < needmoney){
							break;
						}
						money -= needmoney;
						usemoney += needmoney;
						addStreLv++;
						sumStreLv++;
						haveStre = true;
					}
					if(haveStre){
						JSONArray arr = new JSONArray();
						arr.add(itemid);
						arr.add(strelv);
						arr.add(addStreLv);
						arr.add(equipRs.getString("name"));
						strearr.add(arr);
					}
				}
			}
			if(usemoney == 0){
				BACException.throwInstance("不需要强化");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PARTNER_SHORTCUT_STRE_EQUIP);
			PlayerBAC.getInstance().useMoney(dbHelper, playerid, usemoney, gl);
			JSONArray returnarr = new JSONArray();
			for(int i = 0; i < strearr.length(); i++){
				JSONArray arr = strearr.optJSONArray(i);
				SqlString equipSqlStr = new SqlString();
				equipSqlStr.addChange("strelv", arr.optInt(2));
				EquipOrdinaryBAC.getInstance().updateByKey(dbHelper, playerid, equipSqlStr, arr.optInt(0));
				gl.addChaNote(GameLog.formatNameID(arr.optString(3), arr.optInt(0))+" 强化等级", arr.optInt(1), arr.optInt(2));
				returnarr.add(new int[]{arr.optInt(0), arr.optInt(1)+arr.optInt(2)});
			}
			PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_EQUIP_STRENGTHEN, sumStreLv, gl);
			
			gl.save();
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试加伙伴
	 */
	public ReturnValue debugAdd(int playerid, int num, int phase, int star){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+num);
			if(!partnerRs.exist()){
				BACException.throwInstance("伙伴编号不存在 num="+num);
			}
			DBPsRs partnerStorRs = query(playerid, "playerid="+playerid+" and num="+num);
			if(partnerStorRs.have()){
				BACException.throwInstance("不能创建已拥有的伙伴");
			}
			create(dbHelper, playerid, num, partnerRs.getInt("awaken")==0?1:0, 1, phase, star, null, null, null);
			
			GameLog.getInst(playerid, GameServlet.ACT_DEBUG_GAME_LOG)
			.addRemark("调试增加伙伴 "+partnerRs.getString("name"))
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试删伙伴
	 */
	public ReturnValue debugDelete(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+num);
			if(!partnerRs.exist()){
				BACException.throwInstance("伙伴编号不存在 num="+num);
			}
			DBPsRs partnerStorRs = query(playerid, "playerid="+playerid+" and num="+num);
			if(!partnerStorRs.have()){
				BACException.throwInstance("未拥有此伙伴");
			}
			while(partnerStorRs.next()){
				for(int i = 1; i <= 6; i++){
					int itemid = partnerStorRs.getInt("pos"+i);
					if(itemid != 0){
						putoffEquip(playerid, partnerStorRs.getInt("id"), i);
					}
				}
			}
			delete(dbHelper, playerid, "playerid="+playerid+" and num="+num);
			
			GameLog.getInst(playerid, GameServlet.ACT_DEBUG_GAME_LOG)
			.addRemark("调试删除伙伴 "+partnerRs.getString("name"))
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试加阶级
	 */
	public ReturnValue debugAddPhase(int playerid, int partnerid, int add){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("phase", add);
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			GameLog.getInst(playerid, GameServlet.ACT_DEBUG_GAME_LOG)
			.addRemark("调试增加伙伴阶级 "+partnerRs.getString("name"))
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试加星级
	 */
	public ReturnValue debugAddStar(int playerid, int partnerid, int add){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
			if(!partnerStorRs.exist()){
				BACException.throwInstance("伙伴未找到");
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("star", add);
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
			GameLog.getInst(playerid, GameServlet.ACT_DEBUG_GAME_LOG)
			.addRemark("调试增加伙伴星级 "+partnerRs.getString("name"))
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试加经验
	 */
	public ReturnValue debugAddExp(int playerid, int partnerid, int add){
		DBHelper dbHelper = new DBHelper();
		try {
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_DEBUG_GAME_LOG);
			addExp(dbHelper, playerid, partnerid, add, gl);
			
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取伙伴战斗属性箱
	 */
	public ReturnValue bkGetSpriteBox(int playerid){
		try {
			ArrayList<SpriteBox> sprites = getSpriteBoxList(playerid, null, null);
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < sprites.size(); i++){
				SpriteBox spritebox = sprites.get(i);
				sb.append(spritebox.getIngredientStr());
				sb.append("\r\n");
				sb.append("能力战力："+(getSpriteBoxBattlePower(spritebox)-spritebox.skillAddBattlerPower)+"\r\n");
				sb.append("技能战力："+spritebox.skillAddBattlerPower+"\r\n");
				sb.append("总战力："+getSpriteBoxBattlePower(spritebox)+"\r\n");
			}
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 调试服务端战斗
	 * @param playerid
	 * @param posarr1 结构：JSONARRAY[1号位伙伴ID，2号位伙伴ID，3号位伙伴ID，4号位伙伴ID，5号位伙伴ID，6号位伙伴ID]
	 * @param oppid 对手角色ID
	 * @param posarr2 对手伙伴站位
	 * @return
	 */
	public ReturnValue debugServerBattle(int playerid, String posarr1, int oppid, String posarr2){
		try {
			TeamBox teambox1 = getTeamBox(playerid, 0, new JSONArray(posarr1));
			TeamBox teambox2 = getTeamBox(oppid, 1, new JSONArray(posarr2));
			BattleBox battlebox = new BattleBox();
			battlebox.teamArr[0].add(teambox1);
			battlebox.teamArr[1].add(teambox2);
			BattleManager.createPVPBattle(battlebox);
			return new ReturnValue(true, battlebox.replayData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 调试获取BATTLEBOX
	 */
	public ReturnValue debugGetBattleBox(int playerid, String posarr1, int oppid, String posarr2){
		try {
			TeamBox teambox1 = getTeamBox(playerid, 0, new JSONArray(posarr1));
			TeamBox teambox2 = getTeamBox(oppid, 1, new JSONArray(posarr2));
			BattleBox battlebox = new BattleBox();
			battlebox.teamArr[0].add(teambox1);
			battlebox.teamArr[1].add(teambox2);
			Player pla = SocketServer.getInstance().plamap.get(playerid);
			pla.verifybattle_battlebox = battlebox;
			return new ReturnValue(true, battlebox.getJSONArray().toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 调试验证战斗
	 */
	public ReturnValue debugServerVerify(int playerid, String battleRecord){
		try {
			Player pla = SocketServer.getInstance().plamap.get(playerid);
			BattleManager.verifyPVEBattle(pla.verifybattle_battlebox, battleRecord);
			pla.verifybattle_battlebox = null;
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 获取登录数据
	 */
	public JSONArray getLoginData(int playerid) throws Exception {
		DBPsRs partnerStorRs = query(playerid, "playerid="+playerid);
		JSONArray returnarr = new JSONArray();
		while(partnerStorRs.next()){
			JSONArray arr = new JSONArray();
			arr.add(partnerStorRs.getInt("id"));//伙伴ID
			arr.add(partnerStorRs.getInt("num"));//编号
			arr.add(partnerStorRs.getInt("lv"));//等级
			arr.add(partnerStorRs.getInt("exp"));//经验
			arr.add(partnerStorRs.getInt("phase"));//阶级
			arr.add(partnerStorRs.getInt("star"));//星级
			for(int i = 1; i <= 6; i++){
				arr.add(partnerStorRs.getInt("pos"+i));//装备部位	
			}
			for(int i = 1; i <= 6; i++){
				arr.add(partnerStorRs.getInt("orb"+i));//灵珠部位
			}
			for(int i = 1; i <= 5; i++){
				arr.add(partnerStorRs.getInt("skilllv"+i));	//技能等级
			}
			returnarr.add(arr);
		}
		return returnarr;
	}
	
	/**
	 * 加经验
	 */
	public JSONArray addExp(DBHelper dbHelper, int playerid, int partnerid, long addexp, GameLog gl) throws Exception {
		DBPaRs partnerStorRs = getDataRsByKey(playerid, partnerid);
		if(!partnerStorRs.exist()){
			BACException.throwInstance("伙伴未找到");
		}
		DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+partnerStorRs.getInt("num"));
		int lv = partnerStorRs.getInt("lv");
		int maxpartnerlv = PlayerBAC.getInstance().getMaxPartnerLv(playerid);
		JSONArray returnarr = ExpBAC.getInstance().addExp(tab_partner_uplv, lv, partnerStorRs.getInt("exp"), addexp, maxpartnerlv, GameLog.formatNameID(partnerRs.getString("name"), partnerid), gl);
		if(returnarr != null){
			SqlString sqlStr = new SqlString();
			sqlStr.add("lv", returnarr.optInt(0));
			sqlStr.add("exp", returnarr.optInt(1));
			updateByKey(dbHelper, playerid, sqlStr, partnerid);
			if(returnarr.optInt(0) > lv){
				PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_PARTNER_LV_UP, gl);
			}
		}
		return returnarr;
	}
	
	/**
	 * 队伍数据检查
	 */
	public JSONArray checkPosarr(int playerid, JSONArray posarr, int minlevel, int minamount) throws Exception {
		if(posarr.length() != 6){
			BACException.throwInstance("数据长度不等于6");
		}
		JSONObject posobj = new JSONObject();
		DBPsRs partnerStorRs = query(playerid, "playerid="+playerid);
		while(partnerStorRs.next()){
			posobj.put(partnerStorRs.getString("id"), partnerStorRs.getInt("lv"));
		}
		JSONArray posarr2 = new JSONArray();
		for(int i = 0; i < posarr.length(); i++){
			int partnerid = posarr.getInt(i);
			if(partnerid != 0){
				if(posarr2.contains(partnerid)){
					BACException.throwInstance("有重复的伙伴 "+partnerid);
				}
				if(!posobj.has(String.valueOf(partnerid))){
					BACException.throwInstance("伙伴不存在 "+partnerid);
				}
				if(posobj.optInt(String.valueOf(partnerid)) < minlevel){
					BACException.throwInstance("不满足等级要求 "+partnerid);
				}
				posarr2.add(partnerid);
			}
		}
		if(posarr2.length() < minamount){
			BACException.throwInstance("伙伴数不不足"+minamount+"个");
		}
		return posarr2;
	}
	
	/**
	 * 更新伙伴战力
	 */
	public void updateBattlePower(DBHelper dbHelper, int playerid) throws Exception {
		ArrayList<SpriteBox> sprites = getSpriteBoxList(playerid, null, null);
		DBPsRs partnerStorRs = query(playerid, "playerid="+playerid);
		JSONObject powerobj = new JSONObject();
		while(partnerStorRs.next()){
			powerobj.put(partnerStorRs.getString("id"), partnerStorRs.getInt("battlepower"));
		}
		//伙伴战力
		int totalbattlepower = 0;
		for(int i = 0; i < sprites.size(); i++){
			SpriteBox spritebox = sprites.get(i);
			int power = getSpriteBoxBattlePower(spritebox);
			if(power != powerobj.optInt(String.valueOf(spritebox.partnerId))){
				SqlString sqlStr = new SqlString();
				sqlStr.add("battlepower", power);
				updateByKey(dbHelper, playerid, sqlStr, spritebox.partnerId);
				powerobj.put(String.valueOf(spritebox.partnerId), power);
				//System.out.println("--------PartnerBAC----------update------------------");
			}
			totalbattlepower += power;
		}
		//总战力
		DBPaRs plaroleRs = PlaRoleBAC.getInstance().getDataRs(playerid);
		if(totalbattlepower != plaroleRs.getInt("totalbattlepower")){
			SqlString sqlStr = new SqlString();
			sqlStr.add("totalbattlepower", totalbattlepower);
			PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
		}
		//竞技场防守阵容战力
		DBPaRs plajjcrankingRs = PlaJJCRankingBAC.getInstance().getDataRs(playerid);
		if(plajjcrankingRs.exist()){
			int power = 0;
			JSONArray posarr = new JSONArray(plajjcrankingRs.getString("defformation"));
			for(int i = 0; i < posarr.length(); i++){
				if(posarr.optInt(i) != 0){
					power += powerobj.optInt(posarr.optString(i));
				}
			}
			SqlString sqlStr = new SqlString();
			if(power != plajjcrankingRs.getInt("battlepower")){
				sqlStr.add("battlepower", power);
				//System.out.println("--------PlaJJCRankingBAC----------update------------------");
			}
			if(plajjcrankingRs.getString("wkdefform") != null){
				int wkpower = 0;
				JSONArray wkposarr = new JSONArray(plajjcrankingRs.getString("wkdefform"));
				for(int i = 0; i < posarr.length(); i++){
					if(wkposarr.optInt(i) != 0){
						wkpower += powerobj.optInt(wkposarr.optString(i));
					}
				}
				if(wkpower != plajjcrankingRs.getInt("wkbattlepower")){
					sqlStr.add("wkbattlepower", wkpower);
					//System.out.println("--------wkbattlepower----------update------------------");
				}
			}
			if(sqlStr.getColCount() > 0){
				PlaJJCRankingBAC.getInstance().update(dbHelper, playerid, sqlStr);
			}
		}
		//国战队伍池战力
		DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
		int factoinid = plafacRs.getInt("factionid");
		if(factoinid != 0){
			DBPsRs teamRs = CBTeamPoolBAC.getInstance().query(factoinid, "factionid="+factoinid+" and playerid="+playerid);
			while(teamRs.next()){
				JSONArray posarr = new JSONArray(teamRs.getString("teamdata"));
				int power = 0;
				for(int i = 0; i < posarr.length(); i++){
					if(posarr.optInt(i) != 0){
						power += powerobj.optInt(posarr.optString(i));
					}
				}
				if(power != teamRs.getInt("battlepower")){
					SqlString sqlStr = new SqlString();
					sqlStr.add("battlepower", power);
					CBTeamPoolBAC.getInstance().update(dbHelper, factoinid, sqlStr, "factionid="+factoinid+" and id="+teamRs.getInt("id"));
					//System.out.println("--------CBTeamPoolBAC----------update------------------");
				}
			}
		}
		//太守战力
		DBPsRs cityStorRs = CBBAC.getInstance().query(Conf.sid, "serverid="+Conf.sid+" and leaderid="+playerid);
		while(cityStorRs.next()){
			JSONArray posarr = new JSONArray(cityStorRs.getString("leaderposarr"));
			int power = 0;
			for(int i = 0; i < posarr.length(); i++){
				if(posarr.optInt(i) != 0){
					power += powerobj.optInt(posarr.optString(i));
				}
			}
			if(power != cityStorRs.getInt("leaderbattlepower")){
				SqlString sqlStr = new SqlString();
				sqlStr.add("leaderbattlepower", power);
				CBBAC.getInstance().update(dbHelper, Conf.sid, sqlStr, "serverid="+Conf.sid+" and id="+cityStorRs.getInt("id"));
				//System.out.println("--------CBBAC----------update------------------");
			}
		}
	}
	
	/**
	 * 根据阵型数据返回伙伴基础数据
	 */
	public JSONArray getPartnerDataByPosarr(int playerid, JSONArray posarr) throws Exception {
		JSONObject posobj = new JSONObject();
		DBPsRs partnerStorRs = query(playerid, "playerid="+playerid);
		while(partnerStorRs.next()){
			JSONArray arr = new JSONArray();
			arr.add(partnerStorRs.getInt("id"));
			arr.add(partnerStorRs.getInt("num"));
			arr.add(partnerStorRs.getInt("lv"));
			arr.add(partnerStorRs.getInt("phase"));
			arr.add(partnerStorRs.getInt("star"));
			posobj.put(partnerStorRs.getString("id"), arr);
		}
		JSONArray dataarr = new JSONArray();
		for(int i = 0; i < posarr.length(); i++){
			int partnerid = posarr.getInt(i);
			if(partnerid != 0){
				dataarr.put(i, posobj.optJSONArray(String.valueOf(partnerid)));
			}
		}
		return dataarr;
	}
	
	/**
	 * 获取队伍属性箱
	 */
	public TeamBox getTeamBox(int playerid, int teamType, JSONArray posarr) throws Exception {
		return getTeamBox(playerid, teamType, posarr, null);
	}
	
	/**
	 * 获取SpriteBox集合
	 * @param posarr 传NULL则表示获取所有伙伴
	 * @param extendlistener 能力值扩展回调
	 */
	public ArrayList<SpriteBox> getSpriteBoxList(int playerid, JSONArray posarr, SpriteBoxExtendPropListener extendlistener) throws Exception {
		JSONArray allPosarr = null;
		if(posarr == null){//传空则表示获取所有伙伴的数据
			allPosarr = new JSONArray();
		}
		ArrayList<SpriteBox> sprites = new ArrayList<SpriteBox>();
		JSONObject fetter_partner = new JSONObject();//伙伴羁绊
		DBPsRs partnerStorRs = query(playerid, "playerid="+playerid);
		while(partnerStorRs.next()){
			if(allPosarr != null){
				allPosarr.add(partnerStorRs.getInt("id"));
			}
			fetter_partner.put(partnerStorRs.getString("num"), partnerStorRs.getInt("star"));
		}
		JSONObject fetter_equip = new JSONObject();//装备数据
		DBPsRs equipStorRs = EquipOrdinaryBAC.getInstance().query(playerid, "playerid="+playerid);
		while(equipStorRs.next()){
			fetter_equip.put(equipStorRs.getString("itemid"), new int[]{equipStorRs.getInt("num"), equipStorRs.getInt("strelv"), equipStorRs.getInt("starlv")});
		}
		double[][] artifactprop = ArtifactBAC.getInstance().getBattleProp(playerid);//神器数据
		if(allPosarr != null){
			posarr = allPosarr;
		}
		for(int i = 0; i < posarr.length(); i++){
			int partnerid = posarr.optInt(i);
			if(partnerid == 0){//此位置没有伙伴
				sprites.add(null);
				continue;
			}
			partnerStorRs.beforeFirst();
			while(partnerStorRs.next()){//查找伙伴
				if(partnerStorRs.getInt("id") == partnerid){
					break;
				}
				if(partnerStorRs.getRow() == partnerStorRs.count()){
					BACException.throwAndPrintInstance("数据错误，伙伴ID未找到 partnerid="+partnerid);
				}
			}
			int num = partnerStorRs.getInt("num");
			int star = partnerStorRs.getInt("star");
			int lv = partnerStorRs.getInt("lv");
			int phase = partnerStorRs.getInt("phase");
			int[] orbnumarr = new int[6];//灵珠数据
			for(int k = 1; k <= 6; k++){
				orbnumarr[k-1] = partnerStorRs.getInt("orb"+k);
			}
			int[][] equiparr = new int[6][3];//装备数据
			for(int k = 1; k <= 6; k++){
				int itemid = partnerStorRs.getInt("pos"+k);
				if(itemid != 0){
					int[] eqdata = (int[])fetter_equip.opt(String.valueOf(itemid));
					if(eqdata != null){
						equiparr[k-1] = eqdata;
					} else {
						System.out.println("===========ERROR：装备数据异常，partnerid="+partnerid+" itemid="+itemid);
					}
				}
			}
			int[] skilllvarr = new int[5];//技能数据
			for(int k = 1; k <= 5; k++){
				skilllvarr[k-1] = partnerStorRs.getInt("skilllv"+k);
			}
			SpriteBox spritebox = getSpriteBox(playerid, partnerid, num, star, lv, phase, orbnumarr, equiparr, skilllvarr, fetter_partner, artifactprop, extendlistener);
			sprites.add(spritebox);
		}
		return sprites;
	}
	
	/**
	 * 获取队伍属性箱
	 */
	public TeamBox getTeamBox(int playerid, int teamType, JSONArray posarr, SpriteBoxExtendPropListener extendlistener) throws Exception {
		ArrayList<SpriteBox> sprites = getSpriteBoxList(playerid, posarr, extendlistener);
		TeamBox teambox = new TeamBox();
		for(int i = 0; i < sprites.size(); i++){
			SpriteBox spritebox = sprites.get(i);
			if(spritebox != null){
				spritebox.teamType = (byte)teamType;
				spritebox.posNum = (byte)(i+1);
				teambox.sprites.add(spritebox);
			}
		}
		DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
		teambox.playerid = playerid;
		teambox.pname = plaRs.getString("name");
		teambox.pnum = plaRs.getInt("num");
		teambox.teamType = (byte)teamType;
		return teambox;
	}
	
	/**
	 * 获取队伍属性箱
	 */
	public TeamBox getTeamBox(int playerid, String pname, int pnum, int teamType, SpriteBox[][] spriteboxarr) throws Exception {
		TeamBox teambox = new TeamBox();
		for(int i = 0; i < spriteboxarr.length; i++){
			for(int j = 0; j < spriteboxarr[i].length; j++){
				if(spriteboxarr[i][j] != null){
					spriteboxarr[i][j].teamType = (byte)teamType;
					spriteboxarr[i][j].posNum = (byte)(i*3+j+1);
					teambox.sprites.add(spriteboxarr[i][j]);
				}
			}
		}
		teambox.playerid = playerid;
		teambox.pname = pname;
		teambox.pnum = pnum;
		teambox.teamType = (byte)teamType;
		return teambox;
	}
	
	/**
	 * 获取战斗精灵属性箱
	 */
	public SpriteBox getSpriteBox(int playerid, int partnerid, int num, int star, int lv, int phase, int[] orbnumarr, int[][] equiparr, int[] skilllvarr, JSONObject fetter_partner, double[][] artifactprop) throws Exception {
		return getSpriteBox(playerid, partnerid, num, star, lv, phase, orbnumarr, equiparr, skilllvarr, fetter_partner, artifactprop, null);
	}
	
	/**
	 * 获取战斗精灵属性箱
	 */
	public SpriteBox getSpriteBox(int playerid, int partnerid, int num, int star, int lv, int phase, int[] orbnumarr, int[][] equiparr, int[] skilllvarr, JSONObject fetter_partner, double[][] artifactprop, SpriteBoxExtendPropListener extendlistener) throws Exception {
		DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+num);
		SpriteBox spritebox = new SpriteBox();
		//基础属性
		spritebox.playerId = playerid;
		spritebox.partnerId = partnerid;
		spritebox.type = 1;
		spritebox.num = num;
		spritebox.level = (short)lv;
		spritebox.name = partnerRs.getString("name");
		spritebox.phase = (byte)phase;
		spritebox.star = (byte)star;
		spritebox.battletype = partnerRs.getByte("battletype");
		spritebox.sex = partnerRs.getByte("sex");
		/*
		 * 属性总组成 = (基础 + 成长&星级 + 灵珠 + 阶级)*羁绊百分比 + 被动技能 + 装备 + 神器 + 羁绊具体值
		 */
		//初始值
		for(int i = 0; i < 12; i++){
			if(i == Const.PROP_HP){
				continue;
			}
			spritebox.addProp(1, i, 1, partnerRs.getInt("init"+i));
		}
		spritebox.updateIngredientData("初始值");
		//成长&星级
		DBPaRs starRs = DBPool.getInst().pQueryA(tab_partner_upstar, "star="+star);
		double growvalue = starRs.getDouble("growvalue")/10000;
		for(int i = 0; i <= Const.PROP_MAGICDEF; i++){
			int addvalue = (int)(partnerRs.getDouble("grow"+i) * growvalue * (lv-1));
			spritebox.addProp(1, i , 1, addvalue);
		}
		spritebox.updateIngredientData("成长&星级");
		//灵珠
		if(phase > 1){
			int[] upphasenumArr = Tools.splitStrToIntArr(partnerRs.getString("upphasenum"), ",");
			for(int i = 0; i < phase-1; i++){
				DBPaRs upphaseRs = DBPool.getInst().pQueryA(tab_partner_upphase, "num="+upphasenumArr[i]);
				for(int p = 1; p <= 6; p++){
					DBPaRs orbRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_ORB, upphaseRs.getInt("pos"+p));
					spritebox.addProp(orbRs.getString("attr"));
				}
			}
		}
		for(int i = 0; i < orbnumarr.length; i++){
			if(orbnumarr[i] != 0){
				DBPaRs orbRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_ORB, orbnumarr[i]);
				spritebox.addProp(orbRs.getString("attr"));
			}
		}
		spritebox.updateIngredientData("灵珠");
		//阶级
		DBPsRs phaseattrRs = DBPool.getInst().pQueryS(tab_partner_phase_attr, "num<="+phase);
		while(phaseattrRs.next()){
			spritebox.addProp(phaseattrRs.getString("battletype"+partnerRs.getInt("battletype")));
		}
		spritebox.updateIngredientData("阶级");
		//存储用于计算百分比的属性
		spritebox.saveBattlePropSave();
		//装备
		JSONArray fetter_equiparr = new JSONArray();
		for(int i = 0; i < equiparr.length; i++){
			if(equiparr[i][0] != 0){
				int[] equipdata = getEquipBattleData(equiparr[i][0], equiparr[i][1], equiparr[i][2]);
				spritebox.addBattleProp(equipdata);
				fetter_equiparr.add(equiparr[i][0]);
			}
		}
		spritebox.updateIngredientData("装备");
		//神器
		spritebox.addProp(artifactprop);
		spritebox.updateIngredientData("神器");
		//技能
		spritebox.addSkill(partnerRs.getInt("skill"), skilllvarr[0]);
		int[][] bskilldata = Tools.splitStrToIntArr2(partnerRs.getString("bskill"), "|", ",");
		for(int i = 0; i < bskilldata.length; i++){
			if(bskilldata[i][1]!=0 && phase>=bskilldata[i][1]){
				spritebox.addSkill(bskilldata[i][0], skilllvarr[i+1]);
			}
		}
		//spritebox.updateIngredientData("");//这里仅单纯加技能，所以不用记录
		//羁绊
		int[] fetternumarr = Tools.splitStrToIntArr(partnerRs.getString("fetternum"), ",");
		for(int i = 0; i < fetternumarr.length; i++){
			DBPaRs fetterRs = DBPool.getInst().pQueryA(tab_partner_fetter, "num="+fetternumarr[i]);
			boolean match = true;
			int[][] func = Tools.splitStrToIntArr2(fetterRs.getString("func"), "|", ",");
			for(int k = 0; k < func.length; k++){
				if(func[k][0] == 1){
					match = fetter_partner.optInt(String.valueOf(func[k][1]))>=func[k][2];
				} else 
				if(func[k][0] == 2){
					match = fetter_equiparr.contains(func[k][1]);
				}
				if(!match){
					break;
				}
			}
			if(match){
				spritebox.addProp(fetterRs.getString("attr"));
			}
		}
		spritebox.updateIngredientData("羁绊");
		if(extendlistener != null){//自定义扩展属性
			extendlistener.extend(spritebox);
		}
		//转换
		spritebox.conver();
		return spritebox;
	}
	
	/**
	 * 检查是否拥有指定伙伴
	 */
	public boolean checkHave(int playerid, int partnernum) throws Exception {
		DBPsRs partnerStorRs = query(playerid, "playerid="+playerid+" and num="+partnernum);
		return partnerStorRs.have();
	}
	
	/**
	 * 获取指定伙伴的星级
	 */
	public int getStar(int playerid, int partnernum) throws Exception {
		DBPsRs partnerStorRs = query(playerid, "playerid="+playerid+" and num="+partnernum);
		if(partnerStorRs.next()){
			return partnerStorRs.getInt("star");
		} else {
			return 0;
		}
	}
	
	/**
	 * 获取指定星级的伙伴数量
	 */
	public int getAmountByStar(int playerid, int star) throws Exception {
		DBPsRs partnerStorRs = query(playerid, "playerid="+playerid+" and star>="+star);
		return partnerStorRs.count();
	}
	
	/**
	 * 获取评分
	 * @param posarr 阵型
	 * @param offlinenum 离线使用值
	 */
	public int getPlayerBattlePower(int playerid, JSONArray posarr, int offlinenum) throws Exception {
		if(SocketServer.getInstance().checkOnline(playerid)){
			TeamBox teambox = getTeamBox(playerid, 0, posarr);
			return getTeamBoxBattlePower(teambox);		
		} else {
			return offlinenum;
		}
	}
	
	/**
	 * 获取评分
	 */
	public int getTeamBoxBattlePower(TeamBox teambox) throws Exception {
		int totalGrade = 0;
		for(int i = 0; i < teambox.sprites.size(); i++){
			SpriteBox spritebox = teambox.sprites.get(i);
			totalGrade += getSpriteBoxBattlePower(spritebox);
		}
		return totalGrade;
	}
	
	/**
	 * 获取评分
	 */
	public int getSpriteBoxBattlePower(SpriteBox spritebox) throws Exception {
		int battlepower = getBattlePower(spritebox.battletype, spritebox.battle_prop);
		battlepower += spritebox.skillAddBattlerPower;
		return battlepower;
	}
	
	/**
	 * 获取战力
	 * @return
	 */
	public int getBattlePower(int battletype, int[] battledata) throws Exception {
		double battlepower = 0;
		DBPsRs battlepowerRs = DBPool.getInst().pQueryS(tab_battlepower);
		while(battlepowerRs.next()){
			battlepower += battlepowerRs.getDouble("battletype"+battletype)*battledata[battlepowerRs.getInt("num")];
		}
		return (int)battlepower;
	}
	
	/**
	 * 获取装备增加的战斗值
	 */
	public int[] getEquipBattleData(DBPRs equipStorRs) throws Exception {
		int num = equipStorRs.getInt("num");
		int strelv = equipStorRs.getInt("strelv");
		int starlv = equipStorRs.getInt("starlv");
		return getEquipBattleData(num, strelv, starlv);
	}
	
	/**
	 * 获取装备增加的战斗值
	 */
	public int[] getEquipBattleData(int num, int strelv, int starlv) throws Exception {
		int[] equipdata = new int[SpriteBox.BATTLE_PROP_LEN];
		DBPaRs equipRs = ItemBAC.getInstance().getListRs(ItemBAC.TYPE_EQUIP_ORDINARY, num);
		int[] attrtypearr = Tools.splitStrToIntArr(equipRs.getString("attrtype"), ",");
		int[] baseattrarr = Tools.splitStrToIntArr(equipRs.getString("baseattr"), ",");
		int[] stregrowarr = Tools.splitStrToIntArr(equipRs.getString("stregrow"), ",");
		double star_percent = 0;
		if(starlv > 0){
			DBPaRs upstarRs = DBPool.getInst().pQueryA(EquipOrdinaryBAC.tab_equip_upstar, "star="+starlv);
			star_percent = upstarRs.getDouble("add"+equipRs.getInt("rare"));
		}
		for(int k = 0; k < attrtypearr.length; k++){
			int addvalue = (int)(baseattrarr[k] + stregrowarr[k] * strelv * ((100 + star_percent) / 100));
			//System.out.println("addvalue:"+addvalue);
			equipdata[attrtypearr[k]] += addvalue;
			//System.out.println("--------num-"+num+"-strelv-"+strelv+"-starlv-"+starlv+"-baseattrarr-"+baseattrarr[k]+"-stregrowarr-"+stregrowarr[k]+"-star_percent-"+star_percent+"-------addvalue-"+addvalue+"-equ-"+equipdata[attrtypearr[k]]+"----------");
		}
		return equipdata;
	}
	
	/**
	 * 将灵珠穿戴状态转换为穿戴编号
	 */
	public int[] converOrbStateToNum(int phase, String upphasenumStr, String stateStr) throws Exception {
		int[] numarr = new int[6];
		int[] upphasenumArr = Tools.splitStrToIntArr(upphasenumStr, ",");
		DBPaRs partnerUpphaseRs = DBPool.getInst().pQueryA(PartnerBAC.tab_partner_upphase, "num="+upphasenumArr[phase-1]);//伙伴阶段初始值为1
		int[] statearr = Tools.splitStrToIntArr(stateStr, ",");
		for(int k = 0; k < numarr.length; k++){
			if(statearr[k] == 1){
				numarr[k] = partnerUpphaseRs.getInt("pos"+(k+1));
			}
		}
		return numarr;
	}
	
	/**
	 * 将装备穿戴状态转换为穿戴数据
	 */
	public int[][] converEquipStateToData(String equipStr) throws Exception {
		int[][] equiparr = new int[6][3];
		int[][] parequip = null;
		if(!equipStr.equals("0")){
			parequip = Tools.splitStrToIntArr2(equipStr, "|", ",");
		}
		for(int k = 0; parequip != null && k < equiparr.length; k++){
			int equipnum = 0;
			if(parequip[k][0] == 1){
				equipnum = parequip[k][1];
			} else 
			if(parequip[k][0] == 2){
				DBPsRs equipRs = DBPool.getInst().pQueryS(ItemBAC.getInstance().getTab(ItemBAC.TYPE_EQUIP_ORDINARY), "equiptype="+(k+1)+" and rare="+parequip[k][1]);
				equipRs.setRow(MyTools.getRandom(1, equipRs.count()));
				equipnum = equipRs.getInt("num");
			}
			equiparr[k][0] = equipnum;//编号
			equiparr[k][1] = parequip[k][2];//强化等级
			equiparr[k][2] = parequip[k][3];//星级
		}
		return equiparr;
	}
	
	/**
	 * 获得伙伴
	 */
	public void obtainPartner(DBHelper dbHelper, int playerid, int num, JSONArray itemarr, JSONArray partnerarr, GameLog gl) throws Exception {
		DBPsRs partnerStorRs = query(playerid, "playerid="+playerid+" and num="+num);
		DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+num);
		if(partnerStorRs.have()){
			int initstar = partnerRs.getInt("initstar");
			//DBPsRs upStarRs = DBPool.getInst().pQueryS(tab_partner_upstar, "star<="+initstar);
			int addamount = 0;
			if(initstar == 1){
				addamount = 7;
			} else 
			if(initstar == 2){
				addamount = 16;
			} else 
			if(initstar == 3){
				addamount = 30;
			}
			JSONArray arr = ItemBAC.getInstance().add(dbHelper, playerid, ItemBAC.TYPE_SOUL_STONE, partnerRs.getInt("needsoulstone"), addamount, ItemBAC.ZONE_BAG, ItemBAC.SHORTCUT_MAIL, 1, gl);
			MyTools.combJsonarr(itemarr, arr);
			gl.addRemark("已拥有伙伴 "+partnerRs.getString("name")+" 将本次获得伙伴分解为魂石");
		} else {
			int partnerid = create(dbHelper, playerid, num, partnerRs.getInt("awaken")==0?1:0, 1, 1, partnerRs.getInt("initstar"), null, null, null);
			partnerarr.add(new JSONArray(new int[]{num, partnerid}));
			gl.addRemark("获得伙伴 "+GameLog.formatNameID(partnerRs.getString("name"), partnerid));
		}
	}
	
	/**
	 * 调试还原伙伴
	 */
	public ReturnValue debugResetPartner(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPsRs partnerStorRs = query(playerid, "playerid="+playerid+" and num="+num);
			if(!partnerStorRs.next()){
				BACException.throwInstance("尚未获得此伙伴");
			}
			DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_partner, "num="+num);
			SqlString sqlStr = new SqlString();
			sqlStr.add("lv", 1);
			sqlStr.add("exp", 0);
			sqlStr.add("phase", 1);
			sqlStr.add("star", partnerRs.getInt("initstar"));
			for(int i = 0; i < 6; i++){
				if(partnerStorRs.getInt("pos"+(i+1)) != 0){
					putoffEquip(playerid, partnerStorRs.getInt("id"), i+1);
					sqlStr.add("pos"+(i+1), 0);
				}
			}
			for(int i = 0; i < 6; i++){
				sqlStr.add("orb"+(i+1), 0);
			}
			for(int i = 0; i < 5; i++){
				sqlStr.add("skilllv"+(i+1), 1);
			}
			update(dbHelper, playerid, sqlStr, "playerid="+playerid+" and num="+num);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 创造伙伴
	 */
	public int create(DBHelper dbHelper, int playerid, int num, int awaken, int lv, int phase, int star, int[] equiparr, int[] orbarr, int[] skilvarr) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("serverid", Conf.sid);
		sqlStr.add("num", num);
		sqlStr.add("awaken", awaken);
		sqlStr.add("lv", lv);
		sqlStr.add("exp", 0);
		sqlStr.add("phase", phase);
		sqlStr.add("star", star);
		for(int i = 0; i < 6; i++){
			if(equiparr != null){
				sqlStr.add("pos"+(i+1), equiparr[i]);
			} else {
				sqlStr.add("pos"+(i+1), 0);
			}
		}
		for(int i = 0; i < 6; i++){
			if(equiparr != null){
				sqlStr.add("orb"+(i+1), orbarr[i]);
			} else {
				sqlStr.add("orb"+(i+1), 0);
			}
		}
		for(int i = 0; i < 5; i++){
			if(skilvarr != null){
				sqlStr.add("skilllv"+(i+1), skilvarr[i]);
			} else {
				sqlStr.add("skilllv"+(i+1), 1);
			}
		}
		return insert(dbHelper, playerid, sqlStr);
	}
	
	//--------------静态区--------------
	
	private static PartnerBAC instance = new PartnerBAC();
	
	/**
	 * 获取实例
	 */
	public static PartnerBAC getInstance(){
		return instance;
	}
}
