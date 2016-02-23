package com.moonic.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.bac.BkSysMailBAC;
import com.moonic.bac.CBBAC;
import com.moonic.bac.ChargeBAC;
import com.moonic.bac.ChargeSendBAC;
import com.moonic.bac.ConfigBAC;
import com.moonic.bac.FileMgrBAC;
import com.moonic.bac.MailBAC;
import com.moonic.bac.PartnerBAC;
import com.moonic.bac.PlaAssectBAC;
import com.moonic.bac.PlaJJCRankingBAC;
import com.moonic.bac.PlaMineralsBAC;
import com.moonic.bac.PlaTeamBAC;
import com.moonic.bac.PlatformGiftCodeBAC;
import com.moonic.bac.PlayerBAC;
import com.moonic.bac.RankingBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.bac.SystemUpdateBAC;
import com.moonic.bac.TxtFileBAC;
import com.moonic.bac.WorldBossBAC;
import com.moonic.battle.BattleManager;
import com.moonic.mirror.MirrorMgr;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketInfoMgr;
import com.moonic.socket.SocketServer;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPool;
import com.moonic.util.MyLog;

import conf.Conf;
import conf.LogTbName;

public class STSServlet extends HttpServlet {
	private static final long serialVersionUID = 4598035092703154800L;
	
	//-----------------资源服-----------------
	
	/**
	 * 存文件
	 */
	public static final short R_SAVE_FILE = 101;
		
	//-----------------用户服-----------------
	
	/**
	 * 注销用户
	 */
	public static final short M_USER_LOGOUT = 101;
	/**
	 * 查询支付订单状态
	 */
	public static final short M_QUERY_ORDER = 103;
	/**
	 * 获取充值订单号
	 */
	public static final short M_GET_ORDERNO = 104;
	/**
	 * 封测活动兑换推送
	 */
	public static final short M_CBT_SEND_EXCHANGE = 105;
	/**
	 * 推广员接收邀请
	 */
	public static final short M_EXTEN_1 = 106;
	/**
	 * 推广员成长礼包
	 */
	public static final short M_EXTEN_2 = 107;
	
	//-----------------游戏服-----------------
	
