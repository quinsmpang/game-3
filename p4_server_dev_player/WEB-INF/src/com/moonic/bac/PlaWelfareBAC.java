package com.moonic.bac;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import server.common.Tools;

/**
 * 角色福利（日常）
 * @author wkc
 */
public class PlaWelfareBAC extends PlaBAC{
	public static final String tab_daily_task = "tab_daily_task";
	public static final String tab_achievement = "tab_achievement";
	public static final String tab_target = "tab_target";
	public static final String tab_checkin = "tab_checkin";
	public static final String tab_checkin_spe = "tab_checkin_spe";
	public static final String tab_checkin_total = "tab_checkin_total";
	
	public static final byte TYPE_MONTHCARD = 1;//月卡在有效期内
	public static final byte TYPE_COPYMAP_ORDINARY = 2;//普通副本通关次数
	public static final byte TYPE_COPYMAP_ELITE = 3;//精英副本通关次数
	public static final byte TYPE_TRIAL_MONEY = 4;//铜钱试炼次数
	public static final byte TYPE_JJC = 5;//竞技场次数
	public static final byte TYPE_TRIAL_EXP = 6;//经验试炼次数
	public static final byte TYPE_PARTNER_LV_UP = 7;//伙伴升级
	public static final byte TYPE_PARTNER_SKILL_UP = 8;//伙伴技能升级
	public static final byte TYPE_BUY_ENERGY = 9;//买体力次数
	public static final byte TYPE_BUY_MONEY = 10;//买铜钱次数
	public static final byte TYPE_CALL_PARTNER = 11;//伙伴召唤次数
	public static final byte TYPE_CALL_EQUIP = 12;//装备召唤次数
	public static final byte TYPE_TOWER = 13;//轮回塔次数
	public static final byte TYPE_TEAMCM = 14;//团本战斗次数
	public static final byte TYPE_CITYBATTLE = 15;//城战次数
	public static final byte TYPE_WORSHIP = 16;//帮派膜拜次数
	public static final byte TYPE_TRIAL_PARTNER = 17;//伙伴试炼次数
	public static final byte TYPE_INSTRUMENT = 18;//神器经验注入次数
	public static final byte TYPE_TAKE_ENERGY_NOON = 19;//中午领体力（12点~14点）
	public static final byte TYPE_TAKE_ENERGY_NIGHT = 20;//晚上领体力（18点~20点）
	public static final byte TYPE_EQUIP_STRENGTHEN = 21;//装备强化次数
	
	public static final byte ACHIEVE_PARTER_AM = 1;//拥有X个伙伴
	public static final byte ACHIEVE_COPYMAP_PASS = 2; //通关编号X的副本Y次
	public static final byte ACHIEVE_PARTNER_QUALITY = 3;//X个伙伴升到Y品质
	public static final byte ACHIEVE_PLV = 4;//历练等级达到X级
	public static final byte ACHIEVE_VIP = 5;//VIP达到X级
	public static final byte ACHIEVE_PARTNER_WAKE = 6;//X个伙伴觉醒
	public static final byte ACHIEVE_FACCM_NUM = 7;//通关编号X的团队副本地图
	public static final byte ACHIEVE_FACCM_TIMES = 8;//累积参与团队副本战斗X次
	
	public static final byte TAREGT_LV = 1;//达到指定等级
	public static final byte TAREGT_COPYMAP = 2; //通关指定副本
	public static final byte TAREGT_TIMEDAY = 3;//游戏时间达到指定的天数
	
	/**
	 * 构造
	 */
	public PlaWelfareBAC() {
		super("tab_pla_welfare", "playerid");
	}
	
