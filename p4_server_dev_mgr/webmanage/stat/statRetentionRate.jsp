<%@page import="com.moonic.mgr.TabStor"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
	String model = "运营统计";
	String perm = "留存率统计";
%>  
<%@ include file="../system/inc_checkperm.jsp"%>

<%
	int stattype = Tools.str2int(request.getParameter("stattype"));
	
	String server = Tools.strNull(request.getParameter("server"));
	String startTime = Tools.strNull(request.getParameter("startTime"));
	String endTime = Tools.strNull(request.getParameter("endTime"));
	String statBtn = request.getParameter("statBtn");
	String channel = Tools.strNull(request.getParameter("channel"));
	
	int aitype = Tools.str2int(request.getParameter("aitype"));
									
	JsonRs returnRs = null;
	if(statBtn != null)
	{
		returnRs = OperationStatBAC.getInstance().getRetentionData(pageContext);
	}
%>
<script>
var allValue=new Object();
allValue.stattype="<%=stattype%>";
allValue.server="<%=server%>";
allValue.channel="<%=channel%>";
allValue.startTime="<%=startTime%>";
allValue.endTime="<%=endTime%>";
allValue.aitype="<%=aitype%>";
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
	
	if(document.getElementById("startTime").value=="")
	{
		alert("必须填写起始日期");
		document.getElementById("startTime").focus();
		return false;
	}
	
	wait();
	theForm.submit();
}
function changeTgr(){
	var tgrtype = document.getElementById("stattype").value;
	if(tgrtype==0){
		document.getElementById("aitype").style.display="";
		document.getElementById("server_label").style.display="";
	} else {
		document.getElementById("aitype").value="0";
		document.getElementById("server").checked=false;
		document.getElementById("aitype").style.display="none";
		document.getElementById("server_label").style.display="none";
	}
}
</script>
	</head>
	<body bgcolor="#EFEFEF">
		<form name="form1" method="post" enctype="application/x-www-form-urlencoded" onSubmit="return stat()">
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
												<table width="95%" border="0" cellspacing="1" cellpadding="2">
													<tr>
														<td>
															<table width="100%" border="0" cellspacing="1" cellpadding="2">
																<tr>
																  <td>
<select name="stattype" id="stattype" onpropertychange="changeTgr()">
<option value="0">角色留存率</option>
<option value="1">设备留存率</option>
<option value="2">帐号留存率</option>
</select>
起始日期：<input name="startTime" type="text" id="startTime" size="10" maxlength="10" onClick="new Calendar().show(this);" value="" />
结束日期：<input name="endTime" type="text" id="endTime" size="10" maxlength="10" onClick="new Calendar().show(this);" value="" />
<select name="aitype" id="aitype" style="display:none">
<option value="0">当日返回留存</option>
<option value="1">当日范围留存</option>
</select>
<label><input name="channel" type="checkbox">分渠道统计  </label>
<label id="server_label" name="server_label" style="display:none"><input type="checkbox" name="server">分服务器统计</label>
<input name="statBtn" type="submit" id="statBtn" value="统计">
</td>
																</tr>
															</table>
<%
if(returnRs != null){
String[] colname = {"新增角色数", "新增设备数", "新增帐号数"};
%>
<table width="100%" border="0" cellpadding="0" cellspacing="1" bgcolor="#666666">
  <tr class="listtopbgc" height="20">
  <td align="center" bgcolor="#FFFFFF">日期</td>
  <td align="center" bgcolor="#FFFFFF">渠道</td>
  <td align="center" bgcolor="#FFFFFF">服务器</td>
  <td align="center" bgcolor="#FFFFFF"><%=colname[stattype] %></td>
  <td align="center" bgcolor="#FFFFFF">次日留存</td>
  <td align="center" bgcolor="#FFFFFF">三日留存</td>
  <td align="center" bgcolor="#FFFFFF">七日留存</td>
  <td align="center" bgcolor="#FFFFFF">15日留存</td>
  <td align="center" bgcolor="#FFFFFF">30日留存</td> 
  </tr>
	<%
	DecimalFormat df = new DecimalFormat("0.0");
	while(returnRs.next()){
		String channelStr = "";
		String serveridStr = "";
		if(channel!=null&&!channel.equals("")){
			channelStr = TabStor.getListVal(TabStor.tab_channel, "code="+returnRs.getString("channel"), "name")+"("+returnRs.getString("channel")+")";
		}
		if(server!=null&&!server.equals("")){
			serveridStr = ServerBAC.getInstance().getNameById(returnRs.getInt("serverid"))+"("+returnRs.getInt("serverid")+")";
		}
		int total_new_player = returnRs.getInt("total_new_player");
		int total_stay_num1 = returnRs.getInt("total_stay_num1");
		int total_stay_num3 = returnRs.getInt("total_stay_num3");
		int total_stay_num7 = returnRs.getInt("total_stay_num7");
		int total_stay_num15 = returnRs.getInt("total_stay_num15");
		int total_stay_num30 = returnRs.getInt("total_stay_num30");
	%>
	<tr height="20">
	<td align="center" bgcolor="#EFEFEF"><%=MyTools.getDateStr(returnRs.getTime("log_date"))%></td>
	<td align="center" bgcolor="#EFEFEF"><%=channelStr%></td>
	<td align="center" bgcolor="#EFEFEF"><%=serveridStr%></td>
	<td align="center" bgcolor="#EFEFEF"><%=total_new_player%></td>
	<td align="center" bgcolor="#EFEFEF"><%=total_stay_num1+"("+df.format(100.0*total_stay_num1/total_new_player)+"%)"%></td>
	<td align="center" bgcolor="#EFEFEF"><%=total_stay_num3+"("+df.format(100.0*total_stay_num3/total_new_player)+"%)"%></td>
	<td align="center" bgcolor="#EFEFEF"><%=total_stay_num7+"("+df.format(100.0*total_stay_num7/total_new_player)+"%)"%></td>
	<td align="center" bgcolor="#EFEFEF"><%=total_stay_num15+"("+df.format(100.0*total_stay_num15/total_new_player)+"%)"%></td>
	<td align="center" bgcolor="#EFEFEF"><%=total_stay_num30+"("+df.format(100.0*total_stay_num30/total_new_player)+"%)"%></td>
	</tr>
	<%
	}
	%>
</table>
<%
}
%>
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
changeTgr();
</script>
<iframe name=hiddenFrame width=0 height=0></iframe>
</body>
</html>
