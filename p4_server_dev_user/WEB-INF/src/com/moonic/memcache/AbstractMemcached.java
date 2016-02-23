package com.moonic.memcache;

import java.util.Date;

import net.rubyeye.xmemcached.MemcachedClientStateListener;

public abstract class AbstractMemcached {

	private int connectPoolSize;        
	private boolean primitiveAsString;
	private int connectTimeout;
	
	/**
	 * 实例化memcached对象
	 * @param serverAddress
	 * @return
	 */
	public static AbstractMemcached getInstance(String serverAddress) {
		return new MemcachedImpl(serverAddress);
	}
	
	/**
	 * 添加Memcached监听器
	 * @param listener
	 */
	public abstract void addStateListener(MemcachedClientStateListener listener);
	
	/**
	 * memcache初始化
	 * 
	 */
	public abstract void init();
	
	/**
	 * 停止
	 */
	public abstract void stop();
	
	/**
	 * 设置memcache缓存
	 * @param key
	 * @param value
	 * @param expTime
	 */
	public abstract void set(String key, Object value, int expTime);
	
	/**
	 * 获取memcached缓存信息
	 * @param key
	 * @param opTimeout
	 * @return
	 */
	public abstract Object get(String key, int opTimeout);
	
	/**
	 * 删除memcached缓存信息
	 * @param key
	 */
	public abstract void delete(String key);
	
	/**
	 * 自增Memcached值
	 * @param key
	 */
	public abstract void incr(String key);
	
	public abstract void add(String key, Object value, Date expiryDate);
	
	public int getConnectPoolSize() {
		return connectPoolSize;
	}

	public void setConnectPoolSize(int connectPoolSize) {
		this.connectPoolSize = connectPoolSize;
	}

	public boolean isPrimitiveAsString() {
		return primitiveAsString;
	}

	public void setPrimitiveAsString(boolean primitiveAsString) {
		this.primitiveAsString = primitiveAsString;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
}
