package com.moonic.bac;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.txtdata.ArtifactData;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 角色人物
 * @author John
 */
public class PlaRoleBAC extends PlaBAC {
	
	/**
	 * 构造
	 */
	private PlaRoleBAC() {
		super("tab_pla_role", "playerid");
	}

	/**
	 * 初始化
	 */
	public void init(DBHelper dbHelper, int playerid, Object... param) throws Exception {
		DBPaRs playeruplvRs = DBPool.getInst().pQueryA(PlayerBAC.tab_player_uplv, "lv=1");
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("serverid", Conf.sid);
		sqlStr.add("energy", playeruplvRs.getInt("maxenergy"));
		sqlStr.addDateTime("energystarttime", MyTools.getTimeStr());
		sqlStr.add("soulpoint", 0);
		sqlStr.add("eqsmeltfailam", "[]");
		sqlStr.add("jjccoin", 0);
		sqlStr.add("artifactdata", "{}");
		sqlStr.add("towercoin", 0);
		sqlStr.add("artifactrobtimes", 0);
		sqlStr.add("artifactprotecttimes", 0);
		sqlStr.add("moneytimes", 0);
		sqlStr.add("exptimes", 0);
		sqlStr.add("partnertimes", "{}");
		sqlStr.add("present", "[]");
		sqlStr.add("bepresent", "{}");
		sqlStr.add("deletefriend", "[]");
		sqlStr.add("totalbattlepower", 0);
		insert(dbHelper, playerid, sqlStr);
	}
	
	/**
	 * 恢复体力
	 */
	public ReturnValue recoverEnergy(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_ROLE_RECOVERENERGY);
			int recoveramount = recoverEnergy(dbHelper, playerid, gl);
			
			gl.save();
			return new ReturnValue(true, String.valueOf(recoveramount));
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 恢复神器碎片抢夺次数
	 */
	public ReturnValue recoverArtifactRobTimes(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_ARTIFACT_RECOVERROBTIMES);
			int recoveramount = recoverArtifactRobTimes(dbHelper, playerid, gl);
			
			gl.save();
			return new ReturnValue(true, String.valueOf(recoveramount));
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 恢复体力
	 */
	public int recoverEnergy(DBHelper dbHelper, int playerid, GameLog gl) throws Exception {
		DBPaRs dataRs = getDataRs(playerid);
		int energy = dataRs.getInt("energy");
		int maxenergy = PlayerBAC.getInstance().getMaxEnergy(playerid);
		if(energy >= maxenergy){
			return 0;
		}
		long starttime = dataRs.getTime("energystarttime");//起始计算时间
		long nowtime = System.currentTimeMillis();//当前时间
		long offtime = nowtime - starttime;//可用时长
		long timelen = MyTools.long_minu * 6;//恢复一组的时间
		int group = (int) (offtime / timelen);//增加的组数
		if(group < 0){
			return 0;
		}
		int oldenergy = energy;
		energy += group;
		if(energy > maxenergy){
			energy = maxenergy;
		}
		SqlString sqlStr = new SqlString();
		sqlStr.add("energy", energy);
		sqlStr.addDateTime("energystarttime", MyTools.getTimeStr(starttime + timelen * group));
		update(dbHelper, playerid, sqlStr);
		
		int recoveramount = energy - oldenergy;
		gl.addChaNote("体力", oldenergy, recoveramount);
		gl.addRemark("累积时长："+(group*timelen/60000)+"分钟");
		return recoveramount;
	}
	
	/**
	 * 恢复抢夺次数
	 */
	public int recoverArtifactRobTimes(DBHelper dbHelper, int playerid, GameLog gl) throws Exception {
		DBPaRs dataRs = getDataRs(playerid);
		int times = dataRs.getInt("artifactrobtimes");
		if(times >= ArtifactData.maxrobtimes){
			return 0;
		}
		long starttime = dataRs.getTime("artifactrobstarttime");//起始计算时间
		long nowtime = System.currentTimeMillis();//当前时间
		long offtime = nowtime - starttime;//可用时长
		long timelen = ArtifactData.robrecovertimelen;//恢复一组的时间
		int group = (int) (offtime / timelen);//增加的组数
		if(group < 0){
			return 0;
		}
		int oldtimes = times;
		times += group;
		if(times > ArtifactData.maxrobtimes){
			times = ArtifactData.maxrobtimes;
		}
		SqlString sqlStr = new SqlString();
		sqlStr.add("artifactrobtimes", times);
		sqlStr.addDateTime("artifactrobstarttime", MyTools.getTimeStr(starttime + timelen * group));
		update(dbHelper, playerid, sqlStr);
		
		int recoveramount = times - oldtimes;
		gl.addChaNote("神器碎片抢夺次数", oldtimes, recoveramount);
		gl.addRemark("累积时长："+(group*timelen/60000)+"分钟");
		return recoveramount;
	}
	
