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
import com.moonic.mgr.TabStor;
import com.moonic.util.DBHelper;
import com.moonic.util.DBUtil;
import com.moonic.util.FusionChartsCreator;
import com.moonic.util.JsonRs;
import com.moonic.util.MyTools;

/**
 * 充值统计
 * @author John
 */
public class ChargeStatBAC {
	//充值走势图
	public static String INFULL_TREND_REPORT = "INFULL_TREND_REPORT";
	//充值排行
	public static String INFULL_ORDER_REPORT = "INFULL_ORDER_REPORT";
	//渠道充值数据
	public static String INFULL_CHANNEL_REPORT = "INFULL_CHANNEL_REPORT";
	//地域充值数据
	public static String INFULL_AREA_REPORT = "INFULL_AREA_REPORT";
	//综合充值数据
	public static String INFULL_ANALYSE_REPORT = "INFULL_ANALYSE_REPORT";
	
	/**
	 * 充值走势图
	 */
	public JSONArray getChargeRunChart(PageContext pageContext) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			String channel = request.getParameter("channel");//联运渠道
			int serverId = Tools.str2int(request.getParameter("serverId"));//指定服务器
			String startdate = request.getParameter("startdate");//起始时间
			int stattype = Tools.str2int(request.getParameter("stattype"));//统计方式
			if (startdate == null || startdate.equals("")) {
				startdate = MyTools.getDateStr();
			}
			String enddate = request.getParameter("enddate");//终止时间
			if (enddate == null || enddate.equals("")) {
				enddate = MyTools.getDateStr();
			}
			String pname = request.getParameter("pname");//玩家名
			SqlString sqlStr = new SqlString();
			if(channel != null && !channel.equals("")){
				sqlStr.add("channel", channel);
			}
			if(serverId > 0){
				sqlStr.add("serverid", serverId);
			}
			sqlStr.addDate("log_date", startdate, ">=");
			sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(enddate)+MyTools.long_day), "<");
			if(pname != null && !pname.equals("")){
				sqlStr.add("user_name", pname);
				sqlStr.add("type", 1);
			} else {
				sqlStr.add("type", 2);
			}
			
			dbHelper.openConnection();
			String formatStr = null;
			if(stattype == 0){
				formatStr = "to_char(log_date,'yyyy-MM-dd')";
			} else 
			if(stattype == 1){
				formatStr = "to_char(log_date,'yyyy-MM')";
			} else 
			if(stattype == 2){
				formatStr = "to_char(log_date,'yyyy')";
			}
			StringBuffer tgrSb = new StringBuffer();
			tgrSb.append(formatStr);
			tgrSb.append(" as time,sum(user_num) as total_user_num,sum(infull_num) as total_infull_num");
			ResultSet reportRs = dbHelper.query(INFULL_TREND_REPORT, tgrSb.toString(), sqlStr.whereString(), formatStr, formatStr);
			StringBuffer lineSb = new StringBuffer();
			StringBuffer dataSb1 = new StringBuffer();
			StringBuffer dataSb2 = new StringBuffer();
			while(reportRs.next()){
				lineSb.append("<category name='" + reportRs.getString("time") + "'/>");
				dataSb1.append("<set value='" + reportRs.getInt("total_infull_num") + "'/>");
				dataSb2.append("<set value='" + reportRs.getInt("total_user_num") + "'/>");
			}
			
			StringBuffer sb1 = new StringBuffer();
			sb1.append("<graph  caption='充值金额统计' baseFont='宋体' baseFontSize='12'   yAxisMinValue='0' yAxisMaxValue='10' xaxisname='时间' yaxisname='充值金额' hovercapbg='#87CEFF' hovercapborder='#8B0A50' formatNumberScale='0' decimalPrecision='0' showvalues='1' numdivlines='10' numVdivlines='0' shownames='1'  rotateNames='1' drawAnchors='1'  rotateLabels='1' showShadow='0' anchorSides='3'>");
			sb1.append("<categories>");
			sb1.append(lineSb.toString());
			sb1.append("</categories>");
			sb1.append("<dataset seriesName='充值金额' color='#FF0000' anchorBorderColor='#FF0000'>");
			sb1.append(dataSb1.toString());
			sb1.append("</dataset>");
			sb1.append("</graph>");
			String code1 = FusionChartsCreator.createChart("../Charts/MSLine.swf", "", sb1.toString(), "chargeamount", 950, 300, false, false);
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("<graph  caption='充值人数统计' baseFont='宋体' baseFontSize='12'   yAxisMinValue='0' yAxisMaxValue='10' xaxisname='时间' yaxisname='充值人数' hovercapbg='#87CEFF' hovercapborder='#8B0A50' formatNumberScale='0' decimalPrecision='0' showvalues='1' numdivlines='10' numVdivlines='0' shownames='1'  rotateNames='1' drawAnchors='1'  rotateLabels='1' showShadow='0' anchorSides='3'>");
			sb2.append("<categories>");
			sb2.append(lineSb.toString());
			sb2.append("</categories>");
			sb2.append("<dataset seriesName='充值人数' color='#FF0000' anchorBorderColor='#FF0000'>");
			sb2.append(dataSb2.toString());
			sb2.append("</dataset>");
			sb2.append("</graph>");
			String code2 = FusionChartsCreator.createChart("../Charts/MSLine.swf", "", sb2.toString(), "chargetimes", 950, 300, false, false);
			
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
	 * 充值排行
	 */
	public JsonRs getChargeRanking(PageContext pageContext) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			ServletRequest request = pageContext.getRequest();
			int serverId = Tools.str2int(request.getParameter("serverId"));
			int rows = Tools.str2int(request.getParameter("rows"));
			String channel = request.getParameter("channel");
			if (rows <= 0){
				rows = 10;
			}
			SqlString sqlStr = new SqlString();
			if(channel != null && !channel.equals("")){
				sqlStr.add("channel", channel);
			}
			if(serverId > 0){
				sqlStr.add("serverid", serverId);
			}
			sqlStr.addDate("log_date", MyTools.getDateStr(System.currentTimeMillis()-MyTools.long_day), ">=");
			sqlStr.addDate("log_date", MyTools.getDateStr(), "<");
			dbHelper.openConnection();
			ResultSet reportRs = dbHelper.query(INFULL_ORDER_REPORT, null, sqlStr.whereString(), "infull_num desc", null, 1, rows);
			JsonRs returnRs = DBUtil.convertRsToJsonRs(reportRs);
			return returnRs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 渠道充值数据分析
	 */
	public JSONArray getChannelChargeData(HttpServletRequest req, HttpServletResponse res) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			String startDate = req.getParameter("startDate");
			String endDate = req.getParameter("endDate");
			int serverId = Tools.str2int(req.getParameter("serverId"));
			SqlString sqlStr = new SqlString();
			if (startDate != null && !"".equals(startDate)) {
				sqlStr.addDate("log_date", startDate, ">=");
			}
			if (endDate != null && !"".equals(endDate)) {
				sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(endDate)+MyTools.long_day), "<");
			}
			if (serverId > 0) {
				sqlStr.add("serverid", serverId);
			}
			dbHelper.openConnection();
			ResultSet reportRs = dbHelper.query(INFULL_CHANNEL_REPORT, "channel,sum(infull_num) as total_infull_num", sqlStr.whereString(), null, "channel order by total_infull_num desc");
			
			int totalcharge = 0;
			StringBuffer sb1 = new StringBuffer();
			sb1.append("<chart caption='玩家充值渠道数据分析'  palette='3' showAlternateHGridColor='1'  animation='1' showAboutMenuItem='0'  formatNumberScale='0' pieYScale='30'showLabels='1'       pieSliceDepth='20' startingAngle='10' baseFont='宋体'  baseFontSize='14' bgColor='FFFFFF' shadowAlpha='100' showValues='1' canvasBgColor='FFFFFF' showPercentageInLabel='1'showAboutMenuIte='1' showLegend='1' legendIconScale='0'>");
			while(reportRs.next()) {
				String channel = reportRs.getString("channel");
				String name = TabStor.getListVal(TabStor.tab_channel, "code='"+channel+"'", "name");
				sb1.append("<set label='" + name + "("+channel+")" +": 金额 "+reportRs.getInt("total_infull_num")+"' value='" + reportRs.getInt("total_infull_num")+ "' isSliced='1'/>");
				totalcharge += reportRs.getInt("total_infull_num");
			}
			sb1.append("<styles>");
			sb1.append("<definition>");
			sb1.append("<style type='font' name='CaptionFont' size='15' color='666666' />");
			sb1.append("<style type='font' name='SubCaptionFont' bold='0' />");
			sb1.append("</definition>");
			sb1.append("<application>");
			sb1.append("<apply toObject='caption' styles='CaptionFont' />");
			sb1.append("<apply toObject='SubCaption' styles='SubCaptionFont' />");
			sb1.append("</application>");
			sb1.append("</styles>");
			sb1.append("</chart>");
			String code1 = FusionChartsCreator.createChart("../Charts/Pie2D.swf", "", sb1.toString(), "loginArea", 550,650, false, false);
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("<graph caption='玩家充值渠道数据分析' baseFont='宋体' baseFontSize='12' yAxisMinValue='0' yAxisMaxValue='10' xaxisname='渠道  ' yaxisname='金额' hovercapbg='#87CEFF' hovercapborder='#8B0A50' formatNumberScale='0' decimalPrecision='0' showvalues='1'numdivlines='10' numVdivlines='0' shownames='1' rotateNames='1' drawAnchors='1'  rotateLabels='1' showShadow='0' anchorSides='3'>");
			sb2.append("<categories>");
			reportRs.beforeFirst();
			while(reportRs.next()){
				String channel = reportRs.getString("channel");
				String name = TabStor.getListVal(TabStor.tab_channel, "code='"+channel+"'", "name");
				sb2.append("<category name='" + name + "("+channel+")" +"'/>");
			}
			sb2.append("</categories>");
			sb2.append("<dataset>");
			reportRs.beforeFirst();
			while(reportRs.next()){
				sb2.append("<set value='"+reportRs.getInt("total_infull_num")+"'/>");
			}
			sb2.append("</dataset>");
			sb2.append("</graph>");
			String code2 = FusionChartsCreator.createChart("../Charts/MSBar3D.swf", "", sb2.toString(), "chargeBar3D", 550, 650, false, false);
			
			JSONArray returnarr = new JSONArray();
			returnarr.add(code1);
			returnarr.add(code2);
			returnarr.add(totalcharge);
			return returnarr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 充值地域统计
	 */
	public JSONArray getAreaChargeData(HttpServletRequest req) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			String startDate = req.getParameter("startDate");
			String endDate = req.getParameter("endDate");
			int serverId = Tools.str2int(req.getParameter("serverId"));
			SqlString sqlStr = new SqlString();
			if (startDate != null && !"".equals(startDate)) {
				sqlStr.addDate("log_date", startDate, ">=");
			}
			if (endDate != null && !"".equals(endDate)) {
				sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(endDate)+MyTools.long_day), "<");
			}
			if (serverId > 0) {
				sqlStr.add("serverid", serverId);
			}
			
			dbHelper.openConnection();
			ResultSet reportRs = dbHelper.query(INFULL_AREA_REPORT, "area_name,sum(infull_num) as total_infull_num", sqlStr.whereString(), null, "area_name order by total_infull_num desc");
			
			int totalcharge = 0;
			StringBuffer sb1 = new StringBuffer();
			sb1.append("<chart caption='玩家充值地域数据分析'  palette='3' showAlternateHGridColor='1'  animation='1' showAboutMenuItem='0'  formatNumberScale='0' pieYScale='30'showLabels='1'       pieSliceDepth='20' startingAngle='10' baseFont='宋体'  baseFontSize='14' bgColor='FFFFFF' shadowAlpha='100'   showValues='1'   canvasBgColor='FFFFFF' showPercentageInLabel='1'showAboutMenuIte='1' showLegend='1'legendIconScale='0'>");
			while(reportRs.next()) {
				sb1.append("<set label='" + reportRs.getString("area_name")+": 总额 "+reportRs.getInt("total_infull_num")+"' value='" + reportRs.getInt("total_infull_num")+ "' isSliced='1'/>");
				totalcharge += reportRs.getInt("total_infull_num");
			}
			sb1.append("<styles>");
			sb1.append("<definition>");
			sb1.append("<style type='font' name='CaptionFont' size='15' color='666666' />");
			sb1.append("<style type='font' name='SubCaptionFont' bold='0' />");
			sb1.append("</definition>");
			sb1.append("<application>");
			sb1.append("<apply toObject='caption' styles='CaptionFont' />");
			sb1.append("<apply toObject='SubCaption' styles='SubCaptionFont' />");
			sb1.append("</application>");
			sb1.append("</styles>");
			sb1.append("</chart>");
			String code1 = FusionChartsCreator.createChart("../Charts/Pie2D.swf", "", sb1.toString(), "loginArea", 550, 650, false, false);
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("<graph  caption='玩家充值地域数据分析' baseFont='宋体' baseFontSize='12'   yAxisMinValue='0' yAxisMaxValue='10' xaxisname='地区  ' yaxisname='总额' hovercapbg='#87CEFF' hovercapborder='#8B0A50' formatNumberScale='0' decimalPrecision='0' showvalues='1'numdivlines='10' numVdivlines='0' shownames='1' rotateNames='1' drawAnchors='1'  rotateLabels='1' showShadow='0' anchorSides='3'>");
			sb2.append("<categories>");
			reportRs.beforeFirst();
			while(reportRs.next()){
				sb2.append(" <category name='"+reportRs.getString("area_name")+"'/>");
			}
			sb2.append("</categories>");
			sb2.append("<dataset >");
			reportRs.beforeFirst();
			while(reportRs.next()){
				sb2.append("<set value='"+reportRs.getInt("total_infull_num")+"'/>");
			}
			sb2.append("</dataset>");
			sb2.append("</graph>");
			String code2 = FusionChartsCreator.createChart("../Charts/MSBar3D.swf", "", sb2.toString(), "chargeBar3D", 550, 650, false, false);
			
			JSONArray returnarr = new JSONArray();
			returnarr.add(code1);
			returnarr.add(code2);
			returnarr.add(totalcharge);
			return returnarr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 充值数据综合分析
	 */
	public JSONArray getOtherChargeData(HttpServletRequest request) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			int serverId = Tools.str2int(request.getParameter("serverId"));
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			SqlString sqlStr = new SqlString();
			if (startDate != null && !"".equals(startDate)) {
				sqlStr.addDate("log_date", startDate, ">=");
			}
			if (endDate != null && !"".equals(endDate)) {
				sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(endDate)+MyTools.long_day), "<");
			}
			if (serverId > 0) {
				sqlStr.add("serverid", serverId);
			}
			
			StringBuffer tgrSb = new StringBuffer();
			tgrSb.append("sum(coin_num) as total_coin_num,");
			tgrSb.append("sum(coin_count) as total_coin_count,");
			tgrSb.append("sum(power_num1) as total_power_num1,");
			tgrSb.append("sum(power_count1) as total_power_count1,");
			tgrSb.append("sum(power_num2) as total_power_num2,");
			tgrSb.append("sum(power_count2) as total_power_count2,");
			tgrSb.append("sum(power_num3) as total_power_num3,");
			tgrSb.append("sum(power_count3) as total_power_count3");
			
			ResultSet reportRs = dbHelper.query(INFULL_ANALYSE_REPORT, tgrSb.toString(), sqlStr.whereString());
			JSONArray returnarr = new JSONArray();
			if(reportRs.next()){
				int coin_num = reportRs.getInt("total_coin_num");
				int coin_count = reportRs.getInt("total_coin_count");
				int power_num1 = reportRs.getInt("total_power_num1");
				int power_count1 = reportRs.getInt("total_power_count1");
				int power_num2 = reportRs.getInt("total_power_num2");
				int power_count2 = reportRs.getInt("total_power_count2");
				int power_num3 = reportRs.getInt("total_power_num3");
				int power_count3 = reportRs.getInt("total_power_count3");
				
				StringBuffer sb1 = new StringBuffer();
				sb1.append("<chart caption='购买类型数据分析' palette='3' showAlternateHGridColor='1' animation='1' showAboutMenuItem='0' formatNumberScale='0' pieYScale='30'showLabels='1' pieSliceDepth='20' startingAngle='10' baseFont='宋体'  baseFontSize='14' bgColor='FFFFFF' shadowAlpha='100' showValues='1' canvasBgColor='FFFFFF' showPercentageInLabel='1'showAboutMenuIte='1' showLegend='1'legendIconScale='0'>");
				sb1.append("<set label='钻石:"+coin_count+"次 金额:"+coin_num+"' value='" + coin_num+ "' isSliced='1'/>");
				sb1.append("<set label='特权:"+(power_count1+power_count2+power_count3)+"次 金额:"+(power_num1+power_num2+power_num3)+"' value='" + (power_num1+power_num2+power_num3)+ "' isSliced='1'/>");
				sb1.append("<styles>");
				sb1.append("<definition>");
				sb1.append("<style type='font' name='CaptionFont' size='15' color='666666' />");
				sb1.append("<style type='font' name='SubCaptionFont' bold='0' />");
				sb1.append("</definition>");
				sb1.append("<application>");
				sb1.append("<apply toObject='caption' styles='CaptionFont' />");
				sb1.append("<apply toObject='SubCaption' styles='SubCaptionFont' />");
				sb1.append("</application>");
				sb1.append("</styles>");
				sb1.append("</chart>");
				String code1 = FusionChartsCreator.createChart("../Charts/Pie2D.swf", "", sb1.toString(), "buytype", 550, 650, false, false);
				
				StringBuffer sb2 = new StringBuffer();
				sb2.append("<chart caption='购买特权数据分析' palette='3' showAlternateHGridColor='1' animation='1' showAboutMenuItem='0' formatNumberScale='0' pieYScale='30'showLabels='1' pieSliceDepth='20' startingAngle='10' baseFont='宋体'  baseFontSize='14' bgColor='FFFFFF' shadowAlpha='100' showValues='1' canvasBgColor='FFFFFF' showPercentageInLabel='1'showAboutMenuIte='1' showLegend='1'legendIconScale='0'>");
				sb2.append("<set label='30天特权:"+power_count1+"次 金额:"+power_num1+"' value='" + power_num1+ "' isSliced='1'/>");
				sb2.append("<set label='90天特权:"+power_count2+"次 金额:"+power_num2+"' value='" + power_num2+ "' isSliced='1'/>");
				sb2.append("<set label='180天特权:"+power_count3+"次 金额:"+power_num3+"' value='" + power_num3+ "' isSliced='1'/>");
				sb2.append("<styles>");
				sb2.append("<definition>");
				sb2.append("<style type='font' name='CaptionFont' size='15' color='666666' />");
				sb2.append("<style type='font' name='SubCaptionFont' bold='0' />");
				sb2.append("</definition>");
				sb2.append("<application>");
				sb2.append("<apply toObject='caption' styles='CaptionFont' />");
				sb2.append("<apply toObject='SubCaption' styles='SubCaptionFont' />");
				sb2.append("</application>");
				sb2.append("</styles>");
				sb2.append("</chart>");
				String code2 = FusionChartsCreator.createChart("../Charts/Pie2D.swf", "", sb2.toString(), "tqtype", 550, 650, false, false);
				
				returnarr.add(code1);
				returnarr.add(code2);
			}
			return returnarr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	private static ChargeStatBAC instance = new ChargeStatBAC();
	
	public static ChargeStatBAC getInstance() {		
		return instance;
	}
}
