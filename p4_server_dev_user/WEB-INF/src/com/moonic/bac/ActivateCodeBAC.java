package com.moonic.bac;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

public class ActivateCodeBAC extends BaseActCtrl//TODO 去除BAC
{
	public static String tbName = "TAB_ACTIVATE_CODE";
	
	private static ActivateCodeBAC self = new ActivateCodeBAC();
	
	private static Object syncLock = new Object();
	
	public static ActivateCodeBAC getInstance() {
		return self;
	}
	
	public ActivateCodeBAC()
	{
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}
	
	//恭喜您成功领取激活码：GHFDN,登陆口袋幻兽OL客户端激活游戏帐号，参与封测人人有奖。详情请关注官方论坛：www.xianmobbs.com【波克城市】

	/**
	 * 参加激活码抽奖
	 * @param userName 用户名
	 * @return
	 */
	public ReturnValue joinLottery(String userName)
	{
		JSONObject returnJsonObj = new JSONObject();
		
		
		//本次抽奖活动已经结束，更多精彩请关注《口袋幻兽OL》官方论坛
		returnJsonObj.put("note", "本次抽奖活动已经结束，更多精彩请关注《口袋幻兽OL》官方论坛");
		return new ReturnValue(false,returnJsonObj.toString());
		/*
		if(userName==null || userName.equals(""))
		{
			returnJsonObj.put("note", "无效的用户名！");
			return new ReturnValue(false,returnJsonObj.toString());
		}
		synchronized (syncLock) 
		{			
			if(checkActivateTmp(null,userName))
			{
				int lottery = getIntValue("lottery", "ACTIVATE_USER='"+userName+"'");
				if(lottery==1)
				{
					returnJsonObj.put("note", "您已参加过本次抽奖活动，不能重复提交！");	
				}
				else				
				{
					update("lottery=1,lottery_time="+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr()), "ACTIVATE_USER='"+userName+"'");
					returnJsonObj.put("note", "恭喜您游戏帐号提交成功，获奖名单敬请关注官方论坛及官方微博！");	
				}
				
				return new ReturnValue(true,returnJsonObj.toString());
			}
			else
			{
				returnJsonObj.put("note", "您尚未激活帐号，请先下载游戏后在游戏中激活帐号，才能参加本次抽奖活动！");
				return new ReturnValue(false,returnJsonObj.toString());
			}
		}	*/	
	}
	
