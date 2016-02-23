package com.moonic.platform;

import org.json.JSONException;
import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * 当乐
 * @author 
 */
public class P012 extends P
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
			BACException.throwInstance("当乐" + platform + "登录扩展参数异常");
		}
		String app_id = extendJson.optString("app_id");
		String mid = extendJson.optString("mid");
		String token = extendJson.optString("token");

		if (app_id.equals("") || mid.equals("") || token.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数app_id=" + app_id + ",mid=" + mid + ",token=" + token + ",extend=" + extend);
			BACException.throwInstance(platform + "渠道,缺少参数");
		}
		String appKey = "MY69b7JY";
		NetClient netClient = new NetClient();
		netClient.setAddress("http://connect.d.cn/open/member/info/");
		netClient.addParameter("app_id", app_id);
		netClient.addParameter("mid", mid);
		netClient.addParameter("token", token);
		netClient.addParameter("sig", MD5.encode(token + "|" + appKey));
		ReturnValue rv = netClient.send();
		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				/*成功
				 {
					"memberId":32608510,
					"username":"ym1988ym",
					"nickname":"当乐_小牧",
					"gender":"男",
					"level":11, "avatar_url":"http://d.cn/images/item/35/002.gif",
					"created_date":1346140985873,
					"token":"F9A0F6A0E0D4564F56C483165A607735FA4F324",
					"error_code":0
					}
					失败
					{
					"error_code":211,
					"error_msg":"app_key错误"
					}*/
				try
				{
					JSONObject dljson = new JSONObject(new String(rv.binaryData, "UTF-8"));
					//System.out.println("当乐用户验证返回"+dljson.toString());
					String dlUsername = dljson.getString("username");
					String error_code = dljson.getString("error_code");
					String error_msg = dljson.getString("error_msg");
					if (error_code.equals("0"))
					{
						//LogBAC.logout("login/"+platform, "登录成功username="+dlUsername);
						return new ReturnValue(true, dlUsername);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败,error_code=" + error_code + ",error_msg=" + error_msg + ",dlUsername=" + dlUsername);
						//用户验证失败
						return new ReturnValue(false, "用户验证失败,error_code=" + error_code + ",error_msg=" + error_msg);
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
					LogBAC.logout("login_error", "platform=" + platform + ",e=" + e.toString() + ",str=" + new String(rv.binaryData, "UTF-8"));
					//LogBAC.logout("login/"+platform, "用户验证失败,"+e.toString());
					return new ReturnValue(false, "用户验证失败," + e.toString());
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
