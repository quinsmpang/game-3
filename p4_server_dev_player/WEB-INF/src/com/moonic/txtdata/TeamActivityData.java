package com.moonic.txtdata;

import server.common.Tools;

import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;

/**
 * 组队数据
 * @author wkc
 */
public class TeamActivityData {
	//活动周期
	public static int[] weekarr;
	//活动时间
	public static String[] timearr;
	//活动时长
	public static int actiTimeLen;
	//可获得奖励次数
	public static int times;
	
	static {
		init();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			public void callback(String key) {
				if(key.equals("team")){
					init();
				}
			}
		});
	}
	
	public static void init() {
		try {
			String fileText = DBPool.getInst().readTxtFromPool("team");
			weekarr = Tools.splitStrToIntArr(Tools.getStrProperty(fileText, "week"), ",");
			timearr = Tools.splitStr(Tools.getStrProperty(fileText, "time"), ",");
			actiTimeLen = Tools.getIntProperty(fileText, "timelen");
			times = Tools.getIntProperty(fileText, "times");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
