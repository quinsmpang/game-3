package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;

/**
 * 云点
 * @author 
 */
public class P028yundian extends P {
	
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
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
		username = extendJson.optString("username");

		if (username.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数uid=" + username + ",extend=" + extend);
			BACException.throwInstance(platform + "渠道,缺少参数");
		}
		
		return new ReturnValue(true, username);

		//String platform_code = "2f642b2";//厂商编号  string 不能为空
		/*NetClient netClient = new NetClient();
		netClient.setAddress("http://api.cpo2o.com/v1/API/PayLogin/otherVerifyUser");
		netClient.addParameter("uid", username);
		//netClient.addParameter("platform_code", platform_code);
		ReturnValue rv = netClient.send();
		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData, "UTF-8");
					//LogBAC.logout("login_error", "platform=" + platform + ",result=" + result);
					JSONObject resultJson = new JSONObject(result);
					String code = resultJson.optString("code");//状态码 100000表示成功，其它表示失败
					String msg = resultJson.optString("msg");//返回消息
					//LogBAC.logout("login/" + platform, "code=" + code);
					//LogBAC.logout("login/" + platform, "msg=" + msg);
					if (code.equals("100000")) 
					{
						//LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						BACException.throwInstance("用户验证失败msg=" + msg + ",uid=" + username);
					}
				}
				catch (Exception ex)
				{
					LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败" + ex.toString() + ",str=" + new String(rv.binaryData, "UTF-8") + ",uid=" + username);
					BACException.throwInstance("用户验证失败" + ex.toString());
				}
			}
			else
			{
				LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败,数据格式异常" + ",uid=" + username);
				BACException.throwInstance("用户验证失败,数据格式异常");
			}
		}
		else
		{
			LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败," + rv.info + ",uid=" + username);
			BACException.throwInstance("用户验证失败," + rv.info);
		}*/
	}
}
