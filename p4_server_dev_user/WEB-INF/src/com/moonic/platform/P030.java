package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.MD5;

/**
 * pps
 * @author 
 */
public class P030 extends P {
	
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false,"你的渠道" + platform + ",登录缺少扩展参数");
		}
		JSONObject extendJson = null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",扩展参数异常extend=" + extend);
			return new ReturnValue(false,platform + "渠道,登录扩展参数异常");
		}
		username = extendJson.optString("username");
		String uid = extendJson.optString("uid");
		String time = extendJson.optString("time");
		String fromSign = extendJson.optString("sign");

		if (username.equals("") || uid.equals("") || time.equals("") || fromSign.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数username=" + username + ",uid=" + uid + ",time=" + time + ",fromSign=" + fromSign + ",extend=" + extend);
			return new ReturnValue(false,platform + "渠道,缺少参数");
		}

		String key = "74974bf301ff7e270d0e1e6860735f38";
		String sign = MD5.encode(uid + "&" + time + "&" + key);
//		LogBAC.logout("login_error", "platform=" + platform + ",fromSign=" + fromSign + ",sign=" + sign);
		if (fromSign.equals(sign))
		{
			return new ReturnValue(true, username);
		}
		else
		{
			return new ReturnValue(false,"用户验证失败fromSign=" + fromSign + ",sign=" + sign + "uid=" + uid + ",time=" + time + ",key=" + key);
		}
	}
}
