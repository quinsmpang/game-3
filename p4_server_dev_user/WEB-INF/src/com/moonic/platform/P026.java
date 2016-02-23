package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * 7k7k
 * @author 
 */
public class P026 extends P
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
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false, "帐号渠道" + platform + ",登录缺少扩展参数");
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
			return new ReturnValue(false, platform + "渠道,登录扩展参数异常");
		}
		String token = extendJson.optString("token");

		if (token.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数token=" + token);
			return new ReturnValue(false, platform + "渠道,缺少参数");
		}

		String url = "http://sdk.7k7k.com/oauth/check_user_result.php";

		String app_key = "6f9c8605bf10999a";

		//md5(md5(test.sdk.7k7k.com/oauth/check_user_result.php)accesstoken=fd01665ed80884cb7036da3968cd3756&appkey=f1436ab48781)
		String key = MD5.encode(MD5.encode("sdk.7k7k.com/oauth/check_user_result.php") + "accesstoken=" + token + "&appkey=" + app_key);

		String sendStr = "accesstoken=" + token + "&appkey=" + app_key + "&key=" + key;
		//LogBAC.logout("login/" + platform, "登录发给渠道数据=" + sendStr);

		url = url + "?" + sendStr;
		//LogBAC.logout("login/" + platform, "用户验证url=" + url);

		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				String result = "";
				try
				{
					result = new String(rv.binaryData, "UTF-8");
					LogBAC.logout("login/" + platform, "登录收到渠道数据=" + result);
					//{"code":1,"rows":{"userid":"532764337","username":"ygxf0h","token":"3e01b50918a0f4ead13924d2b584e2a3"}}

					JSONObject resultJson = new JSONObject(result);
					String code = resultJson.optString("code");
					String msg = resultJson.optString("msg");
					if (code.equals("1"))
					{
						JSONObject json_Temp = resultJson.optJSONObject("rows");
						username = json_Temp.optString("userid");
						LogBAC.logout("login/" + platform, "渠道返回username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						return new ReturnValue(false, "用户验证失败msg=" + msg);
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
