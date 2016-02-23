package com.moonic.txtdata;

import server.common.Tools;

import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;

/**
 * 世界BOSS数据
 * @author wkc
 */
public class WorldBossData {
	//活动周期
	public static int[] weekarr;
	//活动时间
	public static String[] timearr;
	//活动时长
	public static int actiTimeLen;
	//挑战次数
	public static int chaTimes;
	//战斗背景编号
	public static int bgNum;
	
	static {
		init();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			public void callback(String key) {
				if(key.equals("worldboss")){
					init();
				}
			}
		});
	}
	
	public static void init() {
		try {
			String fileText = DBPool.getInst().readTxtFromPool("worldboss");
			weekarr = Tools.splitStrToIntArr(Tools.getStrProperty(fileText, "week"), ",");
			timearr = Tools.splitStr(Tools.getStrProperty(fileText, "time"), ",");
			actiTimeLen = Tools.getIntProperty(fileText, "timelen");
			chaTimes = Tools.getIntProperty(fileText, "chatimes");
			bgNum = Tools.getIntProperty(fileText, "bgnum");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
