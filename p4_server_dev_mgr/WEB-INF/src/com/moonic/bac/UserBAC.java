package com.moonic.bac;

import java.io.File;
import java.sql.ResultSet;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.moonic.mgr.PookNet;
import com.moonic.mgr.TabStor;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBUtil;
import com.moonic.util.FileUtil;
import com.moonic.util.JsonRs;
import com.moonic.util.MD5;
import com.moonic.util.MyTools;
import com.moonic.util.NetFormSender;
import com.moonic.util.StreamHelper;

import conf.Conf;

/**
 * 用户BAC
 * @author John
 */
public class UserBAC extends BaseActCtrl{
	public static final String tab_user = "tab_user";
	
	/**
	 * 构造
	 */
	public UserBAC() {
		super.setTbName(tab_user);
		setDataBase(ServerConfig.getDataBase_Backup());
	}
	
	/**
	 * 创建强登码
	 */
	private String createPwd(){
		char[] pwdChar = new char[16];
		for(int i = 0; i < pwdChar.length; i++){
			int type = MyTools.getRandom(0, 2);
			if(type == 0){
				pwdChar[i] = (char)MyTools.getRandom(48, 57);	
			} else 
			if(type == 1){
				pwdChar[i] = (char)MyTools.getRandom(65, 90);	
			} else 
			{
				pwdChar[i] = (char)MyTools.getRandom(97, 122);		
			}
		}
		return new String(pwdChar);
	}
	
	public ReturnValue save(PageContext pageContext)
	{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		try {						
			
			int id=Tools.str2int(request.getParameter("id"));
			
			if(id == 0){
				BACException.throwInstance("ERROR:ID为0");
			}
			
			int devuser=Tools.str2int(request.getParameter("devuser"));
			int enforcementlogin=Tools.str2int(request.getParameter("enforcementlogin"));
			
			FormXML formXML = new FormXML();
			formXML.add("devuser",devuser);
			formXML.add("enforcementlogin",enforcementlogin==1?createPwd():null);
			
			formXML.setAction(FormXML.ACTION_UPDATE);
			formXML.setWhereClause("id=" + id);
			setDataBase(ServerConfig.getDataBase());
			ReturnValue rv = save(formXML);	
			setDataBase(ServerConfig.getDataBase_Backup());
			if(rv.success)
			{
			  return new ReturnValue(true,"修改成功");
			}else
			{
			  return new ReturnValue(false,"修改失败");
			}
		} 
		catch (Exception e) 
		{			
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		} 		
	}
	
	public JSONObject getPageList(PageContext pageContext)
	{
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
		String ordertype=request.getParameter("ordertype");
		if(ordertype==null || ordertype.equals(""))
		{
			ordertype="DESC";
		}
		String showorder=request.getParameter("showorder");
		if(showorder==null || showorder.equals(""))
		{
			showorder="tab_user.id";
		}
		String colname=request.getParameter("colname");
		String colvalue =request.getParameter("colvalue");
		String operator=request.getParameter("operator");
		
		
		SqlString sqlS = new SqlString();
		String orderClause = showorder + " " + ordertype;
		
		if(colname!=null && !colname.equals("") && colvalue!=null && !colvalue.equals(""))
		{
			if(operator.equals("等于"))
			{
				sqlS.add(colname,colvalue);
			}
			else
			{
				sqlS.add(colname,colvalue,"like");
			}
		}		
		String channel=Tools.strNull(request.getParameter("channel"));
		String platform=Tools.strNull(request.getParameter("platform"));
		if(!channel.equals(""))
		{
			sqlS.add("tab_user.channel",channel);
		}
		if(!platform.equals(""))
		{
			sqlS.add("tab_user.platform",platform);
		}
		String devuser=Tools.strNull(request.getParameter("devuser"));
		if(!devuser.equals(""))
		{
			sqlS.add("tab_user.devuser",Tools.str2int(devuser));
		}
		String enable=Tools.strNull(request.getParameter("enable"));
		if(!enable.equals(""))
		{
			sqlS.add("tab_user.enable",Tools.str2int(enable));
		}
		
		String sql="select id,username,enable,logintime,regtime,playerid,devuser,channel,serverid,platform " + "from tab_user " + sqlS.whereStringEx()+" order by "+orderClause;
		//System.out.println(sql);
		JSONObject jsonObj =getJsonPageListBySQL(sql, page, rpp);
		return jsonObj;
	}
	
