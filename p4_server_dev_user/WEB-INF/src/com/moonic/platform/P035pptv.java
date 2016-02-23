package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.NetClient;

public class P035pptv extends P {
	
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false,"帐号渠道" + platform + ",登录缺少扩展参数");
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
		String pptvusername = extendJson.optString("pptvusername");
		String sessionid = extendJson.optString("sessionid");

		if (username.equals("") || pptvusername.equals("") || sessionid.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数username=" + username + ",pptvusername=" + pptvusername + ",sessionid=" + sessionid + ",extend=" + extend);
			return new ReturnValue(false,platform + "渠道,缺少参数");
		}

		String url = "http://user.vas.pptv.com/api/c/v2/cksession.php";
		String type = "login";
		String app = "mobgame";
		String sendStr = "type=" + type + "&sessionid=" + sessionid + "&username=" + pptvusername + "&app=" + app;
		//LogBAC.logout("login/" + platform, "sendStr=" + sendStr);
		String urlPath = url + "?" + sendStr;
		LogBAC.logout("login/" + platform, "用户验证url=" + urlPath);
		NetClient netClient = new NetClient();
		netClient.setAddress(urlPath);

		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData, "UTF-8");
					LogBAC.logout("login/" + platform, "result=" + result);
					JSONObject resultJson = new JSONObject(result);
					String code = resultJson.optString("status");//0 验证不通过,1 验证通过
					//String msg = resultJson.optString("message");
					if (code.equals("1"))
					{
						//LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						return new ReturnValue(false,"用户验证失败result=" + result);
					}
				}
				catch (Exception ex)
				{
					LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败" + ex.toString() + ",str=" + new String(rv.binaryData, "UTF-8"));
					return new ReturnValue(false,"用户验证失败" + ex.toString());
				}
			}
			else
			{
				LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败,数据格式异常");
				return new ReturnValue(false,"用户验证失败,数据格式异常");
			}
		}
		else
		{
			LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败," + rv.info);
			return new ReturnValue(false,"用户验证失败," + rv.info);
		}
	}
}
