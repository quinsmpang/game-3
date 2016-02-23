package com.moonic.txtdata;

import server.common.Tools;

import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;

/**
 * 轮回塔数据
 * @author wkc
 */
public class TowerData {
	//基础积分
	public static int[] basicArr;
	//星级倍率
	public static int[] starArr;
	//难度对应机器人类型
	public static byte[] typeArr;
	//禁止挑战起始时间
	public static String forbiddenstarttime;
	//禁止挑战终止时间
	public static String forbiddenendtime;
	
	static {
		init();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			public void callback(String key) {
				if(key.equals("tower")){
					init();
				}
			}
		});
	}
	
	public static void init() {
		try {
			String fileText = DBPool.getInst().readTxtFromPool("tower");
			basicArr = Tools.splitStrToIntArr(Tools.getStrProperty(fileText, "basic"), ",");
			starArr = Tools.splitStrToIntArr(Tools.getStrProperty(fileText, "star"), ",");
			typeArr = Tools.splitStrToByteArr(Tools.getStrProperty(fileText, "type"), ",");
			forbiddenstarttime = Tools.getStrProperty(fileText, "forbiddenstarttime");
			forbiddenendtime = Tools.getStrProperty(fileText, "forbiddenendtime");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
