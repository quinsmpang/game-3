package com.moonic.platform;

import org.json.JSONObject;
import server.config.LogBAC;
import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * 37玩
 * @author 
 */
public class P028 extends P
{

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			BACException.throwInstance("帐号渠道" + platform + ",登录缺少扩展参数");
		}
		JSONObject extendJson = null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",扩展参数异常extend=" + extend);
			BACException.throwInstance(platform + "渠道,登录扩展参数异常");
		}
		long time = System.currentTimeMillis() / 1000;

		username = extendJson.optString("username");
		String token = extendJson.optString("token");
		String appId = extendJson.optString("appid");
		String appKey = extendJson.optString("appkey");

		//LogBAC.logout("login/" + platform, "签名之前=" + "{$" + appId + "}" + "{$" + time + "}" + "{$" + appKey + "}");

		String sign = MD5.encode(appId + time + appKey).toLowerCase();

		//LogBAC.logout("login/" + platform, "签名之后=" + sign);

		if (username.equals("") || token.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数uid=" + username + ",extend=" + extend);
			BACException.throwInstance(platform + "渠道,缺少参数");
		}
		String uidCheckUrl = "http://vt.api.m.37.com/verify/token/";
		String url = uidCheckUrl + "?pid=1&gid=" + appId + "&time=" + time + "&sign=" + sign + "&token=" + token;
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
					JSONObject resultJson = new JSONObject(result);
					LogBAC.logout("login/" + platform, "渠道返回result=" + result + "===data = " + resultJson.optString("msg"));
					if (resultJson.optInt("state") == 1)
					{
						//username = resultJson.optString("openid");
						LogBAC.logout("login/" + platform, "渠道返回username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败msg=" + "sign签名错误");
						return new ReturnValue(false, "用户验证失败msg=" + "sign签名错误");
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
