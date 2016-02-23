package com.moonic.util;

import com.ehc.common.ReturnValue;

/**
 * 返回结果
 * @author John
 */
public class NetResult {
	public byte servertype;//类型
	public int serverid;//ID
	public String name;//名称
	public String urlStr;//发送地址
	public byte result;//请求结果
	public byte[] buff;//二进制数据
	public String strData;//字符串数据
	public ReturnValue rv;//结果的ReturnValue表达形式
	
	/**
	 * 结果检查
	 */
	public void check() throws Exception {
		if(result==0){
			BACException.throwInstance(strData);
		}
	}
}