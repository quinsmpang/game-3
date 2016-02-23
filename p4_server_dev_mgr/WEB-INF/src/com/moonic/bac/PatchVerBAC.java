package com.moonic.bac;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.SmartUpload;


public class PatchVerBAC extends BaseActCtrl
{	
	public static String tbName = "tab_version_patch";	 
	private static PatchVerBAC self;	 
	  		
	public static PatchVerBAC getInstance()
	{						
		if(self==null)
		{
			self = new PatchVerBAC();
		}
		return self;
	}
	
	public PatchVerBAC()
	{			
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}

	public ReturnValue save(PageContext pageContext)
	{
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.setEncode("UTF-8");
		try {
			smartUpload.initialize(pageContext);
			smartUpload.upload();
			com.jspsmart.upload.Request request = smartUpload.getRequest();				
			
			int id=Tools.str2int(request.getParameter("id"));
			String channel=request.getParameter("channel");
			String packagename=request.getParameter("packagename");
			String fromversion=request.getParameter("fromversion");
			String toversion=request.getParameter("toversion");
			String patchfile=request.getParameter("patchfile");
			int filesize=Tools.str2int(request.getParameter("filesize"));
			String savetime=request.getParameter("savetime");
			String subfolder="";
			String platform=request.getParameter("platform");
			String crc=request.getParameter("crc");
			if(crc!=null)crc=crc.toUpperCase();
			
			FormXML formXML = new FormXML();
			formXML.add("channel",channel);
			formXML.add("packagename",packagename);
			formXML.add("fromversion",fromversion);
			formXML.add("toversion",toversion);
			formXML.add("patchfile",patchfile);
			formXML.add("filesize",filesize);
			formXML.addDateTime("savetime",savetime);
			formXML.add("subfolder",subfolder);
			formXML.add("platform",platform);
			formXML.add("crc",crc);
			
			if(id>0)
			{
				int count = getCount("fromversion='"+fromversion+"' and toversion='"+toversion+"' and platform='"+platform+"' and id <>"+id);
				if(count>0)
				{
						return new ReturnValue(false,"版本重复");
				}
			}
			else
			{
				int count = getCount("fromversion='"+fromversion+"' and toversion='"+toversion+"' and platform='"+platform+"'");
				if(count>0)
				{
						return new ReturnValue(false,"版本重复");
				}
			}
			
			if(id>0)  //修改
			{	
				formXML.setAction(FormXML.ACTION_UPDATE);
				formXML.setWhereClause("id=" + id);
				ReturnValue rv = save(formXML);	
				if(rv.success)
				{
				  return new ReturnValue(true,"修改成功");
				}else
				{
				  return new ReturnValue(false,"修改失败");
				}					
			}else  //添加
			{
				formXML.setAction(FormXML.ACTION_INSERT);
				ReturnValue rv =save(formXML);
				if(rv.success)
				{
				  return new ReturnValue(true,"保存成功");
				}else
				{
				  return new ReturnValue(false,"保存失败");
				}			
			}
		} 
		catch (Exception e) 
		{			
			e.printStackTrace();
			return new ReturnValue(false,e.getMessage());
		} 		
	}
	
	public ReturnValue del(PageContext pageContext)
	{	
		ServletRequest req = pageContext.getRequest();
		int id = Tools.str2int(req.getParameter("id"));
		ReturnValue rv = super.del("id="+ id);			
		//todo 删除其他关联表中的记录
		return rv;
	}
}