	/**
	 * 发送通知
	 */
	public static final short G_SEND_INFORM = 101;
	/**
	 * 发系统消息
	 */
	public static final short G_SEND_SYSMSG = 102;
	/**
	 * 发游戏推送
	 */
	public static final short G_SEND_GAMEPUSH = 104;
	/**
	 * 发顶部推送消息
	 */
	public static final short G_SEND_TOPMSG = 105;
	/**
	 * 向单个玩家发通知
	 */
	public static final short G_SEND_INFORM_TOONE = 106;
	/**
	 * 断开所用角色
	 */
	public static final short G_CLEAR_ALLPLAYER = 151;
	/**
	 * 获取推送队列消息
	 */
	public static final short G_GET_PUSHDATA = 152;
	/**
	 * 启动SOCKET服务器
	 */
	public static final short G_START_SOCKET = 153;
	/**
	 * 停止SOCKET服务器
	 */
	public static final short G_STOP_SOCKET = 154;
	/**
	 * 获取SOCKET运行状态
	 */
	public static final short G_SOCKET_GETSTATE = 155;
	/**
	 * 清除推送队列
	 */
	public static final short G_CLEAR_PUSHDATA = 156;
	/**
	 * 断开指定角色
	 */
	public static final short G_BREAK_ONEPLAYER = 157;
	/**
	 * 获取SOCKET运行信息
	 */
	public static final short G_SOCKET_RUN_INFO = 161;
	/**
	 * 设置待机连接数到最大
	 */
	public static final short G_SERVER_OPENREADY = 162;
	/**
	 * 调整待机连接数
	 */
	public static final short G_DB_ADJUST_IDLE = 163;
	/**
	 * 获取数据库信息
	 */
	public static final short G_GET_DBINFO = 251;
	/**
	 * 获取列表缓存清单
	 */
	public static final short G_TESTA = 252;
	/**
	 * 获取文本缓存清单
	 */
	public static final short G_TESTB = 253;
	/**
	 * 查看列表缓存
	 */
	public static final short G_GET_LISTPOOL = 254;
	/**
	 * 清除列表缓存
	 */
	public static final short G_CLEAR_TABPOOL = 255;
	/**
	 * 查看文本缓存
	 */
	public static final short G_GET_TXTPOOL = 256;
	/**
	 * 清除文本缓存
	 */
	public static final short G_CLEAR_TXTPOOL = 257;
	/**
	 * 清除服务器数据
	 */
	public static final short G_CLEAR_SERVER_DATA = 258;
	/**
	 * 通知被挤下线
	 */
	public static final short G_PLAYER_BEOFFLINE = 353;
	/**
	 * 通知被挤下线
	 */
	public static final short G_PLAYER_LOGOUT = 354;
	/**
	 * 充值
	 */
	public static final short G_PLAYER_RECHARGE = 355;
	/**
	 * 买特权
	 */
	public static final short G_PLAYER_BUY_TQ = 357;
	/**
	 * 发送更新包
	 */
	public static final short G_SERVER_UPDATE = 358;
	/**
	 * 订单批量补发货
	 */
	public static final short G_ORDER_BATCH_GIVE = 359;
	/**
	 * 有改变值
	 */
	public static final short G_PLAYER_CHANGEVALUE = 364;
	/**
	 * 通知游戏光标闪烁
	 */
	public static final short G_ICON_FLASH = 365;
	/**
	 * 发系统邮件
	 */
	public static final short G_BK_SEND_SYS_MAIL = 366;
	/**
	 * 发全服系统邮件
	 */
	public static final short G_BK_SEND_SERVER_SYS_MAIL = 367;
	/**
	 * 角色封号
	 */
	public static final short G_PLAYER_BLANK = 368;
	/**
	 * 角色解封
	 */
	public static final short G_PLAYER_UNBLANK = 369;
	/**
	 * 角色禁言
	 */
	public static final short G_PLAYER_BANNED_MSG = 370;
	/**
	 * 角色解禁
	 */
	public static final short G_PLAYER_UNBANNED_MSG = 371;
	/**
	 * 恢复玩家财产
	 */
	public static final short G_PLAYER_ASSECT_RECOVER = 372;
	/**
	 * WEB领取礼包
	 */
	public static final short G_WEB_GET_PLATFORMGIFT = 373;
	/**
	 * 发系统邮件
	 */
	public static final short G_SEND_SYS_MAIL2 = 379;
	/**
	 * 获取文件内容
	 */
	public static final short G_TXT_FILE_GET_CONTENT = 402;
	/**
	 * 获取插入日志线程状态
	 */
	public static final short G_INSERTLOG_GET_STATE = 403;
	/**
	 * 重置插入日志失败次数
	 */
	public static final short G_RESET_INSERTLOG_TIMEOUTAM = 404;
	/**
	 * 检查文件
	 */
	public static final short G_FILE_CHECK = 407;
	/**
	 * 获取排名
	 */
	public static final short G_WEB_TARGET_GETRANKING = 408;
	/**
	 * 获取角色数据
	 */
	public static final short G_BK_GET_PLAYER_DATA = 409;
	/**
	 * 获取服务器运行状态
	 */
	public static final short G_GET_SERVER_RUN_STATE = 410;
	/**
	 * 获取表镜像
	 */
	public static final short G_MIRROR_GET_TAB = 411;
	/**
	 * 获取角色镜像
	 */
	public static final short G_MIRROR_GET_PLA = 412;
	/**
	 * 获取角色表镜像
	 */
	public static final short G_MIRROR_GET_PLA_TAB = 413;
	/**
	 * 清镜像
	 */
	public static final short G_MIRROR_CLEAR_TAB = 414;
	/**
	 * 刷新游戏排行榜
	 */
	public static final short G_REFRESH_GAME_RANKING = 464;
	/**
	 * 获取指定玩家所有伙伴的战斗数据
	 */
	public static final short G_PARTNER_GETSPRITEBOX = 465;
	/**
	 * 创建竞技场假人
	 */
	public static final short G_JJC_CREATE_PC = 466;
	/**
	 * 国战-NPC入侵
	 */
	public static final short G_CB_NPCINVADE = 467;
	/**
	 * 世界BOSS-启动
	 */
	public static final short G_WB_START = 468;
	/**
	 * 获取PVP战斗信息
	 */
	public static final short G_PVP_BATTLE_INFO = 469;
	/**
	 * 组队活动-启动
	 */
	public static final short G_TEAM_ACTI_START = 470;
	/**
	 * 更新世界等级
	 */
	public static final short G_UPDATE_WORLDLEVEL = 471;
	/**
	 * 发放竞技场奖励
	 */
	public static final short G_ISSUE_JJCRANKING_AWARD = 472;
	/**
	 * 启动挖矿活动
	 */
	public static final short G_MINERALS_START = 473;
	/**
	 * 停止挖矿活动
	 */
	public static final short G_MINERALS_END = 474;
	/**
	 * 获取坑位信息
	 */
	public static final short G_MINERALS_GETPOSDATA = 475;
	
