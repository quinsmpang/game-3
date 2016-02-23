package com.moonic.bac;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.SqlString;
import com.moonic.util.DBHelper;

/**
 * 指令统计
 * @author John
 */
public class ActStatBAC {
	
	/**
	 * 指令统计
	 */
	public JSONArray statAct(PageContext pageContext) {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		SqlString sqlS = new SqlString();
		if(startTime!=null && !startTime.equals(""))
		{
			sqlS.addDateTime("tab_http_log.reqTime", startTime,">=");
		}
		if(endTime!=null && !endTime.equals(""))
		{
			sqlS.addDateTime("tab_http_log.reqTime",endTime,"<=");
		}
		DBHelper dbHelper = new DBHelper();
		JSONArray array = new JSONArray();
		try {
			dbHelper.openConnection();
			JSONObject times = new JSONObject();
			String sql = "select act,count(tab_http_log.act) as amount from tab_http_log "+sqlS.whereStringEx()+" group by act";
			ResultSet rs = dbHelper.executeQuery(sql);
			while(rs!=null && rs.next())
			{
				String act = rs.getString("act");
				int t = rs.getInt("amount");
				times.put(act, t);
			}
			sql = "select tab_http_log.act,tab_game_func.name as actname,sum(usedtime) as totaltime from tab_http_log left join tab_game_func on tab_http_log.act =tab_game_func.code "+sqlS.whereStringEx()+"  group by tab_http_log.act,tab_game_func.name order by sum(usedtime) desc";
			rs = dbHelper.executeQuery(sql);
			while(rs!=null && rs.next())
			{
				JSONObject line = new JSONObject();
				String act = rs.getString("act");
				line.put("act", act);
				line.put("actname", rs.getString("actname"));
				line.put("totaltime", rs.getInt("totaltime"));
				int t = times.optInt(act);
				line.put("times", t);
				array.add(line);
			}
			return array;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			dbHelper.closeConnection();
		}
	}
	
	//------------------静态区-------------------
	
	private static ActStatBAC instance = new ActStatBAC();
		
	public static ActStatBAC getInstance() {		
		return instance;
	}
}
