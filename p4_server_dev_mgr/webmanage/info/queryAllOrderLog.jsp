<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%  
	String model = "充值管理";
	String perm = "充值总览";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
boolean root = false;
if(userObj != null) 
{
	root=userObj.optBoolean("root");
}
int serverId = Tools.str2int(request.getParameter("serverId"));
String channel = Tools.strNull(request.getParameter("channel"));
String startdate = Tools.strNull(request.getParameter("startdate"));
String enddate = Tools.strNull(request.getParameter("enddate"));
String pagenum=request.getParameter("page");
if(pagenum==null || pagenum.equals("")) 
{
	pagenum="1";
}
String pname=Tools.strNull(request.getParameter("pname"));
String playerId=Tools.strNull(request.getParameter("playerId"));
String uname=Tools.strNull(request.getParameter("username"));	
String orderNo=Tools.strNull(request.getParameter("orderNo"));
String corderNo=Tools.strNull(request.getParameter("corderNo"));
int systemType = Tools.str2int(request.getParameter("systemType"));

int rows = Tools.str2int(request.getParameter("rows"));
if(rows<=0)rows=10;	 
String orderType=Tools.strNull(request.getParameter("orderType"));	
String buyType=Tools.strNull(request.getParameter("buyType"));
String result=request.getParameter("result");
String getpower=Tools.strNull(request.getParameter("getpower"));
//String givedSelect = Tools.strNull(request.getParameter("gived"));
String sendresult = Tools.strNull(request.getParameter("sendresult"));
JSONObject xml = null;
int isstat = Tools.str2int(request.getParameter("isstat"));
if(isstat==1){
	xml = ChargeOrderBAC.getInstance().queryAllUserOrderLog(request);
}

//if(result==null)result="1";
//System.out.println(xml);

long totalMoney = 0;
long totalGetcoin = 0 ;
if(xml!=null)
{
	totalMoney = xml.optLong("totalMoney");
	totalGetcoin = xml.optLong("totalgetcoin");
}
%>

