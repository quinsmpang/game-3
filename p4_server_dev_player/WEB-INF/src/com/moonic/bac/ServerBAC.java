package com.moonic.bac;

import java.io.File;
import java.sql.ResultSet;
import java.util.concurrent.ScheduledExecutorService;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.moonic.socket.GamePushData;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.timertask.CBIssueLeaderAwardTT;
import com.moonic.timertask.CBNpcInvadeTT;
import com.moonic.timertask.CBOutPutFacMoneyTT;
import com.moonic.timertask.CBRecoverNPCTT;
import com.moonic.timertask.CBRefWorldLvTT;
import com.moonic.timertask.ClearDataTT;
import com.moonic.timertask.DBIdleAdjustTT;
import com.moonic.timertask.FacRankingTT;
import com.moonic.timertask.JJCAwardIssueTT;
import com.moonic.timertask.MineralsTT;
import com.moonic.timertask.ReplayClearTT;
import com.moonic.timertask.SummonDayTT;
import com.moonic.timertask.SummonWeekTT;
import com.moonic.timertask.TeamActivityTT;
import com.moonic.timertask.TowerSendAwardTT;
import com.moonic.timertask.WorldBossTT;
import com.moonic.util.BACException;
import com.moonic.util.ConfFile;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;
import com.moonic.util.NetResult;
import com.moonic.util.NetSender;
import com.moonic.util.ProcessQueue;

import conf.Conf;

/**
 * 服务器
 * @author John
 */
public class ServerBAC {
	public static final String tab_server = "tab_server";
	public static final String tab_channel_server = "tab_channel_server";
	
	public static final String tab_notice = "tab_notice";
	
	public static ProcessQueue sender_pq = new ProcessQueue();
	
	/**
	 * 向验证服务器发请求
	 */
	public NetResult sendReqToMain(NetSender sender){
		return sendReqToOne(sender, Conf.ms_url);
	}
	
	/**
	 * 向指定服务器发请求
	 */
	public NetResult sendReqToOne(NetSender sender, String url) {
		NetResult nr = null;
		try {
			String stsurl = url + "sts.do";
			nr = sender.send(stsurl);
		} catch(Exception e){
			e.printStackTrace();
			nr = new NetResult();
			nr.rv = new ReturnValue(false, e.toString());
		}
		return nr;
	}
	
