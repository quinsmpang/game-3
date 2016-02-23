package com.moonic.bac;

import com.moonic.mirror.Mirror;
import com.moonic.util.DBPsRs;

/**
 * 粉丝
 * @author John
 */
public class FansBAC extends Mirror {
	
	/**
	 * 构造
	 */
	public FansBAC(){
		super("tab_friend", "friendid", null);
	}
	
	/**
	 * 获取粉丝ID数组
	 */
	public int[] getFansIds(int playerid, byte type) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("friendid="+playerid);
		if(type != FriendBAC.TYPE_ALL){
			sb.append(" and type="+type);
		}
		int[] byids = null;
		DBPsRs fansRs = query(playerid, sb.toString());
		if(fansRs.count() > 0){
			byids = new int[fansRs.count()];
			while(fansRs.next()){
				byids[fansRs.getRow()-1] = fansRs.getInt("playerid");
			}
		}
		return byids;
	}
	
	/**
	 * 检查指定玩家对我的关注类型
	 */
	public byte getFansType(int playerid, int targetid) throws Exception {
		DBPsRs fansRs = query(playerid, "friendid="+playerid+" and playerid="+targetid);
		byte type = FriendBAC.TYPE_NONE;
		if(fansRs.next()){
			type = fansRs.getByte("type");
		}
		return type;
	}
	
	//--------------静态区--------------
	
	private static FansBAC instance = new FansBAC();
	
	/**
	 * 获取实例
	 */
	public static FansBAC getInstance(){
		return instance;
	}
}
