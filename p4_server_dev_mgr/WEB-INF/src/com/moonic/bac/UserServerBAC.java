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
 * 用户服务器
 * @author 
 */
public class UserServerBAC extends BaseActCtrl {
	public static String tab_user_server = "tab_user_server";
	
	/**
	 * 构造
	 */
	public UserServerBAC() {
		super.setTbName(tab_user_server);
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
			String http=request.getParameter("http");

			FormXML formXML = new FormXML();
			formXML.add("name",name);
			formXML.add("http",http);

			if(id>0)
			{
				int count = getCount("http='"+http+"' and id <>"+id);
				if(count>0)
				{
						return new ReturnValue(false,"地址重复");
				}
			}
			else
			{
				int count = getCount("http='"+http+"'");
				if(count>0)
				{
						return new ReturnValue(false,"地址重复");
				}
			}
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
	
	private static UserServerBAC instance = new UserServerBAC();
	
	/**
	 * 获取实例
	 */
	public static UserServerBAC getInstance() {
		return instance;
	}
}