	/**
	 * 升级操作
	 */
	public void upLevelOperate(DBHelper dbHelper, int playerid, int oldlv, int newlv, GameLog gl) throws Exception {
		DBPaRs plaroleRs = getDataRs(playerid);
		int oldenergy = plaroleRs.getInt("energy");
		int addenergy = 0;
		DBPsRs playeruplvRs = DBPool.getInst().pQueryS(PlayerBAC.tab_player_uplv, "lv>"+oldlv+" and lv<="+newlv);
		while(playeruplvRs.next()){
			addenergy += playeruplvRs.getInt("addenergy");
		}
		SqlString sqlStr = new SqlString();
		sqlStr.addChange("energy", addenergy);
		update(dbHelper, playerid, sqlStr);
		gl.addChaNote("体力", oldenergy, addenergy);
	}
	
	/**
	 * 减值回调
	 */
	public void subTrigger(DBHelper dbHelper, int playerid, String col, long srcVal, long nowVal) throws Exception {
		if(col.equals("energy")){
			int maxenergy = PlayerBAC.getInstance().getMaxEnergy(playerid);
			if(srcVal >= maxenergy && nowVal < maxenergy){
				SqlString sqlStr = new SqlString();
				sqlStr.addDateTime("energystarttime", MyTools.getTimeStr());
				update(dbHelper, playerid, sqlStr);
			}
//			PlaMysteryShopBAC.getInstance().handleEnergyConsume(dbHelper, playerid, (int)(srcVal-nowVal));
		} else 
		if(col.equals("artifactrobtimes")){
			if(srcVal >= ArtifactData.maxrobtimes && nowVal < ArtifactData.maxrobtimes){
				SqlString sqlStr = new SqlString();
				sqlStr.addDateTime("artifactrobstarttime", MyTools.getTimeStr());
				update(dbHelper, playerid, sqlStr);
			}
		}
	}

	/**
	 * 获取数据
	 */
	public JSONArray getLoginData(int playerid) throws Exception {
		DBPaRs plaroleRs = getDataRs(playerid);
		JSONArray arr = new JSONArray();
		arr.add(plaroleRs.getInt("energy"));//体力
		arr.add(plaroleRs.getTime("energystarttime"));//上次恢复体力时间
		arr.add(plaroleRs.getInt("soulpoint"));//魂点
		arr.add(new JSONArray(plaroleRs.getString("eqsmeltfailam")));//装备熔炼失败次数
		arr.add(plaroleRs.getInt("jjccoin"));//竞技币
		arr.add(new JSONObject(plaroleRs.getString("artifactdata")));//神器数据
		arr.add(plaroleRs.getInt("towercoin"));//塔币
		arr.add(plaroleRs.getTime("artifactendtime"));//神器碎片抢夺保护结束时间
		arr.add(plaroleRs.getInt("artifactrobtimes"));//神器碎片可抢夺次数
		arr.add(plaroleRs.getInt("artifactprotecttimes"));//开启神器碎片抢夺保护次数
		arr.add(plaroleRs.getTime("artifactrobstarttime"));;//结算抢夺次数起始时间
		arr.add(new JSONArray(plaroleRs.getString("present")));//赠送好友数据
		arr.add(new JSONObject(plaroleRs.getString("bepresent")));//被好友赠送数据
		return arr;
	}
	
	/**
	 * 重置数据
	 */
	public void resetData(DBHelper dbHelper, int playerid) throws Exception {
		SqlString sqlStr = new SqlString();
		sqlStr.add("artifactprotecttimes", 0);
		sqlStr.add("moneytimes", 0);
		sqlStr.add("exptimes", 0);
		sqlStr.add("partnertimes", "{}");
		sqlStr.add("present", "[]");
		update(dbHelper, playerid, sqlStr);
	}
	//--------------静态区--------------
	
	private static PlaRoleBAC instance = new PlaRoleBAC();
	
	/**
	 * 获取实例
	 */
	public static PlaRoleBAC getInstance(){
		return instance;
	}
}
