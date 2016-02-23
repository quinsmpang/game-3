package com.moonic.util;

import java.util.ArrayList;

/**
 * 耗时测试
 * @author John
 */
public class TimeTest {
	private ArrayList<Long> timeconsumes;
	private ArrayList<String> notes;
	
	private MyLog log;
	
	private boolean enabled;
	
	private boolean write;
	private boolean print;
	
	private long savetime;
	private long total_timeconsume;
	
	/**
	 * 构造
	 */
	public TimeTest(String dir, String tip, boolean enabled, boolean write, boolean print){
		timeconsumes = new ArrayList<Long>();
		notes = new ArrayList<String>();
		
		timeconsumes.add((long)0);
		notes.add(null);
		
		this.enabled = enabled;
		
		this.write = write;
		this.print = print;
		
		savetime = System.currentTimeMillis();
		
		log = new MyLog(MyLog.NAME_DATE, dir, tip, write, print, true, null);
	}
	
	/**
	 * 记录
	 */
	public void add(String note){
		if(enabled){
			long consume = System.currentTimeMillis()-savetime;
			if(notes.contains(note)){
				int ind = notes.indexOf(note);
				timeconsumes.set(ind, timeconsumes.get(ind)+consume);
			} else {
				timeconsumes.add(consume);
				notes.add(note);
			}
			total_timeconsume += consume;
			savetime = System.currentTimeMillis();		
		}
	}
	
	/**
	 * 存储
	 */
	public void save(long threshold){
		if(enabled && write){
			if(total_timeconsume >= threshold){
				StringBuffer sb = new StringBuffer();
				sb.append("\t");
				for(int i = 1; i < timeconsumes.size(); i++){
					sb.append(notes.get(i)+" ： "+timeconsumes.get(i));
					sb.append("\t");
				}
				sb.append("总耗时 ： " + total_timeconsume);
				log.d(sb.toString());
				log.save();
			}
		}
	}
	
	/**
	 * 输出
	 */
	public void print(){
		if(enabled && print){
			System.out.println("------------------------");
			for(int i = 1; i < timeconsumes.size(); i++){
				System.out.println(notes.get(i)+" ： "+timeconsumes.get(i));
			}
			System.out.println("总耗时 ： " + total_timeconsume);
			System.out.println("------------------------");
		}
	}
}
