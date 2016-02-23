package com.moonic.mgr;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.common.ToolFunc;
import com.jspsmart.upload.SmartUpload;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.FileUtil;
import com.moonic.util.MyLog;
import com.moonic.util.MyTools;
import com.moonic.util.StreamHelper;

import conf.Conf;

/**
 * 数据表管理
 * @author John
 */
public class TabMgr {
	
	/**
	 * 生成SqlFile
	 */
	public ReturnValue createSqlFile(PageContext pageContext){
		try {
			//补充数据表
			ReturnValue rv1 = TabMgr.getInstance().createTabFromDir(ServerConfig.getWebInfPath()+"res/tab_list", false, false, false, false, false);
			if(!rv1.success){
				return rv1;
			}
			//补充应用表
			ReturnValue rv2 = TabMgr.getInstance().createTabFromDir(ServerConfig.getWebInfPath()+"res/tab_data", false, true, false, false, false);
			if(!rv1.success){
				return rv2;
			}
			//补充日志表
			ReturnValue rv3 = TabMgr.getInstance().createTabFromDir(ServerConfig.getWebInfPath()+"res/tab_log", false, true, false, true, false);
			if(!rv1.success){
				return rv3;
			}
			//检查数据表字段
			ReturnValue rv4 = TabMgr.getInstance().checkDataTabColumn(false);
			if(!rv1.success){
				return rv4;
			}
			//检查日志表字段
			ReturnValue rv5 = TabMgr.getInstance().checkDataTabColumn(true);
			if(!rv1.success){
				return rv5;
			}
			StringBuffer sb = new StringBuffer();
			sb.append("\r\n");
			sb.append("-----------补充数据表-----------\r\n\r\n");
			sb.append(rv1.info+"\r\n\r\n");
			sb.append("-----------补充应用表-----------\r\n\r\n");
			sb.append(rv2.info+"\r\n\r\n");
			sb.append("-----------补充日志表-----------\r\n\r\n");
			sb.append(rv3.info+"\r\n\r\n");
			sb.append("-----------检查数据表字段-----------\r\n\r\n");
			sb.append(rv4.info+"\r\n\r\n");
			sb.append("-----------检查日志表字段-----------\r\n\r\n");
			sb.append(rv5.info+"\r\n\r\n");
			
			byte[] data = sb.toString().getBytes("UTF-8");
			
			HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
			
			response.reset();
			response.setContentType("application/txt");
			response.setContentLength(data.length);
			response.setHeader("Content-disposition", new String(("attachment;filename="+MyTools.formatTime("yyyyMMddhhmmss")+"_sql_"+Conf.stsKey+".txt").getBytes("GBK"),"ISO-8859-1"));
			
			OutputStream os = response.getOutputStream();
			os.write(data);
			os.close();
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 更新列表文件
	 */
	public ReturnValue updateListFile(PageContext context, SmartUpload smartUpload, String tag, boolean allowCreateTab, boolean createTab, String suffix){
		try {
			String updateip = context.getRequest().getRemoteAddr();
			char[] iparr = updateip.toCharArray();
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < iparr.length; i++){
				if(iparr[i] != '.'){
					sb.append(iparr[i]);
				} else {
					sb.append("-");
				}
			}
			String save_path = Conf.logRoot+"load/list"+suffix+"_"+Tools.getCurrentDateTimeStr("yyMMddHHmmss")+" "+sb.toString()+"/";
			String saveAs_path = ServerConfig.getWebInfPath()+"res/tab_list"+suffix+"/";
			java.io.File newdir = new java.io.File(save_path);
			newdir.mkdir();
			ReturnValue uploadResult = StreamHelper.getInstance().uploadFiles2(smartUpload, save_path, tag);
			if(!uploadResult.success){
				return uploadResult;
			}
			ReturnValue saveAsResult = StreamHelper.getInstance().uploadFiles2(smartUpload, saveAs_path, tag);
			if(!saveAsResult.success){
				return saveAsResult;
			}
			if(createTab){
				return TabMgr.getInstance().createTabFromDir(save_path, true, false, allowCreateTab, false, false);
			} else {
				return new ReturnValue(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 更新文本文件
	 */
	public ReturnValue updateTxtFile(PageContext context, SmartUpload smartUpload, String tag){
		try {
			String updateip = context.getRequest().getRemoteAddr();
			char[] iparr = updateip.toCharArray();
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < iparr.length; i++){
				if(iparr[i] != '.'){
					sb.append(iparr[i]);
				} else {
					sb.append("-");
				}
			}
			String save_path = Conf.logRoot+"load/txt_" + Tools.getCurrentDateTimeStr("yyMMddHHmmss") + " " + sb.toString() + "/";
			String saveAs_path = ServerConfig.getWebInfPath() + "res/tab_txt/";
			java.io.File newdir = new java.io.File(save_path);
			newdir.mkdir();
			ReturnValue uploadResult = StreamHelper.getInstance().uploadFiles2(smartUpload, save_path, tag);
			if(!uploadResult.success){
				return uploadResult;
			}
			ReturnValue saveAsResult = StreamHelper.getInstance().uploadFiles2(smartUpload, saveAs_path, tag);
			if(!saveAsResult.success){
				return saveAsResult;
			}
			return TabMgr.getInstance().saveTxtDirToTab(save_path, true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * 整理SEQ
	 */
	public ReturnValue tidySEQ(boolean allowdb, boolean recreate) {
		DBHelper dbHelper = new DBHelper();
		try {
			StringBuffer sb = new StringBuffer();
			dbHelper.openConnection();
			ResultSet listRs = dbHelper.executeQuery("select * from user_tables");
			listRs.last();
			String[] tabs = new String[listRs.getRow()];
			listRs.beforeFirst();
			while(listRs.next()){
				tabs[listRs.getRow()-1] = listRs.getString("table_name").toLowerCase();
			}
			String sql = "select sequence_name from user_sequences";
			ResultSet rs = dbHelper.executeQuery(sql);
			while(rs.next()){
				String seqname = rs.getString("sequence_name");
				String filePath1 = ServerConfig.getWebInfPath() + "res/tab_data/"+seqname.substring(1).toLowerCase()+".txt";
				String filePath2 = ServerConfig.getWebInfPath() + "res/tab_log/"+seqname.substring(1).toLowerCase()+".txt";
				File cehckfile1 = new File(filePath1);
				File cehckfile2 = new File(filePath2);
				if(cehckfile1.exists()){
				} else 
				if(cehckfile2.exists()){
				} else 
				{
					continue;
				}
				String table = seqname.substring(1).toLowerCase();
				tidySEQ(dbHelper, true, table, MyTools.checkInStrArr(tabs, table), allowdb, recreate, sb);
			}
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 检查所有应用表的字段
	 */
	public ReturnValue checkDataTabColumn(boolean log){
		long t1 = System.currentTimeMillis();
		DBHelper dbHelper = null;
		if(log){
			dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		} else 
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase());
		}
		try {
			dbHelper.openConnection();
			MyLog checklog = new MyLog(MyLog.NAME_DATE, "log_checklog", "CHECK_COLUMN", true, false, true, Tools.getCurrentDateTimeStr("yyyy-MM-dd-HH-mm-ss"));
			String dbuser = dbHelper.getDataBase().getUsername();
			ResultSet listRs = dbHelper.executeQuery("select table_name,comments from user_tab_comments");
			ResultSet colRs = dbHelper.executeQuery("select table_name,column_name,comments from user_col_comments");
			StringBuffer dropSb = new StringBuffer();
			StringBuffer upd1Sb = new StringBuffer();
			StringBuffer upd2Sb = new StringBuffer();
			StringBuffer upd3Sb = new StringBuffer();
			StringBuffer addSb = new StringBuffer();
			StringBuffer noteSb = new StringBuffer();
			StringBuffer errorSb = new StringBuffer();
			while(listRs.next()){
				String tabname = listRs.getString("table_name").toLowerCase();
				String thecolumn = null;
				try {
					checklog.d("检查"+tabname+"是否为数据表...");
					String path1 = ServerConfig.getWebInfPath() + "res/tab_data/"+tabname+".txt";
					String path2 = ServerConfig.getWebInfPath() + "res/tab_list/"+tabname+".txt";
					String path3 = ServerConfig.getWebInfPath() + "res/tab_log/"+tabname+".txt";
					File cehckfile1 = new File(path1);
					File cehckfile2 = new File(path2);
					File cehckfile3 = new File(path3);
					String filePath = null;
					if(!log && cehckfile1.exists()){
						filePath = path1;
						checklog.d("检测"+tabname+"为应用表，开始检查字段");
					} else 
					if(!log && cehckfile2.exists()){
						filePath = path2;
						checklog.d("检测"+tabname+"为数据表，开始检查字段");
					} else
					if(log && cehckfile3.exists()){
						filePath = path3;
						checklog.d("检测"+tabname+"为日志表，开始检查字段");
					} else 
					{
						checklog.d("检测"+tabname+"未找到，结束操作");
						checklog.d("-----------------------------------\r\n");
						continue;
					}
					ResultSet tabRs = dbHelper.executeQuery("select * from " + tabname);
					tabRs.next();
					ResultSetMetaData rsmd = tabRs.getMetaData();
					int colCount = rsmd.getColumnCount();
					byte[] fileData = ToolFunc.getBytesFromFile(filePath);
					String fileText = new String(fileData, "UTF-8");
					String[] fieldData = Tools.splitStr(Tools.getStrProperty(fileText, "field"), "\t");
					String[][] field = new String[fieldData.length][];
					for(int i = 0; i < field.length; i++){
						field[i] = Tools.splitStr(fieldData[i], ",");
					}
					for(int i = 1; i <= colCount; i++){
						String colName = rsmd.getColumnName(i);
						thecolumn = colName;
						if(colName.toLowerCase().equals("id")){
							continue;
						}
						int the = -1;
						for(int k = 0; field != null && k < field.length; k++){
							if(field[k][0].equals(colName.toLowerCase())){
								the = k;
								break;
							}
						}
						if(the == -1){
							dropSb.append("alter table "+dbuser+"."+tabname+" drop column "+colName+";\r\n");
							checklog.d("删除字段：" + colName.toLowerCase());
						} else {
							int type = -1;
							int colType = rsmd.getColumnType(i);
							int isnulltype = rsmd.isNullable(i);
							if(colType == Types.NUMERIC){
								type = 0;
							} else 
							if(colType == Types.VARCHAR){
								type = 1;
							} else 
							if(colType == Types.DATE || colType == Types.TIMESTAMP){
								if(rsmd.getColumnDisplaySize(i) == 11){
									type = 5;		
								} else {
									type = 2;
								}
							} else 
							if(colType == Types.BLOB){
								type = 3;
							} else 
							if(colType == Types.NVARCHAR){
								type = 4;
							} else {
								type = -1;
								System.out.println("有无法识别的数据类型：" + colType + " from " + dbuser + "." + tabname);
							}
							StringBuffer updSb = null;
							if(type != Tools.str2int(field[the][1])){
								updSb = upd1Sb;
							} else 
							if(!(colType == Types.DATE || colType == Types.TIMESTAMP || colType == Types.BLOB) 
									&& rsmd.getPrecision(i)!=0 
									&& (rsmd.getPrecision(i)<Integer.valueOf(field[the][2]) || Integer.valueOf(field[the][2])==0)){//只记录需要加长的
								updSb = upd2Sb;
							} else 
							if(rsmd.isNullable(i)==ResultSetMetaData.columnNoNulls && field[the].length>=4 && Integer.valueOf(field[the][3])==1){
								updSb = upd3Sb;
							}
							if(updSb != null){
								updSb.append("alter table "+dbuser+"."+tabname+" modify "+field[the][0]+" " + getDataTypeStr(field[the][1]));
								if(!field[the][2].equals("0")){
									updSb.append("("+field[the][2]+")");
								}
								/*
								if(!(field[i].length>=4 && field[i][3].equals("1"))){
									addSb.append(" not null");
								}
								*/
								if(isnulltype != ResultSetMetaData.columnNullable && field[the].length>=4 && field[the][3].equals("1")){
									updSb.append(" null");
								}
								updSb.append(";\r\n"); 
							}
							field = Tools.removeOneFromStrArr2(field, the);
						}
					}
					for(int i = 0; field != null && i < field.length; i++){
						addSb.append("alter table "+dbuser+"."+tabname+" add "+field[i][0]+" " + getDataTypeStr(field[i][1]));
						if(!field[i][2].equals("0")){
							addSb.append("("+field[i][2]+")");
						}
						/*
						if(!(field[i].length>=4 && field[i][3].equals("1"))){
							addSb.append(" not null");
						}
						*/
						addSb.append(";\r\n");
						addSb.append("--update "+dbuser+"."+tabname+" set "+field[i][0]+"=;\r\n\r\n");
						checklog.d("添加字段：" + field[i][0].toLowerCase());
					}
					checklog.d("检查字段完成");
					checklog.d("-----------------------------------\r\n");
					thecolumn = null;
					String tabnamenote = Tools.getStrProperty(fileText, "namenote");
					String[] fieldnote = Tools.splitStr(Tools.getStrProperty(fileText, "fieldnote"), "\t");
					field = new String[fieldData.length][];
					for(int i = 0; i < field.length; i++){
						field[i] = Tools.splitStr(fieldData[i], ",");
					}
					if(!tabnamenote.equals(listRs.getString("comments"))){
						noteSb.append("comment on table " + dbuser + "." + tabname + " is '" + tabnamenote + "';\r\n");
					}
					HashMap<String, String> ncmap = new HashMap<String, String>();
					colRs.beforeFirst();
					while(colRs.next()){
						if(colRs.getString("table_name").equalsIgnoreCase(tabname)){
							ncmap.put(colRs.getString("column_name"), colRs.getString("comments"));		
						}
					}
					for(int i = 0; i < field.length; i++){
						String column_name = field[i][0];
						String comments = fieldnote[i];
						String oComments = ncmap.get(column_name.toUpperCase());
						if(!comments.equals(oComments)){
							noteSb.append("comment on column " + dbuser + "." + tabname + "." + field[i][0] + " is '" + fieldnote[i] + "';\r\n");
						}
					}
					dbHelper.closeRs(tabRs);
				} catch (Exception e) {
					errorSb.append("tabname="+tabname+" thecolumn="+thecolumn+" error="+e.toString()+"\r\n");
					e.printStackTrace();
				}
			}
			checklog.save();
			StringBuffer sb = new StringBuffer();
			sb.append("--删除字段：\r\n");
			sb.append(dropSb.toString());
			sb.append("\r\n");
			sb.append("--类型修改：\r\n");
			sb.append(upd1Sb.toString());
			sb.append("\r\n");
			sb.append("--长度修改：\r\n");
			sb.append(upd2Sb.toString());
			sb.append("\r\n");
			sb.append("--允许为空：\r\n");
			sb.append(upd3Sb.toString());
			sb.append("\r\n");
			sb.append("--添加字段：\r\n");
			sb.append(addSb.toString());
			sb.append("\r\n");
			sb.append("--更新注释：\r\n");
			sb.append(noteSb.toString());
			sb.append("\r\n");
			sb.append("--报错信息：\r\n");
			sb.append("\r\n");
			sb.append("--用时："+(System.currentTimeMillis()-t1));
			sb.append(errorSb.toString());
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 输出数据库中所有内容到指定文件夹中
	 */
	public ReturnValue outSQLTabToTxt(String outPath){
		return outSQLTabToTxt(null, outPath);
	}
	
	/**
	 * 输出数据库中所有内容到指定文件夹中
	 */
	public ReturnValue outSQLTabToTxt(String[] checkPath, String outPath){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			MyLog outlog = new MyLog(MyLog.NAME_CUSTOM, "log_outtab", "OUT_TAB", true, false, true, MyTools.formatTime("yyyy-MM-dd-HH-mm-ss"));
			ResultSet listRs = dbHelper.executeQuery("select * from user_tables");
			FileUtil fu = new FileUtil();
			fu.deleteDirectory(outPath);
			long t = System.currentTimeMillis();
			outlog.d("输出目录：" + outPath);
			outlog.d("过滤文件参考目录("+(checkPath!=null?checkPath.length:0)+")：");
			for(int i = 0; checkPath != null && i < checkPath.length; i++){
				outlog.d(i + " | " + checkPath[i]);
			}
			outlog.d("------------------------");
			StringBuffer returnSb = new StringBuffer();
			while(listRs.next()){
				StringBuffer sb = new StringBuffer();
				String tabname = listRs.getString("table_name");
				outlog.d("检查文件是否已存在...");
				boolean check = false;
				for(int i = 0; checkPath != null && i < checkPath.length; i++){
					File cehckfile = new File(checkPath[i]+tabname.toLowerCase()+".txt");
					if(cehckfile.exists()){
						check = true;
						break;
					}
				}
				if(check){
					outlog.d("文件已存在，取消生成");
					continue;
				} else {
					if(checkPath != null){
						returnSb.append("drop table "+dbHelper.getDataBase().getUsername()+"."+tabname.toLowerCase()+";\r\n");	
					}
					outlog.d("文件不存在，开始生成"+tabname.toLowerCase()+".txt");
				}
				ResultSet tabRs = dbHelper.executeQuery("select * from " + tabname);
				tabRs.next();
				outlog.d("生成字段信息  ...");
				long t1 = System.currentTimeMillis();
				ResultSetMetaData rsmd = tabRs.getMetaData();
				int colCount = rsmd.getColumnCount();
				sb.append("\r\n");
				sb.append("name="+tabname.toLowerCase()+"\r\n\r\n");
				sb.append("field=");
				for (int i = 1; i <= colCount; i++) {
					String colName = rsmd.getColumnName(i);
					if(colName.toLowerCase().equals("id")){
						continue;
					}
					int colType = rsmd.getColumnType(i);
					sb.append(colName.toLowerCase());
					sb.append(",");
					if(colType == Types.NUMERIC){
						sb.append(0);	
					} else 
					if(colType == Types.VARCHAR){
						sb.append(1);
					} else 
					if(colType == Types.DATE || colType == Types.TIMESTAMP){
						if(rsmd.getColumnDisplaySize(i) == 11){
							sb.append(5);
						} else {
							sb.append(2);		
						}
					} else 
					if(colType == Types.BLOB){
						sb.append(3);
					} else 
					if(colType == Types.NVARCHAR){
						sb.append(4);
					} else {
						sb.append(-1);
						System.out.println("有无法识别的数据类型：" + colType + " from " + tabname);
					}
					sb.append(",");
					if(colType == Types.DATE || colType == Types.TIMESTAMP || colType == Types.BLOB){
						sb.append(0);
					} else {
						sb.append(rsmd.getPrecision(i));
					}
					if(rsmd.isNullable(i) == ResultSetMetaData.columnNullable){
						sb.append(",1");
					}
					if(i != colCount){
						sb.append("\t");
					}
				}
				sb.append("\r\n\r\n");
				long t2 = System.currentTimeMillis();
				outlog.d("生成字段信息完成  用时："+(t2-t1));
				outlog.d("生成索引信息  ...");
				ResultSet indexRs = dbHelper.executeQuery("select a.index_name,a.column_name,b.index_type,a.table_name,b.uniqueness from " +
						"user_ind_columns a left join user_indexes b on a.index_name=b.index_name " +
						"where a.table_name=upper('"+tabname+"') order by a.index_name,a.column_position");
				String[] index_names = null;
				String[] column_names = null;
				String[] index_type = null;
				String[] index_unique = null;
				while(indexRs.next()){
					int index = -1;
					String in = indexRs.getString("index_name");
					String cn = indexRs.getString("column_name");
					for(int i = 0; index_names!=null && i<index_names.length; i++){
						if(in.equals(index_names[i])){
							column_names[i] += ","+cn;
							index = i;
							break;
						}
					}
					if(index == -1){
						index_names = Tools.addToStrArr(index_names, in);
						column_names = Tools.addToStrArr(column_names, cn);
						index_type = Tools.addToStrArr(index_type, indexRs.getString("index_type"));
						index_unique = Tools.addToStrArr(index_unique, indexRs.getString("uniqueness"));
					}
				}
				if(column_names != null){
					sb.append("index:\r\n");
					for(int k = 0; k < column_names.length; k++){
						sb.append(column_names[k].toLowerCase());
						sb.append("\t");
						int type = getIndextype(index_type[k], index_unique[k]);
						sb.append(type);
						if(!index_names[k].toLowerCase().startsWith("index_")){
							sb.append("\t");
							sb.append(index_names[k].toLowerCase());
						}
						sb.append("\r\n");
					}
					sb.append("indexEnd\r\n");
				}
				long t3 = System.currentTimeMillis();
				outlog.d("生成索引信息完成  用时："+(t3-t2));
				outlog.d("写入数据到文件  ...");
				fu.addToTxt(outPath+"/"+tabname.toLowerCase()+".txt", sb.toString());
				long t4 = System.currentTimeMillis();
				outlog.d("写入数据到文件完成  用时："+(t4-t3));
				outlog.d("文件创建完成  用时："+(t4-t1));
				dbHelper.closeRs(tabRs);
				dbHelper.closeRs(indexRs);
			}
			outlog.d("所有数据表导出完成  总用时："+(System.currentTimeMillis()-t));
			outlog.save();
			return new ReturnValue(true, returnSb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 在所有数据表中搜索含有指定字段的表
	 */
	public ReturnValue searchCol(String columns){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			String[] cols = Tools.splitStr(columns, ",");
			StringBuffer sb = new StringBuffer();
			ResultSet listRs = dbHelper.executeQuery("select * from user_tables");
			sb.append("\r\n关联表：\r\n");
			while(listRs.next()){
				String tabname = listRs.getString("table_name");
				ResultSet tabRs = dbHelper.executeQuery("select * from " + tabname);
				tabRs.next();
				ResultSetMetaData rsmd = tabRs.getMetaData();
				int colCount = rsmd.getColumnCount();
				boolean join = true;
				for(int c = 0; c < cols.length; c++){
					boolean exist = false;
					for(int i = 1; i <= colCount; i++){
						String colName = rsmd.getColumnName(i);
						if(colName.toLowerCase().equals(cols[c])){
							exist = true;
							break;
						}
					}
					if(!exist){
						join = false;
						break;
					}
				}
				if(join){
					sb.append(tabname+"\r\n");		
				}
				dbHelper.closeRs(tabRs);
			}
			
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 转换索引类型
	 */
	public int getIndextype(String index_type, String index_unique){
		int type = 0;
		if("NORMAL".equals(index_type) && "NONUNIQUE".equals(index_unique)){
			type = 1;		
		} else 
		if("NORMAL".equals(index_type) && "UNIQUE".equals(index_unique)){
			type = 2;
		} else 
		if("BITMAP".equals(index_type)){
			type = 3;
		} else {
			System.out.println("未知索引类型：" + index_type + " - " + index_unique);
		}
		return type;
	}
	
	/**
	 * 将目录中所有文件转换为数据表
	 */
	public ReturnValue createTabFromDir(String dir, boolean recreate, boolean useseq, boolean allowdb, boolean log, boolean showprocess){
		DBHelper dbHelper = null;
		if(log){
			dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		} else 
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase());
		}
		try {
			dbHelper.openConnection();
			ResultSet listRs = dbHelper.executeQuery("select table_name,comments from user_tab_comments");
			listRs.last();
			String[] tabs = new String[listRs.getRow()];
			listRs.beforeFirst();
			while(listRs.next()){
				tabs[listRs.getRow()-1] = listRs.getString("table_name").toLowerCase();
			}
			long t1 = System.currentTimeMillis();
			ResultSet indexRs = dbHelper.executeQuery("select a.index_name,a.column_name,a.table_name,a.column_position,b.index_type,b.uniqueness from user_ind_columns a left join user_indexes b on a.index_name=b.index_name order by column_position");
			ResultSet seqRs = dbHelper.executeQuery("select sequence_name from user_sequences");
			File[] files = new File(dir).listFiles();
			StringBuffer indexAddSb = new StringBuffer();
			StringBuffer indexDelSb = new StringBuffer();
			StringBuffer indexUpdSb = new StringBuffer();
			StringBuffer indexUpdSb2 = new StringBuffer();
			StringBuffer createInfoSb = new StringBuffer();
			for(int i = 0; files != null && i < files.length; i++){
				createTab(dbHelper, files[i], tabs, indexRs, allowdb, recreate, useseq, seqRs, showprocess, createInfoSb, indexAddSb, indexDelSb, indexUpdSb, indexUpdSb2);
			}
			long t2 = System.currentTimeMillis();
			StringBuffer sb = new StringBuffer();
			sb.append("--删除索引：\r\n");
			sb.append(indexDelSb.toString());
			sb.append("\r\n");
			sb.append("--新增索引：\r\n");
			sb.append(indexAddSb.toString());
			sb.append("\r\n");
			sb.append("--修改索引名：\r\n");
			sb.append(indexUpdSb.toString());
			sb.append("\r\n");
			sb.append("--修改索引类型：\r\n");
			sb.append(indexUpdSb2.toString());
			sb.append("\r\n");
			sb.append("--建表信息：\r\n");
			sb.append(createInfoSb.toString());
			sb.append("\r\n");
			sb.append("--用时："+(t2-t1));
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 根据指定路径的文件生成数据表并写入文件内数据
	 */
	public void createTab(DBHelper dbHelper, File file, String[] tabs, ResultSet indexRs, boolean allowdb, boolean recreate, boolean useseq, ResultSet seqRs, boolean showprocess, StringBuffer createInfoSb, StringBuffer indexAddSb, StringBuffer indexDelSb, StringBuffer indexUpdSb, StringBuffer indexUpdSb2){
		try {
			long t1 = System.currentTimeMillis();
			byte[] fileData = ToolFunc.getBytesFromFile(file.getPath());
			String fileText = null;
			try {
				fileText = new String(fileData, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			String tabname = Tools.getStrProperty(fileText, "name");
			if(showprocess){
				System.out.print("--"+tabname);
			}
			String dbuser = dbHelper.getDataBase().getUsername();
			if(!file.getName().startsWith("tab_") && !file.getName().startsWith("tb_")){
				BACException.throwInstance("非“tab_”开头的文件\r\n");
				return;
			}
			if(!file.getName().equals(tabname+".txt")){
				BACException.throwInstance(tabname+": 文件名与表名不一致("+file.getName()+","+tabname+".txt"+")\r\n");
				return;
			}
			String tabnamenote = Tools.getStrProperty(fileText, "namenote");
			boolean tabexist = MyTools.checkInStrArr(tabs, tabname);
			String[] fieldData = Tools.splitStr(Tools.getStrProperty(fileText, "field"), "\t");
			String[][] field = new String[fieldData.length][];
			for(int i = 0; i < field.length; i++){
				field[i] = Tools.splitStr(fieldData[i], ",");
			}
			String[] fieldnote = Tools.splitStr(Tools.getStrProperty(fileText, "fieldnote"), "\t");
			fieldData = null;
			String[][] data = Tools.getStrLineArrEx2(fileText, "data:", "dataEnd");
			String delSql = null;
			String addSql = null;
			StringBuffer noteSb = new StringBuffer();
			if(!tabexist || recreate){
				if(tabexist && recreate){//已存在，且需要重建时允许删表
					delSql = "drop table " + dbuser + "." + tabname;
					createInfoSb.append("\r\n"+delSql+";\r\n");
				}
				noteSb.append("comment on table " + dbuser + "." + tabname + " is '" + tabnamenote + "';\r\n");
				StringBuffer sb = new StringBuffer();
				sb.append("create table " + dbuser + "." + tabname + " (\r\n");
				sb.append("id number(11) not null,\r\n");
				for(int i = 0; i < field.length; i++){
					String length = "";
					if(!field[i][2].equals("0")){
						length = "(" + field[i][2] + ")";
					}
					String notnull = "not null";
					if(field[i].length >= 4 && field[i][3].equals("1")){
						notnull = "";
					}
					sb.append(field[i][0] + " " + getDataTypeStr(field[i][1]) + length + " " + notnull);
					if(i < field.length-1){
						sb.append(",\r\n");
					}
					noteSb.append("comment on column " + dbuser + "." + tabname + "." + field[i][0] + " is '" + fieldnote[i] + "';\r\n");
				}
				sb.append("\r\n)");
				addSql = sb.toString();
				createInfoSb.append("\r\n"+addSql+";\r\n");
				createInfoSb.append("\r\n"+noteSb.toString()+"\r\n");
				if(allowdb){
					if(delSql != null){
						dbHelper.execute(delSql);
						createInfoSb.append("<font color='#12ff00'>--execute del tab</font>\r\n");
					}
					dbHelper.execute(addSql);
					tabexist = true;
					createInfoSb.append("<font color='#12ff00'>--execute add tab</font>\r\n");
					String[] commSql = noteSb.toString().split(";\r\n");
					for(int c = 0; c < commSql.length; c++){
						try {
							dbHelper.execute(commSql[c]);
						} catch (Exception e) {
							createInfoSb.append("<font color='#ff0000'>--"+commSql[c]+": exception "+e.toString()+"</font>\r\n\r\n");
						}
					}
					createInfoSb.append("<font color='#12ff00'>--execute add note</font>\r\n");
				}
			}
			long t2 = System.currentTimeMillis();
			if(useseq){
				boolean seqexist = false;
				seqRs.beforeFirst();
				while(seqRs.next()){
					if(seqRs.getString("sequence_name").equalsIgnoreCase("s"+tabname)){
						seqexist = true;
						break;
					}
				}
				tidySEQ(dbHelper, seqexist, tabname, tabexist, allowdb, false, createInfoSb);
			}
			long t3 = System.currentTimeMillis();
			boolean insert = false;
			if(data!=null && tabexist){
				if(!allowdb && recreate){
					dbHelper.delete(tabname, "id>0");
					createInfoSb.append("<font color='#12ff00'>--delete</font>\r\n");
				}
				boolean havedata = false;
				if(!recreate){
					ResultSet tbRs = dbHelper.query(tabname, "id", null);
					havedata = tbRs.next();
					dbHelper.closeRs(tbRs);
				}
				insert = recreate || !havedata;
			}
			if(insert){
				for(int i = 0; i < data.length; i++){
					SqlString sqlStr = new SqlString();
					for(int k = 0; k < data[i].length; k++){
						if(field[k][1].equals("0")){
							if(data[i][k].indexOf('.')!=-1){
								sqlStr.add(field[k][0], Tools.str2double(data[i][k]));
							} else {
								sqlStr.add(field[k][0], Tools.str2long(data[i][k]));
							}
						} else 
						if(field[k][1].equals("1") || field[k][1].equals("4")){
							sqlStr.add(field[k][0], data[i][k]);
						} else 
						if(field[k][1].equals("2")){
							if(data[i][k].length() <= 10){//YYYY/MM/DD
								sqlStr.addDate(field[k][0], data[i][k]);
							} else {
								sqlStr.addDateTime(field[k][0], data[i][k]);
							}
						} else 
						if(field[k][1].equals("3")){
							//sqlStr.add(field[k][0], null);
						} else 
						if(field[k][1].equals("5")){
							sqlStr.addDateTimeMS(field[k][0], data[i][k]);
						}
					}
					if(useseq){
						dbHelper.insert(tabname, sqlStr);
					} else {
						dbHelper.insert(tabname, sqlStr, i+1);
					}
				}
				DBPoolMgr.getInstance().addClearTablePoolTask(tabname, null);
				createInfoSb.append("<font color='#12ff00'>--insert</font>\r\n");
			}
			long t4 = System.currentTimeMillis();
			String[][] indexdata = Tools.getStrLineArrEx2(fileText, "index:", "indexEnd");
			tidyIndexByTab(dbHelper, indexRs, tabname, indexdata, indexAddSb, indexDelSb, indexUpdSb, indexUpdSb2);//整理索引
			long t5 = System.currentTimeMillis();
			if(showprocess){
				System.out.println(" TAB:"+(t2-t1)+" SEQ:"+(t3-t2)+" DATA:"+(t4-t3)+" INDEX:"+(t5-t4));		
			}
		} catch (Exception e) {
			createInfoSb.append("<font color='#ff0000'>--"+file.getName()+": exception "+e.toString()+"</font>\r\n\r\n");
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据类型获取字段数据类型的字符串形式
	 */
	private String getDataTypeStr(String type){
		String result = null;
		if(type.equals("0")){
			result = "number";
		} else if(type.equals("1")){
			result = "varchar2";
		} else if(type.equals("2")){
			result = "date";
		} else if(type.equals("3")){
			result = "blob";
		} else if(type.equals("4")){
			result = "nvarchar2";
		} else if(type.equals("5")){
			result = "timestamp";
		}
		if(result == null){
			System.out.println("有错误的数据类型，错误码：" + type);
		}
		return result;
	}
	
	/**
	 * 将指定目录中的文本文件数据读入到tab_txt数据表中
	 */
	public ReturnValue saveTxtDirToTab(String dir, boolean update){
		File[] files = new File(dir).listFiles();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; files != null && i < files.length; i++){
			String filePath = dir + "/" + files[i].getName();
			ReturnValue fileRv = saveTxtToTab(filePath, update);
			sb.append(">>> file：" + files[i].getName() + " ");
			sb.append(fileRv.info + "\n");
		}
		return new ReturnValue(true, sb.toString());
	}
	
	/**
	 * 将指定文本文件数据读入到tab_txt数据表中
	 */
	public ReturnValue saveTxtToTab(String path, boolean update){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			byte[] fileData = ToolFunc.getBytesFromFile(path);
			int begintindex = path.lastIndexOf('/')+1;
			int endindex = path.lastIndexOf('.');
			String filename = path.substring(begintindex, endindex);
			if(fileData == null){
				BACException.throwInstance(filename + "内容为空");
			}
			String result = null;
			if(dbHelper.queryExist("tab_txt", "txtkey='"+filename+"'")){
				if(!update){
					BACException.throwInstance("none");
				}
				SqlString sqlStr = new SqlString();
				sqlStr.addBlob("txtvalue", fileData);
				dbHelper.update("tab_txt", sqlStr, "txtkey='"+filename+"'");
				result = "update";
			} else {
				SqlString sqlStr = new SqlString();
				sqlStr.add("txtkey", filename);
				sqlStr.addBlob("txtvalue", fileData);
				dbHelper.insert("tab_txt", sqlStr);
				result = "insert";
			}
			DBPoolMgr.getInstance().addClearTxtPoolTask(filename, null);
			return new ReturnValue(true, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	public static final String user_ind_columns = "user_ind_columns";
	
	/**
	 * 删除指定表的所有索引
	 */
	public void deleteAllIndexByTab(DBHelper dbHelper, String table, StringBuffer resultSb) throws Exception {
		ResultSet rs = dbHelper.query(user_ind_columns, "index_name,count(*)", "table_name=upper('"+table+"') group by index_name");
		StringBuffer sb = new StringBuffer("");
		while(rs.next()){
			sb.append("execute immediate'drop index "+rs.getString("index_name")+"';");
		}
		if(sb.length() > 0){
			dbHelper.execute("BEGIN " + sb.toString() + " END;");
			resultSb.append(">>> delete index finish\n");
		}
	}
	
	/**
	 * 整理SEQ
	 */
	public void tidySEQ(DBHelper dbHelper, boolean seqexist, String table, boolean tabexist, boolean allowdb, boolean recreate, StringBuffer createSb) throws Exception {
		String seqname = "s" + table;
		String delSql = null;
		String dbuser = dbHelper.getDataBase().getUsername();
		if(seqexist && recreate){
			delSql = "drop sequence " + dbuser + "." + seqname.toUpperCase();
			seqexist = false;
			createSb.append(delSql+";\r\n");
		}
		String addSql = null;
		int minvalue = 1;
		if(!seqexist){
			if(tabexist){
				ResultSet maxRs = dbHelper.query(table, "max(id) as maxid", null);
				maxRs.next();
				minvalue = maxRs.getInt("maxid")+1;
			}
			addSql = "create sequence " + dbuser + "." + seqname.toUpperCase() + " minvalue "+minvalue;
			createSb.append(addSql+";\r\n");
		}
		if(allowdb){
			if(delSql != null){
				dbHelper.execute(delSql);
				createSb.append("<font color='#12ff00'>--execute del seq</font>\r\n");
			}
			if(addSql != null){
				dbHelper.execute(addSql);
				createSb.append("<font color='#12ff00'>--execute add seq</font>\r\n");
			}
		}
	}
	
	/**
	 * 创建指定表的所有索引
	 */
	public void createAllIndexByTab(DBHelper dbHelper, String table, String[][] indexdata, StringBuffer resultSb) throws Exception {
		StringBuffer sb = new StringBuffer("");
		for(int i = 0; indexdata!=null && i<indexdata.length; i++){
			String column = indexdata[i][0];
			String type = "";
			if(indexdata[i][1].equals("2")){
				type = "unique";
			} else 
			if(indexdata[i][1].equals("3")){
				type = "bitmap";
			}
			String indexStr = indexdata[i][2];
			sb.append("execute immediate'create "+type+" index "+indexStr+" on "+table+" ("+column+")';");
		}
		if(sb.length() > 0){
			dbHelper.execute("BEGIN " + sb.toString() + " END;");
			resultSb.append(">>> create index finish\n");
		}
	}
	
	/**
	 * 整理索引
	 */
	public void tidyIndexByTab(DBHelper dbHelper, ResultSet indexRs, String table, String[][] indexdata, StringBuffer indexAddSb, StringBuffer indexDelSb, StringBuffer indexUpdSb, StringBuffer indexUpdSb2) throws Exception {
		indexRs.beforeFirst();
		String[] index_names = null;
		String[] column_names = null;
		String[] index_type = null;
		String[] index_unique = null;
		while(indexRs.next()){
			if(indexRs.getString("table_name").equalsIgnoreCase(table)){
				int index = -1;
				String i_name = indexRs.getString("index_name");//索引名
				String c_name = indexRs.getString("column_name");//字段
				for(int i = 0; index_names!=null && i<index_names.length; i++){
					if(i_name.equals(index_names[i])){
						column_names[i] += ","+c_name;
						index = i;
						break;
					}
				}
				if(index == -1){
					index_names = Tools.addToStrArr(index_names, i_name);
					column_names = Tools.addToStrArr(column_names, c_name);
					index_type = Tools.addToStrArr(index_type, indexRs.getString("index_type")!=null?indexRs.getString("index_type").toUpperCase():null);
					index_unique = Tools.addToStrArr(index_unique, indexRs.getString("uniqueness")!=null?indexRs.getString("uniqueness").toUpperCase():null);
				}
			}
		}
		/*
		for(int i = 0; index_names!=null && i<index_names.length; i++){
			tidyindexlog.d("TABLE:"+table+" NAME:"+index_names[i]+" COLUMN:"+column_names[i]+" TYPE:"+index_type[i]+" UNIQUE:"+index_unique[i]);
			tidyindexlog.d("-------------------");
			tidyindexlog.save();
		}
		*/
		for(int i = 0; indexdata!=null && i<indexdata.length; i++){//循环列表中数据
			String column = indexdata[i][0].toUpperCase();//列表中字段
			//indexUpdSb.append(column+" | \r\n");
			boolean exist = false;
			for(int k = 0; column_names!=null && k<column_names.length; k++){//循环所有数据库字段
				if(column_names[k].equals(column)){//找到匹配
					String temp1 = index_names[k].toLowerCase();//数据库中索引名
					String temp2 = null;//列表中索引名
					if(indexdata[i].length >= 3){
						temp2 = indexdata[i][2];
					}
					int temp3 = getIndextype(index_type[k], index_unique[k]);//数据库中索引类型
					int temp4 = Tools.str2int(indexdata[i][1]);//列表中索引类型
					String type = getIndexTypeStr(indexdata[i][1]);
					String indexStr = indexdata[i][2];
					if(temp3 != temp4){
						indexUpdSb2
						.append("--表："+table+" | 字段："+column_names[k].toLowerCase()+" | "+index_type[k]+" | "+index_unique[k]+" | TYPE:TAB "+temp3+"("+index_type[k]+","+index_unique[k]+")"+" | TXT " + temp4+" \r\n")
						.append("drop index "+temp1+";\r\n")
						.append("create "+type+" index "+indexStr+" on "+table+" ("+column+");\r\n");
					} else 
					if(temp2!=null && !temp1.equals(temp2)){//索引名不匹配
						indexUpdSb
						.append("--表："+table+" | 字段："+column_names[k].toLowerCase()+" | NAME：TAB "+temp1+" | TXT " + temp2+" \r\n")
						.append("alter index "+temp1+" rename to "+indexStr+";\r\n");
					} 
					index_names = Tools.removeOneFromStrArr(index_names, k);
					column_names  = Tools.removeOneFromStrArr(column_names, k);
					index_type = Tools.removeOneFromStrArr(index_type, k);
					index_unique = Tools.removeOneFromStrArr(index_unique, k);
					exist = true;
					break;
				}
			}
			if(exist){
				continue;
			}
			String type = getIndexTypeStr(indexdata[i][1]);
			String indexStr = indexdata[i][2];
			indexAddSb.append("--表："+table+" | \r\n");
			indexAddSb.append("create "+type+" index "+indexStr+" on "+table+" ("+column+");\r\n");
		}
		for(int i = 0; index_names!=null && i<index_names.length; i++){
			indexDelSb.append("--表："+table+" |  索引："+index_names[i].toLowerCase()+" |  字段："+column_names[i].toLowerCase()+" | 类型："+getIndextype(index_type[i], index_unique[i])+" "+index_type[i]+","+index_unique[i]+" | \r\n");
			indexDelSb.append("drop index "+index_names[i]+";\r\n");
		}
	}
	
	/**
	 * 获取索引类型的字符串形式
	 */
	public String getIndexTypeStr(String type){
		String typeStr = "";
		if("2".equals(type)){
			typeStr = "unique";
		} else 
		if("3".equals(type)){
			typeStr = "bitmap";
		}
		return typeStr;
	}
	
	//--------------静态区--------------
	
	/**
	 * 获取实例
	 */
	public static TabMgr getInstance(){
		return new TabMgr();
	}
}