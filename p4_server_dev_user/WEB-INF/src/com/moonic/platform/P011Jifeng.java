package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.NetClient;

/**
 * 机锋
 * @author 
 */
public class P011Jifeng extends P {
	
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		if(extend==null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform="+platform+",缺少扩展参数platform="+platform+",username="+username+",ip="+ip);
			return new ReturnValue(false,"帐号渠道"+platform+",登录缺少扩展参数");
		}
		//LogBAC.logout("login/" + platform, "收到用户验证数据" + extend);
		JSONObject extendJson=null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch(Exception ex)
		{
			LogBAC.logout("login_error","platform="+platform+",扩展参数异常extend="+extend);				
			return new ReturnValue(false,platform+"渠道,登录扩展参数异常");
		}
		
		username = extendJson.optString("username");
		String token = extendJson.optString("token");

		if (token.equals(""))
		{
			LogBAC.logout("login_error","platform="+platform+",缺少参数token="+token+",extend=" + extend);				
			return new ReturnValue(false,platform+"渠道,缺少参数");
		}
		
		String url = "http://api.gfan.com/uc1/common/verify_token";
		//LogBAC.logout("login/" + platform, "用户验证url=" + url);
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");

		String sendStr = "token=" + token;
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
					String code = resultJson.optString("resultCode");
					String msg = "";
					//LogBAC.logout("login/" + platform, "code=" + code);
					//LogBAC.logout("login/" + platform, "msg=" + msg);
					if (code.equals("1"))
					{
						//LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						if (code.equals("-1"))
						{
							msg = "参数为空";
						}
						else if (code.equals("-2"))
						{
							msg = "无效token";
						}
						//LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						return new ReturnValue(false,"用户验证失败msg=" + msg);
					}
				}
				catch (Exception ex)
				{
					LogBAC.logout("login_error","platform="+platform+",用户验证失败" + ex.toString()+",str="+new String(rv.binaryData, "UTF-8"));
					return new ReturnValue(false,"用户验证失败" + ex.toString());
				}
			}
			else
			{
				LogBAC.logout("login_error","platform="+platform+"用户验证失败,数据格式异常");					
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
