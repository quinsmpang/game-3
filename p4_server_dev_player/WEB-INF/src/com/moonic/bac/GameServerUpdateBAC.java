package com.moonic.bac;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;

import conf.Conf;

public class GameServerUpdateBAC extends BaseActCtrl {

	public static String tbName = "tab_gameserver_update";
	private static GameServerUpdateBAC self;

	public static GameServerUpdateBAC getInstance() {
		if (self == null) {
			self = new GameServerUpdateBAC();
		}
		return self;
	}

	public GameServerUpdateBAC() {
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}

	public ReturnValue updateSystemBySTS(String filename, byte[] zipBytes) {
		if (filename.toLowerCase().endsWith(".zip")) {
			try {
				update(zipBytes);

				FormXML formXML = new FormXML();
				formXML.add("serverid", Conf.sid);
				formXML.add("updfile", filename);
				formXML.addDateTime("savetime", Tools.getCurrentDateTimeStr());
				formXML.add("filesize", zipBytes.length);

				formXML.setAction(FormXML.ACTION_INSERT);
				ReturnValue rv = save(formXML);
				if (rv.success) {
					return new ReturnValue(true, "更新成功");
				} else {
					return new ReturnValue(false, "更新失败");
				}
			} catch (Exception ex) {
				return new ReturnValue(false, "更新失败" + ex.toString());
			}
		} else {
			return new ReturnValue(false, "请上传zip文件");
		}
	}

	/**
	 * 释放zip文件内容覆盖更新系统
	 * @param zipBytes zip文件内容
	 */
	private void update(byte[] zipBytes) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
		ZipInputStream zis = new ZipInputStream(bais);
		ZipEntry zipEntry = null;
		byte[] buffer = new byte[4096];
		while ((zipEntry = zis.getNextEntry()) != null) {
			if (!zipEntry.isDirectory()) {
				String entryName = zipEntry.getName();
				File writeFile = new File(ServerConfig.getAppRootPath()
						+ entryName);
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
}
