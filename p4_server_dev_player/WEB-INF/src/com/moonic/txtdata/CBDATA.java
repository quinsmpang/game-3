package com.moonic.txtdata;

import server.common.Tools;

import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;
import com.moonic.util.MyTools;

/**
 * 国战
 * @author John
 */
public class CBDATA {
	//允许宣战起始时间
	public static long declarewarstarttime;
	//允许宣战终止时间
	public static long declarewarendtime;
	//允许参战起始时间
	public static long joinwarstarttime;
	//允许参战终止时间
	public static long joinwarendtime;
	//城市资金产出起始时间
	public static long cityoutputstarttime;
	//城市资金产出终止时间
	public static long cityoutputendtime;
	//宣战时长（分钟）
	public static int declarewarwaittimelen;
	//战斗间隔时长（秒）
	public static int battlespacetimelen;
	//免战时长（分钟）
	public static int nowartimelen;
	//城防并回复速度（每小时）
	public static byte npcrecoverspeed;
	//太守争夺起始时间
	public static long leaderstarttime;
	//太守争夺终止时间
	public static long leaderendtime;
	//太守争夺时间间隔（分钟）
	public static int leaderspacetimelen;
	//太守奖励发放时间
	public static long leaderawardissuetime;
	//太守放弃时间间隔（分钟）
	public static int leadergiveupspacetimelen;
	//助攻消耗体力数
	public static byte assist;
	//世界等级刷新时间
	public static long worldclassrefresh;
	//入侵规模
	public static String[][] invadescale;
	//入侵势力
	public static String[][] invadeinfluence;
	//入侵宣战
	public static String[][] invadedeclare;
	//复活价格
	public static int reliveprice;
	
	static {
		init();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			public void callback(String key) {
				if(key.equals("cb")){
					init();
				}
			}
		});
	}
	
	public static void init() {
		try {
			String fileText = DBPool.getInst().readTxtFromPool("cb");
			declarewarstarttime = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "declarewarstarttime"));
			declarewarendtime = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "declarewarendtime"));
			joinwarstarttime = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "joinwarstarttime"));
			joinwarendtime = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "joinwarendtime"));
			cityoutputstarttime = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "cityoutputstarttime"));
			cityoutputendtime = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "cityoutputendtime"));
			declarewarwaittimelen = Tools.getIntProperty(fileText, "declarewarwaittimelen");
			battlespacetimelen = Tools.getIntProperty(fileText, "battlespacetimelen");
			nowartimelen = Tools.getIntProperty(fileText, "nowartimelen");
			npcrecoverspeed = Tools.getByteProperty(fileText, "npcrecoverspeed");
			leaderstarttime = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "leaderstarttime"));
			leaderendtime = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "leaderendtime"));
			leaderspacetimelen = Tools.getIntProperty(fileText, "leaderspacetimelen");
			leaderawardissuetime = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "leaderawardissuetime"));
			leadergiveupspacetimelen = Tools.getIntProperty(fileText, "leadergiveupspacetimelen");
			assist = Tools.getByteProperty(fileText, "assist");
			worldclassrefresh = MyTools.getPointTimeLong(Tools.getStrProperty(fileText, "worldclassrefresh"));
			invadescale = Tools.getStrLineArrEx2(fileText, "invadescale:", "invadescaleEnd");
			invadeinfluence = Tools.getStrLineArrEx2(fileText, "invadeinfluence:", "invadeinfluenceEnd");
			invadedeclare = Tools.getStrLineArrEx2(fileText, "invadedeclare:", "invadedeclareEnd");
			reliveprice = Tools.getIntProperty(fileText, "reliveprice");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getInfluenceName(int num){
		for(int m = 0; m < invadeinfluence.length; m++){
			if(num == Tools.str2int(invadeinfluence[m][0])){
				return invadeinfluence[m][1];
			}
		}
		return null;
	}
}
