package com.moonic.platform;

import org.json.JSONObject;
import server.config.LogBAC;
import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;
import com.moonic.util.Base64Anzhi;
import com.moonic.util.NetClient;

/**
 * 安智
 * @author 
 */
public class P017 extends P
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

		String uid = extendJson.optString("uid");
		String sid = extendJson.optString("sid");

		if (username == null || username.equals(""))
		{
			BACException.throwInstance("用户名不能为空");
		}

		if (uid.equals("") || sid.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数username=" + username + ",uid=" + uid + ",sid=" + sid + ",extend=" + extend);
			BACException.throwInstance(platform + "渠道,缺少参数");
		}

		String appKey = "14119186579x9gn5f8xF65QvE9pqve";
		String appSecret = "HW6rxJ25Irx6I189hw5Pl1Pt";
		String time = String.valueOf(System.currentTimeMillis() / 1000);

		String url = "http://user.anzhi.com/web/api/sdk/third/1/queryislogin";
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");

		//当前登录用户信息转化对应json格式
		//String gameUser = "{\"id\":3,\"loginname\":\"" + username + "\"}";
		//String msgTemp = "{'head':{'appkey':'" + appKey + "','version':'1.0','time':'" + time + "'},'body':{'msg':{'gameUser':'" + gameUser + "','time':'" + time + "'},'ext':{}}}";
		//LogBAC.logout("login/" + platform, "登录发给渠道数据=" + msgTemp);
		//String sendStr = Des3Util.encrypt(msgTemp, appSecret);
		String sign = Base64Anzhi.encodeToString(appKey + sid + appSecret);
		String sendStr = "time=" + time + "&appkey=" + appKey + "&sid=" + sid + "&sign=" + sign;
		LogBAC.logout("login/" + platform, "登录发给渠道数据=" + sendStr);

		//netClient.ignoreSSL();//加密
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
//					{
//						“sc”: “1”,
//						“st”: “成功(sid 有效) ” ，
//						“time”:”20130228101059123”
//						“msg” : {
//						“uid”:”123456789”
//						}
//						}
					LogBAC.logout("login/" + platform, "登录收到渠道数据=" + result);
					JSONObject resultJson = new JSONObject(result);
					if (resultJson.optString("sc").equals("1"))
					{
						LogBAC.logout("login/" + platform, "渠道返回username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败msg=" + resultJson.optString("st"));
						return new ReturnValue(false, "用户验证失败msg=" + resultJson.optString("st"));
						//return new ReturnValue(true, username);
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
