package com.moonic.bac;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;

import conf.LogTbName;

/**
 * 用户登录日志
 * @author John
 */
public class UserLoginLogBAC extends BaseActCtrl {
	
	/**
	 * 构造
	 */
	public UserLoginLogBAC(){
		super.setTbName(LogTbName.TAB_USER_LOGIN_LOG());
		setDataBase(ServerConfig.getDataBase_Log());
	}
	
	/**
	 * 用户登录日志
	 */
	public JSONObject getUserLoginLogList(PageContext pageContext) {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String userName = request.getParameter("username");
		String mac = request.getParameter("mac");
		String imei = request.getParameter("imei");
		int page = Tools.str2int(request.getParameter("page"));
		int rowsPerPage = 10;
		SqlString sqlStr = new SqlString();
		if(userName != null && !"".equals(userName)){
			sqlStr.add("username", userName);
		}
		if(startDate != null && !"".equals(startDate)){
			sqlStr.addDate("logintime", startDate, ">=");
		}
		if(endDate != null && !"".equals(endDate)){
			sqlStr.addDate("logintime", endDate, "<=");
		}
		if(mac != null && !mac.equals("")) {
			sqlStr.add("mac", mac);
		}
		if(imei != null && !imei.equals("")) {
			sqlStr.add("imei", imei);
		}
		String sql = "select * from " + LogTbName.TAB_USER_LOGIN_LOG() + " " + sqlStr.whereStringEx() + " order by id desc";
		JSONObject Obj = getJsonPageListBySQL(sql, page, rowsPerPage);
		return Obj;
	}
	
	public static String getNetWorkType(int type) {
		String typeStr = "";
		switch (type) {
		case 0:
			typeStr = "未知";
			break;
		case 1:
			typeStr = "GPRS";
			break;
		case 2:
			typeStr = "EDGE";
			break;
		case 3:
			typeStr = "UMTS";
			break;
		case 4:
			typeStr = "CDMA";
			break;
		case 5:
			typeStr = "EVDO_0";
			break;
		case 6:
			typeStr = "EVDO_A";
			break;
		case 7:
			typeStr = "1xRTT";
			break;
		case 8:
			typeStr = "HSDPA";
			break;
		case 9:
			typeStr = "HSUPA";
			break;
		case 10:
			typeStr = "HSPA";
			break;
		}
		return typeStr;
	}
	
	//--------------静态区--------------
	
	private static UserLoginLogBAC instance = new UserLoginLogBAC();
	
	/**
	 * 获取实例
	 */
	public static UserLoginLogBAC getInstance(){
		return instance;
	}
}
