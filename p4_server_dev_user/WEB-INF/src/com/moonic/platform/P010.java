package com.moonic.platform;

import org.json.JSONObject;
import server.config.LogBAC;
import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.NetClient;

/**
 * coolpad
 * @author 
 */
public class P010 extends P
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

		String authCode = extendJson.optString("authCode");
		String appId = "5000000450";//extendJson.optString("appId");
		String appKey = "b486d0fd12b74904bcb76747fe560703";//extendJson.optString("appKey");
		String notifyUrl = extendJson.optString("notifyUrl");

		if (authCode.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数token,extend=" + extend);
			return new ReturnValue(false, "platform" + platform + ",缺少参数token,extend=" + extend);
		}

		String uidCheckUrl = "https://openapi.coolyun.com/oauth2/token";
		String url = uidCheckUrl + "?grant_type=authorization_code" + "&client_id=" + appId + "&redirect_uri=" + appKey + "&client_secret=" + appKey + "&code=" + authCode;
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		LogBAC.logout("login/" + platform, "登录发给渠道数据=" + url);
		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				String result = "";
				try
				{
					result = new String(rv.binaryData, "UTF-8");
					//{"openid":"103400","expires_in":"2592000","refresh_token":"0.b6037940ec15224e3f711fae3b9fbb74","access_token":"0.e10adc3949ba59abbe56e057f20f883e.cf0243b77a2ba505a1ab2c8c82574736.1410342326163"}
					LogBAC.logout("login/" + platform, "登录收到渠道数据=" + result);
					JSONObject resultJson = new JSONObject(result);
					if (resultJson.has("openid"))
					{
						username = resultJson.optString("openid");
						LogBAC.logout("login/" + platform, "渠道返回username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败msg=" + "没有用户openid");
						//return new ReturnValue(true, "18878963");
						return new ReturnValue(false, "用户验证失败msg=" + "没有用户openid");
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
