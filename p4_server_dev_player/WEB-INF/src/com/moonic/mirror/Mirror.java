package com.moonic.mirror;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;

import com.ehc.common.SqlString;
import com.moonic.mgr.LockStor;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPsRs;
import com.moonic.util.DBUtil;
import com.moonic.util.MyLog;
import com.moonic.util.Out;

import conf.Conf;

/**
 * 镜像抽象(一对多关系)
 * @author John
 */
public abstract class Mirror {
	public final String tab;
	public final String col;
	
	public final String key_col;
	
	public boolean serverWhere;//是否在条件前自动增加SID等于本服SID的SERVERID条件
	public boolean haveServerWhere;//是否有以SERVERID为关键字段的镜像，如果有则不自动清理镜像数据
	
	public HashMap<Integer, JSONArray> q_mirror;//已查询数据库过的镜像单元
	public HashMap<Integer, JSONArray> noq_mirror;//未查询过的数据库镜像单元
	
	/**
	 * 构造
	 */
	public Mirror(String tab, String col, String key_col){
		if(tab == null){
			throw new RuntimeException("镜像表名不可为空");
		}
		if(col == null){
			throw new RuntimeException("镜像关键字段不可为空");
		}
		this.tab = tab;
		this.col = col;
		
		this.key_col = key_col;
		
		q_mirror = new HashMap<Integer, JSONArray>(32768);
		noq_mirror = new HashMap<Integer, JSONArray>(32768);
		
		ArrayList<Mirror> tab_mirrorobjList = MirrorMgr.tab_mirrorobjTab.get(tab);
		if(tab_mirrorobjList == null){
			tab_mirrorobjList = new ArrayList<Mirror>();
			MirrorMgr.tab_mirrorobjTab.put(tab, tab_mirrorobjList);
		}
		tab_mirrorobjList.add(this);
		//System.out.println("--------------\r\n"+tab+":"+tab_mirrorobjList+"\r\n-------------------");
		MirrorMgr.classname_mirror.put(getClass().getName(), this);
		Out.println("loading "+getClass().getName());
	}
	
	private static MyLog mirrorLog = new MyLog(MyLog.NAME_DATE, "mirrorlog", "MIRRORLOG", true, false, true, null);
	
