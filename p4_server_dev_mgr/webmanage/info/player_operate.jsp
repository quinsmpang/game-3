<%@page import="com.moonic.bac.UserBAC"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
	String model = "玩家管理";  
	String perm = "玩家操作";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
	int serverId = Tools.str2int(request.getParameter("serverId"));
	String name = Tools.strNull(request.getParameter("name"));
	
	int pid = Tools.str2int(request.getParameter("pid"));
	int sid = Tools.str2int(request.getParameter("sid"));
	int opt = Tools.str2int(request.getParameter("opt"));
	
	//System.out.println("pid:"+pid+" sid:"+sid);
	
	if(opt == 1){
 		PlayerOperateBAC.getInstance().blankOffPlayer(request);
	} else 
	if(opt == 2){
		PlayerOperateBAC.getInstance().unBlankOffPlayer(request);
	} else 
	if(opt == 3){
		PlayerOperateBAC.getInstance().bannedToPostPlayer(request);
	} else
	if(opt == 4){
		PlayerOperateBAC.getInstance().unBannedToPostPlayer(request);
	} else
	if(opt == 5){
		PlayerOperateBAC.getInstance().kickOut(request);
	}
%>

<script>
var allValue=new Object();
allValue.serverId="<%=serverId%>";
allValue.name="<%=name%>";
allValue.pid="<%=pid%>";
allValue.sid="<%=sid%>";
</script>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="../css/style1.css" rel="stylesheet" type="text/css">
<script src="../js/common.js"></script>
<script language="javascript" type="text/javascript" src="../My97DatePicker/WdatePicker.js"></script>
<script>
function stat1() {
	theForm = document.forms[0];
	if(theForm.name.value==""){
		alert("请输入角色名称");
		return false;
	}
 	
	wait();
	theForm.submit();
}
function stat2(pid,sid) {
	theForm = document.forms[0];
 	theForm.pid.value = pid;
 	theForm.sid.value = sid;
 	
	wait();
	theForm.submit();
}		 
function stat3(opt) {
	theForm = document.forms[0];
 	theForm.opt.value = opt;
 	
	wait();
	theForm.submit();
}
</script>


	</head>
	<body bgcolor="#EFEFEF">
		<form name="form1" method="post">
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
															<img height="22" width="6" src="../images/tab_left.gif">
														</td>
														<td nowrap background="../images/tab_midbak.gif"><%=perm%><br></td>
														<td>
															<img height="22" width="6" src="../images/tab_right.gif">
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
									                    <tr > 
									                      <td bgcolor="#FFFFFF" colspan="2" height=1></td>
									                    </tr>
									                    <tr > 
									                      <td height=3></td>
									                      <td bgcolor="#848284"  height=3 width="1"></td>
									                    </tr>
								                  </table>
											</td>
										</tr>
									</table>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td rowspan="2" bgcolor="#FFFFFF" width="1">
												<img height="1" width="1" src="../images/spacer.gif">
											</td>
											<td valign="top" align="center">
												<table width="100%" border="0" cellspacing="1" cellpadding="2">
													<tr>
														<td colspan="2">
															<table width="100%" border="0" cellspacing="1"cellpadding="2">
																<tr>
																	<td>
																		游戏服务器：<select name="serverId" id="serverId"> 
																			<option value="0">选择服务器</option> 
																			<%
																			DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server); 
																			while(serverRs.next()) { 
																			%> 
																			<option value='<%=serverRs.getInt("id")%>'><%=serverRs.getString("name")%></option> 
																			<% 
																			} 
																			%>
																			</select>
																		  角色名:<input type="text" name="name" id="name"> 
																		<input type="button" name="statBtn" id="statBtn" value="查询" onClick="stat1()">
																	</td>
																</tr>
														  </table>
													  </td>
													</tr>
													
													<%
													if (!name.equals("")) {
													%>
													<tr>
														<td valign="top" width="20%" align="center" >
														
														 <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1" >
															<tr class="listtopbgc"  >
																<td align="center" nowrap>角色名</td>
																<td align="center" nowrap>服务器 </td>
																<td align="center" nowrap>操作</td>
															</tr>
														 	<%
														 	JSONArray jonsarr = PlayerOperateBAC.getInstance().queryPlayerListByName(name, serverId);
															for (int i = 0; i < jonsarr.length(); i++) { 
																JSONArray arr = jonsarr.optJSONArray(i);
																int playerid = arr.optInt(0); 
																int serverid = arr.optInt(1);
														 	%>
															 
															<tr  bgcolor="#EFEFEF"    onMouseOver="this.style.backgroundColor='#7EC0EE'" onMouseOut="this.style.backgroundColor='#EFEFEF'" >
																<td align="center" nowrap><%=PlayerBAC.getInstance().getNameById(playerid)%><br></td>
																<td align="center" nowrap><%=ServerBAC.getInstance().getNameById(serverid)%></td>
																<td align="center" nowrap ><a href="javascript:stat2('<%=playerid%>','<%=serverid%>')">查询详情</a></td>
															</tr>
														<%
														}
														%> 
														  </table>
														  
													  </td>
													  
													<td valign="top" width="70%" align="center" >
													<%
													if(pid != 0){
														JSONObject plaobj = PlayerBAC.getInstance().getJsonObj("id="+pid);
														JSONObject userobj = UserBAC.getInstance().getJsonObj("id="+plaobj.optInt("userid"));
														String sname = ServerBAC.getInstance().getNameById(plaobj.getInt("serverid"));
														String pname = plaobj.getString("name");
														String bannedmsgtime = plaobj.optString("bannedmsgtime");
														int enable = plaobj.optInt("enable");
														String blankofftime = plaobj.optString("blankofftime");
													%>
														<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1" >
															<tr class="listtopbgc"  >
																<td align="center" nowrap   colspan="13"><%=sname%>----<%=pname%>----账号信息</td>
															</tr>
														 	<tr class="nrbgc1" height="30">
														 		<td align="center" nowrap width="10%"><strong>账号名:</strong></td>
														 		<td align="center" nowrap width="10%"><%=userobj.optString("username")%><br></td>
														 		<td align="center" nowrap width="10%"><strong>在线:</strong></td>
														 		<td align="center" nowrap width="5%"><%=userobj.optInt("onlinestate")==0?"否":"是"%><br></td>
														 		<td align="center" nowrap width="5%" >&nbsp;</td>
													 		  	<td align="center" nowrap width="10%"><br></td>
														 		<td align="center" nowrap><strong>注册时间:</strong></td>
														 		<td align="center" nowrap colspan="2"><%=userobj.optString("regtime")%><br></td>
														 		<td align="center" nowrap><strong>最后在线时间:</strong></td>
														 		<td align="center" nowrap colspan="2"><%=userobj.optString("logintime")%><br></td>
														 	</tr>
													 		<tr class="listtopbgc"  >
																<td align="center" nowrap   colspan="12"><%=sname %>----<%=pname %>----角色信息</td>
															</tr>
															<tr class="nrbgc1" height="30">
														 		<td align="center" nowrap width="10%"><strong>当前钻石:</strong></td>
														 		<td align="center" nowrap width="10%"><%=plaobj.optInt("coin")%></td>
														 		<td align="center" nowrap width="10%"><strong>充值钻石总数:</strong></td>
														 		<td align="center" nowrap width="10%"><%=plaobj.optInt("buycoin")%></td>
														 		<td align="center" nowrap width="10%"><strong>充值RMB:</strong></td>
														 		<td align="center" nowrap width="10%"><%=plaobj.optInt("rechargermb")%></td>
														 		<td align="center" nowrap width="10%"><strong>等级:</strong></td>
														 		<td align="center" nowrap width="10%"><%=plaobj.optInt("lv")%></td>
														 		<td align="center" nowrap width="10%"><strong>VIP等级:</strong></td>
														 		<td align="center" nowrap width="10%"><%=plaobj.optInt("vip")%></td>
														 		<td align="center" nowrap width="10%">&nbsp</td>
															</tr>
															<tr class="nrbgc1" height="30">
																<td align="center" nowrap width="10%"><strong>禁言截止时间:</strong></td>
														 		<td align="center" nowrap width="10%" colspan="2">
														 		<%
													 			if(MyTools.checkSysTimeBeyondSqlDate(bannedmsgtime)){
														 		%>
														 		 正常状态
														 		<%
														 		} else {
														 		%>
														 		<font color="red" style="font:bold; italic;"><%=bannedmsgtime%></font>
														 		<%
														 		}
														 		%>
														 		
														 		<br></td>
																<td align="center" nowrap width="5%" colspan="2"><strong>封号截止时间:</strong></td>
														 		<td align="center" nowrap width="10%" colspan="2">
													 			<%
													 			if(enable < 0){
													 			%>
													 			<font color="red" style="font:bold; italic;">永久封号</font>
													 			<%
													 			} else 
													 			if(enable==0){ 
													 			%>
													 			<font color="red" style="font:bold; italic;"><%=blankofftime%></font>
													 			<%
													 			} else 
													 			{
													 			%>
													 			正常状态
													 			<%
													 			}
													 			%>
														 		<br></td>
														 		<td align="center" nowrap width="10%"><strong>特权:</strong></td>
														 		<td align="center" nowrap width="10%"><%=plaobj.optInt("tqnum")%><br></td>
														 		<td align="center" nowrap width="10%"><strong>到期时间:</strong></td>
														 		<td align="center" nowrap colspan="2" width="5%"><%=plaobj.optInt("tqquetime")%><br></td>
															</tr>
																	
															<tr class="listtopbgc"  >
																<td align="center" nowrap   colspan="12"><%=sname %>----<%=pname %>----操作</td>
															</tr>
															
															<tr >
															  <td colspan="12" align="center" nowrap bgcolor="#EFEFEF">
<table width="100%" border="0" cellspacing="1" cellpadding="2">
	<tr>
		<%
		if (plaobj.optInt("onlinestate") == 1) {
		%>
		<td align="center" colspan="5" nowrap>
		<input type="text" id="tips" name="tips"> 
		<input type="button" value="踢下线" onClick="stat3(5)">
		</td>
		<%
		}
		%>
		<td>
		<%
		if(MyTools.checkSysTimeBeyondSqlDate(bannedmsgtime)) {
		%>
		禁言期限：<input name="bannedMsgTime" type="text" id="bannedMsgTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" size="25"> 
		<img onClick="WdatePicker({el:'bannedMsgTime',dateFmt:'yyyy-MM-dd HH:mm:ss'})" src="../My97DatePicker/skin/datePicker.gif" width="16" height="22" align="absmiddle"><br>
		禁言理由：<input name="banNote" type="text" id="banNote" size="25"> <br>
		<input id="bannedButtom" name="bannedButtom" type="button" value="禁言" onClick="stat3(3)">
		<%
		} else {
		%> 
		<input type="hidden" name="bannedMsgTime" id="bannedMsgTime"> 
		<input id="bannedButtom" name="bannedButtom" type="button" value="解除禁言" onClick="stat3(4)">
		<%
		}
		%>
		</td>
		<td>
		<%
		if(enable > 0){
		%>
		封号期限：<input type="text" name="blankOffTime" id="blankOffTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
		<img onClick="WdatePicker({el:'blankOffTime',dateFmt:'yyyy-MM-dd HH:mm:ss'})" src="../My97DatePicker/skin/datePicker.gif" width="16" height="22" align="absmiddle"> <br/>
		封号理由：<input name="blankOffNote" type="text" id="blankOffNote" size="25"> <br /> 
		<input id="blankoffButtom" name="blankoffButtom" type="button" value="封号" onClick="stat3(1)">
		<%
		} else {
		%>
		<input id="blankoffButtom" name="blankoffButtom" type="button" value="解除封号" onClick="stat3(2)">
		<%
		}
		%>
		</td>
		<td>UID:<%=plaobj.optInt("userid")%></td>
		<td>PID:<%=pid %></td>
	</tr>
</table>
															</td>
														   </tr>
															</table>
															<%
														 	}
														 	%>
														 </td>
														 
													</tr>
													<%
													}
													%>
												</table>

											</td>
											<td rowspan="2" bgcolor="#848284" width="1">
												<img src="../images/spacer.gif" width="1" height="1">
											</td>
										</tr>
										<tr>
											<td bgcolor="#848284" height="1" colspan="2"></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			<input name="opt" type="hidden" value="0">
			<input name="pid" type="hidden" value="0">
			<input name="sid" type="hidden" value="0">
		</form>
		<script>
autoChoose(allValue);
</script>
		<iframe name=hiddenFrame width=0 height=0></iframe>
	</body>
</html>
