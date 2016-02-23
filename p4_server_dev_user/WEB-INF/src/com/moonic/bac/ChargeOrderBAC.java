package com.moonic.bac;

import java.net.URLDecoder;
import java.sql.ResultSet;

import org.json.JSONException;
import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;
import server.config.ServerConfig;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.moonic.chargecenter.OrderCenter;
import com.moonic.mgr.PookNet;
import com.moonic.servlet.STSServlet;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.MD5;
import com.moonic.util.MyTools;
import com.moonic.util.NetClient;
import com.moonic.util.NetFormSender;
import com.moonic.util.STSNetSender;

/**
 * 充值订单支付接口
 * @author 黄琰
 *
 */
public class ChargeOrderBAC extends BaseActCtrl 
{
	public static String tbName = "tab_charge_order";
	
	private ChargeOrderBAC()
	{
		super.setTbName(tbName);
		setDataBase(ServerConfig.getDataBase());
	}		
	
	
	public ReturnValue updateCenterOrderNo(String orderNo, String cOrderNo)
	{
		SqlString sqlStr = new SqlString();
		sqlStr.add("corderNo", cOrderNo);		
		DBHelper dbHelper = new DBHelper();
		try
		{
			dbHelper.update(tbName, sqlStr, "orderNo='"+orderNo+"'");
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		}
		finally
		{
			dbHelper.closeConnection();
		}
	}
	
