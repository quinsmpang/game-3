package com.moonic.bac;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

/**
 * 角色召唤
 * @author wkc
 */
public class PlaSummonBAC extends PlaBAC{
	public static final String tab_summon_ordinary = "tab_summon_ordinary";
	public static final String tab_summon_advanced = "tab_summon_advanced";
	public static final String tab_summon_mystery = "tab_summon_mystery";
	public static final String tab_summon_consume = "tab_summon_consume";
	public static final String tab_summon_first = "tab_summon_first";
	public static final String tab_summon_ordinary_type = "tab_summon_ordinary_type";
	public static final String tab_summon_advanced_type = "tab_summon_advanced_type";
	
	public static JSONArray mystery_week;//神秘召唤周数据
	public static JSONArray mystery_day;//神秘召唤日数据
	
	/**
	 * 构造
	 */
	public PlaSummonBAC() {
		super("tab_pla_summon", "playerid");
	}
	
	/**
	 * 初始化目标数据
	 */
	public void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("daily1", 0);
		sqlStr.add("total1", 0);
		sqlStr.add("daily2", 0);
		sqlStr.add("single2", 0);
		sqlStr.add("total2", 0);
		sqlStr.add("multi2", 0);
		sqlStr.add("summonprop", 0);
		insert(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 普通召唤
	 */
	public ReturnValue summonOrdinary(int playerid, byte multi){
		DBHelper dbHelper = new DBHelper();
		try {
			int redo = 0;//召唤次数
			String priceCol = null;
			if(multi == 1){
				redo = 1;
				priceCol = "price1";
			} else
			if(multi == 2){
				redo = 10;
				priceCol = "price2";
			} else{
				BACException.throwInstance("次数参数错误"+multi);
			}
			dbHelper.openConnection();
			DBPaRs plaSummonRs = getDataRs(playerid);
			int times = plaSummonRs.getInt("daily1");
			DBPaRs conListRs = getConListRs(1);
			int freeTimes = conListRs.getInt("free");
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_SUMMON_ORDINARY);
			if(!(multi == 1 && times < freeTimes)){//每日单次免费次数
				int[] conarr = Tools.splitStrToIntArr(conListRs.getString(priceCol), ",");
				subValue(dbHelper, playerid, "summonprop", conarr[1], gl, "召唤道具");
			}
			int total = plaSummonRs.getInt("total1");
			StringBuffer remarkSb = new StringBuffer();
			remarkSb.append("第"+(total+1));
			StringBuffer awardSb = new StringBuffer();//奖励字符串
			JSONArray summonarr = new JSONArray();//抽到的内容
			String itemStr = null;
			if(multi == 1 && total == 0){//首次获得指定编号伙伴
				itemStr = getFirstData("frist1");
				awardSb.append(itemStr);
				summonarr.add(itemStr);
				gl.addRemark("首次普通召唤");
				total++;
			} else{
				int random = 0;
				if(multi == 2){
					random = MyTools.getRandom(1, 10);
				}
				for(int i = 1; i <= redo; i++){
					total++;
					String where = null;
					if(total % 50 == 0){//总量第50次特殊处理,获得类型1或2
						where = "itemtype=1 or itemtype=2";
					} else{
						if(i == random){
							where = "itemtype=4";
						} else{
							int type = randomItemType(tab_summon_ordinary_type, "type!=1 and type!=2");
							where = "itemtype="+type;
						}
					}
					DBPsRs ordinaryRs = DBPool.getInst().pQueryS(tab_summon_ordinary, where);
					int[] oddsarr = new int[ordinaryRs.count()];
					while(ordinaryRs.next()){
						int odds = ordinaryRs.getInt("odds");
						int type = ordinaryRs.getInt("itemtype");
						if(type == 2){
							int[] item = Tools.splitStrToIntArr(ordinaryRs.getString("item"), ",");
							DBPsRs partnerRs = PartnerBAC.getInstance().query(playerid, "playerid="+playerid+" num="+item[1]);
							int count = partnerRs.count();
							if(count > 0){
								odds /= 2;
							}
						}
						oddsarr[ordinaryRs.getRow()-1] = odds;
					}
					int index = MyTools.getIndexOfRandom(oddsarr);
					ordinaryRs.setRow(index+1);
					itemStr = ordinaryRs.getString("item");
					if(i > 1){
						awardSb.append("|");
					}
					awardSb.append(itemStr);
					summonarr.add(itemStr);
				}
			}
			SqlString sqlStr = new SqlString();
			if(multi == 1){
				sqlStr.addChange("daily1", 1);
			}
			sqlStr.add("total1", total);
			update(dbHelper, playerid, sqlStr);
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
			PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_CALL_PARTNER, redo, gl);
			
			JSONArray returnarr = new JSONArray();
			returnarr.add(summonarr);//物品内容
			returnarr.add(awardarr);//背包数据
			
			if(multi == 2){
				remarkSb.append("~"+total);
			}
			remarkSb.append("次召唤");
			gl.addRemark(remarkSb);
			gl.addRemark("召唤获得数据"+summonarr.toString());
			
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
	 * 至尊召唤
	 */
	public ReturnValue summonAdvanced(int playerid, byte multi){
		DBHelper dbHelper = new DBHelper();
		try {
			int redo = 0;//召唤次数
			String priceCol = null;
			if(multi == 1){
				redo = 1;
				priceCol = "price1";
			} else
			if(multi == 2){
				redo = 10;
				priceCol = "price2";
			} else{
				BACException.throwInstance("次数参数错误"+multi);
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_SUMMON_ADVANCED);
			DBPaRs conListRs = getConListRs(2);
			int freeTimes = conListRs.getInt("free");
			DBPaRs plaSummonRs = getDataRs(playerid);
			int times = plaSummonRs.getInt("daily2");
			int[] conarr = Tools.splitStrToIntArr(conListRs.getString(priceCol), ",");
			int need = conarr[1];
			if(multi == 1){//单次在免费次数内
				if(times < freeTimes){
					need = 0;
				}
			}
			dbHelper.openConnection();
			if(need > 0){
				PlayerBAC.getInstance().useCoin(dbHelper, playerid, need, gl);
			}
			StringBuffer remarkSb = new StringBuffer();
			int total = plaSummonRs.getInt("total2");//单次总计
			remarkSb.append("第"+(total+1));
			int times10 = plaSummonRs.getInt("multi2");//10连次数总计
			int firstcoin = plaSummonRs.getInt("single2");//是否进行过首次消耗金锭
			StringBuffer awardSb = new StringBuffer();
			boolean isFirstCoin = false;//是否为首次消耗金锭
			JSONArray summonarr = new JSONArray();//抽到的内容
			if(multi == 1 && total == 0){//首次获得指定编号伙伴
				String first = getFirstData("frist2");
				awardSb.append(first);//获得指定编号伙伴
				summonarr.add(first);
				total++;
			} else
			if(multi == 1 && need > 0 && times == freeTimes && firstcoin == 0){
				String first = getFirstData("frist3");
				awardSb.append(first);//获得指定编号伙伴
				summonarr.add(first);
				isFirstCoin = true;
				total++;
			} else
			{
				boolean isFirst10 = false;//是否是首次10连
				if(multi == 2){
					if(times10 == 0){
						isFirst10 = true;
						if(firstcoin == 0){
							isFirstCoin = true;
						}
					}
				}
				int[] arr = new int[redo];
				for(int i = 1; i <= redo; i++){
					arr[i-1] = i;
				}
				int maxRanAm = 2;//随机出现的最大伙伴次数
				int ranAm = 0;
				if(isFirst10){
					ranAm = 1;
					maxRanAm = 1;
				}
				if(isFirstCoin){
					ranAm = 2;
					maxRanAm = 0;
				}
				int[] randomArr = null;
				if(ranAm > 0){
					randomArr = getDiffRandom(arr, ranAm);
				}
				int partnerAm = 0;//出现伙伴个数
				JSONArray parterArr = new JSONArray();
				for(int i = 1; i <= redo; i++){
					total++;
					String itemStr = null;
					if(randomArr != null && Tools.contain(randomArr, i)){
						if(MyTools.getIndexByInt(randomArr, i) == 0){
							itemStr = getFirstData("frist4");
						} else{
							itemStr = getFirstData("frist3");
						}
					} else{
						String where = null;
						if(total % 10 == 0){//10次必得伙伴
							if(multi == 1){
								where = "itemtype=1 or itemtype=2";
							} else{
								if(!isFirst10){
									where = "itemtype=2 or itemtype=3";
								}
							}
						} else{
							int type = 0;
							if(partnerAm >= maxRanAm){//出现伙伴的数量最多为2
								type = randomItemType(tab_summon_advanced_type, "type!=1 and type!=2 and type!=3");
							} else{
								type = randomItemType(tab_summon_advanced_type, null);
							}
							where = "itemtype="+type;
						}
						DBPsRs advancedRs = DBPool.getInst().pQueryS(tab_summon_advanced, where);
						int[] oddsarr = new int[advancedRs.count()];
						while(advancedRs.next()){
							int itemtype = advancedRs.getInt("itemtype");
							int odds = advancedRs.getInt("odds");
							int[] item = Tools.splitStrToIntArr(advancedRs.getString("item"), ",");
							if(itemtype == 3){//已获得的3星几率为减半
								DBPsRs partnerRs = PartnerBAC.getInstance().query(playerid, "playerid="+playerid+" num="+item[1]);
								int count = partnerRs.count();
								if(count > 0){
									odds /= 2;
								}
							}
							if(itemtype == 2){//已获得的2星几率为减半
								DBPsRs partnerRs = PartnerBAC.getInstance().query(playerid, "playerid="+playerid+" num="+item[1]);
								int count = partnerRs.count();
								if(count > 0){
									odds /= 2;
								}
							}
							if((itemtype == 1 || itemtype == 2 || itemtype == 3) && parterArr.contains(item[1])){
								odds = 1/2;
							}
							oddsarr[advancedRs.getRow()-1] = odds;
						}
						int index = MyTools.getIndexOfRandom(oddsarr);
						advancedRs.setRow(index+1);
						itemStr = advancedRs.getString("item");
					}
					if(i > 1){
						awardSb.append("|");
					}
					int[] itemarr = Tools.splitStrToIntArr(itemStr, ",");
					if(itemarr[0] == 9){
						partnerAm++;
						parterArr.add(itemarr[1]);
					} 
					awardSb.append(itemStr);
					summonarr.add(itemStr);
				}
			}
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
			SqlString sqlStr = new SqlString();
			sqlStr.add("total2", total);
			if(multi == 1){
				sqlStr.addChange("daily2", 1);
			} else{
				sqlStr.addChange("multi2", 1);
			}
			if(isFirstCoin == true){
				sqlStr.add("single2", 1);
			}
			update(dbHelper, playerid, sqlStr);
			PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_CALL_PARTNER, redo, gl);
			JSONArray returnarr = new JSONArray();
			returnarr.add(summonarr);//抽的内容
			returnarr.add(awardarr);//处理后的奖励数据
			
			if(multi == 2){
				remarkSb.append("~"+total);
			}
			remarkSb.append("次召唤");
			gl.addRemark(remarkSb);
			gl.addRemark("召唤获得数据"+summonarr.toString());
			
			gl.save();
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 神秘召唤
	 */
	public ReturnValue summonMystery(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs conListRs = getConListRs(4);
			int[] conarr = Tools.splitStrToIntArr(conListRs.getString("price1"), ",");
			int need = conarr[1];
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_SUMMON_MYSTERY);
			if(need > 0){
				PlayerBAC.getInstance().useCoin(dbHelper, playerid, need, gl);
			}
			JSONArray mysteryArr = new JSONArray();
			for(int i = 0; i < mystery_day.length(); i++){
				mysteryArr.add(mystery_day.getJSONArray(i));
			}
			for(int i = 0; i < mystery_week.length(); i++){
				mysteryArr.add(mystery_week.getJSONArray(i));
			}
			int[] oddsarr = new int[mysteryArr.length()];
			for(int i = 0; i < mysteryArr.length(); i++){
				JSONArray item = mysteryArr.optJSONArray(i);
				oddsarr[i] = item.optInt(2);
			}
			int index = MyTools.getIndexOfRandom(oddsarr);
			JSONArray item = mysteryArr.optJSONArray(index);
			int random = MyTools.getRandom(1, 100);
			StringBuffer awardSb = new StringBuffer();
			if(random < item.optInt(3)){//出伙伴
				awardSb.append("9,");
				awardSb.append(item.optInt(0));
			} else{
				awardSb.append("1,7,");
				awardSb.append(item.optInt(1));
				awardSb.append(",");
				awardSb.append(MyTools.getRandom(item.optInt(4), item.optInt(5)));
			}
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
			PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_CALL_PARTNER, gl);
			
			JSONArray returnarr = new JSONArray();
			returnarr.add(awardSb.toString());//奖励内容
			returnarr.add(awardarr);//加背包数据
			gl.save();
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 创建神秘召唤日物品
	 * @param type 1:周物品，2:日物品
	 * @param amount 随机物品数量，周物品1一个，日物品3个
	 */
	public JSONArray createMysteryItem(byte type, int amount) throws Exception {
		JSONArray jsonarr = new JSONArray();
		if(type == 2){//日物品指定伙伴
			String paramStr = CustomActivityBAC.getInstance().getFuncActiPara(CustomActivityBAC.TYPE_SUMMON_PARTNER_COERCE);
			if(paramStr != null){
				int param = Tools.str2int(paramStr);
				DBPaRs partnerRs = DBPool.getInst().pQueryA(tab_summon_mystery, "partner="+param);
				if(!partnerRs.exist()){
					BACException.throwInstance("伙伴编号不存在"+param);
				}
				JSONArray item = new JSONArray();
				item.add(param);//伙伴编号
				item.add(partnerRs.getInt("soul"));//对应魂石编号
				item.add(partnerRs.getInt("hit"));//抽中几率
				item.add(partnerRs.getInt("odds"));//出成品概率
				int[] amarr = Tools.splitStrToIntArr(partnerRs.getString("amount"), ",");
				item.add(amarr[0]);//魂石数量最小值
				item.add(amarr[1]);//魂石数量最大值
				jsonarr.add(item);
				amount -= 1;
			} 
		}
		DBPsRs mysteryRs = DBPool.getInst().pQueryS(tab_summon_mystery, "cycle="+type);
		int[] oddsarr = new int[mysteryRs.count()];
		while(mysteryRs.next()){
			oddsarr[mysteryRs.getRow()-1] = mysteryRs.getInt("show");
		}
		for(int i = 0; i < amount; i++){
			int index = MyTools.getIndexOfRandom(oddsarr);
			mysteryRs.setRow(index+1);
			JSONArray item = new JSONArray();
			item.add(mysteryRs.getInt("partner"));//伙伴编号
			item.add(mysteryRs.getInt("soul"));//对应魂石编号
			item.add(mysteryRs.getInt("hit"));//抽中几率
			item.add(mysteryRs.getInt("odds"));//出成品概率
			int[] amarr = Tools.splitStrToIntArr(mysteryRs.getString("amount"), ",");
			item.add(amarr[0]);//魂石数量最小值
			item.add(amarr[1]);//魂石数量最大值
			jsonarr.add(item);
			oddsarr[index] = 0;
		}
		return jsonarr;
	}

	/**
	 * 根据消耗编号获取消耗数据
	 */
	public DBPaRs getConListRs(int num) throws Exception {
		DBPaRs conListRs = DBPool.getInst().pQueryA(tab_summon_consume, "num="+num);
		if(!conListRs.exist()){
			BACException.throwInstance("消耗编号不存在");
		}
		return conListRs;
	}
	
	/**
	 * 获取首次特殊处理数据
	 */
	public String getFirstData(String column) throws Exception {
		DBPsRs firstListRs = DBPool.getInst().pQueryS(tab_summon_first);
		firstListRs.next();
		return firstListRs.getString(column);
	}
	
	/**
	 * 从数组中获取两个不同的随机数
	 */
	public int[] getDiffRandom(int[] arr, int amount){
		int[] ranarr = new int[amount];
		for(int i = 0; i < amount; i++){
			int ranInd = MyTools.getRandom(0, arr.length-1);
			ranarr[i] = arr[ranInd];
			arr = Tools.removeOneFromIntArr(arr, ranInd);
		}
		return ranarr;
	}
	
	/**
	 * 随机物品类型
	 * @throws Exception 
	 */
	public int randomItemType(String tab, String where) throws Exception{
		DBPsRs typeRs = DBPool.getInst().pQueryS(tab, where);
		int[] oddsarr = new int[typeRs.count()];
		while(typeRs.next()){
			oddsarr[typeRs.getRow()-1] = typeRs.getInt("odds");
		}
		int index = MyTools.getIndexOfRandom(oddsarr);
		typeRs.setRow(index+1);
		return typeRs.getInt("type");
	}
	
	
	/**
	 * 重置每日数据
	 */
	public void resetData(DBHelper dbHelper, int playerid) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("daily1", 0);
		sqlStr.add("daily2", 0);
		update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 获取数据
	 */
	public JSONArray getData(int playerid) throws Exception {
		DBPaRs plaSummonRs = getDataRs(playerid);
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(mystery_day);//每日神秘召唤物品
		jsonarr.add(mystery_week);//每周神秘召唤物品
		jsonarr.add(plaSummonRs.getInt("daily1"));//每日普通单次召唤次数
		jsonarr.add(plaSummonRs.getInt("daily2"));//每日至尊单次召唤次数
		jsonarr.add(plaSummonRs.getInt("total1"));//普通召唤总次数
		jsonarr.add(plaSummonRs.getInt("total2"));//至尊召唤总次数
		jsonarr.add(plaSummonRs.getInt("summonprop"));//召唤道具数量
		return jsonarr;
	}
	
	//--------------静态区--------------
	
	private static PlaSummonBAC instance = new PlaSummonBAC();

	/**
	 * 获取实例
	 */
	public static PlaSummonBAC getInstance() {
		return instance;
	}
	
	//--------------调试区--------------
	
	/**
	 * 重置召唤记录
	 */
	public ReturnValue debugResetSummon(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.add("daily1", 0);
			sqlStr.add("total1", 0);
			sqlStr.add("daily2", 0);
			sqlStr.add("single2", 0);
			sqlStr.add("total2", 0);
			sqlStr.add("multi2", 0);
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 增加召唤道具
	 */
	public ReturnValue debugAddSummonprop(int playerid, int amount){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("summonprop", amount);
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
}
