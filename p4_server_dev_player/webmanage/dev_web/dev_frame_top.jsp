<%@ page contentType="text/html; charset=UTF-8"%>

<%@ include file="inc_import.jsp"%>

<%
String sessionid = CookieUtil.get(pageContext, "dev_sessionid");
com.moonic.socket.Player pla = null;
if(sessionid!=null) {
	pla = com.moonic.socket.SocketServer.getInstance().session_plamap.get(sessionid);
}
if(pla == null){
	out.print("<font size='2' color='#ff0000'>请先登录</font>");
	return;
}
%>

<html>
<head>
<title>-</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="css/style1.css" rel="stylesheet" type="text/css">
</head>
<body>

<script>

//注销
function logout(){
	document.forms[0].submit();
}

</script>

<form id="form" name="form" method="post" action="dev_request.jsp" target="_parent" enctype="multipart/form-data">

<font size="2">

当前登录角色：<font color='#ff0000'><%=pla.pname+"("+pla.pid+")"%></font><strong>&nbsp;|&nbsp;</strong><a href="javascript:logout()">注销</a>

<input name="player_logout" type="hidden" id="player_logout" value="1">

</font>

</form>

</body>
</html>
