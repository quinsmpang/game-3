package com.moonic.bac;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.battle.BattleBox;
import com.moonic.battle.BattleManager;
import com.moonic.battle.Const;
import com.moonic.battle.SpriteBox;
import com.moonic.battle.TeamBox;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.Player;
import com.moonic.socket.SocketServer;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

/**
 * 副本BAC
 * @author wkc
 */
public class CopymapBAC extends PlaStorBAC {
	public static final String tab_copymap = "tab_copymap";
	public static final String tab_copymap_dropaward = "tab_copymap_dropaward";
	public static final String tab_copymap_buytimes = "tab_copymap_buytimes";
	public static final String tab_copymap_map = "tab_copymap_map";
	
	/**
	 * 构造
	 */
	public CopymapBAC() {
		super("tab_copymap_stor", "playerid", "bigmap");
	}
	
	/**
	 * 进入副本
	 */
	public ReturnValue enter(int playerid, int cmnum, String posStr){
		try {
			int amounts = ItemBAC.getInstance().getAmountByItemtype(playerid, ItemBAC.TYPE_EQUIP_ORDINARY, ItemBAC.ZONE_BAG);
			if (amounts > 50) {
				BACException.throwInstance("装备背包已满");
			}
			JSONArray posArr = new JSONArray(posStr);
			PartnerBAC.getInstance().checkPosarr(playerid, posArr, 1, 1);
			checkCondition(playerid, cmnum);
			BattleBox battlebox = getBattleBox(playerid, cmnum, posArr);
			Player pla = SocketServer.getInstance().plamap.get(playerid);
			battlebox.parameterarr = new JSONArray().add(System.currentTimeMillis());
			pla.verifybattle_battlebox = battlebox;
			
			DBPaRs cmListRs = getCmListRs(cmnum);
			StringBuffer remarkSb = new StringBuffer();
			remarkSb.append("进入");
			remarkSb.append(cmListRs.getInt("cmtype") == 1 ? "普通":"精英");
			remarkSb.append("副本");
			remarkSb.append(GameLog.formatNameID(cmListRs.getString("name"), cmnum));
			GameLog.getInst(playerid, GameServlet.ACT_COPYMAP_ENTER)
			.addRemark(remarkSb)
			.save();
			return new ReturnValue(true, battlebox.getJSONArray().toString());
		} catch(Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 副本战斗结束后通知
	 */
	public ReturnValue endChallenge(int playerid, int cmnum, String battleRecord){
		DBHelper dbHelper = new DBHelper();
		try {
			boolean haveStar = false;
			DBPaRs cmListRs = getCmListRs(cmnum);
			int pointType = cmListRs.getInt("pointtype");
			if(pointType == 2){
				haveStar = true;
			}
			int star = verifyBattle(playerid, battleRecord, haveStar);
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_COPYMAP_ENDCHALLENGE);
			JSONArray jsonarr = endBattle(dbHelper, playerid, cmnum, star , gl);
			
			gl.save();
			return new ReturnValue(true, jsonarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 购买挑战次数
	 */
	public ReturnValue buy(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs cmListRs = getCmListRs(num);
			int limit = cmListRs.getInt("limit");
			if(limit == -1){
				BACException.throwInstance("此副本无次数限制");
			}
			int bigmap = cmListRs.getInt("bigmap");
			DBPaRs cmRs = getDataRsByKey(playerid, bigmap);
			if(!cmRs.exist()){
				BACException.throwInstance("此副本尚未挑战过");
			}
			JSONObject timesobj = new JSONObject(cmRs.getString("dailytimes"));
			int times = timesobj.optInt(String.valueOf(num));
			if(times < limit){
				BACException.throwInstance("此副本挑战次数还有剩余");
			} 
			JSONObject buyobj = new JSONObject(cmRs.getString("buy"));
			int buytimes = buyobj.optInt(String.valueOf(num));
			int need = 0;
			DBPsRs conListRs = DBPool.getInst().pQueryS(tab_copymap_buytimes);
			while(conListRs.next()){
				int[] rank = Tools.splitStrToIntArr(conListRs.getString("rank"), ",");
				if(buytimes+1 <= rank[1] || rank[1] == -1){
					need = conListRs.getInt("consume");
					break;
				}
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_COPYMAP_BUYTIMES);
			PlayerBAC.getInstance().useCoin(dbHelper, playerid, need, gl);
			buyobj.put(String.valueOf(num), buytimes+1);
			timesobj.put(String.valueOf(num), 0);
			SqlString sqlStr = new SqlString();
			sqlStr.add("dailytimes", timesobj.toString());
			sqlStr.add("buy", buyobj.toString());
			updateByKey(dbHelper, playerid, sqlStr, bigmap);
			
			gl.addRemark("购买副本（"+GameLog.formatNameID(cmListRs.getString("name"), num)+"）挑战次数");
			gl.addChaNote("购买次数", buytimes, 1);
			gl.save();
			return new ReturnValue(true);
		} catch(Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 扫荡副本
	 * @param num,副本编号
	 * @param times,扫荡次数
	 */
	public ReturnValue sweep(int playerid, int num, int times){
		DBHelper dbHelper = new DBHelper();
		try {
			int amounts = ItemBAC.getInstance().getAmountByItemtype(playerid, ItemBAC.TYPE_EQUIP_ORDINARY, ItemBAC.ZONE_BAG);
			if (amounts > 50) {
				BACException.throwInstance("装备背包已满");
			}
			DBPaRs cmListRs = getCmListRs(num);
			int pointType = cmListRs.getInt("pointtype");
			if(pointType == 1){
				BACException.throwInstance("此副本为小关卡，不能重复打");
			}
			int limit = cmListRs.getInt("limit");
			int bigmap = cmListRs.getInt("bigmap");
			int cmtype = cmListRs.getInt("cmtype");
			DBPaRs cmRs = getDataRsByKey(playerid, bigmap);
			if(!cmRs.exist()){
				BACException.throwInstance("此副本尚未挑战过");
			}
			JSONObject passObj = new JSONObject(cmRs.getString("passed"));
			int star = passObj.optInt(String.valueOf(num));
			if(star == 0){
				BACException.throwInstance("此副本尚未通过");
			}
			if(star < 3){
				BACException.throwInstance("此副本尚未3星通过");
			}
			if(limit != -1){
				JSONObject timesobj = new JSONObject(cmRs.getString("dailytimes"));
				if(limit - timesobj.optInt(String.valueOf(num)) < times){
					BACException.throwInstance("此副本剩余挑战次数不足");
				} 
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_COPYMAP_SWEEP);
			int needEnergy = cmListRs.getInt("energy");
			PlaRoleBAC.getInstance().subValue(dbHelper, playerid, "energy", needEnergy*times, gl, "体力");
			int dropnum = cmListRs.getInt("dropaward");
			DBPaRs dropListRs = DBPool.getInst().pQueryA(tab_copymap_dropaward, "num="+dropnum);
			if(!dropListRs.exist()){
				BACException.throwInstance("掉落编号不存在");
			}
			String[] item = Tools.splitStr(dropListRs.getString("item"), "|");
			int[] odds = Tools.splitStrToIntArr(dropListRs.getString("odds"), ",");
			int[] control = Tools.splitStrToIntArr(dropListRs.getString("control"), ",");
			String paramStrDouble = CustomActivityBAC.getInstance().getFuncActiPara(cmtype == 1 ? CustomActivityBAC.TYPE_CM_DOUBLE : CustomActivityBAC.TYPE_CM_ELITE_DOUBLE);
			int param = 0;
			if(paramStrDouble != null){
				param = Tools.str2int(paramStrDouble);
			}
			int dropAm = 1;
			if(param == 1 || param == 3){
				doubleOddsArray(odds);
				dropAm = 2;
			} 
			JSONObject dropObj = new JSONObject(cmRs.getString("drops"));
			JSONArray dropArr = dropObj.optJSONArray(String.valueOf(num));
			dropArr = initDropsData(dropArr, control);
			JSONArray returnarr = new JSONArray();
			JSONArray contentarr = new JSONArray();//奖励内容
			StringBuffer awardSb = new StringBuffer();
			String moneyaward = cmListRs.getString("moneyaward");//铜钱奖励
			String wipeaward = cmListRs.getString("wipeaward");//扫荡额外奖励
			for(int i = 0; i < times; i++){
				if(i > 0){
					awardSb.append("|");
				}
				awardSb.append(moneyaward);
				awardSb.append("|");
				awardSb.append(wipeaward);
				dropArr.put(0, dropArr.getInt(0) + 1);
				JSONArray jsonarr = handleDropsAward(odds, control, dropArr, dropAm);//返回掉数据
				JSONArray awardindex = jsonarr.getJSONArray(0);//奖励下标
				StringBuffer oneSb = new StringBuffer();//每次的奖励
				for(int j = 0; j < awardindex.size(); j++){
					String itemStr = item[awardindex.getInt(j)];
					if(param == 2 || param == 3){
						itemStr = doubleItemAmount(itemStr);
					}
					if(j > 0){
						oneSb.append("|");
					}
					oneSb.append(itemStr);
					awardSb.append("|");
					awardSb.append(itemStr);
				}
				if(awardindex.size() == 0){//特殊处理，如果没获得任何奖励，送固定物品
					oneSb.append("1,1,2,3");
					awardSb.append("|");
					awardSb.append("1,1,2,3");
				}
				//副本追加奖励
				String appendStr = getAppendItem(cmtype);
				if(appendStr.length() > 0){
					awardSb.append("|");
					awardSb.append(appendStr);
					oneSb.append("|");
					oneSb.append(appendStr);
				}
				contentarr.add(oneSb.toString());
				JSONArray dropsarr = jsonarr.getJSONArray(1);//有控制的掉落情况
				dropArr = handleDropsAfter(dropArr, dropsarr, control);
			}
			if(dropObj.optJSONArray(String.valueOf(num)) == null){
				dropObj.put(String.valueOf(num), dropArr);
			}
			SqlString sqlStr = new SqlString();
			sqlStr.add("drops", dropObj.toString());
			JSONObject timesObj = new JSONObject(cmRs.getString("dailytimes"));
			if(limit != -1){
				timesObj.put(String.valueOf(num), timesObj.optInt(String.valueOf(num))+times);
				sqlStr.add("dailytimes", timesObj.toString());
			}
			JSONObject passAmObj = new JSONObject(cmRs.getString("passedam"));
			int currAm = passAmObj.optInt(String.valueOf(num));
			if(currAm < 100){
				passAmObj.put(String.valueOf(num), currAm+times);
				sqlStr.add("passedam", passAmObj.toString());
			}
			updateByKey(dbHelper, playerid, sqlStr, bigmap);
			int exp = cmListRs.getInt("exp");
			PlayerBAC.getInstance().addExp(dbHelper, playerid, exp*times, gl);
			int[] money = Tools.splitStrToIntArr(moneyaward, ",");
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
			PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, cmtype == 1 ? PlaWelfareBAC.TYPE_COPYMAP_ORDINARY : PlaWelfareBAC.TYPE_COPYMAP_ELITE, times, gl);
			returnarr.add(contentarr);
			returnarr.add(awardarr);
			returnarr.add(wipeaward);
			returnarr.add(exp);
			returnarr.add(money[1]);
			
			StringBuffer remarkSb = new StringBuffer();
			remarkSb.append("扫荡");
			remarkSb.append(cmtype == 1 ? "普通":"精英");
			remarkSb.append("副本");
			remarkSb.append(GameLog.formatNameID(cmListRs.getString("name"), num));
			remarkSb.append(times+"次");
			gl.addRemark(remarkSb);
			gl.save();
			return new ReturnValue(true, returnarr.toString());
		} catch(Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 领取星级奖励
	 * @param awardnum,奖励编号1~3
	 */
	public ReturnValue getStarAward(int playerid, int bigmap, int awardnum){
		DBHelper dbHelper = new DBHelper();
		try {
			if(!(awardnum >= 1 && awardnum <= 3)){
				BACException.throwInstance("奖励编号错误");
			}
			DBPaRs cmRs = getDataRsByKey(playerid, bigmap);
			if(!cmRs.exist()){
				BACException.throwInstance("此地图尚未挑战过");
			}
			DBPaRs mapListRs = DBPool.getInst().pQueryA(tab_copymap_map, "num="+bigmap);
			if(!mapListRs.exist()){
				BACException.throwInstance("不存在的副本地图编号"+bigmap);
			}
			JSONArray havedarr = new JSONArray(cmRs.getString("award"));
			if(havedarr.contains(awardnum)){
				BACException.throwInstance("此奖励已领取");
			}
			int need = mapListRs.getInt("star"+awardnum);
			JSONObject passObj = new JSONObject(cmRs.getString("passed"));
			int total = passObj.optInt("total");
			if(total < need){
				BACException.throwInstance("此副本星数不够");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_COPYMAP_GETSTARAWARD);
			dbHelper.openConnection();
			String award = mapListRs.getString("award"+awardnum);
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, award, ItemBAC.SHORTCUT_MAIL, 1, gl);
			havedarr.add(awardnum);
			SqlString sqlStr = new SqlString();
			sqlStr.add("award", havedarr.toString());
			updateByKey(dbHelper, playerid, sqlStr, bigmap);
			
			gl.addRemark("领取大地图编号"+bigmap+"的星级奖励编号"+awardnum);
			gl.save();
			return new ReturnValue(true, awardarr.toString());
		} catch(Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 挑战每波结束的处理
	 */
	public JSONArray endBattle(DBHelper dbHelper, int playerid, int num, int star, GameLog gl) throws Exception{
		DBPaRs cmListRs = getCmListRs(num);
		int limit = cmListRs.getInt("limit");
		int bigmap = cmListRs.getInt("bigmap");
		boolean isFirst = false;//第一次通关
		SqlString sqlStr = new SqlString();
		StringBuffer awardSb = new StringBuffer();//奖励字符串
		awardSb.append(cmListRs.getString("moneyaward"));
		DBPaRs cmRs = getDataRsByKey(playerid, bigmap);
		JSONArray exparr = new JSONArray();
		boolean updateTimes = true;//是否需要更新次数
		Player pla = SocketServer.getInstance().plamap.get(playerid);
		if(pla.verifybattle_battlebox.parameterarr.getLong(0) < MyTools.getCurrentDateLong()){//战斗结束时是否过了0点
			updateTimes = false;
		}
		int cmtype = cmListRs.getInt("cmtype");
		StringBuffer remarkSb = new StringBuffer();
		remarkSb.append("通关");
		remarkSb.append(cmtype == 1 ? "普通":"精英");
		remarkSb.append("副本");
		remarkSb.append(GameLog.formatNameID(cmListRs.getString("name"), num));
		remarkSb.append(star > 0 ? star+"星":"无星级");
		if(!cmRs.exist()){
			isFirst = true;
			JSONObject passObj = new JSONObject();
			JSONObject passAmObj = new JSONObject();
			if(star != 0){
				passObj.put(String.valueOf(num), star);
				if(star > 0){
					passObj.put("total", star);
				}
				passAmObj.put(String.valueOf(num), 1);
			}
			JSONObject timesobj = new JSONObject();
			if(updateTimes){
				if(limit != -1){
					timesobj.put(String.valueOf(num), 1);
				}
			}
			JSONObject dropobj = new JSONObject();
			sqlStr.add("playerid", playerid);
			sqlStr.add("bigmap", bigmap);
			sqlStr.add("passed", passObj.toString());
			sqlStr.add("drops", dropobj.toString());
			sqlStr.add("dailytimes", timesobj.toString());
			sqlStr.add("buy", new JSONObject().toString());
			sqlStr.add("award", new JSONArray().toString());
			sqlStr.add("passedam", passAmObj.toString());
			insert(dbHelper, playerid, sqlStr);
		} else{
			if(updateTimes){
				JSONObject timesObj = new JSONObject(cmRs.getString("dailytimes"));
				if(limit != -1){
					timesObj.put(String.valueOf(num), timesObj.optInt(String.valueOf(num))+1);
					sqlStr.add("dailytimes", timesObj.toString());
				}
			}
			if(star != 0){//胜利
				JSONObject passAmObj = new JSONObject(cmRs.getString("passedam"));
				int currAm = passAmObj.optInt(String.valueOf(num));
				if(currAm < 100){
					passAmObj.put(String.valueOf(num), currAm+1);
					sqlStr.add("passedam", passAmObj.toString());
				}
				JSONObject passObj = new JSONObject(cmRs.getString("passed"));
				int oldStar = passObj.optInt(String.valueOf(num));
				if(oldStar == 0){
					isFirst = true;
				}
				if(oldStar == 0 || star > oldStar){//星级大于当前星级时更新
					passObj.put(String.valueOf(num), star);
					if(star > oldStar){
						passObj.put("total", passObj.optInt("total")+(star-oldStar));
					}
					if(oldStar > 0){
						remarkSb.append(",星级变化" + oldStar + "->" + star) ;
					}
					sqlStr.add("passed", passObj.toString());
				}
				if(!isFirst){
					int dropnum = cmListRs.getInt("dropaward");
					DBPaRs dropListRs = DBPool.getInst().pQueryA(tab_copymap_dropaward, "num="+dropnum);
					if(!dropListRs.exist()){
						BACException.throwInstance("掉落编号不存在");
					}
					String[] item = Tools.splitStr(dropListRs.getString("item"), "|");
					int[] odds = Tools.splitStrToIntArr(dropListRs.getString("odds"), ",");
					int[] control = Tools.splitStrToIntArr(dropListRs.getString("control"), ",");
					String paramStrDouble = CustomActivityBAC.getInstance().getFuncActiPara(cmtype == 1 ? CustomActivityBAC.TYPE_CM_DOUBLE : CustomActivityBAC.TYPE_CM_ELITE_DOUBLE);
					int param = 0;
					if(paramStrDouble != null){
						param = Tools.str2int(paramStrDouble);
					}
					int dropAm = 1;
					if(param == 1 || param == 3){
						doubleOddsArray(odds);
						dropAm = 2;
					} 
					JSONObject dropObj = new JSONObject(cmRs.getString("drops"));
					JSONArray dropArr = dropObj.optJSONArray(String.valueOf(num));
					dropArr = initDropsData(dropArr, control);
					dropArr.put(0, dropArr.getInt(0) + 1);
					JSONArray jsonarr = handleDropsAward(odds, control, dropArr, dropAm);//返回掉落数据
					JSONArray awardindex = jsonarr.getJSONArray(0);//奖励下标
					for(int i = 0; i < awardindex.size(); i++){
						awardSb.append("|");
						String itemStr = item[awardindex.getInt(i)];
						if(param == 2 || param == 3){
							itemStr = doubleItemAmount(itemStr);
						}
						awardSb.append(itemStr);
					}
					if(awardindex.size() == 0){//特殊处理，如果没获得任何奖励，送固定物品
						awardSb.append("|");
						awardSb.append("1,1,2,3");
					}
					//副本追加奖励
					String appendStr = getAppendItem(cmtype);
					if(appendStr.length() > 0){
						awardSb.append("|");
						awardSb.append(appendStr);
					}
					JSONArray dropsarr = jsonarr.getJSONArray(1);//有控制的掉落情况
					dropArr = handleDropsAfter(dropArr, dropsarr, control);
					if(dropObj.optJSONArray(String.valueOf(num)) == null){
						dropObj.put(String.valueOf(num), dropArr);
					}
					sqlStr.add("drops", dropObj.toString());
				}
			}
			if(sqlStr.getColCount() > 0){
				updateByKey(dbHelper, playerid, sqlStr, bigmap);
			}
		}
		int exp = 0;//历练经验
		int exp1 = 0;//伙伴经验
		if(star != 0){
			int needEnergy = cmListRs.getInt("energy");
			PlaRoleBAC.getInstance().subValue(dbHelper, playerid, "energy", needEnergy, gl, "体力");
			if(isFirst){//首次通关奖励
				String award = cmListRs.getString("firstaward");
				awardSb.append("|");
				awardSb.append(award);
				if(star > 0){
					remarkSb.append(",首次通关星级为"+star);
				}
			}
			exp = cmListRs.getInt("exp");
			exp1 = cmListRs.getInt("exp1");
			exparr.add(exp);
			exparr.add(exp1);
			PlayerBAC.getInstance().addExp(dbHelper, playerid, exp, gl);
			BattleBox battleBox = pla.verifybattle_battlebox;
			for(int i = 0; i< battleBox.teamArr[0].get(0).sprites.size(); i++){
				int partnerId = battleBox.teamArr[0].get(0).sprites.get(i).partnerId;
				if(partnerId != 0){
					PartnerBAC.getInstance().addExp(dbHelper, playerid, partnerId, exp1, gl);
				}
			}
		}
		JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
		PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, cmtype == 1 ? PlaWelfareBAC.TYPE_COPYMAP_ORDINARY : PlaWelfareBAC.TYPE_COPYMAP_ELITE, gl);
		CustomActivityBAC.getInstance().updateProcess(dbHelper, playerid, 27, num);
		gl.addRemark(remarkSb);
		pla.verifybattle_battlebox = null;
		JSONArray returnarr = new JSONArray();
		returnarr.add(awardSb.toString());//奖励内容
		returnarr.add(awardarr);//背包数据
		returnarr.add(exp);//历练经验
		returnarr.add(exp1);//伙伴经验
		returnarr.add(star);//星级
		return returnarr;
	}
	
	/**
	 * 根据掉落编号获得奖励
	 * @param oddsarr,掉落几率数值
	 * @param control,掉落控制数值
	 * @param droparr,[循环中的次护送,已掉落的个数..]
	 * @param dropAm,一个循环内的掉落次数
	 */
	public JSONArray handleDropsAward(int[] oddsarr, int[] control, JSONArray dropArr, int dropAm) throws Exception{
		int maxAm = 4;//TODO随机奖励掉落的最大数量，有活动时最多6个，无活动时最多4个
		JSONArray jsonarr = new JSONArray();
		JSONArray awardarr = new JSONArray();//奖励下标
		JSONArray drop = new JSONArray();
		int j = 1;
		int times = dropArr.getInt(0);
		for(int i = 0; control != null && i < control.length; i++){
			if(awardarr.length() >= maxAm){
				break;
			}
			if(control[i] <= 0){//直接随机概率
				int random = MyTools.getRandom(1, 1000);
				if(random <= oddsarr[i]){
					awardarr.add(i);
				}
			} else{
				int droped = 0;//是否掉落
				int haveObtainAm = dropArr.getInt(j);
				double odd = (double)((times % control[i] != 0 ? times % control[i] : control[i])*(dropAm-haveObtainAm))/(double)control[i];
				double ran = Math.random();
				if(ran < odd){
					awardarr.add(i);
					droped = 1;
				}
				drop.add(droped);
				j++;
			}
		}
		jsonarr.add(awardarr);
		jsonarr.add(drop);
		return jsonarr;
	}
	
	/**
	 * 初始化掉落数据
	 */
	public JSONArray initDropsData(JSONArray dropArr, int[] control) throws Exception{
		if(dropArr == null){//初始化掉落记录
			dropArr = new JSONArray();
			dropArr.add(0);//第几次掉落
			for(int i = 0; i < control.length; i++){
				if(control[i] != 0){
					dropArr.add(0);//已获得的个数
				}
			}
		} else{
			int count = 0;//修正掉落记录
			for(int i = 0; i < control.length; i++){
				if(control[i] != 0){
					count++;
				}
			}
			int addSize = count - (dropArr.size()-1);
			if(addSize > 0){
				for(int i = 0; i < addSize; i++){
					dropArr.add(0);
				}
			}
		} 
		return dropArr;
	}
	
	/**
	 * 掉落后处理掉落记录
	 */
	public JSONArray handleDropsAfter(JSONArray dropArr, JSONArray dropsarr, int[] control) throws Exception{
		for(int j = 0; j < dropsarr.size(); j++){
			dropArr.put(j+1, dropArr.getInt(j+1) + dropsarr.getInt(j));
		}
		int index = 1;
		for(int j = 0; j < control.length; j++){
			if(control[j] != 0){
				if(dropArr.getInt(0) % control[j] == 0){
					dropArr.put(index, 0);
				}
				index++;
			}
		}
		int multi = 1;//副本循环计数归0
		for(int k = 0; k < control.length; k++){
			if(control[k] != 0){
				multi *= control[k];
			}
		}
		if((dropArr.getInt(0) % multi) == 0){
			dropArr.put(0, 0);
		}
		return dropArr;
	}
	
	/**
	 * 对几率数组翻倍
	 */
	public void doubleOddsArray(int[] oddsarr){
		for(int i = 0; i < oddsarr.length; i++){
			oddsarr[i] *= 2;
		}
	}
	
	/**
	 * 对物品字符串的中数量翻倍
	 */
	public String doubleItemAmount(String item){
		int index = item.lastIndexOf(",");
		StringBuffer itemSb = new StringBuffer();
		itemSb.append(item.substring(0, index+1));
		itemSb.append(Tools.str2int(item.substring(index+1))*2);
		return itemSb.toString();
	}
	
	/**
	 * 获取副本追加奖励数据
	 */
	public String getAppendItem(int cmtype) throws Exception{
		StringBuffer itemSb = new StringBuffer();
		String paramStrAppend = CustomActivityBAC.getInstance().getFuncActiPara(cmtype == 1 ? CustomActivityBAC.TYPE_CM_OTHER_ITEM : CustomActivityBAC.TYPE_CM_ELITE_OTHER_ITEM);
		if(paramStrAppend != null){
			String[] itemStr = Tools.splitStr(paramStrAppend, "|");
			for(int i = 0; i < itemStr.length; i++){
				String[] item = Tools.splitStr(itemStr[i], "#");
				int odds = Tools.str2int(item[1]);
				int random = MyTools.getRandom(1, 1000);
				if(random < odds){
					if(itemSb.length() > 0){
						itemSb.append("|");
					}
					itemSb.append(item[0]);
				}
			}
		}
		return itemSb.toString();
	}
	
	/**
	 * 检查进入副本条件
	 */
	public void checkCondition(int playerid, int cmnum) throws Exception{
		DBPaRs cmListRs = getCmListRs(cmnum);
		String condition = cmListRs.getString("condition");
		int[][] conditon = Tools.splitStrToIntArr2(condition, "|", ",");
		for(int i = 0; conditon != null && i < conditon.length; i++){
			int type = conditon[i][0];
			if(type == 1){//达到历练等级
				int plv = PlayerBAC.getInstance().getIntValue(playerid, "lv");
				if(plv < conditon[i][1]){
					BACException.throwInstance("历练等级未达到条件");
				}
			} else
			if(type == 2){//通过指定编号副本
				boolean isPass = checkPass(playerid, conditon[i][1]);
				if(!isPass){
					BACException.throwInstance("尚未通过指定副本"+conditon[i][1]);
				}
			}
		}
		//关卡类型
		int pointType = cmListRs.getInt("pointtype");
		if(pointType == 1){
			boolean isPass = checkPass(playerid, cmnum);
			if(isPass){
				BACException.throwInstance("此副本已通过，不能重复挑战");
			}
		}
		//副本次数限制
		int limit = cmListRs.getInt("limit");
		int bigmap = cmListRs.getInt("bigmap");
		if(limit != -1){
			DBPaRs cmRs = CopymapBAC.getInstance().getDataRsByKey(playerid, bigmap);
			if(cmRs.exist()){
				JSONObject jsonobj = new JSONObject(cmRs.getString("dailytimes"));
				int times = jsonobj.optInt(String.valueOf(cmnum));
				if(times >= limit){
					BACException.throwInstance("此副本挑战次数已用完");
				}
			}
		}
		//需要体力
		int needEnergy = cmListRs.getInt("energy");
		int energy = PlaRoleBAC.getInstance().getIntValue(playerid, "energy");
		if(needEnergy > energy){
			BACException.throwInstance("体力不足");
		}
	}
	
	/**
	 * 检查是否通过指定副本
	 */
	public boolean checkPass(int playerid, int cmnum) throws Exception{
		boolean isPass = false;
		DBPaRs cmListRs = getCmListRs(cmnum);
		int bigmap = cmListRs.getInt("bigmap");
		DBPaRs cmRs = getDataRsByKey(playerid, bigmap);
		if(cmRs.exist()){
			JSONObject passObj = new JSONObject(cmRs.getString("passed"));
			if(passObj.optInt(String.valueOf(cmnum)) != 0){
				isPass = true;
			}
		}
		return isPass;
	}
	
	/**
	 * 获取角色BatteBox
	 */
	public BattleBox getBattleBox(int playerid, int cmnum, JSONArray posArr) throws Exception{
		DBPaRs cmListRs = getCmListRs(cmnum);
		TeamBox myTeamBox = PartnerBAC.getInstance().getTeamBox(playerid, 0, posArr, null);
		int intopos = cmListRs.getInt("intopos");
		BattleBox battleBox = new BattleBox();
		battleBox.bgnum = cmListRs.getInt("scene");
		battleBox.teamArr[0].add(myTeamBox);
		for(int i = intopos; i <= 3; i++){
			String enemy = cmListRs.getString("enemy"+i);
			TeamBox enemyTeamBox = Enemy.getInstance().createTeamBox(enemy);
			battleBox.teamArr[1].add(enemyTeamBox);
		}
		return battleBox;
	}
	
	/**
	 * 验证战斗，并返回星级
	 * @param haveStar，是否有星级
	 */
	public int verifyBattle(int playerid, String battleRecord, boolean haveStar) throws Exception{
		Player pla = SocketServer.getInstance().plamap.get(playerid);
		if(pla.verifybattle_battlebox == null){
			BACException.throwInstance("请先进入挑战");
		}
		BattleBox battlebox = pla.verifybattle_battlebox;
		ReturnValue rv = BattleManager.verifyPVEBattle(battlebox, battleRecord);
		if(!rv.success){
			BACException.throwInstance(rv.info);
		}
		if(battlebox.winTeam != Const.teamA){//失败
			BACException.throwInstance("通关失败");
		}
		int star = 0;//星级
		int deadam = 0;//计算战斗后挂了的数量
		if(haveStar){
			//计算星级
			ArrayList<SpriteBox> sprites = battlebox.teamArr[0].get(0).sprites;
			for(int i = 0; i< sprites.size(); i++){
				if(sprites.get(i).battle_prop[Const.PROP_HP] <= 0){
					deadam++;
				}
			}
			star = 3 - (deadam > 2 ? 2 : deadam);
		} else{//没有星级时为-1
			star = -1;
		}
		return star;
	}
	
	/**
	 * 获取指定编号副本的通过次数
	 */
	public int getPassedAm(int playerid, int num) throws Exception{
		int passedAm = 0;
		DBPaRs cmListRs = getCmListRs(num);
		int bigmap = cmListRs.getInt("bigmap");
		DBPaRs cmRs = getDataRsByKey(playerid, bigmap);
		if(cmRs.exist()){
			JSONObject passAmObj = new JSONObject(cmRs.getString("passedam"));
			passedAm = passAmObj.optInt(String.valueOf(num));
		}
		return passedAm;
	}
	
	/**
	 * 重置各副本每日挑战次数
	 */
	public void resetData(DBHelper dbHelper, int playerid) throws Exception{
		SqlString sqlStr = new SqlString();
		sqlStr.add("dailytimes", new JSONObject().toString());
		sqlStr.add("buy", new JSONObject().toString());
		update(dbHelper, playerid, sqlStr, "playerid="+playerid);
	}
	
	/**
	 * 获取副本数据列表
	 */
	public DBPaRs getCmListRs(int cmnum) throws Exception{
		DBPaRs cmListRs = DBPool.getInst().pQueryA(tab_copymap, "num="+cmnum);
		if(!cmListRs.exist()){
			BACException.throwInstance("不存在的副本编号"+cmnum);
		}
		return cmListRs;
	}
	
	/**
	 * 登陆时获取副本数据
	 */
	public JSONObject getLoginData(int playerid) throws Exception{
		JSONObject jsonObj = new JSONObject();
		DBPsRs cmRs = query(playerid, "playerid="+playerid);
		while(cmRs.next()){
			JSONObject bigmapObj = new JSONObject();
			bigmapObj.put("passed", new JSONObject(cmRs.getString("passed")));//已通过的副本编号和星数
			bigmapObj.put("dailytimes", new JSONObject(cmRs.getString("dailytimes")));//各副本的每日挑战次数
			bigmapObj.put("buy", new JSONObject(cmRs.getString("buy")));//各副本每日购买次数
			bigmapObj.put("award", new JSONArray(cmRs.getString("award")));//已领取的星级奖励
			bigmapObj.put("passedam", new JSONObject(cmRs.getString("passedam")));//各副本的通关次数
			jsonObj.put(String.valueOf(cmRs.getInt("bigmap")), bigmapObj);
		}
		return jsonObj;
	}
	
	//--------------------调试区------------------
	
	/**
	 * 一键通过副本区间
	 */
	public ReturnValue debugOneKeyPass(int playerid, int start, int end){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			String where = null;
			if(end != 0){
				where = "num>="+start+" and num<="+end;
			}
			DBPsRs cmListRs = DBPool.getInst().pQueryS(tab_copymap, where);
			while(cmListRs.next()){
				int bigmap = cmListRs.getInt("bigmap");
				DBPaRs cmRs = getDataRsByKey(playerid, bigmap);
				int star = -1;
				int pointType = cmListRs.getInt("pointtype");
				if(pointType == 2){
					star = 3;
				}
				int num = cmListRs.getInt("num");
				SqlString sqlStr = new SqlString();
				if(cmRs.exist()){
					JSONObject passObj = new JSONObject(cmRs.getString("passed"));
					if(passObj.optInt(String.valueOf(num)) == 0){
						passObj.put(String.valueOf(num), star);
						if(star != -1){
							passObj.put("total", passObj.optInt("total")+3);
						}
						sqlStr.add("passed", passObj.toString());
						updateByKey(dbHelper, playerid, sqlStr, bigmap);
					}
				} else{
					JSONObject passObj = new JSONObject();
					passObj.put(String.valueOf(num), star);
					if(star != -1){
						passObj.put("total", star);
					}
					sqlStr.add("playerid", playerid);
					sqlStr.add("bigmap", bigmap);
					sqlStr.add("passed", passObj.toString());
					sqlStr.add("drops", new JSONObject().toString());
					sqlStr.add("dailytimes", new JSONObject().toString());
					sqlStr.add("buy", new JSONObject().toString());
					sqlStr.add("award", new JSONArray().toString());
					sqlStr.add("passedam", new JSONObject().toString());
					insert(dbHelper, playerid, sqlStr);
				}
			}
			
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试清空挑战纪录
	 */
	public ReturnValue debugClearRecord(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			delete(dbHelper, playerid, "playerid="+playerid);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	//--------------------静态区------------------
	
	private static CopymapBAC instance = new CopymapBAC();
	
	public static CopymapBAC getInstance(){
		return instance;
	}
}
