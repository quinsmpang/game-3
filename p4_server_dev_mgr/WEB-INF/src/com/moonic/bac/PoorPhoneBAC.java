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
 * 低端机型设置
 * @author 
 */
public class PoorPhoneBAC extends BaseActCtrl {
	public static String tbName = "tab_poor_phone";

	public PoorPhoneBAC() {
		super.setTbName(tbName);
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
			String vendor = request.getParameter("vendor");
			String phonemodel = request.getParameter("phonemodel");

			FormXML formXML = new FormXML();
			formXML.add("vendor", vendor);
			formXML.add("phonemodel", phonemodel);

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

	public ReturnValue del(PageContext pageContext) {
		ServletRequest req = pageContext.getRequest();
		int id = Tools.str2int(req.getParameter("id"));
		ReturnValue rv = super.del("id=" + id);
		return rv;
	}

	//------------------静态区----------------

	private static PoorPhoneBAC instance = new PoorPhoneBAC();

	public static PoorPhoneBAC getInstance() {
		return instance;
	}
}
