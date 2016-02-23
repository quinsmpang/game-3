package com.moonic.bac;

import org.json.JSONArray;

import server.common.Tools;

import com.moonic.mirror.Mirror;
import com.moonic.util.DBPsRs;

/**
 * 帮派成员
 * @author John
 */
public class FacMemBAC extends Mirror {
	
	/**
	 * 构造
	 */
	public FacMemBAC() {
		super("tab_pla_faction", "factionid", null);
	}
	
	/**
	 * 获取帮众ID数组
	 * @param excludepid 要排除的玩家ID
	 */
	public int[] getFacMemIDs(int factionid, int... excludepid) throws Exception {
		int[] byids = null;
		if(factionid > 0){
			DBPsRs plaRs = query(factionid, "factionid="+factionid);
			while(plaRs.next()){
				int playerid = plaRs.getInt("playerid");
				if(!Tools.intArrContain(excludepid, playerid)){
					byids = Tools.addToIntArr(byids, playerid);
				}
			}
		}
		return byids;
	}
	
	/**
	 * 获取帮派管理员ID数组(不排除自己，excludeid to 0)
	 */
	public int[] getFacMgrIDs(int factionid, int excludepid) throws Exception {
		int[] byids = null;
		if(factionid > 0){
			DBPsRs plaRs = query(factionid, "factionid="+factionid+" and position>0 and playerid!="+excludepid);
			while(plaRs.next()){
				byids = Tools.addToIntArr(byids, plaRs.getInt("playerid"));
			}
		}
		return byids;
	}
	
	/**
	 * 获取帮派成员数量
	 */
	public int getAmount(int factionid) throws Exception {
		DBPsRs facmemRs = query(factionid, "factionid="+factionid);
		return facmemRs.count();
	}
	
	/**
	 * 获取帮派族员信息
	 */
	public JSONArray getFacMemData(int factionid) throws Exception {
		DBPsRs facmemRs = query(factionid, "factionid="+factionid);
		JSONArray facmemarr = new JSONArray();
		while(facmemRs.next()){
			facmemarr.add(PlaFacBAC.getInstance().getMemData(facmemRs));
		}
		return facmemarr;
	}
	
	//--------------静态区--------------
	
	private static FacMemBAC instance = new FacMemBAC();

	/**
	 * 获取实例
	 */
	public static FacMemBAC getInstance() {
		return instance;
	}
}