	/**
	 * service
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long t1= System.currentTimeMillis();
		InputStream is = request.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[4096];
		int readLen = -1;
		while ((readLen = is.read(buff)) != -1) {
			baos.write(buff, 0, readLen);
		}
		buff = baos.toByteArray();
		long t2= System.currentTimeMillis();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buff));
		DataOutputStream dos = new DataOutputStream(response.getOutputStream());
		try {
			ReturnValue val = null;
			SqlString reqSqlStr = new SqlString();
			reqSqlStr.addDateTime("reqtime", Tools.getCurrentDateTimeStr());
			reqSqlStr.add("reqflow", buff.length);
			if (buff.length == 0) {
				val = new ReturnValue(false, "无效请求");
			} 
			/*else if(DBHelper.connectionAmount >= 100){
				val = new ReturnValue(false, "服务器繁忙");
			}*/
			else {
				try {
					val = processingReq(request, response, dis, dos, reqSqlStr);		
				} catch (EOFException e) {
					DataInputStream edis = new DataInputStream(new ByteArrayInputStream(buff));
					int act = edis.readShort();
					System.out.println(e.toString()+"(act="+act+")");
					e.printStackTrace();
					val = new ReturnValue(false, e.toString());
				} catch (Exception e) {
					e.printStackTrace();
					val = new ReturnValue(false, e.toString());
				}
			}
			dis.close();
			byte[] responseData = null;
			if(val.getDataType()==ReturnValue.TYPE_STR) {
				responseData = Tools.strNull(val.info).getBytes("UTF-8");
			} else 
			if(val.getDataType()==ReturnValue.TYPE_BINARY) {
				responseData = val.binaryData;
			}
			long t3= System.currentTimeMillis();
			dos.writeByte(val.success ? 1 : 0);
			dos.write(responseData);
			long t4= System.currentTimeMillis();
			reqSqlStr.addDateTime("resptime", Tools.getCurrentDateTimeStr());
			reqSqlStr.add("respflow", responseData.length);
			reqSqlStr.add("respresult", val.success ? 1 : 0);
			reqSqlStr.add("respdatatype", val.getDataType());
			reqSqlStr.add("usedtime", t3-t2);
			reqSqlStr.add("uploadtime", t2-t1);
			reqSqlStr.add("downloadtime", t4 - t3);
			if(!"无效请求".equals(val.info)){
				if(ConfigBAC.getBoolean("sts_http_log"))
				{
					if(ConfigBAC.getInt("logout_sts_http_threshold")<(t3-t2))
					{
						DBHelper.logInsert(LogTbName.TAB_STS_HTTP_LOG(), reqSqlStr);
					}						
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			dos.writeByte(0);
			dos.write(e.toString().getBytes("UTF-8"));
		}
		finally
		{
			dos.close();
		}
	}
	
