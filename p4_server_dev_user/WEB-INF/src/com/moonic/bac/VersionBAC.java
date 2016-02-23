package com.moonic.bac;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.moonic.util.BACException;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;
import com.moonic.util.DBPsRs;

import conf.Conf;

/**
 * 上传更新版本
 * @author alexhy
 */
public class VersionBAC {
	public static final String tab_version_apk = "tab_version_apk";	
	public static final String tab_version_res = "tab_version_res";
//	public static final String tab_version_patch = "tab_version_patch";
	public static final String tab_version_filelist = "tab_version_filelist";
	
	
	static
	{
		DBPool.getInst().addTxtClearListener(new TxtPoolClearListener());
	}
	/**
	 * 检测程序版本
	 */
	public ReturnValue checkApkVer(int platform,String clientVer, String channel, String packageName, boolean isBigApk, boolean needPatch, String imei, String mac) {
		try {			
			//检查是否测试用的版本
			String[] testVersionArr=null;
			String testVersions = ConfigBAC.getString("testVersion");
			if(testVersions!=null && !testVersions.equals("0"))
			{
				testVersionArr = Tools.splitStr(testVersions,",");
			}
			if(Conf.testRedir!=null && !Conf.testRedir.equals("") && testVersionArr!=null && Tools.strArrContain(testVersionArr, clientVer))
			{
				JSONArray arr = new JSONArray();
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("redir", Conf.testRedir); //返回客户端需跳转的新地址
				arr.add(jsonObj);
				
				return new ReturnValue(true, arr.toString());
			}
			else
			{
				DBPaRs channeListRs = ChannelBAC.getInstance().getChannelListRs(channel);
				if(!channeListRs.exist()){
					BACException.throwInstance("渠道码不存在 channel="+channel);
				}
				String platformFolder = ResFilelistBAC.getPlatformFolderByPlatformNum(platform);				
				
				String fileListCRC="";
				//获取对应平台的filelist的crc
				DBPsRs filelistCRCRs = DBPool.getInst().pQueryS("tab_version_filelist", "platform="+platform+" and enable=1");
				
				if(filelistCRCRs!=null && filelistCRCRs.next())
				{
					fileListCRC = filelistCRCRs.getString("crc");
				}			
				
				String apkchannel = channeListRs.getString("apkchannel");
//				int updateType = channeListRs.getInt("updatetype"); //1:可打补丁 2:只能下载APK 3:打开更新网站
				//String reschannel = channeListRs.getString("reschannel");
				DBPsRs apkRs = DBPool.getInst().pQueryS(tab_version_apk,"platform="+platform+" and version>'"+clientVer+"' and channel='"+apkchannel+"'", "version desc", 1);
				apkRs.next();
				
//				DBPsRs patchRs = null;		
				
				//if(!isBigApk && needPatch && platform==1)				
				
				
				//获取资源子目录
				String subfolder="";
				DBPsRs subfolderRs = DBPool.getInst().pQueryS(tab_version_filelist, "platform="+platform);
				if(subfolderRs!=null && subfolderRs.have())
				{
					subfolderRs.next();
					subfolder = Tools.strNull(subfolderRs.getString("subfolder"));
				}
				String web = getChannelWeb(apkchannel);	
				
				//TODO 没有补丁包
				/*if(updateType==1 || updateType==3) //可打补丁
				{ 
					if(platform==1) //android客户端才允许下补丁
					{
						if(apkRs.have()) 
						{					
							patchRs = DBPool.getInst().pQueryS(tab_version_patch, "fromversion='"+clientVer+"' and toversion='"+apkRs.getString("version")+"' and packagename='"+packageName+"' and channel='"+apkchannel+"'");
							if(!patchRs.have()) {
								patchRs = DBPool.getInst().pQueryS(tab_version_patch, "fromversion='"+clientVer+"' and toversion='"+apkRs.getString("version")+"' and packagename is null and channel='"+apkchannel+"'");
							}
						}
					}
					
					if(patchRs!=null && patchRs.have())  //有可用补丁
					{
						String preApkCRC="";
						//查旧版本apk的crc值
						DBPsRs preApkRs = DBPool.getInst().pQueryS(tab_version_apk,"platform="+platform+" and version='"+clientVer+"' and channel='"+apkchannel+"'", "version desc", 1);
						if(preApkRs.next())
						{
							preApkCRC = preApkRs.getString("crc");
						}
						
						patchRs.next();
						JSONArray arr = new JSONArray();
						JSONObject jsonObj = new JSONObject();
						String patchfile = patchRs.getString("patchfile");
						int filesize = patchRs.getInt("filesize");	
						String patchfilecrc = patchRs.getString("crc");
						
						String downPath = ServerConfig.dl_apk_url + platformFolder+"/"+ apkchannel + "/" + patchfile;
						jsonObj.put("updatetype",2);//更新类型 1：文件 2：补丁
						jsonObj.put("ver",apkRs.getString("version"));//版本号
						jsonObj.put("patchfilename",patchfile);//补丁文件名
						jsonObj.put("patchfilesize",filesize);//补丁文件大小
						jsonObj.put("patchfilecrc",patchfilecrc);//补丁文件crc
						jsonObj.put("patchdownpath",downPath);//下载路径		
						jsonObj.put("apkfilename",apkRs.getString("updfile"));//文件名
						jsonObj.put("apkfilesize",apkRs.getInt("filesize"));
						jsonObj.put("apkfilecrc",apkRs.getString("crc")); //apk文件crc
						jsonObj.put("mustupdate",apkRs.getInt("mustupdate"));//必要更新	
						jsonObj.put("oldapkcrc",preApkCRC); //旧版本APK的CRC,用于打补丁前的校验
						
						String apkFileName = apkRs.getString("updfile");	
						String apkDownPath = ServerConfig.dl_apk_url + platformFolder+"/"+ apkchannel + "/" + apkFileName;
						jsonObj.put("apkdownpath",apkDownPath); //新apk下载路径	
						jsonObj.put("web", web);
						
						arr.add(jsonObj);
						arr.add(ServerConfig.dl_res_url + platformFolder + (subfolder.equals("")?"/":"/"+subfolder+"/")); //资源下载url	
						arr.add(fileListCRC); //资源文件列表CRC					
						return new ReturnValue(true, arr.toString());
					} 					
				}		*/			
				
				if(apkRs.have()) //下APK
				{
					JSONArray arr = new JSONArray();
					JSONObject jsonObj = new JSONObject();
					String fileName = apkRs.getString("updfile");				
							
					String downPath = ServerConfig.dl_apk_url + platformFolder+"/"+ apkchannel + "/" + fileName;
					jsonObj.put("updatetype",1);//更新类型 1：文件 2：补丁
					jsonObj.put("ver",apkRs.getString("version"));//版本号
					jsonObj.put("apkfilename",fileName);//文件名
					jsonObj.put("downpath",downPath);//下载路径						
					jsonObj.put("apkfilesize",apkRs.getInt("filesize"));
					jsonObj.put("apkfilecrc",apkRs.getString("crc"));
					jsonObj.put("mustupdate",apkRs.getInt("mustupdate"));//必要更新
					jsonObj.put("web", web);
					
					arr.add(jsonObj);
					
//					arr.add(ServerConfig.dl_res_url + platformFolder + (subfolder.equals("")?"/":"/"+subfolder+"/")); //资源下载url		
//					arr.add(fileListCRC); //资源文件列表crc
					return new ReturnValue(true, arr.toString());
				}
				else {	
					JSONArray arr = new JSONArray();
					JSONObject jsonObj = new JSONObject();
					
					arr.add(jsonObj);
					
//					arr.add(ServerConfig.dl_res_url + platformFolder + (subfolder.equals("")?"/":"/"+subfolder+"/")); //资源下载url	
//					arr.add(fileListCRC); //资源文件列表crc					
					return new ReturnValue(true, arr.toString());
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 检测资源版本
	 */
	public ReturnValue checkResVer(String clientVer, byte platform){
		try {
			DBPsRs resRs = DBPool.getInst().pQueryS(tab_version_res, "version>'"+clientVer+"' and platform="+platform, "version");
			JSONArray jsonarr = new JSONArray();
			while(resRs.next()){
				String fileName = resRs.getString("updfile");
				String subfolder = resRs.getString("subfolder");
				if(subfolder == null || subfolder.trim().length() == 0) {
					subfolder = "";
				} else {
					subfolder += "/";
				}
				String resPlatform = ResFilelistBAC.getPlatformFolderByPlatformNum(platform);
				String downPath = ServerConfig.dl_res_url + subfolder + resPlatform + "/" + fileName;
				JSONArray arr = new JSONArray();
				arr.add(resRs.getString("version"));//版本号
				arr.add(fileName);//文件名
				arr.add(downPath);//下载路径
				arr.add(resRs.getInt("filesize"));//文件大小
				arr.add(resRs.getInt("mustupdate"));//必要更新
				jsonarr.add(arr);
			}
			return new ReturnValue(true, jsonarr.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} 
	}
	
	static String[][] channelWebData;
	
	/**
	 * 获取渠道官网地址
	 * @param channel
	 * @return
	 */
	public String getChannelWeb(String channel)
    {
        if(channelWebData == null)
    	{
        	String txt=null;
        	try {
    			txt = DBPool.getInst().readTxtFromPool("channel_web");
    			channelWebData = Tools.getStrLineArrEx2(txt, "data:","dataEnd");
    		} catch (Exception e) {		
    			e.printStackTrace();    			
    		}
    	}
        
    	for(int i=0;channelWebData!=null && i<channelWebData.length;i++)
    	{
    		if(channelWebData[i][0].equals(channel))
    		{
    			return channelWebData[i][1];
    		}
    	}
            
        return "0";
    }
	
	/**
	 * 获取资源CRC列表文本
	 * @param phonePlatform 手机平台类型1安卓2ios
	 * @param channel 渠道  不同渠道资源如果不相同时使用
	 * @param serverId 游戏服务器id
	 * @return
	 */
	/*public ReturnValue getResCRCFileList(int phonePlatform,String channel)
	{
		try {
			//查serverId对应的server的reslv	
			long t1=System.currentTimeMillis();
			byte[] fileBytes = ResFilelistBAC.getInstance().getFileListStr(phonePlatform);
			long t2=System.currentTimeMillis();
			System.out.println("耗时"+(t2-t1)+"毫秒");
			if(fileBytes!=null)
			{
				return new ReturnValue(true,fileBytes);
			}
			else
			{
				return new ReturnValue(false,"");
			}
						
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false,"");
		} 
	}*/
		
	//--------------静态区--------------
	
	private static VersionBAC instance = new VersionBAC();
	
	/**
	 * 获取实例
	 */
	public static VersionBAC getInstance(){
		return instance;
	}
	static class TxtPoolClearListener implements DBPoolClearListener
	{
		public void callback(String key)
		{
			if(key!=null && key.toLowerCase().equals("channel_web"))
			{
				channelWebData=null;
			}
		}
	}
}
