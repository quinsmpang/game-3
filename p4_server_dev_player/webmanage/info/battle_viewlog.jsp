
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="游戏管理";
String perm="查看当前战斗";
%>
<%@ include file="../system/inc_checkperm.jsp"%>


<script src="../js/common.js"></script>

<%
long battleId = Tools.str2long(request.getParameter("battleId"));
byte viewType= Tools.str2byte(request.getParameter("viewType"));

String log = "";
if(viewType==1)
{
	log = BattleConsole.getReplayLog(battleId);
}
else
if(viewType==2)
{
	log = BattleConsole.getDetailLog(battleId);
}
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script>
self.focus();
</script>
</head>
<body bgcolor="#EFEFEF">
<pre>
battleId=<%=battleId%>

<%=log%>
</pre>
</body>

</html>