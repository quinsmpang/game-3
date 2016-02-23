package com.moonic.bac;

import java.sql.ResultSet;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.SqlString;
import com.moonic.util.DBHelper;
import com.moonic.util.DBUtil;
import com.moonic.util.FusionChartsCreator;
import com.moonic.util.JsonRs;
import com.moonic.util.MyTools;

/**
 * 市场统计
 * @author John
 */
public class MarketStatBAC {
	//汇总数据
	public static String MARKET_TOTAL_REPORT = "MARKET_TOTAL_REPORT";
	//服务器汇总数据
	public static String MARKET_SERVER_REPORT = "MARKET_SERVER_REPORT";
	//在线人数统计
	public static String TAB_REPORT_ONLINE_NUM = "TAB_REPORT_ONLINE_NUM";
	//数据对比
	public static String MARKET_COMPARE_REPORT = "MARKET_COMPARE_REPORT";
	//每日玩家数据
	public static String MARKET_DAY_REPORT = "MARKET_DAY_REPORT";
	//每日用户数据
	public static String MARKET_USER_REPORT = "MARKET_USER_REPORT";
	//渠道注册数据
	public static String MARKET_CHANNEL_REPORT = "MARKET_CHANNEL_REPORT";
	//充值分析表
	public static String MARKET_INFULL_REPORT = "MARKET_INFULL_REPORT";
	//每日运营数据
	public static String BUSINESS_REPORT = "business_reprort";
	