	/**
	 * 用户激活
	 * @param channel 渠道
	 * @param userName 用户标志名
	 * @param code 激活码
	 * @param ip 
	 */
	public ReturnValue activate(String channel,String userName,String code,String ip) 
	{
		DBHelper dbHelper = new DBHelper();
		try {
			//判断该用户是否已激活
			if(checkActivate(channel,userName)) {
				return new ReturnValue(true,"该用户已经激活过了。");
			}
			//判断激活码是否存在
			JSONObject jsonObj = getJsonObj("code='"+code.toUpperCase()+"'");
			if(jsonObj==null) {
				return new ReturnValue(false,"该激活码不存在。");
			}
			//判断该激活码是否已使用过
			if(jsonObj.optInt("ACTIVATED")==1) {
				return new ReturnValue(false,"该激活码已经被激活过不能使用了。");
			}
			//判断激活码是否发布过
			if(jsonObj.optInt("publish")==0) {
				return new ReturnValue(false,"未分发过的不可用激活码。");
			}
			//判断激活码是否有效期
			String startTime = jsonObj.optString("startTime");
			if(startTime!=null && !startTime.equals("")) {
				Date currentDate = Tools.str2date(Tools.getCurrentDateTimeStr());
				Date codeStartDate = Tools.str2date(startTime);
				if(currentDate.before(codeStartDate)) {
					return new ReturnValue(false,"该激活码要到"+Tools.strdate2str(startTime,"yyyy-MM-dd")+"才能使用。");
				}
			}
			DBPaRs channelRs = DBPool.getInst().pQueryA(ChannelBAC.tab_channel, "code="+channel);
			SqlString sqlS = new SqlString();
			sqlS.add("ACTIVATED", 1);
			sqlS.add("ACTIVATE_USER", userName);
			sqlS.addDateTime("ACTIVATE_TIME", Tools.getCurrentDateTimeStr());
			//tab_activate_code中的channel实际意义为platform
			sqlS.add("CHANNEL", channelRs.getString("platform"));
			sqlS.add("ip", ip);			
			dbHelper.update(tbName, sqlS, "code='"+code.toUpperCase()+"'");
			//update(sqlS.updateString(), "code='"+code.toUpperCase()+"'");
			return new ReturnValue(true);			
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
		finally
		{	
			dbHelper.closeConnection();
		}
	}
	
	private static String[] exemptActivateChannel = {"003"};
	
	/**
	 * 检测用户是否已激活
	 * @param channel 渠道
	 * @param userName 用户标志名
	 */
	public boolean checkActivate(String channel, String userName) throws Exception {
		if(!ConfigBAC.getBoolean("needactivate")){
			return true;
		}
		if(Tools.contain(exemptActivateChannel, channel)){
			return true;
		}
		DBPaRs channelRs = DBPool.getInst().pQueryA(ChannelBAC.tab_channel, "code="+channel);
		//tab_activate_code中的channel实际意义为platform
		return getJsonObj("channel='"+channelRs.getString("platform")+"' and activate_user='"+userName+"' and activated=1")!=null;
	}
	
	public ReturnValue haveActivateCode()
	{
		JSONObject returnJsonObj = new JSONObject();
		int count = getCount("publish=0");
		if(count>0)
		{
			returnJsonObj.put("note", "");			
			return new ReturnValue(true,returnJsonObj.toString());
		}
		else
		{
			returnJsonObj.put("note", "本次抽奖活动已经结束，更多精彩请关注《口袋幻兽OL》官方网站http://kd.pook.com/。");			
			return new ReturnValue(false,returnJsonObj.toString());
		}
		
		/*returnJsonObj.put("note", "激活码已领取完，请等待官方重新发布新激活码。");			
		return new ReturnValue(false,returnJsonObj.toString());*/
	}
	
	public ReturnValue getActivateCode(String phone,String ip,int method,String publishUser)
	{
		//System.out.println(phone+"来领取激活码");
		JSONObject returnJsonObj = new JSONObject();
		DBHelper dbHelper = new DBHelper();
		synchronized (syncLock) 
		{
			try
			{
				//System.out.println("获取激活码 phone="+phone);
				if(phone==null || phone.equals(""))
				{
					returnJsonObj.put("note", "请提供手机号。");
					//writeLog(Tools.getCurrentDateTimeStr()+ "：请提供手机号ip="+ip);
					return new ReturnValue(false,returnJsonObj.toString());
				}
				//检查此手机号码是否已验证过或领取过
				String[] codeInfo = getValues(new String[]{"code","activated","starttime"}, "publish=1 and phone='"+phone+"'");
				//System.out.println("codeInfo="+Tools.strArr2Str(codeInfo));
				if(codeInfo!=null)
				{
					//System.out.println("codeInfo[2]="+codeInfo[2]);
					if(codeInfo[1]!=null && codeInfo[1].equals("1")) //已使用过激活码
					{			
						String msg = "您的手机号"+phone+"已领取过激活码："+codeInfo[0]+"，赶快登陆口袋幻兽OL客户端激活游戏帐号吧！下载地址：http://kd.pook.com";
						returnJsonObj.put("note", msg);
						LogBAC.logout("activate", "phone="+phone+",from="+method+",user="+publishUser+",短信："+msg);
						//writeLog("您的手机号码"+phone+"已使用过激活码"+codeInfo[0]+"激活游戏帐号，如果疑问请到官方论坛www.xianmobbs.com咨询。");
					}
					else	 //已领取过但是未激活
					{
						//System.out.println("已领取过但是未激活");
						int compare=0;
						String startdate = codeInfo[2];
						if(startdate!=null)
						{
							startdate = Tools.strdate2str(startdate, "yyyy-M-d");
							compare=Tools.compareStrDate(Tools.getCurrentDateTimeStr(),startdate);
						}
						else
						{
							compare=1;
						}
						if(compare>0) //已到可激活日期
						{		
							String msg ="您的手机号"+phone+"已领取过激活码："+codeInfo[0]+"，赶快登陆口袋幻兽OL客户端激活游戏帐号吧！下载地址：http://kd.pook.com";
							returnJsonObj.put("note", msg);
							//writeLog("您的手机号"+phone+"已领取过激活码："+codeInfo[0]+"，赶快登陆口袋幻兽OL客户端激活游戏帐号吧！下载地址：wap.xm.pook.com");
							LogBAC.logout("activate", "phone="+phone+",from="+method+",user="+publishUser+",短信："+msg);
						}
						else //未到可激活日期
						{	
							String msg ="您的手机号"+phone+"已领取过预约激活码："+codeInfo[0]+"，需要等到"+startdate+"号才能登陆口袋幻兽OL客户端激活自己的游戏帐号，下载地址：http://kd.pook.com";
							returnJsonObj.put("note", msg);
							LogBAC.logout("activate", "phone="+phone+",from="+method+",user="+publishUser+",短信："+msg);
						}					
					}								
					return new ReturnValue(true,returnJsonObj.toString());
				}
				
				SqlString sqlStr = new SqlString();
				//找有效期内的
				sqlStr.add("activated", 0);
				sqlStr.add("PUBLISH", 0);
				sqlStr.addWhere("(STARTTIME is null or STARTTIME<="+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr())+")");
				JSONObject jsonObj = getTopJsonList("code", 1, sqlStr.whereString(), "id ASC");
				//System.out.println("领取码结果="+jsonObj.toString());
				if(jsonObj!=null)
				{
					JSONArray arr = jsonObj.optJSONArray("list");
					if(arr.length()>0)
					{
						JSONObject line = arr.optJSONObject(0);
						String code = line.optString("code");
						SqlString updateSqlS = new SqlString();
						updateSqlS.add("method", method);
						updateSqlS.add("phone", phone);
						updateSqlS.add("publish_user", publishUser);
						updateSqlS.add("publish", 1);
						updateSqlS.addDateTime("publish_time", Tools.getCurrentDateTimeStr());
						updateSqlS.add("ip", ip);
						dbHelper.update(tbName, updateSqlS, "code='"+code+"'");
						//update("method="+method+",phone='"+phone+"',publish_user='"+publishUser+"',PUBLISH=1,publish_time="+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr())+",ip='"+ip+"'", "code='"+code+"'");
						//恭喜您成功领取激活码：GHFDN,登陆口袋幻兽OL客户端激活游戏帐号，参与封测人人有奖。详情请关注官方论坛：www.xianmobbs.com【波克城市】
						String msg = "恭喜您成功领取激活码："+code+"，登陆口袋幻兽OL客户端激活游戏帐号，参与封测人人有奖。下载地址：http://kd.pook.com";
						returnJsonObj.put("note", msg);
						//writeLog("手机号码"+phone+":"+"恭喜您成功领取激活码："+code+"，登陆口袋幻兽OL客户端激活游戏帐号，参与封测人人有奖。下载地址：wap.xm.pook.com");
						LogBAC.logout("activate", "phone="+phone+",from="+method+",user="+publishUser+",短信："+msg);
						return new ReturnValue(true,returnJsonObj.toString());
					}			
				}
				else //找能预约的
				{
					sqlStr.clear();
					sqlStr.add("activated", 0);
					sqlStr.add("PUBLISH", 0);
					sqlStr.addWhere("(STARTTIME is not null and STARTTIME>"+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr())+")");
					jsonObj = getTopJsonList("code,STARTTIME", 1, sqlStr.whereString(), "id ASC");
					if(jsonObj!=null)
					{
						JSONArray arr = jsonObj.optJSONArray("list");
						if(arr.length()>0)
						{
							JSONObject line = arr.optJSONObject(0);
							String code = line.optString("code");
							String startdate = Tools.strdate2str(line.optString("STARTTIME"),"yyyy-M-d");
							SqlString updateSqlS = new SqlString();
							updateSqlS.add("method", method);
							updateSqlS.add("phone", phone);
							updateSqlS.add("publish_user", publishUser);
							updateSqlS.add("publish", 1);
							updateSqlS.addDateTime("publish_time", Tools.getCurrentDateTimeStr());
							updateSqlS.add("ip", ip);
							dbHelper.update(tbName, updateSqlS, "code='"+code+"'");
							
							//update("method="+method+",phone='"+phone+"',publish_user='"+publishUser+"',PUBLISH=1,publish_time="+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr())+",ip='"+ip+"'", "code='"+code+"'");
							//returnJsonObj.put("code", code);
							//returnJsonObj.put("startdate", startdate);
							String msg = "您已成功领取预约激活码："+code+"，需要等到"+startdate+"号才能登陆口袋幻兽OL客户端激活游戏帐号。下载地址：http://kd.pook.com";
							returnJsonObj.put("note", msg);
							//System.out.println("您已成功领取预约激活码："+code+"，可以等到"+startdate+"号登录口袋幻兽OL游戏激活自己的游戏帐号。请关注官方论坛：http://xianmobbs.0211.com/。");
							//writeLog("手机号码"+phone+":"+"您已成功领取预约激活码："+code+"，需要等到"+startdate+"号才能登陆口袋幻兽OL客户端激活游戏帐号。下载地址：wap.xm.pook.com");
							LogBAC.logout("activate", "phone="+phone+",from="+method+",user="+publishUser+",短信："+msg);
							
							return new ReturnValue(true,returnJsonObj.toString());
						}
					}
				}
				//一期抢码活动已经结束，将于4月9日11时准时开启第二期抢码活动
				//returnJsonObj.put("note", "激活码已分发完毕,请等待下次机会，详情请关注官方论坛：www.xianmobbs.com ");
				//writeLog("手机号码"+phone+":"+"激活码已分发完毕,请等待下次机会，详情请关注官方论坛：www.xianmobbs.com ");
				
				String msg = "激活码现已发完，请关注《口袋幻兽OL》官方网站http://kd.pook.com";
				returnJsonObj.put("note", msg);
				//returnJsonObj.put("note", "今天抢码活动已经结束，请等待明天的抢码活动。");
				LogBAC.logout("activate", "phone="+phone+",from="+method+",user="+publishUser+",页面提示："+msg);
				//writeLog("手机号码"+phone+":"+"本次抽奖活动已经结束，更多精彩请关注《口袋幻兽OL》官方论坛。");
				//System.out.println("激活码已分发完毕,请等待下次机会，请先关注口袋幻兽OL官方论坛：http://xianmobbs.0211.com/。");
				//DataBase.setUseDebug(false);
				return new ReturnValue(false,returnJsonObj.toString());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				returnJsonObj.put("note",ex.toString());
				return new ReturnValue(false,returnJsonObj.toString());
			}
			finally
			{
				dbHelper.closeConnection();
			}
		}		
	}	
}
