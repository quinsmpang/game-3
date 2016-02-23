<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%  
	String model = "充值统计";
	String perm = "充值走势图";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
	int serverId = Tools.str2int(request.getParameter("serverId"));
	String channel = Tools.strNull(request.getParameter("channel"));
	String startdate = Tools.strNull(request.getParameter("startdate"));
	String enddate = Tools.strNull(request.getParameter("enddate"));
	int buytype = Tools.str2int(request.getParameter("buytype"));
	String statBtn = request.getParameter("statBtn");
	String pname = Tools.strNull(request.getParameter("pname"));
	int stattype = Tools.str2int(request.getParameter("stattype"));
	
	JSONArray returnarr = null;
	if(statBtn != null) 
	{
		returnarr = ChargeStatBAC.getInstance().getChargeRunChart(pageContext);
	}
%>

<script>
var allValue=new Object();
allValue.channel="<%=channel%>";
allValue.startdate="<%=startdate%>";
allValue.enddate="<%=enddate%>";
allValue.serverId="<%=serverId%>";
allValue.buytype="<%=buytype%>";
allValue.pname="<%=pname%>";
allValue.stattype="<%=stattype%>";
</script>

<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href="../css/style1.css" rel="stylesheet" type="text/css">
		<script src="../js/common.js"></script>
		<script src="../js/Calendar3.js"></script>
		<SCRIPT  SRC="../Charts/FusionCharts.js"></SCRIPT>
		<script>
function stat()
{	
	if(document.getElementById("startdate").value=="")
	{
		alert("必须填写起始日期");
		document.getElementById("startdate").focus();
		return false;
	}
	wait();
	return true;
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
															<table width="100%" border="0" cellspacing="1"
																cellpadding="2">
																<tr>
																  <td>	
																  
																<select name="channel" id="channel" >
																  <option value="">联运渠道</option>
																  	<%
																  	DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
																	while(channelRs.next()){
																	%>
																  <option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")%>(<%=channelRs.getString("code")%>)</option>
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
																		起始日期：<input name="startdate" type="text" id="startdate" onClick="new Calendar().show(this);" value="" size="8" />
																		结束日期：<input name="enddate" type="text" id="enddate" onClick="new Calendar().show(this);" value="" size="8" />																  																				
																		角色名： <input name="pname" type="text" id="pname" value="" size="8" />
																		<select name="stattype" id="stattype">
																		  	<option value="0">按天统计</option>
																		  	<option value="1">按月统计</option>
																		  	<option value="2">按年统计</option>
																    	</select>
																		<input name="statBtn" type="submit" id="statBtn" value="统计">
																	</td>
																</tr>
																<tr>
																  <td>
<%
if(returnarr != null){
%>
<table width="100%" border="0" cellspacing="1" cellpadding="2">
	<tr>
		<td align="center">
		<%=returnarr.optString(0) %>
		</td>
   </tr>
   <tr>
		<td align="center">
		<%=returnarr.optString(1) %>
		</td>
   </tr>
</table>
<%
}
%>
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
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0  frameborder="0"></iframe>
</body>
</html>
