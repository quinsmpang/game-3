package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;
import com.moonic.util.HmacSHA1Encryption;
import com.moonic.util.NetClient;

/**
 * 小米
 * @author 
 */
public class P013 extends P
{

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		String appId = "2882303761517297618"; //appID
		String appKEY = "VrRECrXpmikY74KLUPQ2Yg==";//AppSecret			

		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数extend=" + extend);
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
			LogBAC.logout("login_error", "platform=" + platform + ",登录扩展参数异常extend=" + extend);
			//System.out.println("小米005扩展参数转json异常extend="+extend);
			///System.out.println(ex.toString());
			BACException.throwInstance(platform + "渠道,登录扩展参数异常");
		}
		String uid = extendJson.optString("uid");
		String sessionid = extendJson.optString("sessionid");
		username = extendJson.optString("username");

		if (uid.equals("") || sessionid.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数uid=" + uid + ",sessionid=" + sessionid + ",extend=" + extend);
			//BACException.throwInstance("缺少参数uid="+uid+",sessionid="+sessionid);
			BACException.throwInstance(platform + "渠道,缺少参数");
		}

		String str = "appId=" + appId + "&session=" + sessionid + "&uid=" + uid;

		String signature = HmacSHA1Encryption.hmacSHA1Encrypt(str, appKEY);
		//LogBAC.logout("charge/"+platform, "用户验证提交参数=appId="+appId+"&session="+sessionid+"&uid="+uid+"&signature="+signature);
		NetClient netClient = new NetClient();
		netClient.setAddress("http://mis.migc.xiaomi.com/api/biz/service/verifySession.do");
		netClient.addParameter("appId", appId);
		netClient.addParameter("session", sessionid);
		netClient.addParameter("uid", uid);
		netClient.addParameter("signature", signature);
		ReturnValue rv = netClient.send();
		//LogBAC.logout("charge/"+platform, "用户验证返回结果="+rv.success+" "+rv.info);
		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					JSONObject userjson = new JSONObject(new String(rv.binaryData, "UTF-8"));
					//LogBAC.logout("charge/"+platform, "用户验证成功返回结果="+userjson);
					//LogBAC.logout("charge/"+platform, "uc用户"+sid+"验证返回结果\r\n"+userjson);
					int errcode = userjson.optInt("errcode");
					String errMsg = userjson.optString("errMsg");

					if (errcode == 200)
					{
						//LogBAC.logout("login/"+platform, "登录成功username="+username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/" + platform, "用户验证失败：errcode=" + errcode + ",errMsg=" + errMsg);
						return new ReturnValue(false, "用户验证失败：errcode=" + errcode + ",errMsg=" + errMsg);
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
