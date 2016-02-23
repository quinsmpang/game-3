package com.moonic.bac;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;

/**
 * 礼品兑换码
 * @author huangyan
 *
 */
public class ExchangeCodeBAC extends BaseActCtrl
{
	public static String tbName = "TAB_EXCHANGE_CODE";
	
	private static ExchangeCodeBAC self = new ExchangeCodeBAC();
	
	private static Object syncLock = new Object();
	
	public static ExchangeCodeBAC getInstance() {
		return self;
	}
	
	public ExchangeCodeBAC()
	{
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}	
	
	/**
	 * 设置兑换码
	 * @param code
	 * @return
	 */
	public ReturnValue exchangeCode(String code)
	{			
		String[] codeInfo = getValues(new String[]{"published","exchanged"}, "code='"+code+"'");
		if(codeInfo!=null)
		{
			if(codeInfo[0].equals("1"))
			{
				if(codeInfo[1].equals("0"))
				{
					update("exchanged=1,exchange_time="+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr()), "code='"+code+"'");
					return new ReturnValue(true);
				}
				else
				{
					JSONObject returnJsonObj = new JSONObject();
					returnJsonObj.put("note", code+"已兑换过了");		
					return new ReturnValue(false,returnJsonObj.toString());
				}
			}
			else
			{
				JSONObject returnJsonObj = new JSONObject();
				returnJsonObj.put("note", "未分发的兑换码");				
				return new ReturnValue(false,returnJsonObj.toString());
			}
		}
		else
		{
			JSONObject returnJsonObj = new JSONObject();
			returnJsonObj.put("note", "兑换码不存在");		
			return new ReturnValue(false,returnJsonObj.toString());
		}				
	}
	/**
	 * 检验兑换码
	 * @param code
	 * @return
	 */
	public ReturnValue checkCode(String code,String phone)
	{			
		String[] codeInfo = getValues(new String[]{"published","exchanged"}, "code='"+code+"' and phone='"+phone+"'");
		if(codeInfo!=null)
		{
			if(codeInfo[0].equals("1"))
			{
				/*if(codeInfo[1].equals("0"))
				{
					update("exchanged=1,exchange_time="+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr()), "code='"+code+"'");
				}*/
				return new ReturnValue(true);
			}
			else
			{
				JSONObject returnJsonObj = new JSONObject();
				returnJsonObj.put("note", "未分发的兑换码");				
				return new ReturnValue(false,returnJsonObj.toString());
			}
		}
		else
		{
			JSONObject returnJsonObj = new JSONObject();
			returnJsonObj.put("note", "手机号码与兑换码不匹配");		
			return new ReturnValue(false,returnJsonObj.toString());
		}				
	}
	
	
	public ReturnValue getExchangeCode(String phone)
	{	
		JSONObject returnJsonObj = new JSONObject();
		synchronized (syncLock) 
		{
			if(phone==null || phone.equals(""))
			{
				returnJsonObj.put("note", "请提供手机号。");
				return new ReturnValue(false,returnJsonObj.toString());
			}
			if(phone.length()!=11)
			{
				returnJsonObj.put("note", "手机号必须11位");
				return new ReturnValue(false,returnJsonObj.toString());
			}
			//检查此手机号码是否已验证过或领取过
			String[] codeInfo = getValues(new String[]{"code","exchanged"}, "published=1 and phone='"+phone+"'");
			if(codeInfo!=null)
			{				
				if(codeInfo[1]!=null && codeInfo[1].equals("1")) //已兑换过
				{			
					String msg = "您的手机号码"+phone+"已兑换过礼品,不能重复领取";
					returnJsonObj.put("note", msg);
					LogBAC.logout("exchange", "短信："+msg);
					return new ReturnValue(false,returnJsonObj.toString());					
				}
				else	 //已领取过但是未兑换
				{		
					String msg = "您已经领取过昂口熊兑换码"+codeInfo[0]+",请关注口袋幻兽官方微信kdhsol，点击下方兑换码领取按钮，完成兑奖信息。";					
					returnJsonObj.put("note", msg);
					LogBAC.logout("exchange", "短信："+msg);
					return new ReturnValue(true,returnJsonObj.toString());		
				}
			}
			
			SqlString sqlStr = new SqlString();
			//找开放日期的
			sqlStr.add("exchanged", 0);
			sqlStr.add("published", 0);
			sqlStr.addWhere("(open_date is null or (to_char(sysdate,'yyyy')= to_char(open_date,'yyyy') and to_char(sysdate,'mm')= to_char(open_date,'mm') and to_char(sysdate,'dd')=to_char(open_date,'dd')))");
			JSONObject jsonObj = getTopJsonList("code", 1, sqlStr.whereString(), "id ASC");
			
			if(jsonObj!=null)
			{
				JSONArray arr = jsonObj.optJSONArray("list");
				if(arr.length()>0)
				{
					JSONObject line = arr.optJSONObject(0);
					String code = line.optString("code");
					update("phone='"+phone+"',PUBLISHED=1,publish_time="+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr()), "code='"+code+"'");
					String msg = "恭喜您，昂口熊兑换码为"+code+",请关注口袋幻兽官方微信kdhsol，点击下方兑换码领取按钮，完成兑奖信息。";
					returnJsonObj.put("note", msg);
					LogBAC.logout("activate", "phone="+phone+",短信："+msg);
					return new ReturnValue(true,returnJsonObj.toString());
				}
			}
			
			String msg = null;	
			
			if(dateAfter("2014-8-3")) //最后一天
			{
				msg = "可惜，昂口熊已经都被被领走了,关注官方微信kdhsol，会有更多精彩活动哦！";	
			}	
			else
			{
				msg = "客官，今日已经被领光，明日赶早。";	
			}
			returnJsonObj.put("note", msg);
			LogBAC.logout("exchange", "短信："+msg);
			return new ReturnValue(false,returnJsonObj.toString());
		}		
	}
	private boolean dateAfter(String compareDateStr)
	{
		if(compareDateStr==null || compareDateStr.equals(""))return false;
		Date compareDate = Tools.str2date(compareDateStr);
		Date now = new Date();
		Calendar cal = Calendar.getInstance();		
		cal.setTime(compareDate);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(now);
		if(cal2.after(cal))
		{
			return true;			
		}
		else
		{
			return false;
		}			
	}
}