	/**
	 * 汇总数据
	 */
	public JSONArray getStatData(){
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.addDate("log_date", MyTools.getDateStr(System.currentTimeMillis()-MyTools.long_day), ">=");
			sqlStr.addDate("log_date", MyTools.getDateStr(), "<");
			ResultSet reportRs = dbHelper.query(MARKET_TOTAL_REPORT, null, sqlStr.whereString());
			JSONArray returnarr = new JSONArray();
			if(reportRs.next()){
				returnarr.add(reportRs.getInt("total_real_equ"));
				returnarr.add(reportRs.getInt("total_reg"));
				returnarr.add(reportRs.getInt("total_real_reg"));
				returnarr.add(reportRs.getInt("total_players"));
				returnarr.add(reportRs.getInt("max_level"));
				returnarr.add(reportRs.getInt("max_online_num"));
				returnarr.add(reportRs.getInt("max_activ_num"));
				returnarr.add(reportRs.getInt("seven_activ_num"));
			}
			return returnarr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 在线人数数据
	 */
	public String getOnlineData(PageContext pageContext) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			dbHelper.openConnection();
			ServletRequest request = pageContext.getRequest();
			String channel = Tools.strNull(request.getParameter("channel"));
			int serverId = Tools.str2int(request.getParameter("serverId"));
			String date = Tools.strNull(request.getParameter("date"));
			SqlString sqlStr = new SqlString();
			if(channel!=null && !channel.equals("")){
				sqlStr.add("channel", channel);
			}
			if(serverId != 0){
				sqlStr.add("serverid", serverId);
			}
			long time = MyTools.getCurrentDateLong(MyTools.getTimeLong(date));
			sqlStr.addDateTime("log_date", MyTools.getTimeStr(time), ">=");
			sqlStr.addDateTime("log_date", MyTools.getTimeStr(time+MyTools.long_day), "<");
			ResultSet repportRs = dbHelper.query(TAB_REPORT_ONLINE_NUM, "log_date,sum(online_num) as total_online_num", sqlStr.whereString(), null, "log_date order by log_date");
			//String colors[]={"#CD4F39","#EEC900","#698B22","#FFEC8B","#FF3030","#C1FFC1","#9A32CD","#87CEFF","#008B8B"};
			StringBuffer sb = new StringBuffer();
			sb.append("<graph  caption='当天在线人数' baseFont='宋体' baseFontSize='12'   yAxisMinValue='0' yAxisMaxValue='10' xaxisname='时间' yaxisname='人数' hovercapbg='#87CEFF' hovercapborder='#8B0A50' formatNumberScale='0' decimalPrecision='0' showvalues='1' numdivlines='10' numVdivlines='0' shownames='1' rotateNames='1' drawAnchors='1'  rotateLabels='1' showShadow='0' anchorSides='3'>");
			sb.append("<categories>");
			while(repportRs.next()){
				if((MyTools.getTimeLong(repportRs.getTimestamp("log_date"))-MyTools.getCurrentDateLong())%(MyTools.long_hour/2) == 0){
					sb.append(" <category name='"+repportRs.getString("log_date")+"'/>");		
				}
			}
			sb.append("</categories>");
			sb.append("<dataset seriesName='在线人数' color='#87CEFF' anchorBorderColor='#CD4F39'>");
			repportRs.beforeFirst();
			while(repportRs.next()) {
				if((MyTools.getTimeLong(repportRs.getTimestamp("log_date"))-MyTools.getCurrentDateLong())%(MyTools.long_hour/2) == 0){
					sb.append(" <set value='"+repportRs.getString("total_online_num")+"'/>");
				}
			}
			sb.append("</dataset>");
			sb.append("</graph>");
			String code = FusionChartsCreator.createChart("../Charts/MSLine.swf", "", sb.toString(), "chart1", 950, 450, false, false);
			return code;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 数据对比
	 */
	public JsonRs getCompData(PageContext pageContext) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			ServletRequest request = pageContext.getRequest();
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			if (startDate == null || startDate.equals("")){
				return null;
			}
			if (endDate == null || endDate.equals("")) {
				endDate = MyTools.getDateStr();
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addDate("log_date", startDate, ">=");
			sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(endDate)+MyTools.long_day), "<");
			StringBuffer tgrSb = new StringBuffer();
			tgrSb.append("log_date");
			tgrSb.append(",day_real_equ");
			tgrSb.append(",day_reg");
			tgrSb.append(",day_real_reg");
			tgrSb.append(",day_players");
			tgrSb.append(",max_online_num");
			tgrSb.append(",activ_num");
			tgrSb.append(",activ_num_7");
			tgrSb.append(",avg_online_time");
			StringBuffer orderSb = new StringBuffer();
			orderSb.append("log_date");
			dbHelper.openConnection();
			ResultSet reportRs = dbHelper.query(MARKET_COMPARE_REPORT, tgrSb.toString(), sqlStr.whereString(), orderSb.toString());
			return DBUtil.convertRsToJsonRs(reportRs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 服务器汇总数据
	 */
	public JsonRs getServerStatData(){
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.addDate("log_date", MyTools.getDateStr(System.currentTimeMillis()-MyTools.long_day), ">=");
			sqlStr.addDate("log_date", MyTools.getDateStr(), "<");
			ResultSet reportRs = dbHelper.query(MARKET_SERVER_REPORT, null, sqlStr.whereString(), "serverid");
			return DBUtil.convertRsToJsonRs(reportRs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 每日玩家数据
	 */
	public JsonRs getPlayerDayData(PageContext pageContext) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			ServletRequest request = pageContext.getRequest();
			int serverId = Tools.str2int(request.getParameter("serverId"));
			String channel = request.getParameter("channel");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String statChannel = request.getParameter("statChannel");
			String statServer = request.getParameter("statServer");
			if (startDate == null || startDate.equals("")){
				return null;
			}
			if (endDate == null || endDate.equals("")) {
				endDate = MyTools.getDateStr();
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addDate("log_date", startDate, ">=");
			sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(endDate)+MyTools.long_day), "<");
			if(channel!=null && !channel.equals("")){
				sqlStr.add("channel", channel);
			}
			if(serverId != 0){
				sqlStr.add("serverid", serverId);
			}
			StringBuffer tgrSb = new StringBuffer();
			tgrSb.append("log_date");
			tgrSb.append(",sum(total_player) as sum_total_player");
			tgrSb.append(",sum(login_num) as sum_login_num");
			tgrSb.append(",sum(active_num) as sum_active_num");
			tgrSb.append(",sum(new_player) as sum_new_player");
			tgrSb.append(",sum(max_online_num) as sum_max_online_num");
			tgrSb.append(",sum(total_online_time) as sum_total_online_time");
			tgrSb.append(",avg(avg_online_time) as avg_avg_online_time");
			tgrSb.append(",sum(first_infull_num) as sum_first_infull_num");
			tgrSb.append(",sum(infull_user) as sum_infull_user");
			tgrSb.append(",sum(infull_num) as sum_infull_num");
			tgrSb.append(",sum(used_coin) as sum_used_coin");
			StringBuffer groupSb = new StringBuffer();
			groupSb.append("log_date");
			StringBuffer orderSb = new StringBuffer();
			orderSb.append("log_date");
			if(statChannel!=null){
				tgrSb.append(",channel");
				groupSb.append(",channel");
				orderSb.append(",channel");
			}
			if(statServer!=null){
				tgrSb.append(",serverid");
				groupSb.append(",serverid");
				orderSb.append(",serverid");
			}
			dbHelper.openConnection();
			ResultSet reportRs = dbHelper.query(MARKET_DAY_REPORT, tgrSb.toString(), sqlStr.whereString(), null, groupSb.toString()+" order by "+orderSb.toString());
			return DBUtil.convertRsToJsonRs(reportRs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 每日用户数据
	 */
	public JsonRs getUserDayData(PageContext pageContext) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			ServletRequest request = pageContext.getRequest();
			String channel = request.getParameter("channel");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String statChannel = request.getParameter("statChannel");
			if (startDate == null || startDate.equals("")){
				return null;
			}
			if (endDate == null || endDate.equals("")) {
				endDate = MyTools.getDateStr();
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addDate("log_date", startDate, ">=");
			sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(endDate)+MyTools.long_day), "<");
			if(channel!=null && !channel.equals("")){
				sqlStr.add("channel", channel);
			}
			StringBuffer tgrSb = new StringBuffer();
			tgrSb.append("log_date");
			tgrSb.append(",sum(day_equ) as sum_day_equ");
			tgrSb.append(",sum(day_reg) as sum_day_reg");
			tgrSb.append(",sum(day_real_reg) as sum_day_real_reg");
			StringBuffer groupSb = new StringBuffer();
			groupSb.append("log_date");
			StringBuffer orderSb = new StringBuffer();
			orderSb.append("log_date");
			if(statChannel != null){
				tgrSb.append(",channel");
				groupSb.append(",channel");
				orderSb.append(",channel");
			}
			dbHelper.openConnection();
			ResultSet reportRs = dbHelper.query(MARKET_USER_REPORT, tgrSb.toString(), sqlStr.whereString(), null, groupSb.toString()+" order by "+orderSb.toString());
			return DBUtil.convertRsToJsonRs(reportRs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 渠道注册比例分析
	 */
	public JSONArray statChannelReg(HttpServletRequest req, HttpServletResponse res) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			String startDate = req.getParameter("startDate");
			String endDate = req.getParameter("endDate");
			SqlString sqlStr = new SqlString();
			if(startDate!=null && !startDate.equals("")){
				sqlStr.addDate("log_date", startDate, ">=");	
			}
			if(endDate!=null && !endDate.equals("")){
				sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(endDate)+MyTools.long_day), "<");
			}
			ResultSet reportRs = dbHelper.query(MARKET_CHANNEL_REPORT, "channel_name,sum(day_reg) as sum_day_reg", sqlStr.whereString(), null, "channel_name order by sum_day_reg desc");
			
			StringBuffer sb1 = new StringBuffer();
			sb1.append("<chart caption='渠道注册比例分析'  palette='3' showAlternateHGridColor='1'  animation='1' showAboutMenuItem='0'  formatNumberScale='0' pieYScale='30'showLabels='1'       pieSliceDepth='20' startingAngle='10' baseFont='宋体'  baseFontSize='14' bgColor='FFFFFF' shadowAlpha='100'   showValues='1'   canvasBgColor='FFFFFF' showPercentageInLabel='1'showAboutMenuIte='1' showLegend='1'legendIconScale='0'>");
			while(reportRs.next()) {
				sb1.append("<set label='" + reportRs.getString("channel_name") + ": 总数 "+reportRs.getInt("sum_day_reg")+"' value='" + reportRs.getInt("sum_day_reg")+ "' isSliced='1'/>");
			}
			sb1.append("<styles>");
			sb1.append("<definition>");
			sb1.append("<style type='font' name='CaptionFont' size='15' color='666666' />");
			sb1.append("<style type='font' name='SubCaptionFont' bold='0' />");
			sb1.append("</definition><application>");
			sb1.append("<apply toObject='caption' styles='CaptionFont'/>");
			sb1.append("<apply toObject='SubCaption' styles='SubCaptionFont' />");
			sb1.append("</application>");
			sb1.append("</styles>");
			sb1.append("</chart>");
			String code1 = FusionChartsCreator.createChart("../Charts/Pie2D.swf", "", sb1.toString(), "loginArea", 550,550, false, false);
			
			StringBuffer sb2 = new StringBuffer();
			//String colors[]={"#CD4F39","#EEC900","#698B22","#FFEC8B","#FF3030","#C1FFC1","#9A32CD","#87CEFF","#008B8B"};
			sb2.append("<graph  caption='渠道注册比例分析' baseFont='宋体' baseFontSize='12'   yAxisMinValue='0' yAxisMaxValue='10' xaxisname='渠道 ' yaxisname='总额' hovercapbg='#87CEFF' hovercapborder='#8B0A50' formatNumberScale='0' decimalPrecision='0' showvalues='1'numdivlines='10' numVdivlines='0' shownames='1' rotateNames='1' drawAnchors='1'  rotateLabels='1' showShadow='0' anchorSides='3'  >");
			sb2.append("<categories>");
			reportRs.beforeFirst();
			while(reportRs.next()){
				sb2.append(" <category name='"+reportRs.getString("channel_name")+"'/>");
			}
			sb2.append("</categories>");
			sb2.append("<dataset >");
			reportRs.beforeFirst();
			while(reportRs.next()){
				sb2.append(" <set value='"+reportRs.getString("sum_day_reg")+"'/>");																 																						
			}
			sb2.append("</dataset>");
			sb2.append("</graph>");
			String code2 = FusionChartsCreator.createChart(  "../Charts/MSBar3D.swf", "", sb2.toString(), "chargeBar3D", 550, 550, false, false);
			
			JSONArray returnarr = new JSONArray();
			returnarr.add(code1);
			returnarr.add(code2);
			return returnarr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 充值分析表
	 */
	public JsonRs getChargeDayData(PageContext pageContext) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			ServletRequest request = pageContext.getRequest();
			String channel = request.getParameter("channel");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String statChannel = request.getParameter("statChannel");
			if (startDate == null || startDate.equals("")){
				return null;
			}
			if (endDate == null || endDate.equals("")) {
				endDate = MyTools.getDateStr();
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addDate("log_date", startDate, ">=");
			sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(endDate)+MyTools.long_day), "<");
			if(channel!=null && !channel.equals("")){
				sqlStr.add("channel", channel);
			}
			StringBuffer tgrSb = new StringBuffer();
			tgrSb.append("log_date");
			tgrSb.append(",sum(day_real_equ) as sum_day_real_equ");
			tgrSb.append(",sum(day_real_reg) as sum_day_real_reg");
			tgrSb.append(",sum(first_infull_num) as sum_first_infull_num");
			tgrSb.append(",sum(active_num) as sum_active_num");
			tgrSb.append(",sum(infull_user) as sum_infull_user");
			tgrSb.append(",sum(infull_num) as sum_infull_num");
			StringBuffer groupSb = new StringBuffer();
			groupSb.append("log_date");
			StringBuffer orderSb = new StringBuffer();
			orderSb.append("log_date");
			if(statChannel != null){
				tgrSb.append(",channel");
				groupSb.append(",channel");
				orderSb.append(",channel");
			}
			dbHelper.openConnection();
			ResultSet reportRs = dbHelper.query(MARKET_INFULL_REPORT, tgrSb.toString(), sqlStr.whereString(), null, groupSb.toString()+" order by "+orderSb.toString());
			return DBUtil.convertRsToJsonRs(reportRs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 每日运营数据
	 */
	public JsonRs[] getBusinessDayData(PageContext pageContext) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			ServletRequest request = pageContext.getRequest();
			String channel = request.getParameter("channel");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String statChannel = request.getParameter("statChannel");
			if (startDate == null || startDate.equals("")){
				return null;
			}
			if (endDate == null || endDate.equals("")) {
				endDate = MyTools.getDateStr();
			}
			SqlString sqlStr1 = new SqlString();
			sqlStr1.addDate("createtime", startDate, ">=");
			sqlStr1.addDate("createtime", MyTools.getDateStr(MyTools.getTimeLong(endDate)+MyTools.long_day), "<");
			if(channel!=null && !channel.equals("")){
				sqlStr1.add("channel", channel);
			}
			ResultSet reportRs0 = dbHelper.query(BUSINESS_REPORT, "createtime", sqlStr1.whereString(), null, "createtime");
			reportRs0.last();
			int day = reportRs0.getRow();
			if(day <= 0){
				return null;
			}
			String lasttime = MyTools.getDateStr();
			ResultSet reportRs1 = dbHelper.query(BUSINESS_REPORT, "max(createtime) as createtime", sqlStr1.whereString());
			if(!reportRs1.next()){
				return null;
			}
			lasttime = MyTools.getDateStr(reportRs1.getTimestamp("createtime"));
			SqlString sqlStr2 = new SqlString();
			sqlStr2.addDate("createtime", lasttime);
			if(channel!=null && !channel.equals("")){
				sqlStr2.add("channel", channel);
			}
			StringBuffer tgrSb2 = new StringBuffer();
			tgrSb2.append("sum(total_acti) as total_acti");
			tgrSb2.append(",sum(total_reg) as total_reg");
			tgrSb2.append(",sum(total_realreg) as total_realreg");
			tgrSb2.append(",sum(total_player) as total_player");
			tgrSb2.append(",sum(active_num) as active_num");
			tgrSb2.append(",sum(total_infull_user) as total_infull_user");
			tgrSb2.append(",sum(total_infull_num) as total_infull_num");
			if(statChannel != null){
				tgrSb2.append(",channel");
			}
			String groupStr2 = null;
			if(statChannel != null){
				groupStr2 = "channel order by total_acti desc";
			}
			ResultSet reportRs2 = dbHelper.query(BUSINESS_REPORT, tgrSb2.toString(), sqlStr2.whereString(), null, groupStr2);
			StringBuffer tgrSb3 = new StringBuffer();
			tgrSb3.append("sum(channel_rate)/"+day+" as channel_rate");
			tgrSb3.append(",sum(acti) as acti");
			tgrSb3.append(",sum(reg) as reg");
			tgrSb3.append(",sum(realreg) as realreg");
			tgrSb3.append(",sum(new_player) as new_player");
			tgrSb3.append(",sum(first_infull_num) as first_infull_num");
			tgrSb3.append(",sum(infull_num) as infull_num");
			tgrSb3.append(",sum(new_equip) as new_equip");
			tgrSb3.append(",sum(stay_num1) as stay_num1");
			tgrSb3.append(",sum(stay_num7) as stay_num7");
			if(statChannel != null){
				tgrSb3.append(",channel");
			}
			String groupStr3 = null;
			if(statChannel != null){
				groupStr3 = "channel order by channel";
			}
			dbHelper.openConnection();
			ResultSet reportRs3 = dbHelper.query(BUSINESS_REPORT, tgrSb3.toString(), sqlStr1.whereString(), null, groupStr3);
			return new JsonRs[]{DBUtil.convertRsToJsonRs(reportRs2), DBUtil.convertRsToJsonRs(reportRs3)};
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	private static MarketStatBAC instance = new MarketStatBAC();
	
	public static MarketStatBAC getInstance() {		
		return instance;
	}
}
