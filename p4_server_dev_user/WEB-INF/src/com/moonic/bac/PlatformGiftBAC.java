package com.moonic.bac;

import org.json.JSONArray;

import com.ehc.common.ReturnValue;
import com.moonic.servlet.STSServlet;
import com.moonic.util.BACException;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.NetResult;
import com.moonic.util.STSNetSender;



public class PlatformGiftBAC {	
	public static String tab_platform_gift = "tab_platform_gift";	 
	
	/**
	 * WEB获取礼包列表
	 */
	public ReturnValue webGetGiftList(){
		try {
			DBPsRs giftRs = DBPool.getInst().pQueryS(PlatformGiftBAC.tab_platform_gift, "pub=1", "num");
			JSONArray returnarr = new JSONArray();
			while(giftRs.next()){
				JSONArray arr = new JSONArray();
				arr.add(giftRs.getInt("num"));
				arr.add(giftRs.getString("name"));
				returnarr.add(arr);
			}
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	/**
	 * WEB领取礼包
	 */
	public ReturnValue webGetPlatformGift(int playerid, int vsid, String num){
		try {
			DBPsRs channelServerRs = ServerBAC.getInstance().getChannelServer("001", vsid);
			if(!channelServerRs.next()){
				BACException.throwInstance("服务器未找到 visd="+vsid);
			}
			STSNetSender sender = new STSNetSender(STSServlet.G_WEB_GET_PLATFORMGIFT);
			sender.dos.writeInt(playerid);
			sender.dos.writeUTF(num);
			NetResult nr = ServerBAC.getInstance().sendReqToOne(sender, channelServerRs.getInt("serverid"));
			return nr.rv;
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	//--------------静态区--------------
	
	private static PlatformGiftBAC self = new PlatformGiftBAC();	 
	  		
	public static PlatformGiftBAC getInstance() {						
		return self;
	}
}
