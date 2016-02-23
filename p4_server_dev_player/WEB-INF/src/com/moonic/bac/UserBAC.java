package com.moonic.bac;

import java.sql.ResultSet;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.memcache.MemcachedUtil;
import com.moonic.mgr.PookNet;
import com.moonic.mode.User;
import com.moonic.servlet.GameServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.MD5;
import com.moonic.util.MyTools;
import com.moonic.util.NetFormSender;

/**
 * 用户BAC
 * @author John
 */
public class UserBAC {
	public static final String tab_user = "tab_user";
	
	public static Hashtable<String, User> session_usermap = new Hashtable<String, User>(8192);
	
	/**
	 * 绑定身份证
	 */
	public ReturnValue bindCard(int userid, String card, String realname, String ip, int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet dataRs = dbHelper.query(tab_user, "username,pook,channel,platform", "id="+userid);
			if(!dataRs.next()){
				BACException.throwInstance("找不到用户："+userid);
			}
			String platform = dataRs.getString("platform");
			if(!platform.equals("001")){
				BACException.throwInstance("错误的运营渠道："+platform);
			}
			JSONObject pookObj = getSafetyInfo(dataRs.getString("pook"), ip);
			if(!pookObj.getString("card").equals("")){
				BACException.throwInstance("已绑定过身份证");
			}
			String username = dataRs.getString("username");
			dbHelper.closeConnection();
			NetFormSender sender = new NetFormSender(PookNet.bindcard_do);
			card = card.toUpperCase();
			sender.addParameter("userId", pookObj.getInt("uid"));
			sender.addParameter("userToken", pookObj.getString("token"));
			sender.addParameter("userName", username);
			sender.addParameter("identityCard", card);
			sender.addParameter("reIdentityCard", card);
			sender.addParameter("realName", realname);
			sender.addParameter("ipString", ip);
			StringBuffer ticket = new StringBuffer();
			ticket.append(pookObj.getInt("uid"));
			ticket.append("_");
			ticket.append(pookObj.getString("token"));
			ticket.append("_");
			ticket.append(username);
			ticket.append("_");
			ticket.append(card);
			ticket.append("_");
			ticket.append(card);
			ticket.append("_");
			ticket.append(realname);
			ticket.append("_");
			ticket.append(ip);
			ticket.append("_");
			ticket.append(PookNet.screctKey);
			sender.addParameter("ticket", MD5.encode(ticket.toString()));
			sender.send().check();
			
			boolean result = false;
			String returnStr = null;
			if (sender.rv.success) {
				JSONObject resultJson = new JSONObject(sender.rv.info);
				String pook_result = resultJson.optString("result");
				String pook_message = resultJson.optString("message");
				if (pook_result.equals("S")) {
					pookObj.put("card", card);
					pookObj.put("realName", realname);
					SqlString sqlStr = new SqlString();
					sqlStr.add("pook", pookObj.toString());
					dbHelper.openConnection();
					update(dbHelper, userid, sqlStr);
					dbHelper.closeConnection();
					result = true;
				}
				returnStr = pook_message;
			} else {
				returnStr = sender.rv.info;
			}
			
			StringBuffer sbRemark = new StringBuffer();
			sbRemark
			.append("姓名：")
			.append(realname)
			.append("\r\n身份证号：")
			.append(card)
			.append("\r\n绑定结果：")
			.append(returnStr);
			GameLog.getInst(playerid, GameServlet.ACT_USER_BIND_CARD)
			.addRemark(sbRemark)
			.save();
			return new ReturnValue(result, returnStr);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取手机验证码
	 */
	public ReturnValue getMobileVaildNum(int userid, String phone, String card, String ip){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet dataRs = dbHelper.query(tab_user, "pook,channel,platform", "id="+userid);
			if(!dataRs.next()){
				BACException.throwInstance("找不到用户："+userid);
			}
			String platform = dataRs.getString("platform");
			if(!platform.equals("001")){
				BACException.throwInstance("错误的运营渠道："+platform);
			}
			JSONObject pookObj = getSafetyInfo(dataRs.getString("pook"), ip);
			if(pookObj.getString("card").equals("")){
				BACException.throwInstance("尚未绑定身份证，请先绑定身份证");
			}
			if(card == null) {
				card = pookObj.getString("card");
			}
			card = card.toUpperCase();
			if(!pookObj.getString("card").equals(card)){
				BACException.throwInstance("身份证号不匹配");
			}
			if(!pookObj.getString("mobile").equals("")){
				BACException.throwInstance("已绑定过手机");
			}
			dbHelper.closeConnection();
			NetFormSender sender = new NetFormSender(PookNet.getmobilevalidnum_do);
			sender.addParameter("userId", pookObj.getInt("uid"));
			sender.addParameter("userToken", pookObj.getString("token"));
			sender.addParameter("identityCard", pookObj.getString("card"));
			sender.addParameter("mobilePhone", phone);
			sender.addParameter("ipString", ip);
			StringBuffer ticket = new StringBuffer();
			ticket.append(pookObj.getInt("uid"));
			ticket.append("_");
			ticket.append(pookObj.getString("token"));
			ticket.append("_");
			ticket.append(card);
			ticket.append("_");
			ticket.append(phone);
			ticket.append("_");
			ticket.append(ip);
			ticket.append("_");
			ticket.append(PookNet.screctKey);
			sender.addParameter("ticket", MD5.encode(ticket.toString()));
			sender.send().check();
			
			boolean result = false;
			String returnStr = null;
			if (sender.rv.success) {
				JSONObject resultJson = new JSONObject(sender.rv.info);
				String pook_result = resultJson.optString("result");
				String pook_message = resultJson.optString("message");
				if (pook_result.equals("S")) {
					result = true;
				}
				returnStr = pook_message;
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
	 * 绑定手机
	 */
	public ReturnValue bindMobile(int userid, String phone, String validnum, String card, String ip, int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet dataRs = dbHelper.query(tab_user, "pook,platform", "id="+userid);
			if(!dataRs.next()){
				BACException.throwInstance("找不到用户："+userid);
			}
			String platform = dataRs.getString("platform");
			if(!platform.equals("001")){
				BACException.throwInstance("错误的运营渠道："+platform);
			}
			JSONObject pookObj = getSafetyInfo(dataRs.getString("pook"), ip);
			if(pookObj.getString("card").equals("")){
				BACException.throwInstance("尚未绑定身份证，请先绑定身份证");
			}
			card = card.toUpperCase();
			if(!pookObj.getString("card").equals(card)){
				BACException.throwInstance("身份证号不匹配");
			}
			if(!pookObj.getString("mobile").equals("")){
				BACException.throwInstance("已绑定过手机");
			}
			dbHelper.closeConnection();
			NetFormSender sender = new NetFormSender(PookNet.bindmobile_do);
			sender.addParameter("userId", pookObj.getInt("uid"));
			sender.addParameter("userToken", pookObj.getString("token"));
			sender.addParameter("mobilePhone", phone);
			sender.addParameter("validNum", validnum);
			sender.addParameter("ipString", ip);
			StringBuffer ticket = new StringBuffer();
			ticket.append(pookObj.getInt("uid"));
			ticket.append("_");
			ticket.append(pookObj.getString("token"));
			ticket.append("_");
			ticket.append(phone);
			ticket.append("_");
			ticket.append(validnum);
			ticket.append("_");
			ticket.append(ip);
			ticket.append("_");
			ticket.append(PookNet.screctKey);
			sender.addParameter("ticket", MD5.encode(ticket.toString()));
			sender.send().check();
			
			boolean result = false;
			String returnStr = null;
			if (sender.rv.success) {
				JSONObject resultJson = new JSONObject(sender.rv.info);
				String pook_result = resultJson.optString("result");
				String pook_message = resultJson.optString("message");
				if (pook_result.equals("S")) {
					pookObj.put("mobile", phone);
					SqlString sqlStr = new SqlString();
					sqlStr.add("pook", pookObj.toString());
					dbHelper.openConnection();
					update(dbHelper, userid, sqlStr);
					dbHelper.closeConnection();
					result = true;
				}
				returnStr = pook_message;
			} else {
				returnStr = sender.rv.info;
			}
			
			StringBuffer sbRemark = new StringBuffer();
			sbRemark
			.append("手机号：")
			.append(phone)
			.append("\r\n身份证：")
			.append(card)
			.append("\r\n绑定结果：")
			.append(returnStr);
			GameLog.getInst(playerid, GameServlet.ACT_USER_BIND_MOBILE)
			.addRemark(sbRemark)
			.save();
			return new ReturnValue(result, returnStr);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 绑定邮箱
	 */
	public ReturnValue bindEmail(int userid, String email, String card, String ip, int playerid) {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet dataRs = dbHelper.query(tab_user, "pook,channel,platform", "id="+userid);
			if(!dataRs.next()){
				BACException.throwInstance("找不到用户："+userid);
			}
			String platform = dataRs.getString("platform");
			if(!platform.equals("001")){
				BACException.throwInstance("错误的运营渠道："+platform);
			}
			JSONObject pookObj = getSafetyInfo(dataRs.getString("pook"), ip);
			if(pookObj.getString("card").equals("")){
				BACException.throwInstance("尚未绑定身份证，请先绑定身份证");
			}
			card = card.toUpperCase();
			if(!pookObj.getString("card").equals(card)){
				BACException.throwInstance("身份证号不匹配");
			}
			if(!pookObj.getString("email").equals("")){
				BACException.throwInstance("已绑定过邮箱");
			}
			dbHelper.closeConnection();
			NetFormSender sender = new NetFormSender(PookNet.bindemail_do);
			sender.addParameter("userId", pookObj.getInt("uid"));
			sender.addParameter("userToken", pookObj.getString("token"));
			sender.addParameter("email", email);
			sender.addParameter("identityCard", card);
			sender.addParameter("ipString", ip);
			StringBuffer ticket = new StringBuffer();
			ticket.append(pookObj.getInt("uid"));
			ticket.append("_");
			ticket.append(pookObj.getString("token"));
			ticket.append("_");
			ticket.append(email);
			ticket.append("_");
			ticket.append(card);
			ticket.append("_");				
			ticket.append(ip);
			ticket.append("_");
			ticket.append(PookNet.screctKey);
			sender.addParameter("ticket", MD5.encode(ticket.toString()));
			sender.send().check();
			
			boolean result = false;
			String returnStr = null;
			if (sender.rv.success) {
				JSONObject resultJson = new JSONObject(sender.rv.info);
				String pook_result = resultJson.optString("result");
				String pook_message = resultJson.optString("message");
				if (pook_result.equals("S")) {
					pookObj.put("email", email);
					SqlString sqlStr = new SqlString();
					sqlStr.add("pook", pookObj.toString());
					dbHelper.openConnection();
					update(dbHelper, userid, sqlStr);
					dbHelper.closeConnection();
					result = true;
				}
				returnStr = pook_message;
			} else {
				returnStr = sender.rv.info;
			}
			
			StringBuffer sbRemark = new StringBuffer();
			sbRemark
			.append("邮箱地址：")
			.append(email)
			.append("\r\n身份证：")
			.append(card)
			.append("\r\n绑定结果：")
			.append(returnStr);
			GameLog.getInst(playerid, GameServlet.ACT_USER_BIND_EMAIL)
			.addRemark(sbRemark)
			.save();
			return new ReturnValue(result, returnStr);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 修改密码
	 */
	public ReturnValue modifyPwd(int userid, String oldpwd, String newpwd, String ip, int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			if(oldpwd.equals(newpwd)){
				BACException.throwInstance("新密码与原密码相同");
			}
			dbHelper.openConnection();
			ResultSet dataRs = dbHelper.query(tab_user, "pook,channel,platform", "id="+userid);
			if(!dataRs.next()){
				BACException.throwInstance("找不到用户："+userid);
			}
			String platform = dataRs.getString("platform");
			if(!platform.equals("001")){
				BACException.throwInstance("错误的运营渠道："+platform);
			}
			JSONObject pookObj = getSafetyInfo(dataRs.getString("pook"), ip);
			dbHelper.closeConnection();
			NetFormSender sender = new NetFormSender(PookNet.modifypwd_do);
			sender.addParameter("userId", pookObj.getInt("uid"));
			sender.addParameter("userToken", pookObj.getString("token"));
			sender.addParameter("oldPassword", oldpwd);
			sender.addParameter("iPassword", newpwd);
			sender.addParameter("rePassword", newpwd);
			sender.addParameter("ipString", ip);
			StringBuffer ticket = new StringBuffer();
			ticket.append(pookObj.getInt("uid"));
			ticket.append("_");
			ticket.append(pookObj.getString("token"));
			ticket.append("_");
			ticket.append(oldpwd);
			ticket.append("_");
			ticket.append(newpwd);
			ticket.append("_");
			ticket.append(newpwd);
			ticket.append("_");
			ticket.append(ip);
			ticket.append("_");
			ticket.append(PookNet.screctKey);
			sender.addParameter("ticket", MD5.encode(ticket.toString()));
			sender.send().check();
			
			boolean result = false;
			String returnStr = null;
			if(sender.rv.success) {
				JSONObject resultJson = new JSONObject(sender.rv.info);
				String pook_result = resultJson.optString("result");
				String pook_message = resultJson.optString("message");
				if (pook_result.equals("S")) {
					result = true;
				}
				returnStr = pook_message;
			} else {
				returnStr = sender.rv.info;
			}
			GameLog.getInst(playerid, GameServlet.ACT_USER_MODIFY_PWD)
			.addRemark("修改结果："+returnStr)
			.save();
			return new ReturnValue(result, returnStr);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取用户安全信息的绑定状态
	 */
	public ReturnValue getSafetyBindState(int userid, String ip){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet dataRs = dbHelper.query(tab_user, "pook,channel,platform", "id="+userid);
			if(!dataRs.next()){
				BACException.throwInstance("找不到用户："+userid);
			}
			String platform = dataRs.getString("platform");
			if(!platform.equals("001")){
				BACException.throwInstance("错误的运营渠道："+platform);
			}
			JSONObject pookObj = getSafetyInfo(dataRs.getString("pook"), ip);
			JSONArray jsonarr = new JSONArray();
			String card = pookObj.getString("card");
			String mobile = pookObj.getString("mobile");
			String email = pookObj.getString("email");
			jsonarr.add(MyTools.getEncrypeStr(card, 6, card.length()-2));
			jsonarr.add(MyTools.getEncrypeStr(mobile, mobile.length()/2, mobile.length()));
			jsonarr.add(MyTools.getEncrypeStr(email, 3, 6));
			return new ReturnValue(true, jsonarr.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取手机号
	 */
	public ReturnValue getPhonenum(int userid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet userRs = dbHelper.query(tab_user, "phonenum", "id="+userid);
			if(!userRs.next()){
				BACException.throwInstance("用户未找到");
			}
			return new ReturnValue(true, userRs.getString("phonenum"));
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 填写手机号
	 */
	public ReturnValue writePhonenum(int userid, String phonenum){
		DBHelper dbHelper = new DBHelper();
		try {
			if(phonenum == null || !phonenum.matches("^(13|14|15|17|18)\\d{9}$")){
				BACException.throwInstance("手机号不合法");
			}
			dbHelper.openConnection();
			ResultSet userRs = dbHelper.query(tab_user, "phonenum", "id="+userid);
			if(!userRs.next()){
				BACException.throwInstance("用户未找到");
			}
			if(userRs.getString("phonenum") != null){
				BACException.throwInstance("已填写过手机号");
			}
			SqlString sqlStr = new SqlString();
			sqlStr.add("phonenum", phonenum);
			dbHelper.update(tab_user, sqlStr, "id="+userid);
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	public ReturnValue updateUserStep(int userid, int stepId) {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet userRs = dbHelper.query(tab_user, "stepid", "id="+userid);
			if (userRs.next()) {
				if (userRs.getInt("stepid") < stepId) {
					SqlString sqlStr = new SqlString();
					sqlStr.add("stepid", stepId);
					dbHelper.update(tab_user, sqlStr, "id="+userid);
				}
			}
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取用户安全信息
	 */
	public JSONObject getSafetyInfo(String str, String ip) throws Exception {
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
		//System.out.println("获取波克用户安全信息返回："+sender.rv.info);
		if(sender.rv.success)
		{
			JSONObject resultJson = new JSONObject(sender.rv.info);
			String result = resultJson.optString("result");
			String message = resultJson.optString("message");
			if(result.equals("S"))
			{	
				pookObj.put("card", resultJson.optString("cardNo").toUpperCase());
				pookObj.put("mobile", resultJson.optString("mobilePhone"));
				pookObj.put("email", resultJson.optString("email"));
				return pookObj;				
			}
			else
			{				
				BACException.throwInstance("波克返回："+message);
			}
		}
		else
		{
			BACException.throwInstance("波克返回："+sender.rv.info);
		}
		return null;		
	}
	
	/**
	 * 创建USER对象
	 */
	public User createUser(String sessionid) throws Exception {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			if(sessionid==null || sessionid.equals("") || sessionid.equals("0")){
				BACException.throwInstance("用户登录异常");
			}
			String str = (String)MemcachedUtil.get(sessionid);
			if(str == null || str.equals("")){
				BACException.throwInstance("登录已失效，请重新登录");
			}
			JSONArray jsonarr = new JSONArray(str);
			User user = new User();
			user.uid = jsonarr.optInt(1);
			user.channel = jsonarr.optString(2);
			session_usermap.put(sessionid, user);
			return user;
		} catch (Exception e) {
			throw e;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 更新
	 */
	public void update(DBHelper dbHelper, int userid, SqlString sqlStr) throws Exception {
		dbHelper.update(tab_user, sqlStr, "id="+userid);
	}
	
	//--------------静态区--------------
	
	private static UserBAC instance = new UserBAC();
	
	/**
	 * 获取实例
	 */
	public static UserBAC getInstance(){
		return instance;
	}
}
