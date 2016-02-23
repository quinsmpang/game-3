package com.moonic.mgr;

import java.util.ArrayList;


/**
 * 事件中心
 * @author John
 */
public class EventCenter {
	private static ArrayList<EventListener> listeners = new ArrayList<EventListener>();
	
	/**
	 * 添加监听
	 */
	public static void addListener(EventListener listener){
		synchronized (listeners) {
			listeners.add(listener);		
		}
	}
	
	/**
	 * 移除监听
	 */
	public static void removeListener(EventListener listener){
		synchronized (listeners) {
			listeners.remove(listener);		
		}
	}
	
	/**
	 * 监听回调
	 */
	public static void send(byte type, Object... param){
		for(int i = 0; i < listeners.size(); i++){
			EventListener lis = listeners.get(i);
			if(lis != null){
				lis.callback(type, param);
			}
		}
	}
}
