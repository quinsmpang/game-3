package com.moonic.util;

import java.io.File;
import java.util.Hashtable;

import server.config.ServerConfig;

/**
 * 配置文件
 * @author John
 */
public class ConfFile {
	private static Hashtable<String, String> filenametab = new Hashtable<String, String>();
	
	/**
	 * 启动服务器时获取配置值
	 */
	public static String getFileValueInStartServer(String filename, String defaultValue){
		String value = null;
		String path = getFilePath(filename);
		File file = new File(path);
		if(file.exists()){
			value = MyTools.readTxtFile(path);
		} else {
			value = defaultValue;
			(new FileUtil()).writeNewToTxt(getFilePath(filename), value);
		}
		filenametab.put(filename, value);
		return value;
	}
	
	/**
	 * 获取配置值
	 */
	public static String getFileValue(String filename){
		if(!filenametab.containsKey(filename)){
			System.out.println("获取配置-配置文件 "+filename+" 未在启动时初始化");
		}
		return filenametab.get(filename);
	}
	
	/**
	 * 更新配置值
	 */
	public static void updateFileValue(String filename, String value){
		if(!filenametab.containsKey(filename)){
			System.out.println("更新配置-配置文件 "+filename+" 未在启动时初始化");
		}
		(new FileUtil()).writeNewToTxt(getFilePath(filename), value);
		filenametab.put(filename, value);
	}
	
	/**
	 * 获取存储时间文件路径
	 */
	private static String getFilePath(String filename){
		return ServerConfig.getWebInfPath()+"txt_conf/"+filename+".txt";
	}
}
