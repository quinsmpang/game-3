package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import com.moonic.bac.PlaSummonBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.util.ConfFile;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 神秘召唤日物品计时器
 * @author wkc
 */
public class SummonDayTT extends MyTimerTask {
	public static final String SUMMON_DAY = "summon_day_v1";
	public static final String TIME_SUMMON_DAY = "time_summon_day_v1";
	
	/**
	 * 执行
	 */
	public void run2() {
		try {
			PlaSummonBAC.mystery_day = PlaSummonBAC.getInstance().createMysteryItem((byte)2, 3);
			String nexttime = MyTools.getTimeStr(MyTools.getCurrentDateLong() + MyTools.long_day + MyTools.long_hour*5);
			ConfFile.updateFileValue(SUMMON_DAY, PlaSummonBAC.mystery_day.toString());
			ConfFile.updateFileValue(TIME_SUMMON_DAY, nexttime);
			PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_SUMMON_DAY_ITEM, PlaSummonBAC.mystery_day.toString());
			Out.println("执行刷新神秘召唤日物品");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 初始化
	 */
	public static void init(){
		try{
			long defaulttime = MyTools.getCurrentDateLong() + MyTools.long_hour*5;
			if(System.currentTimeMillis() > defaulttime){
				defaulttime += MyTools.long_day;
			}
			String dayStr = ConfFile.getFileValueInStartServer(SUMMON_DAY, "");
			if(!dayStr.equals("")){
				PlaSummonBAC.mystery_day = new JSONArray(dayStr);
			} else{
				ServerBAC.timer.schedule(new SummonDayTT(), 0, TimeUnit.MILLISECONDS);
			}
			long filetime = MyTools.getTimeLong(ConfFile.getFileValueInStartServer(TIME_SUMMON_DAY, MyTools.getTimeStr(defaulttime)));
			long delay = 0;
			if(MyTools.checkSysTimeBeyondSqlDate(filetime)){
				ServerBAC.timer.schedule(new SummonDayTT(), 0, TimeUnit.MILLISECONDS);
				delay = defaulttime - System.currentTimeMillis();
			} else {
				delay = filetime - System.currentTimeMillis();
			}
			ServerBAC.timer.scheduleAtFixedRate(new SummonDayTT(), delay, MyTools.long_day, TimeUnit.MILLISECONDS);
			Out.println("启动初始化刷新神秘召唤日数据计时器完成"+MyTools.getTimeStr(filetime));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}