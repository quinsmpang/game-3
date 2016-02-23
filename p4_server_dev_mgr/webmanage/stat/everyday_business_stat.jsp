<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="市场统计";
String perm="每日运营数据";

String pagetitle = request.getParameter("title");
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%

String channel = request.getParameter("channel");
String startDate = Tools.strNull(request.getParameter("startDate"));
String endDate = Tools.strNull(request.getParameter("endDate"));
String statChannel = request.getParameter("statChannel");
String statServer = request.getParameter("statServer");

String statBtn = request.getParameter("statBtn");

JsonRs[] returnRs = null;
if(statBtn != null){
	returnRs = MarketStatBAC.getInstance().getBusinessDayData(pageContext);
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
<form name="form1" method="post" action="" onSubmit="return stat()">
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
                    <td nowrap background="../images/tab_midbak.gif">每日运营数据</td>
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
开始日期：<input name="startDate" type="text" id="startDate" onClick="new Calendar().show(this)">
结束日期：<input name="endDate" type="text" id="endDate" onClick="new Calendar().show(this)">
<label><input name="statChannel" type="checkbox" id="statChannel" value="1">分渠道统计</label>
<input name="statBtn" type="submit" id="statBtn" value="统计">
									</td>
									</tr>
								</table>
								<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                                  <tr class="listtopbgc" >
                                    <td align="center" nowrap>渠道</td>
									<td align="center" nowrap>真实注册占比</td>
                                    <td align="center" nowrap>总激活数</td>
                                    <td align="center" nowrap>总注册数</td>
                                    <td align="center" nowrap>总真实注册数</td>
                                    <td align="center" nowrap>总角色数</td>
                                    <td align="center" nowrap>当日活跃数</td>
									<td align="center" nowrap>活跃角色率</td>
									<td align="center" nowrap>总充值人数</td>
									<td align="center" nowrap>总充值金额</td>
									<td align="center" nowrap>新增激活</td>
									<td align="center" nowrap>新增注册</td>
									<td align="center" nowrap>新增真实注册</td>
									<td align="center" nowrap>新增角色数</td>
									<td align="center" nowrap>新增充值人数</td>
									<td align="center" nowrap>新增充值金额</td>
									<td align="center" nowrap>充值ARPU</td>
									<td align="center" nowrap>注册ARPU</td>
									<td align="center" nowrap>付费率</td>
									<td align="center" nowrap>新增设备数（设备）</td>
									<td align="center" nowrap>次日留存数（设备）</td>
									<td align="center" nowrap>七日留存数（设备）</td>
									<td align="center" nowrap>次日留存（设备）</td>
									<td align="center" nowrap>七日留存（设备）</td>
                                  </tr>
                                 	<%
                                 	DecimalFormat df = new DecimalFormat("0.0");
                                 	while(returnRs!=null && returnRs[0].next()){
                                 		String channelStr = "";
                                 		String serverStr = "";
                                 		if(statChannel != null){
											boolean finded = false;
											returnRs[1].beforeFirst();
											while(returnRs[1].next()){
												if(returnRs[0].getString("channel").equals(returnRs[1].getString("channel"))){
													finded = true;
													break;
												}
											}
											if(!finded){
System.out.println("渠道未找到 channel="+returnRs[0].getString("channel"));
												continue;
											}
channelStr = TabStor.getListVal(TabStor.tab_channel, "code='"+returnRs[0].getString("channel")+"'", "name")+"("+returnRs[0].getString("channel")+")";
                                 		} else {
											returnRs[1].next();
										}
										double active_num = returnRs[0].getDouble("active_num");
										double total_player = returnRs[0].getDouble("total_player");
										double total_infull_num = returnRs[0].getDouble("total_infull_num");
										double total_infull_user = returnRs[0].getDouble("total_infull_user");
										double total_realreg = returnRs[0].getDouble("total_realreg");
										double stay_num1 = returnRs[1].getDouble("stay_num1");
										double stay_num7 = returnRs[1].getDouble("stay_num7");
										double new_equip = returnRs[1].getDouble("new_equip");
                                 	%>
                                  <tr class="nrbgc1">
<td align="center" nowrap><%=channelStr%></td>
<td align="center" nowrap><%=df.format(100*returnRs[1].getDouble("channel_rate"))+"%"%></td>
<td align="center" nowrap><%=returnRs[0].getString("total_acti")%></td>
<td align="center" nowrap><%=returnRs[0].getString("total_reg")%></td>
<td align="center" nowrap><%=returnRs[0].getString("total_realreg")%></td>
<td align="center" nowrap><%=returnRs[0].getString("total_player")%></td>
<td align="center" nowrap><%=returnRs[0].getString("active_num")%></td>
<td align="center" nowrap><%=(total_player!=0?df.format(100*active_num/total_player):0)+"%"%></td>
<td align="center" nowrap><%=returnRs[0].getString("total_infull_user")%></td>
<td align="center" nowrap><%=returnRs[0].getString("total_infull_num")%></td>
<td align="center" nowrap><%=returnRs[1].getString("acti")%></td>
<td align="center" nowrap><%=returnRs[1].getString("reg")%></td>
<td align="center" nowrap><%=returnRs[1].getString("realreg")%></td>
<td align="center" nowrap><%=returnRs[1].getString("new_player")%></td>
<td align="center" nowrap><%=returnRs[1].getString("first_infull_num")%></td>
<td align="center" nowrap><%=returnRs[1].getString("infull_num")%></td>
<td align="center" nowrap><%=(total_infull_user!=0?df.format(total_infull_num/total_infull_user):0)%></td>
<td align="center" nowrap><%=(total_realreg!=0?df.format(total_infull_num/total_realreg):0)%></td>
<td align="center" nowrap><%=(total_realreg!=0?df.format(100*total_infull_user/total_realreg):0)+"%"%></td>
<td align="center" nowrap><%=returnRs[1].getString("new_equip")%></td>
<td align="center" nowrap><%=returnRs[1].getString("stay_num1")%></td>
<td align="center" nowrap><%=returnRs[1].getString("stay_num7")%></td>
<td align="center" nowrap><%=(new_equip!=0?df.format(100*stay_num1/new_equip):0)+"%"%></td>
<td align="center" nowrap><%=(new_equip!=0?df.format(100*stay_num7/new_equip):0)+"%"%></td>
                                  </tr>
								  	<%
								  	}
								  	%>
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
