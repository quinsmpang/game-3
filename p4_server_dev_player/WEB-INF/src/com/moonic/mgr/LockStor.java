package com.moonic.mgr;

import java.util.HashMap;

import com.moonic.util.Out;

/**
 * 锁仓库
 * @author John
 */
public class LockStor {
	private static final HashMap<String, byte[]> stor = new HashMap<String, byte[]>(131072);
	
	/**
	 * 锁生成锁
	 */
	public static final byte[] LOCK = new byte[0];
	
	//-----------------系统锁-----------------
	
	/**
	 * 数据库列表缓存
	 */
	public static final short DB_POOL_TAB = 101;
	/**
	 * 数据库文本缓存
	 */
	public static final short DB_POOL_TXT = 102;
	/**
	 * 随机数
	 */
	public static final short RANDOM_NEXT = 103;
	/**
	 * 随机数种子时间
	 */
	public static final short RANDOM_TIME = 104;
	/**
	 * 存日志库日志
	 */
	public static final short LOG_SAVE = 105;
	/**
	 * 恢复日志存储计时器
	 */
	public static final short LOG_EXC_RECOVER = 106;
	/**
	 * 角色镜像锁
	 */
	public static final short PLA_MIRROR = 108;
	/**
	 * 推送锁
	 */
	public static final short PUSH_LOCK = 109;
	/**
	 * 流量锁
	 */
	public static final short FLOW_LOCK = 110;
	
	//-----------------自定义锁-----------------
	
	/**
	 * 创建角色
	 */
	public static final short PLAYER_NAME = 201;
	/**
	 * 重置帮派当日数据
	 */
	public static final short FACTION_RESET_DAYDATA = 202;
	/**
	 * 帮派名
	 */
	public static final short FACTION_NAME = 203;
	/**
	 * 帮派加经验
	 */
	public static final short FACTION_ADDMONEY = 204;
	/**
	 * 帮派排行
	 */
	public static final short FACTION_RAKNING = 205;
	/**
	 * 帮派锁
	 */
	public static final short FACTION_MEMBER = 206;
	/**
	 * 推广员
	 */
	public static final short EXTENSION_AGENT = 207;
	/**
	 * 竞技场最大排名
	 */
	public static final short JJC_MAX_RANKING = 208;
	/**
	 * 竞技场挑战
	 */
	public static final short JJC_BATTLE = 209;
	/**
	 * 重置玩家当日数据
	 */
	public static final short PLAYER_RESET_DAYDATE = 210;
	/**
	 * 全服系统邮件
	 */
	public static final short SMAIL_INSERT = 211;
	/**
	 * 国战宣战
	 */
	public static final short CB_DECLAREWAR = 212;
	/**
	 * 国战首席
	 */
	public static final short CB_LEADER = 213;
	/**
	 * 帮派副本
	 */
	public static final short FAC_COPYMAP = 214;
	/**
	 * 挖矿
	 */
	public static final short MINERALS = 215;
	/**
	 * 好友赠送体力
	 */
	public static final short FRIEND_PRESENT = 216;
	/**
	 * 组队活动
	 */
	public static final short TEAM_ACTI = 217;
	
	/**
	 * 获取锁
	 */
	public static byte[] getLock(short lockname, Object... keys){
		StringBuffer sb = new StringBuffer();
		sb.append(lockname);
		for(int i = 0; i < keys.length; i++){
			sb.append("0");
			sb.append(keys[i]);
		}
		String str = sb.toString();
		synchronized (LOCK) {
			long t1 = System.currentTimeMillis();
			byte[] lock = stor.get(str);
			if(lock == null){
				if(stor.size() >= 98000){
					stor.clear();
				}
				lock = new byte[0];
				stor.put(str, lock);
			}
			long t2 = System.currentTimeMillis();
			if(t2-t1>5){
				Out.println("getLock 获取"+sb.toString()+"用时："+(t2-t1)+" len:"+stor.size());
			}
			return lock;
		}
	}
}
