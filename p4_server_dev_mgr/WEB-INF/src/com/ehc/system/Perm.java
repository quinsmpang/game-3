package com.ehc.system;

/**
 * 权限对象
 *
 */
public class Perm {
	/**
	 * 模块名
	 */
	public String module;
	/**
	 * 权限名
	 */
	public String permission;
	
	public Perm(String module,String permission)
	{
		this.module=module;
		this.permission=permission;		
	}

}
