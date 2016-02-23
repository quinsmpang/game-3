package com.moonic.bac;

import org.json.JSONArray;

import com.ehc.common.ReturnValue;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;

/**
 * 系统公告
 * @author John
 */
public class SysNoticeBAC {
	public static String tab_sys_notice = "tab_sys_notice";
	
	/**
	 * 获取系统公告
	 */
	public ReturnValue getSysNotice(String channel, long tagtime){
		try {
			JSONArray noticearr = new JSONArray();
			DBPsRs noticeRs = DBPool.getInst().pQueryS(tab_sys_notice);
			while(noticeRs.next()){
				long currtime = System.currentTimeMillis();
				long createtime = noticeRs.getTime("createtime");
				long starttime = noticeRs.getTime("starttime");
				long overtime = noticeRs.getTime("overtime");
				String noticechannel = noticeRs.getString("channel");
				if(createtime>tagtime && starttime<=currtime && overtime>=currtime && (noticechannel.equals("0") || noticechannel.contains(channel))){
					JSONArray arr = new JSONArray();
					arr.add(noticeRs.getInt("id"));
					arr.add(noticeRs.getString("title"));
					arr.add(noticeRs.getString("content"));
					arr.add(starttime);
					arr.add(overtime);
					noticearr.add(arr);
				}
			}
			JSONArray jsonarr = new JSONArray();
			jsonarr.add(noticearr);
			jsonarr.add(System.currentTimeMillis());
			return new ReturnValue(true, jsonarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	//--------------静态区--------------
	
	private static SysNoticeBAC instance = new SysNoticeBAC();
	
	public static SysNoticeBAC getInstance() {
		return instance;
	}
}
