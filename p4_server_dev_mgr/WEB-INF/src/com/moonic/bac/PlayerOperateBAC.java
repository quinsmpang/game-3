package com.moonic.bac;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import server.common.Tools;
import util.IPAddressUtil;

import com.ehc.common.SqlString;
import com.moonic.servlet.STSServlet;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;
import com.moonic.util.STSNetSender;

/**
 * 玩家处理
 * @author John
 */
public class PlayerOperateBAC {
	
	/**
	 * 查询玩家列表
	 */
	public JSONArray queryPlayerListByName(String name, int serverid) {
		SqlString sqlstr = new SqlString();
		sqlstr.add("name", name, "like");
		if (serverid > 0){
			sqlstr.add("serverid", serverid);
		}
		String sql = "select * from tab_player " + sqlstr.whereStringEx();
		JSONArray jsonarr = new JSONArray();
		DBHelper dbhelper = new DBHelper();
		try {
			dbhelper.openConnection();
			ResultSet rs = dbhelper.executeQuery(sql);
			while (rs.next()) {
				JSONArray arr = new JSONArray();
				arr.add(rs.getInt("id"));
				arr.add(rs.getInt("serverid"));
				jsonarr.add(arr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbhelper.closeConnection();
		}
		return jsonarr;
	}
	
	/**
     * 封号
     */
    public void blankOffPlayer(HttpServletRequest request){				    	
    	try {
    		HttpSession session = request.getSession();
        	String username = (String) session.getAttribute("username");
        	int playerid = Tools.str2int(request.getParameter("pid"));
        	int serverid = Tools.str2int(request.getParameter("sid"));
        	String date = request.getParameter("blankOffTime");
         	String note = request.getParameter("blankOffNote");
        	
        	STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_BLANK);
        	sender.dos.writeInt(playerid);
        	sender.dos.writeUTF(date);
        	ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverid);
        	
        	if(date==null|| "".equals(date)){
        		date="永久";
        	}
        	PlayerOperateLogBAC.getInstance().addLog(playerid, serverid, 0, date, note, username, IPAddressUtil.getIp(request));
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 解封
     */
    public void unBlankOffPlayer(HttpServletRequest request){
    	try {
	    	HttpSession session = request.getSession();
	    	String username = (String) session.getAttribute("username");
	    	int playerid = Tools.str2int(request.getParameter("pid"));
	    	int serverid = Tools.str2int(request.getParameter("sid"));
	    	
	    	STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_UNBLANK);
        	sender.dos.writeInt(playerid);
        	ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverid);
	    	
	    	PlayerOperateLogBAC.getInstance().addLog(playerid, serverid, 1, null, null, username, IPAddressUtil.getIp(request));
    	} catch (Exception e) {
			e.printStackTrace();
		}
    } 
    
	/**
	 * 禁言
	 */
	public void bannedToPostPlayer(HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			String useruame = (String) session.getAttribute("username");
			int playerid = Tools.str2int(request.getParameter("pid"));
			int serverid = Tools.str2int(request.getParameter("sid"));
			String date = request.getParameter("bannedMsgTime");
			String note = request.getParameter("banNote");
			
			if (date == null || "".equals(date)) {
				date = MyTools.getTimeStr(System.currentTimeMillis() + MyTools.long_day * 30);
			}

			STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_BANNED_MSG);
			sender.dos.writeInt(playerid);
			sender.dos.writeUTF(date);
			ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverid);

			PlayerOperateLogBAC.getInstance().addLog(playerid, serverid, 2, date, note, useruame, IPAddressUtil.getIp(request));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * 解除禁言
     */
    public void unBannedToPostPlayer(HttpServletRequest request){
    	try {
	    	HttpSession session = request.getSession();
	    	String username = (String) session.getAttribute("username");
	    	int playerid = Tools.str2int(request.getParameter("pid"));
	    	int serverid = Tools.str2int(request.getParameter("sid"));
	    	
	    	STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_UNBANNED_MSG);
			sender.dos.writeInt(playerid);
			ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverid);
	    	
	    	PlayerOperateLogBAC.getInstance().addLog(playerid, serverid, 3, null, null, username, IPAddressUtil.getIp(request));
    	} catch (Exception e) {
			e.printStackTrace();
		}
  	}
    
    /**
     * 踢下线
     */
    public void kickOut(HttpServletRequest request){
    	try {
	    	HttpSession session = request.getSession();
	    	String username = (String) session.getAttribute("username");
	    	int playerid = Tools.str2int(request.getParameter("pid"));
	    	int serverid = Tools.str2int(request.getParameter("sid"));
	    	String remark = request.getParameter("tips");
	    	
	    	if(null==remark||"".equals(remark)){
				remark="与服务器连接中断";
			}
	    	
			STSNetSender sender = new STSNetSender(STSServlet.G_BREAK_ONEPLAYER);
			sender.dos.writeInt(playerid);//玩家ID
			sender.dos.writeUTF(remark);//推送说明
			ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverid);//服务器ID
	    	
			PlayerOperateLogBAC.getInstance().addLog(playerid, serverid, 4, null, remark, username, IPAddressUtil.getIp(request));
    	} catch (Exception e) {
			e.printStackTrace();
		}
  	}
    
	//--------------静态区--------------
	
	private static PlayerOperateBAC instance = new PlayerOperateBAC();
	
	/**
	 * 获取实例
	 */
	public static PlayerOperateBAC getInstance(){
		return instance;
	}
}
