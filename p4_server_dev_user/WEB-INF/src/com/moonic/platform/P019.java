package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * 丫丫玩
 * @author 
 */
public class P019 extends P {
	
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		if(extend==null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform="+platform+",缺少扩展参数platform="+platform+",username="+username+",ip="+ip);
			return new ReturnValue(false,"你的渠道"+platform+",登录缺少扩展参数");
		}
		
		JSONObject extendJson=null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch(Exception ex)
		{
			LogBAC.logout("login_error", "platform="+platform+",扩展参数异常extend="+extend);				
			return new ReturnValue(false,platform+"渠道,登录扩展参数异常");
		}
		username = extendJson.optString("username");
		String uid = extendJson.optString("uid");
		String token = extendJson.optString("token");

		if (uid.equals("") || token.equals(""))
		{
			LogBAC.logout("login_error", "platform="+platform+",缺少参数uid="+uid+",token="+token+",extend=" + extend);				
			return new ReturnValue(false,platform+"渠道,缺少参数");
		}
		//LogBAC.logout("login/" + platform, "uid=" + uid + ",username=" + username + ",token=" + token);

		String url = "http://passport.yayawan.com/oauth/userinfo";
		//LogBAC.logout("login/" + platform, "用户验证url=" + url);
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");

		String app_id = "4024916039";
		String yayawan_game_key = "63dc61f72a97e485625687335c1e8a57";
		String sendStr = "app_id=" + app_id + "&uid=" + uid + "&token=" + token;
		sendStr += "&sign=" + MD5.encode(token + "|" + yayawan_game_key);
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
					//String id = resultJson.optString("id");//乐乐号
					username = resultJson.optString("username");//用户名
					//String reg_time = resultJson.optString("reg_time");//注册时间 （since：1970-1-1 00:00:00）
					token = resultJson.optString("token");//接口访问令牌，原样返回
					String code = resultJson.optString("error_code");//错误码
					String msg = resultJson.optString("error_msg");//错误描述
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
					LogBAC.logout("login_error", "platform="+platform+",用户验证失败" + ex.toString()+",str="+new String(rv.binaryData, "UTF-8"));
					return new ReturnValue(false,"用户验证失败" + ex.toString());
				}
			}
			else
			{
				LogBAC.logout("login_error", "platform="+platform+",用户验证失败,数据格式异常");					
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
