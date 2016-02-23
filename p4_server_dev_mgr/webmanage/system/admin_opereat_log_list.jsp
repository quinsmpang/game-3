<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ page import="com.ehc.system.UserBAC"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="系统设置";
String perm="管理员操作日志";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%

int defaultRow = Tools.str2int(request.getParameter("rows"));
if(defaultRow<=0)
defaultRow=10;	
String pagenum=request.getParameter("page");
if(pagenum==null || pagenum.equals("")) 
pagenum="1";

JSONObject xml = LogBAC.getInstance().getOpereatLogList(Tools.str2int(pagenum),defaultRow);
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="../js/common.js"></script>

<script>
var allValue=new Object();
	allValue.rows="<%=defaultRow%>";
</script>
<link href="../css/style1.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EFEFEF">

<form name="form1" method="post" action="admin_opereat_log_list.jsp">
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
                    <td nowrap background="../images/tab_midbak.gif"><%=perm %></td>
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
                                  </td>
									</tr>
								</table>
								
								<table width="100%" border="0" cellpadding="2" cellspacing="1"
									class="tbbgc1">
									<tr class="listtopbgc">
										<td align="center" nowrap>管理员</td>
										<td align="center" nowrap>操作</td>
										<td align="center">详细</td>
										<td align="center" nowrap>IP</td>
										<td align="center" nowrap>时间</td>
									 
									</tr>
									<%
									int rows = Tools.str2int(request.getParameter("rows"));
							
					  					int count = 0;
										JSONArray list=null;
										if(xml!=null){
											count=(xml.optInt("rsPageNO")-1)*rows+1;
											list = xml.optJSONArray("list");
										}
										if(list!=null){
										String serverName="";
										for(int i=0;i<list.length();i++){
											JSONObject jsonObj=list.getJSONObject(i);											
									 %>
									 
									<tr class="nrbgc1">
										<td align="center" nowrap><%=jsonObj.getString("userName")%></td>
										<td align="center" nowrap><%=jsonObj.getString("act")%></td>
										<td align="center"><%=jsonObj.getString("param1")%></td>
										<td align="center" nowrap><%=jsonObj.getString("ip")%></td>
										<td align="center" nowrap><%=jsonObj.getString("savedate")%></td>
									</tr>
								 <%}} %>
								</table>
								<br>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td align="right">
										   <%@ include file="../info/inc_list_bottom.jsp"%>
								</td>
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
</script>
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>
