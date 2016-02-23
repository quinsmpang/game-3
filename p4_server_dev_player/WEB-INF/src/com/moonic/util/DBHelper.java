package com.moonic.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;
import server.database.DataBase;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.MyPreparedStatement;
import com.moonic.bac.ServerBAC;
import com.moonic.mgr.LockStor;

import conf.Conf;

/**
 * 数据库管理助手
 * @author John
 */
public class DBHelper {
	private DataBase db;
	private Connection conn;
	private static DataBase defaultDataBase;
	private ArrayList<Statement> stmtArrlist;
	
	public static int connectionAmount; //总打开连接数
	public static int totalOpenRsAmount; //总打开rs数
	
	private int openRsAmount; //当前打开rs数
	private Hashtable<ResultSet, String> rsHashTable = new Hashtable<ResultSet, String>();
	
	public static MyLog log;
	
	{
		log = new MyLog(MyLog.NAME_DATE, "log_db", "DB", false, false, false, null);
	}
	
	/**
	 * 获取连接数量信息
	 */
	public static String getConnAmInfo(){
		StringBuffer sb = new StringBuffer();
		sb.append("连接数：" + connectionAmount + "\r\n");
		sb.append("RS数：" + totalOpenRsAmount + "\r\n");
		sb.append("当前连接池的总启动连接数：" + (ServerConfig.getDataBase().getNumActive()+ServerConfig.getDataBase().getNumIdle()) + "\r\n");
		sb.append("当前连接池活动的连接数：" + ServerConfig.getDataBase().getNumActive() + "\r\n");
		sb.append("当前连接池待机的连接数：" + ServerConfig.getDataBase().getNumIdle() + "\r\n\r\n");
		sb.append("尚未关闭的RS列表：\r\n");
		for(int i = 0; i < dbhVec.size(); i++){
			sb.append(dbhVec.get(i).getHashtable());
		}
		return sb.toString();
	}
	
	public static Vector<DBHelper> dbhVec = new Vector<DBHelper>();
	
	/**
	 * 构造
	 */
	public DBHelper() {
		if (defaultDataBase == null) {
			System.out.println("未设置默认数据库");
		} else {
			db = defaultDataBase;
		}
	}
	
	/**
	 * 构造
	 */
	public DBHelper(DataBase database) {
		db = database;
	}
	
	/**
	 * 设置默认连接的数据库
	 */
	public static void setDefaultDataBase(DataBase database) {
		defaultDataBase = database;
	}
	
	/**
	 * 获取连接的数据库
	 */
	public DataBase getDataBase() {
		return db;
	}
	
	/**
	 * 打开连接
	 */
	public Connection openConnection() throws Exception {
		return openConnection(true);
	}	
	
	/**
	 * 打开连接
	 */
	public Connection openConnection(boolean allowAutoCommit) throws Exception {
		if(conn != null) {//已连结过直接返回
			return conn;
		}
		try {
			conn = db.getConnection();
			conn.setAutoCommit(allowAutoCommit);
		} catch (Exception e) {
			e.printStackTrace();
			BACException.throwInstance("获取数据库连接失败");
		}
		if(rsHashTable == null) {
			rsHashTable = new Hashtable<ResultSet, String>();
		}
		connectionAmount++;
		log.d("打开连接 连接数：" + connectionAmount, Conf.out_sql);
		if(connectionAmount > 100) {
			log.e("连接数超过了100，当前连接数=" + connectionAmount);
		}
		if(!dbhVec.contains(this)) {
			dbhVec.add(this);
		}
		return conn;
	}
	
