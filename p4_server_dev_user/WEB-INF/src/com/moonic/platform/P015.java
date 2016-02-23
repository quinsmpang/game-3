package com.moonic.platform;

import org.json.JSONObject;
import server.config.LogBAC;
import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;

/**
 * n多
 * @author 
 */
public class P015 extends P
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

		String token = extendJson.optString("token");

		if (username == null || username.equals(""))
		{
			BACException.throwInstance("用户名不能为空");
		}

		if (token.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数username=" + username + ",token=" + token + ",extend=" + extend);
			BACException.throwInstance(platform + "渠道,缺少参数");
		}
		return new ReturnValue(true, username);
	}
}
