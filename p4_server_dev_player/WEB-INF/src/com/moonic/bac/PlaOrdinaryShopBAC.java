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
 * 角色普通商店
 * @author wkc
 */
public class PlaOrdinaryShopBAC {
	public static final String tab_ordinary_shop = "tab_ordinary_shop";
	public static final String tab_ordinary_shop_stone = "tab_ordinary_shop_stone";
	public static final String tab_ordinary_shop_time = "tab_ordinary_shop_time";
	public static final String tab_ordinary_shop_refresh = "tab_ordinary_shop_refresh";
	
	
	/**
	 * 初始化数据
	 */
	public void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception {
		JSONArray dataarr = refreshItem(playerid);
		SqlString sqlStr = new SqlString();
		sqlStr.add("item1", dataarr.optJSONArray(0).toString());
		sqlStr.add("buy1", dataarr.optJSONArray(1).toString());
		sqlStr.addDateTime("refreshtime1", MyTools.getTimeStr(getNextRefreshTime()));
		sqlStr.add("times1", 0);
		PlaShopBAC.getInstance().update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 获取普通商店数据
	 */
	public ReturnValue getShopData(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plaOrdRs = PlaShopBAC.getInstance().getDataRs(playerid);
			if(!plaOrdRs.exist()){
				BACException.throwInstance("普通商店尚未开放");
			}
			dbHelper.openConnection();
			JSONArray returnarr = new JSONArray();
			returnarr.add(plaOrdRs.getInt("times1"));
			if(MyTools.checkSysTimeBeyondSqlDate(plaOrdRs.getTime("refreshtime1"))){
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_ORDINARY_SHOP_GETDATA);
				JSONArray refresharr = refresh(dbHelper, playerid, true, gl);
				MyTools.combJsonarr(returnarr, refresharr);
				gl.save();
			} else {
				returnarr.add(new JSONArray(plaOrdRs.getString("item1")));
				returnarr.add(new JSONArray(plaOrdRs.getString("buy1")));
				returnarr.add(plaOrdRs.getTime("refreshtime1"));
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
			DBPaRs plaOrdRs = PlaShopBAC.getInstance().getDataRs(playerid);
			JSONArray buyarr = new JSONArray(plaOrdRs.getString("buy1"));
			if(buyarr.size() == 0){
				BACException.throwInstance("普通商店尚未开放");
			}
			if(index < 0 || index > buyarr.length()-1){
				BACException.throwInstance("index参数错误");
			}
			if(buyarr.optInt(index) == 1){
				BACException.throwInstance("此物品已购买");
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_ORDINARY_SHOP_BUY);
			JSONArray itemarr = (new JSONArray(plaOrdRs.getString("item1"))).optJSONArray(index);
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
			sqlStr.add("buy1", buyarr.toString());
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
	 * 主动刷新商店
	 */
	public ReturnValue refreshShop(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plaOrdRs = PlaShopBAC.getInstance().getDataRs(playerid);
			if(!plaOrdRs.exist()){
				BACException.throwInstance("普通商店尚未开放");
			}
			int times = plaOrdRs.getInt("times1");
			int need = 0;
			DBPsRs conListRs  = DBPool.getInst().pQueryS(tab_ordinary_shop_refresh);
			while(conListRs.next()){
				if(times+1 <= conListRs.getInt("end") || conListRs.getInt("end") == -1) {
					need = conListRs.getInt("need");
					break;
				}
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_ORDINARY_SHOP_REFRESH);
			if(need > 0){
				PlayerBAC.getInstance().useCoin(dbHelper, playerid, need, gl);
			}
			JSONArray returnarr = refresh(dbHelper, playerid, false, gl);
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("times1", 1);
			PlaShopBAC.getInstance().update(dbHelper, playerid, sqlStr);
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
	 * 刷新
	 */
	public JSONArray refresh(DBHelper dbHelper, int playerid, boolean refreshTime, GameLog gl) throws Exception {
		JSONArray refresharr = refreshItem(playerid);
		JSONArray itemarr = refresharr.optJSONArray(0);
		JSONArray buyarr = refresharr.optJSONArray(1);
		JSONArray returnarr = new JSONArray();
		returnarr.add(itemarr);
		returnarr.add(buyarr);
		long nexttime = getNextRefreshTime();
		SqlString sqlStr = new SqlString();
		sqlStr.add("item1", itemarr.toString());
		sqlStr.add("buy1", buyarr.toString());
		if(refreshTime){
			sqlStr.addDateTime("refreshtime1", MyTools.getTimeStr(nexttime));
			returnarr.add(nexttime);
			gl.addRemark("下次刷新时间："+MyTools.getTimeStr(nexttime));
		}
		PlaShopBAC.getInstance().update(dbHelper, playerid, sqlStr);
		gl.addRemark("刷新的物品："+itemarr);
		return returnarr;
	}
	
	/**
	 * 刷新物品
	 */
	public JSONArray refreshItem(int playerid) throws Exception{
		JSONArray itemarr = new JSONArray();//物品数据
		JSONArray buyarr = new JSONArray();//购买数据
		//魂石库
		DBPsRs stoneListRs  = DBPool.getInst().pQueryS(tab_ordinary_shop_stone);
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
		DBPaRs shopListRs = DBPool.getInst().pQueryA(tab_ordinary_shop, "begin<="+plv+" and end>="+plv);
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
		return ShopData.getNextRefreshTime(ShopData.time_ordinary);
	}
	
	//--------静态区--------
	
	private static PlaOrdinaryShopBAC instance = new PlaOrdinaryShopBAC();
	
	public static PlaOrdinaryShopBAC getInstance(){
		return instance;
	}
	
}
