package com.moonic.bac;

import org.json.JSONArray;

import com.moonic.mirror.Mirror;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPsRs;

/**
 * 帮派申请
 * @author John
 */
public class FacApplyBAC extends Mirror {
	
	/**
	 * 构造
	 */
	public FacApplyBAC(){
		super("tab_faction_apply", "factionid", null);
	}
	
	/**
	 * 清除所有申请者
	 */
	public void clearAllApplyer(DBHelper dbHelper, int playerid, String pname, int factionid) throws Exception {
		DBPsRs applyRs = query(factionid, "factionid="+factionid);
		if(applyRs.count() > 0){
			int[] byids = new int[applyRs.count()];
			while(applyRs.next()){
				byids[applyRs.getRow()-1] = applyRs.getInt("playerid");
			}
			JSONArray pusharr = new JSONArray();
			pusharr.add(factionid);//帮派ID
			pusharr.add(playerid);//玩家ID
			pusharr.add(pname);//玩家名
			pusharr.add(1);//处理方式
			PushData.getInstance().sendPlaToSome(SocketServer.ACT_FACTION_PROCESS_APPLY, pusharr.toString(), byids);
			delete(dbHelper, factionid, "factionid="+factionid);
		}
	}
	
	/**
	 * 获取已申请人数
	 */
	public int getAmount(int factionid) throws Exception {
		DBPsRs rs = query(factionid, "factionid="+factionid);
		return rs.count();
	}
	
	/**
	 * 获取所有申请者信息
	 */
	public JSONArray getApplyerData(int factionid) throws Exception {
		DBPsRs applyRs = query(factionid, "factionid="+factionid);
		JSONArray applyarr = new JSONArray();
		while(applyRs.next()){
			applyarr.add(PlaFacApplyBAC.getInstance().getApplyData(applyRs));
		}
		return applyarr;
	}
	
	//--------------静态区--------------
	
	private static FacApplyBAC instance = new FacApplyBAC();

	/**
	 * 获取实例
	 */
	public static FacApplyBAC getInstance() {
		return instance;
	}
}
