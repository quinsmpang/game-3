package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.moonic.bac.CBBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.txtdata.CBDATA;
import com.moonic.util.ConfFile;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

import conf.Conf;

public class CBRefWorldLvTT extends MyTimerTask {
	public static final String WORLDLEVEL = "worldlevle";
	
	/**
	 * 执行
	 */
	public void run2() {
		try {
			ReturnValue rv = CBBAC.getInstance().updateWorldLevel();
			Out.println("执行更新世界等级 更新结果："+rv.info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化计时器
	 */
	public static void init(){
		Conf.worldLevel = Tools.str2int(ConfFile.getFileValueInStartServer(WORLDLEVEL, "10"));
		long delay = MyTools.getCurrentDateLong()+CBDATA.worldclassrefresh-System.currentTimeMillis();
		if(delay <= 0){
			delay += MyTools.long_day;
		}
		ServerBAC.timer.scheduleAtFixedRate(new CBRefWorldLvTT(), delay, MyTools.long_day, TimeUnit.MILLISECONDS);
		Out.println("初始化更新世界等级计时器完成 下次执行时间："+MyTools.getTimeStr(System.currentTimeMillis()+delay));
	}
}
