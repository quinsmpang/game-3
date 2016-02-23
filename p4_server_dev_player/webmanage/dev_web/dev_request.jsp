<%@ page contentType="text/html; charset=UTF-8"%>

<%@ include file="inc_import.jsp"%>

<%
	ReturnValue rv = ReqManager.processingReq(pageContext);
	if(rv.info!=null && rv.info.equals("非法请求")){
		rv.info="";
	} else {
		rv.info = MyTools.getTimeStr()+"-"+(rv.success?"[处理成功]":"[处理失败]")+"-返回信息：\r\n"+Tools.strNull(rv.info);
	}
%>

<html>
<head>
<title>-</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="css/style1.css" rel="stylesheet" type="text/css">

<style type="text/css">
<!--
.style1 {color: #FF0000}
-->
</style>
</head>

<body>
<table border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td>
	<pre><%=rv.info %></pre>
	</td>
	</tr>
</table>
</body>

</html>