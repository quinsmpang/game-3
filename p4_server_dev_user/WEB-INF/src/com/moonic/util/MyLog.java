package com.moonic.util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.common.Tools;
import conf.Conf;

/**
 * 日志
 * @author John
 */
public class MyLog {
	public static final byte NAME_DATE = 1;//按日期分文件
	public static final byte NAME_CUSTOM = 3;//自定义
	
	private byte name_type;
	
	private String dir;
	private String tip;
	private boolean write;
	private boolean print;
	
	private boolean addtag;
	private String customname;
	
	private StringBuffer buffer;
	
	private long nextSaveTime;
	
	private String filenameSuffix;
	
	/**
	 * 构造
	 */
	public MyLog(byte name_type, String dir, String tip, boolean write, boolean print, boolean addtag, String customname){
		this.buffer = new StringBuffer();
		
		this.name_type = name_type;
		
		this.dir = dir;
		this.tip = tip;
		
		this.write = write;
		this.print = print;
		
		this.addtag = addtag;
		this.customname = customname;
		
		if(write){
			timer.schedule(new SaveTT(), MyTools.long_minu*5, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * 设置是否输出
	 */
	public void setPrint(boolean print) {
		this.print = print;
	}
	
	/**
	 * 写日志
	 */
	public void d(String str){
		write("D", str, false);
	}
	
	/**
	 * 写日志
	 */
	public void d(String str, boolean print_){
		write("D", str, print_);
	}
	
	/**
	 * 写日志
	 */
	public void e(String str){
		write("E", str, false);
	}
	
	private byte[] saveLock = new byte[0];
	
	/**
	 * 写日志
	 */
	private void write(String tag, String str, boolean print_){
		if(addtag){
			str = Out.get(str);	
		}
		if(write){
			synchronized (saveLock) {
				if(addtag){
					buffer.append(tag+" - ");
				}
				buffer.append(str+"\r\n");
				if(buffer.length() >= 10*1024){//最多缓存10K内存
					save(true);
				}		
			}
		}
		if(print || print_){
			if(addtag){
				System.out.println(tag+" - "+tip+" - "+str);		
			} else {
				System.out.println(str);
			}
		}
	}
	
	/**
	 * 提交存储
	 */
	public void save(){
		synchronized (saveLock) {
			save(false);
		}
	}
	
	/**
	 * 提交存储
	 */
	public void save(boolean autoCommit){
		if(write){
			FileUtil fileUtil = new FileUtil();
			String txtname = getTxtName();
			fileUtil.addToTxt(Conf.logRoot + dir + "/"+txtname+".txt", buffer.toString());
			buffer = new StringBuffer();
			nextSaveTime = System.currentTimeMillis()+MyTools.long_minu*5;
		}
	}
	
	/**
	 * 获取文件名
	 */
	private String getTxtName(){
		String txtname = null;
		if(name_type == NAME_DATE){
			txtname = Tools.getCurrentDateStr();
		} else 
		if(name_type == NAME_CUSTOM){
			txtname = customname;
		}
		if(filenameSuffix != null){
			txtname += "_"+filenameSuffix;
		}
		return txtname;
	}
	
	/**
	 * 设置文件名后缀
	 */
	public void setFilenameSuffix(String filenameSuffix) {
		this.filenameSuffix = filenameSuffix;
	}
	
	private static ScheduledExecutorService timer = MyTools.createTimer(3);
	
	/**
	 * 自动存储计时器
	 * @author John
	 */
	class SaveTT extends MyTimerTask {
		public void run2() {
			if(write){
				synchronized (saveLock) {
					if(System.currentTimeMillis() >= nextSaveTime && buffer.length() > 0){
						save(true);
					}
					//Out.println("日志“"+dir+"”执行自动存储");
				}
				timer.schedule(new SaveTT(), MyTools.long_minu*5, TimeUnit.MILLISECONDS);
			}
		}	
	}
}
