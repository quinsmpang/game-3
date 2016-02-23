<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
	String model = "充值统计";
	String perm = "充值数据综合分析";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
	int serverId = Tools.str2int(request.getParameter("serverId"));
	String startDate = Tools.strNull(request.getParameter("startDate"));
	String statBtn = request.getParameter("statBtn");
	String endDate = Tools.strNull(request.getParameter("endDate"));
			 
	JSONArray returnarr = null;
	if(statBtn != null) {
		returnarr= ChargeStatBAC.getInstance().getOtherChargeData(request);
	}
%>

<script>
var allValue=new Object();
allValue.serverId="<%=serverId%>";
allValue.startDate="<%=startDate%>";
allValue.endDate="<%=endDate%>";
</script>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="../css/style1.css" type="text/css">
		<SCRIPT  SRC="../Charts/FusionCharts.js"></SCRIPT>
		<script src="../js/common.js"></script>
		<script language="javascript" type="text/javascript" src="../My97DatePicker/WdatePicker.js"></script>
		<script language="javascript" type="text/javascript" src="../js/jquery-1.9.1.min.js"></script>
		<script> 
		function stat(){
			var theForm = document.forms[0];
						
			wait();
			theForm.submit();
		}
	 </script>
	</head>
	<body bgcolor="#EFEFEF">
		<form name="form1" method="post" action="statisticalOrderData.jsp">
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
														<table width="100%" border="0" cellspacing="0"
															cellpadding="0">
															<tr>
																<td>
																
													 
															<table width="100%" border="0" cellspacing="1"
																cellpadding="2">
																<tr>
																	<td>
																		游戏服务器
																		  <select name="serverId" id="serverId"
																			>
                                                                            <option value="0">选择服务器</option>
                                                                            <%
                                                                            DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
																			while(serverRs.next()) {
																			%>
                                                                            <option value="<%=serverRs.getInt("id")%>"> <%=serverRs.getString("name")%></option>
                                                                            <%
																			}
																			%>
                                                                          </select>
起始日期:<input type="text" name="startDate" id="startDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" style="width: 120px;"/>		  
结束日期:<input type="text" name="endDate" id="endDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" style="width: 120px;"/>
<input name="statBtn" type="submit" id="statBtn" value="查询" onClick="stat()">
																</tr>
															</table>

																				<table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
																					<tr>
																						<td align="left" class="listtopbgc" colspan="2">
																							充值数据分析
																						</td>
																					</tr>
<%
if(returnarr != null){
%>
<tr>
<td align="center" class="whitebgc">
<%=returnarr.optString(0) %>
</td>
<td align="center" class="whitebgc">
<%=returnarr.optString(1) %>
</td>
</tr>
<%
}
%>
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
	<iframe name=hiddenFrame width=0 height=0></iframe>
<script>
autoChoose(allValue);
</script>				

	</body>

</html>
