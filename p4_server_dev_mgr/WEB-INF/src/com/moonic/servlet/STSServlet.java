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

import server.common.Tools;
import server.config.LogBAC;
import server.config.ServerConfig;
import util.IPAddressUtil;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.bac.ConfigBAC;
import com.moonic.util.DBHelper;
import com.moonic.util.MyLog;

import conf.Conf;
import conf.LogTbName;

public class STSServlet extends HttpServlet {
	private static final long serialVersionUID = 4598035092703154800L;
	
	//-----------------用户服-----------------
	
	/**
	 * 注册竞技场假人帐号
	 */
	public static final short M_JJC_REGISTER_PC = 108;
	/**
	 * 检查文件
	 */
	public static final short M_FILE_CHECK = 151;
	/**
	 * 获取数据库信息
	 */
	public static final short M_GET_DBINFO = 251;
	/**
	 * 获取列表缓存清单
	 */
	public static final short M_TESTA = 252;
	/**
	 * 获取文本缓存清单
	 */
	public static final short M_TESTB = 253;
	/**
	 * 查看列表缓存
	 */
	public static final short M_GET_LISTPOOL = 254;
	/**
	 * 清除列表缓存
	 */
	public static final short M_CLEAR_TABPOOL = 255;
	/**
	 * 查看文本缓存
	 */
	public static final short M_GET_TXTPOOL = 256;
	/**
	 * 清除文本缓存
	 */
	public static final short M_CLEAR_TXTPOOL = 257;
	/**
	 * 清除文本缓存
	 */
	public static final short M_CLEAR_COLPOOL = 259;
	/**
	 * 获取插入日志线程状态
	 */
	public static final short M_INSERTLOG_GET_STATE = 403;
	/**
	 * 重置插入日志失败次数
	 */
	public static final short M_RESET_INSERTLOG_TIMEOUTAM = 404;
	/**
	 * 获取服务器运行状态
	 */
	public static final short M_GET_SERVER_RUN_STATE = 410;
	/**
	 * 发送更新包
	 */
	public static final short M_SERVER_UPDATE = 501;
	
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
	 * 合服退家族
	 */
	public static final short G_MERGERSERVER_EXITFAC = 378;
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
		int currentAct=0;
		
		try {
			String ip = IPAddressUtil.getIp(request);
			
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
				if(ConfigBAC.getBoolean("logout_sts_ex"));
				{		
					DataInputStream edis = new DataInputStream(new ByteArrayInputStream(buff));
					currentAct = edis.readShort();
					LogBAC.logout("sts", request.getSession().getId()+"\tip="+ip+"\tact="+currentAct+"\tstart\tactive="+ServerConfig.getDataBase().getNumActive()+"\tidle="+ServerConfig.getDataBase().getNumIdle());
				}
				
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
			if(!"无效请求".equals(val.info))
			{				
				if(ConfigBAC.getBoolean("logout_sts_ex"))
				{
					LogBAC.logout("sts", request.getSession().getId()+"\tip="+ip+"\tact="+currentAct+"\tend\tactive="+ServerConfig.getDataBase().getNumActive()+"\tidle="+ServerConfig.getDataBase().getNumIdle()+"\t"+(t3-t2)+"ms");
				}
				
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
	
	public static MyLog stslog = new MyLog(MyLog.NAME_DATE, "log_sts", "STS", false, false, true, null);
	
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
		{
			return new ReturnValue(false, "无效请求 " + act);
		}
	}
}
