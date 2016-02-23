package com.moonic.bac;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.SmartUpload;
import com.moonic.util.BACException;
import com.moonic.util.MyTools;

/**
 * 系统公告
 * @author John
 */
public class SysNoticeBAC extends BaseActCtrl {
	public static String tbName = "tab_sys_notice";
	
	/**
	 * 构造
	 */
	public SysNoticeBAC() {
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
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			String writer = request.getParameter("writer");
			String starttime = request.getParameter("starttime");
			String overtime = request.getParameter("overtime");
			
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
			formXML.add("title", title);
			formXML.add("content", content);
			formXML.add("writer", writer);
			formXML.addDateTime("starttime", starttime);
			formXML.addDateTime("overtime", overtime);
			formXML.addDateTime("createtime", MyTools.getTimeStr());
			formXML.add("channel", channel);
			
			if(MyTools.getTimeLong(starttime)>=MyTools.getTimeLong(overtime)){
				BACException.throwInstance("发布时间不能超过过期时间");
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
	
	public ReturnValue del(PageContext pageContext) {
		ServletRequest req = pageContext.getRequest();
		int id = Tools.str2int(req.getParameter("id"));
		ReturnValue rv = super.del("id=" + id);
		return rv;
	}
	
	//----------------静态区------------------
	
	private static SysNoticeBAC instance = new SysNoticeBAC();
	
	public static SysNoticeBAC getInstance() {
		return instance;
	}
}
