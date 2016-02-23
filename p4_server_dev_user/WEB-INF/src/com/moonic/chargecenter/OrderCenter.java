package com.moonic.chargecenter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.impl.Log4JLogger;
import org.json.JSONException;
import org.json.JSONObject;

import pook.paycenter.exception.PayException;
import pook.paycenter.helper.QueryCardInfoHelper;
import pook.paycenter.helper.impl.*;
import pook.paycenter.response.*;
import pook.paycenter.response.msg.*;

import server.common.Tools;
import server.config.LogBAC;
import util.IPAddressUtil;

import com.ehc.common.Log4jConfigurer;
import com.ehc.common.ReturnValue;
import com.moonic.bac.ChargeOrderBAC;
import com.moonic.bac.UserBAC;
import com.moonic.mgr.PookNet;
import com.moonic.platform.*;
import com.moonic.util.DBHelper;
import com.moonic.util.MD5;
import com.moonic.util.NetFormSender;

import conf.Conf;

/**
 * 充值中心接口
 * @author alexhy
 *
 */
public class OrderCenter extends HttpServlet
{	
	public static int platformType = 10; //新口袋幻兽OL平台 		
	
	/*充值网站编码	编码
	1	6	       支付宝       
	2	6	     支付宝快充     
	3	12	      网上银行      
	4	12	      银联电子      
	5	5	     联通充值卡     
	6	4	     移动充值卡     
	7	3	     电信充值卡     
	8	14	    波克城市点卡    
	9	0	     骏网一卡通     
	10	0	     宝迪一卡通     
	11	0	      电信V 币      
	12	0	 移动短信(联动优势) 
	13	0	      网吧充值      
	14	0	    苹果APP 充值    
	15	9	联通充值卡(手机易宝)
	16	8	移动充值卡(手机易宝)
	17	10	电信充值卡(手机易宝)
	18	0	      移动基地      
	19	0	 骏网第三方页面充值 
	20	0	      联通VAC       
	21	1	     手机支付宝     
	22	0	      电信短信      
	23	0	      天翼空间      
	24	2	      手机银联      
	25	0	      移动MM        
	*/
	
	//private static short[][] orderTypeCenterMap; //网站充值中心和的订单类型编码映射
	private static String[][] merchantIdMap;
	static
	{
		/*orderTypeCenterMap = new short[][]{			
				{1,6},
				{2,6},
				{3,12},
				{4,12},
				{5,5},
				{6,4},
				{7,3},
				{8,14},
				{15,5},
				{16,4},
				{17,3},
				{21,1},
				{24,2},
				{99,99}
			};*/
		
		merchantIdMap = new String[][]{
				{"21","2088901623514629##shiyi"},
				{"24","802310048990794##shiyi"}
		};
	}
	
	static OrderCenter self;
	public static byte iosInfullType=14; //appstore
	public static byte unionInfullType=24; //银联
	public static byte zfbInfullType=21; //支付宝
	public static byte pookInfullType=8; //波克点卡
	public static byte CUCCInfullType=5; //联通充值卡
	public static byte CMCCInfullType=6; //移动充值卡
	public static byte CTCCInfullType=7; //电信充值卡
	
	
	public static OrderCenter getInstance()
	{
		if(self==null)
		{
			self = new OrderCenter();
		}
		return self;
	}
	/**
	 * 充值中心网站平台支付编号转编号
	 * @param orderType
	 * @return
	 */
	/*private int centerToXianmo(int orderType)
	{
		for(int i=0;i<orderTypeCenterMap.length;i++)
		{
			if(orderTypeCenterMap[i][0]==orderType)
			{
				return orderTypeCenterMap[i][1];
			}
		}
		return 0;
	}*/
	
