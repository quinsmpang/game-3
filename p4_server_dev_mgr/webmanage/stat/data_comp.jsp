<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="市场统计";
String perm="数据对比";

String pagetitle = request.getParameter("title");
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String startDate = Tools.strNull(request.getParameter("startDate"));
String endDate = Tools.strNull(request.getParameter("endDate"));

String statBtn = request.getParameter("statBtn");

JsonRs returnRs = null;
if(statBtn != null){
	returnRs = MarketStatBAC.getInstance().getCompData(pageContext);
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
allValue.startDate="<%=startDate%>";
allValue.endDate="<%=endDate%>";
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
                    <td nowrap background="../images/tab_midbak.gif">数据对比</td>
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
开始日期：<input name="startDate" type="text" id="startDate" onClick="new Calendar().show(this)">
结束日期：<input name="endDate" type="text" id="endDate" onClick="new Calendar().show(this)">
<input name="statBtn" type="submit" id="statBtn" value="统计">
									</td>
									</tr>
								</table>
								<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                                  <tr class="listtopbgc" >                                    								   
                                    <td align="center" nowrap>日期</td>
                                    <td align="center" nowrap>新增激活</td>
                                    <td align="center" nowrap>新增注册</td>
                                    <td align="center" nowrap>真实注册</td>
                                    <td align="center" nowrap>新增角色</td>
                                    <td align="center" nowrap>活跃玩家数</td>
                                    <td align="center" nowrap>最高在线数</td>
                                    <td align="center" nowrap>平均在线时长</td>
                                  </tr>
                                 	<%
                                 	DecimalFormat df = new DecimalFormat("0.0");
                                 	while(returnRs!=null && returnRs.next()){
                                 	%>
                                  <tr class="nrbgc1">
<td align="center" nowrap><%=MyTools.getDateStr(returnRs.getTime("log_date"))%></td>									 
<td align="center" nowrap><%=returnRs.getString("day_real_equ")%></td>
<td align="center" nowrap><%=returnRs.getString("day_reg")%></td>
<td align="center" nowrap><%=returnRs.getString("day_real_reg")%></td>
<td align="center" nowrap><%=returnRs.getString("day_players")%></td>
<td align="center" nowrap><%=returnRs.getString("activ_num")%></td>
<td align="center" nowrap><%=returnRs.getString("max_online_num")%></td>
<td align="center" nowrap><%=returnRs.getString("avg_online_time")%></td>
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
