package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.moonic.bac.BattleReplayBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

import conf.Conf;

public class ReplayClearTT extends MyTimerTask {

	public void run2() {
		BattleReplayBAC.getInstance().clearExpirationReplay();
		Out.println("清理过期战斗回放");
		
	}
	
	/**
	 * 初始化计时器
	 */
	public static void init(){
		if(Conf.useClearReplayTT){
			ServerBAC.timer.scheduleAtFixedRate(new ReplayClearTT(), 0, MyTools.long_hour, TimeUnit.MILLISECONDS);
			Out.println("启动清理过期战斗回放计时器");		
		}
	}
}
