package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * 凤凰网
 * @author 
 */
public class P033 extends P {
	
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false,"你的渠道" + platform + ",登录缺少扩展参数");
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
			return new ReturnValue(false,platform + "渠道,登录扩展参数异常");
		}
		username = extendJson.optString("username");
		String userId = extendJson.optString("userid");

		String partner_key = "p4gh1nsvxlety6by7w";

		String service = "user.validate";
		String partner_id = "1052";
		String game_id = "100590";
		String server_id = "1";
		String ticket = extendJson.optString("ticket");
//		LogBAC.logout("login/" + platform, "签名前=" + (partner_id + game_id + server_id + ticket + partner_key));
		String sign = MD5.encode(partner_id + game_id + server_id + ticket + partner_key).toUpperCase();
		String formart = "json";

		if (userId.equals("") || ticket.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数userId=" + userId + ",ticket=" + ticket + ",extend=" + extend);
			return new ReturnValue(false,platform + "渠道,缺少参数");
		}
		//LogBAC.logout("login/" + platform, "Uin=" + Uin + ",username=" + username + ",SessionId=" + SessionId);

		String url = "http://union.play.ifeng.com/mservice2";
		//LogBAC.logout("login/" + platform, "用户验证url=" + url);
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");

		String sendStr = "service=" + service + "&partner_id=" + partner_id + "&game_id=" + game_id + "&server_id=" + server_id + "&ticket=" + ticket + "&sign=" + sign + "&formart" + formart;
//		LogBAC.logout("login/" + platform, "sendStr=" + sendStr);
		netClient.setSendBytes(sendStr.getBytes());
		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData, "UTF-8");
					//LogBAC.logout("login/" + platform, "result=" + result);
					JSONObject resultJson = new JSONObject(result);
					String code = resultJson.optString("code");//错误码（1有效）
					String msg = resultJson.optString("msg");
					//LogBAC.logout("login/" + platform, "code=" + code);
					//LogBAC.logout("login/" + platform, "msg=" + msg);
					if (code.equals("1"))
					{
						//LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						return new ReturnValue(false,"用户验证失败code=" + code + ",msg=" + msg);
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
