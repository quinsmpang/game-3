package com.moonic.util;

/**
 * 缓存清理回调接口
 * @author John
 */
public interface DBPoolClearListener {
	
	/**
	 * 回调
	 * @param key 包含TABLE和TXT
	 */
	public void callback(String key);
}
