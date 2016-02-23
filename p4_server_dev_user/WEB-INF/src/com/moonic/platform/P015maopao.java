package com.moonic.platform;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.util.MD5;
import com.moonic.util.NetClient;

/**
 * 思凯
 * @author 
 */
public class P015maopao extends P {
	
	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		if(extend==null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform="+platform+",缺少扩展参数platform="+platform+",username="+username+",ip="+ip);
			return new ReturnValue(false,"帐号渠道"+platform+",登录缺少扩展参数");
		}
		//LogBAC.logout("login/"+platform, "收到用户验证数据"+extend);
		JSONObject extendJson=null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch(Exception ex)
		{
			LogBAC.logout("login_error","platform="+platform+",扩展参数异常extend="+extend);
			//System.out.println("斯凯015扩展参数转json异常extend="+extend);
			//System.out.println(ex.toString());
			return new ReturnValue(false,platform+"渠道,登录扩展参数异常");
		}
		
		username = extendJson.optString("username");
		String skyid = extendJson.optString("skyid");
		String ticket = extendJson.optString("ticket");
		
		
		if(ticket.equals("")||skyid.equals(""))
		{
			LogBAC.logout("login_error","platform="+platform+",缺少参数ticket="+ticket+",skyid="+skyid+",extend=" + extend);				
			return new ReturnValue(false,platform+"渠道,缺少参数");
		}	
		//LogBAC.logout("login/"+platform,"skyid="+skyid+",ticket="+ticket+",username="+username);
		
		String appid = "6000651";
		String appSecret = "f055bd7a9a8da3661be8";
		
		String url = "http://sdkpassport.51mrp.com/ticket";
		//LogBAC.logout("login/"+platform, "用户验证url="+url);
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		//netClient.setContentType("application/x-www-form-urlencoded");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("skyid", skyid);
		params.put("ticket", ticket);
		params.put("appid", appid);
		ArrayList<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++)
		{
			String key = keys.get(i);
			String value = params.get(key);
			if (i == keys.size() - 1)
			{//拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			}
			else
			{
				prestr = prestr + key + "=" + value + "&";
			}
		}
		//LogBAC.logout("login/"+platform, "prestr="+prestr);
		
		String sign = MD5.encode(prestr+"&sign="+appSecret);
		JSONObject json = new JSONObject();
		json.put("skyid", skyid);
		json.put("ticket", ticket);
		json.put("appid", appid);
		json.put("sign", sign);
		/*String sendStr = ""
				+"skyid="+skyid
				+"&ticket="+ticket
				+"&appid="+appid
				+"&sign="+sign;*/
		String sendStr =json.toString();
		//LogBAC.logout("login/"+platform, "sendStr="+sendStr);
		
		netClient.setSendBytes(sendStr.getBytes());
		ReturnValue rv = netClient.send();

		if(rv.success)
		{
			if(rv.dataType==ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData,"UTF-8");
					JSONObject resultJson = new JSONObject(result);
					String code = resultJson.optString("code");
					String msg = URLDecoder.decode(resultJson.optString("msg"),"UTF-8");
					//code={"skyid":"361517369","username":"mp478350429","logined":1,"code":200,"msg":"%E6%88%90%E5%8A%9F"}
					//LogBAC.logout("login/"+platform, "result="+result);
					//LogBAC.logout("login/"+platform, "msg="+msg);
					if(code.equals("200"))
					{
						//LogBAC.logout("login/"+platform, "登录成功username="+username);
						return new ReturnValue(true,username);	
					}
					else
					{
						//LogBAC.logout("login/"+platform, "用户验证失败msg="+msg);
						return new ReturnValue(false,"用户验证失败msg="+msg);
					}				
				}
				catch(Exception ex)
				{
					LogBAC.logout("login_error","platform="+platform+",用户验证失败"+ex.toString()+",str="+new String(rv.binaryData,"UTF-8"));	
					
					return new ReturnValue(false,"用户验证失败"+ex.toString());
				}
			}
			else
			{
				LogBAC.logout("login_error","platform="+platform+",用户验证失败,数据格式异常");					
				return new ReturnValue(false,"用户验证失败,数据格式异常");
			}
		}				
		else
		{	
			LogBAC.logout("login_error", "platform="+platform+",用户验证失败,"+rv.info);
			return new ReturnValue(false,"用户验证失败,"+rv.info);
		}
	}
}
