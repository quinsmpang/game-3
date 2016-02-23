<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="权限分组";
%>
<%@ include file="inc_checkperm.jsp"%>

<script src="../js/common.js"></script>

<%
int id=ToolFunc.str2int(request.getParameter("id"));

String roleName=request.getParameter("roleName");
int roleType=ToolFunc.str2int(request.getParameter("roleType"));


RoleBAC roleBAC = new RoleBAC();

ReturnValue rv=roleBAC.save(pageContext);

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
	wait_end(parent);
	alert("<%=rv.info%>");		
	</script>
<%}%>


