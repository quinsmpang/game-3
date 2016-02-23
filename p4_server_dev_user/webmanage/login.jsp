<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="system/inc_import.jsp"%>
<%
UserBAC userBAC = new UserBAC();
//验证用户帐号
ReturnValue rv=userBAC.checkLogin(pageContext);
//System.out.println("rv.success="+rv.success);
if(rv.success)  //验证通过
{	
%>
	<script>
	location.replace("workzone.jsp");
	</script>
<%
}
else  //验证不通过
{
%>
	<script>
	alert("<%=rv.info%>");
	history.back();
	</script>
<%}%>