	/**
	 * 初始化
	 */
	private void initMirror(int colid) throws Exception {
		DBHelper dbHelper = new DBHelper();
		try {
			if(!haveServerWhere && q_mirror.size()+noq_mirror.size()>4000){
				mirrorLog.d("镜像清理 表："+tab+" Q_LEN:"+q_mirror.size()+" NOQ_LEN:"+noq_mirror.size());
				MirrorMgr.clearTabData(tab, false);
			}
			dbHelper.openConnection();
			String where = col+"="+colid;
			if(serverWhere){
				where += " and serverid="+Conf.sid;
			}
			ResultSet rs = dbHelper.query(tab, null, where);
			JSONArray dbarr = DBUtil.jsonQuery(tab, DBUtil.convertRsToFormat(tab, rs), null, "id", 0, 0);//从数据库拿出来的数据
			dbHelper.closeRs(rs);
			if(noq_mirror.containsKey(colid)){
				JSONArray noqarr = DBUtil.jsonQuery(tab, noq_mirror.get(colid), null, "id", 0, 0);//已存在的数据
				int k = 0;
				for(int i = 0; i < noqarr.length(); i++){//1,4,5
					JSONArray noq = noqarr.optJSONArray(i);
					for(; k < dbarr.length(); k++){//1,2,3,4,5,6,7
						JSONArray db = dbarr.optJSONArray(k);
						if(noq.optInt(0)==db.optInt(0)){
							dbarr.put(k, noq);//用已存在的对象替换数据库对象
							k++;
							break;
						} else {
							MirrorMgr.sendInsertMessage(this, tab, db);//新增对象通知
						}
					}
				}
				for(int i = k; i < dbarr.length(); i++){//6,7
					MirrorMgr.sendInsertMessage(this, tab, dbarr.optJSONArray(i));//新增对象通知
				}
				noq_mirror.remove(colid);
			} else {
				for(int k = 0; k < dbarr.length(); k++){
					MirrorMgr.sendInsertMessage(this, tab, dbarr.optJSONArray(k));//新增对象通知
				}
			}
			q_mirror.put(colid, dbarr);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 检查是是否查询过
	 */
	private boolean checkHas(int colid) throws Exception {
		boolean has = q_mirror.containsKey(colid);
		if(!has){
			initMirror(colid);
		}
		return has;
	}
	
	/**
	 * 清除镜像数据
	 */
	public void clear(){
		q_mirror.clear();
		noq_mirror.clear();
	}
	
	/**
	 * 插入，默认传入COLID=-1将使用插入记录生成的ID
	 */
	public int insertByAutoID(DBHelper dbHelper, SqlString sqlStr) throws Exception {
		return insert(dbHelper, -1, sqlStr);
	}
	
	/**
	 * 插入
	 * @param colid 传-1时表示用插入数据后得到的值作为KEY
	 */
	public int insert(DBHelper dbHelper, int colid, SqlString sqlStr) throws Exception {
		if(colid == 0){
			throw new RuntimeException("主要条件值不能为0");
		}
		int id = dbHelper.insertAndGetId(tab, sqlStr);
		if(colid == -1){
			colid = id;
		}
		mirrorInsert(colid, sqlStr, id);
		return id;
	}
	
	/**
	 * 插入
	 */
	public void insert(DBHelper dbHelper, int colid, SqlString sqlStr, int id) throws Exception {
		if(colid == 0){
			throw new RuntimeException("主要条件值不能为0");
		}
		dbHelper.insert(tab, sqlStr, id);
		mirrorInsert(colid, sqlStr, id);
	}
	
	/**
	 * 镜像插入
	 */
	private void mirrorInsert(int colid, SqlString sqlStr, int id) throws Exception {
		long t1 = System.currentTimeMillis();
		synchronized (LockStor.getLock(LockStor.PLA_MIRROR, tab)) {
			boolean has = checkHas(colid);
			if(has){//插入的特殊性
				JSONObject colobj = DBUtil.colmap.optJSONObject(tab);
				JSONArray data = new JSONArray();
				for(int i = 0; i < colobj.length(); i++){
					data.add(null);
				}
				sqlStr.updateMirror(colobj, data);
				data.put(colobj.optInt("id"), id);
				JSONArray unit_mirror = q_mirror.get(colid);
				unit_mirror.add(data);
				MirrorMgr.sendInsertMessage(this, tab, data);
			}
		}
		long t2 = System.currentTimeMillis();
		if(t2-t1>10){
			mirrorLog.d("插入耗时："+(t2-t1)+" 表："+tab+" q_mirror："+q_mirror.size()+" noq_mirror："+noq_mirror.size()+"("+getClass().getName()+")");
		}
	}
	
	/**
	 * 回调镜像插入
	 */
	public void callbackMirrorInsert(JSONArray data){
		JSONObject colobj = DBUtil.colmap.optJSONObject(tab);
		int colid = data.optInt(colobj.optInt(col));
		if(colid != 0){
			HashMap<Integer, JSONArray> use_mirror = null;
			if(q_mirror.containsKey(colid)){
				use_mirror = q_mirror;
			} else {
				use_mirror = noq_mirror;
			}
			JSONArray unit_mirror = use_mirror.get(colid);
			if(unit_mirror == null){
				unit_mirror = new JSONArray();
				use_mirror.put(colid, unit_mirror);
			}
			unit_mirror.add(data);
		}
	}
	
	/**
	 * 删除
	 */
	public void delete(DBHelper dbHelper, int colid, String where) throws Exception {
		if(colid == 0){
			throw new RuntimeException("主要条件值不能为0");
		}
		if(where.indexOf(col) == -1){
			throw new RuntimeException("缺少主要条件 "+col);
		}
		long t1 = System.currentTimeMillis();
		synchronized (LockStor.getLock(LockStor.PLA_MIRROR, tab)) {
			checkHas(colid);
			JSONArray unit_mirror = q_mirror.get(colid);
			JSONArray delarr = DBUtil.jsonQuery(tab, unit_mirror, where, null, 0, 0);
			for(int i = 0; delarr != null && i < delarr.length(); i++){
				unit_mirror.remove(delarr.opt(i));
			}
			MirrorMgr.sendDeleteMessage(this, tab, delarr);
		}
		long t2 = System.currentTimeMillis();
		if(t2-t1>10){
			mirrorLog.d("删除耗时："+(t2-t1)+" 表："+tab+" q_mirror："+q_mirror.size()+" noq_mirror："+noq_mirror.size()+"("+getClass().getName()+")");
		}
		if(serverWhere){
			where += " and serverid="+Conf.sid;
		}
		dbHelper.delete(tab, where);
	}
	
	/**
	 * 镜像回调删除
	 */
	public void callbackMirrorDelete(JSONArray delarr){
		JSONObject colobj = DBUtil.colmap.optJSONObject(tab);
		for(int i = 0; delarr != null && i < delarr.length(); i++){
			JSONArray data = delarr.optJSONArray(i);
			int colid = data.optInt(colobj.optInt(col));
			if(colid != 0){
				HashMap<Integer, JSONArray> use_mirror = null;
				if(q_mirror.containsKey(colid)){
					use_mirror = q_mirror;
				} else {
					use_mirror = noq_mirror;
				}
				JSONArray unit_mirror = use_mirror.get(colid);
				if(unit_mirror != null){
					unit_mirror.remove(data);
				}		
			}
		}
	}
	
	/**
	 * 更新
	 */
	public void update(DBHelper dbHelper, int colid, SqlString sqlStr, String where) throws Exception {
		if(colid == 0){
			throw new RuntimeException("主要条件值不能为0");
		}
		if(where.indexOf(col) == -1){
			throw new RuntimeException("缺少主要条件 "+col);
		}
		long t1 = System.currentTimeMillis();
		synchronized (LockStor.getLock(LockStor.PLA_MIRROR, tab)) {
			checkHas(colid);
			JSONArray unit_mirror = q_mirror.get(colid);
			JSONObject colobj = DBUtil.colmap.optJSONObject(tab);
			JSONArray updarr = DBUtil.jsonQuery(tab, unit_mirror, where, null, 0, 0);
			boolean updcol = sqlStr.containCol(col);
			JSONArray new_unit_mirror = null;
			if(updcol){
				int new_colid = Tools.str2int(sqlStr.getColValue(col));
				if(new_colid != 0){
					HashMap<Integer, JSONArray> new_use_mirror = null;
					if(q_mirror.containsKey(new_colid)){
						new_use_mirror = q_mirror;
					} else {
						new_use_mirror = noq_mirror;
					}
					new_unit_mirror = new_use_mirror.get(new_colid);
					if(new_unit_mirror == null){
						new_unit_mirror = new JSONArray();
						new_use_mirror.put(new_colid, new_unit_mirror);
					}		
				}
			}
			MirrorMgr.sendUpdateMessage(this, tab, updarr, sqlStr);
			for(int i = 0; updarr != null && i < updarr.length(); i++){
				JSONArray data = updarr.optJSONArray(i);
				sqlStr.updateMirror(colobj, data);
				if(updcol){
					unit_mirror.remove(data);
					if(new_unit_mirror != null){
						new_unit_mirror.add(data);
					}
				}
			}
		}
		long t2 = System.currentTimeMillis();
		if(t2-t1>10){
			mirrorLog.d("更新耗时："+(t2-t1)+" 表："+tab+" q_mirror："+q_mirror.size()+" noq_mirror："+noq_mirror.size()+"("+getClass().getName()+")");
		}
		if(serverWhere){
			where += " and serverid="+Conf.sid;
		}
		dbHelper.update(tab, sqlStr, where);
	}
	
	/**
	 * 镜像回调更新
	 */
	public void callbackMirrorUpdate(JSONArray updarr, int new_colid){
		JSONObject colobj = DBUtil.colmap.optJSONObject(tab);
		JSONArray new_unit_mirror = null;
		if(new_colid != 0){
			HashMap<Integer, JSONArray> new_use_mirror = null;
			if(q_mirror.containsKey(new_colid)){
				new_use_mirror = q_mirror;
			} else {
				new_use_mirror = noq_mirror;
			}
			new_unit_mirror = new_use_mirror.get(new_colid);
			if(new_unit_mirror == null){
				new_unit_mirror = new JSONArray();
				new_use_mirror.put(new_colid, new_unit_mirror);
			}	
		}
		for(int i = 0; updarr != null && i < updarr.length(); i++){
			JSONArray data = updarr.optJSONArray(i);
			int colid = data.optInt(colobj.optInt(col));
			if(colid != 0){
				HashMap<Integer, JSONArray> use_mirror = null;
				if(q_mirror.containsKey(colid)){
					use_mirror = q_mirror;
				} else {
					use_mirror = noq_mirror;
				}
				JSONArray unit_mirror = use_mirror.get(colid);
				if(unit_mirror != null){
					unit_mirror.remove(data);
				}
			}
			if(new_unit_mirror != null){
				new_unit_mirror.add(data);
			}
		}
	}
	
	/**
	 * 查询
	 */
	public DBPsRs query(int colid, String where) throws Exception {
		return query(colid, where, null, 0, 0);
	}
	
	/**
	 * 查询
	 */
	public DBPsRs query(int colid, String where, String order) throws Exception {
		return query(colid, where, order, 0, 0);
	}
	
	/**
	 * 查询
	 */
	public DBPsRs query(int colid, String where, String order, int minRow, int maxRow) throws Exception {
		if(colid == 0){
			throw new RuntimeException("主要条件值不能为0");
		}
		if(where.indexOf(col) == -1){
			throw new RuntimeException("缺少主要条件 "+col);
		}
		long t1 = System.currentTimeMillis();
		JSONArray json = null;
		synchronized (LockStor.getLock(LockStor.PLA_MIRROR, tab)) {
			checkHas(colid);
			JSONArray unit_mirror = q_mirror.get(colid);
			json = DBUtil.jsonQuery(tab, unit_mirror, where, order, minRow, maxRow);
		}
		long t2 = System.currentTimeMillis();
		if(t2-t1>10){
			mirrorLog.d("查询耗时："+(t2-t1)+" 表："+tab+" q_mirror："+q_mirror.size()+" noq_mirror："+noq_mirror.size()+"("+getClass().getName()+")");
		}
		return new DBPsRs(tab, where, json);
	}
	
	/**
	 * 根据KEY删除
	 */
	public void deleteByKey(DBHelper dbHelper, int colid, int key) throws Exception {
		if(key_col == null){
			throw new RuntimeException("key_col is null");
		}
		delete(dbHelper, colid, col+"="+colid+" and "+key_col+"="+key);
	}
	
	/**
	 * 根据KEY更新
	 */
	public void updateByKey(DBHelper dbHelper, int colid, SqlString sqlStr, int key) throws Exception {
		if(key_col == null){
			throw new RuntimeException("key_col is null");
		}
		update(dbHelper, colid, sqlStr, col+"="+colid+" and "+key_col+"="+key);
	}
	
	/**
	 * 根据KEY获取数据集
	 */
	public DBPaRs getDataRsByKey(int colid, int key) throws Exception {
		if(key_col == null){
			throw new RuntimeException("key_col is null");
		}
		return new DBPaRs(query(colid, col+"="+colid+" and "+key_col+"="+key));
	}
}