	/**
	 * 提交
	 */
	public void commit() {
		try {
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 回滚
	 */
	public void rollback() {
		try {
			if (conn!=null && !conn.getAutoCommit()) {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭连接
	 */
	public void closeConnection() {
		if(conn != null) {
			try {
				conn.setAutoCommit(true);			
			} catch (SQLException e) {
				e.printStackTrace();
			}
			closeAllStatement();
			db.closeConnection(conn);
			conn = null;
			connectionAmount--;
			totalOpenRsAmount-=openRsAmount;
			openRsAmount=0;
			log.d("--- 总打开rs数="+totalOpenRsAmount);
			log.d("关闭连接 连接数：" + connectionAmount, Conf.out_sql);
		}
		if(dbhVec.contains(this)){
			dbhVec.remove(this);
		}
	}
	
	/**
	 * LOGDB插入
	 */
	public static void logInsert(String table, SqlString sqlStr){
		synchronized (LockStor.getLock(LockStor.LOG_SAVE)) {
			SaveLogTask slt = new SaveLogTask(table, sqlStr);
			pqarr[pquseInd].addTask(slt);
			pquseInd++;
			if(pquseInd >= pqarr.length){
				pquseInd = 0;
			}
		}
	}
	private static byte pquseInd;
	private static byte timeoutAm;
	private static ProcessQueue[] pqarr = new ProcessQueue[10];
	
	static {
		for(int i = 0; i < pqarr.length; i++){
			pqarr[i] = new ProcessQueue();
		}
	}
	
	/**
	 * 重置插入日志失败次数
	 */
	public static ReturnValue resetInsertLogTimeoutAm(){
		timeoutAm = 0;
		return new ReturnValue(true, "重置成功");
	}
	
	/**
	 * 获取存线程队列运行状态
	 */
	public static ReturnValue getSaveLogPQState(){
		StringBuffer sb = new StringBuffer();
		sb.append("插入日志失败次数："+timeoutAm+"\r\n");
		for(int i = 0; i < pqarr.length; i++){
			sb.append("队列 "+(i+1)+" 待处理任务数：" + pqarr[i].getQueueSize()+"\r\n");
		}
		return new ReturnValue(true , sb.toString());
	}
	
	/**
	 * 存日志任务
	 * @author John
	 */
	static class SaveLogTask implements ProcessQueueTask {
		public String table;
		public SqlString sqlStr;
		public SaveLogTask(String table, SqlString sqlStr){this.table=table;this.sqlStr=sqlStr;}
		public void execute() {
			if(timeoutAm <= 50){
				DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
				try {
					dbHelper.openConnection();
					dbHelper.insert(table, sqlStr);
					timeoutAm = 0;
				} catch (Exception e) {
					timeoutAm++;
					System.out.println("存储日志异常("+timeoutAm+"):"+e.getMessage()+"， ["+table+"]["+sqlStr.colString()+"]["+sqlStr.valueString()+"]");
				} finally {
					dbHelper.closeConnection();
				}		
			} else {
				System.out.println("存储日志异常"+timeoutAm+"，已停止记录日志 ["+table+"]["+sqlStr.colString()+"]["+sqlStr.valueString()+"]");
				synchronized (LockStor.getLock(LockStor.LOG_EXC_RECOVER)) {
					if(recoverSaveLogTask == null){
						recoverSaveLogTask = new RecoverSaveLogTT();
						ServerBAC.timer.schedule(recoverSaveLogTask, MyTools.long_minu*5, TimeUnit.MILLISECONDS);
					}		
				}
			}
		}
	}
	
	private static RecoverSaveLogTT recoverSaveLogTask = null;
	
	/**
	 * 恢复记录日志计时器
	 */
	static class RecoverSaveLogTT extends MyTimerTask {
		public void run2() {
			timeoutAm = 0;
			recoverSaveLogTask = null;
			System.out.println("恢复记录日志");
		}
	}
	
	/**
	 * 获取下一自增ID
	 */
	public int getNextId(String table) throws Exception {
		ResultSet rs = executeQuery("select "+SqlString.getSeqNextStr(table)+" from dual");
		rs.next();
		int id = rs.getInt("nextval");
		closeRs(rs);
		return id;
	}
	
	/**
	 * 获取指定RS数据量
	 */
	public int getRsDataCount(ResultSet rs) throws Exception {
		int row = rs.getRow();
		rs.last();
		int count = rs.getRow();
		if(row == 0){
			rs.beforeFirst();
		} else {
			rs.absolute(row);	
		}
		return count;
	}
	
	/**
	 * 获取的插入的 STMT
	 */
	public MyPreparedStatement getInsertStmt(String table, SqlString sqlStr) throws Exception {
		if(conn==null) {
			openConnection();
		}
		MyPreparedStatement stmt = sqlStr.getInsertPreparedStatementAutoID(conn, table);//使用自动ID，不能与使用指定ID合并
		return stmt;
	}
	
	/**
	 * 获取的插入的 STMT
	 */
	public MyPreparedStatement getInsertStmt(String table, SqlString sqlStr, int id) throws Exception{
		sqlStr.add("id", id);
		if(conn==null) {
			openConnection();
		}
		MyPreparedStatement stmt = sqlStr.getInsertPreparedStatement(conn, table);//使用指定ID
		return stmt;
	}
	
	/**
	 * 插入
	 */
	public void insert(String table, SqlString sqlStr) throws Exception {
		if(conn==null) {
			openConnection();
		}
		MyPreparedStatement stmt = sqlStr.getInsertPreparedStatementAutoID(conn, table);//使用自动ID，不能与使用指定ID合并
		execute(stmt);
	}
	
	/**
	 * 插入
	 */
	public int insertAndGetId(String table, SqlString sqlStr) throws Exception {
		int id = getNextId(table);
		insert(table, sqlStr, id);
		return id;
	}
	
	/**
	 * 插入
	 */
	public void insert(String table, SqlString sqlStr, int id) throws Exception {
		sqlStr.add("id", id);
		if(conn==null) {
			openConnection();
		}
		MyPreparedStatement stmt = sqlStr.getInsertPreparedStatement(conn, table);//使用指定ID
		execute(stmt);
	}
	
	/**
	 * 删除
	 */
	public void delete(String table, String where) throws Exception{
		if(where == null || where.equals("")){
			BACException.throwAndPrintInstance("删除命令执行失败，条件为空，表：" + table);
		}
		String where1 = where;
		SqlString wStr = new SqlString();
		where = DBUtil.convertWhere(where, wStr);
		if(conn==null) {
			openConnection();
		}		
		MyPreparedStatement stmt = wStr.getDeletePreparedStatement(conn, table, "where "+where);
		stmt.where1 = where1;
		stmt.where2 = where;
		stmt.wStr = wStr;
		execute(stmt);
	}
	
	/**
	 * 获取修改的 STMT
	 */
	public MyPreparedStatement getUpdateStmt(String table, SqlString sqlStr, String where) throws Exception{
		if(where == null || where.equals("")){
			BACException.throwAndPrintInstance("获取失败，条件为空，表：" + table);
		}
		String where1 = where;
		SqlString wStr = new SqlString();
		where = DBUtil.convertWhere(where, wStr);
		if(conn==null) {			
			openConnection();			
		}
		MyPreparedStatement stmt = sqlStr.getUpdatePreparedStatement(conn, table, wStr, "where "+where);
		stmt.where1 = where1;
		stmt.where2 = where;
		stmt.wStr = wStr;
		return stmt;
	}
	
	/**
	 * 修改
	 * @param where 传null修改所有，传""修改失败
	 */
	public void update(String table, SqlString sqlStr, String where) throws Exception{
		if(where != null && where.equals("")){
			BACException.throwAndPrintInstance("修改命令执行失败，条件为空，表：" + table);
		}
		if(sqlStr.getColCount() == 0){
			BACException.throwAndPrintInstance("修改命令执行失败，字段为空，表：" + table);
		}
		String where1 = where;
		SqlString wStr = new SqlString();
		where = DBUtil.convertWhere(where, wStr);
		if(where != null && !where.equals("")){
			where = "where "+where;
		} else {
			where = "";
		}
		if(conn==null) {			
			openConnection();			
		}
		MyPreparedStatement stmt = sqlStr.getUpdatePreparedStatement(conn, table, wStr, where);
		stmt.where1 = where1;
		stmt.where2 = where;
		stmt.wStr = wStr;
		execute(stmt);
	}
	
	/**
	 * 查询结果以JSONARR返回
	 */
	public JSONArray queryJsonArray(String sql) {
		JSONArray jsonarr = null;
		try {
			openConnection();
			ResultSet rs = executeQuery(sql);
			jsonarr = DBUtil.convertRsToJsonarr(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeConnection();
		}
		return jsonarr;
	}
	
	/**
	 * 查询结果以JSONOBJ返回
	 */
	public JSONObject queryJsonObj(String sql) {
		JSONObject jsonobj = null;
		try {
			openConnection();
			ResultSet rs = executeQuery(sql);
			jsonobj = DBUtil.convertRsToJsonobj(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeConnection();
		}
		return jsonobj;
	}
	
	/**
	 * 查询符合条件的多个记录
	 */
	public JSONArray queryJsonArray(String table, String target, String where, String order) throws Exception {
		return queryJsonArray(table, target, where, order, null, 0, 0);
	}
	
	/**
	 * 查询结果以JSONOBJ形式返回
	 */
	public JSONObject queryJsonObj(String table, String target, String where) throws Exception {
		return queryJsonObj(table, target, where, null, null, 0, 0);
	}
	
	
	/**
	 * 查询符合条件的多个记录
	 */
	public JSONArray queryJsonArray(String table, String target, String where, String order, String group, int minRow, int maxRow) throws Exception {
		JSONArray jsonarr = null;
		try {
			openConnection();
			ResultSet rs = query(table, target, where, order, group, minRow, maxRow);
			jsonarr = DBUtil.convertRsToJsonarr(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeConnection();
		}
		return jsonarr;
	}
	
	/**
	 * 查询单个记录
	 */
	public JSONObject queryJsonObj(String table, String target, String where, String order, String group, int minrows, int maxrows) throws Exception {
		JSONObject jsonobj = null;
		try {
			openConnection();
			ResultSet rs = query(table, target, where, order, group, minrows, maxrows);
			jsonobj = DBUtil.convertRsToJsonobj(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeConnection();
		}
		return jsonobj;
	}
	
	/**
	 * 获取数值
	 */
	public int getIntValue(String table, String target, String where) throws Exception {
		return Tools.str2int(getStrValue(table, target, where));
	}
	
	/**
	 * 获取字符串值
	 */
	public String getStrValue(String table, String target, String where) throws Exception {
		ResultSet rs = query(table, target, where);
		if (!rs.next()) {
			BACException.throwInstance("记录不存在");
		}
		String val = rs.getString(target);
		closeRs(rs);
		return val;
	}
	
	/**
	 * 联表查询
	 * @param tables 不允许有null
	 * @param targets 不允许有null
	 */
	public ResultSet query(String[] tables, String[] targets, String where) throws Exception{
		StringBuffer tableStr = new StringBuffer("");
		StringBuffer targetStr = new StringBuffer("");
		for(int i = 0; i < tables.length; i++){
			if(tableStr.length() > 0){
				tableStr.append(",");
			}
			tableStr.append(tables[i]);
			String[] columns = Tools.splitStr(targets[i], ",");
			for(int k = 0; k < columns.length; k++){
				if(targetStr.length() > 0){
					targetStr.append(",");
				}
				targetStr.append(tables[i]);
				targetStr.append(".");
				targetStr.append(columns[k]);
			}
		}
		return query(tableStr.toString(), targetStr.toString(), where);
	}
	
	/**
	 * 内联查询
	 * @param tables 表名    需要指定别名
	 */
	public ResultSet queryInnerJoin(String[] tables, String  targets, String where,String onWhere) throws Exception{
		StringBuffer tableStr = new StringBuffer();
		for(int i = 0; i < tables.length; i++){
			if(i> 0&&i<tableStr.length()-1){
				tableStr.append(" inner join ");	
			}
			tableStr.append(tables[i]);
		}
		tableStr.append(" on "+onWhere+" ");
		return query(tableStr.toString(), targets, where);
	}
	
	/**
	 * 左联表查询
	 * @param tables 不允许有null
	 * @param targets 不允许有null
	 */
	public ResultSet queryLeftJoin(String[] tables, String[] targets, String where,String onWhere) throws Exception{
		StringBuffer tableStr = new StringBuffer("");
		StringBuffer targetStr = new StringBuffer("");
		for(int i = 0; i < tables.length; i++){
			if(tableStr.length() > 0){
				tableStr.append(" left join ");
			}
			tableStr.append(tables[i]);
			String[] columns = Tools.splitStr(targets[i], ",");
			for(int k = 0; k < columns.length; k++){
				if(targetStr.length() > 0){
					targetStr.append(",");
				}
				targetStr.append(tables[i]);
				targetStr.append(".");
				targetStr.append(columns[k]);
			}
		}
		tableStr.append(" on "+onWhere+" ");
		return query(tableStr.toString(), targetStr.toString(), where);
	}
	
	/**
	 * 查询是否存在符合条件的记录
	 */
	public boolean queryExist(String table, String where) throws Exception{
		boolean exist = false;
		ResultSet rs = query(table, "id", where);
		exist = rs.next();
		closeRs(rs);
		return exist;
	}
	
	/**
	 * 查询符合条件的记录数
	 */
	public int queryCount(String table, String where) throws Exception {
		int amount = 0;
		ResultSet rs = query(table, "count(1)", where);
		if (rs.next()) {
			amount = rs.getInt(1);
		}
		closeRs(rs);
		return amount;
	}
	
	/**
	 * 查询
	 */
	public ResultSet query(String table, String target, String where) throws Exception{
		return query(table, target, where, null, null, 0, 0);
	}
	
	/**
	 * 查询有排序
	 */
	public ResultSet query(String table, String target, String where, String order) throws Exception {
		return query(table, target, where, order, null, 0, 0);
	}
	
	/**
	 * 查询有排序分组
	 */
	public ResultSet query(String table, String target, String where, String order, String group) throws Exception {
		return query(table, target, where, order, group, 0, 0);
	}
	
	/**
	 * 查询并获取指定行数
	 */
	public ResultSet query(String table, String target, String where, String order, int rows) throws Exception {
		return query(table, target, where, order, null, 0, rows);
	}
	
	/**
	 * 查询并获取指定起止行数
	 */
	public ResultSet query(String table, String target, String where, String order, int minrows, int maxrows) throws Exception {
		return query(table, target, where, order, null, minrows, maxrows);
	}
	
	/**
	 * 查询
	 * ('target' 为空表示'*'查询)
	 * ('where' 为空表示无条件)
	 * ('order' 为空表示无排序)
	 * ('group' 为空表示无分组)
	 */
	public ResultSet query(String table, String target, String where, String order, String group, int minRow, int maxRow) throws Exception{
		String where1 = where;
		SqlString wStr = new SqlString();
		where = DBUtil.convertWhere(where, wStr);
		if(where != null && !where.equals("")){
			where = "where "+where;
		} else {
			where = "";
		}
		if(conn==null) {
			openConnection();
		}
		MyPreparedStatement stmt = wStr.getQueryPreparedStatement(conn, table, target, where, order, group, minRow, maxRow);
		stmt.where1 = where1;
		stmt.where2 = where;
		stmt.wStr = wStr;
		return executeQuery(stmt);
	}
	
	/**
	 * 执行更改
	 */
	public void execute(MyPreparedStatement stmt) throws Exception {
		log.d("执行SQL更新语句：" + stmt.getSql(), Conf.out_sql);
		try {
			db.preparedExecute(stmt);
		} catch(SQLException ex) {
			System.out.println("执行SQL更新语句失败 异常信息："+ex.toString()+"\r\n" + stmt.getExceptionMsg());
			ex.printStackTrace();
			BACException.throwInstance("执行SQL更新语句失败 异常信息："+ex.toString());
		}
	}
	
	/**
	 * 获取STMT
	 */
	public MyPreparedStatement getStmt(String sql) throws Exception {
		if(conn==null) {
			openConnection();			
		}
		PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		MyPreparedStatement myStmt = new MyPreparedStatement(conn,stmt,sql);
		return myStmt;
	}
	
	/**
	 * 执行更改
	 */
	public boolean execute(String sql) throws Exception {
		log.d("执行SQL更新语句：" + sql, Conf.out_sql);
		if(conn==null) {			
			openConnection();
		}
		return db.executeWithException(conn, sql);
	}
	
	/**
	 * 执行查询
	 */
	public ResultSet executeQuery(MyPreparedStatement stmt) throws Exception {
		try {
			log.d("执行SQL查询语句：" + stmt.getSql(), Conf.out_sql);
			ResultSet rs = db.preparedQuery(stmt);
			addStatement(rs.getStatement());
			openRsAmount++;
			totalOpenRsAmount++;
			log.d("打开RS 总打开rs数=" + totalOpenRsAmount);
			if(rsHashTable!=null) {
				rsHashTable.put(rs, stmt.getSql());
			}
			return rs;
		} catch(SQLException ex) {
			System.out.println("执行SQL查询语句失败 异常信息："+ex.toString()+"\r\n" + stmt.getExceptionMsg());
			ex.printStackTrace();
			BACException.throwInstance("执行SQL查询语句失败 异常信息："+ex.toString());
		}
		return null;
	}
	
	/**
	 * 执行查询
	 */
	public ResultSet executeQuery(String sql) throws Exception {
		log.d("执行SQL查询语句：" + sql, Conf.out_sql);
		if(conn==null) {
			openConnection();
		}
		ResultSet rs = db.executeQueryWithException(conn, sql);
		addStatement(rs.getStatement());
		openRsAmount++;
		totalOpenRsAmount++;
		log.d("打开RS 总打开rs数=" + totalOpenRsAmount);
		rsHashTable.put(rs, sql);
		return rs;
	}
	
	/**
	 * 获取带表的字段名字
	 */
	public String getTabColumn(String table, String column){
		StringBuffer sb = new StringBuffer();
		sb.append(table);
		sb.append(".");
		sb.append(column);
		return sb.toString();
	}
	
	/**
	 * 关闭所有STMT
	 */
	public void closeAllStatement() {
		Enumeration<ResultSet> enu = rsHashTable.keys();
		while (enu.hasMoreElements()) {
			try {
				enu.nextElement().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		rsHashTable = null;
		for (int i = 0; stmtArrlist != null && i < stmtArrlist.size(); i++) {
			try {
				((Statement) stmtArrlist.get(i)).close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		stmtArrlist = null;
		totalOpenRsAmount -= openRsAmount;
		openRsAmount = 0;
		log.d("关闭Rs 总打开rs数=" + totalOpenRsAmount);
	}
	
	/**
	 * 关闭指定RS
	 */
	public void closeRs(ResultSet rs) {
		try {
			if(rs!=null) {
				Statement stmt = rs.getStatement();
				if(stmt != null){
					rs.close();
					stmt.close();
					if (openRsAmount > 0) {
						openRsAmount--;
					}
					if (totalOpenRsAmount > 0) {
						totalOpenRsAmount--;
					}
					log.d("关闭RS 总打开rs数=" + totalOpenRsAmount);
					rsHashTable.remove(rs);
				}
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印当前打开的RS
	 */
	public String getHashtable() {
		StringBuffer sb = new StringBuffer();
		Enumeration<ResultSet> enu = rsHashTable.keys();
		while (enu.hasMoreElements()) {
			sb.append(rsHashTable.get(enu.nextElement())+"\r\n");
		}
		return sb.toString();
	}
	
	/**
	 * 记录打开的STMT
	 */
	private void addStatement(Statement stmt) {
		if (stmtArrlist == null) {
			stmtArrlist = new ArrayList<Statement>();
		}
		if (!stmtArrlist.contains(stmt)) {
			stmtArrlist.add(stmt);
		}
	}
}
