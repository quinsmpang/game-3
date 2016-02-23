/*
 * Created on 2005-12-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ehc.system;


import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import server.config.LogBAC;
import server.config.ServerConfig;
import util.IPAddressUtil;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.common.ToolFunc;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.dbc.DataXmlAdapter;
import com.ehc.xml.AimXML;
import com.moonic.util.DBHelper;



/**
 * 用户组（角色）管理对象
 *
 */
public class RoleBAC extends BaseActCtrl
{
    public static final String tbName = "tb_baRole";
	
	public RoleBAC()
	{		
		super.setTbName(tbName);	
		setDataBase(ServerConfig.getDataBase());
	}
	

    /**
     * 添加新组时检测组名的唯一性
     * @param id int:role的id
     * @param roleName String: 组名
     * @return ReturnValue:返回结果,true则组名可用
     */
    public ReturnValue checkRoleId(int id,String roleName)
    {
    	DataXmlAdapter adapter = new DataXmlAdapter(ServerConfig.getDataBase());
    	
    	
    	AimXML xml=null;
      
    	String sql=null;
    	if(id==0)
        {
        	sql="select id from "+ tbName + " where roleName = '"+ roleName +"'";
        }
        else
        {
        	sql="select id from "+ tbName + " where id<> "+ id +" and roleName='"+ roleName +"'";
        }        	
        xml = adapter.getRsPageToXML(sql, 1, 1);
            
        if(xml!=null)
        {
        	return new ReturnValue(false,"该组名已存在");	
        }
        else
        {
        	return new ReturnValue(true,"组名可以使用");
        }
       
    }
    
    /**
     * 取得指定角色的权限对象数组
     * @param roleId int:角色id
     * @return Perm[]:返回权限对象数组
     */
    public Perm[] getPermissions(int roleId)
    {
    	RolePermissionBAC PermBAC = new RolePermissionBAC();
    	AimXML xml=PermBAC.getXMLObjs("roleId="+ roleId,"id");
    	Vector vc=new Vector();
    	if(xml!=null)
    	{
    		xml.openRs(RolePermissionBAC.tbName);
    		while(xml.next())
    		{
    			String moudleStr=xml.getRsValue("ModuleId");
    			String permStr=xml.getRsValue("Permission");
    			vc.add(new Perm(moudleStr,permStr));
    		}
    	}
    	if(vc.size()>0)
    	{
    		Perm[] perms=new Perm[vc.size()];
    		vc.toArray(perms);
    		return perms;
    	}
    	else
    	{    		
    		return null;
    	}    		
    }
    
    public ReturnValue save(PageContext pageContext)
	{				
    	HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();    	
	
		int id=ToolFunc.str2int(request.getParameter("id"));
		
		String roleName=request.getParameter("roleName");
		int roleType=ToolFunc.str2int(request.getParameter("roleType"));		
		
		//检查唯一性
		ReturnValue rv = checkRoleId(id,roleName);
		if(!rv.success)
		{
			return rv;
		}
		DBHelper dbHelper = new DBHelper();
		
		try {
			dbHelper.openConnection();
			SqlString sqlS = new SqlString();
			
			sqlS.add("roleName",roleName);
			sqlS.add("roleType",roleType);
			sqlS.add("isEnable",1);
			if(id>0)
			{				
				dbHelper.update(tbName, sqlS, "id="+id);
			}
			else
			{				
				dbHelper.insert(tbName, sqlS);
				String opusername = (String)pageContext.getSession().getAttribute("username");
				TBLogParameter  parameter=TBLogParameter.getInstance();
				parameter.addParameter("note", "新增组："+roleName);
				LogBAC.addLog(opusername,"权限分组",parameter.toString(),IPAddressUtil.getIp(request));
			}
			return new ReturnValue(true,"保存成功");
		} catch (Exception e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
			return new ReturnValue(true,"保存失败："+e.toString());
		}
		finally
		{
			dbHelper.closeConnection();
		}					
	}

}
