package com.moonic.bac;

import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.mgr.LockStor;
import com.moonic.mirror.MirrorOne;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPRs;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 帮派
 * @author John
 */
public class FactionBAC extends MirrorOne {
	public static final String tab_faction_lv = "tab_faction_lv";
	public static final String tab_faction_technology = "tab_faction_technology";
	public static final String tab_faction_welfare = "tab_faction_welfare";
	public static final String tab_faction_worship = "tab_faction_worship";
	
	/**
	 * 构造
	 */
	public FactionBAC(){
		super("tab_faction_stor", "id");
		haveServerWhere = true;
	}
	
	/**
	 * 创建帮派
	 */
	public ReturnValue create(int playerid, String name){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FACTION_NAME)) {
				MyTools.checkNoCharEx(name, '#');
				if(name.toLowerCase().equals("null")){
					BACException.throwInstance("名字不可用，请更改后重试");
				}
				DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
				if(plafacRs.getInt("factionid") != 0){
					BACException.throwInstance("你已在帮派中，不可创建帮派");
				}
				DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
				DBPsRs facRs = ServerFacBAC.getInstance().query(Conf.sid, "serverid="+Conf.sid+" and name='"+name+"'");
				if(facRs.next()){
					BACException.throwInstance("帮派名已存在");
				}
				PlaFacBAC.getInstance().intoCheck(plafacRs);
				dbHelper.openConnection();
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_CREATE);
				PlayerBAC.getInstance().useCoin(dbHelper, playerid, 100, gl);
				SqlString sqlStr = new SqlString();
				sqlStr.add("playerid", playerid);
				sqlStr.add("pname", plaRs.getString("name"));
				sqlStr.add("serverid", Conf.sid);
				sqlStr.add("name", name);
				sqlStr.add("lv", 1);
				sqlStr.add("money", 0);
				sqlStr.add("declaream", 0);
				sqlStr.addDateTime("resetdate", MyTools.getDaiylResetTime(5));
				sqlStr.add("technology", (new JSONObject()).toString());
				sqlStr.add("cmpassdata", (new JSONArray()).toString());
				sqlStr.add("cmdata", (new JSONObject()).toString());
				sqlStr.add("occupyselfcity", 0);
				int factionid = insertByAutoID(dbHelper, sqlStr);
				PlaFacBAC.getInstance().intoFaction(dbHelper, playerid, factionid, name, 2);
				PlaFacApplyBAC.getInstance().clearAllApply(dbHelper, playerid, 0);//通知清除所有申请
				ServerFacBAC.getInstance().updateFactionRanking();
				
				gl.addObtain("创建帮派：" + GameLog.formatNameID(name, factionid))
				.save();
				
				MailBAC.getInstance().sendModelMail(dbHelper, new int[]{playerid}, 3, new Object[]{name}, new Object[]{name});
				
				JSONArray returnarr = new JSONArray();
				returnarr.add(factionid);//帮派ID
				return new ReturnValue(true, returnarr.toString());
			}
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 设置入帮条件
	 */
	public ReturnValue setJoinCond(int playerid, String joincond){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			if(plafacRs.getInt("position") == 0){
				BACException.throwInstance("权限不够");
			}
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.add("joincond", joincond);
			update(dbHelper, factionid, sqlStr);
			
			JSONArray pusharr = new JSONArray();
			pusharr.add(playerid);
			pusharr.add(joincond);
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_JOINCOND, pusharr.toString(), factionid, playerid);
			
			GameLog.getInst(playerid, GameServlet.ACT_FACTION_UPD_INFO)
			.addRemark("更新入帮条件：" + joincond)
			.save();
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 加入帮派
	 */
	public ReturnValue join(int playerid, int factionid){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FACTION_MEMBER, factionid)) {
				DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
				if(plafacRs.getInt("factionid") != 0){
					BACException.throwInstance("你已在帮派中，不可加入其他帮派");
				}
				DBPaRs facRs = getDataRs(factionid);
				if(facRs.getInt("serverid")!=Conf.sid){
					BACException.throwInstance("帮派不存在");
				}
				int curamount = FacMemBAC.getInstance().getAmount(factionid);
				int maxamount = getTechnologyFunc(facRs, 1, 0);
				if(curamount >= maxamount){
					BACException.throwInstance("此帮派人数已满，无法加入");
				}
				DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
				boolean needApply = false;
				int[][] joincond = Tools.splitStrToIntArr2(facRs.getString("joincond"), "|", ",");
				for(int i = 0; joincond != null && i < joincond.length; i++){
					if(joincond[i][0] == 1){// 等级条件
						if (joincond[i][1] > plaRs.getInt("lv")) {
							BACException.throwInstance("等级不足");
						}
					} else if (joincond[i][0] == 4) {// 是否需要申请
						if (joincond[i][1] == 1) {
							needApply = true;
						}
					}
				}
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_JOIN);
				PlaFacBAC.getInstance().intoCheck(plafacRs);
				
				JSONArray returnarr = new JSONArray();
				if (needApply) {
					DBPsRs plaAppRs = PlaFacApplyBAC.getInstance().query(playerid, "playerid="+playerid+" and factionid="+factionid);
					if(plaAppRs.next()){
						BACException.throwInstance("你已申请过此帮派，请等待回复");
					}
					int applyAm = PlaFacApplyBAC.getInstance().getAmount(playerid);
					if(applyAm >= 3){
						BACException.throwInstance("申请数已满，请撤销其他申请后再尝试");
					}
					int applyPlaAm = FacApplyBAC.getInstance().getAmount(factionid);
					if(applyPlaAm >= 30){
						BACException.throwInstance("此帮派申请人数已满，请尝试申请其他帮派");
					}
					SqlString sqlStr = new SqlString();
					sqlStr.add("playerid", playerid);
					sqlStr.add("factionid", factionid);
					sqlStr.addDateTime("applytime", MyTools.getTimeStr());
					PlaFacApplyBAC.getInstance().insert(dbHelper, playerid, sqlStr);
					DBPsRs applyRs = PlaFacApplyBAC.getInstance().query(playerid, "playerid="+playerid+" and factionid="+factionid);
					applyRs.next();
					JSONArray jsonarr = PlaFacApplyBAC.getInstance().getApplyData(applyRs);
					PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_APPLY, jsonarr.toString(), factionid, 0);
				} else {
					returnarr = intoFaction(dbHelper, playerid, factionid, facRs, new JSONArray(), 0 , SocketServer.ACT_FACTION_JOIN);
				}
				
				gl.addRemark("加入帮派：" + GameLog.formatNameID(facRs.getString("name"), factionid))
				.save();
				return new ReturnValue(true, returnarr.toString());
			}
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}

	/**
	 * 修改帮会公告
	 */
	public ReturnValue updInfo(int playerid, String info){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			if(plafacRs.getInt("position") == 0){
				BACException.throwInstance("权限不够");
			}
			if(info.length() > 90){
				info = info.substring(0, 90);
			}
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.add("innote", info);
			update(dbHelper, factionid, sqlStr);
			
			JSONArray pusharr = new JSONArray();
			pusharr.add(playerid);
			pusharr.add(info);
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_UPD_INFO, pusharr.toString(), factionid, playerid);
			
			GameLog.getInst(playerid, GameServlet.ACT_FACTION_UPD_INFO)
			.addRemark("更新帮会公告：" + info)
			.save();
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调整职位
	 * @param position 2.族长 1.副族长 0.族员
	 */
	public ReturnValue adjustPosition(int playerid, int memberid, byte position){
		DBHelper dbHelper = new DBHelper();
		try {
			if(position != 0 && position != 1){
				BACException.throwInstance("错误的职位");
			}
			if(playerid == memberid){
				BACException.throwInstance("不能调整自己的职位");
			}
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			DBPaRs facRs = getDataRs(factionid);
			if(plafacRs.getInt("position") != 2){
				BACException.throwInstance("权限不够");
			}
			DBPaRs memFacRs = PlaFacBAC.getInstance().getDataRs(memberid);
			if(memFacRs.getInt("factionid") != factionid){
				BACException.throwInstance("不是帮派成员");
			}
			if(memFacRs.getInt("position") == position){
				BACException.throwInstance("已经是此职位");
			}
			checkAllowChangePersonnel(3);//检查是否允许人事变动
			if(position == 1){
				DBPsRs facmemRs = FacMemBAC.getInstance().query(factionid, "factionid="+factionid+" and position=1");
				if(facmemRs.count() >= 2){
					BACException.throwInstance("最多只能有两个副帮主");
				}
			}
			DBPaRs memRs = PlayerBAC.getInstance().getDataRs(memberid);
			dbHelper.openConnection();
			PlaFacBAC.getInstance().setPosition(dbHelper, memberid, position);
			
			JSONArray pusharr = new JSONArray();
			pusharr.add(memberid);
			pusharr.add(position);
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_ADJUSET_POSITION, pusharr.toString(), factionid, playerid);
			
			StringBuffer sbRemark = new StringBuffer();
			sbRemark
			.append("帮派：")
			.append(GameLog.formatNameID(facRs.getString("name"), factionid))
			.append(" 将 ")
			.append(GameLog.formatNameID(memRs.getString("name"), memberid))
			.append(" 从 ")
			.append(position_name[memFacRs.getInt("position")])
			.append(" 调整为  ")
			.append(position_name[position]);
			GameLog.getInst(playerid, GameServlet.ACT_FACTION_ADJUSET_POSITION, factionid)
			.addRemark(sbRemark)
			.save();
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	public static final String[] position_name = {"帮众", "副帮主", "帮主"};
	
	/**
	 * 移除族员
	 */
	public ReturnValue removeMember(int playerid, String memberidStr){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FACTION_MEMBER)) {
				if(memberidStr == null || memberidStr.equals("")){
					BACException.throwInstance("要移除的成员清单为空");
				}
				int[] memberids = Tools.splitStrToIntArr(memberidStr, ",");
				if(Tools.intArrContain(memberids, playerid)){
					BACException.throwInstance("不能移除自己");
				}
				DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
				int factionid = plafacRs.getInt("factionid");
				if(factionid == 0){
					BACException.throwInstance("尚未加入帮派");
				}
				int position = plafacRs.getInt("position");
				if(position == 0){
					BACException.throwInstance("权限不够");
				}
				DBPsRs facmemRs = FacMemBAC.getInstance().query(factionid, "factionid="+factionid);
				int[] pids = new int[facmemRs.count()];
				while(facmemRs.next()){
					int memid = facmemRs.getInt("playerid");
					if(Tools.intArrContain(memberids, memid) && position <= facmemRs.getInt("position")){
						BACException.throwInstance("权限不够");
					}
					pids[facmemRs.getRow()-1] = memid;
				}
				for(int i = 0; i < memberids.length; i++){
					if(!Tools.intArrContain(pids, memberids[i])){
						BACException.throwInstance(memberids[i]+"不是帮派成员");
					}
				}
				checkAllowChangePersonnel(2);//检查是否允许人事变动
				dbHelper.openConnection();
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_REMOVE_MEMBER, factionid);
				StringBuffer sb1 = new StringBuffer();
				StringBuffer sb2 = new StringBuffer();
				for(int i = 0; i < memberids.length; i++){
					DBPaRs memRs = PlayerBAC.getInstance().getDataRs(memberids[i]);
					PlaFacBAC.getInstance().exitFaction(dbHelper, factionid, memberids[i], gl);
					if(sb1.length() > 0){
						sb1.append("、");
						sb2.append("、");
					}
					sb1.append(memRs.getString("name"));
					sb2.append(GameLog.formatNameID(memRs.getString("name"), memberids[i]));
				}
				
				JSONArray pusharr_2 = new JSONArray();
				pusharr_2.add(playerid);//踢人者角色ID
				pusharr_2.add((new JSONArray(memberids)));//被踢族员
				PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_REMOVE_MEMBER, pusharr_2.toString(), factionid, playerid);
				DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
				JSONArray pusharr = new JSONArray();
				pusharr.add(factionid);//帮派ID
				pusharr.add(plafacRs.getString("facname"));//帮派名
				pusharr.add(playerid);//踢人者角色ID
				pusharr.add(plaRs.getString("name"));//踢人者角色名
				PushData.getInstance().sendPlaToSome(SocketServer.ACT_FACTION_BEREMOVE, pusharr.toString(), memberids);
				
				gl.addRemark("帮派："+GameLog.formatNameID(plafacRs.getString("facname"), factionid)+"将："+sb2.toString()+"踢出")
				.save();
				return new ReturnValue(true);
			}
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 注销帮派
	 */
	public void dissmiss(DBHelper dbHelper, int factionid) throws Exception {
		delete(dbHelper, factionid, "id="+factionid);//注销帮派
		CBBAC.getInstance().giveupCity(dbHelper, factionid);
		CBTeamPoolBAC.getInstance().clearTeam(dbHelper, factionid);
		ServerFacBAC.getInstance().updateFactionRanking();
	}
	
	/**
	 * 退出帮派
	 */
	public ReturnValue exitFaction(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FACTION_MEMBER)) {
				if (CBBAC.getInstance().isInCb(playerid)) {
					BACException.throwInstance("已参加城战，无法退出帮派");
				}
				DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
				int factionid = plafacRs.getInt("factionid");
				if(factionid == 0){
					BACException.throwInstance("尚未加入帮派");
				}
				checkAllowChangePersonnel(2);//检查是否允许人事变动
				int position = plafacRs.getInt("position");
				/*
				if(position == 2){
					BACException.throwInstance("族长不可退出帮派");
				}
				*/
				String facname = plafacRs.getString("facname");
				dbHelper.openConnection();
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_EXIT, factionid);
				PlaFacBAC.getInstance().exitFaction(dbHelper, factionid, playerid, gl);
				int memberid = 0;
				if(position == 2){
					DBPsRs facmemRs = FacMemBAC.getInstance().query(factionid, "factionid="+factionid, "position desc");
					if(facmemRs.next()){
						memberid = facmemRs.getInt("playerid");
						PlaFacBAC.getInstance().setPosition(dbHelper, memberid, 2);
						SqlString sqlStr = new SqlString();
						sqlStr.add("playerid", memberid);
						sqlStr.add("pname", PlayerBAC.getInstance().getStrValue(memberid, "name"));
						update(dbHelper, factionid, sqlStr);
						CBTeamPoolBAC.getInstance().clearTeam(dbHelper, playerid, factionid);
					} else {
						dissmiss(dbHelper, factionid);//注销帮派
						memberid = -1;
						// 帮派解散，所有的申请者
						FacApplyBAC.getInstance().clearAllApplyer(dbHelper, playerid, PlayerBAC.getInstance().getStrValue(playerid, "name"), factionid);
					}
				}
				
				if(memberid != -1){
					JSONArray pusharr = new JSONArray();
					pusharr.add(playerid);
					if(memberid > 0){
						pusharr.add(memberid);
					}
					PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_EXIT, pusharr.toString(), factionid, playerid);
				}
				
				gl.addRemark("退出帮派："+GameLog.formatNameID(facname, factionid));
				if(memberid > 0){
					String mname = PlayerBAC.getInstance().getStrValue(memberid, "name");
					gl.addRemark("，族长被 "+GameLog.formatNameID(mname, memberid)+" 继承");
				} else 
				if(memberid == -1){
					gl.addRemark("无其他帮派成员，帮派自动解散");
				}
				gl.save();
				return new ReturnValue(true);
			}
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 族长禅让
	 */
	public ReturnValue shanrang(int playerid, int memberid){
		DBHelper dbHelper = new DBHelper();
		try {
			if(playerid == memberid){
				BACException.throwInstance("不能禅让给自己");
			}
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			if(plafacRs.getInt("position") != 2){
				BACException.throwInstance("只有族长可以禅让");
			}
			DBPaRs memfacRs = PlaFacBAC.getInstance().getDataRs(memberid);
			if(memfacRs.getInt("factionid") != factionid){
				BACException.throwInstance("被禅让者不是帮派成员");
			}
			checkAllowChangePersonnel(3);//检查是否允许人事变动
			dbHelper.openConnection();
			PlaFacBAC.getInstance().setPosition(dbHelper, playerid, 0);
			PlaFacBAC.getInstance().setPosition(dbHelper, memberid, 2);
			DBPaRs memRs = PlayerBAC.getInstance().getDataRs(memberid);
			SqlString sqlStr = new SqlString();
			sqlStr.add("playerid", memberid);
			sqlStr.add("pname", memRs.getString("name"));
			update(dbHelper, factionid, sqlStr);
			
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_SHANRANG, String.valueOf(memberid), factionid, playerid);
			String mname = PlayerBAC.getInstance().getStrValue(memberid, "name");
			
			StringBuffer sbRemark = new StringBuffer();
			sbRemark
			.append("帮派：")
			.append(GameLog.formatNameID(plafacRs.getString("facname"), factionid))
			.append("，禅让给：")
			.append(GameLog.formatNameID(mname, memberid));
			GameLog.getInst(playerid, GameServlet.ACT_FACTION_SHANRANG, factionid)
			.addRemark(sbRemark)
			.save();
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 弹劾族长
	 */
	public ReturnValue impeach(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			if(plafacRs.getInt("position")==2){
				BACException.throwInstance("不能弹劾自己");
			}
			DBPaRs facRs = getDataRs(factionid);
			DBPaRs headRs = PlayerBAC.getInstance().getDataRs(facRs.getInt("playerid"));
			if(System.currentTimeMillis()-headRs.getTime("logintime") < MyTools.long_day*3){
				BACException.throwInstance("族长三天不上线才允许被弹劾");
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTOIN_IMPEACH, factionid);
			PlayerBAC.getInstance().useCoin(dbHelper, playerid, 100, gl);
			PlaFacBAC.getInstance().setPosition(dbHelper, playerid, 2);
			PlaFacBAC.getInstance().setPosition(dbHelper, headRs.getInt("id"), 0);
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			SqlString sqlStr = new SqlString();
			sqlStr.add("playerid", playerid);
			sqlStr.add("pname", plaRs.getString("name"));
			update(dbHelper, factionid, sqlStr);
			
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_IMPEACH, ""+playerid, factionid, playerid);
			
			StringBuffer sbRemark = new StringBuffer();
			sbRemark
			.append("帮派：")
			.append(GameLog.formatNameID(plafacRs.getString("facname"), factionid))
			.append("，弹劾族长：")
			.append(GameLog.formatNameID(headRs.getString("name"), headRs.getInt("id")));
			gl.addRemark(sbRemark)
			.save();
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取指定编号的帮派信息
	 */
	public ReturnValue getFactionData(int playerid, int factionid){
		try {
			JSONArray facarr = getAllData(factionid, true, false);
			return new ReturnValue(true, facarr.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 改名
	 */
	public ReturnValue rename(int playerid, String newName){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FACTION_NAME)) {
				MyTools.checkNoCharEx(newName, '#');
				if(newName.toLowerCase().equals("null")){
					BACException.throwInstance("名字不可用，请更改后重试");
				}
				DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
				int factionid = plafacRs.getInt("factionid");
				if(factionid == 0){
					BACException.throwInstance("尚未加入帮派");
				}
				DBPaRs facRs2 = getDataRs(factionid);
				String oldName = facRs2.getString("name");
				if(!oldName.contains("#")){
					BACException.throwInstance("无改名资格");
				}
				DBPsRs facRs = ServerFacBAC.getInstance().query(Conf.sid, "serverid="+Conf.sid+" and name='"+newName+"'");
				if(facRs.next()){
					BACException.throwInstance("帮派名已存在");
				}
				SqlString sqlStr = new SqlString();
				sqlStr.add("name", newName);
				update(dbHelper, factionid, sqlStr);//帮派
				SqlString facmemSqlStr = new SqlString();
				facmemSqlStr.add("facname", newName);
				FacMemBAC.getInstance().update(dbHelper, factionid, facmemSqlStr, "factionid="+factionid);//成员
				
				PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_RENAME, newName, factionid, playerid);
				
				GameLog.getInst(playerid, GameServlet.ACT_FACTION_RENAME)
				.addRemark("由"+oldName+"改名为"+newName)
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
	 * 帮派升级
	 */
	public ReturnValue upLevel(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			if(plafacRs.getInt("position") == 0){
				BACException.throwInstance("无操作权限");
			}
			DBPaRs facRs = getDataRs(factionid);
			int faclv = facRs.getInt("lv");
			DBPaRs lvRs = DBPool.getInst().pQueryA(tab_faction_lv, "lv="+faclv);
			int needmoney = lvRs.getInt("needmoney");
			if(needmoney == -1){
				BACException.throwInstance("帮派已达满级");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_UPLEVEL, factionid);
			FactionBAC.getInstance().changeMoney(dbHelper, factionid, -needmoney, gl);
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("lv", 1);
			update(dbHelper, factionid, sqlStr);
			JSONArray pusharr = new JSONArray();//推送
			pusharr.add(playerid);
			pusharr.add(faclv+1);
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_UPLEVEL, pusharr.toString(), factionid, 0);
			
			gl.addChaNote("帮派等级", faclv, 1)
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
	 * 升帮派科技
	 */
	public ReturnValue upTechnology(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs technologyRs = DBPool.getInst().pQueryA(tab_faction_technology, "num="+num);
			if(!technologyRs.exist()){
				BACException.throwInstance("科技编号不存在 num="+num);
			}
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			int position = plafacRs.getInt("position");
			if(position == 0){
				BACException.throwInstance("权限不够");
			}
			DBPaRs facRs = getDataRs(factionid);
			JSONObject techobj = new JSONObject(facRs.getString("technology"));
			int currlv = techobj.optInt(String.valueOf(num));
			if(currlv == 0){
				currlv = 1;//科技等级初始为1级
			}
			if(currlv >= technologyRs.getInt("maxlv")){
				BACException.throwInstance("此科技已升至顶级");
			}
			if(!technologyRs.getString("needfaclv").equals("-1")){
				int[] needlv = Tools.splitStrToIntArr(technologyRs.getString("needfaclv"), ",");
				if(facRs.getInt("lv") < needlv[currlv-1]){
					BACException.throwInstance("帮派等级不足，无法升级");
				}	
			}
			int[] needmoney = Tools.splitStrToIntArr(technologyRs.getString("needfacmoney"), ",");
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_UPTECHNOLOGE, factionid);
			FactionBAC.getInstance().changeMoney(dbHelper, factionid, -needmoney[currlv-1], gl);
			techobj.put(String.valueOf(num), currlv+1);
			SqlString sqlStr = new SqlString();
			sqlStr.add("technology", techobj.toString());
			update(dbHelper, factionid, sqlStr);
			
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_UP_TECHNOLOGY, String.valueOf(num), factionid, 0);
			
			gl.addChaNote("科技("+technologyRs.getString("name")+")等级", currlv, 1);
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
	 * 获取帮派科技能力值
	 */
	public int getTechnologyFunc(DBPRs facRs, int num, int funcindex) throws Exception {
		int currlv = getTechnologyLv(facRs, num);
		DBPaRs technologyRs = DBPool.getInst().pQueryA(tab_faction_technology, "num="+num);
		int[][] func = Tools.splitStrToIntArr2(technologyRs.getString("func"), "|", ",");
		return func[currlv-1][funcindex];
	}
	
	/**
	 * 获取帮派科技等级
	 */
	public int getTechnologyLv(DBPRs facRs, int num) throws Exception {
		JSONObject techobj = new JSONObject(facRs.getString("technology"));
		int currlv = techobj.optInt(String.valueOf(num));
		if(currlv == 0){
			currlv = 1;//科技等级初始为1级
		}
		return currlv;
	}
	
	/**
	 * 领取帮派福利
	 */
	public ReturnValue getWelfare(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			if(plafacRs.getInt("getwelfare") == 1){
				BACException.throwInstance("今日已领取过帮派福利");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_GETWELFARE);
			DBPaRs facRs = getDataRs(factionid);
			int tochnologyLv = getTechnologyLv(facRs, 2);
			DBPaRs welfareRs = DBPool.getInst().pQueryA(tab_faction_welfare, "lv="+tochnologyLv);
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, welfareRs.getString("award"), ItemBAC.SHORTCUT_MAIL, 1, gl);
			SqlString sqlStr = new SqlString();
			sqlStr.add("getwelfare", 1);
			PlaFacBAC.getInstance().update(dbHelper, playerid, sqlStr);
			
			gl.save();
			return new ReturnValue(true, awardarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 膜拜
	 */
	public ReturnValue worship(int playerid, int worshippid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			if(playerid == worshippid){
				BACException.throwInstance("不能膜拜自己");
			}
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			DBPaRs worshipRs = DBPool.getInst().pQueryA(tab_faction_worship, "num="+num);
			if(!worshipRs.exist()){
				BACException.throwInstance("膜拜方式不存在 num="+num);
			}
			if(plafacRs.getInt("worship"+num) == 1){
				BACException.throwInstance("今天已进行过"+worshipRs.getString("name"));
			}
			DBPaRs plafacRs2 = PlaFacBAC.getInstance().getDataRs(worshippid);
			if(plafacRs2.getInt("factionid") != factionid){
				BACException.throwInstance("只能膜拜同一帮派的玩家");
			}
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			DBPaRs plaRs2 = PlayerBAC.getInstance().getDataRs(worshippid);
			if(plaRs.getInt("lv") > plaRs2.getInt("lv")){
				BACException.throwInstance("只能膜拜比自己等级高的玩家");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_WORSHIP);
			int[] consume = Tools.splitStrToIntArr(worshipRs.getString("consume"), ",");
			if(consume[0] == 1){
				PlayerBAC.getInstance().useMoney(dbHelper, playerid, consume[1], gl);
			} else 
			if(consume[0] == 2){
				PlayerBAC.getInstance().useCoin(dbHelper, playerid, consume[1], gl);
			}
			SqlString sqlStr1 = new SqlString();
			sqlStr1.add("worship"+num, 1);
			PlaFacBAC.getInstance().update(dbHelper, playerid, sqlStr1);
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, worshipRs.getString("waward"), ItemBAC.SHORTCUT_MAIL, 1, gl);
			JSONObject beworshipObj = new JSONObject(plafacRs2.getString("beworship"));
			beworshipObj.put(String.valueOf(num), beworshipObj.optInt(String.valueOf(num))+1);
			SqlString sqlStr = new SqlString();
			sqlStr.add("beworship", beworshipObj.toString());
			PlaFacBAC.getInstance().update(dbHelper, worshippid, sqlStr);
			
			PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_WORSHIP, gl);
			
			PushData.getInstance().sendPlaToOne(SocketServer.ACT_FACTION_WORSHIP, String.valueOf(num), worshippid);
			
			gl.addRemark("膜拜 "+plaRs2.getString("name"));
			gl.save();
			return new ReturnValue(true, awardarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 领取被膜拜奖励
	 */
	public ReturnValue getBeWorshipAward(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			JSONObject beworshipObj = new JSONObject(plafacRs.getString("beworship"));
			if(beworshipObj.length() == 0){
				BACException.throwInstance("无奖励可领");
			}
			DBPsRs worshipRs = DBPool.getInst().pQueryS(tab_faction_worship);
			StringBuffer sb = new StringBuffer();
			while(worshipRs.next()){
				int amount = beworshipObj.optInt(worshipRs.getString("num"));
				for(int i = 0; i < amount; i++){
					if(sb.length() > 0){
						sb.append("|");
					}
					sb.append(worshipRs.getString("bwaward"));
				}
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_GET_BEWORSHIPAWARD);
			SqlString sqlStr = new SqlString();
			sqlStr.add("beworship", (new JSONObject()).toString());
			PlaFacBAC.getInstance().update(dbHelper, playerid, sqlStr);
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, sb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
			
			gl.save();
			return new ReturnValue(true, awardarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 合服退帮派
	 */
	public ReturnValue mergerServerExitFactoin(){
		DBHelper dbHelper = new DBHelper();
		try {
			String del_where = " and lv<=15 and rechargermb=0 and logintime<"+MyTools.getDateSQL(MyTools.getCurrentDateLong()-MyTools.long_day*15);
			String sql = 
				"select a.*,b.factionid,b.facname from (" +
				"select id,lv,logintime,serverid,rechargermb from tab_player where serverid=" + Conf.sid + del_where + 
				") a left join tab_pla_faction b on a.id=b.playerid where b.factionid>0";
			ResultSet exitFacRs = dbHelper.executeQuery(sql);
			while(exitFacRs.next()){
				exitFaction(exitFacRs.getInt("id"));
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
	 * 申请加入帮派
	 */
	/*public ReturnValue applyJoin(int playerid, int factionid){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FACTION_MEMBER, factionid)) {
				DBPsRs plaAppRs = PlaFacApplyBAC.getInstance().query(playerid, "playerid="+playerid+" and factionid="+factionid);
				if(plaAppRs.next()){
					BACException.throwInstance("你已申请过此帮派，请等待回复");
				}
				DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
				if(plafacRs.getInt("factionid") != 0){
					BACException.throwInstance("你已在帮派中，不可申请加入其他帮派");
				}
				DBPaRs facRs = getDataRs(factionid);
				if(facRs.getInt("serverid")!=Conf.sid){
					BACException.throwInstance("帮派不存在");
				}
				int curamount = FacMemBAC.getInstance().getAmount(factionid);
				int maxamount = getTechnologyFunc(facRs, 1, 0);
				if(curamount >= maxamount){
					BACException.throwInstance("此帮派人数已满，无法加入");
				}
				int applyAm = PlaFacApplyBAC.getInstance().getAmount(playerid);
				if(applyAm >= 3){
					BACException.throwInstance("申请数已满，请撤销其他申请后再尝试");
				}
				int applyPlaAm = FacApplyBAC.getInstance().getAmount(factionid);
				if(applyPlaAm >= 30){
					BACException.throwInstance("此帮派申请人数已满，请尝试申请其他帮派");
				}
				PlaFacBAC.getInstance().intoCheck(plafacRs);
				dbHelper.openConnection();
				SqlString sqlStr = new SqlString();
				sqlStr.add("playerid", playerid);
				sqlStr.add("factionid", factionid);
				sqlStr.addDateTime("applytime", MyTools.getTimeStr());
				PlaFacApplyBAC.getInstance().insert(dbHelper, playerid, sqlStr);
				
				DBPsRs applyRs = PlaFacApplyBAC.getInstance().query(playerid, "playerid="+playerid+" and factionid="+factionid);
				applyRs.next();
				JSONArray jsonarr = PlaFacApplyBAC.getInstance().getApplyData(applyRs);
				PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_APPLY, jsonarr.toString(), factionid, 0);
				
				GameLog.getInst(playerid, GameServlet.ACT_FACTION_APPLY)
				.addRemark("申请加入帮派：" + GameLog.formatNameID(facRs.getString("name"), factionid))
				.save();
				return new ReturnValue(true);
			}
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}*/
	
	/**
	 * 撤销申请
	 */
	public ReturnValue revocationApply(int playerid, int factionid){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FACTION_MEMBER, factionid)) {
				DBPsRs plaappRs = PlaFacApplyBAC.getInstance().query(playerid, "playerid="+playerid+" and factionid="+factionid);
				if(!plaappRs.next()){
					BACException.throwInstance("未找到申请记录");
				}
				DBPaRs facRs = getDataRs(factionid);
				dbHelper.openConnection();
				PlaFacApplyBAC.getInstance().delete(dbHelper, playerid, "playerid="+playerid+" and factionid="+factionid);
				
				PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_REVOCATION_APPLY, String.valueOf(playerid), factionid, 0);
				
				GameLog.getInst(playerid, GameServlet.ACT_FACTION_REVOCATION_APPLY)
				.addRemark("撤销加入：" + GameLog.formatNameID(facRs.getString("name"), factionid) + "帮派的申请")
				.save();
				return new ReturnValue(true);
			}
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 处理申请
	 * @param way 0.同意 1.拒绝
	 */
	public ReturnValue processApply(int playerid, int applyid, byte way){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FACTION_MEMBER)) {
				if(way !=0 && way != 1){
					BACException.throwInstance("错误的处理方式");
				}
				DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
				int factionid = plafacRs.getInt("factionid");
				if(factionid == 0){
					BACException.throwInstance("尚未加入帮派");
				}
				if(plafacRs.getInt("position") == 0){
					BACException.throwInstance("权限不够");
				}
				DBPaRs facRs = getDataRs(factionid);
				DBPaRs applyerRs = PlayerBAC.getInstance().getDataRs(applyid);
				DBPsRs applyRs = PlaFacApplyBAC.getInstance().query(applyid, "playerid="+applyid+" and factionid="+factionid);
				if(!applyRs.next()){
					BACException.throwInstance("申请已被撤销");
				}
				JSONArray facInfoArray = new JSONArray();
				JSONArray meminfoarr = new JSONArray();
				DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
				dbHelper.openConnection();
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FACTION_PROCESS_APPLY);
				if(way == 0){
					int curamount = FacMemBAC.getInstance().getAmount(factionid);
					int maxamount = getTechnologyFunc(facRs, 1, 0);
					if(curamount >= maxamount){
						BACException.throwInstance("帮派人数已满");
					}
					DBPaRs appPlaFacRs = PlaFacBAC.getInstance().getDataRs(applyid);
					if(appPlaFacRs.getInt("factionid") != 0){
						BACException.throwInstance("对方已有帮派");
					}
					checkAllowChangePersonnel(1);//检查是否允许人事变动
					PlaFacApplyBAC.getInstance().delete(dbHelper, applyid, "playerid="+applyid+" and factionid="+factionid);
					PlaFacApplyBAC.getInstance().clearAllApply(dbHelper, applyid, factionid);
					if(maxamount - curamount == 1){
						FacApplyBAC.getInstance().clearAllApplyer(dbHelper, playerid, plaRs.getString("name"), factionid);
					}
					facInfoArray = intoFaction(dbHelper, applyid, factionid, facRs, meminfoarr, playerid, SocketServer.ACT_FACTION_AGREE_APPLY);
				} else {
					PlaFacApplyBAC.getInstance().delete(dbHelper, applyid, "playerid="+applyid+" and factionid="+factionid);
					PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_REFUSE_APPLY, String.valueOf(applyid), factionid, 0);
				}
				
				JSONArray jsonarr = new JSONArray();
				jsonarr.add(factionid);//帮派ID
				jsonarr.add(playerid);//处理者玩家ID
				jsonarr.add(plaRs.getString("name"));//处理者玩家名
				jsonarr.add(way);//处理方式
				if(way == 0){
					jsonarr.add(facInfoArray);//帮派详细信息
				}
				PushData.getInstance().sendPlaToOne(SocketServer.ACT_FACTION_PROCESS_APPLY, jsonarr.toString(), applyid);
				
				gl.addRemark((way==0?"同意":"拒绝") + "玩家：" + GameLog.formatNameID(applyerRs.getString("name"), applyid) + "加入帮派")
				.save();
				return new ReturnValue(true, meminfoarr.toString());
			}
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试改变值
	 */
	public ReturnValue debugChangeValue(int playerid, String column, long value, long min, long max, String logname) {
		try {
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			return super.debugChangeValue(plafacRs.getInt("factionid"), column, value, min, max, logname);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false ,e.toString());
		}
	}
	
	/**
	 * 调试加帮派资金
	 */
	public ReturnValue debugAddMoney(int playerid, int addmoney) throws Exception {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
			int factionid = plafacRs.getInt("factionid");
			if(factionid == 0){
				BACException.throwInstance("尚未加入帮派");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_DEBUG_GAME_LOG);
			changeMoney(dbHelper, factionid, addmoney, gl);
			
			gl.addRemark("调试加帮派资金");
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
	 * 检查是否允许人事变动
	 * @param type 1.进帮派 2.退帮派 3.调职位
	 */
	public void checkAllowChangePersonnel(int type) throws Exception {
		
	}
	
	/**
	 * 获取登录信息
	 */
	public JSONArray getLoginData(int playerid) throws Exception {
		DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
		int factionid = plafacRs.getInt("factionid");
		JSONArray facarr = new JSONArray();
		if(factionid != 0){
			facarr = FactionBAC.getInstance().getAllData(factionid, true, true);
		} else {
			facarr = PlaFacApplyBAC.getInstance().getApplyFacList(playerid);
		}
		return facarr;
	}
	
	/**
	 * 获取帮派详细信息
	 */
	public JSONArray getAllData(int factionid, boolean interactive, boolean deputy) throws Exception{
		JSONArray allarr = new JSONArray();
		DBPaRs facRs = getDataRs(factionid);
		//帮派信息
		JSONArray facarr = new JSONArray();
		facarr.add(facRs.getString("name"));//名称
		facarr.add(facRs.getInt("lv"));//等级
		facarr.add(facRs.getInt("money"));//帮派资金
		facarr.add(facRs.getString("joincond"));//入帮条件
		facarr.add(facRs.getString("innote"));//帮会公告
		facarr.add(new JSONObject(facRs.getString("technology")));//帮派科技
		facarr.add(new JSONArray(facRs.getString("cmpassdata")));//帮派已通关数据
		facarr.add(new JSONObject(facRs.getString("cmdata")));//帮派副本数据
		facarr.add(facRs.getInt("occupyselfcity"));//是否已占领自有城市
		facarr.add(facRs.getInt("declaream"));//宣战次数
		allarr.add(facarr);
		/*--交互副数据--*/
		if(interactive){
			//成员信息
			JSONArray memarr = FacMemBAC.getInstance().getFacMemData(factionid);
			allarr.add(memarr);//帮派成员信息
		}
		/*--私有副数据--*/
		if(deputy){
			//申请信息
			JSONArray applyarr = FacApplyBAC.getInstance().getApplyerData(factionid);
			allarr.add(applyarr);
		}
		return allarr;
	}
	
	/**
	 * 进入帮派
	 * @return
	 */
	private JSONArray intoFaction(DBHelper dbHelper, int playerid, int factionid, DBPaRs facRs, JSONArray meminfoarr, int applyid, short pushAct) throws Exception {
		PlaFacBAC.getInstance().intoFaction(dbHelper, playerid, factionid, facRs.getString("name"), 0);
		meminfoarr = PlaFacBAC.getInstance().getMemData(playerid, 0);//帮派成员基本信息
		PushData.getInstance().sendPlaToFacMem(pushAct, meminfoarr.toString(), factionid, playerid, applyid);		
		JSONArray returnarr = getAllData(factionid, true, false);		
		MailBAC.getInstance().sendModelMail(dbHelper, new int[]{playerid}, 2, new Object[]{facRs.getString("name")}, new Object[]{facRs.getString("name")});		
		return returnarr;
	}
	
	/**
	 * 检查与重置日常数据
	 */
	public boolean checkAndResetDayData(DBHelper dbHelper, int playerid, boolean must) throws Exception {
		DBPaRs plafacRs = PlaFacBAC.getInstance().getDataRs(playerid);
		int factionid = plafacRs.getInt("factionid");
		if(factionid == 0){
			return false;
		}
		synchronized (LockStor.getLock(LockStor.FACTION_RESET_DAYDATA, factionid)) {
			DBPaRs facRs = getDataRs(factionid);
			long resetdate = facRs.getTime("resetdate");
			boolean needreset = must || MyTools.checkSysTimeBeyondSqlDate(resetdate);
			if(needreset){
				JSONObject pushdata = new JSONObject();
				SqlString sqlStr = new SqlString();
				long starttime = resetdate-MyTools.long_day;
				long endtime = System.currentTimeMillis();
				if(MyTools.checkWeek(starttime, endtime)){//周重置数据
					
				}
				if(MyTools.checkMonth(starttime, endtime)){//月重置数据
					
				}
				sqlStr.add("declaream", 0);
				sqlStr.addDateTime("resetdate", MyTools.getDaiylResetTime(5));
				update(dbHelper, factionid, sqlStr);
				PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_RESETDATA, pushdata.toString(), factionid, 0);
			}
			return needreset;
		}
	}
	
	/**
	 * 获取帮派概要信息
	 */
	public JSONArray getInfo(DBPRs facRs) throws Exception {
		JSONArray arr = new JSONArray();
		arr.add(facRs.getInt("id"));//帮派ID
		arr.add(facRs.getString("name"));//帮派名
		arr.add(facRs.getString("pname"));//帮主名
		arr.add(facRs.getInt("lv"));//帮派等级
		arr.add(facRs.getString("joincond"));//入帮条件
		arr.add(FacMemBAC.getInstance().getAmount(facRs.getInt("id")));//当前人数
		arr.add(new JSONObject(facRs.getString("technology")));//帮派科技
		return arr;
	}
	
	/**
	 * 加帮派资金
	 */
	public void changeMoney(DBHelper dbHelper, int factionid, int changemoney, GameLog gl) throws Exception {
		synchronized (LockStor.getLock(LockStor.FACTION_ADDMONEY, factionid)) {
			DBPaRs facRs = getDataRs(factionid);
			int oldmoney = facRs.getInt("money");//原资金
			if(changemoney < 0 && oldmoney < -changemoney){
				BACException.throwInstance("帮派资金不足");
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("money", changemoney);
			update(dbHelper, factionid, sqlStr);
			PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_FACTION_CHANGEMONEY, String.valueOf(changemoney), factionid, 0);
			gl.addChaNote("帮派资金", oldmoney, changemoney);		
		}
	}
	
	//--------------静态区--------------
	
	private static FactionBAC instance = new FactionBAC();
	
	/**
	 * 获取实例
	 */
	public static FactionBAC getInstance(){
		return instance;
	}
}