	/**
	 * 支付类型对应的商户id
	 * @param infullType
	 * @return
	 */
	private String infullTypeToMerchant(String infullType)
	{
		for(int i=0;i<merchantIdMap.length;i++)
		{
			if(merchantIdMap[i][0].equals(infullType))
			{
				return merchantIdMap[i][1];
			}
		}
		return "10012063118";
	}
	public ReturnValue sendToCenter(int infullType,String platformOrder,int price,String userId,String userName,int userSource,String returnUrl,String ipString,String cardNo,String cardPwd,String bankValue,String otherParam,String merchantId,String extend,String iosData)
	{			
		int orderAmount = price; //金额（元）
		//if(orderAmount==null || orderAmount.equals(""))orderAmount="0";	
		//if(userId==null || userId.equals(""))userId="0";
		
		String notifyUrl = Conf.ms_url+"payBack.do";		
		
		//到充值中心去创建新订单		
		//NetFormSender sender = new NetFormSender(PookNet.chargecenter_do);
		

		userId="0"; //强制设为0
		
		if(ipString==null || ipString.equals(""))
		{
			ipString = "0.0.0.0";
			LogBAC.logout("chargecenter", "sendToCenter ipString异常为空");
		}
		JSONObject extendJson=null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (JSONException e1)
		{			
			e1.printStackTrace();
			return new ReturnValue(false,"扩展参数extend解析异常"+e1.toString());
		}
		int playerId=extendJson.optInt("playerId");	
		if(playerId>0)
		{
			DBHelper dbHelper = new DBHelper();
			try
			{				
				dbHelper.openConnection();
				//System.out.println("extend="+extend);
				
				//根据playerid查用户名					
				int uId = dbHelper.getIntValue("tab_player", "userid", "id="+playerId);
				userName = dbHelper.getStrValue("tab_user", "username", "id="+uId);
				userId = String.valueOf(uId);
				
				if(userName==null || userName.equals(""))
				{
					return new ReturnValue(false,"查询不到用户名");
				}
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
		/*if(userName==null || userName.equals(""))
		{
			DBHelper dbHelper = new DBHelper();
			try
			{				
				dbHelper.openConnection();
				//System.out.println("extend="+extend);
				
				//根据playerid查用户名					
				int uId = dbHelper.getIntValue("tab_player", "userid", "id="+playerId);
				userName = dbHelper.getStrValue("tab_user", "username", "id="+uId);
				userId = String.valueOf(uId);
				
				if(userName==null || userName.equals(""))
				{
					return new ReturnValue(false,"查询不到用户名");
				}
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
		}	*/
		//检查渠道的支付类型是否存在
		String channel = extendJson.optString("channel");
		boolean checkResult = ChargeOrderBAC.getInstance().checkChannelChargeType(channel, infullType);
		if(!checkResult)		
		{
			LogBAC.logout("chargecenter", "渠道"+channel+"的支付类型"+infullType+"不存在");
			return new ReturnValue(false,"渠道"+channel+"的支付类型"+infullType+"不存在");
		}
		int agentId = 0;
		
		try {
			//保存新订单到数据库中
			ReturnValue rv = ChargeOrderBAC.getInstance().createCenterNewOrderWithoutCOrder(platformOrder, infullType, orderAmount, userName, extend, Tools.getCurrentDateTimeStr(), ipString,userSource);
			if(rv.success)
			{
				LogBAC.logout("chargecenter", "子平台订单创建成功,platformOrder="+platformOrder);
				IResponse response=null;
				
				//给充值中心的用户名长度不能超过16，且不能包含*，否则用userId代替
				if(userName.length()>16 || userName.indexOf("*")!=-1)
				{
					userName = userId;
				}
				
				
				if(infullType==iosInfullType) //IOS
				{
					response =AppleInfullHelper.request(platformOrder, orderAmount, platformType, Tools.str2int(userId), userName, userSource, returnUrl, notifyUrl, ipString, iosData);					
				}
				else
				if(infullType==unionInfullType) //银联
				{					
					response =MobileUnionPayInfullHelper.request(platformOrder, platformType, infullType, orderAmount, Tools.str2int(userId), userName, agentId, returnUrl, notifyUrl, ipString);
				}
				else
				if(infullType==CUCCInfullType || infullType==CTCCInfullType || infullType==CMCCInfullType) //电信联通移动
				{					
					response = DirectCardInfullHelper.request(platformOrder, orderAmount, infullType, platformType, Tools.str2int(userId), userName, agentId, returnUrl, notifyUrl, cardNo,cardPwd, ipString);					
				}
				else
				if(infullType==pookInfullType)
				{
					//查询波克点卡面额
					QueryCardInfoResponse resp = (QueryCardInfoResponse) QueryCardInfoHelper.request(cardNo);
					int cardValue = resp.getValue();
					//LogBAC.logout("chargecenter", "QueryCardInfoHelper.request查询"+cardNo+"的面额为"+cardValue);
					
					if(orderAmount==cardValue)
					{
						//System.out.println("波克点卡");					
						response = PookCardInfullHelper.request(platformOrder, platformType, Tools.str2int(userId), userName, agentId, returnUrl, notifyUrl, cardNo,MD5.encode(cardPwd),ipString);
						//System.out.println("波克点卡调用完成");	
					}
					else
					{
						return new ReturnValue(false,"波克点卡面额为"+cardValue+"和订单价格"+orderAmount+"不一致无法支付");
					}
				}
				else
				{					
					response = GenerateInfullOrderHelper.request(platformOrder, platformType, orderAmount, infullType, Tools.str2int(userId), userName, userSource, returnUrl, notifyUrl, "", ipString);
				}
				InfullRequestResponse respInfo = (InfullRequestResponse)response;
				String cOrderNo = respInfo.getOrderNo();
				int cPrice = respInfo.getInfullAmount();
				//String message = response.getMsg();
				String ext = respInfo.getFormInfo();
				
				JSONObject json = new JSONObject();
				json.setForceLowerCase(false);
				
				String ver = extendJson.optString("ver");  //金立渠道的特殊参数
				
				if(channel.equals("018")) //步步高
				{
					P018 p018 =new P018();
					ReturnValue vivoRV = p018.getOrderInfo(cOrderNo, orderAmount);
					if(vivoRV.success)
					{
						String vivoExt = vivoRV.info;	
						
						json.put("orderId", cOrderNo);				
						json.put("ext", vivoExt);
					}
					else
					{
						ChargeOrderBAC.getInstance().updateCenterOrderNo(platformOrder, cOrderNo);
						return new ReturnValue(false,vivoRV.info);
					}
				}
				else
				if(channel.equals("009") && ver!=null && ver.equals("2")) //金立新sdk
				{
					String subject = extendJson.optString("subject");
					P009 p009 =new P009();
					ReturnValue gioneeRV = p009.getOrderInfo(playerId,cOrderNo, orderAmount,subject);
					if(gioneeRV.success)
					{
						String gioneeExt = gioneeRV.info;	
						
						json.put("orderId", cOrderNo);				
						json.put("ext", gioneeExt);
					}
					else
					{
						ChargeOrderBAC.getInstance().updateCenterOrderNo(platformOrder, cOrderNo);
						return new ReturnValue(false,gioneeRV.info);
					}
				}
				else
				{
					json.put("orderId", cOrderNo);				
					json.put("ext", ext);
				}				
				
				LogBAC.logout("chargecenter", "充值中心定单创建成功，返回cOrderNo="+cOrderNo+",ext="+ext);			
				//写入充值中心订单号
				ChargeOrderBAC.getInstance().updateCenterOrderNo(platformOrder, cOrderNo);
				return new ReturnValue(true,json.toString());
			}
			else
			{						
				String message = "订单创建失败："+rv.info+"(来自本地)";
				LogBAC.logout("chargecenter", message);
				return new ReturnValue(false,message);				
			}
		} catch (PayException e) {
			String message = "订单创建失败："+e.getMsg()+"(来自中心)PayException="+e.toString();
			LogBAC.logout("chargecenter", message);
			return new ReturnValue(false,"订单创建失败："+e.getMsg()+"(来自中心)");		
		} catch (Exception e) {
			e.printStackTrace();				
			String message = "订单创建失败："+e.toString()+"(来自本地)";			
			LogBAC.logout("chargecenter", message);
			return new ReturnValue(false,message);			
		}
	}
	protected void service(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException 
	{
		String ip = IPAddressUtil.getIp(request);
		LogBAC.logout("chargecenter", "------------------------------------------------");
		LogBAC.logout("chargecenter", "收到来自"+ip+"的新订单");
		Enumeration keysEnum = request.getParameterNames();
		//System.out.println(Tools.getCurrentDateTimeStr()+"-收到来自"+ip+"的请求");		
		while (keysEnum.hasMoreElements())
		{
			String key = (String) keysEnum.nextElement();
			LogBAC.logout("chargecenter", key + "=" + request.getParameter(key));			
		}		
		
		DataOutputStream dos = new DataOutputStream(response.getOutputStream());
		
		String infullType = Tools.strNull(request.getParameter("infullType")); //充值方式
		
		if(infullType==null || infullType.equals(""))
		{			
			dos.writeByte(0);			
			dos.write("缺少有效参数".getBytes("UTF-8"));
			dos.close();	
			return;
		}
		 
		String platformOrder = ChargeOrderBAC.getNextOrderNo(); //子平台订单号		
		String orderAmount = Tools.strNull(request.getParameter("orderAmount")); //金额（元）
		if(orderAmount==null || orderAmount.equals(""))orderAmount="0";
		String userId =Tools.strNull(request.getParameter("userId"));
		if(userId==null || userId.equals(""))userId="0";
		String userNameOrMobile = Tools.strNull(request.getParameter("userNameOrMobile"));
		String userName = Tools.strNull(request.getParameter("userName"));
		int userSource =Tools.str2int(request.getParameter("userSource"));
		String returnUrl=Tools.strNull(request.getParameter("returnUrl"));
		//String notifyUrl = Conf.ms_url+"payOrderCallback.do";
		String ipString = Tools.strNull(request.getParameter("ipString"));
		if(ipString==null || ipString.equals(""))
		{
			ipString = ip;
		}
		if(ipString==null || ipString.equals(""))
		{
			ipString = "0.0.0.0";
			LogBAC.logout("chargecenter", "ipString异常为空");
		}
		
		String cardNo = Tools.strNull(request.getParameter("cardNo"));
		String cardPwd = Tools.strNull(request.getParameter("cardPwd"));
		String bankValue = Tools.strNull(request.getParameter("bankValue"));
		String otherParam = Tools.strNull(request.getParameter("otherParam"));  //充值中心扩展参数
		String merchantId = infullTypeToMerchant(infullType); //商户id		
		String extend = Tools.strNull(request.getParameter("extend"));  //子平台扩展参数
		String iosData = Tools.strNull(request.getParameter("iosData"));  //ios付费参数
		
		if(userSource==0)
		{
			userSource=1;
		}
		
		if(userName==null || userName.equals(""))
		{
			userName = userNameOrMobile;
		}
		userId="0"; //强制设为0
		
		ReturnValue rv = sendToCenter(Tools.str2int(infullType),platformOrder,Tools.str2int(orderAmount),userId,userName,userSource,returnUrl,ipString,cardNo,cardPwd,bankValue,otherParam,merchantId,extend,iosData);
		
		if(rv.success)
		{
			dos.writeByte(1);
		}
		else
		{
			dos.writeByte(0);
		}
		dos.write(rv.info.getBytes("UTF-8"));
		dos.close();		
	}
	public static void main(String[] args)
	{
		/*
		
		4700033040	355a4x6r7y
		4700033039	6qykfsbkzz
		4700033038	2041f4mhm0
		4700033037	tx0351etg5
		4700033036	68m39p3drz
		4700033035	ddfkx0h41x
		4700033034	xhtk8h6xkt
		4700033033	hm54qgz74m*/

		
		String appendType = "12";
				
		String platformOrder =String.valueOf(System.currentTimeMillis());  //子平台订单号			
		String appendAmount = "10"; //金额(元)			
		String userId = "1";		//用户ID			
		String userName = "alexhy";		//用户名			
		String accountSource=""; //账号来源（可选）:暂未使用			
		String returnUrl = "http://xmlogintest.pook.com:82/xianmo_user/orderCenterCallback.do";		//回调地址:充值成功后的回调地址			
		String ipString = "118.242.16.50";		//用户IP			
		String cardNo = "4700033040";		//卡号（可选）			
		String cardPwd= "355a4x6r7y";	//密码（可选）			
		String bankValue= "";	//网上银行类型(可选)			
		String otherParam= "";	//其他参数（可选）
		
		
		String key = "79c3eea3f305d6b823f562ac4be35212";
		String url = "http://paytest.pook.com.cn/pay.jsp"; //测试地址
		NetFormSender sender = new NetFormSender(url);
		
		sender.addParameter("appendType",appendType);
		sender.addParameter("platformType",platformType);
		sender.addParameter("platformOrder",platformOrder);
		sender.addParameter("appendAmount",appendAmount); //10元
		sender.addParameter("userId",userId);
		sender.addParameter("userName",userName);
		sender.addParameter("userName",userName);
		sender.addParameter("returnUrl",returnUrl);
		sender.addParameter("ipString",ipString);		
		sender.addParameter("cardNo",cardNo);
		sender.addParameter("cardPwd",cardPwd);		
		sender.addParameter("ipString",ipString);
		
		StringBuffer ticket = new StringBuffer();
		ticket.append(appendType);
		ticket.append(platformType);
		ticket.append(platformOrder);
		ticket.append(appendAmount);
		ticket.append(userId);
		ticket.append(userName);			
		ticket.append(accountSource);
		ticket.append(returnUrl);
		ticket.append(ipString);
		ticket.append(cardNo);			
		ticket.append(cardPwd);
		ticket.append(bankValue);
		ticket.append(otherParam);
		//System.out.println("ticket="+ticket.toString());
		String sign = MD5.encode(ticket.toString()+key);
		//System.out.println("sign="+sign);
		sender.addParameter("sign", sign);
	
		try {
			sender.send().check();
			if(sender.rv.success)
			{
				//波克返回：{"code":"0001","orderId":"D201309091131073850","message":"延时充值中","ext":""}
				System.out.println("生成订单请求成功,波克返回："+sender.rv.info);
			}
			else
			{
				System.out.println("生成订单请求失败,波克返回："+sender.rv.info);
			}
		} catch (Exception e) {
			System.out.println("生成订单请求失败："+e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
