package com.moonic.bac;

import java.sql.ResultSet;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.moonic.servlet.STSServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.NetResult;
import com.moonic.util.STSNetSender;

import conf.LogTbName;

/**
 * 玩家财产管理
 * @author John
 */
public class PlaAssectBAC extends BaseActCtrl {
	public static final String[] ASSECT_TYPE = {"普通装备", "时装", "宝玉", "", "宠物"};
	
	public PlaAssectBAC() {			
		super.setTbName(LogTbName.TAB_PLA_ASSECT_DISCARD_LOG());
		setDataBase(ServerConfig.getDataBase_Log());
	}
	
	/**
	 * 恢复
	 */
	public ReturnValue recover(PageContext pageContext) {
		DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		try {
			ServletRequest request = pageContext.getRequest();
			String[] boxids = request.getParameterValues("recoverbox");
			if(boxids == null){
				BACException.throwInstance("未选中任何项");
			}
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < boxids.length; i++){
				ResultSet rs = dbHelper.query(LogTbName.TAB_PLA_ASSECT_DISCARD_LOG(), "serverid", "id="+boxids[i]+" and recovertime is null");
				sb.append(boxids[i]+":");
				if(rs.next()){
					STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_ASSECT_RECOVER);
					sender.dos.writeInt(Tools.str2int(boxids[i]));
					NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, rs.getInt("serverid"));
					sb.append(nr.rv.info+"\\r\\n");
				} else {
					sb.append("数据未找到\\r\\n");
				}
			}
			return new ReturnValue(true, sb.toString());
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	//--------------静态区--------------
	
	private static PlaAssectBAC instance = new PlaAssectBAC();
	
	/**
	 * 获取实例
	 */
	public static PlaAssectBAC getInstance(){
		return instance;
	}
}
