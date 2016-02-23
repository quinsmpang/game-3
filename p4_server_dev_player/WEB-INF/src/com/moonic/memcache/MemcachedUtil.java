package com.moonic.memcache;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import com.moonic.util.MyLog;

import conf.Conf;

public final class MemcachedUtil {	
	private static AbstractMemcached memcached;
	// 默认cache失效时间 in second  60 * 60（秒为单位，1小时）
	private final static int cacheExpireTime = 60 * 60 * 8;
	// 默认get操作超时时间（毫秒为单位，3秒）
	private final static int defaultGetOpTimeout = 1000 * 3;
	static {
		
		try {			
			Properties props = PropertiesLoaderUtil.loadProperties("memcached.properties");
			if(props!=null)
			{
				String serverAddress = props.getProperty("address");
				memcached = AbstractMemcached.getInstance(serverAddress);
				// 状态监听器
				memcached.addStateListener(new MemcachedListener());
				memcached.init();
				
				if (memcached == null) {
					throw new NullPointerException("Null MemcachedClient,please check memcached has been started");
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		MemcachedUtil.set("test", "1111");
		Object obj = MemcachedUtil.get("test");
		System.out.println(obj.toString());
	}
	
	private static MyLog log = new MyLog(MyLog.NAME_DATE, "memcached", "MEMCACHED", true, false, true, null);
	
	/**
	 * 将key对应的Object存入缓存
	 * @param key
	 * @param value
	 */
	public static void set(String key, Object value) {
		memcached.set(key, value, cacheExpireTime);
		log.d("SET:"+key+"-"+value);
		if(Conf.gdout){
			log.save();
		}
	}
	
	/**
	 * @param key
	 * @param value
	 * @param expireTime 过期时间(秒为单位)
	 */
	public static void set(String key, Object value, int expireTime) {
		memcached.set(key, value, expireTime);
	}
	
	/**
	 * 自动增长key对应的数值
	 * @param key
	 */
	public static void incr(String key) {
		memcached.incr(key);
	}
	
	public static Object get(String key) {
		Object obj = memcached.get(key, defaultGetOpTimeout);
		log.d("GET:"+key+"-"+obj);
		if(Conf.gdout){
			log.save();
		}
		return obj;
	}
	
	public static Object get(String key, int opTimeout) {
		return memcached.get(key, opTimeout);
	}
	
	public static void delete(String key) {
		memcached.delete(key);
		log.d("DEL:"+key);
		if(Conf.gdout){
			log.save();
		}
	}
	
	public static void add(String key, Object value, Date expireTime) {
		memcached.add(key, value, expireTime);
	}
	
	public static void stop(){
		memcached.stop();
	}
}
