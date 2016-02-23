package com.moonic.chargecenter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.bac.ChargeOrderBAC;
import com.moonic.util.DBHelper;
import com.moonic.util.MD5;

import server.common.Tools;
import server.config.LogBAC;
import server.config.ServerConfig;
import util.IPAddressUtil;

/**
 * 充值中心接口
 * @author alexhy
 *
 */
public class OrderCenterCallback extends HttpServlet
{
	//private String channel="001";
	//private String channelName="波克";	
	protected void service(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException 
	{		
		String ip = IPAddressUtil.getIp(request);
		LogBAC.logout("chargecenter", "------------------------------------------------");
		LogBAC.logout("chargecenter", "收到来自"+ip+"的订单结果");
		Enumeration keysEnum = request.getParameterNames();
		while (keysEnum.hasMoreElements())
		{
			String key = (String) keysEnum.nextElement();
			LogBAC.logout("chargecenter", key + "=" + request.getParameter(key));
		}
		/*System.out.println("收到来自"+ip+"的订单结果---------------");		
		InputStream is = request.getInputStream();
		byte[] bytes = Tools.getBytesFromInputstream(is);
		is.close();
		String str = new String(bytes,"UTF-8");
		System.out.println("结果内容="+str);*/
		
		
		//sign=cebc0f71afdc2aa7625a98073cc89056&message=%E5%85%85%E5%80%BC%E6%88%90%E5%8A%9F&appendAmount=10&code=0000&platformOrder=1378705983589&orderId=D201309091353044753
		String code = request.getParameter("code");  //订单状态
		String orderId = request.getParameter("orderId"); //中心订单号
		String platformOrder= request.getParameter("platformOrder"); //平台订单号
		String orderAmount = request.getParameter("orderAmount"); //订单金额
		String message = request.getParameter("message"); //成功/失败提示
		String aiId = request.getParameter("aiId"); //收款账号ID，需要记录入数据库
		String sign = request.getParameter("sign"); //签名
		
		if(code==null)
		{
			OutputStream os = response.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.write("缺少有效参数".getBytes("UTF-8"));
			dos.close();
			return;
		}
		
		if(platformOrder!=null)platformOrder = platformOrder.trim();
		
		String key = "d3ceb5881a0a1fdaad01296d7554868e";
		StringBuffer ticket = new StringBuffer();
		ticket.append(code);
		ticket.append(orderId);
		ticket.append(platformOrder);
		ticket.append(orderAmount);
		ticket.append(message);
		ticket.append(aiId);
		String mySign = MD5.encode(ticket + key);
		
		JSONObject returnJson = new JSONObject();
		
		if(mySign.equals(sign))
		{
			if(code.equals("0000"))
			{
				LogBAC.logout("chargecenter", "验签成功，给玩家订单"+platformOrder+"充值");
				//System.out.println("验签成功，给玩家订单"+platformOrder+"充值");
				//todo 给玩家充值
				ReturnValue rv = ChargeOrderBAC.getInstance().orderCallback(null,platformOrder.trim(), 1,"",ip,Tools.str2int(orderAmount));
				//System.out.println("时间="+System.currentTimeMillis()+"给玩家订单"+platformOrder+"充值结果="+rv.success+","+rv.info);
				LogBAC.logout("chargecenter", "给玩家订单"+platformOrder+"充值结果="+rv.success+","+rv.info);
				if(rv.success)
				{
					returnJson.put("code", "0000");
					returnJson.put("message", "订单充值成功");
				}
				else
				{
					LogBAC.logout("chargecenter","订单"+platformOrder+"充值失败:"+rv.info);
					returnJson.put("code", "0200");
					returnJson.put("message", rv.info);
				}	
			}
			else
			{
				ReturnValue rv = ChargeOrderBAC.getInstance().orderCallback(null,platformOrder, 0,message,ip,Tools.str2int(orderAmount));
				
				returnJson.put("code", "0000");
				returnJson.put("message", "接收成功");
				LogBAC.logout("chargecenter","订单"+platformOrder+"充值失败,code="+code+",message="+message+",orderId="+orderId);
			}
		}
		else
		{
			ReturnValue rv = ChargeOrderBAC.getInstance().orderCallback(null,platformOrder, 0,"签名校验不匹配",ip,Tools.str2int(orderAmount));
			
			LogBAC.logout("chargecenter","签名校验不匹配,sign="+sign+",mySign="+mySign);			
			returnJson.put("code", "0102");
			returnJson.put("message", "签名校验不匹配");			
		}
		/*Enumeration keysEnum = request.getParameterNames();
		while(keysEnum.hasMoreElements())
		{
			String key = (String)keysEnum.nextElement();
			LogBAC.logout("charge/center",key+"="+request.getParameter(key));
			//System.out.println(key+"="+request.getParameter(key));
		}*/
		
		/*例：	失败：	{"code":"0102","message":"签名被篡改"}			
		成功：	{"code":"0000","message":"订单充值成功"}			
			{"code":"0200","message":"充值失败"}		*/	

		//System.out.println("返回中心字串="+returnJson.toString());
		
		OutputStream os = response.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		dos.write(returnJson.toString().getBytes("UTF-8"));
		dos.close();
	}

}
