package com.moonic.bac;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.SmartUpload;

/**
 * 服务器异常邮件发送地址
 * @author John
 */
public class ServerExcMailBAC extends BaseActCtrl {
	public static String tbName = "tab_server_exc_mail_addr";
	
	/**
	 * 构造
	 */
	public ServerExcMailBAC() {
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}
	
	/**
	 * 添加/更新
	 */
	public ReturnValue save(PageContext pageContext) {
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.setEncode("UTF-8");
		try {
			smartUpload.initialize(pageContext);
			smartUpload.upload();
			com.jspsmart.upload.Request request = smartUpload.getRequest();
			
			int id = Tools.str2int(request.getParameter("id"));
			String name=request.getParameter("name");
			String mailaddr=request.getParameter("mailaddr");

			FormXML formXML = new FormXML();
			formXML.add("name",name);
			formXML.add("mailaddr",mailaddr);
			
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
	 * 删除记录
	 */
	public ReturnValue del(PageContext pageContext) {
		ServletRequest req = pageContext.getRequest();
		int id = Tools.str2int(req.getParameter("id"));
		ReturnValue rv = super.del("id=" + id);
		return rv;
	}
	
	//--------------静态区---------------
	
	private static ServerExcMailBAC instance = new ServerExcMailBAC();
	
	/**
	 * 获取实例
	 */
	public static ServerExcMailBAC getInstance() {
		return instance;
	}
}
