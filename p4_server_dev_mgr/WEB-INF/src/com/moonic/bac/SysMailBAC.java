package com.moonic.bac;

import javax.servlet.jsp.PageContext;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.jspsmart.upload.SmartUpload;
import com.moonic.mgr.DBPoolMgr;
import com.moonic.mgr.DBPoolMgrListener;
import com.moonic.servlet.STSServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;
import com.moonic.util.STSNetSender;
import com.moonic.util.StreamHelper;

import conf.Conf;

/**
 * 系统邮件
 * @author John
 */
public class SysMailBAC extends BaseActCtrl {
	public static final String tab_mail_stor = "tab_mail_stor";
	public static final String tab_sys_mail_send_log = "tab_sys_mail_send_log";
	
	/**
	 * 构造
	 */
	public SysMailBAC() {
		super.setTbName(tab_sys_mail_send_log);
		setDataBase(ServerConfig.getDataBase());
	}
	
	public static final String[] tgrType = {"指定玩家", "指定服务器"};
	
	/**
	 * 添加
	 */
	public ReturnValue save(PageContext pageContext) {
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.setEncode("UTF-8");
		DBHelper dbHelper = new DBHelper();
		try {
			smartUpload.initialize(pageContext);
			smartUpload.upload();
			com.jspsmart.upload.Request request = smartUpload.getRequest();
			
			int tgr = Tools.str2int(request.getParameter("tgr"));
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			String adjunct = request.getParameter("adjunct");
			String filtercond = request.getParameter("filtercond");
			String endtime = request.getParameter("endtime");
			
			SqlString sqlStr = new SqlString();
			sqlStr.add("title", title);
			sqlStr.add("content", content);
			sqlStr.add("adjunct", adjunct);
			sqlStr.add("filtercond", filtercond);
			sqlStr.add("tgr", tgr);
			sqlStr.add("stopsend", 0);
			sqlStr.addDateTime("endtime", MyTools.getTimeStr(MyTools.getTimeLong(endtime)));
			sqlStr.addDateTime("createtime", MyTools.getTimeStr());
			
			if(tgr == 0){//指定玩家
				long currtime = 0;
				
				synchronized (this) {
					currtime = System.currentTimeMillis();
					currtime-=currtime%1000;
					Thread.sleep(1000);
				}
				
				String dir = Conf.logRoot+"mail/";
				ReturnValue rv = StreamHelper.getInstance().upload(smartUpload, dir, "tgrfile", currtime+".txt");
				if(!rv.success){
					return rv;
				}
				
				String[][] plaarr = Tools.getStrLineArrEx2(MyTools.readTxtFile(dir+currtime+".txt"), "data:", "dataEnd");//服务器ID 角色ID
				
				if(plaarr == null || plaarr.length <= 0){
					BACException.throwInstance("无效数据");
				}
				
				JSONObject serverobj = new JSONObject();//按服务器记录角色
				int count = 0;
				for(int i = 0; i < plaarr.length; i++){
					if(plaarr[i] != null){
						JSONObject obj = serverobj.optJSONObject(plaarr[i][0]);
						if(obj == null){
							obj = new JSONObject();
							obj.put("sid", plaarr[i][0]);
							obj.put("pid", new JSONArray());
							serverobj.put(plaarr[i][0], obj);//服务器ID-待发送数据
						}
						obj.optJSONArray("pid").add(plaarr[i][1]);//加入角色ID
						count++;
					}
				}
				if(count <= 0){
					BACException.throwInstance("无效数据");
				}
				
				sqlStr.add("createmark", String.valueOf(currtime));
				final int smailid = dbHelper.insertAndGetId(tab_sys_mail_send_log, sqlStr);
				
				final JSONObject back_serverobj = serverobj;
				DBPoolMgr.getInstance().addClearTablePoolTask(tab_sys_mail_send_log, new DBPoolMgrListener() {
					public void callback() {
						try {
							JSONArray array = back_serverobj.toJSONArray();
							for(int i = 0; i < array.length(); i++){
								JSONObject obj = array.optJSONObject(i);
								STSNetSender sender = new STSNetSender(STSServlet.G_BK_SEND_SYS_MAIL);
								sender.dos.writeUTF(obj.optJSONArray("pid").toString());
								sender.dos.writeInt(smailid);
								ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, obj.optInt("sid"));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} else 
			if(tgr == 1){//指定服务器
				String server = null;
				String chooseAll = request.getParameter("chooseAll");
				int[] int_sids = null;
				if(chooseAll!=null && chooseAll.equals("1")){
					server = "0";
				} else {
					String[] serverarr = request.getParameterValues("server");
					if(serverarr==null || serverarr.length<=0){
						return new ReturnValue(false, "必须选择服务器");
					}
					int_sids = new int[serverarr.length];
					StringBuffer sb = new StringBuffer("|");
					for(int i = 0; i < serverarr.length; i++){
						sb.append(serverarr[i]);
						sb.append("|");
						int_sids[i] = Tools.str2int(serverarr[i]);
					}
					server = sb.toString();
				}
				
				String channel = null;
				String chooseAll_channel = request.getParameter("chooseAll_channel");
				if(chooseAll_channel!=null && chooseAll_channel.equals("1")){
					channel = "0";
				} else {
					String[] channelarr = request.getParameterValues("channel");
					if(channelarr==null || channelarr.length<=0){
						return new ReturnValue(false, "必须选择渠道");
					}
					StringBuffer sb = new StringBuffer("|");
					for(int i = 0; i < channelarr.length; i++){
						sb.append(channelarr[i]);
						sb.append("|");
					}
					channel = sb.toString();
				}
				
				sqlStr.add("server", server);
				sqlStr.add("channel", channel);
				final int smailid = dbHelper.insertAndGetId(tab_sys_mail_send_log, sqlStr);
				
				final String back_server = server;
				final int[] back_int_sids = int_sids;
				
				DBPoolMgr.getInstance().addClearTablePoolTask(tab_sys_mail_send_log, new DBPoolMgrListener() {
					public void callback() {
						try {
							if(back_server.equals("0")){
								STSNetSender sender = new STSNetSender(STSServlet.G_BK_SEND_SERVER_SYS_MAIL);
								sender.dos.writeInt(smailid);
								ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_GAME_SERVER, sender);
							} else {
								STSNetSender sender = new STSNetSender(STSServlet.G_BK_SEND_SERVER_SYS_MAIL);
								sender.dos.writeInt(smailid);
								ServerBAC.getInstance().sendReqToSome(ServerBAC.STS_GAME_SERVER, sender, back_int_sids);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			return new ReturnValue(true, "发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, "发送失败："+e.getMessage());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 使邮件失效
	 */
	public ReturnValue disenabled(int id){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.add("stopsend", 1);
			dbHelper.update(tab_sys_mail_send_log, sqlStr, "id="+id);
			DBPoolMgr.getInstance().addClearTablePoolTask(tab_sys_mail_send_log, null);
			return new ReturnValue(true, "处理成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 清理数据
	 */
	public void clearData(DBHelper dbHelper) throws Exception {
		dbHelper.delete(tab_mail_stor, "createtime<="+MyTools.getTimeStr(MyTools.getCurrentDateLong()-MyTools.long_day*7));
	}
	
	//--------------静态区---------------
	
	private static SysMailBAC instance = new SysMailBAC();
	
	/**
	 * 获取实例
	 */
	public static SysMailBAC getInstance() {
		return instance;
	}
}
