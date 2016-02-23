package com.moonic.gamelog;

import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.SqlString;
import com.moonic.bac.ItemBAC;
import com.moonic.bac.PlayerBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MyTools;

import conf.Conf;
import conf.LogTbName;


/**
 * 游戏日志
 * @author John
 */
public class GameLog {
	private static final String tab_game_log_datatype = "tab_game_log_datatype";
	
	private static final String tab_infull_prop_log = "tab_infull_prop_log";
	private static final String tab_coin_log_type = "tab_coin_log_type";
	
	public static final String tab_income_prop_log = "tab_income_prop_log";
	
	private short act;
	
	private int factionid;
	
	public int playerid;
	
	public StringBuffer consumeSb = new StringBuffer();//消耗
	public StringBuffer obtainSb = new StringBuffer();//获得
	public StringBuffer remarkSb = new StringBuffer();//备注
	
	public JSONObject chadataarr = new JSONObject();
	
	private String centerOrderNo;
	
	/**
	 * 构造
	 */
	private GameLog(int playerid, short act, int factionid) {
		this.act = act;
		this.factionid = factionid;
		
		this.playerid = playerid;
	}
	
	/**
	 * 增加物品数据变化说明
	 */
	public GameLog addItemChaNoteArr(JSONArray jsonarr) throws Exception {
		for(int i = 0; jsonarr != null && i < jsonarr.length(); i ++) {
			addItemChaNoteObj(jsonarr.optJSONObject(i));
		}
		return this;
	}
	
	/**
	 * 增加物品数据变化说明
	 */
	public GameLog addItemChaNoteObj(JSONObject jsonobj) throws Exception {
		if(jsonobj != null){
			int id = jsonobj.optInt("id");
			String name = null;
			if(jsonobj.has("name")){
				name = jsonobj.optString("name");
			} else {
				name = ItemBAC.getInstance().getListRs(jsonobj.optInt("type"), jsonobj.optInt("num")).getString("name");
			}
			int oldamount = jsonobj.optInt("oldamount");
			int nowamount = jsonobj.optInt("amount");
			byte oldzone = (byte)jsonobj.optInt("oldzone");
			byte newzone = (byte)jsonobj.optInt("zone");
			addChaNote(formatNameID("["+ItemBAC.itemZoneName[newzone]+"]"+name, id), oldamount, nowamount-oldamount, true);
			if(oldzone != newzone){
				addRemark(formatNameID("["+ItemBAC.itemZoneName[newzone]+"]"+name, id)+"从"+ItemBAC.itemZoneName[oldzone]+"移到"+ItemBAC.itemZoneName[newzone]);
			}
			jsonobj.remove("name");
			jsonobj.remove("oldamount");
			jsonobj.remove("oldzone");
			jsonobj.remove("extend");
		}
		return this;
	}
	
	/**
	 * 铜钱
	 */
	public static final String TYPE_MONEY = "铜钱";
	/**
	 * 金锭
	 */
	public static final String TYPE_COIN = "金锭";
	
	/**
	 * 增加数据变化说明
	 */
	public GameLog addChaNote(String name, long oldVal, long chaVal){
		return addChaNote(name, oldVal, chaVal, true);
	}
	
	/**
	 * 增加数据变化说明
	 * @param name 数据名称
	 * @param oldVal 变化前数据值
	 * @param chaVal 变化量
	 * @param sys_change 是否为系统变化
	 */
	public GameLog addChaNote(String name, long oldVal, long chaVal, boolean sys_change){
		if(chaVal != 0){
			if(chaVal > 0){
				obtainSb/*.append("获得")*/.append(name).append("：").append(formatAmount(oldVal, chaVal)).append("\r\n");
			} else 
			if(chaVal < 0){
				consumeSb/*.append("消耗")*/.append(name).append("：").append(formatAmount(oldVal, chaVal)).append("\r\n");
			}
			try {
				DBPaRs rs = DBPool.getInst().pQueryA(tab_game_log_datatype, "name='"+name+"'");	
				if(rs.exist()){
					JSONArray arr = chadataarr.optJSONArray(name);
					if(arr == null){
						arr = new JSONArray();
						arr.add(rs.getString("chacol"));
						arr.add(rs.getString("nowcol"));
						arr.add(rs.getString("syscol"));
						arr.add(oldVal);
						arr.add(chaVal);
						arr.add(sys_change?1:0);
						chadataarr.put(name, arr);
					} else {
						arr.put(3, arr.optLong(3)+chaVal);
					}
				}
			} catch (Exception e) {
				System.out.println("name="+name);
				e.printStackTrace();
			}
		}
		return this;
	}
	