	/**
	 * 发通知
	 */
	public ReturnValue sendInform(int playerid, String title, String content, String overtimeStr, String extend, boolean sqlAll, int[] byids){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			if(title == null){
				BACException.throwInstance("标题不可为空");
			}
			if(content == null){
				BACException.throwInstance("内容不可为空");
			}
			JSONArray jsonarr = new JSONArray();
			jsonarr.add(title);
			jsonarr.add(content);
			if(extend != null && !extend.equals("")){
				jsonarr.add(new JSONObject(extend));	
			}
			if(byids != null){
				PushData.getInstance().setOverTime(overtimeStr).sendPlaToSome(SocketServer.ACT_SYS_INFORM, jsonarr.toString(), byids);
			} else {
				if(playerid == 0){
					if(sqlAll){
						PushData.getInstance().setOverTime(overtimeStr).sendPlaToAllSql(dbHelper, SocketServer.ACT_SYS_INFORM, jsonarr.toString());
					} else {
						PushData.getInstance().setOverTime(overtimeStr).sendPlaToAllOL(SocketServer.ACT_SYS_INFORM, jsonarr.toString());
					}
				} else {
					PushData.getInstance().setOverTime(overtimeStr).sendPlaToOne(SocketServer.ACT_SYS_INFORM, jsonarr.toString(), playerid);
				}	
			}
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 发系统消息
	 */
	public ReturnValue sendSysMsg(String msg){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			if(msg == null){
				BACException.throwInstance("消息内容不可为空");
			}
			JSONArray arr = MsgBAC.getInstance().getSysMsgBag(msg);
			PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_MESSAGE_RECEIVE, arr.toString());
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 发游戏推送
	 */
	public ReturnValue sendGamePush(String param){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			if(param == null){
				BACException.throwInstance("消息内容不可为空");
			}
			String[] params = Tools.splitStr(param, ",");
			GamePushData gpd = GamePushData.getInstance(Integer.valueOf(params[0]));
			for(int i = 1; i < params.length; i++){
				gpd.add(params[i]);
			}
			gpd.sendToAllOL();
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 发顶部消息
	 */
	public ReturnValue sendTopMsg(String msg){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			if(msg == null){
				BACException.throwInstance("消息内容不可为空");
			}
			PushData.getInstance().setNopool(true).sendPlaToAllOL(SocketServer.ACT_MESSAGE_TOP, msg);
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 删除服务器数据
	 */
	public ReturnValue clearServerData(){
		deleteConfTxt();
		return new ReturnValue(true);
	}
	
	/**
	 * 删除TXT配置文件
	 */
	public ReturnValue deleteConfTxt(){
		try {
			String txtFolderPath = ServerConfig.getWebInfPath()+"txt_conf/";
			File txtConf = new File(txtFolderPath);
			if(txtConf.exists()){
				File[] files = txtConf.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}	
			}
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 开服准备
	 */
	public ReturnValue openServerReady(){
		try {
			int minIdle = ServerConfig.getDataBase().setMinIdleToMax();
			ConfFile.updateFileValue(DBIdleAdjustTT.MIN_IDLE, String.valueOf(minIdle));
			return new ReturnValue(true, "处理成功("+minIdle+")");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 连接数调整
	 */
	public ReturnValue adjustDBIdle(){
		try {
			int minIdle = ServerConfig.getDataBase().adjustMinIdle();
			ConfFile.updateFileValue(DBIdleAdjustTT.MIN_IDLE, String.valueOf(minIdle));
			return new ReturnValue(true, String.valueOf(minIdle));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 获取开服时间
	 */
	public long getOpenTime() throws Exception{
		DBPaRs serRs = DBPool.getInst().pQueryA(tab_server, "id="+Conf.sid);
		long time = serRs.getTime("opentime");
		return time;
	}
	
	/**
	 * 获取开服天数
	 */
	public int getOpenDays() throws Exception{
		long t1 = MyTools.getCurrentDateLong();
		long t2 = MyTools.getCurrentDateLong(getOpenTime());
		int opendays = (int)((t1-t2)/MyTools.long_day)+1;
		if(opendays < 1){
			opendays = 1;
		}
		return opendays;
	}
	
	/**
	 * 获取公告
	 */
	public JSONArray getNotice(DBHelper dbHelper, long lastlogintime) throws Exception {
		DBPaRs noticeRs = DBPool.getInst().pQueryA(tab_notice, "serverid="+Conf.sid);
		if(!noticeRs.exist()){
			return null;
		}
		if(noticeRs.getInt("loopshow")==0 && (lastlogintime>noticeRs.getTime("createtime") || System.currentTimeMillis()>noticeRs.getTime("createtime"))){
			return null;
		}
		JSONArray returnarr = new JSONArray();
		returnarr.add(noticeRs.getString("title"));
		returnarr.add(noticeRs.getString("content"));
		return returnarr;
	}
	
	/**
	 * 获取指定渠道的服务器列表
	 */
	public DBPsRs getChannelServerList(String channel) throws Exception {
		DBPsRs channelServerRs = DBPool.getInst().pQueryS(tab_channel_server, "channel='"+channel+"' and visible=1", "disporder desc");//先查渠道的自定义配置方式
		if(!channelServerRs.have()) {//不存在自定义设置，用默认设置001
			channelServerRs = DBPool.getInst().pQueryS(tab_channel_server, "channel='001' and visible=1", "disporder desc");
		}			
		return channelServerRs;
	}
	
	/**
	 * 获取指定渠道的服务器
	 */
	public DBPsRs getChannelServer(String channel, int vsid) throws Exception {
		//策略：查询渠道是否有自定义配置，有自定义使用自定义，没自定义使用全局设置
		DBPsRs channelServerRs = DBPool.getInst().pQueryS(tab_channel_server, "channel='"+channel+"' and visible=1 and vsid="+vsid, "disporder desc");//先查渠道的自定义配置方式
		if(!channelServerRs.have()) {//不存在自定义设置，用默认设置001
			channelServerRs = DBPool.getInst().pQueryS(tab_channel_server, "channel='001' and visible=1 and vsid="+vsid, "disporder desc");
		}			
		return channelServerRs;
	}
	
	/**
	 * 后台获取数据
	 */
	public JSONObject getServerData(){
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		try {
			dbHelper.openConnection();
			DBPaRs serverRs = DBPool.getInst().pQueryA(tab_server, "id="+Conf.sid);
			JSONObject serverobj = serverRs.getJsonobj();
			ResultSet amountRs = dbHelper.query("tab_player", "count(*) as amount", "serverid="+Conf.sid);
			amountRs.next();
			serverobj.put("pam", amountRs.getInt("amount"));
			serverobj.put("pamol", SocketServer.getInstance().plamap.size());
			serverobj.put("maxplayer", Conf.max_player);
			return serverobj;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取服务器运行状态数据
	 */
	public ReturnValue getRunState(){
		try {
			JSONArray statearr = new JSONArray();
			statearr.add(ServerConfig.getDataBase().getNumActive());//活动连接数
			statearr.add(ServerConfig.getDataBase().getNumIdle());//待机连接数
			statearr.add(ServerConfig.getDataBase().getMaxThe());//最大连接数
			statearr.add(Runtime.getRuntime().freeMemory()/1000/1000);//空闲内存
			statearr.add(Runtime.getRuntime().totalMemory()/1000/1000);//总内存
			statearr.add(Runtime.getRuntime().maxMemory()/1000/1000);//最大内存
			statearr.add(Thread.activeCount());//总线程数
			return new ReturnValue(true, statearr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	//--------------内部类--------------
	
	
	//--------------静态区--------------
	
	public static ScheduledExecutorService timer;
	
	/**
	 * 初始化计时器
	 */
	public static void initTimer(){
		timer = MyTools.createTimer(3);
		FacRankingTT.init();
		ClearDataTT.init();
		DBIdleAdjustTT.init();
		JJCAwardIssueTT.init();
		SummonDayTT.init();
		SummonWeekTT.init();
		CBOutPutFacMoneyTT.init();
		CBRecoverNPCTT.init();
		CBIssueLeaderAwardTT.init();
		CBNpcInvadeTT.init();
		WorldBossTT.init();
		TowerSendAwardTT.init();
		CBRefWorldLvTT.init();
		TeamActivityTT.init();
		MineralsTT.init();
		ReplayClearTT.init();
	}
	
	private static ServerBAC instance = new ServerBAC();
	
	/**
	 * 获取实例
	 */
	public static ServerBAC getInstance(){
		return instance;
	}
}
