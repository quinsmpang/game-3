package com.moonic.bac;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.mgr.TabStor;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.FusionChartsCreator;
import com.moonic.util.MyTools;


/**
 * 游戏统计
 * @author 
 */
public class GameStatBAC {
	public static final String tab_gamestat_type = "tab_gamestat_type";
	
	/**
	 * 获取报表数据
	 */
	public ReturnValue getReportData(PageContext pageContext){
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		
		int num = Tools.str2int(request.getParameter("funclist"));
		String starttime = request.getParameter("starttime");
		String endtime = request.getParameter("endtime");
		int serverid = Tools.str2int(request.getParameter("serverid"));
		
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		try {
			dbHelper.openConnection();
			DBPaRs typeRs = DBPool.getInst().pQueryA(tab_gamestat_type, "num="+num);
			String funcname = typeRs.getString("funcname");
			String tabname = typeRs.getString("tabname");
			String[][] customfilter = null;
			if(!typeRs.getString("customfilter").equals("0")){
				customfilter = Tools.splitStrToStrArr2(typeRs.getString("customfilter"), "|", ",");
			}
			String[] grouppara = null;
			if(!typeRs.getString("grouppara").equals("0")){
				grouppara = Tools.splitStr(typeRs.getString("grouppara"), ",");
			}
			String orderpara = null;
			if(!typeRs.getString("orderpara").equals("0")){
				orderpara = typeRs.getString("orderpara");
			}
			String[] sumpara = null;
			if(!typeRs.getString("sumpara").equals("0")){
				sumpara = Tools.splitStr(typeRs.getString("sumpara"), ",");
			}
			String[] avgpara = null;
			if(!typeRs.getString("avgpara").equals("0")){
				avgpara = Tools.splitStr(typeRs.getString("avgpara"), ",");
			}
			String[][] showpara = Tools.splitStrToStrArr2(typeRs.getString("showpara"), "|", ",");
			String[][] replacepara = null;
			if(!typeRs.getString("replacepara").equals("0")){
				replacepara = Tools.splitStrToStrArr2(typeRs.getString("replacepara"), "|", ",");
			}
			
			SqlString sqlStr = new SqlString();
			if(!starttime.equals("")){
				sqlStr.addDate("log_date", starttime, ">=");	
			}
			if(!endtime.equals("")){
				sqlStr.addDate("log_date", MyTools.getDateStr(MyTools.getTimeLong(endtime)+MyTools.long_day), "<");
			}
			if(serverid != 0){
				sqlStr.add("serverid", serverid);
			}
			for(int i = 0; customfilter != null && i < customfilter.length; i++){
				String val = request.getParameter(customfilter[i][1]);
				if(val!=null && !val.equals("")){
					sqlStr.add(customfilter[i][1], val);
				}
			}
			StringBuffer groupSb = new StringBuffer();
			StringBuffer sumSb = new StringBuffer();
			for(int i = 0; grouppara != null && i < grouppara.length; i++){
				if(groupSb.length() > 0){
					groupSb.append(",");
				}
				groupSb.append(grouppara[i]);
				if(sumSb.length() > 0){
					sumSb.append(",");
				}
				sumSb.append(grouppara[i]);
			}
			for(int i = 0; sumpara != null && i < sumpara.length; i++){
				if(sumSb.length() > 0){
					sumSb.append(",");
				}
				if(sumpara[i].contains(" as ")){
					sumSb.append(sumpara[i]);
				} else {
					sumSb.append("sum("+sumpara[i]+") as "+sumpara[i]);
				}
			}
			for(int i = 0; avgpara != null && i < avgpara.length; i++){
				if(sumSb.length() > 0){
					sumSb.append(",");
				}
				if(avgpara[i].contains(" as ")){
					sumSb.append(avgpara[i]);
				} else {
					sumSb.append("trunc(avg("+avgpara[i]+"),2) as "+avgpara[i]);
				}
			}
			String tgrStr = sumSb.length() > 0 ? sumSb.toString() : null;
			String groupStr = groupSb.length() > 0 ? groupSb.toString() : null;
			String orderStr = null;
			if(orderpara !=null){
				if(groupStr != null){
					groupStr += " order by "+orderpara;
				} else {
					orderStr = orderpara;
				}
			}
			//System.out.println("tabname:"+tabname);
			//System.out.println("tgrStr:"+tgrStr);
			//System.out.println("where:"+sqlStr.whereString());
			//System.out.println("orderStr:"+orderStr);
			//System.out.println("groupStr:"+groupStr);
			ResultSet rs = dbHelper.query(tabname, tgrStr, sqlStr.whereString(), orderStr, groupStr);
			
			JSONArray returnarr = new JSONArray();
			for(int i = 0; i < showpara.length; i++){
				String returnStr = null;
				if(showpara[i][0].equals("1")){
					JSONArray dataarr = new JSONArray();
					String sql = "select * from user_col_comments where table_name='"+tabname.toUpperCase()+"'";
					ResultSet commentRs = dbHelper.executeQuery(sql);
					JSONObject commentobj = new JSONObject();
					while(commentRs.next()){
						commentobj.put(commentRs.getString("column_name").toLowerCase(), commentRs.getString("comments"));
					}
					ResultSetMetaData rsmd = rs.getMetaData();
					int colCount = rsmd.getColumnCount();
					JSONArray colcomentarr = new JSONArray();//字段注释名
					for (int k = 1; k <= colCount; k++) {
						String colname = rsmd.getColumnName(k);
						String colcoment = commentobj.optString(colname);
						if(colcoment == null || colcoment.equals("")){
							colcoment = colname;
						} else {
							colcoment += "("+colname+")";
						}
						colcomentarr.add(colcoment);
					}
					dataarr.add(colcomentarr);
					while(rs.next()){
						JSONArray one = new JSONArray();
						for (int k = 1; k <= colCount; k++) {
							one.add(getReplaceValue(rsmd.getColumnName(k), rs.getString(k), replacepara));
						}
						dataarr.add(one);//字段值
					}
					returnStr = dataarr.toString();
				} else 
				if(showpara[i][0].equals("2")){
					StringBuffer sb = new StringBuffer();
					sb.append("<graph  caption='"+funcname+"' baseFont='宋体' baseFontSize='12'   yAxisMinValue='0' yAxisMaxValue='10' xaxisname='"+showpara[i][1]+"' yaxisname='"+showpara[i][3]+"' hovercapbg='#87CEFF' hovercapborder='#8B0A50' formatNumberScale='0' decimalPrecision='0' showvalues='1' numdivlines='10' numVdivlines='0' shownames='1'  rotateNames='1' drawAnchors='1'  rotateLabels='1' showShadow='0' anchorSides='3'>");
					sb.append("<categories>");
					while(rs.next()){
						sb.append("<category name='" + getReplaceValue(showpara[i][2], rs.getString(showpara[i][2]), replacepara) + "'/>");
					}
					sb.append("</categories>");
					sb.append("<dataset seriesName='"+showpara[i][3]+"' color='#FF0000' anchorBorderColor='#FF0000'>");
					rs.beforeFirst();
					while(rs.next()){
						sb.append("<set value='" + rs.getString(showpara[i][4]) + "'/>");
					}
					sb.append("</dataset>");
					sb.append("</graph>");
					returnStr = FusionChartsCreator.createChart("../Charts/MSLine.swf", "", sb.toString(), "reportshow", 950, 300, false, false);
				} else 
				if(showpara[i][0].equals("3")){
					StringBuffer sb = new StringBuffer();
					sb.append("<chart caption='"+funcname+"'  palette='3' showAlternateHGridColor='1'  animation='1' showAboutMenuItem='0'  formatNumberScale='0' pieYScale='30'showLabels='1'       pieSliceDepth='20' startingAngle='10' baseFont='宋体'  baseFontSize='14' bgColor='FFFFFF' shadowAlpha='100' showValues='1' canvasBgColor='FFFFFF' showPercentageInLabel='1'showAboutMenuIte='1' showLegend='1' legendIconScale='0'>");
					while(rs.next()) {
						sb.append("<set label='" + getReplaceValue(showpara[i][1], rs.getString(showpara[i][1]), replacepara)+": "+rs.getString(showpara[i][3])+"' value='" + rs.getString(showpara[i][3])+ "' isSliced='1'/>");
					}
					sb.append("<styles>");
					sb.append("<definition>");
					sb.append("<style type='font' name='CaptionFont' size='15' color='666666' />");
					sb.append("<style type='font' name='SubCaptionFont' bold='0' />");
					sb.append("</definition>");
					sb.append("<application>");
					sb.append("<apply toObject='caption' styles='CaptionFont' />");
					sb.append("<apply toObject='SubCaption' styles='SubCaptionFont' />");
					sb.append("</application>");
					sb.append("</styles>");
					sb.append("</chart>");
					returnStr = FusionChartsCreator.createChart("../Charts/Pie2D.swf", "", sb.toString(), "loginArea", 950,500, false, false);
				}
				returnarr.add(returnStr);
				rs.beforeFirst();
			}
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取替换值
	 */
	public String getReplaceValue(String col, String val, String[][] replacepara){
		for(int i = 0; replacepara != null && i < replacepara.length; i++){
			if(replacepara[i][0].equalsIgnoreCase(col)){
				if(replacepara[i][1].equals("1")){
					String[] valData = Tools.splitStr(val, ",");
					String querytabname = replacepara[i][2];
					for(int k = 0; k < valData.length; k++){
						String rep_val = TabStor.getListVal(querytabname, replacepara[i][3+2*k]+"="+valData[k], replacepara[i][3+2*k+1]);
						if(k < valData.length-1){
							querytabname = rep_val;
						} else 
						if(rep_val != null){
							val = rep_val;
						}
					}
				} else 
				if(replacepara[i][1].equals("2")){
					String[] replacearr = Tools.splitStr(replacepara[i][2], "#");
					for(int k = 0; k < replacearr.length-1; k+=2){
						if(replacearr[k].equals(val)){
							val = replacearr[k+1];
							break;
						}
					}
				}
				break;
			}
		}
		return val;
	}
	
	//--------------静态区---------------
	
	private static GameStatBAC instance = new GameStatBAC();
	
	/**
	 * 获取实例
	 */
	public static GameStatBAC getInstance() {
		return instance;
	}
}
