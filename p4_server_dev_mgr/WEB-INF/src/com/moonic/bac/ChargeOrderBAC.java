package com.moonic.bac;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.SmartUpload;
import com.moonic.servlet.STSServlet;
import com.moonic.util.DBHelper;
import com.moonic.util.NetResult;
import com.moonic.util.STSNetSender;

/**
 * 充值订单支付接口
 * @author 黄琰
 */
public class ChargeOrderBAC extends BaseActCtrl
{
	public static String tbName = "TAB_CHARGE_ORDER";
	private static ChargeOrderBAC self;
	
	public static ChargeOrderBAC getInstance()
	{				
		if(self==null)
		{
			self = new ChargeOrderBAC();
		}
		return self;
	}
	
	private ChargeOrderBAC()
	{
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase_Backup());
	}
	
	/**
	 * 查询所有订单
	 */
	//public JSONObject queryAllUserOrderLog(int serverId,String date,String name,String uName,String orderNum,String orderType,String buyType,int result,int currentPage,int row)
	public JSONObject queryAllUserOrderLog(HttpServletRequest request)
	{		
		int serverId = Tools.str2int(request.getParameter("serverId"));
		String channel=request.getParameter("channel");
		String startdate = Tools.strNull(request.getParameter("startdate"));
		String enddate = Tools.strNull(request.getParameter("enddate"));
		int page = Tools.str2int(request.getParameter("page"), 1);	
		if(page<=0)page=1;	 
		String pname=request.getParameter("pname");
		String playerId=request.getParameter("playerId");
		String uname=request.getParameter("username");		
		String orderNo=request.getParameter("orderNo");		
		int rows = Tools.str2int(request.getParameter("rows"));
		if(rows<=0)rows=10;	 
		String orderType=request.getParameter("orderType");
		String buyType=request.getParameter("buyType");		
		String result=request.getParameter("result");
		//String gived = request.getParameter("gived");		
		String sendresult = request.getParameter("sendresult");
		String getpower=request.getParameter("getpower");	
		int systemType=Tools.str2int(request.getParameter("systemType"));
		String corderNo=request.getParameter("corderNo");	
		//if(result==null)result="1";
		
		SqlString sqlStr=new SqlString();
		SqlString sqlStrTotal=new SqlString();
		if(serverId>0)
		{
			sqlStr.add("O.SERVERID", serverId);
			sqlStrTotal.add("O.SERVERID", serverId);
		}
		if(channel!=null && !channel.equals(""))
		{
			sqlStr.add("O.channel", channel);
			sqlStrTotal.add("O.channel", channel);
		}
		if(pname!=null && !pname.equals(""))
		{
			sqlStr.add("P.NAME", pname);
			sqlStrTotal.add("P.NAME", pname);
		}
		if(playerId!=null && !playerId.equals(""))
		{
			sqlStr.add("O.playerid", playerId);
			sqlStrTotal.add("O.playerid", playerId);
		}
		if(uname!=null && !uname.equals(""))
		{
			sqlStr.add("O.USERNAME", uname);
			sqlStrTotal.add("O.USERNAME", uname);
		}
		
		if(orderNo!=null && !orderNo.equals(""))
		{
			sqlStr.add("O.ORDERNO", orderNo);
			sqlStrTotal.add("O.ORDERNO", orderNo);
		}
		if(corderNo!=null && !corderNo.equals(""))
		{
			sqlStr.add("O.corderNo", corderNo);
			sqlStrTotal.add("O.corderNo", corderNo);
		}
		
		if(orderType!=null && !orderType.equals(""))
		{
			sqlStr.add("O.ORDERTYPE", Tools.str2int(orderType));
			sqlStrTotal.add("O.ORDERTYPE", Tools.str2int(orderType));
		}
		if(buyType!=null && !buyType.equals(""))
		{
			sqlStr.add("O.BUYTYPE", Tools.str2int(buyType));
			sqlStrTotal.add("O.BUYTYPE", Tools.str2int(buyType));
		}
		if(result!=null && !result.equals(""))
		{
			sqlStr.add("O.RESULT", result);				
		}
		if(getpower!=null && !getpower.equals(""))
		{
			sqlStr.add("O.getpower", Tools.str2int(getpower));
			sqlStrTotal.add("O.getpower", Tools.str2int(getpower));
		}
		if(systemType>0)
		{
			sqlStr.add("O.systemtype", systemType);
			sqlStrTotal.add("O.systemtype", systemType);
		}
		
		sqlStrTotal.add("O.RESULT", 1);
		/*if(result==null || result.equals("") || result.equals("1"))
		{
			sqlStrTotal.add("O.RESULT", 1);
		}*/
		
		
		if(sendresult!=null && !sendresult.equals(""))
		{
			if(sendresult.equals("1"))
			{
				sqlStr.addWhere("(O.gived=1 or S.result=1)");
			}
			else
			if(sendresult.equals("0"))
			{
				sqlStr.add("O.gived",0);
			}
			else
			if(sendresult.equals("2"))
			{
				sqlStr.add("O.gived",2);
			}
			else
			if(sendresult.equals("-1"))
			{
				sqlStr.addWhere("(O.gived=-1 or S.result=-1)");
			}
		}		
			
		if(startdate!=null && !startdate.equals(""))
		{
			/*Calendar newcal = Calendar.getInstance();
			newcal.setTime(Tools.shortstr2date(startdate));
			newcal.add(newcal.DAY_OF_MONTH, 1);
			SimpleDateFormat sf=new  SimpleDateFormat("yyyy-MM-dd");
			String nextDate =sf.format(newcal.getTime());*/
			sqlStr.addDate("O.SAVETIME", startdate, ">=");
			sqlStrTotal.addDate("O.SAVETIME", startdate, ">=");
			//sqlStr.addDate("O.SAVETIME", nextDate, "<");
		}
		if(enddate!=null && !enddate.equals(""))
		{
			Calendar newcal = Calendar.getInstance();
			newcal.setTime(Tools.str2date(enddate));
			newcal.add(Calendar.DAY_OF_MONTH, 1);
			SimpleDateFormat sf=new  SimpleDateFormat("yyyy-MM-dd");
			String nextDate =sf.format(newcal.getTime());
			sqlStr.addDate("O.SAVETIME", nextDate, "<");
			sqlStrTotal.addDate("O.SAVETIME", nextDate, "<");
		}
		//查询充值金额合计
		long totalmoney=0;
		long totalgetcoin=0;
		//System.out.println(sqlStrTotal.whereStringEx());
		String sql = "select sum(O.price) as totalmoney,sum(O.getcoin) as totalgetcoin FROM TAB_CHARGE_ORDER O LEFT JOIN TAB_PLAYER P ON O.PLAYERID=P.ID "+sqlStrTotal.whereStringEx();
		
		JSONObject totalJson = getJsonObjs(sql);
		if(totalJson!=null)
		{
			JSONArray array = totalJson.optJSONArray("list");
			if(array!=null)
			{
				JSONObject line = array.optJSONObject(0);
				totalmoney = line.optLong("totalmoney");
				totalgetcoin= line.optLong("totalgetcoin");
			}
		}
		//System.out.println(totalJson);
		//查询分页数据
		sql="SELECT O.*,P.NAME,S.result as sendResult,C.name as channelname FROM TAB_CHARGE_ORDER O LEFT JOIN TAB_PLAYER P ON O.PLAYERID=P.ID left join tab_charge_send S on O.orderno=S.orderNo and O.channel=S.channel and O.serverId = S.serverId left join tab_channel C on O.channel = C.code "+sqlStr.whereStringEx()+"  ORDER BY O.SAVETIME DESC ";
		JSONObject json = getJsonPageListBySQL(sql, page, rows);
		if(json!=null)
		{
			json.put("totalMoney", totalmoney);
			json.put("totalgetcoin", totalgetcoin);
		}				
		return json;
	}
	
	public JSONObject getChargeDataByServer(DBHelper dbh,int serverid){
		if(dbh==null)
		dbh=new DBHelper();
		JSONObject	jsObj=new JSONObject();
		try {
			dbh.openConnection();
			String sql="SELECT COUNT(DISTINCT PLAYERID) AS CHARGEPLAYERS,COUNT(PLAYERID) AS CHARGETIMES,SUM(PRICE) AS AMOUNT  FROM  TAB_CHARGE_ORDER WHERE RESULT=1 AND SERVERID="+serverid;
			ResultSet res=dbh.executeQuery(sql);
			res.next();
			jsObj.put("chargeplayers", res.getInt("CHARGEPLAYERS"));
			jsObj.put("chargetimes", res.getInt("CHARGETIMES"));
			jsObj.put("amount", res.getInt("AMOUNT"));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbh.closeConnection();
		}
		return jsObj;
	}
	
	/**
	 * 订单批量补发货
	 */
	public ReturnValue orderBatchGive(HttpServletRequest request)
	{
		int batch = Tools.str2int(request.getParameter("batch"));
		if(batch==1)
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -10);
			String time = Tools.date2str(cal.getTime());
			DBHelper dbHelper = new DBHelper();
			try
			{
				StringBuffer sb = new StringBuffer();
				dbHelper.openConnection();
				SqlString sqlS = new SqlString();
				sqlS.add("result", 1);
				sqlS.add("gived", 1,"<>");
				
				sqlS.addDateTimePrepare("savetime", time,"<=");
				ResultSet rs = dbHelper.query("tab_charge_order", "distinct serverid",sqlS.whereString());
				while(rs!=null && rs.next())
				{
					//System.out.println(rs.getInt("serverId"));
					int serverId = rs.getInt("serverId");
					/*String orderNo = json.optString("orderno");
						String channel = json.optString("channel");
						int orderResult = json.optInt("result");
						int buytype = json.optInt("buytype");
						int gived = json.optInt("gived");
						int playerId = json.optInt("playerId");
						int orderType = json.optInt("orderType");
						int price = json.optInt("price");
						int getpower= json.optInt("getpower");
						String note = json.optString("note");*/
					JSONObject json = getJsonObjs("orderno,channel,result,buytype,gived,playerId,orderType,price,getpower,note", "serverId="+serverId+" and result=1 and gived<>1","id asc");
					
					if(json!=null)
					{	
						int total = json.optInt("totalrecord");
						try 
						{
							byte[] bytes = json.toString().getBytes("UTF-8");
							STSNetSender sender = new STSNetSender(STSServlet.G_ORDER_BATCH_GIVE);
							sender.dos.writeByte(0);//TODO 充值点：需要客户端发送
							sender.dos.writeInt(bytes.length);
							sender.dos.write(bytes);					
							NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverId);
							if(nr.rv.success)
							{
								LogBAC.logout("charge_regive","游戏服(id="+serverId+")补单"+total+"条提交成功,补单内容="+json.toString());
								sb.append("游戏服(id="+serverId+")补单"+total+"条提交成功\\r\\n");	
							}
							else
							{
								LogBAC.logout("charge_regive","游戏服(id="+serverId+")补单提交失败："+nr.rv.info);
								sb.append("游戏服(id="+serverId+")补单"+total+"条提交失败："+nr.rv.info+"\\r\\n");
							}
						} 
						catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							return new ReturnValue(false, e.toString());
						} catch (Exception e) {
							e.printStackTrace();
							return new ReturnValue(false, e.toString());			
						}
					}					
				}
				if(sb.length()>0)
				{					
					return new ReturnValue(true,sb.toString());
				}
				else
				{
					return new ReturnValue(false,"没有符合条件的单子");
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				dbHelper.closeConnection();
			}
			return new ReturnValue(true,"对10分钟前的单子批量补单成功");
		}
		else
		{
			int serverId = Tools.str2int(request.getParameter("serverId"));
			String channel = request.getParameter("channel");
			String orderNo = request.getParameter("orderNo");
			
			JSONObject json = getJsonObjs("orderno,channel,result,buytype,gived,playerId,orderType,price,getpower,note","channel='"+channel+"' and orderNo='"+orderNo+"' and result=1 and gived<>1","id asc");
			//System.out.println(json);
			if(json!=null)
			{
				try {
					byte[] bytes = json.toString().getBytes("UTF-8");
					STSNetSender sender = new STSNetSender(STSServlet.G_ORDER_BATCH_GIVE);
					sender.dos.writeByte(0);//TODO 充值点：需要客户端发送
					sender.dos.writeInt(bytes.length);
					sender.dos.write(bytes);					
					NetResult nr = ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverId);
					if(nr.rv.success)
					{
						LogBAC.logout("charge_regive","补单请求已提交游戏服("+serverId+")游戏服(id="+serverId+")，orderNo="+orderNo);
						return new ReturnValue(true, "补单请求已提交游戏服("+serverId+")");	
					}
					else
					{
						LogBAC.logout("charge_regive","补单请求提交游戏服("+serverId+")失败:"+nr.rv.info);
						return new ReturnValue(false, "补单请求提交游戏服("+serverId+")失败:"+nr.rv.info);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return new ReturnValue(false, e.toString());
				} catch (Exception e) {
					e.printStackTrace();
					return new ReturnValue(false, e.toString());			
				}
			}
			else
			{
				return new ReturnValue(false, "单子不符合补单条件");
			}
		}		
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
			String chargecenter=request.getParameter("chargecenter");
			String corderno=request.getParameter("corderno");
			int cordertype=Tools.str2int(request.getParameter("cordertype"));
			String orderno=request.getParameter("orderno");
			int ordertype=Tools.str2int(request.getParameter("ordertype"));
			int price=Tools.str2int(request.getParameter("price"));
			String extend=request.getParameter("extend");
			int result=Tools.str2int(request.getParameter("result"));
			String username=request.getParameter("username");
			String ordertime=request.getParameter("ordertime");
			String savetime=request.getParameter("savetime");
			String ip=request.getParameter("ip");
			int serverid=Tools.str2int(request.getParameter("serverid"));
			int playerid=Tools.str2int(request.getParameter("playerid"));
			String channel=request.getParameter("channel");
			int getcoin=Tools.str2int(request.getParameter("getcoin"));
			String buytype=request.getParameter("buytype");
			int getpower=Tools.str2int(request.getParameter("getpower"));
			String platform=request.getParameter("platform");
			String gived=request.getParameter("gived");
			String note=request.getParameter("note");
			String fromwhere=request.getParameter("fromwhere");

			
			FormXML formXML = new FormXML();
			formXML.add("chargecenter",chargecenter);
			formXML.add("corderno",corderno);
			formXML.add("cordertype",cordertype);
			formXML.add("orderno",orderno);
			formXML.add("ordertype",ordertype);
			formXML.add("price",price);
			formXML.add("extend",extend);
			formXML.add("result",result);
			formXML.add("username",username);
			formXML.addDate("ordertime",ordertime);
			formXML.addDate("savetime",savetime);
			formXML.add("ip",ip);
			formXML.add("serverid",serverid);
			formXML.add("playerid",playerid);
			formXML.add("channel",channel);
			formXML.add("getcoin",getcoin);
			formXML.add("buytype",buytype);
			formXML.add("getpower",getpower);
			formXML.add("platform",platform);
			formXML.add("gived",gived);
			formXML.add("note",note);
			formXML.add("fromwhere",fromwhere);
			
			if(id>0)  //修改
			{	
				formXML.setAction(FormXML.ACTION_UPDATE);
				formXML.setWhereClause("id=" + id);
				setDataBase(ServerConfig.getDataBase());
				ReturnValue rv = save(formXML);	
				setDataBase(ServerConfig.getDataBase_Backup());
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
				setDataBase(ServerConfig.getDataBase());
				ReturnValue rv =save(formXML);
				setDataBase(ServerConfig.getDataBase_Backup());
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
}
