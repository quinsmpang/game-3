package com.moonic.bac;

import java.sql.ResultSet;
import java.util.concurrent.ScheduledExecutorService;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.moonic.timertask.DBIdleAdjustTT;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MassNetSender;
import com.moonic.util.MyTools;
import com.moonic.util.NetResult;
import com.moonic.util.NetSender;
import com.moonic.util.ProcessQueue;

import conf.Conf;

/**
 * 游戏服务器BAC
 * @author John
 */
public class ServerBAC {
	public static final String tab_server = "tab_server";
	public static final String tab_channel_server = "tab_channel_server";
	public static final String tab_notice = "tab_notice";
	
	public static ProcessQueue sender_pq = new ProcessQueue();
	
	String goodColor = "[339f2f]"; //顺畅
	String busyColor = "[c53f2a]"; //繁忙
	String fireyColor = "[ff0000]"; //火爆
	String restColor = "[d4210b]"; //维护
	
	/**
	 * 获取服务器列表
	 */
	public ReturnValue getServerList(int userid, String channel) {
		DBHelper dbHelper = new DBHelper();
		try {
			JSONObject popjson = PlayerBAC.getInstance().getPop();
			JSONArray listarr = new JSONArray();
			DBPsRs channelServerRs = getChannelServerList(channel);//虚拟服务器列表
			JSONObject userJson = dbHelper.queryJsonObj(UserBAC.tab_user, "devuser", "id="+userid);
			while(channelServerRs.next()){
				if(channelServerRs.getInt("istest")==1 && userJson.optInt("devuser")!=1){
					continue;
				}
				int vsid = channelServerRs.getInt("vsid");
				int serverid = channelServerRs.getInt("serverid");
				DBPaRs serverRs = DBPool.getInst().pQueryA(tab_server, "id="+serverid);//查物理服务器数据
				String servername = channelServerRs.getString("servername");
				int state = channelServerRs.getInt("state")!=-1?channelServerRs.getInt("state"):serverRs.getInt("state");
				long opentime = MyTools.getTimeLong(!channelServerRs.getString("opentime").equals("-1")?channelServerRs.getString("opentime"):serverRs.getString("opentime"));
				int tip = channelServerRs.getInt("tip")!=-1?channelServerRs.getInt("tip"):serverRs.getInt("tip");
				String note = !channelServerRs.getString("note").equals("-1")?channelServerRs.getString("note"):serverRs.getString("note");
				String httpurl = serverRs.getString("http");
				String[] socketdata = Tools.splitStr(serverRs.getString("tcp"), ":");
				int onlineamount = popjson.optInt(String.valueOf(serverid));
				int reslv = serverRs.getInt("reslv");
				if(!MyTools.checkSysTimeBeyondSqlDate(opentime)) {
					state = 1;
					note = "[218ab6]"+MyTools.formatTime(opentime, "M月d日 HH:mm")+"开放[-]";
				}
				if(userJson.optInt("devuser")==1) {
					state = 0;
				}
				if(state == 0){
					if(tip==2 || onlineamount>=200){
						note = fireyColor+"火爆[-]";
					} else 
					if(onlineamount>=100){
						note = busyColor+"繁忙[-]";
					} else 
					{
						note = goodColor+"顺畅[-]";
					}
				} else {
					if(note==null || note.equals("")) {
						note = restColor+"维护中[-]";
					}
				}
				JSONArray arr = new JSONArray();
				arr.add(serverid);//服务器ID
				arr.add(servername);//服务器名
				arr.add(state);//服务器状态
				arr.add(tip);//标签
				arr.add(note);//顺畅
				arr.add(httpurl);//HTTP地址
				arr.add(socketdata[0]);//SOCKET地址
				arr.add(socketdata[1]);//SOCKET端口
				arr.add(Conf.res_url);//资源下载地址
				arr.add(vsid);//虚拟服务器ID
				arr.add(reslv);//资源等级
				arr.add(Math.max(0, (opentime-System.currentTimeMillis())/1000));//剩余开服时间，单位：S
				listarr.add(arr);
			}
			dbHelper.openConnection();
			JSONArray usedarr = PlayerBAC.getInstance().getUsedServer(dbHelper, userid);
			JSONArray jsonarr = new JSONArray();
			jsonarr.add(listarr);//服务器列表
			jsonarr.add(usedarr);//玩过的服务器列表
			return new ReturnValue(true, jsonarr.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
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
	 * 向所有服务器发请求
	 */
	public ReturnValue sendReqToAll(NetSender sender){
		try {
			String info = converNrsToString(sendReq(null, sender));
			return new ReturnValue(true, info);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 向指定服务器发请求
	 */
	public NetResult sendReqToOne(NetSender sender, int serverid) {
		return sendReqToOne(sender, serverid, "sts.do");
	}
	
	/**
	 * 向指定服务器发请求
	 */
	public NetResult sendReqToOne(NetSender sender, int serverid, String doStr) {
		NetResult nr = null;
		try {
			nr = sendReq("id="+serverid, sender, doStr)[0];
		} catch(Exception e){
			e.printStackTrace();
			nr = new NetResult();
			nr.rv = new ReturnValue(false, e.toString());
		}
		return nr;
	}
	
	/**
	 * 发送请求
	 */
	public NetResult[] sendReq(String where, NetSender sender) throws Exception {
		return sendReq(where, sender, "sts.do");
	}
	
	/**
	 * 发送请求
	 */
	public NetResult[] sendReq(String where, NetSender sender, String doStr) throws Exception {
		String tabname = tab_server;
		if(where != null){
			where = "usestate=1 and ("+where+")";
		} else {
			where = "usestate=1";
		}
		DBPsRs sRs = DBPool.getInst().pQueryS(tabname, where);
		if(!sRs.have()){
			BACException.throwInstance("指定目标服务器不存在 where:"+where);
		}
		MassNetSender mns = new MassNetSender();
		while(sRs.next()){
			mns.addURL((byte)1, sRs.getInt("id"), sRs.getString("name"), "http://"+sRs.getString("http") + doStr);
		}
		NetResult[] nrs = mns.send(sender);
		return nrs;
	}
	
	/**
	 * 请求结果转化为字符串
	 */
	private String converNrsToString(NetResult[] nrs){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < nrs.length; i++){
			sb.append("服务器：");
			sb.append(nrs[i].name);
			sb.append("\t");
			sb.append("结果：");
			sb.append(nrs[i].rv.success);
			sb.append(", ");
			sb.append(nrs[i].rv.info);
			sb.append("\r\n\r\n");
		}
		return sb.toString();
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
	
	/**
	 * WEB获取指定渠道的所有服务器
	 */
	public ReturnValue webGetServerList(String channel){
		try {
			DBPsRs channelServerRs = getChannelServerList(channel);
			JSONArray returnarr = new JSONArray();
			while(channelServerRs.next()){
				if(channelServerRs.getInt("istest")==0){
					JSONObject obj = new JSONObject();
					obj.put("id", channelServerRs.getInt("vsid"));
					obj.put("name", channelServerRs.getString("servername"));
					returnarr.add(obj);
				}
			}
			return new ReturnValue(true, returnarr.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * WEB获取指定用户下角色的服务器
	 */
	public ReturnValue webGetUserServerList(String platform, String username){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			JSONArray returnarr = new JSONArray();
			ResultSet userRs = dbHelper.query("tab_user", "id,channel", "username='"+username+"' and platform='"+platform+"'");
			if(userRs.next()){
				JSONArray usedarr = PlayerBAC.getInstance().getUsedServer(dbHelper, userRs.getInt("id"));
				DBPsRs channelServerRs = getChannelServerList(userRs.getString("channel"));
				while(channelServerRs.next()){
					if(channelServerRs.getInt("istest")==0){
						if(usedarr.contains(channelServerRs.getInt("vsid"))){
							JSONObject obj = new JSONObject();
							obj.put("id", channelServerRs.getInt("vsid"));
							obj.put("name", channelServerRs.getString("servername"));
							returnarr.add(obj);
						}
					}
				}	
			}
			return new ReturnValue(true, returnarr.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	public static ScheduledExecutorService timer;
	
	/**
	 * 初始化计时器
	 */
	public static void initTimer(int serverid){
		timer = MyTools.createTimer(3);
		DBIdleAdjustTT.init();
	}
	
	private static ServerBAC instance = new ServerBAC();
	
	/**
	 * 获取实例
	 */
	public static ServerBAC getInstance(){
		return instance;
	}
}
