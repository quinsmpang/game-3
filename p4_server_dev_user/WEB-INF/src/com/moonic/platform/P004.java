package com.moonic.platform;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;
import com.moonic.util.NetClient;

/**
 * 2016-01-20 14:00:00 同步推
 * @author 
 */
public class P004 extends P {
	
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		if (extend == null || extend.equals("")) {
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数extend=" + extend);
			BACException.throwInstance("你的渠道" + platform + ",登录缺少扩展参数");
		}
		JSONObject extendJson = null;
		try {
			extendJson = new JSONObject(extend);
		} catch (Exception ex) {
			LogBAC.logout("login_error", "platform=" + platform + ",扩展参数转json异常extend=" + extend);
			BACException.throwInstance(platform + "渠道,登录扩展参数异常");
		}
		
		String session = extendJson.optString("session");
		if (session == null || session.trim().length() <= 0) {
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数" + "session=" + session + ",extend=" + extend);
			BACException.throwInstance(platform + "渠道,缺少参数");
		}
		//http://tgi.tongbu.com/api/LoginCheck.ashx?session=afa1c75257f400079a576588d0bb41bc&appid=100000
		int appid = 160112;
		String url = "http://tgi.tongbu.com/api/LoginCheck.ashx";
		String sendStr = "session=" + session + "&appid=" + appid;
		LogBAC.logout("login/" + platform, "发送的用于验证数据sendStr=" + sendStr);

		url = url + "?" + sendStr;
		LogBAC.logout("login/" + platform, "用户验证url=" + url);

		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		ReturnValue rv = netClient.send();
		if (rv.success) {
			if (rv.dataType == ReturnValue.TYPE_BINARY) {
				try {
					String result = new String(rv.binaryData, "UTF-8");
					LogBAC.logout("login/" + platform, "渠道用户验证返回" + result);
					int resultNum = Integer.valueOf(result);

					if (resultNum > 0) {
						LogBAC.logout("login/" + platform, "result=" + result);
						return new ReturnValue(true, result);
					} else {
						String errorMsg = resultNum == 0 ? "session已经过期" : "格式有错";
						//用户验证失败
						LogBAC.logout("login/" + platform, "用户验证失败," + errorMsg);
						return new ReturnValue(false, "用户验证失败," + errorMsg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogBAC.logout("login_error", "platform=" + platform + ",用户验证异常," + e.toString() + ",str=" + new String(rv.binaryData, "UTF-8"));
					return new ReturnValue(false, "用户验证异常," + e.toString());
				}
			} else {
				LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败,数据格式异常");
				return new ReturnValue(false, "用户验证失败,数据格式异常");
			}
		} else {
			LogBAC.logout("login_error", "platform=" + platform + ",用户验证失败," + rv.info);
			return new ReturnValue(false, "用户验证失败," + rv.info);
		}
	}
}
