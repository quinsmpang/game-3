package com.moonic.bac;

import org.json.JSONObject;

import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.moonic.servlet.STSServlet;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.NetResult;
import com.moonic.util.STSNetSender;

import conf.LogTbName;

/**
 * TXT文件管理
 * @author John
 */
public class TxtFileBAC extends BaseActCtrl {
	public static final String tab_txt_file_type = "tab_txt_file_type";
	
	/**
	 * 构造
	 */
	public TxtFileBAC(){
		super.setTbName(LogTbName.TAB_TXT_FILE());
		setDataBase(ServerConfig.getDataBase_Log());
	}
	

	/**
	 * 获取文件类型数据
	 */
	public ReturnValue getFileTypeData(){
		try {
			DBPsRs listRs = DBPool.getInst().pQueryS(tab_txt_file_type);
			JSONObject jsonobj = new JSONObject();
			while(listRs.next()){
				jsonobj.put(listRs.getString("type"), listRs.getString("name"));
			}
			return new ReturnValue(true, jsonobj.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}

	/**
	 * 获取文件内容
	 */
	public ReturnValue reqGetFileContent(int serverid, int fileid) {
		try {
			STSNetSender sender = new STSNetSender(STSServlet.G_TXT_FILE_GET_CONTENT);
			sender.dos.writeInt(fileid);
			NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverid);
			return nr.rv;
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
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
