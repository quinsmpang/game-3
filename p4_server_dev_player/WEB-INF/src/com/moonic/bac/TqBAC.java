package com.moonic.bac;

import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 特权
 * @author John
 */
public class TqBAC {
	public static final String tab_prerogative = "tab_prerogative";
	
	/**
	 * 改变特权
	 */
	public void changeTQ(DBHelper dbHelper, int playerid, int tqnum, int adddays, GameLog gl) throws Exception {
		DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
		changeTQ(dbHelper, plaRs, playerid, tqnum, adddays, gl);
	}
	
	/**
	 * 改变特权
	 */
	public void changeTQ(DBHelper dbHelper, DBPaRs plaRs, int playerid, int tqnum, int adddays, GameLog gl) throws Exception {
		SqlString sqlStr = new SqlString();
		addChangeTQToSqlStr(plaRs, tqnum, adddays, sqlStr, gl);
		PlayerBAC.getInstance().update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 增加改变特权数据到SqlString
	 */
	public void addChangeTQToSqlStr(DBPaRs plaRs, int tqnum, int adddays, SqlString sqlStr, GameLog gl) throws Exception {
		if(MyTools.checkSysTimeBeyondSqlDate(plaRs.getTime("tqduetime"))){
			long duetime = System.currentTimeMillis()+MyTools.long_day*adddays;
			sqlStr.add("tqnum", tqnum);
			sqlStr.addDateTime("tqduetime", MyTools.getTimeStr(duetime));
			gl.addChaNote("特权编号", 0, tqnum);
			gl.addChaNote("特权到期时间", 0, duetime);
		} else {
			int old_tqnum = plaRs.getInt("tqnum");
			if(tqnum > old_tqnum){
				sqlStr.add("tqnum", tqnum);
				gl.addChaNote("特权编号", old_tqnum, tqnum-old_tqnum);
			}
			long old_duetime = plaRs.getTime("tqduetime");
			long duetime = old_duetime+MyTools.long_day*adddays;
			sqlStr.addDateTime("tqduetime", MyTools.getTimeStr(duetime));
			gl.addChaNote("特权到期时间", old_duetime, duetime-old_duetime);
		}
	}
	
	/**
	 * 检查特权功能是否已开启
	 */
	public boolean checkTQFuncOpen(DBPaRs plaRs, int funcnum) throws Exception {
		return getTQFuncData(plaRs, funcnum) == 1;
	}
	
	/**
	 * 获取特权功能数据
	 */
	public int getTQFuncData(DBPaRs plaRs, int funcnum) throws Exception {
		int num = getTQNum(plaRs);
		DBPaRs tqRs = DBPool.getInst().pQueryA(tab_prerogative, "num="+num);
		if(tqRs.exist()){
			return tqRs.getInt("func"+funcnum);
		} else {
			Out.println("错误的特权编号："+num);
			return 0;
		}
	}
	
	/**
	 * 获取特权编号
	 */
	public int getTQNum(int playerid) throws Exception {
		DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
		return getTQNum(plaRs);
	}
	
	/**
	 * 获取特权编号
	 */
	public int getTQNum(DBPaRs plaRs) throws Exception {
		int num = plaRs.getInt("tqnum");
		if(MyTools.checkSysTimeBeyondSqlDate(plaRs.getTime("tqduetime"))){
			num = 0;
		}
		return num;
	}
	
	//------------------静态区--------------------
	
	private static TqBAC instance = new TqBAC();

	public static TqBAC getInstance() {
		return instance;
	}
}
