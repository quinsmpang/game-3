package com.moonic.bac;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.battle.BattleBox;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.MyTools;

/**
 * 角色经验试炼
 * @author wkc
 */
public class PlaTrialExpBAC extends PlaTrialBAC{
	public static final String tab_trial_exp = "tab_trial_exp";
	
	/**
	 * 构造
	 */
	public PlaTrialExpBAC() {
		super(tab_trial_exp);
	}

	/**
	 * 初始化数据
	 */
	public void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("exptimes", 0);
		PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 开始试炼
	 */
	public ReturnValue startTrial(int playerid, int num, String posStr){
		try {
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_TRIAL_EXP_START);
			BattleBox battleBox = start(playerid, TYPE_EXP, num, posStr, gl);
			
			gl.save();
			return new ReturnValue(true, battleBox.getJSONArray().toString());
		} catch(Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	/**
	 * 结束试炼
	 */
	public ReturnValue endTrial(int playerid, int num, String battleRecord){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_PLA_TRIAL_EXP_END);
			JSONArray jsonarr = end(dbHelper, playerid, num, battleRecord, gl);
			
			gl.save();
			return new ReturnValue(true, jsonarr.toString());
		} catch(Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	@Override
	public JSONArray endHandle(DBHelper dbHelper, int playerid, int num, int star, GameLog gl) throws Exception {
		DBPaRs trialListRs = getTrialListRs(num);
		StringBuffer remarkSb = new StringBuffer();
		remarkSb.append("试炼");
		remarkSb.append(GameLog.formatNameID(trialListRs.getString("name"), num));
		StringBuffer awardSb = new StringBuffer();
		JSONArray exparr = new JSONArray();
		if(star > 0){//胜利
			String[] awardarr = Tools.splitStr(trialListRs.getString("award"), "|");
			int[][] amarr = Tools.splitStrToIntArr2(trialListRs.getString("star"+star), "|", ",");
			for (int i = 0; i < awardarr.length; i++) {
				if(i > 0){
					awardSb.append("|");
				}
				awardSb.append(awardarr[i]);
				awardSb.append(",");
				int amount = MyTools.getRandom(amarr[i][0], amarr[i][1]);
				String paramStr = CustomActivityBAC.getInstance().getFuncActiPara(CustomActivityBAC.TYPE_TRIAL_EXP_MUL);
				if(paramStr != null){
					int param = Tools.str2int(paramStr);
					amount *= param;
				} 
				awardSb.append(amount);
			}
			exparr = getExpAward(dbHelper, playerid, num, gl);
			remarkSb.append("成功，星级为"+star);
			addTrialTimes(dbHelper, playerid, gl, TYPE_EXP);
		} else{
			remarkSb.append("失败");
		}
		gl.addRemark(remarkSb);
		JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 1, gl);
		PlaWelfareBAC.getInstance().updateTaskProgress(dbHelper, playerid, PlaWelfareBAC.TYPE_TRIAL_EXP, gl);
		JSONArray returnarr = new JSONArray();
		returnarr.add(awardSb.toString());//奖励String
		returnarr.add(awardarr);//背包数据
		returnarr.add(exparr);//历练经验和伙伴经验
		returnarr.add(star);//星级
		return returnarr;
	}
	
	/**
	 * 获取数据
	 */
	public JSONArray getData(int playerid) throws Exception {
		DBPaRs plaTrialRs = PlaRoleBAC.getInstance().getDataRs(playerid);
		if(!plaTrialRs.exist()){
			return null;
		}
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(plaTrialRs.getInt("exptimes"));//当日已挑战次数
		return jsonarr;
	}
	
	//------------------静态区------------------
	
	private static PlaTrialExpBAC instance = new PlaTrialExpBAC();
	
	public static PlaTrialExpBAC getInstance(){
		return instance;
	}

}
