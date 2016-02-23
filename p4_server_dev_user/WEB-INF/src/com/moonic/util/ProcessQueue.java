package com.moonic.util;

import java.util.LinkedList;

/**
 * 处理队列
 * @author John
 */
public class ProcessQueue {
	private LinkedList<ProcessQueueTask> taskQueue = new LinkedList<ProcessQueueTask>();
	private Processer processer;
	private boolean isRun;
	private long dalay;
	
	private static boolean PQ_RUN_STATE = true;
	
	/**
	 * 设置所有处理队列的运行状态
	 */
	public static void setRunState(boolean state){
		PQ_RUN_STATE = state;
	}
	
	/**
	 * 构造(是默认的0秒休眠时间)
	 */
	public ProcessQueue() {
		this(0);
	}
	
	/**
	 * 构造
	 * @param dalay 处理完每个请求的休眠时间
	 */
	public ProcessQueue(int dalay) {
		isRun = true;
		processer = new Processer();
		processer.start();
	}
	
	/**
	 * 线程
	 * @author John
	 */
	class Processer extends Thread {
		public void run() {
			while(PQ_RUN_STATE && isRun){
				while(taskQueue.size() == 0){
					try {
						Thread.sleep(100);
						if(!(isRun && PQ_RUN_STATE)){
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				ProcessQueueTask task = taskQueue.get(0);
				task.execute();
				removeTask();
				try {
					Thread.sleep(dalay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}	
	}
	
	/**
	 * 获取当前队列长度
	 */
	public int getQueueSize(){
		return taskQueue.size();
	}
	
	/**
	 * 停止
	 */
	public void stop(){
		isRun = false;
	}
	
	/**
	 * 加入任务
	 */
	public void addTask(ProcessQueueTask task){
		synchronized (this) {
			taskQueue.offer(task);
		}
	}
	
	/**
	 * 移除任务
	 */
	public void removeTask(){
		synchronized (this) {
			taskQueue.poll();
		}
	}
}
