package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.moonic.bac.PlaTowerBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.util.ConfFile;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 轮回塔发送奖励计时器
 * @author wkc
 */
public class TowerSendAwardTT extends MyTimerTask {
	private static final String TOWER_SEND_AWARD_TIME = "towersendaward_v1";
	
	/**
	 * 执行
	 */
	public void run2() {
		DBHelper dbHelper = new DBHelper();
		try {
			PlaTowerBAC.getInstance().sendAward(dbHelper);
			long nexttime = MyTools.getCurrentDateLong() + MyTools.long_hour*4;
			if(MyTools.checkSysTimeBeyondSqlDate(nexttime)){
				nexttime += MyTools.long_day;
			}
			String nexttimeStr = MyTools.getTimeStr(nexttime);
			ConfFile.updateFileValue(TOWER_SEND_AWARD_TIME, nexttimeStr);
			Out.println("执行轮回塔发送奖励 下次执行时间："+nexttimeStr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 初始化计时器
	 */
	public static void init(){
		long defaulttime = MyTools.getCurrentDateLong() + MyTools.long_hour*4;
		if(System.currentTimeMillis() > defaulttime){
			defaulttime += MyTools.long_day;
		}
		long filetime = MyTools.getTimeLong(ConfFile.getFileValueInStartServer(TOWER_SEND_AWARD_TIME, MyTools.getTimeStr(defaulttime)));
		long delay = 0;
		if(MyTools.checkSysTimeBeyondSqlDate(filetime)){
			ServerBAC.timer.schedule(new TowerSendAwardTT(), 0, TimeUnit.MILLISECONDS);
			delay = defaulttime-System.currentTimeMillis();
		} else {
			delay = filetime-System.currentTimeMillis();
		}
		ServerBAC.timer.scheduleAtFixedRate(new TowerSendAwardTT(), delay, MyTools.long_day, TimeUnit.MILLISECONDS);
		Out.println("启动轮回塔发送奖励计时器完成");
	}
}
