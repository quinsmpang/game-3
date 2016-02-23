/*
 * Created on 2005-12-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ehc.system;


import server.config.ServerConfig;

import com.ehc.dbc.BaseActCtrl;



/**
 * 用户角色对应关系管理对象
 *
 */
public class UserRoleBAC extends BaseActCtrl
{        
    public static final String tbName = "tb_baUserRole";
    
	
	public UserRoleBAC()
	{
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}

}
