package com.moonic.bac;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;

import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.MyTools;

/**
 * 奖励
 * @author John
 */
public class AwardBAC {
	
	/**
	 * 领取奖励
	 */
	public JSONArray getAward(DBHelper dbHelper, int playerid, String contentStr, byte shortcut, int from, GameLog gl) throws Exception {
		try {
			return getAward(dbHelper, playerid, Tools.splitStrToIntArr2(contentStr, "|", ","), shortcut, from, gl);		
		} catch (Exception e) {
			System.out.println("contentStr= "+contentStr);
			throw e;
		}
	}
	
	/**
	 * 整理奖励
	 */
	public String tiyContent(String contentStr){
		int[][] content = tidyContent(Tools.splitStrToIntArr2(contentStr, "|", ","));
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < content.length; i++){
			if(sb.length() > 0){
				sb.append("|");
			}
			for(int j = 0; j < content[i].length; j++){
				if(j != 0){
					sb.append(",");
				}
				sb.append(content[i][j]);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 整理奖励
	 */
	public int[][] tidyContent(int[][] content){
		int[][] new_content = null;
		for(int i = 0; i < content.length; i++){
			int type = content[i][0];
			if(type == 1){
				System.out.println("------ERROR------ TODO 整理物品，尚未处理");
				new_content = Tools.addToIntArr2(new_content, content[i]);
			} else 
			if(type == 5){
				System.out.println("------ERROR------ TODO 整理主角经验，尚未处理");
				new_content = Tools.addToIntArr2(new_content, content[i]);
			} else 
			if(type == 6){
				System.out.println("------ERROR------ TODO 整理月卡，尚未处理");
				new_content = Tools.addToIntArr2(new_content, content[i]);
			} else 
			if(type == 9){
				System.out.println("------ERROR------ TODO 整理获得伙伴，尚未处理");
				new_content = Tools.addToIntArr2(new_content, content[i]);
			} else 
			{	
				boolean use = false;
				for(int j = 0; new_content != null && j < new_content.length; j++){
					if(type == new_content[j][0]){
						new_content[j][1] += content[i][1];
						use = true;
						break;
					}
				}
				if(!use){
					new_content = Tools.addToIntArr2(new_content, content[i]);
				}
			}
		}
		return new_content;
	}
	
	/**
	 * 领取奖励
	 * @param content 按标准奖励格式，内部不进行计算和选择操作
	 */
	public JSONArray getAward(DBHelper dbHelper, int playerid, int[][] content, byte shortcut, int from, GameLog gl) throws Exception {
		JSONArray itemarr = new JSONArray();
		JSONArray partnerarr = new JSONArray();
		int[][] itemContent = new int[content.length][];//物品集合
		int itemIndex = -1;//物品集合下标
		int addEnergy = 0;//角色人物-体力
		int addMoney = 0;//角色-铜钱
		int addCoin = 0;//角色-金锭
		int addJJCCoin = 0;//角色人物-竞技币
		int addSoulPoint = 0;//角色人物-魂点
		int addFactionCon = 0;//角色帮派-功勋
		int addTowerCoin = 0;//角色人物-塔币
		int addSummonprop = 0;//召唤-伙伴召唤道具
		SqlString plaSqlStr = new SqlString();//角色
		SqlString plaRoleSqlStr = new SqlString();//角色人物
		SqlString plaFacSqlStr = new SqlString();//角色帮派
		SqlString plaSummonSqlStr = new SqlString();//召唤
		DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
		for(int i = 0; content != null && i < content.length; i++){
			int type = content[i][0];
			if(type == 1){//物品
				boolean used = false;
				if(content[i].length == 4){//标准格式统计
					for(int k = 0; k <= itemIndex; k++){
						if(itemContent[k].length != 4){
							continue;
						}
						if(content[i][1]!=itemContent[k][1] || content[i][2]!=itemContent[k][2]){
							continue;
						}
						itemContent[k][3] += content[i][3];
						used = true;
					}
				}
				if(!used){
					itemIndex++;
					itemContent[itemIndex] = content[i].clone();
				}
			} else 
			if(type == 2){//体力
				addEnergy += content[i][1];
			} else 
			if(type == 3){//铜钱
				addMoney += content[i][1];
			} else 
			if(type == 4){//金锭
				addCoin += content[i][1];
			} else 
			if(type == 5){//主角经验
				PlayerBAC.getInstance().addExp(dbHelper, playerid, content[i][1], gl);
			} else 
			if(type == 6){//月卡
				TqBAC.getInstance().changeTQ(dbHelper, plaRs, playerid, 1, content[i][1], gl);
			} else 
			if(type == 8){//功勋
				addFactionCon += content[i][1];
			} else 
			if(type == 9){//获得伙伴
				PartnerBAC.getInstance().obtainPartner(dbHelper, playerid, content[i][1], itemarr, partnerarr, gl);
			} else 
			if(type == 10){//竞技代币
				addJJCCoin += content[i][1];
			} else 
			if(type == 12){//魂点
				addSoulPoint += content[i][1];
			} else 
			if(type == 14){//塔币
				addTowerCoin += content[i][1];
			} else 
			if(type == 15){//召唤伙伴道具
				addSummonprop +=  content[i][1];
			} else 
			{
				BACException.throwAndPrintInstance("错误的奖励："+(new JSONArray(content[i])));
			}
		}
		for(int i = 0; i <= itemIndex; i++){
			int itemtype = itemContent[i][1];
			int itemnum = itemContent[i][2];
			int itemamount = itemContent[i][3];
			JSONArray extendarr = null;
			if(itemContent[i].length >= 5){
				extendarr = new JSONArray();
				for(int k = 4; k < itemContent[i].length; k++){
					extendarr.add(itemContent[i][k]);
				}
			}
			JSONArray thearr = ItemBAC.getInstance().add(dbHelper, playerid, itemtype, itemnum, itemamount, ItemBAC.ZONE_BAG, shortcut, extendarr, from, gl);
			MyTools.combJsonarr(itemarr, thearr);
		}
		if(addEnergy > 0){
			plaRoleSqlStr.addChange("energy", addEnergy);
		}
		if(addMoney > 0){
			plaSqlStr.addChange("money", addMoney);	
		}
		if(addCoin > 0){
			plaSqlStr.addChange("coin", addCoin);
		}
		if(addFactionCon > 0){
			plaFacSqlStr.addChange("factioncon", addFactionCon);
		}
		if(addJJCCoin > 0){
			plaRoleSqlStr.addChange("jjccoin", addJJCCoin);
		}
		if(addSoulPoint > 0){
			plaRoleSqlStr.addChange("soulpoint", addSoulPoint);
		}
		if(addTowerCoin > 0){
			plaRoleSqlStr.addChange("towercoin", addTowerCoin);
		}
		if(addSummonprop > 0){
			plaSummonSqlStr.addChange("summonprop", addSummonprop);
		}
		if(plaSqlStr.getColCount() > 0){
			gl.addChaNote(GameLog.TYPE_MONEY, plaRs.getInt("money"), addMoney);
			gl.addChaNote(GameLog.TYPE_COIN, plaRs.getInt("coin"), addCoin);
			PlayerBAC.getInstance().update(dbHelper, playerid, plaSqlStr);
		}
		if(plaRoleSqlStr.getColCount() > 0){
			DBPaRs plaroleRs = PlaRoleBAC.getInstance().getDataRs(playerid);
			gl.addChaNote("体力", plaroleRs.getInt("energy"), addEnergy);
			gl.addChaNote("竞技币", plaroleRs.getInt("jjccoin"), addJJCCoin);
			gl.addChaNote("魂点", plaroleRs.getInt("soulpoint"), addSoulPoint);
			gl.addChaNote("塔币", plaroleRs.getInt("towercoin"), addTowerCoin);
			PlaRoleBAC.getInstance().update(dbHelper, playerid, plaRoleSqlStr);
		}
		if(plaFacSqlStr.getColCount() > 0){
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			gl.addChaNote("帮派功勋", plafacRs.getInt("factioncon"), addFactionCon);
			PlaFacBAC.getInstance().update(dbHelper, playerid, plaFacSqlStr);		
		}
		if(plaSummonSqlStr.getColCount() > 0){
			DBPaRs plasumRs = PlaSummonBAC.getInstance().getDataRs(playerid);
			gl.addChaNote("伙伴召唤道具", plasumRs.getInt("summonprop"), addSummonprop);
			PlaSummonBAC.getInstance().update(dbHelper, playerid, plaSummonSqlStr);
		}
		gl.addItemChaNoteArr(itemarr);
		JSONArray returnarr = new JSONArray();
		returnarr.add(itemarr);//物品变化
		returnarr.add(partnerarr);//获得伙伴
		return returnarr;
	}
	
	/**
	 * 奖励推送
	 */
	public void toPush(JSONArray awardarr, PushInterface pushinterface) throws Exception {
		if(awardarr == null){
			return;
		}
		JSONArray itemawardarr = awardarr.optJSONArray(0);//物品奖励返回信息
		for(int i = 0; i < itemawardarr.size(); i++){
			JSONObject itemawardobj = itemawardarr.optJSONObject(i);
			DBPaRs itemRs = ItemBAC.getInstance().getListRs(itemawardobj.optInt("type"), itemawardobj.optInt("num"));//物品数据表信息
			int itemrare = itemRs.getInt("rare");
			pushinterface.push(itemRs.getString("name"), itemrare);
		}
	}
	
	/**
	 * 推送接口
	 */
	public static interface PushInterface {
		public void push(String itemname, int itemrare) throws Exception ;
	}
	
	//--------------静态区--------------
	
	private static AwardBAC instance = new AwardBAC();
	
	/**
	 * 获取实例
	 */
	public static AwardBAC getInstance(){
		return instance;
	}
}
