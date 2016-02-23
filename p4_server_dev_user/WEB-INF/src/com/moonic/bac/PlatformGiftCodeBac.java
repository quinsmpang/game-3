package com.moonic.bac;

import java.util.Random;

import org.json.JSONObject;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MyTools;

public class PlatformGiftCodeBac {
	public static String tab_platform_gift_code = "tab_platform_gift_code";
	
	/*
	 * number 数字类型
	 * character 字符类型
	 * all 数字字符混合型
	 */
	public static enum CodeType {NUMBER,CHARACTER,ALL};
	static char letter[]= {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H','J', 'K',  'M', 'N',  'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	static char digit[]={'2', '3', '4', '5', '6', '7', '8', '9'};
	
	private static String generate(CodeType type,int length){
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < length; i++) 
		switch(type){
		case NUMBER:
			sb.append(digit[new Random().nextInt(digit.length)]);
			break;
		case CHARACTER:
			sb.append(letter[new Random().nextInt(letter.length)]);
			break;
		case ALL:
			sb.append((char)(new Random().nextInt(2)==0? letter[new Random().nextInt(letter.length)]:digit[new Random().nextInt(digit.length)]));
			break;
		}
		return sb.toString();
	}
	
	 /**
	  * 根据手机号码生成平台激活码
	  */
	public ReturnValue createGiftCodeByPhonenumber(int vsid, String platformId, int giftgroup, CodeType type, int length, String phonenumber) {
		DBHelper dbHelper = new DBHelper();
		try{
			if(giftgroup==0) {
				BACException.throwInstance("请选择礼包类型");
			}
			if(vsid==0) {
				BACException.throwInstance("请选择游戏服");
			}
			if(phonenumber==null || phonenumber.equals("")) {
				BACException.throwInstance("请输入手机号码");
			}
			dbHelper.openConnection();
			//检查礼包类型是否存在
			DBPaRs giftRs = DBPool.getInst().pQueryA(PlatformGiftBAC.tab_platform_gift, "num="+giftgroup);
			if(!giftRs.exist()){
				BACException.throwInstance("该礼包类型不存在");
			}
			//检查该手机号是否已获取过领取码
			JSONObject json = dbHelper.queryJsonObj(tab_platform_gift_code, "playerid", "phonenumber='"+phonenumber+"' and giftcode="+giftgroup+" and serverid="+vsid);
			if(json != null) {
				BACException.throwInstance("你的手机号"+phonenumber+"已经获取过"+giftRs.getString("name")+"礼包码了，请勿重复获取。");
			}
			synchronized (instance) {
				int daylimit = giftRs.getInt("daylimit");
				if(daylimit > 0) {
					long currtime = System.currentTimeMillis();
					long starttime = 0;
					long endtime = 0;
					long pointtime = MyTools.getCurrentDateLong()+MyTools.getPointTimeLong("15:00:00");
					if(currtime < pointtime){
						starttime = pointtime-MyTools.long_day;
						endtime = pointtime;
					} else {
						starttime = pointtime;
						endtime = pointtime+MyTools.long_day;
					}
					int todaygive = dbHelper.queryCount(tab_platform_gift_code, "publishtime>="+MyTools.getTimeStr(starttime)+" and publishtime<="+MyTools.getTimeStr(endtime)+" and giftcode="+giftgroup);
					if(todaygive >= daylimit) {
						if(currtime < pointtime) {
							BACException.throwInstance(giftRs.getString("name")+"今天已经被领取满"+daylimit+"次了，请明天15:00再来领取。");
						} else {
							BACException.throwInstance(giftRs.getString("name")+"今天已经被领取满"+daylimit+"次了，请明天15:00再来领取。");
						}
					}
				}
				String code = null;
				while(true) {
					code = generate(type,length);
					//类型为ALL时遇到纯数字则重新生成
					if(type == CodeType.ALL && Tools.str2long(code)>0) {
						continue;
					}
					boolean exist = dbHelper.queryExist(tab_platform_gift_code,"code='"+code+"'");
					if(exist){
						continue;
					}
					SqlString sqlStr = new SqlString();
					sqlStr.add("code", code);
					sqlStr.add("platform", platformId);
					sqlStr.add("giftcode", giftgroup);
					sqlStr.add("playerid", 0);
					sqlStr.add("gived", 0);
					sqlStr.add("publish", 1);
					sqlStr.add("phonenumber", phonenumber);
					sqlStr.add("serverid",vsid);
					sqlStr.addDateTime("publishtime", MyTools.getTimeStr()); 
					dbHelper.insert(tab_platform_gift_code, sqlStr);
					break;
				}
				JSONObject returnJson = new JSONObject();
				returnJson.put("note", "恭喜您已经成功领取了"+giftRs.getString("name")+"，礼包码："+code+",进入游戏通过活动NPC“活动大使”激活领取。游戏下载地址：http://xm.pook.com");
				return new ReturnValue(true, returnJson.toString());
			}
		} catch(Exception ex){
			ex.printStackTrace();
			JSONObject returnJson = new JSONObject();
			returnJson.put("note", "获取礼包码异常："+ex.toString());
			return new ReturnValue(true, returnJson.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	private static PlatformGiftCodeBac instance = new PlatformGiftCodeBac();
	
	public static PlatformGiftCodeBac getInstance(){
		return instance;
	}
}
