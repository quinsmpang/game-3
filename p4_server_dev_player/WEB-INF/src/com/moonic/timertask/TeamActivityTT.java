package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.ehc.common.ReturnValue;
import com.moonic.bac.ServerBAC;
import com.moonic.bac.PlaTeamBAC;
import com.moonic.txtdata.TeamActivityData;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 组队活动计时器
 * @author wkc
 */
public class TeamActivityTT extends MyTimerTask {
	
	/**
	 * 执行
	 */
	public void run2() {
		try {
			ReturnValue rv = PlaTeamBAC.getInstance().start(MyTools.long_minu*TeamActivityData.actiTimeLen, (byte)0);
			Out.println("启动组队活动结果：" + rv.info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化
	 */
	public static void init(){
		long delay = 0;
		long current = System.currentTimeMillis();
		int[] weekarr = TeamActivityData.weekarr;
		String[] timearr = TeamActivityData.timearr;
		for(int i = 0; i < weekarr.length; i++){
			for(int j = 0; j < timearr.length; j++){
				long point = MyTools.getTimeLong(MyTools.getDateStr()+" "+timearr[j]) - MyTools.getCurrentDateLong();
				long targettime = MyTools.getNextWeekDay(current, weekarr[i], point) + point;
				if(current >= targettime){
					targettime += MyTools.long_day*7;
				}
				delay = targettime - current;
				if(delay < 0){
					delay = 0;
				}
				ServerBAC.timer.scheduleAtFixedRate(new TeamActivityTT(), delay, MyTools.long_day*7, TimeUnit.MILLISECONDS);
			}
		}
		Out.println("启动组队活动计时器完成");
	}
}
