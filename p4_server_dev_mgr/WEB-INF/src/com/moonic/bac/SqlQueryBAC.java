package com.moonic.bac;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;
import server.config.ServerConfig;
import server.database.DataBase;
import util.IPAddressUtil;

import com.ehc.common.ReturnValue;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.system.TBLogParameter;
import com.jspsmart.upload.SmartUpload;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;
import com.moonic.util.StreamHelper;

import conf.Conf;

/**
 * SQL操作
 * @author John
 */
public class SqlQueryBAC extends BaseActCtrl{
	public static String tbName = "tb_sql_query";
	
	private static SqlQueryBAC self = new SqlQueryBAC();
	  		
	public static SqlQueryBAC getInstance() {		
		return self;
	}
	
	public SqlQueryBAC() {
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}
	
	public JSONArray getRsColumns(DataBase dataBase,String strSQL)
	{ 
		DBHelper dbHelper=new DBHelper(dataBase);
    	JSONArray array = new JSONArray();
    	ResultSet rs=null;
        try
		{	
			if (strSQL.equals(""))
				return null;
			
			String sql = "select * from (select tb1.*,rownum as newrow from (" + strSQL + ") tb1) where newrow<=1";
			rs = dbHelper.executeQuery(sql);
			
			ResultSetMetaData rmd = rs.getMetaData();
			for (int j=1; j<=rmd.getColumnCount(); j++)
			{
				array.add(rmd.getColumnName(j).trim().toLowerCase());               
			}
			return array;
		}
		catch (Exception e)
		{			
			System.out.println("SQL="+strSQL);
			e.printStackTrace();
			return null;
		}
		finally
		{
			dbHelper.closeRs(rs);
			dbHelper.closeConnection();				
		}
	}
	
	public JSONObject queryBySql(PageContext pageContext)
	{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		
		String pagenum=request.getParameter("page");
		if(pagenum==null || pagenum.equals(""))
		{
			pagenum="1";
		}

		String rpp=request.getParameter("rpp");
		if(rpp==null || rpp.equals(""))
		{
			rpp="10";
		}

		String sql = Tools.strNull(request.getParameter("sql"));
		
		DataBase dataBase=null;
		int useDB = Tools.str2int(request.getParameter("useDB"));
		if(useDB==0)useDB=1;
		
		if(useDB==1)
		{
			dataBase = ServerConfig.getDataBase();
		}
		else
		if(useDB==2)
		{
			dataBase = ServerConfig.getDataBase_Backup();
		}
		else
		if(useDB==3)
		{
			dataBase = ServerConfig.getDataBase_Log();
		}			
		else
		if(useDB==4)
		{
			dataBase =  ServerConfig.getDataBase_Report();
		}
		//输出日志
		String ip = IPAddressUtil.getIp(request);
		String username = (String)pageContext.getSession().getAttribute("username");
		
		JSONArray array = getRsColumns(dataBase,sql);
		SqlQueryBAC sqlQueryBAC = new SqlQueryBAC(); //因为是用的单类模式，避免不同人查不同的库，所以这里必须new一个类
		sqlQueryBAC.setDataBase(dataBase);
		JSONObject jsonObj = sqlQueryBAC.getJsonPageListBySQL(sql, Tools.str2int(pagenum), Tools.str2int(rpp));
		
		TBLogParameter  parameter=TBLogParameter.getInstance();
		parameter.addParameter("sql", sql);
		LogBAC.addLog(username,"SQL查询",parameter.toString(),ip);
		
		if(jsonObj!=null)
		{
			jsonObj.put("columns", array);	
		}
		else
		{
			jsonObj = new JSONObject();
			jsonObj.put("columns", array);	
		}
		
		return jsonObj;			
	}
	
