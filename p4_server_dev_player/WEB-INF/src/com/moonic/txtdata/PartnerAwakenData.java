package com.moonic.txtdata;

import server.common.Tools;

import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;


/**
 * »ï°é¾õÐÑÊý¾Ý
 * @author John
 */
public class PartnerAwakenData {
	public static int awaken_needphase;
	public static int awaken_needstar;
	public static int awaken_needmoney;
	
	static {
		init();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			public void callback(String key) {
				if(key.equals("partner_awaken")){
					init();
				}
			}
		});
	}
	
	public static void init() {
		try {
			String fileText = DBPool.getInst().readTxtFromPool("partner_awaken");
			awaken_needphase = Tools.getIntProperty(fileText, "needphase");
			awaken_needstar = Tools.getIntProperty(fileText, "needstar");
			awaken_needmoney = Tools.getIntProperty(fileText, "needmoney");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
