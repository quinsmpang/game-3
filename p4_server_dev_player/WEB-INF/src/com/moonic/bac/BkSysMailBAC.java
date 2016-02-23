package com.moonic.bac;

import java.sql.ResultSet;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 后台系统邮件
 * @author John
 */
public class BkSysMailBAC {
	public static final String tab_sys_mail_send_log = "tab_sys_mail_send_log";
	
	/**
	 * 向指定角色发邮件
	 */
	public ReturnValue sendToSome(String receiverids, int smailid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			DBPaRs smStorRs = DBPool.getInst().pQueryA(tab_sys_mail_send_log, "id="+smailid);
			String title = smStorRs.getString("title");
			String content = smStorRs.getString("content");
			String adjunct = smStorRs.getString("adjunct");
			MailBAC.getInstance().sendSysMail(dbHelper, receiverids, title, content, adjunct, 0);		
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 向服务器符合条件的角色发邮件
	 */
	public ReturnValue sendToServer(int smailid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPaRs smStorRs = DBPool.getInst().pQueryA(tab_sys_mail_send_log, "id="+smailid);
			String filtercond = smStorRs.getString("filtercond");
			String title = smStorRs.getString("title");
			String content = smStorRs.getString("content");
			String adjunct = smStorRs.getString("adjunct");
			String channelStr = smStorRs.getString("channel");
			StringBuffer whereSb = new StringBuffer();
			whereSb.append("serverid="+Conf.sid+" and onlinestate=1");
			String[][] filter = Tools.splitStrToStrArr2(filtercond, "|", ",");
			for(int i = 0; filter != null && i < filter.length; i++){
				if(filter[i][0].equals("1")){
					whereSb.append(" and savetime<="+filter[i][1]);
				} else 
				if(filter[i][0].equals("2")){
					whereSb.append(" and lv>="+filter[i][1]);
				} else 
				if(filter[i][0].equals("3")){
					whereSb.append(" and vip>="+filter[i][1]);
				} else 
				if(filter[i][0].equals("-1")){
					int[] vsid_int = new int[filter[i].length-1];
					for(int k = 0; k < vsid_int.length; k++){
						vsid_int[k] = Integer.valueOf(filter[i][k+1]);
					}
					whereSb.append(" and ("+MyTools.converWhere("or", "vsid", "=", vsid_int)+")");
				}
			}
			//System.out.println("whereSb:"+whereSb);
			dbHelper.openConnection();
			ResultSet plaRs = dbHelper.query("tab_player", "id,channel", whereSb.toString());
			while(plaRs.next()){
				if(!(channelStr.equals("0") || channelStr.contains(plaRs.getString("channel")))){
					continue;
				}
				MailBAC.getInstance().sendSysMail(dbHelper, plaRs.getInt("id"), title, content, adjunct, smailid);
			}
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区--------------
	
	private static BkSysMailBAC instance = new BkSysMailBAC();
	
	/**
	 * 获取实例
	 */
	public static BkSysMailBAC getInstance(){
		return instance;
	}
}
