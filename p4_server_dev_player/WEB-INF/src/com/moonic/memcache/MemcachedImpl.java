package com.moonic.memcache;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;


import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.MemcachedClientStateListener;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;


public final class MemcachedImpl extends AbstractMemcached {
	// default nio connection pool size 
	private final static int defaultPoolSize = 20;
	// default PrimitiveAsString false
	private final static boolean defaultPrimitiveAsString = true;
	// default Connect Time out
	private final static int defaultConnectTimeout = 1000 * 3;
	
	private MemcachedClientBuilder builder;
	private MemcachedClient memcachedClient;
	public MemcachedImpl(String serverAddress) {
		builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses(serverAddress));		
		builder.setConnectionPoolSize(getConnectPoolSize() == 0 ? defaultPoolSize : getConnectPoolSize());
	}

	/**
	 * 
	 * 
	 */
	public void init() {
		try {
			memcachedClient = builder.build();
			memcachedClient.setPrimitiveAsString(defaultPrimitiveAsString);
			memcachedClient.setConnectTimeout((getConnectTimeout() == 0) ? 
					defaultConnectTimeout : getConnectTimeout());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop(){
		try {
			memcachedClient.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addStateListener(MemcachedClientStateListener listener) {
		builder.addStateListener(listener);
	}

	public void set(String key, Object value, int expTime) {
		try {
			memcachedClient.set(key, expTime, value);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}

	public Object get(String key, int opTimeout) {
		try {
			Object value = memcachedClient.get(key);
			return value;
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void delete(String key) {
		try {
			memcachedClient.delete(key);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}

	public void incr(String key) {
		try {
			memcachedClient.incr(key, 1);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}
	
	public void add(String key, Object value, Date expiryDate) {
		try {
			memcachedClient.add(key, (int) expiryDate.getTime(), value);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}
}
