package com.moonic.bac;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;

import conf.Conf;

import server.common.Tools;
import server.config.ServerConfig;

/**
 * 系统更新
 * @author 
 */
public class SystemUpdateBAC {
	public static String tb_system_update = "tb_system_update";
	
	/**
	 * 更新系统
	 */
	public ReturnValue updateSystem(String filename, byte[] zipBytes) {
		DBHelper dbHelper = new DBHelper();
		try {
			if(!filename.toLowerCase().endsWith(".zip")){
				BACException.throwInstance("请上传zip文件");
			}
			update(zipBytes);
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.add("server", Conf.stsKey);
			sqlStr.add("updfile", filename);
			sqlStr.addDateTime("savetime", Tools.getCurrentDateTimeStr());
			sqlStr.add("filesize", zipBytes.length);
			dbHelper.insert(tb_system_update, sqlStr);
			return new ReturnValue(true, "更新成功");
		} catch (Exception ex) {
			return new ReturnValue(false, "更新失败" + ex.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 释放zip文件内容覆盖更新系统
	 */
	private void update(byte[] zipBytes) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
		ZipInputStream zis = new ZipInputStream(bais);
		ZipEntry zipEntry = null;
		byte[] buffer = new byte[4096];
		while ((zipEntry = zis.getNextEntry()) != null) {
			if (!zipEntry.isDirectory()) {
				String entryName = zipEntry.getName();
				File writeFile = new File(ServerConfig.getAppRootPath() + entryName);
				if (!writeFile.getParentFile().exists()) {
					writeFile.getParentFile().mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(writeFile);
				int len = 0;
				while ((len = zis.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
		}
		zis.close();
	}
	
	//----------------静态区------------------
	
	private static SystemUpdateBAC instance = new SystemUpdateBAC();

	public static SystemUpdateBAC getInstance() {
		return instance;
	}
}
