package com.moonic.socket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.bac.ConfigBAC;
import com.moonic.bac.PlayerBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.bac.UserBAC;
import com.moonic.mgr.LockStor;
import com.moonic.servlet.STSServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.MyLog;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;
import com.moonic.util.ProcessQueue;
import com.moonic.util.ProcessQueueTask;
import com.moonic.util.STSNetSender;

import conf.Conf;
import conf.LogTbName;

/**
 * 服务端
 * @author John
 */
public class SocketServer {
	/**
	 * 连接服务器
	 */
	public static final short ACT_CONNECT_SERVER = 1;
	/**
	 * 心跳包
	 */
	public static final short ACT_ONLINE = -1;
	
	/**
	 * 调试信息
	 */
	public static final short ACT_SYS_TEST = 101;
	/**
	 * 被迫下线
	 */
	public static final short ACT_SYS_BEOFFLINE = 102;
	/**
	 * 被迫退出游戏
	 */
	public static final short ACT_SYS_BEEXIT = 103;
	/**
	 * 系统通知
	 */
	public static final short ACT_SYS_INFORM = 104;
	/**
	 * 收信息
	 */
	public static final short ACT_MESSAGE_RECEIVE = 151;
	/**
	 * 游戏推送
	 */
	public static final short ACT_MESSAGE_GAMEPUSH = 152;
	/**
	 * 顶部消息
	 */
	public static final short ACT_MESSAGE_TOP = 153;
	/**
	 * 玩家上线
	 */
	public static final short ACT_PLAYER_ONLINE = 201;
	/**
	 * 玩家下线
	 */
	public static final short ACT_PLAYER_OFFLINE = 202;
	/**
	 * 通知游戏光标闪烁
	 */
	public static final short ACT_ICON_FLASH = 203;
	/**
	 * 有充值
	 */
	public static final short ACT_PLAYER_RECHARGE = 204;
	/**
	 * 有买特权
	 */
	public static final short ACT_PLAYER_BUY_TQ = 205;
	/**
	 * 有改变值
	 */
	public static final short ACT_PLAYER_CHANGEVALUE = 206;
	/**
	 * 有角色升级
	 */
	public static final short ACT_PLAYER_LVUP = 207;
	/**
	 * 有角色改名
	 */
	public static final short ACT_PLAYER_RENAME = 208;
	/**
	 * 收邮件
	 */
	public static final short ACT_MAIL_RECEIVER = 251;
	/**
	 * 有加入帮派
	 */
	public static final short ACT_FACTION_JOIN = 301;
	/**
	 * 有更新入帮条件
	 */
	public static final short ACT_FACTION_JOINCOND = 302;
	/**
	 * 有更新帮会公告
	 */
	public static final short ACT_FACTION_UPD_INFO = 303;
	/**
	 * 有调整职位
	 */
	public static final short ACT_FACTION_ADJUSET_POSITION = 304;
	/**
	 * 有踢出帮众
	 */
	public static final short ACT_FACTION_REMOVE_MEMBER = 305;
	/**
	 * 有退出帮派
	 */
	public static final short ACT_FACTION_EXIT = 306;
	/**
	 * 帮主禅让
	 */
	public static final short ACT_FACTION_SHANRANG = 307;
	/**
	 * 被踢出帮派
	 */
	public static final short ACT_FACTION_BEREMOVE = 308;
	/**
	 * 弹劾
	 */
	public static final short ACT_FACTION_IMPEACH = 309;
	/**
	 * 有帮派资金变化
	 */
	public static final short ACT_FACTION_CHANGEMONEY = 310;
	/**
	 * 重置帮派数据
	 */
	public static final short ACT_FACTION_RESETDATA = 311;
	/**
	 * 帮派事记
	 */
	public static final short ACT_FACTION_LOG = 312;
	/**
	 * 有成员改名
	 */
	public static final short ACT_FACTION_RENAME = 313;
	/**
	 * 帮派升级
	 */
	public static final short ACT_FACTION_UPLEVEL = 314;
	/**
	 * 被膜拜
	 */
	public static final short ACT_FACTION_WORSHIP = 315;
	/**
	 * 有申请入帮
	 */
	public static final short ACT_FACTION_APPLY = 316;
	/**
	 * 有撤销申请
	 */
	public static final short ACT_FACTION_REVOCATION_APPLY = 317;
	/**
	 * 有处理申请
	 */
	public static final short ACT_FACTION_PROCESS_APPLY = 318;
	/**
	 * 有同意申请
	 */
	public static final short ACT_FACTION_AGREE_APPLY = 319;
	/**
	 * 有拒绝申请
	 */
	public static final short ACT_FACTION_REFUSE_APPLY = 320;
	/**
	 * 有帮派科技升级
	 */
	public static final short ACT_FACTION_UP_TECHNOLOGY = 321;
	/**
	 * 竞技场战斗
	 */
	public static final short ACT_JJC_RANKING_BATTLE = 451;
	/**
	 * 神秘召唤日物品刷新
	 */
	public static final short ACT_SUMMON_DAY_ITEM = 501;
	/**
	 * 神秘召唤周物品刷新
	 */
	public static final short ACT_SUMMON_WEEK_ITEM = 502;
	/**
	 * 帮派副本-进入副本
	 */
	public static final short ACT_FACCOPYMAP_INTO = 551;
	/**
	 * 帮派副本-战斗结束
	 */
	public static final short ACT_FACCOPYMAP_END = 552;
	/**
	 * 帮派副本-重置地图
	 */
	public static final short ACT_FACCOPYMAP_RESETMAP = 553;
	/**
	 * 帮派副本-退出
	 */
	public static final short ACT_FACCOPYMAP_EXIT = 554;
	/**
	 * 国战-有宣战
	 */
	public static final short ACT_CB_DECLAREWAR = 601;
	/**
	 * 国战-有太守变更
	 */
	public static final short ACT_CB_CHANGELEADER = 602;
	/**
	 * 国战-城战结束
	 */
	public static final short ACT_CB_BATTLE_END = 603;
	/**
	 * 国战-战斗结果
	 */
	public static final short ACT_CB_BATTLE_RESULT = 604;
	/**
	 * 国战-派出队伍
	 */
	public static final short ACT_CB_DISPATCH = 605;
	/**
	 * 国战-下一出场信息
	 */
	public static final short ACT_CB_NEXT_BATTLE = 606;
	/**
	 * 国战-帮派产出奖励
	 */
	public static final short ACT_CB_FACTION_AWARD = 607;
	/**
	 * 加好友
	 */
	public static final short ACT_FRIEND_ADD = 651;
	/**
	 * 删好友
	 */
	public static final short ACT_FRIEND_DELETE = 652;
	/**
	 * 好友赠送
	 */
	public static final short ACT_FRIEND_PRESENT = 653;
	/**
	 * 世界BOSS开始
	 */
	public static final short ACT_WORLD_BOSS_START = 701;
	/**
	 * 组队活动-开始
	 */
	public static final short ACT_TEAM_ACTI_START = 751;
	/**
	 * 组队活动-加入队伍
	 */
	public static final short ACT_TEAM_ACTI_JOIN = 752;
	/**
	 * 组队活动-踢出队伍
	 */
	public static final short ACT_TEAM_ACTI_KICK = 753;
	/**
	 * 组队活动-布阵
	 */
	public static final short ACT_TEAM_ACTI_FORMAT = 754;
	/**
	 * 组队活动-准备
	 */
	public static final short ACT_TEAM_ACTI_BEREADY = 755;
	/**
	 * 组队活动-取消准备
	 */
	public static final short ACT_TEAM_ACTI_CANCELREADY = 756;
	/**
	 * 组队活动-战斗
	 */
	public static final short ACT_TEAM_ACTI_BATTLE = 757;
	/**
	 * 组队活动-关闭房间
	 */
	public static final short ACT_TEAM_ACTI_CLOSE = 758;
	/**
	 * 组队活动-退出房间
	 */
	public static final short ACT_TEAM_ACTI_EXIT = 759;
	/**
	 * 挖矿-有占矿
	 */
	public static final short ACT_MINERALS_CLOCKIN = 801;
	/**
	 * 挖矿-有抢矿
	 */
	public static final short ACT_MINERALS_CONDENT = 802;
	/**
	 * 挖矿-开始活动
	 */
	public static final short ACT_MINERALS_START = 803;
	/**
	 * 挖矿-结束活动
	 */
	public static final short ACT_MINERALS_END = 804;
	
