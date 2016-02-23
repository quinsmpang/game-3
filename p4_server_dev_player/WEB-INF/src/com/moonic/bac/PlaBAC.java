package com.moonic.bac;

import com.moonic.mirror.MirrorMgr;
import com.moonic.mirror.MirrorOne;
import com.moonic.util.DBHelper;

/**
 * 角色数据抽象类
 * @author John
 */
public abstract class PlaBAC extends MirrorOne {
	
	/**
	 * 构造
	 */
	public PlaBAC(String tab, String col){
		super(tab, col);
		MirrorMgr.pla_mirrorobjTab.put(tab, this);
	}
	
	//------------------抽象区--------------------
	
	public abstract void init(DBHelper dbHelper, int playerid, Object... parm) throws Exception;
}
