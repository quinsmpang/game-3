package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.nearme.oauth.model.AccessToken;
import com.nearme.oauth.open.AccountAgent;

/**
 * oppo
 * @author 
 */
public class P007 extends P
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
			//System.out.println("3G007扩展参数转json异常extend="+extend);
			//System.out.println(ex.toString());
			return new ReturnValue(false, platform + "渠道,登录扩展参数异常");
		}

		username = extendJson.optString("username");
		String uid = extendJson.optString("uid");
		String oauth_token = extendJson.optString("oauth_token");
		String oauth_token_secret = extendJson.optString("oauth_token_secret");

		if (uid.equals("") || oauth_token.equals("") || oauth_token_secret.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数uid=" + uid + ",oauth_token=" + oauth_token + ",oauth_token_secret=" + oauth_token_secret + ",extend=" + extend);
			//return new ReturnValue(false,"缺少参数");
			return new ReturnValue(false, platform + "渠道,缺少参数");
		}

		String gcUserInfo = AccountAgent.getInstance().getGCUserInfo(new AccessToken(oauth_token, oauth_token_secret));

		LogBAC.logout("charge/" + platform, "用户验证返回结果=" + gcUserInfo);

		//正常返回的数据格式为JSON： 
		/*{"BriefUser":
		 * {
		 * "id":"11686668",
		 * "constellation":0,
		 * "sex":true,
		 * "profilePictureUrl":"http://gcfs.nearme.com.cn/avatar/common/male.png",
		 * "name":"ZTEU880E11686668",
		 * "userName":"NM11686668",
		 * "emailStatus":"false",
		 * "mobileStatus":"false",
		 * "status":"Visitor",
		 * "mobile":"",
		 * "email":"",
		 * "gameBalance":"0"}}*/

		try
		{
			JSONObject resultJson = new JSONObject(gcUserInfo);
			JSONObject json = resultJson.optJSONObject("BriefUser");
			int id = json.optInt("id");
			if (id > 0)
			{
				LogBAC.logout("login/" + platform, "登录成功username=" + username);
				return new ReturnValue(true, username);
			}
			else
			{
				LogBAC.logout("login/" + platform, "用户验证失败gcUserInfo=" + gcUserInfo);
				return new ReturnValue(false, "用户验证失败id=" + id);
			}
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败" + ex.toString() + ",str=" + gcUserInfo);
			return new ReturnValue(false, "用户验证失败" + ex.toString());
		}
	}
}