	/**
	 * 设置充值中心订单号
	 */
	public GameLog setCenterOrderNo(String centerOrderNo) {
		this.centerOrderNo = centerOrderNo;
		return this;
	}
	
	/**
	 * 格式化变化数量
	 */
	private String formatAmount(long oldAmount, long chaAmount) {
		StringBuffer formatStr = new StringBuffer();
		formatStr.append(chaAmount);
		formatStr.append("（");
		formatStr.append(oldAmount);
		formatStr.append("→");
		formatStr.append(oldAmount+chaAmount);
		formatStr.append("）");
		return formatStr.toString();
	}
	
	/**
	 * 获取格式化的名字ID字符串
	 */
	public static String formatNameID(String name, int id){
		return name+"("+id+")";
	}
	
	/**
	 * 增加消耗说明
	 */
	public GameLog addConsume(StringBuffer consumeSb) {
		if(consumeSb != null && consumeSb.length() > 0){
			this.consumeSb.append(consumeSb.toString()+"\r\n");
		}
		return this;
	}
	
	/**
	 * 增加消耗说明
	 */
	public GameLog addConsume(String str){
		if(str != null){
			consumeSb.append(str+"\r\n");
		}
		return this;
	}
	
	/**
	 * 增加获得说明
	 */
	public GameLog addObtain(StringBuffer obtainSb) {
		if(obtainSb != null && obtainSb.length() > 0){
			this.obtainSb.append(obtainSb.toString()+"\r\n");
		}
		return this;
	}
	
	/**
	 * 增加获得说明
	 */
	public GameLog addObtain(String str){
		if(str != null){
			obtainSb.append(str+"\r\n");
		}
		return this;
	}
	
	/**
	 * 增加备注说明
	 */
	public GameLog addRemark(StringBuffer remarkSb) {
		if(remarkSb != null && remarkSb.length() > 0){
			this.remarkSb.append(remarkSb.toString()+"\r\n");
		}
		return this;
	}
	
	/**
	 * 增加备注说明
	 */
	public GameLog addRemark(String str){
		if(str != null){
			remarkSb.append(str+"\r\n");
		}
		return this;
	}
	
