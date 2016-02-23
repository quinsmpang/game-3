package com.moonic.platform;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.NetClient;

/**
 * 17173
 * @author 
 */
public class P043 extends P {
	
	public JSONObject login(String channel, String extend, String username, String password, String ip, String imei, String mac, int loginport, SqlString userSqlStr) throws Exception {
		JSONObject returnobj = super.login(channel, extend, username, password, ip, imei, mac, loginport, userSqlStr);
		returnobj.put("channeldata", returnobj.optString("username"));
		return returnobj;
	}

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			return new ReturnValue(false,"帐号渠道" + platform + ",登录缺少扩展参数");
		}
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
//		username = extendJson.optString("username");
		String token = extendJson.optString("token");

		if (/*username.equals("") || */token.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数username=" + username + ",token=" + token + ",extend=" + extend);
			return new ReturnValue(false,platform + "渠道,缺少参数");
		}

		/** 接口地址*/
		String BASE_URL = "http://gop.37wanwan.com/api/";
		/** 接口名字*/
		String apiName = "verifyUser";
		/** 游戏在平台上分配的唯一标识*/
		String gameId = "39";
		/** 游戏平台给游戏分配的一个私密字符串,只有游戏开发商和平台知道，用于通信加密校验 */
		String gameSecret = "4a4adb7c34d6be5214d2b3e76d1f529e";
		/** 开发商的名称*/
		String vendor = extendJson.optString("vendor");
		/** 时间戳 */
		String date;
		/** SDK版本*/
		String version = extendJson.optString("version");

		String url = BASE_URL + apiName;//请求地址+接口名
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");

		HashMap<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("token", token);
		// 设置请求头
		netClient.addHttpHead("Accept", "application/json; version=" + version);
		/** 计算时间戳 */
		date = getDate();
		netClient.addHttpHead("Date", date);
		/** 设置请求头的参数不需要encode*/
		String headerParam = sortParams(paramsMap);
		netClient.addHttpHead("Authentication", getAuthentication(gameId, gameSecret, vendor, date, apiName, headerParam));
		/** 设置请求体参数需要encode */
		String bodyParam = sortEncoderParams(paramsMap);
//		LogBAC.logout("login/" + platform, "sendStr=" + bodyParam);

		netClient.setSendBytes(bodyParam.getBytes());
		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData, "UTF-8");
					LogBAC.logout("login/" + platform, "result=" + result);
					JSONObject resultJson = new JSONObject(result);
					String usergameid = resultJson.optString("usergameid");
					//String errcode = resultJson.optString("errcode");
					//String msg = resultJson.optString("message");
					if (usergameid != null && !usergameid.equals(""))
					{
						//LogBAC.logout("login/" + platform, "登录成功username=" + username);
						return new ReturnValue(true, usergameid);
					}
					else
					{
						//LogBAC.logout("login/" + platform, "用户验证失败msg=" + msg);
						return new ReturnValue(false,"用户验证失败result=" + result);
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
	
	/**
	 * 对参数进行排序
	 */
	public static String sortParams(HashMap<String, String> params)
	{
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (String key : keys)
		{
			String value = params.get(key);
			prestr = prestr + key + "=" + value + "&";
		}
		prestr = prestr.substring(0, prestr.length() - 1);
		return prestr;
	}

	/**
	 * 对参数进行排序+encode
	 */
	public static String sortEncoderParams(HashMap<String, String> params) throws UnsupportedEncodingException
	{
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (String key : keys)
		{
			String value = params.get(key);
			prestr = prestr + key + "=" + URLEncoder.encode(value, "utf-8") + "&";
		}
		prestr = prestr.substring(0, prestr.length() - 1);
		return prestr;
	}

	/**
	 * 生成时间戳
	 */
	public static String getDate()
	{
		SimpleDateFormat dfs = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ", Locale.US);
		return dfs.format(new Date()).toString() + "GMT";
	}

	/**
	 * 计算授权号
	 */
	public static String getAuthentication(String gameId, String gameSecret, String vendor, String date, String apiName, String params)
	{
		String sign = getSign(date, apiName, params, gameSecret);
		String authentication = vendor + " " + gameId + ":" + sign;
		return authentication;
	}

	/**
	 * 计算签名
	 */
	private static String getSign(String date, String apiName, String params, String gameSecret)
	{
		String str = date + ":" + apiName + ":" + params + ":" + gameSecret;
		return md5(str);
	}

	/**
	 * MD5编码
	 */
	public static String md5(String data)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("md5");
			md.update(data.getBytes());
			byte[] digest = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : digest)
			{
				sb.append(String.format("%02x", b & 0xFF));
			}
			return sb.toString();
		}
		catch (Exception e)
		{
			return "";
		}
	}
}
