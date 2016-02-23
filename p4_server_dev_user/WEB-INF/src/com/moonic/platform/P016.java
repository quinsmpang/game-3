package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.NetClient;

/**
 * 应用汇
 * @author 
 */
public class P016 extends P
{
	public JSONObject login(String channel, String extend, String username, String password, String ip, String imei, String mac, int loginport, SqlString userSqlStr) throws Exception
	{
		JSONObject returnobj = super.login(channel, extend, username, password, ip, imei, mac, loginport, userSqlStr);
		returnobj.put("channeldata", returnobj.optString("username"));
		return returnobj;
	}

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数extend=" + extend + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false, "platform" + platform + ",缺少扩展参数extend=" + extend);
		}
		JSONObject extendJson = null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数extend=" + extend + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false, "platform" + platform + ",缺少扩展参数extend=" + extend);
		}

		String ticket = extendJson.optString("ticket");
		String app_id = extendJson.optString("app_id");
		String app_key = extendJson.optString("app_key");

		if (ticket.equals("") || app_id.equals("") || app_key.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数token,extend=" + extend);
			return new ReturnValue(false, "platform" + platform + ",缺少参数token,extend=" + extend);
		}

		String uidCheckUrl = "http://api.appchina.com/appchina-usersdk/user/get.json";
		String url = uidCheckUrl + "?app_id=" + app_id + "&app_key=" + app_key + "&ticket=" + ticket;
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		//LogBAC.logout("login/" + platform, "登录发给渠道数据=" + url);
		netClient.setContentType("application/x-www-form-urlencoded");
		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				String result = "";
				try
				{
					result = new String(rv.binaryData, "UTF-8");

					//{ "data": {"nick_name": "jakyzhang", "user_name": null, "phone": null, "avatar_url": null, "email": "jakyzhang@live.com", "ticket": "e84b04d2-8866-11e1-8501-782bcb60a987", "state": "state", "user_id": 16058, "actived": true }, "status": 0, "message": "OK" }
					//LogBAC.logout("login/" + platform, "登录收到渠道数据=" + result);
					JSONObject resultJson = new JSONObject(result);
					JSONObject json = resultJson.optJSONObject("data");
					if (resultJson.optString("message").equals("OK"))
					{
						username = json.optString("user_id");
						LogBAC.logout("login/" + platform, "渠道返回username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败msg=" + "没有用户user_id");
						return new ReturnValue(false, "用户验证失败msg=" + "没有用户user_id");
					}
				}
				catch (Exception ex)
				{
					LogBAC.logout("login_error", "platform=" + platform + ",用户验证异常ex=" + ex.toString() + ",登录收到渠道数据=" + result);
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
