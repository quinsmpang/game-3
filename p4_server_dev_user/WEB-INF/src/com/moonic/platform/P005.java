package com.moonic.platform;

import java.io.ByteArrayInputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;
import server.config.LogBAC;
import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.NetClient;

/**
 * lenovo
 * @author 
 */
public class P005 extends P
{

	public JSONObject login(String channel, String extend, String username, String password, String ip, String imei, String mac, int loginport, SqlString userSqlStr) throws Exception
	{
		JSONObject returnobj = super.login(channel, extend, username, password, ip, imei, mac, loginport, userSqlStr);
		returnobj.put("channeldata", returnobj.optString("username"));
		return returnobj;
	}

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数extend=" + extend + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false, "platform" + platform + ",缺少扩展参数extend=" + extend);
		}
		JSONObject extendJson = null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数extend=" + extend + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false, "platform" + platform + ",缺少扩展参数extend=" + extend);
		}

		String ticket = extendJson.optString("ticket");
		String appId = extendJson.optString("appId");

		if (ticket.equals("") || appId.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数token,extend=" + extend);
			return new ReturnValue(false, "platform" + platform + ",缺少参数token,extend=" + extend);
		}

		String uidCheckUrl = "http://passport.lenovo.com/interserver/authen/1.2/getaccountid";
		String url = uidCheckUrl + "?lpsust=" + ticket + "&realm=" + appId;
		NetClient netClient = new NetClient();

		netClient.setAddress(url);
		LogBAC.logout("login/" + platform, "登录发给渠道数据=" + url);
		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				String result = "";
				try
				{
					result = new String(rv.binaryData, "UTF-8");
					//<?xml version="1.0" encoding="UTF-8"?><IdentityInfo><AccountID>10012674129</AccountID><Username>15000689240</Username><DeviceID>359543054034539</DeviceID><verified>1</verified></IdentityInfo>
					//LogBAC.logout("login/" + platform, "登录收到渠道数据=" + result);
					ByteArrayInputStream bais = new ByteArrayInputStream(rv.binaryData);
					SAXReader saxReader = new SAXReader();
					Document document = saxReader.read(bais);
					Element root = document.getRootElement();
					//Element verified = root.element("verified");
					Element AccountID = root.element("AccountID");
					//LogBAC.logout("login/"+platform, "AccountID="+AccountID);

					if (AccountID != null)
					{
						username = AccountID.getText();
						LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/"+platform, "用户验证失败xml="+resultXml);
						return new ReturnValue(false, "用户验证失败");
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