	public ReturnValue createCenterNewOrderWithoutCOrder(String orderNo, int orderType,int price,String username,String extend,String ordertime,String ip,int userSource)
	{
		if(extend==null || extend.equals(""))
		{
			return new ReturnValue(false,"缺少扩展参数extend");	
		}		
		
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			
			String channel="001"; //联运渠道默认为波克渠道			
			
			//String extendStr = URLDecoder.decode(extend,"UTF-8");
			int serverId=0;
			int playerId=0;
			int buyType=1;
			int systemtype=1; //默认安卓
			
			//ChannelChargeTypeBAC.getInstance().getCount("channel='"+channel+"' and chargetype="+orderType)
			
			String platform=null;
			int power = 0; //特权
			try
			{		
				JSONObject json = new JSONObject(extend);
				
				int vsid = json.optInt("serverId");
				DBPaRs channelServerRs = DBPool.getInst().pQueryA(ServerBAC.tab_channel_server, "vsid="+vsid);
				if(!channelServerRs.exist()){
					LogBAC.logout("chargecenter/"+channel,"服务器未找到 vsid="+vsid);
					return new ReturnValue(false,"服务器未找到 vsid="+vsid);	
				}
				serverId = channelServerRs.getInt("serverid");
				playerId = json.optInt("playerId");				
				buyType = json.optInt("buytype");
				power = json.optInt("power");
				channel = json.optString("channel");
				systemtype = json.optInt("system");
				if(systemtype==0)systemtype=1; //无参数默认安卓
				
				if(channel==null || channel.equals(""))
				{
					channel = "001";
				}
				if(buyType==0)
				{
					LogBAC.logout("chargecenter/"+channel,"扩展参数缺少buyType");
					return new ReturnValue(false,"必须选择购买类型");	
				}
				if(serverId==0)
				{
					LogBAC.logout("chargecenter/"+channel,"扩展参数缺少serverId");
					return new ReturnValue(false,"必须选择游戏服");	
				}
				if(playerId==0)
				{
					LogBAC.logout("chargecenter/"+channel,"扩展参数缺少playerId");
					return new ReturnValue(false,"游戏角色未选择");	
				}	
				/*try
				{
					platform= json.getString("platform");
				}
				catch(Exception ex)
				{
					
				}*/
				
			}
			catch(Exception ex)
			{
				LogBAC.logout("chargecenter/"+channel,channel+"渠道的订单"+orderNo+"扩展参数不是json格式"+extend);
				//System.out.println("订单"+orderNo+"扩展参数不是json格式"+extendStr);
			}				
			platform = ChannelBAC.getInstance().getChannelListRs(channel).getString("platform");
			
			/*if(platform==null || platform.equals(""))
			{
				platform=channel;
			}*/
			
			boolean exist = dbHelper.queryExist(tbName, "orderno='"+orderNo+"'");
			
			if(!exist)
			{		
				if(buyType==1)
				{
					power = 0; //修正客户端传来的power
				}
				//校验
				if(!ConfigBAC.getBoolean("chargetest"))
				{
					if(buyType!=1 && buyType!=2)
					{
						LogBAC.logout("chargecenter/"+channel,"buytype="+buyType+",购买类型非法");
						return new ReturnValue(false,"购买类型非法");	
					}
					if(buyType==1)
					{
						if(orderType==OrderCenter.iosInfullType)
						{
							if(price<6)
							{
								LogBAC.logout("chargecenter/"+channel,"buytype=1 price="+price+",购买金额至少6元");
								return new ReturnValue(false,"购买金额至少6元");
							}
						}
						else
						{
							if(price<10)
							{
								LogBAC.logout("chargecenter/"+channel,"buytype=1 price="+price+",购买金额至少10元");
								return new ReturnValue(false,"购买金额至少10元");
							}
						}
					}
					else
					if(buyType==2)
					{
						if(price>0)
						{							
							DBPaRs dbPaRs= DBPool.getInst().pQueryA("tab_prerogative", "price="+price);
							
							if(!dbPaRs.exist())
							{
								LogBAC.logout("chargecenter/"+channel,"buytype=2 price="+price+",特权购买金额非法");
								return new ReturnValue(false,"特权购买金额非法");
							}
							power = dbPaRs.getInt("num");

							if(power<=0)
							{
								LogBAC.logout("chargecenter/"+channel,"buytype=2 price="+price+",特权购买金额非法");
								return new ReturnValue(false,"特权购买金额非法");
							}			
						}
						else
						{
							LogBAC.logout("chargecenter/"+channel,"buytype=2 price="+price+",特权购买金额必须大于0元");
							return new ReturnValue(false,"特权购买金额必须大于0元");
						}
					}
				}
				
				int from=userSource; //1客户端 2 网站 
				/*if(orderType==1 || orderType==2 || orderType==3 || orderType==4 || orderType==5 || orderType==14 || orderType==99)
				{
					from=1;
				}
				else
				{
					from=2;
				}*/
				SqlString sqlStr = new SqlString();
				sqlStr.add("orderNo", orderNo);
				sqlStr.add("orderType", orderType);				
				sqlStr.add("fromWhere", from);
				sqlStr.add("buyType",buyType);
				sqlStr.add("price", price);
				sqlStr.add("serverid", serverId);
				sqlStr.add("playerId", playerId);
				sqlStr.add("channel", channel);
				sqlStr.add("platform", platform);
				sqlStr.add("corderType", orderType);
				sqlStr.add("chargecenter", 1);
				sqlStr.add("systemtype",systemtype);
				/*if(Tools.str2int(channel)<=100)
				{
					sqlStr.add("platform", channel); //channel大于100的都算自有账号渠道
				}
				else
				{
					sqlStr.add("platform", "001");  //channel大于100的都算波克账号渠道
				}*/
				
				sqlStr.add("username", username);
				sqlStr.add("extend", extend);
				sqlStr.add("getpower", power);
				sqlStr.add("ip", ip);
				sqlStr.addDateTime("ordertime", ordertime);
				sqlStr.addDateTime("savetime", MyTools.getTimeStr());
				sqlStr.add("result", 0);
				sqlStr.add("gived", 0);
				
				dbHelper.insert(tbName, sqlStr);
				//System.out.println("创建"+System.currentTimeMillis()+"创建新订单"+orderNo);
				
				LogBAC.logout("chargecenter/"+channel,"生成"+channel+"渠道新的订单"+orderNo+",serverId="+serverId+",username="+username+",playerId="+playerId+",orderType="+orderType+",buyType="+buyType+",price="+price+",platform="+platform+",extend="+extend+",power="+power);
				return new ReturnValue(true,"");	
			}
			else
			{				
				LogBAC.logout("chargecenter/"+channel,channel+"渠道的订单号"+orderNo+"已存在");
				return new ReturnValue(false,"订单号已存在");
				//return new ReturnValue(false,"same orderno is exist!");
			}
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	
	String spchannels="010,013,021,022,023"; //支付成功后不切换为最初注册渠道的玩家渠道
	
	/**
	 * 波克专用返回订单结果
	 * @param orderNo 订单号
	 * @param result 结果1成功 0失败
	 */
	public synchronized ReturnValue orderCallback(String channel,String orderNo, int result,String note,String ip,int realPayMoney)
	{
		DBHelper dbHelper = new DBHelper();
		try {
			JSONObject jsonObj=null;
			
			if(channel!=null && !channel.equals("")) //第三方带自己sdk的渠道
			{
				jsonObj = dbHelper.queryJsonObj("tab_charge_order", null, "orderno='"+orderNo+"' and channel='"+channel+"'");
			}
			else //波克渠道
			{				
				jsonObj = dbHelper.queryJsonObj("tab_charge_order", null, "orderno='"+orderNo+"'");						
			}
			if(jsonObj!=null)
			{				
				int orderResult =  jsonObj.optInt("result");
				int serverId = jsonObj.optInt("serverId");
				int playerId= jsonObj.optInt("playerId");
				short chargeType=(short)jsonObj.optInt("ordertype"); //1:支付宝 2:银联
				int money = jsonObj.optInt("price"); //人民币
				int buytype = jsonObj.optInt("buytype");
				byte powernum = (byte)jsonObj.optInt("getpower");
				int gived = jsonObj.optInt("gived"); //是否已发货
				int from=jsonObj.optInt("fromWhere"); //1客户端 2 网站 
				String sourceChannel = jsonObj.optString("channel"); //支付发起时的渠道
				String centerOrderNo = jsonObj.optString("corderno");//充值中心订单号
				
				if(realPayMoney < money)
				{
					return new ReturnValue(false,"价格不匹配,订单价格"+money+"实际只支付了"+realPayMoney);
				}
				
				
				if(channel==null || channel.equals("")) //波克渠道回调无channel
				{
					if(sourceChannel==null || sourceChannel.equals("") || spchannels.indexOf(sourceChannel)==-1) //不在特殊渠道里的
					{
						try
						{
							JSONObject json = dbHelper.queryJsonObj("tab_player", "userid", "id="+playerId);
							int userid = json.optInt("userid");
							json = dbHelper.queryJsonObj("tab_user", "channel", "id="+userid);				
							channel = json.optString("channel"); //玩家最初的渠道
							/*if(( channel.equals("009") || channel.equals("010") || channel.equals("018")) && chargeType!=99) //安智市场，应用汇，魅族，但是不是渠道专属充值替换成波克
							{
								channel ="001";
							}*/
							LogBAC.logout("charge/"+channel,"收到订单结果通知orderNo="+orderNo+",result="+result);
							SqlString sqlStr = new SqlString();
							sqlStr.add("channel", channel);
							dbHelper.openConnection();
							dbHelper.update(tbName, sqlStr, "orderno='"+orderNo+"'"); //更新订单的渠道为玩家的渠道
							dbHelper.closeConnection();
							LogBAC.logout("charge/","更新订单渠道为玩家的渠道"+ channel);
						}
						catch(Exception ex)
						{
							System.out.println("channel="+channel+",playerId="+playerId+",orderNo="+orderNo+",result="+result);
							ex.printStackTrace();
							return new ReturnValue(false,"更新订单渠道失败:"+ex.toString());
						}
					}
					else
					{
						channel = sourceChannel;
					}
				}
				
				if(orderResult==0 || (ConfigBAC.getBoolean("chargetest") && orderResult==-1) || (channel.equals("003") && orderResult==-1)) //未处理的订单,003渠道九游会有失败变成功的订单需特殊处理
				{
					SqlString sqlStr = new SqlString();
					if(result==1)
					{	
						if(buytype==1)
						{
							sqlStr.add("result", 1);
							sqlStr.add("gived", 2); //发货中
							sqlStr.add("getcoin", getCoinByRMB(chargeType, money));
							if(ip!=null)sqlStr.add("ip",ip);
							sqlStr.addDateTime("savetime", Tools.getCurrentDateTimeStr()); //更新时间为付款时间
							dbHelper.openConnection();
							int updateReslut = dbHelper.update(tbName, sqlStr, "orderno='"+orderNo+"' and channel='"+channel+"' and result=0");
							dbHelper.closeConnection();							
							
							if(updateReslut>0) //必须满足result=0 有更新记录时才通知充钱
							{
								//给玩家钻石或特权
								LogBAC.logout("charge/"+channel,channel+"渠道的订单"+orderNo+"支付成功,支付人民币="+money+",playerId="+playerId+",chargeType="+chargeType+",buytype="+buytype+",向游戏服"+serverId+"发送消息");
								STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_RECHARGE);
								sender.dos.writeByte(1);
								sender.dos.writeUTF(""); //成功描述
								sender.dos.writeByte(from); //来源
								sender.dos.writeUTF(channel); //联运渠道
								sender.dos.writeUTF(orderNo); //订单号
								sender.dos.writeUTF(centerOrderNo);//充值中心订单号
								sender.dos.writeInt(playerId);
								sender.dos.writeByte(0);//TODO 充值点：需要客户端发送，用于记录充值日志和报表统计
								sender.dos.writeShort(chargeType);
								sender.dos.writeInt(money);
								ServerBAC.getInstance().sendReqToOne(sender, serverId);
							}
						}
						else
						if(buytype==2)
						{		
							if(ConfigBAC.getBoolean("chargetest"))
							{
								//测试期处理
								sqlStr.add("result", 1);
								sqlStr.add("gived", 2); //发货中
								sqlStr.add("getpower", powernum);
								if(ip!=null)sqlStr.add("ip",ip);
								sqlStr.addDateTime("savetime", Tools.getCurrentDateTimeStr()); //更新时间为付款时间
								dbHelper.openConnection();
								int updateReslut = dbHelper.update(tbName, sqlStr, "orderno='"+orderNo+"' and channel='"+channel+"' and result=0");
								dbHelper.closeConnection();								
								
								if(updateReslut>0) //必须满足result=0 有更新记录时才通知充钱
								{
									//给玩家钻石或特权
									STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_BUY_TQ);
									sender.dos.writeByte(1);
									sender.dos.writeUTF(""); //成功描述
									sender.dos.writeByte(from); //来源
									sender.dos.writeUTF(channel); //联运渠道
									sender.dos.writeUTF(orderNo); //订单号
									sender.dos.writeUTF(centerOrderNo);//充值中心订单号
									sender.dos.writeInt(playerId);
									sender.dos.writeByte(powernum);
									ServerBAC.getInstance().sendReqToOne(sender, serverId);
									LogBAC.logout("charge/"+channel,channel+"渠道的订单"+orderNo+"支付成功,支付人民币="+money+",playerId="+playerId+",buytype="+buytype+",获得特权="+powernum+",向游戏服"+serverId+"发送消息");
								}
								
							}
							else
							{
								//正式处理
								dbHelper.openConnection();
								DBPaRs dbPaRs= DBPool.getInst().pQueryA("tab_prerogative", "price="+money);
								dbHelper.closeConnection();
								if(dbPaRs.exist())
								{
									sqlStr.add("result", 1);
									sqlStr.add("gived", 2); //发货中
									powernum = (byte)dbPaRs.getInt("num");
									sqlStr.add("getpower", powernum);
									if(ip!=null)sqlStr.add("ip",ip);
									sqlStr.addDateTime("savetime", Tools.getCurrentDateTimeStr()); //更新时间为付款时间
									dbHelper.openConnection();
									int updateReslut = dbHelper.update(tbName, sqlStr, "orderno='"+orderNo+"' and channel='"+channel+"' and result=0");
									dbHelper.closeConnection();									
									
									if(updateReslut>0) //必须满足result=0 有更新记录时才通知充钱
									{
										//给玩家钻石或特权
										STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_BUY_TQ);
										sender.dos.writeByte(1);
										sender.dos.writeUTF(""); //成功描述
										sender.dos.writeByte(from); //来源
										sender.dos.writeUTF(channel); //联运渠道
										sender.dos.writeUTF(orderNo); //订单号
										sender.dos.writeUTF(centerOrderNo);//充值中心订单号
										sender.dos.writeInt(playerId);
										sender.dos.writeByte(powernum);
										ServerBAC.getInstance().sendReqToOne(sender, serverId);
										LogBAC.logout("charge/"+channel,channel+"渠道的订单"+orderNo+"支付成功,支付人民币="+money+",playerId="+playerId+",buytype="+buytype+",获得特权="+powernum+",向游戏服"+serverId+"发送消息");
									}
								}
								else
								{
									//System.out.println("订单购买特权失败money="+money);
									LogBAC.logout("charge/"+channel,channel+"订单购买特权失败money="+money);
									
									sqlStr.add("result", -1);
									dbHelper.openConnection();
									int updateReslut = dbHelper.update(tbName, sqlStr, "orderno='"+orderNo+"' and channel='"+channel+"' and result=0");
									dbHelper.closeConnection();									
									
									if(updateReslut>0) //必须满足result=0 有更新记录时才通知充钱
									{									
										STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_BUY_TQ);
										sender.dos.writeByte(0);
										sender.dos.writeUTF("价格错误"); //失败描述
										sender.dos.writeByte(from); //来源
										sender.dos.writeUTF(channel); //联运渠道
										sender.dos.writeUTF(orderNo); //订单号
										sender.dos.writeUTF(centerOrderNo);//充值中心订单号
										sender.dos.writeInt(playerId);
										ServerBAC.getInstance().sendReqToOne(sender, serverId);
									}
								}
							}							
						}
						else
						{
							LogBAC.logout("charge/"+channel,channel+"渠道的订单"+orderNo+"的buytype="+buytype+"不支持,playerId="+playerId);
						}
					}
					else
					{
						sqlStr.add("result", -1);
						sqlStr.add("note", note);
						dbHelper.openConnection();
						dbHelper.update(tbName, sqlStr, "orderno='"+orderNo+"' and channel='"+channel+"'");
						dbHelper.closeConnection();
						if(buytype==1)
						{
							LogBAC.logout("charge/"+channel,channel+"渠道的钻石订单"+orderNo+"支付失败,支付人民币="+money+",playerId="+playerId+",向游戏服"+serverId+"发送消息");	
						}
						else
						if(buytype==2)
						{
							LogBAC.logout("charge/"+channel,channel+"渠道的特权订单"+orderNo+"支付失败,支付人民币="+money+",playerId="+playerId+",向游戏服"+serverId+"发送消息");	
						}
						else						
						{
							LogBAC.logout("charge/"+channel,channel+"渠道的订单"+orderNo+"支付失败,支付人民币="+money+",playerId="+playerId+",而且buytype="+buytype+"不支持");
						}
						if(buytype==2)
						{
							STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_BUY_TQ);
							sender.dos.writeByte(0);
							sender.dos.writeUTF("订单支付失败:"+note); //失败描述
							sender.dos.writeByte(from); //来源
							sender.dos.writeUTF(channel); //联运渠道
							sender.dos.writeUTF(orderNo); //订单号
							sender.dos.writeUTF(centerOrderNo);//充值中心订单号
							sender.dos.writeInt(playerId);
							ServerBAC.getInstance().sendReqToOne(sender, serverId);
						}
						else
						{
							STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_RECHARGE);
							sender.dos.writeByte(0);
							sender.dos.writeUTF("订单支付失败:"+note); //失败描述
							sender.dos.writeByte(from); //来源
							sender.dos.writeUTF(channel); //联运渠道
							sender.dos.writeUTF(orderNo); //订单号
							sender.dos.writeUTF(centerOrderNo);//充值中心订单号
							sender.dos.writeInt(playerId);
							ServerBAC.getInstance().sendReqToOne(sender, serverId);
						}						
					}
					//dbHelper.update(tbName, sqlStr, "orderno='"+orderNo+"'");
					
					return new ReturnValue(true,"");
				}
				else
				{
					//return new ReturnValue(false,"该订单已处理过");
					/*STSNetSender sender = new STSNetSender(STSServlet.G_PLAYER_RECHARGE);
					sender.dos.writeByte(0);
					sender.dos.writeUTF("该订单已处理过"); //失败描述
					sender.dos.writeByte(from); //来源
					sender.dos.writeUTF(channel); //联运渠道
					sender.dos.writeUTF(orderNo); //订单号
					sender.dos.writeUTF(centerOrderNo);//充值中心订单号
					sender.dos.writeInt(playerId);
					ServerBAC.getInstance().sendReqToOne(sender, serverId);*/
					//System.out.println("订单号"+orderNo+"已处理过");
					LogBAC.logout("charge/"+channel,channel+"渠道的订单号"+orderNo+"已处理过");
					//return new ReturnValue(false,"this order has been processed");
					return new ReturnValue(true,"订单号"+orderNo+"已处理过");
				}				
			}
			else
			{				
				LogBAC.logout("charge/"+channel,channel+"渠道的订单号"+orderNo+"不存在");
				return new ReturnValue(false,"orderno not exist");
			}
			
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}		
	}
	
	private static Object ordernoLock; //获取订单号的锁
	static
	{
		ordernoLock = new Object();
	}
	
	/**
	 * 获取下一订单编号
	 */
	public static String getNextOrderNo()
	{
		synchronized(ordernoLock) 
		{
			String orderno = String.valueOf(System.currentTimeMillis());
			try 
			{
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return orderno;
		}
	}
	
	/**
	 * 获取充值订单号
	 */
	public ReturnValue getChargeOrderno(String channel,String extend)
	{
		if(channel==null || channel.equals(""))
		{
			return new ReturnValue(false,"缺少渠道号");
		}
		String token=null;
		String refresh_token=null;
		if(channel.equals("008")) //360联运渠道刷新token
		{
			String appKey = "531ca7e66f8743fde3ea7b7cd68b52f4";
			String appSecret = "129d4c155bce4a5f6cc9a397014d9451";
			JSONObject json;
			try {
				json = new JSONObject(extend);
				refresh_token = json.optString("refresh_token");
				//https://openapi.360.cn/oauth2/access_token?grant_type=refresh_token&refresh_token=12065961868762ec8ab911a3089a7ebdf11f8264d5836fd41&client_id=0fb2676d5007f123756d1c1b4b5968bc&client_secret=8d9e3305c1ab18384f56.....&scope=basic
				String url = "https://openapi.360.cn/oauth2/access_token";
				//LogBAC.logout("charge/"+channel, "用户验证url="+url);
				NetClient netClient = new NetClient();
				netClient.setAddress(url);
				netClient.setContentType("application/x-www-form-urlencoded");
				String sendStr = "grant_type=refresh_token"
						+"&refresh_token="+refresh_token
						+"&client_id="+appKey
						+"&client_secret="+appSecret
						+"&scope=basic";
				
				netClient.ignoreSSL();
				netClient.setSendBytes(sendStr.getBytes());
				ReturnValue rv = netClient.send();
				if(rv.success)
				{
					try
					{
						String result = new String(rv.binaryData,"UTF-8");
						//{"access_token":"51274456c5021375a1d60d37e50d389efda2ebd703684fae","expires_in":"36000","scope":"basic","refresh_token":"512744561753b0725722cac52304a60b6bc5413e38708b53"}
						JSONObject resultJson = new JSONObject(result);
						token = resultJson.optString("access_token");
						refresh_token= resultJson.optString("refresh_token");
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						return new ReturnValue(false,"刷新toekn失败"+ex.toString());
					}
					//{ "access_token":"120652e586871bb6bbcd1c7b77818fb9c95d92f9e0b735873", "expires_in":"36000", "scope":"basic", "refresh_token":"12065961868762ec8ab911a3089a7ebdf11f8264d5836fd41 " }
				}
				else
				{
					return new ReturnValue(false,"刷新toekn失败"+rv.info);
				}
			} catch (JSONException e) {				
				e.printStackTrace();
				return new ReturnValue(false,"刷新toekn失败"+e.toString());
			}
		}
		
		String orderno = getNextOrderNo();
		JSONObject json;
		try {
			json = new JSONObject(extend);
			int price = json.getInt("price");
			String username = json.optString("username");
			//orderno ="ON"+orderno;
			
			ReturnValue rv=null;
			
			//String centerChannel = ConfigBAC.getString("centerChannel");
			
			//if(centerChannel!=null && !centerChannel.equals("") && centerChannel.indexOf(channel)!=-1)
			{
				//请求发到充值中心
				rv = OrderCenter.getInstance().sendToCenter(99, orderno, price, "0",username, 1, "", "0.0.0.0", "", "", "", "", "", extend,null);
				if(rv.success)
				{
					JSONObject orderJson = new JSONObject(rv.info);
					LogBAC.logout("sdk", "订单创建成功,中心返回字串="+rv.info);
					orderno = orderJson.optString("orderId"); //获取中心订单号
					
					LogBAC.logout("sdk", "订单创建成功,中心订单号="+orderno);
				}
				else
				{
					LogBAC.logout("sdk", "订单创建失败,原因="+rv.info);
				}
			}
			/*else
			{
				rv = ChargeOrderBAC.getInstance().createNewOrder(channel,orderno, 99, price,username, extend, Tools.getCurrentDateTimeStr(),"0.0.0.0");
			}*/
			
			if(rv.success)
			{				
				if(channel.equals("008")) //360联运渠道刷新token
				{
					//{"orderno":"33333":"token":"dddddd","refresh_token":"ddddd"}
					JSONObject returnJson = new JSONObject();
					returnJson.put("orderno", orderno);
					returnJson.put("token", token);
					returnJson.put("refresh_token", refresh_token);
					return new ReturnValue(true,returnJson.toString());
				}
				else
				{
					JSONObject returnJson = new JSONObject();
					returnJson.put("orderno", orderno);
					return new ReturnValue(true,returnJson.toString());
				}					
			}
			else
			{
				return new ReturnValue(false,"生成订单失败"+rv.info);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false,"生成订单失败"+e.toString());
		}
	}
	
	/**
	 * 计算人民币可购买的钻石
	 * @param rechargetype 支付方式
	 * @param rmb 人民币
	 */
	public int getCoinByRMB(int rechargetype, int rmb) 
	{
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			int buycoin = rmb * 10; //充值获得的钻石
			int rebatecoin = 0; //额外送的钻石
			//if(rechargetype==1 || rechargetype==2) //支付宝 | 银联
			/*{
				ResultSet rebateRs = dbHelper.query("TAB_RECHARGE_REBATE", "rebateam", "rechargeam<="+rmb, "rechargeam desc", 1);
				if(rebateRs.next()){
					rebatecoin = rebateRs.getInt("rebateam");
				}
			}*/
			int count_temp = rmb;
			while(true){
				ResultSet rebateRs = dbHelper.query("tab_recharge_rebate", "rechargeam,rebateam", "rechargeam<="+count_temp, "rechargeam desc", 1);
				if(!rebateRs.next()){
					break;
				}
				rebatecoin += rebateRs.getInt("rebateam");
				count_temp -= rebateRs.getInt("rechargeam");
			}
			return buycoin + rebatecoin;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取波克充值卡面额
	 */
	public ReturnValue getCardValue(String cardNum)
	{
		NetFormSender sender = new NetFormSender(PookNet.getcardvalue_do);

		sender.addParameter("platformType","6");
		sender.addParameter("cardNum",cardNum);
		String key = "x#2xilnx0t9x0opsr8";
		String sign = MD5.encode("6"+cardNum+key);
		sender.addParameter("signature",sign);
		try {
			sender.send().check();			
			 //{"ret":"S","msg":"","cardValue":10000}
			JSONObject json = new JSONObject(sender.rv.info);
			//System.out.println("波克点卡"+cardNum+"返回查询面额原始结果="+sender.rv.info);
			json.setForceLowerCase(false);
			if(json.optString("ret").equals("S"))
			{
				int cardValue = json.optInt("cardvalue");
				//波克点单位/1000转为元
				int price = cardValue / 1000;
				json.remove("cardvalue");
				json.put("cardValue", price);
				return new ReturnValue(true,json.toString());
			}
			else
			{
				return new ReturnValue(false,sender.rv.info);
			}
			//System.out.println("波克点卡"+cardNum+"返回查询面额结果="+json.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		}		
	}
	/**
	 * 检查渠道对应的支付类型是否存在
	 * @param channel
	 * @param chargeType
	 * @return
	 */
	public boolean checkChannelChargeType(String channel,int chargeType)
	{
		try
		{
			DBPaRs channelServerRs = DBPool.getInst().pQueryA("tab_channel_charge_type", "channel='"+channel+"' and chargeType="+chargeType);
			if(channelServerRs.exist())
			{
				return true;
			}
			else
			{
				return false;
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	//--------------静态区--------------
	
	private static ChargeOrderBAC self = new ChargeOrderBAC();
	
	public static ChargeOrderBAC getInstance()
	{
		return self;
	}	
}