	//public static final int MAX_USER = 1000;//最大承载
	
	//public static final int TCP_PORT = Conf.socket_port;//连接端口
	
	private boolean isRun;
	private ServerSocket serversocket;
	
	public Hashtable<Integer, Player> plamap = new Hashtable<Integer, Player>(8192);//(KEY=PID)
	public Hashtable<String, Player> session_plamap = new Hashtable<String, Player>(8192);//(KEY=SESSIONID)
	
	public ArrayList<String> plainfolist = new ArrayList<String>();
	
	private LinkedList<PushData> pushQueue = new LinkedList<PushData>();
	
	public MyLog pushlog;
	public MyLog log;
	public MyLog connectlog;
	public MyLog oclog;
	
	/**
	 * 构造
	 */
	public SocketServer(){
		pushlog = new MyLog(MyLog.NAME_DATE, "log_socket_push", "SOCKET_PUSH", Conf.debug, false, true, null);
		log = new MyLog(MyLog.NAME_DATE, "log_socket", "Socket", Conf.debug, false, true, null);
		connectlog = new MyLog(MyLog.NAME_DATE, "log_socket_connect", "SOCKET_CONNECT", Conf.debug, false, true, null);
		oclog = new MyLog(MyLog.NAME_DATE, "log_socket_oc", "SOCKET_OC", Conf.debug, false, true, null);
	}
	
