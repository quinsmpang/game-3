<%@ page contentType="text/html; charset=UTF-8"%>

<%@ include file="inc_import.jsp"%>

<%
	ReturnValue rv = ReqManager.processingReq(pageContext);
	if(rv.info==null || rv.info.equals("")){
		if(rv.success){
			rv.info="处理成功";
		} else {
			rv.info="处理失败";
		}
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
  </tr>
</table>
</body>

</html>