<script>
var allValue=new Object();
allValue.channel="<%=channel%>";
allValue.startdate="<%=startdate%>";
allValue.enddate="<%=enddate%>";
allValue.serverId="<%=serverId%>";
allValue.pname="<%=pname%>";
allValue.playerId="<%=playerId%>";
allValue.username="<%=uname%>";
allValue.buyType="<%=buyType%>";
allValue.orderType="<%=orderType%>";
allValue.orderNo="<%=orderNo%>";
allValue.corderNo="<%=corderNo%>";
allValue.rows="<%=rows%>";
allValue.result="<%=result%>";
allValue.sendresult="<%=sendresult%>";
allValue.getpower="<%=getpower%>";
allValue.isstat="<%=isstat%>";
allValue.systemType="<%=systemType%>";
</script>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="../css/style1.css" rel="stylesheet" type="text/css">
<script src="../js/common.js"></script>
<script src="../js/Calendar3.js"></script>
<script>
function stat()
{
	
	var theForm = document.forms[0];
	theForm.isstat.value="1";
	wait();
	theForm.submit();
}
function modify(id)
{
	var w=1074,h=802,newwindow;
	var url="chargeorder_edit.jsp?id="+id;
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
</script>
	</head>


	<body bgcolor="#EFEFEF">

		<form name="form1" method="post" action="queryAllOrderLog.jsp">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="1" valign="bottom">
												<table width="100%" border="0" cellspacing="0"
													cellpadding="0">
													<tr>
														<td>
															<img src="../images/tab_left.gif" width="6" height="22">
														</td>
														<td nowrap background="../images/tab_midbak.gif"><%=perm%></td>
														<td>
															<img src="../images/tab_right.gif" width="6" height="22">
														</td>
													</tr>
												</table>
											</td>
											<td valign="bottom">
												<table width="100%" border="0" cellspacing="0"
													cellpadding="0" height="22">
													<tr>
														<td></td>
													</tr>
													<tr>
														<td bgcolor="#FFFFFF" colspan="2" height=1></td>
													</tr>
													<tr>
														<td height=3></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td rowspan="2" bgcolor="#FFFFFF" width="1">
												<img src="../images/spacer.gif" width="1" height="1">
											</td>
											<td valign="top" align="center">
												<table width="95%" border="0" cellspacing="1"
													cellpadding="2">
													<tr>
														<td>
														<input name="isstat" type="hidden" value="">
															<table width="100%" border="0" cellspacing="1"
																cellpadding="2">
																<tr>
																  <td colspan="2">																	  
																		   起始日期:
																		  <input name="startdate" type="text" id="startdate" onClick="new Calendar().show(this);"
																				value="" size="8" />
																			结束日期:
																		  <input name="enddate" type="text" id="enddate" onClick="new Calendar().show(this);"
																				value="" size="8" /> 
																		  角色id:
																		  <input name="playerId" type="text" id="playerId" 
																				value="" size="8" />
																    角色名:	
																			  <input name="pname" type="text" id="pname" 
																				value="" size="8" /> 
																				
																		  账号名:	
																			  <input name="username" type="text" id="username" 
																				value="" size="8" /> 
																		  中心订单号:
																		  <input name="corderNo" type="text" id="corderNo" 
																				value="" size="21" />
																    子平台订单号:	
																			  <input name="orderNo" type="text" id="orderNo" 
																				value="" size="12" /> 
																				
																	      <label>
																			  <input name="statBtn" type="button" id="statBtn" value="查询" onClick="stat()">
																      </label>																	</td>
																</tr>
																<tr>
																  <td>记录数<font color=red><%if(xml!=null){ %>
																			<%=xml.optInt("totalrecord")%>
																			<%}else{%>0<%}%></font></td>
															      <td align="right">每页行数
                                                                    <input name="rows" id="rows" value="10" size="3">
                                                                  <input name="statBtn2" type="button"  
																					value="刷新" onClick="stat()"></td>
															  </tr>
															</table>															
															<table width="100%" border="0" cellpadding="2"
																cellspacing="1" class="tbbgc1">
																<tr class="listtopbgc">
																  <td align="center" nowrap>No</td>
																  <td align="center" nowrap>
															<select name="channel" id="channel" onChange="stat()">
															  <option value="">联运渠道</option>
															  <%
															  DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
																while(channelRs.next()){
																%>
															  <option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")%>(<%=channelRs.getString("code")%>)</option>
															  <%
															  }
															  %>
															</select></td>
																	
																	<td align="center" nowrap>
																		<%
																			JSONObject serverListData = ServerBAC.getInstance().getJsonObjs("id,name", null,
																					"id ASC");
																		%>																	 
																	  <select name="serverId" id="serverId"
																			onChange="stat()">
																		  <option value="0">
																			  游戏服务器																		</option>
																		  <%
																				if (serverListData != null) {
																					JSONArray serverList = serverListData.optJSONArray("list");
																					for (int i = 0; i < serverList.length(); i++) {
																						JSONObject lineData = serverList.optJSONObject(i);
																			%>
																		  <option value="<%=lineData.optInt("id")%>">
																			  <%=lineData.optString("name")%></option>
																		  <%
																				}
																				}
																			%>
																    </select>																	</td>
																	<td align="center" nowrap>中心订单号</td>
																	<td align="center" nowrap>
																		子平台订单号																	</td>
																	<td align="center" nowrap>
																		<select name="orderType" id="orderType" onChange="stat()">
																			<option value="">订单类型</option>
																	<%
																		DBPsRs chargetypeRs = DBPool.getInst().pQueryS(TabStor.tab_charge_type);
																		while(chargetypeRs.next()){
																	 %>
																		<option value="<%=chargetypeRs.getInt("NUM")%>"><%=chargetypeRs.getString("NAME")%></option>
																	 <%
																	 }
																	 %>
																		</select>																	</td>
																	<td align="center" nowrap>
																		<select  name="buyType" id="buyType" onChange="stat()">
																			<option value="">购买类型</option>
																			<option value="1">钻石</option>
																			<option value="2">特权</option>
																		</select>																	</td>
																	<td align="center" nowrap><select  name="systemType" id="systemType" onChange="stat()">
																	  <option value="0">客户端类型</option>
																	  <option value="1">安卓</option>
																	  <option value="2">IOS</option>
																	  <option value="3">PC</option>
                                                                       </select></td>
																	<td align="center" nowrap>账号</td>
																	<td align="center" nowrap>角色id</td>
																	<td align="center" nowrap>角色名</td>
																	<td align="center" nowrap>支付金额</td>
																	<td align="center" nowrap>获得钻石</td>
																	<td align="center" nowrap>
																	  <select  name="getpower" id="getpower" onChange="stat()">
                                                                        <option value="">获得特权</option>
                                                                        <option value="0">无</option>
                                                                        <option value="1">月卡</option>
                                                                        <option value="2">季卡</option>
																		<option value="3">年卡</option>
                                                                      </select></td>																													
																	<td align="center" nowrap>
																		<select  name="result" id="result" onChange="stat()">
																			<option value="">购买结果</option>
																			<option value="0">未处理</option>
																			<option value="1">成功</option>
																			<option value="-1">失败</option>
																		</select>																	</td>
																	<td align="center" nowrap><select  name="sendresult" id="sendresult" onChange="stat()">
																	  <option value="">发货结果</option>
																	  <option value="1">发货成功</option>
																	  <option value="0">未发货</option>
																	  <option value="-1">发货失败</option>
																	  <option value="2">发货中</option>
                                                                    </select></td>
																	<td align="center" nowrap>失败描述</td>
																	<td align="center" nowrap>下单时间</td>
																    <td align="center" nowrap>时间</td>
																    <%
																    if(root){
																    %>
																    <td align="center" nowrap>操作</td>
																    <%
																    }
																    %>
																</tr>
																<%
											  					int count = 0;
																JSONArray list=null;
																if(xml!=null){
																	count=(xml.optInt("rsPageNO")-1)*rows+1;
																	list = xml.optJSONArray("list");
																}
																if(list!=null){
																
																String serverName="";
																for(int i=0;i<list.length();i++){
																	JSONObject jsonObj=list.getJSONObject(i);
																 	String userName=jsonObj.getString("USERNAME");
																 	String ordeNo=jsonObj.getString("ORDERNO");
																	String cordeNo=jsonObj.getString("CORDERNO");
																 	int oType=jsonObj.getInt("ORDERTYPE");
				 													int price=jsonObj.getInt("PRICE");
				 													int coin=jsonObj.getInt("GETCOIN");
				 													int power=jsonObj.getInt("GETPOWER");
				 													int buytype=jsonObj.getInt("BUYTYPE");
				 													int res=jsonObj.getInt("RESULT");				 													
				 													String saveTime=jsonObj.getString("SAVETIME");
																	String orderTime=jsonObj.getString("orderTime");
				 													String IP=jsonObj.getString("IP");
				 													int sid=jsonObj.getInt("SERVERID");
				 													String name=jsonObj.getString("NAME");
																	int gived = jsonObj.getInt("gived");
																	int fromWhere = jsonObj.getInt("fromWhere");
				 													int sendResult = jsonObj.getInt("sendResult");
																	int pid = jsonObj.getInt("playerId");
																	int sType=jsonObj.getInt("systemType");
																	if(gived==1)
																	{
																		sendResult = 1;
																	}
																	else
																	if(gived==-1)
																	{
																		sendResult = -1;
																	}
																	
			 														if (serverListData != null&&sid>0) {
																		JSONArray serverList = serverListData.optJSONArray("list");
																		for (int j = 0; j < serverList.length(); j++) {
																		JSONObject lineData = serverList.optJSONObject(j);
																			if(sid==lineData.optInt("id")){
																				serverName=lineData.optString("name");
																			break;
																			}
																		}
																	}		 													
																	 
																	String type="";
																	switch(buytype){
																	case 1:
				 														type="钻石";break;
			 														case 2:
				 														type="特权";break;
																	}
				 													String systemTypeName="";
																	switch(sType){
																	case 1:
				 														systemTypeName="安卓";break;
			 														case 2:
				 														systemTypeName="IOS";break;
																	case 3:
				 														systemTypeName="PC";break;
																	}
															 		 %>
																	<tr class="nrbgc1">
																	  <td align="center" nowrap><%=count++%></td>
																	  <td align="center" nowrap><%=jsonObj.getString("channelname")%>(<%=jsonObj.getString("channel")%>)</td>
																		<td align="center" nowrap><%=serverName%></td>
																		<td align="center" nowrap><%=cordeNo%></td>
																		<td align="center" nowrap><%=ordeNo%></td>
																		<td align="center" nowrap><%=TabStor.getListVal(TabStor.tab_charge_type, "num="+oType, "name")%>(<%=oType%>)</td>
																		<td align="center" nowrap><%=type%></td>
																		<td align="center" nowrap><%=systemTypeName%></td>
																		<td align="center" nowrap><%=userName%></td>
																		<td align="center" nowrap><%=pid%></td>
																		<td align="center" nowrap><a href="player_data.jsp?playerName=<%=name%>&serverId=<%=sid%>"><%=name%></a></td>
																		<td align="center" nowrap><%=price%></td>
																		
																		<td align="center" nowrap><%=coin%></td>
																		<td align="center" nowrap><%if(power==1){%>月卡<%}else if(power==2){%>季卡<%}else if(power==3){%>年卡<%}else{%><%}%></td>
																		
																		<td align="center" nowrap><%if(res==0){%><font color="#003399">未处理</font><%}else if(res==1){%><font color="#006633">成功</font><%}else if(res==-1){%><font color="#FF0000">失败</font><%}%></td>
																		<td align="center" nowrap><%if(res==1){%><%if(sendResult==1){%><font color="#006633">发货成功</font><%}else if(sendResult==0){%><font color="#CC3366">发货中</font><%}else if(sendResult==-1){%><font color="#FF0000">发货失败</font><%}%>
																		  <%if(sendResult==0 || sendResult==-1){%> | <a href="queryAllOrderLog_batchgive.jsp?serverId=<%=sid%>&channel=<%=jsonObj.getString("channel")%>&orderNo=<%=ordeNo%>" target="hiddenFrame"><font color="#CC6600">再次发货</font></a><%}}%></td>
																		<td align="center" nowrap><%=jsonObj.optString("note")%></td>																		
																		<td align="center" nowrap><%=orderTime%></td>
																	    <td align="center" nowrap><%=saveTime%></td>
																	    <%
																	    if(root){
																	    %>
<td align="center" nowrap><img src="../images/icon_modify.gif" alt="Modify" style="cursor:hand" onClick="modify(<%=jsonObj.getInt("id")%>)"></td>
																	    <%
																	    }
																	    %>
																	</tr>
																	<%
																	}
																	}
																	%>
															</table>
															<table width="100%" border="0" cellspacing="1" cellpadding="2">
  <tr>
    <td>充值金额合计：<font color="#FF0000"><%=totalMoney%></font>元<br/>获得钻石合计：<font color="#FF0000"><%=totalGetcoin%></font></td>
  </tr>
</table>
															
															<table width="100%" border="0" cellspacing="1"
																cellpadding="2">
																<tr>
																	<td align="left"> 
																		  <%@ include file="inc_list_bottom.jsp"%>
																		  
																	</td>
																	<td align="left"> 
																																						
																		  
																	</td>
																	<td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                                                      <tr>
                                                                        <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="if(confirm('确定批量补单吗？'))document.getElementById('hiddenFrame').src='queryAllOrderLog_batchgive.jsp?batch=1'" height="21"><img src="../images/icon_havedispense.gif" align="absmiddle"> 对10分钟前成功但仍未发货的单子进行批量补单</td>
                                                                      </tr>
                                                                    </table></td>
																</tr>
															</table>
														</td>
													</tr>

												</table>

											</td>
											<td rowspan="2" bgcolor="#848284" width="1">
												<img src="../images/spacer.gif" width="1" height="1">
											</td>
										</tr>
										<tr>
											<td bgcolor="#848284" height="1"></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</form>
<script>
autoChoose(allValue);
</script>
<iframe id="hiddenFrame" name="hiddenFrame" width=10 height=10  frameborder="0"></iframe>
</body>
</html>
