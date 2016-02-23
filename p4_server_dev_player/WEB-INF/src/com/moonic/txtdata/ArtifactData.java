package com.moonic.txtdata;

import server.common.Tools;

import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;
import com.moonic.util.MyTools;

/**
 * 神器
 * @author John
 */
public class ArtifactData {
	//恢复抢夺次数单位时间（分钟）
	public static long robrecovertimelen;
	//最大累积抢夺次数
	public static int maxrobtimes;
	//开启保护所需金锭
	public static int[] openprotectcoin;
	//保护单位时长（分钟）
	public static long protecttimelen;
	
	static {
		init();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			public void callback(String key) {
				if(key.equals("artifact")){
					init();
				}
			}
		});
	}
	
	public static void init() {
		try {
			String fileText = DBPool.getInst().readTxtFromPool("minerals");
			robrecovertimelen = Tools.getIntProperty(fileText, "robrecovertimelen")*MyTools.long_minu;
			maxrobtimes = Tools.getIntProperty(fileText, "maxrobtimes");
			openprotectcoin = Tools.splitStrToIntArr(Tools.getStrProperty(fileText, "openprotectcoin"), ",");
			protecttimelen = Tools.getIntProperty(fileText, "protecttimelen")*MyTools.long_minu;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
