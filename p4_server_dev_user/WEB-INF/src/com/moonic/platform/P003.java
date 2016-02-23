package com.moonic.platform;

import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.BACException;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * PP助手
 * 2016-01-20 13:30:00 PP助手
 * @author 
 */
public class P003 extends P {
	
	@Override
	public JSONObject login(String channel, String extend, String username, String password, String ip, String imei, String mac, int loginport, SqlString userSqlStr) throws Exception
	{
		JSONObject returnobj = super.login(channel, extend, username, password, ip, imei, mac, loginport, userSqlStr);
		returnobj.put("channeldata", returnobj.optString("username"));
		return returnobj;
	}
	
	@Override
	public ReturnValue checkLogin(String username, String extend, String ip)
			throws Exception {
		if (extend == null || extend.equals("")) {
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数extend=" + extend);
			BACException.throwInstance("帐号渠道" + platform + ",登录缺少扩展参数");
		}
		JSONObject extendJson = null;
		try {
			extendJson = new JSONObject(extend);
		} catch (Exception ex) {
			LogBAC.logout("login_error", "platform=" + platform + ",扩展参数异常extend=" + extend);
			BACException.throwInstance(platform + "渠道,登录扩展参数异常");
		}
		/*
		 {
			"id":1330395827,
			"service":"account.verifySession",
			"data":{"sid":"80a5fe53d3540300005a17e308a4b1fb"},
			"game":{"gameId":12345},
			"encrypt":"md5",
			"sign":"6e9c3c1e7d99293dfc0c81442f9a9984"
		 }
			sign 的签名规则：MD5(sid=... + AppKey)（去掉 + 替换 ... 为实际sid 值）
		 */
		// TODO 缺少三个参数：sid由客户端发送过来，appKey和gameId需要申请
		String sid = extendJson.optString("sid");
		if (sid.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数sid,extend=" + extend);
			BACException.throwInstance(platform + "渠道,缺少sid参数");
		}
		int gameId = 7306;
		String appKey = "8379b85e6a00eef657ce76fccaa43e81";

		JSONObject json = new JSONObject();
		json.setForceLowerCase(false);
		json.put("id", Tools.getSyncMillisecond() / 1000);	//PP助手 时间戳不能超过10位
		json.put("service", "account.verifySession");

		JSONObject dataJson = new JSONObject();
		dataJson.put("sid", sid);
		json.put("data", dataJson);

		JSONObject gameJson = new JSONObject();
		gameJson.setForceLowerCase(false);
		gameJson.put("gameId", gameId);
		json.put("game", gameJson);

		String signSource = "sid=" + sid + appKey;//组装签名原文
		LogBAC.logout("login/" + platform, "[签名原文]" + signSource);

		String sign = MD5.encode(signSource);
		LogBAC.logout("login/" + platform, "[签名结果]" + sign);
		
		json.put("encrypt", "md5");
		json.put("sign", sign);
		LogBAC.logout("login/" + platform, "uc用户" + sid + "验证发送内容\r\n" + json.toString());
		LogBAC.logout("login/" + platform, "登录发送：" + json.toString());
		NetClient netClient = new NetClient();
		String url = "http://passport_i.25pp.com:8080/account?tunnel-command=2852126760";
		netClient.setAddress(url); //正式环境  
		netClient.setSendBytes(json.toString().getBytes("UTF-8"));
		ReturnValue rv = netClient.send();
		
		if (rv.success) {
			if (rv.dataType == ReturnValue.TYPE_BINARY) {
				try {
					JSONObject userjson = new JSONObject(new String(rv.binaryData, "UTF-8"));
					LogBAC.logout("login/" + platform, "uc用户" + sid + "验证返回结果\r\n" + userjson);
					JSONObject stateJson = userjson.optJSONObject("state");
					JSONObject dataJson2 = userjson.optJSONObject("data");
					int state = stateJson.optInt("code");
					String msg = stateJson.optString("msg");
					LogBAC.logout("login/" + platform, "登录成功返回：" + userjson.toString());
					if (state == 1) {
						String accountId = dataJson2.optString("accountId");
						String creator = dataJson2.optString("creator");
						//请一定注意，新的验证接口协议已调整，使用creator+accountId做唯一标识！
						username = creator + accountId;
						LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					} else {
						LogBAC.logout("login/" + platform, "用户验证失败：" + msg);
						return new ReturnValue(false, "用户验证失败：" + msg);
					}
				} catch (Exception e) {
					LogBAC.logout("login_error", "platform=" + platform + ",用户验证异常" + e.toString() + ",str=" + new String(rv.binaryData, "UTF-8"));
					return new ReturnValue(false, "用户验证失败" + e.toString());
				}
			} else {
				LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败,数据格式异常");
				return new ReturnValue(false, "用户验证失败,数据格式异常");
			}
		} else {
			LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败," + rv.info);
			return new ReturnValue(false, "用户验证失败," + rv.info);
		}
	}
	
	/*
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数extend=" + extend);
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
		String sid = extendJson.optString("sid");

		if (sid.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数sid,extend=" + extend);
			BACException.throwInstance(platform + "渠道,缺少sid参数");
		}

		int cpId = 25494;
		int gameId = 552716;
		int serverId = 0;
		int channelId = 2;
		String apikey = "47e35a04a8dadee9389c49527af76d4c";

		JSONObject json = new JSONObject();
		json.setForceLowerCase(false);
		json.put("id", Tools.getSyncMillisecond());
		json.put("service", "account.verifySession");

		JSONObject dataJson = new JSONObject();
		dataJson.put("sid", sid);
		json.put("data", dataJson);

		JSONObject gameJson = new JSONObject();
		gameJson.setForceLowerCase(false);
		gameJson.put("gameId", gameId);
		json.put("game", gameJson);

		String signSource = "sid=" + sid + apikey;//组装签名原文
		LogBAC.logout("login/" + platform, "[签名原文]" + signSource);

		String sign = MD5.encode(signSource);
		LogBAC.logout("login/" + platform, "[签名结果]" + sign);

		json.put("sign", sign);
		LogBAC.logout("login/" + platform, "uc用户" + sid + "验证发送内容\r\n" + json.toString());
		LogBAC.logout("login/" + platform, "登录发送：" + json.toString());
		NetClient netClient = new NetClient();
		netClient.setAddress("http://sdk.g.uc.cn/cp/account.verifySession"); //正式环境  
		netClient.setSendBytes(json.toString().getBytes("UTF-8"));
		ReturnValue rv = netClient.send();
		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					JSONObject userjson = new JSONObject(new String(rv.binaryData, "UTF-8"));
					LogBAC.logout("login/" + platform, "uc用户" + sid + "验证返回结果\r\n" + userjson);
					JSONObject stateJson = userjson.optJSONObject("state");
					JSONObject dataJson2 = userjson.optJSONObject("data");
					int state = stateJson.optInt("code");
					String msg = stateJson.optString("msg");
					LogBAC.logout("login/" + platform, "登录成功返回：" + userjson.toString());
					if (state == 1)
					{
						username = dataJson2.optString("accountId");
						LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败：" + msg);
						return new ReturnValue(false, "用户验证失败：" + msg);
					}
				}
				catch (Exception ex)
				{
					LogBAC.logout("login_error", "platform=" + platform + ",用户验证异常" + ex.toString() + ",str=" + new String(rv.binaryData, "UTF-8"));
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
*/
}