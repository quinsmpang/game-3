package com.moonic.bac;

import java.sql.ResultSet;

import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.FileUtil;
import com.moonic.util.MyTools;

import conf.Conf;
import conf.LogTbName;

/**
 * TXT文件管理
 * @author John
 */
public class TxtFileBAC {
	public static final String tab_txt_file_type = "tab_txt_file_type";

	/**
	 * 获取文件内容
	 */
	public ReturnValue getFileContent(int fileid) throws Exception {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		try {
			dbHelper.openConnection();
			String content = getFileContent(dbHelper, fileid);
			return new ReturnValue(true, content);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 创建记录
	 */
	public void createFile(DBHelper dbHelper, int type, String content) throws Exception {
		long createtime = System.currentTimeMillis();
		String filename = String.valueOf(createtime);
		SqlString sqlStr = new SqlString();
		sqlStr.add("serverid", Conf.sid);
		sqlStr.add("type", type);
		sqlStr.add("filename", filename);
		sqlStr.addDateTime("createtime", MyTools.getTimeStr(createtime));
		DBHelper.logInsert(LogTbName.TAB_TXT_FILE(), sqlStr);
		String path = getFilePath(type)+filename+".txt";
		FileUtil fu = new FileUtil();
		fu.addToTxt(path, content);
	}
	
	/**
	 * 获取文件内容
	 */
	public String getFileContent(DBHelper dbHelper, int fileid) throws Exception {
		ResultSet rs = dbHelper.query(LogTbName.TAB_TXT_FILE(), "type,filename", "id="+fileid);
		if(!rs.next()){
			BACException.throwInstance("文件未找到");
		}
		String path = getFilePath(rs.getInt("type"))+rs.getString("filename")+".txt";
		dbHelper.closeRs(rs);
		String content = MyTools.readTxtFile(path);
		return content;
	}
	
	/**
	 * 获取路径
	 */
	public String getFilePath(int type){
		return ServerConfig.getAppRootPath()+"cache/txtfile/"+type+"/";
	}
	
	//--------------静态区--------------
	
	private static TxtFileBAC instance = new TxtFileBAC();
	
	/**
	 * 获取实例
	 */
	public static TxtFileBAC getInstance(){
		return instance;
	}
}
