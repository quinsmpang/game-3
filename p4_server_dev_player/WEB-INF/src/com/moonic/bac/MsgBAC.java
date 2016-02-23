package com.moonic.bac;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.socket.Player;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.Conf;
import conf.LogTbName;

/**
 * 消息BAC
 * @author John
 */
public class MsgBAC {
	public static final String tab_ensitive_words = "tab_ensitive_words";
	public static final String tab_game_push_msg = "tab_game_push_msg";
	
	public static final String[] CHA_NAME = {"玩家", "帮派", "密语", "系统", "顶部", "世界"};
	
	/**
	 * 玩家
	 */
	public static final byte CHA_PLAYER = 1;
	/**
	 * 帮派
	 */
	public static final byte CHA_FACTION = 2;
	/**
	 * 私语
	 */
	public static final byte CHA_PRIVATE = 3;
	/**
	 * 系统
	 */
	public static final byte CHA_SYSTEM = 4;
	/**
	 * 顶部
	 */
	public static final byte CHA_TOP = 5;
	/**
	 * 世界(客户端预留，按客户端需求将多个频道的消息显示在世界)
	 */
	public static final byte CHA_WORLD = 6;
	
	/**
	 * 发言频道
	 */
	public static final byte[] SEND_CHANNEL = 
		{
		CHA_PLAYER, CHA_FACTION, CHA_PRIVATE
		};
	
