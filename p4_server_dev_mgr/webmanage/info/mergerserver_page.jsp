<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="服务器管理";
String perm="合并服务器";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
int subm = Tools.str2int(request.getParameter("subm"));
if(subm == 1){
	String scrids = request.getParameter("scrids");
	int mergerserver = Tools.str2int(request.getParameter("mergerserver"));
	ReturnValue rv = ServerBAC.getInstance().mergerServer(scrids, mergerserver);
%>
<script>
alert("<%=rv.info%>");
</script>
<%
}
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script src="../js/mytools.js"></script>
<script type="text/javascript">
function refreshserverlist(){
	for(var i = 0; i < document.getElementsByName("bemergerserver").length; i++){
		if(document.getElementsByName("bemergerserver")[i].value == document.forms[0].mergerserver.value){
			document.getElementsByName("bemergerserver")[i].disabled=true;
		} else {
			document.getElementsByName("bemergerserver")[i].disabled=false;
		}
		//alert(document.getElementsByName("bemergerserver")[i].title);
		document.getElementsByName("bemergerserver")[i].checked=false;
	}
}
function toMerger(){
	var beserver = "";
	var beserverid = "";
	var have = false;
	for(var i = 0; i < document.getElementsByName("bemergerserver").length; i++){
		if(document.getElementsByName("bemergerserver")[i].checked){
			have = true;
			if(beserver.length>0){
				beserver +="，";
				beserverid += ",";
			}
			beserver += document.getElementsByName("bemergerserver")[i].title;
			beserverid += document.getElementsByName("bemergerserver")[i].value;
		}
	}
	if(!have){
		alert("请选择要并入的服务器");
		return;
	}
	if(!enterTip("请确认本次合服操作：将"+beserver+" 并入 "+getSelectedText(document.forms[0].mergerserver))){
		return;
	}
	var theForm = document.forms[0];
	theForm.action="mergerserver_page.jsp?subm=1&scrids="+beserverid;
	wait();
	theForm.submit();
}
function getSelectedText(selectobj) {
	for (i = 0; i < selectobj.length; i++) {             
		if (selectobj.options[i].selected == true){
			 return selectobj.options[i].text;
		}
	}
}

</script>
<style type="text/css">
<!--
.style1 {color: #FF0000}
-->
</style>
</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="">
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
                    <td nowrap background="../images/tab_midbak.gif">合并服务器</td>
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
                  
				  <table width="90%" border="0" cellspacing="1" cellpadding="2">
                    <tr> 
                      <td>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td>
<table border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
	<%
	  DBPsRs serverRs = DBPool.getInst().pQueryS("tab_server", "usestate=1");
	  while(serverRs.next()){
	  %>
	  <input name="bemergerserver" type="checkbox" value="<%=serverRs.getInt("id")%>" title="<%=serverRs.getString("name")%>"><%=serverRs.getString("name")%><br>
	  <%
	  }
  %>
	</td>
    <td width="200" align="center"><span class="style1">并入</span></td>
    <td>
	<select name="mergerserver" onChange="refreshserverlist()">
	<%
	serverRs.beforeFirst();
	while(serverRs.next()){
	%>
	<option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
	<%
	}
	%>
	</select>
	</td>
	<td width="100" align="center" valign="bottom">
<table width="50" border="0" cellspacing="0" cellpadding="2">
<tr>
<td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="toMerger()"><img src="../images/icon_save2.gif" width="16" height="16" align="absmiddle">执行</td>
</tr>
</table>
	</td>
  </tr>
</table>

							  
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
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
<script>
refreshserverlist();
</script>
</html>
