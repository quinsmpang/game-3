package com.moonic.bac;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;

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
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.SmartUpload;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;

/**
 * 激活码
 * @author John
 */
public class ActivateCodeBAC extends BaseActCtrl
{
	public static String tbName = "TAB_ACTIVATE_CODE";
	
	private static ActivateCodeBAC self;
	
	private static Object syncLock;
	
	public static ActivateCodeBAC getInstance()
	{
		if(self==null)
		{
			self = new ActivateCodeBAC();
		}
		if(syncLock==null)
		{
			syncLock = new Object();
		}
		return self;
	}
	
	public ActivateCodeBAC()
	{
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}
	//恭喜您成功领取激活码：GHFDN,登陆仙魔九界OL客户端激活游戏帐号，参与封测人人有奖。详情请关注官方论坛：www.xianmobbs.com【波克城市】

	
	public ReturnValue batchAdd(PageContext pageContext)
	{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		int len = Tools.str2int(request.getParameter("len"));
		int amount = Tools.str2int(request.getParameter("amount"));
		int type = Tools.str2int(request.getParameter("type"));
		String starttime = request.getParameter("starttime");
		String mark = request.getParameter("mark");
		
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
					sqlStr.add("mark", mark);
					if(starttime!=null && !starttime.equals("")){
						sqlStr.addDateTime("starttime", starttime);
					}
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
			String create_time=Tools.getCurrentDateTimeStr();
			String starttime=request.getParameter("starttime");
			int publish=Tools.str2int(request.getParameter("publish"));
			int activated=Tools.str2int(request.getParameter("activated"));
			String publish_time=request.getParameter("publish_time");
			String activate_user=request.getParameter("activate_user");
			String activate_time=request.getParameter("activate_time");
			String phone=request.getParameter("phone");
			int method=Tools.str2int(request.getParameter("method"));
			int lottery=Tools.str2int(request.getParameter("lottery"));
			String lottery_time=request.getParameter("lottery_time");
			String mark = request.getParameter("mark");
			
			String opusername = (String)pageContext.getSession().getAttribute("username");
			
			FormXML formXML = new FormXML();
			

			if(id>0)
			{
				int count = getCount("code='"+code+"' and id <>"+id);
				if(count>0)
				{
					return new ReturnValue(false,"激活码重复");
				}
			}
			else
			{
				int count = getCount("code='"+code+"'");
				if(count>0)
				{
						return new ReturnValue(false,"激活码重复");
				}
			}
			if(id>0)  //修改
			{	
				formXML.add("code",code);			
				formXML.addDateTime("create_time",create_time);
				formXML.add("publish",publish);				
				formXML.addDateTime("starttime",starttime);				
				formXML.add("activated",activated);		
				formXML.addDateTime("publish_time",publish_time);	
				formXML.add("activate_user",activate_user);	
				formXML.addDateTime("activate_time",activate_time);
				formXML.add("phone",phone);
				formXML.add("method",method);
				formXML.add("lottery",lottery);
				formXML.add("createuser", opusername);
				formXML.addDateTime("lottery_time",lottery_time);
				formXML.add("mark", mark);
				
				/*String oldCode = getValue("code", "id=" + id);
				if(!oldCode.equals(code)) //激活码改了，状态复位
				{
					formXML.add("publish", 0);
					formXML.add("activated", 0);
					formXML.addDateTime("publish_time", null);
					formXML.add("activate_user", null);		
					formXML.addDateTime("activate_time", null);
				}*/
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
				formXML.add("code",code);			
				formXML.addDateTime("create_time",create_time);
				formXML.add("publish",publish);
				if(starttime!=null && !starttime.equals(""))
				{
					formXML.addDateTime("starttime",starttime);
				}
				formXML.add("activated",activated);		
				formXML.addDateTime("publish_time",publish_time);	
				formXML.add("activate_user",activate_user);	
				formXML.addDateTime("activate_time",activate_time);
				formXML.add("phone",phone);
				formXML.add("method",method);
				formXML.add("lottery",lottery);
				formXML.add("createuser", opusername);
				formXML.addDateTime("lottery_time",lottery_time);
				formXML.add("mark", mark);
				
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
			return new ReturnValue(false,e.toString());
		} 		
	}
	
