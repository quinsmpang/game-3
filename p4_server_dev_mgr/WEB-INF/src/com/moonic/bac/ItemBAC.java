package com.moonic.bac;

import com.moonic.util.BACException;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 物品BAC
 * @author John
 */
public class ItemBAC {
	public static final String tab_item_type = "tab_item_type";
	
	public static final byte TYPE_ALL = 0;
	public static final byte TYPE_PROP_CONSUME = 1;
	public static final byte TYPE_MATERIAL = 3;
	public static final byte TYPE_GIFT = 6;
	public static final byte TYPE_EQUIP_ORDINARY = 8;
	
	/**
	 * 获得物品列表数据集
	 * @param itemtype 物品类型
	 * @param itemnum 物品编号
	 */
	public DBPaRs getListRs(int itemtype, int itemnum) throws Exception{
		try {
			DBPaRs rs = DBPool.getInst().pQueryA(getTab(itemtype), "num="+itemnum);
			if(!rs.exist()){
				BACException.throwInstance("不存在的物品编号 itemtype="+itemtype+" itemnum="+itemnum);
			}
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取表名
	 */
	public String getTab(int itemtype) {
		try {
			DBPaRs typeRs = DBPool.getInst().pQueryA(tab_item_type, "itemtype="+itemtype);
			if(!typeRs.exist()){
				BACException.throwInstance("物品类型不存在 itemtype="+itemtype);
			}
			return typeRs.getString("tabname");		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//--------------静态区--------------
	
	private static ItemBAC instance = new ItemBAC();
	
	/**
	 * 获取实例
	 */
	public static ItemBAC getInstance(){
		return instance;
	}
}
