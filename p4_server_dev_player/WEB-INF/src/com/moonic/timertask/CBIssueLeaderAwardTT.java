package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import com.ehc.common.ReturnValue;
import com.moonic.bac.CBBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.socket.GamePushData;
import com.moonic.txtdata.CBDATA;
import com.moonic.util.ConfFile;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 国战发放太守奖励计时器
 * @author John
 */
public class CBIssueLeaderAwardTT extends MyTimerTask {
	private static final String CB_ISSUE_LEADER_AWARD = "cbissueleaderaward_v1";
	
	/**
	 * 执行
	 */
	public void run2() {
		ReturnValue rv = CBBAC.getInstance().issueLeaderAward();
		ReturnValue rv2 = CBBAC.getInstance().issueSelfCityAward();
		long nexttime = MyTools.getCurrentDateLong()+CBDATA.leaderawardissuetime;
		if(MyTools.checkSysTimeBeyondSqlDate(nexttime)){
			nexttime += MyTools.long_day;
		}
		String nexttimeStr = MyTools.getTimeStr(nexttime);
		ConfFile.updateFileValue(CB_ISSUE_LEADER_AWARD, nexttimeStr);
		Out.println("国战发放太守奖励，发放结果："+rv.info+" 自有城市产出资金，结果："+rv2.info);
	}
	
	/**
	 * 初始化计时器
	 */
	public static void init(){
		long time1 = MyTools.getCurrentDateLong()+CBDATA.declarewarstarttime-System.currentTimeMillis();
		long time2 = MyTools.getCurrentDateLong()+CBDATA.declarewarendtime-System.currentTimeMillis();
		long time3 = MyTools.getCurrentDateLong()+CBDATA.joinwarendtime-System.currentTimeMillis();
		long time4 = MyTools.getCurrentDateLong()+CBDATA.leaderstarttime-System.currentTimeMillis();
		long time5 = MyTools.getCurrentDateLong()+CBDATA.leaderendtime-System.currentTimeMillis();
		ServerBAC.timer.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					GamePushData.getInstance(9).sendToAllOL();		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, time1>0?time1:(time1+MyTools.long_day), MyTools.long_day, TimeUnit.MILLISECONDS);
		ServerBAC.timer.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					GamePushData.getInstance(10).sendToAllOL();		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, time2>0?time2:(time2+MyTools.long_day), MyTools.long_day, TimeUnit.MILLISECONDS);
		ServerBAC.timer.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					GamePushData.getInstance(11).sendToAllOL();		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, time3>0?time3:(time3+MyTools.long_day), MyTools.long_day, TimeUnit.MILLISECONDS);
		ServerBAC.timer.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					GamePushData.getInstance(13).sendToAllOL();		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, time4>0?time4:(time4+MyTools.long_day), MyTools.long_day, TimeUnit.MILLISECONDS);
		ServerBAC.timer.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					GamePushData.getInstance(15).sendToAllOL();		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, time5>0?time5:(time5+MyTools.long_day), MyTools.long_day, TimeUnit.MILLISECONDS);
		long defaulttime = MyTools.getCurrentDateLong()+CBDATA.leaderawardissuetime;
		if(System.currentTimeMillis() > defaulttime){
			defaulttime += MyTools.long_day;
		}
		long filetime = MyTools.getTimeLong(ConfFile.getFileValueInStartServer(CB_ISSUE_LEADER_AWARD, MyTools.getTimeStr(defaulttime)));
		long delay = 0;
		if(MyTools.checkSysTimeBeyondSqlDate(filetime)){
			ServerBAC.timer.schedule(new CBIssueLeaderAwardTT(), 0, TimeUnit.MILLISECONDS);
			delay = defaulttime-System.currentTimeMillis();
		} else {
			delay = filetime-System.currentTimeMillis();
		}
		ServerBAC.timer.scheduleAtFixedRate(new CBIssueLeaderAwardTT(), delay, MyTools.long_day, TimeUnit.MILLISECONDS);
		Out.println("启动国战发放太守奖励计时器完成 下次发放时间："+MyTools.getTimeStr(System.currentTimeMillis()+delay));
	}
}
