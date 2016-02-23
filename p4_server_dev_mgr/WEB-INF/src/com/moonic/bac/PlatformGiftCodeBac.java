package com.moonic.bac;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Random;

import javax.servlet.ServletRequest;
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
import com.moonic.mgr.TabStor;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;

/**
 * 礼包码
 * @author 
 */
public class PlatformGiftCodeBac extends BaseActCtrl {
	public static String tbName = "tab_platform_gift_code";
	private static PlatformGiftCodeBac instance = null;

	public PlatformGiftCodeBac() {
		setDataBase(ServerConfig.getDataBase());
		setTbName(tbName);
	}
	
	/*
	 * number 数字类型
	 * character 字符类型
	 * all 数字字符混合型
	 */
	public static enum CodeType {NUMBER,CHARACTER,ALL};
	static char letter[]= { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H','J',  
            'K',  'M', 'N',  'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',  
            'X', 'Y', 'Z' };
	static char digit[]={'2', '3', '4', '5', '6', '7', '8', '9'};
	 
	public static PlatformGiftCodeBac getInstance(){
		if(instance==null)
		{
			instance=new PlatformGiftCodeBac();		
		}
		return instance;
	}
	
	private static String generate(CodeType type,int length){
		 StringBuffer sb=new StringBuffer();
			for (int i = 0; i < length; i++) 
			switch(type){
			case NUMBER:
				sb.append(digit[new Random().nextInt(digit.length)]);
				break;
			case CHARACTER:
				sb.append(letter[new Random().nextInt(letter.length)]);
				break;
			case ALL:
				sb.append((char)(new Random().nextInt(2)==0? letter[new Random().nextInt(letter.length)]:digit[new Random().nextInt(digit.length)]));
				break;
			}
	
		return sb.toString();
	}
	
	public int getTodayLimitGiftGiveCount(int giftCode)
	{
		DBHelper dbHelper=new DBHelper();
		
		try{
			dbHelper.openConnection();
			
			String timeStr = "15:00:00";
			
			String startTime = null;
			String endTime = null;
			//15:00前从昨天15:00为开始时间，15:00后从当天15:00到明天15:00
			int compare = Tools.compareStrDate(Tools.getCurrentDateTimeStr(), Tools.getCurrentDateStr()+" "+timeStr);
			if(compare>=0) //15:00后从当天15:00到明天15:00
			{							
				startTime = Tools.getCurrentDateStr()+" "+timeStr;
				endTime = Tools.getOffsetDateStr(Calendar.DAY_OF_MONTH, 1)+" "+timeStr;
			}
			else //15:00前从昨天15:00为开始时间
			{
				startTime = Tools.getOffsetDateStr(Calendar.DAY_OF_MONTH, -1)+" "+timeStr;
				endTime = Tools.getCurrentDateStr()+" "+timeStr;
			}
			
			SqlString sqlS = new SqlString();
			sqlS.addDate("publishtime", startTime,">=");
			sqlS.addDate("publishtime", endTime,"<");
			sqlS.add("giftcode",giftCode );
			int todaygive = dbHelper.queryCount("tab_platform_gift_code", sqlS.whereString());
			return todaygive;		
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}finally{
			dbHelper.closeConnection();
		}		
	}
	
	private static Object syncLock = new Object();
	private long lastms;
	private byte[] lastExcelBytes;
	private String lastExcelFilenames;
	
