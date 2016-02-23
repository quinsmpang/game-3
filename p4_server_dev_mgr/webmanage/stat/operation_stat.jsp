<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="市场统计";
String perm="服务器数据汇总";

String pagetitle = request.getParameter("title");
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%

%>
<script src="../js/meizzDate.js"></script>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EFEFEF">

<form name="form1" method="post" onSubmit="wait();return true;">
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
                    <td nowrap background="../images/tab_midbak.gif"><%=perm+" （"+MyTools.getDateStr(System.currentTimeMillis()-MyTools.long_day)+"）"%></td>
                    <td><img src="../images/tab_right.gif" width="6" height="22"></td>
                  </tr>
                </table></td>
                <td valign="bottom" > 
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
										<td></td>
									</tr>
								</table>
								
								<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                                  <tr class="listtopbgc" >
                                    <td align="center" nowrap>No</td>
                                    <td align="center" nowrap>服务器</td>
                                    <td align="center" nowrap>开服天数</td>
                                    <td align="center" nowrap>玩家数</td>
                                    <td align="center" nowrap>最高在线</td>
                                    <td align="center" nowrap>最高等级</td>
                                    <td align="center" nowrap>充值人数</td>
                                    <td align="center" nowrap>充值次数</td>
                                    <td align="center" nowrap>充值金额</td>
                                  </tr>
                                  <%
                                  JsonRs returnRs = MarketStatBAC.getInstance().getServerStatData();
                                  while(returnRs.next()){
                                  %>
                                  <tr class="nrbgc1">
                                    <td align="center" nowrap><%=returnRs.getRow()%></td>
                                    <td align="center" nowrap><%=ServerBAC.getInstance().getNameById(returnRs.getInt("serverid"))+"("+returnRs.getInt("serverid")+")"%></td>
                                    <td align="center" nowrap><%=returnRs.getInt("open_day")%></td>
                                    <td align="center" nowrap><%=returnRs.getInt("user_num")%></td>
                                    <td align="center" nowrap><%=returnRs.getInt("max_online")%></td>
                                    <td align="center" nowrap><%=returnRs.getInt("max_lv")%></td>
                                    <td align="center" nowrap><%=returnRs.getInt("infull_user")%></td>
                                    <td align="center" nowrap><%=returnRs.getInt("infull_count")%></td>
                                    <td align="center" nowrap><%=returnRs.getInt("infull_num")%></td>
                                  </tr>
                                  <%
                                  }
                                  %>
                                </table>
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
<iframe name=hiddenFrame width=0 height=0></iframe>
</body>
</html>
