package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.NetClient;

/**
 * 木蚂蚁
 * @author 
 */
public class P023 extends P
{

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false, "帐号渠道" + platform + ",登录缺少扩展参数");
		}
		JSONObject extendJson = null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",扩展参数异常extend=" + extend);
			return new ReturnValue(false, platform + "渠道,登录扩展参数异常");
		}
		username = extendJson.optString("username");
		String token = extendJson.optString("token");
		String uid = extendJson.optString("uid");

		if (username.equals("") || token.equals("") || uid.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数username=" + username + ",token=" + token + ",uid=" + uid + ",extend=" + extend);
			return new ReturnValue(false, platform + "渠道,缺少参数");
		}

		String url = "http://pay.mumayi.com/user/index/validation";
		String sendStr = "token=" + token + "&uid=" + uid;
		//LogBAC.logout("login/" + platform, "sendStr=" + sendStr);
		String urlPath = url + "?" + sendStr;
		LogBAC.logout("login/" + platform, "用户验证url=" + urlPath);
		NetClient netClient = new NetClient();
		netClient.setAddress(urlPath);

		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData, "UTF-8");
					LogBAC.logout("login/" + platform, "result=" + result);
					//String msg = resultJson.optString("ErrorDesc");
					if (result.equals("success"))
					{
						//LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						return new ReturnValue(false, "用户验证失败result=" + result);
					}
				}
				catch (Exception ex)
				{
					LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败" + ex.toString() + ",str=" + new String(rv.binaryData, "UTF-8"));
					return new ReturnValue(false, "用户验证失败" + ex.toString());
				}
			}
			else
			{
				LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败,数据格式异常");
				return new ReturnValue(false, "用户验证失败,数据格式异常");
			}
		}
		else
		{
			LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败," + rv.info);
			return new ReturnValue(false, "用户验证失败," + rv.info);
		}
	}
}
