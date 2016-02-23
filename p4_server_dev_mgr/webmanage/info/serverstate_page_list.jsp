<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="服务器管理";
String perm="服务器状态监控";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
String servertype = Tools.strNull(request.getParameter("servertype"));
int serverid = Tools.str2int(request.getParameter("serverid"));
String starttime = Tools.strNull(request.getParameter("starttime"));
String endtime = Tools.strNull(request.getParameter("endtime"));
JSONArray returnarr = null;
if(request.getParameter("statBtn") != null){
	returnarr = ServerStateBAC.getInstance().getStateData(pageContext);
}

DBPsRs serverRs1 = DBPool.getInst().pQueryS(ServerBAC.tab_user_server);
JSONArray s1arr = new JSONArray();
while(serverRs1.next()){
	JSONObject obj = new JSONObject();
	obj.put("serverid", serverRs1.getString("id"));
	obj.put("servername", serverRs1.getString("name"));
	s1arr.add(obj);
}
JSONArray s2arr = new JSONArray();
DBPsRs serverRs2 = DBPool.getInst().pQueryS(ServerBAC.tab_server, "usestate=1");
while(serverRs2.next()){
	JSONObject obj = new JSONObject();
	obj.put("serverid", serverRs2.getString("id"));
	obj.put("servername", serverRs2.getString("name"));
	s2arr.add(obj);
}
JSONArray typearr = new JSONArray();
typearr.add(s1arr);
typearr.add(s2arr);
//System.out.println(typearr.toString());
%>

<script>
var allValue = new Object();
allValue.servertype = "<%=servertype%>";
allValue.serverid = "<%=serverid%>";
allValue.starttime="<%=starttime%>";
allValue.endtime="<%=endtime%>";
</script>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<SCRIPT  SRC="../Charts/FusionCharts.js"></SCRIPT>
<script src="../js/Calendar3.js"></script>
<style type="text/css">
<!--
.style1 {color: #FF0000}
-->
</style>
</head>

<script type="text/javascript" language="javascript">
var data = <%=typearr.toString()%>;
function stat(){
	if(document.getElementById("starttime").value==""){
		alert("请填写起始时间");
		return false;
	}
	var theForm = document.forms[0];
	wait();
	return true;
}
function changeServer(obj,subobj){
	removeoption(subobj,1);
	if(obj.value!=-1){
		for(var k = 0; k < data[obj.value].length; k++){
			addoption(subobj,data[obj.value][k]["serverid"],data[obj.value][k]["servername"]);
		}
	}
}
function addoption(obj,value,text){
	var newOption = document.createElement("option");
	newOption.value = value;
	newOption.text = text;
	obj.options.add(newOption);
}
function removeoption(obj,index){
	while(obj.options.length>index){
		obj.options.remove(index);
	}
}
</script>

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
                    <td nowrap background="../images/tab_midbak.gif">服务器状态监控</td>
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
                              <td>&nbsp;</td>
                            </tr>
                          </table>
                          
                          <select name="servertype" id="servertype" onClick="changeServer(this,document.getElementById('serverid'))">
                          <option value="">服务器类型</option>
                          <option value="0">用户服务器</option>
                          <option value="1">游戏服务器</option>
                          </select>
                          
	                      <select name="serverid" id="serverid">
	                       <option value="0">选择服务器</option>
                          </select>
                          
                        起始时间：<input name="starttime" type="text" id="starttime" onClick="new Calendar().show(this)" size="20" />
                        截至时间：<input name="endtime" type="text" id="endtime" onClick="new Calendar().show(this)" size="20" />
                        <span class="style1">（不填则为当前时间）</span>
                        <input name="statBtn" type="submit" id="statBtn" value="查询">
                          <br>
                          	<%
                          	for(int i = 0; returnarr!=null && i<returnarr.length(); i++){
                          	%>
                          	<%=returnarr.optString(i) %>
                          	<%
                          	}
                          	%>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right"><input name="delid" type="hidden" id="delid"></td>
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
autoChoose(allValue);
changeServer(document.getElementById('servertype'),document.getElementById('serverid'));
autoChoose(allValue);
</script>
</html>