	/**
	 * 初始化目标数据
	 */
	public void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("taskdata", new JSONObject().toString());
		sqlStr.add("taskaward", new JSONArray().toString());
		sqlStr.add("achievedata", new JSONObject().toString());
		sqlStr.add("achieveaward", new JSONArray().toString());
		sqlStr.add("checkin", 0);
		sqlStr.add("checkintotal", 0);
		sqlStr.add("checkinaward", new JSONArray().toString());
		sqlStr.add("ischecked", 0);
		sqlStr.add("targetaward", new JSONArray().toString());
		insert(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 领取日常任务奖励
	 * @param num 任务编号
	 */
	public ReturnValue getTaskAward(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs taskListRs = DBPool.getInst().pQueryA(tab_daily_task, "num="+num);
			if(!taskListRs.exist()){
				BACException.throwInstance("不存在的任务编号"+num);
			}
			DBPaRs plaWelRs = getDataRs(playerid);
			JSONArray jsonarr = new JSONArray(plaWelRs.getString("taskaward"));
			if(jsonarr.contains(num)){
				BACException.throwInstance("此任务奖励已领取");
			}
			int[] conarr = Tools.splitStrToIntArr(taskListRs.getString("finish"), ",");
			checkTaskCondition(playerid, conarr);
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_WELFARE_GETTASKAWARD);
			jsonarr.add(num);
			SqlString sqlStr = new SqlString();
			sqlStr.add("taskaward", jsonarr.toString());
			update(dbHelper, playerid, sqlStr);
			String award = taskListRs.getString("award");
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, award, ItemBAC.SHORTCUT_MAIL, 1, gl);
			CustomActivityBAC.getInstance().updateProcess(dbHelper, playerid, 4);
			
