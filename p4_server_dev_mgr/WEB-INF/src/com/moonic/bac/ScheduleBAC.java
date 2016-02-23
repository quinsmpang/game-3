package com.moonic.bac;

import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.SmartUpload;
import com.moonic.util.DBHelper;


public class ScheduleBAC extends BaseActCtrl
{		
	
	public static String tbName = "tab_schedule";	 
	private static ScheduleBAC self;	 
	
	private static Vector<ScheduleTask> taskVC;
	  		
		public static ScheduleBAC getInstance()
		{						
			if(self==null)
			{
				self = new ScheduleBAC();
			}
			if(taskVC==null)
			{
				taskVC = new Vector<ScheduleTask>();
			}
			return self;
		}
		public ScheduleBAC()
		{			
			super.setTbName(tbName);
			setDataBase(ServerConfig.getDataBase());
		}

		public ReturnValue save(PageContext pageContext)
		{
			SmartUpload smartUpload = new SmartUpload();
			smartUpload.setEncode("UTF-8");
			try {
				smartUpload.initialize(pageContext);
				smartUpload.upload();
				com.jspsmart.upload.Request request = smartUpload.getRequest();				
				
				int id=Tools.str2int(request.getParameter("id"));
				String name=request.getParameter("name");
				int type=Tools.str2int(request.getParameter("type"));
				String starttime=request.getParameter("starttime");
				int period=Tools.str2int(request.getParameter("period"));
				String sql=request.getParameter("sql");
				String savetime=request.getParameter("savetime");
				int state=Tools.str2int(request.getParameter("state"));
				String word1=request.getParameter("word1");
				String word2=request.getParameter("word2");
				String word3=request.getParameter("word3");
				
				FormXML formXML = new FormXML();
				formXML.add("name",name);
				formXML.add("type",type);
				formXML.addDate("starttime",starttime);
				formXML.add("period",period);
				formXML.add("sql",sql);
				formXML.add("state",state);
				formXML.addDate("savetime",savetime);
				formXML.add("word1",word1);
				formXML.add("word2",word2);
				formXML.add("word3",word3);
				
				if(id>0)  //修改
				{	
					formXML.setAction(FormXML.ACTION_UPDATE);
					formXML.setWhereClause("id=" + id);
					ReturnValue rv = save(formXML);	
					if(rv.success)
					{
					  return new ReturnValue(true,"修改成功");
					}else
					{
					  return new ReturnValue(false,"修改失败");
					}					
				}else  //添加
				{
					formXML.setAction(FormXML.ACTION_INSERT);
					ReturnValue rv =save(formXML);
					if(rv.success)
					{
					  return new ReturnValue(true,"保存成功");
					}else
					{
					  return new ReturnValue(false,"保存失败");
					}
				}
			}
			catch (Exception e) 
			{			
				e.printStackTrace();
				return new ReturnValue(false,e.getMessage());
			} 		
		}
		public ReturnValue del(PageContext pageContext)
		{	
			ServletRequest req = pageContext.getRequest();
			int id = Tools.str2int(req.getParameter("id"));
			stopTaskById(id);
			ReturnValue rv = super.del("id="+ id);			
			//todo 删除其他关联表中的记录
			return rv;
		}
		/**
		 * 停止全部任务
		 */
		public void stopAllTask()
		{
			for(int i=0;taskVC!=null && i<taskVC.size();i++)
			{
				ScheduleTask task = taskVC.elementAt(i);
				task.stop();
			}
			if(taskVC!=null)
			{
				taskVC.clear();	
			}			
		}
		/**
		 * 获取计时任务对象
		 * @param taskId 计时任务id
		 * @return
		 */
		public ScheduleTask getTaskById(int taskId)
		{
			for(int i=0;taskVC!=null && i<taskVC.size();i++)
			{
				ScheduleTask task = taskVC.elementAt(i);
				if(task.getId() == taskId)
				{
					return task;	
				}
			}
			return null;
		}
		/**
		 * 启动指定id的任务
		 * @param taskId
		 * @return
		 */
		public ReturnValue startTaskById(int taskId)
		{		
			ReturnValue rv=null;
			//从当前已有的任务队列里停止并清除已有的用id任务
			for(int i=0;taskVC!=null && i<taskVC.size();i++)
			{
				ScheduleTask task = taskVC.elementAt(i);
				if(task.getId() == taskId)
				{
					task.stop();
					taskVC.remove(task);
					break;
				}
			}
			//创建并启动新任务
			DBHelper dbHelper = new DBHelper();
			try {
				String sql = "select * from TAB_SCHEDULE where id="+taskId;
				JSONObject json = dbHelper.queryJsonObj(sql);
				if(json!=null)
				{	
					String name = json.optString("name");
					int type = json.optInt("type");
					String startTime = json.optString("starttime");
					long period = json.optLong("period");
					String sqlStr = json.optString("sql");
					String word1 = json.optString("word1");
					String word2 = json.optString("word2");
					String word3 = json.optString("word3");
					//int state = json.optInt("state");
					ScheduleTask task = new ScheduleTask();
					
					task.setTask(taskId,name,ScheduleTask.STATE_WAIT,type, Tools.str2date(startTime), period, sqlStr, word1, word2, word3);
					
					rv = task.start();
					if(rv.success)
					{
						taskVC.add(task);
						ScheduleTask.updateState(taskId, task.getState());										
					}
					
					return rv;			
				}
				else
				{
					return new ReturnValue(false,"任务不存在");
				}
			}
			catch (Exception e)
			{				
				e.printStackTrace();
				return new ReturnValue(false,e.toString());
			}
			finally
			{
				dbHelper.closeConnection();
			}
		}
		/**
		 * 停止指定id的任务
		 * @param taskId
		 * @return
		 */
		public ReturnValue stopTaskById(int taskId)
		{		
			ReturnValue rv=null;
			//从当前已有的任务队列里找
			for(int i=0;taskVC!=null && i<taskVC.size();i++)
			{
				ScheduleTask task = taskVC.elementAt(i);
				if(task.getId() == taskId)
				{
					rv = task.stop();				
					taskVC.remove(task);					
					return rv;
				}
			}	
			return new ReturnValue(false,"任务不存在");
		}
		/**
		 * 获取任务集合
		 * @return
		 */
		public Vector getTaskVC()
		{
			return taskVC;
		}
		/**
		 * 停止和清除全部任务
		 * @return
		 */
		public ReturnValue stopAndClearAllTask()
		{
			stopAllTask();
			taskVC = new Vector<ScheduleTask>();
			return new ReturnValue(true,"全部任务已经成功停止和清除");
		}
		/**
		 * 从数据库中加载计时任务
		 * @return
		 */
		public ReturnValue loadTaskFromDB()
		{
			stopAllTask();
			taskVC = new Vector<ScheduleTask>();
			DBHelper dbHelper = new DBHelper();
			try {
				String sql = "select * from TAB_SCHEDULE order by id";
				JSONArray array = dbHelper.queryJsonArray(sql);
				for(int i=0;array!=null && i<array.length();i++)
				{
					JSONObject line = array.optJSONObject(i);
					int id = line.optInt("id");
					String name = line.optString("name");
					int type = line.optInt("type");
					String startTime = line.optString("starttime");
					long period = line.optLong("period");
					String sqlStr = line.optString("sql");
					int state = line.optInt("state");
					String word1 = line.optString("word1");
					String word2 = line.optString("word2");
					String word3 = line.optString("word3");
					
					ScheduleTask task = new ScheduleTask();
					task.setTask(id,name,ScheduleTask.STATE_WAIT,type, Tools.str2date(startTime), period, sqlStr, word1, word2, word3);
					if(state==ScheduleTask.STATE_TIMER || state==ScheduleTask.STATE_RUN) //未执行的任务自动开始计时
					{
						if(task.start().success)
						{
							taskVC.add(task);	
						}
						else						
						{
							ScheduleTask.updateState(id, ScheduleTask.STATE_WAIT);
						}
					}						
				}
				System.out.println("计划任务加载成功");
				return new ReturnValue(true,"计划任务加载成功");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new ReturnValue(false,e.toString());
			}
			finally
			{
				dbHelper.closeConnection();
			}
		}
		
		
	
}
