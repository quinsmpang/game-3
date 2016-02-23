package com.moonic.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import server.config.ServerConfig;
import server.database.DataBase;

/**
 * 测试工具集
 * @author John
 */
public class TestUtil {
	
	/**
	 * 初始化数据库
	 */
	public static void initDB() throws Exception {
		DataBase database = new DataBase();
		DBHelper.setDefaultDataBase(database);
		Document document;
		SAXReader saxReader = new SAXReader();
		document = saxReader.read(ServerConfig.getWebInfPath() + "conf/xianmo_db.xml");
		Element db_conf = document.getRootElement();
		String db_use = db_conf.element("use").getText();
		Element db_info = db_conf.element(db_use);
		database.init(
			db_info.element("driver").getText(), 
			db_info.element("dbname").getText(), 
			db_info.element("username").getText(), 
			db_info.element("password").getText(), 
			Integer.parseInt((db_info.element("maxatv")).getText()), 
			Integer.parseInt(db_info.element("maxidl").getText()), 
			Integer.parseInt(db_info.element("minidl").getText())
		);
	}
}
