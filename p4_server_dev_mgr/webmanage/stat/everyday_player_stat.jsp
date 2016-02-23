<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="市场统计";
String perm="每日玩家数据";

String pagetitle = request.getParameter("title");
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String channel = request.getParameter("channel");
int serverId = Tools.str2int(request.getParameter("serverId"));
String startDate = Tools.strNull(request.getParameter("startDate"));
String endDate = Tools.strNull(request.getParameter("endDate"));
String statChannel = request.getParameter("statChannel");
String statServer = request.getParameter("statServer");

String statBtn = request.getParameter("statBtn");

JsonRs returnRs = null;
if(statBtn != null){
	returnRs = MarketStatBAC.getInstance().getPlayerDayData(pageContext);
}
%> 

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link href="../css/style1.css" rel="stylesheet" type="text/css">
<script src="../js/common.js"></script>
<script src="../js/Calendar3.js"></script>
<script>
var allValue=new Object();
allValue.channel="<%=channel%>";
allValue.serverId="<%=serverId%>";
allValue.startDate="<%=startDate%>";
allValue.endDate="<%=endDate%>";
allValue.statChannel="<%=statChannel%>";
allValue.statServer="<%=statServer%>";
</script>
<script>
function stat()
{
var theForm = document.forms[0];
if(document.getElementById("startDate").value=="")
{
alert("必须选择开始日期");
document.getElementById("startDate").focus();
return ;
}
wait();
theForm.submit();
}

</script>
</head>
<body bgcolor="#EFEFEF">
<form name="form1" method="post" action=""  onSubmit="stat()">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td > 
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">每日玩家数据</td>
                    <td><img src="../images/tab_right.gif" width="6" height="22"></td>
                  </tr>
                </table></td>
                <td valign="bottom" > 
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
                <td rowspan="2" bgcolor="#FFFFFF" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
                <td valign="top" align="center"> 
                  <table width="95%" border="0" cellspacing="1" cellpadding="2">
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
开始日期：<input name="startDate" type="text" id="startDate" onClick="new Calendar().show(this)">
结束日期：<input name="endDate" type="text" id="endDate" onClick="new Calendar().show(this)">
<label><input name="statChannel" type="checkbox" id="statChannel" value="1">分渠道统计</label>
<label><input name="statServer" type="checkbox" id="statServer" value="1">分游戏服统计</label>
<input name="statBtn" type="submit" id="statBtn" value="统计">
									</td>
									</tr>
								</table>
								<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                                  <tr class="listtopbgc" >
                                    <td align="center" nowrap>日期</td>
                                    <td align="center" nowrap>渠道</td>
								   	<td align="center" nowrap>服务器</td>                                   
                                    <td align="center" nowrap>总角色数</td>
                                    <td align="center" nowrap>登陆次数</td>
                                    <td align="center" nowrap>活跃人数</td>
                                    <td align="center" nowrap>创建角色数</td>
                                    <td align="center" nowrap>最高在线</td>
                                    <td align="center" nowrap>总在线时长(小时)</td>
                                    <td align="center" nowrap>平均在线时长(分钟)</td>
                                    <td align="center" nowrap>首充人数</td>
                                    <td align="center" nowrap>充值人数</td>
                                    <td align="center" nowrap>充值金额</td>
                                    <td align="center" nowrap>钻石消耗</td>
                                    <td align="center" nowrap>ARRPU</td>
                                    <td align="center" nowrap>ARPU</td>
                                  </tr>
                                 <%
                                 DecimalFormat df = new DecimalFormat("0.0");
							 	 while(returnRs!=null && returnRs.next()){
								   String channelStr = "";
								   String serverStr = "";
								   if(statChannel != null){
								   	channelStr = TabStor.getListVal(TabStor.tab_channel, "code='"+returnRs.getString("channel")+"'", "name")+"("+returnRs.getString("channel")+")";
								   }
								   if(statServer != null){
								   	serverStr = ServerBAC.getInstance().getNameById(returnRs.getInt("serverid"))+"("+returnRs.getInt("serverid")+")";
								   }
								 %>
                                  <tr class="nrbgc1">
                                  <td align="center" nowrap><%=MyTools.getDateStr(returnRs.getTime("log_date"))%></td>
								  <td align="center" nowrap><%=channelStr%></td>
								  <td align="center" nowrap><%=serverStr%></td>
								  <td align="center" nowrap><%=returnRs.getString("sum_total_player")%></td>
								  <td align="center" nowrap><%=returnRs.getString("sum_login_num")%></td>
								  <td align="center" nowrap><%=returnRs.getString("sum_active_num")%></td>
								  <td align="center" nowrap><%=returnRs.getString("sum_new_player")%></td>
								  <td align="center" nowrap><%=statChannel!=null?returnRs.getString("sum_max_online_num"):""%></td>
								  <td align="center" nowrap><%=returnRs.getLong("sum_total_online_time")/60/60%></td>
								  <td align="center" nowrap><%=statChannel!=null?returnRs.getLong("avg_avg_online_time")/60:""%></td>
								  <td align="center" nowrap><%=returnRs.getString("sum_first_infull_num")%></td>
								  <td align="center" nowrap><%=returnRs.getString("sum_infull_user")%></td>
								  <td align="center" nowrap><%=returnRs.getString("sum_infull_num")%></td>
								  <td align="center" nowrap><%=returnRs.getString("sum_used_coin")%></td>
								  <td align="center" nowrap><%=df.format(returnRs.getDouble("sum_infull_num")/returnRs.getDouble("sum_infull_user"))%></td>
								  <td align="center" nowrap><%=df.format(returnRs.getDouble("sum_infull_num")/returnRs.getDouble("sum_active_num"))%></td>
                                  </tr>                                  
								  <%
								  }
								  %>
                                </table>
								<font color="#FF0000">ARRPU</font>=充值金额/充值人数<br>
								<font color="#FF0000">ARPU</font>=充值金额/当日活跃玩家
								<br>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
									  <td align="right">&nbsp;</td>
							</tr>
						</table>
						</td>						
					</tr>

				</table>			
				
                </td>
                <td rowspan="2" bgcolor="#848284" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
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
