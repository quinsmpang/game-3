package com.moonic.bac;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import server.common.Tools;
import server.config.LogBAC;
import server.config.ServerConfig;
import util.IPAddressUtil;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.jspsmart.upload.SmartUpload;
import com.moonic.servlet.STSServlet;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;
import com.moonic.util.STSNetSender;

/**
 * 系统更新
 * @author 
 */
public class ServerUpdateBAC extends BaseActCtrl{
	public static String tbName = "tb_system_update";
	
	/**
	 * 构造
	 */
	public ServerUpdateBAC() {
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}
	
	/**
	 * 更新
	 */
	public ReturnValue update(PageContext pageContext) {
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.setEncode("UTF-8");
		try {
			smartUpload.initialize(pageContext);
			smartUpload.upload();
			com.jspsmart.upload.File file = smartUpload.getFile("file");
			int fileLen = file.getSize();
			if(!file.getFileName().toLowerCase().endsWith(".zip")){
				return new ReturnValue(false, "请上传zip文件");
			}
			byte[] fileBytes = new byte[fileLen];
			for(int i=0;i<fileBytes.length;i++) {
				fileBytes[i] = file.getBinaryData(i);
			}
			int type = Tools.str2int(smartUpload.getRequest().getParameter("updtype"));
			if(type == 0){//后台管理服
				return SystemUpdateBAC.getInstance().updateSystem(file.getFileName(), fileBytes);
			} else 
			if(type == 1 || type == 2){//用户服|游戏服
				String[] serverIds = smartUpload.getRequest().getParameterValues("serverId");
				if(serverIds == null){
					return new ReturnValue(false, "请选择要更新的游戏服务器");
				}
				short act = 0;
				byte servertype = 0;
				if(type == 1){
					act = STSServlet.M_SERVER_UPDATE;
					servertype = ServerBAC.STS_USER_SERVER;
				} else 
				{
					act = STSServlet.G_SERVER_UPDATE;
					servertype = ServerBAC.STS_GAME_SERVER;
				}
				STSNetSender sender = new STSNetSender(act);
				sender.dos.writeUTF(file.getFileName());
				sender.dos.writeInt(fileBytes.length);
				sender.dos.write(fileBytes);
				String where = MyTools.converWhere("or", "id", "=", serverIds);
				String resultStr = ServerBAC.getInstance().converNrsToPromptStr(ServerBAC.getInstance().sendReq(servertype, where, sender));
				//记录更新日志
				String ip = IPAddressUtil.getIp((HttpServletRequest)pageContext.getRequest());
				String username = (String)pageContext.getSession().getAttribute("username");
				LogBAC.logout("gameupdate", "来自"+ip+"的"+username+"用户提交了服务器("+type+")("+Tools.strArr2Str(serverIds)+")更新包"+file.getFileName());
				return new ReturnValue(true, resultStr);
			} else 
			{
				return new ReturnValue(false, "更新类型错误 type="+type);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		} 		
	}
	
	/**
	 * 删除指定记录
	 */
	public ReturnValue del(PageContext pageContext) {	
		ServletRequest req = pageContext.getRequest();
		int id = Tools.str2int(req.getParameter("id"));
		ReturnValue rv = super.del("id="+ id);			
		return rv;
	}
	
	/**
	 * 清除所有记录
	 */
	public ReturnValue clear() {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.execute("delete from "+tbName);	
			return new ReturnValue(true,"更新日志清除成功");
		} catch(Exception ex) {
			ex.printStackTrace();
			return new ReturnValue(false,ex.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	//--------------静态区---------------
	
	private static ServerUpdateBAC instance = new ServerUpdateBAC();
		
	public static ServerUpdateBAC getInstance() {
		return instance;
	}
}
