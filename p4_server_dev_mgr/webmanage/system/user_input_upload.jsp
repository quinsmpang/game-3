<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="用户管理";
%>
<%@ include file="inc_checkperm.jsp"%>

<script src="../js/common.js"></script>
<%

UserBAC userBAC = new UserBAC();

ReturnValue rv=userBAC.save(pageContext);

if(rv.success)
{
%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
parent.opener.document.forms[0].submit();
top.close();
</script>
<%}else{%>
<script>
	alert("<%=rv.info%>");	
	wait_end(parent);
	</script>
<%}%>

