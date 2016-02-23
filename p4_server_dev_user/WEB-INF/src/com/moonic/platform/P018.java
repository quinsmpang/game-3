package com.moonic.platform;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;

import com.ehc.common.MD5;
import com.ehc.common.ReturnValue;
import com.moonic.mgr.PookNet;
import com.moonic.util.NetClient;

/**
 * 步步高
 * @author 
 */
public class P018 extends P
{
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

		String authtoken = extendJson.optString("authtoken");
		String openid = extendJson.optString("openid");

		if (authtoken.equals("") || openid.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数token,extend=" + extend);
			return new ReturnValue(false, "platform" + platform + ",缺少参数token,extend=" + extend);
		}

		String url = "https://usrsys.inner.bbk.com/auth/user/info";
		//LogBAC.logout("login/" + platform, "用户验证url=" + url);
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");

		String sendStr = "access_token=" + authtoken;
		LogBAC.logout("login/" + platform, "登录发给渠道数据=" + sendStr);

		netClient.setSendBytes(sendStr.getBytes());
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
					//{"email":"","uid":"8536a738dcdfc58e"} 成功的
					//{"msg":"请求参数错误","stat":"440"} 失败的
					String code = resultJson.optString("stat");
					String msg = resultJson.optString("msg");
					if (resultJson.has("uid"))
					{
						username = resultJson.optString("uid");
						LogBAC.logout("login/" + platform, "渠道返回username=" + username);
						return new ReturnValue(true, username);
					}
					else
					{
						LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						return new ReturnValue(false, "用户验证失败msg=" + msg);
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

	String version = "2.1.0";
	String url = "https://pay.vivo.com.cn/vivoPay/getVivoOrderNum";
	String signMethod = "MD5";
	String appId = "1e9575d9d8aaba3cf6140b03148ca5fa";
	String cpId = "20141013105627098509";
	String cpKey = "a1c4f39e2a9241ed13efe4727f112396";

	public ReturnValue getOrderInfo(String storeOrder, int orderAmount)
	{
		String orderTime = Tools.date2str(new Date(), "yyyyMMddHHmmss");
		String orderTitle = "buydiamond";
		String orderDesc = "none";
		String notifyUrl = PookNet.vivoNotify;

		/* 注意事项：
		（1）被签名字符串中的关键信息需要按照key值做升序排列；
		（2）空值（空字符串或null值）不参与签名运算； 
		（3）将被签名字符串转成字节数组时必须指定编码为utf-8。*/

		//signature= md5_hex(key1=value1&key2=value2&...&keyn=valuen&to_lower_case(md5_hex(Cp-key)))
		//参数排序
		Map<String, String> map = new HashMap<String, String>();
		map.put("version", version);
		map.put("storeId", cpId);
		map.put("appId", appId);
		map.put("storeOrder", storeOrder);
		map.put("notifyUrl", notifyUrl);
		map.put("orderTime", orderTime);
		map.put("orderAmount", String.valueOf(orderAmount) + ".00");
		map.put("orderTitle", orderTitle);
		map.put("orderDesc", orderDesc);

		StringBuilder contentBuffer = new StringBuilder();
		Object[] signParamArray = map.keySet().toArray();
		Arrays.sort(signParamArray);
		for (Object key : signParamArray)
		{
			String value = map.get(key);
			contentBuffer.append(key + "=" + value + "&");
		}
		String beSignStr = contentBuffer.toString() + MD5.encode(cpKey, "UTF-8").toLowerCase();
		String sign = MD5.encode(beSignStr, "UTF-8");

		contentBuffer.append("signMethod=" + signMethod);
		contentBuffer.append("&signature=" + sign);
		//LogBAC.logout("chargecenter/018", "beSignStr="+beSignStr);
		LogBAC.logout("chargecenter/018", "contentBuffer=" + contentBuffer);

		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");

		netClient.ignoreSSL();
		netClient.setSendBytes(contentBuffer.toString().getBytes());
		ReturnValue rv = netClient.send();
		if (rv.success)
		{
			try
			{
				String result = new String(rv.binaryData, "UTF-8");
				//LogBAC.logout("chargecenter/018", "result="+result);
				JSONObject resultJson = new JSONObject(result);
				String respCode = resultJson.optString("respCode");
				String respMsg = resultJson.optString("respMsg");
				if (respCode.equals("200"))
				{
					String vivoSignature = resultJson.optString("vivoSignature");
					String vivoOrder = resultJson.optString("vivoOrder");
					StringBuffer sb = new StringBuffer();
					sb.append(vivoOrder);
					sb.append(",");
					sb.append(vivoSignature);
					return new ReturnValue(true, sb.toString());
				}
				else
				{
					LogBAC.logout("chargecenter/018", "result=" + result);
					return new ReturnValue(false, respMsg + "(" + respCode + ")");
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return new ReturnValue(false, "获取步步高订单失败:" + ex.toString());
			}
		}
		else
		{
			return new ReturnValue(false, "获取步步高订单失败:" + rv.info);
		}
	}
}
