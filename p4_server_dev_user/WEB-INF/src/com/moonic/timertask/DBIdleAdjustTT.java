package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import server.config.ServerConfig;

import com.moonic.bac.ServerBAC;
import com.moonic.util.ConfFile;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 数据库连接数调整计时器
 * @author John
 */
public class DBIdleAdjustTT extends MyTimerTask {
	public static final String MIN_IDLE = "min_idle_v1";
	
	public void run2() {
		try {
			int minIdle = ServerConfig.getDataBase().adjustMinIdle();
			ConfFile.updateFileValue(MIN_IDLE, String.valueOf(minIdle));		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化计时器
	 */
	public static void init(){
		long daley = MyTools.getCurrentDateLong()+MyTools.long_day+MyTools.long_hour*4-System.currentTimeMillis();
		ServerBAC.timer.scheduleAtFixedRate(new DBIdleAdjustTT(), daley, MyTools.long_day, TimeUnit.MILLISECONDS);
		Out.println("启动数据库连接数调整计时器完成 下次执行时间："+MyTools.getTimeStr(System.currentTimeMillis()+daley));
	}
}
