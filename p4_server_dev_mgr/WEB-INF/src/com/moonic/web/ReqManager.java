package com.moonic.web;

import javax.servlet.jsp.PageContext;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.moonic.bac.FileMgrBAC;
import com.moonic.bac.PlayerBAC;
import com.moonic.bac.RankingBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.bac.SuppDataMgrBAC;
import com.moonic.bac.TempBAC;
import com.moonic.mail.MailSender;
import com.moonic.mgr.DBPoolMgr;
import com.moonic.mgr.TabMgr;
import com.moonic.servlet.STSServlet;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPool;
import com.moonic.util.MyTools;
import com.moonic.util.NetResult;
import com.moonic.util.STSNetSender;

/**
 * 请求管理
 * @author John
 */
public class ReqManager {
	
	/**
	 * 处理web请求
	 * @param context
	 * @return 请求结果
	 */
	public static ReturnValue processingReq(PageContext context){
		/*try {
			InputStream is = context.getRequest().getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[4*1024];
			int len = 0;
			while((len = is.read(buffer)) != -1){
				baos.write(buffer, 0, len);
			}
			String str = new String(baos.toByteArray(), "gbk");
			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		SmartUpload smartUpload = new SmartUpload();
		try {
			smartUpload.initialize(context);
			smartUpload.setEncode("UTF-8");
			smartUpload.upload();
			Request request = smartUpload.getRequest();
			
			//---------------数据管理----------------
			if(check(request, "createsqlfile")){
				return TabMgr.getInstance().createSqlFile(context);
			} else 
			if(check(request, "list_init")){
				String dir = ServerConfig.getWebInfPath() + "res/tab_list";
				boolean showprocess = Tools.str2boolean(request.getParameter("list_init1"));
				return TabMgr.getInstance().createTabFromDir(dir, true, false, false, false, showprocess);
			} else 
			if(check(request, "txt_init")){
				String dir = ServerConfig.getWebInfPath() + "res/tab_txt";
				return TabMgr.getInstance().saveTxtDirToTab(dir, true);
			} else 
			if(check(request, "list_add")){
				String dir = ServerConfig.getWebInfPath() + "res/tab_list";
				boolean allowdb = Tools.str2boolean(request.getParameter("list_add1"));
				boolean showprocess = Tools.str2boolean(request.getParameter("list_add2"));
				return TabMgr.getInstance().createTabFromDir(dir, false, false, allowdb, false, showprocess);
			} else 
			if(check(request, "data_add")){
				String dir = ServerConfig.getWebInfPath() + "res/tab_data";
				boolean allowdb = Tools.str2boolean(request.getParameter("data_add1"));
				boolean showprocess = Tools.str2boolean(request.getParameter("data_add2"));
				return TabMgr.getInstance().createTabFromDir(dir, false, true, allowdb, false, showprocess);
			} else 
			if(check(request, "txt_add")){
				String dir = ServerConfig.getWebInfPath() + "res/tab_txt";
				return TabMgr.getInstance().saveTxtDirToTab(dir, false);
			} else 
			if(check(request, "log_add")){
				String dir = ServerConfig.getWebInfPath() + "res/tab_log";
				boolean allowdb = Tools.str2boolean(request.getParameter("log_add1"));
				boolean showprocess = Tools.str2boolean(request.getParameter("log_add2"));
				return TabMgr.getInstance().createTabFromDir(dir, false, true, allowdb, true, showprocess);
			} else 
			if(check(request, "tidy_seq")){
				return TabMgr.getInstance().tidySEQ(Tools.str2boolean(request.getParameter("tidy_seq1")), true);
			} else 
			if(check(request, "add_seq")){
				return TabMgr.getInstance().tidySEQ(Tools.str2boolean(request.getParameter("add_seq1")), false);
			} else 
			if(check(request, "check_datacolumn")){
				return TabMgr.getInstance().checkDataTabColumn(false);
			} else 
			if(check(request, "check_log_datacolumn")){
				return TabMgr.getInstance().checkDataTabColumn(true);
			} else 
			if(check(request, "out_tabs")){
				return TabMgr.getInstance().outSQLTabToTxt(ServerConfig.getWebInfPath()+"out_tabs");
			} else 
			if(check(request, "out_tabs2")){
				String[] p = new String[]{
						ServerConfig.getWebInfPath() + "res/tab_list/",
						ServerConfig.getWebInfPath() + "res/tab_data/",
						ServerConfig.getWebInfPath() + "res/tab_log/"
						};
				return TabMgr.getInstance().outSQLTabToTxt(p, ServerConfig.getWebInfPath()+"out_tabs_miss");
			} else 
			if(check(request, "file_check")){
				boolean del = Tools.str2boolean(request.getParameter("file_check1"));
				return FileMgrBAC.getInstance().checkFile(del);
			} else 
			if(check(request, "us_file_check")){
				STSNetSender sender = new STSNetSender(STSServlet.M_FILE_CHECK);
				sender.dos.writeBoolean(Tools.str2boolean(request.getParameter("us_file_check1")));
				return ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_USER_SERVER, sender);
			} else 
			if(check(request, "gs_file_check")){
				STSNetSender sender = new STSNetSender(STSServlet.G_FILE_CHECK);
				sender.dos.writeBoolean(Tools.str2boolean(request.getParameter("gs_file_check1")));
				return ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_GAME_SERVER, sender);
			} else 
			if(check(request, "supp_pla_all")){
				return SuppDataMgrBAC.getInstance().suppPlaAll();
			} else 
			if(check(request, "supp_pla")){
				return SuppDataMgrBAC.getInstance().suppPla(request.getParameter("supp_pla1"));
			} else 
			if(check(request, "search_col")){
				return TabMgr.getInstance().searchCol(request.getParameter("search_col1"));
			}
			//---------------消息管理----------------
			if(check(request, "console_createnotice")){
				return ServerBAC.getInstance().createNotice(
						request.getParameter("console_createnotice1"),
						request.getParameter("console_createnotice2"),
						request.getParameter("console_createnotice3"),
						Tools.str2int(request.getParameter("console_createnotice4")),
						Tools.splitStrToIntArr(request.getParameter("console_createnotice0"), ",")
						);
			} else 
			if(check(request, "console_sendinform")){
				int thesid = Tools.str2int(request.getParameter("console_sendinform0"));
				STSNetSender sender = new STSNetSender(STSServlet.G_SEND_INFORM);
				sender.dos.writeUTF(request.getParameter("console_sendinform1"));
				sender.dos.writeUTF(request.getParameter("console_sendinform2"));
				sender.dos.writeUTF(request.getParameter("console_sendinform3"));
				sender.dos.writeUTF(request.getParameter("console_sendinform4"));
				sender.dos.writeByte(Tools.str2byte(request.getParameter("console_sendinform5")));
				return ServerBAC.getInstance().sendReqToAllOrOneBySid(ServerBAC.STS_GAME_SERVER, sender, thesid);
			} else 
			if(check(request, "console_sendsysmsg")){
				int thesid = Tools.str2int(request.getParameter("console_sendsysmsg0"));
				STSNetSender sender = new STSNetSender(STSServlet.G_SEND_SYSMSG);
				sender.dos.writeUTF(request.getParameter("console_sendsysmsg1"));
				return ServerBAC.getInstance().sendReqToAllOrOneBySid(ServerBAC.STS_GAME_SERVER, sender, thesid);
			} else 
			if(check(request, "console_sendgamepush")){
				int thesid = Tools.str2int(request.getParameter("console_sendgamepush0"));
				STSNetSender sender = new STSNetSender(STSServlet.G_SEND_GAMEPUSH);
				sender.dos.writeUTF(request.getParameter("console_sendgamepush2"));
				return ServerBAC.getInstance().sendReqToAllOrOneBySid(ServerBAC.STS_GAME_SERVER, sender, thesid);
			} else 
			if(check(request, "console_sendtop")){
				int thesid = Tools.str2int(request.getParameter("console_sendtop0"));
				STSNetSender sender = new STSNetSender(STSServlet.G_SEND_TOPMSG);
				sender.dos.writeUTF(request.getParameter("console_sendtop1"));
				return ServerBAC.getInstance().sendReqToAllOrOneBySid(ServerBAC.STS_GAME_SERVER, sender, thesid);
			} else 
			if(check(request, "console_sendinform_toone")){
				int thesid = Tools.str2int(request.getParameter("console_sendinform_toone0"));
				STSNetSender sender = new STSNetSender(STSServlet.G_SEND_INFORM_TOONE);
				sender.dos.writeInt(Tools.str2int(request.getParameter("console_sendinform_toone1")));
				sender.dos.writeUTF(request.getParameter("console_sendinform_toone2"));
				sender.dos.writeUTF(request.getParameter("console_sendinform_toone3"));
				sender.dos.writeUTF(request.getParameter("console_sendinform_toone4"));
				sender.dos.writeUTF(request.getParameter("console_sendinform5"));
				return ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, thesid).rv;
			}
			//---------------用户管理----------------
			if(check(request, "console_openlogin")){
				return ServerBAC.getInstance().openMainServerLogin();
			} else 
			if(check(request, "console_closelogin")){
				return ServerBAC.getInstance().closeMainServerLogin(request.getParameter("console_closelogin1"));
			} else 
			if(check(request, "console_getpushdata")){
				STSNetSender sender = new STSNetSender(STSServlet.G_GET_PUSHDATA);
				ReturnValue srv = ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_GAME_SERVER, sender);
				return srv;
			} else 
			if(check(request, "console_startsocket")){
				STSNetSender sender = new STSNetSender(STSServlet.G_START_SOCKET);
				ReturnValue srv = ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_GAME_SERVER, sender);
				return srv;
			} else 
			if(check(request, "console_stopsocket")){
				STSNetSender sender = new STSNetSender(STSServlet.G_STOP_SOCKET);
				ReturnValue srv = ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_GAME_SERVER, sender);
				return srv;
			} else 
			if(check(request, "console_getsocketstate")){
				STSNetSender sender = new STSNetSender(STSServlet.G_SOCKET_GETSTATE);
				ReturnValue srv = ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_GAME_SERVER, sender);
				return srv;
			} else 
			if(check(request, "console_clearpushdata")){
				STSNetSender sender = new STSNetSender(STSServlet.G_CLEAR_PUSHDATA);
				ReturnValue srv = ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_GAME_SERVER, sender);
				return srv;
			} else 
			if(check(request, "console_serverallmaintain")){
				String note = request.getParameter("console_serverallmaintain1");
				String prompt = request.getParameter("console_serverallmaintain2");
				String shownote = request.getParameter("console_serverallmaintain3");
				byte type = Tools.str2byte(request.getParameter("console_serverallmaintain4"));
				return ServerBAC.getInstance().maintain(0, note, prompt, shownote, type);
			} else 
			if(check(request, "console_serverallopen")){
				return ServerBAC.getInstance().openGameServer(0);
			} else 
			if(check(request, "console_serveronemaintain")){
				int serverid = Tools.str2int(request.getParameter("console_serveronemaintain1"));
				String note = request.getParameter("console_serveronemaintain2");
				String prompt = request.getParameter("console_serveronemaintain3");
				String shownote = request.getParameter("console_serveronemaintain4");
				byte type = Tools.str2byte(request.getParameter("console_serverallmaintain5"));
				return ServerBAC.getInstance().maintain(serverid, note, prompt, shownote, type);
			} else 
			if(check(request, "console_serveroneopen")){
				return ServerBAC.getInstance().openGameServer(Tools.str2int(request.getParameter("console_serveroneopen1")));
			} else 
			if(check(request, "console_breakonepla")){
				STSNetSender sender = new STSNetSender(STSServlet.G_BREAK_ONEPLAYER);
				sender.dos.writeInt(Tools.str2int(request.getParameter("console_breakonepla2")));//玩家ID
				sender.dos.writeUTF(request.getParameter("console_breakonepla3"));//推送说明
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("console_breakonepla1")));//服务器ID
				return nr.rv;
			} else 
			if(check(request, "del_serverdata")){
				return ServerBAC.getInstance().clearServerData(Tools.str2int(request.getParameter("del_serverdata1")));
			} else 
			if(check(request, "del_plaarrdata")){
				return ServerBAC.getInstance().deletePlayerData(request.getParameter("del_plaarrdata1"));
			} else 
			if(check(request, "export_pladata")){
				return ServerBAC.getInstance().exportPlayerData(Tools.str2int(request.getParameter("export_pladata1")));
			} else 
			if(check(request, "player_recharge")){
				STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_RECHARGE);
				sender.dos.writeByte(1);
				sender.dos.writeUTF("控制中心补发"); //成功描述
				sender.dos.writeByte(4); //来源
				sender.dos.writeUTF(request.getParameter("player_recharge2")); //联运渠道
				sender.dos.writeUTF(String.valueOf(System.currentTimeMillis())); //订单号
				sender.dos.writeInt(Tools.str2int(request.getParameter("player_recharge3")));//玩家ID
				sender.dos.writeByte(0);
				sender.dos.writeShort(Tools.str2short(request.getParameter("player_recharge4")));//充值类型
				sender.dos.writeInt(Tools.str2int(request.getParameter("player_recharge5")));//充值RMB数
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("player_recharge1")));
				return nr.rv;
			} else 
			if(check(request, "player_buytq")){
				STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_BUY_TQ);
				sender.dos.writeByte(1);
				sender.dos.writeUTF("控制中心补发"); //成功描述
				sender.dos.writeByte(4); //来源
				sender.dos.writeUTF(request.getParameter("player_buytq2")); //联运渠道
				sender.dos.writeUTF(String.valueOf(System.currentTimeMillis())); //订单号
				sender.dos.writeInt(Tools.str2int(request.getParameter("player_buytq3")));//玩家ID
				sender.dos.writeByte(Tools.str2byte(request.getParameter("player_buytq4")));//特权编号
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("player_buytq1")));
				return nr.rv;
			} else 
			if(check(request, "socket_run_info")){
				int serverid = Tools.str2int(request.getParameter("socket_run_info1"));
				STSNetSender sender = new STSNetSender(STSServlet.G_SOCKET_RUN_INFO);
				return ServerBAC.getInstance().sendReqToAllOrOneBySid(ServerBAC.STS_GAME_SERVER, sender, serverid);
			} else 
			if(check(request, "console_clear_game_pool")){
				STSNetSender sender = new STSNetSender(STSServlet.G_CLEAR_SERVER_DATA);
				sender.dos.writeByte(Tools.str2int(request.getParameter("console_clear_game_pool2")));
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("console_clear_game_pool1")));
				return nr.rv;
			} else 
			if(check(request, "server_open_ready")){
				STSNetSender sender = new STSNetSender(STSServlet.G_SERVER_OPENREADY);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("server_open_ready1")));
				return nr.rv;
			} else 
			if(check(request, "db_adjust_idle")){
				STSNetSender sender = new STSNetSender(STSServlet.G_DB_ADJUST_IDLE);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("db_adjust_idle1")));
				return nr.rv;
			}
			//---------------模块管理----------------
			if(check(request, "debug_refresh_game_ranking")){
				return RankingBAC.getInstance().refreshRanking(MyTools.getTimeLong(request.getParameter("debug_refresh_game_ranking1")));
			} else 
			if(check(request, "partner_getspritebox")){
				STSNetSender sender = new STSNetSender(STSServlet.G_PARTNER_GETSPRITEBOX);
				sender.dos.writeInt(Tools.str2int(request.getParameter("partner_getspritebox2")));
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("partner_getspritebox1")));
				return nr.rv;
			} else 
			if(check(request, "jjc_create_user")){
				STSNetSender sender = new STSNetSender(STSServlet.M_JJC_REGISTER_PC);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_USER_SERVER, sender, Tools.str2int(request.getParameter("jjc_create_user1")));
				return nr.rv;
			} else 
			if(check(request, "jjc_create_pc")){
				STSNetSender sender = new STSNetSender(STSServlet.G_JJC_CREATE_PC);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("jjc_create_pc1")));
				return nr.rv;
			} else 
			if(check(request, "cb_npcinvade")){
				STSNetSender sender = new STSNetSender(STSServlet.G_CB_NPCINVADE);
				sender.dos.writeInt(Tools.str2int(request.getParameter("cb_npcinvade2")));
				sender.dos.writeInt(Tools.str2int(request.getParameter("cb_npcinvade3")));
				sender.dos.writeUTF(request.getParameter("cb_npcinvade4"));
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("cb_npcinvade1")));
				return nr.rv;
			} else
			if(check(request, "wb_start")){
				STSNetSender sender = new STSNetSender(STSServlet.G_WB_START);
				sender.dos.writeLong(Tools.str2int(request.getParameter("wb_start2")));
				sender.dos.writeByte(Tools.str2byte(request.getParameter("wb_start3")));
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("wb_start1")));
				return nr.rv;
			} else
			if(check(request, "team_acti_start")){
				STSNetSender sender = new STSNetSender(STSServlet.G_TEAM_ACTI_START);
				sender.dos.writeLong(Tools.str2int(request.getParameter("team_acti_start2")));
				sender.dos.writeByte(Tools.str2byte(request.getParameter("team_acti_start3")));
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("team_acti_start1")));
				return nr.rv;
			} else
			if(check(request, "get_pvp_battle_info")){
				STSNetSender sender = new STSNetSender(STSServlet.G_PVP_BATTLE_INFO);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("get_pvp_battle_info1")));
				return nr.rv;
			} else 
			if(check(request, "get_battle_replay")){
				long battleid = Tools.str2long(request.getParameter("get_battle_replay1"));
				return PlayerBAC.getInstance().getBattleReplay(battleid);
			} else
			if(check(request, "update_worldlevel")){
				STSNetSender sender = new STSNetSender(STSServlet.G_UPDATE_WORLDLEVEL);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("update_worldlevel1")));
				return nr.rv;
			} else
			if(check(request, "issue_jjcranking_award")){
				STSNetSender sender = new STSNetSender(STSServlet.G_ISSUE_JJCRANKING_AWARD);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("issue_jjcranking_award1")));
				return nr.rv;
			} else
			if(check(request, "minerals_start")){
				STSNetSender sender = new STSNetSender(STSServlet.G_MINERALS_START);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("minerals_start1")));
				return nr.rv;
			} else
			if(check(request, "minerals_end")){
				STSNetSender sender = new STSNetSender(STSServlet.G_MINERALS_END);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("minerals_end1")));
				return nr.rv;
			} else
			if(check(request, "minerals_getposdata")){
				STSNetSender sender = new STSNetSender(STSServlet.G_MINERALS_GETPOSDATA);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("minerals_getposdata1")));
				return nr.rv;
			}
			//---------------数据库管理----------------
			if(check(request, "console_getserverinfo")){
				STSNetSender sender = new STSNetSender(STSServlet.G_GET_DBINFO);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(Tools.str2byte(request.getParameter("console_getserverinfo0")), sender, Tools.str2int(request.getParameter("console_getserverinfo1")));
				return new ReturnValue(true, DBHelper.getConnAmInfo()+"\r\nS:\r\n "+nr.rv.info);
			} else 
			if(check(request, "console_dbtesta")){
				STSNetSender sender = new STSNetSender(STSServlet.G_TESTA);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(Tools.str2byte(request.getParameter("console_dbtesta0")), sender, Tools.str2int(request.getParameter("console_dbtesta1")));
				ReturnValue rv = DBPool.getInst().TestA();;
				rv.info = "B:\r\n" + rv.info + "\r\nS:\r\n" + nr.rv.info;
				return rv;
			} else 
			if(check(request, "console_dbtestb")){
				STSNetSender sender = new STSNetSender(STSServlet.G_TESTB);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(Tools.str2byte(request.getParameter("console_dbtestb0")), sender, Tools.str2int(request.getParameter("console_dbtestb1")));
				ReturnValue rv = DBPool.getInst().TestB();
				rv.info = "B:\r\n" + rv.info + "\r\nS:\r\n" + nr.rv.info;
				return rv;
			} else 
			if(check(request, "console_dbtest1")){
				String table_name = request.getParameter("console_dbtest11");
				STSNetSender sender = new STSNetSender(STSServlet.G_GET_LISTPOOL);
				sender.dos.writeUTF(table_name);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(Tools.str2byte(request.getParameter("console_dbtest10")), sender, Tools.str2int(request.getParameter("console_dbtest12")));
				ReturnValue rv = DBPool.getInst().Test1(table_name);
				rv.info = "B:\r\n" + rv.info + "\r\nS:\r\n" + nr.rv.info;
				return rv;
			} else 
			if(check(request, "console_dbtest2")){
				String table_name = request.getParameter("console_dbtest21");
				DBPoolMgr.getInstance().addClearTablePoolTask(table_name, null);
				return new ReturnValue(true);
			} else 
			if(check(request, "console_dbtest3")){
				String txt_name = request.getParameter("console_dbtest31");
				STSNetSender sender = new STSNetSender(STSServlet.G_GET_TXTPOOL);
				sender.dos.writeUTF(txt_name);
				NetResult nr = ServerBAC.getInstance().sendReqToOne(Tools.str2byte(request.getParameter("console_dbtest30")), sender, Tools.str2int(request.getParameter("console_dbtest32")));
				ReturnValue rv = DBPool.getInst().Test3(txt_name);
				rv.info = "B:\r\n" + rv.info + "\r\nS:\r\n" + nr.rv.info;
				return rv;
			} else 
			if(check(request, "console_dbtest4")){
				String txt_name = request.getParameter("console_dbtest41");
				DBPoolMgr.getInstance().addClearTxtPoolTask(txt_name, null);
				return new ReturnValue(true);
			} else 
			if(check(request, "console_get_insertlog_state")){
				byte type = Tools.str2byte(request.getParameter("console_get_insertlog_state0"));
				int serverid = Tools.str2int(request.getParameter("console_get_insertlog_state1"));
				STSNetSender sender = new STSNetSender(STSServlet.G_INSERTLOG_GET_STATE);
				ReturnValue therv = ServerBAC.getInstance().sendReqToAllOrOneBySid(type, sender, serverid);
				ReturnValue rv = DBHelper.getSaveLogPQState();
				rv.info = "B:\r\n" + rv.info + "\r\nS:\r\n" + therv.info;
				return rv;
			} else 
			if(check(request, "console_reset_insertlog_timeoutam")){
				byte type = Tools.str2byte(request.getParameter("console_reset_insertlog_timeoutam0"));
				int serverid = Tools.str2int(request.getParameter("console_reset_insertlog_timeoutam1"));
				STSNetSender sender = new STSNetSender(STSServlet.G_RESET_INSERTLOG_TIMEOUTAM);
				ReturnValue therv = ServerBAC.getInstance().sendReqToAllOrOneBySid(type, sender, serverid);
				ReturnValue rv = DBHelper.resetInsertLogTimeoutAm();
				rv.info = "B:\r\n" + rv.info + "\r\nS:\r\n" + therv.info;
				return rv;
			} else 
			if(check(request, "clear_log")){
				return ServerBAC.getInstance().clearLog(request.getParameter("clear_log1"));
			} else 
			if(check(request, "debug_sendmail")){
				MailSender.sendMail(request.getParameter("debug_sendmail1"), request.getParameter("debug_sendmail2"), request.getParameter("debug_sendmail3"));
				return new ReturnValue(true);
			} else if(check(request, "debug_getplaallmirror")){
				STSNetSender sender = new STSNetSender(STSServlet.G_MIRROR_GET_TAB);
				sender.dos.writeInt(Tools.str2int(request.getParameter("debug_getplaallmirror2")));
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("debug_getplaallmirror1")));
				return new ReturnValue(true, nr.rv.info);
			} else 
			if(check(request, "debug_getplamirror")){
				STSNetSender sender = new STSNetSender(STSServlet.G_MIRROR_GET_PLA);
				sender.dos.writeInt(Tools.str2int(request.getParameter("debug_getplamirror2")));
				sender.dos.writeUTF(request.getParameter("debug_getplamirror3"));
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("debug_getplamirror1")));
				return new ReturnValue(true, nr.rv.info);
			} else 
			if(check(request, "debug_gettabmirror")){
				STSNetSender sender = new STSNetSender(STSServlet.G_MIRROR_GET_PLA_TAB);
				sender.dos.writeUTF(request.getParameter("debug_gettabmirror2"));
				NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("debug_gettabmirror1")));
				return new ReturnValue(true, nr.rv.info);
			} else 
			if(check(request, "debug_mirrorcleartab")){
				STSNetSender sender = new STSNetSender(STSServlet.G_MIRROR_CLEAR_TAB);
				sender.dos.writeUTF(request.getParameter("debug_mirrorcleartab2"));
				return ServerBAC.getInstance().sendReqToAllOrOneBySid(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(request.getParameter("debug_mirrorcleartab1")));
			} else 
			if(check(request, "temp_jsonquerytest")){
				return TempBAC.getInstance().jsonQueryTest(request.getParameter("temp_jsonquerytest1"));
			} else 
			if(check(request, "temp_groovy_test")){
				return TempBAC.getInstance().groovyTest();
			}
			//---------------数据更新---------------
			if(check(request, "update_list")){
				String suffix = request.getParameter("update_list2");
				boolean createTab = suffix.equals("");
				return TabMgr.getInstance().updateListFile(context, smartUpload, "update_listfile", Tools.str2boolean(request.getParameter("update_list1")), createTab, suffix);
			} else 
			if(check(request, "update_txt")){
				return TabMgr.getInstance().updateTxtFile(context, smartUpload, "update_txtfile");
			} else 
			{
				return new ReturnValue(false, "非法请求");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	private static boolean check(Request request, String str){
		return request.getParameter(str)!=null;
	}
}
