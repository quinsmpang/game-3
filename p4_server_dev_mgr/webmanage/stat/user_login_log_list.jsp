<%@page import="com.moonic.util.DBPool"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.moonic.util.ipseek.IPSeeker"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
	String model = "玩家管理";
	String perm = "用户登录日志";
%>  
<%@ include file="../system/inc_checkperm.jsp"%>
<%
	int rpp = 10;
	String playerName = Tools.strNull(request.getParameter("playerName"));
	String pagenum = request.getParameter("page");
	String username = Tools.strNull(request.getParameter("username"));
	String startDate = Tools.strNull(request.getParameter("startDate"));
	String endDate = Tools.strNull(request.getParameter("endDate"));
	String statBtn = request.getParameter("statBtn");
	String mac = Tools.strNull(request.getParameter("mac"));
	String imei = Tools.strNull(request.getParameter("imei"));
		
	JSONObject xml=null;
	if(statBtn!=null || pagenum!=null) {
		xml = UserLoginLogBAC.getInstance().getUserLoginLogList(pageContext);
	}
	if(pagenum==null || pagenum.equals("")) {
		pagenum="1";
	}
%>
<script>
var allValue=new Object();
allValue.username="<%=username%>";
allValue.startDate="<%=startDate%>";
allValue.endDate="<%=endDate%>";
allValue.mac="<%=mac%>";
allValue.imei="<%=imei%>";
</script>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="../css/style1.css" rel="stylesheet" type="text/css">
<script src="../js/common.js"></script>
<script language="javascript" type="text/javascript" src="../My97DatePicker/WdatePicker.js"></script>
<script>
function stat() {

var theForm = document.forms[0];
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
										<table width="95%" border="0" cellspacing="1" cellpadding="2">
											<tr>
												<td>
													<table width="100%" border="0" cellspacing="1" cellpadding="2">
														<tr>
															<td>
															用户名:
															 <input name="username" id="username" type="text">
															 MAC:
															 <input name="mac" id="mac" type="text">
															 IMEI:
															 <input name="imei" id="imei" type="text">
															起始日期:
															<input type="text" name="startDate" id="startDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" style="width: 120px;"/>		  
															结束日期:
															<input type="text" name="endDate" id="endDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" style="width: 120px;"/>		  
															<input name="statBtn" type="submit" id="statBtn" value="查询" onClick="stat()">
															</td>
														</tr>
													</table>
													<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
														<tr class="listtopbgc">
													  	<td align="center" nowrap >USER_ID</td>
						                                 <td align="center" nowrap >账号</td>
						                                 <td align="center" nowrap >联运渠道</td>
						                                 <td align="center" nowrap >账户渠道</td>
						                                 <td align="center" nowrap >分辨率</td>
						                                 <td align="center" nowrap >网络名</td>
						                                 <td align="center" nowrap >网络制式</td>
						                                 <td align="center" nowrap >是否WIFI</td>
						                                 <td align="center" nowrap >IP</td>
						                                 <td align="center" nowrap >MAC</td>
						                                 <td align="center" nowrap >IMEI</td>
						                                 <td align="center" nowrap >手机品牌</td>
						                                 <td align="center" nowrap >手机型号</td>
						                                 <td align="center" nowrap >剩余内存</td>
						                                 <td align="center" nowrap >总内存</td>
						                                 <td align="center" nowrap >ANDROID_LV</td>
						                                 <td align="center" nowrap >ANDROID_版本</td>						                                
						                                 <td align="center" nowrap >登陆时间</td>
						                                 </tr>
													 <%
													 
													 int count = 0;
														JSONArray list=null;
														if(xml!=null)
														{
															count=(xml.optInt("rsPageNO")-1)*rpp+1;
															list = xml.optJSONArray("list");
														}
													 	 
													for(int i=0;list!=null&&i<list.length();i++){
														JSONObject jsonObj=list.getJSONObject(i);
													  %>
													  
													  <tr>
						                                  <td align="center" nowrap class="nrbgc1"><%=jsonObj.optInt("userid")%></td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("username")%></td>
						                                  <td align="center" class="nrbgc1"><%=TabStor.getListVal(TabStor.tab_channel, "code='"+jsonObj.optString("channel")+"'", "name")%>(<%=jsonObj.optString("channel")%>)</td>
						                                  <td align="center" class="nrbgc1"><%=TabStor.getListVal(TabStor.tab_platform, "code='"+jsonObj.optString("platform")+"'", "name")%>(<%=jsonObj.optString("platform")%>)</td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("resolution")%></td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("netname")%></td>
						                                  <td align="center" class="nrbgc1"><%=UserLoginLogBAC.getNetWorkType(jsonObj.optInt("nettype"))%></td>
						                                  <td align="center" class="nrbgc1"><%if(jsonObj.optInt("wifi")==1){%>是<%}else{%>否<%} %></td>
						                                  <td align="center" class="nrbgc1" ><%=jsonObj.optString("ip")%></td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("mac")%></td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("imei")%></td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("phonevendor")%></td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("phonemodel")%></td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("phonefreemem")%>MB</td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("phonetotalmem")%>MB</td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("phonesdklv")%></td>
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("phonesdkver")%></td>					                                     
						                                  <td align="center" class="nrbgc1"><%=jsonObj.optString("logintime")%></td>
						                                </tr>
													 <%
													 }
												  %>
													</table>
			 						
													<table width="100%" border="0" cellspacing="1" cellpadding="2">
														<tr>
															<td align="center"> <%@ include file="inc_list_bottom.jsp"%></td>
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
