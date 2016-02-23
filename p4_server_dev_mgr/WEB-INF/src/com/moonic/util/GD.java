package com.moonic.util;


/**
 * 调试工具集
 * @author John
 */
public class GD {
	public StringBuffer sb = new StringBuffer();
	public boolean save;
	
	/**
	 * 构造
	 * @param save
	 */
	public GD(boolean save){
		this.save = save;
	}
	
	/**
	 * 输出
	 */
	public void print(String str){
		if(save){
			sb.append(str);
		}
	}
	
	/**
	 * 输出
	 */
	public void println(String str){
		if(save){
			sb.append(str+"\r\n");
		}
	}
}
