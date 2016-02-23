package com.moonic.bac;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.json.JSONObject;

import server.common.Tools;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.ehc.xml.FormXML;
import com.jspsmart.upload.SmartUpload;

/**
 * 发货单
 * @author John
 */
public class ChargeSendBAC extends BaseActCtrl
{	
	public static String tbName = "TAB_CHARGE_SEND";	 
	private static ChargeSendBAC self;	 
	
	public static ChargeSendBAC getInstance()
	{						
		if(self==null)
		{
			self = new ChargeSendBAC();
		}
		return self;
	}
	public ChargeSendBAC()
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
			int serverid=Tools.str2int(request.getParameter("serverid"));
			String channel=request.getParameter("channel");
			String orderno=request.getParameter("orderno");
			int playerid=Tools.str2int(request.getParameter("playerid"));
			int price=Tools.str2int(request.getParameter("price"));
			int getcoin=Tools.str2int(request.getParameter("getcoin"));
			int getpower=Tools.str2int(request.getParameter("getpower"));
			String savetime=request.getParameter("savetime");

			FormXML formXML = new FormXML();
			formXML.add("serverid",serverid);
			formXML.add("channel",channel);
			formXML.add("orderno",orderno);
			formXML.add("playerid",playerid);
			formXML.add("price",price);
			formXML.add("getcoin",getcoin);
			formXML.add("getpower",getpower);
			formXML.addDate("savetime",savetime);

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
	
	public JSONObject getJsonPageList(PageContext pageContext)
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
		String ordertype=request.getParameter("ordertype");
		if(ordertype==null || ordertype.equals(""))
		{
			ordertype="DESC";
		}
		String showorder=request.getParameter("showorder");
		if(showorder==null || showorder.equals(""))
		{
			showorder="TAB_CHARGE_SEND.savetime";
		}
		String colname=request.getParameter("colname");
		if(colname==null){colname="";}
		String operator=request.getParameter("operator");
		if(operator==null){operator="";}
		String colvalue=request.getParameter("colvalue");
		if(colvalue==null){colvalue="";}
		
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
		String orderClause = showorder + " " + ordertype;
		String sql = "select tab_player.name as playername,tab_charge_order.price,tab_charge_order.getcoin,tab_charge_order.getpower,TAB_CHARGE_SEND.*,tab_server.name as servername,tab_channel.name as channelname from TAB_CHARGE_SEND left join tab_server on TAB_CHARGE_SEND.serverid=tab_server.id left join tab_channel on TAB_CHARGE_SEND.channel=tab_channel.code left join tab_charge_order on TAB_CHARGE_SEND.orderno = tab_charge_order.orderno left join tab_player on tab_charge_order.playerid = tab_player.id  "+sqlS.whereStringEx()+" order by "+orderClause;					
		return getJsonPageListBySQL(sql, page, rpp);
	}
}
