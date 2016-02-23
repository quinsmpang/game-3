package com.moonic.socket;

import java.sql.ResultSet;

import server.common.Tools;

import com.moonic.bac.FacMemBAC;
import com.moonic.bac.FansBAC;
import com.moonic.util.DBHelper;

import conf.Conf;


/**
 * 推送数据
 * @author John
 */
public class PushData {
	/**
	 * 指定玩家
	 */
	public static final byte TARGET_PLA_SOME = 0;
	/**
	 * 除自己外的所有玩家
	 */
	public static final byte TARGET_PLA_NOS = 1;
	/**
	 * 所有玩家
	 */
	public static final byte TARGET_PLA_ALL = 2;
	
	/**
	 * 发送目标类型
	 */
	public byte target;
	
	/**
	 * 动作
	 */
	public short act;
	/**
	 * 附加信息
	 */
	public String info;
	
	/**
	 * TARGET=TARGET_USER_SOME 目标数组 || TARGET=TARGET_SOME 目标数组
	 */
	public int[] byids;
	/**
	 * TARGET=TARGET_NOSELF_All 玩家编号
	 */
	public int myid;
	
	/**
	 * 过期时间
	 */
	public String overtimeStr;
	/**
	 * 异常信息
	 */
	public String excepInfo;
	/**
	 * 推送时间
	 */
	public String pushtime;
	/**
	 * 创建时间
	 */
	public long timemark;
	/**
	 * 在指定创建角色时间之前才发送
	 */
	public long beforecreatetime;
	
	/**
	 * 允许忽略推送
	 */
	public boolean allowIgnore;
	/**
	 * 免缓存
	 */
	public boolean nopool;
	/**
	 * 发送给同场景时的场景编号
	 */
	public int scenenum;
	/**
	 * 发送给同场景人数限制
	 */
	public short maxsend;
	/**
	 * 发送给同场景时的限制范围
	 */
	public int[] range;//X,Z,W,H
	/**
	 * 发送给同场景时排除的玩家ID
	 */
	public int[] excludepid;
	/**
	 * 是否为系统问候语
	 */
	public boolean isSysMsg;
	
	/**
	 * 构造
	 */
	public PushData(){
		timemark = System.currentTimeMillis();
	}
	
	/**
	 * 设置过期时间
	 */
	public PushData setOverTime(String overtimeStr){
		this.overtimeStr = overtimeStr;
		return this;
	}
	
	/**
	 * 设置是否允许忽略
	 */
	public PushData setAllowIgnore(boolean allowIgnore) {
		this.allowIgnore = allowIgnore;
		return this;
	}
	
	/**
	 * 设置时间(当前仅发送给所有在线玩家有效)
	 */
	public PushData setBeforecreatetime(long beforecreatetime) {
		this.beforecreatetime = beforecreatetime;
		return this;
	}
	
	/**
	 * 设置免缓存
	 */
	public PushData setNopool(boolean nopool) {
		this.nopool = nopool;
		return this;
	}
	
	/**
	 * 设置为问候语
	 */
	public PushData setSysMsg(){
		this.isSysMsg = true;
		return this;
	}
	
	/**
	 * 重写
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(pushtime != null){
			sb.append(pushtime+" - ");
		}
		sb.append("动作：");
		sb.append(act+",");
		sb.append("目标：");
		sb.append(target+",");
		sb.append("信息：");
		sb.append(info);
		return sb.toString();
	}
	
	//--------------发送区---------------
	
	/**
	 * 发送给单个玩家
	 */
	public void sendPlaToOne(short act, String info, int byid){
		sendPlaToSome(act, info, new int[]{byid});
	}
	
	/**
	 * 发送给玩家的FANS
	 */
	public void sendPlaToFans(short act, String info, int playerid, byte type) throws Exception {
		int[] byids = FansBAC.getInstance().getFansIds(playerid, type);
		sendPlaToSome(act, info, byids);
	}
	
	/**
	 * 发送给指定玩家
	 */
	public void sendPlaToSome(short act, String info, int[] byids){
		if(byids == null){
			return;
		}
		this.target = TARGET_PLA_SOME;
		this.act = act;
		this.info = info;
		this.byids = byids;
		SocketServer.getInstance().addPush(this, 1);
	}
	
	/**
	 * 发送给除自己的所有玩家
	 */
	public void sendPlaToNosOL(short act, String info, int myid){
		this.target = TARGET_PLA_NOS;
		this.act = act;
		this.info = info;
		this.myid = myid;
		SocketServer.getInstance().addPush(this, 2);
	}
	
	/**
	 * 发送给所有在线玩家
	 */
	public void sendPlaToAllOL(short act, String info){
		this.target = TARGET_PLA_ALL;
		this.act = act;
		this.info = info;
		SocketServer.getInstance().addPush(this, 3);
	}
	
	/**
	 * 发送给所有人，包括不在线玩家
	 */
	public void sendPlaToAllSql(DBHelper dbHelper, short act, String info) throws Exception {
		ResultSet plaRs = dbHelper.query("tab_player", "id", "serverid="+Conf.sid);
		byids = new int[dbHelper.getRsDataCount(plaRs)];
		while(plaRs.next()){
			byids[plaRs.getRow()-1] = plaRs.getInt("id");
		}
		this.target = TARGET_PLA_SOME;
		this.act = act;
		this.info = info;
		SocketServer.getInstance().addPush(this, 4);
	}
	
	/**
	 * 发给所有帮众
	 */
	public void sendPlaToFacMem(short act, String info, int factionid, int... excludeid) throws Exception {
		int[] byids = FacMemBAC.getInstance().getFacMemIDs(factionid, excludeid);
		PushData.getInstance().sendPlaToSome(act, info, byids);
	}
	
	/**
	 * 发给所有帮派管理
	 */
	public void sendPlaToFacMgr(short act, String info, int factionid, int excludeid) throws Exception {
		int[] byids = FacMemBAC.getInstance().getFacMgrIDs(factionid, excludeid);
		PushData.getInstance().sendPlaToSome(act, info, byids);
	}
	
	/**
	 * 发送给FANS和帮众
	 */
	public void sendPlaToFansAndNosFac(short act, String info, int factionid, int myid, byte type) throws Exception {
		int[] byids1 = FansBAC.getInstance().getFansIds(myid, type);
		int[] byids2 = FacMemBAC.getInstance().getFacMemIDs(factionid, myid);
		for(int i = 0; byids2!=null && i<byids2.length; i++){
			if(!Tools.intArrContain(byids1, byids2[i])){
				byids1 = Tools.addToIntArr(byids1, byids2[i]);
			}
		}
		PushData.getInstance().sendPlaToSome(act, info, byids1);
	}
	
	//--------------静态区---------------
	
	/**
	 * 获取实例
	 */
	public static PushData getInstance(){
		return new PushData();
	}
}
