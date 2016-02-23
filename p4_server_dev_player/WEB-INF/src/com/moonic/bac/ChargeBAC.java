package com.moonic.bac;

import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.GamePushData;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 充值
 * @author John
 */
public class ChargeBAC {
	public static final String tab_channel_charge_type = "tab_channel_charge_type";
	public static final String tab_charge_type = "tab_charge_type";
	public static final String tab_charge = "tab_charge";
	
	public static final byte FROM_CLIENT = 1; //客户端购买
	public static final byte FROM_WEB = 2; //网站购买
	public static final byte FROM_ORDERGIVE = 3; //单子补发货
	public static final byte FROM_CONSOLE = 4; //控制中心发放
	
	public static final String[] from_str = {"客户端", "网站", "补单", "后台"};
	
	public String getFromStr(byte from){
		if(from <= from_str.length){
			return from_str[from-1];
		} else {
			return "未知("+from+")";
		}
	}
	
	/**
	 * 可自定义推送act的充值
	 * @param from 来源 1客户端购买 2网站购买 3补单
	 */
	public ReturnValue recharge(int playerid, int rechargetype, int rmbam, byte result, String resultnote, byte from, String channel, byte chargepoint, String centerOrderNo) {
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs chargetypeRs = DBPool.getInst().pQueryA(tab_charge_type, "num="+rechargetype);
			String ctStr = null;
			if(chargetypeRs.exist()){
				ctStr = chargetypeRs.getString("name");
			} else {
				ctStr = "未知("+rechargetype+")";
			}
			if(result == 0){
				GameLog.getInst(playerid, GameServlet.ACT_PLAYER_RECHARGE)
				.addRemark("充值失败，充值类型："+ctStr+"，失败信息："+resultnote+"，来自："+getFromStr(from))
				.save();
				if(from==FROM_CLIENT)
				{
					JSONObject theobj = new JSONObject();
					theobj.put("result", result);
					theobj.put("note", resultnote);
					theobj.put("from", from);
					PushData.getInstance().sendPlaToOne(SocketServer.ACT_PLAYER_RECHARGE, theobj.toString(), playerid);
				}
				return new ReturnValue(true);
			}
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			int oldcoin = plaRs.getInt("coin");
			JSONArray rechargetypesarr = new JSONArray(plaRs.getString("rechargtypes"));
			DBPsRs chargeRs = DBPool.getInst().pQueryS(tab_charge, "rmb<="+rmbam, "rmb desc");
			boolean isfirst = plaRs.getString("firstrechargetime") == null;
			int buycoin = rmbam * 10; //充值获得的金锭
			int rebatecoin = 0; //额外送的金锭
			if(chargeRs.next()){
				rebatecoin = chargeRs.getInt("rebatecoin");
				if(!rechargetypesarr.contains(rmbam)){
					rechargetypesarr.add(rmbam);
				}
			} else {
				System.out.println("未找到充值金额，无对应赠送金锭数 rmb="+rmbam);
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLAYER_RECHARGE);
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("coin", buycoin+rebatecoin);
			sqlStr.addChange("rebatecoin", rebatecoin);
			sqlStr.addChange("rechargermb", rmbam);
			sqlStr.addChange("rechargeam", 1);
			sqlStr.add("rechargtypes", rechargetypesarr.toString());
			if(isfirst){
				sqlStr.addDateTime("firstrechargetime", MyTools.getTimeStr());
			}
			int[] vipdata = VipBAC.getInstance().addChangeVIPToSqlStr(plaRs, buycoin, sqlStr, gl);
			PlayerBAC.getInstance().update(dbHelper, playerid, sqlStr);
			
			gl.addChaNote(GameLog.TYPE_COIN, oldcoin, buycoin+rebatecoin)
			.addRemark("充值成功，充值类型："+ctStr+"，购买金锭："+buycoin+"，获赠金锭："+rebatecoin+"，来自："+getFromStr(from))
			.setCenterOrderNo(centerOrderNo)
			.save();
			
			JSONObject buyInfo = new JSONObject();
			buyInfo.put("result", result);
			buyInfo.put("buytype", 1); //buytype=1表示购买金锭，其他特权用2之后的数值表示
			buyInfo.put("givecoin", buycoin+rebatecoin); //充值的金锭数量
			String note="您已成功充值"+rmbam+"元,获得"+buycoin+"金锭";
			if(rebatecoin>0)
			{
				note += ",并额外获得"+rebatecoin+"金锭";
			}
			if(vipdata[1] > vipdata[0])
			{
				note += ",恭喜您的VIP等级从"+vipdata[0]+"级升级到了"+vipdata[1]+"级！";
			}			
			buyInfo.put("note", note); //充值描述
			buyInfo.put("viplevel", vipdata[1]); //充值后的vip等级
			buyInfo.put("vipexp", vipdata[2]); //充值后的vip经验值
			buyInfo.put("rechargermb", rmbam);
			buyInfo.put("from", from); //充值来源
			LogBAC.logout("charge/"+channel, "购买推送push_act="+SocketServer.ACT_PLAYER_RECHARGE+",buyInfo="+buyInfo.toString()+"playerid="+playerid);
			PushData.getInstance().sendPlaToOne(SocketServer.ACT_PLAYER_RECHARGE, buyInfo.toString(), playerid);
			
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 可自定义推送act的购买特权
	 * @param push_act 自定义推送act
	 */
	public ReturnValue buyTQ(int playerid, byte tqnum, byte result, String resultnote, byte from, String channel, String centerOrderNo) {
		DBHelper dbHelper = new DBHelper();
		try {
			if(result == 0){
				GameLog.getInst(playerid, GameServlet.ACT_PLAYER_BUY_TQ)
				.addRemark("购买特权失败，特权编号："+tqnum+"，失败信息："+resultnote+"，来自："+getFromStr(from))
				.save();
				if(from == FROM_CLIENT)
				{
					JSONObject theobj = new JSONObject();
					theobj.put("result", result);
					theobj.put("note", resultnote);
					theobj.put("from", from);
					PushData.getInstance().sendPlaToOne(SocketServer.ACT_PLAYER_BUY_TQ, theobj.toString(), playerid);
				}
				return new ReturnValue(true);
			}
			DBPaRs tqRs = DBPool.getInst().pQueryA(TqBAC.tab_prerogative, "num="+tqnum);
			if(!tqRs.exist() || tqnum==0){
				BACException.throwInstance("错误的特权编号："+tqnum);
			}
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLAYER_BUY_TQ);
			int oldcoin = plaRs.getInt("coin");
			int rebatecoin = tqRs.getInt("rebatecoin");
			SqlString plaSqlStr = new SqlString();
			TqBAC.getInstance().addChangeTQToSqlStr(plaRs, tqRs.getInt("num"), tqRs.getInt("days"), plaSqlStr, gl);
			plaSqlStr.addChange("rechargermb", tqRs.getInt("price"));
			plaSqlStr.addChange("rechargeam", 1);
			if(plaRs.getString("firstrechargetime") == null){
				plaSqlStr.addDateTime("firstrechargetime", MyTools.getTimeStr());
			}
			plaSqlStr.addChange("coin", rebatecoin);
			plaSqlStr.addChange("rebatecoin", rebatecoin);
			PlayerBAC.getInstance().update(dbHelper, playerid, plaSqlStr);
			
			gl.addChaNote(GameLog.TYPE_COIN, oldcoin, rebatecoin)
			.addRemark("购买特权成功，特权编号："+tqnum+"，来自："+getFromStr(from))
			.save();
			
			plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			JSONObject buyInfo = new JSONObject();
			buyInfo.put("result", result);
			buyInfo.put("buytype", 2);
			buyInfo.put("num", plaRs.getInt("tqnum"));
			buyInfo.put("tqduetime", plaRs.getTime("tqduetime"));
			buyInfo.put("rechargermb", tqRs.getInt("price"));
			buyInfo.put("givecoin", rebatecoin);
			buyInfo.put("from", from); //充值来源
			LogBAC.logout("charge/"+channel, "购买推送push_act="+SocketServer.ACT_PLAYER_BUY_TQ+",buyInfo="+buyInfo.toString()+"playerid="+playerid);
			PushData.getInstance().sendPlaToOne(SocketServer.ACT_PLAYER_BUY_TQ, buyInfo.toString(), playerid);
			
			int userid = 0;
			String username = null;
			String platformname = null;
			String ip = null;
			ResultSet userRs = dbHelper.query("tab_user", "pookid,username,platform,ip", "id="+plaRs.getInt("id"));
			if(userRs.next()){
				userid = userRs.getInt("pookid");
				username = userRs.getString("username");
				DBPaRs platformRs = DBPool.getInst().pQueryA("tab_platform", "code='"+userRs.getString("platform")+"'");
				platformname = platformRs.getString("name");
				ip = userRs.getString("ip");
			}
			DBPaRs channelServerRs = DBPool.getInst().pQueryA(ServerBAC.tab_channel_server, "vsid="+plaRs.getInt("vsid"));
			String gamename = "刀塔传奇-"+channelServerRs.getString("servername");
			
			SqlString incomeSqlStr = new SqlString();
			incomeSqlStr.add("user_id", userid);
			incomeSqlStr.add("user_name", username);
			incomeSqlStr.add("agent_name", platformname);
			incomeSqlStr.add("prop_type", 0);
			incomeSqlStr.add("game_name", gamename);
			incomeSqlStr.add("prop_name", tqRs.getString("name"));
			incomeSqlStr.add("buy_amount", 1);
			incomeSqlStr.add("money_type", 1);
			incomeSqlStr.add("pay_money", tqRs.getInt("price"));
			incomeSqlStr.addDateTime("pay_time", MyTools.getTimeStr());
			incomeSqlStr.addDateTime("effective_time", MyTools.getTimeStr(System.currentTimeMillis()+tqRs.getInt("days")*MyTools.long_day));
			incomeSqlStr.add("effective_day", tqRs.getInt("days"));
			incomeSqlStr.add("present_info", "购买特权");
			incomeSqlStr.add("client_ip", ip);
			incomeSqlStr.add("platform", "移动");
			incomeSqlStr.add("related_num", centerOrderNo);
			incomeSqlStr.add("company_id", "世熠");
			incomeSqlStr.addDateTime("createtime", MyTools.getTimeStr());
			dbHelper.insert(GameLog.tab_income_prop_log, incomeSqlStr);
			
			GamePushData.getInstance(14)
			.add(plaRs.getString("name"))
			.add(tqRs.getString("name"))
			.add(tqRs.getString("func1").split("\\|")[0].replace("3,", ""))
			.sendToAllOL();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * @param from 1 客户端购买 2网站购买 3 用户服补单
	 */
	public ReturnValue orderBatchGive(JSONObject json, byte from, byte chargepoint) {
		JSONArray array = json.optJSONArray("list");
		for(int i=0;array!=null && i<array.length();i++) {
			JSONObject line = array.optJSONObject(i);
			orderReGive(line, from, chargepoint);
		}
		return new ReturnValue(true);
	}
	
	/**
	 * @param from 1 客户端购买 2网站购买 3 用户服补单 
	 */
	public ReturnValue orderReGive(JSONObject json, byte from, byte chargepoint) {
		String orderNo = json.optString("orderno");
		String channel = json.optString("channel");
		int orderResult = json.optInt("result");
		int buytype = json.optInt("buytype");
		int gived = json.optInt("gived");
		int playerId = json.optInt("playerId");
		int orderType = json.optInt("orderType");
		int price = json.optInt("price");
		int getpower= json.optInt("getpower");
		String note = json.optString("note");
		String centerOrderNo = json.optString("corderno");
		if(orderResult==1 || orderResult==-1) {
			if(gived==0 || ((gived==-1 || gived==2) && from==FROM_ORDERGIVE)) {//不是已发货状态或后台补单
				ReturnValue rv=null;
				//重新发货
				if(buytype==1)
				{
					if(from==FROM_ORDERGIVE) //用户服补单
					{
						rv = recharge(playerId, (byte)orderType, price, (byte)(orderResult==1?1:0), note, from, channel, chargepoint, centerOrderNo);	
					}
					else
					if(from==FROM_WEB) //网站购买
					{
						rv = recharge(playerId, (byte)orderType, price, (byte)(orderResult==1?1:0), note, from, channel, chargepoint, centerOrderNo);
					}
					else //客户端购买
					{
						rv = recharge(playerId, (byte)orderType, price, (byte)(orderResult==1?1:0), note, from, channel, chargepoint, centerOrderNo);
					}
				}
				else
				if(buytype==2)
				{
					if(from==FROM_ORDERGIVE) //用户服补单
					{
						rv = buyTQ(playerId, (byte)getpower, (byte)(orderResult==1?1:0), note, from,channel, centerOrderNo);
					}
					else
					if(from==FROM_WEB) //网站购买
					{
						rv = buyTQ(playerId, (byte)getpower, (byte)(orderResult==1?1:0), note, from,channel, centerOrderNo);
					}
					else //客户端购买
					{
						rv = buyTQ(playerId, (byte)getpower, (byte)(orderResult==1?1:0), note, from,channel, centerOrderNo);
					}					
				}
				else
				{
					return new ReturnValue(false,"购买类型不正确buytype="+buytype);
				}
				//日志
				if(from==FROM_ORDERGIVE) //用户服补单
				{
					LogBAC.logout("charge/"+channel,"用户服补单"+orderNo+"发货结果="+rv.success);
				}
				else
				if(from==FROM_WEB) //网站购买				
				{
					LogBAC.logout("charge/"+channel,"网站购买"+orderNo+"发货结果="+rv.success);
				}
				else
				if(from==FROM_CLIENT) //客户端购买
				{
					LogBAC.logout("charge/"+channel,"客户端购买"+orderNo+"发货结果="+rv.success);	
				}
				
				if(orderResult==1)
				{
					try
					{
						if(rv.success)
						{
							ChargeSendBAC.getInstance().createSendOrder(Conf.sid, channel, orderNo, 1);	
						}
						else
						{
							ChargeSendBAC.getInstance().createSendOrder(Conf.sid, channel, orderNo, -1);	
						}
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						return new ReturnValue(false,ex.toString());
					}
				}
				
				return rv;
			}
			else
			{
				if(gived==2)
				{
					return new ReturnValue(false,"订单"+orderNo+"正在发货中");
				}
				else
				{
					return new ReturnValue(false,"订单"+orderNo+"已经发货过");
				}				
			}
		}
		else
		if(orderResult==0)
		{
			return new ReturnValue(false,"订单"+orderNo+"还在处理中");
		}
		/*else
		if(result==-1)
		{
			return new ReturnValue(false,"订单购买失败:"+note);
		}*/
		else
		{
			return new ReturnValue(false,"订单结果值异常result="+orderResult+",请重新购买");
		}
	}
	
	/**
	 * 根据指定渠道获取支付类型
	 */
	public JSONArray getChargeType(String channel) throws Exception {
		DBPsRs chargeTypeRs = DBPool.getInst().pQueryS(tab_channel_charge_type);
		JSONArray chargeArr = new JSONArray();
		while(chargeTypeRs.next()){
			if(chargeTypeRs.getString("channel").equals(channel)){
				chargeArr.add(chargeTypeRs.getInt("chargetype"));
			}
		}
		return chargeArr;
	}
	
	//------------------静态区--------------------
	
	private static ChargeBAC instance = new ChargeBAC();

	public static ChargeBAC getInstance() {
		return instance;
	}
}
