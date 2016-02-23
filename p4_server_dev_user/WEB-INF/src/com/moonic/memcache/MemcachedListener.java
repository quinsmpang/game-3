package com.moonic.memcache;

import java.net.InetSocketAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientStateListener;

public class MemcachedListener implements MemcachedClientStateListener {
	private transient static Log logger = LogFactory.getLog(MemcachedListener.class);
	
	public void onStarted(MemcachedClient memcachedClient) {
//		logger.info(memcachedClient.getName() + " has started.");
	}

	public void onShutDown(MemcachedClient memcachedClient) {
//		logger.info(memcachedClient.getName() + " has shutdowned.");
	}

	public void onConnected(MemcachedClient memcachedClient,
			InetSocketAddress inetSocketAddress) {		
//		logger.info("Connect to " + inetSocketAddress);		
	}

	public void onDisconnected(MemcachedClient memcachedClient,
			InetSocketAddress inetSocketAddress) {
//		logger.info("Disconnect to " + inetSocketAddress);
	}

	public void onException(MemcachedClient memcachedClient, Throwable throwable) {
		logger.error(memcachedClient.getName() + " exception occured.", throwable);
	}

}
