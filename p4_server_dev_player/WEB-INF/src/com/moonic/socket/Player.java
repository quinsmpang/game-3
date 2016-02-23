package com.moonic.socket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.moonic.bac.MsgBAC;
import com.moonic.bac.PlayerBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.bac.UserBAC;
import com.moonic.battle.BattleBox;
import com.moonic.mgr.LockStor;
import com.moonic.servlet.STSServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.STSNetSender;

/**
 * 角色
 * @author John
 */
public class Player {
	public boolean isNew = true;
	public boolean isRun;
	public boolean isPush;//接收推送
	public byte connectmark;//第几次连接
	
	public Socket socket;
	public DataInputStream dis;
	public DataOutputStream dos;
	
	public BreakLineTT breaklineTT;//自动断线计时器
	
	public long overtime;//心跳过期时间
	public OvertimeTT overtimeTT;//过期计时器检测计时器
	
	public Vector<Short> breaklinepdindex = new Vector<Short>();//推送下标缓存
	public Vector<PushData> breaklinepd = new Vector<PushData>();//推送对象缓存
	
	public static int totalThreadAmount;//线程连接数
	
	public int tcpnum;//TCP连接编号
	
	public long lastmsgtime;//最后一次发言的时间
	
	public short pushmark = -1;//推送计数MARK
	
	public boolean conf_receive_game_push = true;//是否接收游戏推送
	
	public String tag;//对象标识
	
	public byte usepushindex = 0;//使用的推送线程下标
	
	//-----------------创建对象时初始化------------------
	
	public String sessionid;
	public int uid;
	public int pid;
	public String pname;
	public long createtime;
	public JSONArray openfunc;//已开启功能集合
	
	//-----------------连接成功后初始化------------------
	
	public String mac;//用户MAC
	public String imei;//用户IMEI
	public String platform;//用户渠道
	
	//-----------------接收请求时赋值------------------
	
	public String ip;
	public String channel;
	
	//-----------------游戏过程中赋值------------------
	
	public BattleBox verifybattle_battlebox;//验证战斗记录战斗属性箱
	
	/**
	 * 构造
	 */
	public Player(String sessionid, int uid, int pid, String pname, long createtime, JSONArray openfunc){
		StringBuffer sb = new StringBuffer();
		sb.append(pid);
		sb.append("=[");
		sb.append(MyTools.getTimeStr());
		sb.append("]=");
		sb.append(hashCode());
		tag = sb.toString();
		SocketServer.getInstance().plainfolist.add(tag);
		synchronized (SocketServer.pushindex_lock) {
			usepushindex = SocketServer.nextuseindex;
			if(SocketServer.nextuseindex < SocketServer.pushPq.length-1){
				SocketServer.nextuseindex++;
			} else {
				SocketServer.nextuseindex = 0;
			}		
		}
		this.sessionid = sessionid;
		this.uid = uid;
		this.pid = pid;
		this.pname = pname;
		this.createtime = createtime;
		this.openfunc = openfunc;
		//System.out.println("usepushindex:"+usepushindex);
	}
	
