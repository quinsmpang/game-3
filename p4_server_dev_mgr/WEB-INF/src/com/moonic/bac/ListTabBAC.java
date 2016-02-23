package com.moonic.bac;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;
import org.json.JSONObject;

import server.config.ServerConfig;

import com.ehc.dbc.BaseActCtrl;
import com.moonic.util.DBHelper;

/**
 * 数据表查看
 * @author John
 */
public class ListTabBAC extends BaseActCtrl {
	public static final String tab_listtab = "tab_listtab";
	
	/**
	 * 获取文件列表
	 */
	public File[] getFileList(){
		File dir = new File(ServerConfig.getWebInfPath()+"res/tab_list");
		File[] files = dir.listFiles();
		return files;
	}
	
	/**
	 * 获取数据表数据
	 */
	public JSONArray getTabData(PageContext pageContext){
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		
		String tabname = request.getParameter("tablist");
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			
			String sql = "select * from user_col_comments where table_name='"+tabname.toUpperCase()+"'";
			
			ResultSet commentRs = dbHelper.executeQuery(sql);
			String key = null;
			JSONObject commentobj = new JSONObject();
			while(commentRs.next()){
				if(key == null){
					key = commentRs.getString("column_name").toLowerCase();
				}
				commentobj.put(commentRs.getString("column_name").toLowerCase(), commentRs.getString("comments"));
			}
			
			ResultSet rs = dbHelper.query(tabname, null, null, "id");
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			
			JSONArray dataarr = new JSONArray();
			
			JSONArray colarr = new JSONArray();
			for (int i = 1; i <= colCount; i++) {
				String colname = rsmd.getColumnName(i);
				String colcoment = commentobj.optString(colname);
				if(colcoment == null || colcoment.equals("")){
					colcoment = colname;
				}
				colarr.add(colcoment);
			}
			dataarr.add(colarr);
			
			while(rs.next()){
				JSONArray one = new JSONArray();
				for (int i = 1; i <= colCount; i++) {
					one.add(rs.getString(i));
				}
				dataarr.add(one);
			}
			
			return dataarr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区---------------
	
	private static ListTabBAC instance = new ListTabBAC();
	
	/**
	 * 获取实例
	 */
	public static ListTabBAC getInstance() {
		return instance;
	}
}
