package com.moonic.platform;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.bac.UserBAC;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.MD5;

/**
 * 测试
 * @author 
 */
public class P000 extends P {
	
	public void register(DBHelper dbHelper, String username, String password, String rePassword, String ip, String channel, JSONArray logdata) throws Exception {
		UserBAC.getInstance().insert(dbHelper, username, MD5.encode(password).toUpperCase(), channel, platform, ip, logdata);
	}

	public JSONObject login(String channel, String extend, String username, String password, String ip, String imei, String mac, int loginport, SqlString userSqlStr) throws Exception {
		DBHelper dbHelper = new DBHelper();
		JSONObject userJson = dbHelper.queryJsonObj(UserBAC.tab_user, "*", "username='"+username+"' and platform='"+platform+"'");
		if(userJson == null)
		{
			BACException.throwInstance("用户名不存在");
		}
		if(!userJson.optString("password").equals(MD5.encode(password).toUpperCase()))
		{
			BACException.throwInstance("密码错误");
		}
		JSONObject returnobj = new JSONObject();
		returnobj.put("username", username);
		return returnobj;
	}

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception {
		DBHelper dbHelper = new DBHelper();
		JSONObject userJson = dbHelper.queryJsonObj(UserBAC.tab_user, "*", "username='"+username+"' and platform='"+platform+"'");
		if(userJson == null)
		{
			BACException.throwInstance("用户名不存在");
		}
		if(userJson.getInt("onlinestate")==0){
			BACException.throwInstance("尚未登录");
		}
		return new ReturnValue(true, username);
	}
}
