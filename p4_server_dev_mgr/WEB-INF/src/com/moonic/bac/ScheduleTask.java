package com.moonic.bac;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;

public class ScheduleTask
{
	public static final byte STATE_WAIT=0; //等待中
	public static final byte STATE_TIMER=1; //计时中
	public static final byte STATE_RUN=2; //执行中
	public static final byte STATE_COMPLETE=3; //已执行完毕
	
	private int id;
	private String name;
	private int type;
	private int theState; //0等待中 
	private ScheduledExecutorService timer;
	private MyTimerTask timerTask;
	private int executeTimes; //执行次数
	private long period; //执行间隔，毫秒为单位
	private Date startTime;
	
	public int getExecuteTimes()
	{
		return executeTimes;
	}
	public int getState()
	{
		return theState;
	}
	public int getId()
	{
		return id;
	}
	public String getName()
	{
		return name;
	}
	public int getType()
	{
		return type;
	}
	public String getStartTime()
	{
		return Tools.date2str(startTime);
	}
	public long getPeriod()
	{
		return period/1000;
	}
	public static String getTypeName(int type)
	{
		if(type==1) //关服
		{
			return "关服";
		}
		else
		if(type==2) //开服
		{
			return "开服";
		}
		else
		if(type==3) //执行SQL
		{
			return "执行SQL";
		}
		return "未定义";
	}
	
	/**
	 * 设置任务
	 * @param type 任务类型
	 * @param startTime 执行开始时间
	 * @param period 时间间隔(秒为单位)
	 * @param sql 执行的sql语句
	 * @return
	 */
	public boolean setTask(final int id,String name,int state,int type,Date startTime,final long period,final String sql,final String word1,final String word2,final String word3)
	{
		timerTask =null;
		if(type==1) //关服
		{
			this.id = id;
			this.name = name;
			this.type = type;
			this.theState = state;
			this.startTime = startTime;
			this.period = period * 1000;
			timerTask = new MyTimerTask()
			{					
				public void run2() 
				{
					System.out.println(Tools.getCurrentDateTimeStr()+"--"+"关服");
					
					if(word1!=null && !word1.equals(""))
					{
						System.out.println("设置下线语句："+word1);
						ServerBAC.getInstance().maintain(0, word2, word1, word3, 0);
					}
					else
					{
						ServerBAC.getInstance().maintain(0, word2, word1, word3, 0);
					}
					
					/*
					if(word2!=null && !word2.equals(""))
					{
						System.out.println("设置维护中语句："+word2);
						ServerBAC.getInstance().closeMainServerLogin(word2);
					}
					else
					{
						ServerBAC.getInstance().closeMainServerLogin("系统维护中");
					}
					*/
					
					if(period>0)
					{
						theState=STATE_RUN;								
					}
					else
					{
						theState=STATE_COMPLETE;	
						ScheduleBAC.getInstance().stopTaskById(id);
					}
					executeTimes++;
					updateTimes(id,executeTimes);
					//更新数据库中的状态
					updateState(id,theState);					
				}
			};	
			return true;
		}
		else
		if(type==2) //开服
		{
			this.id = id;
			this.name = name;
			this.type = type;
			this.theState = state;
			this.startTime = startTime;
			this.period = period * 1000;
			
			timerTask = new MyTimerTask()
			{					
				public void run2() {
					ServerBAC.getInstance().openGameServer(0);
					/*
					ServerBAC.getInstance().openMainServerLogin();
					*/
					System.out.println(Tools.getCurrentDateTimeStr()+"--"+"开服");
					if(period>0)
					{
						theState=STATE_RUN;								
					}
					else
					{
						theState=STATE_COMPLETE;	
						ScheduleBAC.getInstance().stopTaskById(id);
					}
					executeTimes++;
					updateTimes(id,executeTimes);
					//更新数据库中的状态
					updateState(id,theState);	
				}
			};	
			return true;
		}
		else
		if(type==3) //SQL
		{
			this.id = id;
			this.name = name;
			this.type = type;
			this.theState = state;
			this.startTime = startTime;
			this.period = period * 1000;
			
			timerTask = new MyTimerTask()
			{					
				public void run2() {
					DBHelper dbHelper = new DBHelper();
					try {
						dbHelper.execute(sql);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finally
					{
						dbHelper.closeConnection();
					}
					System.out.println(Tools.getCurrentDateTimeStr()+"--"+"执行SQL："+sql);
					if(period>0)
					{
						theState=STATE_RUN;								
					}
					else
					{
						theState=STATE_COMPLETE;
						ScheduleBAC.getInstance().stopTaskById(id);
					}
					executeTimes++;
					updateTimes(id,executeTimes);
					//更新数据库中的状态
					updateState(id,theState);	
				}
			};	
			return true;
		}
		return false;
	}
	/**
	 * 更新任务数据库中的状态
	 * @param taskId
	 * @param state
	 */
	public static void updateState(int taskId,int state)
	{
		//更新数据库中的状态
		DBHelper dbHelper = new DBHelper();
		SqlString sqlS = new SqlString();
		sqlS.add("state", state);
		try {
			dbHelper.update("TAB_SCHEDULE", sqlS, "id="+taskId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally					
		{
			dbHelper.closeConnection();
		}		
	}
	public static void updateTimes(int taskId,int times)
	{
		//更新数据库中的状态
		DBHelper dbHelper = new DBHelper();
		SqlString sqlS = new SqlString();
		sqlS.add("exectimes", times);
		try {
			dbHelper.update("TAB_SCHEDULE", sqlS, "id="+taskId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally					
		{
			dbHelper.closeConnection();
		}
	}

	/**
	 * 停止任务
	 */
	public ReturnValue stop()
	{
		if(theState!=STATE_RUN && theState!=STATE_TIMER)
		{
			return new ReturnValue(false,"只有计时中或执行中的任务才能停止");
		}
		
		if(timer!=null)
		{
			MyTools.cancelTimer(timer);
		}
		timer=null;
		theState=STATE_WAIT;
		executeTimes=0;		
		updateTimes(id,executeTimes);
		updateState(id, getState());
		return new ReturnValue(true,"计划任务["+name+"]停止成功");
	}
	/**
	 * 启动任务
	 * @param type
	 * @param startTime
	 * @param period
	 * @param sql
	 */
	public ReturnValue start()
	{
		if(theState!=STATE_WAIT && theState!=STATE_COMPLETE)
		{
			return new ReturnValue(false,"只有等待中或已完成的任务才能启动");
		}
		
		if(Tools.compareStrDate(Tools.getCurrentDateTimeStr(), Tools.date2str(startTime))>0) //当前早于开始时间
		{
			return new ReturnValue(false,"计划任务["+name+"]开始时间已过期");
		}
		
		if(timer!=null)
		{
			MyTools.cancelTimer(timer);			
		}				
		timer = MyTools.createTimer(3);
		executeTimes=0;
		
		if(period>0)
		{
			theState = STATE_TIMER;
			timer.scheduleAtFixedRate(timerTask, Math.max(startTime.getTime()-System.currentTimeMillis(), 0), period, TimeUnit.MILLISECONDS);
		}
		else
		{
			theState = STATE_TIMER;
			timer.schedule(timerTask, Math.max(startTime.getTime()-System.currentTimeMillis(), 0), TimeUnit.MILLISECONDS);
		}
		updateTimes(id,executeTimes);
		updateState(id, getState());
		
		return new ReturnValue(true,"计划任务["+name+"]启动成功");
	}
}
