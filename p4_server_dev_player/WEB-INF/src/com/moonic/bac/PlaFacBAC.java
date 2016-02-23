package com.moonic.bac;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPRs;
import com.moonic.util.DBPaRs;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 角色帮派
 * @author John
 */
public class PlaFacBAC extends PlaBAC {
	
	/**
	 * 构造
	 */
	public PlaFacBAC() {
		super("tab_pla_faction", "playerid");
	}
	
	/**
	 * 初始化目标数据
	 */
	public void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception {
		SqlString sqlStr = getInitSqlStr();
		sqlStr.add("playerid", playerid);
		sqlStr.add("factioncon", 0);
		sqlStr.add("cbpartnerrelivedata", "{}");
		sqlStr.add("getwelfare", 0);
		sqlStr.add("worship1", 0);
		sqlStr.add("worship2", 0);
		insert(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 获取初始化角色帮派信息的SqlStr
	 */
	public SqlString getInitSqlStr(){
		SqlString sqlStr = new SqlString();
		sqlStr.add("factionid", 0);
		sqlStr.add("facname", null);
		sqlStr.add("position", 0);
		sqlStr.add("beworship", (new JSONObject()).toString());
		sqlStr.add("cmdata", (new JSONObject()).toString());//隔日才可进入另一个帮派，退出帮派重置数据无影响
		return sqlStr;
	}
	
	/**
	 * 退出帮派后次日才允许再次进入帮派
	 */
	public void intoCheck(DBPaRs plafacRs) throws Exception {
		if(System.currentTimeMillis()-MyTools.getCurrentDateLong(plafacRs.getTime("jointime"))<Conf.joinfacspacetime*MyTools.long_minu){
			BACException.throwInstance("退出帮派后次日才可再次申请");
		}		
	}
	
	/**
	 * 进入帮派
	 */
	public void intoFaction(DBHelper dbHelper, int playerid, int factionid, String facname, int position) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("factionid", factionid);
		sqlStr.add("facname", facname);
		sqlStr.add("position", position);
		sqlStr.addDateTime("jointime", MyTools.getTimeStr());
		update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 调整职位
	 */
	public void setPosition(DBHelper dbHelper, int playerid, int position) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("position", position);
		update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 退出帮派
	 */
	public void exitFaction(DBHelper dbHelper, int factionid, int playerid, GameLog gl) throws Exception {
		SqlString sqlStr = getInitSqlStr();
		sqlStr.addDateTime("jointime", MyTools.getTimeStr());
		update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 获取族员信息
	 */
	public JSONArray getMemData(DBPRs plafacRs) throws Exception {
		return getMemData(plafacRs.getInt("playerid"), plafacRs.getInt("position"));
	}
	
	/**
	 * 获取族员信息
	 */
	public JSONArray getMemData(int playerid, int position) throws Exception {
		DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
		JSONArray arr = new JSONArray();
		arr.add(plaRs.getInt("id"));//玩家ID
		arr.add(plaRs.getString("name"));//玩家名
		arr.add(plaRs.getInt("lv"));//玩家等级
		arr.add(plaRs.getInt("onlinestate"));//是否在线
		arr.add(plaRs.getInt("num"));//玩家编号
		arr.add(plaRs.getInt("vip"));//VIP等级
		arr.add(TqBAC.getInstance().getTQNum(plaRs));//特权
		arr.add(plaRs.getTime("logintime"));//登录时间
		arr.add(position);//职位
		return arr;
	}
	
	/**
	 * 获取登录信息
	 */
	public JSONArray getLoginData(int playerid) throws Exception {
		DBPaRs plafacRs = getDataRs(playerid);
		int factionid = plafacRs.getInt("factionid");
		JSONArray otherarr = new JSONArray();
		if(factionid != 0){
			otherarr.add(new JSONObject(plafacRs.getString("cmdata")));//帮派副本数据
		}
		JSONArray plafacarr = new JSONArray();
		plafacarr.add(factionid);//帮派ID
		plafacarr.add(plafacRs.getInt("factioncon"));//功勋
		plafacarr.add(new JSONObject(plafacRs.getString("cbpartnerrelivedata")));//国战伙伴复活次数
		plafacarr.add(plafacRs.getInt("getwelfare"));//是否已领取福利
		plafacarr.add(plafacRs.getInt("worship1"));//是否已膜拜
		plafacarr.add(plafacRs.getInt("worship2"));//是否已膜拜
		plafacarr.add(new JSONObject(plafacRs.getString("beworship")));//被膜拜数据
		plafacarr.add(plafacRs.getTime("jointime"));//最后一次加入帮派或退出帮派时间
		plafacarr.add(plafacRs.getInt("factioncon"));//功勋
		plafacarr.add(plafacRs.getTime("leadercdendtime"));//太守争夺冷却时间
		plafacarr.add(otherarr);//其他数据
		return plafacarr;
	}
	
	/**
	 * 重置每日数据
	 */
	public void resetData(DBHelper dbHelper, int playerid, long resetdate) throws Exception {
		DBPaRs plafacRs = getDataRs(playerid);
		int factionid = plafacRs.getInt("factionid");
		SqlString sqlStr = new SqlString();
		if(factionid != 0) {
			sqlStr.add("cmdata", (new JSONObject()).toString());//帮派副本数据
		}
		sqlStr.add("cbpartnerrelivedata", "{}");
		sqlStr.add("getwelfare", 0);//是否已领取福利
		sqlStr.add("worship1", 0);//是否已膜拜
		sqlStr.add("worship2", 0);//是否已膜拜
		update(dbHelper, playerid, sqlStr);
	}
	
	//--------------静态区--------------
	
	private static PlaFacBAC instance = new PlaFacBAC();

	/**
	 * 获取实例
	 */
	public static PlaFacBAC getInstance() {
		return instance;
	}
}
