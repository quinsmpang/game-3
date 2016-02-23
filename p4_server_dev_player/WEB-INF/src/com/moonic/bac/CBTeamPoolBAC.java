package com.moonic.bac;

import com.moonic.mirror.Mirror;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPsRs;

/**
 * 国战队伍池
 * @author John
 */
public class CBTeamPoolBAC extends Mirror {
	
	/**
	 * 构造
	 */
	public CBTeamPoolBAC() {
		super("tab_cb_teampool", "factionid", null);
	}
	
	/**
	 * 解散帮派清除队伍池
	 */
	public void clearTeam(DBHelper dbHelper, int factionid) throws Exception {
		DBPsRs cityStorRs = query(factionid, "factionid="+factionid);
		if(!cityStorRs.have()){
			return ;
		}
		delete(dbHelper, factionid, "factionid="+factionid);
	}
	
	/**
	 * 退出帮派清除队伍池
	 */
	public void clearTeam(DBHelper dbHelper, int playerid, int factionid) throws Exception {
		DBPsRs cityStorRs = query(factionid, "factionid="+factionid+" and playerid="+playerid);
		if(!cityStorRs.have()){
			return ;
		}
		delete(dbHelper, factionid, "factionid="+factionid+" and playerid="+playerid);
	}
	
	//-----------------静态区--------------------
	
	private static CBTeamPoolBAC instance = new CBTeamPoolBAC();

	public static CBTeamPoolBAC getInstance() {
		return instance;
	}
}