	/**
	 * 启动服务器
	 */
	public ReturnValue start() {
		if(!isRun) {
			try {
				isRun = true;
				timer = MyTools.createTimer(3);
				(new ConnectListener()).start();
				(new Pusher()).start();
				TCPAmount=0;
				return new ReturnValue(true,"对"+Conf.socket_port+"端口的TCP监听服务启动成功。");
			} catch(Exception ex) {
				ex.printStackTrace();
				return new ReturnValue(false,"对"+Conf.socket_port+"端口的TCP监听服务启动失败。");
			}
		} else {
			return new ReturnValue(false,"对"+Conf.socket_port+"端口的TCP监听服务还在运行中，请先停止。");
		}
	}
	
	/**
	 * 停止服务器
	 */
	public ReturnValue stop() {
		if(isRun) {
			isRun = false;
			try {
				Player[] plaarr = plamap.values().toArray(new Player[plamap.size()]);
				for(int i = 0; i < plaarr.length; i++){
					plaarr[i].stop("服务器停止");
				}
				MyTools.cancelTimer(timer);
				serversocket.close();
				return new ReturnValue(true,"对"+Conf.socket_port+"端口的TCP监听服务停止成功。");
			} catch (Exception e) {
				e.printStackTrace();
				return new ReturnValue(true,"对"+Conf.socket_port+"端口的TCP监听服务停止失败"+e.toString());
			}
		} else {
			return new ReturnValue(false,"对"+Conf.socket_port+"端口的TCP监听服务没有启动，请先启动。");
		}
	}
	
	/**
	 * 获取推送队列数据
	 */
	public String getPushQueueData(){
		PushData[] pdarr = pushQueue.toArray(new PushData[pushQueue.size()]);
		StringBuffer sb = new StringBuffer();
		sb.append("待推送队列：\r\n\r\n");
		for(int i = 0; pdarr!=null && i < pdarr.length; i++){
			sb.append(pdarr[i].toString()+"\r\n");
		}
		return sb.toString();
	}
	
	/**
	 * 清除推送队列
	 */
	public String clearPushQueue(){
		pushQueue.clear();
		return "清理成功";
	}
	
	/**
	 * 检查指定角色是否在线
	 */
	public boolean checkOnline(int playerid){
		return plamap.get(playerid) != null;
	}
	
	/**
	 * 获取运行状态
	 */
	public ReturnValue getRunState(){
		StringBuffer sb = new StringBuffer();
		if(isRun){
			sb.append("\r\n");
			sb.append("正常运行\r\n");
			sb.append("当前连接数(AM/MAP)：" + Player.totalThreadAmount + "/" + plamap.size()+"\r\n");
			sb.append("待推送长度：" + pushQueue.size()+"\r\n");
			sb.append("操作记录：\r\n" );
			/*for(int i = 0; i < pushop.size(); i++){
				sb.append(pushop.get(i)+"\r\n");
			}
			sb.append("等待记录：\r\n");
			for(int i = 0; i < pushwait.size(); i++){
				sb.append(pushwait.get(i)+"\r\n");
			}
			sb.append("推送历史：\r\n");
			for(int i = 0; i < pushhistory.size(); i++){
				sb.append(pushhistory.get(i)+"\r\n");
			}*/
		} else {
			sb.append("已停止");
		}
		return new ReturnValue(isRun, sb.toString());
	}
	
