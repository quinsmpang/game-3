package com.moonic.bac;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.txtdata.ShopData;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

/**
 * 角色神秘商店
 * @author wkc
 */
public class PlaMysteryShopBAC {
	public static final String tab_mystery_shop = "tab_mystery_shop";
	public static final String tab_mystery_shop_stone = "tab_mystery_shop_stone";
	public static final String tab_mystery_shop_time = "tab_mystery_shop_time";
	
	/**
	 * 初始化数据
	 */
	public void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception {
		SqlString sqlStr = new SqlString();
		JSONArray itemarr = new JSONArray();
		JSONArray buyarr = new JSONArray();
		int viplv = PlayerBAC.getInstance().getIntValue(playerid, "vip");
		boolean isOpen = VipBAC.getInstance().checkVipFuncOpen(viplv, 5);
		if(isOpen){
			JSONArray dataarr = refreshItem(playerid);
			itemarr = dataarr.optJSONArray(0);
			buyarr = dataarr.optJSONArray(1);
			sqlStr.addDateTime("refreshtime2", MyTools.getTimeStr(getNextRefreshTime()));
		} 
		sqlStr.add("item2", itemarr.toString());
		sqlStr.add("buy2", buyarr.toString());
		PlaShopBAC.getInstance().update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 获取神秘商店数据
	 */
	public ReturnValue getShopData(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plaMysRs = PlaShopBAC.getInstance().getDataRs(playerid);
			if(!plaMysRs.exist()){
				BACException.throwInstance("神秘商店尚未开放");
			}
			dbHelper.openConnection();
			JSONArray returnarr = null;
			int viplv = PlayerBAC.getInstance().getIntValue(playerid, "vip");
			boolean isOpen = VipBAC.getInstance().checkVipFuncOpen(viplv, 5);
			if (!isOpen) {
				BACException.throwInstance("VIP等级不满足条件");
			}
			long refreshTime = plaMysRs.getTime("refreshtime2");
			if(refreshTime == 0 || MyTools.checkSysTimeBeyondSqlDate(refreshTime)){
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_MYSTERY_SHOP_GETDATA);
				returnarr = refresh(dbHelper, playerid, gl);
				gl.save();
			} else {
				returnarr = new JSONArray();
				returnarr.add(new JSONArray(plaMysRs.getString("item2")));
				returnarr.add(new JSONArray(plaMysRs.getString("buy2")));
				returnarr.add(plaMysRs.getTime("refreshtime2"));
			}
			
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 购买物品
	 */
	public ReturnValue buy(int playerid, int index){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plaMysRs = PlaShopBAC.getInstance().getDataRs(playerid);
			if(!plaMysRs.exist()){
				BACException.throwInstance("神秘商店尚未开放");
			}
			int viplv = PlayerBAC.getInstance().getIntValue(playerid, "vip");
			boolean isOpen = VipBAC.getInstance().checkVipFuncOpen(viplv, 5);
			if(!isOpen){
				BACException.throwInstance("VIP等级不满足条件");
			}
			JSONArray buyarr = new JSONArray(plaMysRs.getString("buy2"));
			if(index < 0 || index > buyarr.length()-1){
				BACException.throwInstance("index参数错误");
			}
			if(buyarr.optInt(index) == 1){
				BACException.throwInstance("此物品已购买");
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_MYSTERY_SHOP_BUY);
			JSONArray itemarr = (new JSONArray(plaMysRs.getString("item2"))).optJSONArray(index);
			int[] consume = Tools.splitStrToIntArr(itemarr.optString(1), ",");
			int consume_type = consume[0];
			int consume_amount = consume[1];
			if(consume_type == 3){
				PlayerBAC.getInstance().useMoney(dbHelper, playerid, consume_amount, gl);
			} else 
			if(consume_type == 4){
				PlayerBAC.getInstance().useCoin(dbHelper, playerid, consume_amount, gl);
			} 
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, itemarr.optString(0), ItemBAC.SHORTCUT_MAIL, 39, gl);
			buyarr.put(index, 1);
			SqlString sqlStr = new SqlString();
			sqlStr.add("buy2", buyarr.toString());
			PlaShopBAC.getInstance().update(dbHelper, playerid, sqlStr);
			
			gl.save();
			return new ReturnValue(true, awardarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 刷新
	 */
	public JSONArray refresh(DBHelper dbHelper, int playerid, GameLog gl) throws Exception {
		JSONArray refresharr = refreshItem(playerid);
		JSONArray itemarr = refresharr.optJSONArray(0);
		JSONArray buyarr = refresharr.optJSONArray(1);
		long nexttime = getNextRefreshTime();
		SqlString sqlStr = new SqlString();
		sqlStr.add("item2", itemarr.toString());
		sqlStr.add("buy2", buyarr.toString());
		sqlStr.addDateTime("refreshtime2", MyTools.getTimeStr(nexttime));
		PlaShopBAC.getInstance().update(dbHelper, playerid, sqlStr);
		gl.addRemark("刷新的物品："+itemarr);
		gl.addRemark("下次刷新时间："+MyTools.getTimeStr(nexttime));
		JSONArray returnarr = new JSONArray();
		returnarr.add(itemarr);
		returnarr.add(buyarr);
		returnarr.add(nexttime);
		return returnarr;
	}
	
	/**
	 * 刷新物品
	 */
	public JSONArray refreshItem(int playerid) throws Exception{
		JSONArray itemarr = new JSONArray();//物品数据
		JSONArray buyarr = new JSONArray();//购买数据
		//魂石库
		DBPsRs stoneListRs  = DBPool.getInst().pQueryS(tab_mystery_shop_stone);
		int[] odds = new int[stoneListRs.count()];
		while(stoneListRs.next()){
			odds[stoneListRs.getRow()-1] = stoneListRs.getInt("odds");
		}
		int index = MyTools.getIndexOfRandom(odds);
		stoneListRs.setRow(index+1);
		int[] split = Tools.splitStrToIntArr(stoneListRs.getString("item"), ",");
		String item = Tools.combineInt((int[])ItemBAC.getInstance().enterItem(split, "lotteryodds")[0], ",");
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(item);//物品信息
		jsonarr.add(stoneListRs.getString("price"));//价格信息
		itemarr.add(jsonarr);
		buyarr.add(0);
		//库1库2
		int plv = PlayerBAC.getInstance().getIntValue(playerid, "lv");
		DBPaRs shopListRs = DBPool.getInst().pQueryA(tab_mystery_shop, "begin<="+plv+" and end>="+plv);
		for(int i = 1; i <= 2; i++){
			String[] item1 = Tools.splitStr(shopListRs.getString("store"+i), "|");
			String[] price = Tools.splitStr(shopListRs.getString("price"+i), "|");
			int[] odds1 = Tools.splitStrToIntArr(shopListRs.getString("odds"+i), ",");
			int amount = shopListRs.getInt("amount"+i);
			String oddsCol = "shopcoinodds";
			String priceCol = "shopcoinprice";
			String priceType = "4,";
			if(i == 2){
				oddsCol = "shopmoneyodds";
				priceCol = "shopmoneyprice";
				priceType = "3,";
			}
			for(int j = 0; j < amount; j++){
				int index1 = MyTools.getIndexOfRandom(odds1);
				int[] split1 = Tools.splitStrToIntArr(item1[index1], ",");
				Object[] enter = ItemBAC.getInstance().enterItem(split1, oddsCol);
				String itemStr = Tools.combineInt((int[])enter[0], ",");
				String priceStr = price[index1];
				if(priceStr.equals("0")){
					StringBuffer priceSb = new StringBuffer();
					priceSb.append(priceType);
					priceSb.append(((DBPsRs)enter[1]).getInt(priceCol));
					priceStr = priceSb.toString();
				}
				JSONArray jsonarr1 = new JSONArray();
				jsonarr1.add(itemStr);//物品信息
				jsonarr1.add(priceStr);//价格信息
				itemarr.add(jsonarr1);
				buyarr.add(0);
				odds1[index1] = 0;
			}
		}
		JSONArray returnarr = new JSONArray();
		returnarr.add(itemarr);
		returnarr.add(buyarr);
		return returnarr;
	}
	
	/**
	 * 获取下次刷新时间
	 */
	public long getNextRefreshTime() throws Exception{
		return ShopData.getNextRefreshTime(ShopData.time_mystery);
	}
	
	
	/**
	 * 处理累计消耗体力
	 */
	/*
	public void handleEnergyConsume(DBHelper dbHelper, int playerid, int amount) throws Exception {
		DBPaRs plaMysRs = PlaShopBAC.getInstance().getDataRs(playerid);
		int oldConsume = plaMysRs.getInt("conenergy");
		if(oldConsume != -1){
			int viplv = PlayerBAC.getInstance().getIntValue(playerid, "vip");
			boolean isOpen = VipBAC.getInstance().checkVipFuncOpen(viplv, 5);
			if(!isOpen){//没有永久开启的情况下累计消耗体力
				int needConsume = VipBAC.getInstance().getVipFuncData(viplv, 4);
				SqlString sqlStr = new SqlString();
				if(amount + oldConsume >= needConsume){
					long current = System.currentTimeMillis();
					//在每日4~5点之间不出现
					if(!(current >= MyTools.getCurrentDateLong() + MyTools.long_hour*4 && current <= MyTools.getCurrentDateLong() + MyTools.long_hour*5)){
						long endtime = plaMysRs.getTime("endtime");
						//每天只出现一次
						if(endtime == 0 || current > MyTools.getCurrentDateLong(endtime) + MyTools.long_day + MyTools.long_hour*5){
							JSONArray refresharr = refreshItem(playerid);
							JSONArray itemarr = refresharr.optJSONArray(0);
							JSONArray buyarr = refresharr.optJSONArray(1);
							sqlStr.add("item2", itemarr.toString());
							sqlStr.add("buy2", buyarr.toString());
							sqlStr.addDateTime("endtime", MyTools.getTimeStr(current+MyTools.long_hour));
							int conenergy = oldConsume + amount - needConsume;
							sqlStr.add("conenergy", conenergy);
							GameLog.getInst(playerid, GameServlet.ACT_MYSTERY_SHOP_CONENERGY_REFRESH)
							.addRemark("累计消耗体力刷新物品："+itemarr)
							.addRemark("剩余累计积分："+conenergy)
							.save();
						} else{
							sqlStr.addChange("conenergy", amount);
						}
					}
				} else{
					sqlStr.addChange("conenergy", amount);
				}
				if(sqlStr.getColCount() > 0){
					PlaShopBAC.getInstance().update(dbHelper, playerid, sqlStr);
				}
			}
		}
	}
	*/
	
	
	/**
	 * 获取数据
	 */
	/*
	public JSONArray getData(int playerid) throws Exception {
		JSONArray jsonarr = new JSONArray();
		DBPaRs plaMysRs = PlaShopBAC.getInstance().getDataRs(playerid);
		if(plaMysRs.exist()){
			jsonarr.add(plaMysRs.getInt("conenergy"));//累计消耗的体力
			jsonarr.add(plaMysRs.getTime("endtime"));//购买结束时间
		}
		return jsonarr;
	}
	*/
	
	//--------静态区--------
	
	private static PlaMysteryShopBAC instance = new PlaMysteryShopBAC();
	
	public static PlaMysteryShopBAC getInstance(){
		return instance;
	}
	
}
