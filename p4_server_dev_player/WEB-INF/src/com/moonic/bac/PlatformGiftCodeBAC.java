package com.moonic.bac;

import java.sql.ResultSet;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 平台礼包码
 * @author John
 */
public class PlatformGiftCodeBAC {
	public static final String tab_pla_giftcode = "tab_pla_giftcode";
	public static final String tab_platform_gift_code = "tab_platform_gift_code";
	public static final String tab_platform_code_give_log = "tab_platform_code_give_log";
	
	public static final String tab_platform_gift = "tab_platform_gift";
	
	/**
	 * WEB领取激活码礼包
	 */
	public ReturnValue webGetPlatformGift(int playerid, String code){
		int userid = 0;
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			userid = PlayerBAC.getInstance().getIntValue(playerid, "userid");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
		return getPlatformGift(playerid, userid, code, true);
	}
	
	/**
	 * 领取激活码礼包
	 */
	public ReturnValue getPlatformGift(int playerid, int userid, String code, boolean fromWeb){
		DBHelper dbHelper = new DBHelper();
		try {
			MyTools.checkNoChar(code);
			code = code.toUpperCase();
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_GIFTCODE_GET);
			
			ResultSet plaGcRs = dbHelper.query(tab_pla_giftcode, "erroram,unbannedtime", "playerid="+playerid);
			boolean gcExist = plaGcRs.next();
			if(gcExist && !MyTools.checkSysTimeBeyondSqlDate(plaGcRs.getTimestamp("unbannedtime"))){
				long remainmin = (MyTools.getTimeLong(plaGcRs.getTimestamp("unbannedtime"))-System.currentTimeMillis())/MyTools.long_minu;
				if(remainmin < 1){
					remainmin = 1;
				}
				BACException.throwInstance("封禁时间内无法领取礼包，请在"+remainmin+"分钟后尝试！");
			}
			
			ResultSet userRs = dbHelper.query(UserBAC.tab_user, "channel", "id="+userid);
			if(!userRs.next()){
				BACException.throwInstance("用户未找到");
			}
			String channel = userRs.getString("channel");
			dbHelper.closeRs(userRs);
			
			int repeat = 0;//是否可重复使用
			int giftgroupnum = 0;//礼包组编号
			if(fromWeb){//WEB领取礼包码，直接领取礼包，每个礼包限1次
				giftgroupnum = Integer.valueOf(code);
			} else 
			if(code.equals("WANDOUJIA")){
				if(!channel.equals("006")){
					BACException.throwInstance("无效礼包码");
				}
				giftgroupnum = 1;
			} else 
			{
				ResultSet codeRs = dbHelper.query(tab_platform_gift_code, "id,serverid,giftcode,gived,publish,repeat,expiretime", "code='"+code+"'");
				boolean error = false;
				if(!codeRs.next() || codeRs.getInt("publish")==0){//礼包码不存在或未发放
					error = true;
				} else 
				if(codeRs.getInt("serverid")!=0 && codeRs.getInt("serverid")!=Conf.sid){//有服务器限制且服务器ID不符
					error = true;
				} else 
				if(codeRs.getInt("repeat")==0 && codeRs.getInt("gived")==1){//非重复使用且已使用过
					error = true;
				} else 
				if(codeRs.getString("expiretime")!=null && !codeRs.getString("expiretime").equals("") && MyTools.checkSysTimeBeyondSqlDate(codeRs.getTimestamp("expiretime"))){//有过期时间且过期时间已到
					error = true;
				}
				if(error){
					String errorinfo = null;
					if(gcExist){
						int erroram = plaGcRs.getInt("erroram");
						SqlString gcSqlStr = new SqlString();
						if(erroram+1 < 10){
							gcSqlStr.addChange("erroram", 1);
							errorinfo = "，您还有"+(10-(erroram+1))+"次机会";
						} else {
							gcSqlStr.add("erroram", 0);
							gcSqlStr.addDateTime("unbannedtime", MyTools.getTimeStr(System.currentTimeMillis()+MyTools.long_hour*2));
							errorinfo = "，您已被封禁，请在两小时后尝试";
						}
						gcSqlStr.addChange("totalerroram", 1);
						dbHelper.update(tab_pla_giftcode, gcSqlStr, "playerid="+playerid);
					} else {
						SqlString gcSqlStr = new SqlString();
						gcSqlStr.add("playerid", playerid);
						gcSqlStr.add("erroram", 1);
						gcSqlStr.add("totalerroram", 1);
						dbHelper.insert(tab_pla_giftcode, gcSqlStr);
						errorinfo = "，您还有"+(10-1)+"次机会";
					}
					BACException.throwInstance("礼包码错误或已使用"+errorinfo);//无效礼包码	
				}
				repeat = codeRs.getInt("repeat");
				giftgroupnum = codeRs.getInt("giftcode");
			}
			boolean exist = false;
			if(repeat==0){//不可重复使用，查礼包码是否已使用
				exist = dbHelper.queryExist(tab_platform_gift_code, "playerid="+playerid+" and giftcode="+giftgroupnum+" and gived=1");
			} else 
			if(repeat==1){//可重复使用，查领取日志中是否有领取记录
				exist = dbHelper.queryExist(tab_platform_code_give_log, "playerid="+playerid+" and giftcode='"+code+"'");
			}
			if(exist){
				BACException.throwInstance("您已领取过此礼包");
			}
			DBPaRs platformgiftRs = DBPool.getInst().pQueryA(tab_platform_gift, "num="+giftgroupnum);
			if(!platformgiftRs.exist()){
				BACException.throwInstance("礼包组不存在");
			}
			MailBAC.getInstance().sendModelMail(dbHelper, new int[]{playerid}, 8, null, new Object[]{platformgiftRs.getString("name")}, platformgiftRs.getString("gifts"));
			
			if(repeat==0){//不可重复使用的礼包码
				if(fromWeb || code.equals("WANDOUJIA")){
					SqlString sqlStr = new SqlString();
					sqlStr.add("platform", channel);
					if(fromWeb){
						sqlStr.add("code", "WEB"+playerid+"N"+giftgroupnum);
					} else {
						sqlStr.add("code", "WDJ"+playerid+"N"+giftgroupnum);	
					}
					sqlStr.add("giftcode", giftgroupnum);
					sqlStr.add("playerid", playerid);
					sqlStr.add("gived", 1);
					sqlStr.addDateTime("givetime", MyTools.getTimeStr());
					sqlStr.add("publish", 1);
					sqlStr.addDateTime("publishtime", MyTools.getTimeStr());
					sqlStr.add("serverid", Conf.sid);
					dbHelper.insert("tab_platform_gift_code", sqlStr);
				} else {
					SqlString sqlStr = new SqlString();
					sqlStr.add("platform", channel);
					sqlStr.add("playerid", playerid);
					sqlStr.add("serverid", Conf.sid);
					sqlStr.add("gived", 1);
					sqlStr.addDateTime("givetime", Tools.getCurrentDateTimeStr());
					dbHelper.update("tab_platform_gift_code", sqlStr, "code='"+code+"'");		
				}
			} else 
			if(repeat==1){//可重复使用的礼包码
				SqlString sqlStr = new SqlString();
				sqlStr.add("playerid", playerid);
				sqlStr.add("giftcode", code);
				sqlStr.addDateTime("savetime", MyTools.getTimeStr());
				dbHelper.insert("tab_platform_code_give_log", sqlStr);
			}
			if(gcExist){
				int erroram = plaGcRs.getInt("erroram");
				if(erroram > 0){
					SqlString sqlStr = new SqlString();
					sqlStr.add("erroram", 0);
					dbHelper.update(tab_pla_giftcode, sqlStr, "playerid="+playerid);
				}
			}
			
			//gl.addItemChaNoteArr(jsonarr);
			gl.addRemark("领取渠道："+(fromWeb?"网站":"客户端（礼包码："+code+"）"));
			gl.addRemark("领取礼包："+platformgiftRs.getString("name"));
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
	 * 重置错误次数
	 */
	public void resetPlaGiftCodeData(DBHelper dbHelper, int playerid) throws Exception {
		ResultSet rs = dbHelper.query(tab_pla_giftcode, "erroram", "playerid="+playerid);
		if(rs.next()){
			if(rs.getInt("erroram") > 0){
				SqlString sqlStr = new SqlString();
				sqlStr.add("erroram", 0);
				dbHelper.update(tab_pla_giftcode, sqlStr, "playerid="+playerid);
			}
		}
		dbHelper.closeRs(rs);
	}
	
	//--------------静态区--------------
	
	private static PlatformGiftCodeBAC instance = new PlatformGiftCodeBAC();
	
	/**
	 * 获取实例
	 */
	public static PlatformGiftCodeBAC getInstance(){
		return instance;
	}
}
