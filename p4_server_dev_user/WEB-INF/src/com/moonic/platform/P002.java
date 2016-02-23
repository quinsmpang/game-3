package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.NetClient;

/**
 * XY助手
 * @author 
 */
public class P002 extends P
{
	@Override
	public ReturnValue checkLogin(String username, String extend, String ip)
			throws Exception {
		if (extend == null || extend.equals("")) {
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false, "帐号渠道" + platform + ",登录缺少扩展参数");
		}
		JSONObject extendJson = null;
		try {
			extendJson = new JSONObject(extend);
		} catch (Exception ex) {
			LogBAC.logout("login_error", "platform=" + platform + ",扩展参数异常extend=" + extend);
			return new ReturnValue(false, platform + "渠道,登录扩展参数异常");
		}
		int uid = extendJson.optInt("uid");
//		int appid = extendJson.optInt("appid");
		int appid = 100027347;
		String token = extendJson.optString("token");
		if (uid < 0 || token.equals("")) {
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数uid=" + uid + ",token=" + token + ",extend=" + extend);
			return new ReturnValue(false, platform + "渠道,缺少参数");
		}
		String url = "http://passport.xyzs.com/checkLogin.php";
		NetClient netClient = new NetClient();
		String sendStr = "uid=" + uid + "&appid=" + appid + "&token=" + token;
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");
		
		netClient.setSendBytes(sendStr.getBytes());
		ReturnValue rv = netClient.send();

		if (rv.success) {
			if (rv.dataType == ReturnValue.TYPE_BINARY) {
				String result = new String(rv.binaryData, "UTF-8");
				LogBAC.logout("login/" + platform, "result=" + result);
				JSONObject resultJson = new JSONObject(result);
				
				String ret = resultJson.optString("ret");
				String error = resultJson.optString("error");
				if (ret.equals("0")) {
					LogBAC.logout("login/" + platform, "登录成功username=" + username + ",uid=" + uid);
					return new ReturnValue(true, String.valueOf(uid));
				} else {
					LogBAC.logout("login/" + platform, "用户验证失败error=" + error);
					return new ReturnValue(false, "用户验证失败error=" + error);
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
}
