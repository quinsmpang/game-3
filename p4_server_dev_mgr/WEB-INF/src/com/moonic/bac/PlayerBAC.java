package com.moonic.bac;

import java.sql.ResultSet;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.moonic.servlet.STSServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.NetResult;
import com.moonic.util.STSNetSender;

import conf.LogTbName;

/**
 * 角色BAC
 * @author John
 */
public class PlayerBAC extends BaseActCtrl {
	public static final String tab_player = "tab_player";
	
	/**
	 * 构造
	 */
	public PlayerBAC() {			
		super.setTbName(tab_player);
		setDataBase(ServerConfig.getDataBase_Backup());
	}
	
	/**
	 * 获取玩家数据
	 */
	public ReturnValue getAllData(int playerid, int serverid) {
		try {
			STSNetSender sender = new STSNetSender(STSServlet.G_BK_GET_PLAYER_DATA);
			sender.dos.writeInt(playerid);
			NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverid);
			return nr.rv;
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 查询数据集
	 */
	public ResultSet getDataRs(DBHelper dbHelper, String target, String where) throws Exception{
		return dbHelper.query(tab_player, target, where);
	}
	
	/**
	 * 查询数据集
	 */
	public ResultSet getDataRs(DBHelper dbHelper, String target, String where, String order) throws Exception{
		return dbHelper.query(tab_player, target, where, order);
	}
	
	public JSONObject getPageList(PageContext pageContext) {
		ServletRequest request = pageContext.getRequest();
		int page=Tools.str2int(request.getParameter("page"));
		if(page==0)
		{
			page=1;
		}
		int rpp=Tools.str2int(request.getParameter("rpp"));
		if(rpp==0)
		{
			rpp=10;
		}		
		
		String ordertype = request.getParameter("ordertype");
		if(ordertype==null || ordertype.equals(""))
		{
			ordertype="desc";
		}
		String showorder = request.getParameter("showorder");
		if(showorder==null || showorder.equals(""))
		{
			showorder="a.id";
		}
		String orderClause = showorder + " " + ordertype;
		
		SqlString plaSqlStr = new SqlString();
		
		String serverid = request.getParameter("serverId");
		String vsid = request.getParameter("vsid");
		String onlinestate= request.getParameter("onlinestate");
		
		if(serverid!=null && !serverid.equals("")) {
			plaSqlStr.add("serverid", serverid);
		}
		if(vsid!=null && !vsid.equals("")) {
			plaSqlStr.add("vsid", vsid);
		}
		if(onlinestate!=null && !onlinestate.equals("")) {
			plaSqlStr.add("onlinestate", onlinestate);
		}
		
		SqlString userSqlStr = new SqlString();
		
		String channel = request.getParameter("channel");
		if(channel!=null && !channel.equals("")) {
			userSqlStr.add("channel", channel);
		}
		
		String colname = request.getParameter("colname");
		String colvalue = request.getParameter("colvalue");
		String operator = request.getParameter("operator");
		
		if(colvalue!=null && !colvalue.equals("")) {
			SqlString sqlStr = null;
			if(colname.startsWith("tab_player")){
				sqlStr = plaSqlStr;
			} else 
			if(colname.startsWith("tab_user")){
				sqlStr = userSqlStr;
			}
			if(operator.equals("包含")) {
				sqlStr.add(colname, colvalue, "like");
			} else
			if(operator.equals("等于")) {
				sqlStr.add(colname, colvalue);
			}			
		}
		String sql = "select a.*,b.username from (select * from tab_player "+plaSqlStr.whereStringEx()+") a inner join (select * from tab_user "+userSqlStr.whereStringEx()+") b on a.userid=b.id order by "+orderClause;
		//System.out.println(sql);
		return getJsonPageListBySQL(sql, page, rpp);
	}
	
	/**
	 * 根据ID找玩家名
	 */
	public String getNameById(int id) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		try {
			if(id!=0){
				ResultSet plaRs = dbHelper.query(tab_player, "name", "id="+id);
				if(plaRs.next()){
					return plaRs.getString("name");		
				} else {
					return "未找到角色名";
				}				
			} else {
				return "";
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return "["+ex.toString()+"]";
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取战斗回放数据
	 */
	public ReturnValue getBattleReplay(long battleid){
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		try {
			ResultSet rs = dbHelper.query(LogTbName.tab_battle_replay(), "replaydata", "battleid="+battleid);
			if(!rs.next()){
				BACException.throwInstance("未找到相关日志");
			}
			return new ReturnValue(true, new String(rs.getBytes("replaydata"), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	private static PlayerBAC instance = new PlayerBAC();
	
	/**
	 * 获取实例
	 */
	public static PlayerBAC getInstance(){
		return instance;
	}
}
