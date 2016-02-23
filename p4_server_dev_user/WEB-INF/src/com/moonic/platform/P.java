package com.moonic.platform;

import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.bac.ChannelBAC;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;

/**
 * 代理类
 * @author John
 */
public abstract class P {
	protected String platform;
	
	/**
	 * 注册
	 */
	public void register(DBHelper dbHelper, String username, String password, String rePassword, String ip, String channel, JSONArray logdata) throws Exception {
		BACException.throwInstance("帐号渠道"+platform+"未提供注册功能");
	}
	
	/**
	 * 登录
	 */
	public JSONObject login(String channel, String extend, String username, String password, String ip, String imei, String mac, int loginport, SqlString userSqlStr) throws Exception {
		ReturnValue rv = checkLogin(username, extend, ip);
		if(!rv.success){
			BACException.throwInstance(rv.info);
		}
		JSONObject returnobj = new JSONObject();
		returnobj.put("username", rv.info);
		return returnobj;
	}
	
	/**
	 * 是否已登录
	 */
	public abstract ReturnValue checkLogin(String username, String extend, String ip) throws Exception ;
	
	//-----------------静态区------------------
	
	public static Hashtable<String, P> platformTab = new Hashtable<String, P>();
	
	/**
	 * 获取单例对象
	 */
	public static P getInstanceByChannel(String channel) throws Exception {
		DBPaRs channelRs = ChannelBAC.getInstance().getChannelListRs(channel);
		if(!channelRs.exist()){
			BACException.throwInstance("联运渠道不存在 channel="+channel);
		}
		return getInstance(channelRs.getString("platform"));
	}
	
	/**
	 * 获取单例对象
	 */
	public static synchronized P getInstance(String platform) throws Exception 
	{
		P p = platformTab.get(platform);
		if(p == null){
			try {
				p = (P)Class.forName("com.moonic.platform.P"+platform).newInstance();
				platformTab.put(platform, p);		
			} catch (ClassNotFoundException e) {
				BACException.throwInstance("帐号渠道不存在 platform="+platform);
			}
			p.platform = platform;
		}
		return p;
	}
}
