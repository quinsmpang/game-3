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
	
	//-----------------自定义锁-----------------
	
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
