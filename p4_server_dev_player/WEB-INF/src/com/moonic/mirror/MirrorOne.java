package com.moonic.mirror;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

/**
 * 镜像抽象(一对一关系)
 * @author John
 */
public class MirrorOne extends Mirror {
	public boolean needcheck = true;
	
	/**
	 * 构造
	 */
	public MirrorOne(String tab, String col){
		super(tab, col, null);
	}
	
	/**
	 * 调试改变值
	 */
	public ReturnValue debugChangeValue(int colid, String column, long value, long min, long max, String logname){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			long srcval = getLongValue(colid, column);
			if(srcval+value>max){
				BACException.throwInstance("不可超出最大值 "+max);
			} else 
			if(srcval+value<min){
				BACException.throwInstance("不可小于最小值 "+min);
			}
			SqlString sqlStr = new SqlString();
			sqlStr.addChange(column, value);
			update(dbHelper, colid, sqlStr);
			
			GameLog.getInst(colid, GameServlet.ACT_DEBUG_GAME_LOG)
			.addChaNote(logname, srcval, value)
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试设置日期
	 */
	public ReturnValue debugSetTime(int colid, String column, String timeStr, String logname){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			GameLog gl = GameLog.getInst(colid, GameServlet.ACT_DEBUG_GAME_LOG);
			setTime(dbHelper, colid, column, timeStr, gl, logname);
			
			gl.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 加值
	 */
	public void addValue(DBHelper dbHelper, int colid, String column, long addVal, GameLog gl, String logname) throws Exception{
		if(addVal <= 0){
			String str = "增加值失败 修改项：" + column + " 增加值：" + addVal + "("+colid+")";
			try {
				throw new Exception(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			BACException.throwAndPrintInstance(str);
		}
		DBPaRs rs = getDataRs(colid);
		long srcVal = rs.getLong(column);
		SqlString sqlStr = new SqlString();
		sqlStr.addChange(column, addVal);
		update(dbHelper, colid, sqlStr);
		gl.addChaNote(logname, srcVal, addVal);
	}
	
	/**
	 * 减值
	 */
	public void subValue(DBHelper dbHelper, int colid, String column, long subVal, GameLog gl, String logname) throws Exception{
		if(subVal <= 0){
			String str = "减少值失败 修改项：" + column + " 减少值：" + subVal + "("+colid+")";
			try {
				throw new Exception(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			BACException.throwInstance(str);
		}
		DBPaRs rs = getDataRs(colid);
		long srcVal = rs.getLong(column);
		if(srcVal < subVal){
			BACException.throwAndPrintInstance(column + "不足" + "("+colid+":"+subVal+"/"+srcVal+")");
		}
		SqlString sqlStr = new SqlString();
		sqlStr.addChange(column, -subVal);
		update(dbHelper, colid, sqlStr);
		subTrigger(dbHelper, colid, column, srcVal, srcVal-subVal);
		gl.addChaNote(logname, srcVal, -subVal);
	}
	
	/**
	 * 减值触发器(回调方法，调用subValue时将回调此方法，重写此方法实现自身需求)
	 */
	public void subTrigger(DBHelper dbHelper, int colid, String col, long srcVal, long nowVal) throws Exception {}
	
	/**
	 * 设置时间
	 */
	public void setTime(DBHelper dbHelper, int colid, String column, String timeStr, GameLog gl, String logname) throws Exception{
		SqlString sqlStr = new SqlString();
		sqlStr.addDateTime(column, MyTools.getTimeStr(MyTools.getTimeLong(timeStr)));
		update(dbHelper, colid, sqlStr);
		gl.addRemark(logname+" 设置为 "+timeStr);
	}
	
	/**
	 * 设置值
	 */
	public void setValue(DBHelper dbHelper, int colid, String column, long value, GameLog gl, String logname) throws Exception{
		SqlString sqlStr = new SqlString();
		sqlStr.add(column, value);
		update(dbHelper, colid, sqlStr);
		gl.addRemark(logname+" 设置为 "+value);
	}
	
	/**
	 * 设置值
	 */
	public void setValue(DBHelper dbHelper, int colid, String column, String value, GameLog gl, String logname) throws Exception{
		SqlString sqlStr = new SqlString();
		sqlStr.add(column, value);
		update(dbHelper, colid, sqlStr);
		gl.addRemark(logname+" 设置为 "+value);
	}
	
	/**
	 * 获取INT值
	 */
	public int getIntValue(int colid, String column) throws Exception{
		return Tools.str2int(getStrValue(colid, column));
	}
	
	/**
	 * 获取LONG值
	 */
	public long getLongValue(int colid, String column) throws Exception{
		return Tools.str2long(getStrValue(colid, column));
	}
	
	/**
	 * 获取STRING值
	 */
	public String getStrValue(int colid, String column) throws Exception{
		DBPaRs rs = getDataRs(colid);
		String value = rs.getString(column);
		return value;
	}
	
	/**
	 * 更新
	 */
	public void update(DBHelper dbHelper, int colid, SqlString sqlStr) throws Exception {
		update(dbHelper, colid, sqlStr, col+"="+colid);
	}
	
	/**
	 * 获取数据集
	 */
	public DBPaRs getDataRs(int colid) throws Exception {
		DBPsRs rs = query(colid, col+"="+colid);
		if(needcheck && rs.count()<=0){
			BACException.throwAndPrintInstance("获取数据集异常" + " TAB:" + tab + " WHERE:" + col + "=" + colid);
		}
		return new DBPaRs(rs);
	}
}
