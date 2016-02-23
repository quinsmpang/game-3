package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.moonic.bac.CustomActivityBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.bac.SysMailBAC;
import com.moonic.util.ConfFile;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 清理数据计时器
 * @author John
 */
public class ClearDataTT extends MyTimerTask {
	private static final String CLEAR_DATA_TIME = "cleardatatime_v1";
	
	/**
	 * 执行
	 */
	public void run2() {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			String nexttimeStr = MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day+MyTools.long_hour*3);
			SysMailBAC.getInstance().clearData(dbHelper);
			CustomActivityBAC.getInstance().clearData(dbHelper);
			ConfFile.updateFileValue(CLEAR_DATA_TIME, nexttimeStr);
			Out.println("执行清理数据 下次清理时间："+nexttimeStr);
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
		long defaulttime = MyTools.getCurrentDateLong()+MyTools.long_hour*3;
		if(System.currentTimeMillis() > defaulttime){
			defaulttime += MyTools.long_day;
		}
		long filetime = MyTools.getTimeLong(ConfFile.getFileValueInStartServer(CLEAR_DATA_TIME, MyTools.getTimeStr(defaulttime)));
		long delay = 0;
		if(MyTools.checkSysTimeBeyondSqlDate(filetime)){
			ServerBAC.timer.schedule(new ClearDataTT(), 0, TimeUnit.MILLISECONDS);
			delay = defaulttime-System.currentTimeMillis();
		} else {
			delay = filetime-System.currentTimeMillis();
		}
		ServerBAC.timer.scheduleAtFixedRate(new ClearDataTT(), delay, MyTools.long_day, TimeUnit.MILLISECONDS);
		Out.println("启动清理数据计时器完成");
	}
}
