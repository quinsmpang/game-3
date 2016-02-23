package com.moonic.bac;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.mgr.LockStor;
import com.moonic.mirror.MirrorMgr;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.GamePushData;
import com.moonic.socket.Player;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;
import com.moonic.util.TimeTest;

import conf.Conf;
import conf.LogTbName;

/**
 * 角色BAC
 * @author John
 */
public class PlayerBAC extends PlaBAC {
	public static final String tab_player_change_type = "tab_player_change_type";
	public static final String tab_shortcut_grow = "tab_shortcut_grow";
	public static final String tab_player_uplv = "tab_player_uplv";
	
	/**
	 * 构造
	 */
	public PlayerBAC(){
		super("tab_player", "id");
	}
	
	/**
	 * 初始化
	 */
	public void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception {
		
	}
	
	/**
	 * 创建角色
	 */
	public ReturnValue create(int userid, int vsid, String name, byte num, int partnernum, int isrobot) {
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.PLAYER_NAME)) {
				MyTools.checkNoCharEx(name, '#');
				if(name.equals("") || name.toLowerCase().equals("null")){
					BACException.throwInstance("名字不可用，请更改后重试");
				}
				DBPaRs partnerRs = null;
				if(partnernum != 0){
					partnerRs = DBPool.getInst().pQueryA(PartnerBAC.tab_partner, "num="+partnernum);
					if(partnerRs.getInt("cpchoose") == 0){
						BACException.throwInstance("无法选择此伙伴作为初始伙伴");
					}
				}
				dbHelper.openConnection();
				ResultSet userRs = dbHelper.query(UserBAC.tab_user, "id,channel,onlinestate,devuser", "id="+userid);
				//System.out.println("userid="+userid);
				if(!userRs.next()){
					BACException.throwInstance("用户未找到");
				}
				String channel = userRs.getString("channel");
				DBPsRs channelServerRs = ServerBAC.getInstance().getChannelServer(channel, vsid);
				if(!channelServerRs.next()) {
					BACException.throwInstance("服务器不存在");
				}
				int csid = channelServerRs.getInt("serverid");
				if(csid != Conf.sid){
					BACException.throwInstance("服务器ID不匹配("+csid+"/"+Conf.sid+")");
				}
				DBPaRs serverRs = DBPool.getInst().pQueryA(ServerBAC.tab_server, "id="+Conf.sid);
				int state = channelServerRs.getInt("state")!=-1?channelServerRs.getInt("state"):serverRs.getInt("state");
				String opentime = !channelServerRs.getString("opentime").equals("-1")?channelServerRs.getString("opentime"):serverRs.getString("opentime");
				//检查服务器是否已开放
				if(!MyTools.checkSysTimeBeyondSqlDate(MyTools.getTimeLong(opentime))) {
					if(userRs.getInt("devuser")!=1){
						String shownote = serverRs.getString("shownote");
						BACException.throwInstance((shownote==null||shownote.equals(""))?"服务器将于"+opentime+"开放":shownote);
					}
				}
				//检查服务器是否在维护
				if(state==1) {
					if(userRs.getInt("devuser")!=1) {
						String shownote = serverRs.getString("shownote");
						BACException.throwInstance(((shownote==null||shownote.equals(""))?"服务器维护中":shownote)+(!ConfigBAC.getBoolean("openlogin")?"#1":""));
					}
				}
				boolean exist1 = dbHelper.queryExist("tab_player", "serverid="+Conf.sid+" and vsid="+vsid+" and userid="+userid);
				if(exist1){
					BACException.throwInstance("用户在此服务器已经创建角色 userid="+userid+",vsid="+vsid);
				}
				boolean exist2 = dbHelper.queryExist("tab_player", "serverid="+Conf.sid+" and name='"+name+"'");
				if(exist2){
					BACException.throwInstance("角色名已存在");
				}
				
				SqlString sqlStr = new SqlString();
				sqlStr.add("userid", userid);
				sqlStr.add("serverid", Conf.sid);
				sqlStr.add("vsid", vsid);
				sqlStr.add("channel", channel);
				sqlStr.addDateTime("savetime", MyTools.getTimeStr());
				sqlStr.add("onlinestate", 0);
				sqlStr.add("onlinetime", 0);
				sqlStr.add("num", num);
				sqlStr.add("name", name);
				sqlStr.add("money", 0);
				sqlStr.add("coin", 0);
				sqlStr.add("lv", 1);
				sqlStr.add("exp", 0);
				sqlStr.add("vip", Conf.initvip);
				sqlStr.add("buycoin", 0);
				sqlStr.add("rebatecoin", 0);
				sqlStr.add("tqnum", 0);
				sqlStr.add("rechargermb", 0);
				sqlStr.add("rechargeam", 0);
				sqlStr.add("rechargtypes", (new JSONArray()).toString());
				sqlStr.addDateTime("resetdate", MyTools.getDaiylResetTime(5));//特别注意第一次登录不会重置日常数据
				sqlStr.add("openfunc", (new JSONArray()).toString());
				sqlStr.add("enable", 1);
				sqlStr.add("isrobot", isrobot);
				if(isrobot == 1){
					sqlStr.addDateTime("logintime", MyTools.getTimeStr());
				}
				int playerid = insertByAutoID(dbHelper, sqlStr);
				
				PlaRoleBAC.getInstance().init(dbHelper, playerid);
				PlaFacBAC.getInstance().init(dbHelper, playerid);
				PlaWelfareBAC.getInstance().init(dbHelper, playerid);
				PlaSupplyBAC.getInstance().init(dbHelper, playerid);
				PlaSummonBAC.getInstance().init(dbHelper, playerid);
				PlaShopBAC.getInstance().init(dbHelper, playerid);
				if(partnernum != 0){
					PartnerBAC.getInstance().create(dbHelper, playerid, partnernum, partnerRs.getInt("awaken")==0?1:0, 1, 1, partnerRs.getInt("initstar"), null, null, null);
				}
				
				GameLog.getInst(playerid, GameServlet.ACT_PLAYER_CREATE)
				.addRemark("创建角色：" + GameLog.formatNameID(name, playerid))
				.addRemark(partnerRs!=null?"选择伙伴："+partnerRs.getString("name"):"未选择伙伴")
				.save();
				return new ReturnValue(true, String.valueOf(playerid));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	private static ArrayList<Integer> waituserList = new ArrayList<Integer>();//排队的用户
	private static ArrayList<Long> waitstarttimelist = new ArrayList<Long>();//开始排队时间
	
	/**
	 * 角色登录
	 */
	public ReturnValue login(int userid, String client_sessionid, int vsid)
	{
		TimeTest timetest = new TimeTest("plalogin", "PLALOGIN", true, true, false);
		DBHelper dbHelper = new DBHelper();
		int remove_wait_uid = -1;
		try {
			//检查服务器承载人数是否已满
			synchronized (waituserList) {
				//清除超时未发送请求的等待玩家，客户端3s发一次请求
				for(int i = 0; i < waitstarttimelist.size(); ){
					if(System.currentTimeMillis()-waitstarttimelist.get(i)>8000){
						//System.out.println("UID:"+userid+"请求超时，清除出等待队列，起始时间："+waitstarttimelist.get(0)+"当前时间："+System.currentTimeMillis()+" waituserList:"+waituserList+" waitstarttimelist:"+waitstarttimelist);
						waituserList.remove(i);
						waitstarttimelist.remove(i);
					} else {
						i++;
					}
				}
				int onlineamount = SocketServer.getInstance().session_plamap.size();
				//人数已满
				if(onlineamount >= Conf.max_player){
					int wait_ind = waituserList.indexOf(userid);
					if(wait_ind == -1){
						waituserList.add(userid);
						waitstarttimelist.add(System.currentTimeMillis());
						//System.out.println("UID:"+userid+"加入到等待队列 时间："+System.currentTimeMillis());
					} else {
						waitstarttimelist.set(wait_ind, System.currentTimeMillis());
						//System.out.println("UID:"+userid+"更新等待时间："+System.currentTimeMillis());
					}
					BACException.throwInstance("服务器人数已满,"+waituserList.indexOf(userid));
				}
				//有排队用户
				if(waituserList.size() > 0){
					int wait_ind = waituserList.indexOf(userid);
					//不在队伍中或未到允许进入的范围
					if(wait_ind == -1 || wait_ind+1 > Conf.max_player-onlineamount){
						if(wait_ind == -1){
							waituserList.add(userid);
							waitstarttimelist.add(System.currentTimeMillis());
							//System.out.println("UID:"+userid+"加入到等待队列 时间："+System.currentTimeMillis());
						} else {
							waitstarttimelist.set(wait_ind, System.currentTimeMillis());
							//System.out.println("UID:"+userid+"更新等待时间："+System.currentTimeMillis());
						}
						BACException.throwInstance("服务器人数已满,"+waituserList.indexOf(userid));
					}
					remove_wait_uid = userid;
				}
			}
			dbHelper.openConnection();
			ResultSet userRs = dbHelper.query(UserBAC.tab_user, "id,channel,username,wifi,devuser,onlinestate,serverid,playerid,sessionid,mac,imei,platform", "id="+userid);
			if(!userRs.next()){
				BACException.throwInstance("用户未找到");
			}
			if(userRs.getInt("onlinestate")==0){
				BACException.throwInstance("帐号已被注销，请重新登录("+userid+")");
			}
			if(!ActivateCodeBAC.getInstance().checkActivate(dbHelper, userRs.getString("channel"), userRs.getString("username"))){
				BACException.throwInstance("帐号尚未激活");
			}
			String sessionid = userRs.getString("sessionid");
			if(userRs.getInt("onlinestate")==1 && userRs.getInt("serverid")!=0 && userRs.getInt("playerid")!=0){
				if(!client_sessionid.equals(sessionid)){
					BACException.throwInstance("帐号已在其他服务器登录("+userRs.getInt("serverid")+")，请重新登录帐号后尝试");
				} else {
					//System.out.println("error:角色登录异常，客户端Session("+client_sessionid+")与数据库信息一致");  //一致报什么异常？
				}
			}
			timetest.add("用户验证");
			DBPsRs channelServerRs = ServerBAC.getInstance().getChannelServer(userRs.getString("channel"), vsid);
			if(!channelServerRs.next()){
				BACException.throwInstance("服务器未找到");
			}
			if(channelServerRs.getInt("istest")==1 && userRs.getInt("devuser")!=1){
				BACException.throwInstance("服务器未找到");
			}
			DBPaRs serverRs = DBPool.getInst().pQueryA(ServerBAC.tab_server, "id="+Conf.sid);
			int state = channelServerRs.getInt("state")!=-1?channelServerRs.getInt("state"):serverRs.getInt("state");
			String opentime = !channelServerRs.getString("opentime").equals("-1")?channelServerRs.getString("opentime"):serverRs.getString("opentime");
			//检查服务器是否已开放
			if(!MyTools.checkSysTimeBeyondSqlDate(MyTools.getTimeLong(opentime))) {
				if(userRs.getInt("devuser")!=1){
					String shownote = serverRs.getString("shownote");
					BACException.throwInstance((shownote==null||shownote.equals(""))?"服务器将于"+opentime+"开放":shownote);
				}
			}
			//检查服务器是否在维护
			if(state==1) {
				if(userRs.getInt("devuser")!=1) {
					String shownote = serverRs.getString("shownote");
					BACException.throwInstance(((shownote==null||shownote.equals(""))?"服务器维护中":shownote)+(!ConfigBAC.getBoolean("openlogin")?"#1":""));
				}
			}
			timetest.add("服务器验证");
			//获取角色数据
			ResultSet dataRs = dbHelper.query("tab_player", null, "serverid="+Conf.sid+" and vsid="+vsid+" and userid="+userid);
			if(!dataRs.next()){
				BACException.throwInstance("尚未创建角色");
			}
			int psid = dataRs.getInt("serverid");
			if(psid != Conf.sid){
				BACException.throwInstance("登录服务器异常("+psid+"/"+Conf.sid+")");
			}
			int enable = dataRs.getInt("enable");
			if(enable==-1 || (enable==0 && !MyTools.checkSysTimeBeyondSqlDate(dataRs.getTimestamp("blankofftime")))){
				BACException.throwInstance("角色已被冻结");
			}
			timetest.add("角色验证");
			//准备数据
			int playerid = dataRs.getInt("id");
			String dateStr = Tools.getCurrentDateStr();
			String timeStr = Tools.getCurrentDateTimeStr();
			boolean firstlogin = dataRs.getString("logintime")==null;
			long lastlogintime = MyTools.getTimeLong(dataRs.getTimestamp("logintime"));
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLAYER_LOGIN);
			//Socket登录
			Player pla = new Player(sessionid, userid, playerid, dataRs.getString("name"), MyTools.getTimeLong(dataRs.getTimestamp("savetime")), new JSONArray(dataRs.getString("openfunc")));
			pla.initUserInfo(userRs);
			SocketServer.getInstance().plamap.put(pla.pid, pla);
			int old_amount = SocketServer.getInstance().session_plamap.size();
			SocketServer.getInstance().session_plamap.put(pla.sessionid, pla);
			SocketServer.getInstance().connectlog.d("加入角色：" + pla.pname + "("+ pla.pid + "," + pla.sessionid +")" + " 人数：" + old_amount + " -> " + SocketServer.getInstance().session_plamap.size());
			timetest.add("SOCKET注入");
			//登录角色日志
			createLoginLog(playerid, userid, dateStr, timeStr);
			//用户信息更新
			SqlString userSqlStr = new SqlString();
			userSqlStr.add("serverid", Conf.sid);
			userSqlStr.add("playerid", playerid);
			UserBAC.getInstance().update(dbHelper, userid, userSqlStr);
			//登录角色信息更新
			SqlString sqlStr = new SqlString();
			sqlStr.addDateTime("logintime", timeStr);
			sqlStr.add("onlinestate", 1);
			sqlStr.add("sessionid", sessionid);
			update(dbHelper, playerid, sqlStr);
			timetest.add("更新登录信息");
			//恢复体力
			PlaRoleBAC.getInstance().recoverEnergy(dbHelper, playerid, gl);
			//恢复挑战次数
			PlaRoleBAC.getInstance().recoverArtifactRobTimes(dbHelper, playerid, gl);
			//检查并选择是否清除日常数据
			checkAndResetDayData(dbHelper, playerid, false, firstlogin, gl);
			//获取角色信息
			JSONObject json_data = getAllData(dbHelper, playerid, true, true, true, true);
			timetest.add("获取角色信息");
			//通知上线
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			PushData.getInstance().sendPlaToFansAndNosFac(SocketServer.ACT_PLAYER_ONLINE, String.valueOf(playerid), plafacRs.getInt("factionid"), playerid, FriendBAC.TYPE_ALL);
			timetest.add("通知上线");
			//支付类型
			JSONArray chargeArr = ChargeBAC.getInstance().getChargeType(userRs.getString("channel"));
			json_data.put("charge", chargeArr);
			timetest.add("其他数据");
			timetest.save(1000);
			
			gl.save();
			return new ReturnValue(true, json_data.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			if(remove_wait_uid != - 1){
				synchronized (waituserList) {
					int remove_wait_ind = waituserList.indexOf(remove_wait_uid);
					if(remove_wait_ind != -1){
						waituserList.remove(remove_wait_ind);
						waitstarttimelist.remove(remove_wait_ind);
					}
				}	
			}
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 打开推送
	 */
	public ReturnValue openPush(int playerid){
		try {
			Player pla = SocketServer.getInstance().plamap.get(playerid);
			if(pla!=null){
				pla.openPush();
			}
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 更新心跳
	 */
	public ReturnValue updateOnlineState(int playerid, short mark){
		try {
			Player pla = SocketServer.getInstance().plamap.get(playerid);
			if(pla!=null){
				pla.updateOnlineState(mark);
			}
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 角色登出
	 */
	public ReturnValue logout(int playerid, String reason){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			logout(dbHelper, playerid, reason);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 通知被挤下线
	 */
	public ReturnValue beOffline(String sessionid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			Player pla = SocketServer.getInstance().session_plamap.get(sessionid);
			if(pla != null){
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream baos_dos = new DataOutputStream(baos);
					baos_dos.writeShort(SocketServer.ACT_SYS_BEOFFLINE);
					baos_dos.writeShort(-100);//表示忽略MARK匹配
					baos_dos.write("你的帐号在其他地方上线，你将被迫下线。".getBytes("UTF-8"));
					baos_dos.close();
					byte[] pushdata = baos.toByteArray();
					SocketServer.getInstance().exePush(pla.dos, pushdata);	
				} catch (Exception e) {}
				logout(dbHelper, pla.pid, "被挤下线");
			}
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 后台获取玩家数据
	 */
	public ReturnValue bkGetAllData(int playerid) {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			//基本数据
			JSONObject jsonobj = getAllData(dbHelper, playerid, true, true, false, false);
			//TODO 补充数据
			return new ReturnValue(true, jsonobj.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 从缓存获取角色数据
	 */
	public ReturnValue getAllData(int playerid, int targetid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			JSONObject jsonobj = getAllData(dbHelper, targetid);
			return new ReturnValue(true, jsonobj.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 检查与重置日常数据
	 */
	public ReturnValue checkAndResetDayData(int playerid, boolean must){
		DBHelper dbHelper = new DBHelper();
		try {
			if(!Conf.debug && must){
				BACException.throwInstance("非法操作");
			}
			synchronized (LockStor.getLock(LockStor.PLAYER_RESET_DAYDATE)) {
				dbHelper.openConnection();
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_DEBUG_RESETDAYDATE);
				JSONObject returnobj = checkAndResetDayData(dbHelper, playerid, must, false, gl);
				
				gl.save();
				return new ReturnValue(true, returnobj.toString());		
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 改名
	 */
	public ReturnValue rename(int playerid, String newName){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.PLAYER_NAME)) {
				MyTools.checkNoCharEx(newName, '#');
				if(newName.equals("") || newName.toLowerCase().equals("null")){
					BACException.throwInstance("名字不可用，请更改后重试");
				}
				DBPaRs plaRs = getDataRs(playerid);
				String oldName = plaRs.getString("name");
				dbHelper.openConnection();
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLAYER_RENAME);
				boolean exist2 = dbHelper.queryExist("tab_player", "serverid="+Conf.sid+" and name='"+newName+"'");
				if(exist2){
					BACException.throwInstance("角色名已存在");
				}
				if(!oldName.contains("#")){
					useCoin(dbHelper, playerid, 100, gl);
				}
				SqlString sqlStr = new SqlString();
				sqlStr.add("name", newName);
				update(dbHelper, playerid, sqlStr);
				
				JSONArray pusharr = new JSONArray();
				pusharr.add(playerid);
				pusharr.add(newName);
				PushData.getInstance().sendPlaToFans(SocketServer.ACT_PLAYER_RENAME, pusharr.toString(), playerid, FriendBAC.TYPE_ALL);
				
				
				gl.addRemark("原名"+oldName+" 改名为"+newName)
				.save();
				return new ReturnValue(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 设置头像
	 */
	public ReturnValue setFace(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plaRs = getDataRs(playerid);
			int oldNum = plaRs.getInt("num");
			if(oldNum == num){
				BACException.throwInstance("头像无改变");
			}
			SqlString sqlStr = new SqlString();
			sqlStr.add("num", num);
			update(dbHelper, playerid, sqlStr);
			
			GameLog.getInst(playerid, GameServlet.ACT_PLAYER_SETFACE)
			.addRemark("原头像"+oldNum+" 设置头像为"+num)
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * WEB获取各排行
	 * @param serverid 服务器ID
	 * @param type 0.所有排行 1.等级排行 2.竞技排名排行
	 */
	public ReturnValue WebGetRanking(byte type){
		DBHelper dbHelper = new DBHelper();
		try {
			JSONArray jsonarr = new JSONArray();//排行类型 服务器 服务器名称 排名 用户名 等级/战力
			//TODO 获取游戏排行供WEB展示
			return new ReturnValue(true, jsonarr.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 改变值
	 */
	public ReturnValue changeValue(int playerid, byte type, String changevalue, String from){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLAYER_CHANGEVALUE);
			
			DBPaRs pctRs = DBPool.getInst().pQueryA(tab_player_change_type, "type="+type);
			
			String classname = pctRs.getString("classname");
			String columnName = pctRs.getString("columnname");
			String columnDesc = pctRs.getString("name");
			
			SqlString sqlStr = new SqlString();
			if(pctRs.getInt("changetype") == 1){
				sqlStr.addChange(columnName, Tools.str2int(changevalue));	
			} else 
			if(pctRs.getInt("changetype") == 2){
				sqlStr.add(columnName, changevalue);
			} else 
			if(pctRs.getInt("changetype") == 3){
				sqlStr.addDateTime(columnName, MyTools.getTimeStr(MyTools.getTimeLong(changevalue)));
			}
			PlaBAC plaBac = (PlaBAC)MirrorMgr.classname_mirror.get("com.moonic.bac."+classname);
			DBPaRs rs = plaBac.getDataRs(playerid);
			String oldvalue = rs.getString(columnName);
			plaBac.update(dbHelper, playerid, sqlStr);
			if(pctRs.getInt("changetype") == 1){
				gl.addChaNote(columnDesc, Tools.str2int(oldvalue), Tools.str2int(changevalue));	
			} else 
			if(pctRs.getInt("changetype") == 2){
				gl.addRemark("设置"+columnDesc+"为"+changevalue);
			} else 
			if(pctRs.getInt("changetype") == 3){
				gl.addRemark("设置"+columnDesc+"为"+changevalue);
			}
			
			JSONArray pusharr = new JSONArray();
			pusharr.add(type);
			pusharr.add(changevalue);
			PushData.getInstance().sendPlaToOne(SocketServer.ACT_PLAYER_CHANGEVALUE, pusharr.toString(), playerid);
			
			gl.addRemark("来源："+from);
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试加经验
	 */
	public ReturnValue debugAddExp(int playerid, long addexp){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			if(addexp <= 0){
				BACException.throwInstance("增加值不能小于0");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_DEBUG_GAME_LOG);
			addExp(dbHelper, playerid, addexp, gl);
			
			gl.addRemark("调试加经验");
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试快速成长
	 */
	public ReturnValue debugShortcutGrow(int playerid, JSONArray openfunc, int num){
		try {
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_DEBUG_GAME_LOG);
			DBPaRs plaRs = getDataRs(playerid);
			int plalv = plaRs.getInt("lv");
			int plaexp = plaRs.getInt("exp");
			shortcutGrow(playerid, num, plalv, plaexp, openfunc, gl);
			
			gl.addRemark("调试快速成长");
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 快速成长
	 */
	public void shortcutGrow(int playerid, int num, int plalv, int plaexp, JSONArray openfunc, GameLog gl) throws Exception {
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs shortcutgrowRs = DBPool.getInst().pQueryA(tab_shortcut_grow, "num="+num);
			if(!shortcutgrowRs.exist()){
				BACException.throwInstance("快速成长编号 "+num+" 未找到");
			}
			//主角等级
			int tgr_plalv = shortcutgrowRs.getInt("plalv");
			if(plalv < tgr_plalv){
				int needexp = (int)DBPool.getInst().pQueryS(tab_player_uplv, "lv>="+plalv+" and lv<"+tgr_plalv).sum("needexp")-plaexp;
				addExp(dbHelper, playerid, needexp, gl);
			}
			//加铜钱
			int addmoney = shortcutgrowRs.getInt("money");
			if(addmoney > 0){
				addValue(dbHelper, playerid, "money", addmoney, gl, GameLog.TYPE_MONEY);
			}
			//加金锭
			int addcoin = shortcutgrowRs.getInt("coin");
			if(addcoin > 0){
				addValue(dbHelper, playerid, "coin", addcoin, gl, GameLog.TYPE_COIN);
			}
			//加伙伴
			int[] parnumarr = Tools.splitStrToIntArr(shortcutgrowRs.getString("parnum"), ",");
			while(parnumarr[0] == 2 && parnumarr[0] > 6){
				parnumarr = Tools.removeOneFromIntArr(parnumarr, MyTools.getRandom(1, parnumarr.length-1));
			}
			int parlv = shortcutgrowRs.getInt("parlv");
			int parstar = shortcutgrowRs.getInt("parstar");
			int parphase = shortcutgrowRs.getInt("parquality");
			String parequipStr = shortcutgrowRs.getString("parequip");
			for(int i = 1; i < parnumarr.length; i++){
				DBPaRs partnerRs = DBPool.getInst().pQueryA(PartnerBAC.tab_partner, "num="+parnumarr[i]);
				int star = 0;
				if(parstar != -1){
					star = parstar;
				} else {
					star = partnerRs.getInt("initstar");
				}
				int[] equiparr = new int[6];
				int[][] equipdataarr = PartnerBAC.getInstance().converEquipStateToData(parequipStr);
				for(int k = 0; k < equipdataarr.length; k++){
					if(equipdataarr[k][0] != 0){
						JSONArray itemarr = ItemBAC.getInstance().add(dbHelper, playerid, ItemBAC.TYPE_EQUIP_ORDINARY, equipdataarr[k][0], 1, ItemBAC.ZONE_USE, ItemBAC.SHORTCUT_MAIL, new JSONArray(new int[]{equipdataarr[k][1], equipdataarr[k][2]}), 1, gl);
						equiparr[k] = itemarr.optJSONObject(0).optInt("id");		
					}
				}
				int[] orbarr = PartnerBAC.getInstance().converOrbStateToNum(parphase, partnerRs.getString("upphasenum"), shortcutgrowRs.getString("parorb"));
				int[] skilvarr = Tools.splitStrToIntArr(shortcutgrowRs.getString("skilllv"), ",");
				PartnerBAC.getInstance().create(dbHelper, playerid, parnumarr[i], partnerRs.getInt("awaken")==0?1:0, parlv, parphase, star, equiparr, orbarr, skilvarr);
			}
			//增加物品
			String haveitem = shortcutgrowRs.getString("haveitem");
			if(!haveitem.equals("-1")){
				AwardBAC.getInstance().getAward(dbHelper, playerid, haveitem, ItemBAC.SHORTCUT_MAIL, -1, gl);
			}
			//开启功能
			FunctionBAC.getInstance().debugOpenAllFunc(playerid, openfunc, new int[]{1002});
			//更新战力
			PartnerBAC.getInstance().updateBattlePower(dbHelper, playerid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 加经验
	 */
	public JSONArray addExp(DBHelper dbHelper, int playerid, long addexp, GameLog gl) throws Exception {
		DBPaRs plaRs = getDataRs(playerid);
		int oldlv = plaRs.getInt("lv");
		int oldexp = plaRs.getInt("exp");
		JSONArray jsonarr = ExpBAC.getInstance().addExp(tab_player_uplv, oldlv, oldexp, addexp, 0, "主角", gl);
		if(jsonarr != null){
			int newlv = jsonarr.optInt(0);
			int newexp = jsonarr.optInt(1);
			SqlString sqlStr = new SqlString();
			sqlStr.add("lv", newlv);
			sqlStr.add("exp", newexp);
			update(dbHelper, playerid, sqlStr);
			if(newlv > oldlv){
				PlaRoleBAC.getInstance().upLevelOperate(dbHelper, playerid, oldlv, newlv, gl);
				int factionid = PlaFacBAC.getInstance().getIntValue(playerid, "factionid");
				JSONArray pusharr = new JSONArray();
				pusharr.add(playerid);
				pusharr.add(newlv);
				PushData.getInstance().sendPlaToFansAndNosFac(SocketServer.ACT_PLAYER_LVUP, pusharr.toString(), factionid, playerid, FriendBAC.TYPE_ALL);
			}
		}
		return jsonarr;
	}
	
	/**
	 * 获取伙伴最高等级
	 */
	public int getMaxPartnerLv(int playerid) throws Exception {
		DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
		DBPaRs plauplvRs = DBPool.getInst().pQueryA(PlayerBAC.tab_player_uplv, "lv="+plaRs.getInt("lv"));
		int partnermaxlv = plauplvRs.getInt("partnermaxlv");
		return partnermaxlv;
	}
	
	/**
	 * 获取最高体力
	 */
	public int getMaxEnergy(int playerid) throws Exception {
		DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
		DBPaRs plauplvRs = DBPool.getInst().pQueryA(PlayerBAC.tab_player_uplv, "lv="+plaRs.getInt("lv"));
		int maxenergy = plauplvRs.getInt("maxenergy");
		return maxenergy;
	}
	
	/**
	 * 创建登录游戏服务器日志
	 */
	public void createLoginLog(int playerid, int userid, String dateStr, String timeStr) {
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("userid", userid);
		sqlStr.add("serverid", Conf.sid);
		sqlStr.addDate("logindate", dateStr);
		sqlStr.addDateTime("logintime", timeStr);
		DBHelper.logInsert(LogTbName.TAB_PLAYER_LOGIN_LOG(), sqlStr);
	}
	
	/**
	 * 角色封锁
	 */
	public ReturnValue blankOffPlayer(int playerid, String date) {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			if (date != null && !"".equals(date)) {
				sqlStr.addDateTime("blankofftime", date);
				sqlStr.add("enable", 0);
			} else {
				sqlStr.add("enable", -1);
			}
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 角色解封
	 */
	public ReturnValue unBlankOffPlayer(int playerid) {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.add("enable", 1);
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(true);
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 角色禁言
	 */
	public ReturnValue bannedToPostPlayer(int playerid, String date) {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.addDateTime("bannedmsgtime", date);
			update(dbHelper, playerid, sqlStr);
			
			GamePushData.getInstance(1).sendToOne(playerid);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}

	/**
	 * 角色解除禁言
	 */
	public ReturnValue unBannedToPostPlayer(int playerid) {
		return bannedToPostPlayer(playerid, MyTools.getTimeStr());
	}
	
	/**
	 * 获取角色在线时长
	 */
	public long getOnlineTimeLen(int playerid) throws Exception {
		DBPaRs plaRs = getDataRs(playerid);
		return plaRs.getInt("onlinetime")+(System.currentTimeMillis()-Math.max(MyTools.getCurrentDateLong(), plaRs.getTime("logintime")));
	}
	
	/**
	 * 角色登出
	 */
	public void logout(DBHelper dbHelper, int playerid, String reason) throws Exception {
		//System.out.println("--------------logout---"+playerid+"----------------");
		DBPaRs plaRs = getDataRs(playerid);
		if(plaRs.getInt("onlinestate")==1) 
		{
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLAYER_LOGOUT);
			long onlinetimelen = System.currentTimeMillis()-Math.max(MyTools.getCurrentDateLong(), plaRs.getTime("logintime"));
			//退出团队副本
			FacCopymapBAC.getInstance().exit(dbHelper, playerid);
			//更新战力持久数据
			PartnerBAC.getInstance().updateBattlePower(dbHelper, playerid);
			//退出组队活动
			PlaTeamBAC.getInstance().logout(playerid);
			//清除在线信息
			SqlString sqlStr = new SqlString();
			sqlStr.add("sessionid", "0");
			sqlStr.add("onlinestate", 0);
			sqlStr.addChange("onlinetime", onlinetimelen);
			update(dbHelper, playerid, sqlStr);
			//注销日志
			long currenttime = System.currentTimeMillis();
			int onlinetime = (int)((currenttime - plaRs.getTime("logintime"))/1000);
			SqlString logoutSqlStr = new SqlString();
			logoutSqlStr.add("playerid", playerid);
			logoutSqlStr.add("userid", plaRs.getInt("userid"));
			logoutSqlStr.add("serverid", plaRs.getInt("serverid"));
			logoutSqlStr.addDate("logindate", MyTools.getDateStr(plaRs.getTime("logintime")));
			logoutSqlStr.addDateTime("logintime", MyTools.getTimeStr(plaRs.getTime("logintime")));
			logoutSqlStr.addDateTime("logouttime", MyTools.getTimeStr(currenttime));
			logoutSqlStr.add("onlinetime", onlinetime);
			DBHelper.logInsert(LogTbName.TAB_PLAYER_LOGIN_LOG(), logoutSqlStr);
			//通知下线
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			PushData.getInstance().sendPlaToFansAndNosFac(SocketServer.ACT_PLAYER_OFFLINE, String.valueOf(playerid), plafacRs.getInt("factionid"), playerid, FriendBAC.TYPE_ALL);
			//Socket注销
			SocketServer.getInstance().removePla(playerid, reason);
			//更新用户信息
			SqlString userSqlStr = new SqlString();
			userSqlStr.add("serverid", 0);
			userSqlStr.add("playerid", 0);
			UserBAC.getInstance().update(dbHelper, plaRs.getInt("userid"), userSqlStr);
			
			gl.addRemark("注销原因:"+reason);
			gl.save();
		}
	}
	
	/**
	 * 获取角色和所有伙伴的数据(获取其他角色数据)
	 */
	public JSONObject getAllData(DBHelper dbHelper, int playerid) throws Exception{
		return getAllData(dbHelper, playerid, true, false, false, false);
	}
	
	/**
	 * 获取角色和所有伙伴的数据
	 * @param deputy 副数据(后台查看角色信息包含)
	 * @param acti 活动数据
	 * @param info 消息数据
	 */
	public JSONObject getAllData(DBHelper dbHelper, int playerid, boolean interactive, boolean deputy, boolean acti, boolean info) throws Exception{
		JSONObject json_data = new JSONObject();
		//玩家数据
		JSONArray plaarr = getData(playerid);
		json_data.put("player", plaarr);
		/*--交互副数据--*/
		if(interactive){
			//角色数据
			JSONArray plarolearr = PlaRoleBAC.getInstance().getLoginData(playerid);
			json_data.put("plarole", plarolearr);
			//物品数据
			JSONArray itemarr = ItemBAC.getInstance().getItemList(playerid);
			json_data.put("item", itemarr);
			//角色帮派
			JSONArray plafacarr = PlaFacBAC.getInstance().getLoginData(playerid);
			json_data.put("plafac", plafacarr);
			//帮派
			JSONArray facarr = FactionBAC.getInstance().getLoginData(playerid);
			json_data.put("fac", facarr);
			//伙伴
			JSONArray partnerarr = PartnerBAC.getInstance().getLoginData(playerid);
			json_data.put("partner", partnerarr);
		}
		/*--个人副数据--*/
		if(deputy){
			//好友
			JSONArray friarr = FriendBAC.getInstance().getLoginData(playerid);
			json_data.put("friend", friarr);
			//副本
			JSONObject copymap = CopymapBAC.getInstance().getLoginData(playerid);
			json_data.put("copymap", copymap);
			//角色福利
			JSONArray welfare = PlaWelfareBAC.getInstance().getData(playerid);
			json_data.put("plawelfare", welfare);
			//角色补给
			JSONArray plasupplyarr = PlaSupplyBAC.getInstance().getLoginData(playerid);
			json_data.put("plasupply", plasupplyarr);
			//角色神秘商店
		//	JSONArray plamysarr = PlaMysteryShopBAC.getInstance().getData(playerid);
		//	json_data.put("plamys", plamysarr);
			//角色铜钱试炼
			JSONArray plamoney = PlaTrialMoneyBAC.getInstance().getData(playerid);
			json_data.put("plamoney", plamoney);
			//角色经验试炼
			JSONArray plaexp = PlaTrialExpBAC.getInstance().getData(playerid);
			json_data.put("plaexp", plaexp);
			//角色伙伴试炼
			JSONObject plapartner = PlaTrialPartnerBAC.getInstance().getData(playerid);
			json_data.put("plapartner", plapartner);
			//角色召唤
			JSONArray plasummon = PlaSummonBAC.getInstance().getData(playerid);
			json_data.put("plasummon", plasummon);
			//角色轮回塔
			JSONArray platower = PlaTowerBAC.getInstance().getData(playerid);
			json_data.put("platower", platower);
		}
		/*--活动数据--*/
		if(acti){
			//自定义活动
			JSONArray customactiarr = CustomActivityBAC.getInstance().getLoginData(playerid);
			json_data.put("costomacti", customactiarr);
			//世界BOSS
			JSONArray worldboss = WorldBossBAC.getInstance().getLoginData();
			json_data.put("worldboss", worldboss);
			//组队活动
			JSONArray team = PlaTeamBAC.getInstance().getLoginData();
			json_data.put("team", team);
		}
		/*--消息数据--*/
		if(info){
			JSONArray sysarr = getSysData();
			json_data.put("sys", sysarr);
			JSONArray mailarr = MailBAC.getInstance().getMailList(dbHelper, playerid);
			json_data.put("mail", mailarr);
		}
		return json_data;
	}
	
	/**
	 * 获取玩家数据
	 */
	public JSONArray getData(int playerid) throws Exception {
		JSONArray arr = new JSONArray();
		DBPaRs rs = getDataRs(playerid);
		arr.add(rs.getInt("id"));//0.ID
		arr.add(rs.getInt("num"));//1.编号
		arr.add(rs.getInt("onlinetime"));//2.在线时长
		arr.add(rs.getString("name"));//3.名称
		arr.add(rs.getInt("money"));//5.铜钱
		arr.add(rs.getInt("coin"));//6.金锭
		arr.add(rs.getInt("lv"));//9.等级
		arr.add(rs.getInt("exp"));//10.当前经验
		arr.add(rs.getInt("vip"));//11.VIP
		arr.add(rs.getInt("buycoin"));//12.购买金锭总数
		arr.add(rs.getInt("rebatecoin"));//13.赠送金锭总数
		arr.add(rs.getInt("tqnum"));//14.特权编号
		arr.add(rs.getTime("tqduetime"));//15.特权到期时间
		arr.add(rs.getInt("rechargermb"));//16.充值RMB总额
		arr.add(new JSONArray(rs.getString("openfunc")));//17.已开启功能JSONARR
		arr.add(rs.getTime("bannedmsgtime"));//18.禁言截至时间
		arr.add(new JSONArray(rs.getString("rechargtypes")));//19.已购买过的金额
		arr.add(rs.getTime("savetime"));//角色创建时间
		return arr;
	}
	
	/**
	 * 获取系统数据
	 */
	public JSONArray getSysData() throws Exception {
		JSONArray arr = new JSONArray();
		arr.add(System.currentTimeMillis());//系统时间
		arr.add(ServerBAC.getInstance().getOpenTime());//开服时间
		arr.add(Conf.worldLevel);//世界等级
		return arr;
	}
	
	/**
	 * 检查与重置日常数据
	 */
	public JSONObject checkAndResetDayData(DBHelper dbHelper, int playerid, boolean must, boolean firstlogin, GameLog gl) throws Exception {
		JSONObject returnobj = new JSONObject();
		DBPaRs plaRs = getDataRs(playerid);
		long resetdate = plaRs.getTime("resetdate");
		boolean needreset = must || MyTools.checkSysTimeBeyondSqlDate(resetdate);
		if(needreset){
			//重置角色数据
			SqlString sqlStr = new SqlString();
			sqlStr.add("onlinetime", 0);
			sqlStr.addDateTime("resetdate", MyTools.getDaiylResetTime(5));
			update(dbHelper, playerid, sqlStr);
			long starttime = resetdate-MyTools.long_day;
			long endtime = System.currentTimeMillis();
			boolean weekReset = MyTools.checkWeek(starttime, endtime);
			boolean moonReset = MyTools.checkMonth(starttime, endtime);
			//重置角色帮派数据
			PlaFacBAC.getInstance().resetData(dbHelper, playerid, resetdate);
			//重置帮派日常数据
			FactionBAC.getInstance().checkAndResetDayData(dbHelper, playerid, must);
			returnobj.put("result", 1);
			//重置排行竞技数据
			PlaJJCRankingBAC.getInstance().resetData(dbHelper, playerid);
			//重置副本每日数据
			CopymapBAC.getInstance().resetData(dbHelper, playerid);
			//重置角色福利数据
			PlaWelfareBAC.getInstance().resetData(dbHelper, playerid, moonReset);
			//重置补给数据
			PlaSupplyBAC.getInstance().resetData(dbHelper, playerid);
			//重置每日召唤次数
			PlaSummonBAC.getInstance().resetData(dbHelper, playerid);
			//重置每日轮回塔数据
			PlaTowerBAC.getInstance().resetData(dbHelper, playerid, returnobj);
			//重置
			PlaRoleBAC.getInstance().resetData(dbHelper, playerid);
			//重置商店数据
			PlaShopBAC.getInstance().resetData(dbHelper, playerid);
		} else {
			returnobj.put("result", 0);
			returnobj.put("time", System.currentTimeMillis());
		}
		if(needreset || firstlogin){
			//抽象活动
			CustomActivityBAC.getInstance().supplement(dbHelper, playerid, plaRs.getString("channel"));
			//抽象活动登录天数
			CustomActivityBAC.getInstance().updateProcess(dbHelper, playerid, 6);
		}
		return returnobj;
	}
	
	/**
	 * 还原在线角色
	 */
	public void restoreOnLinePla(){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet plaRs = dbHelper.query("tab_player", "id,sessionid,userid,name,savetime,openfunc", "serverid="+Conf.sid+" and onlinestate=1");
			while(plaRs.next()){
				Player pla = new Player(plaRs.getString("sessionid"), plaRs.getInt("userid"), plaRs.getInt("id"), plaRs.getString("name"), MyTools.getTimeLong(plaRs.getTimestamp("savetime")), new JSONArray(plaRs.getString("openfunc")));
				SocketServer.getInstance().plamap.put(pla.pid, pla);
				int old_amount = SocketServer.getInstance().session_plamap.size();
				SocketServer.getInstance().session_plamap.put(pla.sessionid, pla);
				pla.startBreakLineTT(MyTools.long_minu*5, "还原在线");
				SocketServer.getInstance().connectlog.d("加入角色：" + pla.pname + "("+ pla.pid + "," + pla.sessionid +")" + " 人数：" + old_amount + " -> " + SocketServer.getInstance().session_plamap.size());
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 使用铜钱
	 */
	public void useMoney(DBHelper dbHelper, int playerid, int useamount, GameLog gl) throws Exception {
		useMoney(dbHelper, playerid, useamount, true, true, gl);
	}
	
	/**
	 * 使用铜钱
	 */
	public void useMoney(DBHelper dbHelper, int playerid, int useamount, boolean sys_change, boolean ca_change, GameLog gl) throws Exception {
		if(useamount <= 0){
			String str = "减少值失败 修改项：铜钱 减少值：" + useamount;
			try {
				throw new Exception(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			BACException.throwInstance(str);
		}
		DBPaRs plaRs = getDataRs(playerid);
		int money = plaRs.getInt("money");
		if(money < useamount){
			BACException.throwInstance("铜钱不足("+money+"/"+useamount+")");
		}
		SqlString sqlStr = new SqlString();
		sqlStr.addChange("money", -useamount);
		update(dbHelper, playerid, sqlStr);
		
		CustomActivityBAC.getInstance().updateProcess(dbHelper, playerid, 9, useamount);
		
		gl.addChaNote(GameLog.TYPE_MONEY, money, -useamount, sys_change);
	}
	
	/**
	 * 使用金锭
	 * @param useamount 使用数量
	 */
	public void useCoin(DBHelper dbHelper, int playerid, int useamount, GameLog gl) throws Exception {
		useCoin(dbHelper, playerid, useamount, true, true, gl);
	}
	
	/**
	 * 使用金锭
	 * @param useamount 使用数量
	 */
	public void useCoin(DBHelper dbHelper, int playerid, int useamount, boolean sys_change, boolean ca_change, GameLog gl) throws Exception {
		if(useamount <= 0){
			String str = "减少值失败 修改项：金锭 减少值：" + useamount;
			try {
				throw new Exception(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			BACException.throwInstance(str);
		}
		DBPaRs plaRs = getDataRs(playerid);
		int coin = plaRs.getInt("coin");
		if(coin < useamount){
			BACException.throwInstance("金锭不足("+coin+"/"+useamount+")");
		}
		SqlString sqlStr = new SqlString();
		sqlStr.addChange("coin", -useamount);
		update(dbHelper, playerid, sqlStr);
		
		CustomActivityBAC.getInstance().updateProcess(dbHelper, playerid, 10, useamount);
		
		gl.addChaNote(GameLog.TYPE_COIN, coin, -(coin>=useamount?useamount:coin), sys_change);
	}
	
	/**
	 * 使用金锭检查
	 */
	public void useCoinCheck(int playerid, int useamount) throws Exception {
		if(useamount <= 0){
			String str = "减少值失败 修改项：金锭 减少值：" + useamount;
			try {
				throw new Exception(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			BACException.throwInstance(str);
		}
		DBPaRs plaRs = getDataRs(playerid);
		int coin = plaRs.getInt("coin");
		if(coin < useamount){
			BACException.throwInstance("金锭不足("+coin+"/"+useamount+")");
		}
	}
	
	//--------------静态区--------------
	
	private static PlayerBAC instance = new PlayerBAC();
	
	/**
	 * 获取实例
	 */
	public static PlayerBAC getInstance(){
		return instance;
	}
}
