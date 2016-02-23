package com.moonic.bac;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.mgr.LockStor;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 邮件
 * @author John
 */
public class MailBAC {
	public static final String tab_mail_stor = "tab_mail_stor";
	public static final String tab_pla_mail = "tab_pla_mail";
	public static final String tab_mail_model = "tab_mail_model";
	
	/**
	 * 发邮件
	 */
	public ReturnValue sendMail(int playerid, String receiveridStr, String title, String content){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			if(title == null || title.equals("")){
				BACException.throwInstance("标题不能为空");
			}
			if(title.length()>=16){
				BACException.throwInstance("标题名过长");
			}
			if(content == null || content.equals("")){
				BACException.throwInstance("内容不能为空");
			}
			if(content.length()>=140){
				BACException.throwInstance("内容过长");
			}
			int[] receiverids = Tools.splitStrToIntArr(receiveridStr, ",");
			if(receiverids == null){
				BACException.throwInstance("没有有效的发送对象");
			}
			String[] recenames = new String[receiverids.length];
			for(int i = 0; i < receiverids.length; i++){
				DBPaRs receRs = PlayerBAC.getInstance().getDataRs(receiverids[i]);
				if(receRs.getInt("serverid") != Conf.sid){
					BACException.throwInstance("发送对象不存在");
				}
				// TODO 发送邮件的其他检查
				recenames[i] = receRs.getString("name");
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_MAIL_SEND);
			gl.addRemark("发送名单：");
			int needmoney = 100 * receiverids.length;
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			PlayerBAC.getInstance().useMoney(dbHelper, playerid, needmoney, gl);
			for(int i = 0; i < receiverids.length; i++){
				sendMail(dbHelper, playerid, plaRs.getString("name"), receiverids[i], recenames[i], title, content, null, 0);
				gl.addRemark(GameLog.formatNameID(recenames[i], receiverids[i]));
			}
			
			ResultSet plamailRs = dbHelper.query(tab_pla_mail, "linklist", "playerid="+playerid);
			boolean exist = plamailRs.next();
			JSONArray linklist = null;
			if(exist){
				linklist = new JSONArray(plamailRs.getString("linklist"));
			} else {
				linklist = new JSONArray();
				linklist.add(new JSONArray());//PID
				linklist.add(new JSONArray());//PNAME
			}
			JSONArray pidlist = linklist.optJSONArray(0);
			JSONArray pnamelist = linklist.optJSONArray(1);
			boolean update = false;
			for(int i = 0; i < receiverids.length; i++){
				if(!pidlist.contains(receiverids[i])){
					if(pidlist.length() < 6){
						pidlist.add(receiverids[i]);
						pnamelist.add(recenames[i]);
						update = true;
					} else {
						pidlist.remove(0);
						pnamelist.remove(0);
						pidlist.add(receiverids[i]);
						pnamelist.add(recenames[i]);
						update = true;
					}
				} 
			}
			if(update){
				if(exist){
					SqlString sqlStr = new SqlString();
					sqlStr.add("linklist", linklist.toString());
					dbHelper.update(tab_pla_mail, sqlStr, "playerid="+playerid);
				} else {
					SqlString sqlStr = new SqlString();
					sqlStr.add("playerid", playerid);
					sqlStr.add("linklist", linklist.toString());
					dbHelper.insert(tab_pla_mail, sqlStr);
				}	
			}
			
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取模版信息
	 */
	public void sendModelMail(DBHelper dbHelper, int[] receiverids, int num, Object[] titleReplace, Object[] contentReplace) throws Exception {
		sendModelMail(dbHelper, receiverids, num, titleReplace, contentReplace, null);
	}
	
	/**
	 * 获取模版信息
	 */
	public void sendModelMail(DBHelper dbHelper, int[] receiverids, int num, Object[] titleReplace, Object[] contentReplace, String customadjunct) throws Exception {
		DBPaRs modelRs = DBPool.getInst().pQueryA(tab_mail_model, "num="+num);
		String title = modelRs.getString("title");
		String content = modelRs.getString("note");
		String adjunct = null;
		if(modelRs.getString("award").equals("-1")){//-1表示用外部传入的奖励
			adjunct = customadjunct;
		} else 
		if(!modelRs.getString("award").equals("0")){//0表示没有奖励
			adjunct = modelRs.getString("award");
		}
		for(int i = 0; titleReplace != null && i < titleReplace.length; i++){
			title = title.replace("{"+i+"}", titleReplace[i].toString());
		}
		for(int i = 0; contentReplace != null && i < contentReplace.length; i++){
			content = content.replace("{"+i+"}", contentReplace[i].toString());
		}
		sendSysMail(dbHelper, receiverids, title, content, adjunct, 0);
	}
	
	/**
	 * 发系统邮件(多人)
	 */
	public ReturnValue sendSysMail(String receiverids, String title, String content, String adjunct, int smailid) throws Exception {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			sendSysMail(dbHelper, receiverids, title, content, adjunct, smailid);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 发系统邮件(多人)
	 */
	public void sendSysMail(DBHelper dbHelper, String receiverids, String title, String content, String adjunct, int smailid) throws Exception {
		JSONArray receiveridarr = new JSONArray(receiverids);
		for(int i = 0; i < receiveridarr.length(); i++){
			sendSysMail(dbHelper, receiveridarr.optInt(i), title, content, adjunct, smailid);
		}
	}
	
	/**
	 * 发系统邮件(多人)
	 */
	public void sendSysMail(DBHelper dbHelper, int[] receiverids, String title, String content, String adjunct, int smailid) throws Exception {
		for(int i = 0; receiverids != null && i < receiverids.length; i++){
			sendSysMail(dbHelper, receiverids[i], title, content, adjunct, smailid);
		}
	}
	
	/**
	 * 发系统邮件
	 */
	public void sendSysMail(DBHelper dbHelper, int receiverid, String title, String content, String adjunct, int smailid) throws Exception {
		sendMail(dbHelper, 0, null, receiverid, null, title, content, adjunct, smailid);//PNAME目前用于收集常用联系人，这里为系统邮件，传空，如有需要再具体化
	}
	
	/**
	 * 发邮件
	 * @param senderid 发件人ID
	 * @param sendername 发件人名
	 * @param receiverid 收件人ID
	 * @param receivername 收件人名
	 * @param title 标题
	 * @param content 内容
	 * @param adjunct 附件(格式为奖励格式)
	 * @param smailid 系统邮件库ID(非库邮件传0)
	 */
	public void sendMail(DBHelper dbHelper, int senderid, String sendername, int receiverid, String receivername, String title, String content, String adjunct, int smailid) throws Exception {
		int mailid = insertMail(dbHelper, senderid, sendername, receiverid, receivername, title, content, adjunct, smailid);
		JSONArray pusharr = new JSONArray();
		pusharr.add(mailid);//邮件ID
		pusharr.add(senderid);//发邮件角色ID
		pusharr.add(sendername);//发邮件角色名
		pusharr.add(title);//邮件标题
		pusharr.add(adjunct!=null&&!adjunct.equals("")?0:-1);//是否已提取附件
		PushData.getInstance().sendPlaToOne(SocketServer.ACT_MAIL_RECEIVER, pusharr.toString(), receiverid);
	}
	
	/**
	 * 插入邮件记录
	 */
	public int insertMail(DBHelper dbHelper, int senderid, String sendername, int receiverid, String receivername, String title, String content, String adjunct, int smailid) throws Exception {
		if(smailid != 0){
			synchronized (LockStor.getLock(LockStor.SMAIL_INSERT, receiverid)) {
				ResultSet mailStorRs = dbHelper.query(tab_mail_stor, "id", "playerid="+receiverid+" and smailid="+smailid);
				if(mailStorRs.next()){
					return mailStorRs.getInt("id");
				}
				SqlString sqlStr = new SqlString();
				sqlStr.add("playerid", receiverid);
				sqlStr.add("pname", receivername);
				sqlStr.add("senderid", 0);
				sqlStr.add("sendername", sendername);
				sqlStr.add("readed", 0);
				sqlStr.add("title", title);
				sqlStr.add("content", content);
				sqlStr.add("adjunct", adjunct);
				sqlStr.add("extracted", adjunct!=null&&!adjunct.equals("")?0:-1);
				sqlStr.addDateTime("createtime", MyTools.getTimeStr());
				sqlStr.add("smailid", smailid);
				int mailid = dbHelper.insertAndGetId(tab_mail_stor, sqlStr);
				return mailid;
			}
		} else {
			SqlString sqlStr = new SqlString();
			sqlStr.add("playerid", receiverid);
			sqlStr.add("pname", receivername);
			sqlStr.add("senderid", 0);
			sqlStr.add("sendername", sendername);
			sqlStr.add("readed", 0);
			sqlStr.add("title", title);
			sqlStr.add("content", content);
			sqlStr.add("adjunct", adjunct);
			sqlStr.add("extracted", adjunct!=null&&!adjunct.equals("")?0:-1);
			sqlStr.addDateTime("createtime", MyTools.getTimeStr());
			sqlStr.add("smailid", smailid);
			int mailid = dbHelper.insertAndGetId(tab_mail_stor, sqlStr);
			return mailid;
		}
	}
	
	/**
	 * 获取邮件内容
	 */
	public ReturnValue getMailContent(int playerid, int mailid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet mailStorRs = dbHelper.query(tab_mail_stor, "readed,content,adjunct,extracted", "id="+mailid+" and playerid="+playerid);
			if(!mailStorRs.next()){
				BACException.throwInstance("邮件未找到");
			}
			if(mailStorRs.getInt("readed")==0){
				SqlString sqlStr = new SqlString();
				sqlStr.add("readed", 1);
				dbHelper.update(tab_mail_stor, sqlStr, "id="+mailid);
			}
			String adjunct = mailStorRs.getString("adjunct");
			JSONArray contentarr = new JSONArray();
			contentarr.add(mailStorRs.getString("content"));//内容
			contentarr.add(adjunct);//附件
			return new ReturnValue(true, contentarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 提取附件
	 */
	public ReturnValue extractAdjunct(int playerid, int mailid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet mailStorRs = dbHelper.query(tab_mail_stor, "title,adjunct,extracted", "id="+mailid+" and playerid="+playerid);
			if(!mailStorRs.next()){
				BACException.throwInstance("邮件未找到");
			}
			if(mailStorRs.getInt("extracted")!=0){
				BACException.throwInstance("无附件可提取");
			}
			String adjunct = mailStorRs.getString("adjunct");
			if(adjunct==null){
				BACException.throwInstance("无附件可提取");
			}
			SqlString sqlStr = new SqlString();
			sqlStr.add("extracted", 1);
			dbHelper.update(tab_mail_stor, sqlStr, "id="+mailid);
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_MAIL_EXTRACT_ADJUNCT);
			JSONArray itemarr = AwardBAC.getInstance().getAward(dbHelper, playerid, adjunct, ItemBAC.SHORTCUT_MAIL, 0, gl);
			
			gl.addRemark("提取邮件："+GameLog.formatNameID(mailStorRs.getString("title"), mailid));
			gl.save();
			return new ReturnValue(true, itemarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 一键提取附件
	 */
	public ReturnValue shortcutExtractAdjunct(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			ResultSet mailStorRs = dbHelper.query(tab_mail_stor, "id,title,adjunct", "playerid="+playerid+" and extracted=0");
			StringBuffer sb = new StringBuffer();
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_MAIL_SHORTCUT_EXTRACT_ADJUNCT);
			while(mailStorRs.next()){
				if(sb.length() > 0){
					sb.append("|");
				}
				sb.append(mailStorRs.getString("adjunct"));
				gl.addRemark("提取邮件："+GameLog.formatNameID(mailStorRs.getString("title"), mailStorRs.getInt("id")));
			}
			if(sb.length() <= 0){
				BACException.throwInstance("没有可提取的附件");
			}
			SqlString sqlStr = new SqlString();
			sqlStr.add("readed", 1);
			sqlStr.add("extracted", 1);
			dbHelper.update(tab_mail_stor, sqlStr, "playerid="+playerid+" and extracted=0");
			JSONArray itemarr = AwardBAC.getInstance().getAward(dbHelper, playerid, sb.toString(), ItemBAC.SHORTCUT_MAIL, 0, gl);
			
			JSONArray returnarr = new JSONArray();
			returnarr.add(sb.toString());//奖励内容 3,30|4,30..
			returnarr.add(itemarr);
			
			gl.save();
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 一键删除
	 */
	public ReturnValue shortcatDel(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			dbHelper.delete(tab_mail_stor, "playerid="+playerid+" and extracted!=0 and smailid=0");
			SqlString sqlStr = new SqlString();
			sqlStr.add("readed", 2);
			dbHelper.update(tab_mail_stor, sqlStr, "playerid="+playerid+" and extracted!=0 and smailid!=0");
			
			GameLog.getInst(playerid, GameServlet.ACT_MAIL_SHORTCUT_DEL)
			.addRemark("一键删除邮件")
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 删除邮件
	 */
	public ReturnValue delMail(int playerid, int mailid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet mailStorRs = dbHelper.query(tab_mail_stor, "extracted,smailid", "id="+mailid+" and playerid="+playerid);
			if(!mailStorRs.next()){
				BACException.throwInstance("邮件未找到");
			}
			if(mailStorRs.getInt("extracted")==0){
				BACException.throwInstance("尚未提取附件，无法删除");
			}
			if(mailStorRs.getInt("smailid")!=0){
				SqlString sqlStr = new SqlString();
				sqlStr.add("readed", 2);
				dbHelper.update(tab_mail_stor, sqlStr, "id="+mailid);
			} else {
				dbHelper.delete(tab_mail_stor, "id="+mailid);
			}
			
			GameLog.getInst(playerid, GameServlet.ACT_MAIL_DEL)
			.addRemark("删除邮件："+mailid)
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取邮件列表
	 */
	public JSONArray getMailList(DBHelper dbHelper, int playerid) throws Exception {
		ResultSet mailStorRs = dbHelper.query(tab_mail_stor, null, "playerid="+playerid);
		JSONArray mailarr = new JSONArray();
		ArrayList<Integer> smailids = new ArrayList<Integer>();
		while(mailStorRs.next()){
			if(mailStorRs.getInt("readed")!=2){//非已删状态
				JSONArray arr = new JSONArray();
				arr.add(mailStorRs.getInt("id"));//邮件ID
				arr.add(mailStorRs.getInt("senderid"));//玩家ID
				arr.add(mailStorRs.getString("sendername"));//玩家名
				arr.add(mailStorRs.getInt("readed"));//是否已读
				arr.add(mailStorRs.getString("title"));//邮件标题
				arr.add(MyTools.getTimeLong(mailStorRs.getTimestamp("createtime")));
				arr.add(mailStorRs.getInt("extracted"));//是否已提取附件
				mailarr.add(arr);
			}
			smailids.add(mailStorRs.getInt("smailid"));
		}
		DBPsRs smStorRs = DBPool.getInst().pQueryS(BkSysMailBAC.tab_sys_mail_send_log, "tgr=1 and stopsend=0 and endtime>="+MyTools.getTimeStr());
		DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
		while(smStorRs.next()){
			String serverStr = smStorRs.getString("server");
			if(!(serverStr.equals("0") || serverStr.contains("|"+Conf.sid+"|"))){
				continue;
			}
			String channelStr = smStorRs.getString("channel");
			if(!(channelStr.equals("0") || channelStr.contains(plaRs.getString("channel")))){
				continue;
			}
			if(smailids.contains(smStorRs.getInt("id"))){
				continue;
			}
			String[][] filter = Tools.splitStrToStrArr2(smStorRs.getString("filtercond"), "|", ",");
			boolean match = true;
			for(int i = 0; filter != null && i < filter.length; i++){
				if(filter[i][0].equals("1")){
					match = plaRs.getTime("savetime") <= MyTools.getTimeLong(filter[i][1]);
				} else 
				if(filter[i][0].equals("2")){
					match = plaRs.getInt("lv") >= Tools.str2int(filter[i][1]);
				} else 
				if(filter[i][0].equals("3")){
					match = plaRs.getInt("vip") >= Tools.str2int(filter[i][1]);
				} else 
				if(filter[i][0].equals("-1")){
					match = Tools.contain(filter[i], plaRs.getString("vsid"));
				}
				if(!match){
					break;
				}
			}
			if(!match){
				continue;
			}
			int mailid = insertMail(dbHelper, 0, null, playerid, null, smStorRs.getString("title"), smStorRs.getString("content"), smStorRs.getString("adjunct"), smStorRs.getInt("id"));
			JSONArray arr = new JSONArray();
			arr.add(mailid);
			arr.add(0);//玩家ID
			arr.add(0);//玩家名
			arr.add(0);//是否已读
			arr.add(smStorRs.getString("title"));//邮件标题
			arr.add(smStorRs.getTime("createtime"));
			arr.add(smStorRs.getString("adjunct")!=null?0:-1);//是否已提取附件
			mailarr.add(arr);
		}
		dbHelper.closeRs(mailStorRs);
		JSONArray linkmanarr = new JSONArray();
		ResultSet plamailRs = dbHelper.query(tab_pla_mail, "linklist", "playerid="+playerid);
		if(plamailRs.next()){
			JSONArray linklist = new JSONArray(plamailRs.getString("linklist"));
			JSONArray pidlist = linklist.optJSONArray(0);
			JSONArray pnamelist = linklist.optJSONArray(1);
			for(int i = pidlist.length()-1; i >= 0; i--){
				JSONArray arr = new JSONArray();
				arr.add(pidlist.optInt(i));
				arr.add(pnamelist.optString(i));
				linkmanarr.add(arr);
			}
		}
		dbHelper.closeRs(plamailRs);
		JSONArray returnarr = new JSONArray();
		returnarr.add(mailarr);//邮件列表
		returnarr.add(linkmanarr);//常用联系人
		return returnarr;
	}
	
	//--------------静态区--------------
	
	private static MailBAC instance = new MailBAC();
	
	/**
	 * 获取实例
	 */
	public static MailBAC getInstance(){
		return instance;
	}
}
