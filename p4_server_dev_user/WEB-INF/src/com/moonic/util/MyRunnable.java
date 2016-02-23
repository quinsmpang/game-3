package com.moonic.util;

/**
 * ScheduledExecutorService执行的任务抽象类
 * @author huangyan
 *
 */
public abstract class MyRunnable implements Runnable
{
	private boolean allowExec = true; //取消任务控制变量
	public void cancel()
	{
		allowExec = false;
	}
	public boolean allowRun()
	{
		return allowExec;
	}
}
