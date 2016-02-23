package com.moonic.bac;

import org.json.JSONArray;

import com.ehc.common.ReturnValue;
import com.moonic.util.ConfFile;
import com.moonic.util.DBHelper;

/**
 * 排行
 * @author John
 */
public class RankingBAC {
	private static final String FILENAME_BATTLEPOWERRANKING = "battlepower_ranking";
	public static JSONArray battlepowerranking_data;
	
	static {
		try {
			battlepowerranking_data = new JSONArray(ConfFile.getFileValueInStartServer(FILENAME_BATTLEPOWERRANKING, "[]"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取战力排行
	 */
	public ReturnValue getBattlePowerRanking(int playerid){
		try {
			return new ReturnValue(true, battlepowerranking_data.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 刷新排行榜数据
	 */
	public ReturnValue refreshRanking(long refreshtime, String data){
		DBHelper dbHelper = new DBHelper();
		try {
			JSONArray datarr = new JSONArray(data);
			//TODO 刷新本地数据
			
			//天梯排行
			try {
				battlepowerranking_data = datarr.optJSONArray(0);
				ConfFile.updateFileValue(FILENAME_BATTLEPOWERRANKING, battlepowerranking_data.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return new ReturnValue(true, "刷新完成");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	private static RankingBAC instance = new RankingBAC();
	
	/**
	 * 获取实例
	 */
	public static RankingBAC getInstance(){
		return instance;
	}
}
