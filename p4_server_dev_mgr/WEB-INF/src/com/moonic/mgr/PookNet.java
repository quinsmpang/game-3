package com.moonic.mgr;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import server.config.ServerConfig;

import com.moonic.util.Out;

public class PookNet {
	/**
	 * 波克注册
	 */
	public static String login_do;
	/**
	 * 波克登录
	 */
	public static String register_do;
	/**
	 * 手机找回密码
	 */
	public static String mobilefindpwd_do;
	/**
	 * 邮箱找回密码
	 */
	public static String emailfindpwd_do;
	/**
	 * 获取安全信息
	 */
	public static String getsafety_do;
	
	/**
	 * 充值中心接口
	 */
	public static String chargecenter_do;
	
	/**
	 * 获取波克卡面额
	 */
	public static String getcardvalue_do;
	
	public static final String screctKey = "REWREWdsjksu32uksjf35468"; //波克登录秘钥
	
	/**
	 * 初始化波克请求地址配置
	 */
	public static void initPokerReq(){
		Document document;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(ServerConfig.getWebInfPath() + "conf/poker_req.xml");
			Element root = document.getRootElement();
			login_do = root.element("login").getText();
			register_do = root.element("register").getText();
			mobilefindpwd_do = root.element("mobilefindpwd").getText();
			emailfindpwd_do = root.element("emailfindpwd").getText();
			getsafety_do = root.element("getsafety").getText();
			chargecenter_do = root.element("chargecenter").getText();
			getcardvalue_do = root.element("getcardvalue").getText();
			Out.println("波克请求地址配置初始化完成");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
