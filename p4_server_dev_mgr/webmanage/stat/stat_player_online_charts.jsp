<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
	String model = "市场统计";
	String perm  = "在线人数统计";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
	String channel = Tools.strNull(request.getParameter("channel"));
	int serverId = Tools.str2int(request.getParameter("serverId"));
	String date = Tools.strNull(request.getParameter("date"));
	
	String statBtn = request.getParameter("statBtn");
	
	String returnStr = null;
	
	if(statBtn != null) 
	{
		returnStr = MarketStatBAC.getInstance().getOnlineData(pageContext);
	}
	//System.out.println("returnStr:"+returnStr);
%>
<script>
var allValue=new Object();
allValue.serverId = "<%=serverId%>";
allValue.channel="<%=channel%>";
allValue.date="<%=date%>";

</script>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script src="../js/meizzDate.js"></script>
<SCRIPT  SRC="../Charts/FusionCharts.js"></SCRIPT>
<script src="../js/Calendar3.js"></script>
<script type="text/javascript" language="javascript">
function stat(){
	var theForm = document.forms[0];
	if(document.getElementById("date").value=="")
	{
		alert("必须填写日期");
		document.getElementById("date").focus();
		return false;
	}
	wait();
	theForm.submit();
}
</script>
	</head>
	<body bgcolor="#EFEFEF">
		<form name="form1" method="post" action="" onSubmit="return stat()">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td valign="top">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="1" valign="bottom">
											<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
											<table width="100%" border="0" cellspacing="0" cellpadding="0" height="22">
												<tr>
													<td></td>
													<td width=1></td>
												</tr>
												<tr>
													<td bgcolor="#FFFFFF" colspan="2" height=1></td>
												</tr>
												<tr>
													<td height=3></td>
													<td bgcolor="#848284" height=3 width="1"></td>
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
											<table width="90%" border="0" cellspacing="1" cellpadding="2">
												<tr>
													<td>
														<table width="100%" border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td>
															<table width="100%" border="0" cellspacing="1" cellpadding="2">
																<tr>
																	<td>
																	<select name="channel" id="channel">
																	<option value="">联运渠道</option>
																	<%
																	DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
																	while(channelRs.next()) {
																	%>
																	<option value="<%=channelRs.getString("CODE")%>"><%=channelRs.getString("NAME")%>(<%=channelRs.getString("CODE")%>)</option>
																	<%
																	}
																	%>
																	</select>
																	<select name="serverId" id="serverId">
																	<option value="0">游戏服务器</option>
																  	<%
																  	DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
																	while(serverRs.next()) {
																  	%>
																	<option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
																	<%
																	}
																	%>
																  </select>
 																日期
 																<input name="date" type="text" id="date" onClick="new Calendar().show(this)" size="10" />
 																<input name="statBtn" type="submit" id="statBtn" value="统计">
																	</td>
																</tr>
															</table>
															
															<table width="100%" border="0" cellspacing="1" cellpadding="2">
																<tr>
																	<td>
															<%
															if(returnStr != null){
															%>
															<table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
															<tr>
															<td align="center" class="whitebgc"><%=returnStr %></td>
															</tr>
															</table>
															<%
															}
															%>
																</td>
																		</tr>
																		<tr>
																			<td>&nbsp;</td>
																		</tr>
																	</table>
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
	</body>
<script>
autoChoose(allValue);
</script>
</html>
