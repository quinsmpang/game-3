package com.moonic.util;


/**
 * 联网回调监听者
 * @author John
 */
public interface NetListener 
{
	
	/**
	 * 返回成功数据
	 */
	public static final byte RESULT_SUCCESS = 0;
	/**
	 * 返回失败数据
	 */
	public static final byte RESULT_FAIL = 1;
	/**
	 * 用户拒绝联网
	 */
	public static final byte RESULT_ACCESSDENIED = 2;
	/**
	 * 联网失败
	 */
	public static final byte RESULT_NETFAILURE = 3;
	/**
	 * 其他异常
	 */
	public static final byte RESULT_OTHERERROR = 4;
	
	/**
	 * 请求回调
	 * @param act 动作类型
	 * @param result 返回结果
	 * @param strData 返回字符串信息
	 */
	public abstract void callBack(int act, int result, String strData);
	public abstract void callBack(int act, int result, byte[] strData);
}
