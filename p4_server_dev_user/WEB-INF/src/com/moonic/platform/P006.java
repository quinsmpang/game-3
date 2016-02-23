package com.moonic.platform;

import org.json.JSONObject;
import server.config.LogBAC;
import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.NetClient;

/**
 * 360
 * @author
 */
public class P006 extends P
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

		String token = extendJson.optString("token");

		if (token.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数token,extend=" + extend);
			return new ReturnValue(false, "platform" + platform + ",缺少参数token,extend=" + extend);
		}
		/*appid：202241421
		appkey：dddaeb2de31f7e4c5ac939cefd67dfcf
		appsecret：7a3670342a5bb51f4bce86ec9d8ca1fa（保密）*/

		String url = "https://openapi.360.cn/user/me.json";
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");
		String sendStr = "access_token=" + token + "&fields=" + "id,name";
		LogBAC.logout("login/" + platform, "登录发给渠道数据=" + sendStr);

		netClient.ignoreSSL();//加密
		netClient.setSendBytes(sendStr.getBytes());
		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				String result = "";
				try
				{
					result = new String(rv.binaryData, "UTF-8");
					//{"id":"308524290","name":"XHL_会飞的鱼","avatar":"http://u1.qhimg.com/qhimg/quc/48_48/22/02/55/220255dq9816.3eceac.jpg?f=9c73c1a60722c956cc51a18b4ea2f0ee"}
					LogBAC.logout("login/" + platform, "登录收到渠道数据=" + result);
					JSONObject resultJson = new JSONObject(result);
					if (resultJson.has("id"))
					{
						username = resultJson.optString("id");
						LogBAC.logout("login/" + platform, "渠道返回username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败msg=" + "没有用户id");
						return new ReturnValue(false, "用户验证失败msg=" + "没有用户id");
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
