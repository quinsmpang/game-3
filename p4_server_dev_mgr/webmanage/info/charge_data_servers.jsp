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
	String date = request.getParameter("date");
	date=null==date?"":date;
	int defaultRow = Tools.str2int(request.getParameter("rows"));
	if(defaultRow<=0)
	defaultRow=30;
	JSONArray arr = null;
	if("1".equals(request.getParameter("issub"))){
		//TODO 获取排行数据
	}
%>

<script>
var allValue=new Object();
allValue.serverId="<%=serverId%>";
allValue.rows="<%=defaultRow%>";
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
												<table width="95%" border="0" cellspacing="1"
													cellpadding="2">
													<tr>
														<td>
															<table width="100%" border="0" cellspacing="1"
																cellpadding="2">
																<tr>
																	<td>
																	游戏服务器:<select name="serverId" id="serverId" onChange="switchSelect(this)">
																			<option value="0">
																				选择服务器
																			</option>
																			<%
																			DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
																			while(serverRs.next()){
																			%>
																			<option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
																			<%
																			}
																			%>
																			</select>
																			 前:
																			<input name="rows" type="text" id="rows"
																				maxlength="3" 
																				 size="1" />名
																				
																			<label>
																				<input name="statBtn" type="button" id="statBtn"
																					value="查询" onClick="stat()">
																			</label>
																	</td>
																</tr>
															</table>
															<%if(arr!=null){ %>
															<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
																<tr class="listtopbgc">
																	<td align="center" nowrap>服务器</td>
																	<td align="center" nowrap>账号</td>
																	<td align="center" nowrap>角色名</td>
																	<td align="center" nowrap>等级</td>
																	<td align="center" nowrap>充值金锭</td>
																	<td align="center" nowrap>当前剩余</td>
																</tr>
																<%
											  					int count = 0;
																for(int i=0; i<arr.length(); i++){
																	JSONObject jsonObj=arr.getJSONObject(i);
																 	String userName=jsonObj.getString("uname");
																 	int uid=jsonObj.getInt("uid");
				 													String pname=jsonObj.getString("name");
				 													int sid=jsonObj.getInt("sid");
				 													int lv=jsonObj.getInt("lv");
				 													int bcoin=jsonObj.getInt("bcoin");
				 													int coin=jsonObj.getInt("coin");
				 													String serverName = ServerBAC.getInstance().getNameById(sid);
															 		 %>
																	<tr class="nrbgc1">
																		<td align="center" nowrap><%=serverName%></td>
																		<td align="center" nowrap><%=userName%></td>
																		<td align="center" nowrap><%=pname%></td>
																		<td align="center" nowrap><%=lv%></td>
																		<td align="center" nowrap><%=bcoin%></td>
																		<td align="center" nowrap><%=coin%></td>
																	</tr>
																	<%
																	}
																	%>
															</table>
															<br>
															<%} %>
															<table width="100%" border="0" cellspacing="1"
																cellpadding="2">
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
switchSelect(document.getElementById("serverId"));
autoChoose(allValue);
</script>
		<iframe name=hiddenFrame width=0 height=0></iframe>
	</body>
</html>
