package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.moonic.bac.ServerBAC;
import com.moonic.bac.ServerFacBAC;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;

/**
 * 更新帮派排名计时器
 * @author John
 */
public class FacRankingTT extends MyTimerTask {
	
	/**
	 * 执行
	 */
	public void run2() {
		try {
			ServerFacBAC.getInstance().updateFactionRanking();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//-------------静态区----------------
	
	/**
	 * 初始化
	 */
	public static void init(){
		ServerBAC.timer.scheduleAtFixedRate(new FacRankingTT(), 0, MyTools.long_minu*10, TimeUnit.MILLISECONDS);
	}
}
