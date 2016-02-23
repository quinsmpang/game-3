package com.moonic.bac;


import org.json.JSONObject;
import java.sql.ResultSet;
import com.moonic.util.DBHelper;
import com.moonic.util.Pool;


public class ResFilelistBAC
{	
	public static String tbName = "tab_version_filelist";	 
	private static ResFilelistBAC self;	 	
	  		
	public static ResFilelistBAC getInstance()
	{						
		if(self==null)
		{
			self = new ResFilelistBAC();
		}
		return self;
	}
	private ResFilelistBAC()
	{			
		
	}
	
	/**
	 * 获取指令平台和等级的资源文本
	 * @param platform
	 * @param reslv
	 * @return
	 */
	public byte[] getFileListStr(int platform)
	{	
		synchronized(this)
		{
			byte[] zipBytes = (byte[])Pool.getObjectFromPoolById(platform+"_res");
			if(zipBytes!=null)
			{				
				System.out.println("从缓存获取filelist");
				return zipBytes;
			}
			else			
			{
				DBHelper dbHelper = new DBHelper();
				try {
					dbHelper.openConnection();
					ResultSet rs = dbHelper.query(tbName, "filelist", "platform="+platform);
					if(rs.next())
					{			
						byte[] fileBytes = rs.getBytes("filelist");
						Pool.addObjectToPool(platform+"_res", 600,fileBytes);
						System.out.println("从数据库获取filelist");
						return fileBytes;
					}	
					else
					{
						return null;
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					return null;
				} finally {
					dbHelper.closeConnection();
				}
			}
		}					
	}
	public static String getPlatformFolderByPlatformNum(int platform)
	{
		if(platform==1)
		{
			return "android";			
		}
		else
		if(platform==2)
		{
			return "ios";
		}
		else
		if(platform==3)
		{
			return "pc";
		}
		else
		{
			return "android";		
		}			
	}
}
