package com.moonic.util;

/**
 * 计时器任务
 * @author John
 */
public abstract class MyTimerTask implements Runnable {
	private boolean allowRun = true;
	
	public final void run() {
		try {//防止任务抛出异常导致后续任务被取消
			if(allowRun){
				run2();
			} 
			/*else {
				System.out.println("计时器已取消");
			}*/		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancel(){
		allowRun = false;
	}
	
	public abstract void run2();
}
