package com.moonic.bac;

import java.sql.ResultSet;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.SmartUpload;
import com.moonic.mgr.DBPoolMgr;
import com.moonic.servlet.STSServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;
import com.moonic.util.STSNetSender;

/**
 * 自定义活动
 * @author John
 */
public class CustomActivityBAC extends BaseActCtrl {
	public static final String tab_custom_activity = "tab_custom_activity";
	public static final String tab_custom_activity_stor = "tab_custom_activity_stor";
	public static final String tab_openserver_activity = "tab_openserver_activity";
	
	public CustomActivityBAC() {
		super.setTbName(tab_custom_activity);
		setDataBase(ServerConfig.getDataBase());
	}
	
	public ReturnValue save(PageContext pageContext) {
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.setEncode("UTF-8");
		try {
			smartUpload.initialize(pageContext);
			smartUpload.upload();
			com.jspsmart.upload.Request request = smartUpload.getRequest();
			
			int id = Tools.str2int(request.getParameter("id"));
			String name = request.getParameter("name");
			int actitype = Tools.str2int(request.getParameter("actitype"));
			String showtime = request.getParameter("showtime");
			String starttime = request.getParameter("starttime");
			String endtime = request.getParameter("endtime");
			String hidetime = request.getParameter("hidetime");
			String note = request.getParameter("note");
			String award = request.getParameter("award");
			String imgurl = request.getParameter("imgurl");
			int layout = Tools.str2int(request.getParameter("layout"));
			String opentime = request.getParameter("opentime");
			int expirationlen = Tools.str2int(request.getParameter("expirationlen"));
			long t1 = MyTools.getTimeLong(showtime);
			long t2 = MyTools.getTimeLong(starttime);
			long t3 = MyTools.getTimeLong(endtime);
			long t4 = MyTools.getTimeLong(hidetime);
			if(t1>t2){
				BACException.throwAndPrintInstance("显示时间不能大于开始时间");
			}
			if(t4 != 0){
				if(t1>t3){
					BACException.throwAndPrintInstance("显示时间不能大于结束时间");
				} else
				if(t1>t4){
					BACException.throwAndPrintInstance("显示时间不能大于隐藏时间");
				} else
				if(t2>t3){
					BACException.throwAndPrintInstance("开始时间不能大于结束时间");
				} else 
				if(t2>t4){
					BACException.throwAndPrintInstance("开始时间不能大于隐藏时间");
				} else 
				if(t3>t4){
					BACException.throwAndPrintInstance("结束时间不能大于隐藏时间");
				}	
			}
			String server = null;
			String chooseAll = request.getParameter("chooseAll");
			if(chooseAll!=null && chooseAll.equals("1")){
				server = "0";
			} else {
				String[] serverarr = request.getParameterValues("server");
				if(serverarr==null || serverarr.length<=0){
					return new ReturnValue(false, "必须选择服务器");
				}
				StringBuffer sb = new StringBuffer("|");
				for(int i = 0; i < serverarr.length; i++){
					sb.append(serverarr[i]);
					sb.append("|");
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
			
			FormXML formXML = new FormXML();
			formXML.add("name", name);
			formXML.add("actitype", actitype);
			formXML.addDateTime("showtime", showtime);
			formXML.addDateTime("starttime", starttime);
			if(t4 != 0){
				formXML.addDateTime("endtime", endtime);
				formXML.addDateTime("hidetime", hidetime);
			}
			formXML.add("note", note);
			formXML.add("award", award);
			formXML.add("imgurl", imgurl);
			formXML.add("server", server);
			formXML.add("channel", channel);
			formXML.add("layout", layout);
			if(opentime!=null && !opentime.equals("")){
				formXML.addDateTime("opentime", opentime);	
			}
			formXML.add("expirationlen", expirationlen);
			
			if (id > 0) // 修改
			{
				formXML.setAction(FormXML.ACTION_UPDATE);
				formXML.setWhereClause("id=" + id);
				ReturnValue rv = save(formXML);
				if (rv.success) {
					return new ReturnValue(true, "修改成功");
				} else {
					return new ReturnValue(false, "修改失败");
				}
			} else // 添加
			{
				formXML.setAction(FormXML.ACTION_INSERT);
				ReturnValue rv = save(formXML);
				if (rv.success) {
					return new ReturnValue(true, "保存成功");
				} else {
					return new ReturnValue(false, "保存失败");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.getMessage());
		}
	}
	
	/**
	 * 导入开服活动
	 */
	public ReturnValue importOpenServerActivity(int serverid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPsRs osactiRs = DBPool.getInst().pQueryS(tab_openserver_activity, "isopen=1");
			while(osactiRs.next()){
				SqlString sqlStr = new SqlString();
				sqlStr.add("name", osactiRs.getString("name"));
				sqlStr.add("actitype", osactiRs.getInt("actitype"));
				sqlStr.addDateTime("showtime", MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day*osactiRs.getInt("showtimeoffset")));
				sqlStr.addDateTime("starttime", MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day*osactiRs.getInt("starttimeoffset")));
				if(osactiRs.getInt("hidetimeoffset") != -1){
					sqlStr.addDateTime("endtime", MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day*osactiRs.getInt("endtimeoffset")));
					sqlStr.addDateTime("hidetime", MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day*osactiRs.getInt("hidetimeoffset")));
				}
				sqlStr.add("note", osactiRs.getString("note"));
				sqlStr.add("award", osactiRs.getString("award"));
				if(!osactiRs.getString("imgurl").equals("-1")){
					sqlStr.add("imgurl", osactiRs.getString("imgurl"));	
				}
				sqlStr.add("layout", osactiRs.getInt("layout"));
				sqlStr.add("server", "|"+serverid+"|");
				sqlStr.add("channel", osactiRs.getString("channel"));
				sqlStr.add("expirationlen", osactiRs.getInt("expirationlen"));
				dbHelper.insert(tab_custom_activity, sqlStr);
			}
			DBPoolMgr.getInstance().addClearTablePoolTask(tab_custom_activity, null);
			return new ReturnValue(true, "导入完成");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	public ReturnValue del(PageContext pageContext) {
		ServletRequest req = pageContext.getRequest();
		int id = Tools.str2int(req.getParameter("id"));
		ReturnValue rv = super.del("id=" + id);
		if(rv.success){
			DBHelper dbHelper = new DBHelper();
			try {
				dbHelper.openConnection();
				dbHelper.delete(tab_custom_activity_stor, "actiid="+id);
				STSNetSender sender = new STSNetSender(STSServlet.G_MIRROR_CLEAR_TAB);
				sender.dos.writeUTF(tab_custom_activity_stor);
				ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_GAME_SERVER, sender);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbHelper.closeConnection();
			}
		}
		return rv;
	}
	
	public void clearData(DBHelper dbHelper) throws Exception {
		ResultSet rs = dbHelper.query(tab_custom_activity, "id", "hidetime is not null and hidetime<="+MyTools.getTimeStr(System.currentTimeMillis()-MyTools.long_day*7));
		while(rs.next()){
			dbHelper.delete(tab_custom_activity_stor, "actiid="+rs.getInt("id"));
			dbHelper.delete(tab_custom_activity, "id="+rs.getInt("id"));
		}
		STSNetSender sender = new STSNetSender(STSServlet.G_MIRROR_CLEAR_TAB);
		sender.dos.writeUTF(tab_custom_activity_stor);
		ServerBAC.getInstance().sendReqToAll(ServerBAC.STS_GAME_SERVER, sender);
		DBPoolMgr.getInstance().addClearTablePoolTask(tab_custom_activity, null);
	}
	
	//--------------静态区--------------
	
	private static CustomActivityBAC instance = new CustomActivityBAC();
	
	public static CustomActivityBAC getInstance() {
		return instance;
	}
}
