package com.moonic.bac;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.SmartUpload;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;


public class ExchangeCodeBAC extends BaseActCtrl
{	
	public static String tbName = "tab_exchange_code";	 
	private static ExchangeCodeBAC self;	
	
	private static Object syncLock;
	  		
	public static ExchangeCodeBAC getInstance()
	{						
		if(self==null)
		{
			self = new ExchangeCodeBAC();
		}
		if(syncLock==null)
		{
			syncLock = new Object();
		}
		return self;
	}
	public ExchangeCodeBAC()
	{			
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}
	
	public ReturnValue batchAdd(PageContext pageContext)
	{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		int len = Tools.str2int(request.getParameter("len"));
		int amount = Tools.str2int(request.getParameter("amount"));
		int type = Tools.str2int(request.getParameter("type"));
		String opendate = request.getParameter("opendate");
		
		synchronized (syncLock) 
		{
			String[] exclude =null;
			JSONObject codeRs = getJsonObjs("code", null, null);
			if(codeRs!=null)
			{
				JSONArray codeArr = codeRs.optJSONArray("list");
				for(int i=0;codeArr!=null && i<codeArr.length();i++)
				{
					exclude = Tools.addToStrArr(exclude, codeArr.optString(i));
				}
			}
			
			String[] arr = MyTools.generateCode(type,len,amount,exclude);
			
			String create_time=Tools.getCurrentDateTimeStr();
			
			DBHelper dbHelper = new DBHelper();
			
			SqlString sqlStr = new SqlString();
			try
			{
				dbHelper.openConnection(false);
				String opusername = (String)pageContext.getSession().getAttribute("username");
				for(int i=0;arr!=null && i<arr.length;i++)
				{
					sqlStr.add("code", arr[i]);
					if(opendate!=null && !opendate.equals(""))sqlStr.addDateTime("open_date", opendate);
					sqlStr.add("published", 0);
					sqlStr.add("exchanged", 0);
					sqlStr.add("createuser", opusername);
					sqlStr.addDateTime("create_time", create_time);
					dbHelper.insert(tbName, sqlStr);
					sqlStr.clear();				
				}
				dbHelper.commit();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return new ReturnValue(false,"批量生成失败");
			}
			finally
			{
				dbHelper.closeConnection();
			}
		}
		return new ReturnValue(true,"批量生成成功");
	}
	
	public JSONObject getPageList(PageContext pageContext)
	{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		
		int page = Tools.str2int(request.getParameter("page"), 1);	
		if(page<=0)page=1;
		
		int rows = Tools.str2int(request.getParameter("rpp"));
		if(rows<=0)rows=10;
		
		String ordertype=request.getParameter("ordertype");
		if(ordertype==null || ordertype.equals(""))
		{
			ordertype="DESC";
		}
		String showorder=request.getParameter("showorder");
		if(showorder==null || showorder.equals(""))
		{
			showorder = "nvl(publish_time,to_date('1970','YYYY'))";
		}
		else
		if(showorder.equals("id"))
		{
			showorder = "nvl(publish_time,to_date('1970','YYYY'))";
		}
		
		String published = Tools.strNull(request.getParameter("published"));	
		String exchanged = Tools.strNull(request.getParameter("exchanged"));		
		
		String colname=request.getParameter("colname");
		if(colname==null){colname="";}
		String operator=request.getParameter("operator");
		if(operator==null){operator="";}
		String colvalue=request.getParameter("colvalue");
		if(colvalue==null){colvalue="";}
		colvalue=Tools.replace(colvalue,"\"","\\\"");
		
		SqlString sqlS = new SqlString();
		
		if(colvalue!=null && !colvalue.equals(""))
		{
			if(colname!=null)
			{
				if(colname.equals("open_date"))
				{
					if(operator.equals("等于"))
					{
						sqlS.addDate(colname,colvalue);
					}
					else
					if(operator.equals("大于"))
					{
						sqlS.addDate(colname,colvalue,">");
					}
					else
					if(operator.equals("小于"))
					{
						sqlS.addDate(colname,colvalue,"<");
					}
				}
				else
				{
					if(operator.equals("等于"))
					{
						sqlS.add(colname,colvalue);
					}					
					else
					if(operator.equals("包含"))				
					{
						sqlS.add(colname,colvalue,"like");
					}
				}
				
			}
		}

		if(published!=null && !published.equals(""))
		{
			sqlS.add("published",Tools.str2int(published));
		}
		if(exchanged!=null && !exchanged.equals(""))
		{
			sqlS.add("exchanged",Tools.str2int(exchanged));
		}
		
		
		String sql = "select * from "+tbName+" "+sqlS.whereStringEx()+" order by "+showorder+" "+ordertype;
		//System.out.println(sql);
		JSONObject json = getJsonPageListBySQL(sql, page, rows);
		return json;
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
			String code=request.getParameter("code");
			String open_date=request.getParameter("open_date");
			int published=Tools.str2int(request.getParameter("published"));
			String publish_time=request.getParameter("publish_time");
			String phone=request.getParameter("phone");
			int exchanged=Tools.str2int(request.getParameter("exchanged"));
			String exchange_time=request.getParameter("exchange_time");
			String create_time=request.getParameter("create_time");
			
			String opusername = (String)pageContext.getSession().getAttribute("username");
			
			FormXML formXML = new FormXML();
			formXML.add("code",code);
			formXML.addDate("open_date",open_date);
			formXML.add("published",published);
			formXML.addDate("publish_time",publish_time);
			formXML.add("phone",phone);
			formXML.add("exchanged",exchanged);
			formXML.addDate("exchange_time",exchange_time);
			formXML.add("createuser", opusername);

			if(id>0)
			{
				formXML.addDate("create_time",create_time);
				int count = getCount("code='"+code+"' and id <>"+id);
				if(count>0)
				{
						return new ReturnValue(false,"兑换码重复");
				}
			}
			else
			{
				formXML.addDate("create_time",Tools.getCurrentDateTimeStr());
				int count = getCount("code='"+code+"'");
				if(count>0)
				{
						return new ReturnValue(false,"兑换码重复");
				}
			}
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
		ReturnValue rv = super.del("id="+ id);			
		//todo 删除其他关联表中的记录
		return rv;
	}	
}