	/**
	 * 初始化连接成功后的数据
	 */
	public void initConnectSuccessData(){
		DBHelper dbHelper = new DBHelper();
		try {
			if(platform == null){
				ResultSet userRs = dbHelper.query(UserBAC.tab_user, "id,channel,username,wifi,devuser,onlinestate,serverid,playerid,sessionid,mac,imei,platform", "id="+uid);
				if(!userRs.next()){
					BACException.throwInstance("用户未找到");
				}
				initUserInfo(userRs);
			}
			//其他需要初始化的游戏数据加在下面
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 初始化用户信息
	 */
	public void initUserInfo(ResultSet userRs) throws Exception {
		mac = userRs.getString("mac");
		imei = userRs.getString("imei");
		platform = userRs.getString("platform");
	}
	
	/**
	 * 启动
	 */
	public void start() {
		if(!isRun){
			isRun = true;
			overtimeTT = new OvertimeTT();
			SocketServer.getInstance().timer.scheduleAtFixedRate(overtimeTT, MyTools.long_minu/2, MyTools.long_minu/2, TimeUnit.MILLISECONDS);
			//(new ReqHandler()).start();
			totalThreadAmount++;
			//Out.println("用户线程运行数达到"+totalThreadAmount);
			initConnectSuccessData();
		}
	}
	
	/**
	 * 停止
	 */
	public void stop(String tip){
		try {
			if(isRun){
				isRun = false;
				isPush = false;
				dis.close();
				dos.close();
				socket.close();
				overtimeTT.cancel();
				totalThreadAmount--;
				SocketServer.getInstance().oclog.d("[close] -- [" + SocketServer.getInstance().deciamalformat.format(tcpnum) + "]" + " -- 原因：" + tip);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 异常断线
	 */
	public void exceptionstop(String info){
		if(isRun){
			stop(info);
			startBreakLineTT(info);
		}
	}
	
	/**
	 * 启动断线计时器
	 */
	public void startBreakLineTT(String info){
		startBreakLineTT(MyTools.long_minu, info);
	}
	
	/**
	 * 启动断线计时器
	 */
	public void startBreakLineTT(long delay, String info){
		if(breaklineTT == null){
			breaklineTT = new BreakLineTT(info);
			SocketServer.getInstance().timer.schedule(breaklineTT, delay, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * 更新过期时间
	 */
	public void updateOverTime(){
		overtime = System.currentTimeMillis() + MyTools.long_minu*2;
		SocketServer.getInstance().log.d("收到 "+pname+" 心跳包，更新过期时间到"+MyTools.getTimeStr(overtime));
	}
	
	/**
	 * 开启推送
	 */
	public void openPush() throws Exception {
		if(!isRun){
			BACException.throwAndOutInstance("尚未建立Socket连接("+pid+")");
		}
		if(isPush){
			BACException.throwAndOutInstance("推送已打开("+pid+")");
		}
		SocketServer.getInstance().connectlog.d(pname+"("+pid+")开启推送");
		if(isNew){
			connectmark++;
			isPush = true;
			isNew = false;
			MsgBAC.getInstance().sendLoginSysMsg(pid);//发送登录问候语
		} else {
			/*if(breaklineTT != null){
				breaklineTT.cancel();
				breaklineTT = null;
			}*/
			synchronized (LockStor.getLock(LockStor.PUSH_LOCK, pid)) {
				connectmark++;
				isPush = true;
				for(int k = 0; k < breaklinepd.size(); k++){
					SocketServer.getInstance().push(this, breaklinepd.get(k), breaklinepdindex.get(k), false);
				}	
			}
			SocketServer.getInstance().pushlog.d(pname+"("+pid+")将推送缓存中的推送对象加入到推送序列中并置空容器("+breaklinepd.size()+")");
		}
	}
	
	/**
	 * 更新在线状态
	 * @param clearmark 从什么标签开始清理
	 */
	public void updateOnlineState(short clearmark){
		if(isRun){
			short startmark = 0;
			boolean clear = false;
			synchronized (LockStor.getLock(LockStor.PUSH_LOCK, pid)) {
				for(int i = 0; i < breaklinepdindex.size(); i++){
					if(breaklinepdindex.get(i)==clearmark){
						startmark = breaklinepdindex.get(0);
						for(int k = 0; k <= i; k++){
							breaklinepdindex.remove(0);
							breaklinepd.remove(0);
						}
						clear = true;
						break;
					}
				}	
			}
			if(clear){
				SocketServer.getInstance().pushlog.d("收到 "+pname+" 心跳包，清除("+startmark+"~"+clearmark+")的推送缓存 | "+breaklinepdindex.size());
			} else {
				SocketServer.getInstance().pushlog.d("收到 "+pname+" 心跳包，标识("+clearmark+")，不需清理 | "+breaklinepdindex.size());
			}
			updateOverTime();
		} else {
			SocketServer.getInstance().log.d("收到 "+pname+" 心跳包，连接已断开，更新过期时间失败");
		}
	}
	
	/**
	 * 回收对象(重写)
	 */
	protected void finalize() throws Throwable {
		SocketServer.getInstance().plainfolist.remove(tag);
		super.finalize();
	}
	
	// ---------------内部类---------------
	
	/**
	 * 请求处理
	 * @author John
	 */
	class ReqHandler extends Thread {
		public void run() {
			try {
				while (isRun) {
					short act = dis.readShort();
					if(act == SocketServer.ACT_ONLINE){
						byte mark = dis.readByte();
						updateOnlineState(mark);
					} else {
						String str = dis.readUTF();
						SocketServer.getInstance().log.d("SocketServer收到客户端数据：" + str);
						JSONObject jsonobj = new JSONObject(str);
						ReturnValue val = processingReq(jsonobj);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						DataOutputStream baos_dos = new DataOutputStream(baos);
						baos_dos.writeShort(act);
						baos_dos.writeByte(val.success ? 1 : 0);
						baos_dos.writeUTF(val.info);
						baos_dos.close();
						byte[] pushdata = baos.toByteArray();
						SocketServer.getInstance().exePush(dos, pushdata);
					}
				}
			} catch (EOFException e) {
				SocketServer.getInstance().log.d("断开客户端  " + pname + " 异常" + e.toString());
			} catch (SocketException e) {
				SocketServer.getInstance().log.d("断开客户端  " + pname + " 异常" + e.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				exceptionstop("处理请求时发生异常");
				SocketServer.getInstance().log.d(pname + " Socket异常断线，启动断线重连倒计时");
			}
		}
		public ReturnValue processingReq(JSONObject jsonobj) throws Exception{
			{
				return new ReturnValue(false, "无效请求");
			}
		}
	}
	
	/**
	 * 断线用户计时任务
	 */
	class BreakLineTT extends MyTimerTask {
		public String info;
		public BreakLineTT(String info){
			this.info = info+"("+MyTools.getTimeStr()+")";
			SocketServer.getInstance().log.d(pname+"("+pid+") Socket启动异常断线倒计时("+this.info+")");
		}
		public void run2() {
			try {
				PushData.getInstance().sendPlaToOne(SocketServer.ACT_SYS_TEST, "异常断线倒计时结束，注销用户登录("+info+")", pid);
				STSNetSender sender = new STSNetSender(STSServlet.M_USER_LOGOUT);
				sender.dos.writeInt(uid);
				sender.dos.writeUTF("异常断线倒计时结束，注销用户登录");
				ServerBAC.getInstance().sendReqToMain(sender);
				PlayerBAC.getInstance().logout(pid, "异常断线倒计时结束，注销用户登录");
				SocketServer.getInstance().log.d(pname+"("+pid+") Socket异常断线倒计时结束，注销用户登录("+info+")");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 过期时间检测任务
	 */
	class OvertimeTT extends MyTimerTask {
		public OvertimeTT(){
			updateOverTime();
		}
		public void run2() {
			if(System.currentTimeMillis() > overtime){
				exceptionstop("已到过期时间"+MyTools.getTimeStr(overtime));
				SocketServer.getInstance().log.d(pname + " Socket已到过期时间，启动断线重连倒计时");
			}
		}
	}
}