	public void exportQueryExcel(PageContext pageContext)
	{			
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		
		String sql= request.getParameter("sql");		
		if(sql==null || sql.equals(""))
		{
			return;
		}
		DataBase dataBase=null;
		int useDB = Tools.str2int(request.getParameter("useDB"));
		if(useDB==0)useDB=1;
		
		if(useDB==1)
		{
			dataBase = ServerConfig.getDataBase();
		}
		else
		if(useDB==2)
		{
			dataBase = ServerConfig.getDataBase_Backup();
		}
		else
		if(useDB==3)
		{
			dataBase = ServerConfig.getDataBase_Log();
		}			
		else
		if(useDB==4)
		{
			dataBase =  ServerConfig.getDataBase_Report();
		}
		
		//输出日志
		String ip = IPAddressUtil.getIp(request);
		String username = (String)pageContext.getSession().getAttribute("username");
		
		TBLogParameter  parameter=TBLogParameter.getInstance();
		parameter.addParameter("sql", sql);
		LogBAC.addLog(username,"SQL导出",parameter.toString(),ip);
		
		Connection conn=null;
		ResultSet rs=null;
		try {
			conn = dataBase.getConnection();
			int total=0;
			String totalSql="select count(*) from ("+sql+")";
			rs = dataBase.executeQuery(conn,totalSql);
			if(rs!=null && rs.next())
			{
				total = rs.getInt(1);
				rs.close();
			}
			
			rs = dataBase.executeQuery(conn,sql);
			
			if(rs!=null)
			{
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFCellStyle titleStyle = workbook.createCellStyle();		
				HSSFCellStyle leftStyle = workbook.createCellStyle();	
				HSSFCellStyle rightStyle = workbook.createCellStyle();
				HSSFCellStyle centerStyle = workbook.createCellStyle();
				
				titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				leftStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				rightStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				titleStyle.setRightBorderColor((short)0);
				
				int rowsPerPage=50000; //一页最大数
				//获得页数					
				int sheetAmount = total /rowsPerPage + ((total % rowsPerPage>0)?1:0);
				
				int rowIndex=0; //行索引
				//创建sheet和标题
				ResultSetMetaData rmd = rs.getMetaData();
				
				
				for(int i=0;i<sheetAmount;i++)
				{
					HSSFSheet sheet = workbook.createSheet();
					workbook.setSheetName(i, "第"+(i+1)+"页");
					
					rowIndex=0;	
					//标题					
					HSSFRow row = sheet.createRow(rowIndex);
					//列
					for(int j=0;j<rmd.getColumnCount();j++)
					{
						sheet.setColumnWidth(j, (short) (35.7 * 100));
						HSSFCell cell = row.createCell(j);
						cell.setCellStyle(titleStyle);
						cell.setCellValue(new HSSFRichTextString(rmd.getColumnName(j+1)));
					}	
					rowIndex++;
														
					//数据
					while(rs !=null && rs.next())
					{
						row = sheet.createRow(rowIndex);
						for(int j=0;j<rmd.getColumnCount();j++)
						{
							HSSFCell cell = row.createCell(j);	
							cell.setCellStyle(centerStyle);	
							cell.setCellValue(rs.getString(j+1));								
						}	
					
						rowIndex++;
						if(rowIndex>=(i+1)*rowsPerPage)
						{
							break;//分页				
						}
					}
				}					
				dataBase.closeRS(rs);
				try {				
					String contenttype="application/octet-stream";
					
					response.reset() ;
					response.setContentType(contenttype);					
					
					response.setHeader("Content-Disposition", new String(("attachment;Filename=SQL查询结果.xls").getBytes("GBK"),"ISO8859-1"));
					OutputStream os = response.getOutputStream();
					workbook.write(os);					
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch(Exception e){
			e.printStackTrace();	
			dataBase.closeRS(rs);
		} finally {				
			dataBase.closeConnection(conn);
		}
	}
	
	public ReturnValue executeBySql(PageContext pageContext)
	{		
		boolean needLog = true;
		
		int useDB = 0;
		
		String[] lines = null;
		
		try {
			SmartUpload smartUpload = new SmartUpload();
			smartUpload.setEncode("UTF-8");
			
			smartUpload.initialize(pageContext);
			smartUpload.upload();
			
			com.jspsmart.upload.Request request = smartUpload.getRequest();
			
			String sqlLines = request.getParameter("sqlLines");
			useDB = Tools.str2int(request.getParameter("useDB"));
			
			if(sqlLines != null && !sqlLines.equals("")){
				lines = Tools.splitStr(sqlLines, "\r\n");
			}
			
			if(lines == null){
				com.jspsmart.upload.File file = smartUpload.getFile("exesqlfile");
				if(file != null && file.getSize() > 0){
					String filename = file.getFileName();
					
					String path = Conf.logRoot + "exesql/";
					StreamHelper.getInstance().upload(smartUpload, path, "exesqlfile");
					
					lines = Tools.getStrLineArr(MyTools.readTxtFile(path+filename), "data:", "dataEnd");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DataBase dataBase = null;
		if(useDB==1)
		{
			dataBase = ServerConfig.getDataBase();
		}
		else
		if(useDB==2)
		{
			dataBase = ServerConfig.getDataBase_Log();
		}			
		else
		if(useDB==3)
		{
			dataBase = ServerConfig.getDataBase_Report();
		}
		//输出日志
		String ip = IPAddressUtil.getIp((HttpServletRequest) pageContext.getRequest());
		String username = (String)pageContext.getSession().getAttribute("username");
		
		Connection conn = null;
		try
		{
			conn = dataBase.getConnection();
			int succ_count = 0;	
			StringBuffer failSb = new StringBuffer();
			for(int i=0;i<lines.length;i++)
			{	
				lines[i] =lines[i].trim();
				if(lines[i].equals("")){
					continue;
				}
				if(lines[i].endsWith(";")) {
					lines[i] = lines[i].substring(0, lines[i].length()-1);
				}
				boolean success = dataBase.execute(conn, lines[i]);
				if(success)
				{
					succ_count++;
				}
				else
				{
					if(needLog)
					{
						failSb.append((i+1)+"."+lines[i]+" 执行失败\\n");
					}
					else
					{
						failSb.append("执行失败");
					}
				}
				TBLogParameter  parameter=TBLogParameter.getInstance();
				parameter.addParameter("sql", lines[i]);
				parameter.addParameter("result", success);
				LogBAC.addLog(username,"SQL执行",parameter.toString(),ip);
			}
			StringBuffer sb = new StringBuffer();
			sb.append("共"+lines.length+"条sql，其中"+succ_count+"条执行成功\\r\\n"+failSb.toString());
			return new ReturnValue(true, sb.toString());
		}
		catch(Exception ex)
		{
			return new ReturnValue(true, ex.getMessage());
		}
		finally
		{
			dataBase.closeConnection(conn);
		}			
	}
}
