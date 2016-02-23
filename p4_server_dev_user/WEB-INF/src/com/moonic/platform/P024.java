package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * 点金
 * @author 
 */
public class P024 extends P {
	
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false,"帐号渠道" + platform + ",登录缺少扩展参数");
		}
		//LogBAC.logout("login/" + platform, "收到用户验证数据" + extend);
		JSONObject extendJson = null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",扩展参数异常extend=" + extend);				
			return new ReturnValue(false,platform+"渠道,登录扩展参数异常");
		}
		username = extendJson.optString("username");
		String Uin = extendJson.optString("Uin");
		String SessionId = extendJson.optString("SessionId");

		if (Uin.equals("") || SessionId.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数Uin="+Uin+",SessionId="+SessionId+",extend="+extend);				
			return new ReturnValue(false,platform+"渠道,缺少参数");
		}
		//LogBAC.logout("login/" + platform, "Uin=" + Uin + ",username=" + username + ",SessionId=" + SessionId);

		String url = "http://pay.mdong.com.cn/phone/index.php/DeveloperServer/Index";
		int Act  = 3;//固定值3
		String AppId = "590";
		String app_key = "224a27a7043c0310688eb443e34a7749";
		String Version = "1.07";//固定值1.07
		String Sign = MD5.encode("Act="+Act+"&AppId="+AppId+"&SessionId="+SessionId+"&Uin="+Uin+"&Version="+Version+app_key);//MD5(Act=3&AppId=9&SessionId=d891b6f03f361128b10c69d440c92c34&Uin=1326&Version=1.07a123456789b123456789c123456789d1)其中红色部分为app_key， 请不要修改蓝色字体的顺序。
		//LogBAC.logout("login/" + platform, "用户验证url=" + url);
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");

		String sendStr = "Act="+Act+"&AppId="+AppId+"&SessionId="+SessionId+"&Uin="+Uin+"&Version="+Version+"&Sign="+Sign;
		//LogBAC.logout("login/" + platform, "sendStr=" + sendStr);
		netClient.setSendBytes(sendStr.getBytes());
		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData, "UTF-8");
					//LogBAC.logout("login/" + platform, "result=" + result);
					JSONObject resultJson = new JSONObject(result);
					String code = resultJson.optString("Error_Code");//错误码（0有效，1无效）
					String msg = resultJson.optString("Sign");//签名示例： MD5(Error_Code=1 a123456789b123456789c123456789d1)其中红色部分为app_key
					//LogBAC.logout("login/" + platform, "code=" + code);
					//LogBAC.logout("login/" + platform, "msg=" + msg);
					if (code.equals("0"))
					{
						//LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						return new ReturnValue(false,"用户验证失败msg=" + msg);
					}
				}
				catch (Exception ex)
				{
					LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败" + ex.toString()+",str="+ new String(rv.binaryData, "UTF-8"));
					return new ReturnValue(false,"用户验证失败" + ex.toString());
				}
			}
			else
			{
				LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败,数据格式异常");					
				return new ReturnValue(false,"用户验证失败,数据格式异常");
			}
		}
		else
		{	
			LogBAC.logout("login_error", "platform="+platform+",用户验证失败,"+rv.info);
			return new ReturnValue(false,"用户验证失败," + rv.info);
		}
	}
}
