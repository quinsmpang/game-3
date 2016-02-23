package com.moonic.bac;

import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.MyPreparedStatement;
import com.moonic.util.DBHelper;

/**
 * 补充数据
 * @author John
 */
public class SuppDataMgrBAC {
	private String[] all_tables = {
			"tab_pla_faction",
			"tab_pla_role",
			"tab_pla_welfare",
			"tab_pla_supply", 
			"tab_pla_summon",
			"tab_pla_shop"
	};
	
	/**
	 * 补充所有表数据
	 */
	public ReturnValue suppPlaAll(){
		return suppPla(all_tables);
	}
	
	/**
	 * 补充指定表数据
	 */
	public ReturnValue suppPla(String tablesStr){
		String[] tables = tablesStr.split(",");
		return suppPla(tables);
	}
	
	/**
	 * 补充指定表数据
	 */
	public ReturnValue suppPla(String[] tables){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection(false);
			StringBuffer sb = new StringBuffer("补充结果（不需要补充数据的表未被显示）：");
			for(int i = 0; i < tables.length; i++){
				try {
					String sql = "select * from (select a.*,b.playerid from tab_player a left join "+tables[i]+" b on a.id=b.playerid) where playerid is null";
					MyPreparedStatement stmt = dbHelper.getStmt(sql);
					ResultSet dataRs = stmt.executeQuery();
					int totalamount = 0;
					int amount = 0;
					while(dataRs.next()){
						int playerid = dataRs.getInt("id");
						SqlString sqlStr = new SqlString();
						if(tables[i].equals("tab_pla_faction")){
							sqlStr.add("playerid", playerid);
						} else 
						if(tables[i].equals("tab_pla_role")){
							sqlStr.add("playerid", playerid);
						} else
						if(tables[i].equals("tab_pla_item")){
							sqlStr.add("playerid", playerid);
							sqlStr.add("bagzone", 24);
							sqlStr.add("storzone", 24);
						} else
						if(tables[i].equals("tab_pla_welfare")){
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
						} else
						if(tables[i].equals("tab_pla_summon")){
							sqlStr.add("playerid", playerid);
							sqlStr.add("daily1", 0);
							sqlStr.add("total1", 0);
							sqlStr.add("daily2", 0);
							sqlStr.add("single2", 0);
							sqlStr.add("total2", 0);
							sqlStr.add("multi2", 0);
							sqlStr.add("summonprop", 0);
						} else
						if(tables[i].equals("tab_pla_supply")){
							sqlStr.add("playerid", playerid);
							sqlStr.add("buymoneyam", 0);
							sqlStr.add("buyenergyam", 0);
							sqlStr.add("gettqaward", 0);
						} else 
						if(tables[i].equals("tab_extension_agent")){
							sqlStr.add("userid", dataRs.getString("userid"));
							sqlStr.add("playerid", playerid);
							sqlStr.add("serverid", dataRs.getInt("serverid"));
							sqlStr.add("inviteamount", 0);
							sqlStr.add("invitemepid", 0);
							sqlStr.add("invitemesid", 0);
							sqlStr.add("getgiftamount", (new JSONObject()).toString());
						} else
						if(tables[i].equals("tab_pla_shop")){
							sqlStr.add("playerid", playerid);
							sqlStr.add("item1", "[]");
							sqlStr.add("buy1", "[]");
							sqlStr.add("times1", 0);
							sqlStr.add("item2", "[]");
							sqlStr.add("buy2", "[]");
							sqlStr.add("conenergy", -1);
							sqlStr.add("item3", "[]");
							sqlStr.add("buy3", "[]");
							sqlStr.add("times3", 0);
							sqlStr.add("item4", "[]");
							sqlStr.add("buy4", "[]");
							sqlStr.add("times4", 0);
							sqlStr.add("item5", "[]");
							sqlStr.add("buy5", "[]");
							sqlStr.add("times5", 0);
							sqlStr.add("item6", "[]");
							sqlStr.add("buy6", "[]");
							sqlStr.add("times6", 0);
						} else
						{
							sb.append("<font color='#ff0000'>--出错 没有相应的处理方法</font>\r\n");
							continue;
						}
						dbHelper.insert(tables[i], sqlStr);
						amount++;
						if(amount >= 50){
							dbHelper.commit();
							amount = 0;
						}
						//sb.append(dataRs.getString("name")+"("+playerid+")\r\n");
						totalamount++;
					}
					dbHelper.commit();
					if(totalamount > 0){
						sb.append("表 "+tables[i].toUpperCase()+" 补充"+totalamount+"条数据\r\n");		
					}
				} catch (Exception e) {
					sb.append("<font color='#ff0000'>--出错 e="+e.toString()+" table="+tables[i]+"</font>\r\n");
				}
				sb.append("\r\n");
			}
			return new ReturnValue(true, sb.toString());
		} catch (Exception e){
			e.printStackTrace();
			dbHelper.rollback();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	private static SuppDataMgrBAC instance = new SuppDataMgrBAC();
	
	/**
	 * 获取实例
	 */
	public static SuppDataMgrBAC getInstance(){
		return instance;
	}
}
