package com.moonic.bac;

import java.sql.ResultSet;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.moonic.util.DBHelper;

/**
 * 表管理
 * @author 
 */
public class SystemTableBAC extends BaseActCtrl
{
	private static SystemTableBAC instance;

	public SystemTableBAC()
	{			
		super.setTbName("");
		setDataBase(ServerConfig.getDataBase());
	}
	
	public static SystemTableBAC getInstance()
	{
		if(instance==null)
		{
			instance = new SystemTableBAC();
		}
		return instance;
	}
	
	public JSONArray getTableList(PageContext pageContext)
	{
		ServletRequest request = pageContext.getRequest();
		
		
		int useDB = Tools.str2int(request.getParameter("useDB"));
		if(useDB==0)useDB=1;
		
		int showTotal = Tools.str2int(request.getParameter("showTotal"));
		int showMinId = Tools.str2int(request.getParameter("showMinId"));
		int showMaxId = Tools.str2int(request.getParameter("showMaxId"));
		
		DBHelper dbHelper = new DBHelper();
		if(useDB==1)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase());
		}
		else
		if(useDB==2)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		}
		else
		if(useDB==3)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		}
		else
		if(useDB==4)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		}
		
		
		String sql = "select table_name,cache from USER_TABLES order by table_name";			
		JSONArray tableArr = dbHelper.queryJsonArray(sql);
		
		sql = "select sequence_name,min_value,last_number from user_sequences";;
		JSONArray seqArr = dbHelper.queryJsonArray(sql);
		//System.out.println(seqArr);
		for(int i=0;tableArr!=null && i<tableArr.length();i++)
		{
			JSONObject line = tableArr.optJSONObject(i);
			String tableName = line.optString("table_name");
			try
			{					
				if(showTotal==1)
				{
					long count = dbHelper.queryCount(tableName, null);
					line.put("maxrows", count);	
				}
				int maxId=0;					
				int minId=0;
				if(showMinId==1 && showMaxId!=1)
				{
					ResultSet rs = dbHelper.executeQuery("select min(id) as minid from "+tableName);
					if(rs!=null && rs.next())
                    {
                    	minId = rs.getInt("minid");	                    	  
                    	line.put("minId", minId);	                    	
                    }  
                    if(rs!=null)
                    {						  
                    	rs.close();                          
                    }
				}
				else
				if(showMaxId==1 && showMinId!=1)
				{
					ResultSet rs = dbHelper.executeQuery("select max(id) as maxid from "+tableName);
					if(rs!=null && rs.next())
                    {	                    	
                    	maxId = rs.getInt("maxid");
                    	line.put("maxId", maxId);
                    }  
                    if(rs!=null)
                    {						  
                    	rs.close();                          
                    }
				}
				else
				if(showMaxId==1 && showMinId==1)
				{
					ResultSet rs = dbHelper.executeQuery("select min(id) as minid,max(id) as maxid from "+tableName);
					if(rs!=null && rs.next())
                    {	                    	
                    	maxId = rs.getInt("maxid");	                    	
                    	minId = rs.getInt("minid");
                    	line.put("maxId", maxId);
                    	line.put("minId", minId);	
                    }  
                    if(rs!=null)
                    {						  
                    	rs.close();                          
                    }
				}				
                
                String seqName=null;
                int currVal=0;
				int min_value=1;
				int last_number=0;
                for(int j=0;seqArr!=null && j<seqArr.length();j++)
                {
                	String theSeqName = seqArr.optJSONObject(j).optString("sequence_name");
					
                	if(theSeqName.equals("S"+tableName.toUpperCase()))
                	{
                		seqName = theSeqName;
						min_value = seqArr.optJSONObject(j).optInt("min_value");
						last_number= seqArr.optJSONObject(j).optInt("last_number");
						line.put("seqName", seqName);
						line.put("min_value", min_value);
						line.put("last_number", last_number);
						ResultSet rs=null;
                		try
                		{
                			rs = dbHelper.executeQuery("select "+("S"+tableName).toUpperCase()+".CURRVAL from "+tableName);
                			if(rs!=null && rs.next())
	                        {
	                          	currVal = rs.getInt("CURRVAL");										
	                        }
                		}
                		catch(Exception ex){
                		} 
                		finally
                		{
                			if(rs!=null)
                            {						  
                            	rs.close();                          
                            }
                		}
                	}
                }
			}
			catch(Exception ex)
			{					
			}
			finally
			{
				dbHelper.closeConnection();
			}
		}
		
		return tableArr;
	}
	
	public ReturnValue createSequence(PageContext pageContext)
	{
		ServletRequest request = pageContext.getRequest();
		int useDB = Tools.str2int(request.getParameter("useDB"));
		String tbName = request.getParameter("tbName");
		
		if(useDB<=0)
		{
			return new ReturnValue(false,"未选择数据库");
		}
		if(tbName==null || tbName.equals(""))
		{
			return new ReturnValue(false,"未选择表");
		}
		
		DBHelper dbHelper = new DBHelper();
		
		if(useDB==1)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase());
		}
		else
		if(useDB==2)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		}
		else
		if(useDB==3)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		}				
		else
		if(useDB==4)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		}
		
		//if(true)return new ReturnValue(true,"useDB="+useDB+",tbName="+tbName);		
		
		try
		{
			int startId=1;
			String sql="select max(id) as maxid from "+tbName;
			JSONObject json = dbHelper.queryJsonObj(sql);
			
			if(json!=null)
			{
				startId = json.optInt("maxid")+1;
			}
			//建立seq
			sql = "create sequence S"+tbName.toUpperCase()+"  minvalue "+startId;
			dbHelper.execute(sql);
			return new ReturnValue(true,"创建S"+tbName.toUpperCase()+"成功");				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ReturnValue(false,ex.toString());
		}	
		finally
		{
			dbHelper.closeConnection();				
		}
	}
	
	public ReturnValue delSequence(PageContext pageContext)
	{
		ServletRequest request = pageContext.getRequest();
		int useDB = Tools.str2int(request.getParameter("useDB"));
		String seqName = request.getParameter("seqName");
		
		if(useDB<=0)
		{
			return new ReturnValue(false,"未选择数据库");
		}
		if(seqName==null || seqName.equals(""))
		{
			return new ReturnValue(false,"未选择序列对象");
		}
		
		DBHelper dbHelper = new DBHelper();
		
		if(useDB==1)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase());
		}
		else
		if(useDB==2)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		}
		else
		if(useDB==3)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		}			
		else
		if(useDB==4)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		}
		
		//if(true)return new ReturnValue(true,"useDB="+useDB+",seqName="+seqName);	
		
		try
		{				
			String sql = "drop sequence " + seqName;
			dbHelper.execute(sql);
			return new ReturnValue(true,"删除序列对象"+seqName.toUpperCase()+"成功");				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ReturnValue(false,ex.toString());
		}	
		finally
		{
			dbHelper.closeConnection();				
		}
	}
	
	public ReturnValue clearAllSequence(PageContext pageContext)
	{
		ServletRequest request = pageContext.getRequest();
		int useDB = Tools.str2int(request.getParameter("useDB"));
					
		if(useDB<=0)
		{
			return new ReturnValue(false,"未选择数据库");
		}			
		
		DBHelper dbHelper = new DBHelper();
		
		if(useDB==1)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase());
		}
		else
		if(useDB==2)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		}
		else
		if(useDB==3)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		}			
		else
		if(useDB==4)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		}
		
		//if(true)return new ReturnValue(true,"useDB="+useDB);	
		
		try
		{				
			String sql = "select table_name from USER_TABLES order by table_name";
			JSONArray array = dbHelper.queryJsonArray(sql);
			for(int i=0;array!=null && i<array.length();i++)
			{
				JSONObject line = array.optJSONObject(i);
				String tbName = line.optString("table_name");
				sql = "select sequence_name from user_sequences where sequence_name='S"+tbName.toUpperCase()+"'";
				JSONObject json = dbHelper.queryJsonObj(sql);
				if(json!=null)
				{
					dbHelper.execute("drop sequence " + "S"+tbName.toUpperCase());
				}
			}
			return new ReturnValue(true,"序列对象清除成功"); 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ReturnValue(false,ex.toString());
		}	
		finally
		{
			dbHelper.closeConnection();
		}
	}
	
	public ReturnValue createAllNotExistSequence(PageContext pageContext)
	{
		ServletRequest request = pageContext.getRequest();
		int useDB = Tools.str2int(request.getParameter("useDB"));
					
		if(useDB<=0)
		{
			return new ReturnValue(false,"未选择数据库");
		}			
		
		DBHelper dbHelper = new DBHelper();
		
		if(useDB==1)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase());
		}
		else
		if(useDB==2)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		}
		else
		if(useDB==3)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		}
		else
		if(useDB==4)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		}
		
		//if(true)return new ReturnValue(true,"useDB="+useDB);	
		
		try
		{
			String sql = "select table_name from USER_TABLES order by table_name";
			JSONArray array = dbHelper.queryJsonArray(sql);
			for(int i=0;array!=null && i<array.length();i++)
			{
				JSONObject line = array.optJSONObject(i);
				String tbName = line.optString("table_name");
				sql = "select sequence_name from user_sequences where sequence_name='S"+tbName.toUpperCase()+"'";
				JSONObject json = dbHelper.queryJsonObj(sql);
				
				if(json==null)
				{
					int startId=1;
					sql="select max(id) as maxid from "+tbName;
					JSONObject json2 = dbHelper.queryJsonObj(sql);
					
					if(json2!=null)
					{
						startId = json2.optInt("maxid")+1;
					}
					//建立seq
					sql = "create sequence S"+tbName.toUpperCase()+" minvalue "+startId;
					dbHelper.execute(sql);						
				}					
			}							
			return new ReturnValue(true,"序列对象生成成功"); 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ReturnValue(false,ex.toString());
		}	
		finally
		{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取指定表的索引数量
	 * @param pageContext
	 * @return
	 */
	public int getIndexCount(int useDB,String tbName)
	{
		if(useDB<=0)
		{
			return -1;
		}				
		
		DBHelper dbHelper = new DBHelper();
		
		if(useDB==1)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase());
		}
		else
		if(useDB==2)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		}
		else
		if(useDB==3)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		}
		else
		if(useDB==4)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		}
		dbHelper.getDataBase().setUseDebug(false);
		try
		{
			String sql = "select count(*) from dba_ind_columns where table_owner='"+dbHelper.getDataBase().getUsername().toUpperCase()+"' and  table_name = '"+tbName.toUpperCase()+"'";
			ResultSet rs= dbHelper.query("dba_ind_columns", "count(*)", "table_owner='"+dbHelper.getDataBase().getUsername().toUpperCase()+"' and  table_name='"+tbName.toUpperCase()+"'");
			if(rs!=null && rs.next())
			{
				return rs.getInt(1);
			}
			else
			{
				return 0;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return -1;
		}	
		finally
		{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取指定表的索引集合
	 * @param pageContext
	 * @return
	 */
	public JSONArray getIndexJsonArray(PageContext pageContext)
	{
		ServletRequest request = pageContext.getRequest();
		
		int useDB = Tools.str2int(request.getParameter("useDB"));

		String tbName = request.getParameter("tbName");
		
		if(useDB<=0)
		{
			return null;
		}				
		
		DBHelper dbHelper = new DBHelper();
		
		if(useDB==1)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase());
		}
		else
		if(useDB==2)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Backup());
		}
		else
		if(useDB==3)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
		}
		else
		if(useDB==4)
		{
			dbHelper = new DBHelper(ServerConfig.getDataBase_Report());
		}
		
		try {
			JSONArray array = dbHelper.queryJsonArray("dba_ind_columns", "*", "table_owner='"+dbHelper.getDataBase().getUsername().toUpperCase()+"' and  table_name='"+tbName.toUpperCase()+"'","index_name asc");
			return array;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
