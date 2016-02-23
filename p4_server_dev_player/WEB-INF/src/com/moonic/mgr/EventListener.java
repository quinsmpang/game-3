package com.moonic.mgr;

/**
 * °ïÅÉ¼àÌı
 * @author John
 */
public interface EventListener {
	
	/**
	 * »Øµ÷
	 */
	public void callback(byte type, Object... param);
}
