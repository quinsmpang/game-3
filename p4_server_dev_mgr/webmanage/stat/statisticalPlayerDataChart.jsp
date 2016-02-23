<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
	String model = "运营统计";
	String perm = "单日角色数据分析";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
int serverId = Tools.str2int(request.getParameter("serverId"));
String date = request.getParameter("date");
String statBtn = request.getParameter("statBtn");
if(null==date || "".equals(date)){
	date = MyTools.getDateStr();
}
JSONArray returnarr = null;
if(statBtn != null) 
{
	returnarr = OperationStatBAC.getInstance().getPlayerData(pageContext);
}
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script type="text/javascript">
var allValue=new Object();
allValue.serverId='<%=serverId%>';
allValue.date='<%=date%>';
</script>
<SCRIPT  SRC="../Charts/FusionCharts.js"></SCRIPT>
<script src="../js/common.js"></script>
<script src="../js/Calendar3.js"></script>
<script type="text/javascript" language="javascript">
function stat(){
	var theForm = document.forms[0];
	if(!isDate(theForm.date.value)){
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
																游戏服务器：<select name="serverId" id="serverId">
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
<input name="date" type="text" id="date" maxlength="10" onClick="new Calendar().show(this);" size="16" />
<input name="statBtn" type="submit" id="statBtn" value="查询">
															</td>
														</tr>
													</table>
													<%
													if(returnarr != null){
													%>
													<table width="100%" border="0" cellspacing="1" cellpadding="2">
																<tr>
																	<td>
																		<table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
																			<tr>
																				<td align="left" class="listtopbgc">
																					创建角色统计
																				</td>
																			</tr>
																			<tr>
																				<td align="left" CLASS="whitebgc">  
																					<%=returnarr.optString(0) %>
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>																	
																	<tr>
																	<td>
																		<table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
																			<tr>
																				<td  align="left" class="listtopbgc">
																					在线量统计
																				</td>
																				 
																			</tr>
																			<tr>
																				<td align="left" CLASS="whitebgc">
																					<%=returnarr.optString(1) %>
																				</td>
																			</tr>

																		</table>
																	</td>
																</tr>
																<tr>
																	<td>
																		<table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
																			<tr>
																				<td align="left" class="listtopbgc">
																					在线时长统计
																				</td>
																			</tr>
																			<tr>
																				<td align="left" CLASS="whitebgc">
																					<%=returnarr.optString(2) %> 
																						</td>
																					</tr>
 
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
																					<tr>
																						<td   align="left" class="listtopbgc">
																							钻石使用数据分析
																						</td>
																						 
																					</tr>
																					<tr>
																						<td   align="left" CLASS="whitebgc">
																					<%=returnarr.optString(3) %>
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td>
																		<table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
																			<tr>
																				<td align="left" class="listtopbgc">
																					登陆次数统计
																				</td>
																			</tr>
																			<tr>
																				<td align="left" CLASS="whitebgc">
																					<%=returnarr.optString(4) %>
																				</td>
																			</tr>

																		</table>
																	</td>
																</tr>
																<tr>
																	<td>&nbsp;
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
<iframe name=hiddenFrame width=0 height=0></iframe>
<script>
autoChoose(allValue);
</script>				

	</body>

</html>
