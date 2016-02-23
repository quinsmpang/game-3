package com.moonic.bac;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.DBHelper;

public class ChargeSendBAC {
	public static String tbName = "TAB_CHARGE_SEND";
	
	/**
	 * 生成发货单
	 */
	public ReturnValue createSendOrder(int serverId, String channel, String orderNo, int result) {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			// 查询已有的单子
			boolean haveExist = dbHelper.queryExist("tab_charge_send", "serverId=" + serverId + " and channel='" + channel + "' and orderNo='" + orderNo + "'");
			if (haveExist) {
				SqlString sqlS = new SqlString();
				sqlS.add("result", result);
				dbHelper.update("tab_charge_send", sqlS, "serverId=" + serverId + " and channel='" + channel + "' and orderNo='" + orderNo + "'");
			} else {
				SqlString sqlS = new SqlString();
				sqlS.add("serverId", serverId);
				sqlS.add("channel", channel);
				sqlS.add("orderNo", orderNo);
				sqlS.add("result", result);
				sqlS.addDateTime("savetime", Tools.getCurrentDateTimeStr());
				dbHelper.insert("tab_charge_send", sqlS);
			}
			if (result == 1 || result == -1) {
				// 更新主订单
				SqlString sqlS = new SqlString();
				sqlS.add("gived", result);
				dbHelper.update("tab_charge_order", sqlS, "serverId=" + serverId + " and channel='" + channel + "' and orderNo='" + orderNo + "'");
			}
			return new ReturnValue(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ReturnValue(false, ex.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}

	//----------------静态区------------------
	
	private static ChargeSendBAC self = new ChargeSendBAC();

	public static ChargeSendBAC getInstance() {
		return self;
	}
}
