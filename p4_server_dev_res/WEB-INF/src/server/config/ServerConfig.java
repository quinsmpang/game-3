package server.config;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import server.database.DataBase;

import com.moonic.util.Out;
import com.moonic.util.ProcessQueue;

import conf.Conf;

/**
 * 服务配置
 * @author 
 */
public class ServerConfig implements ServletContextListener { 
	 
	private static String appRoot; 
	
	
	/**
	 * 启动回调
	 */
	public void contextInitialized(ServletContextEvent context) {
		appRoot = context.getServletContext().getRealPath("/");
		
		ServerConfig.init();    
    }   
	
	/**
	 * 退出回调
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		ServerConfig.exit();
	}
	
	/**
	 * 初始化
	 */
	public static void init() {
		try {			
			DataBase.setAppRootPath(getAppRootPath());
			
			Conf.stsKey = "P4资源服务器";
			
			System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(10000));// （单位：毫秒）  
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(10000)); // （单位：毫秒）
			
            initConf();
            
			Thread.sleep(10);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化验证服务器
	 */
	public static void initConf(){
		Document document;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(getWebInfPath() + "conf/conf.xml");
			Element root = document.getRootElement();
			Conf.savepath = root.element("savepath").getText();
			Out.println("服务器配置初始化完成");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 退出
	 */
	public static void exit() {
		ProcessQueue.setRunState(false);
	}
	
	public static String getAppRootPath(){
		return appRoot;
    } 
    
	public static String getWebInfPath() {
		return appRoot + "WEB-INF/";
	}
	
	public static String getPermissionXmlPath(){
		return appRoot + "WEB-INF/conf/permission.xml";
    }
}
