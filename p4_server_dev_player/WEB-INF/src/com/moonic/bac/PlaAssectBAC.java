package com.moonic.bac;

import java.sql.ResultSet;
import java.sql.Types;

import org.json.JSONArray;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.mirror.MirrorMgr;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPsRs;
import com.moonic.util.DBUtil;
import com.moonic.util.MyTools;

import conf.Conf;
import conf.LogTbName;

/**
 * 玩家财产管理
 * @author John
 */
public class PlaAssectBAC {
	
	/**
	 * 恢复
	 */
	public ReturnValue recover(int ass_id) {
		DBHelper main_dbHelper = new DBHelper();
		DBHelper log_dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		try {
			main_dbHelper.openConnection();
			log_dbHelper.openConnection();
			ResultSet rs = log_dbHelper.query(LogTbName.TAB_PLA_ASSECT_DISCARD_LOG(), "info", "id="+ass_id+" and recovertime is null");
			rs.next();
			JSONArray jsonarr = new JSONArray(rs.getString("info"));
			for(int k = 0; k < jsonarr.length(); k++){
				JSONArray data_arr = jsonarr.optJSONArray(k);
				JSONArray data_col_arr = data_arr.optJSONArray(1);
				JSONArray data_typ_arr = data_arr.optJSONArray(2);
				JSONArray data_val_arr = data_arr.optJSONArray(3);
				SqlString sqlStr = new SqlString();
				int id = 0;
				int playerid = 0;
				for(int i = 0; i < data_col_arr.length(); i++){
					String col = data_col_arr.optString(i);
					if(!col.toLowerCase().equals("id")){
						int type = data_typ_arr.optInt(i);
						String val = data_val_arr.optString(i);
						if(col.toLowerCase().equals("playerid")){
							playerid = Tools.str2int(val);
						}
						if(!val.equals("null")){
							if(type == Types.DATE || type == Types.TIMESTAMP){
								if(val.length() <= 10){//2014-12-31
									sqlStr.addDate(col, val.replace('/', '-'));
								} else 
								if(val.length() <= 19){//2014-12-31 12:00:00
									sqlStr.addDateTime(col, val.replace('/', '-'));
								} else 
								{
									sqlStr.addDateTimeMS(col, val.replace('/', '-'));
								}
							} else {
								sqlStr.add(col, val);
							}		
						}
					} else {
						id = data_val_arr.optInt(i);
					}
				}
				((PlaStorBAC)MirrorMgr.classname_mirror.get(data_arr.optString(0))).insert(main_dbHelper, playerid, sqlStr, id);
			}
			SqlString logSqlStr = new SqlString();
			logSqlStr.addDateTime("recovertime", MyTools.getTimeStr());
			log_dbHelper.update(LogTbName.TAB_PLA_ASSECT_DISCARD_LOG(), logSqlStr, "id="+ass_id);
			return new ReturnValue(true, "处理成功");
		} catch (Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			main_dbHelper.closeConnection();
			log_dbHelper.closeConnection();
		}
	}
	
	/**
	 * 存储
	 */
	public void saveLog(PlaStorBAC stor, int playerid, String where, int type, String[] replace_tab, String[] replace_col, String[] replace_val) throws Exception {
		JSONArray jsonarr = new JSONArray();//总数据集
		DBPsRs rs = converToJsonarr(stor, playerid, where, jsonarr, replace_tab, replace_col, replace_val);
		if(type == 1 || type == 2 || type == 3){//物品
			while(rs.next()){
				converToJsonarr(ItemBAC.getInstance(), playerid, "playerid="+playerid+" and id="+rs.getInt("itemid"), jsonarr, replace_tab, replace_col, replace_val);
			}
		}
		SqlString sqlStr = new SqlString();
		sqlStr.add("playerid", playerid);
		sqlStr.add("serverid", Conf.sid);
		sqlStr.add("type", type);
		sqlStr.add("info", jsonarr.toString());
		sqlStr.addDateTime("createtime", MyTools.getTimeStr());
		DBHelper.logInsert(LogTbName.TAB_PLA_ASSECT_DISCARD_LOG(), sqlStr);
	}
	
	/**
	 * 转换
	 */
	private DBPsRs converToJsonarr(PlaStorBAC stor, int playerid, String where, JSONArray jsonarr, String[] replace_tab, String[] replace_col, String[] replace_val) throws Exception {
		DBPsRs rs = stor.query(playerid, where);
		String table = stor.tab;
		JSONArray colarr = DBUtil.converColobjToArr(DBUtil.colmap.optJSONObject(table));
		JSONArray coltypearr = DBUtil.coltypemap.optJSONArray(table);
		while(rs.next()){
			JSONArray data_col_arr = new JSONArray();
			JSONArray data_typ_arr = new JSONArray();
			JSONArray data_val_arr = new JSONArray();
			int colCount = colarr.length();
			for (int i = 0; i < colCount; i++) {
				String colName = colarr.optString(i);
				String colVal = rs.getString(colName);
				if(replace_tab != null){
					for(int k = 0; k < replace_tab.length; k++){
						if(replace_tab[k].equalsIgnoreCase(table) && replace_col[k].equalsIgnoreCase(colName)){
							colVal = replace_val[k];
						}
					}
				}
				if(colVal != null){
					data_col_arr.add(colName);
					data_typ_arr.add(coltypearr.optInt(i));
					data_val_arr.add(colVal);
				}
			}
			JSONArray data_arr = new JSONArray();
			data_arr.add(stor.getClass().getName());
			data_arr.add(data_col_arr);
			data_arr.add(data_typ_arr);
			data_arr.add(data_val_arr);
			jsonarr.add(data_arr);
		}
		rs.beforeFirst();
		return rs;
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
