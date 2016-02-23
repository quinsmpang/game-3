package com.moonic.socket;

import org.json.JSONArray;
import org.json.JSONObject;

import com.moonic.bac.MsgBAC;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 游戏推送
 * @author John
 */
public class GamePushData extends PushData {
	private int channel;
	private JSONObject pushobj;
	private JSONArray msgarr;
	
	/**
	 * 构造
	 */
	public GamePushData(int num) throws Exception {
		DBPaRs msgRs = DBPool.getInst().pQueryA(MsgBAC.tab_game_push_msg, "num="+num);
		if(!msgRs.exist()){
			System.out.println("游戏推送消息编号不存在 num="+num);
			return;
		}
		pushobj = new JSONObject();
		msgarr = new JSONArray();
		channel = msgRs.getInt("showchannel");
		allowIgnore = msgRs.getInt("allowignore")==1;
		nopool = true;
		add(num);
		pushobj.put("channel", channel);
		pushobj.put("content", msgarr);
	}

	/**
	 * 加入元素
	 */
	public GamePushData add(Object obj){
		msgarr.add(obj);
		return this;
	}
	
	/**
	 * 设置角色信息
	 */
	public GamePushData setPlaInfo(DBPaRs plaRs) throws Exception {
		int pid = plaRs.getInt("id");
		String pname = plaRs.getString("name");
		return setPlaInfo(pid, pname);
	}
	
	/**
	 * 设置角色信息
	 */
	public GamePushData setPlaInfo(int id, String name) {
		pushobj.put("pid", id);
		pushobj.put("pname", name);
		return this;
	}
	
	/**
	 * 发送
	 */
	public void sendToAllOL() {
		if(channel == 0){
			return;
		}
		sendPlaToAllOL(SocketServer.ACT_MESSAGE_GAMEPUSH, pushobj.toString());
	}
	
	/**
	 * 发送
	 */
	public void sendToAllFac(int factionid) throws Exception {
		if(channel == 0){
			return;
		}
		sendPlaToFacMem(SocketServer.ACT_MESSAGE_GAMEPUSH, pushobj.toString(), factionid);
	}
	
	/**
	 * 发送
	 */
	public void sendToOne(int playerid){
		if(channel == 0){
			return;
		}
		sendPlaToOne(SocketServer.ACT_MESSAGE_GAMEPUSH, pushobj.toString(), playerid);
	}
	
	//--------------静态区---------------
	
	/**
	 * 获取实例
	 */
	public static GamePushData getInstance(int num) throws Exception {
		return new GamePushData(num);
	}
}
