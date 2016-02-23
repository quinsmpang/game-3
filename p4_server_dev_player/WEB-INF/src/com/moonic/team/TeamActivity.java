package com.moonic.team;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import com.moonic.bac.AwardBAC;
import com.moonic.bac.Enemy;
import com.moonic.bac.ItemBAC;
import com.moonic.bac.PartnerBAC;
import com.moonic.bac.PlaTeamBAC;
import com.moonic.battle.BattleBox;
import com.moonic.battle.BattleManager;
import com.moonic.battle.Const;
import com.moonic.battle.TeamBox;
import com.moonic.gamelog.GameLog;
import com.moonic.mgr.LockStor;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.txtdata.TeamActivityData;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.MyLog;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

import server.common.Tools;

/**
 * 组队活动
 * @author wkc
 */
public class TeamActivity {
	public long startTime;// 开始时间
	public long actiTimeLen;// 活动时长

	public Hashtable<Integer, Team> teamTable = new Hashtable<Integer, Team>();// 队伍table
	
	public Hashtable<Integer, Integer> pla_tab = new Hashtable<Integer, Integer>();//玩家所在队伍编号

	public MyLog log = new MyLog(MyLog.NAME_CUSTOM, "teamacti", "TEAMACTI", true, false, true, MyTools.formatTime("yyyy-MM-dd-hh-mm-ss"));// 日志

	private ScheduledExecutorService timer = MyTools.createTimer(3);//计时器
	
	/**
	 * 构造
	 */
	public TeamActivity(long actiTimeLen){
		this.startTime = System.currentTimeMillis();
		this.actiTimeLen = actiTimeLen;
	}
	
