<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%  
	String model = "充值统计";
	String perm = "充值排行";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
	int serverId = Tools.str2int(request.getParameter("serverId"));
	String channel = request.getParameter("channel");
	int rows = Tools.str2int(request.getParameter("rows"));
	if(rows <= 0){
		rows=10;
	} 
	JsonRs returnRs = null;
	if("1".equals(request.getParameter("issub"))){
		returnRs = ChargeStatBAC.getInstance().getChargeRanking(pageContext);
	}
%>

<script>
var allValue=new Object();
allValue.serverId="<%=serverId%>";
allValue.channel="<%=channel%>";
allValue.rows="<%=rows%>";
</script>
 
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="../css/style1.css" rel="stylesheet" type="text/css">
<script src="../js/common.js"></script>
<script src="../js/Calendar3.js"></script>
<script src="../js/meizzDate.js"></script>
		
<script>

function amountCheck(){
	if(document.forms[0].rows.value > 50){
		document.forms[0].rows.value = 50;
		alert("只提供50名以内查询");
	} else 
	if(document.forms[0].rows.value <= 0){
		document.forms[0].rows.value = 10;
		alert("查询数据必须大于0");
	}
}

function stat()
{
	var theForm = document.forms[0];
	theForm.issub.value="1";
	wait();
	theForm.submit();
}

</script>
	</head>


	<body bgcolor="#EFEFEF">

		<form name="form1" method="post" action="charge_top50.jsp">
		<input name="issub" type="hidden" value="0">
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
															<table width="100%" border="0" cellspacing="1" cellpadding="2">
																<tr>
																	<td>
																<select name="channel" id="channel" >
																  <option value="">联运渠道</option>
																  <%
																  DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
																  while(channelRs.next()) {
																  %>
																  <option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")%>(<%=channelRs.getString("code")%>)</option>
																  <%
																  }											
																  %>
																</select>
																游戏服务器:<select name="serverId" id="serverId" >
																<option value="0">
																选择服务器
																</option>
																<%
																DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
																while(serverRs.next()) {
																%>
																<option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
																<%
																}
																%>
																</select>
前:<input name="rows" type="text" id="rows" size="3" onChange="amountCheck()"/>名
																				
<input name="statBtn" type="button" id="statBtn" value="查询" onClick="stat()">
																	</td>
																</tr>
															</table>
															<%
															if(returnRs != null){ 
															%>
															<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
																<tr class="listtopbgc">
																	<td align="center" nowrap>服务器</td>
																	<td align="center" nowrap>账号	</td>
																	<td align="center" nowrap>渠道</td>
																	<td align="center" nowrap>角色名</td>
																	<td align="center" nowrap>充值金额</td>
																	<td align="center" nowrap>当前剩余</td>
																	<td align="center" nowrap>等级</td>
																	<td align="center" nowrap>天梯积分</td>
																    <td align="center" nowrap>最后登录时间</td>
																</tr>
																<%
											  					int count = 0;
																while(returnRs.next()){
																	int rs_serverid = returnRs.getInt("serverid");
																	String rs_channel = returnRs.getString("channel");
															 	%>
																	<tr class="nrbgc1">
																		<td align="center" nowrap><%=ServerBAC.getInstance().getNameById(rs_serverid)+"("+rs_serverid+")"%></td>
																		<td align="center" nowrap><%=returnRs.getString("user_name")%></td>
																		<td align="center" nowrap><%=TabStor.getListVal(TabStor.tab_channel, "code="+rs_channel, "name")+"("+rs_channel+")"%></td>
																		<td align="center" nowrap><%=returnRs.getString("play_name")%></td>														
																		<td align="center" nowrap><%=returnRs.getInt("infull_num")%></td>
																		<td align="center" nowrap><%=returnRs.getInt("current_num")%></td>
																		<td align="center" nowrap><%=returnRs.getInt("lv_num")%></td>
																		<td align="center" nowrap><%=returnRs.getInt("jjc_rank")%></td>
																	    <td align="center" nowrap><%=returnRs.getString("last_login_time")%></td>
																	</tr>
																	<%
																	}
																	%>
															</table>
															<br>
															<%
															}
															%>
															<table width="100%" border="0" cellspacing="1" cellpadding="2">
																<tr>
																	<td align="left"> 
																		 
																	</td>
																	 
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
		<iframe name=hiddenFrame width=0 height=0></iframe>
	</body>
</html>
