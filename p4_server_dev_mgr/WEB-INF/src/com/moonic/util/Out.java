package com.moonic.util;

import server.common.Tools;
import conf.Conf;

/**
 * 输出类
 * @author John
 */
public class Out {
	
	public static void println(String str){
		if(str == null){
			try {
				throw new Exception("输出空内容");
			} catch (Exception e) {
				e.toString();
			}
		}
		System.out.println(Conf.stsKey + " -- " + Tools.getCurrentDateTimeStr("yyyy/MM/dd HH:mm:ss") + " -- " + str);
	}
	
	public static String get(String str){
		if(str == null){
			try {
				throw new Exception("输出空内容");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Conf.stsKey + " -- " + Tools.getCurrentDateTimeStr("yyyy/MM/dd HH:mm:ss") + " -- " + str;
	}
}
