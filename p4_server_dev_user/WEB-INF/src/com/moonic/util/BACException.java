package com.moonic.util;

/**
 * 数据库操作异常
 * @author John
 */
public class BACException extends Exception {
	private static final long serialVersionUID = -6840493542701775332L;
	
	/**
	 * 构造
	 */
	public BACException(String message){
		super(message);
	}
	
	/**
	 * 异常打印(重写父类方法，不在控制台中输出异常信息)
	 */
	public void printStackTrace() {}
	
	/**
	 * 重写
	 */
	public String toString() {
		return super.getMessage();
	}

	//---------静态区---------
	
	/**
	 * 抛出异常对象
	 */
	public static void throwInstance(String message) throws BACException{
		throw new BACException(message);
	}
	
	/**
	 * 抛出异常对象并输出错误内容
	 */
	public static void throwAndOutInstance(String message) throws BACException{
		System.out.println("exception-"+message);
		throw new BACException(message);
	}
	
	/**
	 * 抛出异常对象并打印堆栈
	 */
	public static void throwAndPrintInstance(String message) throws Exception{
		throw new Exception(message);
	}
}