	public static MyLog stslog = new MyLog(MyLog.NAME_DATE, "log_sts", "STS", Conf.debug, false, true, null);
	
	/**
	 * 处理请求
	 */
	private ReturnValue processingReq(HttpServletRequest request, HttpServletResponse response, DataInputStream dis, DataOutputStream dos, SqlString reqSqlStr) throws Exception{
		short act = dis.readShort();
		String senderkey = dis.readUTF();
		stslog.d("接收到来自 " + senderkey + " 的请求 "+ act);
		
		reqSqlStr.add("act", act);
		reqSqlStr.add("reqserver", senderkey);
		reqSqlStr.add("respserver", Conf.stsKey);
		if(act == G_SEND_INFORM){
			String title = dis.readUTF();
			String content = dis.readUTF();
			String overtimeStr = dis.readUTF();
			String extend = dis.readUTF();
			byte isAll = dis.readByte();
			return ServerBAC.getInstance().sendInform(0, title, content, overtimeStr, extend, isAll==1, null);
		} else 
		if(act == G_SEND_SYSMSG){
			String msg = dis.readUTF();
			return ServerBAC.getInstance().sendSysMsg(msg);
		} else 
		if(act == G_SEND_GAMEPUSH){
			String param = dis.readUTF();
			return ServerBAC.getInstance().sendGamePush(param);
		} else 
		if(act == G_SEND_TOPMSG){
			String msg = dis.readUTF();
			return ServerBAC.getInstance().sendTopMsg(msg);
		} else 
		if(act == G_SEND_INFORM_TOONE){
			int playerid = dis.readInt();
			String title = dis.readUTF();
			String content = dis.readUTF();
			String overtimeStr = dis.readUTF();
			String extend = dis.readUTF();
			return ServerBAC.getInstance().sendInform(playerid, title, content, overtimeStr, extend, false, null);
		} else 
		if(act == G_CLEAR_ALLPLAYER){
			String info = dis.readUTF();
			byte type = dis.readByte();
			return SocketServer.getInstance().clearAllPla(info, type);
		} else 
		if(act == G_GET_PUSHDATA){
			return new ReturnValue(true, SocketServer.getInstance().getPushQueueData());
		} else 
		if(act == G_START_SOCKET){
			return SocketServer.getInstance().start();
		} else 
		if(act == G_STOP_SOCKET){
			return SocketServer.getInstance().stop();
		} else 
		if(act == G_SOCKET_GETSTATE){
			return SocketServer.getInstance().getRunState();
		} else 
		if(act == G_CLEAR_PUSHDATA){
			return new ReturnValue(true, SocketServer.getInstance().clearPushQueue());
		} else 
		if(act == G_BREAK_ONEPLAYER){
			int playerid = dis.readInt();
			String info = dis.readUTF();
			return SocketServer.getInstance().breakOnePla(playerid, info);
		} else 
		if(act == G_SOCKET_RUN_INFO){
			return SocketInfoMgr.getInstance().getSocketRunData();
		} else 
		if(act == G_SERVER_OPENREADY){
			return ServerBAC.getInstance().openServerReady();
		} else 
		if(act == G_DB_ADJUST_IDLE){
			return ServerBAC.getInstance().adjustDBIdle();
		} else 
		if(act == G_GET_DBINFO){
			return new ReturnValue(true, DBHelper.getConnAmInfo());
		} else 
		if(act == G_TESTA){
			return DBPool.getInst().TestA();
		} else 
		if(act == G_TESTB){
			return DBPool.getInst().TestB();
		} else 
		if(act == G_GET_LISTPOOL){
			String tab = dis.readUTF();
			return DBPool.getInst().Test1(tab);
		} else 
		if(act == G_CLEAR_TABPOOL){
			String tab = dis.readUTF();
			return DBPool.getInst().Test2(tab);
		} else 
		if(act == G_GET_TXTPOOL){
			String key = dis.readUTF();
			return DBPool.getInst().Test3(key);
		} else 
		if(act == G_CLEAR_TXTPOOL){
			String key = dis.readUTF();
			return DBPool.getInst().Test4(key);
		} else 
		if(act == G_CLEAR_SERVER_DATA){
			return ServerBAC.getInstance().clearServerData();
		} else 
		if(act == G_PLAYER_BEOFFLINE){
			String sessionid = dis.readUTF();
			return PlayerBAC.getInstance().beOffline(sessionid);
		} else 
		if(act == G_PLAYER_LOGOUT){
			int playerid = dis.readInt();
			String reason = dis.readUTF();
			return PlayerBAC.getInstance().logout(playerid, "STS"+reason);
		} else 
		if(act == G_PLAYER_RECHARGE){
			byte result = dis.readByte();
			String note = dis.readUTF(); //描述
			byte from = dis.readByte(); //来源 1 客户端 2网站
			String channel = dis.readUTF(); //联运渠道
			String orderNo = dis.readUTF(); //订单号
			String centerOrderNo = dis.readUTF();//充值中心订单号
			int playerid = dis.readInt();
			byte chargepoint = dis.readByte();//充值点
			short rechargetype = 0;
			int rmbam = 0;
			if(result==1){
				rechargetype = dis.readShort();
				rmbam = dis.readInt();	
			}
			//System.out.println("收到用户服购买金锭的订单"+orderNo+"结果result="+result+",note="+note+",channel="+channel+",playerid="+playerid);
			LogBAC.logout("charge/"+channel,"收到用户服购买金锭的订单"+orderNo+"结果result="+result+",note="+note+",channel="+channel+",playerid="+playerid);
			ReturnValue rv = ChargeBAC.getInstance().recharge(playerid, rechargetype, rmbam, result, note, from, channel, chargepoint, centerOrderNo);
			if(result==1)
			{
				LogBAC.logout("charge/"+channel,"订单"+orderNo+"发货结果="+rv.success);
				if(rv.success)
				{
					ChargeSendBAC.getInstance().createSendOrder(Conf.sid, channel, orderNo, 1);	
				}
				else
				{
					ChargeSendBAC.getInstance().createSendOrder(Conf.sid, channel, orderNo, -1);	
				}
			}
			return rv;
		} else 
		if(act == G_PLAYER_BUY_TQ){
			byte result = dis.readByte();
			String note = dis.readUTF(); //描述
			byte from = dis.readByte(); //来源 1 客户端 2网站
			String channel = dis.readUTF(); //联运渠道
			String orderNo = dis.readUTF(); //订单号	
			String centerOrderNo = dis.readUTF();//充值中心订单号
			int playerid = dis.readInt();
			byte tqnum = 0;
			if(result == 1){
				tqnum = dis.readByte();
			}
			//System.out.println("收到用户服购买特权的订单"+orderNo+"结果result="+result+",note="+note+",channel="+channel+",playerid="+playerid);
			LogBAC.logout("charge/"+channel,"收到用户服购买特权的订单"+orderNo+"结果result="+result+",note="+note+",channel="+channel+",playerid="+playerid);
			ReturnValue rv = ChargeBAC.getInstance().buyTQ(playerid, tqnum, result, note, from, channel, centerOrderNo);
			if(result==1)
			{
				LogBAC.logout("charge/"+channel,"订单"+orderNo+"发货结果="+rv.success);
				if(rv.success)
				{
					ChargeSendBAC.getInstance().createSendOrder(Conf.sid, channel, orderNo, 1);
				}
				else
				{
					ChargeSendBAC.getInstance().createSendOrder(Conf.sid, channel, orderNo, -1);
				}
			}
			return rv;
		} else
		if(act == G_SERVER_UPDATE) {
			String filename = dis.readUTF();
			int fileLen = dis.readInt();
			byte[] zipBytes = new byte[fileLen];
			dis.read(zipBytes);
			return SystemUpdateBAC.getInstance().updateSystem(filename, zipBytes);
		} else 
		if(act == G_ORDER_BATCH_GIVE) {
			byte chargepoint = dis.readByte();//充值点
			int len = dis.readInt();
			byte[] bytes = new byte[len];
			dis.read(bytes);
			String jsonStr = new String(bytes, "UTF-8");
			LogBAC.logout("charge_regive","收到用户服补单数据,jsonStr="+jsonStr);
			//System.out.println("收到"+jsonStr);
			JSONObject jsonObj = new JSONObject(jsonStr);
			return ChargeBAC.getInstance().orderBatchGive(jsonObj, ChargeBAC.FROM_ORDERGIVE, chargepoint);
		} else 
		if(act == G_PLAYER_CHANGEVALUE){
			int playerid = dis.readInt();
			byte type = dis.readByte();
			String changevalue = dis.readUTF();
			String from = dis.readUTF();
			return PlayerBAC.getInstance().changeValue(playerid, type, changevalue, from);
		} else 
		if(act == G_ICON_FLASH){
			int code = dis.readInt();			
			int activity = dis.readInt();	
			JSONArray pusharr = new JSONArray();
			pusharr.add(code);			
			pusharr.add(activity);
			PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_ICON_FLASH, pusharr.toString());
			return new ReturnValue(true);
		} else 
		if(act == G_BK_SEND_SYS_MAIL){
			String receiverids = dis.readUTF();
			int smailid = dis.readInt();
			return BkSysMailBAC.getInstance().sendToSome(receiverids, smailid);
		} else 
		if(act == G_BK_SEND_SERVER_SYS_MAIL){
			int smailid = dis.readInt();
			return BkSysMailBAC.getInstance().sendToServer(smailid);
		} else 
		if(act == G_PLAYER_BLANK){
			int playerid = dis.readInt();
			String date = dis.readUTF();
			return PlayerBAC.getInstance().blankOffPlayer(playerid, date);
		} else 
		if(act == G_PLAYER_UNBLANK){
			int playerid = dis.readInt();
			return PlayerBAC.getInstance().unBlankOffPlayer(playerid);
		} else 
		if(act == G_PLAYER_BANNED_MSG){
			int playerid = dis.readInt();
			String date = dis.readUTF();
			return PlayerBAC.getInstance().bannedToPostPlayer(playerid, date);
		} else 
		if(act == G_PLAYER_UNBANNED_MSG){
			int playerid = dis.readInt();
			return PlayerBAC.getInstance().unBannedToPostPlayer(playerid);
		} else 
		if(act == G_PLAYER_ASSECT_RECOVER){
			int id = dis.readInt();
			return PlaAssectBAC.getInstance().recover(id);
		} else 
		if(act == G_WEB_GET_PLATFORMGIFT){
			int playerid = dis.readInt();
			String code = dis.readUTF();
			return PlatformGiftCodeBAC.getInstance().webGetPlatformGift(playerid, code);
		} else 
		if(act == G_SEND_SYS_MAIL2){
			String receiverids = dis.readUTF();
			String title = dis.readUTF();
			String content = dis.readUTF();
			String adjunct = dis.readUTF();
			return MailBAC.getInstance().sendSysMail(receiverids, title, content, adjunct.equals("")?null:adjunct, 0);
		} else 
		if(act == G_TXT_FILE_GET_CONTENT){
			int fileid = dis.readInt();
			return TxtFileBAC.getInstance().getFileContent(fileid);
		} else 
		if(act == G_INSERTLOG_GET_STATE){
			return DBHelper.getSaveLogPQState();
		} else 
		if(act == G_RESET_INSERTLOG_TIMEOUTAM){
			return DBHelper.resetInsertLogTimeoutAm();
		} else 
		if(act == G_FILE_CHECK){
			boolean del = dis.readBoolean();
			return FileMgrBAC.getInstance().checkFile(del);
		} else 
		if(act == G_WEB_TARGET_GETRANKING){
			byte type = dis.readByte();
			return PlayerBAC.getInstance().WebGetRanking(type);
		} else 
		if(act == G_BK_GET_PLAYER_DATA){
			int playerid = dis.readInt();
			return PlayerBAC.getInstance().bkGetAllData(playerid);
		} else 
		if(act == G_GET_SERVER_RUN_STATE){
			return ServerBAC.getInstance().getRunState();
		} else 
		if(act == G_MIRROR_GET_TAB){
			int pid = dis.readInt();
			return new ReturnValue(true, MirrorMgr.getPlaMirrorData(pid));
		} else 
		if(act == G_MIRROR_GET_PLA){
			int pid = dis.readInt();
			String tab = dis.readUTF();
			return new ReturnValue(true, MirrorMgr.getPlaMirrorData(pid, tab));
		} else 
		if(act == G_MIRROR_GET_PLA_TAB){
			String tab = dis.readUTF();
			return new ReturnValue(true, MirrorMgr.getTabMirrorData(tab));
		} else 
		if(act == G_MIRROR_CLEAR_TAB){
			String tab = dis.readUTF();
			MirrorMgr.clearTabData(tab, true);
			return new ReturnValue(true);
		} else 
		if(act == G_REFRESH_GAME_RANKING){
			long refreshtime = dis.readLong();
			String data = dis.readUTF();
			return RankingBAC.getInstance().refreshRanking(refreshtime, data);
		} else 
		if(act == G_PARTNER_GETSPRITEBOX){
			int playerid = dis.readInt();
			return PartnerBAC.getInstance().bkGetSpriteBox(playerid);
		} else 
		if(act == G_JJC_CREATE_PC){
			return PlaJJCRankingBAC.getInstance().createPCPlayer();
		} else 
		if(act == G_CB_NPCINVADE){
			int citynum = dis.readInt();
			int npcinfluence = dis.readInt();
			int[] npcamount = Tools.splitStrToIntArr(dis.readUTF(), ",");
			return CBBAC.getInstance().npcInvade(citynum, npcinfluence, npcamount);
		} else
		if(act == G_WB_START){
			long actiTimeLen = dis.readLong();
			byte isConstraint = dis.readByte();
			return WorldBossBAC.getInstance().start(actiTimeLen, isConstraint);
		} else 
		if(act == G_PVP_BATTLE_INFO){
			return BattleManager.getPVPBattleInfo();
		} else
		if(act == G_TEAM_ACTI_START){
			long actiTimeLen = dis.readLong();
			byte isConstraint = dis.readByte();
			return PlaTeamBAC.getInstance().start(actiTimeLen, isConstraint);
		} else 
		if(act == G_UPDATE_WORLDLEVEL){
			return CBBAC.getInstance().updateWorldLevel();
		} else 
		if(act == G_ISSUE_JJCRANKING_AWARD){
			return PlaJJCRankingBAC.getInstance().issueAward("后台");
		} else 
		if(act == G_MINERALS_START){
			return PlaMineralsBAC.getInstance().start("后台");
		} else 
		if(act == G_MINERALS_END){
			return PlaMineralsBAC.getInstance().end("后台");
		} else 
		if(act == G_MINERALS_GETPOSDATA){
			return PlaMineralsBAC.getInstance().bkGetPosData();
		} else 
		{
			return new ReturnValue(false, "无效请求 " + act);
		}
	}
}