	/**
	 * 保存
	 */
	public void save() {
		DBHelper dbHelper = new DBHelper();
		try {
			SqlString sqlString = new SqlString();
			sqlString.add("playerid", playerid);
			sqlString.add("serverid", Conf.sid);
			sqlString.add("act", act);
			if(consumeSb != null && consumeSb.length() > 0) {
				sqlString.add("consume", consumeSb.toString());
			} 
			if(obtainSb != null && obtainSb.length() > 0) {
				sqlString.add("obtain", obtainSb.toString());
			}
			if(remarkSb != null && remarkSb.length() > 0) {
				sqlString.add("remark", remarkSb.toString());
			}
			if(factionid != 0) {
				sqlString.add("factionid", factionid);
			}
			long coinlog_data = 0;
			JSONArray chadata = chadataarr.toJSONArray();
			for(int i = 0; chadata!=null && i < chadata.length(); i++){
				JSONArray arr = chadata.optJSONArray(i);
				String cha_col = arr.optString(0);
				String now_col = arr.optString(1);
				String sys_col = arr.optString(2);
				long oldVal = arr.optLong(3);
				long chaVal = arr.optLong(4);
				long sysVal = arr.optLong(5);
				sqlString.add(cha_col, chaVal);
				sqlString.add(now_col, oldVal+chaVal);
				sqlString.add(sys_col, sysVal);
				//金锭结算数据
				if(cha_col.equals("changecoin")){
					coinlog_data = chaVal;
				}
			}
			sqlString.addDateTime("createtime", MyTools.getTimeStr());
			DBHelper.logInsert(LogTbName.TAB_GAME_LOG(), sqlString);
			//金锭结算
			if(coinlog_data != 0){
				DBPaRs typeRs = DBPool.getInst().pQueryA(tab_coin_log_type, "itemid="+act);
				DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
				int userid = 0;
				String username = null;
				String platformname = null;
				String ip = null;
				ResultSet userRs = dbHelper.query("tab_user", "pookid,username,platform,ip", "id="+plaRs.getInt("userid"));
				if(userRs.next()){
					userid = userRs.getInt("pookid");
					username = userRs.getString("username");
					DBPaRs platformRs = DBPool.getInst().pQueryA("tab_platform", "code='"+userRs.getString("platform")+"'");
					platformname = platformRs.getString("name");
					ip = userRs.getString("ip");
				}
				DBPaRs channelServerRs = DBPool.getInst().pQueryA(ServerBAC.tab_channel_server, "vsid="+plaRs.getInt("vsid"));
				String gamename = "刀塔传奇-"+channelServerRs.getString("servername");
				int type = act;
				int awardtype = -1;
				String itemname = null;
				String obtain = null;
				int validitytype = -1;
				int validityday = 0;
				if(typeRs.exist()){
					awardtype = typeRs.getInt("awardtype");
					itemname = typeRs.getString("itemname");
					obtain = typeRs.getString("obtain");
					validitytype = typeRs.getInt("validitytype");
					validityday = typeRs.getInt("validityday");
				}
				int newcoin = plaRs.getInt("coin");
				SqlString streamSqlStr = new SqlString();
				streamSqlStr.add("user_id", userid);
				streamSqlStr.add("user_name", username);
				streamSqlStr.add("agent_name", platformname);
				streamSqlStr.add("game_name", gamename);
				streamSqlStr.add("money_type", "金锭");
				streamSqlStr.add("type", type);
				streamSqlStr.add("award_type", awardtype);
				streamSqlStr.add("award_num", coinlog_data);
				streamSqlStr.add("begin_num", newcoin-coinlog_data);
				streamSqlStr.add("end_num", newcoin);
				streamSqlStr.add("client_ip", ip);
				streamSqlStr.add("related_num", "");
				streamSqlStr.addDateTime("createtime", MyTools.getTimeStr());
				dbHelper.insert(tab_infull_prop_log, streamSqlStr);
				if(coinlog_data < 0){
					SqlString incomeSqlStr = new SqlString();
					incomeSqlStr.add("user_id", userid);
					incomeSqlStr.add("user_name", username);
					incomeSqlStr.add("agent_name", platformname);
					incomeSqlStr.add("prop_type", validitytype);
					incomeSqlStr.add("game_name", gamename);
					incomeSqlStr.add("prop_name", itemname);
					incomeSqlStr.add("buy_amount", 1);
					incomeSqlStr.add("money_type", 0);
					incomeSqlStr.add("pay_money", -coinlog_data);
					incomeSqlStr.addDateTime("pay_time", MyTools.getTimeStr());
					incomeSqlStr.addDateTime("effective_time", MyTools.getTimeStr(System.currentTimeMillis()+validityday*MyTools.long_day));
					incomeSqlStr.add("effective_day", validityday);
					incomeSqlStr.add("present_info", obtain);
					incomeSqlStr.add("client_ip", ip);
					incomeSqlStr.add("platform", "移动");
					incomeSqlStr.add("related_num", centerOrderNo);
					incomeSqlStr.add("company_id", "世熠");
					incomeSqlStr.addDateTime("createtime", MyTools.getTimeStr());
					dbHelper.insert(tab_income_prop_log, incomeSqlStr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取动作号
	 */
	public short getAct() {
		return act;
	}
	
	//--------------静态区---------------
	
	/**
	 * 获取实例
	 */
	public static GameLog getInst(int playerid, short act) {
		return getInst(playerid, act, 0);
	}
	
	/**
	 * 获取实例
	 */
	public static GameLog getInst(int playerid, short act, int factionid) {
		return new GameLog(playerid, act, factionid);
	}
}
