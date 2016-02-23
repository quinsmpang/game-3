package com.moonic.platform;

import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.bac.UserBAC;
import com.moonic.mgr.PookNet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.MD5;
import com.moonic.util.MyTools;
import com.moonic.util.NetFormSender;

/**
 * 波克城市
 * @author 
 */
public class P001 extends P {
	
	/**
	 * 手机找回密码
	 */
	public ReturnValue mobileFindPwd(String username, String phone,String ip) {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet dataRs = dbHelper.query(UserBAC.tab_user, "pook", "username='"+username+"' and platform='001'");
			if(!dataRs.next()){
				BACException.throwInstance("找不到用户："+username);
			}
			String tstamp = String.valueOf(System.currentTimeMillis());
			StringBuffer ticketSb = new StringBuffer();
			ticketSb.append(username);
			ticketSb.append("_");
			ticketSb.append(phone);
			ticketSb.append("_");
			ticketSb.append(tstamp);
			ticketSb.append("_");
			ticketSb.append(ip);
			ticketSb.append("_");						
			ticketSb.append(PookNet.screctKey);
			dbHelper.closeConnection();
			NetFormSender sender = new NetFormSender(PookNet.mobilefindpwd_do);
			sender.addParameter("userName", username);
			sender.addParameter("mobilePhone", phone);
			sender.addParameter("tstamp", tstamp);
			sender.addParameter("ipString", ip);
			sender.addParameter("ticket", MD5.encode(ticketSb.toString()));
			sender.send().check();
			boolean result = false;
			String returnStr = null;
			if(sender.rv.success) {
				JSONObject resultJson = new JSONObject(sender.rv.info);
				String resultStr = resultJson.optString("result");
				String message = resultJson.optString("message");
				result = resultStr.equals("S");
				returnStr = message;
			} else {
				returnStr = sender.rv.info;
			}
			return new ReturnValue(result, returnStr);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 邮箱找回密码
	 */
	public ReturnValue emailFindPwd(String username, String email,String ip){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet dataRs = dbHelper.query(UserBAC.tab_user, "pook,channel", "username='"+username+"' and platform='001'");
			if(!dataRs.next()){
				BACException.throwInstance("找不到用户："+username);
			}
			String tstamp = String.valueOf(System.currentTimeMillis());
			StringBuffer ticketSb = new StringBuffer();
			ticketSb.append(username);
			ticketSb.append("_");
			ticketSb.append(email);
			ticketSb.append("_");
			ticketSb.append(tstamp);
			ticketSb.append("_");			
			ticketSb.append(ip);
			ticketSb.append("_");
			ticketSb.append(PookNet.screctKey);
			dbHelper.closeConnection();
			NetFormSender sender = new NetFormSender(PookNet.emailfindpwd_do);
			sender.addParameter("userName", username);
			sender.addParameter("email", email);
			sender.addParameter("tstamp", tstamp);
			sender.addParameter("ipString", ip);
			sender.addParameter("ticket", MD5.encode(ticketSb.toString()));
			sender.send().check();
			boolean result = false;
			String returnStr = null;
			if(sender.rv.success) {
				JSONObject resultJson = new JSONObject(sender.rv.info);
				String resultStr = resultJson.optString("result");
				String message = resultJson.optString("message");
				result = resultStr.equals("S");
				returnStr = message;
			} else {
				returnStr = sender.rv.info;
			}
			return new ReturnValue(result, returnStr);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	public void register(DBHelper dbHelper, String username, String password, String rePassword, String ip, String channel, JSONArray logdata) throws Exception {
		username = username.toLowerCase(); //波克用户名强行转小写
		NetFormSender sender = new NetFormSender(PookNet.register_do);
		sender.addParameter("rUser.agentId", "094");
		//sender.addParameter("rUser.agentId", "10490912"); //下次维护时采用
		sender.addParameter("rUser.userName", username);
		sender.addParameter("rUser.password", password);
		sender.addParameter("rUser.rePassword", rePassword);
		sender.addParameter("rUser.validCode", "webLobby");
		sender.addParameter("ipString", ip);
		StringBuffer ticket = new StringBuffer();
		ticket.append("094");
		ticket.append("_");
		ticket.append(username);
		ticket.append("_");
		ticket.append(password);
		ticket.append("_");
		ticket.append(password);
		ticket.append("_");
		ticket.append("webLobby");
		ticket.append("_");
		ticket.append(ip);
		ticket.append("_");
		ticket.append(PookNet.screctKey);
		sender.addParameter("ticket", MD5.encode(ticket.toString()));
		
		sender.send().check();
		//System.out.println("注册返回"+sender.rv.info);
		JSONObject pokerobj = new JSONObject(sender.rv.info);
		String result = pokerobj.getString("result");
		if(result.equals("S")){
		} else 
		if(result.equals("E")){
			BACException.throwInstance(pokerobj.getString("message"));
		} else 
		{
			BACException.throwInstance("请求失败");
		}
		UserBAC.getInstance().insert(dbHelper, username, "", channel, platform, ip, logdata);
	}

	public JSONObject login(String channel, String extend, String username, String password, String ip, String imei, String mac, int loginport, SqlString userSqlStr) throws Exception {
		NetFormSender sender = null;
		if(channel.equals("042")){
			String tstamp = String.valueOf(System.currentTimeMillis());
			sender = new NetFormSender(PookNet.login042_do);
			sender.addParameter("gameId",20);
			sender.addParameter("distinctId",1);
			sender.addParameter("userId",username);//USERNAME实际为userId
			sender.addParameter("tstamp",tstamp);
			
			StringBuffer uattest = new StringBuffer();
			uattest.append("userId");
			uattest.append(username);
			uattest.append("time");
			uattest.append(tstamp);
			uattest.append("token");
			uattest.append(password);
			uattest.append(PookNet.key_53wan);
			sender.addParameter("uattest",MD5.encode(uattest.toString()));
			
			sender.addParameter("userToken",password);//PASSWORD实际为token
		} else {
			username = username.toLowerCase(); //波克用户名强行转小写
			//+“=dsfesdffeasd54f64*FEDF::DFEsdf;eWEJDGLURYD>FJE”
			//String md5password = MD5.encode(MD5.encode(password)+"=dsfesdffeasd54f64*FEDF::DFEsdf;eWEJDGLURYD>FJE").toUpperCase();
			String md5password = MD5.encode(password).toUpperCase();
			//NetFormSender sender = new NetFormSender("http://www.pook.com/commLogin.do");
			sender = new NetFormSender(PookNet.login_do);
			//System.out.println("向"+NetFormSender.login_do+"发送登录验证请求,uname="+username+"&upwd="+md5password+"&loginType=13");
			String loginType="13";
			sender.addParameter("uname",username);
			sender.addParameter("upwd",md5password);
			sender.addParameter("loginType",loginType);
			sender.addParameter("imeiString",imei);
			sender.addParameter("macString",mac);
			
			sender.addParameter("ipString",ip);
			
			StringBuffer ticket = new StringBuffer();
			ticket.append(username);
			ticket.append("_");
			ticket.append(md5password);
			ticket.append("_");
			ticket.append(loginType);
			ticket.append("_");			
			ticket.append(mac);
			ticket.append("_");
			ticket.append(imei);
			ticket.append("_");			
			ticket.append(ip);
			ticket.append("_");
			ticket.append(PookNet.screctKey);
			sender.addParameter("ticket", MD5.encode(ticket.toString()));
		}
		
		long t1 = System.currentTimeMillis();
		//System.out.println("IP="+ip+",username="+username+"尝试登录");
		sender.send().check();
		
		long t2 = System.currentTimeMillis();
		if(t2-t1>500)
		{
			//LogBAC.logout("login/"+channel, username+"登录波克耗时="+(t2-t1));
		}
		//System.out.println("登录返回数据="+sender.rv.info);
		//LogBAC.logout("login/"+channel, "登录返回数据="+sender.rv.info);	
		if(!sender.rv.success){
			LogBAC.logout("login_error", "channel="+channel+",用户验证失败,波克返回:"+sender.rv.info);
			BACException.throwInstance("波克返回:"+sender.rv.info);
		}
		String info = sender.rv.info;
		JSONObject json = new JSONObject(info);
		if(channel.equals("042")){
			json.put("result", json.optString("key"));
			json.put("message", json.optString("msg"));
			json.put("userToken", password);
			json.put("userId", username);
			json.put("userName", json.optString("msg"));
		}
		String result=json.optString("result");
		String message=json.optString("message");
		String userToken=json.optString("userToken");
		String userId = json.optString("userId");
		username = json.optString("userName");
		if(!result.equals("S")){
			BACException.throwInstance("波克返回:"+message);
		}
		if(channel.equals("042")){
			JSONObject returnobj = new JSONObject();
			returnobj.put("username", username);
			returnobj.put("channeldata", username);
			return returnobj;
		} else {
			JSONObject pookJson = new JSONObject();//获取用户扩展信息
			pookJson.put("uid", userId);
			pookJson.put("token", userToken);
			JSONObject safeJson = getSafetyInfo(pookJson.toString(),ip);
			if(safeJson == null){
				BACException.throwInstance("波克返回:getSafetyInfo==null");
			}
			safeJson.put("username", username);
			userSqlStr.add("pook", safeJson.toString());
			userSqlStr.add("pookid", userId);
			String card = safeJson.getString("card");
			String mobile = safeJson.getString("mobile");
			String email = safeJson.getString("email");
			JSONArray channeldata = new JSONArray();
			channeldata.add(MyTools.getEncrypeStr(card, 6, card.length()-2));//是否已绑定身份证
			channeldata.add(MyTools.getEncrypeStr(mobile, mobile.length()/2, mobile.length()));//是否已绑定电话
			channeldata.add(MyTools.getEncrypeStr(email, 3, 6));//是否已绑定邮箱
			JSONObject returnobj = new JSONObject();
			returnobj.put("username", username);
			returnobj.put("channeldata", channeldata);
			return returnobj;
		}
	}

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		DBHelper dbHelper = new DBHelper();
		JSONObject userJson = dbHelper.queryJsonObj(UserBAC.tab_user, "*", "username='"+username+"' and platform='"+platform+"'");
		if(userJson == null) {
			BACException.throwInstance("用户名不存在");
		}
		if(userJson.getInt("onlinestate")==0){
			BACException.throwInstance("尚未登录");
		}
		return new ReturnValue(true, username);
	}
	
	/**
	 * 获取用户安全信息
	 */
	public JSONObject getSafetyInfo(String str,String ip) throws Exception {
		JSONObject pookObj = new JSONObject(str);
		String tstamp = String.valueOf(System.currentTimeMillis());
		StringBuffer ticket = new StringBuffer();
		ticket.append(pookObj.getInt("uid"));
		ticket.append("_");
		ticket.append(pookObj.getString("token"));
		ticket.append("_");
		ticket.append(tstamp);
		ticket.append("_");
		ticket.append(ip);
		ticket.append("_");	
		ticket.append(PookNet.screctKey);
		NetFormSender sender = new NetFormSender(PookNet.getsafety_do);
		sender.addParameter("userId", pookObj.getInt("uid"));
		sender.addParameter("userToken", pookObj.getString("token"));
		sender.addParameter("tstamp", tstamp);
		sender.addParameter("ipString", ip);
		sender.addParameter("ticket", MD5.encode(ticket.toString()));
		sender.send().check();
		
		JSONObject resultJson = new JSONObject(sender.rv.info);
		String result = resultJson.optString("result");
		String message = resultJson.optString("message");
		if(!result.equals("S")) {
			BACException.throwInstance("波克返回："+message);
		}
		pookObj.put("card", resultJson.optString("cardNo"));
		pookObj.put("mobile", resultJson.optString("mobilePhone"));
		pookObj.put("email", resultJson.optString("email"));
		return pookObj;
	}
}
