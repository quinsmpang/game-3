package com.moonic.bac;

import java.sql.ResultSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.moonic.servlet.STSServlet;
import com.moonic.util.DBHelper;
import com.moonic.util.STSNetSender;

/**
 * 排行
 * @author John
 */
public class RankingBAC {
	
	/**
	 * 刷新排名
	 */
	public ReturnValue refreshRanking(long refreshtime){
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		try {
			dbHelper.openConnection();
			JSONObject returnobj = new JSONObject();//TODO SID-DATA(K-V)
			
			//战力排行
			String sql1 = "SELECT A.SERVERID,A.PLAYERID,A.TOTALBATTLEPOWER,A.RANK,B.NAME,B.NUM,B.LV FROM (SELECT SERVERID,PLAYERID,TOTALBATTLEPOWER,ROW_NUMBER() OVER(PARTITION BY SERVERID ORDER BY TOTALBATTLEPOWER DESC) RANK FROM TAB_PLA_ROLE WHERE TOTALBATTLEPOWER>=1000) A LEFT JOIN TAB_PLAYER B ON A.PLAYERID=B.ID WHERE A.RANK<=50 ORDER BY RANK";
			ResultSet rankingRs1 = dbHelper.executeQuery(sql1);
			while(rankingRs1.next()){
				JSONArray arr = new JSONArray();
				arr.add(rankingRs1.getInt("playerid"));
				arr.add(rankingRs1.getString("name"));
				arr.add(rankingRs1.getInt("lv"));
				arr.add(rankingRs1.getInt("num"));
				arr.add(rankingRs1.getInt("rank"));
				arr.add(rankingRs1.getInt("totalbattlepower"));
				String sidStr = rankingRs1.getString("serverid");
				if(sidStr == null || sidStr.equals("")){
					continue;
				}
				JSONArray sidarr = returnobj.optJSONArray(sidStr);
				if(sidarr == null){
					sidarr = createReturnJsonarr();
					returnobj.put(sidStr, sidarr);
				}
				sidarr.optJSONArray(0).add(arr);
			}
			
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = returnobj.keys();
			while(iterator.hasNext()){
				String sidStr = iterator.next();
				STSNetSender sender = new STSNetSender(STSServlet.G_REFRESH_GAME_RANKING);
				sender.dos.writeLong(refreshtime);
				sender.dos.writeUTF(returnobj.optJSONArray(sidStr).toString());
				ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, Tools.str2int(sidStr));
			}
			return new ReturnValue(true, "刷新完成");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 创建返回结构
	 */
	public JSONArray createReturnJsonarr(){
		JSONArray returnarr = new JSONArray();
		returnarr.add(new JSONArray());
		return returnarr;
	}
	
	//--------------静态区---------------
	
	private static RankingBAC instance = new RankingBAC();
	
	/**
	 * 获取实例
	 */
	public static RankingBAC getInstance() {
		return instance;
	}
}
