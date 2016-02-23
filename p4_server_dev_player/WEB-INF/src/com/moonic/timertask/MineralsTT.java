package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.moonic.bac.PlaMineralsBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.txtdata.MineralsData;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 挖矿计时器
 * @author John
 */
public class MineralsTT {
	
	static class StartTT extends MyTimerTask {

		public void run2() {
			PlaMineralsBAC.getInstance().start("计时器");
			ServerBAC.timer.schedule(new EndTT(), MineralsData.continuoustime, TimeUnit.MINUTES);
		}
	}
	
	static class EndTT extends MyTimerTask {
		
		public void run2() {
			PlaMineralsBAC.getInstance().end("计时器");
			createStartTT();
		}
	}
	
	public static void createStartTT(){
		try {
			long curr_pointtime = System.currentTimeMillis()-MyTools.getCurrentDateLong();
			long delay = 0;
			for(int i = 0; i < MineralsData.opentime.length; i++){//默认认为数组时间从小到大
				long pointtime = MyTools.getPointTimeLong(MineralsData.opentime[i]);
				if(pointtime > curr_pointtime){
					delay = pointtime - curr_pointtime;
					break;
				}
			}
			if(delay == 0){
				delay = MyTools.long_day + MyTools.getPointTimeLong(MineralsData.opentime[0]) - curr_pointtime;
			}
			Out.println("下次启动挖矿活动时间："+MyTools.getTimeStr(System.currentTimeMillis()+delay));
			ServerBAC.timer.schedule(new StartTT(), delay, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void init(){
		createStartTT();
	}
}