			gl.addRemark("领取任务奖励编号为"+num);
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
	 * 一键领取日常任务奖励
	 * @param num 任务编号
	 */
	public ReturnValue getTaskAwardOneKey(int playerid, String numStr){
		DBHelper dbHelper = new DBHelper();
		try {
			JSONArray numArr = new JSONArray(numStr);
			if(numArr.length() == 0){
				BACException.throwInstance("编号数据为空");
			}
			DBPaRs plaWelRs = getDataRs(playerid);
			JSONArray jsonarr = new JSONArray(plaWelRs.getString("taskaward"));
			StringBuffer awardSb = new StringBuffer();
			StringBuffer remarkSb = new StringBuffer();
			remarkSb.append("一键领取任务奖励编号：");
			for (int i = 0; i < numArr.length(); i++) {
				int num = numArr.optInt(i);
				DBPaRs taskListRs = DBPool.getInst().pQueryA(tab_daily_task, "num="+num);
				if(!taskListRs.exist()){
					BACException.throwInstance("不存在的任务编号"+num);
				}
				if(jsonarr.contains(num)){
					BACException.throwInstance("此任务奖励已领取");
				}
				int[] conarr = Tools.splitStrToIntArr(taskListRs.getString("finish"), ",");
				checkTaskCondition(playerid, conarr);
				if(!awardSb.toString().equals("")){
					awardSb.append("|");
				}
				awardSb.append(taskListRs.getString("award"));
				jsonarr.add(num);
				remarkSb.append(num+",");
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_WELFARE_GETTASKAWARD_ONEKEY);
			SqlString sqlStr = new SqlString();
			sqlStr.add("taskaward", jsonarr.toString());
			update(dbHelper, playerid, sqlStr);
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
			CustomActivityBAC.getInstance().updateProcess(dbHelper, playerid, 4, numArr.length());
			
			gl.addRemark(remarkSb);
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
	 * 领取成就奖励
	 * @param num 成就编号
	 */
	public ReturnValue getAchievementAward(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs achieveListRs = DBPool.getInst().pQueryA(tab_achievement, "num="+num);
			if(!achieveListRs.exist()){
				BACException.throwInstance("不存在的成就编号"+num);
			}
			DBPaRs plaWelRs = getDataRs(playerid);
			JSONArray jsonarr = new JSONArray(plaWelRs.getString("achieveaward"));
			if(jsonarr.contains(num)){
				BACException.throwInstance("此成就奖励已领取");
			}
			int[] needarr = Tools.splitStrToIntArr(achieveListRs.getString("finish"), ",");
			checkAchevimentConditon(playerid, needarr);
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_WELFARE_GETACHIEVEAWARD);
			dbHelper.openConnection();
			String award = achieveListRs.getString("award");
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, award, ItemBAC.SHORTCUT_MAIL, 1, gl);
			jsonarr.add(num);
			SqlString sqlStr = new SqlString();
			sqlStr.add("achieveaward", jsonarr.toString());
			update(dbHelper, playerid, sqlStr);
			
			gl.addRemark("领取成就奖励编号："+num);
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
	 * 一键领取成就奖励
	 * @param num 成就编号
	 */
	public ReturnValue getAchievementAwardOneKey(int playerid, String numStr){
		DBHelper dbHelper = new DBHelper();
		try {
			JSONArray numArr = new JSONArray(numStr);
			if(numArr.length() == 0){
				BACException.throwInstance("编号数据为空");
			}
			StringBuffer awardSb = new StringBuffer();
			StringBuffer remarkSb = new StringBuffer();
			remarkSb.append("一键领取成就奖励编号：");
			DBPaRs plaWelRs = getDataRs(playerid);
			JSONArray jsonarr = new JSONArray(plaWelRs.getString("achieveaward"));
			for(int i = 0; i < numArr.length(); i++){
				int num = numArr.optInt(i);
				DBPaRs achieveListRs = DBPool.getInst().pQueryA(tab_achievement, "num="+num);
				if(!achieveListRs.exist()){
					BACException.throwInstance("不存在的成就编号"+num);
				}
				if(jsonarr.contains(num)){
					BACException.throwInstance("此成就奖励已领取");
				}
				int[] needarr = Tools.splitStrToIntArr(achieveListRs.getString("finish"), ",");
				checkAchevimentConditon(playerid, needarr);
				if(!awardSb.toString().equals("")){
					awardSb.append("|");
				}
				awardSb.append(achieveListRs.getString("award"));
				jsonarr.add(num);
				remarkSb.append(num+",");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_WELFARE_GETACHIEVEAWARD_ONEKEY);
			dbHelper.openConnection();
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
			SqlString sqlStr = new SqlString();
			sqlStr.add("achieveaward", jsonarr.toString());
			update(dbHelper, playerid, sqlStr);
			
			gl.addRemark(remarkSb);
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
	 * 签到
	 */
	public ReturnValue checkin(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plaWelRs = getDataRs(playerid);
			int checkedAm = plaWelRs.getInt("ischecked");
			if(checkedAm == 2){
				BACException.throwInstance("今日已签完");
			} 
			int checkDays = plaWelRs.getInt("checkin");
			int viplv = PlayerBAC.getInstance().getIntValue(playerid, "vip");
			DBPaRs checkinList = null;
			if(checkedAm == 0){
				checkinList = DBPool.getInst().pQueryA(tab_checkin, "num="+(checkDays+1));
				if(!checkinList.exist()){
					BACException.throwInstance("签到天数不存在");
				}
			} else
			if(checkedAm == 1){
				checkinList = DBPool.getInst().pQueryA(tab_checkin, "num="+(checkDays));
				int[] ratearr = Tools.splitStrToIntArr(checkinList.getString("vip"), ",");
				if(ratearr[0] == 0){
					BACException.throwInstance("今日只能签一次");
				} else{
					if(viplv < ratearr[0]){
						BACException.throwInstance("VIP等级不满足条件");
					}
				}
			}
			int[] ratearr = Tools.splitStrToIntArr(checkinList.getString("vip"), ",");
			int rate = 1;//倍率
			if(ratearr[0] > 0 && viplv >= ratearr[0]){
				if(checkedAm == 0){
					rate = ratearr[1];
				} else{
					rate = ratearr[1] - 1;
				}
			} 
			int[][] award = Tools.splitStrToIntArr2(checkinList.getString("award"), "|", ",");
			if(award[0][0] >= 100){
				int year =  Calendar.getInstance().get(Calendar.YEAR);
				int month = Calendar.getInstance().get(Calendar.MONTH)+1;
				DBPaRs speListRs = DBPool.getInst().pQueryA(tab_checkin_spe, "year="+year+" and month="+month);
				award = Tools.splitStrToIntArr2(speListRs.getString("award"+(award[0][0]-100)), "|", ",");
			} 
			for(int i = 0; i < award.length; i++){
				if(award[i][0] == 1){
					award[i][3] *= rate;
				} else
				if(award[i][0] == 4){
					award[i][1] *= rate;
				}
			}
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_WELFARE_CHECKIN);
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, award, ItemBAC.SHORTCUT_MAIL, 1, gl);
			SqlString sqlStr = new SqlString();
			if(checkedAm == 0){
				sqlStr.addChange("checkin", 1);
				sqlStr.addChange("checkintotal", 1);
			} 
			if(checkedAm == 0 && rate > 1){
				sqlStr.add("ischecked", 2);
			} else{
				sqlStr.addChange("ischecked", 1);
			}
			update(dbHelper, playerid, sqlStr);
			
			gl.addRemark("奖励倍率："+rate);
			gl.save();
			return new ReturnValue(true, awardarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}

	/**
	 * 领取签到累积奖励
	 */
	public ReturnValue getCheckinAward(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs totalListRs = DBPool.getInst().pQueryA(tab_checkin_total, "num="+num);
			if(!totalListRs.exist()){
				BACException.throwInstance("签到累积奖励编号不存在"+num);
			}
			DBPaRs plaWelRs = getDataRs(playerid);
			JSONArray checkinawardarr = new JSONArray(plaWelRs.getString("checkinaward"));
			if(checkinawardarr.contains(num)){
				BACException.throwInstance("此签到累积奖励已领取");
			}
			int total = plaWelRs.getInt("checkintotal");
			int days = totalListRs.getInt("days");
			if(total < days){
				BACException.throwInstance("累积签到天数不足");
			}
			String award = totalListRs.getString("award");
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_WELFARE_GETCHECKINAWARD);
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, award, ItemBAC.SHORTCUT_MAIL, 1, gl);
			checkinawardarr.add(num);
			SqlString sqlStr = new SqlString();
			sqlStr.add("checkinaward", checkinawardarr.toString());
			update(dbHelper, playerid, sqlStr);
			
			gl.addRemark("领取累积签到奖励（编号"+num+"）");
			gl.save();
			return new ReturnValue(true, awardarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 领取目标奖励
	 */
	public ReturnValue gerTargetAward(int playerid, int num){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs targetListRs = DBPool.getInst().pQueryA(tab_target, "num="+num);
			if(!targetListRs.exist()){
				BACException.throwInstance("目标奖励编号不存在"+num);
			}
			DBPaRs plaWelRs = getDataRs(playerid);
			JSONArray targetawardarr = new JSONArray(plaWelRs.getString("targetaward"));
			if(targetawardarr.contains(num)){
				BACException.throwInstance("此目标奖励已领取");
			}
			int[] conarr = Tools.splitStrToIntArr(targetListRs.getString("condition"), ",");
			checkTargetCondition(playerid, conarr);;
			String award = targetListRs.getString("award");
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_WELFARE_GETTARGETAWARD);
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, award, ItemBAC.SHORTCUT_MAIL, 1, gl);
			targetawardarr.add(num);
			SqlString sqlStr = new SqlString();
			sqlStr.add("targetaward", targetawardarr.toString());
			update(dbHelper, playerid, sqlStr);
			
			gl.addRemark("领取目标奖励（编号"+num+"）");
			gl.save();
			return new ReturnValue(true, awardarr.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	
	/**
	 * 检查任务条件是否满足
	 */
	public void checkTaskCondition(int playerid, int[] conarr) throws Exception{
		if(conarr[0] == TYPE_MONTHCARD){
			int tqnum = TqBAC.getInstance().getTQNum(playerid);
			if(tqnum == 0){
				BACException.throwInstance("月卡不在有效期");
			}
		} else
		if(conarr[0] == TYPE_TAKE_ENERGY_NOON){
			long current = System.currentTimeMillis();
			if(current < MyTools.getCurrentDateLong() + MyTools.long_hour*12 || current > MyTools.getCurrentDateLong() + MyTools.long_hour*14){
				BACException.throwInstance("体力领取时间：12:00~14:00");
			}
		} else
		if(conarr[0] == TYPE_TAKE_ENERGY_NIGHT){
			long current = System.currentTimeMillis();
			if(current < MyTools.getCurrentDateLong() + MyTools.long_hour*18 || current > MyTools.getCurrentDateLong() + MyTools.long_hour*20){
				BACException.throwInstance("体力领取时间：18:00~20:00");
			}
		} else{
			DBPaRs plaWelRs = getDataRs(playerid);
			JSONObject taskobj = new JSONObject(plaWelRs.getString("taskdata"));
			if(taskobj.optInt(String.valueOf(conarr[0])) < conarr[1]){
				BACException.throwInstance("此任务尚未完成");
			}
		}
	}
	
	/**
	 * 检查成就条件是否满足
	 */
	public void checkAchevimentConditon(int playerid, int[] needarr) throws Exception{
		int type = needarr[0];//类型
		if(type == ACHIEVE_PARTER_AM){//拥有X个伙伴
			DBPsRs partnerRs = PartnerBAC.getInstance().query(playerid, "playerid="+playerid);
			int count = partnerRs.count();
			if(count < needarr[1]){
				BACException.throwInstance("伙伴个数不满足条件");
			}
		} else
		if(type == ACHIEVE_COPYMAP_PASS){//通关编号X的副本Y次
			int passAm = CopymapBAC.getInstance().getPassedAm(playerid, needarr[1]);
			if(passAm < needarr[2]){
				BACException.throwInstance("通过副本不满足条件");
			}
		} else
		if(type == ACHIEVE_PARTNER_QUALITY){//X个伙伴升到Y品质
			DBPsRs partnerRs = PartnerBAC.getInstance().query(playerid, "playerid="+playerid+" and phase>="+needarr[2]);
			int count = partnerRs.count();
			if(count < needarr[1]){
				BACException.throwInstance("指定品质伙伴个数不满足条件");
			}
		} else
		if(type == ACHIEVE_PLV){//历练等级达到X级
			int playerlv = PlayerBAC.getInstance().getIntValue(playerid, "lv");
			if(playerlv < needarr[1]){
				BACException.throwInstance("历练等级不满足条件");
			}
		} else
		if(type == ACHIEVE_VIP){//VIP达到X级
			int viplv = PlayerBAC.getInstance().getIntValue(playerid, "vip");
			if(viplv < needarr[1]){
				BACException.throwInstance("VIP等级不满足条件");
			}
		} else
		if(type == ACHIEVE_PARTNER_WAKE){//X个伙伴觉醒
			DBPsRs partnerRs = PartnerBAC.getInstance().query(playerid, "playerid="+playerid+" and awaken="+1);
			int count = partnerRs.count();
			if(count < needarr[1]){
				BACException.throwInstance("觉醒伙伴个数不满足条件");
			}
		} else{
			boolean success = false;
			DBPaRs plaWelRs = getDataRs(playerid);
			JSONObject achieveObj = new JSONObject(plaWelRs.getString("achievedata"));
			if(type == ACHIEVE_FACCM_NUM){
				JSONArray numarr = achieveObj.optJSONArray(String.valueOf(type));
				if(numarr != null && numarr.contains(needarr[1])){
					success = true;
				}
			} else{
				if(achieveObj.optInt(String.valueOf(type)) >= needarr[1]){
					success = true;
				}
			}
			if(!success){
				BACException.throwInstance("此成就尚未达成");
			}
		}
	}
	
	/**
	 * 检查目标条件是否满足
	 */
	public void checkTargetCondition(int playerid, int[] conarr) throws Exception{
		if(conarr[0] == TAREGT_LV){
			int playerlv = PlayerBAC.getInstance().getIntValue(playerid, "lv");
			if(playerlv < conarr[1]){
				BACException.throwInstance("历练等级不满足条件");
			}
		} else
		if(conarr[0] == TAREGT_COPYMAP){
			int passAm = CopymapBAC.getInstance().getPassedAm(playerid, conarr[1]);
			if(passAm == 0){
				BACException.throwInstance("尚未通过副本"+conarr[1]);
			}
		} else
		if(conarr[0] == TAREGT_TIMEDAY){
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			long createtime = plaRs.getTime("savetime");
			if(MyTools.getCurrentDateLong() - MyTools.getCurrentDateLong(createtime) < (conarr[1]-1)*MyTools.long_day){
				BACException.throwInstance("游戏时间未达到");
			}
		} 
	}
	
	/**
	 * 更新任务进度
	 */
	public void updateTaskProgress(DBHelper dbHelper, int playerid, byte type, GameLog gl) throws Exception{
		updateTaskProgress(dbHelper, playerid, type, 1, gl);
	}
	
	/**
	 * 更新任务进度
	 */
	public void updateTaskProgress(DBHelper dbHelper, int playerid, byte type, int amount, GameLog gl) throws Exception{
		int need = 0;
		DBPsRs taskListRs = DBPool.getInst().pQueryS(tab_daily_task);
		while(taskListRs.next()){
			String finish = taskListRs.getString("finish");
			int[] conarr = Tools.splitStrToIntArr(finish, ",");
			if(conarr[0] == type){
				need = conarr[1];
				break;
			}
		}
		if(need == 0){
			return;
		}
		DBPaRs plaWelRs = getDataRs(playerid);
		JSONObject taskobj = new JSONObject(plaWelRs.getString("taskdata"));
		int currAm = taskobj.optInt(String.valueOf(type));
		if(currAm >= need){
			return;
		} else{
			taskobj.put(String.valueOf(type), currAm + amount);
			SqlString sqlStr = new SqlString();
			sqlStr.add("taskdata", taskobj.toString());
			update(dbHelper, playerid, sqlStr);
			gl.addRemark("任务类型（"+type+"）的完成次数变化："+currAm+"->"+(currAm+amount));
		}
	}
	
	/**
	 * 更新成就进度
	 */
	public void updateAchieveProgress(DBHelper dbHelper, int playerid, byte type, GameLog gl) throws Exception{
		updateAchieveProgress(dbHelper, playerid, type, 1, gl);
	}
	
	/**
	 * 更新成就进度
	 */
	public void updateAchieveProgress(DBHelper dbHelper, int playerid, byte type, int param, GameLog gl) throws Exception{
		DBPaRs plaWelRs = getDataRs(playerid);
		JSONObject achieveobj = new JSONObject(plaWelRs.getString("achievedata"));
		StringBuffer remarkSb = new StringBuffer();
		remarkSb.append("成就类型（"+type+"）");
		if(type == ACHIEVE_FACCM_NUM){//通关帮派副本记录编号
			JSONArray numarr = achieveobj.optJSONArray(String.valueOf(type));
			if(numarr == null){
				numarr = new JSONArray();
			}
			if(numarr.contains(param)){
				return;
			}
			numarr.add(param);
			achieveobj.put(String.valueOf(type), numarr);
			remarkSb.append("加入帮派副本编号"+param);
		} else{
			int maxNeed = 0;//此类型最大的需求数量
			DBPsRs achievListRs = DBPool.getInst().pQueryS(tab_achievement);
			while(achievListRs.next()){
				String finish = achievListRs.getString("finish");
				int[] conarr = Tools.splitStrToIntArr(finish, ",");
				if(conarr[0] == type && maxNeed < conarr[1]){
					maxNeed = conarr[1];
				}
			}
			if(maxNeed == 0){
				return;
			}
			int currAm = achieveobj.optInt(String.valueOf(type));
			if(currAm >= maxNeed){
				return;
			} else{
				achieveobj.put(String.valueOf(type), currAm + param);
				remarkSb.append("完成进度变化："+currAm+"->"+achieveobj.optInt(String.valueOf(type)));
			}
		}
		SqlString sqlStr = new SqlString();
		sqlStr.add("achievedata", achieveobj.toString());
		update(dbHelper, playerid, sqlStr);
		gl.addRemark(remarkSb);
	}
	
	/**
	 * 重置角色福利数据
	 */
	public void resetData(DBHelper dbHelper, int playerid, boolean moonReset) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("taskdata", new JSONObject().toString());
		sqlStr.add("taskaward", new JSONArray().toString());
		if(moonReset){
			sqlStr.add("checkin", 0);
		}
		sqlStr.add("ischecked", 0);
		update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 获取数据
	 */
	public JSONArray getData(int playerid) throws Exception {
		DBPaRs plaWelRs = getDataRs(playerid);
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(new JSONObject(plaWelRs.getString("taskdata")));//任务数据
		jsonarr.add(new JSONArray(plaWelRs.getString("taskaward")));//已领取过的任务奖励
		jsonarr.add(new JSONArray(plaWelRs.getString("achieveaward")));//已领取过的成就奖励
		jsonarr.add(plaWelRs.getInt("checkin"));//已签到天数
		jsonarr.add(plaWelRs.getInt("checkintotal"));//累积签到天数
		jsonarr.add(new JSONArray(plaWelRs.getString("checkinaward")));//已领取的累积签到奖励
		jsonarr.add(plaWelRs.getInt("ischecked"));//当日已签到次数
		jsonarr.add(new JSONObject(plaWelRs.getString("achievedata")));//当前成就数据
		jsonarr.add(new JSONArray(plaWelRs.getString("targetaward")));//已领取的目标奖励
		return jsonarr;
	}
	
	//--------------调试区--------------
	
	/**
	 * 完成任务
	 */
	public ReturnValue debugFinishTask(int playerid, byte type, int amount){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_DEBUG_GAME_LOG);
			updateTaskProgress(dbHelper, playerid, type, amount, gl);
			
			gl.save();
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试加累计签到天数
	 */
	public ReturnValue debugAddCheckin(int playerid, int days){
		DBHelper dbHelper = new DBHelper();
		try {
			if(days > Integer.MAX_VALUE){
				BACException.throwInstance("天数太大了");
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addChange("checkintotal", days);
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试重置成就领取
	 */
	public ReturnValue debugResetAchieveAward(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			SqlString sqlStr = new SqlString();
			sqlStr.add("achieveaward", new JSONArray().toString());
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试重置目标领取
	 */
	public ReturnValue debugResetTargetAward(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			SqlString sqlStr = new SqlString();
			sqlStr.add("targetaward", new JSONArray().toString());
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试重置签到
	 */
	public ReturnValue debugResetCheckIn(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			SqlString sqlStr = new SqlString();
			sqlStr.add("checkin", 0);
			sqlStr.add("ischecked", 0);
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试完成所有日常任务
	 */
	public ReturnValue debugFinishAllTask(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs plaWelRs = getDataRs(playerid);
			JSONObject taskobj = new JSONObject(plaWelRs.getString("taskdata"));
			DBPsRs taskListRs = DBPool.getInst().pQueryS(tab_daily_task);
			while(taskListRs.next()){
				String finish = taskListRs.getString("finish");
				int[] conarr = Tools.splitStrToIntArr(finish, ",");
				if(conarr.length == 2){
					taskobj.put(String.valueOf(conarr[0]), conarr[1]);
				}
			}
			SqlString sqlStr = new SqlString();
			sqlStr.add("taskdata", taskobj.toString());
			update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	private static PlaWelfareBAC instance = new PlaWelfareBAC();

	/**
	 * 获取实例
	 */
	public static PlaWelfareBAC getInstance() {
		return instance;
	}
}
