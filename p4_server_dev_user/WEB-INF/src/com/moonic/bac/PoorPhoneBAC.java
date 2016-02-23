package com.moonic.bac;

import java.sql.ResultSet;
import java.util.ArrayList;

import server.common.Tools;

import com.moonic.util.DBHelper;

public class PoorPhoneBAC {
	public static String tbName = "tab_poor_phone";

	/**
	 * 判断是否为低端机型
	 */
	public boolean isPoor(DBHelper dbHelper, String vendor, String model) throws Exception {
		if (vendor != null) {
			vendor = Tools.replace(vendor, "'", "");
			ArrayList<String> modellist = new ArrayList<String>();
			ResultSet poorRs = dbHelper.query(tbName, "phonemodel", "vendor='" + vendor + "'");
			while (poorRs.next()) {
				modellist.add(poorRs.getString("phonemodel"));
			}
			dbHelper.closeRs(poorRs);
			if (modellist.contains(null)) {// 此品牌下所有机型都为低端机型
				return true;
			}
			if (model != null) {
				model = Tools.replace(model, "'", "");
			}
			if (modellist.contains(model)) {// 找到此品牌下的指定的低端机型
				return true;
			}
		}
		return false;
	}

	// ------------------静态区----------------

	private static PoorPhoneBAC instance = new PoorPhoneBAC();

	public static PoorPhoneBAC getInstance() {
		return instance;
	}
}
