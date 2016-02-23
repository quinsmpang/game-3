package com.moonic.util;

import conf.Conf;

/**
 * 服务器之发请求通讯
 * @author John
 */
public class STSNetSender extends NetSender {
	
	/**
	 * 构造
	 */
	public STSNetSender(short act) throws Exception {
		super(act);
		if(Conf.stsKey == null){
			Conf.stsKey = "识别密匙未设置";
		}
		dos.writeUTF(Conf.stsKey);
	}
}
