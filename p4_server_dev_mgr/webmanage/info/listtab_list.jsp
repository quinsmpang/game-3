<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="基本数据";
String perm="数据表查看";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%

String tab = request.getParameter("tablist");

JSONArray tabdataarr = null;

if(tab != null){
	tabdataarr = ListTabBAC.getInstance().getTabData(pageContext);
}

%>

<script src="../js/meizzDate.js"></script>

<script>
var allValue = new Object();
allValue.tablist = "<%=tab%>";
</script>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>

<script>
function stat(){
	var theForm = document.forms[0];	
	wait();
	theForm.submit();
}
</script>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EFEFEF">

<form name="form1" method="post" enctype="application/x-www-form-urlencoded">
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
                    <td nowrap background="../images/tab_midbak.gif">数据表查看</td>
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
										<td><label>
										  <select name="tablist" onChange="stat()">
										  <option value="">选择数据表</option>
										  	<%
										  	DBPsRs listtabRs = DBPool.getInst().pQueryS(ListTabBAC.tab_listtab);
											while(listtabRs.next()){
											%>
											<option value="<%=listtabRs.getString("tabname") %>"><%=listtabRs.getString("name") %></option>
											<%
											}
											%>
									    </select>
										</label></td>
									</tr>
								</table>
								
								<br>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
									  <td align="right">
									  
									  <%
								if(tabdataarr != null){
								%>
								<table width="100%" border="0" cellpadding="0" cellspacing="1" class="tbbgc1">
									<tr height="20" align="center">
									<%
									JSONArray colarr = tabdataarr.optJSONArray(0);
									for(int c = 0; c < colarr.length(); c++){
									%>
                                    <td class="listtopbgc"><%=colarr.getString(c) %></td>
									<%
									}
									%>
									</tr>
									
									<%
									for(int i = 1; i < tabdataarr.length(); i++){
									JSONArray one = tabdataarr.optJSONArray(i);
									%>
									<tr height="20" align="center" bgcolor="#EFEFEF">
									<%
									for(int c = 0; c < colarr.length(); c++){
									%>
                                    <td><%=one.optString(c) %></td>
                                    <%}%>
                                  	</tr>
									<%
									}
									%>									
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
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
<script>
autoChoose(allValue);
</script>
</html>
