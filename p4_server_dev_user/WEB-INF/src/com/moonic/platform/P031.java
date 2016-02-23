package com.moonic.platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.Text;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * 猎豹
 * @author 
 */
public class P031 extends P
{

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false, "你的渠道" + platform + ",登录缺少扩展参数");
		}
		JSONObject extendJson = null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",扩展参数异常extend=" + extend);
			return new ReturnValue(false, platform + "渠道,登录扩展参数异常");
		}
		username = extendJson.optString("uid");
		String token = extendJson.optString("token");
		String supplier_id = extendJson.optString("supplier_id");
		String supplier_key = extendJson.optString("supplier_key");

		if (username.equals("") || token.equals("") || supplier_id.equals("") || supplier_key.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数username=" + username + ",token=" + token + ",supplier_id=" + supplier_id + ",supplier_key=" + supplier_key + ",extend=" + extend);
			return new ReturnValue(false, platform + "渠道,缺少参数");
		}

		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("mutk", token);
		hm.put("supplier_id", supplier_id);
		hm.put("time", System.currentTimeMillis() / 1000);
		hm.put("client_ip", "0");

		String str = getUrlParam(hm); // 得到参数

		//签名值
		String sign = MD5.encode(str + supplier_key); // 根据参数和key得到签名值

		String url = "http://m.wan.liebao.cn/user/validate_mutk?";
		String checkUrl = url + str + "&sign=" + sign;
		NetClient netClient = new NetClient();
		netClient.setAddress(checkUrl);
		//LogBAC.logout("login/" + platform, "登录发给渠道数据=" + checkUrl);

		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				String result = "";
				try
				{
					result = new String(rv.binaryData, "UTF-8");
					LogBAC.logout("login/" + platform, "登录收到渠道数据=" + result);
					JSONObject resultJson = new JSONObject(result);
//					{
//						"code" : 1,
//						"data" : { "uid" : "1350762178" },
//						"msg" : "ok"
//						}
					if (resultJson.optString("msg").equals("ok"))
					{
						//username = resultJson.optString("id");
						LogBAC.logout("login/" + platform, "渠道返回username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败msg=" + "签名sign 错误");
						return new ReturnValue(false, "用户验证失败msg=" + "签名sign 错误");
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

	/**
	 * 得到地址参数
	 * @param hm
	 * @param signKey
	 * @return
	 */
	public static String getUrlParam(HashMap<String, Object> hm)
	{
		List<String> list = new ArrayList<String>(hm.keySet());
		Collections.sort(list);
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = list.iterator();
		while (iter.hasNext())
		{
			String key = iter.next();
			if (sb.length() <= 0)
			{
				sb.append(key + "=" + hm.get(key));
			}
			else
			{
				sb.append("&" + key + "=" + hm.get(key));
			}
		}
		return sb.toString();
	}
}
