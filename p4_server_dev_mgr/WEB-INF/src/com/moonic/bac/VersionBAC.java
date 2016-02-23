package com.moonic.bac;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.mgr.DBPoolMgr;
import com.moonic.util.DBHelper;

/**
 * 上传更新版本
 * @author alexhy
 */
public class VersionBAC {
	public static final String tab_version_apk = "tab_version_apk";
	public static final String tab_version_patch = "tab_version_patch";
	public static final String tab_version_filelist = "tab_version_filelist";
	
	/**
	 * 关联程序包
	 */
	public ReturnValue uploadApk(DataInputStream dis){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			String fileName = dis.readUTF();
			long filesize = dis.readLong();
			String channel = dis.readUTF();
			String subfolder=dis.readUTF();				
			if(subfolder.endsWith("/"))
			{
				subfolder = subfolder.substring(0, subfolder.length()-1);
			}
			String platform= dis.readUTF();				
			String crc = dis.readUTF();	
			String version = dis.readUTF();	
			
			if(fileName.toLowerCase().endsWith("apk") || fileName.toLowerCase().endsWith("ipa")) 
			{
				//String version = fileName.substring(fileName.lastIndexOf('_')+1, fileName.lastIndexOf('.'));
				boolean exist = dbHelper.queryExist(tab_version_apk, "version='"+version+"' and platform="+platform+" and channel='"+channel+"'");
				if(!exist){
					SqlString sqlStr = new SqlString();
					sqlStr.add("version", version);
					sqlStr.add("updfile", fileName);
					sqlStr.addDateTime("savetime", Tools.getCurrentDateTimeStr());
					sqlStr.add("mustupdate", 1);
					sqlStr.add("filesize", filesize);
					sqlStr.add("channel", channel);
					sqlStr.add("subfolder", subfolder);
					sqlStr.add("platform", platform);
					sqlStr.add("crc", crc);					
					dbHelper.insert(tab_version_apk, sqlStr);
					DBPoolMgr.getInstance().addClearTablePoolTask(tab_version_apk, null);
					return new ReturnValue(true, "上传成功");
				}
				else
				{
					return new ReturnValue(false, "版本已存在");
				}
			}				
			else
			{
				return new ReturnValue(false, "必须是apk或ipa后缀的文件");
			}
		} catch (EOFException eofe) {
			return new ReturnValue(false, "上传数据有误");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {			
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 关联升级补丁
	 */
	public ReturnValue uploadPatch(DataInputStream dis)
	{
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			String fileName = dis.readUTF(); //XianMo_P_0.999_1.000.patch
			long filesize = dis.readLong();
			String channel = dis.readUTF();
			
			String subfolder=dis.readUTF();			
			if(subfolder.endsWith("/"))
			{
				subfolder = subfolder.substring(0, subfolder.length()-1);
			}
			String platform=dis.readUTF();			
			String crc = dis.readUTF();	
			String fromVersion = dis.readUTF();
			String toVersion = dis.readUTF();
			
			if(fileName.toLowerCase().endsWith("patch")) 
			{
				/*int index1 = fileName.lastIndexOf("_");
				int index2 = fileName.lastIndexOf(".patch");
				String toVersion = fileName.substring(index1+1, index2);
				
				int index3 = fileName.lastIndexOf("_",index1-1);
				String fromVersion = fileName.substring(index3+1, index1);*/
				
				boolean exist =false;
				/*if(packageName!=null && !packageName.equals(""))
				{
					exist = dbHelper.queryExist(tab_version_patch, "fromversion='"+fromVersion+"' and toversion='"+toVersion+"' and packageName='"+packageName+"' and channel='"+channel+"'");
				}
				else*/
				{
					exist = dbHelper.queryExist(tab_version_patch, "fromversion='"+fromVersion+"' and toversion='"+toVersion+"' and platform="+platform+" and packageName is null and channel='"+channel+"'");
				}
				if(!exist){
					SqlString sqlStr = new SqlString();
					sqlStr.add("fromversion", fromVersion);
					sqlStr.add("toversion", toVersion);
					sqlStr.add("patchfile", fileName);
					sqlStr.addDateTime("savetime", Tools.getCurrentDateTimeStr());					
					sqlStr.add("filesize", filesize);
					sqlStr.add("channel", channel);
					sqlStr.add("subfolder", subfolder);
					sqlStr.add("platform", platform);
					sqlStr.add("crc", crc);					
					dbHelper.insert(tab_version_patch, sqlStr);
					DBPoolMgr.getInstance().addClearTablePoolTask(tab_version_patch, null);
					return new ReturnValue(true, "上传成功");
				}
				else
				{
					return new ReturnValue(false, "版本已存在");
				}
			}
			else
			{
				return new ReturnValue(false, "必须是patch后缀的文件");
			}
		} catch (EOFException eofe) {
			return new ReturnValue(false, "上传数据有误");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {			
			dbHelper.closeConnection();
		}
	}
	public ReturnValue uploadRes(DataInputStream dis)
	{
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			String fileName = dis.readUTF();			
			
			String platform=dis.readUTF();			
			String crc = dis.readUTF();	
			
			if(fileName.toLowerCase().endsWith("zz")) 
			{				
				boolean exist =false;
				
				SqlString sqlStr = new SqlString();				
				
				
				sqlStr.add("platform", platform);
				sqlStr.add("crc", crc);
				sqlStr.add("enable", 1);
				sqlStr.addDateTime("savetime", Tools.getCurrentDateTimeStr());
				
				exist = dbHelper.queryExist(tab_version_filelist, "platform="+platform);
				
				if(!exist){
					
					dbHelper.insert(tab_version_filelist, sqlStr);
				}
				else
				{
					dbHelper.update(tab_version_filelist, sqlStr, "platform="+platform);
				}
				DBPoolMgr.getInstance().addClearTablePoolTask(tab_version_filelist, null);
				return new ReturnValue(true, "上传成功");
			}
			else
			{
				return new ReturnValue(false, "必须是zz后缀的文件");
			}
		} catch (EOFException eofe) {
			return new ReturnValue(false, "上传数据有误");
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {			
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取APK版本列表
	 */
	public ReturnValue debugGetApkList(){
		try {
			File dir = new File(ServerConfig.getAppRootPath()+"/download/000");
			File[] files = dir.listFiles();
			StringBuffer sb = new StringBuffer();
			for(int i = 0; files != null && i < files.length; i++){
				sb.append("<a href='../download/000/"+files[i].getName()+"'>");
				sb.append(files[i].getName()+"<br>");
				sb.append("</>");
			}
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	//--------------静态区--------------
	
	private static VersionBAC instance = new VersionBAC();
	
	/**
	 * 获取实例
	 */
	public static VersionBAC getInstance(){
		return instance;
	}
}
