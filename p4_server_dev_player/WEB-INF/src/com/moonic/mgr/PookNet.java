package com.moonic.mgr;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import server.config.ServerConfig;

import com.moonic.util.Out;

public class PookNet {
	/**
	 * 绑定身份证
	 */
	public static String bindcard_do;
	/**
	 * 获取绑定手机的手机验证码
	 */
	public static String getmobilevalidnum_do;
	/**
	 * 绑定手机
	 */
	public static String bindmobile_do;
	/**
	 * 绑定邮箱
	 */
	public static String bindemail_do;
	/**
	 * 修改密码
	 */
	public static String modifypwd_do;
	/**
	 * 获取安全信息
	 */
	public static String getsafety_do;
	/**
	 * 封测发短信
	 */
	public static String cbtmsg_do;
	/**
	 * 波克活动
	 */
	public static String pookacti_do;
	
	public static final String screctKey = "REWREWdsjksu32uksjf35468"; //波克登录秘钥
	
	public static String gotyeURL; //亲加语音服务端接口地址
	
	/**
	 * 初始化波克请求地址配置
	 */
	public static void initPokerReq(){
		Document document;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(ServerConfig.getWebInfPath() + "conf/poker_req.xml");
			Element root = document.getRootElement();
			bindcard_do = root.element("bindcard").getText();
			getmobilevalidnum_do = root.element("getmobilevalidnum").getText();
			bindmobile_do = root.element("bindmobile").getText();
			bindemail_do = root.element("bindemail").getText();
			modifypwd_do = root.element("modifypwd").getText();
			getsafety_do = root.element("getsafety").getText();
			cbtmsg_do = root.element("cbtmsg").getText();
			pookacti_do = root.element("pookacti").getText();
			Out.println("波克请求地址配置初始化完成");
			gotyeURL = root.element("gotye").getText();
			Out.println("亲加语音请求地址配置初始化完成");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