	/**
	 * 导出激活码给渠道
	 * @param pageContext
	 */
	public void exportCodeToExcel(PageContext pageContext)
	{
		synchronized(syncLock)
		{
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
			
			//System.out.println("System.currentTimeMillis()="+System.currentTimeMillis());
			//System.out.println("lastms="+lastms);
			//System.out.println("间隔="+(System.currentTimeMillis()-lastms));
			if(lastms>0)
			{
				if(System.currentTimeMillis()-lastms < 1000)
				{				
					//System.out.println("间隔太短请求无效"+(System.currentTimeMillis()-lastms));
					lastms = System.currentTimeMillis();
					try {				
						//String contenttype = "application/vnd.ms-excel";
						String contenttype="application/octet-stream";
						response.reset() ;
						response.setContentType(contenttype);
						
						response.setHeader("Content-Disposition", new String(("attachment;Filename="+lastExcelFilenames).getBytes("GBK"),"ISO8859-1"));
						OutputStream os = response.getOutputStream();						
						os.write(lastExcelBytes);
						os.flush();
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				else
				{
					lastms = System.currentTimeMillis();
				}
			}
			else
			{
				lastms = System.currentTimeMillis();
			}
			
			int amount = Tools.str2int(request.getParameter("amount"));
			String channel = request.getParameter("channel");
			int gift = Tools.str2int(request.getParameter("gift"));
			
						
			SqlString sqlS = new SqlString();
			sqlS.add("publish",0);
			
			if(channel!=null && !channel.equals(""))
			{
				sqlS.add("platform",channel);
			}
			if(gift>0)
			{
				sqlS.add("giftcode",gift);
			}	
			String giftname = TabStor.getListVal(TabStor.tab_platform_gift, "num="+gift, "name");
			
			sqlS.addWhere("rownum<="+amount);
			
				
			setTbName("tab_platform_gift_code");
			DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase());
			boolean haveData=false;
			try {
				dbHelper.openConnection();
				String sql = "select id,code,expiretime" +
						" from tab_platform_gift_code "+
						" where "+sqlS.whereString()+ 
						" order by id DESC";
				ResultSet rs = dbHelper.executeQuery(sql);
				
				if(rs!=null){					
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
					if(maxLen>0)
					{
						haveData=true;
					}
					if(maxLen>amount)
					{
						maxLen = amount;
					}
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
						cell.setCellValue(new HSSFRichTextString("礼包码"));		
						cell = row.createCell(2);	
						cell.setCellStyle(titleStyle);
						cell.setCellValue(new HSSFRichTextString("过期日期"));	
					}
					int rowIndex=titleRowIndex+1;										
					int resultNum=0;  //记录数				
					
					while(rs !=null && rs.next())
					{
						String code = rs.getString("code");
						int id = rs.getInt("id");
						String expiretime = Tools.strdate2shortstr(rs.getString("expiretime"));
						resultNum++;
					
						
						HSSFRow row = sheet[currentSheetIndex].createRow(rowIndex);
						HSSFCell cell = row.createCell(0);
						cell.setCellValue(resultNum); //Number
						cell.setCellStyle(centerStyle);					
						
						cell = row.createCell(1);	
						cell.setCellStyle(centerStyle);
						cell.setCellValue(new HSSFRichTextString(code)); //激活码
						
						cell = row.createCell(2);	
						cell.setCellStyle(centerStyle);
						cell.setCellValue(new HSSFRichTextString(expiretime)); //过期日期
					
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
						SqlString sqlS2 = new SqlString();
						sqlS2.add("publish", 1);
						sqlS2.addDateTime("publishtime", Tools.getCurrentDateTimeStr());
						dbHelper.update("tab_platform_gift_code", sqlS2, "id="+id);
						//update("PUBLISH=1,PUBLISHTIME="+Tools.getOracleDateTimeStr(Tools.getCurrentDateTimeStr()), "id="+id);
						
					}
					if(haveData)
					{										
						try {				
							//String contenttype = "application/vnd.ms-excel";
							String contenttype="application/octet-stream";
							response.reset() ;
							response.setContentType(contenttype);
							
							//response.setHeader("Content-Disposition", new String("inline;Filename=失败手机号码.xls".getBytes(),"ISO8859-1"));
							lastExcelFilenames = giftname+"领取码"+amount+"个.xls";
							response.setHeader("Content-Disposition", new String(("attachment;Filename="+lastExcelFilenames).getBytes("GBK"),"ISO8859-1"));
							OutputStream os = response.getOutputStream();
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							workbook.write(baos);							
							lastExcelBytes = baos.toByteArray();
							os.write(lastExcelBytes);
							os.flush();
							os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
						try {				
							//String contenttype = "application/vnd.ms-excel";
							String contenttype="text/html";
							response.reset() ;
							response.setContentType(contenttype);					
							
							//response.setHeader("Content-Disposition", new String("inline;Filename=失败手机号码.xls".getBytes(),"ISO8859-1"));
							//response.setHeader("Content-Disposition", new String(("attachment;Filename="+giftname+"领取码"+amount+"个.xls").getBytes("GBK"),"ISO8859-1"));
							OutputStream os = response.getOutputStream();
							/*<head>
							<title></title>
							<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">*/
							
							String html = "<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'></head><script>alert('没有数据');history.back()</script></html>";
							os.write(html.getBytes("UTF-8"));	
							os.flush();
							os.close();
							//System.out.println("生成html"+html);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch(Exception e){
				e.printStackTrace();			
			} finally {
				dbHelper.closeConnection();
			}
		}
	}
	
	public ReturnValue save(PageContext pageContext)
	{
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.setEncode("UTF-8");
		DBHelper dbHelper = new DBHelper();
		try {
			smartUpload.initialize(pageContext);
			smartUpload.upload();
			com.jspsmart.upload.Request request = smartUpload.getRequest();				
			
			int id=Tools.str2int(request.getParameter("id"));
			String platform=request.getParameter("platform");
			String code=request.getParameter("code");
			int giftcode=Tools.str2int(request.getParameter("giftcode"));
			int playerid=Tools.str2int(request.getParameter("playerid"));
			int gived=Tools.str2int(request.getParameter("gived"));
			String givetime=request.getParameter("givetime");
			int publish=Tools.str2int(request.getParameter("publish"));
			String publishtime=request.getParameter("publishtime");
			String phonenumber=request.getParameter("phonenumber");
			int serverid=Tools.str2int(request.getParameter("serverId"));
			int repeat=Tools.str2int(request.getParameter("repeat"));
			String expiretime=request.getParameter("expiretime");
			
			String opusername = (String)pageContext.getSession().getAttribute("username");
			
			//判断礼包领取码是否已存在
			if(id>0)  //修改
			{
				boolean exist = dbHelper.queryExist(tbName, "code='"+code+"' and id!="+id);
				if(exist)
				{
					return new ReturnValue(false,"领取码"+code+"已存在");
				}
			}
			else
			{
				boolean exist = dbHelper.queryExist(tbName,"code='"+code+"'");
				if(exist)
				{
					return new ReturnValue(false,"领取码"+code+"已存在");
				}
			}			
			
			FormXML formXML = new FormXML();
			formXML.add("platform",platform);
			formXML.add("code",code);
			formXML.add("giftcode",giftcode);
			formXML.add("playerid",playerid);
			formXML.add("gived",gived);
			formXML.addDateTime("givetime",givetime);
			formXML.add("publish",publish);
			formXML.addDateTime("publishtime",publishtime);
			formXML.add("phonenumber",phonenumber);
			formXML.add("serverid",serverid);
			formXML.add("repeat",repeat);
			formXML.addDate("expiretime",expiretime);
			formXML.add("createuser", opusername);
			formXML.addDateTime("createtime", MyTools.getTimeStr());
			
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
		finally
		{
			dbHelper.closeConnection();
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
	
	public JSONObject getPageList(PageContext pageContext)
	{
		ServletRequest request = pageContext.getRequest();
		int page=Tools.str2int(request.getParameter("page"));
		if(page==0)
		{
			page=1;
		}
		int rpp=Tools.str2int(request.getParameter("rpp"));
		if(rpp==0)
		{
			rpp=10;
		}
		
		String gived = request.getParameter("gived");
		String giftcode = request.getParameter("giftcode");
		String publish= request.getParameter("publish");
		String repeat = request.getParameter("repeat");
		
		String ordertype=request.getParameter("ordertype");
		if(ordertype==null || ordertype.equals(""))
		{
			ordertype="DESC";
		}
		String showorder=request.getParameter("showorder");
		if(showorder==null || showorder.equals(""))
		{
			showorder="tab_platform_gift_code.id";
		}
		
		
		String colname=request.getParameter("colname");			
		String colvalue=request.getParameter("colvalue");
		String channel = request.getParameter("channel");
		String operator = request.getParameter("operator");
		
		String serverId = request.getParameter("serverId");
		
		SqlString sqlS = new SqlString();
		String orderClause = showorder + " " + ordertype;	
		
		
		if(colvalue!=null && !colvalue.equals(""))
		{
			if(colname.equals("playerName"))
			{					
				JSONObject playerJson = PlayerBAC.getInstance().getJsonObjs("id","name='"+colvalue+"'",null);
				if(playerJson!=null)
				{
					JSONArray array = playerJson.optJSONArray("list");
					int[] playerids = null;
					for(int i=0;array!=null && i<array.length();i++)
					{
						JSONObject line = array.optJSONObject(i);							
						playerids = Tools.addToIntArr(playerids, line.optInt("id"));
					}
					sqlS.addWhere("tab_platform_gift_code.playerid in ("+Tools.intArr2Str(playerids)+")");
				}
				else
				{
					sqlS.addWhere("tab_platform_gift_code.playerid in (0)");
				}
			}
			else
			{
				if(operator.equals("包含"))
				{
					sqlS.add(colname, colvalue,"like");	
				}else
				if(operator.equals("等于"))
				{
					sqlS.add(colname, colvalue);	
				}	
			}			
		}	
		if(channel!=null && !channel.equals(""))
		{
			sqlS.add("tab_platform_gift_code.platform", channel);	
		}
		if(serverId!=null && !serverId.equals(""))
		{
			sqlS.add("tab_platform_gift_code.serverid", Tools.str2int(serverId));	
		}
		if(gived!=null && !gived.equals(""))
		{
			sqlS.add("tab_platform_gift_code.gived", Tools.str2int(gived));	
		}
		if(giftcode!=null && !giftcode.equals(""))
		{
			sqlS.add("tab_platform_gift_code.giftcode", Tools.str2int(giftcode));	
		}
		if(publish!=null && !publish.equals(""))
		{
			sqlS.add("tab_platform_gift_code.publish", Tools.str2int(publish));	
		}
		if(repeat!=null && !repeat.equals(""))
		{
			sqlS.add("tab_platform_gift_code.repeat", Tools.str2int(repeat));	
		}
		
		
		String sql = "select tab_platform_gift_code.*,tab_platform_gift.name as giftname,tab_server.name as servername,tab_channel.name as channelname,tab_player.name as playername from tab_platform_gift_code left join tab_platform_gift on tab_platform_gift_code.giftcode=tab_platform_gift.num left join tab_player on tab_platform_gift_code.playerid=tab_player.id left join tab_server on tab_platform_gift_code.serverid=tab_server.id left join tab_channel on tab_platform_gift_code.platform=tab_channel.code "+sqlS.whereStringEx()+" order by "+orderClause;					
		//System.out.println(sql);
		
		return getJsonPageListBySQL(sql, page, rpp);
	}
	
	public ReturnValue batchAddCode(PageContext pageContext)
	{
		ServletRequest request = pageContext.getRequest();
		//int serverid,String platformId, String  giftcode,CodeType type,int length, int amount
		String channel = request.getParameter("channel");
		int giftcode = Tools.str2int(request.getParameter("giftcode"));
		int len = Tools.str2int(request.getParameter("len"));
		int type = Tools.str2int(request.getParameter("type"));
		int amount = Tools.str2int(request.getParameter("amount"));
		String expiretime = request.getParameter("expiretime");
		//int serverId = Tools.str2int(request.getParameter("serverId"));
		CodeType codeType = null;
		if(type==1)
		{
			codeType = CodeType.ALL;
		}
		else
		if(type==2)
		{
			codeType = CodeType.CHARACTER;
		}
		else
		if(type==3)
		{
			codeType = CodeType.NUMBER;
		}
		/*if(serverId<=0)
		{
			return new ReturnValue(false,"必须选择游戏服");
		}*/
		if(channel==null || channel.equals(""))
		{
			return new ReturnValue(false,"必须选择渠道");
		}
		if(giftcode<=0)
		{
			return new ReturnValue(false,"必须选择礼包码");
		}
		if(len<=0)
		{
			return new ReturnValue(false,"礼包码位数必须大于0");
		}
		if(amount<=0)
		{
			return new ReturnValue(false,"数量必须大于0");
		}
		SqlString sqlStr = new SqlString();
		DBHelper dbHelper=new DBHelper();
		synchronized (this)
		{
			try{
				dbHelper.openConnection();
//				String now =Tools.getCurrentDateStr();
				setTbName("tab_platform_gift_code");
				int generateCount=0;
				String opusername = (String)pageContext.getSession().getAttribute("username");
				while(generateCount<amount)
				{
					String code=generate(codeType,len);
					//类型为ALL时遇到纯数字则重新生成
					if(codeType == CodeType.ALL && Tools.str2long(code)>0)
					{							
						continue;
					}
					boolean exist=dbHelper.queryExist(tbName,"code='"+code+"'");
					if(!exist)
					{
						sqlStr.add("code", code);
						//sqlStr.add("serverid", serverId);
						sqlStr.add("platform", channel);
						sqlStr.add("giftcode", giftcode);
						sqlStr.add("playerid", 0);
						sqlStr.add("gived", 0);
						sqlStr.add("publish", 0);
//						sqlStr.addDate("givetime", now); 
//						sqlStr.addDate("publishtime", now); 
						sqlStr.addDate("expiretime", expiretime);
						sqlStr.add("createuser", opusername);
						sqlStr.addDateTime("createtime", MyTools.getTimeStr());
						dbHelper.insert(tbName, sqlStr);
						sqlStr.clear();				
						generateCount++;
					}
				}
				return new ReturnValue(true,amount+"个礼包码生成成功");
			}catch(Exception ex){
				ex.printStackTrace();
				return new ReturnValue(false,ex.toString());
			}finally{
				dbHelper.closeConnection();
			}
		}
		//long e=System.currentTimeMillis();
		//System.out.println("本次生成"+amount+"个激活码总计耗时"+(e-s)+"毫秒");
	}
}
