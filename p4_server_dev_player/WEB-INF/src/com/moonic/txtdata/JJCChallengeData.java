package com.moonic.txtdata;

import server.common.Tools;

import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;
import com.moonic.util.MyTools;


/**
 * æ∫ºº≥°ÃÙ’Ω
 * @author John
 */
public class JJCChallengeData {
	public static int winmoney;
	public static int winjjccoin;
	public static int losemoney;
	public static int losejjccoin;
	public static int maxmoney;
	public static int maxjjccoin;
	public static int partnerexp;
	public static long losewaittimelen;
	public static int clearwaittimeneedcoin;
	public static int refreshoppneedcoin;
	public static String forbiddenstartStr;
	public static String forbiddenendStr;
	public static long forbiddenstarttime;
	public static long forbiddenendtime;
	
	static {
		init();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			public void callback(String key) {
				if(key.equals("jjc_challenge")){
					init();
				}
			}
		});
	}
	
	public static void init() {
		try {
			String fileText = DBPool.getInst().readTxtFromPool("jjc_challenge");
			winmoney = Tools.getIntProperty(fileText, "winmoney");
			winjjccoin = Tools.getIntProperty(fileText, "winjjccoin");
			losemoney = Tools.getIntProperty(fileText, "losemoney");
			losejjccoin = Tools.getIntProperty(fileText, "losejjccoin");
			maxmoney = Tools.getIntProperty(fileText, "maxmoney");
			maxjjccoin = Tools.getIntProperty(fileText, "maxjjccoin");
			partnerexp = Tools.getIntProperty(fileText, "partnerexp");
			losewaittimelen = Tools.getIntProperty(fileText, "losewaittimelen")*MyTools.long_minu;
			clearwaittimeneedcoin = Tools.getIntProperty(fileText, "clearwaittimeneedcoin");
			refreshoppneedcoin = Tools.getIntProperty(fileText, "refreshoppneedcoin");
			forbiddenstartStr = Tools.getStrProperty(fileText, "forbiddenstarttime");
			forbiddenendStr = Tools.getStrProperty(fileText, "forbiddenendtime");
			forbiddenstarttime = MyTools.getPointTimeLong(forbiddenstartStr);
			forbiddenendtime = MyTools.getPointTimeLong(forbiddenendStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
