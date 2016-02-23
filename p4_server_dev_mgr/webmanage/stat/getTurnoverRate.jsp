<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="oracle.net.aso.s"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%  
	String model = "运营统计";
	String perm = "流失率统计";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
int condtype = Tools.str2int(request.getParameter("condtype"));
if(condtype==0){
	condtype=1;
}
int serverId = Tools.str2int(request.getParameter("serverId"));
String startTime = Tools.strNull(request.getParameter("startDate"));
String statBtn = request.getParameter("statBtn");
JsonRs returnRs = null;
if(statBtn != null){
	returnRs = OperationStatBAC.getInstance().getTrunOverData(pageContext);
}
%>
<script>
var allValue=new Object();
allValue.condtype="<%=condtype%>";
allValue.startDate="<%=startTime%>";
allValue.serverId="<%=serverId%>";
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
	if(document.getElementById("startDate").value==""){
		alert("请选择查看日期");
		document.getElementById("startDate").focus();
		return false;
	}
	wait();
	theForm.submit();
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
												<table width="95%" border="0" cellspacing="1" cellpadding="2">
													<tr>
														<td>
															<table width="100%" border="0" cellspacing="1" cellpadding="2">
																<tr>
																	<td>
																		条件：
																		<select name="condtype" id="condtype">
																		<option value="1">等级区间</option>
																		<option value="2">VIP等级</option>
																		</select>
																		游戏服务器：
																		<select name="serverId" id="serverId">
																		<option value="0">选择服务器</option>
																		<%
																		DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
																		while(serverRs.next()) {
																		%>
																		<option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
																		<%
																		}
																		%>
																		</select>
日期：<input name="startDate" type="text" id="startDate" maxlength="10" onClick="new Calendar().show(this);" value="" size="16" />
<input name="statBtn" type="submit" id="statBtn" value="统计">
																	</td>
																</tr>
															</table>
															<%
															if(returnRs != null){
															%>
															<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
																<tr class="listtopbgc">
																	<td align="center" nowrap><%=condtype==1?"等级区间":"VIP等级" %></td>
																	<td align="center" nowrap>玩家数</td>
																	<td align="center" nowrap>一日流失数</td>
																	<td align="center" nowrap>三日流失数</td>
																	<td align="center" nowrap>七日流失数</td>
																</tr>
																<%
																while(returnRs.next()){
																	String type_numStr = null;
																	if(condtype == 1){
																		type_numStr = (returnRs.getInt("type_num")*10-9)+"级~"+returnRs.getInt("type_num")*10+"级";
																	} else 
																	if(condtype == 2){
																		type_numStr = "Vip"+returnRs.getInt("type_num");
																	}
																%>
																<tr class="nrbgc1">
																<td align="center" nowrap><%=type_numStr%></td>
																<td align="center" nowrap><%=returnRs.getInt("sum_total_num")%></td>
																<td align="center" nowrap><%=returnRs.getInt("sum_lose_num1")%></td>
																<td align="center" nowrap><%=returnRs.getInt("sum_lose_num3")%></td>
																<td align="center" nowrap><%=returnRs.getInt("sum_lose_num7")%></td>
																</tr>
																<%
																}
																%>
															</table>
															<%
															}
															%>
															<br>
															<table width="100%" border="0" cellspacing="1" cellpadding="2">
																<tr>
																	<td align="right">&nbsp;
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
