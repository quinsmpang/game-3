package com.moonic.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ZIP工具
 * 
 * @author John
 */
public class ZipUtil {
	
	/**
	 * 解压ZIP文件
	 * @param filePath 目标文件路径
	 * @param folderPath 解压到的文件夹路径
	 */
	public static void upZipFile(String filePath, String folderPath) {
		try {
			ZipFile zipFile = new ZipFile(new File(filePath));
			Enumeration<?> zipList = zipFile.entries();// 返回所有条目
			ZipEntry zipEntry = null;
			byte[] buffer = new byte[1024 * 4];
			while (zipList.hasMoreElements()) {
				zipEntry = (ZipEntry) zipList.nextElement();
				if (!zipEntry.isDirectory()) {// 不是目录
					String dirPath = folderPath + "/" + zipEntry.getName();
					File file = new File(dirPath);
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					if(file.exists()){
						file.delete();
					}
					OutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
					InputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
					int length = 0;
					while ((length = bis.read(buffer)) != -1) {
						bos.write(buffer, 0, length);
					}
					bis.close();
					bos.close();
				}
			}
			zipFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		upZipFile("C:\\Users\\huangminglong\\Desktop\\1.zip", "C:\\Users\\huangminglong\\Desktop\\1");
	}
}
