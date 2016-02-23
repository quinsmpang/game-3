package com.moonic.bac;

import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.battle.BattleBox;
import com.moonic.battle.BattleManager;
import com.moonic.battle.Const;
import com.moonic.battle.TeamBox;
import com.moonic.gamelog.GameLog;
import com.moonic.mgr.LockStor;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.txtdata.MineralsData;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

import conf.Conf;
	
/**
 * 抢矿
 * @author John
 */
public class PlaMineralsBAC {
	private Hashtable<Integer, JSONArray> playertab = new Hashtable<Integer, JSONArray>();//ARR内容：矿类型，占矿时间，矿编号(初级矿没有编号)，玩家ID
	private Vector<Integer> useNumVec = new Vector<Integer>();//已经使用的矿编号(中级从101开始，高级从201开始)
	private boolean isRun;
	
	/**
	 * 占矿
	 */
	public ReturnValue clockIn(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			if(!isRun){
				BACException.throwInstance("不在活动中");
			}
			int diff = num / 100;
			if(diff <0 || diff > 2){
				BACException.throwInstance("编号不正确 num="+num);
			}
			int posnum = num % 100;
			if(diff != 0 && (posnum < 1 || posnum > MineralsData.posamount[diff])){
				BACException.throwInstance("编号不正确 num="+num);
			}
			synchronized (LockStor.getLock(LockStor.MINERALS, num)) {
				if(useNumVec.contains(num)){
					BACException.throwInstance("这个矿已经有人占了 num="+num);
				}
				JSONArray myArr = playertab.get(playerid);
				if(myArr != null){//结算上一坑位奖励
					if(myArr.optInt(2) == num){
						BACException.throwInstance("不可抢自己的矿 num="+num);
					}
					issueAward(dbHelper, playerid, myArr.optInt(0), myArr.optLong(1), System.currentTimeMillis(), null, 1);
					if(diff != 0){
						useNumVec.remove((Integer)myArr.optInt(2));
					}
				}
				long starttime = System.currentTimeMillis();
				JSONArray new_myArr = new JSONArray();//存储新坑位信息
				new_myArr.add(diff);//难度
				new_myArr.add(starttime);//开始时间
				new_myArr.add(num);//坑位编号
				new_myArr.add(playerid);//玩家ID
				playertab.put(playerid, new_myArr);
				
				if(diff != 0){
					useNumVec.add(num);	
				}
				
				if(diff != 0){
					PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_MINERALS_CLOCKIN, new_myArr.toString());	
				}
				
				GameLog.getInst(playerid, GameServlet.ACT_MINERALS_CLOCKIN)
				.addRemark("坑号："+num)
				.save();
				return new ReturnValue(true, String.valueOf(starttime));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 抢夺
	 */
	public ReturnValue contend(int playerid, int opppid, int num, String posarrStr){
		DBHelper dbHelper = new DBHelper();
		try {
			if(!isRun){
				BACException.throwInstance("不在活动中");
			}
			synchronized (LockStor.getLock(LockStor.MINERALS, num)) {
				JSONArray oppArr = playertab.get(opppid);
				if(oppArr == null || oppArr.optInt(0) == 0){
					BACException.throwInstance("对方没有占用中高级矿");
				}
				if(oppArr.optInt(2) != num){
					BACException.throwInstance("坑位已易主 opppid="+opppid+" num="+num);
				}
				int diff = oppArr.optInt(0);
				
				DBPaRs plajjcRs = PlaJJCRankingBAC.getInstance().getDataRs(playerid);
				if(System.currentTimeMillis() < plajjcRs.getTime("wkchatime") + MineralsData.losetime){
					BACException.throwInstance("冷却时间未结束");
				}
				JSONArray myArr = playertab.get(playerid);
				if(myArr != null && myArr.optInt(2) == num){
					BACException.throwInstance("不可抢自己的矿 num="+num);
				}
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_MINERALS_CONDENT); 
				if(plajjcRs.getInt("wkchaam") >= MineralsData.robberynum){
					PlayerBAC.getInstance().useCoin(dbHelper, playerid, MineralsData.buyrobbery, gl);
				}
				DBPaRs oppjjcRs = PlaJJCRankingBAC.getInstance().getDataRs(opppid);
				
				TeamBox teambox1 = PartnerBAC.getInstance().getTeamBox(playerid, 0, new JSONArray(posarrStr));
				TeamBox teambox2 = PartnerBAC.getInstance().getTeamBox(opppid, 1, new JSONArray(oppjjcRs.getString("wkdefform")));
				BattleBox battlebox = new BattleBox();
				battlebox.teamArr[0].add(teambox1);
				battlebox.teamArr[1].add(teambox2);
				BattleManager.createPVPBattle(battlebox);
				
				SqlString sqlStr = new SqlString();
				sqlStr.addChange("wkchaam", 1);
				long starttime = 0;
				if(battlebox.winTeam == Const.teamA){//战斗胜利
					DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
					if(myArr != null){//结算上一坑位奖励
						issueAward(dbHelper, playerid, myArr.optInt(0), myArr.optLong(1), System.currentTimeMillis(), null, 2);
						useNumVec.remove((Integer)myArr.optInt(2));
					}
					issueAward(dbHelper, opppid, oppArr.optInt(0), oppArr.optLong(1), System.currentTimeMillis(), plaRs.getString("name"), 3);//结算对手上一坑位奖励
					
					starttime = System.currentTimeMillis();
					
					JSONArray new_myArr = new JSONArray();//更新我的占矿信息
					new_myArr.add(diff);
					new_myArr.add(starttime);
					new_myArr.add(num);
					new_myArr.add(playerid);
					playertab.put(playerid, new_myArr);
					
					JSONArray new_oppArr = new JSONArray();//更新对手的占矿信息
					new_oppArr.add(0);
					new_oppArr.add(starttime);
					new_oppArr.add(0);
					new_oppArr.add(opppid);
					playertab.put(opppid, new_oppArr);
					
					PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_MINERALS_CONDENT, new_myArr.toString());
				} else {
					sqlStr.addDateTime("wkchatime", MyTools.getTimeStr());
				}
				PlaJJCRankingBAC.getInstance().update(dbHelper, playerid, sqlStr);
				
				JSONArray returnarr = new JSONArray();
				returnarr.add(starttime);
				returnarr.add(battlebox.replayData);
				
				gl.addRemark("坑号："+num+" 结果："+(battlebox.winTeam == Const.teamA?"成功":"失败"));
				gl.save();
				return new ReturnValue(true, returnarr.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 设置防守阵型
	 */
	public ReturnValue setDefForm(int playerid, String posarrStr){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plajjcrankingRs = PlaJJCRankingBAC.getInstance().getDataRs(playerid);
			if(plajjcrankingRs.getString("wkdefform").equals(posarrStr)){
				BACException.throwInstance("阵型无变化");
			}
			JSONArray posarr = new JSONArray(posarrStr);
			PartnerBAC.getInstance().checkPosarr(playerid, posarr, 0, 1);//检查阵型是否合法
			SqlString sqlStr = new SqlString();
			sqlStr.add("wkdefform", posarr.toString());
			PlaJJCRankingBAC.getInstance().update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取个人抢矿信息
	 */
	public ReturnValue getInfo(int playerid){
		try {
			DBPaRs plajjcRs = PlaJJCRankingBAC.getInstance().getDataRs(playerid);
			JSONArray returnarr = new JSONArray();
			returnarr.add(plajjcRs.getInt("wkchaam"));//已挑战次数
			returnarr.add(new JSONArray(plajjcRs.getString("wkdefform")));//防守阵型
			returnarr.add(plajjcRs.getTime("wkchatime"));//最后挑战时间
			returnarr.add(isRun?1:0);
			returnarr.add(playertab.get(playerid));//个人挖矿数据
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 获取矿位信息
	 */
	public ReturnValue getPosData(){
		try {
			if(!isRun){
				BACException.throwInstance("不在活动中");
			}
			JSONArray returnarr = new JSONArray();
			JSONArray[] dataarr = playertab.values().toArray(new JSONArray[playertab.size()]);
			for(int i = 0; dataarr != null && i < dataarr.length; i++){
				if(dataarr[i].optInt(0) != 0){
					returnarr.add(dataarr[i]);
				}
			}
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 后台获取矿位信息
	 */
	public ReturnValue bkGetPosData(){
		try {
			if(!isRun){
				BACException.throwInstance("不在活动中");
			}
			StringBuffer[] strArr = new StringBuffer[3];
			strArr[0] = new StringBuffer();
			strArr[1] = new StringBuffer();
			strArr[2] = new StringBuffer();
			JSONArray[] dataarr = playertab.values().toArray(new JSONArray[playertab.size()]);
			for(int i = 0; dataarr != null && i < dataarr.length; i++){
				strArr[dataarr[i].optInt(0)].append(dataarr[i].optInt(3)+"(坑位："+dataarr[i].optInt(2)+"开始时间："+dataarr[i].optLong(1)+")");
			}
			String returnStr = "初级场："+strArr[0] + "\r\n中级场：" + strArr[1] + "\r\n高级场：" + strArr[2] + "\r\n已占用坑位：" + useNumVec;
			return new ReturnValue(true, returnStr);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 获取矿主信息
	 */
	public ReturnValue getOwnerData(int targetpid){
		try {
			if(!isRun){
				BACException.throwInstance("不在活动中");
			}
			JSONArray tgrArr = playertab.get(targetpid);
			if(tgrArr == null){
				BACException.throwInstance("此玩家未占矿");
			}
			DBPaRs plajjcRs = PlaJJCRankingBAC.getInstance().getDataRs(targetpid);
			
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(targetpid);
			JSONArray arr = new JSONArray();
			arr.add(targetpid);//ID
			arr.add(plaRs.getString("name"));//名字
			arr.add(plaRs.getInt("lv"));//等级
			arr.add(plaRs.getInt("num"));//头像
			arr.add(PartnerBAC.getInstance().getPlayerBattlePower(targetpid, new JSONArray(plajjcRs.getString("wkdefform")), plajjcRs.getInt("wkbattlepower")));//战力
			return new ReturnValue(true, arr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 初始化数据
	 */
	public void init(DBHelper dbHelper, int playerid) throws Exception {
		DBPaRs plaRs = PlaJJCRankingBAC.getInstance().getDataRs(playerid);
		SqlString sqlStr = new SqlString();
		sqlStr.add("wkdefform", plaRs.getString("defformation"));
		PlaJJCRankingBAC.getInstance().update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 发放奖励
	 */
	public void issueAward(DBHelper dbHelper, int playerid, int diff, long starttime, long endtime, String name, int from) throws Exception {
		int para = 0;
		for(int i = 0; i < MineralsData.awardpara.length; i++){
			if(Conf.worldLevel <= Tools.str2int(MineralsData.awardpara[i][0])){
				para = Tools.str2int(MineralsData.awardpara[i][1]);
				break;
			}
		}
		int money = (int)(para * (MineralsData.markon[diff] / 100d) * ((endtime-starttime) / MineralsData.rewardtime));//基础参数 * 倍数 * 时间份数
		if(name != null){
			MailBAC.getInstance().sendModelMail(dbHelper, new int[]{playerid}, 10, null, new Object[]{name, money}, "3,"+money);
		} else {
			MailBAC.getInstance().sendModelMail(dbHelper, new int[]{playerid}, 9, null, new Object[]{money}, "3,"+money);
		}
	}
	
	/**
	 * 启动活动
	 */
	public ReturnValue start(String from){
		try {
			if(isRun){
				BACException.throwInstance("活动正在进行中");
			}
			isRun = true;
			PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_MINERALS_START, "");
			Out.println(from+" 启动挖矿活动");
			return new ReturnValue(true, "启动完成");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 结束活动
	 */
	public ReturnValue end(String from){
		DBHelper dbHelper = new DBHelper();
		try {
			if(!isRun){
				BACException.throwInstance("未进行活动");
			}
			isRun = false;
			long endtime = System.currentTimeMillis();
			JSONArray[] dataarr = playertab.values().toArray(new JSONArray[playertab.size()]);
			for(int i = 0; dataarr != null && i < dataarr.length; i++){
				issueAward(dbHelper, dataarr[i].optInt(3), dataarr[i].optInt(0), dataarr[i].optLong(1), endtime, null, 4);
			}
			playertab.clear();
			useNumVec.clear();
			PushData.getInstance().sendPlaToAllOL(SocketServer.ACT_MINERALS_END, "");
			Out.println(from+" 停止挖矿活动");
			return new ReturnValue(true, "启动完成");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//------------------静态区--------------------
	
	private static PlaMineralsBAC instance = new PlaMineralsBAC();

	public static PlaMineralsBAC getInstance() {
		return instance;
	}
}