	/**
	 * 启动
	 */
	public void start() {
		Out.println("组队活动开始");
		log.d("组队活动倒计时" + actiTimeLen / 1000 + "s");
		MyTimerTask endTT = new MyTimerTask() {
			public void run2() {
				DBHelper dbHelper = new DBHelper();
				try {
					PlaTeamBAC.teamActivity = null;
					Out.println("组队活动结束");
					dbHelper.openConnection();
					PlaTeamBAC.getInstance().resetTimes(dbHelper);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbHelper.closeConnection();
				}
			}
		};
		timer.schedule(endTT, actiTimeLen, TimeUnit.MILLISECONDS);
		JSONArray pusharr = new JSONArray();
		pusharr.add(startTime);
		pusharr.add(actiTimeLen);
		PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_TEAM_ACTI_START, pusharr.toString());
	}

	/**
	 * 创建队伍
	 * @param type,队伍类型
	 * @return teamNum,队伍编号
	 * @throws Exception
	 */
	public int createTeam(int playerid, int type, GameLog gl) throws Exception {
		synchronized (LockStor.getLock(LockStor.TEAM_ACTI)) {
			if(pla_tab.containsKey(playerid)){
				BACException.throwInstance("玩家已在队伍中");
			}
			Member member = new Member(playerid);
			if(member.times >= 3){
				BACException.throwInstance("挑战次数已用完");
			}
			member.isLeader = true;
			member.isReady = true;
			Team team = new Team(member, type);
			team.memberTable.put(playerid, member);
			int teamNum = team.num;
			teamTable.put(teamNum, team);
			pla_tab.put(playerid, teamNum);
			log.d(member.name+"创建队伍"+GameLog.formatNameID(team.name, teamNum));
			gl.addRemark("创建队伍" + GameLog.formatNameID(team.name, teamNum));
			return teamNum;
		}
	}

	/**
	 * 加入队伍
	 * @param num,队伍编号
	 * @throws Exception
	 */
	public JSONArray joinTeam(int playerid, int num, GameLog gl) throws Exception {
		synchronized (LockStor.getLock(LockStor.TEAM_ACTI, num)) {
			if(pla_tab.containsKey(playerid)){
				BACException.throwInstance("玩家已在队伍中");
			}
			Team team = teamTable.get(num);
			if (team == null) {
				BACException.throwInstance("队伍编号不存在");
			}
			if (team.memberTable.containsKey(playerid)) {
				BACException.throwInstance("已在此队伍中");
			}
			if (team.memberTable.size() >= 3) {
				BACException.throwInstance("队伍成员已满");
			}
			Member member = new Member(playerid);
			if (member.lv < team.lvLimit) {
				BACException.throwInstance("此队伍限制等级为" + team.lvLimit);
			}
			JSONArray memberArr = team.getMemberList();
			team.memberTable.put(playerid, member);
			pla_tab.put(playerid, num);
			team.sendMsgToTeam(SocketServer.ACT_TEAM_ACTI_JOIN, member.getMemberInfo().toString(), playerid);
			log.d(member.name+"加入队伍"+GameLog.formatNameID(team.name, num));
			gl.addRemark("加入队伍" + GameLog.formatNameID(team.name, num));
			return memberArr;
		}
	}

	/**
	 * 踢出队伍
	 * @param num,队伍编号
	 * @param memberid,队员ID
	 * @throws Exception
	 */
	public void kickOut(int playerid, int num, int memberid, GameLog gl) throws Exception {
		synchronized (LockStor.getLock(LockStor.TEAM_ACTI, num)) {
			Team team = teamTable.get(num);
			if (team == null) {
				BACException.throwInstance("队伍编号不存在");
			}
			if (team.leader.id != playerid) {
				BACException.throwInstance("只有队长才能踢人");
			}
			Member member = team.memberTable.get(memberid);
			if (member == null) {
				BACException.throwInstance("玩家不在此队伍中");
			}
			team.sendMsgToTeam(SocketServer.ACT_TEAM_ACTI_EXIT, String.valueOf(memberid), playerid);
			team.memberTable.remove(memberid);
			pla_tab.remove(memberid);
			log.d(team.leader.name+"从队伍" + GameLog.formatNameID(team.name, num) + "踢出" + member.name);
			gl.addRemark("从队伍" + GameLog.formatNameID(team.name, num) + "踢出" + member.name);
		}
	}

	/**
	 * 布阵
	 * @param num,队伍编号
	 * @throws Exception
	 */
	public int format(int playerid, int num, JSONArray posarr, GameLog gl) throws Exception {
		synchronized (LockStor.getLock(LockStor.TEAM_ACTI, num)) {
			Team team = teamTable.get(num);
			if (team == null) {
				BACException.throwInstance("队伍编号不存在");
			}
			Member member = team.memberTable.get(playerid);
			if (member == null) {
				BACException.throwInstance("玩家不在此队伍中");
			}
			if (!member.isLeader && member.isReady) {
				BACException.throwInstance("准备状态下不能布阵");
			}
			if (posarr.length() != 2) {
				BACException.throwInstance("阵型长度错误");
			}
			JSONArray partnerarr = new JSONArray();
			for (int i = 0; i < posarr.length(); i++) {
				JSONArray partner = new JSONArray();
				int partnerId = posarr.getInt(i);
				if (partnerId != 0) {
					DBPaRs partnerRs = PartnerBAC.getInstance().getDataRsByKey(playerid, partnerId);
					if (!partnerRs.exist()) {
						BACException.throwInstance("伙伴不存在");
					}
					partner.add(partnerId);//ID
					partner.add(partnerRs.getInt("num"));//编号
					partner.add(partnerRs.getInt("lv"));//等级
					partner.add(partnerRs.getInt("star"));//星级
					partner.add(partnerRs.getInt("phase"));//阶段
				} 
				partnerarr.add(partner);
			}
			member.partnerArr = partnerarr;
			TeamBox teambox = PartnerBAC.getInstance().getTeamBox(playerid, 0, posarr);
			int battlePower = PartnerBAC.getInstance().getTeamBoxBattlePower(teambox);
			member.battlePower = battlePower;
			JSONArray pusharr = new JSONArray();
			pusharr.add(playerid);
			pusharr.add(battlePower);
			pusharr.add(member.partnerArr);
			team.sendMsgToTeam(SocketServer.ACT_TEAM_ACTI_FORMAT, pusharr.toString(), playerid);
			log.d(member.name+"更改阵型为"+posarr);
			gl.addRemark("阵型变为" + posarr);
			return battlePower;
		}
	}

	/**
	 * 准备
	 * @param num,队伍编号
	 * @throws Exception
	 */
	public void beReady(int playerid, int num, GameLog gl) throws Exception {
		synchronized (LockStor.getLock(LockStor.TEAM_ACTI, num)) {
			Team team = teamTable.get(num);
			if (team == null) {
				BACException.throwInstance("队伍编号不存在");
			}
			Member member = team.memberTable.get(playerid);
			if (member == null) {
				BACException.throwInstance("玩家不在此队伍中");
			}
			if (member.isReady) {
				BACException.throwInstance("已准备");
			}
			if(member.getPartnerAm() == 0){
				BACException.throwInstance("至少上阵一个伙伴");
			}
			member.isReady = true;
			team.sendMsgToTeam(SocketServer.ACT_TEAM_ACTI_BEREADY, String.valueOf(playerid), playerid);
			gl.addRemark("准备");
		}
	}

	/**
	 * 取消准备
	 * @param num,队伍编号
	 * @throws Exception
	 */
	public void cancelReady(int playerid, int num, GameLog gl) throws Exception {
		synchronized (LockStor.getLock(LockStor.TEAM_ACTI, num)) {
			Team team = teamTable.get(num);
			if (team == null) {
				BACException.throwInstance("队伍编号不存在");
			}
			Member member = team.memberTable.get(playerid);
			if (member == null) {
				BACException.throwInstance("玩家不在此队伍中");
			}
			if (!member.isReady) {
				BACException.throwInstance("未准备");
			}
			member.isReady = false;
			team.sendMsgToTeam(SocketServer.ACT_TEAM_ACTI_CANCELREADY, String.valueOf(playerid), playerid);
			gl.addRemark("取消准备");
		}
	}
	
	/**
	 * 退出房间
	 * @throws Exception
	 */
	public void exitTeam(int playerid, int num, GameLog gl) throws Exception{
		synchronized (LockStor.getLock(LockStor.TEAM_ACTI, num)) {
			Team team = teamTable.get(num);
			if (team != null) {
				Member member = team.memberTable.get(playerid);
				if (member != null) {
					if(member.isLeader){//队长退出关闭房间
						Iterator<Integer> keys = team.memberTable.keySet().iterator();
						while(keys.hasNext()){
							pla_tab.remove(keys.next());
						}
						teamTable.remove(num);
						log.d("队长"+member.name+"离开队伍"+GameLog.formatNameID(team.name, num));
						team.sendMsgToTeam(SocketServer.ACT_TEAM_ACTI_CLOSE, "", playerid);
						if(gl != null){
							gl.addRemark("队长离开队伍"+GameLog.formatNameID(team.name, num));
						}
					} else{//队员退出
						pla_tab.remove(playerid);
						team.memberTable.remove(playerid);
						log.d("队员"+member.name+"离开队伍"+GameLog.formatNameID(team.name, num));
						team.sendMsgToTeam(SocketServer.ACT_TEAM_ACTI_EXIT, String.valueOf(playerid), playerid);
					}
				}
			}
		}
	}
	
	/**
	 * 战斗
	 * @param num,队伍编号
	 * @throws Exception
	 */
	public JSONArray battle(DBHelper dbHelper, int playerid, int num, GameLog gl) throws Exception {
		synchronized (LockStor.getLock(LockStor.TEAM_ACTI, num)) {
			Team team = teamTable.get(num);
			if (team == null) {
				BACException.throwInstance("队伍编号不存在");
			}
			if (team.leader.id != playerid) {
				BACException.throwInstance("只有队长才能开始战斗");
			}
			if (team.leader.getPartnerAm() == 0) {
				BACException.throwInstance("至少上阵一个伙伴");
			}
			team.checkReady();
			teamTable.remove(num);//移除队伍
			BattleBox battleBox = new BattleBox();
			battleBox.teamArr[0].add(team.getTeamBox());
			battleBox.teamArr[1].add(Enemy.getInstance().createTeamBox(team.boss));
			BattleManager.createPVPBattle(battleBox);
			JSONArray jsonarr = new JSONArray();
			jsonarr.add(battleBox.replayData);// 战斗录像
			StringBuffer remarkSb = new StringBuffer();
			remarkSb.append("队伍"+GameLog.formatNameID(team.name, num)+"挑战");
			remarkSb.append(battleBox.winTeam == Const.teamA ? "成功" : "失败");
			Iterator<Member> members = team.memberTable.values().iterator();
			log.d(remarkSb.toString());
			while(members.hasNext()){
				Member member = members.next();
				pla_tab.remove(member.id);
				if(battleBox.winTeam == Const.teamA && member.times < TeamActivityData.times){
					JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, member.id, team.award, ItemBAC.SHORTCUT_MAIL, 1, gl);
					jsonarr.add(team.award);
					jsonarr.add(awardarr);
					member.times++;//增加获奖次数
					PlaTeamBAC.getInstance().addValue(dbHelper, member.id, "times", 1, gl, "获得奖励次数");
				}
				if(!member.isLeader){
					PushData.getInstance().sendPlaToOne(SocketServer.ACT_TEAM_ACTI_BATTLE, jsonarr.toString(), member.id);
				}
			}
			gl.addRemark(remarkSb);
			return jsonarr;
		}
	}
	
	/**
	 * 登出,关闭队伍
	 * @throws Exception 
	 */
	public void logout(int playerid) throws Exception{
		if(pla_tab.containsKey(playerid)){
			exitTeam(playerid, pla_tab.get(playerid), null);
		}
	}
	
	/**
	 * 获取指定队伍类型的队伍列表
	 */
	public JSONArray getTeamList(int type) {
		JSONArray jsonarr = new JSONArray();
		Team[] teamArr = teamTable.values().toArray(new Team[teamTable.size()]);
		Tools.sort(teamArr, 0);
		for (int i = 0; i < teamArr.length; i++) {
			if(teamArr[i].type == type){
				jsonarr.add(teamArr[i].getTeamInfo());
			}
		}
		return jsonarr;
	}
	
	/**
	 * 获取登陆数据
	 */
	public JSONArray getLoginData(){
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(startTime);
		jsonarr.add(actiTimeLen);
		return jsonarr;
	}
}
