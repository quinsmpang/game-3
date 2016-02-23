package com.moonic.bac;

import java.io.File;

import com.ehc.common.ReturnValue;
import com.moonic.util.MyTools;

import server.common.Tools;
import server.config.ServerConfig;


/**
 * 文件管理
 */
public class FileMgrBAC {
	
	/**
	 * 文件检查
	 */
	public ReturnValue checkFile(boolean del){
		try {
			String rootpath = ServerConfig.getAppRootPath();
			rootpath = rootpath.substring(0, rootpath.length()-1);
			String str = check(rootpath, rootpath+"/"+"filelist.txt", del);
			return new ReturnValue(true, str);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 根据指定路径的路径列表文件检查文件
	 * @param rootpath 项目根目录
	 * @param listpath 文件地址
	 * @param del 是否删除多余文件
	 */
	public String check(String rootpath, String listpath, boolean del){
		StringBuffer sb = new StringBuffer();
		File listFile = new File(listpath);
		if(listFile.exists()){
			sb.append("\r\nfilelist.txt 创建时间："+MyTools.getTimeStr(listFile.lastModified())+"\r\n");
			rootpath = rootpath.replace('\\', '/');
			sb.append("检查结果：\r\n");
			String filetext = MyTools.readTxtFile(listpath);
			String[] list = Tools.splitStr(Tools.getSubString(filetext, "path:", "pathEnd"), "\r\n");
			for(int i = 0; list != null && i < list.length; i++){
				String[] data = Tools.splitStr(list[i], "|");
				//System.out.println(list[i]);
				File file = new File(rootpath+"/"+data[0]);
				if(!file.exists()){
					sb.append("缺少文件："+data[0]+"\r\n");
				} else 
				if(file.length()!=Integer.valueOf(data[1])){
					sb.append("文件大小不一致："+data[0]+"\r\n");
				}
				list[i] = data[0];
			}
			String[] dispath = Tools.splitStr(Tools.getSubString(filetext, "dir:", "dirEnd"), "\r\n");
			String[] list2 = Tools.splitStr(getPath(rootpath, dispath), "\r\n");
			for(int i = 0; i < list2.length; i++){
				String[] data = Tools.splitStr(list2[i], "|");
				if(!MyTools.checkInStrArr(list, data[0])){
					sb.append("多余文件："+data[0]);
					if(del){
						File file = new File(rootpath+"/"+data[0]);
						if(file.exists()){
							file.delete();
							sb.append("(已删除)");
						}
					}
					sb.append("\r\n");
				}
			}	
		} else {
			sb.append("filelist.txt 未找到！");
		}
		return sb.toString();
	}
	
	/**
	 * 获取指定目录数组的所有路径
	 * @param rootpath 项目根目录末尾无"/"
	 * @param dirpath 包含文件夹
	 */
	private String getPath(String rootpath, String[] dirpath){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; dirpath != null && i < dirpath.length; i++){
			File root = new File(rootpath+"/"+dirpath[i]);
			if(!root.exists()){
				System.out.println("指定文件路径不存在 "+rootpath+"/"+dirpath[i]);
				continue;
			}
			if(root.isDirectory()){
				ergodic(root.listFiles(), sb);		
			} else {
				ergodic(new File[]{root}, sb);
			}
		}
		//System.out.println(sb.toString());
		//System.out.println(rootpath+"/");
		return sb.toString().replace(rootpath+"/", "");
	}
	
	/**
	 * 遍历
	 */
	private void ergodic(File[] files, StringBuffer sb){
		for(int i = 0; i < files.length; i++){
			if(files[i].isDirectory()){
				ergodic(files[i].listFiles(), sb);
			} else {
				sb.append(files[i].getPath().replace('\\', '/')+"|"+files[i].length()+"\r\n");
			}
		}
	}
	
	//--------------静态区--------------
	
	private static FileMgrBAC instance = new FileMgrBAC();
	
	/**
	 * 获取实例
	 */
	public static FileMgrBAC getInstance(){
		return instance;
	}
}