	/**
	 * 断开所有角色
	 */
	public ReturnValue clearAllPla(String info, int type){
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("\r\n断开列表：\r\n");
			Player[] plaarr = plamap.values().toArray(new Player[plamap.size()]);
			for(int i = 0; i < plaarr.length; i++){
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream baos_dos = new DataOutputStream(baos);
					baos_dos.writeShort(type==0?SocketServer.ACT_SYS_BEEXIT:SocketServer.ACT_SYS_BEOFFLINE);
					baos_dos.writeShort(-100);//表示忽略MARK匹配
					baos_dos.write(info.getBytes("UTF-8"));
					baos_dos.close();
					byte[] pushdata = baos.toByteArray();
					exePush(plaarr[i].dos, pushdata);	
				} catch (Exception e) {}
				STSNetSender sender = new STSNetSender(STSServlet.M_USER_LOGOUT);
				sender.dos.writeInt(plaarr[i].uid);
				sender.dos.writeUTF("断开所有角色");
				ServerBAC.getInstance().sendReqToMain(sender);
				PlayerBAC.getInstance().logout(plaarr[i].pid, "断开所有角色");
				Thread.sleep(10);
				sb.append("用户ID："+plaarr[i].uid+"-"+plaarr[i].pname+"("+plaarr[i].pid+")\r\n");
			}
			MyTools.cancelTimer(timer);
			timer = MyTools.createTimer(3);
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 断开指定角色
	 */
	public ReturnValue breakOnePla(int playerid, String info){
		try {
			Player pla = plamap.get(playerid);
			if(pla == null){
				BACException.throwInstance("角色已离线");
			}
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream baos_dos = new DataOutputStream(baos);
				baos_dos.writeShort(SocketServer.ACT_SYS_BEEXIT);
				baos_dos.writeShort(-100);//表示忽略MARK匹配
				baos_dos.write(info.getBytes("UTF-8"));
				baos_dos.close();
				byte[] pushdata = baos.toByteArray();
				exePush(pla.dos, pushdata);
			} catch (Exception e) {}
			STSNetSender sender = new STSNetSender(STSServlet.M_USER_LOGOUT);
			sender.dos.writeInt(pla.uid);
			sender.dos.writeUTF("断开指定角色");
			ServerBAC.getInstance().sendReqToMain(sender);
			PlayerBAC.getInstance().logout(pla.pid, "断开指定角色");
			String str = "用户ID："+pla.uid+"-"+pla.pname+"("+pla.pid+")\r\n";
			return new ReturnValue(true, str);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 获取在线角色数据
	 */
	public Player[] getPlaArr(){
		return plamap.values().toArray(new Player[plamap.size()]);
	}
	
	/**
	 * 向指定角色推送消息
	 */
	public void push(int pid, PushData pd){
		Player pla = plamap.get(pid);
		if(pla != null){
			push(pla, pd);
		}
	}
	
	/**
	 * 向指定角色推送消息
	 */
	public void push(Player pla, PushData pd){
		if(pla!=null){
			push(pla, pd, pla.pushmark, true);
		}
	}
	
	/**
	 * 向指定角色推送消息
	 */
	public void push(Player pla, PushData pd, short pushmark, boolean isnew){
		synchronized (LockStor.getLock(LockStor.PUSH_LOCK, pla.pid)) {
			try {
				if(pla.pushmark==-1 && !pd.isSysMsg){
					return;
				}
				//System.out.println("pla.conf_receive_game_log:"+pla.conf_receive_game_log+" pd.allowIgnore:"+pd.allowIgnore);
				if(!pla.conf_receive_game_push && pd.allowIgnore){
					return;
				}
				if(pd.nopool){
					pushmark = -100;
				} else {
					if(isnew){
						pla.breaklinepdindex.add(pla.pushmark);
						pla.breaklinepd.add(pd);
						pushlog.d(pla.pname+"("+pla.pid+","+pla.isPush+")将推送消息加入推送缓存");
						if(pla.pushmark < Short.MAX_VALUE){
							pla.pushmark++;
						} else {
							pla.pushmark = 0;
						}
						pushlog.d(pla.pname+"("+pla.pid+")自增后标签值:"+pla.pushmark);		
					}	
				}
				if(pla.isPush){
					pushPq[pla.usepushindex].addTask(new PushTask(pla, pd, pushmark, pla.connectmark));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ProcessQueue[] pushPq = new ProcessQueue[10];
	public static byte nextuseindex;
	public static byte[] pushindex_lock = new byte[0];
	static {
		for(int i = 0; i < pushPq.length; i++){
			pushPq[i] = new ProcessQueue();
		}
	}
	
	/**
	 * 推送任务
	 * @author John
	 */
	class PushTask implements ProcessQueueTask {
		private Player pla;
		private PushData pd;
		private short pushmark;
		private byte connectmark;
		public PushTask(Player pla, PushData pd, short pushmark, byte connectmark){
			this.pla = pla;
			this.pd = pd;
			this.pushmark = pushmark;
			this.connectmark = connectmark;
		}
		public void execute() {
			try {
				if(pla.isPush && pla.connectmark==connectmark){
					DiscardPushTT discardPushTt = null;
					try {
						discardPushTt = new DiscardPushTT(pla, pd);
						timer.schedule(discardPushTt, 1000, TimeUnit.MILLISECONDS);
						pushlog.d("向 " + pla.pname+"("+pla.pid+") 推送 " + pd.act + "," + pushmark + "," + pd.info);
						byte[] infoBytes = pd.info.getBytes("UTF-8");
						//addToPushOp(PS_PUSH1);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						DataOutputStream baos_dos = new DataOutputStream(baos);
						baos_dos.writeShort(pd.act);
						baos_dos.writeShort(pushmark);
						baos_dos.write(infoBytes);
						baos_dos.close();
						byte[] pushdata = baos.toByteArray();
						exePush(pla.dos, pushdata);
					} catch (SocketException e) {
						pushlog.e("推送异常("+pla.pname+")："+e.toString());
						pla.exceptionstop("推送SocketException异常");
					} catch (Exception e) {
						e.printStackTrace();
						pla.exceptionstop("推送"+e.toString()+"异常");
					} finally {
						discardPushTt.cancel();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 执行推送
	 */
	public void exePush(DataOutputStream dos, byte[] pushdata) throws Exception {
		synchronized (dos) {
			dos.writeInt(pushdata.length);
			dos.write(pushdata);
			dos.flush();
		}
	}
	
	/**
	 * 丢推送计时器
	 */
	class DiscardPushTT extends MyTimerTask {
		private Player pla;
		//private PushData pd;
		public DiscardPushTT(Player pla, PushData pd){
			this.pla = pla;
			//this.pd = pd;
		}
		public void run2() {
			try {
				//Out.println("推送超时 "+pla.pname+"("+pla.pid+")"+pd.toString());
				pla.exceptionstop("推送超时");
				//Out.println("异常强制中断 "+pla.pname+"("+pla.pid+") 连接失败");
				//pla.dos.close();
				//pla.dis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 创建推送日志
	 */
	public void createPushLog(PushData pd) throws Exception {
		String content = pd.info;
		if(content!=null && content.length()>500){
			content = content.substring(0, 499);
		}
		SqlString sqlStr = new SqlString();
		sqlStr.add("act", pd.act);
		sqlStr.add("target", pd.target);
		sqlStr.add("content", content);
		sqlStr.add("offlinesave", 0);
		sqlStr.addDateTime("overtime", pd.overtimeStr);
		sqlStr.add("exception", pd.excepInfo);
		sqlStr.add("timemark", pd.timemark);
		sqlStr.addDateTime("createtime", Tools.getCurrentDateTimeStr());
		DBHelper.logInsert(LogTbName.TAB_PUSH_LOG(), sqlStr);
	}
	
	public static byte[] QUEUE_LOCK = new byte[0];
	/**
	 * 增加推送数据
	 */
	public void addPush(PushData pushdata, int from){
		synchronized(QUEUE_LOCK){
			if(pushdata != null){
				pushQueue.offer(pushdata);		
			} else {
				Out.println("有要加入的空推送对象，来自：" + from);
			}		
		}
	}
	
	/**
	 * 获取在线人数
	 */
	public int getOnlinePlayerAmount() {
		if(session_plamap==null) {
			return 0;
		} else {			
			return session_plamap.size();
		}
	}
	
	public static byte[] REMOVE_PLA = new byte[0];
	/**
	 * 移除角色
	 */
	public void removePla(int pid, String reason){
		synchronized (REMOVE_PLA) {
			Player pla = plamap.get(pid);
			if(pla != null){
				pla.stop("移除玩家("+reason+")");
				if(pla.breaklineTT != null){
					pla.breaklineTT.cancel();
					pla.breaklineTT = null;
					log.d("移除玩家用户"+pla.pname+"("+pla.pid+")，停止断线重连计时器");
				}
				pushlog.d(pla.pname+"("+pla.pid+")将推送缓存中的推送消息存储到数据库("+pla.breaklinepd.size()+")");
				plamap.remove(pid);
				int old_amount = session_plamap.size();
				session_plamap.remove(pla.sessionid);
				UserBAC.session_usermap.remove(pla.sessionid);
				//System.out.println("移除USER："+pla.sessionid);
				connectlog.d("移除角色：" + pla.pname + "("+ pla.pid + "," + pla.sessionid + ")" + " 人数：" + old_amount + " -> " + session_plamap.size());
			}
		}
	}
	
	public ScheduledExecutorService timer;
	
	//-------------内部类---------------
	public static int TCPAmount=0; //建立TCP连接计数
	
	public DecimalFormat deciamalformat = new DecimalFormat("000000");
	
	/**
	 * 连接监听者
	 * @author John
	 */
	class ConnectListener extends Thread {
		public void run() {
			Player.totalThreadAmount=0; //角色线程数复位
			try {				
				Out.println("准备启动TCP服务,监听"+Conf.socket_port+"端口");
				serversocket = new ServerSocket(Conf.socket_port,1000);
				Out.println("启动TCP服务完成,开始监听"+Conf.socket_port+"端口");
				connectlog.d("启动TCP服务完成,开始监听"+Conf.socket_port+"端口");
				while (isRun) {
					Socket socket = null;
					try {
						socket = serversocket.accept();
						ConnectionThread ct = new ConnectionThread(socket);
						ct.start();
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SocketServer.this.stop();
				TCPAmount=0;
				Player.totalThreadAmount=0;
				connectlog.d("端口"+Conf.socket_port+"的TCP服务已关闭");
				connectlog.save();
				Out.println(Tools.getCurrentDateTimeStr()+"--"+"端口"+Conf.socket_port+"的TCP服务已关闭");
			}
		}
	}
	
	/**
	 * 连接线程
	 */
	class ConnectionThread extends Thread {
		private Socket socket;
		public ConnectionThread(Socket socket){
			this.socket = socket;
		}
		public void run() {
			DataInputStream dis = null;
			DataOutputStream dos = null;
			String sessionid = null;
			ReturnValue val = null;
			SqlString reqSqlStr = null;
			long reqtime = 0;
			try {
				if(socket == null){
					BACException.throwInstance("连接推送服务器失败");
				}
				TCPAmount++;
				oclog.d("[open] -- [" + deciamalformat.format(TCPAmount) + "]");
				connectlog.d("成功建立来自"+socket.getRemoteSocketAddress()+"的第"+TCPAmount+"个TCP连接");
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				connectlog.d("获取输入输出流完成");
				connectlog.d("启动SESSION_ID读取计时器");
				SessionidTT sessionidTT = new SessionidTT(dis);
				timer.schedule(sessionidTT, 10000, TimeUnit.MILLISECONDS);
				long t1 = System.currentTimeMillis();
				sessionid = dis.readUTF();
				sessionidTT.cancel();
				long t2 = System.currentTimeMillis();
				connectlog.d("从dis读取SESSION_ID：" + sessionid + "用时[" + (t2-t1) + "]ms 取消读取计时器");
				reqtime = System.currentTimeMillis();
				reqSqlStr = new SqlString();
				reqSqlStr.addDateTime("reqtime", Tools.getCurrentDateTimeStr());
				reqSqlStr.add("reqdata", sessionid);
				Player pla = null;
				/*if(session_plamap.size() >= Conf.max_player) {
					val = new ReturnValue(false, "服务器人数已满,请稍后再试");
					LogBAC.logout("tcplogin", "达到最大限制人数="+Conf.max_player+",当前在线玩家数="+session_plamap.size());
					connectlog.d("TCP连接人数已满");
				} else*/ 
				if(sessionid == null || sessionid.equals("")) {
					val = new ReturnValue(false, "无效数据");
					connectlog.d("SESSIONID无效，连接取消");
				} else {
					pla = session_plamap.get(sessionid);
					if(pla != null){
						if(pla.isRun) {
							try {
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								DataOutputStream baos_dos = new DataOutputStream(baos);
								baos_dos.writeShort(SocketServer.ACT_SYS_BEOFFLINE);
								baos_dos.writeShort(-100);//表示忽略MARK匹配
								baos_dos.write("你的帐号在其他地方上线，你将被迫下线。".getBytes("UTF-8"));
								baos_dos.close();
								byte[] pushdata = baos.toByteArray();
								exePush(pla.dos, pushdata);	
							} catch (Exception e) {}
							pla.stop("玩家再次连接，断开已有连接");
							try {
								Thread.sleep(200);	//延时等待该线程真正结束
							} catch(Exception ex) {
								ex.printStackTrace();
							}
							connectlog.d("结束用户上一连接");
						}
						pla.socket = socket;
						pla.dis = dis;
						pla.dos = dos;
						pla.tcpnum = TCPAmount;
						pla.start();
						JSONArray successarr = new JSONArray();
						successarr.add(pla.pushmark);
						if(pla.isNew){
							successarr.add("连接服务器成功");
							connectlog.d("连接客户端  " + pla.pname + "成功");
						} else {
							successarr.add("断线重连服务器成功");
							connectlog.d("断线重连客户端  " + pla.pname + "，停止断线重连计时器");
						}
						val = new ReturnValue(true, successarr.toString());
						reqSqlStr.add("userid", pla.pid);
					} else {
						val = new ReturnValue(false, "尚未登录角色");
					}
				}
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream baos_dos = new DataOutputStream(baos);
				baos_dos.writeShort(ACT_CONNECT_SERVER);
				baos_dos.writeByte(val.success ? 1 : 0);
				baos_dos.writeUTF(val.info);
				baos_dos.close();
				byte[] pushdata = baos.toByteArray();
				exePush(dos, pushdata);
				connectlog.d("返回连接结果：" + val.success + "," + val.info);
				if(val.success){
					if(pla.breaklineTT != null){
						pla.breaklineTT.cancel();
						pla.breaklineTT = null;
					}
				} else {
					socket.close();
					connectlog.d("连接失败，关闭SOCKET");
					oclog.d("[close] -- [" + deciamalformat.format(TCPAmount) + "]" + " -- 原因：" + val.info);
				}
			} catch (Exception e) {
				connectlog.d("连接出现异常：" + e.toString() + " 终止连接" + sessionid + ",Socket被关闭 ");
				//System.out.println("Socket连接出现异常");
				//e.printStackTrace();
				val = new ReturnValue(false, e.toString());
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream baos_dos = new DataOutputStream(baos);
					baos_dos.writeShort(ACT_CONNECT_SERVER);
					baos_dos.writeByte(0);
					baos_dos.writeUTF(e.toString());
					baos_dos.close();
					byte[] pushdata = baos.toByteArray();
					exePush(dos, pushdata);
					connectlog.d("连接异常，返回" + e.toString());
				} catch(Exception ex){
					//ex.printStackTrace();
					connectlog.d("连接异常后的返回异常，返回" + ex.getMessage());
				} finally {
					if(socket != null){
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						connectlog.d("连接异常，关闭SOCKET");
						oclog.d("[close] -- [" + deciamalformat.format(TCPAmount) + "]" + " -- 原因：" + e.toString());
					}
				}
			} finally {
				try {
					if(reqSqlStr != null){
						reqSqlStr.addDateTime("resptime", Tools.getCurrentDateTimeStr());
						reqSqlStr.add("respresult", val.success ? 0 : 1);
						reqSqlStr.add("respdata", val.info);
						reqSqlStr.add("usedtime", System.currentTimeMillis()-reqtime);
						DBHelper.logInsert(LogTbName.TAB_SOCKET_LOG(), reqSqlStr);
						connectlog.d("连接过程结束，创建连接日志");
					}
				} catch (Exception e){
					e.printStackTrace();
					connectlog.d("创建连接日志过程异常：" + e.toString());
				}
				connectlog.d("---------------------------------");
			}
		}
	}
	
	/**
	 * SESSION_ID读取计时器
	 */
	class SessionidTT extends MyTimerTask{
		public DataInputStream dis;
		public SessionidTT(DataInputStream dis){
			this.dis = dis;
		}
		public void run2() {
			try {
				connectlog.d("SESSION_ID计时器时间到，关闭读入流");
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//public ArrayList<String> pushop = new ArrayList<String>();
	//public ArrayList<String> pushwait = new ArrayList<String>();
	//public ArrayList<PushData> pushhistory = new ArrayList<PushData>();
	
	/**
	 * 加入操作记录
	 */
	/*public void addToPushOp(byte i){
		pushop.add(Tools.getCurrentDateTimeStr()+" "+pushstateStr[i]);
		if(pushop.size() > 10){
			pushop.remove(0);
		}
	}*/
	
	/**
	 * 加入等待记录
	 */
	/*public void addToPushWait(){
		pushwait.add(Tools.getCurrentDateTimeStr());
		if(pushwait.size() > 10){
			pushwait.remove(0);
		}
	}*/
	
	/**
	 * 加入历史推送记录
	 */
	/*public void addTopushHistory(PushData pd){
		pd.pushtime = Tools.getCurrentDateTimeStr();
		pushhistory.add(pd);
		if(pushhistory.size() > 10){
			pushhistory.remove(0);
		}
	}*/
	
	//public static final byte PS_TOPUSH = 0;
	//public static final byte PS_PUSH1 = 1;
	//public static final byte PS_PUSH2 = 2;
	//public static final byte PS_PUSH3 = 3;
	//public static final byte PS_FINSIH = 4;
	//public static final byte PS_LOG = 5;
	//public static final byte PS_REMOVE = 6;
	//public static final byte PS_STOP = 7;
	
	
	//public static String[] pushstateStr = {"准备推送", "推送1", "推送2", "推送3", "完成推送", "创建日志", "从队列移除推送对象", "已停止"};
	
	/**
	 * 推送
	 * @author John
	 */
	class Pusher extends Thread {
		public void run() {
			while(isRun){
				long t1 = System.currentTimeMillis();
				while(pushQueue.size() == 0){
					if(!isRun){
						//addToPushOp(PS_STOP);
						return;
					}
					//addToPushWait();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					t1 = System.currentTimeMillis();
				}
				//addToPushOp(PS_TOPUSH);
				PushData pd = pushQueue.get(0);
				try {
					if(pd.target == PushData.TARGET_PLA_SOME){
						for(int i = 0; pd.byids != null && i < pd.byids.length; i++){
							push(pd.byids[i], pd);
						}
					} else 
					if(pd.target == PushData.TARGET_PLA_NOS){
						Player[] plaarr = plamap.values().toArray(new Player[plamap.size()]);
						for(int i = 0; i < plaarr.length; i++){
							if(plaarr[i].pid != pd.myid){
								push(plaarr[i], pd);
							}
						}
					} else 
					if(pd.target == PushData.TARGET_PLA_ALL){
						Player[] plaarr = plamap.values().toArray(new Player[plamap.size()]);
						for(int i = 0; i < plaarr.length; i++){
							if(!(pd.beforecreatetime!=0 && plaarr[i].createtime>pd.beforecreatetime)){
								push(plaarr[i], pd);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					pd.excepInfo = e.toString();
				}
				//addToPushOp(PS_FINSIH);
				long t2 = System.currentTimeMillis();
				if(ConfigBAC.getBoolean("push_log") && t2-t1>ConfigBAC.getInt("logout_push_threshold")){
					try {
						createPushLog(pd);
						Out.println("总推送时间超过"+ConfigBAC.getInt("logout_push_threshold")+"ms："+pd.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//addToPushOp(PS_LOG);
				synchronized(QUEUE_LOCK){
					pushQueue.poll();
				}
				//addToPushOp(PS_REMOVE);
				//addTopushHistory(pd);
			}
		}
	}
	
	//-------------静态区---------------
	
	private static SocketServer ss = new SocketServer();
	
	/**
	 * 获取实例对象
	 */
	public static SocketServer getInstance(){
		return ss;
	}
}
