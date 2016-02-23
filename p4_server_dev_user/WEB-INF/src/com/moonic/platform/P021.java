package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.NetClient;

/**
 * 3G
 * @author 
 */
public class P021 extends P
{

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false, "帐号渠道" + platform + ",登录缺少扩展参数");
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
			//System.out.println("3G扩展参数转json异常extend="+extend);
			//System.out.println(ex.toString());
			return new ReturnValue(false, platform + "渠道,登录扩展参数异常");
		}

		username = extendJson.optString("username");
		String uid = extendJson.optString("uid");
		String sessionid = extendJson.optString("sessionid");
		String cpid = extendJson.optString("cpid");
		String gameid = extendJson.optString("gameid");

		/*cpid: 2664
		gameid: 2676
		md5key: 2324koudaihuanshou*/

		if (uid.equals("") || sessionid.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数uid=" + uid + ",sessionid=" + sessionid + ",extend=" + extend);
			//return new ReturnValue(false,"缺少参数");
			return new ReturnValue(false, platform + "渠道,缺少参数");
		}
		if (!cpid.equals("2664") || !gameid.equals("2676"))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",参数审核不通过cpid=" + cpid + ",gameid=" + gameid + ",extend=" + extendJson.toString());
			return new ReturnValue(false, platform + "渠道,参数审核不通过");
		}
		Thread.sleep(2000); //延迟2秒
		String url = "http://2324.cn/User/userverify.php";
		//LogBAC.logout("charge/"+platform, "用户验证url="+url);
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.addParameter("cpid", cpid);
		netClient.addParameter("gameid", gameid);
		netClient.addParameter("sid", sessionid);
		netClient.addParameter("token", uid);

		ReturnValue rv = netClient.send();
		//LogBAC.logout("charge/" + platform, "用户验证返回结果=" + rv.success + " " + rv.info);
		/*{
			"code":"1",
			"msg":"验证成功",
			"sid":"911ee641793438c49e788e1c22587833",
			"token":"1856dc9fda05052fb12587981ed5944d"
			}*/

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData, "UTF-8");
					JSONObject resultJson = new JSONObject(result);
					int code = resultJson.optInt("code");
					if (code == 1)
					{
						//LogBAC.logout("login/"+platform, "登录成功username="+username);
						return new ReturnValue(true, username);
					}
					else
					{
						//LogBAC.logout("login/"+platform, "用户验证失败code="+code+",resultJson="+resultJson);
						return new ReturnValue(false, "用户验证失败code=" + code);
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
