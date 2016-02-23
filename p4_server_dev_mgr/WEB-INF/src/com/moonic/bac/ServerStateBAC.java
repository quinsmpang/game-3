package com.moonic.bac;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.dbc.BaseActCtrl;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.FusionChartsCreator;

/**
 * 服务器状态
 * @author John
 */
public class ServerStateBAC extends BaseActCtrl {
	public static String tab_server_state_log = "tab_server_state_log";
	
	/**
	 * 构造
	 */
	public ServerStateBAC() {
		super.setTbName(tab_server_state_log);
		setDataBase(ServerConfig.getDataBase_Log());
	}
	
	/**
	 * 获取状态数据
	 */
	public JSONArray getStateData(PageContext pageContext){
		JSONArray returnarr = new JSONArray();
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		try {
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			int servertype = Tools.str2int(request.getParameter("servertype"));
			int serverid = Tools.str2int(request.getParameter("serverid"));
			String starttime = request.getParameter("starttime");
			String endtime = request.getParameter("endtime");
			dbHelper.openConnection();
			if(starttime == null){
				BACException.throwInstance("起始时间不能为空");
			}
			StringBuffer whereSb = new StringBuffer();
			whereSb.append("servertype="+servertype+" and serverid="+serverid+" and createtime>="+starttime);
			if(endtime!=null&&!endtime.equals("")){
				whereSb.append(" and createtime<="+endtime);
			}
			ResultSet stateRs = dbHelper.query(tab_server_state_log, null, whereSb.toString(), "createtime");
			
			stateRs.last();
			int dataamount = stateRs.getRow();
			stateRs.beforeFirst();
			int showvalues = dataamount<=36?1:0;
			int drowAnchors = dataamount<=72?1:0;
			
			String[] titles = {"连接数","内存","线程数"};
			String[][] seriesNames = {{"活动连接数","空闲连接数","最大连接数"},{"已用内存","分配内存","最大内存"},{"总线程数"}};
			String[][] seriesCols = {{"acticonn","freeconn","maxconn"},{"freemem","totalmem","maxmem"},{"totalthread"}};
			String[] uom = {"个","M","个"};
			String colors[]={"#CD4F39","#EEC900","#698B22","#FFEC8B","#FF3030","#C1FFC1","#9A32CD","#87CEFF","#008B8B"};
			
			for(int n = 0; n < 3; n++){
				StringBuffer sb = new StringBuffer();
				sb.append("<graph  caption='"+titles[n]+"' baseFont='宋体' baseFontSize='12' yAxisMinValue='0' yAxisMaxValue='10' xaxisname='时间' yaxisname='单位("+uom[n]+")' hovercapbg='#87CEFF' hovercapborder='#8B0A50' formatNumberScale='0' decimalPrecision='0' showvalues='"+showvalues+"' numdivlines='10' numVdivlines='0' shownames='1' rotateNames='1' drawAnchors='"+drowAnchors+"'  rotateLabels='1' showShadow='0' anchorSides='3'>");
				sb.append("<categories>");
				stateRs.beforeFirst();
				while(stateRs.next()){
					String time = Tools.strdate2str(stateRs.getString("createtime"),"HH:mm:ss");
					sb.append(" <category name='"+time+"'/>");
				}
				sb.append("</categories>");
				for(int c = 0; c < seriesNames[n].length; c++){
					sb.append("<dataset seriesName='"+seriesNames[n][c]+"' color='"+colors[c]+"' anchorBorderColor='"+colors[c]+"'>");
					stateRs.beforeFirst();
					while(stateRs.next()){
						if(seriesCols[n][c].equals("freemem")){
							sb.append(" <set value='"+(stateRs.getInt("totalmem")-stateRs.getInt("freemem"))+"'/>");
						} else {
							sb.append(" <set value='"+stateRs.getInt(seriesCols[n][c])+"'/>");						
						}
					}
					sb.append("</dataset>");
				}
				sb.append("</graph>");
				String code = FusionChartsCreator.createChart("../Charts/MSLine.swf", "", sb.toString(), "chart"+n, 1150, 600, false, false);
				returnarr.add(code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
		return returnarr;
	}
	
	//--------------静态区---------------
	
	private static ServerStateBAC instance = new ServerStateBAC();
	
	/**
	 * 获取实例
	 */
	public static ServerStateBAC getInstance() {
		return instance;
	}
}
