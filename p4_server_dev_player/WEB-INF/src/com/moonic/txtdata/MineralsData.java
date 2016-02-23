package com.moonic.txtdata;

import server.common.Tools;

import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;
import com.moonic.util.MyTools;

/**
 * 挖矿
 * @author John
 */
public class MineralsData {
	//各级别坑位数量
	public static int[] posamount;
	//开始时间
	public static String[] opentime;
	//每场活动持续时间（分钟）
	public static int continuoustime;
	//奖励结算单位时间（分钟）
	public static long rewardtime;
	//免费抢夺次数
	public static int robberynum;
	//购买抢夺次数价格（金锭）
	public static int buyrobbery;
	//抢夺失败CD（分钟）
	public static long losetime;
	//各级别收获倍数
	public static int[] markon;
	//奖励参数
	public static String[][] awardpara;
	
	static {
		init();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			public void callback(String key) {
				if(key.equals("minerals")){
					init();
				}
			}
		});
	}
	
	public static void init() {
		try {
			String fileText = DBPool.getInst().readTxtFromPool("minerals");
			posamount = Tools.splitStrToIntArr(Tools.getStrProperty(fileText, "posamount"), ",");
			opentime = Tools.splitStr(Tools.getStrProperty(fileText, "opentime"), ",");
			continuoustime = Tools.getIntProperty(fileText, "continuoustime");
			rewardtime = Tools.getIntProperty(fileText, "rewardtime") * MyTools.long_minu;
			robberynum = Tools.getIntProperty(fileText, "robberynum");
			buyrobbery = Tools.getIntProperty(fileText, "buyrobbery");
			losetime = Tools.getIntProperty(fileText, "losetime") * MyTools.long_minu;
			markon = Tools.splitStrToIntArr(Tools.getStrProperty(fileText, "markon"), ",");
			awardpara = Tools.getStrLineArrEx2(fileText, "awardpara:", "awardparaEnd");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