	/**
	 * 转换帐号
	 */
	public ReturnValue converToPook(PageContext pageContext){
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.setEncode("UTF-8");
		DBHelper dbHelper = new DBHelper();
		try {
			smartUpload.initialize(pageContext);
			smartUpload.upload();
			String dir = Conf.logRoot+"convertopook/";
			ReturnValue rv = StreamHelper.getInstance().upload(smartUpload, dir, "converfile");
			if(!rv.success){
				return rv;
			}
			String filename = rv.info;
			//System.out.println(MyTools.readTxtFile(dir+rv.info));
			String[][] temparr = Tools.getStrLineArrEx2(MyTools.readTxtFile(dir+filename), "data:", "dataEnd");
			if(temparr == null){
				BACException.throwInstance("清单为空");
			}
			Request request = smartUpload.getRequest();
			String channel = request.getParameter("channel");
			DBPaRs channelRs = DBPool.getInst().pQueryA(TabStor.tab_channel, "code='"+channel+"'");
			if(!channelRs.exist()){
				BACException.throwInstance("渠道未找到");
			}
			dbHelper.openConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("帐号\t联运渠道\t替换后帐号\t替换后联运渠道\t失败备注\r\n");
			for(int i = 0; i < temparr.length; i++){
				ResultSet rs = dbHelper.query("tab_user", "platform", "username='"+temparr[i][0]+"' and channel='"+temparr[i][1]+"'");
				if(rs.next()){
					if(!rs.getString("platform").equals("001")){
						String username = pookRegister(dbHelper);
						SqlString sqlStr = new SqlString();
						sqlStr.add("username", username);
						sqlStr.add("channel", channel);
						sqlStr.add("platform", channelRs.getString("platform"));
						dbHelper.update("tab_user", sqlStr, "username='"+temparr[i][0]+"' and channel='"+temparr[i][1]+"'");
						sb.append(temparr[i][0]+"\t"+temparr[i][1]+"\t"+username+"\t"+channel+"\r\n");		
					} else {
						sb.append(temparr[i][0]+"\t"+temparr[i][1]+"\tNULL\tNULL\t（要替换帐号为波克帐号，不需要转换）\r\n");
					}
				} else {
					sb.append(temparr[i][0]+"\t"+temparr[i][1]+"\tNULL\tNULL\t（要替换的帐号未找到）\r\n");
				}
				dbHelper.closeRs(rs);
			}
			File srcfile = new File(dir+filename);
			srcfile.delete();
			FileUtil fileutil = new FileUtil();
			fileutil.writeNewToTxt(dir+Tools.getCurrentDateTimeStr("yyyy-MM-dd-HH-mm-ss")+"_"+filename.replace(".txt", "（已转换）")+".txt", sb.toString());
			return new ReturnValue(true, "转换成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	private static final char[] chars = new char[]{
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
		};
	
	public String pookRegister(DBHelper dbHelper) throws Exception {
		String username = null;
		boolean isRun;
		do {
			try {
				isRun = false;
				StringBuffer str = new StringBuffer();
				str.append("user");
				for(int i = 0; i < 5; i++){
					str.append(chars[MyTools.getRandom(0, chars.length-1)]);
				}
				username = str.toString();
				username = username.toLowerCase(); //波克用户名强行转小写
				NetFormSender sender = new NetFormSender(PookNet.register_do);
				sender.addParameter("rUser.agentId", "091");
				//sender.addParameter("rUser.agentId", "10490912"); //下次维护时采用
				sender.addParameter("rUser.userName", username);
				sender.addParameter("rUser.password", username);
				sender.addParameter("rUser.rePassword", username);
				sender.addParameter("rUser.validCode", "webLobby");
				sender.addParameter("ipString", "118.26.160.166");
				StringBuffer ticket = new StringBuffer();
				ticket.append("091");
				ticket.append("_");
				ticket.append(username);
				ticket.append("_");
				ticket.append(username);
				ticket.append("_");
				ticket.append(username);
				ticket.append("_");
				ticket.append("webLobby");
				ticket.append("_");
				ticket.append("118.26.160.166");
				ticket.append("_");
				ticket.append(PookNet.screctKey);
				sender.addParameter("ticket", MD5.encode(ticket.toString()));
				
				sender.send().check();
				//System.out.println("注册返回"+sender.rv.info);
				JSONObject pokerobj = new JSONObject(sender.rv.info);
				String result = pokerobj.getString("result");
				if(result.equals("S")){
				} else 
				if(result.equals("E")){
					BACException.throwInstance(pokerobj.getString("message"));
				} else 
				{
					BACException.throwInstance("请求失败");
				}		
			} catch (Exception e) {
				isRun = true;
			}
		} while(isRun);
		return username;
	}
	
	/**
	 * 根据用户ID获取用户信息
	 */
	public JsonRs getDataRs(int userid){
		JsonRs returnRs = null;
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		try {
			dbHelper.openConnection();
			ResultSet userRs = dbHelper.query(tab_user, null, "id="+userid);
			returnRs = DBUtil.convertRsToJsonRs(userRs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
		return returnRs;
	}
	
	//--------------静态区--------------
	
	private static UserBAC instance = new UserBAC();
	
	/**
	 * 获取实例
	 */
	public static UserBAC getInstance(){
		return instance;
	}
}
