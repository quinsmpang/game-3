
package com.ehc.system;

import java.io.FileInputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ehc.common.ArrBuffer;

/**
 * 权限管理对象
 *
 */
public class Permission
{
	
	/**
	 * 权限集合
	 */
	private static Vector permVC;
	
	/**
     * 从permission.xml文件中取得所有的权限
     * @param fileName String: 权限配置文件名
     */
	public static void initPermission(String fileName)
	{
		try{
			
			FileInputStream fis = new FileInputStream(fileName);
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(fis);
			
			doc.normalize();
			NodeList perList = doc.getElementsByTagName("permissionList");
			
			//myPermission = new MyHashMap();
			permVC = new Vector();

			for (int i=0; i<perList.getLength(); i++){
				Element link = (Element) perList.item(i); //link就是<permissionList>				
				NodeList groups = link.getElementsByTagName("module");
				for(int j=0; j<groups.getLength(); j++){  //遍历每个module
					String groupName = ((Element)groups.item(j)).getAttribute("name");

					NodeList powers = ((Element)groups.item(j)).getElementsByTagName("permission");
					for(int k=0;k<powers.getLength();k++)
					{
						String permissionName=powers.item(k).getFirstChild().getNodeValue();
						permVC.add(new Perm(groupName,permissionName));
					}					
				}
			}			
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	
	}
	
	/**
     * 获取指定的权限模块的相关权限
     * @param moduleName String:权限模块名称
     * @return String[]: 返回指定模块的权限名数组
     */
	public static String[] getPermissionsOfModule(String moduleName)
	{
		ArrBuffer arrBuff = new ArrBuffer();
		for(int i=0;permVC!=null && i<permVC.size();i++)
		{
			Perm perm=(Perm)permVC.elementAt(i);
			if(perm.module.equals(moduleName))
			{
				arrBuff.add(perm.permission);
			}
		}
		if(arrBuff.size()>0)
		{
			return arrBuff.getStrArr();
		}
		else
		{
			return null;
		}		
	}
	
	/**
     * 获取所有的模块名的数组
     * @return String[]:返回所有模块名的数组
     */
	public static String[] getAllModule()
	{
		ArrBuffer arrBuff = new ArrBuffer();
		
		for(int i=0;permVC!=null && i<permVC.size();i++)
		{
			Perm perm=(Perm)permVC.elementAt(i);
			
			//检查是否已包含该module
			if(!arrBuff.contains(perm.module))
			{
				arrBuff.add(perm.module);	
			}
		}
		if(arrBuff.size()>0)
		{
			return arrBuff.getStrArr();
		}
		else
		{
			return null;
		}	
	}
	
	/**
	 * 检测权限是否存在
	 * @param module String:模块名
	 * @param permName String:权限名
	 * @return boolean:返回结果
	 */
	public static boolean isExistPermission(String module,String permName)
	{		
		for(int i=0;permVC!=null && i<permVC.size();i++)
		{
			Perm perm=(Perm)permVC.elementAt(i);
			if(perm.module.equals(module) && perm.permission.equals(permName))
			{
				return true;
			}
		}
		return false;
	}
}