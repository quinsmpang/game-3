package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.ehc.common.ReturnValue;
import com.moonic.bac.CBBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 国战产出资金计时器
 * @author John
 */
public class CBOutPutFacMoneyTT extends MyTimerTask {
	
	/**
	 * 执行
	 */
	public void run2() {
		ReturnValue rv = CBBAC.getInstance().outputFacMoney();
		Out.println("国战产出资金，结果："+rv.info);
	}
	
	/**
	 * 初始化计时器
	 */
	public static void init(){
		long delay = MyTools.long_hour-((System.currentTimeMillis()-MyTools.getCurrentDateLong())%MyTools.long_hour);
		ServerBAC.timer.scheduleAtFixedRate(new CBOutPutFacMoneyTT(), delay, MyTools.long_hour, TimeUnit.MILLISECONDS);
		Out.println("启动国战产出资金计时器完成 下次产出时间："+MyTools.getTimeStr(System.currentTimeMillis()+delay));
	}
}
