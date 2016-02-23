package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.moonic.bac.PlaJJCRankingBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.txtdata.JJCChallengeData;
import com.moonic.util.ConfFile;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 竞技场奖励发放计时器
 * @author John
 */
public class JJCAwardIssueTT extends MyTimerTask {
	public static final String TIME_JJC_AWARD_ISSUE = "jjcawardissue";
	
	/**
	 * 执行
	 */
	public void run2() {
		try {
			Out.println("开始发放竞技场奖励");
			long nexttime = MyTools.getCurrentDateLong()+JJCChallengeData.forbiddenstarttime;
			System.out.println("------竞技场发放奖励调试------nexttime:"+nexttime+" curr"+System.currentTimeMillis());
			PlaJJCRankingBAC.getInstance().issueAward("计时器");
			if(MyTools.checkSysTimeBeyondSqlDate(nexttime)){
				nexttime += MyTools.long_day;
			}
			String nexttimeStr = MyTools.getTimeStr(nexttime);
			ConfFile.updateFileValue(TIME_JJC_AWARD_ISSUE, nexttimeStr);
			Out.println("发放竞技场奖励完成，下次发放奖励时间："+nexttimeStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化竞技场奖励计时器
	 */
	public static void init(){
		try {
			long defaulttime = MyTools.getCurrentDateLong()+JJCChallengeData.forbiddenstarttime;
			if(defaulttime < System.currentTimeMillis()){
				defaulttime += MyTools.long_day;
			}
			long filetime = MyTools.getTimeLong(ConfFile.getFileValueInStartServer(TIME_JJC_AWARD_ISSUE, MyTools.getTimeStr(defaulttime)));
			long delay = 0;
			if(MyTools.checkSysTimeBeyondSqlDate(filetime)){
				ServerBAC.timer.schedule(new JJCAwardIssueTT(), 0, TimeUnit.MILLISECONDS);
				delay = defaulttime-System.currentTimeMillis();
			} else {
				delay = filetime-System.currentTimeMillis();
			}
			ServerBAC.timer.scheduleAtFixedRate(new JJCAwardIssueTT(), delay, MyTools.long_day, TimeUnit.MILLISECONDS);
			Out.println("启动竞技场奖励发放计时器完成 下次执行时间："+MyTools.getTimeStr(System.currentTimeMillis()+delay));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