	/**
	 * 导出已分发未激活的手机号码
	 * @param pageContext
	 */
	public void exportPhoneExcel(PageContext pageContext)
	{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		
		//String startTime = request.getParameter("startTime");
		//String endTime = request.getParameter("endTime");
		String activate_condition = request.getParameter("activate_condition");
		String method1_condition = request.getParameter("method1_condition");
		String method2_condition = request.getParameter("method2_condition");
		String method3_condition = request.getParameter("method3_condition");
		String method4_condition = request.getParameter("method4_condition");
		
		SqlString sqlS = new SqlString();
		sqlS.add("tab_activate_code.publish",1);
		if(activate_condition!=null)
		{
			if(activate_condition.equals("1"))
			{
				sqlS.add("tab_activate_code.activated",1);
			}
			else
			if(activate_condition.equals("0"))
			{
				sqlS.add("tab_activate_code.activated",0);
			}
		}
		if(method1_condition!=null)
		{
			if(method1_condition.equals("1"))
			{
				sqlS.addWhereOr("method=1");
			}
		}
		if(method2_condition!=null)
		{
			if(method2_condition.equals("1"))
			{
				sqlS.addWhereOr("method=2");
			}
		}
		if(method3_condition!=null)
		{
			if(method3_condition.equals("1"))
			{
				sqlS.addWhereOr("method=3");
			}
		}
		if(method4_condition!=null)
		{
			if(method4_condition.equals("1"))
			{
				sqlS.addWhereOr("method=4");
			}
		}
		
		sqlS.addWhere("tab_activate_code.phone is not null");
		
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			String sql = "select tab_activate_code.activated,tab_activate_code.method,tab_activate_code.phone,tab_activate_code.publish_time" +
					" from tab_activate_code "+
					" where "+sqlS.whereString()+ 
					" order by tab_activate_code.publish_time ASC";
			//System.out.println(sql);
			ResultSet rs = dbHelper.executeQuery(sql);
			
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
				
				int rowsPerPage=50000;
				//获得数量
				rs.last();
				int maxLen = rs.getRow();
				int sheetAmount = maxLen /rowsPerPage + ((maxLen % rowsPerPage>0)?1:0);
				HSSFSheet[] sheet=new HSSFSheet[sheetAmount];
				int currentSheetIndex=0; //当前使用的sheet索引
				
				rs.beforeFirst(); //复位
				
				int titleRowIndex=0;
				//创建sheet和标题
				for(int i=0;i<sheetAmount;i++)
				{
					titleRowIndex=0;
					sheet[i] = workbook.createSheet();
					workbook.setSheetName(i, "第"+(i+1)+"页");
					sheet[i].setColumnWidth(0, (short) (35.7 * 60)); //No	
					sheet[i].setColumnWidth(1, (short) (35.7 * 120)); //激活码获取方式
					sheet[i].setColumnWidth(2, (short) (35.7 * 100)); //是否激活
					sheet[i].setColumnWidth(3, (short) (35.7 * 100)); //手机号					
					sheet[i].setColumnWidth(4, (short) (35.7 * 140)); //时间				
									
					
					//标题					
					HSSFRow row = sheet[i].createRow(titleRowIndex);
					
					//游戏服务器	用户名	游戏角色名	兑换项目	兑换时间	是否已兑现	兑现日期
					HSSFCell cell = row.createCell(0);
					cell.setCellStyle(titleStyle);
					cell.setCellValue(new HSSFRichTextString("No"));
					
					cell = row.createCell(1);	
					cell.setCellStyle(titleStyle);
					cell.setCellValue(new HSSFRichTextString("激活码获取方式"));
					
					cell = row.createCell(2);	
					cell.setCellStyle(titleStyle);
					cell.setCellValue(new HSSFRichTextString("是否已激活"));
					
					cell = row.createCell(3);	
					cell.setCellStyle(titleStyle);
					cell.setCellValue(new HSSFRichTextString("手机号"));
					
					cell = row.createCell(4);	
					cell.setCellStyle(titleStyle);
					cell.setCellValue(new HSSFRichTextString("发放时间"));					
				}
				
				int rowIndex=titleRowIndex+1;										
				int resultNum=0;  //记录数				
				
				while(rs !=null && rs.next())
				{
					int method = rs.getInt("method");
					String methodStr="";
					if(method==1)
					{
						methodStr="短信";
					}
					if(method==2)
					{
						methodStr="网站";
					}
					if(method==3)
					{
						methodStr="内部";
					}
					if(method==4)
					{
						methodStr="渠道";
					}
					int activated = rs.getInt("activated");
					String activatedStr="";
					if(activated==1)
					{
						activatedStr="已激活";
					}
					else
					{
						activatedStr="未激活";
					}
					String phone = rs.getString("phone");
					String publish_time = rs.getString("publish_time");					
					
					resultNum++;
					//游戏服务器	用户名	游戏角色名	兑换项目	兑换时间	是否已兑现	兑现日期
					HSSFRow row = sheet[currentSheetIndex].createRow(rowIndex);
					HSSFCell cell = row.createCell(0);				
					cell.setCellValue(resultNum); //Number
					cell.setCellStyle(centerStyle);					
					
					cell = row.createCell(1);	
					cell.setCellStyle(centerStyle);
					cell.setCellValue(new HSSFRichTextString(methodStr)); //激活码获取方式
					
					cell = row.createCell(2);	
					cell.setCellStyle(centerStyle);
					cell.setCellValue(new HSSFRichTextString(activatedStr)); //是否已激活
					
					cell = row.createCell(3);	
					cell.setCellStyle(centerStyle);
					cell.setCellValue(new HSSFRichTextString(phone)); //手机号
					
					cell = row.createCell(4);	
					cell.setCellStyle(centerStyle);
					cell.setCellValue(new HSSFRichTextString(publish_time)); //发放时间
					
				
					rowIndex++;
					if(resultNum==(currentSheetIndex+1)*rowsPerPage)
					{
						//分sheet
						if(currentSheetIndex<sheetAmount-1)
						{
							currentSheetIndex++;
							rowIndex=titleRowIndex+1;
						}					
					}
				}
				try {				
					//String contenttype = "application/vnd.ms-excel";
					String contenttype="application/octet-stream";
					
					response.reset() ;
					response.setContentType(contenttype);					
					
					//response.setHeader("Content-Disposition", new String("inline;Filename=失败手机号码.xls".getBytes(),"ISO8859-1"));
					response.setHeader("Content-Disposition", new String(("attachment;Filename=手机号码.xls").getBytes("GBK"),"ISO8859-1"));
					OutputStream os = response.getOutputStream();
					workbook.write(os);					
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch(Exception e){
			e.printStackTrace();			
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 导出激活码给渠道
	 */
	public void exportCodeExcel(PageContext pageContext)
	{
		synchronized (syncLock) 
		{
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
			String mark = request.getParameter("mark");
			int amount = Tools.str2int(request.getParameter("amount"));
			//String startTime = request.getParameter("startTime");
			//String endTime = request.getParameter("endTime");
			SqlString sqlS = new SqlString();
			sqlS.add("tab_activate_code.publish", 0);
			sqlS.add("tab_activate_code.mark", mark);
			sqlS.addWhere("rownum<="+amount);
			
			DBHelper dbHelper = new DBHelper();
			try {
				dbHelper.openConnection();
				String sql = "select tab_activate_code.code,tab_activate_code.id" +
						" from tab_activate_code "+
						" where "+sqlS.whereString()+ 
						" order by tab_activate_code.id DESC";
				ResultSet rs = dbHelper.executeQuery(sql);
				
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
					
					int rowsPerPage=50000;
					//获得数量
					rs.last();
					int maxLen = rs.getRow();
					int sheetAmount = maxLen /rowsPerPage + ((maxLen % rowsPerPage>0)?1:0);
					HSSFSheet[] sheet=new HSSFSheet[sheetAmount];
					int currentSheetIndex=0; //当前使用的sheet索引
					
					rs.beforeFirst(); //复位
					
					int titleRowIndex=0;
					//创建sheet和标题
					for(int i=0;i<sheetAmount;i++)
					{
						titleRowIndex=0;
						sheet[i] = workbook.createSheet();
						workbook.setSheetName(i, "第"+(i+1)+"页");
						sheet[i].setColumnWidth(0, (short) (35.7 * 60)); //No					
						sheet[i].setColumnWidth(1, (short) (35.7 * 100)); //激活码
										
						
						//标题					
						HSSFRow row = sheet[i].createRow(titleRowIndex);
						
						HSSFCell cell = row.createCell(0);
						cell.setCellStyle(titleStyle);
						cell.setCellValue(new HSSFRichTextString("No"));			
						
						cell = row.createCell(1);	
						cell.setCellStyle(titleStyle);
						cell.setCellValue(new HSSFRichTextString("激活码"));								
					}
					
					int rowIndex=titleRowIndex+1;										
					int resultNum=0;  //记录数				
					
					while(rs !=null && rs.next())
					{
						String phone = rs.getString("code");
						int id = rs.getInt("id");
						
						resultNum++;
						
						HSSFRow row = sheet[currentSheetIndex].createRow(rowIndex);
						HSSFCell cell = row.createCell(0);				
						cell.setCellValue(resultNum); //Number
						cell.setCellStyle(centerStyle);					
						
						cell = row.createCell(1);	
						cell.setCellStyle(centerStyle);
						cell.setCellValue(new HSSFRichTextString(phone)); //激活码				
					
						rowIndex++;
						if(resultNum==(currentSheetIndex+1)*rowsPerPage)
						{
							//分sheet
							if(currentSheetIndex<sheetAmount-1)
							{
								currentSheetIndex++;
								rowIndex=titleRowIndex+1;
							}					
						}
						update("method=4,publish=1,publish_time="+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr()), "id="+id);
					}
					try {				
						//String contenttype = "application/vnd.ms-excel";
						//System.out.println(Tools.getCurrentDateTimeStr()+"--导出excel");
						
						String contenttype="application/octet-stream";
						
						response.reset() ;
						response.setContentType(contenttype);					
						
						//response.setHeader("Content-Disposition", new String("inline;Filename=失败手机号码.xls".getBytes(),"ISO8859-1"));
						response.setHeader("Content-Disposition", new String(("attachment;Filename=激活码"+amount+"个.xls").getBytes("GBK"),"ISO8859-1"));
						OutputStream os = response.getOutputStream();
						workbook.write(os);					
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch(Exception e){
				e.printStackTrace();			
			} finally {
				dbHelper.closeConnection();
			}
		}
	}
	
	/**
	 * 导入渠道手机号码
	 */
	public ReturnValue importChannelPhoneExcel(PageContext pageContext)
	{
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.setEncode("UTF-8");
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			smartUpload.initialize(pageContext);
			smartUpload.upload();
			//com.jspsmart.upload.Request request = smartUpload.getRequest();
			com.jspsmart.upload.File file = smartUpload.getFile("file");
			int fileSize = file.getSize();
			//System.out.println(file.getFileName());
			//System.out.println(file.getFileExt());
			if(fileSize>0)
			{
				if(file.getFileExt().equals("xls") ||  file.getFileExt().equals("xlsx"))
				{
					byte[] fileBytes = new byte[fileSize];
					for(int i=0;i<fileSize;i++)
					{
						fileBytes[i] = file.getBinaryData(i);
					}
					ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
					
					
					int phoneAmount=0;
					
					HSSFWorkbook workbook = new HSSFWorkbook(bais);
					for(int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++)
					{
			            HSSFSheet hssfSheet = workbook.getSheetAt(numSheet);
			            if (hssfSheet == null) {
			                continue;
			            }
			            // 循环行Row
			            for(int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) 
			            {
			                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
			                if (hssfRow == null) 
			                {
			                    continue;
			                }
			                String phone=null;
			                String code=null;
			                HSSFCell cell = hssfRow.getCell(0);
			                
			                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			                phone = cell.getRichStringCellValue().toString();  	
			                
			                cell = hssfRow.getCell(2);
			                code = cell.getRichStringCellValue().toString();
			                if(phone!=null && code!=null && !phone.equals("") && !code.equals(""))
			                {
			                	SqlString sqlS = new SqlString();
		                		sqlS.add("phone", phone);
		                		
		                		phoneAmount++;
		                		dbHelper.update("tab_activate_code", sqlS, "code='"+code+"' and publish=1");
			                }			                		              		              
			            }
			        }
					if(phoneAmount>0)
					{
						return new ReturnValue(true,"导入成功，共导入了"+phoneAmount+"条手机号码数据");
					}
					else
					{
						return new ReturnValue(false,"没有可导入的有效数据");	
					}
				}
				else
				{
					return new ReturnValue(false,"必须导入excel格式的文件");
				}				
			}
			else
			{
				return new ReturnValue(false,"请上传文件");
			}
		}
		catch (Exception e) 
		{				
			e.printStackTrace();
			return new ReturnValue(false,"导入失败"+e.toString());
		} 
		finally
		{
			dbHelper.closeConnection();
		}
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
		if(showorder.equals("publish_time"))
		{
			showorder = "nvl(publish_time,to_date('1970','YYYY'))";
		}
		
		String publish = Tools.strNull(request.getParameter("publish"));
		String activated = Tools.strNull(request.getParameter("activated"));
		String lottery = Tools.strNull(request.getParameter("lottery"));
		int method = Tools.str2int(request.getParameter("method"));
		
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
				if(operator.equals("等于"))
				{
					sqlS.add(colname,colvalue);
				}
				else
				{
					sqlS.add(colname,colvalue,"like");
				}
			}
		}

		if(publish!=null && !publish.equals(""))
		{
			sqlS.add("publish",Tools.str2int(publish));
		}
		if(activated!=null && !activated.equals(""))
		{
			sqlS.add("activated",Tools.str2int(activated));
		}
		if(lottery!=null && !lottery.equals(""))
		{
			sqlS.add("lottery",Tools.str2int(lottery));
		}
		if(method!=0)
		{
			sqlS.add("method",method);
		}
		
		String sql = "select * from tab_activate_code "+sqlS.whereStringEx()+" order by "+showorder+" "+ordertype;
		JSONObject json = getJsonPageListBySQL(sql, page, rows);
		return json;
	}
}
