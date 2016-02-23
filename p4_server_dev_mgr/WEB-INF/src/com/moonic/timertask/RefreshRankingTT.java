package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.moonic.bac.RankingBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 刷新游戏排行计时器
 * @author John
 */
public class RefreshRankingTT extends MyTimerTask {
	public long exetime;
	
	/**
	 * 构造
	 */
	public RefreshRankingTT(long exetime){
		this.exetime = exetime;
	}
	
	/**
	 * 执行
	 */
	public void run2() {
		try {
			RankingBAC.getInstance().refreshRanking(exetime);
			exetime += MyTools.long_minu*30;
			Out.println("执行刷新游戏排行 下次执行时间："+MyTools.getTimeStr(exetime)+"("+exetime+")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化计时器
	 */
	public static void init(){
		long currtime = System.currentTimeMillis();
		long delay = MyTools.long_minu*30-(currtime-MyTools.getCurrentDateLong())%(MyTools.long_minu*30);
		ServerBAC.timer.scheduleAtFixedRate(new RefreshRankingTT(currtime+delay), delay, MyTools.long_minu*30, TimeUnit.MILLISECONDS);
		Out.println("启动刷新游戏排行计时器完成 下次执行时间："+MyTools.getTimeStr(currtime+delay)+"("+(currtime+delay)+")");
	}
}