	/**
	 * 发送频道信息
	 */
	public ReturnValue sendchannelMsg(Player pla, byte channel, int friendid, int type, String content, byte voiceSecond, int voiceLen, byte[] voiceData, short act){
		DBHelper dbHelper = new DBHelper();
		try {
			if(BannedMacBAC.getInstance().isBannedMac(pla.mac, pla.imei)) {
				return new ReturnValue(true);
			}
			//System.out.println("channel:"+channel);
			if(!Tools.intArrContain(SEND_CHANNEL, channel)){
				BACException.throwInstance("错误的频道");
			}
			if(channel == CHA_PRIVATE){
				if(friendid == 0){
					BACException.throwInstance("发送密语失败，未指定密语对象");		
				} else 
				if(SocketServer.getInstance().plamap.get(friendid) == null){
					BACException.throwInstance("发送密语失败，对方已下线");
				}
			}
			
			int factionid = 0;
			if(channel == CHA_FACTION){
				factionid = PlaFacBAC.getInstance().getIntValue(pla.pid, "factionid");
				if(factionid == 0){
					BACException.throwInstance("尚未加入帮派");
				}
			}
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(pla.pid);
			long bannedmsgtime = plaRs.getTime("bannedmsgtime");
			if(!MyTools.checkSysTimeBeyondSqlDate(bannedmsgtime)){
				BACException.throwInstance("被禁言到："+MyTools.formatTime(bannedmsgtime, "yyyy年MM月dd日 HH:mm:ss"));
			}
			if(!MyTools.checkSysTimeBeyondSqlDate(pla.lastmsgtime+5000)) {
				BACException.throwInstance("发言过快");
			}
			JSONArray contentarr = new JSONArray();
			if(type == 1){
				if(content.length()==0){
					BACException.throwInstance("发送失败，消息内容为空");
				}
				if(content.length() > 100){
					BACException.throwInstance("发送失败，消息内容过长");
				}
				DBPsRs ensitiveRs = DBPool.getInst().pQueryS(tab_ensitive_words);
				while(ensitiveRs.next()){
					String str = ensitiveRs.getString("word");
					if(content.toUpperCase().indexOf(str) != -1){
						if(ensitiveRs.getInt("processtype") == 1){
							content = content.replaceAll("(?i)"+str, "**");		
						} else 
						if(ensitiveRs.getInt("processtype") == 2){
							return new ReturnValue(true);
						}
					}
				}
				contentarr.add(content);//字符串内容
			} else 
			if(type == 2){
				if(voiceData == null){
					BACException.throwInstance("发送失败，语音数据为空");
				}
				if(voiceData.length > 300 * 1024){
					BACException.throwInstance("语音内容过多");
				}
				if(voiceLen != voiceData.length){
					BACException.throwInstance("语音数据异常");
				}
				String filename = VoiceBAC.getInstance().uploadVoice(plaRs.getInt("vsid"), voiceData);
				contentarr.add(filename);//语音文件名
				contentarr.add(voiceSecond);//语音时长(秒)
			}
			DBPaRs plaroleRs = PlaRoleBAC.getInstance().getDataRs(pla.pid);
			JSONArray pusharr = new JSONArray();
			pusharr.add(channel);					//渠道
			pusharr.add(type);						//消息类型
			pusharr.add(contentarr);				//消息内容
			pusharr.add(pla.pid);					//玩家ID
			pusharr.add(plaRs.getString("name"));	//玩家名
			pusharr.add(plaRs.getInt("vip"));		//玩家VIP等级
			pusharr.add(plaRs.getInt("num"));		//头像编号
			pusharr.add(plaRs.getInt("lv"));		//等级
			pusharr.add(plaroleRs.getInt("totalbattlepower"));		//总战力
			JSONArray returnarr = null;
			if(channel == CHA_PLAYER){
				PushData.getInstance().sendPlaToNosOL(SocketServer.ACT_MESSAGE_RECEIVE, pusharr.toString(), pla.pid);
				pla.lastmsgtime = System.currentTimeMillis();//5秒限制
			} else 
			if(channel == CHA_FACTION){
				PushData.getInstance().sendPlaToFacMem(SocketServer.ACT_MESSAGE_RECEIVE, pusharr.toString(), factionid, pla.pid);
				pla.lastmsgtime = System.currentTimeMillis()-3000;//2秒限制
			} else 
			if(channel == CHA_PRIVATE){
				PushData.getInstance().sendPlaToOne(SocketServer.ACT_MESSAGE_RECEIVE, pusharr.toString(), friendid);
				pla.lastmsgtime = System.currentTimeMillis()-4500;//1.5秒限制
			}
			SqlString sqlStr = new SqlString();
			sqlStr.add("serverid", Conf.sid);
			sqlStr.add("playerid", pla.pid);
			sqlStr.add("channel", channel);
			sqlStr.add("friendid", friendid);
			sqlStr.add("factionid", factionid);
			sqlStr.add("type", type);
			sqlStr.add("content", contentarr.toString());
			sqlStr.addDateTime("savetime", MyTools.getTimeStr());
			DBHelper.logInsert(LogTbName.TAB_MSG_LOG(), sqlStr);
			
			return new ReturnValue(true, returnarr!=null?returnarr.toString():null);
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 开关聊天
	 */
	public ReturnValue setReceiveGamePush(Player pla, boolean open){
		pla.conf_receive_game_push = open;
		return new ReturnValue(true);
	}
	
	/**
	 * 发送登录问候语
	 */
	public void sendLoginSysMsg(int playerid){
		JSONArray pusharr = getSysMsgBag(ConfigBAC.getString("login_sys_msg"));
		PushData.getInstance().setSysMsg().sendPlaToOne(SocketServer.ACT_MESSAGE_RECEIVE, pusharr.toString(), playerid);
	}
	
	/**
	 * 获取系统信息包
	 */
	public JSONArray getSysMsgBag(String msg){
		JSONArray contentarr = new JSONArray();
		contentarr.add(msg);
		JSONArray msgarr = new JSONArray();
		msgarr.add(CHA_SYSTEM);
		msgarr.add(1);
		msgarr.add(contentarr);
		return msgarr;
	}
	
	//--------------静态区--------------
	
	private static MsgBAC instance = new MsgBAC();
	
	/**
	 * 获取实例
	 */
	public static MsgBAC getInstance(){
		return instance;
	}
}
