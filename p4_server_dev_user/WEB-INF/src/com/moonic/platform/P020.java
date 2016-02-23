package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * pada
 * @author 
 */
public class P020 extends P {
	
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
			return new ReturnValue(false,platform+"渠道,登录扩展参数异常");
		}
		username = extendJson.optString("username");
		String roleId = extendJson.optString("roleId");
		String roleToken = extendJson.optString("roleToken");

		if (roleId.equals("") || roleToken.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数roleId="+roleId+",roleToken="+roleToken+",extend=" + extend);							
			return new ReturnValue(false,platform+"渠道,缺少参数");
		}
		//LogBAC.logout("login/" + platform, "roleId=" + roleId + ",username=" + username + ",roleToken=" + roleToken);

		String url = "http://uac.svc.pada.cc/authRoleToken";
		String appId = "101022";
		String appKey = "af388244b54f74a11c93ba4c171fef1d";
		//MD5_32(appId=123&roleId=123&roleToken=123&AppToken)
		String sendStr = "appId=" + appId + "&roleId=" + roleId + "&roleToken=" + roleToken;
		//LogBAC.logout("login/" + platform, "sendStr=" + sendStr);
		String urlPath = url + "?" + sendStr + "&sign=" + MD5.encode(sendStr + "&" + appKey);
		//LogBAC.logout("login/" + platform, "用户验证url=" + urlPath);
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
					//LogBAC.logout("login/" + platform, "result=" + result);
					JSONObject resultJson = new JSONObject(result);
					String code = resultJson.optString("rescode");//返回码，数字型，0=成功，1=roleToken错误，2=appId错误，3=roleId错误，4=sign错误，5=其它错误
					String msg = resultJson.optString("resmsg");//返回消息
					//LogBAC.logout("login/" + platform, "code=" + code);
					//LogBAC.logout("login/" + platform, "msg=" + msg);
					if (code.equals("0"))
					{
						//LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						return new ReturnValue(false,"用户验证失败msg=" + msg);
					}
				}
				catch (Exception ex)
				{
					LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败" + ex.toString()+",str="+new String(rv.binaryData, "UTF-8"));
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
			LogBAC.logout("login_error", "platform="+platform+",用户验证失败,"+rv.info);
			return new ReturnValue(false,"用户验证失败," + rv.info);
		}
	}
}
