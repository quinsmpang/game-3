package com.moonic.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import server.common.Tools;
import server.config.ServerConfig;

/**
 * 配置文件
 * @author John
 */
public class ConfFile2 {
	private static Hashtable<String, ArrayList<String>> filenametab = new Hashtable<String, ArrayList<String>>();
	
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
		String[] valuearr = Tools.splitStr(value, "\r\n");
		ArrayList<String> valueList = new ArrayList<String>();
		for(int i = 0; valuearr != null && i < valuearr.length; i++){
			valueList.add(valuearr[i]);
		}
		filenametab.put(filename, valueList);
		return value;
	}
	
	/**
	 * 获取配置值
	 */
	public static ArrayList<String> getFileValue(String filename){
		if(!filenametab.containsKey(filename)){
			System.out.println("获取配置-配置文件 "+filename+" 未在启动时初始化");
		}
		return filenametab.get(filename);
	}
	
	/**
	 * 更新配置值
	 */
	public static void addValue(String filename, Object... value){
		if(!filenametab.containsKey(filename)){
			System.out.println("更新配置-配置文件 "+filename+" 未在启动时初始化");
		}
		if(value == null || value.length == 0){
			return;
		}
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < value.length; i++){
			filenametab.get(filename).add(value[i].toString());
			sb.append("\r\n");
			sb.append(value[i]);
		}
		(new FileUtil()).addToTxt(getFilePath(filename), sb.toString());
	}
	
	/**
	 * 清空
	 */
	public static void clear(String filename){
		filenametab.get(filename).clear();
		(new FileUtil()).writeNewToTxt(getFilePath(filename), "");
	}
	
	/**
	 * 获取存储时间文件路径
	 */
	private static String getFilePath(String filename){
		return ServerConfig.getWebInfPath()+"txt_conf/"+filename+".txt";
	}
}
