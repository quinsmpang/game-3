package com.moonic.bac;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.LogTbName;

/**
 * 游戏日志
 * @author John
 */
public class GameLogBAC extends BaseActCtrl {
	public static final String tab_game_log_datatype = "tab_game_log_datatype";
	
	/**
	 * 构造
	 */
	public GameLogBAC() {
		super.setTbName(LogTbName.TAB_GAME_LOG());
		setDataBase(ServerConfig.getDataBase_Backup());
	}
	
	/**
	 * 获取数据
	 */
	public JSONObject getPageList(PageContext pageContext) {
		ServletRequest request = pageContext.getRequest();
		int page = Tools.str2int(request.getParameter("page"));
		if(page==0) {
			page=1;
		}
		int rpp=Tools.str2int(request.getParameter("rpp"));
		if(rpp==0) {
			rpp=10;
		}
		String ordertype=request.getParameter("ordertype");
		if(ordertype==null || ordertype.equals("")) {
			ordertype="DESC";
		}
		String showorder = request.getParameter("showorder");
		if(showorder==null || showorder.equals("")) {
			showorder="log."+showorder;
		}
		String[] moduleArr = request.getParameterValues("module"); //模块筛选
		String[] actStrArr = request.getParameterValues("act"); //指令筛选
		String playerName = request.getParameter("playerName");//指定玩家名
		String search_act = request.getParameter("search_act");//指定动作号
		String startTime = request.getParameter("startTime");//指定起始时间
		String endTime = request.getParameter("endTime");//指定终止时间
		String serverId = request.getParameter("serverId");//指定服务器ID
		
		String[] changeArr = request.getParameterValues("otherchange");//变化删选
		
		String consume = Tools.strNull(request.getParameter("consume"));//消耗信息模糊搜索内容
		String obtain = Tools.strNull(request.getParameter("obtain"));//获取信息模糊搜索内容
		String remark = Tools.strNull(request.getParameter("remark"));//备注信息模糊搜索内容
		
		if(startTime == null || startTime.equals("")) {
			startTime = MyTools.getTimeStr();
		}
		if(playerName!=null) {
			playerName = playerName.trim();
		}
		
		String orderClause = showorder + " " + ordertype;
		
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for(int i=0; actStrArr!=null && i<actStrArr.length; i++) {
			if(i>0){
				sb.append(" or ");
			}
			sb.append("act="+actStrArr[i]);
		}
		sb.append(")");
		String actCondition = sb.toString();
		
		SqlString sqlS = new SqlString();
		if(serverId!=null && !serverId.equals("")) {
			sqlS.add("log.serverid", Tools.str2int(serverId));//服务器条件
		}
		if(!consume.equals("")) {
			sqlS.add("log.consume", consume,"like");//消耗条件
		}
		if(!obtain.equals("")) {
			sqlS.add("log.obtain", obtain,"like");//获得条件
		}
		if(!remark.equals("")) {
			sqlS.add("log.remark", remark,"like");//备注条件
		}
		if(playerName!=null && !playerName.equals("")) {//角色名条件
			if(serverId!=null && !serverId.equals("")) {
				int playerId = PlayerBAC.getInstance().getIntValue("id", "name='"+playerName+"' and serverId="+serverId);
				sqlS.add("log.playerid", playerId);
			} else {
				sqlS.addWhere("log.playerid in (select id from tab_player where name='"+playerName+"')");				
			}
		}
		if(search_act!=null && !search_act.equals("")) {//动作号查询条件
			sqlS.add("log.act", Tools.str2int(search_act));
		}		
		if(startTime!=null && !startTime.equals("")) {//起始时间条件
			sqlS.addDateTime("log.createtime", startTime.trim(),">=");
		}
		if(endTime!=null && !endTime.equals("")) {//终止时间条件
			sqlS.addDateTime("log.createtime", endTime.trim(),"<=");
		} else {
			sqlS.addDateTime("log.createtime", MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day),"<");
		}
		if(changeArr!=null){//数值变化条件
			try {
				DBPsRs dtypeRs = DBPool.getInst().pQueryS(tab_game_log_datatype);
				while(dtypeRs.next()){
					if(MyTools.checkInStrArr(changeArr, dtypeRs.getString("dtype"))){
						sqlS.addWhereOr("log."+dtypeRs.getString("chacol")+" is not null");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(!actCondition.equals("()")) {//指令筛选条件
			sqlS.addWhere(actCondition);
		}
		if(moduleArr != null) {//模块筛选条件
			sqlS.addWhere("log.act in (select code from tab_game_func where module in("+Tools.strArr2Str(moduleArr)+"))");
		}
		String sql = "select log.* from "+LogTbName.TAB_GAME_LOG()+" log "+sqlS.whereStringEx()+" order by "+orderClause;
		//System.out.println("sql:"+sql);
		return getJsonPageListBySQL(sql, page, rpp);
	}
	
	//--------------静态区--------------
	
	public static GameLogBAC instance = new GameLogBAC();
	
	/**
	 * 获取实例
	 */
	public static GameLogBAC getInstance(){
		return instance;
	}
}
