package com.moonic.platform;

import java.net.URLEncoder;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;
import com.moonic.util.NetClient;

/**
 * 豌豆荚
 * @author 
 */
public class P011 extends P
{

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			BACException.throwInstance("你的渠道" + platform + ",登录缺少扩展参数");
		}
		//LogBAC.logout("charge/"+platform, "收到用户验证数据"+extend);
		JSONObject extendJson = null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",extend=" + extend);
			//System.out.println("豌豆荚006扩展参数转json异常extend="+extend);
			//System.out.println(ex.toString());
			BACException.throwInstance(platform + "渠道,登录扩展参数异常");
		}
		String uid = extendJson.optString("uid");
		String token = extendJson.optString("token");
		username = extendJson.optString("username");

		if (uid.equals("") || token.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数uid=" + uid + ",token=" + token + ",extend=" + extend);
			//BACException.throwInstance("缺少参数");
			BACException.throwInstance(platform + "渠道,缺少参数");
		}

		String uidCheckUrl = "https://pay.wandoujia.com/api/uid/check";
		String url = uidCheckUrl + "?uid=" + uid + "&token=" + URLEncoder.encode(token, "UTF-8");
		//LogBAC.logout("charge/"+platform, "用户验证url="+url);
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		ReturnValue rv = netClient.send();
		//LogBAC.logout("charge/" + platform, "用户验证返回结果=" + rv.success + " " + rv.info);
		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData, "UTF-8");
					if (result.equals("true"))
					{
						//LogBAC.logout("login/"+platform, "登录成功username="+username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/" + platform, "用户验证失败 username=" + username + ",binaryData = " + new String(rv.binaryData, "UTF-8"));
						return new ReturnValue(false, "用户验证失败");
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
